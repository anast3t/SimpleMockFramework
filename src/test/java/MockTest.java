import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import org.example.*;
import org.junit.jupiter.api.*;

import javax.management.InstanceNotFoundException;
import java.lang.reflect.InvocationTargetException;

import static com.mocker.Mocker.any;
import static com.mocker.Mocker.when;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class MockTest {
    @Mock
    public SomeClass someClass;

    @Mock
    public SomeInterface someInterface;

    private Integer order = 0;

    @BeforeEach
    public void setUp() throws IllegalAccessException, InstanceNotFoundException {
        Mocker.init(this);
    }

    @Test
    public void dEmpty() {
        Assertions.assertNull(someClass.stringReturnMethod("123"));
    }

    @Test
    public void dNull() throws InstanceNotFoundException {
        when(someClass.testPrint()).thenReturn(1);
        when(someClass.testPrint()).thenNull();
        Assertions.assertNull(someClass.testPrint());
    }

    @Test
    public void dImplemented() throws InstanceNotFoundException {
        when(someClass.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                someClass.stringReturnMethod("123")
        );
    }

    @Test
    public void dReturn() throws InstanceNotFoundException {
        when(someClass.integerReturnMethod(1)).thenReturn(123);
        Assertions.assertEquals(someClass.integerReturnMethod(1), 123);
    }

    @Test
    public void dReturnWithAny() throws Exception {
        when(someClass.integerReturnMethod(any(Integer.class))).thenReturn(123);
        Assertions.assertEquals(someClass.integerReturnMethod(1), 123);
        Assertions.assertEquals(someClass.integerReturnMethod(2), 123);
        Assertions.assertEquals(someClass.integerReturnMethod(3), 123);
    }

    @Test
    public void dThrow() throws InstanceNotFoundException {
        when(someClass.integerReturnMethod(1984)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            someClass.integerReturnMethod(1984);
        });
    }

    @Test
    public void dInterfaceReturn() throws InstanceNotFoundException {
        SomeClass someClass1 = new SomeClass(100);
        someClass1 = Mocker.mock(someClass1);

        when(someInterface.someGenerator()).thenReturn(someClass1);
        when(someClass1.integerReturnMethod(5)).thenImplemented();

        Assertions.assertEquals(105, someInterface.someGenerator().integerReturnMethod(5));
    }

    @Test
    public void dInterfaceImplemented() throws InstanceNotFoundException {
        when(someInterface.someGenerator()).thenImplemented();
        Assertions.assertThrows(Exception.class, ()->{
            someInterface.someGenerator();});
    }

    @Test
    public void dWithMultipleOverridingOperations() throws InstanceNotFoundException {
        when(someClass.stringReturnMethod("123")).thenReturn("234");
        Assertions.assertEquals(someClass.stringReturnMethod("123"), "234");

        when(someClass.stringReturnMethod("123")).thenNull();
        Assertions.assertNull(someClass.stringReturnMethod("123"));

        when(someClass.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                someClass.stringReturnMethod("123")
        );

        when(someClass.stringReturnMethod("123")).thenThrow(new IllegalArgumentException("Some Exception"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            someClass.stringReturnMethod("123");
        });

    }

    @Test void dWithMultipleAny() throws Exception {
        when(someClass.multiInput(any(String.class), 10, any(Boolean.class))).thenImplemented(); // 2
        when(someClass.multiInput(any(String.class), 10, true)).thenReturn(322); // 1
        when(someClass.multiInput("123", 10, any(Boolean.class))).thenReturn(1984); // 1

        Assertions.assertEquals(1984, someClass.multiInput("123", 10, Boolean.TRUE));
        Assertions.assertEquals(14, someClass.multiInput("124", 10, Boolean.TRUE));
        Assertions.assertEquals(13, someClass.multiInput("124", 10, Boolean.FALSE));

    }

    @Test
    public void sEmpty(){
        Assertions.assertNull(SomeClass.staticStringReturnMethod("123", 123));
    }

    @Test
    public void sNull() throws InstanceNotFoundException {
        when(SomeClass.staticStringReturnMethod("123", 123)).thenNull();
        Assertions.assertNull(SomeClass.staticStringReturnMethod("123", 123));
    }

    @Test
    public void sReturn() throws InstanceNotFoundException {
        when(SomeClass.staticStringReturnMethod("str",3)).thenReturn("mocked");
        Assertions.assertEquals(
                "mocked",
                SomeClass.staticStringReturnMethod("str",3)
        );
    }

    @Test
    public void sThrow() throws InstanceNotFoundException {
        when(SomeClass.staticStringReturnMethod("exception", 42)).thenThrow(new ExceptionInInitializerError());

        Assertions.assertThrows(ExceptionInInitializerError.class, () -> {
            SomeClass.staticStringReturnMethod("exception", 42);
        });
    }

    @Test
    public void sImplemented() throws InstanceNotFoundException{
        when(SomeClass.staticStringReturnMethod("someStr", 1)).thenImplemented();
        Assertions.assertEquals(
                "Got String: someStr, and Integer: 101",
                SomeClass.staticStringReturnMethod("someStr", 1)
        );
    }

    @Test
    public void sWithMultipleOverridingOperations() throws InstanceNotFoundException {
        when(SomeClass.staticStringReturnMethod("someStr", 1)).thenReturn("234");
        Assertions.assertEquals(SomeClass.staticStringReturnMethod("someStr", 1), "234");

        when(SomeClass.staticStringReturnMethod("someStr", 1)).thenNull();
        Assertions.assertNull(SomeClass.staticStringReturnMethod("someStr", 1));

        when(SomeClass.staticStringReturnMethod("someStr", 1)).thenImplemented();
        Assertions.assertEquals(
                "Got String: someStr, and Integer: 101",
                SomeClass.staticStringReturnMethod("someStr", 1)
        );

        when(SomeClass.staticStringReturnMethod("someStr", 1)).thenThrow(new IllegalArgumentException("Some Exception"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> SomeClass.staticStringReturnMethod("someStr", 1));

    }
}