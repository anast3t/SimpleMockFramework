package com.mocker.core;

import com.mocker.utils.Pair;

import java.lang.reflect.Method;

public class MockRT<R> { //Return - throw
    public MockRT(MockCoreInstance<?> parent){
        this.parent = parent;
        this.called = parent.lastCalledMethod;
    }

    private final MockCoreInstance<?> parent;
    private final Pair<Method, Object[]> called;

    public void thenReturn(R value){
        parent.addReturn(called, value);
    }

    public void thenThrow (Throwable exception) {
        parent.addException(called, exception);
    }


}
