package com.mishu.cgwy.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static int dayIntervalMilli = 24 * 60 * 60 * 1000;

	public static Integer getIntervalDays(Date start, Date end) {

		long intervalMilli = end.getTime() - start.getTime();

		return (int)(intervalMilli / dayIntervalMilli);

	}

}
