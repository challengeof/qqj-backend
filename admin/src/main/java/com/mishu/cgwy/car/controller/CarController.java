package com.mishu.cgwy.car.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.car.domain.Car;
import com.mishu.cgwy.car.service.CarService;
import com.mishu.cgwy.car.vo.CarVo;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.wrapper.SimpleOrderGroupWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.vo.DepotVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linsen on 15/12/14.
 */
@Controller
public class CarController {
    @Autowired
    private CarService carService;

    @RequestMapping(value = "/api/car/cars", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<CarVo> getCars(CarRequest carRequest,@CurrentAdminUser AdminUser operator){
        Page<Car> cars = carService.getCars(carRequest);
        QueryResponse<CarVo> response = new QueryResponse<>();
        for (Car car : cars) {
            CarVo carVo = new CarVo();
            carVo.setId(car.getId());
            carVo.setName(car.getName());

            CityVo cityVo = new CityVo();
            City city = car.getCity();
            cityVo.setId(city.getId());
            cityVo.setName(city.getName());
            carVo.setCity(cityVo);

            DepotVo depotVo = new DepotVo();
            Depot depot = car.getDepot();
            depotVo.setId(depot.getId());
            depotVo.setName(depot.getName());
            carVo.setDepot(depotVo);

            AdminUserVo adminUserVo = new AdminUserVo();
            AdminUser adminUser = car.getAdminUser();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setRealname(adminUser.getRealname());
            carVo.setAdminUser(adminUserVo);

            carVo.setLicencePlateNumber(car.getLicencePlateNumber());
            carVo.setVehicleLength(car.getVehicleLength());
            carVo.setVehicleWidth(car.getVehicleWidth());
            carVo.setVehicleHeight(car.getVehicleHeight());
            carVo.setVehicleModel(car.getVehicleModel());
            carVo.setWeight(car.getWeight());
            carVo.setCubic(car.getCubic());
            carVo.setStatus(car.getStatus());
            carVo.setExpenses(car.getExpenses());
            carVo.setSource(car.getSource());
            carVo.setTaxingPoint(car.getTaxingPoint());

            response.getContent().add(carVo);
        }

        response.setPage(carRequest.getPage());
        response.setPageSize(carRequest.getPageSize());
        response.setTotal(cars.getTotalElements());

        return response;
    }


    @RequestMapping(value = "/api/car/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public void saveOrUpdateCar(@RequestBody CarRequest carRequest,@CurrentAdminUser AdminUser operator){
        carService.saveOrUpdateCar(carRequest);
    }


    @RequestMapping(value = "/api/car/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CarVo getCar(@PathVariable("id") Long id,@CurrentAdminUser AdminUser operator){
        Car car = carService.getCar(id);
        CarVo carVo = new CarVo();
        carVo.setId(car.getId());
        carVo.setName(car.getName());

        CityVo cityVo = new CityVo();
        City city = car.getCity();
        cityVo.setId(city.getId());
        cityVo.setName(city.getName());
        carVo.setCity(cityVo);

        DepotVo depotVo = new DepotVo();
        Depot depot = car.getDepot();
        depotVo.setId(depot.getId());
        depotVo.setName(depot.getName());
        carVo.setDepot(depotVo);

        AdminUserVo adminUserVo = new AdminUserVo();
        AdminUser adminUser = car.getAdminUser();
        adminUserVo.setId(adminUser.getId());
        adminUserVo.setUsername(adminUser.getUsername());
        adminUserVo.setTelephone(adminUser.getTelephone());
        adminUserVo.setRealname(adminUser.getRealname());
        carVo.setAdminUser(adminUserVo);

        carVo.setLicencePlateNumber(car.getLicencePlateNumber());
        carVo.setVehicleLength(car.getVehicleLength());
        carVo.setVehicleWidth(car.getVehicleWidth());
        carVo.setVehicleHeight(car.getVehicleHeight());
        carVo.setVehicleModel(car.getVehicleModel());
        carVo.setWeight(car.getWeight());
        carVo.setCubic(car.getCubic());
        carVo.setStatus(car.getStatus());
        carVo.setExpenses(car.getExpenses());
        carVo.setSource(car.getSource());
        carVo.setTaxingPoint(car.getTaxingPoint());

        return carVo;
    }
}
