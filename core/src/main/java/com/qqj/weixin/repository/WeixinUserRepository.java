package com.qqj.weixin.repository;

import com.qqj.weixin.domain.WeixinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WeixinUserRepository extends JpaRepository<WeixinUser, Long> , JpaSpecificationExecutor<WeixinUser>{
}
