package com.mishu.cgwy.etl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xingdong on 15/7/20.
 */
@Service
public class ZoneTransferBlock {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void transfer(){
        final List<Map<String, Object>> list = jdbcTemplate.queryForList("select id, active, name, warehouse_id from zone");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("id"), entity.get("active"), entity.get("name"),entity.get("warehouse_id"), 1};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate("insert into block(id,active,name,warehouse_id,city_id) values(?,?,?,?,?)",batchArgs);


    }
}
