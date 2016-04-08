package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xingdong on 15/7/22.
 */
@Service
public class OrderDataUpdateEtl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void transfer() {
        jdbcTemplate.update("update cgwy_order set organization_id = 1");
        List<Map<String,Object>> datas =jdbcTemplate.queryForList("select o.id,vendor.name," +
                "vendor.telephone from cgwy_order o left outer join vendor on vendor.id = o.vendor_id where  o.vendor_id is not null");
        List<Map<String,Object>> organizations = jdbcTemplate.queryForList("select * from vendor where self_support = 1");

        Iterator<Map<String,Object>> iterator = datas.iterator();
        Iterator<Map<String,Object>> organizationIter = organizations.iterator();
        //删除所有的组织
        jdbcTemplate.update("delete from organization where id >3");
        //创建默认的organization
        jdbcTemplate.update("insert into organization(name,create_date,enabled,telephone,city_id) values(?,?,?,?,1)","自营",new Date(),true,"111111111",1);
        while(organizationIter.hasNext()){
            Map<String,Object> obj = organizationIter.next();
            String name = obj.get("name").toString();
            String telephone = obj.get("telephone").toString();

            jdbcTemplate.update("insert into organization(name,create_date,enabled,telephone,city_id) values(?,?,?,?,1)",name,new Date(),true,telephone,1);
        }

        while(iterator.hasNext()){
            Map<String,Object> obj = iterator.next();
            Long id = Long.parseLong(obj.get("id").toString());
            String vendorName = obj.get("name").toString();
            String vendorTele = obj.get("telephone").toString();
        }
    }

}
