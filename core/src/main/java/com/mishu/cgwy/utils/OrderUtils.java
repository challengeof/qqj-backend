package com.mishu.cgwy.utils;

import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * User: xudong
 * Date: 6/20/15
 * Time: 5:02 PM
 */
public class OrderUtils {

    public static Date getExpectedArrivedDate(Date submitDate) {
        return DateUtils.addDays(DateUtils.truncate(submitDate, Calendar.DATE), 1);
    }

}
