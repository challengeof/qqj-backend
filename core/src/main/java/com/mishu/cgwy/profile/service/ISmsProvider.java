package com.mishu.cgwy.profile.service;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 4:02 PM
 */
public interface ISmsProvider {
    boolean send(String message, String... telephone);
}
