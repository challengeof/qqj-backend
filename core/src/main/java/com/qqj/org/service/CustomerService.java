package com.qqj.org.service;

import com.qqj.org.controller.AuditCustomerRequest;
import com.qqj.org.controller.CustomerListRequest;
import com.qqj.org.controller.legacy.pojo.RegisterTaskListRequest;
import com.qqj.org.domain.*;
import com.qqj.org.enumeration.CustomerAuditStage;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.repository.CustomerRepository;
import com.qqj.org.repository.TmpCustomerRepository;
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
    private TmpCustomerRepository tmpCustomerRepository;

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

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
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

        return register(customer);
    }

    public void saveTmpCustomer(TmpCustomer tmpCustomer) {
        tmpCustomerRepository.save(tmpCustomer);
    }

    public Response<TmpCustomerWrapper> getTmpCustomerWrappers(final Customer currentCustomer, final RegisterTaskListRequest request) {
        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<TmpCustomer> page = tmpCustomerRepository.findAll(new Specification<TmpCustomer>() {
            @Override
            public Predicate toPredicate(Root<TmpCustomer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(TmpCustomer_.name), String.format("%%%s%%", request.getName())));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(TmpCustomer_.status), request.getStatus()));
                }

                if (request.getCertificateNumber() != null) {
                    predicates.add(cb.equal(root.get(TmpCustomer_.certificateNumber), request.getCertificateNumber()));
                }

                if (request.getLevel() != null) {
                    predicates.add(cb.equal(root.get(TmpCustomer_.level), request.getLevel()));
                }

                if (request.getTeam() != null) {
                    predicates.add(cb.equal(root.get(TmpCustomer_.team).get(Team_.id), request.getTeam()));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(TmpCustomer_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                if (request.getUsername() != null) {
                    predicates.add(cb.like(root.get(TmpCustomer_.username), String.format("%%%s%%", request.getUsername())));
                }

                if (currentCustomer != null) {
                    predicates.add(cb.equal(root.get(TmpCustomer_.directLeader).get(Customer_.id), currentCustomer.getId()));
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
    public TmpCustomer auditCustomer(TmpCustomer tmpCustomer, AuditCustomerRequest request) {

        if (CustomerAuditStage.get(request.getType()) == CustomerAuditStage.STAGE_1) {
            if (request.getResult().shortValue() == (short)0) {
                tmpCustomer.setStatus(CustomerAuditStatus.DIRECT_LEADER_REJECT.getValue());
            } else {
                tmpCustomer.setStatus(CustomerAuditStatus.WAITING_HQ.getValue());
            }
        } else if (CustomerAuditStage.get(request.getType()) == CustomerAuditStage.STAGE_2) {
            if (request.getResult().shortValue() == (short)0) {
                tmpCustomer.setStatus(CustomerAuditStatus.HQ_REJECT.getValue());
            } else {
                tmpCustomer.setStatus(CustomerAuditStatus.PASS.getValue());
            }
        }

        return tmpCustomerRepository.save(tmpCustomer);
    }

    public TmpCustomer getTmpCustomer(Long id) {
        return tmpCustomerRepository.getOne(id);
    }

    public TmpCustomerWrapper getTmpCustomerWrapper(Long id) {
        return new TmpCustomerWrapper(tmpCustomerRepository.getOne(id));
    }
}