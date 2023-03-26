package com.mocker.core;

import com.mocker.utils.Triple;

import java.util.ArrayList;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Triple<Class<?>, String, ArrayList<Object>> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Triple<Class<?>, String, ArrayList<Object>> called;

    @Override
    public void thenReturn(R value){
        MockStaticCore.addReturnAction(called, value);
    }

    @Override
    public void thenThrow (Throwable exception) {
        MockStaticCore.addExceptionAction(called, exception);
    }

    @Override
    public void thenNull() {
        MockStaticCore.addNullAction(called);
    }

    @Override
    public void thenImplemented() {
        MockStaticCore.addImplementedAction(called);
    }
}
