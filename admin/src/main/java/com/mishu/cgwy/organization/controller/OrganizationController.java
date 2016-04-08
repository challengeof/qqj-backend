package com.mishu.cgwy.organization.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.dto.AdminUserQueryResponse;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.BlockWrapper;
import com.mishu.cgwy.organization.constants.OrganizationStatus;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.facade.OrganizationFacade;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/7/2.
 */
@Controller
public class OrganizationController {

    @Autowired
    private OrganizationFacade organizationFacade;
    @Autowired
    private LocationFacade locationFacade;
    @Autowired
    private OrganizationService organizationService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/organization", method = RequestMethod.GET)
    @ResponseBody
    public OrganizationQueryResponse getOrganizations(OrganizationQueryRequest request, @CurrentAdminUser AdminUser adminUser){
        return organizationFacade.getOrganizations(request, adminUser);
    }

    @RequestMapping(value = "/api/organization/{id}", method = RequestMethod.GET)
    @ResponseBody
    public OrganizationVo getOrganization(@PathVariable(value = "id") Long id) {
        return organizationFacade.getOrganizationById(id);
    }

    @RequestMapping(value = "/api/organization/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateOrganization(@PathVariable(value = "id") Long id, @RequestBody OrganizationRequest request) {
        organizationFacade.updateOrganization(id, request);
    }

    @RequestMapping(value = "/api/organization", method = RequestMethod.POST)
    @ResponseBody
    public void createOrganization(@RequestBody OrganizationRequest request) {
        organizationFacade.createOrganization(request);
    }

    @RequestMapping(value = "/api/organization/status", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganizationStatus> getStatus(){
        final OrganizationStatus[] status = OrganizationStatus.values();
        final List<OrganizationStatus> list =  new ArrayList<>(Arrays.asList(status));
        return list;
    }

    @RequestMapping(value = "/api/organization/{id}/blocks", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockWrapper> getBlocksByOrganizationId(@PathVariable("id") Long organizationId,@CurrentAdminUser AdminUser adminUser){
        return locationFacade.getBlocksByOrganizationId(organizationId);
    }

    //全局管理人员查看
    @RequestMapping(value = "/api/organization/{id}/adminUsers", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserQueryResponse getAdminUsersByOrganizationId(@PathVariable("id") Long organizationId, AdminUserQueryRequest request){
        request.setOrganizationId(organizationId);
        return organizationFacade.listOrganizationAdminUsers(request);
    }
    //组织内部人员使用
    //TODO 这里组织概念弱化，暂时按照自营店走
    @RequestMapping(value = "/api/organization/adminUsers", method = RequestMethod.GET)
    @ResponseBody
    public AdminUserQueryResponse listOrganizationAdminUsers(@RequestParam(value = "role", required = false) String roleName, AdminUserQueryRequest request) {
        Organization organization = organizationService.getDefaultOrganization();
        request.setOrganizationId(organization.getId());
        request.setRoleName(roleName);
        return organizationFacade.listOrganizationAdminUsers(request);
    }




}
