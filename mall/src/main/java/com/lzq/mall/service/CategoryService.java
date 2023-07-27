package com.lzq.mall.service;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.model.pojo.Category;
import com.lzq.mall.model.request.AddCategoryRequest;
import com.lzq.mall.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryRequest addCategoryRequest);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
