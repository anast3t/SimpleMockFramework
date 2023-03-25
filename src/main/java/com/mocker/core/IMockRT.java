package com.mocker.core;

public interface IMockRT <R> {
    public void thenReturn(R value);
    public void thenThrow(Throwable exception);
    public void thenNull();

    public void thenImplemented();
}
