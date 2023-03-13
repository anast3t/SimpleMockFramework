package org.example;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CGLibTest {
    private Enhancer enhancer;
    Class<?> operatingClass;
    HashMap<String, Object> actionMap = new HashMap<>();

    CGLibTest(Object mocking) {
        enhancer = new Enhancer();
        operatingClass = mocking.getClass();
        enhancer.setSuperclass(operatingClass);
        enhancer.setCallback((InvocationHandler) (o, method, objects) -> {
            if (!method.getDeclaringClass().equals(operatingClass)) {
                throw new Exception("Class not correct");
            }

            Optional<Object> returnValue = actionMap
                    .entrySet()
                    .stream()
                    .filter(e -> method.getName().equals(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst();

            if(returnValue.isPresent())
                return returnValue;
            else
                throw new Exception(method.getName() + "  -- Method not mocked");
        });
        System.out.println("hello from builder");
    }

    public <T> T getMock() {
        return (T) enhancer.create();
    }

    public void whenThen(Method method, Object returnValue) throws Exception {
        if(!method.getDeclaringClass().equals(operatingClass))
            throw new Exception("Method is not presented in class");
        actionMap.put(method.getName(), returnValue);
    }
}
