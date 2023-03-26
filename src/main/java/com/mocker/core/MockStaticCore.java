package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.Functions;
import com.mocker.utils.Pair;
import com.mocker.utils.Triple;
import javassist.*;
import org.example.RedefineClassAgent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;

public class MockStaticCore {

    private static final Map<
            Triple<Class<?>, String, ArrayList<Object>>,
            Pair<Object, Boolean>
            > classMap = new HashMap<>();

    public static void defineStatic(Class<?> mocking) {

        try {
            // find a reference to the class and method you wish to inject
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(mocking.getCanonicalName());

            ctClass.stopPruning(true);
            // javassist freezes methods if their bytecode is saved
            // defrost, so we can still make changes.
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            CtMethod[] methods = ctClass.getDeclaredMethods(); //get static - accessFlag - 1001 (9)

            for (CtMethod method : methods) {

                if (method.getModifiers() != 9)
                    continue;

                ctClass.removeMethod(method);

                String callString = "{" +
                        "%s returnValue = %s#upraiseStaticMethod(%s.class, \"%s\", %s.class, $args);" + //4
                        "if(((%s) returnValue) != null){" +
                        "if(((%s)((%s) returnValue).right).equals(java.lang.Boolean.TRUE)){" +
                        "throw (Throwable) ((%s) returnValue).left;" +
                        "}" +
                        "return (%s) ((%s) returnValue).left;" +
                        "}" +
                        "return (%s) null;" +
                        "}";

                String pairCN = Pair.class.getCanonicalName();
                String objectCN = Object.class.getCanonicalName();
                String mockStaticCoreCN = MockStaticCore.class.getCanonicalName();
                String boolCN = Boolean.class.getCanonicalName();

                String body = String.format(callString,
                        objectCN,
                        mockStaticCoreCN,
                        mocking.getCanonicalName(),
                        method.getMethodInfo().getName(),
                        pairCN,
                        pairCN,
                        boolCN,
                        pairCN,
                        pairCN,
                        method.getReturnType().getName(),
                        pairCN,
                        method.getReturnType().getName()
                );

//                System.out.println(body);

                method.setBody(body);

                ctClass.addMethod(method);
            }

            byte[] bytecode = ctClass.toBytecode();

            ClassDefinition definition = new ClassDefinition(Class.forName(mocking.getCanonicalName()), bytecode);
            RedefineClassAgent.redefineClasses(definition);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Object upraiseStaticMethod(Class<?> clazz, String methodname, Class<?> returnType, Object[] params) {
//        System.out.println("Got uprise in: " + clazz.getName() + "." + methodname);

        ArrayList<Object> paramsList = Functions.recArr2ArrListConverter(params);

        Triple<Class<?>, String, ArrayList<Object>> key = new Triple<>(clazz, methodname, paramsList);
        Mocker.updateLastStatic(key);

        Pair<Object, Boolean> returnValue = classMap
                .entrySet()
                .stream()
                .filter(el -> el.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);

        return returnValue;
    }

    public static void addReturn(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Object returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, false));
    }

    public static void addException(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Throwable returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, true));
    }

}
