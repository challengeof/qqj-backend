package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.controller.PaymentQueryResponse;
import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.dto.PaymentListRequest;
import com.mishu.cgwy.accounting.enumeration.PaymentStatus;
import com.mishu.cgwy.accounting.repository.PaymentRepository;
import com.mishu.cgwy.accounting.vo.PaymentVo;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangguodong on 15/10/12.
 */
@Service
public class PaymentService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    PaymentRepository paymentRepository;

    private Specification<Payment> getPaymentListSpec(final PaymentListRequest request) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Payment_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(Payment_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(Payment_.id), request.getId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Payment_.status), request.getStatus()));
                }

                if (request.getMethodCode() != null) {
                    predicates.add(cb.equal(root.get(Payment_.collectionPaymentMethod).get(CollectionPaymentMethod_.code), request.getMethodCode()));
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Payment_.payDate), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Payment_.payDate), request.getEndDate()));
                }

                if (request.getMinAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Payment_.amount), request.getMinAmount()));
                }

                if (request.getMaxAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Payment_.amount), request.getMaxAmount()));
                }

                if (request.getCreator() != null) {
                    predicates.add(cb.like(root.get(Payment_.creator).get(AdminUser_.realname), "%" + request.getCreator() + "%"));
                }

                query.orderBy(cb.desc(root.get(Payment_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<PaymentVo> getPaymentList(PaymentListRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Payment> root = query.from(Payment.class);

        query.multiselect(
                cb.sum(root.get(Payment_.amount))
        );

        Specification<Payment> specification = getPaymentListSpec(request);
        query.where(specification.toPredicate(root, query, cb));

        List<Tuple> tupleList = entityManager.createQuery(query).getResultList();

        BigDecimal totalAmount = null;

        if (CollectionUtils.isNotEmpty(tupleList)) {
            Tuple tuple = tupleList.get(0);
            totalAmount = (BigDecimal)tuple.get(0);
        }

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<Payment> page = paymentRepository.findAll(specification, pageable);

        List<PaymentVo> list = new ArrayList<>();
        for (Payment payment : page.getContent()) {
            PaymentVo paymentVo = new PaymentVo();
            paymentVo.setId(payment.getId());
            paymentVo.setVendor(payment.getVendor().getName());
            paymentVo.setMethod(payment.getCollectionPaymentMethod().getName());
            paymentVo.setAmount(payment.getAmount());
            paymentVo.setPayDate(payment.getPayDate());
            paymentVo.setCreator(payment.getCreator().getRealname());
            paymentVo.setStatus(PaymentStatus.fromInt(payment.getStatus()));
            paymentVo.setRemark(payment.getRemark());
            list.add(paymentVo);
        }

        PaymentQueryResponse<PaymentVo> res = new PaymentQueryResponse<PaymentVo>();
        res.setTotalAmount(totalAmount);
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getOne(Long id) {
        return paymentRepository.getOne(id);
    }

    public HttpEntity<byte[]> exportPayment(PaymentListRequest request) throws Exception {
        List<Payment> list = paymentRepository.findAll(getPaymentListSpec(request));

        List<PaymentVo> paymentVoList = new ArrayList<>();
        for (Payment payment : list) {
            PaymentVo paymentVo = new PaymentVo();
            paymentVo.setId(payment.getId());
            paymentVo.setVendor(payment.getVendor().getName());
            paymentVo.setMethod(payment.getCollectionPaymentMethod().getName());
            paymentVo.setAmount(payment.getAmount());
            paymentVo.setPayDate(payment.getPayDate());
            paymentVo.setCreator(payment.getCreator().getRealname());
            paymentVo.setStatus(PaymentStatus.fromInt(payment.getStatus()));
            paymentVo.setRemark(payment.getRemark());
            paymentVoList.add(paymentVo);
        }

        final String fileName = "payment-list.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<PaymentVo>> beans = new ArrayList<>();
        beans.add(paymentVoList);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("付款");

        return ExportExcelUtils.generateExcelBytes(beans, "paymentList", sheetNames, beanParams, fileName, ExportExcelUtils.PAYMENT_LIST_TEMPLATE);
    }
}
