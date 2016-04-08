package com.mishu.cgwy.car.service;

import com.mishu.cgwy.admin.repository.AdminUserRepository;
import com.mishu.cgwy.car.controller.CarRequest;
import com.mishu.cgwy.car.domain.Car;
import com.mishu.cgwy.car.domain.Car_;
import com.mishu.cgwy.car.repository.CarRepository;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.stock.repository.DepotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * Created by linsen on 15/12/14.
 */
@Service
public class CarService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private DepotRepository depotRepository;


    @Transactional(readOnly = true)
    public Car getCar(Long id){
        return carRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarByDepotId(Long id){
        return carRepository.findByDepotId(id);
    }

    @Transactional(readOnly = true)
    public Car getCarByAdminUserId(Long id){
        return carRepository.findByAdminUserId(id);
    }

    @Transactional(readOnly = true)
    public Page<Car> getCars(final CarRequest carRequest){
        Pageable page = new PageRequest(carRequest.getPage(), carRequest.getPageSize(), Sort.Direction.DESC, "id");
        return carRepository.findAll(new Specification<Car>() {
            @Override
            public Predicate toPredicate(Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(carRequest.getCityId() != null)
                    predicates.add(cb.equal(root.get(Car_.city), carRequest.getCityId()));

                if (carRequest.getDepotId() != null)
                    predicates.add(cb.equal(root.get(Car_.depot) , carRequest.getDepotId()));

                if(carRequest.getStatus() != null)
                    predicates.add(cb.equal(root.get(Car_.status) , carRequest.getStatus()));

                if(carRequest.getTrackerId() != null)
                    predicates.add(cb.equal(root.get(Car_.adminUser) , carRequest.getTrackerId()));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },page);
    }


    @Transactional
    public void saveOrUpdateCar(CarRequest carRequest){

        Car car = new Car();
        if(carRequest.getId() != null)
            car.setId(carRequest.getId());

        car.setName(carRequest.getName());
        car.setStatus(carRequest.getStatus());
        car.setCity(cityRepository.findOne(carRequest.getCityId()));
        car.setAdminUser(adminUserRepository.findOne(carRequest.getTrackerId()));
        car.setDepot(depotRepository.findOne(carRequest.getDepotId()));
        car.setVehicleHeight(carRequest.getVehicleHeight());
        car.setVehicleLength(carRequest.getVehicleLength());
        car.setVehicleWidth(carRequest.getVehicleWidth());
        car.setVehicleModel(carRequest.getVehicleModel());
        car.setLicencePlateNumber(carRequest.getLicencePlateNumber());
        car.setWeight(carRequest.getWeight());
        car.setCubic(carRequest.getCubic());
        car.setExpenses(carRequest.getExpenses());
        car.setTaxingPoint(carRequest.getTaxingPoint());
        car.setSource(carRequest.getSource());

        carRepository.save(car);
    }
}
