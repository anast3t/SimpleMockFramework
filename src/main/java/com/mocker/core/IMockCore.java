package com.mocker.core;

import com.mocker.utils.ActionType;
import com.mocker.utils.Pair;
import com.mocker.utils.Triple;

import java.util.ArrayList;

public interface IMockCore <EventObjectSignature> {
    public void addReturnAction(EventObjectSignature classMethodArgs, Object returnValue);

    public void addExceptionAction(EventObjectSignature classMethodArgs, Throwable returnValue);

    public void addNullAction(EventObjectSignature classMethodArgs);

    public void addImplementedAction(EventObjectSignature classMethodArgs);
}
