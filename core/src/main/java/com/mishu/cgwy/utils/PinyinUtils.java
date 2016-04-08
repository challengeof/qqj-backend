package com.mishu.cgwy.utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * Created by wangguodong on 15/11/3.
 */
public class PinyinUtils {
    public static String toPinyin(String s) {
        String str = "[？]|[?]|、|\\\\|“|”|'|\"|/|‘|’|<|>|[*]|[|]|:|;|：";
        s = s.replaceAll(str, "_");
        return PinyinHelper.convertToPinyinString(s, "_", PinyinFormat.WITHOUT_TONE);
    }
}
