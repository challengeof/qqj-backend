package com.mishu.cgwy.conf.repository;

import com.mishu.cgwy.conf.domain.Conf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangwei on 15/7/6.
 */
public interface ConfRepository extends JpaRepository<Conf, Long>, JpaSpecificationExecutor<Conf> {
    List<Conf> findByName(String name);
}
