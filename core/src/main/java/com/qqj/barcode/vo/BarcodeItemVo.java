package com.qqj.barcode.vo;

import lombok.Data;

/**
 * Created by bowen on 16/4/26.
 */
@Data
public class BarcodeItemVo {

    private Long id;

    private String bagCode;

    private BarcodeVo barcode;
}
