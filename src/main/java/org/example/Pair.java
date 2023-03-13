package org.example;

public class Pair<Left, Right> {
    public Left left;
    public Right right;

    Pair(Left left, Right right){
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair<?, ?>){
            return left.equals(((Pair<?, ?>) obj).left) && right.equals(((Pair<?, ?>) obj).right);
        }
        return false;
    }
}
