package org.example;

import com.mocker.Mocker;
import com.mocker.core.MockCoreInstance;
import javassist.*;
import javassist.bytecode.ClassFile;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, NotFoundException, CannotCompileException, NoSuchMethodException, IOException, InstantiationException, ClassNotFoundException, UnmodifiableClassException, RedefineClassAgent.FailedToLoadAgentException {
        // find a reference to the class and method you wish to inject
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(SomeClass.class.getCanonicalName());
        ctClass.stopPruning(true);

        // javaassist freezes methods if their bytecode is saved
        // defrost so we can still make changes.
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }

        CtMethod method = ctClass.getDeclaredMethod("testPrint"); // populate this from ctClass however you wish

        method.insertBefore("{ System.out.println(\"Wheeeeee!\"); }");
        byte[] bytecode = ctClass.toBytecode();

        ClassDefinition definition = new ClassDefinition(Class.forName(SomeClass.class.getCanonicalName()), bytecode);
        RedefineClassAgent.redefineClasses(definition);

        SomeClass.testPrint();
    }
}

