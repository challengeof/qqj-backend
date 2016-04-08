package com.mishu.cgwy.organization.service;

import com.google.common.base.*;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.organization.OrganizationRepository;
import com.mishu.cgwy.organization.controller.OrganizationQueryRequest;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.domain.Organization_;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by xingdong on 15/7/1.
 */
@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private LocationService locationService;

    public Organization findById(Long id) {
        return organizationRepository.getOne(id);
    }

    public Organization saveOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }


    public Page<Organization> getOrganizations(final OrganizationQueryRequest request, final AdminUser adminUser) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, "id"));


        return organizationRepository.findAll(new Specification<Organization>() {
            @Override
            public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (StringUtils.isNotBlank(request.getName())) {
                    predicates.add(cb.like(root.get(Organization_.name), "%" + request.getName() + "%"));
                }

                if (request.getCreateDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Organization_.createDate), DateUtils.addHours(request
                            .getCreateDate(), -1)));
                }

                if (request.getEnable() != null) {
                    predicates.add(cb.equal(root.get(Organization_.enabled), request.getEnable()));
                }

                if (request.getCityId() != null) {
                    SetJoin<Organization, City> citySetJoin = root.join(Organization_.cities, JoinType.LEFT);
                    SetJoin<Organization, Warehouse> warehouseSetJoin = root.join(Organization_.warehouses, JoinType.LEFT);
                    SetJoin<Organization, Block> blockSetJoin = root.join(Organization_.blocks, JoinType.LEFT);
                    List<Predicate> cityPredicate = new ArrayList<>();
                    cityPredicate.add(cb.equal(citySetJoin.get(City_.id), request.getCityId()));
                    cityPredicate.add(cb.equal(warehouseSetJoin.get(Warehouse_.city).get(City_.id), request.getCityId()));
                    cityPredicate.add(cb.equal(blockSetJoin.get(Block_.city).get(City_.id), request.getCityId()));
                    predicates.add(cb.or(cityPredicate.toArray(new Predicate[cityPredicate.size()])));
                }

                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        predicates.add(cb.equal(root.get(Organization_.id), adminUser.getOrganizations().iterator().next().getId()));
                    }
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    public List<Organization> getOrganizationsByCityId(final Long cityId, final AdminUser adminUser) {
        return new ArrayList<>(Collections2.filter(organizationRepository.findAll(), new com.google.common.base.Predicate<Organization>() {
            @Override
            public boolean apply(Organization input) {
                for (City city : getOrganizationAllCities(input)) {
                    if (city.getId().equals(cityId)) {
                        return true;
                    }
                }
                return false;
            }
        }));
    }


    public Set<City> getOrganizationAllCities(Organization organization) {
        Set<City> cities = new HashSet<>();
        cities.addAll(organization.getCities());
        for (Warehouse warehouse : organization.getWarehouses()) {
            cities.add(warehouse.getCity());
        }
        for (Block block : organization.getBlocks()) {
            cities.add(block.getCity());
        }
        return cities;
    }


    public Set<Long> getOrganizationAllCityIds(Organization organization) {
        Set<Long> cityIds = new HashSet<>();
        for (City city : getOrganizationAllCities(organization)) {
            cityIds.add(city.getId());
        }
        return cityIds;
    }


    public Set<Warehouse> getOrganizationAllWarehouses(Organization organization) {
        Set<Warehouse> warehouses = new HashSet<>();
        warehouses.addAll(organization.getWarehouses());
        for (City city : organization.getCities()) {
            warehouses.addAll(locationService.getAllWarehouses(city.getId()));
        }
        for (Block block : organization.getBlocks()) {
            warehouses.add(block.getWarehouse());
        }

        return new HashSet<Warehouse>(Collections2.filter(warehouses, new com.google.common.base.Predicate<Warehouse>() {
            @Override
            public boolean apply(Warehouse input) {
                if (input.isActive()) {
                    return true;
                }
                return false;
            }
        }));
    }

    public Set<Long> getOrganizationAllWarehouseIds(Organization organization) {
        Set<Long> warehouseIds = new HashSet<>();
        for (Warehouse warehouse : getOrganizationAllWarehouses(organization)) {
            warehouseIds.add(warehouse.getId());
        }
        return warehouseIds;
    }

    public Set<Block> getOrganizationAllBlocks(Organization organization) {
        Set<Block> blocks = new HashSet<>();
        blocks.addAll(organization.getBlocks());
        for (City city : organization.getCities()) {
            blocks.addAll(locationService.getBlocks(city.getId()));
        }
        for (Warehouse warehouse : organization.getWarehouses()) {
            blocks.addAll(locationService.getBlockByWarehouseId(warehouse.getId()));
        }
        return new HashSet<>(Collections2.filter(blocks, new com.google.common.base.Predicate<Block>() {
            @Override
            public boolean apply(Block input) {
                if (input.isActive()) {
                    return true;
                }
                return false;
            }
        }));
    }

    public Set<Long> getOrganizationAllBlockIds(Organization organization) {
        return new HashSet<>(Collections2.transform(getOrganizationAllBlocks(organization), new Function<Block, Long>() {
            @Override
            public Long apply(Block input) {
                return input.getId();
            }
        }));
    }

    public Organization getDefaultOrganization() {
        List<Organization> organizations = organizationRepository.findAll(new Specification<Organization>() {
            @Override
            public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.isTrue(root.get(Organization_.selfSupport));
            }
        });
        if (organizations.isEmpty()) {
            return null;
        } else {
            return organizations.get(0);
        }
    }

}
