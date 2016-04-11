package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.error.CustomerAlreadyExistsException;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:02 PM
 */
@Service
@Transactional
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private RandomCodeValidator randomCodeValidator;

    @Autowired(required = false)
    private ISmsProvider smsProvider;

    @Autowired
    private EntityManager entityManager;

    public Customer register(Customer customer) {
        if (findCustomerByUsername(customer.getUsername()) != null) {
            throw new CustomerAlreadyExistsException();
        }

        // TODO: check username format, it should be a telephone number

        customer.setPassword(getReformedPassword(customer.getUsername(), customer.getPassword()));
        customer.setCreateTime(new Date());

        return customerRepository.save(customer);
    }



    /**
     * 兼容原有系统密码规则
     *
     * @param username
     * @param password
     * @return
     */
    public String getReformedPassword(String username, String password) {
//        return username + password + "mirror";
        return passwordEncoder.encode(username + password + "mirror");
    }

    public Customer update(Customer customer) {
        assert customer.getId() != null;
        return customerRepository.save(customer);
    }

    public Customer updateCustomerPassword(Customer customer, String password) {
        customer.setPassword(getReformedPassword(customer.getUsername(), password));

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

    public List<Customer> getCustomerByAdminUserId(Long adminUserId) {
        return customerRepository.findByAdminUserId(adminUserId);
    }

}