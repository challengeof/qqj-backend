package com.mishu.cgwy.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingdong on 15/7/14.
 */
@Data
public class TreeJsonHasChild {
    private String id;

    private String text;

    private int status;

    private List<TreeJsonHasChild> children = new ArrayList<>();

}
