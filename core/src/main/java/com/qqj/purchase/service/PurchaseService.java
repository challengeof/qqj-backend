package com.qqj.purchase.service;

import com.qqj.org.domain.Customer;
import com.qqj.org.domain.Customer_;
import com.qqj.org.domain.Team_;
import com.qqj.org.enumeration.CustomerAuditStage;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.purchase.controller.AuditPurchaseRequest;
import com.qqj.purchase.controller.PurchaseListRequest;
import com.qqj.purchase.domain.Purchase;
import com.qqj.purchase.domain.Purchase_;
import com.qqj.purchase.repository.PurchaseRepository;
import com.qqj.purchase.wrapper.PurchaseWrapper;
import com.qqj.response.Response;
import com.qqj.response.query.QueryResponse;
import com.qqj.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public Purchase get(Long id) {
        return purchaseRepository.getOne(id);
    }

    public void save(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    @Transactional
    public Purchase auditPurchase(Purchase purchase, AuditPurchaseRequest request) {

        if (CustomerAuditStage.get(request.getType()) == CustomerAuditStage.STAGE_1) {
            if (request.getResult().shortValue() == (short)0) {
                purchase.setStatus(CustomerAuditStatus.DIRECT_LEADER_REJECT.getValue());
            } else {
                purchase.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
            }
        } else if (CustomerAuditStage.get(request.getType()) == CustomerAuditStage.STAGE_2) {
            if (request.getResult().shortValue() == (short)0) {
                purchase.setStatus(CustomerAuditStatus.HQ_REJECT.getValue());
            } else {
                purchase.setStatus(CustomerAuditStatus.PASS.getValue());
            }
        }

        return purchaseRepository.save(purchase);
    }

    public Response<PurchaseWrapper> getPurchaseList(final Customer currentCustomer, final PurchaseListRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<Purchase> page = purchaseRepository.findAll(new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(Purchase_.customer).get(Customer_.name), String.format("%%%s%%", request.getName())));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Purchase_.customer).get(Customer_.status), request.getStatus()));
                }

                if (request.getCertificateNumber() != null) {
                    predicates.add(cb.equal(root.get(Purchase_.customer).get(Customer_.certificateNumber), request.getCertificateNumber()));
                }

                if (request.getLevel() != null) {
                    predicates.add(cb.equal(root.get(Purchase_.customer).get(Customer_.level), request.getLevel()));
                }

                if (request.getTeam() != null) {
                    predicates.add(cb.equal(root.get(Purchase_.customer).get(Customer_.team).get(Team_.id), request.getTeam()));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(Purchase_.customer).get(Customer_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                if (request.getUsername() != null) {
                    predicates.add(cb.like(root.get(Purchase_.customer).get(Customer_.username), String.format("%%%s%%", request.getUsername())));
                }

                if (currentCustomer != null) {
                    predicates.add(cb.equal(root.get(Purchase_.directLeader).get(Customer_.id), currentCustomer.getId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<PurchaseWrapper> res = new QueryResponse<>();
        res.setContent(EntityUtils.toWrappers(page.getContent(), PurchaseWrapper.class));
        res.setTotal(page.getTotalElements());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());

        return res;
    }
}
