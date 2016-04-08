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
public class RegionEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select id, parent_id, name from " +
                "region where parent_id != 0");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("id"), entity.get
                    ("name"), entity.get("parent_id")};
            batchArgs.add(args);
        }

        jdbcTemplate.batchUpdate("insert into region(id, name, city_id) values(?, ?, ?)", batchArgs);
    }
}
