package org.example;

import com.mocker.Mocker;

public class SomeClass {
    private Integer someInt;

    public void someMethod() {
        System.out.println("Plain method called");
    }

    public SomeClass(Integer someInt){
        this.someInt = someInt;
    }

    public SomeClass(){
        someInt = 10;
    }

    public void setSomeInt(Integer someInt) {
        this.someInt = someInt;
    }

    public Integer getSomeInt() {
        return someInt;
    }

    public String stringReturnMethod(String additiveString){
        return "Hello from someclass, passed:" + additiveString;
    }

    public Integer integerReturnMethod(Integer val){
        return val+someInt;
    }

    public static String staticStringReturnMethod(String value, Integer smt){
        Integer smt1 = smt + 100;
        return "Got String: " + value + ", and Integer: " + smt1;
    }

    public Integer testPrint(){
        return 1;
    }

    public Integer multiInput(String string, Integer integer, Boolean bool){
        return string.length() + integer + (bool ? 1 : 0);
    }
}
