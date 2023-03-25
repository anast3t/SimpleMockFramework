package com.mocker.core;

import com.mocker.utils.Pair;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Pair<Pair<Class<?>, String>, Object[]> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Pair<Pair<Class<?>, String>, Object[]> called;

    public void thenReturn(R value){
        MockStaticCore.addReturn(called, value);
    }

    public void thenThrow (Throwable exception) {
        MockStaticCore.addException(called, exception);
    }
}
