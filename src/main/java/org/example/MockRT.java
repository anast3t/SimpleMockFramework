package org.example;

import java.lang.reflect.Method;

public class MockRT<R> { //Return - throw
    public MockRT(MockCoreInstance<?> parent){
        this.parent = parent;
        this.called = parent.lastCalled;
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
