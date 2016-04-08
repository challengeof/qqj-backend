package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DepotRepository extends JpaRepository<Depot, Long>, JpaSpecificationExecutor<Depot> {
    List<Depot> findByCityId(Long cityId);
    List<Depot> findByName(String name);
}
