 package com.sap.engine.tools.sharecheck;
  
 
 /**
  *
  *
  *
  */ 
 public class ClassNamePrefixFilter implements ClassNameFilter{
    String prefix ;
    
    public ClassNamePrefixFilter(String prefix){
    	this.prefix= prefix ;
    }
    
    public boolean include(String className){
    	return className.startsWith(prefix);
    }
 }