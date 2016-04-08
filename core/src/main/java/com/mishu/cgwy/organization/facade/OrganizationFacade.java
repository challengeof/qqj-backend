package com.mishu.cgwy.organization.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.dto.AdminUserQueryResponse;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.organization.controller.OrganizationQueryRequest;
import com.mishu.cgwy.organization.controller.OrganizationQueryResponse;
import com.mishu.cgwy.organization.controller.OrganizationRequest;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.stock.service.DepotService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by wangwei on 15/7/2.
 */
@Service
public class OrganizationFacade {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DepotService depotService;

    @Transactional(readOnly = true)
    public OrganizationVo getOrganizationById(Long id) {
        Organization organization = organizationService.findById(id);
        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(organization.getId());
        organizationVo.setName(organization.getName());
        organizationVo.setCreateDate(organization.getCreateDate());
        organizationVo.setEnabled(organization.isEnabled());
        organizationVo.setTelephone(organization.getTelephone());
        organizationVo.setCityIds(getCityIds(organization));
        organizationVo.setWarehouseIds(getWarehouseIds(organization));
        organizationVo.setBlockIds(getBlockIds(organization));
        return organizationVo;
    }

    private String[] getCityIds(Organization organization) {
        List<String> list = new ArrayList<>();
        for(City city : organization.getCities()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("c", city.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getWarehouseIds(Organization organization) {
        List<String> list = new ArrayList<>();
        for(Warehouse warehouse : organization.getWarehouses()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("w", warehouse.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getBlockIds(Organization organization) {
        List<String> list = new ArrayList<>();
        for (Block block : organization.getBlocks()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("b", block.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    public Organization createOrganization(OrganizationRequest request) {
        Organization organization = new Organization();

        copyAttributes(organization, request);

        organization.setCreateDate(new Date());
        return organizationService.saveOrganization(organization);
    }

    @Transactional
    public Organization updateOrganization(Long organizationId, OrganizationRequest request) {
        Organization organization = organizationService.findById(organizationId);

        copyAttributes(organization, request);

        return organizationService.saveOrganization(organization);
    }

    public void copyAttributes(Organization organization, OrganizationRequest request) {

        organization.setName(request.getName());
        organization.setTelephone(request.getTelephone());
        organization.setEnabled(request.isEnable());

        if (!request.getCityWarehouseBlockIds().isEmpty()) {
            List<String> cityWarehouseBlockIds = request.getCityWarehouseBlockIds();

            Collection<String> cityIds = Collections2.filter(cityWarehouseBlockIds, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    if (StringUtils.startsWith(input, "c")) {
                        return true;
                    }
                    return false;
                }
            });
            Set<City> cities = new HashSet<>();
            for (String cityId : cityIds) {
                cities.add(locationService.getCity(Long.valueOf(StringUtils.remove(cityId, "c"))));
            }
            organization.setCities(cities);


            Collection<String> warehouseIds = Collections2.filter(cityWarehouseBlockIds, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    if (StringUtils.startsWith(input, "w")) {
                        return true;
                    }
                    return false;
                }
            });
            Set<Warehouse> warehouses = new HashSet<>();
            for (String warehouseId : warehouseIds) {
                warehouses.add(locationService.getWarehouse(Long.valueOf(StringUtils.remove(warehouseId, "w"))));
            }
            organization.setWarehouses(warehouses);


            Collection<String> blockIds = Collections2.filter(cityWarehouseBlockIds, new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    if (StringUtils.startsWith(input, "b")) {
                        return true;
                    }
                    return false;
                }
            });
            Set<Block> blocks = new HashSet<>();
            for (String blockId : blockIds) {
                blocks.add(locationService.getBlockById(Long.valueOf(StringUtils.remove(blockId, "b"))));
            }
            organization.setBlocks(blocks);
        }
    }


    @Transactional(readOnly = true)
    public OrganizationQueryResponse getOrganizations(OrganizationQueryRequest request, AdminUser adminUser){
        Page<Organization> page = organizationService.getOrganizations(request, adminUser);

        List<OrganizationVo> organizations = new ArrayList<>();
        for(Organization organization : page) {
            OrganizationVo organizationVo = new OrganizationVo();
            organizationVo.setId(organization.getId());
            organizationVo.setName(organization.getName());
            organizationVo.setCreateDate(organization.getCreateDate());
            organizationVo.setEnabled(organization.isEnabled());
            organizationVo.setTelephone(organization.getTelephone());
            organizations.add(organizationVo);
        }

        OrganizationQueryResponse response = new OrganizationQueryResponse();

        response.setOrganizations(organizations);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }
    @Transactional(readOnly = true)
    public List<OrganizationVo> getOrganizationsByCityId(Long cityId, final AdminUser adminUser) {
        List<OrganizationVo> wrappers = new ArrayList<>();
        for(Organization organization : organizationService.getOrganizationsByCityId(cityId,adminUser)){
            if (organization.isEnabled()) {
                OrganizationVo organizationVo = new OrganizationVo();
                organizationVo.setId(organization.getId());
                organizationVo.setName(organization.getName());
                organizationVo.setCreateDate(organization.getCreateDate());
                organizationVo.setEnabled(organization.isEnabled());
                organizationVo.setTelephone(organization.getTelephone());
                wrappers.add(organizationVo);
            }
        }
        if(!adminUser.isGlobalAdmin()) {
                wrappers = new ArrayList<>(Collections2.filter(wrappers, new Predicate<OrganizationVo>() {
                    @Override
                    public boolean apply(OrganizationVo input) {
                        return adminUser.getOrganizations().iterator().next().getId().equals(input.getId());
                    }
                }));
        }
        return wrappers;
    }

    @Transactional(readOnly = true)
    public AdminUserQueryResponse listOrganizationAdminUsers(AdminUserQueryRequest request) {
        request.setGlobal(false);
        Page<AdminUser> page = adminUserService.getAdminUser(request);

        AdminUserQueryResponse response = new AdminUserQueryResponse();

        response.setPageSize(request.getPageSize());
        response.setPage(request.getPage());
        response.setTotal(page.getTotalElements());

        for(AdminUser adminUser: page){
            AdminUserVo adminUserVo = new AdminUserVo();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setEnabled(adminUser.isEnabled());
            adminUserVo.setRealname(adminUser.getRealname());
            adminUserVo.setGlobalAdmin(adminUser.isGlobalAdmin());

            if (!adminUser.isGlobalAdmin()) {
                adminUserVo.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
            }

            for (AdminRole role : adminUser.getAdminRoles()) {
                AdminRoleVo adminRoleVo = new AdminRoleVo();
                adminRoleVo.setId(role.getId());
                adminRoleVo.setName(role.getName());
                adminRoleVo.setDisplayName(role.getDisplayName());
                adminRoleVo.setOrganizationRole(role.isOrganizationRole());
                adminUserVo.getAdminRoles().add(adminRoleVo);
            }
            response.getAdminUsers().add(adminUserVo);
        }
        return response;
    }

    private boolean isOrganizationAdmin(AdminUser input, Long organizationId) {
        if(!input.isGlobalAdmin() && !input.getOrganizations().isEmpty() &&
                input.getOrganizations().iterator().next().getId().equals(organizationId))
            return true;
        return  false;
    }




}
