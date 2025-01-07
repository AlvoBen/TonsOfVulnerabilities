 package com.sap.engine.tools.sharecheck;
 
 import java.util.* ;
 
/**
 *
 *
 *
 */
public interface SessionSerializationReport{
  
  
  //The object class does not implement serializable interface
  final static int NON_SERIALIZABLE_CLASS = 128; 
  final static String NON_SERIALIZABLE_CLASS_DESCR = "The class does not implement Serializable"; 
  
  //The object class is declares to be serializable ,but the instance cannot be serialized due to a non-serializable field.
  //int NON_SERIALIZABLE_FIELD = 2;
  
  //The object cannot be serialized due to some reason other than the reasons above.
  //For example if an object has a custom serialization and during writeObject a NullPointerException 
  //is thrown.
  final static int NON_SERIALIZABLE_OTHER = 256; 
  final static String NON_SERIALIZABLE_OTHER_DESCR = "An instance cannot be serialized although the class implements Serializable"; 
  
  /*
    shareability problems
  */
   
  final static int DEFINED_SHAREABLE = 512 ; 
  
  //The object class implements custom serialization methods like read/writeObject, readReplace, writeResolve
  final static int CUSTOM_SERIALIZATION_CLASS = 1; 
  final static String CUSTOM_SERIALIZATION_CLASS_DESCR = "The class implements custom serialization methods like read/writeObject, readResolve,writeReplace etc.."; 
  
  //The class loader is not shareable.
  final static int NON_SHAREABLE_CLASSLOADER = 2;
  final static String NON_SHAREABLE_CLASSLOADER_DESCR = "The classloader is not shareable"; 
  
  final static int NON_TRIVIAL_FINALIZER = 4;
  final static String NON_TRIVIAL_FINALIZER_DESCR = "The class has non trivial finalizer"; 
  
  final static int SERIAL_PERSISTENT_FIELD = 8 ;
  final static String SERIAL_PERSISTENT_FIELD_DESCR = "The class has serial persistent field"; 
  
  final static int HAS_TRANSIENT_FIELDS = 16 ;
  final static String HAS_TRANSIENT_FIELDS_DESCR = "The class has transient fields "; 
  
  final static int NON_SERIALIZABLE_PARENT = 32 ;
  final static String NON_SERIALIZABLE_PARENT_DESCR = "The super class does not implement Serializable"; 
  
  //The object cannot be shared due to other reason.
  final static int NON_SHAREABLE_OTHER = 64 ;
  final static String NON_SHAREABLE_OTHER_DESCR = "The object cannot be shared due to a complex reason"; 
  
  //Includes all shareability problems - full report.
  //int ALL = NON_SERIALIZABLE_CLASS + CUSTOM_SERIALIZATION_CLASS + NON_SHARED_CLASSLOADER + NON_SERIALIZABLE_FIELD + NON_SERIALIZABLE_OTHER;
  
  //final static int NON_SERIALIZABLE = NON_SERIALIZABLE_CLASS  | NON_SERIALIZABLE_OTHER;
  //final static int[] NON_SHAREABLE = { CUSTOM_SERIALIZATION_CLASS ,NON_SHAREABLE_CLASSLOADER ,NON_TRIVIAL_FINALIZER ,SERIAL_PERSISTENT_FIELD ,HAS_TRANSIENT_FIELDS ,NON_SHAREABLE_OTHER } ;
  final static int[] ALL = { NON_SERIALIZABLE_CLASS, NON_SERIALIZABLE_OTHER , CUSTOM_SERIALIZATION_CLASS ,NON_SHAREABLE_CLASSLOADER ,NON_TRIVIAL_FINALIZER ,SERIAL_PERSISTENT_FIELD ,HAS_TRANSIENT_FIELDS ,NON_SHAREABLE_OTHER , DEFINED_SHAREABLE } ;
  
  final static int FULL = NON_SERIALIZABLE_CLASS | NON_SERIALIZABLE_OTHER | CUSTOM_SERIALIZATION_CLASS | NON_SHAREABLE_CLASSLOADER | NON_TRIVIAL_FINALIZER | SERIAL_PERSISTENT_FIELD | HAS_TRANSIENT_FIELDS | NON_SHAREABLE_OTHER | DEFINED_SHAREABLE;
  /**
   * Refresh the report to be up-to-date.
   * Normally has to be invoked after a http request.
   *
   */
  public void refresh();
  
  /**
   * The method counts how many objects in the graph have the problem specified.
   *
   */
  int getObjectCountForProblem(int problem);
  
  
  /** 
   * Return list of classes which have the problem specified in parameter problem.
   * filter - a class filter.
   *
   */
  public List getClassesForProblem(int problem) ;
  
  /** 
   *
   * Return list of ClassReport objects
   *
   */
  public List getClassReport() ;
  
  
  /**
   *  Returns list of objects with the problems specified.
   *
   *
   */
  public List getFullObjectReport();  
  
  
  public int getSessionSizeInBytes() ;
  	
  
}