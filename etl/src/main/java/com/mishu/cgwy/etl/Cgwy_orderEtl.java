package com.mishu.cgwy.etl;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class Cgwy_orderEtl {

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Transactional
	public void updateOrderSequence(){
		String sql = "select id, restaurant_id, submit_date from cgwy_order";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> entity : list) {
			String sql2 = "select count(1) from cgwy_order A, (select B.submit_date, B.restaurant_id from cgwy_order B where B.id = ?) C "
					+ "where A.submit_date < C.submit_date and A.restaurant_id = C.restaurant_id";
			int count = jdbcTemplate.queryForInt(sql2, entity.get("id"));
            jdbcTemplate.update("update cgwy_order set sequence = ? where id = ? ", count + 1, entity.get("id"));
        }
	} 
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/application-persist.xml",
        "/application-context.xml"});
		Cgwy_orderEtl bean = applicationContext.getBean(Cgwy_orderEtl.class);
		bean.updateOrderSequence();
		
	}
	
	
	
}
