package com.mocker.utils;

import java.util.Objects;

import static com.mocker.utils.Functions.comparator;

public class Triple<First, Second, Third> {
    public First first;
    public Second second;
    public Third third;

    public Triple(First f, Second s, Third t){
        first = f;
        second = s;
        third = t;
    }

    public Triple(){
        first = null;
        second = null;
        third = null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Triple<?,?,?>){
            Triple<?, ?, ?> cobj = (Triple<?, ?, ?>) obj;
            Boolean f = comparator(first, cobj.first);
            Boolean s = comparator(second, cobj.second);
            Boolean t = comparator(third, cobj.third);
            return f&&s&&t;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
