package com.lzq.mall.controller;

import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.model.request.CreateOrderRequest;
import com.lzq.mall.model.vo.OrderVO;
import com.lzq.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @PostMapping("/order/create")
    @ApiOperation("create an order")
    public ApiRestResponse create(@RequestBody CreateOrderRequest createOrderRequest){
        String orderNo = orderService.create(createOrderRequest);
        return ApiRestResponse.success(orderNo);
    }
    @GetMapping("/order/detail")
    @ApiOperation("order detail for user")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }
    @GetMapping("/order/list")
    @ApiOperation("order list for user")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    @PostMapping("/order/cancel")
    @ApiOperation("user cancel order")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }
    @PostMapping("/order/qrcode")
    @ApiOperation("generate pay QR code")
    public ApiRestResponse qrcode(@RequestParam String orderNo) throws IOException, WriterException {
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }
    @GetMapping("/order/pay")
    @ApiOperation("user pay the order")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
