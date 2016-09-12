package com.qqj.org.facade;

import com.qqj.org.controller.*;
import com.qqj.org.controller.legacy.pojo.RegisterTaskListRequest;
import com.qqj.org.domain.*;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.enumeration.CustomerStatus;
import com.qqj.org.service.CustomerService;
import com.qqj.org.service.TeamService;
import com.qqj.org.service.TmpCustomerService;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.org.wrapper.TmpCustomerWrapper;
import com.qqj.product.service.ProductService;
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

    @Autowired
    private TmpCustomerService tmpCustomerService;

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

        Stock stock = new Stock();
        stock.setCustomer(customer);
        stock.setCreateTime(new Date());

        List<StockItem> stockItems = new ArrayList<StockItem>();

        for (StockInfo stockInfo : request.getStocks()) {
            StockItem stockItem = new StockItem();
            stockItem.setProduct(productService.get(stockInfo.getProductId()));
            stockItem.setQuantity(stockInfo.getQuantity());
            stockItem.setStock(stock);
            stockItems.add(stockItem);
        }

        stock.setStockItems(stockItems);

        customer.setStock(stock);

        Response res = customerService.register(customer);

        team.setFounder(customer);

        teamService.save(team);

        return res;
    }

    public List<TeamWrapper> getAllTeams() {
        return teamService.getAllTeams();
    }

    public Response register(Customer parent, CustomerRequest request) {
        TmpCustomer tmpCustomer = new TmpCustomer();

        //如果是创始人注册代理，直接提交总部审批，否则由直属总代审批。
        if (CustomerLevel.get(parent.getLevel()) == CustomerLevel.LEVEL_0) {
            tmpCustomer.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
        } else {
            tmpCustomer.setStatus(CustomerAuditStatus.WAITING_DIRECT_LEADER.getValue());
        }
        Customer directLeader = getDirectLeader(parent);
        tmpCustomer.setDirectLeader(directLeader);
        tmpCustomer.setParent(parent);
        tmpCustomer.setTeam(parent.getTeam());
        tmpCustomer.setTelephone(request.getTelephone());
        tmpCustomer.setUsername(request.getTelephone());
        tmpCustomer.setName(request.getName());
        tmpCustomer.setAddress(request.getAddress());
        tmpCustomer.setCertificateNumber(request.getCertificateNumber());
        tmpCustomer.setLevel(request.getLevel());
        tmpCustomer.setCreateTime(new Date());

        String rawPassword = tmpCustomer.getUsername() + CustomerService.defaultPassword;
        tmpCustomer.setPassword(passwordEncoder.encode(tmpCustomer.getUsername() + rawPassword + "mirror"));

        if (customerService.findCustomerByUsername(tmpCustomer.getUsername()) != null
                || tmpCustomerService.findCustomerByUsername(tmpCustomer.getUsername()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        TmpStock tmpStock = new TmpStock();
        tmpStock.setCreateTime(new Date());
        tmpStock.setTmpCustomer(tmpCustomer);

        List<TmpStockItem> tmpStockItems = new ArrayList<TmpStockItem>();

        for (StockInfo stockInfo : request.getStocks()) {
            TmpStockItem tmpStockItem = new TmpStockItem();
            tmpStockItem.setProduct(productService.get(stockInfo.getProductId()));
            tmpStockItem.setQuantity(stockInfo.getQuantity());
            tmpStockItem.setTmpStock(tmpStock);
            tmpStockItems.add(tmpStockItem);
        }

        tmpStock.setTmpStockItems(tmpStockItems);

        tmpCustomer.setTmpStock(tmpStock);
        customerService.saveTmpCustomer(tmpCustomer);

        return Response.successResponse;
    }

    public Customer getDirectLeader(Customer parent) {
        if (CustomerLevel.get(parent.getLevel()) == CustomerLevel.LEVEL_0) {
            return parent;
        } else {
            return getDirectLeader(parent.getParent());
        }
    }

    @Transactional
    public Response insertCustomer(TmpCustomer tmpCustomer) {

        Customer parent = tmpCustomer.getParent();

        Customer customer = new Customer();
        customer.setFounder(Boolean.FALSE);
        customer.setStatus(CustomerStatus.VALID.getValue());
        customer.setParent(parent);
        customer.setTeam(parent.getTeam());
        customer.setTelephone(tmpCustomer.getTelephone());
        customer.setUsername(tmpCustomer.getTelephone());
        customer.setName(tmpCustomer.getName());
        customer.setAddress(tmpCustomer.getAddress());
        customer.setCertificateNumber(tmpCustomer.getCertificateNumber());

        customer.setLevel(tmpCustomer.getLevel());
        customer.setCreateTime(new Date());
        customer.setPassword(tmpCustomer.getPassword());

        Stock stock = new Stock();
        List<StockItem> stockItems = new ArrayList<>();
        for (TmpStockItem tmpStockItem : tmpCustomer.getTmpStock().getTmpStockItems()) {
            StockItem stockItem = new StockItem();
            stockItem.setProduct(tmpStockItem.getProduct());
            stockItem.setQuantity(tmpStockItem.getQuantity());
            stockItem.setStock(stock);
            stockItems.add(stockItem);
        }

        stock.setStockItems(stockItems);

        customer.setStock(stock);

        return customerService.insertNode(parent, customer);

    }

    public Response<TmpCustomerWrapper> getTmpCustomerWrappers(Customer currentCustomer, RegisterTaskListRequest customerListRequest) {
        return customerService.getTmpCustomerWrappers(currentCustomer, customerListRequest);
    }

    @Transactional
    public Response auditCustomer(AuditCustomerRequest request) {

        TmpCustomer tmpCustomer = customerService.getTmpCustomer(request.getTmpCustomerId());

        if (customerService.findCustomerByUsername(tmpCustomer.getUsername()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        tmpCustomer = customerService.auditCustomer(tmpCustomer, request);

        CustomerAuditStatus status = CustomerAuditStatus.get(tmpCustomer.getStatus());
        if (status == CustomerAuditStatus.PASS) {
            return insertCustomer(tmpCustomer);
        }

        return Response.successResponse;
    }

    public TmpCustomerWrapper getTmpCustomerWrapper(Long id) {
        return customerService.getTmpCustomerWrapper(id);
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
