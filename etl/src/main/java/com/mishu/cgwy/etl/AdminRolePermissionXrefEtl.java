package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kaicheng on 3/31/15.
 */
@Service
public class AdminRolePermissionXrefEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {

        List<Object[]> batchArgs = Arrays.asList(
                new Object[]{1, 64},
                new Object[]{2, 65},
                new Object[]{3, 66},
                new Object[]{2, 66},
                new Object[]{6, 67},
                new Object[]{3, 67},
                new Object[]{6, 68},
                new Object[]{8, 69},
                new Object[]{8, 70},
                new Object[]{8, 71},
                new Object[]{5, 72},
                new Object[]{6, 73},
                new Object[]{12, 74},
                new Object[]{12, 75},
                new Object[]{12, 73},
                new Object[]{1, 77},
                new Object[]{12, 78},
                new Object[]{5, 79},
                new Object[]{13, 64},
                new Object[]{14, 64},
                new Object[]{2, 80},
                new Object[]{3, 80}

        );
        jdbcTemplate.batchUpdate("insert into admin_role_permission_xref(admin_role_id, admin_permission_id) " +
                "values( ? , ?)", batchArgs);

    }

}
