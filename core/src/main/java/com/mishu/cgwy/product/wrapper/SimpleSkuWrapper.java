package com.mishu.cgwy.product.wrapper;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
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
@Deprecated
@Data
public class SimpleSkuWrapper {
    private Long id;

    private String name;

    private BrandWrapper brand;

    private SkuStatus status;

    private BigDecimal marketPrice;

    private BigDecimal salePrice;

    private List<String> mediaFileUrls = new ArrayList<>();

    private int capacityInBundle = 1;

    private boolean bundle;
    
    private boolean active = true;

    private String searchKeyword;

    private OrganizationVo organization;

    private VendorVo vendor;

    private String specification;//规格

    private String singleUnit;

    private String bundleUnit;

    public SimpleSkuWrapper() {

    }

    public SimpleSkuWrapper(Sku sku) {
        this.id = sku.getId();

        final Product product = sku.getProduct();
        this.name = sku.getName();
        this.brand = product.getBrand() == null ? null : new BrandWrapper(product.getBrand());
        this.status = SkuStatus.fromInt(sku.getStatus());
        this.marketPrice = sku.getMarketPrice();
        this.capacityInBundle = sku.getCapacityInBundle();

        List<MediaFile> mediaFiles = sku.getProduct().getMediaFiles();
        if (!mediaFiles.isEmpty()) {
            for(MediaFile mediaFile : mediaFiles) {
                mediaFileUrls.add(mediaFile.getUrl());
            }
        }

        Organization organizationEntity = sku.getProduct().getOrganization();
        organization = new OrganizationVo();
        organization.setId(organizationEntity.getId());
        organization.setName(organizationEntity.getName());
        organization.setCreateDate(organizationEntity.getCreateDate());
        organization.setEnabled(organizationEntity.isEnabled());
        organization.setTelephone(organizationEntity.getTelephone());

        this.specification = sku.getProduct().getSpecification();
        this.singleUnit = sku.getSingleUnit();
        this.bundleUnit = sku.getBundleUnit();
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
