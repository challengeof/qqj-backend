package com.mishu.cgwy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mishu.cgwy.product.dto.MetadataItem;

/**
 * User: xudong
 * Date: 5/4/15
 * Time: 8:00 PM
 */
public class LegacyProductUtils {

    enum ProductProperty {
    	brand("brand", "品牌"),
    	specification("specification", "规格"),
    	gross_wight("gross_wight", "商品毛重"),
    	unit("unit", "包装"),
    	capacityInBundle("capacityInBundle", "大箱包装"),
    	shelf_life("shelf_life", "保质期"),
    	create_company("create_company", "生产厂商"),
    	licence("licence", "生产许可证"),
    	executive_standard("executive_standard", "产品执行标准"),
    	origin("origin", "产地"),
    	net_weight("net_weight", "净重"),
    	save_condition("save_condition", "保存条件"),
    	ingredient("ingredient", "配料表");
    	
    	private String name;
    	private String value;
    	
    	private ProductProperty(String name, String value){
    		this.name = name;
    		this.value = value;
    	}
    	
    	public String getName(){
    		return name;
    	}
    	
    	public String getValue() {
    		return value;
    	}
    	
    	public ProductProperty getIndex(String name) {
    		for (int i = 0; i < values().length; i ++ ) {
    			if(values()[i].name.endsWith(name)) {
    				return values()[i];
    			}
    		}
    		return null;
    	}
    }



    public static List<MetadataItem> transformMetadataItems(Map<String, String> map) {

        List<MetadataItem> result = new ArrayList<>();
        for(int i = 0; i < ProductProperty.values().length; i ++) {
        	if (map.containsKey(ProductProperty.values()[i].getName()) && StringUtils.isNotBlank(map.get(ProductProperty.values()[i].getName()))) {
        		MetadataItem item = new MetadataItem();
        		item.setName(ProductProperty.values()[i].getValue());
	            item.setValue(map.get(ProductProperty.values()[i].getName()));
	            result.add(item);
        	}
        }

        return result;

    }

}
