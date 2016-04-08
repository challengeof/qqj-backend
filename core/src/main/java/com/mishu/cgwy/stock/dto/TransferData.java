package com.mishu.cgwy.stock.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TransferData {

    private Long id;

    private Long cityId;

    private Long sourceDepotId;

    private Long targetDepotId;

    private String remark;

    private List<TransferItemData> items;

    private Boolean approvalResult;

    private String opinion;
}
