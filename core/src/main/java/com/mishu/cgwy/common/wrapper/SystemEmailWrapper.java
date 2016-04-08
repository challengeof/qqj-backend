package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.SystemEmail;
import com.mishu.cgwy.common.domain.SystemEmailType;
import lombok.Data;

@Data
public class SystemEmailWrapper {
    private Long id;
    private String name;
    private Long cityId;
    private String cityName;
    private int type;
    private String typeName;
    private String sendCc;
    private String sendTo;

    public SystemEmailWrapper() {
    }

    public SystemEmailWrapper(SystemEmail systemEmail) {
        this.id = systemEmail.getId();
        this.name = systemEmail.getName();
        this.sendCc = systemEmail.getSendCc();
        this.sendTo = systemEmail.getSendTo();
        this.type = systemEmail.getType();
        this.typeName = SystemEmailType.fromInt(systemEmail.getType()).getName();
        this.cityId = systemEmail.getCity() != null ? systemEmail.getCity().getId() : null;
        this.cityName = systemEmail.getCity() != null ? systemEmail.getCity().getName() : null;
    }
}
