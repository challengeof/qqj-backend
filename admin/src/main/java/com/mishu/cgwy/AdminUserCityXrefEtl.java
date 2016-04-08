package com.mishu.cgwy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xingdong on 15/7/20.
 */
public class AdminUserCityXrefEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void transfer() {
        //将之前的所有数据导入新的组织当中、北京自营店铺当中
//        List<Map<String,Object>> data1 = jdbcTemplate.queryForList("select * from admin_user");

       List<Map<String,Object>> data2 =  jdbcTemplate.queryForList("select * from admin_user_role_xref");

       Iterator<Map<String,Object>> iterator = data2.iterator();
        while(iterator.hasNext()){
            Map<String,Object> data = iterator.next();
            Integer adminId = Integer.valueOf(data.get("admin_user_id").toString());
            Integer roleId = Integer.valueOf(data.get("admin_role_id").toString());




        }

    }
}
