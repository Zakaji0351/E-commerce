package com.lzq.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.categoryproduct.model.request.ProductListRequest;
import com.lzq.cloud.mall.categoryproduct.service.ProductService;
import com.lzq.cloud.mall.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/product/detailForFeign")
    public Product detailForFeign(@RequestParam Integer id){
        Product product = productService.detail(id);
        return product;
    }
    @PostMapping("/product/updateStock")
    public void updateStock(@RequestParam Integer productId, @RequestParam Integer stock){
        productService.updateStock(productId, stock);
    }

}
