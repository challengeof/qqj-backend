package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.dto.AccountPayableItemListRequest;
import com.mishu.cgwy.accounting.repository.AccountPayableItemRepository;
import com.mishu.cgwy.accounting.wrapper.AccountPayableItemWrapper;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem_;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockIn_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 15/10/21.
 */
@Service
public class AccountPayableItemService {

    @Autowired
    private AccountPayableItemRepository accountPayableItemRepository;

    private Specification<AccountPayableItem> getAccountPayableItemsSpecification(final AccountPayableItemListRequest request) {
        return new Specification<AccountPayableItem>() {
            @Override
            public Predicate toPredicate(Root<AccountPayableItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.vendor).get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getPurchaseOrderType() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.type), request.getPurchaseOrderType()));
                }

                if (request.getAccountPayableType() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.type), request.getAccountPayableType()));
                }

                if (request.getProductName() != null) {
                    Subquery<PurchaseOrderItem> subQuery = query.subquery(PurchaseOrderItem.class);
                    Root<PurchaseOrderItem> subRoot = subQuery.from(PurchaseOrderItem.class);

                    subQuery.where(
                            cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), subRoot.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.id)),
                            cb.like(subRoot.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName()))
                    );
                    predicates.add(cb.exists(subQuery.select(subRoot)));
                }

                if (request.getPurchaseOrderId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()));
                }

                if (request.getPurchaseOrderDateStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.createTime), request.getPurchaseOrderDateStart()));
                }

                if (request.getPurchaseOrderDateEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.createTime), request.getPurchaseOrderDateEnd()));
                }

                if (request.getPayableDateStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.createDate), request.getPayableDateStart()));
                }

                if (request.getPayableDateEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableItem_.accountPayable).get(AccountPayable_.createDate), request.getPayableDateStart()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<AccountPayableItemWrapper> getAccountPayableItems(AccountPayableItemListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<AccountPayableItem> page = accountPayableItemRepository.findAll(getAccountPayableItemsSpecification(request), pageable);

        List<AccountPayableItemWrapper> list = new ArrayList<>();
        for (AccountPayableItem accountPayableItem : page.getContent()) {
            list.add(new AccountPayableItemWrapper(accountPayableItem));
        }

        QueryResponse<AccountPayableItemWrapper> res = new QueryResponse<AccountPayableItemWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    public List<AccountPayableItemWrapper> getAllAccountPayableItems(AccountPayableItemListRequest request) {
        List<AccountPayableItem> accountPayableItems = accountPayableItemRepository.findAll(getAccountPayableItemsSpecification(request));

        List<AccountPayableItemWrapper> list = new ArrayList<>();
        for (AccountPayableItem accountPayableItem : accountPayableItems) {
            list.add(new AccountPayableItemWrapper(accountPayableItem));
        }

        return list;
    }
}
