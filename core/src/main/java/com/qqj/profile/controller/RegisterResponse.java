package com.qqj.profile.controller;

import lombok.Data;

/**
 * User: xudong
 * Date: 6/5/15
 * Time: 4:14 PM
 */
@Data
public class RegisterResponse {
    private Long customerId;
    private boolean zoneActive;

    private boolean inBlock=true;
}
