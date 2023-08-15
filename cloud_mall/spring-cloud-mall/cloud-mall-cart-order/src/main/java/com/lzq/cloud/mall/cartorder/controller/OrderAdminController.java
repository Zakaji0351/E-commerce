package com.lzq.cloud.mall.cartorder.controller;

import com.github.pagehelper.PageInfo;

import com.lzq.cloud.mall.cartorder.service.OrderService;
import com.lzq.cloud.mall.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController

public class OrderAdminController {
    @Autowired
    OrderService orderService;
    @GetMapping("/admin/order/list")
    @ApiOperation("order list for admin")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    @PostMapping("/admin/order/delivered")
    @ApiOperation("admin delivery")
    public ApiRestResponse deliver(@RequestParam String orderNo){
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }
    @PostMapping("/order/finish")
    @ApiOperation("finish order")
    public ApiRestResponse finish(@RequestParam String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }


}
