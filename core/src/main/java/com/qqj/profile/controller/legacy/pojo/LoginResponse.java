package com.qqj.profile.controller.legacy.pojo;

import com.qqj.error.RestError;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 3:34 PM
 */
public class LoginResponse extends RestError {
    private Long userId;
    private String username;
    private String userNumber;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

}
