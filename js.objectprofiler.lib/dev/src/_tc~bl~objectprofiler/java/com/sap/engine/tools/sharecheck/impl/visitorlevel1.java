 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.* ;
 import java.util.logging.* ;
 import com.sap.engine.tools.sharecheck.* ;
  
 /**
  *
  *
  *
  */
 public class VisitorLevel1 implements Visitor {
     
     static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.VisitorLevel1");
   
    ClassInfo classInfo ; 
    int[] non_shareable_problem_count = new int[SessionSerializationReport.ALL.length] ;
    HashSet classHS = new HashSet();
    
    /** 
     *
     *
     */
    public VisitorLevel1(ClassInfo classInfo){
       this.classInfo = classInfo ;
    }  
    
    
    /**
     *
     *
     *
     *
     */
    public void visit(Object obj ,LinkedList path) {   
       if(obj==null){
       	  return ;
       }
       
       Class clazz = obj.getClass(); 
       if(clazz.isArray()){
       	 return ;
       }
 
       if(clazz.isPrimitive()){
       	 return ;
       }

       String className = clazz.getName(); 
       
       //To do : consider the case with two classes with same names and different classloaders.
       //If the class was already iterated/ counted
       if(classHS.contains(className)){       	 
       	  return;
       }
       classHS.add(className);
       
       int shp = classInfo.analyze( clazz );
        
       if(logger.isLoggable(Level.FINE)){
           logger.log(Level.FINE, "analyze class " + clazz.getName() + " - " + shp );       	    
       }       

       for( int i=0;i<SessionSerializationReport.ALL.length ;i++){
       	  int p = SessionSerializationReport.ALL[i] ;
          if( (shp & p) == p ){
             non_shareable_problem_count[i]++;
          }	 
       }//for i
    }
    
    /** 
     *
     *
     *
     */
    public int[] getShareableProblemCount(){
    	return non_shareable_problem_count; 
    }   
    
    
    public void clear(){
    	non_shareable_problem_count = new int[SessionSerializationReport.ALL.length] ;
    	classHS = new HashSet();
    }

 }