package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomerHintEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    public void transfer() {
        List<Map<String, Object>> entities = legacyJdbcTemplate.queryForList("select * from user_hint where user_id in (select id from user)");
        for (Map<String, Object> entity : entities) {
            jdbcTemplate.update("insert into customer_hint(id, create_time, customer_id," +
                            " name, update_time, value) values(?,?,?,?,?,?)",
                    entity.get("id"),
                    entity.get("create_time"),
                    entity.get("user_id"),
                    entity.get("name"),
                    entity.get("update_time"),
                    entity.get("value"));

        }
    }


}
