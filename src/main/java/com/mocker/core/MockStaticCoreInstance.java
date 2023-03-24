package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.Pair;
import javassist.*;
import org.example.RedefineClassAgent;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.IdentityHashMap;
import java.util.Map;

public class MockStaticCoreInstance {

    private static final Map<
            Pair<
                    Pair<Class<?>, String>, // класс - метод
                    Object[] // параметры
                    >,
            Pair<Object, Boolean> // возвращаемые значения (возврат/эксепшн)
            > classMap = new IdentityHashMap<>();

    public static void defineStatic(Class<?> mocking) {

        try {
            // find a reference to the class and method you wish to inject
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(mocking.getCanonicalName());

            ctClass.stopPruning(true);
            // javaassist freezes methods if their bytecode is saved
            // defrost so we can still make changes.
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            CtMethod[] methods = ctClass.getDeclaredMethods(); //get static - accessFlag - 1001 (9)

            for (CtMethod method : methods) {
//            CtClass[] params = method.getParameterTypes();

                if (method.getModifiers() != 9)
                    continue;

                ctClass.removeMethod(method);

                String callString = "{" +
                        " Pair<Object, Boolean> returnValue = %s.upraiseStaticMethod(%s.class, \"%s\", %s.class, $args); " +
                        "if(returnValue != null && returnValue.right){" +
                        "throw (Throwable) returnValue.left;" +
                        "}" +
                        "return (%s) (returnValue != null ? returnValue.left : null);" +
                        "}";

                String body = String.format(callString,
//                        method.getReturnType().getName(),
                        MockStaticCoreInstance.class.getCanonicalName(),
                        mocking.getCanonicalName(),
                        method.getMethodInfo().getName(),
                        method.getReturnType().getName(),
                        method.getReturnType().getName());

//                System.out.println(body);

                method.setBody(body);

                ctClass.addMethod(method);
            }

            byte[] bytecode = ctClass.toBytecode();

            ClassDefinition definition = new ClassDefinition(Class.forName(mocking.getCanonicalName()), bytecode);
            RedefineClassAgent.redefineClasses(definition);

        } catch (UnmodifiableClassException | CannotCompileException | NotFoundException | IOException |
                 RedefineClassAgent.FailedToLoadAgentException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Pair<Object, Boolean> upraiseStaticMethod(Class<?> clazz, String methodname, Class<?> returnType, Object[] params) {
        System.out.println("Got uprise in: " + clazz.getName() + "." + methodname);

        Pair<Class<?>, String> classMethodPair = new Pair<>(clazz, methodname);
        Pair<Pair<Class<?>, String>, Object[]> key = new Pair<>(classMethodPair, params);
        Mocker.updateLastStatic(key);

        Pair<Object, Boolean> returnValue = classMap
                .entrySet()
                .stream()
                .filter(el -> el.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);

        return returnValue;
//        return returnValue == null ? returnType.cast(null) : returnValue.left;

    }

    public static void addReturn(Pair<Pair<Class<?>, String>, Object[]> classMethodPair, Object returnValue) {
        classMap.put(new Pair<>(classMethodPair.left, classMethodPair.right), new Pair<>(returnValue, false));
    }

    public static void addException(Pair<Pair<Class<?>, String>, Object[]> classMethodPair, Throwable returnValue) {
        classMap.put(new Pair<>(classMethodPair.left, classMethodPair.right), new Pair<>(returnValue, true));
    }

}
