package com.lzq.cloud.mall.cartorder.controller;


import com.lzq.cloud.mall.cartorder.feign.UserFeignClient;
import com.lzq.cloud.mall.cartorder.model.vo.CartVO;
import com.lzq.cloud.mall.cartorder.service.CartService;
import com.lzq.cloud.mall.common.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    UserFeignClient userFeignClient;

    @PostMapping("/add")
    @ApiOperation("add product to the cart")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> list = cartService.add(userFeignClient.getUser().getId(), productId, count);
        return ApiRestResponse.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("the list of the cart")
    public ApiRestResponse list(){
        List<CartVO> list = cartService.list(userFeignClient.getUser().getId());
        return ApiRestResponse.success(list);
    }

    @PostMapping("/update")
    @ApiOperation("update the cart")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> list = cartService.update(userFeignClient.getUser().getId(), productId, count);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/delete")
    @ApiOperation("delete the item in the cart")
    public ApiRestResponse delete(@RequestParam Integer productId){
        List<CartVO> list = cartService.delete(userFeignClient.getUser().getId(), productId);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/select")
    @ApiOperation("select or deselect the item in the cart")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected){
        List<CartVO> list = cartService.selectOrNot(userFeignClient.getUser().getId(), productId, selected);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/selectAll")
    @ApiOperation("select or deselect all items in the cart")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        List<CartVO> list = cartService.selectAllOrNot(userFeignClient.getUser().getId(), selected);
        return ApiRestResponse.success(list);
    }
}
