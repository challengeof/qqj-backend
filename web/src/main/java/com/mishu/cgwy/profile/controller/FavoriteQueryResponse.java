package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.wrapper.FavoriteWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 5/22/15
 * Time: 11:47 AM
 */
@Data
@EqualsAndHashCode
public class FavoriteQueryResponse extends RestError {
    private List<FavoriteWrapper> favorites = new ArrayList<>();
}
