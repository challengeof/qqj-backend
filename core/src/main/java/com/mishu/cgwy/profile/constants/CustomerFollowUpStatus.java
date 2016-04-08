package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/8.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerFollowUpStatus {
    followUp(1,"跟进中"),
    delayOne(2,"延期一次"),
    delayTwo(3,"延期二次"),
    delayThree(4,"延期三次"),
    abandon(5,"放弃"),
    determine(6,"确定合作"),
    teamed(7,"已合作");

    public final Integer val;
    public final  String desc;

    private CustomerFollowUpStatus(Integer val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static CustomerFollowUpStatus fromInt(Integer val) {
        for (CustomerFollowUpStatus type : CustomerFollowUpStatus.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return null;
    }



}
