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
        parent.addAction(called, value);
    }

    public void thenThrow(Class<? extends Exception> exception){

    }
}
