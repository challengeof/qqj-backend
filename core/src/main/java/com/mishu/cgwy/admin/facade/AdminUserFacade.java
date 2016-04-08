package com.mishu.cgwy.admin.facade;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminPermission;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.dto.AdminUserQueryResponse;
import com.mishu.cgwy.admin.dto.AdminUserRequest;
import com.mishu.cgwy.admin.dto.RegisterAdminUserRequest;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminPermissionVo;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.vo.BlockVo;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.service.DepotService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 6:58 PM
 */
@Service
public class AdminUserFacade {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LocationService locationServie;

    @Autowired
    private DepotService depotService;


    @Transactional(readOnly = true)
    public UserDetails getUserDetailsByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public AdminUser getAdminUserEntityByUsername(String username) {
        return adminUserService.findAdminUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public AdminUserVo getAdminUserByUsername(String username) {
        AdminUser adminUser = adminUserService.findAdminUserByUsername(username);
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

        for (AdminRole role : adminUser.getAdminRoles()) {
            for (AdminPermission permission : role.getAdminPermissions()) {
                AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
                adminPermissionVo.setName(permission.getName());
                adminPermissionVo.setId(permission.getId());
                adminPermissionVo.setDisplayName(permission.getDisplayName());
                adminUserVo.getAdminPermissions().add(adminPermissionVo);
            }
        }

        setCities(adminUserVo, adminUser);
        setWarehouses(adminUserVo, adminUser);
        setBlocks(adminUserVo, adminUser);
        setDepotCities(adminUserVo, adminUser);
        setDepots(adminUserVo, adminUser);

        return adminUserVo;
    }

    private void setCities(AdminUserVo adminUserVo, AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for(City city : adminUser.getCities()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("c", city.getId()));
            CityVo cityVo = new CityVo();
            cityVo.setId(city.getId());
            cityVo.setName(city.getName());
            adminUserVo.getCities().add(cityVo);
        }
        adminUserVo.setCityIds(list.toArray(new String[list.size()]));
    }

    private void setWarehouses(AdminUserVo adminUserVo, AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for(Warehouse warehouse : adminUser.getWarehouses()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("w", warehouse.getId()));
            CityVo cityVo = new CityVo();
            cityVo.setId(warehouse.getCity().getId());
            cityVo.setName(warehouse.getCity().getName());
            adminUserVo.getCities().add(cityVo);
        }
        adminUserVo.setWarehouseIds(list.toArray(new String[list.size()]));
    }

    private void setBlocks(AdminUserVo adminUserVo, AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (Block block : adminUser.getBlocks()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("b", block.getId()));
            CityVo cityVo = new CityVo();
            cityVo.setId(block.getCity().getId());
            cityVo.setName(block.getCity().getName());
            adminUserVo.getCities().add(cityVo);
        }

        Collection<BlockVo> bvos = Collections2.transform(adminUser.getBlocks(), new Function<Block, BlockVo>() {
            @Override
            public BlockVo apply(Block input) {
                BlockVo bvo =new BlockVo();
                bvo.setActive(input.isActive());
                bvo.setDisplayName(input.getDisplayName());
                bvo.setId(input.getId());
                bvo.setName(input.getName());
                bvo.setCityId(input.getCity().getId());
                bvo.setPointStr(input.getPointStr());
                return bvo;
            }
        });
        adminUserVo.setBlocks(new HashSet<BlockVo>(bvos));
        adminUserVo.setBlockIds(list.toArray(new String[list.size()]));
    }

    private void setDepotCities(AdminUserVo adminUserVo, AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (City city : adminUser.getDepotCities()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("c", city.getId().toString()));
            CityVo cityVo = new CityVo();
            cityVo.setId(city.getId());
            cityVo.setName(city.getName());
            adminUserVo.getDepotCities().add(cityVo);
        }
        adminUserVo.setDepotCityIds(list.toArray(new String[list.size()]));
    }

    private void setDepots(AdminUserVo adminUserVo, AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (Depot depot : adminUser.getDepots()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("d", depot.getId().toString()));
            CityVo cityVo = new CityVo();
            cityVo.setId(depot.getCity().getId());
            cityVo.setName(depot.getCity().getName());
            adminUserVo.getDepotCities().add(cityVo);
        }
        adminUserVo.setDepotIds(list.toArray(new String[list.size()]));
    }

    @Transactional(readOnly = true)
    public List<AdminRoleVo> getAdminRoles() {
        List<AdminRoleVo> result = new ArrayList<>();
        for (AdminRole adminRole : adminUserService.getAdminRoles()) {
            AdminRoleVo adminRoleVo = new AdminRoleVo();
            adminRoleVo.setId(adminRole.getId());
            adminRoleVo.setName(adminRole.getName());
            adminRoleVo.setDisplayName(adminRole.getDisplayName());
            adminRoleVo.setOrganizationRole(adminRole.isOrganizationRole());

            for (AdminPermission adminPermission : adminRole.getAdminPermissions()) {
                AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
                adminPermissionVo.setId(adminPermission.getId());
                adminPermissionVo.setName(adminPermission.getName());
                adminPermissionVo.setDisplayName(adminPermission.getDisplayName());
                adminRoleVo.getAdminPermissions().add(adminPermissionVo);
            }

            result.add(adminRoleVo);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<AdminPermissionVo> getAdminPermissions() {
        List<AdminPermissionVo> result = new ArrayList<>();
        for (AdminPermission adminPermission : adminUserService.findAllAdminPermissions()) {
            AdminPermissionVo vo = new AdminPermissionVo();
            vo.setId(adminPermission.getId());
            vo.setDisplayName(adminPermission.getDisplayName());
            vo.setName(adminPermission.getName());
            result.add(vo);
        }
        return result;
    }

    @Transactional
    public void updateAdminRolePermissions(Long roleId, List<Long> permissionIds) {
        final AdminRole role = adminUserService.getAdminRole(roleId);

        role.getAdminPermissions().clear();

        if(null != permissionIds && !permissionIds.isEmpty()){
        	List<AdminPermission> permissions = new ArrayList<>();
        	for (Long permissionId : permissionIds) {
        		permissions.add(adminUserService.getAdminPermission(permissionId));
        	}
        	role.getAdminPermissions().addAll(permissions);
        }

        adminUserService.saveAdminRole(role);
    }

    @Transactional
    public void register(RegisterAdminUserRequest request, AdminUser operator) {
        AdminUser adminUser = new AdminUser();

        copyAttributes(request, adminUser);
        adminUser.setPassword(request.getPassword());
        PermissionCheckUtils.checkRegisterAdminUserPermission(adminUser, operator);
        adminUser = adminUserService.register(adminUser);
    }
    @Transactional
    public void update(Long id, AdminUserRequest request) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
        copyAttributes(request, adminUser);
        adminUser = adminUserService.update(adminUser);
    }

    private void copyAttributes(AdminUserRequest request, AdminUser adminUser) {
        adminUser.setUsername(StringUtils.trim(request.getUsername()));
        adminUser.setRealname(StringUtils.trim(request.getRealname()));
        adminUser.setTelephone(StringUtils.trim(request.getTelephone()));
        adminUser.setEnabled(request.isEnable());

        Set<AdminRole> roles = new HashSet<AdminRole>();
        for (Long roleId : request.getAdminRoleIds()) {
            roles.add(adminUserService.getAdminRole(roleId));
        }
        adminUser.setAdminRoles(roles);

        List<String> cityWarehouseBlockIds = request.getCityWarehouseBlockIds();

        //城市权限
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
        adminUser.setCities(cities);

        // 市场权限
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
        adminUser.setWarehouses(warehouses);

        // 区块权限
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
        adminUser.setBlocks(blocks);

        //仓库的城市权限
        Collection<String> depotCityIds = Collections2.filter(request.getDepotIds(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (StringUtils.startsWith(input, "c")) {
                    return true;
                }
                return false;
            }
        });
        Set<City> depotCities = new HashSet<>();
        for (String cityId : depotCityIds) {
            depotCities.add(locationService.getCity(Long.valueOf(StringUtils.remove(cityId, "c"))));
        }
        adminUser.setDepotCities(depotCities);


        //仓库全选
        Collection<String> depotIds = Collections2.filter(request.getDepotIds(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                if (StringUtils.startsWith(input, "d")) {
                    return true;
                }
                return false;
            }
        });
        Set<Depot> depots = new HashSet<>();
        for (String depotId : depotIds) {
            depots.add(depotService.findOne(Long.valueOf(StringUtils.remove(depotId, "d"))));
        }
        adminUser.setDepots(depots);

        if(request.isGlobalAdmin()) {

            adminUser.setGlobalAdmin(true);
        }else {
            adminUser.setGlobalAdmin(false);
            //TODO  这里暂时全部按照自营店走
            Set<Organization> organizations = new HashSet<>();
            Organization organization = organizationService.getDefaultOrganization();
            organizations.add(organization);
            adminUser.setOrganizations(organizations);

        }
    }

    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(adminUser.getUsername(),
                adminUserService.getReformedPassword(adminUser.getUsername(), oldPassword));

        Authentication auth = authenticationManager.authenticate(token);

        if (auth.isAuthenticated()) {
            adminUserService.updateAdminUserPassword(adminUser, newPassword);
        }
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        AdminUser adminUser = adminUserService.findAdminUserByUsername(username.trim());
        if (null != adminUser) {
            adminUserService.updateAdminUserPassword(adminUser, newPassword);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<AdminUser> getAdminUserList() {
        return adminUserService.getAllAdminUsers();
    }

    @Transactional(readOnly = true)
    public AdminUserQueryResponse getAdminUsers(AdminUserQueryRequest request) {

        Page<AdminUser> page = adminUserService.getAdminUser(request);
        AdminUserQueryResponse response = new AdminUserQueryResponse();
        List<AdminUserVo> adminUsers = new ArrayList<>();
        for (AdminUser adminUser : page.getContent()) {
            AdminUserVo adminUserVo = new AdminUserVo();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setEnabled(adminUser.isEnabled());
            adminUserVo.setRealname(adminUser.getRealname());
            adminUserVo.setGlobalAdmin(adminUser.isGlobalAdmin());

            if (!adminUserVo.isGlobalAdmin()) {
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
            adminUsers.add(adminUserVo);
        }
        response.setAdminUsers(adminUsers);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        return response;
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<AdminUserVo> getSimpleAdminUsers(final AdminUserQueryRequest request) {
        List<AdminUser> adminUsers = new ArrayList<>();

        if (StringUtils.isBlank(request.getRoleName())) {
            adminUsers = adminUserService.getAllAdminUsers();
        } else {
            AdminRole role = null;
            for (AdminRole r : adminUserService.getAdminRoles()) {
                if (r.getName().equals(request.getRoleName())) {
                    role = r;
                    break;
                }
            }

            if (role == null) {
                adminUsers = new ArrayList<>();
            } else {
                adminUsers = adminUserService.getAdminUsersByRole(role);
            }
        }

        if(request.getCityId() != null){
            adminUsers = new ArrayList<>(Collections2.filter(adminUsers, new Predicate<AdminUser>() {
                @Override
                public boolean apply(AdminUser input) {
                    for(City city:input.getCities()){
                        if(city.getId().equals(request.getCityId()))
                            return true;
                    }
                    return false;
                }
            }));
        }
        List<AdminUserVo> result = new ArrayList<>();
        for (AdminUser adminUser : adminUsers) {
            AdminUserVo adminUserVo = new AdminUserVo();
            adminUserVo.setId(adminUser.getId());
            adminUserVo.setUsername(adminUser.getUsername());
            adminUserVo.setTelephone(adminUser.getTelephone());
            adminUserVo.setEnabled(adminUser.isEnabled());
            adminUserVo.setRealname(adminUser.getRealname());
            adminUserVo.setGlobalAdmin(adminUser.isGlobalAdmin());

            if (!adminUserVo.isGlobalAdmin()) {
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
            result.add(adminUserVo);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public AdminUserVo getAdminUserById(Long id) {
        AdminUser adminUser = adminUserService.getAdminUser(id);
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

        adminUserVo.setCityIds(getCityIds(adminUser));
        adminUserVo.setWarehouseIds(getWarehouseIds(adminUser));
        adminUserVo.setBlockIds(getBlockIds(adminUser));
        adminUserVo.setDepotCityIds(getDepotCityIds(adminUser));
        adminUserVo.setDepotIds(getDepotIds(adminUser));

        return adminUserVo;
    }

    private String[] getCityIds(AdminUser adminUser) {

        List<String> list = new ArrayList<>();
        for(City city : adminUser.getCities()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("c", city.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getWarehouseIds(AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for(Warehouse warehouse : adminUser.getWarehouses()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("w", warehouse.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getBlockIds(AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (Block block : adminUser.getBlocks()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("b", block.getId()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getDepotCityIds(AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (City city : adminUser.getDepotCities()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("c", city.getId().toString()));
        }
        return list.toArray(new String[list.size()]);
    }

    private String[] getDepotIds(AdminUser adminUser) {
        List<String> list = new ArrayList<>();
        for (Depot depot : adminUser.getDepots()) {
            list.add(org.elasticsearch.common.lang3.StringUtils.join("d", depot.getId().toString()));
        }
        return list.toArray(new String[list.size()]);
    }

    @Transactional(readOnly = true)
    public AdminRoleVo getAdminRole(Long roleId) {
        AdminRole adminRole = adminUserService.getAdminRole(roleId);
        AdminRoleVo vo = new AdminRoleVo();
        vo.setId(adminRole.getId());
        vo.setName(adminRole.getName());
        vo.setDisplayName(adminRole.getDisplayName());
        vo.setOrganizationRole(adminRole.isOrganizationRole());

        for (AdminPermission adminPermission : adminRole.getAdminPermissions()) {
            AdminPermissionVo adminPermissionVo = new AdminPermissionVo();
            adminPermissionVo.setId(adminPermission.getId());
            adminPermissionVo.setName(adminPermission.getName());
            adminPermissionVo.setDisplayName(adminPermission.getDisplayName());
            vo.getAdminPermissions().add(adminPermissionVo);
        }

        return vo;
    }
}
