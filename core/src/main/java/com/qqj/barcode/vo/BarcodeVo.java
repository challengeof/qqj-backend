package com.qqj.barcode.vo;

import com.qqj.admin.vo.AdminUserVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
@Data
public class BarcodeVo {

    private Long id;

    private Date createTime;

    private List<BarcodeItemVo> barcodeItems = new ArrayList<>();

    private String boxCode;

    private String expressNo;

    private AdminUserVo operator;
}
