 package com.sap.engine.tools.sharecheck;
 
 import java.util.* ;
 
 /**
  *
  *
  *
  */
 public interface ClassReport{
 
    public Class getClassObject() ;
    /* 
     * List of Strings .Each string is an error message.
     */
    public List getProblems();
        
    public int getShareabilityProblemMask();
        
    public Throwable getShareabilityError() ;
    
 }
 

