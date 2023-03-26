import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import org.example.*;
import org.junit.jupiter.api.*;

import javax.management.InstanceNotFoundException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MockTest {
    @Mock
    public SomeClass someClass;

    @Mock
    public SomeInterface someInterface;


    @BeforeEach
    public void setUp() throws IllegalAccessException, InstanceNotFoundException {
        Mocker.init(this);
    }

    @Test
    @Order(1)
    public void dEmpty() {
        Assertions.assertNull(someClass.stringReturnMethod("123"));
    }

    @Test
    @Order(2)
    public void dNull() throws InstanceNotFoundException {
        Mocker.when(someClass.testPrint()).thenReturn(1);
        Mocker.when(someClass.testPrint()).thenNull();
        Assertions.assertNull(someClass.testPrint());
    }

    @Test
    @Order(3)
    public void dImplemented() throws InstanceNotFoundException {
        Mocker.when(someClass.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                someClass.stringReturnMethod("123")
        );
    }

    @Test
    @Order(4)
    public void dReturn() throws InstanceNotFoundException {
        Mocker.when(someClass.integerReturnMethod(1)).thenReturn(123);
        Assertions.assertEquals(someClass.integerReturnMethod(1), 123);
    }

    @Test
    @Order(5)
    public void dThrow() throws InstanceNotFoundException {
        Mocker.when(someClass.integerReturnMethod(1984)).thenThrow(new IllegalArgumentException());

        Assertions.assertThrows(RuntimeException.class, () -> {
            someClass.integerReturnMethod(1984);
        });
    }

    @Test
    @Order(6)
    public void dInterfaceReturn() throws InstanceNotFoundException {
        SomeClass someClass1 = new SomeClass(100);
        someClass1 = Mocker.mock(someClass1);

        Mocker.when(someInterface.someGenerator()).thenReturn(someClass1);
        Mocker.when(someClass1.integerReturnMethod(5)).thenImplemented();

        Assertions.assertEquals(105, someInterface.someGenerator().integerReturnMethod(5));
    }

    @Test
    @Order(7)
    public void dInterfaceImplemented() throws InstanceNotFoundException {
        Mocker.when(someInterface.someGenerator()).thenImplemented();
        Assertions.assertThrows(Exception.class, ()->{
            someInterface.someGenerator();});
    }

    @Test
    @Order(8)
    public void dMultipleOverridingOperations() throws InstanceNotFoundException {
        Mocker.when(someClass.stringReturnMethod("123")).thenReturn("234");
        Assertions.assertEquals(someClass.stringReturnMethod("123"), "234");

        Mocker.when(someClass.stringReturnMethod("123")).thenNull();
        Assertions.assertNull(someClass.stringReturnMethod("123"));

        Mocker.when(someClass.stringReturnMethod("123")).thenImplemented();
        Assertions.assertEquals(
                "Hello from someclass, passed:"+"123",
                someClass.stringReturnMethod("123")
        );

        Mocker.when(someClass.stringReturnMethod("123")).thenThrow(new IllegalArgumentException("Some Exception"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            someClass.stringReturnMethod("123");
        });

    }

    @Test
    @Order(9)
    public void sEmpty(){
        Assertions.assertNull(SomeClass.staticStringReturnMethod("123", 123));
    }

    @Test
    @Order(10)
    public void sNull() throws InstanceNotFoundException {
        Mocker.when(SomeClass.staticStringReturnMethod("123", 123)).thenNull();
        Assertions.assertNull(SomeClass.staticStringReturnMethod("123", 123));
    }

    @Test
    @Order(11)
    public void sReturn() throws InstanceNotFoundException {
        Mocker.when(SomeClass.staticStringReturnMethod("str",3)).thenReturn("mocked");
        Assertions.assertEquals(
                "mocked",
                SomeClass.staticStringReturnMethod("str",3)
        );
    }

    @Test
    @Order(12)
    public void sThrow() throws InstanceNotFoundException {
        Mocker.when(SomeClass.staticStringReturnMethod("exception", 42)).thenThrow(new ExceptionInInitializerError());

        Assertions.assertThrows(ExceptionInInitializerError.class, () -> {
            SomeClass.staticStringReturnMethod("exception", 42);
        });
    }

    @Test
    @Order(13)
    public void sImplemented() throws InstanceNotFoundException{
        Mocker.when(SomeClass.staticStringReturnMethod("someStr", 1)).thenImplemented();
        Assertions.assertEquals(
                "Got String: someStr, and Integer: 101",
                SomeClass.staticStringReturnMethod("someStr", 1)
        );
    }

    @Test
    @Order(14)
    public void sMultipleOverridingOperations() throws InstanceNotFoundException {
        Mocker.when(SomeClass.staticStringReturnMethod("someStr", 1)).thenReturn("234");
        Assertions.assertEquals(SomeClass.staticStringReturnMethod("someStr", 1), "234");

        Mocker.when(SomeClass.staticStringReturnMethod("someStr", 1)).thenNull();
        Assertions.assertNull(SomeClass.staticStringReturnMethod("someStr", 1));

        Mocker.when(SomeClass.staticStringReturnMethod("someStr", 1)).thenImplemented();
        Assertions.assertEquals(
                "Got String: someStr, and Integer: 101",
                SomeClass.staticStringReturnMethod("someStr", 1)
        );

        Mocker.when(SomeClass.staticStringReturnMethod("someStr", 1)).thenThrow(new IllegalArgumentException("Some Exception"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> SomeClass.staticStringReturnMethod("someStr", 1));

    }
}