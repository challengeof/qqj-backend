package com.mishu.cgwy.etl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by xingdong on 15/7/22.
 */
@Service
public class AdminUserTransfer {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private AdminPermissionEtl adminPermissionEtl;

    @Transactional
    public void insertCity(){
        //插入另外两个城市
//        jdbcTemplate.update("insert into city(id,name)")
        jdbcTemplate.update("delete from city where id in (2,3)");
        jdbcTemplate.update("insert into city (id,name)values(?,?)",2,"成都");
    }

    public void insertWarehouse() {
        jdbcTemplate.update("delete from warehouse where id > 3");
        jdbcTemplate.update("delete from warehouse where name = \"成都海霸王市场\"");
        jdbcTemplate.update("insert into warehouse (id, `name`, city_id,is_default) values (4, \"成都海霸王市场\", 2,0)");
        jdbcTemplate.update("update warehouse set is_default = 0");
        jdbcTemplate.update("update warehouse set is_default = 1 where id in (1,4)");
    }

    public void insertOrganizationRole(){
       jdbcTemplate.update("delete from admin_role where display_name = \"店中店管理\"");
        jdbcTemplate.update("insert into admin_role " +
                "(`admin_role`.`display_name`, `admin_role`.`name`,`admin_role`.`organization_role`)" +
                " values  (\"店中店管理\",\"OrganizationManage\", 0)");

        jdbcTemplate.update("update admin_role set organization_role = 1 where id = 1 or id >= 4 and id < 14");

    }

    public void insertPermission(){
          jdbcTemplate.update("delete from admin_permission where display_name in (\"店面列表\", \"区块列表\", \"城市列表\", \"市场列表\")");
        jdbcTemplate.update("insert into admin_permission (display_name, `name`) VALUES (\"店面列表\", \"organization-list\")," +
                "(\"区块列表\", \"block-list\"),(\"城市列表\", \"city-list\"),(\"市场列表\", \"warehouse-list\")");
    }


    @Transactional
    public void insertBlock(){
        jdbcTemplate.update("delete from block");

      for(int i = 1;i<4;i++){
          jdbcTemplate.update("insert into block(id,active,name,city_id,warehouse_id) values(?,?,?,?,?)", i, 1, "区块默认" + i, 1, i);
          String sql = "select c.id from customer c,zone,warehouse where c.zone_id = zone.id AND zone.warehouse_id = warehouse.id and warehouse.id = "+i;
          List<Map<String,Object>> customers = jdbcTemplate.queryForList(sql);
          System.out.println(customers.size());
          Iterator<Map<String,Object>> mapIterator = customers.iterator();
          while(mapIterator.hasNext()){
              Map<String,Object> obj = mapIterator.next();
              Long id = Long.parseLong(obj.get("id").toString());
              jdbcTemplate.update("update customer set block_id = "+i+" where id = "+id);
          }
      }

        jdbcTemplate.update("insert into block (id, active, name, warehouse_id, city_id) " +
                "values (4, 1, \"成都\", 4, 2)");
    }
    @Transactional
    public void updateOrder(){
        jdbcTemplate.update("update cgwy_order set organization_id = 1");
        Set<String> organizationNames = new HashSet<>();
        List<Map<String,Object>> organizationInfos = jdbcTemplate.queryForList("select id,name from organization");
        Iterator<Map<String,Object>> mapIterator = organizationInfos.iterator();
        while(mapIterator.hasNext()){
            Map<String,Object> obj = mapIterator.next();
            String name = obj.get("name").toString();
            organizationNames.add(name);
        }
        List<Map<String,Object>> datas =jdbcTemplate.queryForList("select o.id,vendor.name," +
                "vendor.telephone from cgwy_order o left outer join vendor on vendor.id = o.vendor_id where  o.vendor_id is not null");
        Iterator<Map<String,Object>> iterator = datas.iterator();
        while(iterator.hasNext()){
            Map<String,Object> obj =  iterator.next();
            Long id = Long.parseLong(obj.get("id").toString());
            String vendorName = obj.get("name").toString();
            if(organizationNames.contains(vendorName)){
                Long organId = jdbcTemplate.queryForList("select id from organization where name = " + "'"+vendorName+"'", Long.class).get(0);
                jdbcTemplate.update("update cgwy_order set organization_id = "+organId +" where id = "+id);
            }

        }


    }
    @Transactional
    public void transfer() {
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");

        initData();
        insertCity();
        insertWarehouse();
        insertPermission();
        insertOrganizationRole();
        insertBlock();
        createOrganization();
        initZone();
        initVendorAndAgent();
        initProduct();
        initGlobalUser();
        updateOrder();
        updateFinance();
        updateCustomerCity();
        jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Transactional
    private void updateCustomerCity() {
        jdbcTemplate.update("update customer set city_id = 1");
    }

    @Transactional
    public void initZone() {
        jdbcTemplate.update("delete from region where city_id = 2");
        jdbcTemplate.update("delete from zone where region_id = 18");
        jdbcTemplate.update("update zone set city_id = 1");
        jdbcTemplate.update("insert into region values(18,'成都',2)");
        jdbcTemplate.update("insert into zone (active,name,region_id,warehouse_id,city_id) values(1,'成都',18,4,2)");
    }
    @Transactional
    public void initVendorAndAgent(){
        jdbcTemplate.update("update vendor set organization_id = 1");
        jdbcTemplate.update("update agent set organization_id = 1");
    }
    @Transactional
    public void initProduct() {
        jdbcTemplate.update("update product set organization_id = 1");
        jdbcTemplate.update("update change_detail set organization_id = 1, city_id = 1");
    }

    @Transactional
    public void updateFinance() {
        jdbcTemplate.update("update cgwy_order_group set city_id = 1, organization_id = 1");
        jdbcTemplate.update("update agent_finance set organization_id = 1");
        jdbcTemplate.update("update vendor_finance set organization_id = 1");
        jdbcTemplate.update("update cgwy_order_group_finance set organization_id = 1");
    }

    public void createOrganization(){
        List<Map<String,Object>> organizations = jdbcTemplate.queryForList("select * from vendor where self_support = 1");
        Iterator<Map<String,Object>> organizationIter = organizations.iterator();
        jdbcTemplate.update("delete from organization");
        jdbcTemplate.update("insert into organization(id,name,enabled,telephone,city_id,create_date) values(?,?,?,?,?,?)",1,"自营",1,"400888",1,new Date());
        while(organizationIter.hasNext()){
            Map<String,Object> obj = organizationIter.next();
            String name = obj.get("name").toString();
            String telephone = obj.get("telephone").toString();

            jdbcTemplate.update
                    ("insert into organization(name,create_date,enabled,telephone,city_id) values(?,?,?,?,?)",name,new Date(),1,telephone,1);
        }
    }

    @Transactional
    private void initData() {
        jdbcTemplate.update("update product set organization_id = null");
        jdbcTemplate.update("update cgwy_order set organization_id = null");
        jdbcTemplate.update("update cgwy_order_group set organization_id = null");
        jdbcTemplate.update("update vendor set organization_id = null");
        jdbcTemplate.update("update agent set organization_id = null");
        jdbcTemplate.update("update customer set block_id = null");
        jdbcTemplate.update("delete from organization_category_xref");
        jdbcTemplate.update("update admin_user set global_admin = 0");
        jdbcTemplate.update("delete from admin_user_city_xref");
        jdbcTemplate.update("delete from admin_user_block_xref");
        jdbcTemplate.update("delete from admin_user_organization_xref");
        jdbcTemplate.update("delete from organization_block_xref");
        jdbcTemplate.update("delete from organization");
        jdbcTemplate.update("delete from city where id > 1");
        jdbcTemplate.update("delete from block");
    }



    public void initGlobalUser(){
        //查出所有的全局工作人员
        final  List<Map<String, Object>> data = jdbcTemplate.queryForList("select distinct au.id from admin_user au left outer join admin_user_role_xref aurx on " +
                "au.id = aurx.admin_user_id left outer join admin_role ar on  ar.id = aurx.admin_role_id where aurx.admin_role_id  in (1,2,3,7,14)");

        List<Integer> blockIds = jdbcTemplate.queryForList("select id from block", Integer.class);

        final Iterator<Map<String, Object>> iterator = data.iterator();

        final List<Integer> ids = new ArrayList<>();
        while (iterator.hasNext()) {
            Map<String,Object> obj = iterator.next();
            Integer adminUserId = Integer.valueOf(obj.get("id").toString());
            jdbcTemplate.update("update admin_user set global_admin = 1 where id = " + adminUserId);
            jdbcTemplate.update("insert into admin_user_city_xref values(" + adminUserId + ",1)");
            jdbcTemplate.update("insert into admin_user_city_xref values(" + adminUserId + ",2)");

            for(Integer block:blockIds){
                jdbcTemplate.update("insert into admin_user_block_xref values(?,?)",adminUserId,block);
            }
            //分配block给其他城市  TODO

            ids.add(adminUserId);
        }

        List<Integer> lists = jdbcTemplate.queryForList("select id from admin_user ", Integer.class);
        Collection c = Collections2.filter(lists, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return !ids.contains(input);
            }
        });

        Iterator it = c.iterator();
        while(it.hasNext()) {
            Integer adminUserId = (Integer) it.next();
            jdbcTemplate.update("insert into admin_user_city_xref values(?,?)",adminUserId,1);
            jdbcTemplate.update("insert into admin_user_organization_xref values(?,?)",adminUserId,1);
            // 给非全局管理员分配自己的block    可视化工具操作   TODO
        }

    }
}
