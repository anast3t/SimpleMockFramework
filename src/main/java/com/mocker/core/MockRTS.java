package com.mocker.core;

import com.mocker.utils.Pair;
import com.mocker.utils.Triple;

import java.util.ArrayList;

public class MockRTS<R> implements IMockRT<R> {
    public MockRTS(Triple<Class<?>, String, ArrayList<Object>> lastCalledStatic){
        this.called = lastCalledStatic;
    }

    private final Triple<Class<?>, String, ArrayList<Object>> called;

    @Override
    public void thenReturn(R value){
        MockStaticCore.addReturn(called, value);
    }

    @Override
    public void thenThrow (Throwable exception) {
        MockStaticCore.addException(called, exception);
    }

    @Override
    public void thenNull() {

    }

    @Override
    public void thenImplemented() {

    }
}
