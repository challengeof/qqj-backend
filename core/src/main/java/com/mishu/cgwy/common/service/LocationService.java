package com.mishu.cgwy.common.service;

import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.repository.*;
import com.mishu.cgwy.error.BlockAlreadyExistsException;
import com.mishu.cgwy.error.CityAlreadyExistsException;
import com.mishu.cgwy.error.WarehouseAlreadyExistsException;
import com.mishu.cgwy.organization.controller.BlockQueryRequest;
import org.apache.commons.lang.StringUtils;
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
import java.util.List;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 2:33 PM
 */
@Service
@Transactional
public class LocationService {
    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private BlockRepository blockRepository;

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public City getCity(Long cityId) {
        return cityRepository.getOne(cityId);
    }

    public City saveCity(City city) {
        City tempCity = findCityByName(city.getName());
        if(tempCity != null && !city.getId().equals(tempCity.getId())){
            throw new CityAlreadyExistsException();
        }
        return cityRepository.save(city);
    }

    public void deleteCity(Long cityId) {
        cityRepository.delete(cityId);
    }

    public Region saveRegion(Region region) {
        return regionRepository.save(region);
    }

    public Zone saveZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    public void deleteRegion(Long regionId) {
        regionRepository.delete(regionId);
    }

    public Warehouse getDefaultWarehouse(Long cityId) {
        List<Warehouse> list = warehouseRepository.findByCityIdAndIsDefault(cityId, true);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public Warehouse saveWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(Warehouse warehouse){
        Warehouse warehouseCity = findWarehouseByNameAndCityId(warehouse.getName(),warehouse.getCity().getId());
        if(warehouseCity != null && !warehouse.getId().equals(warehouseCity.getId())){
            throw new WarehouseAlreadyExistsException();
        }
        return warehouseRepository.save(warehouse);
    }

    public void deleteWarehouse(Long warehouseId) {
        warehouseRepository.delete(warehouseId);
    }

    public Zone getZone(Long zoneId) {
        return zoneRepository.getOne(zoneId);
    }

    public Warehouse getWarehouse(Long warehouseId) {
        return warehouseRepository.getOne(warehouseId);
    }

    public List<Zone> getZones(Long regionId) {
        List<Zone> zones = zoneRepository.findByRegionId(regionId);
        return zones;
    }

    public List<Region> getRegions(Long cityId) {
        return regionRepository.findByCityId(cityId);
    }

    public Region getRegionById(Long id) {
        return regionRepository.getOne(id);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public List<Zone> getAllZones(Long cityId){
        return zoneRepository.findByCityId(cityId);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public List<Warehouse> getAllWarehouses(final Long cityId) {
        return warehouseRepository.findAll(new Specification<Warehouse>() {
            @Override
            public Predicate toPredicate(Root<Warehouse> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (cityId != null) {
                    predicates.add(cb.equal(root.get(Warehouse_.city).get(City_.id), cityId));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    public List<Block> getBlockByWarehouseId(Long warehouseId) {
        return blockRepository.findByWarehouseId(warehouseId);
    }

    public Block getBlockById(Long blockId) {
        return blockRepository.getOne(blockId);
    }

    public Block saveBlock(Block block) {
        return blockRepository.save(block);
    }

    public Block updateBlock(Block block){
        Block blockCity = findBlockByNameAndCityId(block.getName(),block.getCity().getId());
        if(blockCity != null && !block.getId().equals(blockCity.getId())){
            throw new BlockAlreadyExistsException();
        }
        return blockRepository.save(block);
    }
    public Page<Block> findAllBlocks(final BlockQueryRequest request, final AdminUser adminUser) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.ASC, "id"));

        return blockRepository.findAll(new Specification<Block>() {
            @Override
            public Predicate toPredicate(Root<Block> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getBlockId() != null) {
                    predicates.add(cb.equal(root.get(Block_.id), request.getBlockId()));
                }
                if (StringUtils.isNotBlank(request.getBlockName())) {
                    predicates.add(cb.like(root.get(Block_.name), request.getBlockName()));
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Block_.city).get(City_.id), request.getCityId()));
                }

                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(Block_.warehouse), request.getWarehouseId()));
                }
                if (request.getStatus() != null) {
                    Integer temp = request.getStatus();
                    if (temp != 0) {
                        boolean active = request.getStatus() == 1 ? true : false;
                        predicates.add(cb.equal(root.get(Block_.active), active));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Transactional
    public City findCityByName(String name){
        List<City> cities = cityRepository.findByName(name);
        if(cities.isEmpty()){
            return null;
        }
        return cities.get(0);
    }


    @Transactional
    public Warehouse findWarehouseByNameAndCityId(String name,Long cityId){
        List<Warehouse> warehouses = warehouseRepository.findByNameAndCityId(name, cityId);
        if(warehouses.isEmpty()){
            return null;
        }
        return warehouses.get(0);
    }

    @Transactional
    public List<Warehouse> findWarehouseByDepotId(Long depotId){
        return warehouseRepository.findByDepotId(depotId);
    }

    @Transactional
    public List<Warehouse> findActiveWarehouseByDepotId(Long depotId){
        return warehouseRepository.findByDepotIdAndActive(depotId, true);
    }

    @Transactional
    public List<Warehouse> findActiveWarehouseByCityId(Long cityId){
        return warehouseRepository.findByCityIdAndActive(cityId, true);
    }

    @Transactional
    public List<Block> findActiveBlockByWarehouseId(Long warehouseId){
        return blockRepository.findByWarehouseIdAndActive(warehouseId,true);
    }

    @Transactional
    public Block findBlockByNameAndCityId(String name,Long cityId){
        List<Block> blocks = blockRepository.findByNameAndCityId(name,cityId);
        if(blocks.isEmpty()){
            return null;
        }
        return  blocks.get(0);
    }

    //user for etl
    @Transactional
    public List<Block> getBlocks(final Long cityId) {
        List<Block> blocks = blockRepository.findAll();
        return new ArrayList<>(Collections2.filter(blocks, new com.google.common.base.Predicate<Block>() {
            @Override
            public boolean apply(Block input) {
                return input.getCity().getId().equals(cityId);
            }
        }));
    }
}
