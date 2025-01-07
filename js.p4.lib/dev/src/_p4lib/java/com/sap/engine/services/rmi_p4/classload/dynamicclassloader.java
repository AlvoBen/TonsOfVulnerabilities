/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */


package com.sap.engine.services.rmi_p4.classload;

import com.sap.engine.services.rmi_p4.ClientConnection;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.StubImpl;
import com.sap.engine.services.rmi_p4.lite.ClassFilter;
import com.sap.engine.services.rmi_p4.lite.PermanentCacheOrganizer;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.EOFException;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

/**
 * @author Mladen Droshev, Tsvetko Trendafilov
 * @version 7.0
 */

public class DynamicClassLoader extends ClassLoader {

  public static final String URL_PROTOCOL = "byte";
  public P4ObjectBroker broker = P4ObjectBroker.init();
  private ClassLoader parent = null;
  //private File[] localJars = null;
  //private File[] localDirs = null;
  private boolean isLocal = false;
  private boolean keepStatistics = false;
  private boolean enabledPermCache = false;
  private PermanentCacheOrganizer cacheOrganizer = null;
  
  private File[] localResources = null;

  private StubImpl stubContext;

  private HashSet<String> negativeCache = new HashSet<String>();

  private Hashtable hashweak = null;

  private String serverLoaderName = null;

  public DynamicClassLoader(ClassLoader parent) {
    super(parent);
    this.parent = parent;
  }

  public void setAsLocalLoader(boolean b) {
    this.isLocal = b;
  }

  public void setStubContext(StubImpl stub) {
    this.stubContext = stub;
  }

  public boolean isPrepared() {
    return this.stubContext != null;
  }
  
  /**
   * Enable / Disable statistics for downloaded classes and their size.
   * @param flag true  - enable tracking; 
   *             false - disable tracking. 
   */
  public void keepStatistic(boolean flag){
    this.keepStatistics = flag;
  }

  public synchronized Class loadClass(String name, boolean resolve, StubImpl stub) throws ClassNotFoundException {
    StubImpl originalStub = stubContext;
    stubContext = stub;
    Class<?> result = super.loadClass(name, resolve);
    if (originalStub != null){
      stubContext = originalStub;
    }
    return result;
    
  }
  
  /** 
   * Log classes loaded even from resources. 
   */ 
  public Class<?> loadClass(String name) throws ClassNotFoundException{
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.loadClass(String)", "Class: " + name + " will be loaded");
    }
    return super.loadClass(name);
  }

  public void setClasspath(String classpath) {
    parseClasspath(classpath);
  }

  protected Class findClass(String name) throws ClassNotFoundException {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Dynamic Class Loader begin to load class: " + name);
    }
    
    if (name.indexOf("com.sap.engine.services.rmi_p4") != -1) {
      throw new ClassNotFoundException(name + " is forbiden for distribution from server process");
    }
    
    /* check in negative cache */
    if (negativeCache.contains(name)) {
      throw new ClassNotFoundException("com.sap.ASJ.rmip4.rt3000 Following class cannot be loaded: " + name + " Loading this class has already failed in past and had been writen to negative cache. This class was not found in your class path and it could not be loaded remotely.");
    }

    byte[] classData = null;

    try {
      classData = getLocalData((name.replace('.', '/')) + ".class");
    } catch (Exception e) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("DynamicClassLoader.findClass(String)", "Cannot find class " + name + " in local resources because of exception");
      }
    }

    if (name.endsWith("_Stub") && classData == null) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Found class ending on _Stub, which is not available in the local resources. If this happens during narrow, everithing is OK");
      }
      throw new ClassNotFoundException("P4 protocol will raise Proxy Stub. Class " + name + " is forbiden for distribution from server process; not found in local resources as generated Stub");
    }
    
    if (classData == null && enabledPermCache && cacheOrganizer != null && stubContext != null) {
      int remoteBrokeID = stubContext.info.ownerId;
      classData = cacheOrganizer.readFromCache(name, remoteBrokeID);
      if (classData != null && P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", name + " was found in permanent cache");
      }
    }

    boolean loadedFromServer = false;
    boolean negativeCacheAdd = false;
    
    if (!isLocal && classData == null && stubContext != null) {
      ClassFilter.filter(name);
//    if(name.indexOf("com.sap.ats") != -1){
//      throw new ClassNotFoundException("The " + name + " is forbidden to be distributed from the server.");
//    }
      
      /* try to download class from the server and store it in the cache */
      if ((classData == null) || (classData.length == 0)) {//&& serverLoaderName != null) {
        //Forbid Capabilities to be downloaded from server process only
        if (name.equals("com.sap.jvm.Capabilities")) {
          throw new NoClassDefFoundError(name + " is forbiden for downloading from server process");
        }
        
        try {
          if (serverLoaderName != null) {
            classData = stubContext.p4_getClassData(name + P4ObjectBroker.P4_DELIMITER + this.serverLoaderName);
          } else {
            classData = stubContext.p4_getClassData(name + P4ObjectBroker.P4_DELIMITER);
          }
          if (classData != null) {
            loadedFromServer = true;
          } else {
            //If there is an exception this part will not be executed
            negativeCacheAdd = true;
          }
          if (keepStatistics && classData != null) {
            P4ObjectBroker.addRes(name + " by " + Integer.toHexString(hashCode()) + " size: " + classData.length);
            P4ObjectBroker.increaseClassStat(classData.length);
          }
        } catch (Exception cnfe) {
          try {
            classData = readWriteData(parent.getResourceAsStream(name.replace('.', '/') + ".class")); //Linux and Unix style
          } catch (IOException e) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Cannot find class " + name + " from the server process: " + P4Logger.exceptionTrace(cnfe));
            }
            throw new ClassNotFoundException(name, e);
          }
        } catch (Error er) {
          try {
            classData = readWriteData(parent.getResourceAsStream(name.replace('.', '/') + ".class"));
          } catch (IOException e) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Cannot find class " + name + " from the server process: " + P4Logger.exceptionTrace(er));
            }
            throw new ClassNotFoundException(name + " - " + er.getMessage(), e);
          }
        }
      }
    }

    if (classData == null || classData.length == 0) {
      if (isConnected() && negativeCacheAdd) {
        negativeCache.add(name);
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Class " + name + " was added to negative cashe. This class will not be searched from the server anymore");
          StringBuilder localClassPath = new StringBuilder(256);
          for (int i=0; i<localResources.length; i++){
            localClassPath.append(localResources[i].getName()).append("\r\n");
          }
          P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Local Classpath is: \r\n" + localClassPath.toString());
        }
      } else {
        if (P4Logger.getLocation().beInfo()) {
          P4Logger.getLocation().infoT("DynamicClassLoader.findClass(String)", "Class " + name + " was not found in local resources. The class will not be stored in negative cache because of exception during search request to server process. Probably the stub was disconnected: " + stubContext);
        }
      }
      if (name.equals("com.sap.engine.services.dc.cm.deploy.SduLoadingException")) {
        throw new ClassNotFoundException(name + " - Possible root cause: Not enough disk space for deploy operation. Usable free disk space is less than minimal number of bytes to operate. Free some more disk space");
      }
      throw new ClassNotFoundException(name);
    }

    String packageName = "";
    if (name.indexOf(".") != -1) {
      packageName = name.substring(0, name.lastIndexOf("."));
    }

    if (getPackage(packageName) == null) {
      try {
        definePackage(packageName, null, null, null, null, null, null, null);
      } catch (IllegalArgumentException e) {//$JL-EXC$
        //the package is already loaded
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Cannot define package " + packageName + " for class: " + name + ". Maybe the package is already loaded - " + P4Logger.exceptionTrace(e));
        }
      }
    }
    Class cl = null;
    try {
      cl = defineClass(name, classData, 0, classData.length);
    } catch (ClassFormatError classFormatError) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Cannot define class: " + name + " Maybe the class is loaded with wrong classloader");
      }
      throw classFormatError;
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.findClass(String)", "Dynamic Class Loader loaded class: " + name + " successfully");
    }
    if (enabledPermCache && loadedFromServer && cacheOrganizer != null){
      int remoteBrokeID = stubContext.info.ownerId;
      cacheOrganizer.record(name, remoteBrokeID, classData);
    }
    return cl;
  }

  private byte[] readWriteData(InputStream resourceStream) throws IOException {
    byte[] data = null;
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    if (resourceStream != null) {
      do {
        data = new byte[resourceStream.available()];
        int offset = 0;
        int readed = 0;
        while (offset < data.length) {
          readed = resourceStream.read(data, offset, data.length - offset);
          if (readed == -1) {
            bout.write(data, 0, offset);
            return bout.toByteArray();
          }
          offset += readed;
        }
        bout.write(data);
      } while (resourceStream.available() > 0);
    }
    return bout.toByteArray();
  }

  protected URL findResource(String name) {
    URL url = super.findResource(name);
    if (url != null) {
      return url;
    }
    if (P4Logger.getLocation().bePath()){
      P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "Cannot find resource with parent classloader");
    }
    ByteArrayInputStream byteIn = null;
    byte[] resData = null;
    resData = getLocalData(name);

    if (hashweak == null) {
      hashweak = new Hashtable();
    }

    /* try to download resource from the server and to store in the cache */
    if ( (resData == null || resData.length == 0) && stubContext != null) {// && this.serverLoaderName != null) {
      try {
        resData = stubContext.p4_getResourceData(name + P4ObjectBroker.P4_DELIMITER + this.serverLoaderName);
        if (keepStatistics && resData != null) {
          P4ObjectBroker.addRes("Resource " + name + " by " + Integer.toHexString(hashCode()) + " size: " + resData.length);
          P4ObjectBroker.increaseClassStat(resData.length);
        }
      } catch (FileNotFoundException e) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "Cannot find (1.)resource " + name + " in server process");
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
        }
        return null;
      } catch (ClassNotFoundException e) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "Cannot find (2.)resource " + name + " in server process");
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
        }
        return null;
      } catch (IOException io) {
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "Cannot find (3.)resource " + name + " in server process");
        }
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4Logger.exceptionTrace(io));
        }
        return null;
      }
    }

    /* configure resource */
    try {
      byteIn = new ByteArrayInputStream(resData);
      P4URLStreamHandler p4Handler = new P4URLStreamHandler();
      p4Handler.setHashWeak(hashweak);
      url = new URL(URL_PROTOCOL, (InetAddress.getLocalHost()).getHostAddress(), ((ClientConnection) stubContext.repliable).port, name, p4Handler);
      hashweak.put(url, byteIn);
      return url;
    } catch (MalformedURLException e) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "There is a problem with getting the (1.)resource : " + name + " in the server");
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
      }
    } catch (UnknownHostException e) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("DynamicClassLoader.findResource(String)", "There is a problem with getting the (2.)resource : " + name + " in the server");
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
      }
    }
    return null;
    //cannot make url - possible problems: port from repliable, host, or p4Handler
  }

  public Hashtable getttt() {
    return ClassLoaderContext.classIndex;
  }

  public void setServerLoaderName(String name) {
    this.serverLoaderName = name;
  }

  private void parseClasspath(String classpath) {
    if (classpath != null) {
      Vector<File> parsed = new Vector<File>();

      StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
      while (st.hasMoreElements()) {
        try {
          parsed.addElement((new File((String) st.nextElement())).getCanonicalFile());
        } catch (IOException e) {
          P4Logger.getLocation().debugT("DynamicClassLoader.parseClasspath(String)", "Classpath : " + classpath + "  - " + P4Logger.exceptionTrace(e));
        }
      }
      if (parsed.size() > 0) {
        this.localResources = parsed.toArray(new File[parsed.size()]);
      }
    }
  }

  private byte[] getLocalData(String className) {
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.getLocalData(String)", "Try to get from local resources class " + className);
    }
    byte[] buffer = null;
    if (className != null && this.localResources != null && this.localResources.length > 0) {
      String filename = className;

      for (File res : this.localResources) {
        if (res.isDirectory()) {
          buffer = readResourceFromDir(filename, res);
        } else if (res.isFile()) {
          try {
            buffer = readResourceFromJar(filename, new JarFile(res));
          } catch (IOException e) {//maybe the file is not jar file
            buffer = null;
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("DynamicClassLoader.getLocalData(String)", "class name : " + className + " from jar: " + res + "  - " + P4Logger.exceptionTrace(e));
            }
          }
        }
        if (buffer != null) {
          break;
        }
      }
    }
    if (buffer==null && P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.getLocalData(String)", "Resource was not found in local resources. Try to get class from server");
    }
    return buffer;
  }

  private byte[] readResourceFromJar(String fileName, JarFile jar) {
    JarEntry entry = jar.getJarEntry(fileName);
    if (entry == null) {
      return null;
    }
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("DynamicClassLoader.readResourceFromJar(...)", "[Loading " + fileName + " from " + jar.getName() +"]");
    }
    InputStream in = null;

    try {
      int len = (int) entry.getSize();
      byte[] result = new byte[len];
      in = jar.getInputStream(entry);
      int n = 0;

      while (n < len) {
        int count = in.read(result, n, len - n);
        if (count < 0)
          throw new EOFException();
        n += count;
      }

      return result;
    } catch (IOException io) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("DynamicClassLoader.readResourceFromJar(...)", "File name : " + fileName + " <> jar : " + jar.getName() + "  : " + P4Logger.exceptionTrace(io));
      }
    } finally {
      try {
        in.close();
      } catch (Exception e) {//$JL-EXC$
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DynamicClassLoader.readResourceFromJar(...)", P4Logger.exceptionTrace(e));
        }
        in = null;
      }
    }

    return null;
  }

  private byte[] readResourceFromDir(String fileName, File dirFile) {
    File file = new File(dirFile, fileName);

    FileInputStream in = null;

    if (file.exists()) {
      try {
        in = new FileInputStream(file);
        int len = in.available();
        byte[] result = new byte[len];
        int n = 0;

        while (n < len) {
          int count = in.read(result, n, len - n);
          if (count < 0)
            throw new EOFException();
          n += count;
        }

        return result;
      } catch (IOException io) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("DynamicClassLoader.readResoruceFromDir(...)", "file name : " + fileName + " <> dirname : " + dirFile + "  : " + P4Logger.exceptionTrace(io));
        }
      } finally {
        try {
          in.close();
        } catch (Exception e) {//$JL-EXC$
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("DynamicClassLoader.readResoruceFromDir(...)", P4Logger.exceptionTrace(e));
          }

          in = null;
        }
      }
    }
    return null;
  }

  public URL getResource(String name) {

    if (this.localResources != null && this.localResources.length > 0) {
      for (File f : this.localResources) {
        try {
          if (f.isDirectory() && readResourceFromDir(name, f) != null) {
            try {
              return f.toURL();
            } catch (MalformedURLException e) {
              e.printStackTrace();
            }
          } else if (f.isFile() && readResourceFromJar(name, new JarFile(f)) != null) {
            try {

              JarFile jf = new JarFile(f);
              JarEntry je = jf.getJarEntry(name);

              if (je != null) {
                return new URL("jar:" + f.toURL().toString() + "!/" + je.getName());
              }
            } catch (MalformedURLException e) {
              e.printStackTrace();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public Enumeration<URL> getResources(String name) throws IOException {
    Enumeration<URL> res = null;//super.getResources(name);
    if (res == null && this.localResources != null && this.localResources.length > 0) {
      Vector<URL> result = new Vector<URL>();
      for (File f : this.localResources) {
        if (f.isDirectory() && readResourceFromDir(name, f) != null) {

          result.add(f.toURL());
        } else if (f.isFile()) {
          JarFile jf = new JarFile(f);
          JarEntry je = jf.getJarEntry(name);
          if (je != null) {
            result.add(new URL("jar:" + f.toURL().toString() + "!/" + je.getName()));
          }
        }
      }
      res = result.elements();
    }

    return res;
  }
  
  /**
   *  Check if the DynamicClassloader is still connected to the server.
   *  Used when DynamicClassloader is taken in ClassLoaderContext.
   */
  public boolean isConnected(){
    if (stubContext != null && stubContext.repliable != null){
      return ! (this.stubContext.repliable.isClosed());
    }
    return false;
  }
  
  public void reconnect(){
    //Cannot reconnect the Loader's stub
  }
  
  /** 
   * Remove all classes cached in negative cache.
   * If the connection of the stub for this loader is dead, 
   * it will continue to add classes in negative cache. 
   */
  public void invalidateNegativeCache(){
    this.negativeCache = new HashSet<String>();
  }
  
  /**
   *  This method has to be overridden if loadClass(String) is overridden. 
   *  Used when local resources are loaded through class loader as a stream. 
   */
  public InputStream getResourceAsStream(String name) {
    URL url = super.getResource(name);
    if (url == null){
      url = getResource(name);
    }
    try {
        return url != null ? url.openStream() : null;
    } catch (IOException e) {
        return null;
    }
  }
  
  /**
   * Enables using of permanent cache. This method will make DynamicClassloader to 
   * search classes from permanent cache. <br>
   * Note: If application is deployed, used and removed(undeployed), downloaded from 
   * server classes will remain in permanent cache. 
   * @param pco Permanent Cache Organizer - this is the class that organize and search 
   *            in permanent cache.
   */
  public void enablePermCache(PermanentCacheOrganizer pco){
    this.enabledPermCache = true;
    this.cacheOrganizer = pco;
  }
  
  /**
   * This method will stop waiting for more classes thread. 
   * It has nothing more to write, because main process had exited.
   */
  public void stopPermCache(){
    if (enabledPermCache && cacheOrganizer != null){
      cacheOrganizer.stop();
    }
  }
}
