package com.mishu.cgwy.banner.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bowen on 15-5-25.
 */
@Entity
@Data
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    private String description;

    private String rule;

    private String content;

    private String welcomeMessage;

    private String shoppingTip;

    private Integer orderValue;
}
