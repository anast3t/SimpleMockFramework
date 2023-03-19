package org.example;

public class SomeClass {
    public void someMethod() {
        System.out.println("Plain method called");
    }
    @RunIt
    public void annotatedMethod() {
        System.out.println("Annotated method called");
    }

    public String stringReturnMethod(String additiveString){
        return "Henlo from someclass, passed:" + additiveString;
    }
}
