package com.lzq.cloud.mall.cartorder.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.google.zxing.WriterException;
import com.lzq.cloud.mall.cartorder.feign.ProductFeignClient;
import com.lzq.cloud.mall.cartorder.feign.UserFeignClient;
import com.lzq.cloud.mall.cartorder.model.dao.CartMapper;
import com.lzq.cloud.mall.cartorder.model.dao.OrderItemMapper;
import com.lzq.cloud.mall.cartorder.model.dao.OrderMapper;
import com.lzq.cloud.mall.cartorder.model.pojo.Order;
import com.lzq.cloud.mall.cartorder.model.pojo.OrderItem;
import com.lzq.cloud.mall.cartorder.model.request.CreateOrderRequest;
import com.lzq.cloud.mall.cartorder.model.vo.CartVO;
import com.lzq.cloud.mall.cartorder.model.vo.OrderItemVO;
import com.lzq.cloud.mall.cartorder.model.vo.OrderVO;
import com.lzq.cloud.mall.cartorder.service.CartService;
import com.lzq.cloud.mall.cartorder.service.OrderService;
import com.lzq.cloud.mall.cartorder.util.OrderCodeFactory;
import com.lzq.cloud.mall.categoryproduct.common.ProductConstant;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import com.lzq.cloud.mall.common.util.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    CartService cartService;
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    UserFeignClient userFeignClient;
    @Value("${file.upload.ip}")
    String ip;
    @Value("${file.upload.port}")
    String port;
    @Value("${file.upload.dir}")
    String FILE_UPLOAD_DIR;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderRequest createOrderRequest){
        //拿到用户id， 从购物车查找勾选的物品， 如果购物车已勾选的商品为空， 报错
        Integer userId = userFeignClient.getUser().getId();
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            if(cartVO.getSelected().equals(Constant.CheckStatus.CHECKED)){
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        if(CollectionUtils.isEmpty(cartVOList)){
            throw new MallException(MallExceptionEnum.CART_SELECT_EMPTY);
        }
        //判断商品是否存在，上下架状态，库存，把购物车对象转为订单item对象
        validSaleStatusAndStock(cartVOList);
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            Product product = productFeignClient.detailForFeign(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if(stock < 0){
                throw new MallException(MallExceptionEnum.PRODUCT_NOT_ENOUGH);
            }
            productFeignClient.updateStock(product.getId(), stock);
        }
        //把购物车已经勾选的商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverAddress(createOrderRequest.getReceiverAddress());
        order.setReceiverMobile(createOrderRequest.getReceiverMobile());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        orderMapper.insertSelective(order);
        //循环保存到每个商品的order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        return orderNo;
    }
    @Override
    public OrderVO detail(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.ORDER_NOT_FOUND);
        }
        Integer userId = userFeignClient.getUser().getId();
        if(!order.getUserId().equals(userId)){
            throw new MallException(MallExceptionEnum.ORDER_NOT_MATCH);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }
    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize){
        Integer userId = userFeignClient.getUser().getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }
    @Override
    public void cancel(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.ORDER_NOT_FOUND);
        }
        Integer userId = userFeignClient.getUser().getId();
        if(!order.getUserId().equals(userId)){
            throw new MallException(MallExceptionEnum.ORDER_NOT_MATCH);
        }
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MallException(MallExceptionEnum.ORDER_STATUS_EXCEPTION);
        }
    }
    @Override
    public String qrcode(String orderNo){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String address = ip + ":" + port;
        String payURL = "http://" + address + "/cart-order/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.generateQRCodeImage(payURL, 350, 350, FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/cart-order/images/" + orderNo + ".png";
        return pngAddress;
    }
    @Override
    public void pay(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.ORDER_NOT_FOUND);
        }
        if(order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MallException(MallExceptionEnum.ORDER_STATUS_EXCEPTION);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }
    @Override
    public void deliver(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.ORDER_NOT_FOUND);
        }
        if(order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MallException(MallExceptionEnum.ORDER_STATUS_EXCEPTION);
        }
    }
    @Override
    public void finish(String orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(userFeignClient.getUser().getRole().equals(1) && !order.getUserId().equals(userFeignClient.getUser().getId())){
            throw new MallException(MallExceptionEnum.ORDER_NOT_MATCH);
        }
        if(order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            throw new MallException(MallExceptionEnum.ORDER_STATUS_EXCEPTION);
        }
    }



    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for(int i = 0; i < orderList.size(); i++){
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem =  orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for(int i = 0; i < cartVOList.size(); i++){
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO =  cartVOList.get(i);
            Product product = productFeignClient.detailForFeign(cartVO.getProductId());
            if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
                throw new MallException(MallExceptionEnum.NOT_SALE);
            }
            if(cartVO.getQuantity() > product.getStock()){
                throw new MallException(MallExceptionEnum.PRODUCT_NOT_ENOUGH);
            }
        }
    }
}
