package com.mishu.cgwy.admin.dto;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/8/31.
 */
@Data
public class AdminUserQueryResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<AdminUserVo> adminUsers = new ArrayList<>();
}
