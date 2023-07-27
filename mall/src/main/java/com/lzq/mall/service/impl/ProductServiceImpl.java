package com.lzq.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.mall.common.Constant;
import com.lzq.mall.exception.MallException;
import com.lzq.mall.exception.MallExceptionEnum;
import com.lzq.mall.model.dao.ProductMapper;
import com.lzq.mall.model.pojo.Product;
import com.lzq.mall.model.query.ProductListQuery;
import com.lzq.mall.model.request.AddProductRequest;
import com.lzq.mall.model.request.ProductListRequest;
import com.lzq.mall.model.vo.CategoryVO;
import com.lzq.mall.service.CategoryService;
import com.lzq.mall.service.ProductService;
import com.lzq.mall.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    public void addProductByExcel(File destFile) throws IOException {
        List<Product> products = readProductFromExcel(destFile);
        for (int i = 0; i < products.size(); i++) {
            Product product =  products.get(i);
            Product productOld = productMapper.selectByName(product.getName());
            if(productOld != null){
                throw new MallException(MallExceptionEnum.PRODUCT_EXISTED);
            }
            int count = productMapper.insertSelective(product);
            if(count == 0){
                throw new MallException(MallExceptionEnum.PRODUCT_ADD_FAILURE);
            }
        }
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
    private List<Product> readProductFromExcel(File excelFile) throws IOException {
        ArrayList<Product> listProducts = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        while(iterator.hasNext()){
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            Product aProduct = new Product();
            while(cellIterator.hasNext()){
                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex();
                switch(columnIndex){
                    case 0:
                        aProduct.setName((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 1:
                        aProduct.setImage((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 2:
                        aProduct.setDetail((String) ExcelUtils.getCellValue(nextCell));
                        break;
                    case 3:
                        Double cellValue = (Double) ExcelUtils.getCellValue(nextCell);
                        aProduct.setCategoryId(cellValue.intValue());
                        break;
                    case 4:
                        cellValue = (Double) ExcelUtils.getCellValue(nextCell);
                        aProduct.setPrice(cellValue.intValue());
                        break;
                    case 5:
                        cellValue = (Double) ExcelUtils.getCellValue(nextCell);
                        aProduct.setStock(cellValue.intValue());
                        break;
                    case 6:
                        cellValue = (Double) ExcelUtils.getCellValue(nextCell);
                        aProduct.setStatus(cellValue.intValue());
                        break;
                    default:
                        break;
                }
            }
            listProducts.add(aProduct);
        }
        workbook.close();
        inputStream.close();
        return listProducts;

    }
}
