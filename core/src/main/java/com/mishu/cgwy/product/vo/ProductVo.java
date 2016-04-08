package com.mishu.cgwy.product.vo;

import com.mishu.cgwy.common.vo.MediaFileVo;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** admin
 * User: xudong
 * Date: 3/5/15
 * Time: 11:00 PM
 */
@Data
public class ProductVo {
    private Long id;

    private String name;

    private BrandVo brand;

    private CategoryVo category;

    private List<MediaFileVo> mediaFiles = new ArrayList<>();

    private List<SkuVo> skus = new ArrayList<>();

    private String barCode;

    private Map<String,String> properties = new HashMap<>();

    private String details;

    private String specification;//规格

    private Integer shelfLife;   //保质期

    private boolean discrete;

    private OrganizationVo organization;
}
