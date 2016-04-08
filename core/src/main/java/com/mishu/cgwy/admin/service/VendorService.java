package com.mishu.cgwy.admin.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.VendorQueryRequest;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.error.VendorAlreadyExistsException;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.inventory.repository.VendorRepository;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.stock.domain.Depot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by bowen on 2015/4/10.
 */
@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private OrganizationService organizationService;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Vendor createVendor(Vendor vendor){

//        if (findByName(vendor.getName()) !=null){
        if (findByNameAndOrganizationId(vendor.getName(), vendor.getOrganization().getId(), vendor.getCity().getId()) !=null){
            throw new VendorAlreadyExistsException();
        }
        return vendorRepository.save(vendor);
    }

    @Transactional
    public Vendor findByName(String name){
        final List<Vendor> list = vendorRepository.findByName(name);

        if (list.isEmpty()){
            return null;
        }else{
            return list.get(0);
        }
    }

    @Transactional(readOnly = true)
    public Vendor findByNameAndOrganizationId(final String name, final Long organizationId, final Long cityId) {
        final List<Vendor> list = vendorRepository.findAll(new Specification<Vendor>() {
            @Override
            public Predicate toPredicate(Root<Vendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(Vendor_.organization).get(Organization_.id), organizationId));
                predicates.add(cb.equal(root.get(Vendor_.city).get(City_.id), cityId));
                predicates.add(cb.equal(root.get(Vendor_.name), name));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        if (list.isEmpty()){
            return null;
        }else{
            return list.get(0);
        }
    }

    @Transactional
    public Vendor getVendorById(Long id){
        return vendorRepository.getOne(id);
    }

    @Transactional
    public Vendor updateVendor(Vendor vendor){

        final Vendor others = findByName(vendor.getName());

        if (others !=null && !others.getId().equals(vendor.getId())){
            throw new VendorAlreadyExistsException();
        }
        return vendorRepository.save(vendor);
    }

    @Transactional(readOnly = true)
    public Page<Vendor> getVendors(final VendorQueryRequest request, final AdminUser adminUser){
        return vendorRepository.findAll(new Specification<Vendor>() {
            @Override
            public Predicate toPredicate(Root<Vendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }

                    Set<Long> cityIds = new HashSet<>();

                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        cityIds.add(warehouse.getCity().getId());
                    }
                    for (Block block : adminUser.getBlocks()) {
                        cityIds.add(block.getCity().getId());
                    }
                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        cityIds.add(depot.getCity().getId());
                    }

                    List<Predicate> blockCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        blockCondition.add(root.get(Vendor_.city).get(City_.id).in(cityIds));
                    }

                    if (!blockCondition.isEmpty()) {
                        predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Vendor_.city).get(City_.id), request.getCityId()));
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Vendor_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(Vendor_.id), request.getVendorId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, "id")));
    }

    public Vendor findOne(Long vendorId) {
        return vendorRepository.findOne(vendorId);
    }

    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Transactional(readOnly = true)
    public Vendor getDefaultVendor(final Long organizationId, final Long cityId) {
        List<Vendor> vendors = vendorRepository.findAll(new Specification<Vendor>() {
            @Override
            public Predicate toPredicate(Root<Vendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(Vendor_.organization).get(Organization_.id), organizationId));
                predicates.add(cb.equal(root.get(Vendor_.city).get(City_.id), cityId));
                predicates.add(cb.isTrue(root.get(Vendor_.defaultVendor)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        if (vendors.isEmpty()) {
            return null;
        } else {
            return vendors.get(0);
        }
    }

    @Transactional(readOnly = true)
    public Vendor getDefaultVendor(final Long cityId) {
        final Organization organization = organizationService.getDefaultOrganization();
        List<Vendor> vendors = vendorRepository.findAll(new Specification<Vendor>() {
            @Override
            public Predicate toPredicate(Root<Vendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(Vendor_.organization).get(Organization_.id), organization.getId()));
                predicates.add(cb.equal(root.get(Vendor_.city).get(City_.id), cityId));
                predicates.add(cb.isTrue(root.get(Vendor_.defaultVendor)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        if (vendors.isEmpty()) {
            return null;
        } else {
            return vendors.get(0);
        }
    }

    @Transactional(readOnly = true)
    public Vendor findVendorByUsername(String username) {
        final List<Vendor> list = vendorRepository.findByUsername(username);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional
    public void updatePassword(Vendor vendor, String password) {
        vendor.setPassword(getEncodedPassword(vendor.getUsername(), password));
        vendorRepository.save(vendor);
    }

    public String getEncodedPassword(String username, String password) {
        return passwordEncoder.encode(getReformedPassword(username, password));
    }

    public String getReformedPassword(String username, String password) {
        return username + password + "mirror";
    }

    public static final String DEFAULT_VENDOR_PASSWORD = "123456";

    public void pwd() {
        List<Vendor> vendors = vendorRepository.findAll();
        for (Vendor vendor : vendors) {
            if (vendor.getUsername() == null && vendor.getPassword() == null) {
                String username = String.valueOf(vendor.getId());
                vendor.setUsername(username);
                vendor.setPassword(getEncodedPassword(username, DEFAULT_VENDOR_PASSWORD));
                vendorRepository.save(vendor);
            }
        }
    }
}
