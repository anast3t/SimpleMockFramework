package org.example;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Mocker {
    private static Object lastCalled;
    private static final Map<Object, MockCoreInstance<?>> instanceMap = new IdentityHashMap<>();

    public static <T> T mock (Class<T> mocking){
        MockCoreInstance<T> core = new MockCoreInstance<>(mocking);
        T instance = core.getMock();
        instanceMap.put(instance, core);
        return instance;
    }

    public static void init(Object local) throws IllegalAccessException {
        Field[] fields =  local.getClass().getFields();
        for (Field f: fields) {
            if (f.getAnnotation(Mock.class) != null){
                Object instance = mock(f.getType());
                f.set(local, instance);
            }
        }
    }

    public static <R> MockRT<R> when(R smt){
        return instanceMap.get(lastCalled).when(smt);
    }

    protected static void updateLast(Object o){
        lastCalled = o;
    }
}
