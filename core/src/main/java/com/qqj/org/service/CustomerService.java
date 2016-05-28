package com.qqj.org.service;

import com.qqj.org.controller.AuditCustomerRequest;
import com.qqj.org.controller.CustomerListRequest;
import com.qqj.org.controller.legacy.pojo.TmpCustomerListRequest;
import com.qqj.org.domain.*;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerStage;
import com.qqj.org.repository.CustomerRepository;
import com.qqj.org.repository.PendingApprovalCustomerRepository;
import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TmpCustomerWrapper;
import com.qqj.response.Response;
import com.qqj.response.query.QueryResponse;
import com.qqj.utils.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    public static String defaultPassword = "123456";

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PendingApprovalCustomerRepository pendingApprovalCustomerRepository;

    @Autowired
    private EntityManager entityManager;

    public Response register(Customer customer) {
        if (findCustomerByUsername(customer.getUsername()) != null) {
            Response res = new Response<>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("用户已存在");
            return res;
        }

        customerRepository.save(customer);

        return Response.successResponse;
    }

    public Customer update(Customer customer) {
        assert customer.getId() != null;
        return customerRepository.save(customer);
    }

    public Customer updateCustomerPassword(Customer customer, String password) {
        customer.setPassword(passwordEncoder.encode(customer.getUsername() + password + "mirror"));
        return customerRepository.save(customer);
    }

    public Customer findCustomerByUsername(String username) {
        final List<Customer> list = customerRepository.findByUsername(username);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.getOne(id);
    }

    public Response<CustomerWrapper> getCustomerList(final CustomerListRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<Customer> page = customerRepository.findAll(new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(Customer_.name), String.format("%%%s%%", request.getName())));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Customer_.status), request.getStatus()));
                }

                if (request.getCertificateNumber() != null) {
                    predicates.add(cb.equal(root.get(Customer_.certificateNumber), request.getCertificateNumber()));
                }

                if (request.getLevel() != null) {
                    predicates.add(cb.equal(root.get(Customer_.level), request.getLevel()));
                }

                if (request.getTeam() != null) {
                    predicates.add(cb.equal(root.get(Customer_.team).get(Team_.id), request.getTeam()));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(Customer_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                if (request.getUsername() != null) {
                    predicates.add(cb.like(root.get(Customer_.username), String.format("%%%s%%", request.getUsername())));
                }

                if (request.getParent() != null) {
                    predicates.add(cb.equal(root.get(Customer_.parent).get(Customer_.id), request.getParent()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<CustomerWrapper> res = new QueryResponse<>();
        res.setContent(EntityUtils.toWrappers(page.getContent(), CustomerWrapper.class));
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());

        return res;
    }

    @Transactional
    public Response insertNode(Customer parent, Customer customer) {
        customerRepository.updateCustomerRightCode(parent.getRightCode(), parent.getTeam().getId());
        customerRepository.updateCustomerLeftCode(parent.getRightCode(), parent.getTeam().getId());
        customer.setLeftCode(parent.getRightCode());
        customer.setRightCode(parent.getRightCode() + 1);
        customerRepository.save(customer);

        return Response.successResponse;
    }

    public void savePendingApprovalCustomer(PendingApprovalCustomer customer) {
        pendingApprovalCustomerRepository.save(customer);
    }

    public Response<TmpCustomerWrapper> getTmpCustomers(final Customer currentCustomer, final TmpCustomerListRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<PendingApprovalCustomer> page = pendingApprovalCustomerRepository.findAll(new Specification<PendingApprovalCustomer>() {
            @Override
            public Predicate toPredicate(Root<PendingApprovalCustomer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(PendingApprovalCustomer_.name), String.format("%%%s%%", request.getName())));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(PendingApprovalCustomer_.status), request.getStatus()));
                }

                if (request.getCertificateNumber() != null) {
                    predicates.add(cb.equal(root.get(PendingApprovalCustomer_.certificateNumber), request.getCertificateNumber()));
                }

                if (request.getLevel() != null) {
                    predicates.add(cb.equal(root.get(PendingApprovalCustomer_.level), request.getLevel()));
                }

                if (request.getTeam() != null) {
                    predicates.add(cb.equal(root.get(PendingApprovalCustomer_.team).get(Team_.id), request.getTeam()));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(PendingApprovalCustomer_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                if (request.getUsername() != null) {
                    predicates.add(cb.like(root.get(PendingApprovalCustomer_.username), String.format("%%%s%%", request.getUsername())));
                }

                if (CustomerStage.get(request.getStage()) == CustomerStage.STAGE_1) {
                    predicates.add(cb.equal(root.get(PendingApprovalCustomer_.parent).get(Customer_.id), currentCustomer.getId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<TmpCustomerWrapper> res = new QueryResponse<>();
        res.setContent(EntityUtils.toWrappers(page.getContent(), TmpCustomerWrapper.class));
        res.setTotal(page.getTotalElements());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());

        return res;
    }

    @Transactional
    public PendingApprovalCustomer auditCustomer(AuditCustomerRequest request) {

        Long tmpCustomerId = request.getTmpCustomerId();

        PendingApprovalCustomer pendingApprovalCustomer = pendingApprovalCustomerRepository.getOne(tmpCustomerId);

        if (request.getType().shortValue() == (short)1) {
            if (request.getResult().shortValue() == (short)0) {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.CHIEF_REJECT.getValue());
            } else {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.WAITING_TEAM_LEADER.getValue());
                pendingApprovalCustomer.setStage(CustomerStage.STAGE_2.getValue());
            }
        } else if (request.getType().shortValue() == (short)2) {
            if (request.getResult().shortValue() == (short)0) {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.TEAM_LEADER_REJECT.getValue());
            } else {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
                pendingApprovalCustomer.setStage(CustomerStage.STAGE_3.getValue());
            }
        } else if (request.getType().shortValue() == (short)3) {
            if (request.getResult().shortValue() == (short)0) {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.HQ_REJECT.getValue());
            } else {
                pendingApprovalCustomer.setStatus(CustomerAuditStatus.PASS.getValue());
            }
        }

        return pendingApprovalCustomerRepository.save(pendingApprovalCustomer);
    }

    public TmpCustomerWrapper getTmpCustomer(Long id) {
        return new TmpCustomerWrapper(pendingApprovalCustomerRepository.getOne(id));
    }
}