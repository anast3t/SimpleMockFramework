package org.example;

import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");
        Original original = new Original();
        Handler handler = new Handler(original);
        If f = (If) Proxy.newProxyInstance(If.class.getClassLoader(),
                new Class[] { If.class },
                handler);
        f.originalMethod("Hallo");
    }

    //processing code
    public static void callAnnotated(Class<?> aClass) throws Exception {
        Object obj = aClass.getDeclaredConstructor().newInstance();
        Method[] methods = aClass.getMethods();
        for (Method m : methods) {
            if (m.getAnnotation(RunIt.class) != null &&
                    m.getParameterCount() == 0) m.invoke(obj);
        }
    }

    interface If {
        void originalMethod(String s) throws Exception;
    }
    static class Original implements If {
        public void originalMethod(String s) throws Exception {
            callAnnotated(SomeClass.class);
            System.out.println(s);
        }
    }
    static class Handler implements InvocationHandler {
        private final If original;
        public Handler(If original) {
            this.original = original;
        }
        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            System.out.println("BEFORE");
            method.invoke(original, args);
            System.out.println("AFTER");
            return null;
        }
    }
}

