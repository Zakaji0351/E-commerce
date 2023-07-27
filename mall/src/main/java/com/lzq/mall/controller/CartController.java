package com.lzq.mall.controller;

import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.filter.UserFilter;
import com.lzq.mall.model.vo.CartVO;
import com.lzq.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/add")
    @ApiOperation("add product to the cart")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> list = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("the list of the cart")
    public ApiRestResponse list(){
        List<CartVO> list = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(list);
    }

    @PostMapping("/update")
    @ApiOperation("update the cart")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> list = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/delete")
    @ApiOperation("delete the item in the cart")
    public ApiRestResponse delete(@RequestParam Integer productId){
        List<CartVO> list = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/select")
    @ApiOperation("select or deselect the item in the cart")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected){
        List<CartVO> list = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(list);
    }
    @PostMapping("/selectAll")
    @ApiOperation("select or deselect all items in the cart")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        List<CartVO> list = cartService.selectAllOrNot(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(list);
    }
}
