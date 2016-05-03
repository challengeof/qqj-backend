package com.qqj.weixin.service;

import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.repository.WeixinPicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeixinPicService {
    @Autowired
    private WeixinPicRepository weixinPicRepository;

    public WeixinPic findWeixinPicByWeixinUserIdAndType(Long id, Short type) {
        return weixinPicRepository.findByWeixinUserIdAndType(id, type);
    }

    public void save(WeixinPic weixinPic) {
        weixinPicRepository.save(weixinPic);
    }
}
