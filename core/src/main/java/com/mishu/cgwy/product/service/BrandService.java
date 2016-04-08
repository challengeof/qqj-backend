package com.mishu.cgwy.product.service;

import com.mishu.cgwy.product.domain.Brand;
import com.mishu.cgwy.product.domain.Brand_;
import com.mishu.cgwy.product.dto.BrandRequest;
import com.mishu.cgwy.product.repository.BrandRepository;
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

/**
 * Created by bowen on 2015/4/10.
 */
@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public Brand updateBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Transactional
    public Brand getBrandById(Long id) {
        return brandRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<Brand> getBrandList(final BrandRequest request) {

        return brandRepository.findAll(new Specification<Brand>() {
            @Override
            public Predicate toPredicate(Root<Brand> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(Brand_.id), request.getBrandId()));
                }
                if (request.getBrandName() != null) {
                    predicates.add(cb.like(root.get(Brand_.brandName), "%" + request.getBrandName() + "%"));
                }
                if (request.getStatus() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(Brand_.status), request.getStatus()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, new PageRequest(request.getPage(), request.getPageSize()));
    }

}
