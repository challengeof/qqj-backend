package com.mishu.cgwy.etl;

import com.mishu.cgwy.admin.domain.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/30/15
 * Time: 12:15 PM
 */
@Service
public class AdminUserEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<AdminUser> adminUsers = legacyJdbcTemplate.query("select id, username, password, realname, " +
                "telephone, status from admin", new
                RowMapper<AdminUser>() {
                    @Override
                    public AdminUser mapRow(ResultSet resultSet, int i) throws SQLException {
                        Long id = resultSet.getLong("id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String realname = resultSet.getString("realname");
                        String telephone = resultSet.getString("telephone");
                        int status = resultSet.getInt("status");

                        AdminUser adminUser = new AdminUser();
                        adminUser.setId(id);
                        adminUser.setUsername(username);
                        adminUser.setPassword(password);
                        adminUser.setEnabled(status == 1);
                        adminUser.setTelephone(telephone);
                        adminUser.setRealname(realname);
                        return adminUser;
                    }
                });

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (AdminUser adminUser : adminUsers) {
            Object[] args = new Object[]{adminUser.getId(), adminUser.isEnabled(), adminUser.getUsername(), adminUser.getPassword(),
                    adminUser.getTelephone(), adminUser.getRealname()};
            batchArgs.add(args);
        }
        jdbcTemplate.batchUpdate("insert into admin_user(id, enabled, username, password, telephone, realname) values" +
                "(?, ?, ?, ?, ?, ?)", batchArgs);
    }
}
