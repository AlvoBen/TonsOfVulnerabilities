package com.sap.engine.lib.xml.signature;


public abstract class Constants {
    
  public static final String PROVIDER_SUN = "SUN";
  public static final String PROVIDER_IAIK = "IAIK";
  public static String STANDARD_PREFIX = System.getProperty("xml.dsig.prefix","ds") + ":";
  public static final String SIGNATURE_SPEC_NS   = "http://www.w3.org/2000/09/xmldsig#";
  public static final String MORE_ALGORITHMS_SPEC_NS   = "http://www.w3.org/2001/04/xmldsig-more#";
    
  public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
  
  //public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";

  
  // These are the trransformation algorithms' URIs    
  public static final String TR_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";    
  public static final String TR_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";    
  public static final String TR_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  public static final String TR_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  public static final String TR_C14N_EXCL = SIGNATURE_SPEC_NS + "excludeC14N";
  public static final String TR_C14N_EXCL_WITHCOMMENTS = SIGNATURE_SPEC_NS + "excludeC14NwithComments";
  public static final String TR_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
  public static final String TR_BASE64_DECODE = SIGNATURE_SPEC_NS + "base64";
  public static final String TR_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
  public static final String TR_ENVELOPED_SIGNATURE = SIGNATURE_SPEC_NS + "enveloped-signature";
  public static final String TR_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
  public static final String TR_XPATH_FILTER2 = "http://www.w3.org/2002/06/xmldsig-filter2";
  
  public static final String DECRYPT_SPEC_NS = "http://www.w3.org/2002/07/decrypt#";
  // obsolete!!!
  public static final String TR_DECRYPT_OLD = "http://www.w3.org/2001/04/decrypt#";
  public static final String TR_DECRYPT_XML = DECRYPT_SPEC_NS + "XML";
  public static final String TR_DECRYPT_BINARY = DECRYPT_SPEC_NS + "Binary";
    
  // signatures
  public static final String SIGN_DSA = Constants.SIGNATURE_SPEC_NS + "dsa-sha1";
  public static final String SIGN_RSA = Constants.SIGNATURE_SPEC_NS + "rsa-sha1";
  
  // Filter2 types
  public static final int FILTER2_INVALID = 0;   
  public static final int FILTER2_SUBTRACT = 1;   
  public static final int FILTER2_INTERSECT = 2;   
  public static final int FILTER2_UNION = 3;   

  
  // Verification status codes
  public static final int VERIFIER_INITIAL_STATE = -1;
  public static final int VERIFY_OK = 0;
  public static final int NO_KEY_INFO = 1;
  public static final int INVALID_SIGNATURE_VALUE = 2;
  public static final int CONTAINS_INVALID_REFERENCE = 3;
  public static final int UNKNOWN_SIGNATURE_ALGORITHM = 4;
  public static final int UNKNOWN_CANONICALIZATION_ALGORITHM = 5;
    

  // Reference validation status codes
  public static final int REFERENCE_INITIAL_STATE = -1;
  public static final int REFERENCE_VERIFY_OK = 0;
  public static final int REFERENCE_UNREACHABLE = 1;
  public static final int INVALID_DIGEST_VALUE = 2;
  public static final int UNKNOWN_TRANSFORMATION_ALGORITHM = 3;
  public static final int UNKNOWN_DIGEST_ALGORITHM = 4;

  
  
  // Message Digest
  
  //NOT RECOMMENDED MD5
  public static final String DIGEST_MD5 = Constants.MORE_ALGORITHMS_SPEC_NS + "md5";
  // Required SHA1
  public static final String DIGEST_SHA1 = Constants.SIGNATURE_SPEC_NS + "sha1";

  public static final String REFERENCE_TYPE_SIG_PROPERTIES = SIGNATURE_SPEC_NS +  "SignatureProperties";
  public static final String ENCRYPTION_SPEC_NS = "http://www.w3.org/2001/04/xmlenc#";
  public static final String STANDARD_ENC_PREFIX = "xenc:";
  public static final String ELEMENT_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#Element";
  public static final String CONTENT_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#Content";
  public static final String KEY_ENCRYPTION = "http://www.w3.org/2001/04/xmlenc#EncryptedKey";
  public static final String ENCRYPTION_PROPERTIES_REFERENCE = "http://www.w3.org/2001/04/xmlenc#EncryptionProperties";
  // ****  BLOCK ENCRYPTION  ****
  // REQUIRED TRIPLEDES
  public static final String ALG_ENC_TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
  //REQUIRED AES-128
  public static final String ALG_ENC_AES128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
  //REQUIRED AES-256
  public static final String ALG_ENC_AES256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
  //OPTIONAL AES-192
  public static final String ALG_ENC_AES192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
  // ****  MESSAGE DIGEST  ****
  // Note: SHA1 is inherited from signature constants
  //RECOMMENDED SHA256
  public static final String DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  //OPTIONAL SHA512
  public static final String DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  //OPTIONAL RIPEMD-160
  public static final String DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  // ****  KEY ENCRYPTION  ****
  //REQUIRED  RSA1.5
  public static final String KEY_ENC_RSA_1_5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
  //REQUIRED RSAO-AEP
  public static final String KEY_ENC_RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
  

  public static synchronized void setSTANDARD_PREFIX(String standard_prefix) {
    STANDARD_PREFIX = standard_prefix;
  }
}