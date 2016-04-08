package com.mishu.cgwy.task.util;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Created by wangguodong on 16/3/1.
 */
public class ExcelUtils {
    public static String getStringValue(Cell cell) {
        if(null == cell) {
            return null;
        }
        int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_BLANK:
                return null;
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_ERROR :
                return String.valueOf(cell.getErrorCellValue());
            case Cell.CELL_TYPE_FORMULA :
                return String.valueOf(cell.getCellFormula());
            case Cell.CELL_TYPE_NUMERIC :
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING :
                return cell.getStringCellValue();
            default :
                return null;
        }
    }
}
