package com.mocker.utils;

import java.util.Arrays;
import java.util.Objects;

import static com.mocker.utils.Functions.comparator;

public class Pair<Left, Right> {
    public Left left;
    public Right right;

    public Pair(Left left, Right right){
        this.left = left;
        this.right = right;
    }

    public Pair(){
        this.left = null;
        this.right = null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair<?, ?>){
            Boolean l = comparator(left, ((Pair<?, ?>) obj).left);
            Boolean r = comparator(right, ((Pair<?, ?>) obj).right);
            return l&&r;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
