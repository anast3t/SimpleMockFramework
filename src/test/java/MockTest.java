import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import org.example.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionException;


public class MockTest {
    @Mock
    public SomeClass test;

    @Mock
    public SomeInterface itest;

    public static TestClass testClass;


    @BeforeEach
    public void setUp() throws IllegalAccessException {
        Mocker.init(this);
    }

    @Test
    public void mockEmpty() {
        Assertions.assertNull(test.stringReturnMethod("123"));
    }

    @Test
    public void mockPrimitive() {
        Mocker.when(test.stringReturnMethod("123")).thenReturn("mocked");
        Assertions.assertEquals("mocked", test.stringReturnMethod("123"));

        Mocker.when(test.integerReturnMethod(123)).thenReturn(322);
        Assertions.assertEquals(322, test.integerReturnMethod(123));
    }

    @Test
    public void mockThrow() {
        Mocker.when(test.integerReturnMethod(1984)).thenThrow(new IllegalArgumentException());

        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            test.integerReturnMethod(1984);
        });
    }

    @Test
    public void mockInterface() {
        Mocker.when(itest.someGenerator()).thenReturn(test);
        Mocker.when(test.stringReturnMethod("uh")).thenReturn("im out of generator");

        Assertions.assertEquals("im out of generator", itest.someGenerator().stringReturnMethod("uh"));
    }

    @Test
    public void mockStatic() throws IllegalAccessException {
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
}