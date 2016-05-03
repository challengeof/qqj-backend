package com.qqj.weixin.service;

import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.repository.WeixinPicRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeixinPicService {
    @Autowired
    private WeixinPicRepository weixinPicRepository;

    public WeixinPic findWeixinPicByWeixinUserIdAndType(Long id, Short type) {
        List<WeixinPic> weixinPicList = weixinPicRepository.findByWeixinUserIdAndType(id, type);
        return CollectionUtils.isNotEmpty(weixinPicList) ? weixinPicList.get(0) : null;
    }

    public void save(WeixinPic weixinPic) {
        weixinPicRepository.save(weixinPic);
    }
}
