package com.mishu.cgwy.product.wrapper;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.domain.Sku;

import com.mishu.cgwy.product.domain.SkuTag;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 11:08 PM
 */
@Deprecated
@Data
public class SkuWrapper {
    private Long id;

    private Long productId;

    private String name;

    private BrandWrapper brand;

    private CategoryWrapper category;

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


    private BundleDynamicSkuPriceStatusWrapper bundlePrice;
    private SingleDynamicSkuPriceStatusWrapper singlePrice;
    private long saleAmount = 0;

    private OrganizationVo organization;

    private List<SimpleSkuTagWrapper> skuTags = new ArrayList<>();

    @Deprecated
    private String searchKeyword;

    public SkuWrapper() {

    }

    public SkuWrapper(Sku sku) {
        this.id = sku.getId();


        final Product product = sku.getProduct();
        this.name = sku.getName();
        this.productId = product.getId();
        this.brand = product.getBrand() == null ? null : new BrandWrapper(product.getBrand());

        this.status = SkuStatus.fromInt(sku.getStatus());

        List<MediaFile> mediaFiles = sku.getProduct().getMediaFiles();
        if (!mediaFiles.isEmpty()) {
            String postfix = "?imageView2/0/h/250/format/jpg";
            for(MediaFile mediaFile : mediaFiles) {
                mediaFileUrls.add(mediaFile.getUrl() + postfix);
            }
        }

        this.category = product.getCategory() == null ? null : new CategoryWrapper(product.getCategory());

        this.marketPrice = sku.getMarketPrice();
        this.capacityInBundle = sku.getCapacityInBundle();
        this.singleGross_wight = sku.getSingleGross_wight();
        this.singleHeight = sku.getSingleHeight();
        this.singleLong = sku.getSingleLong();
        this.singleWidth = sku.getSingleWidth();
        this.singleNet_weight = sku.getSingleNet_weight();
        this.singleUnit = sku.getSingleUnit();

        this.bundleGross_wight = sku.getBundleGross_wight();
        this.bundleHeight = sku.getBundleHeight();
        this.bundleLong = sku.getBundleLong();
        this.bundleWidth = sku.getBundleWidth();
        this.bundleNet_weight = sku.getBundleNet_weight();
        this.bundleUnit = sku.getBundleUnit();

        this.marketPrice = sku.getMarketPrice();
        this.rate = sku.getRate();
        this.status = SkuStatus.fromInt(sku.getStatus());

        Organization organizationEntity = sku.getProduct().getOrganization();
        organization = new OrganizationVo();
        organization.setId(organizationEntity.getId());
        organization.setName(organizationEntity.getName());
        organization.setCreateDate(organizationEntity.getCreateDate());
        organization.setEnabled(organizationEntity.isEnabled());
        organization.setTelephone(organizationEntity.getTelephone());

        for (SkuTag skuTag : sku.getSkuTags()) {
            skuTags.add(new SimpleSkuTagWrapper(skuTag));
        }
    }

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
