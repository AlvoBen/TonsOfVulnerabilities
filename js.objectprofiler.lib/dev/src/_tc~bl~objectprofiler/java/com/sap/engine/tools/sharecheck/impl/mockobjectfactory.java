  package com.sap.engine.tools.sharecheck.impl ;
 
 import java.util.*;
 import java.lang.reflect.*;
 
 /**
  *
  *
  *
  */
 public class MockObjectFactory{
  
   private static final Class[] EMPTY_CLASS_ARG = new Class[]{} ;
   private static final Class[] EMPTY_OBJECT_ARG = new Class[]{} ; 
   
   private static InvocationHandler DEFAULT_INVOCATION_HANDLER = new InvocationHandler(){
   	  public Object invoke(Object proxy, Method method, Object[] arg) throws Exception {
   	  	return method.invoke(proxy,arg ) ;
   	  }
   };
   private static final byte DEFAULT_BYTE_VALUE =0 ;
   
   /**
    *
    *
    *
    */ 
   public Object newInstance(Class clazz) throws Exception {     
     if( clazz.isInterface() ){
       return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz}, DEFAULT_INVOCATION_HANDLER);
     }     
     
     Class z = clazz ;     
     int dim = 0 ;
     
     for( ; z.isArray();dim++ ){     	
     	z = z.getComponentType() ;
     }
     
     if(dim>0){//means it is array
     	int[] dimA = new int[dim];     	
     	return Array.newInstance(z,dimA) ;
     }
     
     if( clazz.isPrimitive() ){
     	if( clazz.equals(Byte.TYPE)){
     	  return new Byte(DEFAULT_BYTE_VALUE);	
     	}else if(clazz.equals(Integer.TYPE)){
     	  return new Integer(0);
     	}else if(clazz.equals(Long.TYPE)){
     	  return new Long(0l);
     	}else if(clazz.equals(Float.TYPE)){
     	  return new Float(0f);
     	}else if(clazz.equals(Double.TYPE)){
     	  return new Double(0d);
     	}else if(clazz.equals(Character.TYPE)){
     	  return new Character('a');
     	}else if(clazz.equals(Boolean.TYPE)){
     	  return new Boolean(false);
     	}
     }

     try{
        Constructor constr = clazz.getDeclaredConstructor(EMPTY_CLASS_ARG); 
        if(constr!=null){
           constr.setAccessible(true);
        }
        
        return constr.newInstance(EMPTY_OBJECT_ARG);
     }catch(NoSuchMethodException nsm){     	
     	//Expected -> proceed further
     	if("This strange code added because of JLin".equals("" + "a")){
     	   nsm.printStackTrace();
     	}
     }catch(Exception x){
     	throw x ;
     }
     
     
        Constructor[] all = clazz.getDeclaredConstructors();
        Constructor ch=null ;
        Class[] ptypes = null ;
                
        for(int i=0;i<all.length;i++){
           Class[] p = all[i].getParameterTypes();
           if(ptypes==null){
           	ptypes = p;
           	ch = all[i] ;
           	continue;
           }
           
           if(p.length<ptypes.length){
           	ptypes = p ;
           	ch = all[i];
           	continue ;
           }
        }//for 
        
        if(ch!=null){
           Object[] op = new Object[ptypes.length] ;
           for(int i=0;i<ptypes.length;i++){
              op[i] = newInstance(ptypes[i]);
           }//for i
           
           return ch.newInstance(op);
        }
     
     throw new RuntimeException("Cannot instantiate object for type " + clazz );
   }
   
 }