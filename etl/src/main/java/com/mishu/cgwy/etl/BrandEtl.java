package com.mishu.cgwy.etl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by kaicheng on 3/30/15.
 */
@Service
public class BrandEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select value, show_value from dict_value where key_id = 5");

        //去重
        Set<Map<String, Object>> hs = new HashSet<Map<String, Object>>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
        	Object show_value = entity.get("show_value");
        	if(null != show_value && StringUtils.isNotBlank(show_value.toString())){
        		Object[] args = new Object[]{entity.get("value"), entity.get("show_value")};
        		batchArgs.add(args);
        	}
        }
        jdbcTemplate.batchUpdate("insert into brand(id, brand_name) values (? , ?)", batchArgs);
    }
}
