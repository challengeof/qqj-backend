package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 3/27/15
 * Time: 3:49 PM
 */
@Service
public class ZoneEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select id, region_id, name, status from zone");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("id"), entity.get("name"), entity.get("region_id"), entity.get("status"), 1};
            batchArgs.add(args);
        }

        jdbcTemplate.batchUpdate("insert into zone(id, name, region_id, active, warehouse_id) values(?, ?, ?, ?, ?)", batchArgs);
    }
}
