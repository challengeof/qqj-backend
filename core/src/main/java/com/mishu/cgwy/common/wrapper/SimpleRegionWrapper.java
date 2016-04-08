package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Region;
import lombok.Data;

@Data
public class SimpleRegionWrapper {
    private Long id;
    private String name;
    private String displayName;

    public SimpleRegionWrapper() {

    }

    public SimpleRegionWrapper(Region region) {
        id = region.getId();
        name = region.getName();
        displayName = region.getDisplayName();
    }


}
