package com.mishu.cgwy.etl;

import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RestaurantEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        final List<Restaurant> restaurants = legacyJdbcTemplate.query(
                "select id,name,address,license,realname,telephone,status, create_time, "
                        + "user_id,type,location,region_id, zone_id  from restaurant where user_id in (select id from" +
                        " user)",
                new RowMapper<Restaurant>() {
                    @Override
                    public Restaurant mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Long id = rs.getLong("id");
                        String addressName = rs.getString("address");
                        String name = rs.getString("name");
                        String license = rs.getString("license");
                        String receiver = rs.getString("realname");
                        String location = rs.getString("location");
                        String longitude = null;
                        String latitude = null;
                        String create_time = rs.getString("create_time");

                        if (location != null) {
                            if (location.matches("^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)$")) {
                                String[] longitude_latitude = location.split(",");
                                if (longitude_latitude.length == 2){
                                    longitude = longitude_latitude[0];
                                    latitude = longitude_latitude[1];
                                }

                            }
//                            Pattern pattern = Pattern.compile("^\\d+,\\d+$");
//                            Matcher matcher = pattern.matcher(location);
//                            if (matcher.find()) {
//                                String[] longitude_latitude = location.split(",");
//                                longitude = longitude_latitude[0];
//                                latitude = longitude_latitude[1];
//                            }
                        }
                        int type = rs.getInt("type");
                        Long regionId = rs.getLong("region_id");
                        Long zoneId = rs.getLong("zone_id");
                        String telephone = rs.getString("telephone");
                        int status = rs.getInt("status");
                        Long customerId = rs.getLong("user_id");
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(id);

                        try {
                            Date createTime = format.parse(create_time);
                            restaurant.setCreateTime(createTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Address address = new Address();
                        address.setAddress(addressName);
                        Zone zone = new Zone();
//                        Region region = new Region();
                        zone.setId(zoneId);
                        /*if (zoneId == 0) {
                            address.setZone(null);
                        } else {
                            address.setZone(zone);
                        }*/

                        Wgs84Point wgs84Point = new Wgs84Point();

                        if (longitude != null && latitude != null) {
                            wgs84Point.setLongitude(Double.parseDouble(longitude));
                            wgs84Point.setLatitude(Double.parseDouble(latitude));
                        } else {
                            wgs84Point.setLongitude(null);
                            wgs84Point.setLatitude(null);
                        }
                        address.setWgs84Point(wgs84Point);

                        restaurant.setAddress(address);

                        Customer customer = new Customer();
                        customer.setId(customerId);
                        restaurant.setCustomer(customer);
                        restaurant.setLicense(license);
                        restaurant.setName(name);
                        restaurant.setReceiver(receiver);
                        restaurant.setStatus(status);
                        restaurant.setTelephone(telephone);
//                        restaurant.setType(type);
//                        restaurant.setValidated(status == 2 ? true : false);
//                        restaurant.isValidated();
                        return restaurant;
                    }
                });

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Restaurant entity : restaurants) {
            batchArgs.add(new Object[]{entity.getId(),
                    entity.getAddress().getAddress(),
                    entity.getAddress().getWgs84Point().getLatitude(),
                    entity.getAddress().getWgs84Point().getLongitude(),
                    format.format(entity.getCreateTime()),
                    entity.getLicense(),
                    entity.getName(),
                    entity.getReceiver(),
                    entity.getStatus(),
                    entity.getTelephone(),
                    entity.getType(),
//                    entity.isValidated(),
//                    entity.getAddress().getZone() == null ? null : entity.getAddress().getZone().getId(),
                    entity.getCustomer().getId()});

        }
        jdbcTemplate.batchUpdate("insert into restaurant(id, address, latitude, longitude, create_time, " +
                        "license, name, receiver, status, telephone, type, validated, zone_id, " +
                        "customer_id) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                batchArgs);
    }

}
