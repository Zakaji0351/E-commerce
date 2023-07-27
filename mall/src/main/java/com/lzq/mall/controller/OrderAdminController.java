package com.lzq.mall.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.model.vo.OrderStatisticsVO;
import com.lzq.mall.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/admin/order/statistic")
    @ApiOperation("statistics of every day order")
    public ApiRestResponse statistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date startDate,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate){
        List<OrderStatisticsVO> statistics = orderService.statistics(startDate, endDate);
        return ApiRestResponse.success(statistics);
    }


}
