package com.sap.engine.lib.xml.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class FileClassLoader extends ClassLoader {

  protected byte pattern; //0 - empty; 1 - jars; 2 - files; 3 - mixed
  protected final byte EMPTY = 0;
  protected final byte JARS = 1;
  protected final byte DIRS = 2;
  protected final byte MIXED = 3;
  private final int BUFFER_SIZE = 8192;
  protected String name;
  protected String[] jars;
  protected JarFilter[] jarFilters;
  protected String[] dirs;
  protected File[] dirFiles;
  protected int findIndex;
  protected boolean isFindInJar;
  protected ProtectionDomain domain;
  protected Set inloadable = new HashSet();
  protected Set inloadableLocal = new HashSet();
  protected static Set inloadableContainer = new HashSet(); // remove on reboot !!!
  protected static int hits;

  //---------- constructors -------------------------
  public FileClassLoader(ClassLoader parent, String name) {
    this(parent, name, null);
  }

  public FileClassLoader(ClassLoader parent, String name, ProtectionDomain domain) {
    super(parent);
    inloadableContainer.add(inloadableLocal);
    inloadableContainer.add(inloadable);
    setName(name);
    this.domain = domain;
    pattern = EMPTY;
  }

  public FileClassLoader(ClassLoader parent, String name, String fileName, boolean isJar, ProtectionDomain domain) {
    super(parent);
    setName(name);
    this.domain = domain;

    if (isJar) {
      pattern = JARS;
      jars = new String[1];
      jars[0] = fileName;
      jarFilters = new JarFilter[1];
      jarFilters[0] = null;
    } else {
      pattern = DIRS;
      dirs = new String[1];
      dirs[0] = fileName;
      dirFiles = new File[1];
      dirFiles[0] = null;
    }
  }

  public FileClassLoader(ClassLoader parent, String name, String[] fileNames, boolean areJars, ProtectionDomain domain) {
    super(parent);
    setName(name);
    this.domain = domain;

    if (areJars) {
      pattern = JARS;
      jars = fileNames;
      jarFilters = new JarFilter[jars.length];

      for (int i = 0; i < jarFilters.length; i++) {
        jarFilters[i] = null;
      } 
    } else {
      pattern = DIRS;
      dirs = fileNames;
      dirFiles = new File[dirs.length];

      for (int i = 0; i < dirFiles.length; i++) {
        dirFiles[i] = null;
      } 
    }
  }

  public FileClassLoader(ClassLoader parent, String name, String[] jarNames, String[] dirNames, ProtectionDomain domain) {
    super(parent);
    setName(name);
    this.domain = domain;
    pattern = MIXED;
    jars = jarNames;
    dirs = dirNames;
    jarFilters = new JarFilter[jars.length];

    for (int i = 0; i < jarFilters.length; i++) {
      jarFilters[i] = null;
    } 

    dirFiles = new File[dirs.length];

    for (int i = 0; i < dirFiles.length; i++) {
      dirFiles[i] = null;
    } 
  }

  public FileClassLoader(ClassLoader parent, String name, File[] dirFiles, ProtectionDomain domain) {
    super(parent);
    setName(name);
    this.domain = domain;
    this.dirFiles = dirFiles;
    pattern = DIRS;
    dirs = new String[dirFiles.length];

    for (int i = 0; i < dirs.length; i++) {
      dirs[i] = dirFiles[i].getAbsolutePath();
    } 
  }

  public FileClassLoader(ClassLoader parent, String name, File[] files, boolean areJars, ProtectionDomain domain) {
    super(parent);
    if (!areJars) {
      setName(name);
      this.domain = domain;
      this.dirFiles = dirFiles;
      pattern = DIRS;
      dirs = new String[files.length];

      for (int i = 0; i < dirs.length; i++) {
        dirs[i] = files[i].getAbsolutePath();
      } 
    } else {
      setName(name);
      this.domain = domain;
      this.dirFiles = dirFiles;
      pattern = JARS;
      jars = new String[files.length];
      jarFilters = new JarFilter[jars.length];

      for (int i = 0; i < jarFilters.length; i++) {
        jars[i] = files[i].getAbsolutePath();
        jarFilters[i] = null;
      } 
    }
  }

  //------------public methods------------------------
  public URL getResource(String name) {
    ClassLoader parent = getParent();

    if (parent == null) {
      parent = ClassLoader.getSystemClassLoader();
    }

    URL result = parent.getResource(name);

    if (result != null) {
      return result;
    }

    InputStream in;
    boolean isFindInJarLocal;
    synchronized (this.name) { //needed because of isFindInJar flag
      in = getResourceAsStream(name);
      isFindInJarLocal = isFindInJar;
    }

    if (in != null) {
      try {
        in.close();
      } catch (IOException ioException) {
        //$JL-EXC$
        ioException.printStackTrace();
      }
      try {
        if (isFindInJarLocal) {
          return new URL("jar:file:" + jars[findIndex] + "!/" + name);
        } else {
          return new URL("file:" + dirs[findIndex] + name); //DA SE PROVERI!!!
        }
      } catch (MalformedURLException murlException) {
        //$JL-EXC$
        murlException.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  public InputStream getResourceAsStream(String name) {
    synchronized (inloadable) {
      if (inloadable.contains(name)) {
        return null;
      }
    }
    ClassLoader parent = getParent();

    if (parent == null) {
      parent = ClassLoader.getSystemClassLoader();
    }

    InputStream in = parent.getResourceAsStream(name);

    if (in != null) {
      return in;
    }

    byte[] buffer = readResourceData(name, false);

    if (buffer != null) {
      return new ByteArrayInputStream(buffer);
    } else {
      return null;
    }
  }

  public void addJar(String jar) {
    synchronized (this.name) {
      if (jar == null) {
        return;
      }

      String[] jars = new String[1];
      jars[0] = jar;
      addJars(jars);
    }
  }

  public void addJars(String[] names) {
    synchronized (this.name) {
      if (names == null) {
        return;
      }

      clearInloadableContainer();

      switch (pattern) {
        case DIRS: { //$JL-SWITCH$

        }
        case EMPTY: {
          pattern += JARS;
          jars = names;
          jarFilters = new JarFilter[jars.length];

          for (int i = 0; i < jarFilters.length; i++) {
            jarFilters[i] = null;
          } 

          break;
        }
        case JARS: { //$JL-SWITCH$

        }
        case MIXED: {
          String[] newJars = new String[jars.length + names.length];
          System.arraycopy(jars, 0, newJars, 0, jars.length);
          System.arraycopy(names, 0, newJars, jars.length, names.length);
          jars = newJars;
          JarFilter[] newJarFilters = new JarFilter[jars.length];
          System.arraycopy(jarFilters, 0, newJarFilters, 0, jarFilters.length);

          for (int i = jarFilters.length; i < newJarFilters.length; i++) {
            newJarFilters[i] = null;
          } 

          jarFilters = newJarFilters;
          break;
        }
      }
    }
  }

  public String[] getJars() {
    return jars;
  }

  public void addDir(String dir) {
    synchronized (this.name) {
      if (dir == null) {
        return;
      }

      String[] dirs = new String[1];
      dirs[0] = dir;
      addDirs(dirs);
    }
  }

  public void addDirs(String[] names) {
    synchronized (this.name) {
      if (names == null) {
        return;
      }

      clearInloadableContainer();

      switch (pattern) {
        case JARS: { //$JL-SWITCH$

        }
        case EMPTY: {
          pattern += DIRS;
          dirs = names;
          dirFiles = new File[dirs.length];

          for (int i = 0; i < dirFiles.length; i++) {
            dirFiles[i] = null;
          } 

          break;
        }
        case DIRS: { //$JL-SWITCH$

        }
        case MIXED: {
          String[] newDirs = new String[dirs.length + names.length];
          System.arraycopy(dirs, 0, newDirs, 0, dirs.length);
          System.arraycopy(names, 0, newDirs, dirs.length, names.length);
          dirs = newDirs;
          File[] newDirFiles = new File[dirs.length];
          System.arraycopy(dirFiles, 0, newDirFiles, 0, dirFiles.length);

          for (int i = dirFiles.length; i < newDirFiles.length; i++) {
            newDirFiles[i] = null;
          } 

          dirFiles = newDirFiles;
          break;
        }
      }
    }
  }

  public void addMixed(File[] files) {
    synchronized (this.name) {
      if (files == null) {
        return;
      }

      Vector jarsTmp = new Vector();
      Vector dirsTmp = new Vector();

      for (int i = 0; i < files.length; i++) {
        String token = files[i].getPath();

        if (token.endsWith(".jar") || token.endsWith(".zip")) {
          jarsTmp.add(token);
        } else {
          dirsTmp.add(token);
        }
      } 

      String[] jarsTemp = new String[jarsTmp.size()];
      String[] dirsTemp = new String[dirsTmp.size()];

      for (int i = 0; i < jarsTmp.size(); i++) {
        jarsTemp[i] = (String) jarsTmp.elementAt(i);
      } 

      for (int i = 0; i < dirsTmp.size(); i++) {
        dirsTemp[i] = (String) dirsTmp.elementAt(i);
      } 

      addJars(jarsTemp);
      addDirs(dirsTemp);
    }
  }

  public static void clearInloadableContainer() {
    if (inloadableContainer != null) {
      synchronized (inloadableContainer) {
        if (inloadableContainer.size() > 0) {
          int count = 0;
          Iterator iterator = inloadableContainer.iterator();

          while (iterator.hasNext()) {
            Set currentSet = (Set) iterator.next();
            count += currentSet.size();
            synchronized (currentSet) {
              currentSet.clear();
            }
          }
        }
      }
    }
  }

  public void updateJar(String jar, String newFile) throws IOException {
    synchronized (this.name) {
      if (jar == null) {
        return;
      }

      clearInloadableContainer();
      int offset = -1;

      for (int i = 0; i < jars.length; i++) {
        if (jar.equals(jars[i])) {
          offset = i;
        }
      } 

      if (offset == -1) {
        return;
      } else {
        File dest = new File(jar);
        File source = new File(newFile);

        if (dest.exists() && source.exists()) {
          RandomAccessFile destFile = new RandomAccessFile(dest, "rw");
          RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
          destFile.seek(0);
          sourceFile.seek(0);
          byte[] buffer = new byte[16384];
          int read = sourceFile.read(buffer, 0, buffer.length);

          while (read > 0) {
            destFile.write(buffer, 0, read);
            read = sourceFile.read(buffer, 0, buffer.length);
          }

          destFile.setLength(sourceFile.length());
          destFile.close();
          sourceFile.close();
        }

        jarFilters[offset] = null;
      }
    }
  }

  //------------protected methods---------------------
  protected synchronized Class findClass(String name) throws ClassNotFoundException {
    //synchronous lookup in cache
    Class c = findLoadedClass(name);

    if (c != null) {
      return c;
    }

    //read class
    byte[] buffer = readClassData(name);
    // load the package for this class
    loadPackageForClass(name);
    //synchronous definition of class
    c = defineClass(name, buffer, 0, buffer.length, getProtectionDomain());
    return c;
  }

  protected void loadPackageForClass(String className) {
    int index = className.lastIndexOf('.');

    if (index != -1) {
      String pkgName = className.substring(0, index);

      // check if the package is already loaded
      if (getPackage(pkgName) == null) {
        // the package is not loaded yet
        if (isFindInJar) {
          Manifest manifest = jarFilters[findIndex].getManifest();
          definePackage(pkgName, manifest);
        } else {
          definePackage(pkgName, null, null, null, null, null, null, null);
        }
      }
    }
  }

  protected Package definePackage(String pkgName, Manifest manifest) {
    if (manifest == null) {
      return definePackage(pkgName, null, null, null, null, null, null, null);
    } else {
      String specTitle = null;
      String specVersion = null;
      String specVendor = null;
      String implTitle = null;
      String implVersion = null;
      String implVendor = null;
      String sealed = null;
      Attributes attributes = manifest.getAttributes(pkgName.replace('.', '/'));

      if (attributes != null) {
        specTitle = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
        specVersion = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
        specVendor = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        implTitle = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        implVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        implVendor = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        sealed = attributes.getValue(Attributes.Name.SEALED);
      }

      attributes = manifest.getMainAttributes();

      if (attributes != null) {
        if (specTitle == null) {
          specTitle = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
        }

        if (specVersion == null) {
          specVersion = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
        }

        if (specVendor == null) {
          specVendor = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
        }

        if (implTitle == null) {
          implTitle = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
        }

        if (implVersion == null) {
          implVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        }

        if (implVendor == null) {
          implVendor = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
        }

        if (sealed == null) {
          sealed = attributes.getValue(Attributes.Name.SEALED);
        }
      }

      return definePackage(pkgName, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, null);
    }
  }

  //this is a class with .; can call from childs
  protected byte[] readClassData(String name) throws ClassNotFoundException {
    synchronized (inloadableLocal) {
      if (inloadableLocal.contains(name)) {
        throw new ClassNotFoundException(name);
      }
    }

    if (isContainClass(name)) {
      String findName = name.replace('.', '/') + ".class";
      byte[] result = readResourceData(findName, true);

      if (result != null) {
        return result;
      } else {
        synchronized (inloadableLocal) {
          inloadableLocal.add(name);
        }
      }
    }

    throw new ClassNotFoundException(name);
  }

  //can call from childs
  protected byte[] readResourceData(String name, boolean filterFlag) {
    synchronized (this.name) {
      switch (pattern) {
        case EMPTY: {
          return null;
        }
        case JARS: {
          for (int i = 0; i < jarFilters.length; i++) {
            if (filterFlag) {
              if ((jarFilters[i] != null) && (!jarFilters[i].accept(name))) {
                continue;
              }
            }

            if (openJar(i)) {
              byte[] result = readResourceFromJar(name, jarFilters[i].getJar());
              closeJar(i);

              if (result != null) {
                isFindInJar = true;
                findIndex = i;
                return result;
              }
            }
          } 

          return null;
        }
        case DIRS: {
          for (int i = 0; i < dirFiles.length; i++) {
            if (isExist(i)) {
              byte[] result = readResourceFromDir(name, dirFiles[i]);
              releaseDir(i);

              if (result != null) {
                isFindInJar = false;
                findIndex = i;
                return result;
              }
            }
          } 

          return null;
        }
        case MIXED: {
          for (int i = 0; i < jarFilters.length; i++) {
            if (filterFlag) {
              if ((jarFilters[i] != null) && (!jarFilters[i].accept(name))) {
                continue;
              }
            }

            if (openJar(i)) {
              byte[] result = readResourceFromJar(name, jarFilters[i].getJar());
              closeJar(i);

              if (result != null) {
                isFindInJar = true;
                findIndex = i;
                return result;
              }
            }
          } 

          // CHANGED
          for (int i = 0; i < dirFiles.length; i++) {
            if (isExist(i)) {
              byte[] result = readResourceFromDir(name, dirFiles[i]);
              releaseDir(i);

              if (result != null) {
                isFindInJar = false;
                findIndex = i;
                return result;
              }
            }
          } 

          return null;
        }
        default: {
          return null;
        }
      }
    }
  }

  //overwrite from filters
  protected boolean isContainClass(String name) {
    return true;
  }

  //overwrite from multi-domain classloaders
  protected ProtectionDomain getProtectionDomain() {
    return domain;
  }

  //------------private methods------------------------
  private byte[] readResourceFromJar(String name, JarFile jar) {
    JarEntry entry = jar.getJarEntry(name);

    if (entry == null) {
      return null;
    }

    byte[] byteBuffer = new byte[BUFFER_SIZE];
    ByteArrayOutputStream buffer = null;
    int offset = 0;
    try {
      InputStream in = jar.getInputStream(entry);
      int read = 0;

      while ((read != -1) & (offset < BUFFER_SIZE)) {
        read = in.read(byteBuffer, offset, BUFFER_SIZE - offset);

        if (read != -1) {
          offset += read;
        }
      }

      while (read != -1) {
        if (buffer == null) {
          buffer = new ByteArrayOutputStream(2 * BUFFER_SIZE);
        }

        buffer.write(byteBuffer, 0, BUFFER_SIZE);
        offset = 0;

        while ((read != -1) & (offset < BUFFER_SIZE)) {
          read = in.read(byteBuffer, offset, BUFFER_SIZE - offset);

          if (read != -1) {
            offset += read;
          }
        }
      }

      if (buffer != null) {
        buffer.write(byteBuffer, 0, offset);
      }

      in.close();
    } catch (IOException ioException) {
      //$JL-EXC$
      ioException.printStackTrace();
      return null;
    }

    if (buffer != null) {
      try {
        byteBuffer = buffer.toByteArray();
        buffer.close();
      } catch (IOException ioException) {
        //$JL-EXC$
        ioException.printStackTrace();
      }
    } else {
      byte[] tempBuffer = new byte[offset];
      System.arraycopy(byteBuffer, 0, tempBuffer, 0, offset);
      byteBuffer = tempBuffer;
    }

    return byteBuffer;
  }

  private byte[] readResourceFromDir(String name, File dir) {
    File f = new File(dir, name);

    if (f.exists()) {
      try {
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        byte[] result = new byte[(int) raf.length()];
        raf.seek(0);
        raf.read(result, 0, result.length);
        raf.close();
        return result;
      } catch (IOException ioException) {
        //$JL-EXC$
        ioException.printStackTrace();
      }
    }

    return null;
  }

  private boolean openJar(int i) {
    if (jarFilters[i] == null) {
      try {
        jarFilters[i] = new JarFilter(jars[i]);
      } catch (IOException ioException) {
        //$JL-EXC$
        /////
        //  you have to log this
        //        ioException.printStackTrace();
        return false;
      }
    } else {
      try {
        jarFilters[i].open(jars[i]);
      } catch (IOException ioException) {
        //$JL-EXC$
        ioException.printStackTrace();
        return false;
      }
    }

    return true;
  }

  private void closeJar(int i) {
    try {
      jarFilters[i].getJar().close();
    } catch (IOException ioException) {
      //$JL-EXC$
      ioException.printStackTrace();
    } catch (NullPointerException npException) {
      //$JL-EXC$
      npException.printStackTrace();
    }
  }

  private boolean isExist(int i) {
    if (dirFiles[i] == null) {
      dirFiles[i] = new File(dirs[i]);
    }

    return dirFiles[i].exists();
  }

  private void releaseDir(int i) {
    dirFiles[i] = null;
  }

  private void setName(String name) {
    if (name == null) {
      this.name = "NULL";
    } else {
      this.name = name;
    }
  }

  public boolean filterCheck(String className) {
    synchronized (inloadableLocal) {
      if (inloadableLocal.contains(className)) {
        return false;
      }
    }

    if (className.startsWith("[")) {
      return true;
    }

    synchronized (this.name) {
      if (pattern == JARS) {
        String findName = className.replace('.', '/') + ".class";

        for (int i = 0; i < jarFilters.length; i++) {
          if (jarFilters[i] == null) {
            try {
              jarFilters[i] = new JarFilter(jars[i]);
            } catch (IOException ioException) {
              //$JL-EXC$
              continue;
            }
          }

          if (jarFilters[i].accept(findName)) {
            return true;
          }
        } 

        return false;
      } else {
        return true;
      }
    }
  }

}

