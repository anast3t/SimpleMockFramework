package com.mocker.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Functions {
    public static Boolean comparator(Object a, Object b){
        if(a == null)
            return b == null;

        else if(a.getClass().isArray() && b.getClass().isArray())
            return Arrays.deepEquals(((Object[]) a), ((Object[]) b));
        else if(a.getClass().isArray() || b.getClass().isArray())
            return false;

        else
            return a.equals(b);
    }

    public static ArrayList<Object> recArr2ArrListConverter(Object[] array){
        ArrayList<Object> result = new ArrayList<>();
        for(Object element : array){
            if(element.getClass().isArray()){
                result.add(recArr2ArrListConverter((Object[]) element));
            }
            else
                result.add(element);
        }
        return result;
    }
}
