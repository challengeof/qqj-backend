package com.mishu.cgwy.common.dto;

import com.mishu.cgwy.common.domain.Version;
import lombok.Data;

@Data
public class VersionWrapper {

    private Long id;
    private int versionCode;
    private String versionName;
    private String comment;
    private int forceUpdate;
    private int type;
    private String filePath;

    public VersionWrapper() {
    }

    public VersionWrapper(Version version) {
        id = version.getId();
        versionCode = version.getVersionCode();
        versionName = version.getVersionName();
        comment = version.getComment();
        forceUpdate = version.getForceUpdate();
        type = version.getType();
        filePath = version.getFile().getQiNiuHash();
    }

}
