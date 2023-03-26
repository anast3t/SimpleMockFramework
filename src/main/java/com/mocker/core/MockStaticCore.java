package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.ActionType;
import com.mocker.utils.Functions;
import com.mocker.utils.Pair;
import com.mocker.utils.Triple;
import javassist.*;

import java.lang.instrument.ClassDefinition;
import java.util.*;

public class MockStaticCore {

    private static final Map<
            Triple<Class<?>, String, ArrayList<Object>>,
            Pair<Object, ActionType>
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

                String callString = "" +
                        "%s returnValue = %s#upraiseStaticMethod(%s.class, \"%s\", %s.class, $args);" +
                        "if(((%s) returnValue) == null){" +
                        "return (%s) null;" +
                        "}" +
                        "if(((%s)((%s) returnValue).right).equals(%s.THROW)){" +
                        "throw (Throwable) ((%s) returnValue).left;" +
                        "}" +
                        "else if(((%s)((%s) returnValue).right).equals(%s.RETURN)){" +
                        "return (%s) ((%s) returnValue).left;" +
                        "}" +
                        "else if(((%s)((%s) returnValue).right).equals(%s.NULL)){" +
                        "return (%s) null;" +
                        "}" +
                        "else if(((%s)((%s) returnValue).right).equals(%s.IMPLEMENTED)){" +
//                        "System.out.println(\"Running implemented\");"+
                        "}" +
                        ""; //TODO: rework on switch case

                String pairCN = Pair.class.getCanonicalName();
                String objectCN = Object.class.getCanonicalName();
                String mockStaticCoreCN = MockStaticCore.class.getCanonicalName();
                String boolCN = Boolean.class.getCanonicalName();
                String actionCN = ActionType.class.getCanonicalName();
                String methodRTCN = method.getReturnType().getName();

                String body = String.format(callString,
                        objectCN, //call
                        mockStaticCoreCN,
                        mocking.getCanonicalName(),
                        method.getMethodInfo().getName(),
                        pairCN,

                        pairCN, //if null
                        methodRTCN,

                        actionCN, //THROW
                        pairCN,
                        actionCN,
                        pairCN,

                        actionCN, //RETURN
                        pairCN,
                        actionCN,
                        methodRTCN,
                        pairCN,

                        actionCN, //NULL
                        pairCN,
                        actionCN,
                        methodRTCN,

                        actionCN, //IMPLEMENTED
                        pairCN,
                        actionCN
                );

//                System.out.println(body);

//                method.setBody(body);

                method.insertBefore(body);

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

        Pair<Object, ActionType> returnValue = classMap
                .entrySet()
                .stream()
                .filter(el -> el.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);

        return returnValue;
    }

    public static void addReturnAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Object returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, ActionType.RETURN));
    }

    public static void addExceptionAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Throwable returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, ActionType.THROW));
    }

    public static void addNullAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs){
        classMap.put(classMethodArgs, new Pair<>(null, ActionType.NULL));
    }

    public static void addImplementedAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs){
        classMap.put(classMethodArgs, new Pair<>(null, ActionType.IMPLEMENTED));
    }

}
