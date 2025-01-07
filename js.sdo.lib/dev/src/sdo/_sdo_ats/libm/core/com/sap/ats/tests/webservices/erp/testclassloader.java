package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.RandomAccessFile;
import java.io.IOException;
import java.net.URL;

import com.sap.ats.env.LogEnvironment;

public class TestClassLoader extends ClassLoader {
  private String classDirs[];
  private LogEnvironment logger = null;

  public TestClassLoader(String[] dirs) {
    this(null, dirs);
  }

  public TestClassLoader(ClassLoader parent, String[] dirs) {
    super((parent != null) ? parent:TestClassLoader.class.getClassLoader());
    setClassDir(dirs);
  }

  public void setClassDir(String classDir[]) {
    for (int i = 0; i < classDir.length; i++) {
    }
    this.classDirs = classDir;
  }

  public void setLogger(LogEnvironment log) {
    logger = log;
  }
  
  protected Class findClass(String name) throws ClassNotFoundException {
    byte[] buffer = readClassData(name);
    if (buffer != null) {
//      logger.log("Read class data: " + buffer.length);
      return defineClass(name, buffer, 0, buffer.length);
    }
//    logger.log("Read class data: " + buffer);    
    throw new ClassNotFoundException("Cannot load class: " + name);
  }


  protected URL findResource(String name) {
    for (int i = 0; i < classDirs.length; i++) {
      File f = new File(classDirs[i], name);
//      System.out.println("findResource file: " + f.getAbsolutePath());
      if (f.exists()) {
//        System.out.println("findResource file: " + f.getAbsolutePath() +  ".... exists");
        try {
          return f.toURL();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

//    System.out.println("!!!! findResource was unabled to find: " + name);
    return null;
  }

  private byte[] readClassData(String className) {
    String fileName = className.replace('.', '/') + ".class";
//    logger.log("Searching for class: " + className);    
    byte[] res = readResourceFromDir(fileName);
//    logger.log("Read resource from dir: " + res);    
    if (res == null) {
      //for test class
      fileName = className.replace('.', '/') + ".tstcls";
      res = readResourceFromDir(fileName);
    }
    return res;
  }

  //reads the resouce if available
  private byte[] readResourceFromDir(String fileName) {
    for (int i = 0; i < classDirs.length; i++) {
      File f = new File(classDirs[i], fileName);
//      logger.log("File read from resource dir: " + f.getAbsolutePath());
      //System.out.println("readResourceFromDir file: " + f.getAbsolutePath());
      FileInputStream fIn = null;
      
      try {
        fIn = new FileInputStream(f);        
        byte[] result = new byte[(int) fIn.available()];
        fIn.read(result, 0, result.length);
        fIn.close();
        return result;
      } catch (FileNotFoundException fnfe) {
        logger.log(fnfe);
      } catch (IOException ioe) {
        logger.log(ioe);
      }
            
//      if (f.exists()) {
//        logger.log("EXISTS!");
//        try {
//          RandomAccessFile raf = new RandomAccessFile(f, "r");
//          byte[] result = new byte[(int) raf.length()];
//          raf.seek(0);
//          raf.read(result, 0, result.length);
//          raf.close();
//          return result;
//        } catch (IOException ioException) {
//          logger.log(ioException);
//          ioException.printStackTrace();
//        }
//      }
    }
    return null;
  }
  
}
