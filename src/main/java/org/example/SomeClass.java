package org.example;

import com.mocker.Mocker;

public class SomeClass {
    public void someMethod() {
        System.out.println("Plain method called");
    }

    public String stringReturnMethod(String additiveString){
        return "Henlo from someclass, passed:" + additiveString;
    }

    public Integer integerReturnMethod(Integer val){
        return val+1;
    }

    public static String staticStringReturnMethod(String value, Integer smt){

        return value;
    }

    public static Integer testPrint(){
        return 1;
    }
}
