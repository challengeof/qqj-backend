package com.mishu.cgwy.etl;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Customer> customers = legacyJdbcTemplate.query("select id,username,password,telephone,admin_id,zone_id from user",
                new RowMapper<Customer>() {
                    @Override
                    public Customer mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Long id = rs.getLong("id");
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String telephone = rs.getString("telephone");
                        Long adminId = rs.getLong("admin_id");
                        Long zoneId = rs.getLong("zone_id");

                        Customer customer = new Customer();
                        customer.setId(id);
                        customer.setUsername(username);
                        customer.setPassword(password);

                        /*if (zoneId != 0L){
                            Zone zone = new Zone();
                            zone.setId(zoneId);
                            customer.setZone(zone);

                        } else {
                            Zone zone = new Zone();
                            zone.setId(null);
                            customer.setZone(zone);
                        }*/


                        if (adminId != 0L){
                            AdminUser adminUser = new AdminUser();
                            adminUser.setId(adminId);
                            customer.setAdminUser(adminUser);
                        } else {
                            AdminUser adminUser = new AdminUser();
                            adminUser.setId(null);
                            customer.setAdminUser(adminUser);
                        }



                        customer.setReferrerId(null);
                        customer.setEnabled(true);
                        customer.setTelephone(telephone);
                        return customer;
                    }

                });

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Customer customer : customers) {
            /*batchArgs.add(new Object[]{customer.getId(), customer.getUsername(), customer.getPassword(),
                    customer.getZone().getId(), customer.getAdminUser().getId(), customer.getReferrerId(), customer
                    .isEnabled()
                    , customer.getTelephone()});*/

        }
        jdbcTemplate.batchUpdate("insert into customer(id,username,password,zone_id,admin_user_id,referrer_id," +
                "enabled,telephone) values(?,?,?,?,?,?,?,?)", batchArgs);


    }

}
