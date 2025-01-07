package com.sap.ats.tests.webservices.dynamic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.ats.env.LogEnvironment;
import com.sap.ats.env.TestEnvironment;
import com.sap.ats.env.system.ServiceEnvironmentFactory;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.webservices.espbase.client.dynamic.DGenericService;
import com.sap.engine.services.webservices.espbase.client.dynamic.GenericServiceFactory;
import com.sap.engine.services.webservices.espbase.client.dynamic.ServiceFactoryConfig;

/*
 * Created on 2005-10-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author ivan-m
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Util {
  
  private static final Random random = new Random();
  
  public static void main(String[] args) throws Exception {
    extractZipFile(new File("."), new ZipFile("D:/develop/ATS/tests/AppServer/check/webservices/DynamicWS/wsdls.zip"));
    deleteFile(new File("wsdls"));
  }

  public static void extractZipFile(File rootDirFile, ZipFile zipFile) throws IOException {
    Enumeration zipEntriesEnumeraton = zipFile.entries();
    while(zipEntriesEnumeraton.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry)(zipEntriesEnumeraton.nextElement());
      if(zipEntry.isDirectory()) {
        File dir = new File(rootDirFile, zipEntry.getName());
        dir.mkdirs();
      } else {
        extractFileFromZipEntry(rootDirFile, zipFile, zipEntry);
      }
    }
  }
  
  private static void extractFileFromZipEntry(File rootDirFile, ZipFile zipFile, ZipEntry zipEntry) throws IOException {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(new File(rootDirFile, zipEntry.getName())); 
      InputStream zipEntryInputStream = zipFile.getInputStream(zipEntry);
      byte[] buffer = new byte[1024];
      int readByutes = -1;
      while((readByutes = zipEntryInputStream.read(buffer)) > 0) {
        fileOutputStream.write(buffer, 0, readByutes);
      }
    } finally {
      if(fileOutputStream != null) {
        fileOutputStream.close();
      }
    }
  }
  
  public static File determineCompleteDir(String rootDir, String childDir) throws IOException {
    return(new File(rootDir, childDir));
  }
  
  public static File determineCompleteFile(File rootDirFile, String childDir) throws IOException {
    return(new File(rootDirFile, childDir));
  }
  
  public static int generateInt(int limit) {
    return(random.nextInt(limit));
  }
  
  public static int generateInt() {
    return(random.nextInt());
  }
  
  public static long generateLong() {
    return(random.nextLong());
  }

  public static double generateDouble() {
    return(random.nextDouble());
  }
  
  public static float generateFloat() {
    return(random.nextFloat());
  }
  
  public static void generateBytes(byte[] bytes) {
    random.nextBytes(bytes);
  }
  
  public static String[] determineHttpHostandPort(TestEnvironment testEnv) {
    Properties atsProps = testEnv.getProperties();
    String host = atsProps.getProperty("server_host");
    String port = atsProps.getProperty("http_port", "-1");
    return(new String[]{host, port});
  }
  
  public static String[] determineUserAndPassword(TestEnvironment testEnv) {
    String user = testEnv.getProperties().getProperty("server_user");
    String pass = testEnv.getProperties().getProperty("server_password");
    return(new String[]{user, pass});
  }
  
  public static DeployService getDeployService(TestEnvironment testEnv) throws NamingException {
    String[] userAndPass = determineUserAndPassword(testEnv);
    return(getDeployService(userAndPass[0], userAndPass[1]));
  }
  
  public static DeployService getDeployService(String user, String pass) throws NamingException {
    Properties initialCtxProps = new Properties();
    initialCtxProps.setProperty("domain", "true");
    initialCtxProps.setProperty(InitialContext.SECURITY_PRINCIPAL, user);
    initialCtxProps.setProperty(InitialContext.SECURITY_CREDENTIALS, pass);
    InitialContext initialContext = new InitialContext(initialCtxProps);
    return((DeployService)(initialContext.lookup("deploy")));
  }
  
  public static String createAdditionalClasspath(LogEnvironment logEnvironment) throws Exception {
    StringBuffer classpathBuffer = new StringBuffer();
    LoadContext loadContext = ServiceEnvironmentFactory.getServiceContext().getCoreContext().getLoadContext();
    
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames(loadContext.getClassLoader("library:webservices_lib").getParent()));
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames("library:webservices_lib"));
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames("library:tc~bl~base_webservices_lib"));
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames("interface:webservices_api"));
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames("library:sapxmltoolkit"));
    appendJarsToClasspathBuffer(classpathBuffer, loadContext.getResourceNames("library:compilation_lib"));
    
    String additionalClasspath = classpathBuffer.toString();
    logEnvironment.log("Additional classpath is : " + additionalClasspath);
    return(additionalClasspath);
  }

  private static void appendJarsToClasspathBuffer(StringBuffer classpathBuffer, String[] classpathJars) {
    for(int i = 0; i < classpathJars.length; i++) {
      classpathBuffer.append(classpathJars[i]);
      classpathBuffer.append(File.pathSeparator);
    }
  }
  
  public static void logIfLogEnvIsNotNull(LogEnvironment logEnvironment, String logMessage) {
    if(logEnvironment != null) {
      logEnvironment.log(logMessage);
    }
  }
  
  public static void logIfLogEnvIsNotNull(LogEnvironment logEnvironment, Throwable tr) {
    if(logEnvironment != null) {
      logEnvironment.log(tr);
    }
  }
  
  public static void removeApplication(LogEnvironment logEnvironment, String applicationName, DeployService deployService) {
    try {
      deployService.remove(applicationName);
    } catch(RemoteException remoteExc) {
      logIfLogEnvIsNotNull(logEnvironment, "Application " + applicationName + " is not removed successfuly.");
      logIfLogEnvIsNotNull(logEnvironment, remoteExc);
      return;
    }
    logIfLogEnvIsNotNull(logEnvironment, "Application " + applicationName + " is removed successfuly.");
  }
  
  public static void deleteCreatedDir(File dirFile, LogEnvironment logEnvironment) {
    if(dirFile != null) {
      deleteFile(dirFile);
      if(logEnvironment != null) {
        try {
          logIfLogEnvIsNotNull(logEnvironment, "Directory " + dirFile.getCanonicalPath() + " is deleted.");
        } catch(IOException ioExc) {
        }
      }
    }
  }
  
  public static void deleteFile(File file) {
    if(file.isDirectory()) {
      File[] contentFiles = file.listFiles();
      for(int i = 0; i < contentFiles.length; i++) {
        File contentFile = contentFiles[i];
        deleteFile(contentFile);
      }
    }
    file.delete();
  }
  
  public static String deployEar(LogEnvironment logEnvironment, File earFile, DeployService deploy) throws Exception {
    logEnvironment.log("Try to deploy ear file " + earFile.getCanonicalPath());
    String[] remoteSupports = {"p4"};
    Properties deployProps = new Properties(); 
    String[] deployResults = deploy.deploy(earFile.getCanonicalPath(), remoteSupports, deployProps);
    logEnvironment.log("Ear file " + earFile.getCanonicalPath() + " is deployed.");
    String applicationName = deployResults[0].substring("Application : ".length());
    logEnvironment.log("Application " + applicationName + " is deployed.");
    deploy.startApplicationAndWait(applicationName);
    logEnvironment.log("Application " + applicationName + " is started.");
    return(applicationName);
  }
  
  public static File transferAndExtractWSDLsZip(TestEnvironment testEnvironment, String wsdlsZipFilePath, LogEnvironment logEnvironment, String wsdlsDirName) throws IOException {
    File archivedWsdlsFile = testEnvironment.getFile(wsdlsZipFilePath);
    File rootDirFile = archivedWsdlsFile.getParentFile();
    logEnvironment.log("Wsdls zip file is downloaded to the directory " + rootDirFile.getCanonicalPath());
    extractZipFile(rootDirFile, new ZipFile(archivedWsdlsFile));
    File wsdlsDirFile = new File(rootDirFile, wsdlsDirName);
    logEnvironment.log("Wsdl files are extracted in directory " + wsdlsDirFile.getCanonicalPath());
    return(wsdlsDirFile);
  }
  
  private static ServiceFactoryConfig createServiceFactoryConfig(String workingDirPath, boolean appendDefaultBindings, String httpUser, String httpPass, String additionalClasspath) {
    ServiceFactoryConfig serviceFactoryCfg = new ServiceFactoryConfig();
    serviceFactoryCfg.setTemporaryDir(workingDirPath);
    serviceFactoryCfg.setAdditionalClassPath(additionalClasspath);
    serviceFactoryCfg.setAppendDefaultBindings(appendDefaultBindings);
    if(httpUser != null && httpPass != null) {
      serviceFactoryCfg.setUser(httpUser);
      serviceFactoryCfg.setPassword(httpPass);
    }
    return(serviceFactoryCfg);
  }
  
  public static DGenericService createService(GenericServiceFactory serviceFactory, String wsdlUrl, String relativeWorkingDirPath, boolean appendDefaultBindings, String httpUser, String httpPass, String additionalClasspath, LogEnvironment logEnv) throws Exception {
    String workingDirPath = determineWorkingDirPath(relativeWorkingDirPath, logEnv);
    ServiceFactoryConfig serviceFactoryCfg = createServiceFactoryConfig(workingDirPath, appendDefaultBindings, httpUser, httpPass, additionalClasspath);
    DGenericService service = serviceFactory.createService(wsdlUrl, serviceFactoryCfg);
    logEnv.log("SERVICE :");
    logEnv.log(service.toString());
    return(service);
  }
  
  private static String determineWorkingDirPath(String relativeWorkingDirPath, LogEnvironment logEnv) throws Exception {
    File workingDir = new File(relativeWorkingDirPath);
    String workingDirPath = workingDir.getCanonicalPath();
    logEnv.log("WORKING DIR : " + workingDirPath);
    return(workingDirPath);
  }
  
  public static String determineWSDLFilePath(String relativeWSDLFilePath, LogEnvironment logEnv, TestEnvironment testEnv) throws Exception {
    File wsdlFile = testEnv.getFile(relativeWSDLFilePath);
    String wsdlFilePath = wsdlFile.getCanonicalPath();
    logEnv.log("WSDL FILE : " + wsdlFilePath);
    return(wsdlFilePath);
  }
  
  /**
   * Copies file using FileInput/OutputStreams
   *
   * @param src  Source file to copy from
   * @param dest Destination file to copy to
   * @throws IOException Thrown if problem with the copy occurs
   */
  public static void copyFile(File src, File dest) throws IOException {
    FileInputStream fis = null;
    FileOutputStream fos = null;

    if (src == null || dest == null || !src.exists()) {
      return;
    }

    if (!dest.getParentFile().exists()) {
      dest.getParentFile().mkdirs();
    }

    try {
      fis = new FileInputStream(src);
      fos = new FileOutputStream(dest);
      byte[] buffer = new byte[256*1024];
      int read;

      while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
        fos.write(buffer, 0, read);
      }
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
        fis = null;
      }
      try {
        fos.close();
      } catch (Exception e) {
        fos = null;
      }
    }
  }
  
  public static void writeZipEntry(ZipOutputStream zipOut, InputStream in, int size, String name) throws IOException {
    try {
      byte bytes[] = new byte[size];
      int result = in.read(bytes);

      if (result == -1) {
        return;
      }

      ZipEntry zipEntry = new ZipEntry(name.replace('\\', '/'));
      zipEntry.setMethod(ZipEntry.DEFLATED);
      zipEntry.setSize(bytes.length);

      if (bytes.length > 0) {
        zipOut.putNextEntry(zipEntry);
        zipOut.write(bytes, 0, bytes.length);
        zipOut.closeEntry();        
      }      
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        in = null;
      }
    }
  }
  
}
