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

        SomeClass someClass = Mocker.mock(SomeClass.class);

        Mocker.when(SomeClass.staticStringReturnMethod("123", 123)).thenReturn("CHLENY");
        System.out.println(SomeClass.staticStringReturnMethod("123", 123));

        // find a reference to the class and method you wish to inject
/*        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(SomeClass.class.getCanonicalName());
        ctClass.stopPruning(true);

        // javaassist freezes methods if their bytecode is saved
        // defrost so we can still make changes.
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }

        CtMethod method = ctClass.getDeclaredMethod("testPrint"); // populate this from ctClass however you wish

        CtMethod[] methods = ctClass.getDeclaredMethods(); //get static - accessFlag - 1001 (9)

        CtClass[] params = method.getParameterTypes();

        ctClass.removeMethod(method);
        method.setBody("{" + Mocker.class.getCanonicalName() +".test(); return 123;}");
        ctClass.addMethod(method);

        byte[] bytecode = ctClass.toBytecode();

        ClassDefinition definition = new ClassDefinition(Class.forName(SomeClass.class.getCanonicalName()), bytecode);
        RedefineClassAgent.redefineClasses(definition);


        SomeClass.testPrint("123");*/
    }
}

//TODO: убрать в отдельный Core статики (синглтоном),
// Реализовать нормальный синглтон у Мокера (возможно не надо, смотреть поведения мокито),
// Эксепшны у статиков,
// Придумать че делать с парами в статике (возможно триплет ввести, возможно отдельные классы)

