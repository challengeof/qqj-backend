package com.mishu.cgwy.common.controller;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.FeedbackStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by king-ck on 2016/1/25.
 */
@Data
public class FeedBackListRequest {
    private int page = 0;
    private int pageSize = 100;

    private String sortField = "id";
    private boolean asc = false;

    private Long id;

    private Long customerId;

    private String customerName;

    private Long cityId;

    private Long restaurantId;

    private String restaurantName;

    private Long vendorId;

    private String verdorName;

    private Integer status;

    private Short type;

    private Date submitTimeFront;

    private Date submitTimeBack;

    private Date updateTimeFront;

    private Date updateTimeBack;



}
