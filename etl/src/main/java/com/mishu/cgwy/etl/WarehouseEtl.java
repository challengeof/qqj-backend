package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by kaicheng on 4/1/15.
 */
@Service
public class WarehouseEtl {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void init() {
        jdbcTemplate.update("insert into warehouse(id, name, city_id) values(? , ? , ?)", 1, "城北市场", 1);
    }
}
