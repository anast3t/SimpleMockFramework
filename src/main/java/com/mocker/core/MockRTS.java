package com.mocker.core;

import com.mocker.utils.Triple;

import java.util.ArrayList;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Triple<Class<?>, String, ArrayList<Object>> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Triple<Class<?>, String, ArrayList<Object>> called;
    private final MockStaticCore mockStaticCore = MockStaticCore.getInstance();

    @Override
    public void thenReturn(R value){
        mockStaticCore.addReturnAction(called, value);
    }

    @Override
    public void thenThrow (Throwable exception) {
        mockStaticCore.addExceptionAction(called, exception);
    }

    @Override
    public void thenNull() {
        mockStaticCore.addNullAction(called);
    }

    @Override
    public void thenImplemented() {
        mockStaticCore.addImplementedAction(called);
    }
}
