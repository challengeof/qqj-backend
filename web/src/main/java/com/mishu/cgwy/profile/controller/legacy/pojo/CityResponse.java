package com.mishu.cgwy.profile.controller.legacy.pojo;

import com.mishu.cgwy.common.wrapper.CityWrapper;
import com.mishu.cgwy.error.RestError;

import java.util.ArrayList;
import java.util.List;

public class CityResponse extends RestError {
    private List<CityWrapper> regions = new ArrayList<CityWrapper>();

    public void setRegions(List<CityWrapper> regions) {
        this.regions = regions;
    }

    public List<CityWrapper> getRegions() {
        return regions;
    }

}
