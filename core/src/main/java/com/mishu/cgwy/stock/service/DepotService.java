
package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.dto.DepotRequest;
import com.mishu.cgwy.stock.repository.DepotRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DepotService {

    @Autowired
    private DepotRepository depotRepository;

    @Transactional(readOnly = true)
    public List<Depot> findDepotsByCityId(Long cityId) {
        return depotRepository.findByCityId(cityId);
    }

    @Transactional(readOnly = true)
    public Depot findOne(Long depotId) {
        return depotRepository.findOne(depotId);
    }

    @Transactional(readOnly = true)
    public List<Depot> findDepotList(final DepotRequest request, final AdminUser adminUser) {

        List<Depot> depots = depotRepository.findAll(new Specification<Depot>() {
            @Override
            public Predicate toPredicate(Root<Depot> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (null != adminUser) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(Depot_.id).in(depotIds));
                    }
                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Depot_.city).get(City_.id), request.getCityId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },new Sort(new Sort.Order(Sort.Direction.ASC, Depot_.city.getName()), new Sort.Order(Sort.Direction.ASC, Depot_.id.getName())));

        return depots;
    }

    @Transactional
    public Depot addDepot(Depot depot){

        if (findDepotByName(depot.getName()) != null){
            throw new UserDefinedException("仓库名称重复");
        }

        return depotRepository.save(depot);
    }

    @Transactional
    public Depot updateDepot(Depot depot){

        return depotRepository.save(depot);
    }

    @Transactional(readOnly = true)
    public Depot findDepotByName(String name) {
        final List<Depot> list = depotRepository.findByName(name);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public Depot getMainDepot(final Long cityId) {
        List<Depot> list = depotRepository.findAll(new Specification<Depot>() {
            @Override
            public Predicate toPredicate(Root<Depot> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.equal(root.get(Depot_.city).get(City_.id), cityId));
                predicates.add(cb.isTrue(root.get(Depot_.isMain)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}


