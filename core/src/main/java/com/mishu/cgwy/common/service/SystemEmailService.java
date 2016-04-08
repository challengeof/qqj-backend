
package com.mishu.cgwy.common.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.controller.SystemEmailRequest;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.SystemEmail;
import com.mishu.cgwy.common.domain.SystemEmail_;
import com.mishu.cgwy.common.repository.SystemEmailRepository;
import com.mishu.cgwy.error.UserDefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SystemEmailService {

    @Autowired
    private SystemEmailRepository systemEmailRepository;

    @Transactional(readOnly = true)
    public SystemEmail findOne(Long id) {
        return systemEmailRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<SystemEmail> getSystemEmailList(final SystemEmailRequest request, final AdminUser adminUser) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, SystemEmail_.type.getName()),
                new Sort.Order(Sort.Direction.ASC, SystemEmail_.city.getName()));
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), sort);
        Page<SystemEmail> page = systemEmailRepository.findAll(new Specification<SystemEmail>() {
            @Override
            public Predicate toPredicate(Root<SystemEmail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (null != adminUser) {
                    Set<Long> cityIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(SystemEmail_.city).get(City_.id).in(cityIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SystemEmail_.city).get(City_.id), request.getCityId()));
                }
                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(SystemEmail_.type), request.getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    @Transactional
    public SystemEmail addSystemEmail(SystemEmail systemEmail){

        if (findSystemEmailByCityAndType(systemEmail.getCity().getId(), systemEmail.getType()) != null){
            throw new UserDefinedException("该城市已设置过该类型的Email");
        }

        return systemEmailRepository.save(systemEmail);
    }

    @Transactional
    public SystemEmail saveSystemEmail(SystemEmail systemEmail){
        return systemEmailRepository.save(systemEmail);
    }

    @Transactional
    public void deleteSystemEmail(Long id){
        systemEmailRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public SystemEmail findSystemEmailByCityAndType(Long cityId, int type) {
        final SystemEmail systemEmail = systemEmailRepository.findByCityIdAndType(cityId, type);
        return systemEmail;
    }

}


