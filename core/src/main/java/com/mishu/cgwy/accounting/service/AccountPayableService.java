package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.controller.AccountPayableQueryResponse;
import com.mishu.cgwy.accounting.controller.AccountPayableWriteOffQueryResponse;
import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff_;
import com.mishu.cgwy.accounting.domain.AccountPayable_;
import com.mishu.cgwy.accounting.dto.AccountPayableListRequest;
import com.mishu.cgwy.accounting.dto.AccountPaymentWriteOffListRequest;
import com.mishu.cgwy.accounting.enumeration.AccountPayableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountPayableWriteOffStatus;
import com.mishu.cgwy.accounting.repository.AccountPayableRepository;
import com.mishu.cgwy.accounting.repository.AccountPayableWriteOffRepository;
import com.mishu.cgwy.accounting.wrapper.AccountPayableWrapper;
import com.mishu.cgwy.accounting.wrapper.AccountPayableWriteOffWrapper;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import com.mishu.cgwy.purchase.domain.ReturnNote_;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockIn_;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOut_;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 15/10/13.
 */
@Service
public class AccountPayableService {

    @Autowired
    private AccountPayableRepository accountPayableRepository;

    @Autowired
    private AccountPayableWriteOffRepository accountPayableWriteOffRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public AccountPayable save(AccountPayable accountPayable) {
        return accountPayableRepository.save(accountPayable);
    }

    private Specification<AccountPayable> getAccountPayablesSpecification(final AccountPayableListRequest request) {
        return new Specification<AccountPayable>() {
            @Override
            public Predicate toPredicate(Root<AccountPayable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.vendor).get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getPurchaseVendorId() != null) {
                    Subquery<StockIn> stockInQuery = query.subquery(StockIn.class);
                    Root<StockIn> stockInRoot = stockInQuery.from(StockIn.class);
                    stockInQuery.where(
                            cb.equal(stockInRoot.get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getPurchaseVendorId()),
                            cb.equal(stockInRoot.get(StockIn_.id), root.get(AccountPayable_.stockIn).get(StockIn_.id))
                    );
                    Subquery<StockOut> stockOutQuery = query.subquery(StockOut.class);
                    Root<StockOut> stockOutRoot = stockOutQuery.from(StockOut.class);
                    stockOutQuery.where(
                            cb.equal(stockOutRoot.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getPurchaseVendorId()),
                            cb.equal(stockOutRoot.get(StockOut_.id), root.get(AccountPayable_.stockOut).get(StockOut_.id))
                    );

                    predicates.add(
                        cb.or(
                                cb.exists(stockInQuery.select(stockInRoot)),
                                cb.exists(stockOutQuery.select(stockOutRoot))
                        )
                    );
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.status), request.getStatus()));
                } else if (!request.getIncludeWriteOff()) {
                    predicates.add(cb.notEqual(root.get(AccountPayable_.status), AccountPayableStatus.WRITEOFF.getValue()));
                }

                if (request.getPurchaseOrderId() != null) {
                    Subquery<StockIn> stockInQuery = query.subquery(StockIn.class);
                    Root<StockIn> stockInRoot = stockInQuery.from(StockIn.class);
                    stockInQuery.where(
                            cb.equal(stockInRoot.get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()),
                            cb.equal(stockInRoot.get(StockIn_.id), root.get(AccountPayable_.stockIn).get(StockIn_.id))
                    );
                    Subquery<StockOut> stockOutQuery = query.subquery(StockOut.class);
                    Root<StockOut> stockOutRoot = stockOutQuery.from(StockOut.class);
                    stockOutQuery.where(
                            cb.equal(stockOutRoot.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()),
                            cb.equal(stockOutRoot.get(StockOut_.id), root.get(AccountPayable_.stockOut).get(StockOut_.id))
                    );

                    predicates.add(
                            cb.or(
                                    cb.exists(stockInQuery.select(stockInRoot)),
                                    cb.exists(stockOutQuery.select(stockOutRoot))
                            )
                    );
                }

                if (request.getAccountPayableType() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.type), request.getAccountPayableType()));
                }

                if (request.getStockInId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.stockIn).get(StockIn_.id), request.getStockInId()));
                }

                if (request.getStockOutId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayable_.stockOut).get(StockOut_.id), request.getStockOutId()));
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayable_.createDate), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayable_.createDate), DateUtils.addDays(request.getEndDate(), 1)));
                }

                if (request.getWriteOffStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayable_.writeOffDate), request.getWriteOffStartDate()));
                }

                if (request.getWriteOffEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayable_.writeOffDate), request.getWriteOffEndDate()));
                }

                if (request.getMinAccountPayableAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayable_.amount), request.getMinAccountPayableAmount()));
                }

                if (request.getMaxAccountPayableAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayable_.amount), request.getMaxAccountPayableAmount()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<AccountPayableWrapper> getAccountPayables(final AccountPayableListRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<AccountPayable> root = query.from(AccountPayable.class);

        query.multiselect(
                cb.sum(root.get(AccountPayable_.amount)),
                cb.sum(root.get(AccountPayable_.writeOffAmount))
        );

        Specification<AccountPayable> specification = getAccountPayablesSpecification(request);
        query.where(specification.toPredicate(root, query, cb));

        List<Tuple> tupleList = entityManager.createQuery(query).getResultList();

        BigDecimal totalAmount = null;
        BigDecimal totalWriteOffAmount = null;
        BigDecimal totalUnWriteOffAmount = null;

        if (CollectionUtils.isNotEmpty(tupleList)) {
            Tuple tuple = tupleList.get(0);
            totalAmount = (BigDecimal)tuple.get(0);
            totalWriteOffAmount = (BigDecimal)tuple.get(1);

            if (totalAmount != null && totalAmount != null) {
                totalUnWriteOffAmount = totalAmount.subtract(totalWriteOffAmount);
            }
        }

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<AccountPayable> page = accountPayableRepository.findAll(specification, pageable);

        List<AccountPayableWrapper> list = new ArrayList<>();
        for (AccountPayable accountPayable : page.getContent()) {
            list.add(new AccountPayableWrapper(accountPayable));
        }

        AccountPayableQueryResponse<AccountPayableWrapper> res = new AccountPayableQueryResponse<AccountPayableWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        res.setTotalAmount(totalAmount);
        res.setTotalWriteOffAmount(totalWriteOffAmount);
        res.setTotalUnWriteOffAmount(totalUnWriteOffAmount);

        return res;
    }

    @Transactional
    public AccountPayable getOne(Long id) {
        return accountPayableRepository.getOne(id);
    }

    private Specification<AccountPayableWriteoff> getAccountPayableWriteOffsSpecification(final AccountPaymentWriteOffListRequest request) {
        return new Specification<AccountPayableWriteoff>() {
            @Override
            public Predicate toPredicate(Root<AccountPayableWriteoff> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.vendor).get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getPurchaseVendorId() != null) {
                    Subquery<StockIn> stockInQuery = query.subquery(StockIn.class);
                    Root<StockIn> stockInRoot = stockInQuery.from(StockIn.class);
                    stockInQuery.where(
                            cb.equal(stockInRoot.get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getPurchaseVendorId()),
                            cb.equal(stockInRoot.get(StockIn_.id), root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.id))
                    );
                    Subquery<StockOut> stockOutQuery = query.subquery(StockOut.class);
                    Root<StockOut> stockOutRoot = stockOutQuery.from(StockOut.class);
                    stockOutQuery.where(
                            cb.equal(stockOutRoot.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getPurchaseVendorId()),
                            cb.equal(stockOutRoot.get(StockOut_.id), root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockOut).get(StockOut_.id))
                    );

                    predicates.add(
                            cb.or(
                                    cb.exists(stockInQuery.select(stockInRoot)),
                                    cb.exists(stockOutQuery.select(stockOutRoot))
                            )
                    );
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.status), request.getStatus()));
                }

                if (request.getPurchaseOrderId() != null) {
                    Subquery<StockIn> stockInQuery = query.subquery(StockIn.class);
                    Root<StockIn> stockInRoot = stockInQuery.from(StockIn.class);
                    stockInQuery.where(
                            cb.equal(stockInRoot.get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()),
                            cb.equal(stockInRoot.get(StockIn_.id), root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.id))
                    );
                    Subquery<StockOut> stockOutQuery = query.subquery(StockOut.class);
                    Root<StockOut> stockOutRoot = stockOutQuery.from(StockOut.class);
                    stockOutQuery.where(
                            cb.equal(stockOutRoot.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()),
                            cb.equal(stockOutRoot.get(StockOut_.id), root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockOut).get(StockOut_.id))
                    );

                    predicates.add(
                            cb.or(
                                    cb.exists(stockInQuery.select(stockInRoot)),
                                    cb.exists(stockOutQuery.select(stockOutRoot))
                            )
                    );
                }

                if (request.getAccountPayableType() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.type), request.getAccountPayableType()));
                }

                if (request.getStockInId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.id), request.getStockInId()));
                }

                if (request.getStockOutId() != null) {
                    predicates.add(cb.equal(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockOut).get(StockOut_.id), request.getStockOutId()));
                }

                if (request.getStockInStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.receiveDate), request.getStockInStartDate()));
                }

                if (request.getStockInEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.stockIn).get(StockIn_.receiveDate), request.getStockInEndDate()));
                }

                if (request.getMinAccountPayableAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.amount), request.getMinAccountPayableAmount()));
                }

                if (request.getMaxAccountPayableAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableWriteoff_.accountPayable).get(AccountPayable_.amount), request.getMaxAccountPayableAmount()));
                }

                if (request.getWriteOffStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableWriteoff_.writeOffDate), request.getWriteOffStartDate()));
                }

                if (request.getWriteOffEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableWriteoff_.writeOffDate), request.getWriteOffEndDate()));
                }

                if (request.getWriteOffCanceledStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(AccountPayableWriteoff_.cancelDate), request.getWriteOffCanceledStartDate()));
                }

                if (request.getWriteOffCanceledEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(AccountPayableWriteoff_.cancelDate),request.getWriteOffCanceledEndDate()));
                }

                if (request.getWriteOffer() != null) {
                    predicates.add(cb.like(root.get(AccountPayableWriteoff_.writeOffer).get(AdminUser_.realname), "%" + request.getWriteOffer() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public List<AccountPayableWriteOffWrapper> getAllAccountPayableWriteOffs(final AccountPaymentWriteOffListRequest request) {

        List<AccountPayableWriteoff> accountPayableWriteoffs = accountPayableWriteOffRepository.findAll(getAccountPayableWriteOffsSpecification(request));

        List<AccountPayableWriteOffWrapper> list = new ArrayList<>();
        for (AccountPayableWriteoff accountPayableWriteoff : accountPayableWriteoffs) {
            list.add(new AccountPayableWriteOffWrapper(accountPayableWriteoff));
        }

        return list;
    }

    public QueryResponse<AccountPayableWriteOffWrapper> getAccountPayableWriteOffs(final AccountPaymentWriteOffListRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<AccountPayableWriteoff> root = query.from(AccountPayableWriteoff.class);

        Expression e1 = cb.selectCase().when(cb.equal(root.get(AccountPayableWriteoff_.status), AccountPayableWriteOffStatus.NORMAL.getValue()), root.get(AccountPayableWriteoff_.writeOffAmount)).otherwise(BigDecimal.ZERO);

        query.multiselect(
            cb.sum(e1)
        );

        Specification<AccountPayableWriteoff> specification = getAccountPayableWriteOffsSpecification(request);
        query.where(specification.toPredicate(root, query, cb));

        List<Tuple> tupleList = entityManager.createQuery(query).getResultList();

        BigDecimal totalWriteOffAmount = null;

        if (CollectionUtils.isNotEmpty(tupleList)) {
            Tuple tuple = tupleList.get(0);
            totalWriteOffAmount = (BigDecimal)tuple.get(0);
        }

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<AccountPayableWriteoff> page = accountPayableWriteOffRepository.findAll(specification, pageable);

        List<AccountPayableWriteOffWrapper> list = new ArrayList<>();
        for (AccountPayableWriteoff accountPayableWriteoff : page.getContent()) {
            list.add(new AccountPayableWriteOffWrapper(accountPayableWriteoff));
        }

        AccountPayableWriteOffQueryResponse<AccountPayableWriteOffWrapper> res = new AccountPayableWriteOffQueryResponse<AccountPayableWriteOffWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        res.setTotalWriteOffAmount(totalWriteOffAmount);

        return res;

    }

    public List<AccountPayable> findByPurchaseOrderId(final Long id) {
        List<AccountPayable> accountPayables = accountPayableRepository.findAll(new Specification<AccountPayable>() {
            @Override
            public Predicate toPredicate(Root<AccountPayable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), id));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        return accountPayables;
    }

    public List<AccountPayableWrapper> getAllAccountPayables(final AccountPayableListRequest request) {

        List<AccountPayable> accountPayables = accountPayableRepository.findAll(getAccountPayablesSpecification(request));

        List<AccountPayableWrapper> list = new ArrayList<>();
        for (AccountPayable accountPayable : accountPayables) {
            list.add(new AccountPayableWrapper(accountPayable));
        }

        return list;
    }
}
