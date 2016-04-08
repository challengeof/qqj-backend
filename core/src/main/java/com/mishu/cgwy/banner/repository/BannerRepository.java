package com.mishu.cgwy.banner.repository;

import com.mishu.cgwy.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by bowen on 15-5-25.
 */
public interface BannerRepository extends JpaRepository<Banner, Long>, JpaSpecificationExecutor<Banner> {
    @Modifying
    @Query("update Banner b set orderValue = orderValue+1 where id != :id and rule like %:rule% and orderValue >= :ov")
    void updateBannerOrder(@Param("id") Long id,@Param("rule") String rule ,@Param("ov") Integer orderValue);


}
