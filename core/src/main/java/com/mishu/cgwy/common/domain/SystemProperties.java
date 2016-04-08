package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 5/15/15
 * Time: 5:35 PM
 */
@Entity
@Data
public class SystemProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "text")
    private String value;
}
