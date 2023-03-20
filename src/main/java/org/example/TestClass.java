package org.example;

import org.example.Mock;
import org.example.Mocker;
import org.example.SomeClass;

class TestClass{
    @Mock
    public SomeClass someClass;

    @Mock
    public static SomeClass someClassStatic;

    TestClass() throws IllegalAccessException {
        Mocker.init(this);
    }
}