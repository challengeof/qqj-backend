package com.mishu.cgwy.common.controller;

import lombok.Data;

@Data
public class VersionUpdateData {

    private Long id;
    private int versionCode;
    private String versionName;
    private String comment;
    private int type;
    private int forceUpdate;
    private String filePath;
}
