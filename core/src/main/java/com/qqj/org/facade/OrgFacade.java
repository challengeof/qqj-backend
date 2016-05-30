package com.qqj.org.facade;

import com.qqj.org.controller.*;
import com.qqj.org.controller.legacy.pojo.TmpCustomerListRequest;
import com.qqj.org.domain.Customer;
import com.qqj.org.domain.PendingApprovalCustomer;
import com.qqj.org.domain.Team;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerStage;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.service.CustomerService;
import com.qqj.org.service.TeamService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.org.wrapper.TmpCustomerWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrgFacade {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Response<TeamWrapper> getTeamList(TeamListRequest request) {
        return teamService.getTeamList(request);
    }

    @Transactional
    public void addTeam(TeamRequest request) {
        teamService.addTeam(request);
    }

    public Response<CustomerWrapper> getCustomerList(CustomerListRequest request) {
        return customerService.getCustomerList(request);
    }

    @Transactional
    public Response addFounder(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setFounder(Boolean.TRUE);
        customer.setStatus(CustomerStatus.VALID.getValue());
        customer.setParent(null);
        customer.setLeftCode(1L);
        customer.setRightCode(2L);

        Team team = teamService.getOne(request.getTeam());
        customer.setTeam(team);

        customer.setTelephone(request.getTelephone());
        customer.setUsername(request.getTelephone());
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setCertificateNumber(request.getCertificateNumber());

        customer.setLevel(request.getLevel());
        customer.setCreateTime(new Date());

        String rawPassword = customer.getUsername() + CustomerService.defaultPassword;
        customer.setPassword(passwordEncoder.encode(customer.getUsername() + rawPassword + "mirror"));

        Response res = addCustomer(customer, request);

        team.setFounder(customer);

        teamService.save(team);

        return res;
    }

    public Response addCustomer(Customer customer, CustomerRequest request) {
        return customerService.register(customer);
    }

    public List<TeamWrapper> getAllTeams() {
        return teamService.getAllTeams();
    }

    public Response register(Customer parent, CustomerRequest request) {

        PendingApprovalCustomer customer = new PendingApprovalCustomer();

        //如果是创始人注册代理，直接提交总部审批
        if (parent.isFounder()) {
            customer.setStage(CustomerStage.STAGE_3.getValue());
            customer.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
        } else {
            if (parent.getParent().isFounder()) {//如果当前代理的上级是创始人，则直接进入创始人审批阶段。
                customer.setStage(CustomerStage.STAGE_2.getValue());
                customer.setStatus(CustomerAuditStatus.WAITING_TEAM_LEADER.getValue());
            } else {//如果当前代理的上级是不是创始人，则先由上级审批。
                customer.setStage(CustomerStage.STAGE_1.getValue());
                customer.setStatus(CustomerAuditStatus.WAITING_CHIEF.getValue());
            }
        }
        customer.setParent(parent);
        customer.setTeam(parent.getTeam());
        customer.setTelephone(request.getTelephone());
        customer.setUsername(request.getTelephone());
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setCertificateNumber(request.getCertificateNumber());
        customer.setLevel(request.getLevel());
        customer.setCreateTime(new Date());

        String rawPassword = customer.getUsername() + CustomerService.defaultPassword;
        customer.setPassword(passwordEncoder.encode(customer.getUsername() + rawPassword + "mirror"));

        customerService.savePendingApprovalCustomer(customer);

        return Response.successResponse;
    }

    @Transactional
    public Response insertCustomer(PendingApprovalCustomer pendingApprovalCustomer) {

        Customer parent = pendingApprovalCustomer.getParent();

        Customer customer = new Customer();
        customer.setFounder(Boolean.FALSE);
        customer.setStatus(CustomerStatus.VALID.getValue());
        customer.setParent(parent);
        customer.setTeam(parent.getTeam());
        customer.setTelephone(pendingApprovalCustomer.getTelephone());
        customer.setUsername(pendingApprovalCustomer.getTelephone());
        customer.setName(pendingApprovalCustomer.getName());
        customer.setAddress(pendingApprovalCustomer.getAddress());
        customer.setCertificateNumber(pendingApprovalCustomer.getCertificateNumber());

        customer.setLevel(pendingApprovalCustomer.getLevel());
        customer.setCreateTime(new Date());
        customer.setPassword(pendingApprovalCustomer.getPassword());

        return customerService.insertNode(parent, customer);

    }

    public Response<TmpCustomerWrapper> getTmpCustomers(Customer currentCustomer, TmpCustomerListRequest customerListRequest) {
        return customerService.getTmpCustomers(currentCustomer, customerListRequest);
    }

    @Transactional
    public Response auditCustomer(AuditCustomerRequest request) {
        PendingApprovalCustomer pendingApprovalCustomer = customerService.auditCustomer(request);
        CustomerAuditStatus status = CustomerAuditStatus.get(pendingApprovalCustomer.getStatus());
        if (status == CustomerAuditStatus.PASS) {
            insertCustomer(pendingApprovalCustomer);
        }

        return Response.successResponse;
    }

    public TmpCustomerWrapper getTmpCustomer(Long id) {
        return customerService.getTmpCustomer(id);
    }
}
