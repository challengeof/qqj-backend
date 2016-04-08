package com.mishu.cgwy.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtils {

	private static NumberFormat numberformat = NumberFormat.getPercentInstance();
	static {
		numberformat.setMaximumIntegerDigits(5);
		numberformat.setMaximumFractionDigits(2);
	}

    public static BigDecimal createBigDecimal(String str) {
    	return StringUtils.isBlank(str) ? null : new BigDecimal(str);
    }
    
    public static int toInt(String str) {
    	return StringUtils.isBlank(str) ? 0 : Integer.parseInt(str);
    }

	public static double doubleVal(BigDecimal value) {
		return value == null ? 0d : value.doubleValue();
	}

	public static int intValue(Integer integer) {
		return integer == null ? 0 : integer.intValue();
	}

    public static BigDecimal devideBigDecimal(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
    	try {
    		return dividend.divide(divisor, scale, roundingMode);
		} catch (ArithmeticException e) {
			return BigDecimal.ONE;
		}
    }
    
    public static String numberFormat(Number number) {
    	return numberformat.format(number);
    }
    
    public static BigDecimal cancelNull(BigDecimal number) {
    	return number == null ? new BigDecimal(0) : number;
    }

	public static String getFormatRemainedRate(BigDecimal number, BigDecimal originalNumber) {
		return numberFormat(getRemainedRate(number, originalNumber));
	}
    
    public static String getFormatRate(BigDecimal number, BigDecimal originalNumber) {
    	return numberFormat(getRate(number, originalNumber));
    }
    
    public static BigDecimal getRemainedRate(BigDecimal number, BigDecimal originalNumber) {
    	return devideBigDecimal(number.subtract(originalNumber), number, 5, RoundingMode.HALF_UP);
    }

	public static BigDecimal getRate(BigDecimal number, BigDecimal originalNumber) {
		return devideBigDecimal(number.subtract(originalNumber), originalNumber, 5, RoundingMode.HALF_UP);
	}
    
    public static BigDecimal getBigDecimalZeroWithScale(int scale) {
    	return new BigDecimal(0).setScale(scale);
    }

	public static boolean isLong(String val){
		try {
			Long.parseLong(val);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
}
