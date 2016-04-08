package com.mishu.cgwy.push.wrapper;

import com.mishu.cgwy.push.domain.DailyPush;
import lombok.Data;

@Data
public class DailyPushWrapper {

    private Long id;
    private String tag;
    private String message;

    public DailyPushWrapper() {
    }

    public DailyPushWrapper(DailyPush push) {
        this.id = push.getId();
        this.tag = push.getTag();
        this.message = push.getMessage();
    }
}
