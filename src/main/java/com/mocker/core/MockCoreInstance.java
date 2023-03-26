package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.ActionType;
import com.mocker.utils.Functions;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import com.mocker.utils.Pair;

import javax.management.InstanceNotFoundException;
import java.lang.reflect.Method;
import java.util.*;

public class MockCoreInstance<T> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private final HashMap<
            Pair<Method, ArrayList<Object>>, //Вызываемый метод - аргументы метода
            Pair<Object, ActionType> // Возвращаемое значение - является ли значение эксепшном
            > actionMap = new HashMap<>();

    protected Pair<Method, ArrayList<Object>> lastCalledMethod = new Pair<>();

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

    protected void addReturnAction(Pair<Method, ArrayList<Object>> methodPair, Object ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.RETURN));
    }

    protected void addExceptionAction(Pair<Method, ArrayList<Object>> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.THROW));
    }

    protected void addNullAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.NULL));
    }

    protected void addImplementedAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.IMPLEMENTED));
    }

    private Callback invocationHandler(Object originalInstance){
        return (InvocationHandler) (proxy, method, objects) -> {

            if (!method.getDeclaringClass().isAssignableFrom(operatingClass)) {
                throw new Exception("Class not correct");
            }


            ArrayList<Object> listedObjects = Functions.recArr2ArrListConverter(objects);


            this.lastCalledMethod = new Pair<>(method, listedObjects);
            Mocker.updateLast(proxy);

            var key = new Pair<>(method, listedObjects);

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
                case IMPLEMENTED:
                    if(originalInstance != null){
                        return method.invoke(originalInstance, objects);
                    } else throw new IllegalAccessException("Tried to call implemented method of an interface. Action map or instance can be corrupted (check if you called unmocked instance in when())");
//                        throw new InstanceNotFoundException("Can't find instance for running implemented method");
                case RETURN:
                    return returnValue;
                case THROW:
                    throw (Throwable) returnValue;
            }
            return null;
        };
    }
}
