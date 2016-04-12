package com.mishu.cgwy.org.controller;

import com.mishu.cgwy.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamListRequest extends PageRequest {

    private Long name;

    private Long founder;

    private Long telephone;

}
