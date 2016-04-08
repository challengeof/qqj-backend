package com.mishu.cgwy.product.vo;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.mishu.cgwy.product.domain.SkuStatus;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 11:08 PM
 */
@Data
public class SkuVo {
    private Long id;

    private Long productId;

    private String name;

    private ProductVo product;

    private BrandVo brand;

    private CategoryVo category;

    private List<String> mediaFileUrls = new ArrayList<>();

    private int capacityInBundle = 1;

    private BigDecimal marketPrice;

    private BigDecimal rate;
    private SkuStatus status;

    private String singleUnit;
    private BigDecimal singleGross_wight;  //毛重
    private BigDecimal singleNet_weight; //净重
    private BigDecimal singleLong;
    private BigDecimal singleWidth;
    private BigDecimal singleHeight;

    private String bundleUnit;
    private BigDecimal bundleGross_wight;  //毛重
    private BigDecimal bundleNet_weight; //净重
    private BigDecimal bundleLong;
    private BigDecimal bundleWidth;
    private BigDecimal bundleHeight;


    private BundleDynamicSkuPriceStatusVo bundlePrice;
    private SingleDynamicSkuPriceStatusVo singlePrice;
    private long saleAmount = 0;

//    private SimpleOrganizationWrapper organization;

    private List<SkuTagVo> skuTags = new ArrayList<>();

    @Deprecated
    private String searchKeyword;
    /**
     * for js search
     * @return
     */
    public String getSearchKeyword() {
    	if(StringUtils.isNotBlank(name)) {
    		return name + " " + PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
    	}
    	return null;
    };
}
