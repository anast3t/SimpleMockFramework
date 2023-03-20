package org.example;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {

        CGLibTest<SomeClass> cgLibTest = new CGLibTest<>(SomeClass.class);


        SomeClass mocked = cgLibTest.getMock();

        Class<?>[] paramTypes = new Class<?>[]{String.class};
//        cgLibTest.whenThen(SomeClass.class.getDeclaredMethod("stringReturnMethod", paramTypes), "Hello from mocked method!!!");

        cgLibTest.when(mocked.stringReturnMethod("123")).thenReturn("test return on 123");
        cgLibTest.when(mocked.stringReturnMethod("234")).thenReturn("test return on 234");
        cgLibTest.when(mocked.stringReturnMethod("hell")).thenThrow(new IOException());

        System.out.println(mocked.stringReturnMethod("123"));
        System.out.println(mocked.stringReturnMethod("234"));
        try {
            System.out.println(mocked.stringReturnMethod("hell"));
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

//        CGLibTest<Color> test2 = new CGLibTest<>(Color.class);
//        test2.when(Color.average(1,2,3)).thenReturn("hello with static method");

/*        Original original = new Original();
        Handler handler = new Handler(original);
        If f = (If) Proxy.newProxyInstance(If.class.getClassLoader(),
                new Class[] { If.class },
                handler);
        f.originalMethod("Hallo");*/
    }
/*
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
    }*/

}

