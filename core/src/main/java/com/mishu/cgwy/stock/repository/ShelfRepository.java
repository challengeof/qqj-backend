package com.mishu.cgwy.stock.repository;

import com.mishu.cgwy.stock.domain.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ShelfRepository extends JpaRepository<Shelf, Long>, JpaSpecificationExecutor<Shelf> {
    List<Shelf> findByDepotIdAndShelfCode(Long depotId, String shelfCode);
}
