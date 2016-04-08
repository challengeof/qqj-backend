package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import com.mishu.cgwy.accounting.repository.AccountPayableWriteOffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangguodong on 15/10/13.
 */
@Service
public class AccountPayableWriteOffService {

    @Autowired
    private AccountPayableWriteOffRepository accountPayableWriteOffRepository;

    @Transactional
    public AccountPayableWriteoff save(AccountPayableWriteoff accountPayableWriteoff) {
        return accountPayableWriteOffRepository.save(accountPayableWriteoff);
    }

    public AccountPayableWriteoff getOne(Long id) {
        return accountPayableWriteOffRepository.getOne(id);
    }
}
