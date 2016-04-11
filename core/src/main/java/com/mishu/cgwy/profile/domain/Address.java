package com.mishu.cgwy.profile.domain;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:28 PM
 */
@Embeddable
@Data
public class Address {

    /*@ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;*/

    private String address; //地址
    private String streetNumber; //门牌号

    @Embedded
    private Wgs84Point wgs84Point;
}
