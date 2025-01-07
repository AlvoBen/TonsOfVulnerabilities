package com.sap.engine.services.iiop.internal.interceptors;

import org.omg.PortableInterceptor.*;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.CORBA.ORB;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.WeakHashMap;

public class InterceptorsStorage {

  static final int CLIENT_INTERCEPTOR_TYPE = 0;
  static final int SERVER_INTERCEPTOR_TYPE = 1;
  static final int IOR_INTERCEPTOR_TYPE = 2;
  static final int NUM_INTERCEPTOR_TYPES = 3;

  static final Class[] classTypes = {ClientRequestInterceptor.class, ServerRequestInterceptor.class, IORInterceptor.class};

  private static Interceptor[][] interceptors = new Interceptor[NUM_INTERCEPTOR_TYPES][];
  private static WeakHashMap<ORB, Interceptor[][]> local_interceptors = new WeakHashMap<ORB, Interceptor[][]>();

  static {
    for (int type = 0; type < NUM_INTERCEPTOR_TYPES; type++) {
      Class classType = classTypes[type];
      interceptors[type] = (Interceptor[]) Array.newInstance(classType, 0);
    } 
  }

  public static void register_client_interceptor(Interceptor interceptor) throws DuplicateName {
    register_interceptor(interceptor, CLIENT_INTERCEPTOR_TYPE);
  }

  public static void register_server_interceptor(Interceptor interceptor) throws DuplicateName {
    register_interceptor(interceptor, SERVER_INTERCEPTOR_TYPE);
  }

  public static void register_ior_interceptor(Interceptor interceptor) throws DuplicateName {
    register_interceptor(interceptor, IOR_INTERCEPTOR_TYPE);
  }

  public static Interceptor[] getClientInterceptors() {
    return interceptors[CLIENT_INTERCEPTOR_TYPE];
  }

  public static Interceptor[] getServerInterceptors() {
    return interceptors[SERVER_INTERCEPTOR_TYPE];
  }

  public static IORInterceptor[] getIORInterceptors() {
    return (IORInterceptor[]) interceptors[IOR_INTERCEPTOR_TYPE];
  }

  public static Interceptor[] getClientInterceptors(ORB orb) {
    return get_local_interceptors(orb)[CLIENT_INTERCEPTOR_TYPE];
  }

  public static Interceptor[] getServerInterceptors(ORB orb) {
    return get_local_interceptors(orb)[SERVER_INTERCEPTOR_TYPE];
  }

  public static IORInterceptor[] getIORInterceptors(ORB orb) {
    return (IORInterceptor[]) get_local_interceptors(orb)[IOR_INTERCEPTOR_TYPE];
  }

  public static void destroyAll() {
    for (Interceptor[] interceptor : interceptors) {
      for (Interceptor anInterceptor : interceptor) {
        anInterceptor.destroy();
      }
    }

    Collection coll = local_interceptors.values();
    for (Object aColl : coll) {
      Interceptor[][] buff = (Interceptor[][]) aColl;
      for (int i = 0; i < interceptors.length; i++) {
        for (int j = 0; j < buff[i].length; j++) {
          buff[i][j].destroy();
        }
      }
    }
  }

  public static void register_client_interceptor(ORB orb, Interceptor interceptor) throws DuplicateName {
    Interceptor[][] buff = get_local_interceptors(orb);
    buff[CLIENT_INTERCEPTOR_TYPE]  = register_interceptor(interceptor, buff[CLIENT_INTERCEPTOR_TYPE], CLIENT_INTERCEPTOR_TYPE);
    local_interceptors.put(orb, buff);
  }

  public static void register_server_interceptor(ORB orb, Interceptor interceptor) throws DuplicateName {
    Interceptor[][] buff = get_local_interceptors(orb);
    buff[SERVER_INTERCEPTOR_TYPE]  = register_interceptor(interceptor, buff[SERVER_INTERCEPTOR_TYPE], SERVER_INTERCEPTOR_TYPE);
    local_interceptors.put(orb, buff);
  }

  public static void register_ior_interceptor(ORB orb, Interceptor interceptor) throws DuplicateName {
    Interceptor[][] buff = get_local_interceptors(orb);
    buff[IOR_INTERCEPTOR_TYPE]  = register_interceptor(interceptor, buff[IOR_INTERCEPTOR_TYPE], IOR_INTERCEPTOR_TYPE);
    local_interceptors.put(orb, buff);
  }

  private static Interceptor[][] get_local_interceptors(ORB orb) {
    Interceptor[][] buff = local_interceptors.get(orb);
    if (buff != null) {
      return buff;
    } else {
      return new Interceptor[][] {new ClientRequestInterceptor[0], new ServerRequestInterceptor[0], new IORInterceptor[0]};
    }
  }

  private static void register_interceptor(Interceptor interceptor, int type) throws DuplicateName {
    interceptors[type] = register_interceptor(interceptor, interceptors[type], type);
  }

  private static Interceptor[] register_interceptor(Interceptor interceptor, Interceptor[] alias_interceptors, int type) throws DuplicateName {
    String interceptorName = interceptor.name();
    boolean anonymous = interceptorName.equals("");
    boolean foundDuplicate = false;

    if (!anonymous) {
      for (Interceptor anAlias_interceptor : alias_interceptors) {
        if (anAlias_interceptor.name().equals(interceptorName)) {
          foundDuplicate = true;
          break;
        }
      }
    }

    if (!foundDuplicate) {
      Class classType = classTypes[type];
      Interceptor[] replacementArray = (Interceptor[]) Array.newInstance(classType, alias_interceptors.length + 1);
      int index = 0;

      while (index < alias_interceptors.length && alias_interceptors[index].name().compareTo(interceptorName) < 0) {
        replacementArray[index] = alias_interceptors[index];
        index++;
      }

      replacementArray[index++] = interceptor;

      for (; index < replacementArray.length; index++) {
        replacementArray[index] = alias_interceptors[index - 1];
      } 

      return replacementArray;

    } else {
      throw new DuplicateName(interceptorName);
    }

  }

}

