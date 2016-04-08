package com.mishu.cgwy.purchase.controller;

import lombok.Data;

import java.util.List;

@Data
public class ReturnNoteData {

    private Long id;

    private Long depotId;

    private Long purchaseOrderId;

    private String remark;

    private List<ReturnNoteItemData> returnNoteItems;

    private Boolean approvalResult;

    private String opinion;
}
