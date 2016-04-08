package com.mishu.cgwy.operating.skipe.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.wrapper.WarehouseWrapper;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import lombok.Data;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/1/8.
 */
@Data
public class SpikeAddRequest {
    private Long cityId;
//    private Long warehouseId;
    private String startTime;
    private String endTime;
    private String description;

    private List<SpikeItemAddRequest> items;


    public static Spike toSpike(SpikeAddRequest request, AdminUser operater) throws ParseException {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Spike spike = new Spike();
//        Warehouse warehouse = new Warehouse();
//        warehouse.setId(request.getWarehouseId());
//        spike.setWarehouse(warehouse);
        City city = new City();
        city.setId(request.getCityId());

        spike.setCity(city);
        spike.setBeginTime(sdf.parse(request.getStartTime()));
        spike.setEndTime(sdf.parse(request.getEndTime()));
        spike.setCreateTime(new Date());
        spike.setDescription(request.getDescription());
        spike.setOperater(operater);

        return spike;
    }

}
