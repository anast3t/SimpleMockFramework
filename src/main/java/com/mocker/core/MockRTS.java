package com.mocker.core;

import com.mocker.Mocker;
import com.mocker.utils.Pair;

import java.lang.reflect.Method;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Pair<Pair<Class<?>, String>, Object[]> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Pair<Pair<Class<?>, String>, Object[]> called;

    public void thenReturn(R value){
        MockStaticCoreInstance.addReturn(called, value);
    }

    public void thenThrow (Throwable exception) {
        MockStaticCoreInstance.addException(called, exception);
    }
}
