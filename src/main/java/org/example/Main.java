package org.example;

import com.mocker.Mocker;
import com.mocker.annotations.Mock;
import com.mocker.core.MockCoreInstance;
import com.mocker.utils.ActionType;
import com.mocker.utils.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.text.*;

import static com.mocker.Mocker.*;

public class Main {
    public static void main(String[] args) throws Throwable {
        SomeClass someClass = new SomeClass();
        someClass = mock(someClass);

        when(someClass.multiInput(any(String.class), 10, any(Boolean.class))).thenImplemented();
        when(someClass.multiInput(any(String.class), 10, true)).thenReturn(1488);
        when(someClass.multiInput("123", 10, any(Boolean.class))).thenReturn(1984);

        System.out.println(someClass.multiInput("124", 10, Boolean.TRUE));
    }
}
