package org.example;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CGLibTest <T> {
    private final Enhancer enhancer;
    private final Class<T> operatingClass;
    private Class<?> generatedClass;
    private HashMap<Pair<Method, Object[]>, Object> actionMap = new HashMap<>();

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

            Object returnValue = actionMap
                    .entrySet()
                    .stream()
                    .filter(el -> el.getKey().equals(key))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElse(null);
            // TODO: загадка почему через get не тянет (ответ - нет глубокого сравнения и сравнения по массивам видимо)

            return returnValue;
        });
    }

    public T getMock() {
        T returnValue = (T) enhancer.create();
        generatedClass = returnValue.getClass();
        return returnValue;
    }

    public <R> CGLibTestRT<R> when(R smt) {
        return new CGLibTestRT<R>(this); //TODO : спорное решение, мб внутри копировать
    }

    protected void addAction(Pair<Method, Object[]> meth, Object ret){
        this.actionMap.put(meth, ret);
    }

    protected void addException(Pair<Method, Object[]> methodPair, Exception ret){
        this.actionMap.put(methodPair, ret);
    }
}
