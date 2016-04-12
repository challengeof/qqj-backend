package com.mishu.cgwy.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wangguodong on 16/4/12.
 */
@Getter
@Setter
public class PageRequest {
    private int page = 0;
    private int pageSize = 100;
}
