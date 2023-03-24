package com.mocker;

import com.mocker.annotations.Mock;
import com.mocker.core.*;
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

    private static final Map<Object, MockCoreInstance<?>> instanceMap = new IdentityHashMap<>();

    public static <T> T mock (Class<T> mocking) {
        MockCoreInstance<T> core = new MockCoreInstance<>(mocking);
        T instance = core.getMock();
        instanceMap.put(instance, core);

        MockStaticCoreInstance.defineStatic(mocking);

        return instance;
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

    public static <R> IMockRT<R> when(R smt) throws InstanceNotFoundException {
        if(lastCalledOrder.equals(LastCalledOrder.DYNAMIC)){
            return instanceMap.get(lastCalled).when(smt);
        }else if (lastCalledOrder.equals(LastCalledOrder.STATIC)){
            return new MockRTS<R>(lastCalledStatic);
        } else throw new InstanceNotFoundException("Last call is not initialized");

    }



    public static void updateLast(Object o){
        lastCalled = o;
        lastCalledOrder = LastCalledOrder.DYNAMIC;
    } //TODO: protected

    public static void updateLastStatic(Pair<Pair<Class<?>, String>, Object[]> classMethodPair){
        lastCalledStatic = classMethodPair;
        lastCalledOrder = LastCalledOrder.STATIC;
    }
}
