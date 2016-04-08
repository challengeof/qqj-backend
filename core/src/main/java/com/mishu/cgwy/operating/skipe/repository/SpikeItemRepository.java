package com.mishu.cgwy.operating.skipe.repository;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by king-ck on 2016/1/7.
 */
public interface SpikeItemRepository extends JpaRepository<SpikeItem,Long>,JpaSpecificationExecutor<SpikeItem> {


}
