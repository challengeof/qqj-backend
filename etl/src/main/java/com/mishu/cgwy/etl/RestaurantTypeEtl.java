package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/30/15.
 */
@Service
public class RestaurantTypeEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select value, show_value from dict_value where key_id = 4");

        for (Map<String, Object> entity : list) {
            jdbcTemplate.update("insert into restaurant_type(id, show_value) values(? , ?)", entity.get("value"), entity.get("show_value"));
        }
    }
}
