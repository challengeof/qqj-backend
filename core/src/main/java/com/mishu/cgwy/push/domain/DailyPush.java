package com.mishu.cgwy.push.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class DailyPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tag;

    private String message;

}
