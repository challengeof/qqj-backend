package com.mishu.cgwy.product.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bowen on 15-6-5.
 */
@Data
public class DynamicPriceTempResponse {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private Long id;

    private Long objectId;

    private Long objectType;

    private DynamicSkuPriceWrapper dynamicSkuPriceWrapper;

    private Long status;

    private String submitter;

    private String verifier;
    
    private DynamicSkuPriceWrapper originDynamicSkuPriceWrapper;

    private SimpleCityWrapper city;

    private OrganizationVo organization;

    private BigDecimal singleSalePriceLimit;

    private BigDecimal bundleSalePriceLimit;

    private BigDecimal fixedPrice;

    private Date submitDate;

    private Date passDate;

    public DynamicPriceTempResponse() {

    }

    public DynamicPriceTempResponse(ChangeDetail changeDetail) {
        this.id = changeDetail.getId();
        this.objectType = changeDetail.getObjectType();
        this.objectId = changeDetail.getObjectId();
        this.status = changeDetail.getStatus();
        if (changeDetail.getSubmitter() != null) {
            this.submitter = changeDetail.getSubmitter().getRealname();
        }
        if (changeDetail.getVerifier() != null) {
            this.verifier = changeDetail.getVerifier().getRealname();
        }
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.dynamicSkuPriceWrapper = objectMapper.readValue(changeDetail.getContent(), DynamicSkuPriceWrapper.class);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.submitDate = changeDetail.getSubmitDate();
        this.passDate = changeDetail.getPassDate();
    }
}
