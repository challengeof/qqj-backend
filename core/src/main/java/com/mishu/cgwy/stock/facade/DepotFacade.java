package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.dto.DepotData;
import com.mishu.cgwy.stock.dto.DepotRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.vo.DepotVo;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/9/21.
 */
@Service
public class DepotFacade {

    @Autowired
    private DepotService depotService;

    @Autowired
    private LocationService locationService;


    @Transactional(readOnly = true)
    public List<TreeJsonHasChild> getCityDepots() {
        List<TreeJsonHasChild> cityChildren= new ArrayList<>();
        for (City city : locationService.getAllCities()) {
            TreeJsonHasChild cityChild = new TreeJsonHasChild();
            cityChildren.add(cityChild);
            cityChild.setId(StringUtils.join("c", city.getId()));
            cityChild.setText(city.getName());
            List<TreeJsonHasChild> depotChildren = new ArrayList<>();
            cityChild.setChildren(depotChildren);
            for (Depot depot : depotService.findDepotsByCityId(city.getId())){
                TreeJsonHasChild depotChild = new TreeJsonHasChild();
                depotChildren.add(depotChild);
                depotChild.setId(StringUtils.join("d", depot.getId().toString()));
                depotChild.setText(depot.getName());
            }
        }
        return cityChildren;
    }


    @Transactional(readOnly = true)
    public List<DepotWrapper> findDepotsByCityId(Long cityId, AdminUser adminUser) {
        List<DepotWrapper> depots = new ArrayList<>();
        DepotRequest request = new DepotRequest();
        request.setCityId(cityId);
        for (Depot depot : depotService.findDepotList(request, adminUser)) {
            depots.add(new DepotWrapper(depot));
        }
        return depots;
    }

    @Transactional(readOnly = true)
    public List<DepotWrapper> findDepotsByCityId(Long cityId, Vendor vendor) {
        List<DepotWrapper> depots = new ArrayList<>();
        for (Depot depot : depotService.findDepotsByCityId(cityId)) {
            depots.add(new DepotWrapper(depot));
        }
        return depots;
    }

    @Transactional(readOnly = true)
    public List<DepotWrapper> findDepotList(DepotRequest request, AdminUser adminUser) {
        List<DepotWrapper> depots = new ArrayList<>();
        for (Depot depot : depotService.findDepotList(request, adminUser)) {
            depots.add(new DepotWrapper(depot));
        }
        return depots;
    }

    @Transactional
    public DepotWrapper addDepot(DepotData depotData){
        Depot depot = new Depot();
        depot.setName(depotData.getName());
        depot.setCity(locationService.getCity(depotData.getCityId()));
        if (depotData.getLatitude() != null && depotData.getLongitude() != null) {
            depot.setWgs84Point(new Wgs84Point(depotData.getLongitude(), depotData.getLatitude()));
        }

        return new DepotWrapper(depotService.addDepot(depot));
    }

    @Transactional
    public DepotWrapper updateDepot(Long id, DepotData depotData){
        Depot depot = depotService.findOne(id);
        Depot findDepot = depotService.findDepotByName(depotData.getName());
        if (findDepot != null && !findDepot.getId().equals(depot.getId())){
            throw new UserDefinedException("仓库名称重复");
        }

        if (depotData.getName() != null) {
            depot.setName(depotData.getName());
        }
        if (depotData.getLatitude() != null && depotData.getLongitude() != null) {
            depot.setWgs84Point(new Wgs84Point(depotData.getLongitude(), depotData.getLatitude()));
        } else {
            depot.setWgs84Point(new Wgs84Point(null,null));
        }
        depot.setCity(locationService.getCity(depotData.getCityId()));

        return new DepotWrapper(depotService.updateDepot(depot));
    }

    @Transactional
    public void setMainDepot(Long id) {
        Depot depot = depotService.findOne(id);
        Depot cityDepot = depotService.getMainDepot(depot.getCity().getId());
        if (cityDepot != null) {
            cityDepot.setIsMain(false);
            depotService.updateDepot(cityDepot);
        }
        depot.setIsMain(true);
        depotService.updateDepot(depot);
    }

    @Transactional(readOnly = true)
    public DepotWrapper findDepot(Long id) {
        return new DepotWrapper(depotService.findOne(id));
    }

    @Transactional(readOnly = true)
    public List<DepotVo> findDepotsVoByCityId(Long cityId, AdminUser adminUser) {
        List<DepotVo> depots = new ArrayList<>();
        DepotRequest request = new DepotRequest();
        request.setCityId(cityId);
        for (Depot depot : depotService.findDepotList(request, adminUser)) {
            DepotVo depotVo = new DepotVo();
            depotVo.setId(depot.getId());
            depotVo.setName(depot.getName());
            depots.add(depotVo);
        }
        return depots;
    }
}
