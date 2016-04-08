package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.product.domain.ChangeDetail;
import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 15-6-4.
 */
@Data
public class ChangeDetailWrapper {

    private Long id;

    private Long objectId;

    private Long objectType;

    private String content;

    private Long status;

    private Date submitDate;

    private Date passDate;

    public ChangeDetailWrapper() {

    }

    public ChangeDetailWrapper(ChangeDetail changeDetail) {

        this.id = changeDetail.getId();
        this.objectId = changeDetail.getObjectId();
        this.objectType = changeDetail.getObjectType();
        this.content = changeDetail.getContent();
        this.status = changeDetail.getStatus();
        this.submitDate = changeDetail.getSubmitDate();
        this.passDate = changeDetail.getPassDate();
    }
}
