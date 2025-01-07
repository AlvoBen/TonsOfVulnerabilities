/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.portable;

import com.sap.engine.lib.util.HashMapIntObject;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.internal.util.RepositoryID;
import com.sap.tc.logging.Location;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.SendingContext.RunTime;

import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.io.Serializable;
import java.lang.reflect.Method;

/*
 * @author Georgy Stanev
 * @version 4.0
 */
public class IIOPInputStream extends CORBAInputStream {

  public static final String HELPER = "Helper";
  public static final String READ = "read";
  public static final String IDL_WSTRING = "IDL:omg.org/CORBA/WStringValue:1.0";
  public static final String RMI = "RMI:";
  public static final String RMI_CLASSDESCR = "RMI:javax.rmi.CORBA.ClassDesc";
  public static final String DP = ":";
  public static final String EMPTY_STR = "";
  public HashMapIntObject objectCache = new HashMapIntObject();
  public HashMapIntObject RepIdCache = new HashMapIntObject();
  public HashMapIntObject codebaseCache = null;
  private ValueHandler valueHandler;
  protected RunTime default_codebase = null;

  boolean debug = LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug();
  Location location = LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW);


  public IIOPInputStream(org.omg.CORBA.ORB orb0, byte[] data, boolean littleEndian) {
    super(orb0, data, littleEndian);
  }

  public IIOPInputStream(org.omg.CORBA.ORB orb0, byte[] data) {
    super(orb0, data);
  }

  public IIOPInputStream(org.omg.CORBA.ORB orb0, byte[] data, int size) {
    super(orb0, data, size);
  }

  public void setDefault_codebase(RunTime default_codebase) {
    this.default_codebase = default_codebase;
  }

  public RunTime getDefault_codebase() {
    return default_codebase;
  }

  public java.io.Serializable read_value(Class clz) {
    return read_value(clz.getClassLoader(), clz);
  }


  public java.io.Serializable read_value() {
    return read_value(null, null);
  }

  public java.io.Serializable read_value(ClassLoader baseLoader, Class arg_type) {
    String log_place = "IIOPInputStream.read_value()";

    if (debug) {
      location.debugT(log_place, "READ VALUE at : " + getPos());
    }
    String repositoryId = EMPTY_STR;
    int valueTag = read_long();
    int position = getPos();
    if (debug) {
      location.debugT(log_place, "VALUE TAG : " + valueTag + "  POS : " + position);
    }

    // Check if null value is sent
    if (valueTag == 0) {
      return null;
    }

    // indirection TAG
    if (valueTag == 0xffffffff) {
      int cashePosition = unaligned_read_long();
      if (debug) {
        location.debugT(log_place, ">>INDIRECTION : " + cashePosition);
      }
      position += cashePosition + 4;
      if (debug) {
        location.debugT(log_place, "GET INDIRECTION KEY : " + position);
      }
      Serializable ser = (Serializable) objectCache.get(position);
      if (ser == null) {
        if (debug) {
          location.debugT(log_place, ">> Indirection object not ready... going out with the IndirectionException");
        }
        throw new org.omg.CORBA.portable.IndirectionException(position);
      }
      if (debug) {
        location.debugT(log_place, ">>INDIRECTED : " + ser.getClass());
      }

      return ser;
    }
    boolean flag = isChunked;
    Serializable serializableValue = null;
    String codebase = EMPTY_STR;
    boolean hasCodeBase = ((valueTag & 0x00000001) == 1);
    if (hasCodeBase) {
      codebase = read_codebase_URL();
    }
    isChunked = (valueTag & 0x00000008) != 0;
    switch(valueTag & 0x00000006) {
      case 0:  {   // '\0'
        if (valueHandler == null) {
          valueHandler = Util.createValueHandler();
        }
        repositoryId = valueHandler.getRMIRepositoryID(arg_type);
        break;
      }
      case 2: // '\002'
        repositoryId = readRepositoryId();
        break;
      case 6: // '\006'
        repositoryId = readRepositoryIds();
        break;
    }
    if (debug) {
      location.debugT(log_place, "REP ID : " + RepositoryID.convertFromISOLatin1(repositoryId));
    }
    startBlock();
    endTag--;
    if (isChunked) {
      chunkedRecursionLevel--;
    }
    if (debug) {
      location.debugT(log_place, "endTag: " + endTag);
    }
    try {
      if (repositoryId.equals(IDL_WSTRING)) {
        serializableValue = read_wstring();
      } else if (repositoryId.startsWith(RMI_CLASSDESCR)) {
        codebase = (String) read_value();
        String classNameIDL = (String) read_value();
        String className = classNameIDL.substring(4, ((classNameIDL.substring(5)).indexOf(DP) + 5));
        if (className.startsWith("omg.org/CORBA/WStringValue")) {
          className = "java.lang.String";
        }
        try {
          serializableValue = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
          if (hasCodeBase) {
            serializableValue = Util.loadClass(className, codebase, Thread.currentThread().getContextClassLoader());
          } else if (default_codebase != null) {
            serializableValue = Util.loadClass(className, orb.object_to_string(default_codebase), Thread.currentThread().getContextClassLoader());
          } else {
            serializableValue = Util.loadClass(className, "", Thread.currentThread().getContextClassLoader());
          }
        }
      } else if (repositoryId.startsWith(RMI)) { // v Streama da se iznesat methodi za chetene na sthringa kato masiv ot byte
        String className = RepositoryID.convertFromISOLatin1(repositoryId.substring(4, repositoryId.indexOf(DP, 5)));
        // Class loading!!!! da se napravi prez util.ClassLoader - moje ot URL ili ot Drugi ClassLoaders
        Class valueClass = null;
        if (baseLoader != null) {
          try {
            valueClass = Class.forName(className, true, baseLoader);
          } catch (ClassNotFoundException cls) {
            try {
              valueClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException _cls) {
              if (hasCodeBase) {
                valueClass = Util.loadClass(className, codebase, Thread.currentThread().getContextClassLoader());
              } else if (default_codebase != null) {
                valueClass = Util.loadClass(className, orb.object_to_string(default_codebase), Thread.currentThread().getContextClassLoader());
              } else {
                valueClass = Util.loadClass(className, "", Thread.currentThread().getContextClassLoader());
              }
            }
          }
        } else {
          try {
            valueClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
          } catch (ClassNotFoundException cnfex) {
            if (hasCodeBase) {
              valueClass = Util.loadClass(className, codebase, Thread.currentThread().getContextClassLoader());
            } else if (default_codebase != null) {
              valueClass = Util.loadClass(className, orb.object_to_string(default_codebase), Thread.currentThread().getContextClassLoader());
            } else {
              valueClass = Util.loadClass(className, "", Thread.currentThread().getContextClassLoader());
            }
          }
        }

        if (IDLEntity.class.isAssignableFrom(valueClass)) {
          ClassLoader classloader = valueClass != null ? valueClass.getClassLoader() : null;
          try {
            Class class2 = Class.forName(valueClass.getName() + HELPER, true, classloader);
            Class aclass[] = {org.omg.CORBA.portable.InputStream.class};
            Method method = class2.getDeclaredMethod(READ, aclass);
            java.lang.Object aobj[] = {this};
            serializableValue = (Serializable) method.invoke(null, aobj);
          } catch (Exception classnotfoundexception) {
            try {
              if (hasCodeBase) {
                serializableValue = Util.loadClass(className, codebase, classloader);
              } else if (default_codebase != null) {
                serializableValue = Util.loadClass(className, orb.object_to_string(default_codebase), classloader);
              } else {
                serializableValue = Util.loadClass(className, "", classloader);
              }
            } catch (ClassNotFoundException _cnfex) {
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPInputStream.read_value()", LoggerConfigurator.exceptionTrace(_cnfex));
              }
              throw new MARSHAL(_cnfex.getMessage());
            }
          }
        } else {
          if (valueHandler == null) {
            valueHandler = Util.createValueHandler();
          }

          try {
            try {
              RunTime code_base = (default_codebase != null) ? default_codebase : valueHandler.getRunTimeCodeBase();
              serializableValue = valueHandler.readValue(this, position, valueClass, repositoryId, code_base);
            } catch(ClassCastException ccex) {
              if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
                LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("IIOPInputStream.read_value()", "ClassCast exception. May be ValueHandler can't cast the CodeBase object." + LoggerConfigurator.exceptionTrace(ccex));
              }
              serializableValue = valueHandler.readValue(this, position, valueClass, repositoryId, null);
            }
          } catch (Exception t) {
            if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
              LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPInputStream.read_value()", LoggerConfigurator.exceptionTrace(t));
            }
            throw new org.omg.CORBA.MARSHAL(t.toString());
          }
        }
      }
    } catch (ClassNotFoundException classNotfound) {
      String messageWithId = "ID019101: Class not found";
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPInputStream.read_value()", LoggerConfigurator.exceptionTrace(classNotfound));
      }
      throw new org.omg.CORBA.MARSHAL(messageWithId);
    } catch (org.omg.CORBA.MARSHAL marex) {
      throw marex;
    } catch (Exception ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IIOPInputStream.read_value()", LoggerConfigurator.exceptionTrace(ex));
      }
      throw new org.omg.CORBA.MARSHAL(ex.toString());
    }

    endBlock();
    readEndTag();

    isChunked = flag;
    if (serializableValue != null) {
      if (debug) {
        location.debugT(log_place, "STORE INDIRECTION AT : " + position);
      }
      objectCache.put(position, serializableValue);
    }
    startBlock();
    if (serializableValue != null) {
      if (debug) {
        location.debugT(log_place, "***** READ VALUE RETURNS : " + serializableValue.getClass());
      }
    } else {
      if (debug) {
        location.debugT(log_place, "***** READ VALUE RETURNS : " + null);
      }
    }
    return serializableValue;
  }

  public java.lang.Object read_abstract_interface() {
    return read_abstract_interface(null);
  }

  public java.lang.Object read_abstract_interface(Class class1) {
    boolean flag = read_boolean();
    if (flag) {
      return read_Object(class1);
    } else {
      return read_value();
    }
  }

  public String readRepositoryId() {
    int position = getPos();
    int i = read_long();
    if (i == -1) {
      int off = unaligned_read_long();
      position += off + 4;
      return (String) RepIdCache.get(position);
    }

    byte[] arr = new byte[i - 1]; // -1 remove terminating null
    read_octet_array(arr, 0, i - 1);
    unaligned_read_octet(); // read the terminating null
    String repID = new String(arr);
    RepIdCache.put(position, repID);
    return repID;
  }

  private String readRepositoryIds() {
    int i = read_long();
    if(i == -1) {
      int j = getPos() + unaligned_read_long();
      if(RepIdCache != null && RepIdCache.containsKey(j)) {
        return (String) RepIdCache.get(j);
      } else {
        throw new MARSHAL("Unable to locate array of repository IDs from indirection " + j);
      }
    }
    int k = getPos() - 4;
    String rId = readRepositoryId();
    RepIdCache.put(k, rId);
    for(int l = 1; l < i; l++) {
      readRepositoryId();
    }
    return rId;
  }

  private String read_codebase_URL() {
    int length = read_long();
    if (length == 0xffffffff) {  //indirection
      int indirection = getPos() + read_long() - 4;
      if (codebaseCache != null) {
        String returnResult = (String) codebaseCache.get(indirection);
        if (returnResult != null) {
          return returnResult;
        } else {
          throw new org.omg.CORBA.MARSHAL("Codebase indirection @ " + getPos(), 0, CompletionStatus.COMPLETED_MAYBE);
        }
      } else {
        throw new org.omg.CORBA.MARSHAL("Codebase indirection @ " + getPos(), 0, CompletionStatus.COMPLETED_MAYBE);
      }
    } else {
      String result;
      int indirection = getPos() - 4;
      if (length == 0){
        result = "";
      } else {
        setPos(getPos() - 4); // goto the beginning of the string
        result = read_string();
      }

      if (codebaseCache == null) {
        codebaseCache = new HashMapIntObject();
      }
      codebaseCache.put(indirection, result);
      return result;
    }
  }
}

