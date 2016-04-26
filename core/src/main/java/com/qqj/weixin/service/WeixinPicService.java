package com.qqj.weixin.service;

import com.qqj.weixin.repository.WeixinPicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeixinPicService {
    @Autowired
    private WeixinPicRepository adminUserRepository;
}
