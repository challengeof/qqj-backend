package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by wangguodong on 15/8/14.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestaurantGrade {
    INVALID(new Short((short)-1), "无效客户"),
    HIGH_GRADE(new Short((short)1), "优质客户"),
    COMMON(new Short((short)2), "普通客户"),
    NEW(new Short((short)3), "新客户");

    private Short grade;

    private String desc;

    public Short getGrade() {
        return grade;
    }

    public String getDesc() {
        return desc;
    }

    private RestaurantGrade(Short grade, String desc) {
        this.grade = grade;
        this.desc = desc;
    }

    public static RestaurantGrade getRestaurantGradeByCode(Short grade) {
        for (RestaurantGrade restaurantGrade : RestaurantGrade.values()) {
            if (restaurantGrade.getGrade().equals(grade)) {
                return restaurantGrade;
            }
        }
        return NEW;
    }
}
