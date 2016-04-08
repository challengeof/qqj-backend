
package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.domain.Shelf;
import com.mishu.cgwy.stock.domain.Shelf_;
import com.mishu.cgwy.stock.dto.ShelfRequest;
import com.mishu.cgwy.stock.repository.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShelfService {

    @Autowired
    private ShelfRepository shelfRepository;
    @Autowired
    private EntityManager entityManager;
    
    @Transactional(readOnly = true)
    public Shelf findOne(Long shelfId) {
        return shelfRepository.findOne(shelfId);
    }

    @Transactional(readOnly = true)
    public Page<Shelf> getShelfList(final ShelfRequest request, final AdminUser adminUser) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, Shelf_.depot.getName()),
                new Sort.Order(Sort.Direction.ASC, Shelf_.shelfCode.getName()));
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), sort);
        Page<Shelf> page = shelfRepository.findAll(new Specification<Shelf>() {
            @Override
            public Predicate toPredicate(Root<Shelf> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
                        depotCondition.add(root.get(Shelf_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(Shelf_.depot).get(Depot_.id).in(depotIds));
                    }
                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Shelf_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(Shelf_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getArea() != null) {
                    predicates.add(cb.equal(root.get(Shelf_.area), request.getArea()));
                }
                if (request.getRow() != null) {
                    predicates.add(cb.equal(root.get(Shelf_.row), request.getRow()));
                }
                if (request.getNumber() != null) {
                    predicates.add(cb.equal(root.get(Shelf_.name), request.getNumber()));
                }
                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(Shelf_.name), "%" + request.getName() + "%"));
                }
                if (request.getShelfCode() != null) {
                    predicates.add(cb.like(root.get(Shelf_.shelfCode), request.getShelfCode() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    @Transactional
    public Shelf addShelf(Shelf shelf){

        if (findShelfByDepotAndShelfCode(shelf.getDepot().getId(), shelf.getShelfCode()) != null){
            throw new UserDefinedException("该仓库下存在货位 " + shelf.getShelfCode());
        }

        return shelfRepository.save(shelf);
    }

    @Transactional
    public Shelf saveShelf(Shelf shelf){
        return shelfRepository.save(shelf);
    }

    @Transactional
    public void deleteShelf(Long id){
        shelfRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Shelf findShelfByDepotAndShelfCode(Long depotId, String shelfCode) {
        final List<Shelf> list = shelfRepository.findByDepotIdAndShelfCode(depotId, shelfCode);

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<Shelf> findShelfByDepotAndShelfCodes(Long depotId, List<String> shelfCodes) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Shelf> query = cb.createQuery(Shelf.class);
        final Root<Shelf> root = query.from(Shelf.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Shelf_.depot).get(Depot_.id), depotId));
        predicates.add(root.get(Shelf_.shelfCode).in(shelfCodes));
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(query).getResultList();
    }

}


