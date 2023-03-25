package com.mocker.core;

import com.mocker.utils.Pair;

import java.lang.reflect.Method;

public class MockRT<R> implements IMockRT<R> { //Return - throw
    public MockRT(MockCoreInstance<?> parent){
        this.parent = parent;
        this.called = parent.lastCalledMethod;
    }

    private final MockCoreInstance<?> parent;
    private final Pair<Method, Object[]> called;

    public void thenReturn(R value){
        parent.addReturnAction(called, value);
    }

    public void thenThrow (Throwable exception) {
        parent.addExceptionAction(called, exception);
    }

    public void thenInitial(){
        parent.addImplementedAction(called);
    }

    public void thenNull(){
        parent.addNullAction(called);
    }

}
