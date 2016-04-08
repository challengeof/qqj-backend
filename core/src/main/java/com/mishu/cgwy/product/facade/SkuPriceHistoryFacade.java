package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.product.controller.SkuPriceHistoryListRequest;
import com.mishu.cgwy.product.controller.SkuPriceHistoryQueryResponse;
import com.mishu.cgwy.product.controller.SkuPriceListRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.service.SkuPriceHistoryService;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.product.wrapper.SkuPriceHistoryWrapper;
import com.mishu.cgwy.product.wrapper.SkuPriceWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.task.service.AsyncTask;
import com.mishu.cgwy.task.service.AsyncTaskService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SkuPriceHistoryFacade {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dayDf = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    private SkuPriceHistoryService skuPriceHistoryService;

    public SkuPriceHistoryQueryResponse<SkuPriceHistoryWrapper> getSkuPriceHistoryList(SkuPriceHistoryListRequest request) {
        Integer type = request.getType();
        Boolean single = request.getSingle();
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<SkuPriceHistory> page = skuPriceHistoryService.findAll(getSkuPriceHistoryListSpecification(request), pageable);

        SkuPriceHistoryQueryResponse<SkuPriceHistoryWrapper> res = new SkuPriceHistoryQueryResponse<SkuPriceHistoryWrapper>();
        setContent(res, page.getContent(), type, single);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    private void setContent(SkuPriceHistoryQueryResponse<SkuPriceHistoryWrapper> response, List<SkuPriceHistory> skuPriceHistoryList, Integer type, Boolean single) {
        List<SkuPriceHistoryWrapper> wrappers = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        for (SkuPriceHistory skuPriceHistory : skuPriceHistoryList) {
            SkuPriceHistoryWrapper wrapper = new SkuPriceHistoryWrapper();

            BigDecimal price = null;
            if (SkuPriceType.FIXED_PRICE.getValue() == type) {
                price = skuPriceHistory.getFixedPrice();
            } else if (SkuPriceType.SALE_PRICE_LIMIT.getValue() == type) {
                if (single) {
                    price = skuPriceHistory.getSingleSalePriceLimit();
                } else {
                    price = skuPriceHistory.getBundleSalePriceLimit();
                }
            } else if (SkuPriceType.PURCHASE_PRICE.getValue() == type) {
                price = skuPriceHistory.getPurchasePrice();
            } else if (SkuPriceType.SALE_PRICE.getValue() == type) {
                if (single) {
                    price = skuPriceHistory.getSingleSalePrice();
                } else {
                    price = skuPriceHistory.getBundleSalePrice();
                }
            }

            String simpleCreateDate = dayDf.format(skuPriceHistory.getCreateDate());
            price = NumberUtils.cancelNull(price);

            if (price.compareTo(BigDecimal.ZERO) > 0) {
                labels.add(simpleCreateDate);
                data.add(price);
            }

            wrapper.setPrice(price);
            wrapper.setCreateDate(df.format(skuPriceHistory.getCreateDate()));
            wrapper.setOperator(skuPriceHistory.getOperator().getRealname());
            wrapper.setReason(skuPriceHistory.getReason());
            wrappers.add(wrapper);
        }

        String[] labelArray = labels.toArray(new String[labels.size()]);
        ArrayUtils.reverse(labelArray);
        response.setLabels(labelArray);

        BigDecimal[] dataArray = data.toArray(new BigDecimal[data.size()]);
        ArrayUtils.reverse(dataArray);
        response.setData(dataArray);

        response.setContent(wrappers);
    }

    private Specification<SkuPriceHistory> getSkuPriceHistoryListSpecification(final SkuPriceHistoryListRequest request) {
        return new Specification<SkuPriceHistory>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<SkuPriceHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(SkuPriceHistory_.sku).get(Sku_.id), request.getSkuId()));
                predicates.add(cb.equal(root.get(SkuPriceHistory_.type), request.getType()));

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SkuPriceHistory_.createDate), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SkuPriceHistory_.createDate), request.getEndDate()));
                }

                query.orderBy(cb.desc(root.get(SkuPriceHistory_.createDate)));

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
            }
        };
    }
}
