 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.* ;
 import com.sap.engine.tools.sharecheck.* ;
  
 /**
  *
  *
  *
  */
 public class NonSerializableDescriptorVisitor  implements Visitor {
        
    //List objectList ;
    IdentityHashMap nonSerializableMap ; 
    IdentityHashMap objectToPath ;
      
    /** 
     *
     *
     */  
    public NonSerializableDescriptorVisitor(IdentityHashMap nonSerializableMap){       
       //this.objectList = new LinkedList();      
       this.nonSerializableMap = nonSerializableMap ;
       this.objectToPath = new IdentityHashMap(nonSerializableMap.size());
    }          
    
    /**
     *
     *
     *
     */
    public void visit(Object obj, LinkedList path) { 
       if(obj==null){
       	 return ;
       }
       NonSerializableDescriptor descr = (NonSerializableDescriptor)nonSerializableMap.get(obj);
       if(descr!=null){
       	 objectToPath.put(obj, path);       	 
       }       
    }
    
    public void clear(){
       nonSerializableMap.clear();
       objectToPath.clear();
    }

    /** 
     *
     *
     *
     */
    public IdentityHashMap getObjectToPathMap(){    	
    	return objectToPath;
    }   
    

 }