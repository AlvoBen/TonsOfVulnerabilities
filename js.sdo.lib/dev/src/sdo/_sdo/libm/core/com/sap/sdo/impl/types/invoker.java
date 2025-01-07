package com.sap.sdo.impl.types;

import java.lang.reflect.Method;

import commonj.sdo.DataObject;

public interface Invoker {
    
    int getPropertyIndex();
    boolean isModify();
    Object invoke(DataObject dataObject, Method method, Object[] args) throws Throwable; //$JL-EXC$

}
