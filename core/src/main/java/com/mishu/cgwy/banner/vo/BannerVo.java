package com.mishu.cgwy.banner.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.banner.domain.Banner;
import com.mishu.cgwy.banner.dto.BannerUrl;
import lombok.Data;

import java.io.IOException;
import java.util.Date;

/**
 * Created by bowen on 15-7-29.
 */
@Data
public class BannerVo {

    private Long id;

    private Date start;

    private Date end;

    private String description;

    private String rule;

    private BannerUrl bannerUrl;

    private Long cityId;

    private Long warehouseId;

    private Integer orderValue;

    public BannerVo() {

    }

    public BannerVo(Banner banner) {
        this.id = banner.getId();
        this.start = banner.getStart();
        this.end = banner.getEnd();
        this.description = banner.getDescription();
        this.rule = banner.getRule();
        try {
            this.bannerUrl = new ObjectMapper().readValue(banner.getContent(), BannerUrl.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String [] str = banner.getRule().split("&&");
        String [] str1 = str[0].split("==");
        cityId = Long.valueOf(str1[1]);
        if (str[1].equals("true")) {
            warehouseId = 0L;
        }else{

            String[] str2 = str[1].split("==");
            warehouseId = Long.valueOf(str2[1]);
        }

    }

}
