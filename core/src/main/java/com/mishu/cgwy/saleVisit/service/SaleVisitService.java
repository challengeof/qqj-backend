package com.mishu.cgwy.saleVisit.service;

import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.saleVisit.domain.SaleVisit;
import com.mishu.cgwy.saleVisit.domain.SaleVisit_;
import com.mishu.cgwy.saleVisit.repository.SaleVisitRepository;
import com.mishu.cgwy.saleVisit.request.SaleVisitQueryRequest;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by apple on 15/8/13.
 */
@Service
public class SaleVisitService {

    @Autowired
    private SaleVisitRepository saleVisitRepository;

    public SaleVisit updateSaleVisit(SaleVisit saleVisit) {
        return saleVisitRepository.save(saleVisit);
    }

    public void deleteSaleVisit(Long id) {
        saleVisitRepository.delete(id);
    }

    public SaleVisit getSaleVisitById(Long id) {
        return saleVisitRepository.getOne(id);
    }

    public Page<SaleVisit> getSaleVisitPage(SaleVisitQueryRequest request) {
        return saleVisitRepository.findAll(new SaleVisitSpecification(request), new PageRequest(request.getPage(), request.getPageSize()));
    }

    private class SaleVisitSpecification implements Specification<SaleVisit> {

        private SaleVisitQueryRequest request;

        public SaleVisitSpecification(SaleVisitQueryRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<SaleVisit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getRestaurantId() != null) {
                predicates.add(cb.equal(root.get(SaleVisit_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if (request.getRestaurantName() != null) {
                predicates.add(cb.like(root.get(SaleVisit_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if(request.getActiveType() != null){
                predicates.add(cb.equal(root.get(SaleVisit_.restaurant).get(Restaurant_.activeType),request.getActiveType()));
            }
            if(request.getVisitId() != null){
                predicates.add(cb.equal(root.get(SaleVisit_.creator).get(AdminUser_.id),request.getVisitId()));
            }
            if (request.getSellerName() != null) {
                predicates.add(cb.like(root.get(SaleVisit_.restaurant).get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.realname), "%" + request.getSellerName() + "%"));
            }
            if (request.getVisitStage() != null) {
                predicates.add(cb.equal(root.get(SaleVisit_.visitStage), request.getVisitStage()));
            }
            if (request.getVisitPurpose() != null) {
                predicates.add(cb.equal(root.get(SaleVisit_.visitPurposes),request.getVisitPurpose()));
            }
            if (request.getStartVisitTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(SaleVisit_.visitTime),request.getStartVisitTime()));
            }
            if (request.getEndVisitTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(SaleVisit_.visitTime),request.getEndVisitTime()));

            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}
