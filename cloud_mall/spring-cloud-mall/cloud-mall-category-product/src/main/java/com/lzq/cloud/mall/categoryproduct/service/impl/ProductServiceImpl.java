package com.lzq.cloud.mall.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.cloud.mall.categoryproduct.model.dao.ProductMapper;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.categoryproduct.model.query.ProductListQuery;
import com.lzq.cloud.mall.categoryproduct.model.request.AddProductRequest;
import com.lzq.cloud.mall.categoryproduct.model.request.ProductListRequest;
import com.lzq.cloud.mall.categoryproduct.model.vo.CategoryVO;
import com.lzq.cloud.mall.categoryproduct.service.CategoryService;
import com.lzq.cloud.mall.categoryproduct.service.ProductService;
import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Override
    public void add(AddProductRequest addProductRequest){
        Product product = new Product();
        BeanUtils.copyProperties(addProductRequest, product);
        Product productOld = productMapper.selectByName(addProductRequest.getName());
        if(productOld != null){
            throw new MallException(MallExceptionEnum.PRODUCT_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if(count == 0){
            throw new MallException(MallExceptionEnum.PRODUCT_ADD_FAILURE);
        }
    }

    @Override
    public void update(Product updateProductRequest){
        Product productOld = productMapper.selectByName(updateProductRequest.getName());
        if(productOld != null && !productOld.getId().equals(updateProductRequest.getId())){
            throw new MallException(MallExceptionEnum.PRODUCT_UPDATE_FAILURE);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProductRequest);
        if(count == 0){
            throw new MallException(MallExceptionEnum.PRODUCT_UPDATE_FAILURE);
        }
    }
    @Override
    public void delete(Integer id){
        Product productOld = productMapper.selectByPrimaryKey(id);
        if(productOld == null){
            throw new MallException(MallExceptionEnum.PRODUCT_DELETE_FAILURE);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new MallException(MallExceptionEnum.PRODUCT_DELETE_FAILURE);
        }
    }
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus){
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list =  productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id){
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo list(ProductListRequest productListRequest){
        ProductListQuery productListQuery = new ProductListQuery();
        if(!StringUtils.isEmpty(productListRequest.getKeyword())){
            String keyword = new StringBuilder().append("%").append(productListRequest.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        if(productListRequest.getCategoryId() != null){
            List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(productListRequest.getCategoryId());
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListRequest.getCategoryId());
            getCategoryIds(categoryVOS, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //sort
        String orderBy = productListRequest.getOrderBy();
        if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListRequest.getPageNum(), productListRequest.getPageSize(), orderBy);
        }else{
            PageHelper.startPage(productListRequest.getPageNum(), productListRequest.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOS, List<Integer> categoryIds){
        for(int i = 0; i < categoryVOS.size(); i++){
            CategoryVO categoryVO = categoryVOS.get(i);
            if(categoryVO != null){
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }
    @Override
    public void updateStock(Integer productId, Integer stock){
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        productMapper.updateByPrimaryKeySelective(product);
    }
}
