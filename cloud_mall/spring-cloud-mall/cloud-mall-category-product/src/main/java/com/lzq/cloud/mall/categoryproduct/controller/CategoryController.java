package com.lzq.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.cloud.mall.categoryproduct.service.CategoryService;
import com.lzq.cloud.mall.common.common.ApiRestResponse;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Category;
import com.lzq.cloud.mall.categoryproduct.model.request.AddCategoryRequest;
import com.lzq.cloud.mall.categoryproduct.model.request.UpdateCategoryRequest;
import com.lzq.cloud.mall.categoryproduct.model.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @ApiOperation("add category in the backend")
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryRequest addCategoryRequest){
        categoryService.add(addCategoryRequest);
        return ApiRestResponse.success();

    }
    @ApiOperation("update category in the backend")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(HttpSession session, @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest){
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryRequest, category);
        categoryService.update(category);
        return ApiRestResponse.success();

    }

    @ApiOperation("delete a category record in the backend")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }
    @ApiOperation("select admin category list")
    @PostMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("select customer category list")
    @PostMapping("/category/list")
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> list = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(list);
    }


}
