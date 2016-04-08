package com.mishu.cgwy.carRoute.service;

import com.mishu.cgwy.carRoute.domain.CarRoute;
import com.mishu.cgwy.carRoute.domain.CarRoute_;
import com.mishu.cgwy.carRoute.repository.CarRouteRepository;
import com.mishu.cgwy.carRoute.request.CarRouteRequestQuery;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.stock.domain.Depot_;
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
 * Created by xgl on 2016/04/05.
 */
@Service
public class CarRouteService {
    @Autowired
    private CarRouteRepository carRouteRepository;

    public CarRoute updateCarRoute(CarRoute carRoute){
        return carRouteRepository.save(carRoute);
    }

    public CarRoute findOne(Long id){
        return carRouteRepository.findOne(id);
    }

    public void deleteCarRoute(Long id){
        carRouteRepository.delete(id);
    }

    public Page<CarRoute> getCarRouteList(CarRouteRequestQuery query){
        return carRouteRepository.findAll(new CarRouteSpecification(query),new PageRequest(query.getPage(),query.getPageSize()));
    }

    private class CarRouteSpecification implements Specification<CarRoute>{

        private CarRouteRequestQuery query;

        public CarRouteSpecification(CarRouteRequestQuery query){this.query = query;}
        @Override
        public Predicate toPredicate(Root<CarRoute> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            if(query.getName() != null){
                predicates.add(cb.equal(root.get(CarRoute_.name),query.getName()));
            }

            if (query.getPrice() != null){
                predicates.add(cb.equal(root.get(CarRoute_.price),query.getPrice()));
            }

            if (query.getCityId() != null){
                predicates.add(cb.equal(root.get(CarRoute_.city).get(City_.id),query.getCityId()));
            }

            if (query.getDepotId() != null){
                predicates.add(cb.equal(root.get(CarRoute_.depot).get(Depot_.id),query.getDepotId()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}

