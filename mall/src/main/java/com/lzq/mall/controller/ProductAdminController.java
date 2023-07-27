package com.lzq.mall.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.mall.common.ApiRestResponse;
import com.lzq.mall.common.Constant;
import com.lzq.mall.common.ValidList;
import com.lzq.mall.exception.MallException;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.model.pojo.Product;
import com.lzq.mall.model.request.AddProductRequest;
import com.lzq.mall.model.request.UpdateProductRequest;
import com.lzq.mall.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
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
    @Value("${file.upload.uri}")
    String uri;
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
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        System.out.println(destFile.toString());
        createFile(file, fileDirectory, destFile);
//        try {
//            return ApiRestResponse.success(getHost(new URI(request.getRequestURL() + "")) + "/images/" + newFileName);
//        } catch (URISyntaxException e) {
//            return ApiRestResponse.error(MallExceptionEnum.IMAGE_UPLOAD_FAILURE);
//        }
        String address = uri;
        return ApiRestResponse.success("http://" + address + "/images/" + newFileName);
    }

    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
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
    @PostMapping("/admin/upload/product")
    @ApiOperation("batch upload product")
    public ApiRestResponse uploadProduct(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffix;
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        createFile(file, fileDirectory, destFile);
//        if(!fileDirectory.exists()){
//            if(!fileDirectory.mkdir()){
//                throw new MallException(MallExceptionEnum.MKDIR_FAILURE);
//            }
//        }
//        try{
//            multipartFile.transferTo(destFile);
//        }catch(IOException e){
//            e.printStackTrace();
//        }
        productService.addProductByExcel(destFile);
        return ApiRestResponse.success();
    }
    @PostMapping("/admin/upload/image")
    public ApiRestResponse uploadImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffix;
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        System.out.println(destFile.toString());
        createFile(file, fileDirectory, destFile);
        Thumbnails.of(destFile).size(Constant.IMAGE_SIZE, Constant.IMAGE_SIZE)
                .watermark(Positions.BOTTOM_LEFT, ImageIO.read(new File(Constant.FILE_UPLOAD_DIR + Constant.WATER_MARK_JPG)), Constant.OPACITY)
                .toFile(new File(Constant.FILE_UPLOAD_DIR + newFileName));

        try {
            return ApiRestResponse.success(getHost(new URI(request.getRequestURL() + "")) + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MallExceptionEnum.IMAGE_UPLOAD_FAILURE);
        }
    }
    @PostMapping("/admin/product/batchUpdate")
    @ApiOperation("update a batch of product in the backend")
    public ApiRestResponse batchUpdateProduct(@Valid @RequestBody List<UpdateProductRequest> updateProductRequestList){
        for (int i = 0; i < updateProductRequestList.size(); i++) {
            UpdateProductRequest updateProductRequest =  updateProductRequestList.get(i);
            if(updateProductRequest.getPrice() < 1){
                throw new MallException(MallExceptionEnum.PRODUCT_PRICE_LOW);
            }
            if(updateProductRequest.getStock() > 10000){
                throw new MallException(MallExceptionEnum.PRODUCT_STOCK_HIGH);
            }
            Product product = new Product();
            BeanUtils.copyProperties(updateProductRequest, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }
    @PostMapping("/admin/product/validListUpdate")
    @ApiOperation("update a batch of product in the backend using valid list")
    public ApiRestResponse batchUpdateProduct2(@Valid @RequestBody ValidList<UpdateProductRequest> updateProductRequestList){
        for (int i = 0; i < updateProductRequestList.size(); i++) {
            UpdateProductRequest updateProductRequest =  updateProductRequestList.get(i);
            Product product = new Product();
            BeanUtils.copyProperties(updateProductRequest, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
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
