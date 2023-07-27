package com.lzq.mall.service;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.model.pojo.Product;
import com.lzq.mall.model.request.AddProductRequest;
import com.lzq.mall.model.request.ProductListRequest;

import java.io.File;
import java.io.IOException;

public interface ProductService {
    void add(AddProductRequest addProductRequest);

    void update(Product updateProductRequest);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    Product detail(Integer id);

    PageInfo list(ProductListRequest productListRequest);

    void addProductByExcel(File destFile) throws IOException;
}
