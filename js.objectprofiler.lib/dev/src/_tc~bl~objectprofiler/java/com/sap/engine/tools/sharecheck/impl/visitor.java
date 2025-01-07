 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.*;
 
 /**
  *
  *
  *
  */
 public interface Visitor{
 
    public void visit(Object object ,LinkedList path) ;
    
    public void clear();
        
 }