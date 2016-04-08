package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.product.domain.Brand;
import com.mishu.cgwy.utils.ValidStatus;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 3/6/15
 * Time: 4:30 PM
 */
@Data
public class BrandVo {

    private Long id;
    private String brandName;
    private String status;
    private Date lastModified;
}
