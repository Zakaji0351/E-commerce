package com.lzq.cloud.mall.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.cloud.mall.categoryproduct.common.ProductConstant;
import com.lzq.cloud.mall.categoryproduct.model.pojo.Product;
import com.lzq.cloud.mall.categoryproduct.model.request.AddProductRequest;
import com.lzq.cloud.mall.categoryproduct.model.request.UpdateProductRequest;
import com.lzq.cloud.mall.categoryproduct.service.ProductService;
import com.lzq.cloud.mall.common.common.ApiRestResponse;
import com.lzq.cloud.mall.common.common.Constant;
import com.lzq.cloud.mall.common.exception.MallException;
import com.lzq.cloud.mall.common.exception.MallExceptionEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
public class ProductAdminController {
    @Autowired
    ProductService productService;
    @Value("${file.upload.ip}")
    String ip;
    @Value("${file.upload.port}")
    Integer port;
    @PostMapping("/admin/product/add")
    @ApiOperation("add a product in the backend")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductRequest addProductRequest){
        productService.add(addProductRequest);
        return ApiRestResponse.success();
    }
    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffix;
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);
        System.out.println(destFile.toString());
        createFile(file, fileDirectory, destFile);
        try {
            return ApiRestResponse.success(getHost(new URI(request.getRequestURL() + "")) + "/category-product/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MallExceptionEnum.IMAGE_UPLOAD_FAILURE);
        }
    }

    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), ip, port, null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }
    @PostMapping("/admin/product/update")
    @ApiOperation("update a product in the backend")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductRequest updateProductRequest){
        Product product = new Product();
        BeanUtils.copyProperties(updateProductRequest, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/delete")
    @ApiOperation("delete a product in the backend")
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/batchUpdateSellStatus")
    @ApiOperation("product batch operations")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus){
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/product/list")
    @ApiOperation("product list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


    private static void createFile(MultipartFile file, File fileDirectory, File destFile) {
        if(!fileDirectory.exists()){
            if(!fileDirectory.mkdir()){
                throw new MallException(MallExceptionEnum.MKDIR_FAILURE);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
