package com.mishu.cgwy.operating.skipe.repository;

import com.mishu.cgwy.operating.skipe.domain.Spike;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by king-ck on 2016/1/7.
 */
public interface SpikeRepository extends JpaRepository<Spike,Long>,JpaSpecificationExecutor<Spike> {

    @Modifying
    @Query("update SpikeItem s set s.takeNum = s.takeNum + :quantity  where s.id = :spikeItemId  and s.num-s.takeNum >=:quantity")
    public int increaseTakeNum(@Param("spikeItemId")Long spikeItemId, @Param("quantity")int quantity);

}
