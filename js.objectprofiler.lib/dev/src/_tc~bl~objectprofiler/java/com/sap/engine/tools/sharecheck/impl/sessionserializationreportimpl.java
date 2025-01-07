 package com.sap.engine.tools.sharecheck.impl;

import java.io.*;
import java.util.* ;
import java.util.logging.*;
import com.sap.engine.tools.sharecheck.* ;
 
/**
 *
 *
 *
 */
public class  SessionSerializationReportImpl implements SessionSerializationReport{

   static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.SessionSerializationReportImpl");
  
   Object root ;
   int filter ;
   int level ;
   
   ClassInfo classInfo ;
   ObjectOutputStreamInterceptor objectIt ; 
   Visitor visitor ; 
   
   int[] counts ;
   List[] allList ; 
   
   /**
    *
    *
    *
    */
   SessionSerializationReportImpl(Object root,int filter, int level,ClassNameFilter classFilter){
     this.root = root ;     
     this.level = level ;
     this.filter = filter; 
     this.classInfo = new ClassInfo();
     
     if(logger.isLoggable(Level.CONFIG)){
        logger.log(Level.CONFIG, "Shareability Analysis Filter= " + filter);
        logger.log(Level.CONFIG, "Report Level= " + level);
        logger.log(Level.CONFIG, "Class Filter= " + classFilter);
      }
      
     //boolean doPath = (level >=SessionSerializationReportFactory.OBJECT_LEVEL) ; 
     //this.treeIt = new TreeIterator(root , doPath);
     try{
       this.objectIt = new ObjectOutputStreamInterceptor( classInfo, new ByteArrayOutputStream(100 * 1024) , root) ;
       //this.objectIt = new ObjectOutputStreamInterceptor2( root) ;
     }catch(IOException io){
     	logger.log(Level.SEVERE,"Cannot create ObjectOutputStreamInterceptor " , io );
     	throw new RuntimeException("Cannot create OOS " , io) ;
     }
     
     switch(level){
     	case SessionSerializationReportFactory.NO_REPORT : break;
     	case SessionSerializationReportFactory.SUMMARY_LEVEL : {
     		visitor = new VisitorLevel1(classInfo);
     		break;
         }
     	case SessionSerializationReportFactory.APP_CLASS_LEVEL : {
     		visitor = new VisitorLevel2(classInfo, classFilter,filter);
     		break;
     	}
     	case SessionSerializationReportFactory.CLASS_LEVEL : {
     		visitor = new VisitorLevel2(classInfo, classFilter ,filter);
     		break;
     	}
     	case SessionSerializationReportFactory.OBJECT_LEVEL : {
     		visitor = new VisitorLevel4(classInfo,filter);
     		break;
     	}
     } ;
     
     if(visitor!=null){
     	//if(logger.isLoggable(Level.INFO)){
     	//   logger.log(Level.INFO, "Initial iteration ", objectIt);
     	// }
        objectIt.iterate(visitor);
     }
   }

   
  /**
   * Refresh the report to be up-to-date.
   * Normally has to be invoked after a http request.
   *
   */
  public void refresh(){
     //if(logger.isLoggable(Level.INFO)){
     //	 logger.log(Level.INFO, "Refresh ");
     //}

     try{
       //objectIt = new ObjectOutputStreamInterceptor2( root) ;
       this.objectIt = new ObjectOutputStreamInterceptor( classInfo , new ByteArrayOutputStream(100 * 1024) , root) ;
     }catch(IOException io){
     	logger.log(Level.SEVERE,"Cannot create ObjectOutputStreamInterceptor " , io );
     	throw new RuntimeException("Cannot create OOS " , io) ;
     }
          
     if(visitor!=null){
     	visitor.clear();
     	//if(logger.isLoggable(Level.INFO)){
     	//   logger.log(Level.INFO, "Initial iteration ", objectIt);
     	// }
        objectIt.iterate(visitor);        
     }     
  }
  
  /**
   * The method counts how many objects in the graph have the problem specified.
   *
   */
  public int getObjectCountForProblem(int problem){
  	if(level == SessionSerializationReportFactory.SUMMARY_LEVEL ){
  	   VisitorLevel1 v1 = (VisitorLevel1)visitor ;
  	   if(counts==null) { counts =v1.getShareableProblemCount(); }
  	   int cnt = 0  ;
  	   int filtered_problem = problem & filter; 
  	   for(int i=0;i<SessionSerializationReport.ALL.length;i++){
  	      if( (filtered_problem & SessionSerializationReport.ALL[i]) == SessionSerializationReport.ALL[i]){
  	      	cnt += counts[i];
  	      }//if problem
  	   }//for
  	   return cnt;
  	}//if level
  	
  	throw new RuntimeException("Cannot report counts on level " + level );   	
  }
  
  
  /** 
   * Return list of classes which have the problem specified in parameter problem.
   * filter - a class filter.
   *
   */
  public List getClassesForProblem(int problem) {
     if( (level == SessionSerializationReportFactory.CLASS_LEVEL) || (level == SessionSerializationReportFactory.APP_CLASS_LEVEL)){
     	VisitorLevel2 v2 = (VisitorLevel2)visitor ;
     	List list  = v2.getClassReport() ;
     	
     	List list2 = new LinkedList(); 
     	
     	for(Iterator i=list.iterator();i.hasNext();){
  	      ClassReport cr = (ClassReport)i.next();
  	      int mask = cr.getShareabilityProblemMask();
  	      if( (mask | problem) == problem ){
  	      	 list2.add(cr.getClassObject().getName());
  	      }
  	}//for
  	
  	
  	Collections.sort(list2);
  	
  	   
  	return list2 ;
  	   
     }//if level 
     
     throw new RuntimeException("Cannot report class list on level " + level );
  }
  
  /** 
   *
   *
   *
   *
   */
  public List getClassReport(){
      boolean accept = (level == SessionSerializationReportFactory.CLASS_LEVEL) || (level == SessionSerializationReportFactory.APP_CLASS_LEVEL) ;
      if( ! accept){
      	throw new RuntimeException("Cannot report class list on level " + level );
      }
      
      VisitorLevel2 v2 = (VisitorLevel2)visitor ;
      
      List list = v2.getClassReport() ;
      List list2 = new LinkedList(); 
     	
     	for(Iterator i=list.iterator();i.hasNext();){
  	      ClassReport cr = (ClassReport)i.next();
  	      int mask = cr.getShareabilityProblemMask();
  	      if( (mask | filter) == filter ){
  	      	 list2.add(cr);
  	      }
  	}//for
  	
  	
  	//Collections.sort(list2);
  	
  	   
      return list2 ;
      //return ;
  }
 
  
  /**
   *  Returns list of objects with the problems specified.
   *
   *
   */
  public List getFullObjectReport(){
     if(level == SessionSerializationReportFactory.OBJECT_LEVEL ){
  	  VisitorLevel4 v4_s = (VisitorLevel4)visitor ;
  	  List list = v4_s.getObjectList();  	  
  	  return list;
     }
     
     throw new RuntimeException("Cannot report class list on level " + level );
  }
  
  
  public int getSessionSizeInBytes(){
  	return objectIt.getSizeInBytes();
  }
  
  
   /** 
    *
    *
    *
    */
   public  static boolean isShareabilityProblem(int shareable){    	
    	if(shareable==0){
    	  return false;
    	}else if(shareable==SessionSerializationReport.DEFINED_SHAREABLE){
    	  return false ;
    	}else{
    	  return true;
    	}
  }  
  
  /** 
    *
    *
    *
    *
    *
    */
   public static List getProblemList( int flag ){
    	LinkedList list = new LinkedList();
    	
    	if( (flag & NON_SHAREABLE_CLASSLOADER) == NON_SHAREABLE_CLASSLOADER){
    	   list.add(NON_SHAREABLE_CLASSLOADER_DESCR) ;
    	}
    	
    	if( (flag & NON_TRIVIAL_FINALIZER) == NON_TRIVIAL_FINALIZER){
    	   list.add(NON_TRIVIAL_FINALIZER_DESCR) ;
    	}
    	
    	if( (flag & NON_SERIALIZABLE_PARENT) == NON_SERIALIZABLE_PARENT){
    	   list.add(NON_SERIALIZABLE_PARENT_DESCR) ;
    	}
    	
    	if( (flag & CUSTOM_SERIALIZATION_CLASS) == CUSTOM_SERIALIZATION_CLASS){
    	   list.add(CUSTOM_SERIALIZATION_CLASS_DESCR) ;
    	}
    	
    	if( (flag & SERIAL_PERSISTENT_FIELD) == SERIAL_PERSISTENT_FIELD){
    	   list.add(SERIAL_PERSISTENT_FIELD_DESCR) ;
    	}
    	
    	if( (flag & HAS_TRANSIENT_FIELDS) == HAS_TRANSIENT_FIELDS){
    	   list.add(HAS_TRANSIENT_FIELDS_DESCR) ;
    	}    	    	    	    	    	
    	
    	if( (flag & NON_SHAREABLE_OTHER) == NON_SHAREABLE_OTHER){
    	   list.add(NON_SHAREABLE_OTHER_DESCR) ;
    	}    	    
    	
    	if( (flag & NON_SERIALIZABLE_CLASS) == NON_SERIALIZABLE_CLASS){
    	   list.add( NON_SERIALIZABLE_CLASS_DESCR ) ;
    	}	    	    	    	    	
    	
    	if( (flag & NON_SERIALIZABLE_OTHER) == NON_SERIALIZABLE_OTHER){
    	   list.add( NON_SERIALIZABLE_OTHER_DESCR ) ;
    	}	    	    	    	    	
    	
    	return list;

   } 
  
}