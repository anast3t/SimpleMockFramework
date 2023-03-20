package org.example;

public class Color {
    static int r;
    static int g;
    static int b;

    public Color(int r_, int g_, int b_) {
        r = r_;
        g = g_;
        b = b_;
    }

    public static String average(int r2, int g2, int b2) {
        int aver = r + r2 + g + g2 + b + b2;
        return "hello from static method :" + aver + "\n";
    }
}
