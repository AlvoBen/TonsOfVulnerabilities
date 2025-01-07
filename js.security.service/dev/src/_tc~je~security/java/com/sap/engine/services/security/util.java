package com.sap.engine.services.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.Subject;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.lib.lang.ConvertTools;
import com.sap.engine.services.security.login.SubjectWrapper;
import com.sap.engine.services.security.server.ModificationContextImpl;
import com.sap.engine.services.security.server.SecurityConfigurationPath;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 *  Common methods used from the security service,
 *
 * @version 6.30
 * @author  Ekaterina Zheleva
 */
public class Util {

  private final static boolean inServer = SystemProperties.getBoolean("server");
  private final static String COMPONENT_COMMON = "common:";
  private static LoadContext loadContext = null;
	private static final char[] FORBIDDEN_CONFIGNAME_CHARS = new char[] {'%','[',']','#','/'};
  
  private final static byte[] IV = new byte[8];
  private final static String ALGORITHM = "DES";
  private final static String CIPHER = "DES/CBC/PKCS5Padding";
  private final static String ENCRYPTION_KEY = "encryption_key";
  private final static int MAIN_PRINCIPAL_NOT_IN_SUBJECT = 0;
  private final static int MAIN_PRINCIPAL_IN_SUBJECT = 1;
  
  private static Cipher cipher = null;
  private static SecretKey key = null;
  private static IvParameterSpec ivParameterSpec = null;
  
  public static final String SECURITY_CATEGORY = "/System/Security";
  public static final String SECURITY_LOCATION = "com.sap.engine.services.security";
  
  public static final Location SEC_SRV_LOCATION = Location.getLocation(SECURITY_LOCATION);
  public static final Category SEC_SRV_CATEGORY = Category.getCategory(Category.SYS_SECURITY, SECURITY_CATEGORY);


  /**
   *  Deserializes an object with the correct class loader.
   *
   * @param bytes   the serialized version
   * @param offset  the offset of the data in the array
   * @param length  the length of the data in the array
   *
   * @return  the deserialized instance
   *
   * @throws NotSerializableException  thrown if object cannot be deserialized.
   */
  public static Serializable array2object(byte[] bytes, int offset, int length) throws NotSerializableException {
    Serializable serializable;
    ByteArrayInputStream bytearrayinputstream = null;
    InputSerializator objectinputstream = null;
    String loaderName = null;

    try {
      bytearrayinputstream = new ByteArrayInputStream(bytes, offset, length);
      objectinputstream = new InputSerializator(null, bytearrayinputstream);
      loaderName = (String) objectinputstream.readObject();
      objectinputstream.loaderName = loaderName;
      serializable = (Serializable) objectinputstream.readObject();
    } catch (NotSerializableException exception) {
      throw exception;
    } catch (Exception exception) {
      NotSerializableException nse = new NotSerializableException("Deserialization error.");
      nse.initCause(exception);
      throw nse;
    }

    try {
      if (objectinputstream != null) {
        objectinputstream.close();
      }
      if (bytearrayinputstream != null) {
        bytearrayinputstream.close();
      }
    } catch (Exception e) {
      SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "array2object", e);
    }

    return serializable;
  }

  /**
   *  Deserializes a subject instance fromthe given array.
   *
   * @param bytes  the array containing the serialized subject.
   * @param offset the offset of the data in the array
   * @param length the length of the data in the array.
   *
   * @return  the deserialized subject.
   *
   * @throws NotSerializableException  thrown if object cannot be deserialized.
   */
  public static SubjectWrapper array2subject(byte[] bytes, int offset, int length) throws NotSerializableException {
    ConvertTools convert = new ConvertTools(false);
    int contentsCount = 0;
    int partSize = 0;
    int currentOffset = offset;
    Principal principal = null;
    Subject res;

    try {
      partSize = convert.arrToInt(bytes, currentOffset);

      // if the stored subject is anonymous then do not bother with deserialization
      if (partSize == 0) {
        return new SubjectWrapper();
      }

      res = new Subject();

      // deserialize the major principal
      currentOffset += 4;
      try {
        principal = (Principal) Util.array2object(bytes, currentOffset, partSize);
      } catch (NotSerializableException nse) {
        if (SEC_SRV_LOCATION.beWarning()) {
          SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "com.sap.engine.services.security.Util was unable to deserialize the main principal of the persisted subject.", nse);
        }
      }
      currentOffset += partSize;

      int subjectContainsMainPrincipal = convert.arrToInt(bytes, currentOffset);
      currentOffset += 4;
      
      if (subjectContainsMainPrincipal == MAIN_PRINCIPAL_IN_SUBJECT) {
        res.getPrincipals().add(principal);
      }

      // deserialize all the principals
      contentsCount = convert.arrToInt(bytes, currentOffset);
      currentOffset += 4;
      for (int i = 0; i < contentsCount; i++) {
        partSize = convert.arrToInt(bytes, currentOffset);
        currentOffset += 4;
        try {
          res.getPrincipals().add((Principal) Util.array2object(bytes, currentOffset, partSize));
        } catch (NotSerializableException nse) {
          if (SEC_SRV_LOCATION.beWarning()) {
            SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "com.sap.engine.services.security.Util was unable to deserialize a principal of the persisted subject.", nse);
          }
        }
        currentOffset += partSize;
      }
      
      // deserialize the public credentials
      contentsCount = convert.arrToInt(bytes, currentOffset);
      currentOffset += 4;
      for (int i = 0; i < contentsCount; i++) {
        partSize = convert.arrToInt(bytes, currentOffset);
        currentOffset += 4;
        try {
          res.getPublicCredentials().add(Util.array2object(bytes, currentOffset, partSize));
        } catch (NotSerializableException nse) {
          if (SEC_SRV_LOCATION.beWarning()) {
            SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "com.sap.engine.services.security.Util was unable to deserialize a public credential of the persisted subject.", nse);
          }
        }
        currentOffset += partSize;
      }
      
      // deserialize and decrypt the private credentials
      contentsCount = convert.arrToInt(bytes, currentOffset);
      currentOffset += 4;
      
      for (int i = 0; i < contentsCount; i++) {
        partSize = convert.arrToInt(bytes, currentOffset);
        currentOffset += 4;
        
        byte[] credential = null;
        
        try {
          synchronized (cipher) {
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            credential = cipher.doFinal(bytes, currentOffset, partSize);
          }
          
          res.getPrivateCredentials().add(Util.array2object(credential, 0, credential.length));
        } catch (Exception e) {
          SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "com.sap.engine.services.security.Util was unable to deserialize a private credential of the persisted subject.", e);
        }
        
        currentOffset += partSize;
      }

      return new SubjectWrapper(res, principal);
    } catch (Exception ioe) {
      NotSerializableException nse = new NotSerializableException("Deserialization error.");
      nse.initCause(ioe);
      throw nse;
    }
  }

  /**
   *  Retrieves the name of the given class loader from its descriptor.
   *
   * @param classloader  the class loader instance to retrieve the name from
   *
   * @return the name of the classloader as obtained from its descripor
   *         or empty string if the name is not found.
   */
  public static String getClassLoaderName(ClassLoader classloader) {
    return getLoadContext().getName(classloader);
  }

   /**
    * Serializes an instance
    *
    * @param serializable
    *          the instance to be serialized
    *
    * @return the serialized data
    *
    * @throws NotSerializableException
    *           thrown if object cannot be serialized.
    */
  public static byte[] object2array(Serializable serializable) throws NotSerializableException {
    ByteArrayOutputStream bytearrayoutputstream = null;
    OutputSerializator objectoutputstream = null;
    ClassLoader loader = null;
    String loaderName = null;

    try {
      loader = serializable.getClass().getClassLoader();
      loaderName = getClassLoaderName(loader);

      bytearrayoutputstream = new ByteArrayOutputStream();
      objectoutputstream = new OutputSerializator(loaderName, bytearrayoutputstream);
      objectoutputstream.writeObject(loaderName);
      objectoutputstream.writeObject(serializable);
      objectoutputstream.flush();

      return bytearrayoutputstream.toByteArray();
    } catch (Exception exception) {
      NotSerializableException nse = new NotSerializableException("Serialization error.");
      nse.initCause(exception);
      throw nse;
    } finally {
      try {
        if (objectoutputstream != null) {
          objectoutputstream.close();
        }
        if (bytearrayoutputstream != null) {
          bytearrayoutputstream.close();
        }
      } catch (Exception e) {
        SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "object2array", e);
      }
    }
  }

  /**
   *
   * @param className  the name of the class to be loaded
   * @param serviceProperty the name of the security service property which value is the list of class loaders
   *
   * @return  the loaded class
   *
   * @throws ClassNotFoundException  thrown if the class cannot be loaded
   */
  public static Class loadClassFromAdditionalLoaders(String className, String serviceProperty) throws ClassNotFoundException {
    String propertyValue = SecurityServerFrame.getServiceProperties().getProperty(serviceProperty, "");
    StringTokenizer tokens = new StringTokenizer(propertyValue, ",");
    Class result = loadClassFromSAMLService(className);

    if ((result == null) && (tokens.countTokens() <= 0)) {
      SEC_SRV_LOCATION.infoT("Cannot load login module class {0}.", new Object[] {className});
    }

    while ((result == null) && tokens.hasMoreTokens()) {
      try {
        String loader = tokens.nextToken().trim();
        result = loadClass(className, loader);        
      } catch (ClassNotFoundException cnfe) {
        if (!tokens.hasMoreTokens()) {
          throw cnfe;
        }
      }
    }
    
    return result;
  }

  // A temporary solution before all SAP JAAS login modules are separated into a library of their own
  private final static Class loadClassFromSAMLService(String className) {
    try {
      return loadClass(className, "service:tc~sec~saml~service");
    } catch (ClassNotFoundException cnfe) {
      if (SEC_SRV_LOCATION.beDebug()) {
        // the message does not contain the name of the class because the exception specifies it.
        SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Cannot load login module class from SAML service.", cnfe);
      }
      return null;
    }
  }

  /**
   *  Loads the class specified by the given class name using the class loader with
   * the given name as registered with the class load context given to the security
   * service.
   *  If the loader name is combined from several JAR files the class will be searched
   * in each component until found.
   *
   * @param className  the name of the class to be loaded
   * @param loaderName the name of the class loader to load with
   *
   * @return  the loaded class
   *
   * @throws ClassNotFoundException  thrown if the class cannot be loaded
   */
  public static Class loadClass(String className, String loaderName) throws ClassNotFoundException {
    if (loaderName == null || (loaderName.length() == 0) || !inServer) {
      return Util.class.getClassLoader().loadClass(className);
    }

    ClassLoader loader = null;
    LoadContext loadContext = getLoadContext();
    loader = loadContext.getClassLoader(loaderName);

    if (loader != null) {
      return loader.loadClass(className);
    }

    if (loaderName.startsWith(COMPONENT_COMMON)) {
      StringTokenizer loaderParser = new StringTokenizer(loaderName.substring(COMPONENT_COMMON.length()), ";");
      Class classInstance = null;

      while (loaderParser.hasMoreTokens() && classInstance == null) {
        try {
          classInstance = loadContext.getClassLoader(loaderParser.nextToken()).loadClass(className);

          if (classInstance != null) {
            return classInstance;
          }
        } catch (ClassNotFoundException cnfe) {
          if (SEC_SRV_LOCATION.beDebug()) {
            SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Ignored exception when loading class '{0}'.", new Object[]{className}, cnfe);
          }
          continue;
        }
      }
    }

    return Util.class.getClassLoader().loadClass(className);
  }

  /**
   *  Serializes a Subject instance. Only its serializable components are included.
   *
   * @param subjectWrapper  the subject instance to be serialized.
   *
   * @return  the serialized array
   *
   * @throws NotSerializableException  thrown if object cannot be serialized.
   */
  public static byte[] subject2array(SubjectWrapper subjectWrapper) throws NotSerializableException {
    Subject subject = subjectWrapper.getSubject();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ConvertTools convert = new ConvertTools(false);
    Object[] contents = null;
    byte[] data = null;

    // if the subject is anonymous then do not bother with serialization
    if (subjectWrapper.isAnonymous()) {
      return convert.intToArr(0);
    }

    try {
      // major principal should be serializable
      Principal principal = subjectWrapper.getPrincipal();
      if (principal instanceof Serializable) {
        try {
          data = Util.object2array((Serializable) principal);
        } catch (NotSerializableException e) {
          if (SEC_SRV_LOCATION.beWarning()) {
            SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Ignored exception when serializing the main principal of the subject.", e);
          }

          data = Util.object2array(new com.sap.engine.lib.security.Principal(principal.getName()));
        }
      } else {
        data = Util.object2array(new com.sap.engine.lib.security.Principal(principal.getName()));
      }
      baos.write(convert.intToArr(data.length));
      baos.write(data);
      
      Set<Principal> principalsToSerialize;
      
      if (subject.getPrincipals().contains(principal)) {
        principalsToSerialize = getPrincipalsWithoutMain(subject.getPrincipals(), principal);
        baos.write(convert.intToArr(MAIN_PRINCIPAL_IN_SUBJECT));
      } else {
        principalsToSerialize = subject.getPrincipals();
        baos.write(convert.intToArr(MAIN_PRINCIPAL_NOT_IN_SUBJECT));
      }
      
      // serialize the principals
      serializeSubjectElements(baos, principalsToSerialize, false, convert);
      
      // serialize the public credentials
      serializeSubjectElements(baos, subject.getPublicCredentials(), false, convert);
      
      // encrypt and serialize the private credentials
      serializeSubjectElements(baos, subject.getPrivateCredentials(), true, convert);
    } catch (IOException ioe) {
      NotSerializableException nse = new NotSerializableException("Serialization error.");
      nse.initCause(ioe);
      throw nse;
    }

    return baos.toByteArray();
  }
  
  private static Set<Principal> getPrincipalsWithoutMain(Set<Principal> principals, Principal mainPrincipal) {
    Set<Principal> result = new HashSet<Principal>();
    
    for (Principal principal: principals) {
      if (principal != mainPrincipal) {
        result.add(principal);
      }
    }
    
    return result;
  }
  
  private static void serializeSubjectElements(ByteArrayOutputStream baos, Set<? extends Object> elements, boolean encrypt, ConvertTools convert) throws IOException {
    int serializedNum = 0;
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    for (Object object: elements) {
      if (!(object instanceof Serializable)) {
        continue;
      }
      
      try {
        byte[] data = Util.object2array((Serializable) object);
        
        if (encrypt) {
          synchronized (cipher) {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            data =  cipher.doFinal(data);
          }
        }
        
        stream.write(convert.intToArr(data.length));
        stream.write(data);
        serializedNum++;
      } catch (Exception e) {
        if (SEC_SRV_LOCATION.beWarning()) {
          String elementType = "";
          
          if (object instanceof Principal) {
            elementType = "principal";
          } else if (encrypt) {
            elementType = "private credential";
          } else {
            elementType = "public credential";
          }
          
          SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Ignored exception when serializing a " + elementType + " of the subject.", e);
        }
      }
    }
    
    baos.write(convert.intToArr(serializedNum));
    baos.write(stream.toByteArray());
  }
  
  public static synchronized void initializeCipher() throws Exception {
    ModificationContextImpl modificationContext = null;
    byte[] keyBytes = null;
    boolean isKeyStored = false;
    
    try {   
      modificationContext = (ModificationContextImpl) SecurityServerFrame.getSecurityContext().getModificationContext();
      modificationContext.beginModifications();
      Configuration config = modificationContext.getConfiguration(SecurityConfigurationPath.SECURITY_PATH, false, false);

      if (config != null) {
        if (config.existsConfigEntry(ENCRYPTION_KEY)) {
          keyBytes = (byte[]) config.getConfigEntry(ENCRYPTION_KEY);
          
          if (SEC_SRV_LOCATION.beDebug()) {
            SEC_SRV_LOCATION.debugT("Encryption key taken from database.");
          }
          
          DESKeySpec spec = new DESKeySpec(keyBytes);
          SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
          key = factory.generateSecret(spec);
        } else {
          KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
          key = keyGenerator.generateKey();
          keyBytes = key.getEncoded();
          
          config = modificationContext.getConfiguration(SecurityConfigurationPath.SECURITY_PATH, true, false);
          
          if (config != null) {
            config.addConfigEntry(ENCRYPTION_KEY, keyBytes, Configuration.ENTRY_TYPE_SECURE);//TODO ???
            isKeyStored = true;
            
            if (SEC_SRV_LOCATION.beDebug()) {
              SEC_SRV_LOCATION.debugT("Encryption key generated.");
            }
          } else {
            SimpleLogger.trace(Severity.ERROR, SEC_SRV_LOCATION, "ASJ.secsrv.000147", "Could not initialize and store cipher due to missing root security configuration.");
            throw new SecurityException("Initialization of cipher failed because the security configuration cannot be opened for write access. Most probably it does not exist.");
          }
        }

        Util.ivParameterSpec = new IvParameterSpec(IV);
        Util.cipher = Cipher.getInstance(CIPHER);
      } else {
        SimpleLogger.trace(Severity.ERROR, SEC_SRV_LOCATION, "ASJ.secsrv.000148", "Could not initialize cipher due to missing root security configuration.");
        throw new SecurityException("Cannot initialize cipher because the security configuration cannot be opened for read access. Most probably it does not exist.");
      }
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, SEC_SRV_LOCATION, "ASJ.secsrv.000149", "Exception occured during initialization of the cipher: ", e);
      throw e;
    } finally {
      if (isKeyStored) {
        modificationContext.commitModifications();
      } else {
        modificationContext.forgetModifications();
      }
    }
  }

  private static LoadContext getLoadContext() {
    if (loadContext == null) {
      loadContext = SecurityServerFrame.getServiceContext().getCoreContext().getLoadContext();
    }
    return loadContext;
  }
  
	public static String encode(String in) {
		if (in == null || in.trim().equals("")) {
		  return null;
		}

		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
		  int illegalCharPos = -1;
		  for (int j = 0; j < FORBIDDEN_CONFIGNAME_CHARS.length; j++) {
			  if (in.charAt(i) == FORBIDDEN_CONFIGNAME_CHARS[j]) {
			    illegalCharPos = j;
			    break;
			  }
		  }
		  if (illegalCharPos == -1) {
			  if (in.charAt(i) == '$') {
			    temp.append(in.charAt(i));
			  }
			  temp.append(in.charAt(i));
		  } else {
			  temp.append("$" + illegalCharPos);
		  }
		}

		String result = temp.toString();
		return result;
	}

	public static String decode(String in) throws SecurityException {
		if (in == null) {
		  return null;
		}
		StringBuffer temp = new StringBuffer();
		int i = 0;
		char next = 0;
		while (i < in.length()) {
		  next = 0;
		  try {
			  next = in.charAt(i + 1);
		  } catch (IndexOutOfBoundsException e) {
        if (SEC_SRV_LOCATION.beDebug()) {
          SEC_SRV_LOCATION.traceThrowableT(Severity.DEBUG, "Ignored exception when decoding stream.", e);
        }

        next = 0;
		  }
		  if (in.charAt(i) != '$' || next == 0) {
			  temp.append(in.charAt(i));
			  i++;
		  } else {
			  if (next == '$') {
			    temp.append(in.charAt(i));
			  } else {
			    int pos = -1;
			    try {
				    pos = Character.getNumericValue(next);
				    temp.append(FORBIDDEN_CONFIGNAME_CHARS[pos]);
			    } catch (Exception e) {
				    throw new SecurityException("Decoding of [" + in + "] fails - unexpected character after '$' - [" + next + "].");
			    }
			  }
			  i += 2;
		  }
		}
		return temp.toString();
	}  

	public static byte[] encrypt(String data2encrypt){
	  byte[] encryptedData = null;
	  
	  if (data2encrypt != null) {
	    
	    try {
    	  synchronized (cipher) {
          cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
          encryptedData = cipher.doFinal(data2encrypt.getBytes());
        }
  	  } catch (Exception e) {
        SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Ignored exception when serializing a private credential of the subject.", e);
      }
	  }
	  
	  return encryptedData;
	}
	
	public static byte[] decrypt(byte[] data2decrypt) {
	  byte[] decryptedData = null;
    
    if (data2decrypt != null) {
      
      try {
        synchronized (cipher) {
          cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
          decryptedData = cipher.doFinal(data2decrypt);
        }
      } catch (Exception e) {
        SEC_SRV_LOCATION.traceThrowableT(Severity.WARNING, "Ignored exception when serializing a private credential of the subject.", e);
      }
      
    }
    
    return decryptedData;
	}
}

class OutputSerializator extends ObjectOutputStream {

  protected ByteArrayOutputStream stream;
  protected String loaderName;

  public OutputSerializator(String loaderName, ByteArrayOutputStream stream) throws IOException {
    super(stream);

    this.stream = stream;
    this.loaderName = loaderName;
    enableReplaceObject(false);
  }

  public void writeStreamHeader() throws IOException {
  }

  public void close() throws IOException {
    super.close();
    stream.close();
  }

}

class InputSerializator extends ObjectInputStream {

  protected ByteArrayInputStream stream;
  protected String loaderName;

  public InputSerializator(String loaderName, ByteArrayInputStream stream) throws IOException {
    super(stream);

    this.stream = stream;
    this.loaderName = loaderName;
  }

  public void readStreamHeader() throws IOException {
  }

  protected Class resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
    if ("java.lang.String".equals(osc.getName())) {
      return String.class;
    } else {
      return Util.loadClass(osc.getName(), loaderName);
    }
  }

  public void close() throws IOException {
    super.close();
    stream.close();
  }
  
}

