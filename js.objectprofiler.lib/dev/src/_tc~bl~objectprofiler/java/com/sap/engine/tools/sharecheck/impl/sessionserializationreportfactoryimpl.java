package com.sap.engine.tools.sharecheck.impl;
 
import com.sap.engine.tools.sharecheck.* ;

/**
 *
 *
 *
 *
 */ 
public class SessionSerializationReportFactoryImpl extends SessionSerializationReportFactory{
   
   /**
    *
    *
    *
    */
   public SessionSerializationReport createSerializationReport(Object obj, int shareabilityProblemsFilter , int level, ClassNameFilter classFilter){
     return new SessionSerializationReportImpl(obj, shareabilityProblemsFilter, level , classFilter );
   }
   
}