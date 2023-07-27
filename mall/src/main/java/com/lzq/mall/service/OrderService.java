package com.lzq.mall.service;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.lzq.mall.model.request.CreateOrderRequest;
import com.lzq.mall.model.vo.OrderStatisticsVO;
import com.lzq.mall.model.vo.OrderVO;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface OrderService {
    String create(CreateOrderRequest createOrderRequest);

    OrderVO detail(String orderNo);

    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo) throws IOException, WriterException;

    void pay(String orderNo);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    void deliver(String orderNo);


    void finish(String orderNo);

    List<OrderStatisticsVO> statistics(Date startDate, Date endDate);
}
