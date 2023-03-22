package com.mocker;

import com.mocker.annotations.Mock;
import com.mocker.core.IMockRT;
import com.mocker.core.MockCoreInstance;
import com.mocker.core.MockRT;
import com.mocker.core.MockRTS;
import com.mocker.utils.Pair;
import javassist.*;
import org.example.RedefineClassAgent;
import org.example.SomeClass;
import org.example.TestClass;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

//-Djdk.attach.allowAttachSelf=true

public class Mocker {
    private static Object lastCalled;
    public static Pair<Pair<Class<?>, String>, Object[]> lastCalledStatic;
    private enum LastCalledOrder{
        DYNAMIC, STATIC, IDLE
    }
    private static LastCalledOrder lastCalledOrder = LastCalledOrder.IDLE;
    private static Boolean callLock = false;

    private static final Map<Object, MockCoreInstance<?>> instanceMap = new IdentityHashMap<>();

    private static final Map<
            Pair<
                    Pair<Class<?>, String>, // класс - метод
                    Object[] // параметры
                    >,
            Pair<Object, Boolean> // возвращаемые значения (возврат/эксепшн)
            > classMap = new IdentityHashMap<>();

    public static <T> T mock (Class<T> mocking) {
        MockCoreInstance<T> core = new MockCoreInstance<>(mocking);
        T instance = core.getMock();
        instanceMap.put(instance, core);

        defineStatic(mocking);

        return instance;
    }

    private static void defineStatic(Class<?> mocking){

        try{
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

            for(CtMethod method : methods){
//            CtClass[] params = method.getParameterTypes();

                if (method.getModifiers() != 9)
                    continue;

                ctClass.removeMethod(method);

                String callString = "{ return (%s) %s.upraiseStaticMethod(%s.class, \"%s\", %s.class, $args); }";

                String body = String.format(callString,
                        method.getReturnType().getName(),
                        Mocker.class.getCanonicalName(),
                        mocking.getCanonicalName(),
                        method.getMethodInfo().getName(),
                        method.getReturnType().getName());

                System.out.println(body);

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

    public static void init(Object local) throws IllegalAccessException
    {
        Field[] fields =  local.getClass().getFields();
        for (Field f: fields) {
            if (f.getAnnotation(Mock.class) != null){
                Object instance = mock(f.getType());
                f.set(local, instance);
            }
        }
    }

    public static <R> IMockRT<R> when(R smt) {
        if(lastCalledOrder.equals(LastCalledOrder.DYNAMIC)){
            return instanceMap.get(lastCalled).when(smt);
        } else if (lastCalledOrder.equals(LastCalledOrder.STATIC)){
            return new MockRTS<R>(lastCalledStatic);
        } else return null;
//        else throw new InstanceNotFoundException("");

    }



    public static void updateLast(Object o){
        lastCalled = o;
        if(!callLock)
            lastCalledOrder = LastCalledOrder.DYNAMIC;
    } //TODO: protected

    private static void updateLastStatic(Pair<Pair<Class<?>, String>, Object[]> classMethodPair){
        lastCalledStatic = classMethodPair;
        if(!callLock)
            lastCalledOrder = LastCalledOrder.STATIC;
    }

    public static Object upraiseStaticMethod(Class<?> clazz, String methodname, Class<?> returnType, Object[] params) {
        System.out.println("Got uprise in: " + clazz.getName()+"."+methodname);

        Pair<Class<?>, String> classMethodPair = new Pair<>(clazz, methodname);
        Pair<Pair<Class<?>, String>, Object[]> key = new Pair<>(classMethodPair, params);
        updateLastStatic(key);

        Pair<Object, Boolean> returnValue = classMap
                .entrySet()
                .stream()
                .filter(el -> el.getKey().equals(key))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);

        return returnValue == null ? returnType.cast(null) : returnValue.left;

    }

    public static void addReturn(Pair<Pair<Class<?>, String>, Object[]> classMethodPair, Object returnValue){
        classMap.put(new Pair<>(classMethodPair.left, classMethodPair.right), new Pair<>(returnValue, false));
    }


    public static void test(){
        System.out.println("smt");
    }
}
