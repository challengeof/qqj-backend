package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    List<Warehouse> findByCityIdAndIsDefault(Long cityId, boolean b);

    List<Warehouse> findByNameAndCityId(String name, Long cityId);

    List<Warehouse> findByDepotId(Long depotId);

    List<Warehouse> findByDepotIdAndActive(Long depotId, boolean b);

    List<Warehouse> findByCityIdAndActive(Long cityId, boolean b);
}
