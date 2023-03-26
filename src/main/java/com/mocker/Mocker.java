package com.mocker;

import com.mocker.annotations.Mock;
import com.mocker.core.*;
import com.mocker.utils.Pair;
import com.mocker.utils.Triple;

import javax.management.InstanceNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

//-Djdk.attach.allowAttachSelf=true

public class Mocker {
    private static Object lastCalled;
    public static Triple<Class<?>, String, ArrayList<Object>> lastCalledStatic;

    private enum LastCalledOrder {
        DYNAMIC, STATIC, IDLE
    }

    private static LastCalledOrder lastCalledOrder = LastCalledOrder.IDLE;

    private static final Map<Object, MockCoreInstance<?>> instanceMap = new IdentityHashMap<>();

    public static <T> T mock(Class<T> mocking, Object instance) throws ClassCastException, InstanceNotFoundException {
        if (instance == null && !mocking.isInterface())
            throw new InstanceNotFoundException("Instance is null while class is not an interface");
        else if (instance != null) {
            if (!instance.getClass().isAssignableFrom(mocking))
                throw new ClassCastException("Instance is not assignable from mocking class");
        }

        MockCoreInstance<T> core = new MockCoreInstance<>(mocking, instance);

        return getProxy(core, mocking);
    }

    public static <T> T mock(T instance) throws ClassCastException, InstanceNotFoundException {
        if (instance == null)
            throw new InstanceNotFoundException("Can't get mock of 'null'");

        Class mocking = instance.getClass();

        MockCoreInstance<T> core = new MockCoreInstance<>((Class<T>) mocking, instance);

        return getProxy(core, (Class<T>) mocking);
    }

    public static <T> T mock(Class<T> mocking) {
        MockCoreInstance<T> core = new MockCoreInstance<>(mocking, instantiateClass(mocking));
        return getProxy(core, mocking);
    }

    private static <T> T getProxy(MockCoreInstance<T> core, Class<T> mocking) {
        T proxyInstance = core.getMock();
        instanceMap.put(proxyInstance, core);

        MockStaticCore.defineStatic(mocking);

        return proxyInstance;
    }

    public static void init(Object local) throws IllegalAccessException, InstanceNotFoundException {
        Field[] fields = local.getClass().getFields();
        for (Field field : fields) {
            if (field.getAnnotation(Mock.class) != null) {
                Object proxyInstance;
                Object originalInstance = field.get(local);

                if (originalInstance == null)
                    originalInstance = instantiateClass(field.getType());

                proxyInstance = mock(field.getType(), originalInstance);
                field.set(local, proxyInstance);
            }
        }
    }

    public static <R> IMockRT<R> when(R smt) throws InstanceNotFoundException {
        if (lastCalledOrder.equals(LastCalledOrder.DYNAMIC)) {
            return instanceMap.get(lastCalled).when(smt);
        } else if (lastCalledOrder.equals(LastCalledOrder.STATIC)) {
            return new MockRTS<R>(lastCalledStatic);
        } else throw new InstanceNotFoundException("Last call is not initialized");

    }

    public static void updateLast(Object o) {
        lastCalled = o;
        lastCalledOrder = LastCalledOrder.DYNAMIC;
    } //TODO: protected

    public static void updateLastStatic(Triple<Class<?>, String, ArrayList<Object>> classMethodArgs) {
        lastCalledStatic = classMethodArgs;
        lastCalledOrder = LastCalledOrder.STATIC;
    }

    private static Object instantiateClass(Class<?> type) {
        Object instance = null;
        if (!type.isInterface()) {
            try {
                Constructor emptyConstructor = null;
                for (Constructor constructor : type.getConstructors()) {
                    Object[] inputTypes = constructor.getParameterTypes();
                    if (inputTypes.length != 0)
                        continue;
                    else {
                        emptyConstructor = constructor;
                        break;
                    }
                }

                if (emptyConstructor == null)
                    throw new Exception();
                instance = emptyConstructor.newInstance();
            } catch (Exception exception) {
                throw new ExceptionInInitializerError("No 0 parameter constructor in class: " + type.getName());
            }
        }
        return instance;
    }
}
