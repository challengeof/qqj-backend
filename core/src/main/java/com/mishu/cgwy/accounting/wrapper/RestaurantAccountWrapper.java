package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.RestaurantAccount;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by admin on 2015/10/11.
 */
@Data
public class RestaurantAccountWrapper {

    private Long id;

    private BigDecimal amount;

    private BigDecimal unWriteoffAmount;

    private String restaurantName;

    private SimpleCityWrapper city;

    public RestaurantAccountWrapper(RestaurantAccount restaurantAccount) {
        this.id = restaurantAccount.getId();
        this.amount = restaurantAccount.getAmount();
        this.unWriteoffAmount = restaurantAccount.getUnWriteoffAmount();
        this.city = new SimpleCityWrapper(restaurantAccount.getRestaurant().getCustomer().getCity());
        this.restaurantName = restaurantAccount.getRestaurant().getName();
    }

}
