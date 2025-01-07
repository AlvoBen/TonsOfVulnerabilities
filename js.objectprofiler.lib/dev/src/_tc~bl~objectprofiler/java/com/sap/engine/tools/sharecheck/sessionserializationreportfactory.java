 package com.sap.engine.tools.sharecheck;
import java.util.logging.*;

/** 
 *
 *
 *
 */
public abstract class SessionSerializationReportFactory{
   static Logger logger = Logger.getLogger("com.sap.engine.tools.sharecheck.SessionSerializationReportFactory");
   
   public final static String FACTORY_IMPL = "com.sap.engine.tools.sharecheck.impl.SessionSerializationReportFactoryImpl" ;
   public final static int NO_REPORT = 0 ;
   public final static int SUMMARY_LEVEL = 1 ;
   public final static int APP_CLASS_LEVEL = 2 ;
   public final static int CLASS_LEVEL = 3 ;
   public final static int OBJECT_LEVEL = 4 ;
   
   
   /**
    *
    *
    *
    *
    */
   public static SessionSerializationReportFactory getInstance() throws RuntimeException{
   	try{
   	   return (SessionSerializationReportFactory) Class.forName(FACTORY_IMPL).newInstance();
   	 }catch(RuntimeException rx){
   	     throw rx;
   	 }catch(Exception x){
   	    logger.log(Level.SEVERE,"Cannot instantiate Factory Implementation " , x);
   	    throw new RuntimeException("Cannot instantiate Factory Implementation ", x );   	    
   	 }
   }

   /**
    * Factory method to create serializability report.
    * shareabilityProblemsFilter is a combination of problem constants from the class SessionSerializationReport like 
    * CUSTOM_SERIALIZATION ...
    * The filter is used when the report has to take care only  for certain problems. 
    * The Object parameter is the object to be analyzed - typically an HttpSession
    */
   public abstract SessionSerializationReport createSerializationReport(Object obj, int shareabilityProblemsFilter , int level, ClassNameFilter classFilter);
   
}