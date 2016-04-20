package com.qqj.org.facade;

import com.qqj.org.controller.CustomerListRequest;
import com.qqj.org.controller.CustomerRequest;
import com.qqj.org.controller.TeamListRequest;
import com.qqj.org.controller.TeamRequest;
import com.qqj.org.service.CustomerService;
import com.qqj.org.service.TeamService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void addCustomer(CustomerRequest request) {

    }

    public List<TeamWrapper> getAllTeams() {
        return teamService.getAllTeams();
    }
}
