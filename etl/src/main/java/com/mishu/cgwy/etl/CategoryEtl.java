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
public class CategoryEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        //一级分类
        final List<Map<String, Object>> list = legacyJdbcTemplate.queryForList(
                "select id, name, status, order_index, file_id from category where category_d1 = 0 and category_d2 = 0");

        for (Map<String, Object> entity : list) {
            if (entity.get("file_id").equals(0)){
                entity.put("file_id", null);
            }
            jdbcTemplate.update("insert into category(id, display_order, name, status,media_file_id, parent_id) values " +
                            "(? , ? , ? , ? , ? , ?)", entity.get("id"), entity.get("order_index"), entity.get("name"), entity.get("status"),
                    entity.get("file_id"), null);
        }
        //二级分类
        final List<Map<String, Object>> list1 = legacyJdbcTemplate.queryForList(
                "select id, name, status, category_d1, order_index, file_id from category where category_d1 != 0 and category_d2 = 0");
        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list1) {
            if (entity.get("file_id").equals(0)) {
                entity.put("file_id", null);
            }
            Object[] args = new Object[]{entity.get("id"), entity.get("order_index"), entity.get("name"), entity.get("status"),
                    entity.get("file_id"), entity.get("category_d1")};
            batchArgs.add(args);

        }
        jdbcTemplate.batchUpdate("insert into category(id, display_order, name, status,media_file_id, parent_id) values " +
                "(? , ? , ? , ? , ? , ?)", batchArgs);

        //三级分类
        final List<Map<String, Object>> list2 = legacyJdbcTemplate.queryForList(
                "select id, name, status, category_d2, order_index, file_id from category where category_d2 != 0");
        List<Object[]> batchArgs3 = new ArrayList<Object[]>();
        for (Map<String, Object> entity : list2) {
            if (entity.get("file_id").equals(0)) {
                entity.put("file_id", null);
            }
            Object[] args = new Object[]{entity.get("id"), entity.get("order_index"), entity.get("name"), entity.get("status"),
                    entity.get("file_id"), entity.get("category_d2")};
            batchArgs3.add(args);

        }
        jdbcTemplate.batchUpdate("insert into category(id, display_order, name, status, media_file_id, parent_id) values " +
                "(? , ? , ? , ? , ? , ?)", batchArgs3);

    }
}
