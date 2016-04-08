package com.mishu.cgwy.product.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 11:29 AM
 */
@Entity
@Data
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brandName;

    @Deprecated
    private int status;

    @Deprecated
    private Date lastModified;
}
