package com.mishu.cgwy.conf.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: Guodong
 */
@Entity
@Data
public class Conf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;


    private String value;
}
