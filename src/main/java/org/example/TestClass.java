package org.example;

import com.mocker.Mocker;
import com.mocker.annotations.Mock;

import javax.management.InstanceNotFoundException;

public class TestClass{
    @Mock
    public SomeClass someClass;

    @Mock
    public static SomeClass someClassStatic;

    public TestClass() throws IllegalAccessException, InstanceNotFoundException {
        Mocker.init(this);
    }

//    public String test () throws Throwable {
//        java.lang.Object returnValue =
//                com.mocker.core.MockStaticCore.upraiseStaticMethod(
//                        org.example.SomeClass.class,
//                        "staticStringReturnMethod",
//                        java.lang.String.class,
//                        $args
//                );
//        if(((com.mocker.utils.Pair) returnValue) == null){
//            return (java.lang.String) null;
//        }
//        switch(((com.mocker.utils.ActionType)((com.mocker.utils.Pair) returnValue).right).toString()){
//            case "THROW":
//                throw (Throwable) ((com.mocker.utils.Pair) returnValue).left;
//            case "RETURN":
//                return (java.lang.String) ((com.mocker.utils.Pair) returnValue).left;
//            case "NULL":
//                return (java.lang.String) null;
//            case "IMPLEMENTED":
//                System.out.println("Running implemented");
//        }
//    }

}