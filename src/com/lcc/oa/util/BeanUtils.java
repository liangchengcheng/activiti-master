package com.lcc.oa.util;

import java.util.List;
import java.util.Map;

/**
 * Created by asus on 2017/3/24.
 */
public class BeanUtils {

    public static boolean isBlank(Object obj){
        if(obj == null){
            return true;
        }
        return false;
    }

    public static boolean isBlank(List list){
        if(list == null || list.size()<=0){
            return true;
        }
        return false;
    }

    public static boolean isBlank(Map map){
        if(map == null || map.size()<=0){
            return true;
        }
        return false;
    }

    public static boolean isBlank(Object []obj){
        if(obj == null || obj.length<=0){
            return true;
        }
        return false;
    }
}
