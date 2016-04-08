package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/30/15.
 */
@Service
public class MediaFileEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select id, url from file");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("id"), entity.get("url")};
            batchArgs.add(args);

        }
        jdbcTemplate.batchUpdate("insert into media_file(id, local_path) values( ? , ? )",
                batchArgs);
    }
}
