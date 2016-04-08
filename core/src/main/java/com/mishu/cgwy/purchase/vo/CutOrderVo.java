package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CutOrderVo {

    private Long id;

    private Date cutDate;

    private String administrator;

    private CutOrderStatus status;
}
