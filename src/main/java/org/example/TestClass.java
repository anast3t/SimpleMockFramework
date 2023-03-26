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
//
//    }

}