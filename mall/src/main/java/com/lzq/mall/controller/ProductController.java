package com.lzq.mall.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.model.pojo.Product;
import com.lzq.mall.model.request.ProductListRequest;
import com.lzq.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping("/product/detail")
    public ApiRestResponse detail(@RequestParam Integer id){
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }

    @GetMapping("/product/list")
    @ApiOperation("product list")
    public ApiRestResponse list(ProductListRequest productListRequest){
        PageInfo list = productService.list(productListRequest);
        return ApiRestResponse.success(list);
    }
}
