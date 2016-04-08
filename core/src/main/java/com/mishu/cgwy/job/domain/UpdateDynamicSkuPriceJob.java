package com.mishu.cgwy.job.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class UpdateDynamicSkuPriceJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "change_detail_id")
    private ChangeDetail changeDetail;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date effectTime;
}
