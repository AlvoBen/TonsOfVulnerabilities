 package com.sap.engine.tools.sharecheck.impl;
 
 import java.io.*;
 import java.util.*;
 import java.lang.reflect.*;
 import com.sap.engine.tools.sharecheck.SessionSerializationReport;
 
 
 /**
  *
  * This class is used to supply path information to all objects. 
  * This is used only by Level 4 reporting to show "bad" objects locations. 
  *
  */
 public class ObjectTreeIterator implements ObjectIterator{
 
   //Fields of type from the list below would not be analyzed
   final static Class[] CLASSES_TO_SKIP = {String.class , StringBuffer.class , Integer.class , Boolean.class , Long.class ,
                    Float.class , Double.class , Character.class ,Short.class } ;

  //An object of type from the list below would not be iterated further - tree leaf.   
  //Means that an instance of such a class will be interprated as value rather than a graph element.                  
  final static Class[] LEAF_CLASSES = { BitSet.class , Class.class , Date.class , java.io.File.class ,
                    java.sql.Date.class ,java.sql.Time.class,java.sql.Timestamp.class, 
                    java.math.BigDecimal.class ,java.math.BigInteger.class , 
                    java.net.URL.class ,  java.net.InetAddress.class , java.net.Inet4Address.class ,java.net.URI.class,
                    java.text.SimpleDateFormat.class , java.text.NumberFormat.class } ;
     
     
   static HashSet leafClassSet = new HashSet();      
   static HashSet skipClassSet = new HashSet();
   
   static{
     
     for(int i=0;i<CLASSES_TO_SKIP.length;i++){
     	skipClassSet.add(CLASSES_TO_SKIP[i]);
     }//for i
     
     for(int i=0;i<LEAF_CLASSES.length;i++){
     	leafClassSet.add(LEAF_CLASSES[i]);
     }//for i
   }
   
   /**
    *
    *
    *
    *
    */    
   Object root; //The root of the tree (Session object)
   IdentityHashMap objMap ; // the map contains iterated objects as keys and path as value
   boolean doPath = false ; 
   int totalBytes ; // to be removed. 
   
   //Class shareability analyzer. 
   //We used it here to check if a class has a custom serialization
   //If a class has no custom serialization , then we do not follow transient references  since they cannot be used 
   //in serialization process.
   //This can be considered as major performance optimization.
   //If a class has a custom serialization , we have to follow transient references. 
   ClassInfo classInfo ;  
   
   
   
   /**
    *
    *
    */
   public ObjectTreeIterator(ClassInfo classInfo, Object root, boolean doPath ){
   	this.root = root ; 
   	this.doPath = doPath ;  	
   	this.classInfo= classInfo ; 
   }

   public int getSizeInBytes() {
   	return totalBytes;
   }


   
   IdentityHashMap getObjectMap(){
   	return objMap ;
   }
  
   /**
    *
    * This is the main method - scan the graph and collect path information to the objects in the graph. 
    * The visitor does not play an important role (currently).
    */
   public void iterate( Visitor visitor){
   	objMap = new IdentityHashMap(5023);
   	LinkedList path = null ;   	
   	LinkedList levelList = new LinkedList() ;
   	   	
   	if(doPath){
   	   path = new LinkedList();
   	   path.add(root.getClass().getName());
   	   path.add(root);
   	   levelList.add(path);
   	}else{
   	   path = null ;
   	   levelList.add(root);
   	}
   	
   	for(int lev=0;;lev++){
   	  //The iteration iterate current level and collect the next level(the children of this level)
   	  //The outer loop iterates tree levels , while the inner loop iterates the nodes on the given level
   	  LinkedList newLevelList = new LinkedList();
   	  
   	  //System.out.println("--------------- Level " + lev ); 
   	  
   	  for(Iterator i=levelList.iterator();i.hasNext();){
   	     //In this loop we visit the objects on the current level and collect the childred(next tree level)
   	     Object _obj =  i.next();	
   	     Object obj = null ;
   	        	     
   	     if(doPath){
   	     	path = (LinkedList)_obj ;
   	     	obj = path.removeLast();
   	     }else{
   	        path = null ;
   	        obj = _obj ;
   	     }
   	   	     
   	     if(obj==null){
   	     	continue ;
   	     }
   	     
   	     //if obj already visited
   	     if(objMap.containsKey(obj)){
   	     	continue ;
   	     }
   	     
   	     //Mark object as visited
   	     objMap.put(obj,path);
   	     
   	     
   	     //System.out.println("obj= " + obj ); 
   	     //System.out.println("path= " + path ); 
   	     visitor.visit(obj, path);
   	     
   	     
   	     //Navigate to object references
   	     //1.Check if the class is a special collection class or array or map 
   	     //2.Check if the class is a data class
   	     //3.generic field navigation
   	     Class clazz = obj.getClass() ;
   	     
   	     try{
   	     if(clazz.isArray()){      	      	
      	       iterateArray(obj, visitor, path ,newLevelList);      	
      	       continue;      	       
             }
      
             if(Collection.class.isAssignableFrom(clazz)){
             	iterateCollection(obj,visitor,path,newLevelList);
             	continue ;
             }
             
             if(Map.class.isAssignableFrom(clazz)){
             	iterateMap(obj,visitor,path,newLevelList);
             	continue ;
             }
             
             //If clazz is a value class - Date, Integer , BitSet etc.
             if(leafClassSet.contains(clazz)){
             	continue ;
             }
   	    
   	     //Navigate to fields - generic approach    	     
   	     collectFields(obj, clazz , path , newLevelList);
   	     
   	     }catch(Throwable x){
   	     	//Path resolve should continue, no need to brake   
   	     	//We had a case : NoClassDefFoundError thrown on attempt to get fields of a given class
   	     	//This is not an error related to the logic in this library , hence we just report the error 
   	     	//and try to get as deep in the tree as possible. 	     	
   	     	x.printStackTrace();
   	     }
   	  }//for i
   	  
   	  
   	  //If we cannot collect more children, then the tree iteration is over -> exit
   	  //else we iterate the new tree level
   	  if(newLevelList.size()==0){
   	    break ;
   	  }else{
   	     levelList = newLevelList ;
   	  }
   	  
   	}//for
   	
   }

   
   
   /**
    *
    * 
    *
    *
    */
    private void collectFields(Object obj, Class clazz,LinkedList path, LinkedList list){   	   	
   	for(;clazz!=null;clazz=clazz.getSuperclass()){
   	   Field[] f=clazz.getDeclaredFields();
   	   for(int i=0;i<f.length;i++){
   	      Class type = f[i].getType();
   	      //System.out.println("[Tree] : field of type " + type ) ;      	 
   	      int mods = f[i].getModifiers();
   	      
   	      //Static fields does not represent 
   	      //persistent object state.              
              if( Modifier.isStatic(mods) )  {
                continue;
              }
              
              if( Modifier.isTransient(mods) ){
              	//Get class shareability mask.
              	//Most probably the info is cached.
              	int shv = classInfo.analyze(type);
              	
              	//If the class does not have custom serialization
              	if( (shv & SessionSerializationReport.CUSTOM_SERIALIZATION_CLASS) != SessionSerializationReport.CUSTOM_SERIALIZATION_CLASS){
              	  //means the type has no custom serialization 
              	  //and hence a transient reference should not be analyzed. 
              	  //The Serialization protocol would ignore such a field , hence we ignore it also.
              	  continue; 
              	}
              }
              
              if( shouldSkipClass(type) ){
                continue ;
              }  
                                            
   	      
   	      //Make a private field accessible.
   	      f[i].setAccessible(true);   	      
   	         	     
   	     
   	      try{
   	        //Get the field
      	        Object obj2 = f[i].get(obj);
      	   
      	        if(obj2==null){
      	          continue;
      	        }     	 
      	
         	if(doPath ){
      	          LinkedList pathc = (LinkedList)path.clone();
      	          pathc.add( f[i].getName() + "(" + obj2.getClass().getName() + ")");
      	          pathc.add(obj2);
      	          list.add(pathc);
      	        }else{
      	          list.add(obj2);
      	        }
      	 
      	        
      	      }catch(Throwable t){
      	      	//we keep looking in the graph
      	        t.printStackTrace();
      	      }    	     
   	     
   	   }//for   	   
   	}//for   	
   }

     
   /** 
    * The classes like String , Date , BitSet has to be skipped. 
    * The same is for array of String , Date , BitSet and array of array of these types. 
    * We consider them as value classes (records) which has semantic close to primitives. 
    */
   private boolean shouldSkipClass(Class clazz){     
     if(clazz.isPrimitive()){
     	totalBytes += 8 ; 
     	return true ;
     }
         
     
     if(skipClassSet.contains(clazz)){
     	return true;      	
     }
     
     Class componentType = clazz ;     
     while(componentType.isArray()){
        componentType = componentType.getComponentType();
      }
     
      if(componentType.isPrimitive()){
       	 return true;
      }        
       
      if(skipClassSet.contains(componentType)){
     	 return true;      	
      }
     
     return false ;
   }
   
   /**
    *
    * The array members are collected as children in the list parameter. 
    *
    *
    */
   private void iterateArray(Object array,Visitor visitor,LinkedList path,LinkedList list){
   	int length = Array.getLength(array);   	
   	totalBytes += length*4 + 8 ; 
   	
   	for(int i=0;i<length;i++){   	  
   	  Object obj_i = Array.get(array, i);
          
          if(obj_i==null) continue;  
            
   	  if( doPath ){
   	     LinkedList pathc = (LinkedList) path.clone() ;   
             Class clazz_i = obj_i.getClass();	                  	 	  
   	     String str_i = (clazz_i==null)? obj_i.toString() : clazz_i.getName() ;
             pathc.add( "[array "+i+" ](" + str_i + ")"  ) ;
             pathc.add(obj_i);
             list.add(pathc);
   	   }else{
   	     list.add(obj_i);
   	   }
   	  //iterate0(obj_i , visitor ,pathc , depth +1 );
   	}//for i
   }

   /**
    *  The collection members are collected as children in the list parameter.
    * A problem here could be a class which implements Collection interface, but 
    * has a members which are not accessible through collection interface.
    *
    */
   private void iterateCollection(Object coll,Visitor visitor,LinkedList path, LinkedList list){
   	if( ((Collection)coll).size()==0 ) return;
   	//System.out.println("Iterate collection ..." );
   	LinkedList pathc = path ;
   	totalBytes += ((Collection)coll).size()*4 + 3*4 + 2*8 ; 
   	int j=0 ;
   	
   	for(Iterator i = ((Collection)coll).iterator(); i.hasNext() ; j++ ){
   	   Object o = i.next();
   	   if(o==null){
   	   	continue;
   	   }
   	   
   	   if( shouldSkipClass(o.getClass() ) ){
   	   	continue;
   	   }
   	 
   	   //System.out.println("add " + o);
   	   
   	   if( doPath ){   	      
   	      pathc = (LinkedList) path.clone() ;
   	      pathc.add( "[element " +j + "](" + o.getClass().getName() + ")") ;
   	      pathc.add(o);
   	      list.add(pathc);
   	   }else{
   	      list.add(o);
   	   }     	   
   	   //iterate0(o,visitor,pathc,depth+1) ;
   	}   	
   }      
   
   /**
    *  The map keys and values are collected as children in the list parameter.
    * A problem here could be a class which implements Map interface, but 
    * has a members which are not accessible through collection interface.
    *
    */
   private void iterateMap(Object map,Visitor visitor,LinkedList path,LinkedList list){
   	if( ((Map)map).size()==0 ) return ;
   	LinkedList pathc = path ;
        totalBytes += ((Map)map).size()*24 + 3*8 ;    	   	
        
   	Set entrySet = ((Map)map).entrySet() ;
   	for(Iterator i = entrySet.iterator(); i.hasNext() ; ){
   	   Map.Entry  e = (Map.Entry) i.next();
   	   
   	   Object k = e.getKey();
   	   if( (k!=null) && (!shouldSkipClass(k.getClass())) ) {
   	      if( doPath ){
   	      	pathc = (LinkedList) path.clone() ;
   	      	pathc.add( "[key](" + k.getClass().getName() + ")") ;
   	      	pathc.add(k);
   	      	list.add(pathc);
   	      }else{
   	        list.add(k);
   	      }   	      
   	      //iterate0(k,visitor,pathc,depth+1) ;
   	   }
   	   
   	   Object v = e.getValue();
   	   if( (v!=null) && (!shouldSkipClass(v.getClass())) ) {
   	      if( doPath ){
   	      	pathc = (LinkedList) path.clone() ;
   	      	pathc.add( "[value](" + v.getClass().getName() + ")") ;
   	      	pathc.add(v);
   	      	list.add(pathc);
   	      }else{
   	         list.add(v);
   	      }  	      	      	  
   	      //iterate0(v,visitor,pathc,depth+1) ;
   	   }   	   
   	}   	
   }         

}

