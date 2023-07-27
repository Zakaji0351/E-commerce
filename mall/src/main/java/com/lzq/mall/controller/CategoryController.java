package com.lzq.mall.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.common.Constant;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.filter.UserFilter;
import com.lzq.mall.model.pojo.Category;
import com.lzq.mall.model.pojo.User;
import com.lzq.mall.model.request.AddCategoryRequest;
import com.lzq.mall.model.request.UpdateCategoryRequest;
import com.lzq.mall.model.vo.CategoryVO;
import com.lzq.mall.service.CategoryService;
import com.lzq.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @ApiOperation("add category in the backend")
    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryRequest addCategoryRequest){
//        if(addCategoryRequest.getName() == null || addCategoryRequest.getType() == null || addCategoryRequest.getOrderNum() == null
//                || addCategoryRequest.getParentId() ==null){
//            return ApiRestResponse.error(MallExceptionEnum.NAME_NOT_NULL);
//        }
//        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        User currentUser = UserFilter.currentUser;
        if(currentUser == null){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        Boolean adminRole = userService.checkAdmin(currentUser);
        if(!adminRole){
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }else{
            categoryService.add(addCategoryRequest);
            return ApiRestResponse.success();
        }
    }
    @ApiOperation("update category in the backend")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(HttpSession session, @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest){
//        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        User currentUser = UserFilter.currentUser;
        if(currentUser == null){
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        boolean adminRole = userService.checkAdmin(currentUser);
        if(!adminRole){
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }else{
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryRequest, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        }
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
