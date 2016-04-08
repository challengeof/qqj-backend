package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/31/15.
 */
@Service
public class AdminUserRoleXrefEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public void transfer() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select admin_id, role_id from admin_role, " +
                "admin where admin_role.admin_id = admin.id");

        final List<Integer> roleList = jdbcTemplate.queryForList("select id from admin_role",Integer.class);
        final Iterator<Map<String, Object>> iterator = list.iterator();

        while (iterator.hasNext()) {
            Integer roleId = Integer.valueOf(iterator.next().get("role_id").toString());
            if (!roleList.contains(roleId)) {
                iterator.remove();
            }
        }

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (final Map<String, Object> entity : list) {
            Object[] args = new Object[]{entity.get("admin_id"), entity.get("role_id")};
            batchArgs.add(args);

        }
        jdbcTemplate.batchUpdate("insert into admin_user_role_xref(admin_user_id, admin_role_id) values ( ? , ?)", batchArgs);
    }
}
