package com.qqj.org.facade;

import com.qqj.org.controller.*;
import com.qqj.org.controller.legacy.pojo.RegisterTaskListRequest;
import com.qqj.org.domain.*;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.service.CustomerService;
import com.qqj.org.service.TeamService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.RegisterTaskWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.product.controller.service.ProductService;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ProductService productService;

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

        Team team = teamService.getOne(request.getTeam());

        if (team.getFounder() != null) {
            Response res = new Response();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("该团队已设置过创始人");
            return res;
        }

        if (customerService.findCustomerByUsername(request.getTelephone()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        Customer customer = new Customer();
        customer.setFounder(Boolean.TRUE);
        customer.setStatus(CustomerStatus.VALID.getValue());
        customer.setParent(null);
        customer.setLeftCode(1L);
        customer.setRightCode(2L);
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

        List<Stock> stocks = new ArrayList<>();

        for (StockInfo stockInfo : request.getStocks()) {
            Stock stock = new Stock();
            stock.setProduct(productService.get(stockInfo.getProductId()));
            stock.setQuantity(stockInfo.getQuantity());
            stock.setCustomer(customer);
            stocks.add(stock);
        }
        customer.setStocks(stocks);


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
        RegisterTask task = new RegisterTask();

        //如果是创始人注册代理，直接提交总部审批，否则由直属总代审批。
        if (CustomerLevel.get(parent.getLevel()) == CustomerLevel.LEVEL_0) {
            task.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
        } else {
            task.setStatus(CustomerAuditStatus.WAITING_DIRECT_LEADER.getValue());
        }
        task.setDirectLeader(getDirectLeader(parent));
        task.setParent(parent);
        task.setTeam(parent.getTeam());
        task.setTelephone(request.getTelephone());
        task.setUsername(request.getTelephone());
        task.setName(request.getName());
        task.setAddress(request.getAddress());
        task.setCertificateNumber(request.getCertificateNumber());
        task.setLevel(request.getLevel());
        task.setCreateTime(new Date());

        String rawPassword = task.getUsername() + CustomerService.defaultPassword;
        task.setPassword(passwordEncoder.encode(task.getUsername() + rawPassword + "mirror"));

        if (customerService.findCustomerByUsername(task.getUsername()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        List<TmpStock> tmpStocks = new ArrayList<>();

        for (StockInfo stockInfo : request.getStocks()) {
            TmpStock tmpStock = new TmpStock();
            tmpStock.setProduct(productService.get(stockInfo.getProductId()));
            tmpStock.setQuantity(stockInfo.getQuantity());
            tmpStock.setRegisterTask(task);
            tmpStocks.add(tmpStock);
        }
        task.setTmpStocks(tmpStocks);
        customerService.saveRegisterTask(task);

        return Response.successResponse;
    }

    private Customer getDirectLeader(Customer parent) {
        if (CustomerLevel.get(parent.getLevel()) == CustomerLevel.LEVEL_0) {
            return parent;
        } else {
            return getDirectLeader(parent.getParent());
        }
    }

    @Transactional
    public Response insertCustomer(RegisterTask task) {

        Customer parent = task.getParent();

        Customer customer = new Customer();
        customer.setFounder(Boolean.FALSE);
        customer.setStatus(CustomerStatus.VALID.getValue());
        customer.setParent(parent);
        customer.setTeam(parent.getTeam());
        customer.setTelephone(task.getTelephone());
        customer.setUsername(task.getTelephone());
        customer.setName(task.getName());
        customer.setAddress(task.getAddress());
        customer.setCertificateNumber(task.getCertificateNumber());

        customer.setLevel(task.getLevel());
        customer.setCreateTime(new Date());
        customer.setPassword(task.getPassword());

        List<Stock> stocks = new ArrayList<>();
        for (TmpStock tmpStock : task.getTmpStocks()) {
            Stock stock = new Stock();
            stock.setProduct(tmpStock.getProduct());
            stock.setQuantity(tmpStock.getQuantity());
            stock.setCustomer(customer);
            stocks.add(stock);
        }

        customer.setStocks(stocks);

        return customerService.insertNode(parent, customer);

    }

    public Response<RegisterTaskWrapper> getRegisterTasks(Customer currentCustomer, RegisterTaskListRequest customerListRequest) {
        return customerService.getRegisterTasks(currentCustomer, customerListRequest);
    }

    @Transactional
    public Response auditCustomer(AuditCustomerRequest request) {

        RegisterTask task = customerService.getRegisterTask(request.getTmpCustomerId());

        if (customerService.findCustomerByUsername(task.getUsername()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        task = customerService.auditCustomer(task, request);

        CustomerAuditStatus status = CustomerAuditStatus.get(task.getStatus());
        if (status == CustomerAuditStatus.PASS) {
            return insertCustomer(task);
        }

        return Response.successResponse;
    }

    public RegisterTaskWrapper getRegisterTask(Long id) {
        return customerService.getRegisterTaskWrapper(id);
    }

    public Response editCustomer(Long id, CustomerRequest request) {
        Customer customer = customerService.getCustomerById(id);
        customer.setCertificateNumber(request.getCertificateNumber());
        customer.setName(request.getName());
        customer.setTelephone(request.getTelephone());
        customer.setAddress(request.getAddress());

        customerService.saveCustomer(customer);

        return Response.successResponse;
    }

    public CustomerWrapper getCustomer(Long id) {
        return new CustomerWrapper(customerService.getCustomerById(id));
    }
}
