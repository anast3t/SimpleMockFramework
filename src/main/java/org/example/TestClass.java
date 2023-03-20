package org.example;

public class TestClass{
    @Mock
    public SomeClass someClass;

    @Mock
    public static SomeClass someClassStatic;

    public TestClass() throws IllegalAccessException {
        Mocker.init(this);
    }
}