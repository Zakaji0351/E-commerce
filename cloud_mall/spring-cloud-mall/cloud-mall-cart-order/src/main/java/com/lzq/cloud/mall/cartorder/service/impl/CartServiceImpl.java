package com.lzq.cloud.mall.cartorder.service.impl;


import com.lzq.cloud.mall.cartorder.feign.ProductFeignClient;
import com.lzq.cloud.mall.cartorder.model.dao.CartMapper;
import com.lzq.cloud.mall.cartorder.model.pojo.Cart;
import com.lzq.cloud.mall.cartorder.model.vo.CartVO;
import com.lzq.cloud.mall.cartorder.service.CartService;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
//    @Autowired
//    ProductMapper productFeignClient;
    @Autowired
    ProductFeignClient productFeignClient;
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count){
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.CheckStatus.CHECKED);
            cartMapper.insertSelective(cart);
        }else{
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.CheckStatus.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }
    private void validProduct(Integer productId, Integer count){
        Product product = productFeignClient.detailForFeign(productId);
        if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
            throw new MallException(MallExceptionEnum.NOT_SALE);
        }
        if(count > product.getStock()){
            throw new MallException(MallExceptionEnum.PRODUCT_NOT_ENOUGH);
        }
    }
    @Override
    public List<CartVO> list(Integer userId){
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for(int i = 0; i < cartVOS.size(); i++){
            CartVO cartVO = cartVOS.get(i);
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count){
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            throw new MallException(MallExceptionEnum.CART_UPDATE_FAILURE);
        }else{
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.CheckStatus.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> delete(Integer userId, Integer productId){
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            throw new MallException(MallExceptionEnum.CART_DELETE_FAILURE);
        }else{
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected){
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart == null){
            throw new MallException(MallExceptionEnum.CART_UPDATE_FAILURE);
        }else{
            int count = cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected){
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }

}
