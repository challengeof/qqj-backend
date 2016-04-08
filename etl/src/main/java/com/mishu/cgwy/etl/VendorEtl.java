package com.mishu.cgwy.etl;

import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.profile.domain.Address;
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
 * Created by kaicheng on 3/31/15.
 */
@Service
public class VendorEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Vendor> list = legacyJdbcTemplate.query("select id, name, address, brand, realname, telephone, e_mail" +
                " from source", new RowMapper<Vendor>() {
            @Override
            public Vendor mapRow(ResultSet resultSet, int i) throws SQLException {
                Vendor vendor = new Vendor();
                vendor.setName(resultSet.getString("name"));
                vendor.setId(resultSet.getLong("id"));
                vendor.setContact(resultSet.getString("realname"));
                vendor.setTelephone(resultSet.getString("telephone"));
                vendor.setEmail(resultSet.getString("e_mail"));
                vendor.setBrand(resultSet.getString("brand"));
                vendor.setAddress(resultSet.getString("address"));
                return vendor;
            }
        });

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Vendor vendor : list) {
            Object[] args = new Object[]{vendor.getId(), vendor.getAddress(), vendor.getBrand(), vendor.getEmail(),
                    vendor.getContact(), vendor.getTelephone(), vendor.getName()};
            batchArgs.add(args);

        }
        jdbcTemplate.batchUpdate("insert into vendor(id, address, brand, email, contact, telephone, name, prepaid) " +
                "values" +
                "(? , ? , ? ," +
                "? , ? , ? , ?, true)", batchArgs);

    }
}
