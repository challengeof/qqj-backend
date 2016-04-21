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

    public Response addFounder(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setFounder(Boolean.TRUE);
        customer.setStatus(CustomerStatus.STATUS_2.getValue());
        customer.setParent(null);
        customer.setLeftCode(1L);
        customer.setRightCode(2L);
        customer.setTeam(teamService.getOne(request.getTeam()));

        formCustomer(customer, request);
        return addCustomer(customer, request);
    }

    private void formCustomer(Customer customer, CustomerRequest request) {
        customer.setTelephone(request.getTelephone());
        customer.setUsername(request.getTelephone());
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setCertificateNumber(request.getCertificateNumber());

        customer.setLevel(request.getLevel());
        customer.setCreateTime(new Date());
        customer.setPassword(request.getTelephone() + CustomerService.defaultPassword);

    }

    public Response addCustomer(Customer customer, CustomerRequest request) {
        return customerService.register(customer);
    }

    public List<TeamWrapper> getAllTeams() {
        return teamService.getAllTeams();
    }

    public Response register(Customer parent, CustomerRequest request) {
        Customer customer = new Customer();
        customer.setFounder(Boolean.FALSE);
        customer.setStatus(CustomerStatus.STATUS_0.getValue());
        customer.setParent(parent);
        customer.setTeam(parent.getTeam());

        formCustomer(customer, request);
        return customerService.insertNode(parent, customer);

    }
}
