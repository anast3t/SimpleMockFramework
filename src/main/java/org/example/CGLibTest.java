package org.example;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CGLibTest <T> {
    private Enhancer enhancer;
    Class<T> operatingClass;
    HashMap<Pair<String, Parameter[]>, Object> actionMap = new HashMap<>();

    CGLibTest(Class<T> mocking) {
        enhancer = new Enhancer();
        operatingClass = mocking;

        enhancer.setSuperclass(operatingClass);
        enhancer.setCallback((InvocationHandler) (o, method, objects) -> {
            if (!method.getDeclaringClass().equals(operatingClass)) {
                throw new Exception("Class not correct");
            }

            Object returnValue = actionMap.get(new Pair<>(method.getName(), method.getParameters()));
            if(returnValue != null)
                return returnValue;
            else
                throw new Exception(method.getName() + "  -- Method not mocked");
        });
        System.out.println("hello from builder");

    }

    public T getMock() {
        return (T) enhancer.create();
    }

    public void whenThen(Method method, Object returnValue) throws Exception {

        if(!method.getDeclaringClass().equals(operatingClass))
            throw new Exception("Method is not presented in class");
        actionMap.put(new Pair<>(method.getName(), method.getParameters()), returnValue);
    }
}
