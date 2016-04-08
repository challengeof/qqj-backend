package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.common.wrapper.SimpleRegionWrapper;
import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegionListResponse extends RestError {
    private List<SimpleRegionWrapper> regions;
}
