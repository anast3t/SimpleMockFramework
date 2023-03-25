package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.ActionType;
import net.sf.cglib.proxy.Callback;
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
            Pair<Object, ActionType> // Возвращаемое значение - является ли значение эксепшном
            > actionMap = new HashMap<>();

    protected Pair<Method, Object[]> lastCalledMethod = new Pair<>();

    public MockCoreInstance(Class<T> mocking, Object originalInstance) { //TODO: protected
        enhancer = new Enhancer();
        operatingClass = mocking;
        enhancer.setSuperclass(operatingClass);

        enhancer.setCallback(invocationHandler(originalInstance));
    }

    public T getMock() {
        T instance = (T) enhancer.create();
        return instance;
    }

    public <R> MockRT<R> when(R smt) {
        return new MockRT<>(this);
    }

    protected void addReturnAction(Pair<Method, Object[]> methodPair, Object ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.RETURN));
    }

    protected void addExceptionAction(Pair<Method, Object[]> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.THROW));
    }

    protected void addNullAction(Pair<Method, Object[]> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.NULL));
    }

    protected void addImplementedAction(Pair<Method, Object[]> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.IMPL));
    }

    private Callback invocationHandler(Object originalInstance){
        return (InvocationHandler) (proxy, method, objects) -> {

            if (!method.getDeclaringClass().isAssignableFrom(operatingClass)) {
                throw new Exception("Class not correct");
            }

            if(objects.length == 0)
                objects = null;



            this.lastCalledMethod = new Pair<>(method, objects);
            Mocker.updateLast(proxy);

            var key = new Pair<>(method, objects);

            Pair<Object, ActionType> returnPair = actionMap
                    .entrySet()
                    .stream()
                    .filter(el -> el.getKey().equals(key))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse(null);
            // TODO: загадка почему через get не тянет (ответ - нет глубокого сравнения и сравнения по массивам видимо)

            if(returnPair == null)
                return null;

            Object returnValue = returnPair.left;
            ActionType action = returnPair.right;

            switch (action){
                case NULL:
                    return null;
                case IMPL:
                    if(originalInstance != null){
                        return method.invoke(originalInstance, objects);
                    } else return null;
                case RETURN:
                    return returnValue;
                case THROW:
                    throw (Throwable) returnValue;
            }
            return null;
        };
    }
}
