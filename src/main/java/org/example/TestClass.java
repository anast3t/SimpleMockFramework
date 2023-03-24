package org.example;

import com.mocker.Mocker;
import com.mocker.annotations.Mock;

public class TestClass{
    @Mock
    public SomeClass someClass;

    @Mock
    public static SomeClass someClassStatic;

    public TestClass() throws IllegalAccessException {
        Mocker.init(this);
    }

}