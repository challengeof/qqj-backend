package com.mishu.cgwy.task.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.utils.StringUtils;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import net.sf.jxls.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bowen on 15/10/13.
 */


public class ExportExcelUtils {

    private static File dir = null;

    public static String excelFolderName = "/share/excel";

    static {
        dir = new File(excelFolderName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static final String PURCHASE_ORDER_TEMPLATE = "/template/purchase-order.xls";

    public static final String RETURN_NOTE_TEMPLATE = "/template/return-note.xls";

    public static final String STOCKUP_PURCHASE_ORDERS_TEMPLATE = "/template/stockup-purchase-orders.xls";

    public static final String ACCORDING_PURCHASE_ORDERS_TEMPLATE = "/template/according-purchase-orders.xls";

    public static final String PURCHASE_ORDER_ITEMS_TEMPLATE = "/template/purchase-order-items.xls";

    public static final String PURCHASE_ORDER_ITEMS_TOGETHER_TEMPLATE = "/template/purchase-order-items-together.xls";

    public static final String PURCHASE_ORDER_ITEMS_TOGETHER_WITH_RESULT_TEMPLATE = "/template/purchase-order-items-together.xls";

    public static final String RETURN_NOTES_TEMPLATE = "/template/return-notes.xls";

    public static final String VENDOR_ACCOUNTS_TEMPLATE = "/template/vendor-accounts.xls";

    public static final String ACCOUNT_PAYABLE_ITEMS_TEMPLATE = "/template/account-payable-items.xls";

    public static final String VENDOR_ACCOUNT_HISTORIES_TEMPLATE = "/template/vendor-account-histories.xls";

    public static final String ACCOUNT_PAYABLES_TEMPLATE = "/template/account-payables.xls";

    public static final String ACCOUNT_PAYABLE_WRITEOFFS_TEMPLATE = "/template/account-payable-writeoffs.xls";

    public static final String SKU_PRICE_LIST_TEMPLATE = "/template/sku-price-list.xls";

    public static final String SKU_FIXED_PRICE_LIST_TEMPLATE = "/template/sku-fixed-price-list.xls";

    public static final String SKU_PRICE_LIMIT_LIST_TEMPLATE = "/template/sku-price-limit-list.xls";

    public static final String SKU_PURCHASE_PRICE_LIST_TEMPLATE = "/template/sku-purchase-price-list.xls";

    public static final String SKU_SALE_PRICE_LIST_TEMPLATE = "/template/sku-sale-price-list.xls";

    public static final String SKU_PRICE_IMPORT_TEMPLATE = "/template/sku-price-import-template.xls";

    public static final String PAYMENT_LIST_TEMPLATE = "/template/payment-list.xls";

    public static final String ORDER_ITEM_LIST_TEMPLATE = "/template/order-item-list.xls";

    /**
     * @param jsonList    数据
     * @param fileName    导出excel的名字(加了当前时间)
     * @param HeaderTitle excel的标题
     * @param jsonKey     各个字段的名字
     * @param columnName
     * @return
     * @throws Exception
     */
    public static HttpEntity<byte[]> ExcelExporter(String jsonList,
                                                   String fileName, String HeaderTitle, String[] jsonKey, String[] columnName)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray dateList = new JSONArray(jsonList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("" + sdf.format(new Date()));
        for (int i = 0; i <= columnName.length; i++) {
            sheet.setColumnWidth(i, 6000);
        }
        HSSFCellStyle titleStyle = wb.createCellStyle();
        HSSFCellStyle stringStyle = wb.createCellStyle();
        HSSFCellStyle warnStyle = wb.createCellStyle();

        warnStyle.setFillForegroundColor(HSSFFont.COLOR_RED);

        HSSFFont titleFont = wb.createFont();
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleFont.setFontHeightInPoints((short) 12);

        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setWrapText(true);

        stringStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle.setFillBackgroundColor(HSSFColor.GREEN.index);
        stringStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stringStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        stringStyle.setWrapText(true);

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = null;
        cell = row.createCell((0));
        cell.setCellStyle(titleStyle);
        cell.setCellValue(HeaderTitle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (columnName.length - 1)));

        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = null;
        int mark0 = 0;
        for (int i = 0; i < columnName.length; i++) {
            String columnTitle = columnName[i];
            cell1 = row1.createCell(mark0++);
            cell1.setCellStyle(titleStyle);
            cell1.setCellValue(columnTitle);

        }
        HSSFCell celli = null;
        HSSFRow rowi = null;

        for (int i = 0; i < dateList.length(); i++) {
            rowi = sheet.createRow(i + 2);
            int mark = 0;
            JsonNode jsonNode = objectMapper.readTree(dateList.get(i).toString());
            for (int j = 0; j < columnName.length; j++) {
                String columnValue = "";
                if (!String.valueOf(jsonNode.get(jsonKey[j])).equals("null"))
                    columnValue = jsonNode.get(jsonKey[j]).asText();

                celli = rowi.createCell(mark++);
                celli.setCellStyle(stringStyle);
                celli.setCellValue(columnValue);

            }
        }
        String filename = fileName + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";

        File dir = new File("excel");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filename);

        FileOutputStream out = new FileOutputStream(file);
        try {
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            System.err.println("outputStream error:" + e);
        } finally {
            out.close();
        }
        return getHttpEntityXlsx(file.getPath());
    }

    public static HttpEntity<byte[]> getHttpEntityXlsx(String excelName) {
        byte[] readFileToByteArray = null;
        try {
            readFileToByteArray = FileUtils.readFileToByteArray(new File(excelName));

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xls"));
            excelName = excelName.substring(excelName.lastIndexOf("/") + 1);
            excelName = excelName.substring(excelName.lastIndexOf("\\") + 1).replace(" ", "_");
            header.set("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(excelName, "UTF-8"));
            header.setContentLength(readFileToByteArray.length);

            return new HttpEntity<byte[]>(readFileToByteArray, header);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpEntity<byte[]> download(String template) {
        try {
            final InputStream inputStream = ExportExcelUtils.class.getResourceAsStream(template);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                outputStream.write(buff, 0, rc);
            }
            byte[] byteArray = outputStream.toByteArray();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xls"));
            String fileName = template.substring(template.lastIndexOf("/") + 1);
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1).replace(" ", "_");
            header.set("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            header.setContentLength(byteArray.length);

            return new HttpEntity<byte[]>(byteArray, header);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static HttpEntity<byte[]> generateExcelBytes(Map<String, Object> beans, String fileName, String template) throws Exception {
        return generateExcelBytes(beans, fileName, template, true);
    }

    public static HttpEntity<byte[]> generateExcelBytes(Map<String, Object> beans, String fileName, String template, boolean autoSize) throws Exception {
        return getHttpEntityXlsx(generateSingleSheetExcel(beans, new File(dir, StringUtils.skipSpecialCharacters(fileName)), template, autoSize).getPath());
    }

    public static File generateSingleSheetExcel(Map<String, Object> beans, String fileName, String template) throws Exception {
        return generateSingleSheetExcel(beans, new File(dir, StringUtils.skipSpecialCharacters(System.currentTimeMillis() + "_" + fileName)), template, true);
    }

    public static <T> File generateMultiSheetExcel(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, String fileName, String template) throws Exception {
        return generateMultiSheetExcel(beans, beanName, sheetNames, beanParams, new File(dir, StringUtils.skipSpecialCharacters(System.currentTimeMillis() + "_" + fileName)), template, true);
    }

    private static File generateSingleSheetExcel(Map<String, Object> beans, File file, String template, boolean autoSize) throws Exception {
        final InputStream inputStream = ExportExcelUtils.class.getResourceAsStream(template);
        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            XLSTransformer transformer = new XLSTransformer();
            final Workbook workbook = transformer.transformXLS(inputStream, beans);
            if (autoSize) {
                autoSizeExcelRowHeight(workbook);
            }
            workbook.write(outputStream);
        } finally {
            inputStream.close();
            outputStream.close();
        }

        return file;
    }

    public static <T> HttpEntity<byte[]> generateExcelBytes(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, String fileName, String template, boolean autoSize) throws Exception {
        return getHttpEntityXlsx(generateMultiSheetExcel(beans, beanName, sheetNames, beanParams, new File(dir, fileName), template, autoSize).getPath());
    }

    public static <T> HttpEntity<byte[]> generateExcelBytes(ExportIterator<ExportDataVo<T>> vals, String beanName, Map<String, T> beanParams, String fileName, String template, boolean autoSize) throws Exception {
        return getHttpEntityXlsx(generateMultiSheetExcel(vals, beanName, beanParams, new File(dir, fileName), template, autoSize).getPath());
    }

    public static <T> HttpEntity<byte[]> generateExcelBytes(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, String fileName, String template) throws Exception {
        return generateExcelBytes(beans, beanName, sheetNames, beanParams, fileName, template, true);
    }

    private static <T> File generateMultiSheetExcel(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, File file, String template, boolean autoSize) throws InvalidFormatException,
            IOException {

        for (int i = 0; i < sheetNames.size(); i++) {
            sheetNames.set(i, String.format("%s_%s", (i + 1), StringUtils.skipSpecialCharacters(sheetNames.get(i))));
        }

        InputStream inputStream = ExportExcelUtils.class.getResourceAsStream(template);
        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            XLSTransformer transformer = new XLSTransformer();
            final Workbook workbook = transformer.transformMultipleSheetsList(inputStream, beans, sheetNames, beanName, beanParams, 0);
            if (autoSize) {
                autoSizeExcelRowHeight(workbook);
            }
            workbook.write(outputStream);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
            }
        }

        return file;
    }

    private static <T> File generateMultiSheetExcel(ExportIterator<ExportDataVo<T>> vals, String beanName, Map<String, T> beanParams, File file, String template, boolean autoSize) throws InvalidFormatException,
            IOException {

        InputStream inputStream = ExportExcelUtils.class.getResourceAsStream(template);
        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            MyXlsTransformer transformer = new MyXlsTransformer();
            final Workbook workbook = transformer.transformMultipleSheetsList(inputStream, vals, beanName, beanParams, 0);
            if (autoSize) {
                autoSizeExcelRowHeight(workbook);
            }
            workbook.write(outputStream);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
            }
        }

        return file;
    }

    private static void autoSizeExcelRowHeight(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            final Sheet sheet = workbook.getSheetAt(i);

            final Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                rowIterator.next().setHeight((short) -1);
            }
        }
    }

    public static <T> Workbook generateMultiSheetWorkBook(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, String template) throws InvalidFormatException,
            IOException {

        for (int i = 0; i < sheetNames.size(); i++) {
            sheetNames.set(i, String.format("%s_%s", (i + 1), StringUtils.skipSpecialCharacters(sheetNames.get(i))));
        }

        InputStream inputStream = ExportExcelUtils.class.getResourceAsStream(template);

        try {
            XLSTransformer transformer = new XLSTransformer();
            final Workbook workbook = transformer.transformMultipleSheetsList(inputStream, beans, sheetNames, beanName, beanParams, 0);
            return workbook;
        } finally {
            inputStream.close();
        }
    }

    public static HttpEntity<byte[]> generateExcelBytes(Workbook workbook, String fileName) throws Exception {

        File file = new File(dir, StringUtils.skipSpecialCharacters(fileName));
        final FileOutputStream outputStream = new FileOutputStream(file);
        try {
            workbook.write(outputStream);
        } finally {
            outputStream.close();
        }
        return getHttpEntityXlsx(file.getPath());
    }






    public static abstract class ExportIterator<T extends ExportDataVo> implements   Iterator<T>{

        private boolean needCheckNext=true;
        private T nextVal;

        @Override
        public synchronized boolean hasNext() {
            if(needCheckNext){
                nextVal=this.getNextVal();
                needCheckNext=false;
            }
            if(nextVal==null || nextVal.getVals()==null){
                return false;
            }
            if(nextVal.getVals() instanceof Collection){
                return !((Collection) nextVal.getVals()).isEmpty();
            }
            return true;
        }

        @Override
        public synchronized  T next() {
            if(hasNext()){
                needCheckNext=true;
                return nextVal;
            }
            throw new NoSuchElementException();
        }
        protected abstract T getNextVal();

        @Override
        public void remove() {}
    }


    public static class ExportDataVo<T> {
        private String sheetName;
        private T vals;
        public ExportDataVo(String sheetName, T vals) {
            this.sheetName = sheetName;
            this.vals = vals;
        }
        public String getSheetName() {
            return sheetName;
        }
        public T getVals() {
            return vals;
        }
    }


    private static class MyXlsTransformer extends  XLSTransformer{
        public  <T>  org.apache.poi.ss.usermodel.Workbook transformMultipleSheetsList(InputStream is,ExportIterator<ExportDataVo<T>> vals ,String beanName, Map<String, T> beanParams, int startSheetNum) throws ParsePropertyException, InvalidFormatException {
            org.apache.poi.ss.usermodel.Workbook hssfWorkbook = null;
            try {
                if( beanParams!=null && beanParams.containsKey( beanName )){
                    throw new IllegalArgumentException("Selected bean name '" + beanName + "' already exists in the bean map");
                }
                if( beanName==null ){
                    throw new IllegalArgumentException(("Bean name must not be null" ) );
                }
                if( beanParams == null ){
                    beanParams = new HashMap<String, T>();
                }
                hssfWorkbook = WorkbookFactory.create(is);

                for (int sheetNo = 0; sheetNo < hssfWorkbook.getNumberOfSheets(); sheetNo++) {
                    final String spreadsheetName = hssfWorkbook.getSheetName(sheetNo);
                    if (!isSpreadsheetToRemove(spreadsheetName)) {
                        if (isSpreadsheetToRename(spreadsheetName)) {
                            hssfWorkbook.setSheetName(sheetNo, getSpreadsheetToReName(spreadsheetName));
                        }
                        org.apache.poi.ss.usermodel.Sheet hssfSheet = hssfWorkbook.getSheetAt(sheetNo);
                        if( startSheetNum == sheetNo && vals != null && vals.hasNext()){

//                          for (int i = 0, c2 = objects.size(); i < c2 ; i++) {
                            int i=0;
                            while(vals.hasNext()){
                                ExportDataVo<T> bean = vals.next();
                                String sheetName = String.format("%s_%s", (i + 1), StringUtils.skipSpecialCharacters(bean.getSheetName()));

                                String beanKey = beanName;
                                org.apache.poi.ss.usermodel.Sheet newSheet;
                                if( i != 0 ){
                                    beanKey = beanName+i;
                                    newSheet = hssfWorkbook.createSheet( sheetName );
                                    Util.copySheets(newSheet, hssfSheet, beanName, beanKey );
                                    Util.copyPageSetup(newSheet, hssfSheet);
                                    Util.copyPrintSetup(newSheet, hssfSheet);
                                }else{
                                    hssfWorkbook.setSheetName( sheetNo, sheetName);
                                }
                                beanParams.put( beanKey, bean.getVals() );

                                i++;
                            }

                        }
                    } else {
                        // let's remove spreadsheet
                        hssfWorkbook.removeSheetAt(sheetNo);
                        sheetNo--;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if( hssfWorkbook != null ){
                for(int i = 0;i < hssfWorkbook.getNumberOfSheets();i++)
                {
                    Util.setPrintArea(hssfWorkbook,i);
                }
            }
            transformWorkbook( hssfWorkbook, beanParams );
            return hssfWorkbook;
        }
    }


}
