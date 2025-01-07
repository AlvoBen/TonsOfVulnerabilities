 package com.sap.engine.tools.sharecheck.impl;
 
 import java.util.* ;
 import java.util.logging.*;
 import com.sap.engine.tools.sharecheck.* ;
  
 /**
  * VisitorLevel2 has to create list of classes which have shareability problems.
  * For each class there should be list of problems assigned.  
  *
  */
 public class VisitorLevel2  implements Visitor {
   
    static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.VisitorLevel2");
       
    ClassInfo classInfo ; 
    ClassNameFilter classFilter ;    
    int shareabilityFilter ; 
    List list = new LinkedList();
        
    HashMap classToReport = new HashMap();
    
    /** 
     *
     *
     *
     */
    public VisitorLevel2(ClassInfo classInfo , ClassNameFilter classFilter , int shareabilityFilter){
       this.classInfo = classInfo ;
       this.classFilter = classFilter ;
       this.shareabilityFilter = shareabilityFilter ;
    }  
    
    /**
     *
     *
     *
     */
    public void visit(Object obj,LinkedList path) {
       //ObjectInfo node= new ObjectInfo(obj) ;
       logger.log(Level.FINE, "visit" , obj);
       if(logger.isLoggable(Level.FINER)){
          logger.log(Level.FINER, "visit path" , ObjectInfo.toPathString(path));
       }
       
       Class clazz = obj.getClass();
       
       if(clazz.isArray()){
       	 return ;
       }
 
       if(clazz.isPrimitive()){
       	 return ;
       }
       
       String className = clazz.getName() ;
       if( (classFilter!=null) && (! classFilter.include(className) ) ){   
          if(logger.isLoggable(Level.FINER)){
          	logger.log(Level.FINER, "class not included by filter " + className );       	    
          }
       	  return ;
       }
       
       ClassReportImpl cr = (ClassReportImpl)classToReport.get(clazz);
       
       //If class report already done for this class
       if(cr!=null){
       	 return ; 
       }
       
       int shareability = classInfo.analyze(clazz);       
       int filtered_shareability = shareability & shareabilityFilter;
       
       //SessionSerializationReportImpl should decide which flag means problem.
       //Normally we have a problem if the flag is !=0  
       //If there are shareability problems detected 
       if(SessionSerializationReportImpl.isShareabilityProblem(filtered_shareability)){
         cr = new ClassReportImpl( clazz, filtered_shareability);	
         classToReport.put(clazz, cr );
         list.add(cr);
       }
                  
       if(logger.isLoggable(Level.FINE)){
           logger.log(Level.FINE, "analyze " + cr);       	    
       }
       
    }
    
    /** 
     *
     *
     *
     */
    public List getClassReport(){
    	
    	return list ;
    	
    }   
    
    public void clear(){
    	list = new LinkedList() ;
    	classToReport = new HashMap();
    }    
  
 }