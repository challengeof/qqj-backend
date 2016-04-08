package com.mishu.cgwy.order.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateProcessorHandle {
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date) {
        return format.format(date);
    }
}
