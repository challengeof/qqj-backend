package com.mishu.cgwy.workTicket.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by bowen on 16/2/29.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WorkTicketStatus {

    WAITING_PROCESS(1, "等待处理"),
    REPLIED(2, "已回复"),
    SUCCESS(3, "已完成");

    private Integer type;

    private String name;

    private WorkTicketStatus(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static WorkTicketStatus fromInt(int i) {
        switch (i) {
            case 1 :
                return WAITING_PROCESS;
            case 2 :
                return REPLIED;
            case 3 :
                return SUCCESS;
            default:
                return WAITING_PROCESS;
        }
    }

}
