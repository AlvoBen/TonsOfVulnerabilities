 package com.sap.engine.tools.sharecheck.impl ;
 
 import java.io.*;
 import java.util.*;
 import java.lang.reflect.*;
 import java.util.logging.*;
 
 /**
  *
  *
  *
  *
  */
 public class ObjectOutputStreamInterceptor2 extends ObjectOutputStream implements ObjectIterator {
  

   
   static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.impl.ObjectOutputStreamInterceptor2");
   static final int DEFAULT_SIZE_IN_KBYTES = 10 ;
      
   static final int NULL = 1;
   static final int POINTER = 2;    
   static final int STRING = 3;        
   static final int CLASS = 4;      
   static final int OBJECT_STREAM_CLASS = 5;      
   static final int ARRAY = 6;  
   static final int ANY = 7;  

   static Integer[] PointerID = new Integer[100000] ;
   static{
      for(int i=0;i<PointerID.length;i++){
      	 PointerID[i] = new Integer(i);
      }//for 
   }
      
   Visitor visitor ;
   Object root ;
   Object lastIntercepted ;
   
   IdentityHashMap nonSerializableMap = new IdentityHashMap(5023); // object --> NonSrializableDescriptor
   IdentityHashMap pointerMap = new IdentityHashMap(5023);//Object map : objects written --> identifier
   int nextPointerId = 0;
   IdentityHashMap subMap = new IdentityHashMap(5023);//Substitution Map : object --> object  
   //HashMap classToFieldsMap= new HashMap() ;
     
   ThreadLocal threadLoc = new ThreadLocal(); 
   LinkedList path =  new LinkedList();
   DataOutputStream dOut;
   ByteArrayOutputStream buffer ; 
   int depth=0; 
    
   /** 
    *
    *
    */
   public ObjectOutputStreamInterceptor2(Object root) throws IOException {
   	super();
   	//this.buffer = buffer0 ;
   	this.buffer = new ByteArrayOutputStream( 1024 * DEFAULT_SIZE_IN_KBYTES );
   	this.dOut = new DataOutputStream(buffer);
   	//enableReplaceObject(true);
   	this.root = root ;
   }
   
   /**
    *
    *
    */
   public int getSizeInBytes(){
   	return buffer.size();
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
  	
   }
   
   public final void writeObjectOverride(Object obj) throws IOException {
   	LinkedList path = (LinkedList)threadLoc.get();
   	if(path==null){
   		path = new LinkedList();
        }
   	path.add(obj.getClass().getName());
   	writeObjectImpl(obj , path );
   }
   
   /**
    *
    *
    *
    *
    *
    */
   private void writeObjectImpl(Object obj, LinkedList path) throws IOException {
	//boolean oldMode = bout.setBlockDataMode(false);
	//System.out.println("write object "  + obj );
	depth++;
	try {
	        
	    if(obj!=null){
	    	Object rObj = subMap.get(obj);
	    	obj = (rObj==null) ? obj : rObj ;
	    }
	    
	    boolean writtenAsSpecial = tryWriteSpecial(obj,path);
	    
	    if(writtenAsSpecial){
	    	//System.out.println("writtenAsSpecial= "  + writtenAsSpecial );
	    	return ;
	    }
	    
	    
	    Class clazz = obj.getClass();
	    StreamClass sclazz = StreamClass.lookup(clazz); 
	    //System.out.println("class= "  + clazz.getName() );
	    
	    Object replacedObj = obj ;	    
	    Class replacedClass = clazz ;
	    StreamClass replacedStreamClass = sclazz ;
	    	     
	    while( replacedStreamClass.hasWriteReplace() ){	    	 
	    	  try{
	    	    replacedObj =  replacedStreamClass.invokeWriteReplace(replacedObj);
	    	  }catch(Exception x){
	    	    logger.log(Level.SEVERE, "Cannot invoke writeReplace " ,x );
	    	    return ;
	    	  }
	    	  path.add("->writeReplace->");
	    	 
	    	  if(replacedObj==null){
	    	   break;
	    	  }	
	    	  	    	  
	    	  path.add(replacedObj.getClass().getName());    	  
	    	  visitor.visit(replacedObj, path);
	    	  
	    	  replacedClass = replacedObj.getClass() ;
	    	  replacedStreamClass = StreamClass.lookup(replacedClass) ;
	    }//while
	    
	    //System.out.println("Replaced = "  + replacedObj );
	    
	    // if object replaced, run through original checks a second time
	    if (replacedObj != obj) {
		subMap.put(obj, replacedObj);		
		boolean writtenAsSpecial_Replaced = tryWriteSpecial(replacedObj,path);	    
	       if(writtenAsSpecial_Replaced){
	    	  return ;
	       }
	    }
	    
	    
	    if (replacedObj instanceof Serializable) {
	    	//LinkedList pathC = (LinkedList)path.clone();
	    	visitor.visit(replacedObj, path);
	    	//System.out.println("writeAny ");
		writeAny(replacedObj, replacedStreamClass , path );
	    } else {
	    	//System.out.println("Non serializable" );
	    	visitor.visit(replacedObj, path);
		//NonSerializableDescriptor nonSerializableDescr = new NonSerializableDescriptor(replacedClass.getName());
	    }
	} finally {
	    depth--;	    
	}   	
   }
   
   /**
    *
    *
    *
    *
    */
   private boolean tryWriteSpecial(Object obj,LinkedList path) throws IOException {
	    if(obj==null){
	    	//System.out.println("write NULL");
	    	dOut.writeInt(NULL);
	    	return true;
	    }
	
	    Integer pointerId = (Integer)pointerMap.get(obj) ;
	    if( pointerId !=null ){
	    	//System.out.println("write POINTER");
	    	dOut.writeInt(POINTER);
	    	dOut.writeInt(pointerId.intValue());
	    	return true;
	    }
	
	    if(obj instanceof String){	 
	       //System.out.println("write STRING");
	       pointerMap.put(obj,nextPointerID());   	
	       dOut.writeInt(STRING);
	       dOut.writeUTF( (String)obj );	
	       return true ;
	    }
	    
	    if (obj instanceof Class) {	  
	    	//System.out.println("write CLASS");
	    	pointerMap.put(obj,nextPointerID());   	
	    	dOut.writeInt(CLASS);  	
	    	dOut.writeUTF( ((Class)obj).getName() );
	    	//ObjectStreamClass osc = ObjectStreamClass.lookup( (Class)obj , true) ;
	    	//writeObjectStreamClass( osc );
		return true ;
	    }
	    
	    if (obj instanceof ObjectStreamClass) {
	    	//System.out.println("write OSC");
	    	pointerMap.put(obj,nextPointerID());   	
		dOut.writeInt(OBJECT_STREAM_CLASS);		
	        Class clazz = ((ObjectStreamClass)obj).forClass();	
	        dOut.writeUTF( clazz.getName() );	
		return true;
	    }
	    
	    Class cz = obj.getClass() ;
	    if( cz.isArray() ){
	    	//System.out.println("write ARRAY " + cz);
	    	pointerMap.put(obj,nextPointerID());   	
	    	writeArray(obj,path);
	    	return true; 
	    }
	    
   	return false ;
   }

  
   /** 
    *
    *
    *
    *
    */
   private Integer nextPointerID(){   	
   	int id = nextPointerId;
   	nextPointerId++ ;
   	
   	if(id<PointerID.length){
   	   return PointerID[id];
   	}else{
   	  return new Integer(id);
   	}
   }


    
   /**
    *
    *
    *
    *
    */ 
   private void writeArray(Object obj,LinkedList path) throws IOException {
    	Class ctype = obj.getClass().getComponentType();
	if( ctype.isPrimitive() ){
	    if ( Integer.TYPE.equals(ctype) ) {
		int[] array = (int[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeInt(array[i]);
		}
	    } else if (Byte.TYPE.equals(ctype)) {
		byte[] array = (byte[]) obj;
		dOut.writeInt(array.length);
		dOut.write(array, 0, array.length);
	    } else if (Long.TYPE.equals(ctype)) {
		long[] array = (long[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeLong(array[i]);
		}
	    } else if (Float.TYPE.equals(ctype) ) {
		float[] array = (float[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeFloat(array[i]);
		}
	    } else if (Double.TYPE.equals(ctype) ) {
		double[] array = (double[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeDouble(array[i]);
		}
	    } else if (Short.TYPE.equals(ctype) ) {
		short[] array = (short[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeShort(array[i]);
		}
	    } else if (Character.TYPE.equals(ctype) ) {
		char[] array = (char[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeChar(array[i]);
		}
	    } else if (Boolean.TYPE.equals(ctype) ) {
		boolean[] array = (boolean[]) obj;
		dOut.writeInt(array.length);
		for(int i=0;i<array.length;i++){
		   dOut.writeBoolean(array[i]);
		}
	    } else {
		throw new InternalError();
	    } 	    
	}else{
	  //not primitive 
	  if(String.class.equals(ctype)){
	     String[] array = (String[]) obj ;
	     dOut.writeInt(array.length);
	     for(int i=0;i<array.length;i++){
		 if(array[i]!=null){
		    dOut.writeUTF(array[i]);
		 }else{
		    dOut.writeInt(NULL);
		 }
	     }//for i
	  }else{
	    Object[] array = (Object[]) obj ;
	    dOut.writeInt(array.length);
	    for(int i=0;i<array.length;i++){
	    	LinkedList path_i = (LinkedList)path.clone();
	    	String pathEl = (array[i]==null) ? "Null" : array[i].getClass().getName() ;
	    	path_i.add("[" +i+ "] " +  pathEl);
	    	writeObjectImpl(array[i] ,path);
	    }//for i
	  }
        } //else not primitive 	
    }
    
    
    /** 
     *
     *
     *
     *
     *
     *
     */
     private void writeAny(Object obj,StreamClass sclazz, LinkedList path) throws IOException {
     	
	dOut.writeInt(ANY);	
	dOut.writeUTF(obj.getClass().getName());
	
	pointerMap.put(obj, nextPointerID() );
	
	if ( obj instanceof Externalizable ) {	  
	    LinkedList pathC = (LinkedList)path.clone();  
	    pathC.add("->writeExternal->");
	    threadLoc.set(pathC);
	    ((Externalizable)obj).writeExternal(this) ;
	} else {
	    //writeSerialData(obj, desc);
	    Field[] allf = sclazz.fields();
	    	    
	    for(int i=0;i<allf.length ;i++){
	    	try{
	    	  Class type = allf[i].getType();
	    	  Object v = allf[i].get(obj);
	    	
	    	  if(type.isPrimitive()){
	    	    writePrimitive(obj);
	    	  }else{
	    	    LinkedList pathC = path ;
	    	    if(v!=null){
	    	      pathC = (LinkedList)path.clone(); 
	    	      pathC.add(type.getName() +" " + allf[i].getName() + "(" + v.getClass().getName() + ")");	    	      
	    	    }
	    	    writeObjectImpl(v , pathC );
	    	  }
	    	}catch(Exception x){
	    	  logger.log(Level.SEVERE, "Cannot access a field " , x);
	    	}
	    }//for i
	}
     }
     
     /**
      *
      *
      *
      *
      *
      */
     private void writePrimitive(Object obj) throws IOException{
           Class clazz = obj.getClass();
	    if ( Integer.TYPE.equals(clazz) ) {		
		dOut.writeInt( ((Integer)obj).intValue() );		
	    } else if (Byte.TYPE.equals(clazz)) {		
		dOut.writeByte(((Byte)obj).byteValue());
	    } else if (Long.TYPE.equals(clazz)) {
		dOut.writeLong(((Long)obj).longValue());
	    } else if (Float.TYPE.equals(clazz) ) {
		dOut.writeFloat(((Float)obj).floatValue());
	    } else if (Double.TYPE.equals(clazz) ) {
		dOut.writeDouble(((Double)obj).doubleValue());
	    } else if (Short.TYPE.equals(clazz) ) {
		dOut.writeShort(((Short)obj).shortValue());
	    } else if (Character.TYPE.equals(clazz) ) {
		dOut.writeChar(((Character)obj).charValue());
	    } else if (Boolean.TYPE.equals(clazz) ) {
		dOut.writeBoolean(((Boolean)obj).booleanValue());
	    }       
     }
     
  
    

}


 /**
  *
  *
  *
  *
  */
 class  StreamClass {
    static final Class[] EMPTY_CLASS_ARRAY = {  } ;
    static final Object[] EMPTY_OBJECT_ARRAY = {  } ; 	
    
    static HashMap classMap = new HashMap() ;
   
    Class clazz ; 
    Field[] allfield ;
    Method writeReplaceMethod ;
   
   /** 
    *
    *
    */
   StreamClass(Class clazz){
   	this.clazz= clazz ;
   	ArrayList arrListAllF = getAllFields(clazz);
	allfield = (Field[])arrListAllF.toArray(new Field[]{});
	
	try{
	   writeReplaceMethod = clazz.getDeclaredMethod("writeReplace" , EMPTY_CLASS_ARRAY );
        }catch(Exception x){
	   if(1==2){
	       x = null ;
           }	   
	}
   }
   
   /** 
    *
    *
    *
    */
    static StreamClass lookup(Class clazz){
      StreamClass sclazz =  (StreamClass)classMap.get(clazz);
      if(sclazz ==null ){
      	 sclazz = new StreamClass(clazz);
      	 classMap.put(clazz, sclazz);
      }
      return sclazz ;
    }
   
   
    Field[] fields(){
    	return allfield ;
    }
    
   /** 
    *
    *
    *
    */
   boolean hasWriteReplace(){
   	return writeReplaceMethod!=null ; 
   }
   
   /**
    *
    *
    *
    */
   Object invokeWriteReplace(Object obj) throws Exception {
   	return writeReplaceMethod.invoke(obj,EMPTY_OBJECT_ARRAY);
   }
   
    /**
    *
    *
    *
    *
    */
    private ArrayList getAllFields(Class clazz){   	
   	ArrayList fA = new ArrayList(15);
   	for(;clazz!=null;clazz=clazz.getSuperclass()){
   	   Field[] f=clazz.getDeclaredFields();
   	   for(int i=0;i<f.length;i++){
   	      Class type = f[i].getType();
   	      //System.out.println("[Tree] : field of type " + type ) ;      	 
   	      int mods = f[i].getModifiers();
   	                    
              if( Modifier.isStatic(mods) /*|| Modifier.isTransient(mods)*/ )  {
                continue;
              }
            
   	      //System.out.println("[Tree] : field of type " + type + "  added") ;
   	      f[i].setAccessible(true);   	      
   	      fA.add(f[i]);
   	     
   	   }//for   	   
   	}//for
   	return fA ;
   }     
 }