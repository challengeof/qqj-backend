package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.dto.VendorAccountListRequest;
import com.mishu.cgwy.accounting.repository.VendorAccountRepository;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
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
import java.util.List;

/**
 * Created by wangguodong on 15/10/14.
 */
@Service
public class VendorAccountService {

    @Autowired
    VendorAccountRepository vendorAccountRepository;

    public void save(VendorAccount vendorAccount) {
        vendorAccountRepository.save(vendorAccount);
    }

    private Specification<VendorAccount> getVendorAccountListSpecification(final VendorAccountListRequest request) {
        return new Specification<VendorAccount>() {
            @Override
            public Predicate toPredicate(Root<VendorAccount> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(VendorAccount_.vendor).get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(VendorAccount_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
    public Page<VendorAccount> find(VendorAccountListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<VendorAccount> page = vendorAccountRepository.findAll(getVendorAccountListSpecification(request), pageable);
        return page;
    }

    public List<VendorAccount> getAllVendorAccounts(final VendorAccountListRequest request) {
        return vendorAccountRepository.findAll(getVendorAccountListSpecification(request));
    }
}
