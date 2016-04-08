package com.mishu.cgwy.stock.facade;

import com.google.common.collect.Collections2;
import com.google.common.collect.ComparisonChain;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.domain.SkuVendor;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.*;
import com.mishu.cgwy.stock.repository.DepotRepository;
import com.mishu.cgwy.stock.service.StockInService;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.stock.service.StockService;
import com.mishu.cgwy.stock.service.StockTotalDailyService;
import com.mishu.cgwy.stock.wrapper.*;
import com.mishu.cgwy.task.util.ExcelUtils;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 15/9/22.
 */
@Service
public class StockExportFacade {

    @Autowired
    private StockFacade stockFacade;
    @Autowired
    private StockInFacade stockInFacade;
    @Autowired
    private StockInService stockInService;
    @Autowired
    private StockOutFacade stockOutFacade;
    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private SkuVendorService skuVendorService;
    @Autowired
    private VendorService vendorService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockAdjustFacade stockAdjustFacade;
    @Autowired
    private StockTotalDailyService stockTotalDailyService;

    private static final String STOCKTOTAL_LIST = "/template/stockTotal-list.xls";
    private static final String STOCKTOTALDAILY_LIST = "/template/stockTotalDaily-list.xls";
    private static final String STOCKDEPOT_LIST = "/template/stockDepot-list.xls";
    private static final String STOCKIN_BILL = "/template/stockIn-bill.xls";
    private static final String STOCKIN_LIST = "/template/stockIn-list.xls";
    private static final String STOCKIN_RETURN_LIST = "/template/stockIn-return-list.xls";
    private static final String STOCKINITEM_LIST = "/template/stockInItem-list.xls";
    private static final String STOCKINITEM_RETURN_LIST = "/template/stockInItem-return-list.xls";
    private static final String STOCKOUT_ORDER_BILL = "/template/stockOut-order-bill.xls";
    private static final String STOCKOUT_ORDER_LIST = "/template/stockOut-order-list.xls";
    private static final String STOCKOUTITEM_ORDER_LIST = "/template/stockOutItem-order-list.xls";
    private static final String STOCKOUT_PURCHASERETURN_BILL = "/template/stockOut-purchaseReturn-bill.xls";
    private static final String STOCKOUT_PURCHASERETURN_LIST = "/template/stockOut-purchaseReturn-list.xls";
    private static final String STOCKOUTITEM_PURCHASERETURN_LIST = "/template/stockOutItem-purchaseReturn-list.xls";
    private static final String STOCKOUT_TRANSFER_BILL = "/template/stockOut-transfer-bill.xls";
    private static final String STOCKOUT_TRANSFER_LIST = "/template/stockOut-transfer-list.xls";
    private static final String STOCKOUTITEM_TRANSFER_LIST = "/template/stockOutItem-transfer-list.xls";
    private static final String STOCKOUT_TOTAL_LIST = "/template/stockOut-total-list.xls";
    private static final String PICK_SKU_XLS_TEMPLATE = "/template/pick-sku-template.xls";
    private static final String PICK_TRACKER_XLS_TEMPLATE = "/template/pick-tracker-template.xls";
    private static final String ASSOCIATE_XLS_TEMPLATE = "/template/associate-template.xls";
    private static final String BARCODE_XLS_TEMPLATE = "/template/barcode-template.xls";
    private static final String STOCKOUT_OUT_LIST = "/template/stockOut-out-list.xls";
    private static final String INCOME_DAILY_REPORT = "/template/income-daily-report.xls";
    private static final String NOTMATCH_XLS_TEMPLATE = "/template/notmatch-template.xls";
    private static final String STOCKSHELF_LIST = "/template/stockShelf-list.xls";
    private static final String STOCKADJUST_LIST = "/template/stockAdjust-list.xls";
    private static final String STOCK_ONSHELF_XLS_TEMPLATE = "/template/stock-onshelf-template.xls";
    private static final String STOCKEXPIRATION_LIST = "/template/stockExpiration-list.xls";
    private static final String STOCKDULLSALE_LIST = "/template/stockDullSale-list.xls";
    private static final String STOCKOUT_RECEIVE_LIST = "/template/stockOut-receive-list.xls";

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockTotalList(StockTotalRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockTotalWrapper> page = stockFacade.getStockTotalList(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockTotalList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKTOTAL_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockTotalDailyList(StockTotalDailyRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QuerySummationResponse<StockTotalDailyWrapper> page = stockFacade.getStockTotalDailyList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockTotalDailyList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKTOTALDAILY_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockDepotList(StockQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockWrapper> page = stockFacade.findDepotStocks(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockDepotList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKDEPOT_LIST);
    }

    @Transactional
    public HttpEntity<byte[]> exportStockInBills(Long[] ids, AdminUser operator) throws Exception {

        List<StockIn> stockInList = stockInService.getStockInByIds(ids);
        if (stockInList == null || stockInList.isEmpty()) {
            return null;
        }
        stockInService.updateStockInPrintStatus(ids);

        Map<String, Object> beans = new HashMap<>();
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockInBills-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        List<String> sheetNames = new ArrayList<>();
        for (StockIn stockIn : stockInList) {
            sheetNames.add(String.format("入库单号-%s", stockIn.getId()));
        }

        return ExportExcelUtils.generateExcelBytes(stockInList, "stockIn", sheetNames, beans, fileName, STOCKIN_BILL, false);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockInList(StockInRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockInWrapper> page = stockInFacade.getStockInList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("stockInType", request.getStockInType() == Integer.MAX_VALUE ? "全部" : StockInType.fromInt(request.getStockInType()).getName());
        beans.put("stockInStatus", request.getStockInStatus() == Integer.MAX_VALUE ? "全部" : StockInStatus.fromInt(request.getStockInStatus()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockInList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        String templateXls = request.getSaleReturn() != null && request.getSaleReturn().intValue() == 1 ? STOCKIN_RETURN_LIST : STOCKIN_LIST;
        return ExportExcelUtils.generateExcelBytes(beans, fileName, templateXls);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockInItemList(StockInRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QuerySummationResponse<StockInItemWrapper> page = stockInFacade.getStockInItemList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("stockInType", request.getStockInType() == Integer.MAX_VALUE ? "全部" : StockInType.fromInt(request.getStockInType()).getName());
        beans.put("stockInStatus", request.getStockInStatus() == Integer.MAX_VALUE ? "全部" : StockInStatus.fromInt(request.getStockInStatus()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockInItemList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        String templateXls = request.getSaleReturn() != null && request.getSaleReturn().intValue() == 1 ? STOCKINITEM_RETURN_LIST : STOCKINITEM_LIST;
        return ExportExcelUtils.generateExcelBytes(beans, fileName, templateXls);
    }

    @Transactional
    public HttpEntity<byte[]> exportStockOutBills(Long[] ids, AdminUser operator) throws Exception {

        List<StockOut> stockOutList = stockOutService.getStockOutByIds(ids);
        if (stockOutList == null || stockOutList.isEmpty()) {
            return null;
        }
        stockOutService.updateStockOutPrintStatus(ids);
        StockOutType type = StockOutType.fromInt(stockOutList.get(0).getType());

        Map<String, Object> beans = new HashMap<>();
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockOutBills-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        List<String> sheetNames = new ArrayList<>();
        for (StockOut stockOut : stockOutList) {
            sheetNames.add(String.format("出库单号-%s", stockOut.getId()));
        }

        HttpEntity<byte[]> excel = null;
        if (type == StockOutType.ORDER) {
            Collections.sort(stockOutList, new Comparator<StockOut>() {
                @Override
                public int compare(StockOut s1, StockOut s2) {
                    return s1.getOrderGroup().getTracker().getId().compareTo(s2.getOrderGroup().getTracker().getId());
                }
            });
            //  商品行根据内容增加行高，不再缩小字体了
            // excel = ExportExcelUtils.generateExcelBytes(stockOutList, "stockOut", sheetNames, beans, fileName, STOCKOUT_ORDER_BILL, false);
            Workbook workbook = ExportExcelUtils.generateMultiSheetWorkBook(stockOutList, "stockOut", sheetNames, beans, STOCKOUT_ORDER_BILL);
            int sheetNums = workbook.getNumberOfSheets();
            int begRow = 6;
            int standardSize = 34;
            for (int si = 0; si < sheetNums; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                StockOut sheetOut = stockOutList.get(si);

                List<StockOutItem> sheetItems = new ArrayList<>(Collections2.filter(sheetOut.getStockOutItems(),
                    new com.google.common.base.Predicate<StockOutItem>() {
                        @Override
                        public boolean apply(StockOutItem item) {
                            return !StockOutItemStatus.CANCEL.getValue().equals(item.getStatus());
                        }
                    }));

                float pointHeight = sheet.getRow(begRow).getHeightInPoints();
                int rows = sheetItems.size();

                for (int rj = begRow; rj < begRow+rows; rj++) {
                    Cell cell = sheet.getRow(rj).getCell(7);
                    String cellValue = ExcelUtils.getStringValue(cell);
                    int valueSize = cellValue.getBytes("GBK").length;
                    if (valueSize > standardSize) {
                        int multiple = (int)Math.ceil(valueSize/(double)standardSize);
                        sheet.getRow(rj).setHeightInPoints(pointHeight*multiple);
                    }
                }
            }
            excel = ExportExcelUtils.generateExcelBytes(workbook, fileName);

        } else if (type == StockOutType.PURCHASERETURN) {
            //excel = ExportExcelUtils.generateExcelBytes(stockOutList, "stockOut", sheetNames, beans, fileName, STOCKOUT_PURCHASERETURN_BILL, false);
            Workbook workbook = ExportExcelUtils.generateMultiSheetWorkBook(stockOutList, "stockOut", sheetNames, beans, STOCKOUT_PURCHASERETURN_BILL);
            int sheetNums = workbook.getNumberOfSheets();
            int begRow = 6;
            int standardSize = 34;
            for (int si = 0; si < sheetNums; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                StockOut sheetOut = stockOutList.get(si);

                List<StockOutItem> sheetItems = new ArrayList<>(Collections2.filter(sheetOut.getStockOutItems(),
                        new com.google.common.base.Predicate<StockOutItem>() {
                            @Override
                            public boolean apply(StockOutItem item) {
                                return StockOutItemStatus.DISTRIBUTED.getValue().equals(item.getStatus());
                            }
                        }));

                float pointHeight = sheet.getRow(begRow).getHeightInPoints();
                int rows = sheetItems.size();

                for (int rj = begRow; rj < begRow+rows; rj++) {
                    Cell cell = sheet.getRow(rj).getCell(7);
                    String cellValue = ExcelUtils.getStringValue(cell);
                    int valueSize = cellValue.getBytes("GBK").length;
                    if (valueSize > standardSize) {
                        int multiple = (int)Math.ceil(valueSize/(double)standardSize);
                        sheet.getRow(rj).setHeightInPoints(pointHeight*multiple);
                    }
                }
            }
            excel = ExportExcelUtils.generateExcelBytes(workbook, fileName);
        } else if (type == StockOutType.TRANSFER) {
            //excel = ExportExcelUtils.generateExcelBytes(stockOutList, "stockOut", sheetNames, beans, fileName, STOCKOUT_TRANSFER_BILL, false);
            Workbook workbook = ExportExcelUtils.generateMultiSheetWorkBook(stockOutList, "stockOut", sheetNames, beans, STOCKOUT_TRANSFER_BILL);
            int sheetNums = workbook.getNumberOfSheets();
            int begRow = 4;
            int standardSize = 34;
            for (int si = 0; si < sheetNums; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                StockOut sheetOut = stockOutList.get(si);

                List<StockOutItem> sheetItems = new ArrayList<>(Collections2.filter(sheetOut.getStockOutItems(),
                        new com.google.common.base.Predicate<StockOutItem>() {
                            @Override
                            public boolean apply(StockOutItem item) {
                                return StockOutItemStatus.DISTRIBUTED.getValue().equals(item.getStatus());
                            }
                        }));

                float pointHeight = sheet.getRow(begRow).getHeightInPoints();
                int rows = sheetItems.size();

                for (int rj = begRow; rj < begRow+rows; rj++) {
                    Cell cell = sheet.getRow(rj).getCell(7);
                    String cellValue = ExcelUtils.getStringValue(cell);
                    int valueSize = cellValue.getBytes("GBK").length;
                    if (valueSize > standardSize) {
                        int multiple = (int)Math.ceil(valueSize/(double)standardSize);
                        sheet.getRow(rj).setHeightInPoints(pointHeight*multiple);
                    }
                }
            }
            excel = ExportExcelUtils.generateExcelBytes(workbook, fileName);
        }
        return excel;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOutList(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QuerySummationResponse<StockOutWrapper> page = stockOutFacade.getStockOutList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("sourceDepot", request.getSourceDepotId() == null ? "全部" : depotRepository.getOne(request.getSourceDepotId()).getName());
        beans.put("targetDepot", request.getTargetDepotId() == null ? "全部" : depotRepository.getOne(request.getTargetDepotId()).getName());
        beans.put("stockOutStatus", request.getStockOutStatus() == Integer.MAX_VALUE ? "全部" : StockOutStatus.fromInt(request.getStockOutStatus()).getName());
        beans.put("startSendDate", request.getStartSendDate());
        beans.put("endSendDate", request.getEndSendDate());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockOutList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        HttpEntity<byte[]> excel = null;
        if (request.getStockOutType() == StockOutType.ORDER.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_ORDER_LIST);
        } else if (request.getStockOutType() == StockOutType.PURCHASERETURN.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_PURCHASERETURN_LIST);
        } else if (request.getStockOutType() == StockOutType.TRANSFER.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_TRANSFER_LIST);
        }
        return excel;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOutItemList(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockOutItemWrapper> page = stockOutFacade.getStockOutItemList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("sourceDepot", request.getSourceDepotId() == null ? "全部" : depotRepository.getOne(request.getSourceDepotId()).getName());
        beans.put("targetDepot", request.getTargetDepotId() == null ? "全部" : depotRepository.getOne(request.getTargetDepotId()).getName());
        beans.put("stockOutStatus", request.getStockOutStatus() == Integer.MAX_VALUE ? "全部" : StockOutStatus.fromInt(request.getStockOutStatus()).getName());
        beans.put("startSendDate", request.getStartSendDate());
        beans.put("endSendDate", request.getEndSendDate());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockOutItemList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        HttpEntity<byte[]> excel = null;
        if (request.getStockOutType() == StockOutType.ORDER.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUTITEM_ORDER_LIST);
        } else if (request.getStockOutType() == StockOutType.PURCHASERETURN.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUTITEM_PURCHASERETURN_LIST);
        } else if (request.getStockOutType() == StockOutType.TRANSFER.getValue()) {
            excel = ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUTITEM_TRANSFER_LIST);
        }
        return excel;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOutTotalList(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QuerySummationResponse<StockOutWrapper> page = stockOutFacade.getStockOutList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("stockOutType", request.getStockOutType() == Integer.MAX_VALUE ? "全部" : StockOutType.fromInt(request.getStockOutStatus()).getName());
        beans.put("stockOutStatus", request.getStockOutStatus() == Integer.MAX_VALUE ? "全部" : StockOutStatus.fromInt(request.getStockOutStatus()).getName());
        beans.put("startSendDate", request.getStartSendDate());
        beans.put("endSendDate", request.getEndSendDate());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockOutTotalList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_TOTAL_LIST);
    }

    @Transactional
    public File exportPickExcel(StockOutRequest stockOutRequest, AdminUser operator) throws IOException, InvalidFormatException {

        stockOutRequest.setPage(0);
        stockOutRequest.setPageSize(Integer.MAX_VALUE);
        Page<StockOut> page = stockOutService.getStockOutList(stockOutRequest, operator);

        String depotName = null;
        Map<Long, Map<String, StockOutItem>> itemMap = new HashMap<>();
        List<StockOut> pickStockOuts = new ArrayList<>();
        for (StockOut stockOut : page.getContent()) {
            if (depotName == null) {
                depotName = stockOut.getDepot().getName();
            }

            /*没加货位前
            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                    if (itemMap.containsKey(stockOutItem.getSku().getId())) {
                        StockOutItem soi = itemMap.get(stockOutItem.getSku().getId());
                        soi.setExpectedQuantity(soi.getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                    } else {
                        StockOutItem soi = stockOutItem.clone();
                        itemMap.put(stockOutItem.getSku().getId(), soi);
                    }

                    if (!stockOut.isPickPrint()) {
                        stockOut.setPickPrint(true);
                        pickStockOuts.add(stockOut);
                    }
                }
            }*/

            List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());
            for (Stock occupiedStock : occupiedStocks) {

                String shelfName = occupiedStock.getShelf() != null ? occupiedStock.getShelf().getName() : "";
                if (itemMap.containsKey(occupiedStock.getSku().getId())) {
                    Map<String, StockOutItem> shelfMap = itemMap.get(occupiedStock.getSku().getId());
                    if (shelfMap.containsKey(shelfName)) {
                        StockOutItem soi = shelfMap.get(shelfName);
                        soi.setExpectedQuantity(soi.getExpectedQuantity() + occupiedStock.getStock());
                    } else {
                        StockOutItem soi = new StockOutItem();
                        soi.setSku(occupiedStock.getSku());
                        soi.setExpectedQuantity(occupiedStock.getStock());
                        shelfMap.put(shelfName, soi);
                        itemMap.put(occupiedStock.getSku().getId(), shelfMap);
                    }

                } else {
                    Map<String, StockOutItem> shelfMap = new HashMap<>();
                    StockOutItem soi = new StockOutItem();
                    soi.setSku(occupiedStock.getSku());
                    soi.setExpectedQuantity(occupiedStock.getStock());
                    shelfMap.put(shelfName, soi);
                    itemMap.put(occupiedStock.getSku().getId(), shelfMap);
                }

                if (!stockOut.isPickPrint()) {
                    stockOut.setPickPrint(true);
                    pickStockOuts.add(stockOut);
                }
            }
        }
        if (pickStockOuts.size() > 0) {
            for (StockOut stockOut : pickStockOuts) {
                stockOutService.saveStockOut(stockOut);
            }
        }
        List<StockOutPickWrapper> stockOutPickWrappers = new ArrayList<>();
        /*没加货位前
        List<StockOutItemWrapper> stockOutItemWappers = new ArrayList<>();
        for (Map.Entry<Long, StockOutItem> itemEntry : itemMap.entrySet()) {

            StockOutItem soi = itemEntry.getValue();
            StockOutItemWrapper soiw = new StockOutItemWrapper();
            soiw.setExpectedQuantity(soi.getExpectedQuantity());
            soiw.setSkuId(soi.getSku().getId());
            soiw.setSkuName(soi.getSku().getName());
            soiw.setSpecification(soi.getSku().getProduct().getSpecification());
            soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
            int cap = soi.getSku().getCapacityInBundle();
            if (cap > 1) {
                int intNum = soiw.getExpectedQuantity() / cap;
                int modNum = soiw.getExpectedQuantity() % cap;
                if (intNum > 0) {
                    if (modNum > 0) {
                        soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                    } else {
                        soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit());
                    }
                }
            }
            stockOutItemWappers.add(soiw);
        }*/
        for (Map.Entry<Long, Map<String, StockOutItem>> entry : itemMap.entrySet()) {

            Map<String, StockOutItem> shelfMap = entry.getValue();
            for (Map.Entry<String, StockOutItem> itemEntry : shelfMap.entrySet()) {

                StockOutItem soi = itemEntry.getValue();
                StockOutPickWrapper soiw = new StockOutPickWrapper();
                soiw.setExpectedQuantity(soi.getExpectedQuantity());
                soiw.setSkuId(soi.getSku().getId());
                soiw.setSkuName(soi.getSku().getName());
                soiw.setSpecification(soi.getSku().getProduct().getSpecification());
                soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
                soiw.setShelfName(itemEntry.getKey());
                int cap = soi.getSku().getCapacityInBundle();
                if (cap > 1) {
                    int intNum = soiw.getExpectedQuantity() / cap;
                    int modNum = soiw.getExpectedQuantity() % cap;
                    if (intNum > 0) {
                        if (modNum > 0) {
                            soiw.setSkuBundleUnitDes(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                        } else {
                            soiw.setSkuBundleUnitDes(intNum + soi.getSku().getBundleUnit());
                        }
                    }
                }
                stockOutPickWrappers.add(soiw);
            }
        }

        Collections.sort(stockOutPickWrappers, new Comparator<StockOutPickWrapper>() {
            @Override
            public int compare(StockOutPickWrapper o1, StockOutPickWrapper o2) {
                return ComparisonChain.start()
                        .compare(o1.getShelfName(), o2.getShelfName())
                        .compare(o1.getSkuId(), o2.getSkuId())
                        .result();
            }
        });

        Map<String, Object> beans = new HashMap<>();

        beans.put("depotName", depotName);
        beans.put("nums", page.getTotalElements());
        beans.put("printTime", new Date());
        beans.put("stockOutItems", stockOutPickWrappers);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "picksku" + sdf.format(new Date()) + ".xls";
        File file = new File(ExportExcelUtils.excelFolderName, fileName);

        return this.generateSingleSheetExcel(beans, file, PICK_SKU_XLS_TEMPLATE, false);
    }

    @Transactional
    public File exportPickTrackerExcel(StockOutRequest stockOutRequest, AdminUser operator) throws IOException, InvalidFormatException {

        stockOutRequest.setPage(0);
        stockOutRequest.setPageSize(Integer.MAX_VALUE);
        Page<StockOut> page = stockOutService.getStockOutList(stockOutRequest, operator);

        String depotName = null;
        Map<Long, Map<Long, Map<String, StockOutItem>>> trackerMap = new HashMap<>();
        List<StockOut> pickStockOuts = new ArrayList<>();
        for (StockOut stockOut : page.getContent()) {
            if (depotName == null) {
                depotName = stockOut.getDepot().getName();
            }

            /*没加货位前
            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {

                    Long groupId = stockOut.getOrderGroup() != null ? stockOut.getOrderGroup().getId() : 0L;
                    if (trackerMap.containsKey(groupId)) {
                        Map<Long, StockOutItem> itemMap = trackerMap.get(groupId);
                        if (itemMap.containsKey(stockOutItem.getSku().getId())) {
                            StockOutItem soi = itemMap.get(stockOutItem.getSku().getId());
                            soi.setExpectedQuantity(soi.getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                        } else {
                            StockOutItem soi = stockOutItem.clone();
                            itemMap.put(stockOutItem.getSku().getId(), soi);
                            trackerMap.put(groupId, itemMap);
                        }
                    } else {
                        Map<Long, StockOutItem> itemMap = new HashMap<>();
                        StockOutItem soi = stockOutItem.clone();
                        itemMap.put(stockOutItem.getSku().getId(), soi);
                        trackerMap.put(groupId, itemMap);
                    }

                    if (!stockOut.isPickPrint()) {
                        stockOut.setPickPrint(true);
                        pickStockOuts.add(stockOut);
                    }
                }
            }*/

            Long groupId = stockOut.getOrderGroup() != null ? stockOut.getOrderGroup().getId() : 0L;
            List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());
            for (Stock occupiedStock : occupiedStocks) {

                String shelfName = occupiedStock.getShelf() != null ? occupiedStock.getShelf().getName() : "";
                if (trackerMap.containsKey(groupId)) {
                    Map<Long, Map<String, StockOutItem>> itemMap = trackerMap.get(groupId);
                    if (itemMap.containsKey(occupiedStock.getSku().getId())) {
                        Map<String, StockOutItem> shelfMap = itemMap.get(occupiedStock.getSku().getId());
                        if (shelfMap.containsKey(shelfName)) {
                            StockOutItem soi = shelfMap.get(shelfName);
                            soi.setExpectedQuantity(soi.getExpectedQuantity() + occupiedStock.getStock());
                        } else {
                            StockOutItem soi = new StockOutItem();
                            soi.setSku(occupiedStock.getSku());
                            soi.setExpectedQuantity(occupiedStock.getStock());
                            shelfMap.put(shelfName, soi);
                            itemMap.put(occupiedStock.getSku().getId(), shelfMap);
                            trackerMap.put(groupId, itemMap);
                        }
                    } else {
                        Map<String, StockOutItem> shelfMap = new HashMap<>();
                        StockOutItem soi = new StockOutItem();
                        soi.setSku(occupiedStock.getSku());
                        soi.setExpectedQuantity(occupiedStock.getStock());
                        shelfMap.put(shelfName, soi);
                        itemMap.put(occupiedStock.getSku().getId(), shelfMap);
                        trackerMap.put(groupId, itemMap);
                    }
                } else {
                    Map<String, StockOutItem> shelfMap = new HashMap<>();
                    StockOutItem soi = new StockOutItem();
                    soi.setSku(occupiedStock.getSku());
                    soi.setExpectedQuantity(occupiedStock.getStock());
                    shelfMap.put(shelfName, soi);
                    Map<Long, Map<String, StockOutItem>> itemMap = new HashMap<>();
                    itemMap.put(occupiedStock.getSku().getId(), shelfMap);
                    trackerMap.put(groupId, itemMap);
                }

                if (!stockOut.isPickPrint()) {
                    stockOut.setPickPrint(true);
                    pickStockOuts.add(stockOut);
                }
            }
        }
        if (pickStockOuts.size() > 0) {
            for (StockOut stockOut : pickStockOuts) {
                stockOutService.saveStockOut(stockOut);
            }
        }

        /*没加货位前
        List<StockOutItemWrapper> stockOutItemWappers = new ArrayList<>();
        Integer begRow = null;
        Integer endRow = null;
        final Integer begDetial = 3;
        Map<Integer, Integer> mergeMap = new HashMap<>();
        for (Map.Entry<Long, Map<Long, StockOutItem>> entry : trackerMap.entrySet()) {

            Long groupId = entry.getKey();
            String trackerName = "";
            if (groupId.longValue() > 0) {
                OrderGroup orderGroup = orderService.getOrderGroupById(groupId);
                if (orderGroup != null && orderGroup.getTracker() != null) {
                    trackerName = orderGroup.getTracker().getRealname();
                }
            }

            List<StockOutItem> sortStockOutItems = new ArrayList<>(entry.getValue().values());
            Collections.sort(sortStockOutItems, new Comparator<StockOutItem>() {
                @Override
                public int compare(StockOutItem o1, StockOutItem o2) {
                    return ComparisonChain.start()
                            .compare(o1.getSku().getId(), o2.getSku().getId()).result();
                }
            });

            begRow = begRow == null ? begDetial : (endRow + 1);

            for (StockOutItem soi : sortStockOutItems) {

                StockOutItemWrapper soiw = new StockOutItemWrapper();
                soiw.setExpectedQuantity(soi.getExpectedQuantity());
                soiw.setSkuId(soi.getSku().getId());
                soiw.setSkuName(soi.getSku().getName());
                soiw.setSpecification(soi.getSku().getProduct().getSpecification());
                soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
                int cap = soi.getSku().getCapacityInBundle();
                if (cap > 1) {
                    int intNum = soiw.getExpectedQuantity() / cap;
                    int modNum = soiw.getExpectedQuantity() % cap;
                    if (intNum > 0) {
                        if (modNum > 0) {
                            soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                        } else {
                            soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit());
                        }
                    }
                }
                soiw.setTrackerName(trackerName);
                stockOutItemWappers.add(soiw);
            }

            endRow = begRow + (entry.getValue().size() - 1);
            mergeMap.put(begRow, endRow);
        }*/

        List<StockOutPickWrapper> stockOutPickWrappers = new ArrayList<>();
        Integer begRow = null;
        Integer endRow = null;
        final Integer begDetial = 3;
        Map<Integer, Integer> mergeMap = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Map<String, StockOutItem>>> entry : trackerMap.entrySet()) {

            Long groupId = entry.getKey();
            String trackerName = "";
            if (groupId.longValue() > 0) {
                OrderGroup orderGroup = orderService.getOrderGroupById(groupId);
                if (orderGroup != null && orderGroup.getTracker() != null) {
                    trackerName = orderGroup.getTracker().getRealname();
                }
            }

            List<StockOutPickWrapper> groupPickWrappers = new ArrayList<>();
            Map<Long, Map<String, StockOutItem>> itemMap = entry.getValue();
            for (Map.Entry<Long, Map<String, StockOutItem>> itemEntry : itemMap.entrySet()) {

                Map<String, StockOutItem> shelfMap = itemEntry.getValue();
                for (Map.Entry<String, StockOutItem> shelfEntry : shelfMap.entrySet()) {

                    StockOutItem soi = shelfEntry.getValue();
                    StockOutPickWrapper soiw = new StockOutPickWrapper();
                    soiw.setExpectedQuantity(soi.getExpectedQuantity());
                    soiw.setSkuId(soi.getSku().getId());
                    soiw.setSkuName(soi.getSku().getName());
                    soiw.setSpecification(soi.getSku().getProduct().getSpecification());
                    soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
                    soiw.setShelfName(shelfEntry.getKey());
                    int cap = soi.getSku().getCapacityInBundle();
                    if (cap > 1) {
                        int intNum = soiw.getExpectedQuantity() / cap;
                        int modNum = soiw.getExpectedQuantity() % cap;
                        if (intNum > 0) {
                            if (modNum > 0) {
                                soiw.setSkuBundleUnitDes(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                            } else {
                                soiw.setSkuBundleUnitDes(intNum + soi.getSku().getBundleUnit());
                            }
                        }
                    }
                    soiw.setTrackerName(trackerName);
                    groupPickWrappers.add(soiw);
                }
            }


            Collections.sort(groupPickWrappers, new Comparator<StockOutPickWrapper>() {
                @Override
                public int compare(StockOutPickWrapper o1, StockOutPickWrapper o2) {
                    return ComparisonChain.start()
                            .compare(o1.getShelfName(), o2.getShelfName())
                            .compare(o1.getSkuId(), o2.getSkuId())
                            .result();
                }
            });

            begRow = begRow == null ? begDetial : (endRow + 1);

            stockOutPickWrappers.addAll(groupPickWrappers);

            endRow = begRow + (groupPickWrappers.size() - 1);
            mergeMap.put(begRow, endRow);
        }

        Map<String, Object> beans = new HashMap<>();

        beans.put("depotName", depotName);
        beans.put("nums", page.getTotalElements());
        beans.put("printTime", new Date());
        beans.put("stockOutItems", stockOutPickWrappers);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "picktracker" + sdf.format(new Date()) + ".xls";
        File file = new File(ExportExcelUtils.excelFolderName, fileName);

        final InputStream summaryInputStream = getClass().getResourceAsStream(PICK_TRACKER_XLS_TEMPLATE);
        final FileOutputStream summaryOutputStream = new FileOutputStream(file);

        try {
            XLSTransformer transformer = new XLSTransformer();
            Workbook workbook = transformer.transformXLS(summaryInputStream, beans);
            Sheet sheet = workbook.getSheetAt(0);

            for (Map.Entry<Integer, Integer> entryMerge : mergeMap.entrySet()) {
                sheet.addMergedRegion(new CellRangeAddress(entryMerge.getKey(), entryMerge.getValue(), 0, 0));
            }

            workbook.write(summaryOutputStream);
        } finally {
            summaryInputStream.close();
            summaryOutputStream.close();
        }
        return file;
    }

    @Transactional(readOnly = true)
    public File exportAssociateTrackerExcel(StockOutRequest stockOutRequest, AdminUser operator) throws IOException, InvalidFormatException {

        stockOutRequest.setPage(0);
        stockOutRequest.setPageSize(Integer.MAX_VALUE);
        Page<StockOut> page = stockOutService.getStockOutList(stockOutRequest, operator);

        Map<String, Object[]> trackerMap = new HashMap<>();

        for (StockOut stockOut : page.getContent()) {

            Map<Long, StockOutItem> itemMap;
            Object[] objects;
            String tracker = stockOut.getOrderGroup() != null && stockOut.getOrderGroup().getTracker() != null ? stockOut.getOrderGroup().getTracker().getRealname() : "";
            if (trackerMap.containsKey(tracker)) {

                objects = trackerMap.get(tracker);
                Set<Long> stockOutIds = (Set) objects[0];
                Set<Long> restaurantIds = (Set) objects[1];
                BigDecimal allAmount = (BigDecimal) objects[2];
                itemMap = (Map<Long, StockOutItem>) objects[3];
                stockOutIds.add(stockOut.getId());
                restaurantIds.add(stockOut.getOrder().getRestaurant().getId());
                allAmount = allAmount.add(stockOut.getAmount());
                objects[0] = stockOutIds;
                objects[1] = restaurantIds;
                objects[2] = allAmount;

            } else {

                objects = new Object[4];
                Set<Long> stockOutIds = new HashSet<>();
                Set<Long> restaurantIds = new HashSet<>();
                BigDecimal allAmount = stockOut.getAmount();
                itemMap = new HashMap<>();
                stockOutIds.add(stockOut.getId());
                restaurantIds.add(stockOut.getOrder().getRestaurant().getId());
                objects[0] = stockOutIds;
                objects[1] = restaurantIds;
                objects[2] = allAmount;
            }
            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                if (!StockOutItemStatus.CANCEL.getValue().equals(stockOutItem.getStatus())) {
                    if (itemMap.containsKey(stockOutItem.getSku().getId())) {
                        StockOutItem soi = itemMap.get(stockOutItem.getSku().getId());
                        soi.setExpectedQuantity(soi.getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                    } else {
                        StockOutItem soi = stockOutItem.clone();
                        itemMap.put(stockOutItem.getSku().getId(), soi);
                    }
                }
            }
            objects[3] = itemMap;

            trackerMap.put(tracker, objects);
        }

        List<StockOutWrapper> stockOutWrappers = new ArrayList<>();
        List<String> sheetNames = new ArrayList<>();
        int sheetI = 0;
        for (Map.Entry<String, Object[]> entry : trackerMap.entrySet()) {
            sheetI++;
            StockOutWrapper stockOutWrapper = new StockOutWrapper();
            List<StockOutItemWrapper> stockOutItemWappers = new ArrayList<>();
            stockOutWrapper.setTrackerName(entry.getKey());
            Object[] objects = entry.getValue();
            stockOutWrapper.setStockOutId(Long.valueOf((long) ((Set) objects[0]).size()));
            stockOutWrapper.setOrderId(Long.valueOf((long) ((Set) objects[1]).size()));
            stockOutWrapper.setAmount((BigDecimal) objects[2]);
            Map<Long, StockOutItem> itemMap = (Map<Long, StockOutItem>) objects[3];
            for (Map.Entry<Long, StockOutItem> itemEntry : itemMap.entrySet()) {

                StockOutItem soi = itemEntry.getValue();
                StockOutItemWrapper soiw = new StockOutItemWrapper();
                soiw.setExpectedQuantity(soi.getExpectedQuantity());
                soiw.setSkuId(soi.getSku().getId());
                soiw.setSkuName(soi.getSku().getName());
                soiw.setPrice(soi.getPrice());
                soiw.setSpecification(soi.getSku().getProduct().getSpecification());
                soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
                soiw.setCategoryId(SkuCategoryUtils.getTopCategoryId(soi.getSku().getProduct().getCategory()));
                int cap = soi.getSku().getCapacityInBundle();
                if (cap > 1) {
                    int intNum = soiw.getExpectedQuantity() / cap;
                    int modNum = soiw.getExpectedQuantity() % cap;
                    if (intNum > 0) {
                        if (modNum > 0) {
                            soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                        } else {
                            soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit());
                        }
                    }
                }
                stockOutItemWappers.add(soiw);
            }

            Collections.sort(stockOutItemWappers, new Comparator<StockOutItemWrapper>() {
                @Override
                public int compare(StockOutItemWrapper o1, StockOutItemWrapper o2) {
                    return ComparisonChain.start()
                            .compare(o1.getCategoryId(), o2.getCategoryId())
                            .compare(o1.getSkuId(), o2.getSkuId()).result();
                }
            });
            stockOutWrapper.setStockOutItems(stockOutItemWappers);
            stockOutWrappers.add(stockOutWrapper);
            sheetNames.add(sheetI + " 交接单");
        }

        Map<String, Object> beans = new HashMap<>();
        beans.put("printTime", new Date());
        beans.put("printer", operator.getRealname());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "associatetracker" + sdf.format(new Date()) + ".xls";
        File file = new File(ExportExcelUtils.excelFolderName, fileName);

        return this.generateMultiSheetExcel(stockOutWrappers, "stockOut", sheetNames, beans, file, ASSOCIATE_XLS_TEMPLATE, false);
    }

    @Transactional(readOnly = true)
    public File exportBarcodeExcel(StockOutRequest stockOutRequest, int type, AdminUser operator) throws IOException, InvalidFormatException {

        stockOutRequest.setPage(0);
        stockOutRequest.setPageSize(Integer.MAX_VALUE);
        Page<StockOutItem> page = stockOutService.getStockOutItemList(stockOutRequest, operator);

        List<StockOutItemDispatcher> itemDispatchers = new ArrayList<>();
        Map<Long, Map<Long, StockOutItem>> stockOutSkuMap = new HashMap<>();

        for (StockOutItem stockOutItem : page.getContent()) {
            boolean pass = type == 0 ? (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus()))
                    : (type == 1 ? (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(stockOutItem.getStatus()) && stockOutItem.getTransferOut() == null)
                    : (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(stockOutItem.getStatus()) && stockOutItem.getTransferOut() != null));
            if (pass) {

                if (stockOutSkuMap.containsKey(stockOutItem.getStockOut().getId())) {
                    Map<Long, StockOutItem> skuMap = stockOutSkuMap.get(stockOutItem.getStockOut().getId());
                    if (skuMap.containsKey(stockOutItem.getSku().getId())) {
                        skuMap.get(stockOutItem.getSku().getId()).setExpectedQuantity(skuMap.get(stockOutItem.getSku().getId()).getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                    } else {
                        StockOutItem mapStockOutItem = stockOutItem.clone();
                        mapStockOutItem.setStockOut(stockOutItem.getStockOut());
                        skuMap.put(stockOutItem.getSku().getId(), mapStockOutItem);
                    }

                } else {
                    Map<Long, StockOutItem> skuMap = new HashMap<>();
                    StockOutItem mapStockOutItem = stockOutItem.clone();
                    mapStockOutItem.setStockOut(stockOutItem.getStockOut());
                    skuMap.put(stockOutItem.getSku().getId(), mapStockOutItem);
                    stockOutSkuMap.put(stockOutItem.getStockOut().getId(), skuMap);
                }
            }
        }

        Map<Long, VendorVo> skuVendorMap = new HashMap<>();

        for (Map.Entry<Long, Map<Long, StockOutItem>> entry : stockOutSkuMap.entrySet()) {
            for (Map.Entry<Long, StockOutItem> itemEntry : entry.getValue().entrySet()) {

                StockOutItem soi = itemEntry.getValue();
                VendorVo vendorWrapper = new VendorVo();
                vendorWrapper.setId(0L);
                vendorWrapper.setName(null);
                if (type == 1) {
                    if (skuVendorMap.containsKey(soi.getSku().getId())) {
                        vendorWrapper = skuVendorMap.get(soi.getSku().getId());
                    } else {
                        List<SkuVendor> skuVendors = skuVendorService.findByCityIdAndSkuId(soi.getStockOut().getDepot().getCity().getId(), soi.getSku().getId());
                        vendorWrapper = new VendorVo();
                        if (skuVendors.isEmpty()) {
                            vendorWrapper.setId(0L);
                            vendorWrapper.setName(null);
                        } else {
                            vendorWrapper.setId(skuVendors.get(0).getVendor().getId());
                            vendorWrapper.setName(skuVendors.get(0).getVendor().getName());
                        }

                        if (vendorWrapper.getId().longValue() == 0) {
                            Organization organization = organizationService.getDefaultOrganization();
                            if (organization != null) {
                                Vendor defaultVendor = vendorService.getDefaultVendor(organization.getId(), soi.getStockOut().getDepot().getCity().getId());
                                if (defaultVendor != null) {
                                    vendorWrapper.setId(defaultVendor.getId());
                                    vendorWrapper.setName(defaultVendor.getName());
                                }
                            }

                        }
                        skuVendorMap.put(soi.getSku().getId(), vendorWrapper);
                    }
                }

                if (soi.getSku().getProduct().isDiscrete()) {
                    int cap = soi.getSku().getCapacityInBundle();
                    if (cap > 1) {
                        int intNum = soi.getExpectedQuantity() / cap;
                        int modNum = soi.getExpectedQuantity() % cap;
                        if (intNum > 0) {
                            for (int i = 0; i < intNum; i++) {
                                StockOutItemDispatcher itemDispatcher = new StockOutItemDispatcher();
                                itemDispatcher.setTrackerName(soi.getStockOut().getOrderGroup() != null && soi.getStockOut().getOrderGroup().getTracker() != null ? soi.getStockOut().getOrderGroup().getTracker().getRealname() : null);
                                itemDispatcher.setStockOutId(entry.getKey());
                                itemDispatcher.setExpectedArrivedDate(soi.getStockOut().getOrder().getExpectedArrivedDate() != null ? DateFormatUtils.format(soi.getStockOut().getOrder().getExpectedArrivedDate(), "yyyy-MM-dd") : null);
                                itemDispatcher.setOrderId(soi.getStockOut().getOrder().getId());
                                itemDispatcher.setQuantity(cap);
                                itemDispatcher.setRestaurantName(soi.getStockOut().getOrder().getRestaurant().getName());
                                itemDispatcher.setSku(soi.getSku());
                                itemDispatcher.setUnit(soi.getSku().getBundleUnit());
                                itemDispatcher.setVendorId(vendorWrapper.getId());
                                itemDispatcher.setVendorName(vendorWrapper.getName());
                                itemDispatcher.setDisplayName(soi.getSku().getName());
                                itemDispatchers.add(itemDispatcher);
                            }
                        }
                        if (modNum > 0) {
                            for (int i = 0; i < modNum; i++) {
                                StockOutItemDispatcher itemDispatcher = new StockOutItemDispatcher();
                                itemDispatcher.setTrackerName(soi.getStockOut().getOrderGroup() != null && soi.getStockOut().getOrderGroup().getTracker() != null ? soi.getStockOut().getOrderGroup().getTracker().getRealname() : null);
                                itemDispatcher.setStockOutId(entry.getKey());
                                itemDispatcher.setExpectedArrivedDate(soi.getStockOut().getOrder().getExpectedArrivedDate() != null ? DateFormatUtils.format(soi.getStockOut().getOrder().getExpectedArrivedDate(), "yyyy-MM-dd") : null);
                                itemDispatcher.setOrderId(soi.getStockOut().getOrder().getId());
                                itemDispatcher.setQuantity(1);
                                itemDispatcher.setRestaurantName(soi.getStockOut().getOrder().getRestaurant().getName());
                                itemDispatcher.setSku(soi.getSku());
                                itemDispatcher.setUnit(soi.getSku().getSingleUnit());
                                itemDispatcher.setVendorId(vendorWrapper.getId());
                                itemDispatcher.setVendorName(vendorWrapper.getName());
                                itemDispatcher.setDisplayName(soi.getSku().getName());
                                itemDispatchers.add(itemDispatcher);
                            }
                        }
                    } else {
                        for (int i = 0; i < soi.getExpectedQuantity(); i++) {
                            StockOutItemDispatcher itemDispatcher = new StockOutItemDispatcher();
                            itemDispatcher.setTrackerName(soi.getStockOut().getOrderGroup() != null && soi.getStockOut().getOrderGroup().getTracker() != null ? soi.getStockOut().getOrderGroup().getTracker().getRealname() : null);
                            itemDispatcher.setStockOutId(entry.getKey());
                            itemDispatcher.setExpectedArrivedDate(soi.getStockOut().getOrder().getExpectedArrivedDate() != null ? DateFormatUtils.format(soi.getStockOut().getOrder().getExpectedArrivedDate(), "yyyy-MM-dd") : null);
                            itemDispatcher.setOrderId(soi.getStockOut().getOrder().getId());
                            itemDispatcher.setQuantity(1);
                            itemDispatcher.setRestaurantName(soi.getStockOut().getOrder().getRestaurant().getName());
                            itemDispatcher.setSku(soi.getSku());
                            itemDispatcher.setUnit(soi.getSku().getSingleUnit());
                            itemDispatcher.setVendorId(vendorWrapper.getId());
                            itemDispatcher.setVendorName(vendorWrapper.getName());
                            itemDispatcher.setDisplayName(soi.getSku().getName());
                            itemDispatchers.add(itemDispatcher);
                        }
                    }
                } else {
                    StockOutItemDispatcher itemDispatcher = new StockOutItemDispatcher();
                    itemDispatcher.setTrackerName(soi.getStockOut().getOrderGroup() != null && soi.getStockOut().getOrderGroup().getTracker() != null ? soi.getStockOut().getOrderGroup().getTracker().getRealname() : null);
                    itemDispatcher.setStockOutId(entry.getKey());
                    itemDispatcher.setExpectedArrivedDate(soi.getStockOut().getOrder().getExpectedArrivedDate() != null ? DateFormatUtils.format(soi.getStockOut().getOrder().getExpectedArrivedDate(), "yyyy-MM-dd") : null);
                    itemDispatcher.setOrderId(soi.getStockOut().getOrder().getId());
                    itemDispatcher.setQuantity(soi.getExpectedQuantity());
                    itemDispatcher.setRestaurantName(soi.getStockOut().getOrder().getRestaurant().getName());
                    itemDispatcher.setSku(soi.getSku());
                    itemDispatcher.setUnit(soi.getSku().getSingleUnit());
                    itemDispatcher.setVendorId(vendorWrapper.getId());
                    itemDispatcher.setVendorName(vendorWrapper.getName());
                    itemDispatcher.setDisplayName(soi.getExpectedQuantity() + " * " + soi.getSku().getName());
                    itemDispatchers.add(itemDispatcher);
                }
            }
        }

        Collections.sort(itemDispatchers, new Comparator<StockOutItemDispatcher>() {
            @Override
            public int compare(StockOutItemDispatcher o1, StockOutItemDispatcher o2) {
                return ComparisonChain.start()
                        .compare(o1.getVendorId(), o2.getVendorId())
                        .compare(o1.getSku().getId(), o2.getSku().getId()).result();
            }
        });

        int index = 0;
        Long lastSkuId = 0L;
        Long lastVendor = 0L;
        for (StockOutItemDispatcher sid : itemDispatchers) {

            if (sid.getSku().getId().equals(lastSkuId) && sid.getVendorId().equals(lastVendor)) {
                sid.setIndex(index);
            } else {
                sid.setIndex(++index);
                lastSkuId = sid.getSku().getId();
                lastVendor = sid.getVendorId();
            }
        }

        Map<String, Object> beans = new HashMap<>();
        beans.put("stockOutItems", itemDispatchers);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "barcode" + sdf.format(new Date()) + ".xls";
        File file = new File(ExportExcelUtils.excelFolderName, fileName);

        return this.generateSingleSheetExcel(beans, file, BARCODE_XLS_TEMPLATE, true);
    }

    @Transactional(readOnly = true)
    public File exportNotMatchExcel(StockOutRequest stockOutRequest, AdminUser operator) throws IOException, InvalidFormatException {

        stockOutRequest.setPage(0);
        stockOutRequest.setPageSize(Integer.MAX_VALUE);
        Page<StockOutItem> page = stockOutService.getStockOutItemList(stockOutRequest, operator);

        List<StockOutItemWrapper> stockOutItemWappers = new ArrayList<>();
        for (StockOutItem soi : page.getContent()) {

            StockOutItemWrapper soiw = new StockOutItemWrapper();
            soiw.setDepotName(soi.getStockOut().getDepot().getName());
            soiw.setStockOutId(soi.getStockOut().getId());
            soiw.setOrderId(soi.getStockOut().getOrder().getId());
            soiw.setCustomerName(soi.getStockOut().getOrder().getRestaurant().getName());
            soiw.setTrackerName(soi.getStockOut().getOrderGroup() != null && soi.getStockOut().getOrderGroup().getTracker() != null ? soi.getStockOut().getOrderGroup().getTracker().getRealname() : null);
            soiw.setExpectedQuantity(soi.getExpectedQuantity());
            soiw.setSkuId(soi.getSku().getId());
            soiw.setSkuName(soi.getSku().getName());
            soiw.setSpecification(soi.getSku().getProduct().getSpecification());
            soiw.setSkuSingleUnit(soi.getSku().getSingleUnit());
            int cap = soi.getSku().getCapacityInBundle();
            if (cap > 1) {
                int intNum = soiw.getExpectedQuantity() / cap;
                int modNum = soiw.getExpectedQuantity() % cap;
                if (intNum > 0) {
                    if (modNum > 0) {
                        soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit() + modNum + soi.getSku().getSingleUnit());
                    } else {
                        soiw.setSkuBundleUnit(intNum + soi.getSku().getBundleUnit());
                    }
                }
            }
            stockOutItemWappers.add(soiw);
        }

        Map<String, Object> beans = new HashMap<>();

        beans.put("startDate", stockOutRequest.getStartOrderDate());
        beans.put("endDate", stockOutRequest.getEndOrderDate());
        beans.put("printTime", new Date());
        beans.put("stockOutItems", stockOutItemWappers);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "notMatch" + sdf.format(new Date()) + ".xls";
        File file = new File(ExportExcelUtils.excelFolderName, fileName);

        return this.generateSingleSheetExcel(beans, file, NOTMATCH_XLS_TEMPLATE, false);
    }

    public HttpEntity<byte[]> getHttpEntityXls(String pathName, String excelName) {
        byte[] readFileToByteArray = null;
        try {
            readFileToByteArray = FileUtils.readFileToByteArray(new File(pathName));

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "xls"));
            header.set("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(excelName, "UTF-8"));
            header.setContentLength(readFileToByteArray.length);
            FileUtils.deleteQuietly(new File(pathName));

            return new HttpEntity<byte[]>(readFileToByteArray, header);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File generateSingleSheetExcel(Map<String, Object> beans, File file, String template, boolean autoSize) throws IOException, InvalidFormatException {
        final InputStream inputStream = getClass().getResourceAsStream(template);
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

    private <T> File generateMultiSheetExcel(List<T> beans, String beanName, List<String> sheetNames, Map<String, Object> beanParams, File file, String template, boolean autoSize) throws IOException, InvalidFormatException {
        final InputStream inputStream = getClass().getResourceAsStream(template);
        final FileOutputStream outputStream = new FileOutputStream(file);

        try {
            XLSTransformer transformer = new XLSTransformer();
            final Workbook workbook = transformer.transformMultipleSheetsList(inputStream, beans, sheetNames, beanName, beanParams, 0);
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

    private static void autoSizeExcelRowHeight(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            final Sheet sheet = workbook.getSheetAt(i);

            final Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                rowIterator.next().setHeight((short) -1);
            }
        }
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOutOutList(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", stockOutFacade.getStockOutList(request, operator).getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("stockOutOutList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_OUT_LIST);

    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportIncomeDailyReport(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Map<String, Map<String, IncomeDailyReportWrapper>>> dateMap = new HashMap<>();
        //统计应收
        Page<StockOut> receivedStockOut = stockOutService.getStockOutList(request, operator);
        for (StockOut stockOut : receivedStockOut.getContent()) {
            String warehouseName = stockOut.getOrder().getCustomer().getBlock().getWarehouse().getName();
            String cityName = stockOut.getDepot().getCity().getName();
            String receiveDate = DateFormatUtils.format(stockOut.getReceiveDate(), "yyyy-MM-dd");
            if (!dateMap.containsKey(receiveDate)) {
                IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper(stockOut);
                Map<String, IncomeDailyReportWrapper> warehouseMap = new HashMap<>();
                warehouseMap.put(warehouseName, wrapper);
                Map<String, Map<String, IncomeDailyReportWrapper>> cityMap = new HashMap<>();
                cityMap.put(cityName, warehouseMap);
                dateMap.put(receiveDate, cityMap);
            } else if (!dateMap.get(receiveDate).containsKey(cityName)) {
                IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper(stockOut);
                Map<String, IncomeDailyReportWrapper> warehouseMap = new HashMap<>();
                warehouseMap.put(warehouseName, wrapper);
                dateMap.get(receiveDate).put(cityName, warehouseMap);
            } else if (!dateMap.get(receiveDate).get(cityName).containsKey(warehouseName)) {
                IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper(stockOut);
                dateMap.get(receiveDate).get(cityName).put(warehouseName, wrapper);
            } else {
                dateMap.get(receiveDate).get(cityName).get(warehouseName).merge(new IncomeDailyReportWrapper(stockOut));
            }
        }
        //统计结挂帐
        request.setSettle(true);
        request.setStartSettleDate(request.getStartReceiveDate());
        request.setEndSettleDate(request.getEndReceiveDate());
        request.setStartReceiveDate(null);
        request.setEndReceiveDate(null);
        Page<StockOut> settledStockOut = stockOutService.getStockOutList(request, operator);
        for (StockOut stockOut : settledStockOut.getContent()) {
            String receiveDate = DateFormatUtils.format(stockOut.getReceiveDate(), "yyyy-MM-dd");
            String settleDate = DateFormatUtils.format(stockOut.getReceiveDate(), "yyyy-MM-dd");
            if (!receiveDate.equals(settleDate)) {
                String warehouseName = stockOut.getOrder().getCustomer().getBlock().getWarehouse().getName();
                String cityName = stockOut.getDepot().getCity().getName();
                if (!dateMap.containsKey(settleDate)) {
                    IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper();
                    wrapper.setCleanedLiability(stockOut.getReceiveAmount());
                    Map<String, IncomeDailyReportWrapper> warehouseMap = new HashMap<>();
                    warehouseMap.put(warehouseName, wrapper);
                    Map<String, Map<String, IncomeDailyReportWrapper>> cityMap = new HashMap<>();
                    cityMap.put(cityName, warehouseMap);
                    dateMap.put(settleDate, cityMap);
                } else if (!dateMap.get(settleDate).containsKey(cityName)) {
                    IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper();
                    wrapper.setCleanedLiability(stockOut.getReceiveAmount());
                    Map<String, IncomeDailyReportWrapper> warehouseMap = new HashMap<>();
                    warehouseMap.put(warehouseName, wrapper);
                    dateMap.get(settleDate).put(cityName, warehouseMap);
                } else if (!dateMap.get(settleDate).get(cityName).containsKey(warehouseName)) {
                    IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper();
                    wrapper.setCleanedLiability(stockOut.getReceiveAmount());
                    dateMap.get(settleDate).get(cityName).put(warehouseName, wrapper);
                } else {
                    IncomeDailyReportWrapper wrapper = new IncomeDailyReportWrapper();
                    wrapper.setCleanedLiability(stockOut.getReceiveAmount());
                    dateMap.get(settleDate).get(cityName).get(warehouseName).merge(wrapper);
                }
            }
        }

        String fileName = String.format("incomeDailyReport-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        List<String> sheetNames = new ArrayList<>();
        List<Map<String, Map<String, IncomeDailyReportWrapper>>> beanList = new ArrayList<>();
        for (String date : dateMap.keySet()) {
            sheetNames.add(date);
            beanList.add(dateMap.get(date));
        }

        return ExportExcelUtils.generateExcelBytes(beanList, "cityMap", sheetNames, null, fileName, INCOME_DAILY_REPORT);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockShelfExcel(StockQueryRequest request, AdminUser operator) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockShelfWrapper> page = stockFacade.findStocksByShelf(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("按货位查库存-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKSHELF_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockAdjustExcel(StockAdjustQueryRequest request, AdminUser operator) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockAdjustWrapper> page = stockAdjustFacade.getStockAdjustList(request, operator);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("库存调整查询-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKADJUST_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOnShelfExcel(StockQueryRequest request, AdminUser operator) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockWrapper> page = stockFacade.findWillOnShelfStocks(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("depotName", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("printTime", new Date());
        beans.put("operator", operator);
        String fileName = String.format("库存上架-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCK_ONSHELF_XLS_TEMPLATE, false);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockExpirationExcel(StockQueryRequest request, AdminUser operator) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockShelfWrapper> page = stockFacade.findExpirationStocks(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("临期品查询-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKEXPIRATION_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockDullSaleExcel(StockQueryRequest request, AdminUser operator) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<StockShelfWrapper> page = stockFacade.findDullSaleStocks(request);
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", page.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        String fileName = String.format("滞销品查询-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKDULLSALE_LIST);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportStockOutReceiveList(StockOutRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        QuerySummationResponse<StockOutWrapper> stockOutReceive = stockOutFacade.getStockOutList(request, operator);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("list", stockOutReceive.getContent());
        beans.put("now", new Date());
        beans.put("operator", operator);
        beans.put("amounts", stockOutReceive.getAmount());
        String fileName = String.format("送货收款-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, STOCKOUT_RECEIVE_LIST);

    }
}
