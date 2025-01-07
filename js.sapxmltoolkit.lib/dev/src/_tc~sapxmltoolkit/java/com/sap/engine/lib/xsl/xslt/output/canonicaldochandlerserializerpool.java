/*
 * Created on 2004-1-26
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xsl.xslt.output;

import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class CanonicalDocHandlerSerializerPool {

   protected Vector freePool = new Vector(10,10);
   
   protected int maxCount = 400;
   
   
   public CanonicalDocHandlerSerializer get(OutputStream os, Properties ps) throws OutputException{
     CanonicalDocHandlerSerializer serializer;// = new Encoder();

     
     synchronized(freePool){
       // why signature does not work with this!!!
       int size = freePool.size(); 
       if (size>0){
         
         serializer = (CanonicalDocHandlerSerializer) freePool.remove(size-1);
         serializer.init(os,ps); 
       } else {
         serializer = new CanonicalDocHandlerSerializer(os, ps);
       }
     }
     return serializer;
   }
   
   public void release(CanonicalDocHandlerSerializer enc){
     if (enc==null) return;
     enc.release();
     synchronized(freePool){
       if (freePool.size()<maxCount){
         freePool.addElement(enc);
       }
     }
     
     //let the GC do its job!
   }

   public int getMaxCount(){
     return maxCount;
   }
   
   public void setMaxCount(int value){
     if (value<0) {
       throw new RuntimeException("No negative pool size allowed!");
     } else {
       int k;
       synchronized(freePool){       
         while(value<(k=freePool.size())){
           freePool.removeElementAt(k-1);
         }
       }
     }
     maxCount = value;
   }
   
   public void releaseAll(){
     synchronized(freePool){
       freePool.clear();
     }     
   }
}
