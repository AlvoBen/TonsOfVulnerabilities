 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.* ;
 import java.util.logging.*;
 import com.sap.engine.tools.sharecheck.* ;
  
 /**
  *
  *
  *
  */
 public class VisitorLevel4  implements Visitor {
    
   static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.VisitorLevel4");    
    
    ClassInfo classInfo ;  
    List objectList ;
    int shareabilityFilter ;
    
    /**  
     *
     *
     */     
    public VisitorLevel4(ClassInfo classInfo,int shareabilityFilter){
       this.classInfo = classInfo ; 
       this.objectList = new LinkedList();      
       this.shareabilityFilter = shareabilityFilter;
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
       
       Class clazz = obj.getClass() ;
       if(clazz.isArray()){
       	 return ;
       }
 
       if(clazz.isPrimitive()){
       	 return ;
       }
       
       logger.log(Level.FINE, "visit" , obj);
       if(logger.isLoggable(Level.FINER)){
       	 logger.log(Level.FINER, "visit path" , path);
       }
       
       int shp = 0 ;
       try{
         shp = classInfo.analyze(clazz);
       }catch(Throwable t){
       	 shp = SessionSerializationReport.NON_SHAREABLE_OTHER ; 
       }
       
       if(logger.isLoggable(Level.FINE)){
          logger.log(Level.FINE, "full shareability mask = " + shp );
       }
       
       int filtered_shareability = shareabilityFilter & shp ; 
       
       if( SessionSerializationReportImpl.isShareabilityProblem(filtered_shareability) ){
       	  ObjectInfo node= new ObjectInfo(filtered_shareability, obj) ; 
          node.path = path ;
       	  objectList.add(node);
       }
              
    }
    
    /**
     *
     *
     *
     */
    public List getObjectList(){    	
    	return objectList;
    }   
    
    /** 
     *
     *
     */
    public void clear(){
    	objectList = new LinkedList();
    }    

 }