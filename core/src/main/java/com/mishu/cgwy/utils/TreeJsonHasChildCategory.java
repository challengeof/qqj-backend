package com.mishu.cgwy.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/12/1.
 */
@Data
@EqualsAndHashCode
public class TreeJsonHasChildCategory extends TreeJsonHasChild {

    private List<Long> cityIds = new ArrayList<>();
}
