package com.sap.engine.rmic;

import com.sap.engine.rmic.iiop.StubTieGenerator;
import com.sap.engine.rmic.p4.P4StubSkeletonGenerator;
import com.sap.engine.rmic.log.RMICLogger;
import com.sap.engine.rmic.extension.RmicExtensionInterface;
import com.sap.engine.parse.ParseDir;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;


/**
 * Command line tool for invoking P4StubSkeletonGenerator and StubTieGenerator
 *
 * @author Mladen Droshev
 * @version 7.0
 * @see com.sap.engine.rmic.p4.P4StubSkeletonGenerator
 * @see com.sap.engine.rmic.iiop.StubTieGenerator
 */
public class RMIC implements Runnable {//$JL-LOG_CONFIG$  //$JL-SYS_OUT_ERR$

  public static final String RMIC_CLASSPATH_DELIMETER = File.pathSeparator;
  public static final String RMIC_EXTENSION_CLASS = "sap.rmic.extension_class";

  private static Set<RmicExtensionInterface> extensions = new HashSet<RmicExtensionInterface>();
  public static boolean debug = false;

  private String javaDir = null;
  private String classDir = null;
  private String classpath = "." + File.pathSeparator;
  private String[] classes_for_generation = null;   //list of classes for p4-generation
  private Class[] classez_for_generation = null;
  private String rootDir = null;
  private String jarDir = null;
  private String lists = null;
  private String exclude_classes = null;
  private String tempJarDir = "rmic_jars" + File.separator;
  private String tempDir = "." + File.separator + "rmic_logs";
  public static String logDir = null;

  private boolean loadClasses = false;

  private boolean javac = true;
  private boolean rmi_p4 = false;
  private boolean rmi_iiop = false;
  private boolean keep = true;
  private boolean compile_debug = false;
  private boolean additional = false;
  private boolean verbose = false;
  public static String projectName = "rmic";
  public static String projectDir = ".";
  public static boolean dumpOnAntConsole = false;
  private boolean isMadeResources = false;   //check if the resources are ready
  private boolean rmic_classFile = true; //rmic for a classfile
  private boolean rmic_jarFile = false; //rmic for a jarFile
  private boolean rmic_dir = false; //rmic for a directory
  private boolean rmic_list = false; //rmic for jars/directories
  private boolean exclude = false;
  private String[] args = null;
  private static final String consoleHelp = "ConsoleHelp.txt";
  ClassLoader loader = null;
  URL[] urls = null;

  public static boolean ncompile = false;

  private static FileOutputStream out = null;
  private String path_error_dir = ".";
  public static boolean printOnConsole = false;
  static Properties logging_props = null;
  private ParseDir parser = null;
  public static String project = null;

  public RMIC() {
  }

  public RMIC(String javaOutputDir, String classOutputDir, String classpath, String[] stuff_for_generation) {
    this.javaDir = javaOutputDir;
    this.classDir = classOutputDir;
    this.classpath = classpath + File.pathSeparator + System.getProperty("java.class.path");
    if (stuff_for_generation != null) {
      this.classes_for_generation = stuff_for_generation;
      this.rmi_p4 = true;
    }

    if (!isMadeResources) {
      urls = makeURLResources(this.classpath);
      isMadeResources = true;
    }
  }

  public RMIC(String[] args) throws Exception {
    this.args = args;
    ebcdicConvert(this.args);
    if (!parseArgs()) {
      dumpHelpMsg();
    }
  }

  public void run() {
  }

  private static void ebcdicConvert(String[] args) {
    String platform = System.getProperty("platform.notASCII");

    if (platform != null && platform.equalsIgnoreCase("false")) {
      System.setErr(System.out);
      String ASCII = "ISO8859_1";
      String EBCDIC = "Cp1047";
      try {
        for (int i = 0; i < args.length; i++) {
          args[i] = new String(args[i].getBytes(ASCII), EBCDIC);
        }
      } catch (Exception ex) {
        RMICLogger.throwing(ex);
        return;
      }
    }
  }

  public boolean generate_and_compile() throws RMICException {
    RMICLogger.logEnterMethod("generate_and_compile()");
    boolean isOk = true;
    loader = makeURLLoader(this.getClass().getClassLoader(), this.urls);
    if (rmi_p4 || rmi_iiop) {
      isOk = isOk && prepare();
    }

    if (rmi_p4) {
      if (isOk) {
        Vector generated = p4_generate();
        if (generated != null && generated.size() > 0) {
          postProcess(generated, "RMI_P4");
        } else {
          RMICLogger.logMSG(" The RMIC cannot find the classes for p4_generation or maybe there is a problem.");
        }
      }
    }

    if (rmi_iiop) {
      if (isOk) {
        Vector generated = iiop_generate();
        if (generated != null && generated.size() > 0) {
          postProcess(generated, "RMI_IIOP");
        } else {
          RMICLogger.logMSG(" The RMIC cannot find the classes for iiop_generation or maybe there is a problem.");
        }
      }
    }

    if (rmic_jarFile) {
      isOk = isOk && packJarFile();
    }
    try {
      if (parser != null) {
        parser.deleteTempDir();
      }
    } catch (Throwable t) {
      RMICLogger.throwing(t);
    }

    RMICLogger.logExitMethod("generate_and_compile()");
    if (classes_for_generation == null || classes_for_generation.length == 0) {
      return true;
    }
    return isOk;
  }

  private void postProcess(Vector generated, String type) {
    if (extensions == null || extensions.size() == 0) {
      return;
    }

    for (RmicExtensionInterface exten : extensions) {
      exten.postProcess(this, generated, type);
    }
  }

  private ClassLoader makeURLLoader(ClassLoader parent, URL[] resources) {
    return new URLClassLoader(resources, parent);
  }

  private String getCompactPath(String allPath, String delim) {
    String result = "";
    StringTokenizer st = new StringTokenizer(allPath, delim);
    Vector compactPaths = new Vector();
    while (st.hasMoreElements()) {
      String temp = (String) st.nextElement();
      if (!compactPaths.contains(temp)) {
        compactPaths.addElement(temp);
      }
    }
    Enumeration enumer = compactPaths.elements();
    while (enumer.hasMoreElements()) {
      result += delim + enumer.nextElement();
    }
    return result;
  }

  private boolean makeAllDirs(Class[] classez) {
    RMICLogger.logEnterMethod("makeAllDiirs");
    if (classez != null && classez.length > 0) {
      for (int i = 0; i < classez.length; i++) {
        String className = ((Class) classez[i]).getName();
        String aPackage = "";
        if (className.lastIndexOf('.') != -1) {
          aPackage = className.substring(0, className.lastIndexOf('.'));
        }
        String replacedFileName = aPackage.replace('.', File.separatorChar);
        if (!makeDir(javaDir, replacedFileName)) {
          RMICLogger.logMSG("DEBUG :The directory :" + javaDir + File.separator + replacedFileName + " didn't created. ");
        }
        if (!makeDir(classDir, replacedFileName)) {
          RMICLogger.logMSG("DEBUG : The directory :" + javaDir + File.separator + replacedFileName + " didn't created. ");
        }
      }
    } else {
      RMICLogger.logMSG("The rmic task cannot find classes for remote support");
      return false;
    }
    RMICLogger.logExitMethod("makeAllDirs");
    return true;
  }

  private String[] parseExt(String arg) {
    ArrayList<String> result = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(arg, File.pathSeparator);
    while (st.hasMoreTokens()) {
      result.add(st.nextToken());
    }
    if (result.size() == 0) {
      return null;
    }
    return result.toArray(new String[0]);
  }


  private void loadExt(String arg) {
    if (arg != null) {
      String[] _t = parseExt(arg);
      if (_t != null) {
        for (String token : _t) {
          try {
            Class c = Class.forName(token);
            RmicExtensionInterface o = (RmicExtensionInterface) c.newInstance();
            extensions.add(o);
          } catch (ClassNotFoundException e) { //todo
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InstantiationException e) {
            e.printStackTrace();
          } catch (ClassCastException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * parseArgs - prepare settings before execute a process
   */
  private boolean parseArgs() {
    if (args.length < 4) {
      return false;
    }

    /* load Extension classes form system props */
    String extC = System.getProperty(RMIC_EXTENSION_CLASS);
    if (extC != null && extC.length() > 0) {
      loadExt(extC);
    }

    if (project != null) {
      classDir = project;
    }

    for (int i = 0; i < args.length - 1; i++) {
      if (args[i].equalsIgnoreCase(RMIC_EXTENSION_CLASS)) {
        loadExt(args[++i]);
      } else if (args[i].equals("-s")) { //source directory
        javaDir = args[++i].trim();
      } else if (args[i].equals("-classpath")) {
        i++;
        if (args[i].startsWith("@")) {
          classpath = getCPFromFile(args[i].substring(1));     //skip @, read from file
        } else {
          classpath = args[i];
        }
      } else if (args[i].equals("-nkeep")) {
        keep = false;
      } else if (args[i].equals("-additional")) {
        additional = true;
      } else if (args[i].equals("-iiop")) {
        rmi_iiop = true;
      } else if (args[i].equals("-nrmi_p4")) {
        rmi_p4 = false;
      } else if (args[i].equals("-d")) {   //generated class files
        classDir = args[++i];
      } else if (args[i].equals("-jar")) {
        this.rmic_jarFile = true;
        this.rmic_classFile = false;
        this.jarDir = args[++i];
      } else if (args[i].equals("-dir")) {
        this.rmic_dir = true;
        this.rmic_classFile = false;
        this.rootDir = args[++i];
      } else if (args[i].equals("-list")) {
        this.rmic_list = true;
        this.rmic_classFile = false;
        this.lists = args[++i];
      } else if (args[i].equals("-g")) {
        this.compile_debug = true;
      } else if (args[i].equals("-verbose")) {
        this.verbose = true;
      } else if (args[i].equals("-project")) {
        RMIC.projectName = args[++i];
      } else if (args[i].equals("-projectDir")) {
        RMIC.projectDir = args[++i];
        if (logDir != null) {
          tempDir = RMIC.projectDir + File.separator + "rmic_logs" + File.separator;
          tempJarDir = "rmic_jars" + File.separator;
        }
      } else if (args[i].equals("-dump")) {
        RMIC.dumpOnAntConsole = true;
      } else if (args[i].equals("-logDir")) {
        logDir = args[++i];
        tempDir = RMIC.projectDir + File.separator + "rmic_logs" + File.separator;
        tempJarDir = "rmic_jars" + File.separator;
      } else if (args[i].equals("-ncompile")) {
        ncompile = true;
      } else if (args[i].equals("-exclude")) {

      } else {
        RMICLogger.logMSG("Invalid argument :" + args[i]);
      }
    }
    /* dump all arguments */
    for (int i = 0; i < args.length; i++) {
      RMICLogger.logMSG("args[" + i + "]= " + args[i]);
    }

    postParsing();
    RMICLogger.logExitMethod("parseArgs");
    return true;
  }

  private void postParsing() {
    if (classDir == null) {
      classDir = ".";
    }

    this.classpath += File.pathSeparator + System.getProperty("java.class.path");

    if (!rmi_iiop) {
      rmi_p4 = true;
    }

    if (!isMadeResources) {
      this.urls = makeURLResources(this.classpath);
      isMadeResources = true;
    }
    if ((rmi_p4 || rmi_iiop) && javaDir != null) {
      if (this.rmic_dir) {
        this.classpath += File.pathSeparator + this.rootDir;
      } else if (this.rmic_jarFile && javaDir != null) {
        this.classpath += File.pathSeparator + this.jarDir;
      } else if (this.rmic_list) {
        this.classpath += File.pathSeparator + this.lists;
      } else {
        this.classes_for_generation = new String[]{args[args.length - 1]};
      }
    }
  }

  private String getCPFromFile(String fName) {
    String result = null;
    StringBuffer sf = new StringBuffer();
    byte[] readed = null;
    try {
      File f = new File(fName);
      long lenght = f.length();
      FileInputStream dIn = new FileInputStream(f);
      readed = new byte[(int) lenght];
      dIn.read(readed);
    } catch (IOException tt) {
      RMICLogger.throwing(tt);
      RMICLogger.logMSG(">> Exception : " + tt.getMessage());
    }
    result = new String(readed);
    return result;
  }

  public static void main(String[] args) {
    try {
      if (args != null && args.length > 0 && !(args[0].equalsIgnoreCase("-?"))) {
        RMIC rmic = new RMIC(args);
        boolean result = rmic.generate_and_compile();
        RMICLogger.logMSG("GENERATION AND COMPILATION FINISHED " + (result ? " OK" : " WITH ERRORS!!!!"));
        if (!result) {
          System.out.println("RMIC generation and compilation finished with errors. See log files from dir ./traces ");
        } else {
          System.out.println("RMIC generation and compilation finished.  See log files from ./traces dir for more info.");
        }
      } else {
        dumpHelpMsg();
      }
    } catch (Exception e) {
      RMICLogger.throwing(e);
    }
  }

  private static void dumpHelpMsg() {
    try {
      InputStream in = RMIC.class.getResource(consoleHelp).openStream();
      int all = in.available();
      int current = 0;
      byte[] arr = new byte[in.available()];
      current = in.read(arr);
      while (current < all) {
        current += in.read(arr, current, (all - current));
      }
      System.out.println(new String(arr));
    } catch (IOException e) {
      // $JL-EXC$
    }
  }

  private boolean makeDir(String root, String children) {
    File f = new File(root, children);
    return (f).mkdirs();
  }

  private URL[] makeURLResources(String classpath) {
    URL[] res = null;
    if (classpath != null) {
      if (classpath.indexOf(RMIC_CLASSPATH_DELIMETER) != -1) {
        Vector allRes = new Vector();
        StringTokenizer st = new StringTokenizer(classpath, RMIC_CLASSPATH_DELIMETER);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          URL url = createURL(token);
          if (url != null && token.length() > 0) {
            allRes.addElement(url);
          }
        }
        res = new URL[allRes.size()];
        res = (URL[]) allRes.toArray(new URL[0]);
      }
    }
    for (int i = 0; i < res.length; i++) {
      RMICLogger.logMSG("resource : " + res[i]);
    }
    return res;
  }

  private URL createURL(String res) {
    try {
      return (new File(res)).toURL();
    } catch (MalformedURLException e) {    //$JL-EXC$
      RMICLogger.logMSG("DEBUG : createURL() Exception : " + e.getMessage());
      return null;
    }
  }

  public boolean prepare() {
    RMICLogger.logEnterMethod("prepare() for generation and compilation");
    boolean result = true;
    if (!loadClasses) {
      if (this.loader == null) {
        this.loader = makeURLLoader(this.getClass().getClassLoader(), this.urls);
      }
      if (rmic_jarFile) {
        extractFilesFromJars(jarDir);
      } else if (rmic_dir) {
        summarizeFilesFromDir(rootDir);
      } else if (rmic_list) {
        summarizeClasses(lists);
      } else {
        this.classez_for_generation = new Class[this.classes_for_generation.length];
        for (int i = 0; i < classes_for_generation.length; i++) {
          try {
            RMICLogger.logMSG("loadclass:" + classes_for_generation[i]);
            this.classez_for_generation[i] = loader.loadClass(classes_for_generation[i]);
          } catch (ClassNotFoundException e) {
            RMICLogger.throwing(e);
            RMICLogger.logMSG("DEBUG : Status : ClassNotFoundException occurred. Class not found:" + classes_for_generation[i]);
            return false;
          }
        }
      }
    }

    if (exclude && this.classez_for_generation != null && this.classez_for_generation.length > 0 && this.exclude_classes != null) {
      this.classez_for_generation = excludeClasses(this.classez_for_generation, this.exclude_classes);
    }
    result &= makeAllDirs(this.classez_for_generation);
    if (result) {
      RMICLogger.logMSG("current classpath : " + this.classpath + System.getProperty("java.class.path"));
    } else {
      return result;
    }
    RMICLogger.logExitMethod("prepare()");
    return result;
  }

  public Class[] excludeClasses(Class[] allClasses, String excludeList) {
    ArrayList excl = new ArrayList();
    if (excludeList.indexOf(RMIC_CLASSPATH_DELIMETER) != -1) {
      StringTokenizer st = new StringTokenizer(excludeList, RMIC_CLASSPATH_DELIMETER);
      while (st.hasMoreTokens()) {
        excl.add(st.nextToken());
      }
    } else {
      excl.add(excludeList);
    }
    ArrayList result = new ArrayList();
    if (excl.size() > 0) {
      for (int i = 0; i < allClasses.length; i++) {
        boolean foundClass = false;
        for (int j = 0; j < excl.size(); j++) {
          if (((String) excl.get(j)).equals(allClasses[i].getName())) {
            foundClass = true;
          }
        }
        if (!foundClass) {
          result.add(allClasses[i]);
        }
      }
    } else {
      return null;
    }
    return (Class[]) result.toArray(new Class[0]);
  }

  /**
   * generate all p4 remote supprt classes from all set classes
   *
   * @return vector
   */
  public Vector p4_generate() throws RMICException {
    RMICLogger.logEnterMethod("p4_generate()");
    Vector for_compile = new Vector();

    if (this.classez_for_generation != null) {
      int i = 0;
      try {
        for (; i < classez_for_generation.length; i++) {
          P4StubSkeletonGenerator gen = new P4StubSkeletonGenerator(classez_for_generation[i], javaDir, loader);
          Vector temp = (gen).generate();
          Object[] t = temp.toArray();
          for (int j = 0; j < t.length; j++) {
            if (!for_compile.contains(t[j])) {
              for_compile.addElement(t[j]);
            }
          }
        }
      } catch (Throwable e) {//$JL-EXC$
        RMICLogger.logMSG("DEBUG >> Exception : " + e.getMessage() + "<> class: " + classez_for_generation[i]);
        RMICLogger.throwing(e);
        throw new RMICException(e);
      }
    }
    RMICLogger.logExitMethod("p4_generate()");
    return for_compile;
  }

  /**
   * generate all iiop remote supprt classes from all set classes
   *
   * @return vector
   */
  public Vector iiop_generate() throws RMICException {
    RMICLogger.logEnterMethod("iiop_generate()");
    Vector for_compile = new Vector();
    if (this.classez_for_generation != null) {
      int i = 0;
      try {
        for (; i < classez_for_generation.length; i++) {
          Vector temp = (new StubTieGenerator(classez_for_generation[i], javaDir, new Hashtable(), additional, loader)).generate();
          Object[] t = temp.toArray();
          for (int j = 0; j < t.length; j++) {
            for_compile.addElement(t[j]);
          }
        }
      } catch (IOException e) {
        RMICLogger.logMSG("DEBUG : >> Exception : " + e.getMessage() + "<> class: " + classez_for_generation[i]);
        RMICLogger.throwing(e);
        throw new RMICException(e);
      }
    }
    RMICLogger.logExitMethod("iiop_generate()");
    return for_compile;
  }

  public void summarizeFilesFromDir(String dir) {
    if (parser == null) {
      try {
        parser = new ParseDir(dir);
      } catch (Error e) {//$JL-EXC$
        RMICLogger.throwing(e);
        RMICLogger.logMSG(">> Exception : " + e.getMessage());
      }
    } else {
      parser.setRootDir(dir);
    }
    Class[] result = parser.getRemSupportClassesFromDir(dir, classpath);
    if (classez_for_generation != null && classez_for_generation.length > 0 && result != null) {
      Class[] sum = new Class[classez_for_generation.length + result.length];
      System.arraycopy(classez_for_generation, 0, sum, 0, classez_for_generation.length);
      System.arraycopy(result, 0, sum, classez_for_generation.length, result.length);
      classez_for_generation = sum;
    } else {
      classez_for_generation = result;
    }
  }

  public void summarizeClasses(String forParse) {
    String[] temp_lists = null;
    if (forParse != null) {
      if (forParse.indexOf(RMIC_CLASSPATH_DELIMETER) != -1) {
        ArrayList tL = new ArrayList();
        StringTokenizer st = new StringTokenizer(forParse, RMIC_CLASSPATH_DELIMETER);
        while (st.hasMoreTokens()) {
          tL.add((String) st.nextElement());
        }
        if (tL.size() > 0) {
          temp_lists = (String[]) (tL.toArray(new String[0]));
        }
      } else {
        temp_lists = new String[]{forParse};
      }
    }
    if (temp_lists != null && temp_lists.length > 0) {
      for (int i = 0; i < temp_lists.length; i++) {
        File tempFile = new File(temp_lists[i]);
        if (tempFile.isDirectory()) {
          summarizeFilesFromDir(temp_lists[i]);
        } else {
          extractFilesFromJars(temp_lists[i]);
        }
      }
    }
  }

  public void extractFilesFromJars(String jarName) {
    if (parser == null) {
      parser = new ParseDir(jarName, tempDir);
    } else {
      parser.setJarFileName(jarName);
      parser.setTempDir(tempDir);
    }
    //javaDir = tempDir;
    classDir = tempDir;
    Class[] tempCl = parser.getRemSupportClassesFromJar(jarName, classpath, tempDir);
    if (classez_for_generation != null && classez_for_generation.length > 0 && tempCl != null) {
      Class[] res = new Class[classez_for_generation.length + tempCl.length];
      System.arraycopy(classez_for_generation, 0, res, 0, classez_for_generation.length);
      System.arraycopy(tempCl, 0, res, classez_for_generation.length, tempCl.length);
      classez_for_generation = res;
    } else {
      classez_for_generation = tempCl;
    }
  }

  public boolean packJarFile() {
    boolean result = true;
    if (parser == null) {
      parser = new ParseDir(jarDir, tempDir);
    } else {
      parser.setJarFileName(jarDir);
      parser.setTempDir(tempDir);
    }
    parser.makeJarFile();
    parser.deleteTempDir();
    return result;
  }

  public void setJavaOutput(String javaDir) {
    this.javaDir = javaDir;
  }

  public void setClassOutput(String classDir) {
    this.classDir = classDir;
  }

  public void setClasspath(String classpath) {
    this.classpath = classpath;
  }

  public void setTempDir(String tempDir) {
    this.tempDir = tempDir;
  }

  public void setClassedForGeneration(String[] classes_for_generation) {
    this.classes_for_generation = classes_for_generation;
  }


  public static void setProject(String project) {
    RMIC.project = project;
  }

  public String getClassPath() {
    return getCompactPath(this.classpath + File.pathSeparator + System.getProperty("java.class.path"), File.pathSeparator);
  }

  public String getClassDir() {
    return this.classDir;
  }

  public boolean isVerbose() {
    return this.verbose;
  }

  public String getErrorDir() {
    return this.path_error_dir;
  }

  public boolean isDebug() {
    return this.compile_debug;
  }

  public String getSourceDir() {
    return this.javaDir;
  }

  public boolean isCompile() {
    return !ncompile;
  }

}