package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.CustomerHint;
import lombok.Data;

@Data
public class CustomerHintWrapper {
    private String name;
    private int value;

    public CustomerHintWrapper() {

    }

    public CustomerHintWrapper(CustomerHint customerHint) {
        this.name = customerHint.getName();
        this.value = customerHint.getValue();
    }

}
