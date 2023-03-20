package org.example;

import java.lang.reflect.Method;

public class CGLibTestRT<R> { //Return - throw
    public CGLibTestRT(CGLibTest<?> parent){
        this.parent = parent;
        this.called = parent.lastCalled;
    }

    private final CGLibTest<?> parent;
    private final Pair<Method, Object[]> called;

    public void thenReturn(R value){
        parent.addReturn(called, value);
    }

    public void thenThrow (Throwable exception) {
        parent.addException(called, exception);
    }
}
