package com.qqj.weixin.repository;

import com.qqj.weixin.domain.WeixinPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WeixinPicRepository extends JpaRepository<WeixinPic, Long> , JpaSpecificationExecutor<WeixinPic>{
    WeixinPic findByWeixinUserIdAndType(Long id, Short type);
}
