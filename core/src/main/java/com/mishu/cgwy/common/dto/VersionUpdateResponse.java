package com.mishu.cgwy.common.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by kaicheng on 4/16/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VersionUpdateResponse extends RestError{
    private int versionCode;
    private String versionName;
    private String comment;
    private String url;
    private String md5 = "";
    private int size = 0;
    private String fileName = "";

    private boolean forceUpdate = false;

}
