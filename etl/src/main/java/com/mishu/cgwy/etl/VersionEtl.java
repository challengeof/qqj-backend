package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 4/16/15.
 */
@Service
public class VersionEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList("select id, version_code, version_name, comment, type, file_id, force_update from " +
                "terminal_version");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("id"), entity.get("version_code"), entity.get("version_name"), entity.get("comment"), entity.get("type"),
            entity.get("file_id"), entity.get("force_update")};
            batchArgs.add(args);
        }

        jdbcTemplate.batchUpdate("insert into version(id, version_code, version_name, comment, type, file_id, force_update) values(?, ?, ?, ?, ?, ?, ?)", batchArgs);

    }
}
