package com.mishu.cgwy.device.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class DeviceMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String platform;

    private String deviceId;
}
