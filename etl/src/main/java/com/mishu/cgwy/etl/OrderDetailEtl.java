package com.mishu.cgwy.etl;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.domain.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderDetailEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate legacyJdbcTemplate;
    private List<Long> ids = new ArrayList<Long>();

    @Transactional
    public void transfer() {
        List<Map<String, Object>> lists = jdbcTemplate.queryForList("select id from cgwy_order");
        for (Map<String, Object> map : lists) {
            Long id = new Long(Long.parseLong(map.get("id").toString()));
            ids.add(id);
        }
        List<Map<String, Object>> entities = legacyJdbcTemplate.queryForList("select od.* from order_detail od,product p where od.product_id = p.id");
        List<OrderItem> items = new ArrayList<OrderItem>();
        for (Map<String, Object> entity : entities) {
            Long orderId = new Long(Long.parseLong(entity.get("order_id").toString()));
            if (ids.contains(orderId)) {
                OrderItem ot = new OrderItem();
                int id = Integer.parseInt(entity.get("id").toString());
                BigDecimal price = new BigDecimal(Double.parseDouble(entity.get("price").toString()));
                int num = Integer.parseInt(entity.get("num").toString());
                int productId = Integer.parseInt(entity.get("product_id").toString());
                ot.setId(new Long(id));
                Order order = new Order();
                order.setId(new Long(orderId));
                ot.setOrder(order);
                ot.setPrice(price);
//                ot.setQuantity(num);
                Sku sku = new Sku();
//                sku.setBundle(false);
                sku.setId(new Long(productId));
                ot.setSku(sku);
                ot.setTotalPrice(price.multiply(new BigDecimal(num)));
                items.add(ot);
            }
        }

        for (OrderItem ot : items) {

            jdbcTemplate.update("insert into  order_item(id,price,quantity,total_price,order_id,sku_id) values(?,?,?,?,?,?)",
                    ot.getId(),
                    ot.getPrice(),
//                    ot.getQuantity(),
                    ot.getTotalPrice(),
                    ot.getOrder().getId(),
                    ot.getSku().getId());
        }

    }

}
