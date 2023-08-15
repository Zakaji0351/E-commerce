package com.lzq.cloud.mall.categoryproduct.service;

import com.github.pagehelper.PageInfo;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.categoryproduct.model.request.AddProductRequest;
import com.lzq.cloud.mall.categoryproduct.model.request.ProductListRequest;


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

    void updateStock(Integer productId, Integer stock);
}
