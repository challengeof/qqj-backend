package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by wangwei on 15/7/6.
 */
public interface BlockRepository extends JpaRepository<Block, Long>, JpaSpecificationExecutor<Block> {

    List<Block> findByWarehouseId(Long warehouseId);

    List<Block> findByWarehouseIdAndActive(Long warehouseId, boolean b);

    List<Block> findByNameAndCityId(String name, Long cityId);

    List<Block> findByCityId(Long cityId);

    List<Block> findByCityIdAndActive(Long cityId, boolean b);

}
