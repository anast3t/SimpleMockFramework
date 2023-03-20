import org.example.Mock;
import org.example.Mocker;
import org.example.SomeClass;
import org.example.SomeInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class MockTest {
    @Mock
    public SomeClass test;

    @Mock
    public SomeInterface itest;


    @BeforeEach
    public void setUp() throws IllegalAccessException {
        Mocker.init(this);
    }

    @Test
    public void mockEmpty() {
        assertNull(test.stringReturnMethod("123"));
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

        Exception exception = assertThrows(RuntimeException.class, () -> {
            test.integerReturnMethod(1984);
        });
    }

    @Test
    public void mockInterface() {
        Mocker.when(itest.someGenerator()).thenReturn(test);
        Mocker.when(test.stringReturnMethod("uh")).thenReturn("im out of generator");

        Assertions.assertEquals("im out of generator", itest.someGenerator().stringReturnMethod("uh"));
    }
}