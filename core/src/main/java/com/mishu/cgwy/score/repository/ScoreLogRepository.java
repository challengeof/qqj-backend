package com.mishu.cgwy.score.repository;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.SalesFinance;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.stock.domain.StockOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

/**
 * Created by bowen on 15/11/10.
 */
public interface ScoreLogRepository extends JpaRepository<ScoreLog, Long> , JpaSpecificationExecutor<ScoreLog> {


    ScoreLog findByOrderAndStatus(Order order, int status);

}
