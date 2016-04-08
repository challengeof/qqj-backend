package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 4/15/15.
 */
@Service
public class DynamicSkuPriceEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        /*
List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select product.in_price, source_id, " +
        "product_purchase.id as id1, product.id as id2 from product, product_purchase where product.id = product_purchase.product_id");
 List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select product_purchase.id as id1, price, product.id as id2, " +
                " amount from product, product_purchase where product.id = product_purchase.product_id");
         */

        List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select product.in_price, product.price, " +
                "product_purchase.source_id, product.id as product_id, product.status as status, product_purchase" +
                ".amount from product left join product_purchase on product.id = product_purchase.product_id");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {

            Integer sourceId = null;
            final Object in_price = entity.get("in_price");
            if (entity.get("source_id") != null && ((Number)entity.get("source_id")).intValue() != 0) {
                sourceId = ((Number)entity.get("source_id")).intValue();
            }

            Integer amount = 0;

            if (entity.get("amount") != null) {
                amount = ((Number)entity.get("amount")).intValue();
            }

            boolean available = ((Number)entity.get("status")).intValue() == 2;

            batchArgs.add(new Object[]{entity.get("price"),
                    entity.get("product_id"), 1, available, amount, in_price,
                    sourceId});

        }
        jdbcTemplate.batchUpdate("insert into dynamic_sku_price(sale_price, sku_id, warehouse_id, available, " +
                "stock, purchase_price, vendor_id)" +
                " values (? , ? , ? , ? , ? ,?, ?)", batchArgs);


    }
}
