package com.qqj.barcode.repository;

import com.qqj.barcode.domain.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
public interface BarcodeRepository extends JpaRepository<Barcode, Long>, JpaSpecificationExecutor<Barcode> {

    List<Barcode> findByBoxCode(String boxCode);
}
