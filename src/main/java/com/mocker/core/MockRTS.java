package com.mocker.core;

import com.mocker.utils.Pair;
import com.mocker.utils.Triple;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Triple<Class<?>, String, Object[]> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Triple<Class<?>, String, Object[]> called;

    public void thenReturn(R value){
        MockStaticCore.addReturn(called, value);
    }

    public void thenThrow (Throwable exception) {
        MockStaticCore.addException(called, exception);
    }
}
