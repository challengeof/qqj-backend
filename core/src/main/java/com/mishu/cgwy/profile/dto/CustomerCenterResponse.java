package com.mishu.cgwy.profile.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.wrapper.CustomerHintWrapper;

import java.util.ArrayList;
import java.util.List;

public class CustomerCenterResponse extends RestError {
    private String username;
    private int restaurantCount;
    private String adminName;
    private String telephone;
    private Long adminId;
    private Integer scoreCount = 0;
    private List<CustomerHintWrapper> hintList = new ArrayList<CustomerHintWrapper>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRestaurantCount() {
        return restaurantCount;
    }

    public void setRestaurantCount(int restaurantCount) {
        this.restaurantCount = restaurantCount;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public List<CustomerHintWrapper> getHintList() {
        return hintList;
    }

    public void setHintList(List<CustomerHintWrapper> hintList) {
        this.hintList = hintList;
    }


    public Integer getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(Integer scoreCount) {
        this.scoreCount = scoreCount;
    }
}
