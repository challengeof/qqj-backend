package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Restaurant;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by king-ck on 2015/10/15.
 */
@Data
public class CallerListQueryWrapper {

    private CallerWrapper caller;

    private Long resId;
    private String resName;
    private String resReceiver;

    public CallerListQueryWrapper( BigInteger id, String detail,String name,String phone , Date createDate, Date modifyDate ,
                                   BigInteger resId, String resName, String resReceiver
                                   ){
//c.id ,c.detail ,c.name ,c.phone ,c.create_date createDate, c.modify_date modifyDate,r.`id` AS resId,r.`name` AS resName,r.`receiver` AS resReceiver

            //( Long	id, String phone, String name, String detail, Date	createDate, Date	modifyDate){
        this.caller=new CallerWrapper(new Caller(id.longValue(),phone,name,detail,createDate,modifyDate));

        if(resId!=null) {
            this.resId = resId.longValue();
            this.resName = resName;
            this.resReceiver = resReceiver;
        }
    }

}
