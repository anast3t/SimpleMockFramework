package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.ActionType;
import com.mocker.utils.Functions;
import com.mocker.utils.Pair;
import com.mocker.utils.Triple;
import javassist.*;
import org.apache.commons.text.StringSubstitutor;

import java.lang.instrument.ClassDefinition;
import java.util.*;

public class MockStaticCore implements IMockCore<Triple<Class<?>, String, ArrayList<Object>>> {

    private static final MockStaticCore instance = new MockStaticCore();

    private MockStaticCore(){
    }

    public static MockStaticCore getInstance() {
        return instance;
    }

    private final Map<
            Triple<Class<?>, String, ArrayList<Object>>,
            Pair<Object, ActionType>
            > classMap = new HashMap<>();

    public void defineStatic(Class<?> mocking) {
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

                String pairCN = Pair.class.getCanonicalName();
                String objectCN = Object.class.getCanonicalName();
                String mockStaticCoreCN = MockStaticCore.class.getCanonicalName();
                String boolCN = Boolean.class.getCanonicalName();
                String actionCN = ActionType.class.getCanonicalName();
                String methodRTCN = method.getReturnType().getName();
                String mockingCN = mocking.getCanonicalName();
                String methodName = method.getMethodInfo().getName();

                Map<String, String> namesMap = Map.of(
                        "Pair", pairCN,
                        "Object", objectCN,
                        "MSC", mockStaticCoreCN,
                        "ActionType", actionCN,
                        "MethodRT", methodRTCN,
                        "Mocking", mockingCN,
                        "MethodName", methodName
                );

                String callCaseFString = "" +
                        "${Object} returnValue = ${MSC}#getInstance().upraiseStaticMethod(${Mocking}.class, \"${MethodName}\", $args);" +
                        "if(((${Pair}) returnValue) == null){" +
                            "return (${MethodRT}) null;" +
                        "}" +
                        "switch(((${ActionType})((${Pair}) returnValue).right).toString()){" +
                            "case \"THROW\":" +
                                "throw (Throwable) ((${Pair}) returnValue).left;" +
                            "case \"RETURN\":"+
                                "return (${MethodRT}) ((${Pair}) returnValue).left;" +
                            "case \"NULL\":" +
                                "return (${MethodRT}) null;" +
                            "case \"IMPLEMENTED\":" +
                                "break;" +
//                                "System.out.println(\"Running implemented\");"+
                        "}";

                String fBody = new StringSubstitutor(namesMap).replace(callCaseFString);

//                System.out.println(fBody);

                method.insertBefore(fBody);

                ctClass.addMethod(method);
            }

            byte[] bytecode = ctClass.toBytecode();

            ClassDefinition definition = new ClassDefinition(Class.forName(mocking.getCanonicalName()), bytecode);
            RedefineClassAgent.redefineClasses(definition);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Object upraiseStaticMethod(Class<?> clazz, String methodname, Object[] params) { //TODO: any

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

    @Override
    public void addReturnAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Object returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, ActionType.RETURN));
    }

    @Override
    public void addExceptionAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs, Throwable returnValue) {
        classMap.put(classMethodArgs, new Pair<>(returnValue, ActionType.THROW));
    }

    @Override
    public void addNullAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs){
        classMap.put(classMethodArgs, new Pair<>(null, ActionType.NULL));
    }

    @Override
    public void addImplementedAction(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs){
        classMap.put(classMethodArgs, new Pair<>(null, ActionType.IMPLEMENTED));
    }

}
