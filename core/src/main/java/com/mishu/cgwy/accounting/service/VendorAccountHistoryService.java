package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import com.mishu.cgwy.accounting.domain.VendorAccountHistory;
import com.mishu.cgwy.accounting.domain.VendorAccountHistory_;
import com.mishu.cgwy.accounting.dto.VendorAccountHistoryListRequest;
import com.mishu.cgwy.accounting.enumeration.VendorAccountOperationType;
import com.mishu.cgwy.accounting.repository.VendorAccountHistoryRepository;
import com.mishu.cgwy.accounting.vo.VendorAccountHistoryVo;
import com.mishu.cgwy.accounting.wrapper.VendorAccountWrapper;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.response.query.QueryResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wangguodong on 15/10/20.
 */
@Service
public class VendorAccountHistoryService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VendorAccountHistoryRepository vendorAccountHistoryRepository;

    public void save(VendorAccountHistory vendorAccountHistory) {
        vendorAccountHistoryRepository.save(vendorAccountHistory);
    }

    public void delete(Long id) {
        vendorAccountHistoryRepository.delete(id);
    }

    public List<VendorAccountHistory> findByPaymentId(Long id) {
        return vendorAccountHistoryRepository.findByPaymentId(id);
    }

    public List<VendorAccountHistory> findByAccountPayableWriteoffId(Long id) {
        return vendorAccountHistoryRepository.findByAccountPayableWriteoffId(id);
    }

    public VendorAccountWrapper getVendorAccount(final Vendor vendor, final Date statisticalDate) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<VendorAccountHistory> root = query.from(VendorAccountHistory.class);

        Expression e1 = cb.selectCase().when(cb.equal(root.get(VendorAccountHistory_.type), VendorAccountOperationType.PAYABLE.getValue()), root.get(VendorAccountHistory_.unWriteoffAmount)).otherwise(BigDecimal.ZERO);
        Expression e2 = cb.selectCase().when(cb.equal(root.get(VendorAccountHistory_.type), VendorAccountOperationType.PAYMENT.getValue()), root.get(VendorAccountHistory_.amount)).otherwise(BigDecimal.ZERO);
        Expression e3 = cb.selectCase().when(cb.equal(root.get(VendorAccountHistory_.type), VendorAccountOperationType.WRITEOFF.getValue()), root.get(VendorAccountHistory_.amount)).otherwise(BigDecimal.ZERO);
        query.multiselect(
                cb.sum(root.get(VendorAccountHistory_.amount)),
                cb.sum(root.get(VendorAccountHistory_.unWriteoffAmount)),
                cb.sum(e1),
                cb.sum(e2),
                cb.sum(e3)
        );

        Specification<VendorAccountHistory> specification = new Specification<VendorAccountHistory>() {
            List<Predicate> predicates = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<VendorAccountHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                predicates.add(cb.equal(root.get(VendorAccountHistory_.vendor).get(Vendor_.id), vendor.getId()));
                if (statisticalDate != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(VendorAccountHistory_.accountDate), statisticalDate));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        query.where(specification.toPredicate(root, query, cb));
        query.groupBy(root.get(VendorAccountHistory_.vendor));

        VendorAccountWrapper wrapper = new VendorAccountWrapper();
        VendorVo vendorVo = new VendorVo();
        vendorVo.setId(vendor.getId());
        vendorVo.setName(vendor.getName());
        wrapper.setVendor(vendorVo);

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();
        if (CollectionUtils.isNotEmpty(tuples)) {
            Tuple tuple = tuples.get(0);
            wrapper.setBalance((BigDecimal) tuple.get(0));
            wrapper.setPayable((BigDecimal) tuple.get(1));
            wrapper.setTotalHistoryPayable((BigDecimal) tuple.get(2));
            wrapper.setTotalHistoryPayment((BigDecimal) tuple.get(3));
            wrapper.setTotalWriteOffAmount(new BigDecimal(0).subtract((BigDecimal)tuple.get(4)));
        } else {
            wrapper.setBalance(BigDecimal.ZERO);
            wrapper.setPayable(BigDecimal.ZERO);
            wrapper.setTotalHistoryPayable(BigDecimal.ZERO);
            wrapper.setTotalHistoryPayment(BigDecimal.ZERO);
            wrapper.setTotalWriteOffAmount(BigDecimal.ZERO);
        }

        return wrapper;
    }

    private Specification<VendorAccountHistory> getVendorAccountHistoriesSpecification(final VendorAccountHistoryListRequest request) {
        return new Specification<VendorAccountHistory>() {
            @Override
            public Predicate toPredicate(Root<VendorAccountHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(VendorAccountHistory_.vendor).get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(VendorAccountHistory_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(VendorAccountHistory_.accountDate), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(VendorAccountHistory_.accountDate), request.getEndDate()));
                }

                if (request.getTypes() != null) {
                    predicates.add(root.get(VendorAccountHistory_.type).in(request.getTypes()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
    public QueryResponse<VendorAccountHistoryVo> getVendorAccountHistories(VendorAccountHistoryListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<VendorAccountHistory> page = vendorAccountHistoryRepository.findAll(getVendorAccountHistoriesSpecification(request), pageable);

        List<VendorAccountHistoryVo> vendorAccountHistoryVoList = vendorAccountHistoryListToVoList(page.getContent());
        QueryResponse<VendorAccountHistoryVo> res = new QueryResponse<VendorAccountHistoryVo>();
        res.setContent(vendorAccountHistoryVoList);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    private List<VendorAccountHistoryVo> vendorAccountHistoryListToVoList(List<VendorAccountHistory> vendorAccountHistoryList) {
        List<VendorAccountHistoryVo> list = new ArrayList<VendorAccountHistoryVo>();
        for (VendorAccountHistory vendorAccountHistory : vendorAccountHistoryList) {
            VendorAccountHistoryVo vendorAccountHistoryVo = new VendorAccountHistoryVo();
            vendorAccountHistoryVo.setAccountDate(vendorAccountHistory.getAccountDate());

            Vendor vendor = vendorAccountHistory.getVendor();
            VendorVo vendorVo = new VendorVo();
            vendorVo.setId(vendor.getId());
            vendorVo.setName(vendor.getName());
            vendorAccountHistoryVo.setVendor(vendorVo);

            vendorAccountHistoryVo.setType(VendorAccountOperationType.from(vendorAccountHistory.getType()));

            if (vendorAccountHistoryVo.getType().equals(VendorAccountOperationType.PAYABLE)) {
                AccountPayable accountPayable = vendorAccountHistory.getAccountPayable();
                vendorAccountHistoryVo.setPayable(accountPayable.getAmount());
                vendorAccountHistoryVo.setVendorAccountOperationId(accountPayable.getId());
            } else if (vendorAccountHistoryVo.getType().equals(VendorAccountOperationType.WRITEOFF)) {
                AccountPayableWriteoff accountPayableWriteoff = vendorAccountHistory.getAccountPayableWriteoff();
                vendorAccountHistoryVo.setWriteOff(accountPayableWriteoff.getWriteOffAmount());
                vendorAccountHistoryVo.setVendorAccountOperationId(accountPayableWriteoff.getAccountPayable().getId());
            }
            list.add(vendorAccountHistoryVo);
        }

        return list;
    }

    public List<VendorAccountHistoryVo> getAllVendorAccountHistories(VendorAccountHistoryListRequest request) {
        List<VendorAccountHistory> vendorAccountHistories = vendorAccountHistoryRepository.findAll(getVendorAccountHistoriesSpecification(request));

        return vendorAccountHistoryListToVoList(vendorAccountHistories);
    }
}
