package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.Suggestion;
import lombok.Data;

import java.util.Date;

/**
 * User: xudong
 * Date: 7/13/15
 * Time: 8:19 PM
 */
@Data
public class SuggestionWrapper {

    private String cityName;
    private String remark;
    private Long restaurantId;
    private String restaurantName;
    private String receiverName;
    private String telephone;
    private Date createTime;

    public SuggestionWrapper() {

    }

    public SuggestionWrapper(Suggestion suggestion) {
        cityName = suggestion.getRestaurant().getCustomer().getCity().getName();
        remark = suggestion.getRemark();
        restaurantId = suggestion.getRestaurant().getId();
        restaurantName = suggestion.getRestaurant().getName();
        receiverName = suggestion.getRestaurant().getReceiver();
        telephone = suggestion.getRestaurant().getTelephone();
        createTime = suggestion.getCreateTime();
    }
}
