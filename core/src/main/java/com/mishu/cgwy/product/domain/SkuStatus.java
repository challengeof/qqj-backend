package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang.StringUtils;

/**
 * User: xudong
 * Date: 3/20/15
 * Time: 11:02 AM
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SkuStatus {
    UNDEFINED(1, "未审核"), ACTIVE(2, "生效"), INACTIVE(3, "失效");

    private Integer value;
    private String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private SkuStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SkuStatus fromInt(int i) {
        switch (i) {
            case 1:
                return UNDEFINED;
            case 2:
                return ACTIVE;
            case 3:
                return INACTIVE;
            default:
                return UNDEFINED;
        }
    }
    
    public static SkuStatus fromValue(String value) {
        if(StringUtils.isNotBlank(value)){
            if(value.equals("未审核")){
                return UNDEFINED;
            }else if(value.equals("生效")){
                return ACTIVE;
            }else if(value.equals("失效")){
                return INACTIVE;
            }else{
                return UNDEFINED;
            }
        }
    	return UNDEFINED;
    }
    @JsonCreator
    public static SkuStatus fromObject(final JsonNode jsonNode) {
        return fromInt(jsonNode.get("value").asInt());
    }
}
