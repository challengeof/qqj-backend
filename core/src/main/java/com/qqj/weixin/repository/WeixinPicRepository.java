package com.qqj.weixin.repository;

import com.qqj.weixin.domain.WeixinPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WeixinPicRepository extends JpaRepository<WeixinPic, Long> , JpaSpecificationExecutor<WeixinPic>{
    List<WeixinPic> findByWeixinUserIdAndType(Long id, Short type);
}
