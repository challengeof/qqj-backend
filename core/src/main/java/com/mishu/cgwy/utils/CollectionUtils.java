package com.mishu.cgwy.utils;

import java.util.*;

/**
 * Created by king-ck on 2016/1/14.
 */
public class CollectionUtils {

    /**
     * 去重
     */
    public static <T> Collection<T> filterRepeat(Collection<T> lts, GetRepeatKey<T> grKey){
        if(grKey!=null){
            HashMap hmps = new LinkedHashMap();
            for(T c : lts) {
                hmps.put(grKey.getKey(c),c);
            }
            return hmps.values();
        }else{
            return new LinkedHashSet<>(lts);
        }
    }

    public static interface GetRepeatKey<T>{
        public Object getKey(T src);
    }

}
