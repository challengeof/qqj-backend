package com.qqj.utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * Created by wangguodong on 15/11/3.
 */
public class StringUtils {
    public static String skipSpecialCharacters(String s) {
        String str = "[？]|[?]|、|\\\\|“|”|'|\"|/|‘|’|<|>|[*]|[|]|:|;|：";
        if (org.apache.commons.lang.StringUtils.isNotBlank(s)) {
            return s.replaceAll(str, "_");
        } else {
            return null;
        }
    }
}
