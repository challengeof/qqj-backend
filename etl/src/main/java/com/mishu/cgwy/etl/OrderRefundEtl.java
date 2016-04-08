package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderRefundEtl {
    @Autowired
    private JdbcTemplate legacyJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void transfer() {
        List<Map<String, Object>> entities = legacyJdbcTemplate.queryForList("select id,order_id from order_return");

        for (Map<String, Object> entity : entities) {
            Long id = Long.parseLong(entity.get("id") + "");
            Long orderId = Long.parseLong(entity.get("order_id") + "");
            List<Map<String, Object>> lists = legacyJdbcTemplate.queryForList("select id,product_id,num," +
                    "price from order_return_detail where order_return_id = ?", id);
            for (Map<String, Object> list : lists) {
                int num = Integer.parseInt(list.get("num") + "");
                BigDecimal price = new BigDecimal(Double.parseDouble(list.get("price").toString()));
                jdbcTemplate.update("insert into refund(id, price, quantity, total_price, " +
                                "order_id, sku_id) values(?,?,?,?,?,?)",
                        list.get("id"),
                        list.get("price"),
                        list.get("num"),
                        price.multiply(new BigDecimal(num)),
                        orderId,
                        list.get("product_id")
                );
            }
        }
    }
}
