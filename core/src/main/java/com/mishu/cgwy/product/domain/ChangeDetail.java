package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bowen on 15-6-3.
 */
@Entity
@Table(indexes = {@Index(name = "CHANGEDETAIL_OBJECTTYPE_STATUS", columnList = "status, objectType", unique = false)})
@Data
public class ChangeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long objectId;

    private Long objectType;

    @Column(columnDefinition = "text")
    private String content;

    private Long status = Constants.NOT_AUDIT;

    @ManyToOne
    @JoinColumn(name = "submitter_id")
    private AdminUser submitter;

    @ManyToOne
    @JoinColumn(name = "verifier_id")
    private AdminUser verifier;
    
    private String productName;

    private Long organizationId;

    private Long cityId;

    private Long warehouseId;

    private Date submitDate;

    private Date passDate;
}
