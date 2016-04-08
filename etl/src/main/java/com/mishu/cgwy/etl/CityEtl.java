package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 3/27/15
 * Time: 3:49 PM
 */
@Service
public class CityEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select id, parent_id, name from region where parent_id = 0");
        for (Map<String, Object> entity : list) {
            jdbcTemplate.update("insert into city(id, name) values(?, ?)", entity.get("id"), entity.get("name"));
        }
    }
}
