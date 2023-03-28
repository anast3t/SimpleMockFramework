package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.ActionType;
import com.mocker.utils.Functions;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import com.mocker.utils.Pair;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MockCoreInstance<T> implements IMockCore<Pair<Method, ArrayList<Object>>> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private final HashMap<
            Pair<Method, ArrayList<Object>>, //Вызываемый метод - аргументы метода
            Pair<Object, ActionType> // Возвращаемое значение - тип действия
            > actionMap = new HashMap<>();
    private final HashMap<
            Pair<Method, Integer>, // метод - количество any (trinket)
            Pair<Method, ArrayList<Object>> //маска ключа
            > trinketKeymask = new HashMap<>();
    
    protected Integer anyCounter = 0;

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

    public <R> MockActions<R> when(R smt) {
        return new MockActions<>(this);
    }

    private Callback invocationHandler(Object originalInstance){
        return (InvocationHandler) (proxy, method, objects) -> {
            if (!method.getDeclaringClass().isAssignableFrom(operatingClass)) {
                throw new ClassCastException("Method is not from mocked class");
            }

            ArrayList<Object> listedObjects = Functions.recArr2ArrListConverter(objects);
            anyCounter = 0;
            Integer initialSize = Mocker.listOfAny.size();
            for(int i = 0; i < listedObjects.size(); i++){
                Object real = listedObjects.get(i);
                Object anyObject = null;
                for(Object anyElement : Mocker.listOfAny){
                    if(anyElement == real){
                        anyObject = anyElement;
                    }
                }
                if(anyObject != null){
                    Object element = listedObjects.remove(i);
                    listedObjects.add(i, element.getClass());
                    anyCounter+=1;
                }
            }
            if(!initialSize.equals(anyCounter)){
                throw new IllegalCallerException("any() or when() used in wrong way, order or place");
            }
            Mocker.listOfAny = new ArrayList<>();

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
                Pair<Method, ArrayList<Object>> keymask = null;
                for(int i = 1; i <= listedObjects.size(); i++){
                    keymask = trinketKeymask.get(new Pair<>(method, i)); //TODO: абстрагировать отсюда
                    if(keymask != null){
                        Pair<Method, ArrayList<Object>> masked = new Pair<>(keymask.left, new ArrayList<>(keymask.right));
                        for(int j = 0; j < masked.right.size(); j++){
                            if(masked.right.get(j) == null){
                                masked.right.remove(j);
                                masked.right.add(j, listedObjects.get(j));
                            }
                        } // До сюда
                        generalizedReturnPair = actionMap
                                .entrySet()
                                .stream()
                                .filter(el ->el.getKey().equals(masked))
                                .map(Map.Entry::getValue)
                                .findAny()
                                .orElse(null);
                        if(generalizedReturnPair != null){
                            break;
                        }
                    }
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
        addToKeyChain(methodPair);
    }

    @Override
    public void addExceptionAction(Pair<Method, ArrayList<Object>> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, ActionType.THROW));
        addToKeyChain(methodPair);
    }

    @Override
    public void addNullAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.NULL));
        addToKeyChain(methodPair);
    }

    @Override
    public void addImplementedAction(Pair<Method, ArrayList<Object>> methodPair){
        this.actionMap.put(methodPair, new Pair<>(null, ActionType.IMPLEMENTED));
        addToKeyChain(methodPair);
    }

    private void addToKeyChain(Pair<Method, ArrayList<Object>> methodPair){
        if(anyCounter == 0)
            return;
        Pair<Method, Integer> trinket = new Pair<>(methodPair.left, anyCounter);
        Pair<Method, ArrayList<Object>> keymask = trinketKeymask.get(trinket);
        if(keymask != null) {
            System.out.println("Keymask for " + anyCounter + " was overridden"); //TODO: rework from arrayList to solo keymask
        }
        ArrayList<Object> params = methodPair.right;
        params = (ArrayList<Object>) params.stream().map(x->{
            if(x.getClass() != Class.class){
                return null;
            }
            return x;
        }).collect(Collectors.toList());
        keymask = new Pair<>(methodPair.left, params);
        trinketKeymask.put(trinket, keymask);
    }
}
