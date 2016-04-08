package com.mishu.cgwy.etl;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaicheng on 3/31/15.
 */
@Service
public class ProductEtl {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate legacyJdbcTemplate;

    @Transactional
    public void transfer() {
        final List<Map<String, Object>> productList = legacyJdbcTemplate.queryForList(""
        		+ "select A.id,  A. `name`, A.product_number, A.bar_code,	A.brand_id,	 "
        		+ "A.file_id,	A.category_d3_id,	A.create_at,	A.basic_unit,	A.net_weight,	"
        		+ "A.specification,	A.quality_limit,	A.create_permit,	A.exec_permit,	A.create_company,	"
        		+ "A.save_condition,	A.ingredient,	A.big_weight,	B.property from product A "
                + "left join product_purchase B ON A.id = B.product_id");
        final List<Integer> fileList = legacyJdbcTemplate.queryForList("select id from file", Integer.class);

        final List<Map<String, Object>> brandlist = legacyJdbcTemplate.queryForList("select value, show_value from dict_value where key_id = 5");
        final List<Integer> newbrandList = new ArrayList<Integer>();
        for(Map<String, Object> brand : brandlist){
        	Object show_value = brand.get("show_value");
        	if(null != show_value && StringUtils.isNotBlank(show_value.toString())){
        		newbrandList.add((Integer)brand.get("value"));
        	}
        }
        
        for (Map<String, Object> p : productList) {
            Integer id = (Integer) p.get("file_id");
            if (!fileList.contains(id)) {
                p.put("file_id", null);
            }
            Integer brand_id = (Integer)p.get("brand_id");
            if(!newbrandList.contains(brand_id)){
            	p.put("brand_id", null);
            }
        }

        List<Object[]> batchArgs = new ArrayList<Object[]>();
        for (Map<String, Object> entity : productList) {

            Map<String, String> map = new HashMap<>();
            map.put("origin", String.valueOf(entity.get("create_at")));
            map.put("unit", String.valueOf(entity.get("basic_unit")));
            map.put("net_weight", String.valueOf(entity.get("net_weight")));
            map.put("specification", String.valueOf(entity.get("specification")));
            map.put("shelf_life", String.valueOf(entity.get("quality_limit")));
            map.put("licence", String.valueOf(entity.get("create_permit")));
            map.put("executive_standard", String.valueOf(entity.get("exec_permit")));
            map.put("create_company", String.valueOf(entity.get("create_company")));
            map.put("save_condition", String.valueOf(entity.get("save_condition")));
            map.put("ingredient", String.valueOf(entity.get("ingredient")));
            map.put("gross_wight", String.valueOf(entity.get("big_weight")));

            JSONObject jsonObject = new JSONObject(map);
            Object[] args = new Object[]{entity.get("id"), entity.get("name"), 1,
                    entity.get("bar_code"), entity.get("brand_id"), entity.get("file_id"),
                    entity.get("category_d3_id"), Integer.valueOf(2).equals(entity.get("property")) ? false: true ,jsonObject.toString()};
            batchArgs.add(args);

        }
        jdbcTemplate.batchUpdate("insert into product(id, name, capacity_in_bundle, " +
                "bar_code, brand_id, media_file_id, category_id,discrete,properties) " +
                "values(? , ? , ? , ? , ? , ? , ?, ?, ?)", batchArgs);

    }


}
