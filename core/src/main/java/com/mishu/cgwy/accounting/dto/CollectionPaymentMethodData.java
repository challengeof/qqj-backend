package com.mishu.cgwy.accounting.dto;

import lombok.Data;

/**
 * Created by admin on 10/8/15.
 */
@Data
public class CollectionPaymentMethodData {

    private Long id;

    private String code;

    private String name;

    private boolean cash;

    private boolean valid;

    private Long cityId;
}
