package com.mishu.cgwy.common.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SystemEmailData {

    private Long id;

    private Long cityId;

    private String name;

    private String sendTo;

    private String sendCc;

    private int type;

    private Set<Long> systemEmailIds;

}
