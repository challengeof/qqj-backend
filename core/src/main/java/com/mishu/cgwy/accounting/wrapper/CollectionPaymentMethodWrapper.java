package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import lombok.Data;

@Data
public class CollectionPaymentMethodWrapper {

    private Long id;

    private String code;

    private String name;

    private Long cityId;

    private String cityName;

    private boolean cash;

    private boolean valid;

    public CollectionPaymentMethodWrapper(CollectionPaymentMethod collectionPaymentMethod) {
        this.id = collectionPaymentMethod.getId();
        this.code = collectionPaymentMethod.getCode();
        this.name = collectionPaymentMethod.getName();
        this.cityId = collectionPaymentMethod.getCity().getId();
        this.cityName = collectionPaymentMethod.getCity().getName();
        this.cash = collectionPaymentMethod.isCash();
        this.valid = collectionPaymentMethod.isValid();
    }

}
