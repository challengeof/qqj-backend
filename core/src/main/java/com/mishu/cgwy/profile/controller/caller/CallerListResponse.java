package com.mishu.cgwy.profile.controller.caller;

import com.mishu.cgwy.order.dto.OrderStatistics;
import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.wrapper.CallerListQueryWrapper;
import com.mishu.cgwy.profile.wrapper.CallerWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CallerListResponse {
    private long total=99999;
    private int page;
    private int pageSize;

//    private List<CallerWrapper> callerWrappers;
    private List<CallerListQueryWrapper> queryWrappers;




}
