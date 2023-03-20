package org.example;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.*;

public class CGLibTest <T> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private final HashMap<Pair<Method, Object[]>, Pair<Object, Boolean>> actionMap = new HashMap<>();

    protected Pair<Method, Object[]> lastCalled = new Pair<>();
    CGLibTest(Class<T> mocking) {
        enhancer = new Enhancer();
        operatingClass = mocking;
        enhancer.setSuperclass(operatingClass);


        enhancer.setCallback((InvocationHandler) (o, method, objects) -> {
//            System.out.println(Arrays.toString(objects));

            if (!method.getDeclaringClass().equals(operatingClass)) {
                throw new Exception("Class not correct");
            }

            this.lastCalled = new Pair<>(method, objects);

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
        return (T) enhancer.create();
    }

    public <R> CGLibTestRT<R> when(R smt) {
        return new CGLibTestRT<>(this); //TODO : спорное решение, мб внутри копировать
    }

    protected void addReturn(Pair<Method, Object[]> meth, Object ret){
        this.actionMap.put(meth, new Pair<>(ret, false));
    }

    protected void addException(Pair<Method, Object[]> methodPair, Throwable ret){
        this.actionMap.put(methodPair, new Pair<>(ret, true));
    }
}
