package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice_;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.product.controller.SkuCandidatesRequest;
import com.mishu.cgwy.product.controller.SkuListRequest;
import com.mishu.cgwy.product.controller.SkuPriceListRequest;
import com.mishu.cgwy.product.controller.SkuVendorRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.service.SkuPriceHistoryService;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuPriceWrapper;
import com.mishu.cgwy.product.wrapper.SkuVendorWrapper;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockInItem;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.task.service.AsyncTask;
import com.mishu.cgwy.task.service.AsyncTaskService;
import com.mishu.cgwy.task.service.TaskResult;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class SkuPriceFacade {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SkuVendorService skuVendorService;

    @Autowired
    private StockTotalService stockTotalService;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private SkuPriceHistoryService skuPriceHistoryService;

    private Specification<SkuPrice> getSkuPriceListSpecification(final SkuPriceListRequest request) {
        return new Specification<SkuPrice>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<SkuPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

                if (request.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(SkuPrice_.sku).get(Sku_.product).get(Product_.category).get(Category_.id), request.getCategoryId()));
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SkuPrice_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    Subquery<SkuVendor> skuVendorQuery = query.subquery(SkuVendor.class);
                    Root<SkuVendor> skuVendorRoot = skuVendorQuery.from(SkuVendor.class);
                    skuVendorQuery.where(
                            cb.equal(skuVendorRoot.get(SkuVendor_.sku).get(Sku_.id), root.get(SkuPrice_.sku).get(Sku_.id)),
                            cb.equal(skuVendorRoot.get(SkuVendor_.city).get(City_.id), root.get(SkuPrice_.city).get(City_.id)),
                            cb.equal(skuVendorRoot.get(SkuVendor_.vendor).get(Vendor_.id), request.getVendorId())
                    );
                    predicates.add(cb.exists(skuVendorQuery.select(skuVendorRoot)));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(SkuPrice_.sku).get(Sku_.status), request.getStatus()));
                }

                if (request.getProductId() != null) {
                    predicates.add(cb.equal(root.get(SkuPrice_.sku).get(Sku_.product).get(Product_.id), request.getProductId()));
                }

                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(SkuPrice_.sku).get(Sku_.id), request.getSkuId()));
                }

                if (request.getProductName() != null) {
                    predicates.add(cb.like(root.get(SkuPrice_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                }

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<SkuPriceWrapper> getSkuPriceList(final SkuPriceListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<SkuPrice> page = skuPriceService.findAll(getSkuPriceListSpecification(request), pageable);
        QueryResponse<SkuPriceWrapper> res = new QueryResponse<SkuPriceWrapper>();
        res.setContent(convertSkuPriceToWrapper(page.getContent(), request.getType()));
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    private List<SkuPriceWrapper> convertSkuPriceToWrapper(List<SkuPrice> skuPriceList, Short type) {
        List<SkuPriceWrapper> wrappers = new ArrayList<>();
        for (SkuPrice skuPrice : skuPriceList) {
            Sku sku = skuPrice.getSku();
            City city = skuPrice.getCity();
            SkuVendor skuVendor = skuVendorService.findOne(city.getId(), sku.getId());
            StockTotal stockTotal = stockTotalService.findStockTotal(city.getId(), sku.getId());

            SkuPriceWrapper wrapper = new SkuPriceWrapper();

            if (type != 0) {
                SkuPriceHistory history = skuPriceHistoryService.findByCityIdAndSkuIdAndTypeOrderByCreateDateDesc(city.getId(), sku.getId(), type.intValue());
                if (history != null) {
                    wrapper.setModifyAdminUser(history.getOperator().getRealname());
                    wrapper.setModifyTime(df.format(history.getCreateDate()));
                    wrapper.setReason(history.getReason());
                }
            }

            wrapper.setCityId(city.getId());
            wrapper.setCity(city.getName());
            wrapper.setVendor(skuVendor == null || skuVendor.getVendor() == null ? StringUtils.EMPTY : skuVendor.getVendor().getName());
            wrapper.setProductId(sku.getProduct().getId());
            wrapper.setSkuId(sku.getId());
            wrapper.setName(sku.getName());
            wrapper.setStatus(SkuStatus.fromInt(sku.getStatus()));
            wrapper.setCapacityInBundle(sku.getCapacityInBundle());
            wrapper.setSingleUnit(sku.getSingleUnit());
            wrapper.setBundleUnit(sku.getBundleUnit());
            wrapper.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());

            wrapper.setOldFixedPrice(getPrice(skuPrice, skuPrice.getOldFixedPrice()));
            wrapper.setFixedPrice(getPrice(skuPrice, skuPrice.getFixedPrice()));
            wrapper.setFixedPriceInc(getPriceInc(wrapper.getFixedPrice(), wrapper.getOldFixedPrice()));
            wrapper.setFixedPriceIncRate(NumberUtils.getFormatRate(wrapper.getFixedPrice(), wrapper.getOldFixedPrice()));

            wrapper.setOldLastPurchasePrice(getPrice(skuPrice, skuPrice.getOldPurchasePrice()));
            wrapper.setLastPurchasePrice(getPrice(skuPrice, skuPrice.getPurchasePrice()));
            wrapper.setLastPurchasePriceInc(getPriceInc(wrapper.getLastPurchasePrice(), wrapper.getOldLastPurchasePrice()));
            wrapper.setLastPurchasePriceIncRate(NumberUtils.getFormatRate(wrapper.getLastPurchasePrice(), wrapper.getOldLastPurchasePrice()));

            wrapper.setOldSingleSalePriceLimit(getPrice(skuPrice, skuPrice.getOldSingleSalePriceLimit()));
            wrapper.setSingleSalePriceLimit(getPrice(skuPrice, skuPrice.getSingleSalePriceLimit()));
            wrapper.setSingleSalePriceLimitInc(getPriceInc(wrapper.getSingleSalePriceLimit(), wrapper.getOldSingleSalePriceLimit()));
            wrapper.setSingleSalePriceLimitIncRate(NumberUtils.getFormatRate(wrapper.getSingleSalePriceLimit(), wrapper.getOldSingleSalePriceLimit()));

            wrapper.setOldBundleSalePriceLimit(getPrice(skuPrice, skuPrice.getOldBundleSalePriceLimit()));
            wrapper.setBundleSalePriceLimit(getPrice(skuPrice, skuPrice.getBundleSalePriceLimit()));
            wrapper.setBundleSalePriceLimitInc(getPriceInc(wrapper.getBundleSalePriceLimit(), wrapper.getOldBundleSalePriceLimit()));
            wrapper.setBundleSalePriceLimitIncRate(NumberUtils.getFormatRate(wrapper.getBundleSalePriceLimit(), wrapper.getOldBundleSalePriceLimit()));

            wrapper.setOldSingleSalePrice(getPrice(skuPrice, skuPrice.getOldSingleSalePrice()));
            wrapper.setSingleSalePrice(getPrice(skuPrice, skuPrice.getSingleSalePrice()));
            wrapper.setSingleSalePriceInc(getPriceInc(wrapper.getSingleSalePrice(), wrapper.getOldSingleSalePrice()));
            wrapper.setSingleSalePriceIncRate(NumberUtils.getFormatRate(wrapper.getSingleSalePrice(), wrapper.getOldSingleSalePrice()));

            wrapper.setOldBundleSalePrice(getPrice(skuPrice, skuPrice.getOldBundleSalePrice()));
            wrapper.setBundleSalePrice(getPrice(skuPrice, skuPrice.getBundleSalePrice()));
            wrapper.setBundleSalePriceInc(getPriceInc(wrapper.getBundleSalePrice(), wrapper.getOldBundleSalePrice()));
            wrapper.setBundleSalePriceIncRate(NumberUtils.getFormatRate(wrapper.getBundleSalePrice(), wrapper.getOldBundleSalePrice()));

            wrappers.add(wrapper);
        }
        return wrappers;
    }

    private BigDecimal getPrice(SkuPrice skuPrice, BigDecimal price) {
        return NumberUtils.cancelNull(skuPrice == null ? BigDecimal.ZERO : price);
    }

    private BigDecimal getPriceInc(BigDecimal newPrice, BigDecimal oldPrice) {
        return newPrice.subtract(oldPrice);
    }

    public List<SkuPriceWrapper> getAllSkuPriceList(final SkuPriceListRequest request) {
        return convertSkuPriceToWrapper(skuPriceService.findAll(getSkuPriceListSpecification(request)), request.getType());
    }

    public String exportSkuPriceList(SkuPriceListRequest request) throws Exception {

        List<SkuPriceWrapper> list = getAllSkuPriceList(request);

        final String fileName = "商品信息列表.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<SkuPriceWrapper>> beans = new ArrayList<>();
        beans.add(list);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("商品信息列表");

        String template = null;
        int type = request.getType().intValue();

        if (type == 0) {
            template = ExportExcelUtils.SKU_PRICE_LIST_TEMPLATE;
        } else if (SkuPriceType.FIXED_PRICE.getValue() == type) {
            template = ExportExcelUtils.SKU_FIXED_PRICE_LIST_TEMPLATE;
        } else if (SkuPriceType.SALE_PRICE_LIMIT.getValue() == type) {
            template = ExportExcelUtils.SKU_PRICE_LIMIT_LIST_TEMPLATE;
        } else if (SkuPriceType.PURCHASE_PRICE.getValue() == type) {
            template = ExportExcelUtils.SKU_PURCHASE_PRICE_LIST_TEMPLATE;
        } else if (SkuPriceType.SALE_PRICE.getValue() == type) {
            template = ExportExcelUtils.SKU_SALE_PRICE_LIST_TEMPLATE;
        }

        return ExportExcelUtils.generateMultiSheetExcel(beans, "skuPrices", sheetNames, beanParams, fileName, template).getPath();
    }

    public void asyncExportSkuPriceList(final SkuPriceListRequest request, AdminUser adminUser) throws Exception {
        asyncTaskService.export(
                request,
                adminUser,
                "商品信息",
                new AsyncTask(){
                    @Override
                    public TaskResult proceed() throws Exception {
                        TaskResult taskResult = new TaskResult();
                        taskResult.setResult(exportSkuPriceList(request));
                        return taskResult;
                    }
                }
        );
    }
}
