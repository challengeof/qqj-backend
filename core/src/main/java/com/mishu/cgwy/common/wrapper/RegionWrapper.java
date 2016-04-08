package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Region;
import com.mishu.cgwy.common.domain.Zone;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegionWrapper {
    private Long id;
    private String name;
    private String displayName;

    private List<ZoneWrapper> zones = new ArrayList<>();

    public RegionWrapper() {

    }

    public RegionWrapper(Region region) {
        id = region.getId();
        name = region.getName();
        displayName = region.getDisplayName();

        for (Zone zone : region.getZones()) {
            zones.add(new ZoneWrapper(zone));
        }
    }


}
