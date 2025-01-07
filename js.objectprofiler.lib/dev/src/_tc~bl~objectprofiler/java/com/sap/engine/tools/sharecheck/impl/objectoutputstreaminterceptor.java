 package com.sap.engine.tools.sharecheck.impl ;
 
 import java.io.*;
 import java.util.*;
 import java.util.logging.*;
 
 /**
  * This is a intercepted serialization stream. 
  * The goal is to serialize the whole session . The non-serializable objects which are in the session will be replaced 
  * by dummy objects so the serialization would not brake and the whole session will be analuzed. 
  *
  */
 public class ObjectOutputStreamInterceptor extends ObjectOutputStream implements ObjectIterator {
   static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.ObjectOutputStreamInterceptor");
   
      
   Visitor visitor ; // Level visitor VisitorLevel1, VisitorLevel2 , VisitorLevel4
   Object root ; //the session object 
   
      
   LinkedList path =  new LinkedList();//to be removed
   ByteArrayOutputStream buffer ; //the end buffer where the session is kept as byte[]
   ClassInfo classInfo ;  //ClassInfo repository
      
   /** 
    *
    *
    */
   public ObjectOutputStreamInterceptor(ClassInfo classInfo, ByteArrayOutputStream buffer0, Object root) throws IOException {
   	super(buffer0);
   	this.buffer = buffer0 ;
   	enableReplaceObject(true);
   	this.root = root ;
   	this.classInfo = classInfo ;
   }
   
   /** 
    *
    *
    *
    *
    */
   public void iterate(Visitor visitor){
   	this.visitor = visitor ;
   	boolean success = false ;
   	
   	try{
   	     writeObject(root);   	     
   	}catch(Exception e){   	     
   	     logger.log(Level.SEVERE, "Error in serialization " , e);
   	     throw new RuntimeException("Error in serialization" ,e );   	        	     
   	}
          	       	  
        //byte[] ba = buffer.toByteArray();
        
        if( visitor instanceof VisitorLevel4 ) {
           resolvePathForLevel4( (VisitorLevel4)visitor );
        } 	  
  	
   }
   
   /** 
    *
    *
    *
    */
   private void resolvePathForLevel4(VisitorLevel4 visitor4){        
  	List list = visitor4.getObjectList() ; //List of ObjectInfo - objects that has a problem 
  	
  	try{  	    
  	   boolean doPath = true ;
  	   ObjectTreeIterator treeIt = new ObjectTreeIterator(classInfo, root, doPath );
  	   treeIt.iterate(new Visitor(){
  	         public void visit(Object obj, LinkedList path) { }              
                 public void clear(){ }
  	   });
  	   
  	   IdentityHashMap objectToPath = treeIt.getObjectMap();
  	     	   
  	   for(Iterator i=list.iterator();i.hasNext();){
  	      ObjectInfo objInfo = (ObjectInfo)i.next();
  	      
  	      LinkedList path = (LinkedList)objectToPath.get( objInfo.getObject() );  	        	      
  	      objInfo.setPath(path);
  	   }//for i
  	   
  	  }catch(Exception x){
  	    logger.log(Level.SEVERE,"error in path resolve " , x );  	    
  	  }           	
   }
   
   /** 
    *
    *
    *
    *
    */
   public final Object replaceObject(Object obj){      
      logger.log(Level.FINE , "replaceObject" , obj);
      //Object rep= obj ; 
      
      //if( !shouldSkipClass(obj.getClass()) ){
      visitor.visit(obj , path );
      //}
      
      if( ! (obj instanceof Serializable) ){        
      	//rep = nonSerializableMap.get(obj);//NonSerializableDescriptor 
      	//if(rep==null){
        return new NonSerializableDescriptor( obj.getClass().getName() ) ;
        //   logger.log(Level.FINE , "new NonSerializableDescriptor for" , obj);
        //   nonSerializableMap.put(obj, rep);
        //}
      }else{        
        return obj;
      }
      
      //return rep;
   }
   
   
   
   
   public int getSizeInBytes(){
   	return buffer.size();
   }
   

  
 }