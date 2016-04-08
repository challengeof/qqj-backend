package com.mishu.cgwy.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by challenge on 16/1/20.
 */
@Data
@EqualsAndHashCode
public class TreeJsonHasChildRestaurantType extends TreeJsonHasChild {

    private List<Long> cityIds = new ArrayList<>();
}
