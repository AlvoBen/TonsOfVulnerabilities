 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.*;
 import java.lang.reflect.*;
 
 
 public interface ObjectIterator {
 
  public void iterate(Visitor v) ;
  
  public int getSizeInBytes() ;
 
 }