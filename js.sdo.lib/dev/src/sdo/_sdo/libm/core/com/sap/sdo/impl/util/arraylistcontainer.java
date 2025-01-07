package com.sap.sdo.impl.util;

public interface ArrayListContainer <E> {
    
    E[] getArray();
    void setArray(E[] array);
    
    E[] createArray(int length);
    
    int size();
    void setSize(int size);
    
    void increaseModCount();

}
