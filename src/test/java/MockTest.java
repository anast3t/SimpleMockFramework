import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import org.example.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceNotFoundException;
import java.lang.reflect.UndeclaredThrowableException;


public class MockTest {
    @Mock
    public SomeClass test;

    @Mock
    public SomeInterface itest;

    public static TestClass testClass;


    @BeforeEach
    public void setUp() throws IllegalAccessException, InstanceNotFoundException {
        Mocker.init(this);
    }

    @Test
    public void dEmpty() {
        Assertions.assertNull(test.stringReturnMethod("123"));
    }

    @Test
    public void dNull() throws InstanceNotFoundException {
        Mocker.when(test.testPrint()).thenReturn(1);
        Mocker.when(test.testPrint()).thenNull();
        Assertions.assertNull(test.testPrint());
    }

    @Test
    public void dImplemented() throws InstanceNotFoundException {
        Mocker.when(test.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                test.stringReturnMethod("123")
        );
    }

    @Test
    public void dReturn() throws InstanceNotFoundException {
        Mocker.when(test.integerReturnMethod(1)).thenReturn(123);
        Assertions.assertEquals(test.integerReturnMethod(1), 123);
    }

    @Test
    public void dThrow() throws InstanceNotFoundException {
        Mocker.when(test.integerReturnMethod(1984)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            test.integerReturnMethod(1984);
        });
    }

    @Test
    public void dInterfaceReturn() throws InstanceNotFoundException {
        Mocker.when(itest.someGenerator()).thenReturn(test);
        Mocker.when(test.stringReturnMethod("uh")).thenReturn("im out of generator");

        Assertions.assertEquals("im out of generator", itest.someGenerator().stringReturnMethod("uh"));
    }

    @Test
    public void dInterfaceImplemented() throws InstanceNotFoundException {
        Mocker.when(itest.someGenerator()).thenImplemented();
        Assertions.assertThrows(Exception.class, ()->{itest.someGenerator();});
    }

    @Test
    public void dMultipleOverridingOperations() throws InstanceNotFoundException {
        Mocker.when(test.stringReturnMethod("123")).thenReturn("234");
        Assertions.assertEquals(test.stringReturnMethod("123"), "234");

        Mocker.when(test.stringReturnMethod("123")).thenNull();
        Assertions.assertNull(test.stringReturnMethod("123"));

        Mocker.when(test.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                test.stringReturnMethod("123")
        );

        Mocker.when(test.stringReturnMethod("123")).thenThrow(new IllegalArgumentException("Some Exception"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            test.stringReturnMethod("123");
        });

    }

    @Test
    public void sField() throws IllegalAccessException, InstanceNotFoundException {
        MockTest.testClass = new TestClass();

        Mocker
                .when(
                        TestClass.someClassStatic.stringReturnMethod("test")
                )
                .thenReturn("static call");

        Assertions.assertEquals(
                TestClass.someClassStatic.stringReturnMethod("test"),
                "static call"
        );
    }

    @Test
    public void sClass() throws Throwable {
        Mocker.when(test.stringReturnMethod("123")).thenReturn("mocked");
        Assertions.assertEquals("mocked", test.stringReturnMethod("123"));

        Mocker.when(SomeClass.staticStringReturnMethod("str",3)).thenReturn("not_huy");
        Assertions.assertEquals(
                "not_huy",
                SomeClass.staticStringReturnMethod("str",3)
        );
    }

    @Test
    public void sThrow() throws InstanceNotFoundException {
        Mocker.when(SomeClass.staticStringReturnMethod("exception", 3)).thenThrow(new Exception());

        Assertions.assertThrows(Exception.class, () -> {
            SomeClass.staticStringReturnMethod("exception", 3);
        });
    }
}