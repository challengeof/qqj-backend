package com.mishu.cgwy.banner.controller;

import com.mishu.cgwy.banner.dto.Message;
import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 15-7-29.
 */
@Data
public class PushRequest {

    private Long cityId;

    private Long warehouseId;

    private String description;

    private Date start;

    private Date end;

    private String shoppingTip;

    private Message message;

}
