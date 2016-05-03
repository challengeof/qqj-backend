package com.qqj.weixin.repository;

import com.qqj.weixin.domain.WeixinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WeixinUserRepository extends JpaRepository<WeixinUser, Long> , JpaSpecificationExecutor<WeixinUser>{
    List<WeixinUser> findByOpenId(String openId);
}
