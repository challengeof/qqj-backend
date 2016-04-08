package com.mishu.cgwy.profile.domain;

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
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(columnDefinition = "text")
    private String remark;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

}
