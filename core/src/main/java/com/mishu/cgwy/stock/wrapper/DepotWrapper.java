package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

/**
 * Created by wangguodong on 15/9/15.
 */
@Data
public class DepotWrapper {

    private Long id;

    private String name;

    private Boolean isMain;

    private SimpleCityWrapper city;

    private Double latitude;
    private Double longitude;

    public DepotWrapper() {
    }

    public DepotWrapper(Depot depot) {
        this.id = depot.getId();
        this.name = depot.getName();
        this.isMain = depot.getIsMain();
        this.city = new SimpleCityWrapper(depot.getCity());
        this.latitude = depot.getWgs84Point() != null ? depot.getWgs84Point().getLatitude() : null;
        this.longitude = depot.getWgs84Point() != null ? depot.getWgs84Point().getLongitude() : null;
    }
}
