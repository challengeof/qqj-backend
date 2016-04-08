package com.mishu.cgwy.banner.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.banner.domain.Push;
import com.mishu.cgwy.banner.dto.Message;
import lombok.Data;

import java.io.IOException;
import java.util.Date;

/**
 * Created by bowen on 15-7-29.
 */
@Data
public class PushVo {

    private Long id;

    private Date start;

    private Date end;

    private String description;

    private String rule;

    private Message message;

    private String shoppingTip;

    private Long cityId;

    private Long warehouseId;

    public PushVo() {

    }

    public PushVo(Push push) {
        this.id = push.getId();
        this.start = push.getStart();
        this.end = push.getEnd();
        this.description = push.getDescription();
        this.rule = push.getRule();
        try {
            this.message = new ObjectMapper().readValue(push.getWelcomeMessage(), Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.shoppingTip = push.getShoppingTip();

        String [] str = push.getRule().split("&&");
        String [] str1 = str[0].split("==");
        cityId = Long.valueOf(str1[1]);
        if (str[1].equals("true")) {
            warehouseId = 0L;
        }else{

            String[] str2 = str[1].split("==");
            warehouseId = Long.valueOf(str2[1]);
        }
    }
}
