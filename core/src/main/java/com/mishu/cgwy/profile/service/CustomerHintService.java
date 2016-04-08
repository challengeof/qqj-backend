package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.profile.domain.CustomerHint;
import com.mishu.cgwy.profile.repository.CustomerHintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerHintService {
    @Autowired
    private CustomerHintRepository customerHintRepository;

    public void save(CustomerHint customerHint) {
        customerHintRepository.save(customerHint);
    }

}
