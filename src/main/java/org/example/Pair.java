package org.example;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Pair<Left, Right> {
    public Left left;
    public Right right;

    Pair(Left left, Right right){
        this.left = left;
        this.right = right;
    }

    Pair(){
        this.left = null;
        this.right = null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair<?, ?>){
            boolean l = true;
            if(left.getClass().isArray())
                l = Arrays.deepEquals(((Object[]) left), ((Object[])((Pair<?, ?>) obj).left));
            else
                l = left.equals(((Pair<?, ?>) obj).left);

            boolean r = true;
            if(right.getClass().isArray())
                r = Arrays.deepEquals(((Object[]) right), ((Object[])((Pair<?, ?>) obj).right));
            else
                r = right.equals(((Pair<?, ?>) obj).right);


            return l&&r;
        }
        return false;
    }

    public void flush(){
        this.left = null;
        this.right = null;
    }
}
