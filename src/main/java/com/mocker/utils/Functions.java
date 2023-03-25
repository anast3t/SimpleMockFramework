package com.mocker.utils;

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
}
