package com.qqj.barcode.repository;

import com.qqj.barcode.domain.BarcodeItem;
import com.qqj.barcode.vo.BarcodeItemVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by bowen on 16/4/27.
 */
public interface BarcodeItemRepository extends JpaRepository<BarcodeItem, Long>, JpaSpecificationExecutor<BarcodeItem> {

}
