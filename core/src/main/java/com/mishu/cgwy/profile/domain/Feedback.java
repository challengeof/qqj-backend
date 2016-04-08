package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.inventory.domain.Vendor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 8:01 PM
 */
@Entity
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String feedbackDescription;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private MediaFile file;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submitTime = new Date();

    private int status = FeedbackStatus.UNPROCESSED.getValue();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    private Short type;
}
