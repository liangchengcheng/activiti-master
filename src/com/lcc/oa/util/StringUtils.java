package com.lcc.oa.util;

/**
 * Created by asus on 2017/3/26.
 */
public class StringUtils {

    public static boolean isBlank(String...judgeString){
        for (String str : judgeString){
            if (str == null || "".equals(str)){
                return true;
            }
        }
        return false;
    }
}
