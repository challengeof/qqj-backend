package com.qqj.org.facade;

import com.qqj.org.controller.CustomerListRequest;
import com.qqj.org.controller.CustomerRequest;
import com.qqj.org.controller.TeamListRequest;
import com.qqj.org.controller.TeamRequest;
import com.qqj.org.domain.Customer;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.service.CustomerService;
import com.qqj.org.service.TeamService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Response addCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setTelephone(request.getTelephone());
        customer.setUsername(request.getTelephone());
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setCertificateNumber(request.getCertificateNumber());

        customer.setLevel(request.getLevel());
        customer.setFounder(request.isTop());
        customer.setCreateTime(new Date());

        customer.setPassword(request.getTelephone() + CustomerService.defaultPassword);

        /**
         * 如果是创始人，状态直接设置为审批通过；非创始人设置为待上级审批的状态。
         * 如果是创始人，没有上级；非创始人设置上级
         */
        if (request.isTop()) {
            customer.setStatus(CustomerStatus.STATUS_2.getValue());
            customer.setParent(null);
            customer.setLeftCode(1L);
            customer.setRightCode(2L);
        } else {
            customer.setStatus(CustomerStatus.STATUS_0.getValue());
            customer.setParent(customerService.getCustomerById(request.getParent()));
        }

        customer.setTeam(teamService.getOne(request.getTeam()));

        return customerService.register(customer);
    }

    public List<TeamWrapper> getAllTeams() {
        return teamService.getAllTeams();
    }
}
