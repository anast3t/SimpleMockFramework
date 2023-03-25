package com.mocker.core;

import com.mocker.utils.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MockRT<R> implements IMockRT<R> { //Return - throw
    public MockRT(MockCoreInstance<?> parent){
        this.parent = parent;
        this.called = parent.lastCalledMethod;
    }

    private final MockCoreInstance<?> parent;
    private final Pair<Method, ArrayList<Object>> called;

    @Override
    public void thenReturn(R value){
        parent.addReturnAction(called, value);
    }

    @Override
    public void thenThrow (Throwable exception) {
        parent.addExceptionAction(called, exception);
    }

    @Override
    public void thenImplemented(){
        parent.addImplementedAction(called);
    }

    @Override
    public void thenNull(){
        parent.addNullAction(called);
    }

}
