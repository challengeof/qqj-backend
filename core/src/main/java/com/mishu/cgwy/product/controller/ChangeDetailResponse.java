package com.mishu.cgwy.product.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.vo.ProductVo;
import com.mishu.cgwy.product.vo.SkuVo;
import lombok.Data;

import java.io.IOException;
import java.util.Date;

/**
 * Created by bowen on 15-6-5.
 */
@Data
public class
        ChangeDetailResponse {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private Long id;

    private Long objectId;

    private Long objectType;

    private ProductVo productVo;

    private Long status;

    private String submitter;

    private String verifier;
    
    private SkuVo originBundleSku;
    
    private SkuVo originSku;
    
    private ProductVo originProductVo;

    private OrganizationVo organization;

    private Date submitDate;

    private Date passDate;

    public ChangeDetailResponse() {

    }

    public ChangeDetailResponse(ChangeDetail changeDetail) {
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
        	if (changeDetail.getContent() != null) {
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        		this.productVo = objectMapper.readValue(changeDetail.getContent(), ProductVo.class);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.submitDate = changeDetail.getSubmitDate();
        this.passDate = changeDetail.getPassDate();
    }
}
