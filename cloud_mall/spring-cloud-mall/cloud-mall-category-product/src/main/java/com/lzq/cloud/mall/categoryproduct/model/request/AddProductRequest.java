package com.lzq.cloud.mall.categoryproduct.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AddProductRequest {
    @NotNull(message = "the name of the product can't be null")
    private String name;
    @NotNull(message = "the image of the product can't be null")
    private String image;

    private String detail;
    @NotNull(message = "the category of the product can't be null")
    private Integer categoryId;
    @NotNull(message = "the price of the product can't be null")
    @Min(value = 1, message = "the price can't be smaller than 1")
    private Integer price;
    @NotNull(message = "the stock of the product can't be null")
    @Max(value = 10000, message = "the quantity of the product can't be greater than 10000")
    private Integer stock;

    private Integer status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
