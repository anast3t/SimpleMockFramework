package com.mocker.core;

import com.mocker.Mocker;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import com.mocker.utils.Pair;

import java.lang.reflect.Method;
import java.util.*;

public class MockCoreInstance<T> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private final HashMap<
            Pair<Method, Object[]>, //Вызываемый метод - аргументы метода
            Pair<Object, Boolean> // Возвращаемое значение - является ли значение эксепшном
            > actionMap = new HashMap<>();

    private final HashMap<
            Pair<Class<?>, Object[]>, //
            Pair<Object, Boolean>
            > staticActionMap = new HashMap<>();

    protected Pair<Method, Object[]> lastCalledMethod = new Pair<>();

    public MockCoreInstance(Class<T> mocking) { //TODO: protected
        enhancer = new Enhancer();
        operatingClass = mocking;
        enhancer.setSuperclass(operatingClass);


        enhancer.setCallback((InvocationHandler) (o, method, objects) -> {
//            System.out.println(Arrays.toString(objects));
//            System.out.println(o.toString());

            if (!method.getDeclaringClass().isAssignableFrom(operatingClass)) {
                throw new Exception("Class not correct");
            }

            this.lastCalledMethod = new Pair<>(method, objects);
            Mocker.updateLast(o);

            var key = new Pair<>(method, objects);

            Pair<Object, Boolean> returnValue = actionMap
                    .entrySet()
                    .stream()
                    .filter(el -> el.getKey().equals(key))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse(null);
            // TODO: загадка почему через get не тянет (ответ - нет глубокого сравнения и сравнения по массивам видимо)

            if(returnValue != null && returnValue.right){
                throw (Throwable) returnValue.left;
            }
            return returnValue != null ? returnValue.left : null;
        });
    }

    public T getMock() {
        T instance = (T) enhancer.create();
        return instance;
    }

    public <R> MockRT<R> when(R smt) {
        return new MockRT<>(this);
    }

    protected void addReturn(Pair<Method, Object[]> meth, Object ret){
        this.actionMap.put(meth, new Pair<>(ret, false));
    }

    protected void addException(Pair<Method, Object[]> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, true));
    }

    //TODO: can make real method invocation
}
