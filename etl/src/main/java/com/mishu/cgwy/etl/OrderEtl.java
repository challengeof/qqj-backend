package com.mishu.cgwy.etl;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


@Service
public class OrderEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Order> orders = legacyJdbcTemplate.query("select om.id,om.order_number,om.price,om.restaurant_id,om.status,om.user_id,om.create_time," +
                "om.shipping_fee,om.remark  from order_main om,user where om.user_id = user.id", new RowMapper<Order>() {
            @Override
            public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String orderNumber = rs.getString("order_number");
                BigDecimal total = rs.getBigDecimal("price");
                int restaurantId = rs.getInt("restaurant_id");
                int status = rs.getInt("status");
                int userId = rs.getInt("user_id");
                Timestamp createTime = rs.getTimestamp("create_time");
                int shippingFee = rs.getInt("shipping_fee");
                String remark = rs.getString("remark");

                Order order = new Order();
                order.setId(id);

                Customer customer = new Customer();
                customer.setId(new Long(userId));
                order.setCustomer(customer);

                Restaurant r = new Restaurant();
                r.setId(new Long(restaurantId));
                order.setRestaurant(r);
                order.setShipping(new BigDecimal(shippingFee));
                order.setStatus(status);
                order.setSubmitDate(createTime);
                order.setSubTotal(total);
                order.setTotal(total);
                order.setMemo(remark);
                return order;
            }

        });

        for (Order order : orders) {
            jdbcTemplate.update("insert into cgwy_order(id,memo,shipping,status,sub_total, submit_date,total," +
                            "customer_id,restaurant_id) values(?,?,?,?,?,?,?,?,?)",
                    order.getId(),
                    order.getMemo(),
                    order.getShipping(),
                    order.getStatus(),
                    order.getSubTotal(),
                    order.getSubmitDate(),
                    order.getTotal(),
                    order.getCustomer().getId(),
                    order.getRestaurant().getId());

        }


    }

}
