package com.mishu.cgwy.admin.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.VendorQueryRequest;
import com.mishu.cgwy.admin.dto.VendorQueryResponse;
import com.mishu.cgwy.admin.dto.VendorRequest;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 2015/4/10.
 */
@Service
public class VendorFacade {

    @Autowired(required = false)
    @Qualifier("vendorAuthenticationManager")
    private AuthenticationManager vendorAuthenticationManager;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LocationService locationService;

    @Transactional(readOnly = true)
    public VendorVo getVendorByUsername(String username) {

        Vendor vendor = vendorService.findVendorByUsername(username);

        VendorVo vendorVo = new VendorVo();
        vendorVo.setId(vendor.getId());
        vendorVo.setName(vendor.getName());

        City city = vendor.getCity();
        CityVo cityVo = new CityVo();
        cityVo.setId(city.getId());
        cityVo.setName(city.getName());
        vendorVo.setCity(cityVo);

        return vendorVo;
    }

    @Transactional
    public Vendor createVendor(VendorRequest vendorRequest, AdminUser adminUser){

        Vendor supplier = new Vendor();
        /*if (null != adminUser && !adminUser.getOrganizations().isEmpty()) {
            supplier.setOrganization(adminUser.getOrganizations().iterator().next());
        }*/
        copyAttributes(vendorRequest, supplier);

        Vendor vendor =  vendorService.createVendor(supplier);

        String username = String.valueOf(vendor.getId());
        vendor.setUsername(username);
        vendor.setPassword(vendorService.getEncodedPassword(username, VendorService.DEFAULT_VENDOR_PASSWORD));

        return vendorService.save(vendor);
    }

    @Transactional
    public void updateVendor(Long id, VendorRequest vendorRequest){

        Vendor vendor = vendorService.getVendorById(id);
        copyAttributes(vendorRequest, vendor);

        vendorService.updateVendor(vendor);
    }

    @Transactional
    public void updatePassword(Vendor vendor, String oldPassword, String newPassword) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(vendor.getUsername(), vendorService.getReformedPassword(vendor.getUsername(), oldPassword));

        Authentication auth = vendorAuthenticationManager.authenticate(token);

        if (auth.isAuthenticated()) {
            vendorService.updatePassword(vendor, newPassword);
        }
    }

    private void copyAttributes(VendorRequest vendorRequest,Vendor supplier){
        supplier.setOrganization(organizationService.getDefaultOrganization());
        supplier.setCity(vendorRequest.getCity() != null ? locationService.getCity(vendorRequest.getCity().getId()) : null);
        supplier.setName(vendorRequest.getName());
        supplier.setTelephone(vendorRequest.getTelephone());
        supplier.setContact(vendorRequest.getContact());
        supplier.setBrand(vendorRequest.getBrand());
        supplier.setAddress(vendorRequest.getAddress());
        supplier.setEmail(vendorRequest.getEmail());

        if (vendorRequest.getPaymentVendorId() != null) {
            supplier.setPaymentVendor(vendorService.findOne(vendorRequest.getPaymentVendorId()));
        } else {
            supplier.setPaymentVendor(supplier);
        }

        if (vendorRequest.isDefaultVendor()) {
            Vendor defaultVendor = vendorService.getDefaultVendor(supplier.getCity().getId());
            if (defaultVendor != null) {
                defaultVendor.setDefaultVendor(false);
                vendorService.save(defaultVendor);
            }
        }
        supplier.setDefaultVendor(vendorRequest.isDefaultVendor());
    }

    @Transactional(readOnly = true)
    public VendorQueryResponse getVendors(VendorQueryRequest request, AdminUser adminUser){
        VendorQueryResponse response = new VendorQueryResponse();
        List<VendorVo> list = new ArrayList<>();
        Page<Vendor> page = vendorService.getVendors(request, adminUser);
        for (Vendor vendor : page){
            VendorVo vendorVo = new VendorVo();
            vendorVo.setId(vendor.getId());
            vendorVo.setName(vendor.getName());
            vendorVo.setBrand(vendor.getBrand());
            vendorVo.setContact(vendor.getContact());
            vendorVo.setTelephone(vendor.getTelephone());
            vendorVo.setEmail(vendor.getEmail());
            vendorVo.setAddress(vendor.getAddress());

            CityVo city = new CityVo();
            city.setId(vendor.getCity().getId());
            city.setName(vendor.getCity().getName());
            vendorVo.setCity(city);

            list.add(vendorVo);
        }
        response.setVendors(list);
        response.setPage(request.getPage());
        response.setTotal(page.getTotalElements());
        response.setPageSize(request.getPageSize());
        return response;
    }

    @Transactional(readOnly = true)
    public VendorVo getVendorById(Long id){
        Vendor vendor = vendorService.getVendorById(id);
        VendorVo vendorVo = new VendorVo();
        vendorVo.setId(vendor.getId());
        vendorVo.setName(vendor.getName());
        vendorVo.setTelephone(vendor.getTelephone());
        vendorVo.setEmail(vendor.getEmail());
        vendorVo.setAddress(vendor.getAddress());
        vendorVo.setBrand(vendor.getBrand());
        vendorVo.setContact(vendor.getContact());

        Organization organization = vendor.getOrganization();
        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(organization.getId());
        organizationVo.setName(organization.getName());
        vendorVo.setOrganization(organizationVo);

        City city = vendor.getCity();
        CityVo cityVo = new CityVo();
        cityVo.setId(city.getId());
        cityVo.setName(city.getName());
        vendorVo.setCity(cityVo);

        vendorVo.setPaymentVendorId(vendor.getPaymentVendor().getId());
        vendorVo.setDefaultVendor(vendor.isDefaultVendor());

        return vendorVo;
    }

    public void pwd() {
        vendorService.pwd();
    }

    public void updatePassword(Long vendorId, String password) {
        Vendor vendor = vendorService.getVendorById(vendorId);
        vendorService.updatePassword(vendor, password);
    }
}
