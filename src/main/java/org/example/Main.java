package org.example;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {

        TestClass testClass = new TestClass();

        Mocker
                .when(testClass.someClass.stringReturnMethod("123"))
                .thenReturn("hello, im from 123");

        Mocker.when(TestClass.someClassStatic.stringReturnMethod("STATIC")).thenReturn("IM STATIC");



        System.out.println(testClass.someClass.stringReturnMethod("123"));
        System.out.println(TestClass.someClassStatic.stringReturnMethod("STATIC"));

     /*   MockCoreInstance<SomeClass> cgLibTest = new MockCoreInstance<>(SomeClass.class);

        SomeClass mocked = cgLibTest.getMock();
        SomeClass mocked2 = cgLibTest.getMock();


        cgLibTest.when(mocked.stringReturnMethod("123")).thenReturn("test return on 123");
        cgLibTest.when(mocked.stringReturnMethod("234")).thenReturn("test return on 234");
        cgLibTest.when(mocked.stringReturnMethod("hell")).thenThrow(new IOException("Hello from exception"));

        System.out.println(mocked.stringReturnMethod("123"));
        System.out.println(mocked.stringReturnMethod("234"));
        try {
            System.out.println(mocked.stringReturnMethod("hell"));
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }*/

//        CGLibTest<Color> test2 = new CGLibTest<>(Color.class);
//        test2.when(Color.average(1,2,3)).thenReturn("hello with static method");

/*        Original original = new Original();
        Handler handler = new Handler(original);
        If f = (If) Proxy.newProxyInstance(If.class.getClassLoader(),
                new Class[] { If.class },
                handler);
        f.originalMethod("Hallo");*/
    }
}

