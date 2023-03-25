package org.example;

import com.mocker.Mocker;
import com.mocker.core.MockCoreInstance;
import javassist.*;
import javassist.bytecode.ClassFile;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws Throwable {

        SomeClass someClass = new SomeClass();

        MockCoreInstance<SomeClass> core = new MockCoreInstance<>(SomeClass.class, someClass);

        someClass = core.getMock();

        core.when(someClass.integerReturnMethod(1)).thenReturn(123);
        System.out.println(someClass.integerReturnMethod (1));

        core.when(someClass.integerReturnMethod (1)).thenNull();
        System.out.println(someClass.integerReturnMethod (1));

        core.when(someClass.integerReturnMethod (1)).thenInitial();
        System.out.println(someClass.integerReturnMethod (1));

//        core.when(someClass.integerReturnMethod ()).thenThrow(new Exception("123"));
//        System.out.println(someClass.integerReturnMethod ());

    }
}

// TODO: Придумать че делать с парами в статике (возможно триплет ввести, возможно отдельные классы),
//  вынести в отдельный метод equals в паре
//  внедрить ActionType
