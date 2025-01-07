 package com.sap.engine.tools.sharecheck;
 
 import java.util.* ;
 
 /**
  *
  *
  *
  */
 public interface ObjectReport{
    /**
     * List of Strings. Each string is a class name from the object path .
     * The first element in the path is the root object (http session)
     */
    public List getPath();
    
    public String getPathAsString();
    
    public String getClassName();
    
    /* 
     * List of Strings .Each string is an error message.
     */
    public List getProblems();
  
 
    public int getShareabilityProblemMask();
    
    public Throwable getSerializationError() ;
    public Throwable getShareabilityError() ;
    
 }
 

