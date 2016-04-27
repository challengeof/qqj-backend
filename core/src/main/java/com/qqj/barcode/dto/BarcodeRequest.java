package com.qqj.barcode.dto;

import com.qqj.admin.domain.AdminUser;
import com.qqj.barcode.domain.BarcodeItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
@Data
public class BarcodeRequest {

    private List<String> barcodeItems = new ArrayList<>();

    private String boxCode;

    private String expressNo;
}
