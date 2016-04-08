package com.mishu.cgwy.accounting.facade;

import com.google.common.collect.TreeBasedTable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.dto.ProfitRequest;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.service.ProfitService;
import com.mishu.cgwy.accounting.wrapper.*;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.common.repository.WarehouseRepository;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.task.service.AsyncTask;
import com.mishu.cgwy.task.service.AsyncTaskService;
import com.mishu.cgwy.task.service.TaskResult;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiao1zhao2 on 15/12/3.
 */
@Service
public class ProfitFacade {

    @Autowired
    private ProfitService profitService;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AsyncTaskService asyncTaskService;

    private static Logger logger = LoggerFactory.getLogger(ProfitFacade.class);

    private static final String SKU_PROFIT_LIST = "/template/skuProfit-list.xls";
    private static final String CUSTOMER_PROFIT_LIST = "/template/customerSkuProfit-list.xls";
    private static final String CUSTOMER_SELLER_PROFIT_LIST = "/template/customerSellerProfit-list.xls";
    private static final String CATEGORY_SELLER_PROFIT_LIST = "/template/categorySellerProfit-list.xls";
    private static final String SKU_SALES_LIST = "/template/skuSales-list.xls";
    private static final String SKU_SELL_SUMMERY = "/template/skuSellSummeryProfit-list.xls";

    @Transactional(readOnly = true)
    public WarehouseCategoryProfitArrays getWarehouseCategoryProfit(ProfitRequest request) {

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                final String sum = "合计";
                if (sum.equals(s1) || sum.equals(s2)) {
                    return s1.indexOf(sum) - s2.indexOf(sum);
                } else {
                    return s1.compareTo(s2);
                }
            }
        };
        TreeBasedTable<String, String, ProfitWrapper> table = TreeBasedTable.create(comparator, comparator);
        for (WarehouseCategoryProfitWrapper item : profitService.getWarehouseCategoryProfitWrapperList(request)) {
            table.put(item.getWarehouseName(), item.getCategoryName(), item.getProfit());
        }
        TreeBasedTable<String, String, ProfitWrapper> sumTable = TreeBasedTable.create();
        for (String row : table.rowKeySet()) {
            ProfitWrapper profit = new ProfitWrapper();
            for (String column : table.row(row).keySet()) {
                profit.merge(table.get(row, column));
            }
            sumTable.put(row, "合计", profit);
        }
        table.putAll(sumTable);
        sumTable.clear();
        for (String column : table.columnKeySet()) {
            ProfitWrapper profit = new ProfitWrapper();
            for (String row : table.column(column).keySet()) {
                profit.merge(table.get(row, column));
            }
            sumTable.put("合计", column, profit);
        }
        table.putAll(sumTable);

        String[] warehouses = table.rowKeySet().toArray(new String[table.rowKeySet().size()]);
        String[] categories = table.columnKeySet().toArray(new String[table.columnKeySet().size()]);
        ProfitWrapper[][] profits = new ProfitWrapper[warehouses.length][categories.length];
        for (int i = 0, warehousesLength = warehouses.length; i < warehousesLength; i++) {
            for (int j = 0, categoriesLength = categories.length; j < categoriesLength; j++) {
                if (table.get(warehouses[i], categories[j]) != null) {
                    profits[i][j] = table.get(warehouses[i], categories[j]);
                } else {
                    profits[i][j] = new ProfitWrapper();
                }
            }
        }
        return new WarehouseCategoryProfitArrays(warehouses, categories, profits);
    }

    @Transactional(readOnly = true)
    public CategorySellerProfitArrays getCategorySellerProfit(ProfitRequest request) {

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                final String sum = "合计";
                if (sum.equals(s1) || sum.equals(s2)) {
                    return s1.indexOf(sum) - s2.indexOf(sum);
                } else {
                    return s1.compareTo(s2);
                }
            }
        };
        TreeBasedTable<String, String, ProfitWrapper> table = TreeBasedTable.create(comparator, comparator);
        for (CategorySellerProfitWrapper item : profitService.getCategorySellerProfitWrapperList(request)) {
            table.put(item.getSellerName(), item.getCategoryName(), item.getProfit());
        }
        TreeBasedTable<String, String, ProfitWrapper> sumTable = TreeBasedTable.create();
        for (String row : table.rowKeySet()) {
            ProfitWrapper profit = new ProfitWrapper();
            for (String column : table.row(row).keySet()) {
                profit.merge(table.get(row, column));
            }
            sumTable.put(row, "合计", profit);
        }
        table.putAll(sumTable);
        sumTable.clear();
        for (String column : table.columnKeySet()) {
            ProfitWrapper profit = new ProfitWrapper();
            for (String row : table.column(column).keySet()) {
                profit.merge(table.get(row, column));
            }
            sumTable.put("合计", column, profit);
        }
        table.putAll(sumTable);

        String[] sellers = table.rowKeySet().toArray(new String[table.rowKeySet().size()]);
        String[] categories = table.columnKeySet().toArray(new String[table.columnKeySet().size()]);
        ProfitWrapper[][] profits = new ProfitWrapper[sellers.length][categories.length];
        for (int i = 0, warehousesLength = sellers.length; i < warehousesLength; i++) {
            for (int j = 0, categoriesLength = categories.length; j < categoriesLength; j++) {
                if (table.get(sellers[i], categories[j]) != null) {
                    profits[i][j] = table.get(sellers[i], categories[j]);
                } else {
                    profits[i][j] = new ProfitWrapper();
                }
            }
        }
        return new CategorySellerProfitArrays(sellers, categories, profits);
    }

    @Transactional(readOnly = true)
    public QueryResponse<SkuProfitWrapper> getSkuProfitList(ProfitRequest request) {

        Page<AccountReceivableItem> page = profitService.getAccountReceivableItem(request);
        List<SkuProfitWrapper> list = new ArrayList<>();
        for (AccountReceivableItem item : page.getContent()) {
            list.add(new SkuProfitWrapper(item));
        }
        QueryResponse<SkuProfitWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSkuProfitList(ProfitRequest request) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<SkuProfitWrapper> list = new ArrayList<>();
        for (AccountReceivableItem item : profitService.getAccountReceivableItem(request).getContent()) {
            list.add(new SkuProfitWrapper(item));
        }
        Map<String, Object> beans = new HashMap<>();
        beans.put("list", list);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("accountReceivableType", request.getAccountReceivableType() == Integer.MAX_VALUE ? "全部" : AccountReceivableType.fromInt(request.getAccountReceivableType()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("now", new Date());
        return ExportExcelUtils.generateExcelBytes(beans, "skuProfit.xls", SKU_PROFIT_LIST);
    }

    @Transactional(readOnly = true)
    public CustomerSellerProfitArrays getCustomerSellerProfit(ProfitRequest request) {

        Page<CustomerSellerProfitWrapper> page = profitService.getCustomerSellerProfitWrapperList(request);
        TreeBasedTable<Long, String, ProfitWrapper> table = TreeBasedTable.create(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }
        }, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                final String sum = "合计";
                if (sum.equals(s1) || sum.equals(s2)) {
                    return s1.indexOf(sum) - s2.indexOf(sum);
                } else {
                    return s1.compareTo(s2);
                }
            }
        });
        for (CustomerSellerProfitWrapper item : page.getContent()) {
            table.put(item.getRestaurantId(), item.getCategoryName(), item.getProfit());
        }
        TreeBasedTable<Long, String, ProfitWrapper> sumTable = TreeBasedTable.create();
        for (Long row : table.rowKeySet()) {
            ProfitWrapper profit = new ProfitWrapper();
            for (String column : table.row(row).keySet()) {
                profit.merge(table.get(row, column));
            }
            sumTable.put(row, "合计", profit);
        }
        table.putAll(sumTable);

        Long[] restaurantIds = table.rowKeySet().toArray(new Long[table.rowKeySet().size()]);
        String[] categoryNames = table.columnKeySet().toArray(new String[table.columnKeySet().size()]);
        String[] sellerNames = new String[restaurantIds.length];
        String[] warehouseNames = new String[restaurantIds.length];
        String[] restaurantNames = new String[restaurantIds.length];
        String[] receiverNames = new String[restaurantIds.length];
        String[] telephones = new String[restaurantIds.length];
        ProfitWrapper[][] profits = new ProfitWrapper[restaurantIds.length][categoryNames.length];
        for (int i = 0, restaurantIdsLength = restaurantIds.length; i < restaurantIdsLength; i++) {
            Restaurant restaurant = restaurantRepository.findById(restaurantIds[i]);
            sellerNames[i] = restaurant.getCustomer().getAdminUser().getRealname();
            warehouseNames[i] = restaurant.getCustomer().getBlock().getWarehouse().getName();
            restaurantNames[i] = restaurant.getName();
            receiverNames[i] = restaurant.getReceiver();
            telephones[i] = restaurant.getTelephone();
            for (int j = 0, categoryNamesLength = categoryNames.length; j < categoryNamesLength; j++) {
                if (table.get(restaurantIds[i], categoryNames[j]) != null) {
                    profits[i][j] = table.get(restaurantIds[i], categoryNames[j]);
                } else {
                    profits[i][j] = new ProfitWrapper();
                }
            }
        }
        return new CustomerSellerProfitArrays(sellerNames, warehouseNames, restaurantIds, restaurantNames, receiverNames, telephones, categoryNames, profits, request.getPage(), request.getPageSize(), page.getTotalElements());
    }

    public void asyncExportCustomerSellerProfit(final ProfitRequest request, AdminUser adminUser) throws Exception {
        asyncTaskService.export(
                request,
                adminUser,
                "客户销售毛利一览",
                new AsyncTask() {
                    @Override
                    public TaskResult proceed() throws Exception {
                        TaskResult taskResult = new TaskResult();
                        taskResult.setResult(exportCustomerSellerProfit(request));
                        return taskResult;
                    }
                }
        );
    }

    public void asyncExportCategorySellerProfit(final ProfitRequest request, AdminUser adminUser) throws Exception {
        asyncTaskService.export(
                request,
                adminUser,
                "品类销售毛利一览",
                new AsyncTask() {
                    @Override
                    public TaskResult proceed() throws Exception {
                        TaskResult taskResult = new TaskResult();
                        taskResult.setResult(exportCategorySellerProfit(request));
                        return taskResult;
                    }
                }
        );
    }

    @Transactional(readOnly = true)
    public String exportCustomerSellerProfit(ProfitRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("tfdata", getCustomerSellerProfit(request));
        return ExportExcelUtils.generateSingleSheetExcel(beans, "customerSellerProfit-list.xls", CUSTOMER_SELLER_PROFIT_LIST).getPath();
    }

    @Transactional(readOnly = true)
    public String exportCategorySellerProfit(ProfitRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("tfdata", getCategorySellerProfit(request));
        return ExportExcelUtils.generateSingleSheetExcel(beans, "categorySellerProfit-list.xls", CATEGORY_SELLER_PROFIT_LIST).getPath();
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportCustomerSkuProfitList(ProfitRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<CustomerSkuProfitWrapper> list = new ArrayList<>(profitService.getAccountReceivableItemGroupByCustomerSku(request).getContent());

        Map<String, Object> beans = new HashMap<>();
        beans.put("list", list);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("accountReceivableType", request.getAccountReceivableType() == Integer.MAX_VALUE ? "全部" : AccountReceivableType.fromInt(request.getAccountReceivableType()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("now", new Date());

        return ExportExcelUtils.generateExcelBytes(beans, "customerSkuProfit-list.xls", CUSTOMER_PROFIT_LIST);
    }

    @Transactional(readOnly = true)
    public QueryResponse<CustomerSkuProfitWrapper> getCustomerSkuProfit(ProfitRequest request) {
        Page<CustomerSkuProfitWrapper> page = profitService.getAccountReceivableItemGroupByCustomerSku(request);

        QueryResponse<CustomerSkuProfitWrapper> res = new QueryResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<SkuSalesWrapper> getSkuSalesList(ProfitRequest request) {

        Page<SkuSalesWrapper> page = profitService.getSkuSalesWrapperList(request);
        QuerySummationResponse<SkuSalesWrapper> res = new QuerySummationResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal amount = profitService.getSkuSalesAmountSummation(request);
        amount = amount == null ? BigDecimal.ZERO : amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        res.setAmount(new BigDecimal[]{amount});
        return res;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSkuSalesList(ProfitRequest request) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Map<String, Object> beans = new HashMap<>();
        beans.put("list", profitService.getSkuSalesWrapperList(request).getContent());
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        return ExportExcelUtils.generateExcelBytes(beans, "skuSales.xls", SKU_SALES_LIST);
    }


    public QueryResponse<SkuSellSummeryProfitWrapper> getSkuSellSummeryProfit(ProfitRequest request) {

        Page<SkuSellSummeryProfitWrapper> page = profitService.getSkuSellSummeryProfit(request);
        QueryResponse<SkuSellSummeryProfitWrapper> res = new QueryResponse<>();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    public HttpEntity<byte[]> exportSkuSellSummeryProfit(ProfitRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<SkuSellSummeryProfitWrapper> list = new ArrayList<>(profitService.getSkuSellSummeryProfit(request).getContent());

        Map<String, Object> beans = new HashMap<>();
        beans.put("list", list);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("accountReceivableType", request.getAccountReceivableType() == Integer.MAX_VALUE ? "全部" : AccountReceivableType.fromInt(request.getAccountReceivableType()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("now", new Date());

        return ExportExcelUtils.generateExcelBytes(beans, "skuSellSummery-list.xls", SKU_SELL_SUMMERY);
    }
}
