package com.lzq.mall.utils;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtils {
    public static Object getCellValue(Cell cell){
        switch(cell.getCellTypeEnum()){
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }
}
