package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.vo.SkuVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** admin
 * User: xudong
 * Date: 3/5/15
 * Time: 11:00 PM
 */
@Deprecated
@Data
public class ProductWrapper {
    private Long id;

    private String name;

    private BrandWrapper brand;

    private CategoryWrapper category;

    private List<MediaFileWrapper> mediaFiles = new ArrayList<>();

//    private String description;

    private List<SkuWrapper> skus = new ArrayList<>();

    private String barCode;

    private Map<String,String> properties;

    private String details;

    private String specification;//规格

    private Integer shelfLife;   //保质期
    
    private boolean discrete;

    private OrganizationVo organization;
    
    public ProductWrapper() {

    }

    public ProductWrapper(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand() == null ? null : new BrandWrapper(product.getBrand());
        this.category = product.getCategory() == null ? null : new CategoryWrapper(product.getCategory());
        if (!product.getMediaFiles().isEmpty()) {
            for (MediaFile mediaFile : product.getMediaFiles()) {
                mediaFiles.add(new MediaFileWrapper(mediaFile));
            }
        }

//        this.description = product.getDescription();
        this.shelfLife = product.getShelfLife();
        this.specification = product.getSpecification();

        skus = new ArrayList<>();

        for (Sku sku : product.getSkus()) {
            skus.add(new SkuWrapper(sku));
        }

        this.barCode = product.getBarCode();
        this.properties = product.getPropertyMap();
        this.details=product.getDetails();

        this.discrete = product.isDiscrete();

        Organization organizationEntity = product.getOrganization();
        if (organizationEntity != null) {
            organization = new OrganizationVo();
            organization.setId(organizationEntity.getId());
            organization.setName(organizationEntity.getName());
            organization.setCreateDate(organizationEntity.getCreateDate());
            organization.setEnabled(organizationEntity.isEnabled());
            organization.setTelephone(organizationEntity.getTelephone());
        }
    }
}
