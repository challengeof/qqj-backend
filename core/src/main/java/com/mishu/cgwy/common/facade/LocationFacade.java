package com.mishu.cgwy.common.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.controller.WarehouseRequest;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.wrapper.*;
import com.mishu.cgwy.organization.controller.AddBlockRequest;
import com.mishu.cgwy.organization.controller.BlockQueryRequest;
import com.mishu.cgwy.organization.controller.BlockQueryResponse;
import com.mishu.cgwy.organization.controller.UpdateBlockQueryRequest;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class LocationFacade {
    @Autowired
    private LocationService locationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DepotService depotService;


    public List<SimpleRegionWrapper> getRegionWrapper(Long cityId) {
        List<Region> regions = locationService.getRegions(cityId);
        List<SimpleRegionWrapper> lists = new ArrayList<SimpleRegionWrapper>();
        for (Region r : regions) {
            SimpleRegionWrapper rw = new SimpleRegionWrapper(r);
            lists.add(rw);
        }
        return lists;
    }

    public List<ZoneWrapper> getZoneWrapper(Long regionId) {
        List<Zone> zones = locationService.getZones(regionId);
        List<ZoneWrapper> lists = new ArrayList<ZoneWrapper>();
        for (Zone r : zones) {
            ZoneWrapper zw = new ZoneWrapper(r);
            lists.add(zw);
        }
        return lists;
    }

    public CityWrapper getCity(Long id) {
        return new CityWrapper(locationService.getCity(id));
    }

    @Transactional
    public CityWrapper saveCity(Long id, String name) {
        City city = null;
        if (null != id) {
            city = locationService.getCity(id);
        } else {
            city = new City();
        }
        city.setName(name);
        return new CityWrapper(locationService.saveCity(city));
    }

    public List<City> getAllCities() {
        return locationService.getAllCities();
    }

    public List<CityWrapper> getAllCityWrappers() {
        List<CityWrapper> cities = new ArrayList<>();
        for(City city : locationService.getAllCities()) {
            cities.add(new CityWrapper(city));
        }
        return cities;
    }

    public Region getRegionById(Long id) {
        return locationService.getRegionById(id);
    }

    @Transactional(readOnly = true)
    public List<ZoneWrapper> getZones() {
        List<Zone> zones = locationService.getAllZones();
        List<ZoneWrapper> result = new ArrayList<>();
        for (Zone zone : zones) {
            result.add(new ZoneWrapper(zone));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ZoneWrapper> getZones(Long cityId) {
        List<Zone> zones = locationService.getAllZones(cityId);
        List<ZoneWrapper> result = new ArrayList<ZoneWrapper>();
        for (Zone zone : zones) {
            result.add(new ZoneWrapper(zone));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ZoneWrapper> getZones(boolean active) {
        List<Zone> zones = locationService.getAllZones();
        List<ZoneWrapper> result = new ArrayList<ZoneWrapper>();
        for (Zone zone : zones) {
            if (zone.isActive() == active) {
                result.add(new ZoneWrapper(zone));
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ZoneWrapper> getZones(Long cityId, boolean active) {
        List<Zone> zones = locationService.getAllZones(cityId);
        List<ZoneWrapper> result = new ArrayList<ZoneWrapper>();
        for (Zone zone : zones) {
            if (zone.isActive() == active) {
                result.add(new ZoneWrapper(zone));
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getAllWarehouses() {
        final List<Warehouse> warehouses = locationService.getAllWarehouses();
        List<WarehouseWrapper> result = new ArrayList<WarehouseWrapper>();
        for (Warehouse warehouse : warehouses) {
            result.add(new WarehouseWrapper(warehouse));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getAllWarehouse(Long cityId) {
        List<WarehouseWrapper> warehouses = new ArrayList<>();
        for(Warehouse warehouse : locationService.getAllWarehouses(cityId)) {
            WarehouseWrapper warehouseWrapper = new WarehouseWrapper(warehouse);
//            warehouseWrapper.setBlocks(getBlocksInWarehouse(warehouseWrapper.getId()));
            warehouses.add(warehouseWrapper);
        }
        return warehouses;
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getAllWarehouse(Long cityId, AdminUser adminUser) {
        List<WarehouseWrapper> warehouses = new ArrayList<>();
        List<Long> warehouseIds = new ArrayList<>();
        for (Warehouse warehouse : adminUserService.getAdminUserAllWarehouses(adminUser)) {
            warehouseIds.add(warehouse.getId());
        }
        for(Warehouse warehouse : locationService.getAllWarehouses(cityId)) {
            if (warehouseIds.contains(warehouse.getId()) && warehouse.isActive()) {
                warehouses.add(new WarehouseWrapper(warehouse));
            }
        }
        return warehouses;
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getCityWarehouseByWarehouseId(Long warehouseId) {
        List<WarehouseWrapper> warehouses = new ArrayList<>();
        Long cityId = locationService.getWarehouse(warehouseId).getCity().getId();
        for(Warehouse warehouse : locationService.getAllWarehouses(cityId)) {
            WarehouseWrapper warehouseWrapper = new WarehouseWrapper(warehouse);
            warehouses.add(warehouseWrapper);
        }
        return warehouses;
    }

    public List<BlockWrapper> getBlockByCityId(Long cityId, AdminUser adminUser, final Boolean status) {
        List<BlockWrapper> blocks = new ArrayList<>();
        for (Block block : getCityBlocks(cityId, adminUser)) {
            blocks.add(new BlockWrapper(block));
        }
        if (null != status) {
            return new ArrayList<>(Collections2.filter(blocks, new Predicate<BlockWrapper>() {
                @Override
                public boolean apply(BlockWrapper input) {
                    return status.equals(input.isActive());
                }
            }));
        } else {
            return blocks;
        }
    }


    @Transactional(readOnly = true)
    public List<BlockWrapper> getBlocksInWarehouse(Long warehouseId) {
        List<BlockWrapper> blocks = new ArrayList<>();
        for (Block block : locationService.getBlockByWarehouseId(warehouseId)) {
            blocks.add(new BlockWrapper(block));
        }
        return blocks;
    }

    @Transactional(readOnly = true)
    public List<ZoneWrapper> getZonesInWarehouse(Long warehouseId) {
        List<Zone> zones = locationService.getAllZones();
        List<ZoneWrapper> result = new ArrayList<ZoneWrapper>();
        for (Zone zone : zones) {
            if (zone.getWarehouse() != null && zone.getWarehouse().getId().equals(warehouseId)) {
                result.add(new ZoneWrapper(zone));
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public BlockQueryResponse getBlocks(BlockQueryRequest request, AdminUser adminUser) {
       Page<Block> blocks =  locationService.findAllBlocks(request, adminUser);

        List<BlockWrapper> simpleBlockWrappers = new ArrayList<>();
        for (Block block : blocks) {
            simpleBlockWrappers.add(new BlockWrapper(block));
        }

        BlockQueryResponse result = new BlockQueryResponse();
        result.setBlocks(simpleBlockWrappers);
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(blocks.getTotalElements());

        return result;

    }
    @Transactional(readOnly = true)
    public BlockWrapper getBlockById(Long id) {

        return new BlockWrapper(locationService.getBlockById(id));
    }

    @Transactional(readOnly = true)
    public WarehouseWrapper findWarehouseById(Long warehouseId) {
        return new WarehouseWrapper(locationService.getWarehouse(warehouseId));
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getWarehouseByDepotId(Long depotId, AdminUser adminUser) {
        List<WarehouseWrapper> warehouses = new ArrayList<>();
        List<Long> warehouseIds = new ArrayList<>();
        for (Warehouse warehouse : adminUserService.getAdminUserAllWarehouses(adminUser)) {
            warehouseIds.add(warehouse.getId());
        }
        for(Warehouse warehouse : locationService.findActiveWarehouseByDepotId(depotId)) {
            if (warehouseIds.contains(warehouse.getId())) {
                warehouses.add(new WarehouseWrapper(warehouse));
            }
        }
        return warehouses;
    }

    @Transactional(readOnly = true)
    public List<WarehouseWrapper> getWarehouseByCityId(Long cityId, AdminUser adminUser) {
        List<WarehouseWrapper> warehouses = new ArrayList<>();
        List<Long> warehouseIds = new ArrayList<>();
        for (Warehouse warehouse : adminUserService.getAdminUserAllWarehouses(adminUser)) {
            warehouseIds.add(warehouse.getId());
        }
        for(Warehouse warehouse : locationService.findActiveWarehouseByCityId(cityId)) {
            if (warehouseIds.contains(warehouse.getId())) {
                warehouses.add(new WarehouseWrapper(warehouse));
            }
        }
        return warehouses;
    }

    @Transactional
    public WarehouseWrapper updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = locationService.getWarehouse(id);
        warehouse.setName(request.getName());
        warehouse.setActive(request.isActive());
        warehouse.setDepot(depotService.findOne(request.getDepotId()));
        return new WarehouseWrapper(locationService.updateWarehouse(warehouse));
    }

    @Transactional
    public WarehouseWrapper saveWarehouse (Warehouse warehouse) {
        return new WarehouseWrapper(locationService.saveWarehouse(warehouse));
    }

    @Transactional
    public WarehouseWrapper createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCity(locationService.getCity(request.getCityId()));
        warehouse.setName(request.getName());
        warehouse.setDepot(depotService.findOne(request.getDepotId()));
        if (null == getDefaultWarehouse(warehouse.getCity().getId())) {
            warehouse.setDefault(true);
        }
        return saveWarehouse(warehouse);
    }

    @Transactional
    public Warehouse getDefaultWarehouse(Long cityId) {
        return locationService.getDefaultWarehouse(cityId);
    }

    public BlockWrapper updateBlock(Long id,UpdateBlockQueryRequest request,AdminUser adminuser) {
        Block block = locationService.getBlockById(id);
        if(request.getActive() != null) {
            block.setActive(request.getActive());
        }
        if(request.getWarehouseId() != null){
            block.setWarehouse(locationService.getWarehouse(request.getWarehouseId()));
        }

        if(request.getCityId() != null){
            block.setCity(locationService.getCity(request.getCityId()));
        }

        if(StringUtils.isNoneBlank(request.getBlockName())){
            block.setName(request.getBlockName());
        }

        if(StringUtils.isNotBlank(request.getPointStr())){
            block.setPointStr(request.getPointStr());
        }
        return new BlockWrapper(locationService.updateBlock(block));

    }
    @Transactional
    public BlockWrapper addBlock(AddBlockRequest request, AdminUser adminUser) {
        Block block = new Block();
        block.setActive(request.getActive());
        block.setWarehouse(locationService.getWarehouse(request.getWarehouseId()));
        block.setCity(locationService.getCity(request.getCityId()));
        block.setName(request.getBlockName());
        block.setPointStr(request.getPointStr());
        return new BlockWrapper(locationService.saveBlock(block));

    }
    @Transactional
    public List<Block> getCityBlocks(Long cityId, AdminUser adminUser){
        BlockQueryRequest request = new BlockQueryRequest();
        request.setPageSize(Integer.MAX_VALUE);
        request.setCityId(cityId);
        Page<Block> blocks = locationService.findAllBlocks(request,null);
        return blocks.getContent();
    }
    @Transactional
    public List<TreeJsonHasChild> getCityBlocksTree(AdminUser adminUser) {
//        ToDo 这里暂时去掉权限，城市数据不稳定
//        Set<City> cities = adminUser.getCities();
        List<City> cities = locationService.getAllCities();
        Iterator<City> it = cities.iterator();
        List<TreeJsonHasChild> data = new ArrayList<>();
        while(it.hasNext()){
            TreeJsonHasChild treeJsonHasChild = new TreeJsonHasChild();
            data.add(treeJsonHasChild);
            City city = it.next();
            treeJsonHasChild.setId(String.valueOf(StringUtils.join("c", city.getId())));
            treeJsonHasChild.setText(city.getName());
            List<TreeJsonHasChild> warehouseChilds = new ArrayList<>();
            treeJsonHasChild.setChildren(warehouseChilds);
            for (Warehouse warehouse : locationService.getAllWarehouses(city.getId())) {
                if (warehouse.isActive()) {
                    TreeJsonHasChild warehouseChild = new TreeJsonHasChild();
                    warehouseChilds.add(warehouseChild);
                    warehouseChild.setId(StringUtils.join("w", warehouse.getId()));
                    warehouseChild.setText(warehouse.getName());
                    List<TreeJsonHasChild> blockChilds = new ArrayList<>();
                    warehouseChild.setChildren(blockChilds);
                    for (Block block : locationService.getBlockByWarehouseId(warehouse.getId())) {
                        if (block.isActive()) {
                        TreeJsonHasChild blockChild = new TreeJsonHasChild();
                        blockChilds.add(blockChild);
                        blockChild.setId(StringUtils.join("b", block.getId()));
                        blockChild.setText(block.getName());
                        }
                    }
                }
            }
        }
        return data;
    }
    @Transactional
    public List<BlockWrapper> getBlocksByOrganizationId(Long organizationId) {
        List<BlockWrapper> blocks = new ArrayList<>();
        for(Block block:organizationService.findById(organizationId).getBlocks()){
            blocks.add(new BlockWrapper(block));
        }
        return  blocks;
    }

    @Transactional
    public List<WarehouseWrapper> updateWarehouseDefault(Long id) {
        Warehouse warehouse = locationService.getWarehouse(id);
        if(warehouse != null){
            Warehouse defaultWarehouse = getDefaultWarehouse(warehouse.getCity().getId());
            if(defaultWarehouse != null) {
                defaultWarehouse.setDefault(false);
                locationService.saveWarehouse(defaultWarehouse);
            }
            warehouse.setDefault(true);
        }
        locationService.saveWarehouse(warehouse);
        return getAllWarehouses();
    }

    @Transactional
    public List<SimpleBlockWrapper> getSimpleBlockByCityId(Long cityId, AdminUser adminUser, final Boolean status) {
        List<SimpleBlockWrapper> blocks = new ArrayList<>();
        for (Block block : getCityBlocks(cityId, adminUser)) {
            blocks.add(new SimpleBlockWrapper(block));
        }
        if (status != null) {
            return new ArrayList<>(Collections2.filter(blocks, new Predicate<SimpleBlockWrapper>() {
                @Override
                public boolean apply(SimpleBlockWrapper input) {
                    return input.isActive() == status;
                }
            }));
        }
        return blocks;
    }

    @Transactional
    public List<WarehouseWrapper> findActiveWarehouseByDepotId(Long id) {
        List<WarehouseWrapper> list=new ArrayList<>();
        for (Warehouse warehouse:locationService.findActiveWarehouseByDepotId(id)){
            list.add(new WarehouseWrapper(warehouse));
        }
        return list;
    }

    @Transactional
    public List<BlockWrapper> getActiveBlockByWarehouseId(Long id) {
        List<BlockWrapper> list=new ArrayList<>();
        for(Block block:locationService.findActiveBlockByWarehouseId(id)){
            list.add(new BlockWrapper(block));
        }
        return list;
    }

    @Transactional
    public List<BlockWrapper> getBlockByWarehouseId(Long warehouseId, AdminUser adminUser) {
        List<BlockWrapper> list=new ArrayList<>();
        List<Long> blockIds = new ArrayList<>();
        for (Block block : adminUserService.getAdminUserAllBlocks(adminUser)) {
            blockIds.add(block.getId());
        }
        for(Block block:locationService.findActiveBlockByWarehouseId(warehouseId)){
            if (blockIds.contains(block.getId())) {
                list.add(new BlockWrapper(block));
            }
        }
        return list;
    }
}
