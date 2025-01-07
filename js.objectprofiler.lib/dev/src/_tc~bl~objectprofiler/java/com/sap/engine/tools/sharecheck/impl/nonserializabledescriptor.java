 package com.sap.engine.tools.sharecheck.impl;
 
import java.util.* ;
import java.io.* ;

/**
 *
 *
 *
 */
public class NonSerializableDescriptor implements Serializable {
   
   String className ;
   LinkedList path ; 
   
   public NonSerializableDescriptor(String className){
     this.className = className ;
   }

   public String getClassName(){
     return className ;
   }      
   
   public void setPath(LinkedList path){
   	this.path = path ;
   }
   
   public LinkedList getPath(){
   	return path;
   }
   
}