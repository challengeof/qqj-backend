package com.mishu.cgwy.profile.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Embeddable;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:41 PM
 */
@Embeddable
@Data
public class Wgs84Point {

    public Wgs84Point(){}

    public Wgs84Point(Double lng, Double lat){
        this.longitude = lng;
        this.latitude = lat;
    }

    private Double latitude;
    private Double longitude;

    public static Wgs84Point fromString(String s) {
        try {
            if (StringUtils.isNotBlank(s)) {
                final String[] tokens = s.split(",");
                Wgs84Point result = new Wgs84Point();
                result.setLongitude(Double.valueOf(tokens[0]));
                result.setLatitude(Double.valueOf(tokens[1]));
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
