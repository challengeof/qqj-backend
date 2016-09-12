package com.qqj.org.service;

import com.qqj.org.domain.Customer;
import com.qqj.org.domain.TmpCustomer;
import com.qqj.org.repository.TmpCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TmpCustomerService {

    @Autowired
    private TmpCustomerRepository tmpCustomerRepository;

    public TmpCustomer findCustomerByUsername(String username) {
        final List<TmpCustomer> list = tmpCustomerRepository.findByUsername(username);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }
}