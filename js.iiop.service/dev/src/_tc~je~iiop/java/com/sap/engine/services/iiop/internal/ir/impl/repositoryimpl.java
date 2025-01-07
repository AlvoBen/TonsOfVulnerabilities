package com.sap.engine.services.iiop.internal.IR.impl;

import org.omg.CORBA.*;
import org.omg.PortableServer.POA;

import java.util.List;
import java.util.Vector;

import com.sap.engine.services.iiop.logging.LoggerConfigurator;

public class RepositoryImpl extends ContainerImpl implements RepositoryOperations {
  // read interface

  private String name = "::";
  private String id = "";

  private List allIDs = null;
  private static PrimitiveDef[] primitiveDefs = null;
  private String IOR = null;

  public RepositoryImpl(ORB orb, POA poa)    {
    super(DefinitionKind.dk_Repository, orb, poa);

    allIDs = new Vector();
    createPrimitiveDefs();

    RepositoryPOATie tie = new RepositoryPOATie(this);
    Repository rep = null;
    try {
      rep = RepositoryHelper.narrow(poa.servant_to_reference(tie));
      setContainer(rep);

      IOR = orb.object_to_string(rep);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_ACTIVATION).errorT("RepositoryImpl.cinit(ORB, POA)", LoggerConfigurator.exceptionTrace(e));
      }
    }
  }

  protected Repository containing_repository()   {
    return null;
  }

  public org.omg.CORBA.Contained lookup_id(String search_id)   {
    if (search_id == null ||
       search_id.equals("IDL:omg.org/CORBA/Object:1.0") ||
       search_id.equals("IDL:omg.org/CORBA/ValueBase:1.0"))
      return null;

    return (Contained) componentsByID.get(search_id);
  }

  public org.omg.CORBA.PrimitiveDef get_primitive(org.omg.CORBA.PrimitiveKind kind)   {
    return primitiveDefs[kind.value()];
  }

  public org.omg.CORBA.TypeCode get_canonical_typecode(org.omg.CORBA.PrimitiveKind kind)   {
    // not implemented yet!
    throw new NO_IMPLEMENT();
  }


  // write interface
  public StringDef create_string(int bound)   {
    StringDefImpl impl = new StringDefImpl(bound, orb, poa);

    StringDefPOATie tie = new StringDefPOATie(impl);
    StringDef res = null;
    try {
      res = StringDefHelper.narrow(poa.servant_to_reference(tie));
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("RepositoryImpl.create_string(int)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }

  public SequenceDef create_sequence(int bound, IDLType element_type)   {
    SequenceDefImpl impl = new SequenceDefImpl(bound, element_type, orb, poa);

    SequenceDefPOATie tie = new SequenceDefPOATie(impl);
    SequenceDef res = null;
    try {
      res = SequenceDefHelper.narrow(poa.servant_to_reference(tie));
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("RepositoryImpl.create_sequence(int, IDLType)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  public ArrayDef create_array(int length, IDLType element_type)   {
    ArrayDefImpl impl = new ArrayDefImpl(length, element_type, orb, poa);

    ArrayDefPOATie tie = new ArrayDefPOATie(impl);
    ArrayDef res = null;
    try {
      res = ArrayDefHelper.narrow(poa.servant_to_reference(tie));
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("RepositoryImpl.create_array(int, IDLType)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    return res;
  }


  // specific methods for this implementation

  public String getName()   {
    return name;
  }

  public String getID()   {
    return id;
  }

  List getAllIDs() {
    return allIDs;
  }

  public String getIOR() {
    return IOR;
  }

  private void createPrimitiveDefs() {
    int n = 16;
    primitiveDefs = new PrimitiveDef[n];
    for (int i = 0; i < n; i++) {
      PrimitiveDefImpl impl = new PrimitiveDefImpl(PrimitiveKind.from_int(i), orb, poa);

      PrimitiveDefPOATie tie = new PrimitiveDefPOATie(impl);
      try {
        primitiveDefs[i] = PrimitiveDefHelper.narrow(poa.servant_to_reference(tie));
      }
      catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("RepositoryImpl.createPrimitiveDefs()", LoggerConfigurator.exceptionTrace(e));
        }
      }
    }
  }

}
