package com.lzq.cloud.mall.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.lzq.cloud.mall.categoryproduct.service.CategoryService;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import com.lzq.cloud.mall.categoryproduct.model.dao.CategoryMapper;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Category;
import com.lzq.cloud.mall.categoryproduct.model.request.AddCategoryRequest;
import com.lzq.cloud.mall.categoryproduct.model.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryRequest addCategoryRequest){
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryRequest, category);
        Category categoryOld = categoryMapper.selectByName(category.getName());
        if(categoryOld != null){
            throw new MallException(MallExceptionEnum.CATEGORY_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if(count == 0){
            throw new MallException(MallExceptionEnum.CATEGORY_ADD_FAILURE);
        }
    }

    @Override
    public void update(Category updateCategory){
        if(updateCategory.getName() != null){
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if(categoryOld != null && !categoryOld.getName().equals(updateCategory.getName())){
                throw new MallException(MallExceptionEnum.CATEGORY_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if(count == 0){
            throw new MallException(MallExceptionEnum.CATEGORY_UPDATE_FAILURE);
        }
    }

    @Override
    public void delete(Integer id){
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if(categoryOld == null){
            throw new MallException(MallExceptionEnum.CATEGORY_DELETE_FAILTURE);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new MallException(MallExceptionEnum.CATEGORY_DELETE_FAILTURE);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }
    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        List<CategoryVO> categoryVOList = new ArrayList<>();
        resursivelyFindCategories(categoryVOList, parentId);
        return categoryVOList;
    }
    public void resursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId){
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if(!CollectionUtils.isEmpty(categoryList)){
            for(int i = 0; i < categoryList.size(); i++){
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                resursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
