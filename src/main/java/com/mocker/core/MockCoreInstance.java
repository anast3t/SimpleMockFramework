package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import com.mocker.utils.ActionType;
import com.mocker.utils.Functions;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import com.mocker.utils.Pair;

import java.lang.reflect.Method;
import java.util.*;

public class MockCoreInstance<T> implements IMockCore<Pair<Method, ArrayList<Object>>> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private final HashMap<
            Pair<Method, ArrayList<Object>>, //Вызываемый метод - аргументы метода
            Pair<Object, ActionType> // Возвращаемое значение - является ли значение эксепшном
            > actionMap = new HashMap<>();

    protected Pair<Method, ArrayList<Object>> lastCalledMethod = new Pair<>();

    public MockCoreInstance(Class<T> mocking, Object originalInstance) {
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

    private Callback invocationHandler(Object originalInstance){
        return (InvocationHandler) (proxy, method, objects) -> {

            if (!method.getDeclaringClass().isAssignableFrom(operatingClass)) {
                throw new Exception("Class not correct");
            }

            ArrayList<Object> listedObjects = Functions.recArr2ArrListConverter(objects);
            if(listedObjects.size() == 1 && Mocker.anyFlag){
                Mocker.anyFlag = false;
                Object element = listedObjects.remove(0);
                listedObjects.add(element.getClass()); //TODO: check this class in generalized search
            }

            this.lastCalledMethod = new Pair<>(method, listedObjects);
            Mocker.updateLast(proxy);

            Pair<Method, ArrayList<Object>> keySpecific = new Pair<>(method, listedObjects);

            Pair<Object, ActionType> specificReturnPair = actionMap
                    .entrySet()
                    .stream()
                    .filter(el -> el.getKey().equals(keySpecific))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse(null);

            Pair<Object, ActionType> generalizedReturnPair = null;
            if(specificReturnPair == null){
                if(listedObjects.size() == 1){
                    ArrayList<Object> any = new ArrayList<>(1);
                    any.add(listedObjects.get(0).getClass());
                    Pair<Method, ArrayList<Object>> keyGeneralized = new Pair<>(method, any);

                    generalizedReturnPair = actionMap
                            .entrySet()
                            .stream()
                            .filter(el ->
                                    el.getKey().left.equals(keyGeneralized.left) &&
                                    el.getKey().right.get(0).equals(keyGeneralized.right.get(0))
                            )
                            .map(Map.Entry::getValue)
                            .findAny()
                            .orElse(null);
                } else{
                    return null;
                }
            }

            Pair<Object, ActionType> returnPair;
            if(specificReturnPair == null){
                if(generalizedReturnPair == null){
                    return null;
                }
                returnPair = generalizedReturnPair;
            } else {
                returnPair = specificReturnPair;
            }

            Object returnValue = returnPair.left;
            ActionType action = returnPair.right;

            switch (action){
                case NULL:
                    return null;
                case IMPLEMENTED:
                    if(originalInstance != null){
                        return method.invoke(originalInstance, objects);
                    } else throw new IllegalAccessException("Tried to call implemented method of an interface. Action map or instance can be corrupted (check if you called unmocked instance in when())");
                case RETURN:
                    return returnValue;
                case THROW:
                    throw (Throwable) returnValue;
            }
            return null;
        };
    }

    @Override
    public void addReturnAction(Pair<Method, ArrayList<Object>> methodPair, Object ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.RETURN));
    }

    @Override
    public void addExceptionAction(Pair<Method, ArrayList<Object>> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.THROW));
    }

    @Override
    public void addNullAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.NULL));
    }

    @Override
    public void addImplementedAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.IMPLEMENTED));
    }
}
