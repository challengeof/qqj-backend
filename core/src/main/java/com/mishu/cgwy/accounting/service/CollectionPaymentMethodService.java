package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod;
import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod_;
import com.mishu.cgwy.accounting.dto.CollectionPaymentMethodRequest;
import com.mishu.cgwy.accounting.repository.CollectionPaymentMethodRepository;
import com.mishu.cgwy.accounting.wrapper.CollectionPaymentMethodWrapper;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.error.UserDefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
 * Created by wangguodong on 15/10/12.
 */
@Service
public class CollectionPaymentMethodService {

    @Autowired
    private CollectionPaymentMethodRepository collectionPaymentMethodRepository;

    @Transactional(readOnly = true)
    public List<CollectionPaymentMethodWrapper> getCollectionPaymentMethods(Long cityId, boolean valid) {
        List<CollectionPaymentMethodWrapper> collectionPaymentMethods = new ArrayList<>();
        List<CollectionPaymentMethod> list = collectionPaymentMethodRepository.findByCityIdAndValid(cityId, valid);

        for (CollectionPaymentMethod collectionPaymentMethod : list) {
            collectionPaymentMethods.add(new CollectionPaymentMethodWrapper(collectionPaymentMethod));
        }

        return collectionPaymentMethods;
    }

    public CollectionPaymentMethod getOne(Long id) {
        return collectionPaymentMethodRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<CollectionPaymentMethod> findCollectionPaymentMethodList(final CollectionPaymentMethodRequest request) {

        List<CollectionPaymentMethod> collectionPaymentMethods = collectionPaymentMethodRepository.findAll(new Specification<CollectionPaymentMethod>() {
            @Override
            public Predicate toPredicate(Root<CollectionPaymentMethod> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(CollectionPaymentMethod_.city).get(City_.id), request.getCityId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, new Sort(new Sort.Order(Sort.Direction.ASC, CollectionPaymentMethod_.city.getName()), new Sort.Order(Sort.Direction.ASC, CollectionPaymentMethod_.id.getName())));

        return collectionPaymentMethods;
    }

    @Transactional
    public CollectionPaymentMethod addMethod(CollectionPaymentMethod collectionPaymentMethod){

        if (findCollectionPaymentMethodByCode(collectionPaymentMethod.getCity().getId(), collectionPaymentMethod.getCode()) != null){
            throw new UserDefinedException("编码已存在");
        }
        if (findCollectionPaymentMethodByName(collectionPaymentMethod.getCity().getId(), collectionPaymentMethod.getName()) != null){
            throw new UserDefinedException("名称已存在");
        }

        return collectionPaymentMethodRepository.save(collectionPaymentMethod);
    }

    @Transactional
    public CollectionPaymentMethod updateMethod(CollectionPaymentMethod collectionPaymentMethod){

        return collectionPaymentMethodRepository.save(collectionPaymentMethod);
    }

    @Transactional(readOnly = true)
    public CollectionPaymentMethod findCollectionPaymentMethodByCode(Long cityId, String code) {
        final List<CollectionPaymentMethod> list = collectionPaymentMethodRepository.findByCityIdAndCode(cityId, code);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public CollectionPaymentMethod findCollectionPaymentMethodByName(Long cityId, String name) {
        final List<CollectionPaymentMethod> list = collectionPaymentMethodRepository.findByCityIdAndName(cityId, name);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
