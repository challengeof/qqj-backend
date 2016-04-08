package com.mishu.cgwy.common.service;

import com.mishu.cgwy.common.controller.VersionQueryRequest;
import com.mishu.cgwy.common.domain.Version;
import com.mishu.cgwy.common.domain.Version_;
import com.mishu.cgwy.common.repository.VersionRepository;
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
 * Created by kaicheng on 4/16/15.
 */
@Service
public class VersionService {
    @Autowired
    private VersionRepository versionRepository;

    public List<Version> getVersionByVersionCode(Integer versionCode) {
        return versionRepository.findByVersionCodeGreaterThan(versionCode);
    }

    @Transactional
    public Version updateVersion(Version version) {
        return versionRepository.save(version);
    }

    @Transactional
    public Version getVersionById(Long id) {
        return versionRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<Version> getVersionList(final VersionQueryRequest request) {

        return versionRepository.findAll(new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                query.orderBy(cb.desc(root.get(Version_.id)));
                List<Predicate> predicates = new ArrayList<>();
                if (request.getVersionCode() != null) {
                    predicates.add(cb.equal(root.get(Version_.versionCode), request.getVersionCode()));
                }
                if (request.getVersionName() != null) {
                    predicates.add(cb.like(root.get(Version_.versionName), "%" + request.getVersionName() + "%"));
                }
                if (request.getComment() != null) {
                    predicates.add(cb.like(root.get(Version_.comment), "%" + request.getComment() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, new PageRequest(request.getPage(), request.getPageSize()));
    }

}
