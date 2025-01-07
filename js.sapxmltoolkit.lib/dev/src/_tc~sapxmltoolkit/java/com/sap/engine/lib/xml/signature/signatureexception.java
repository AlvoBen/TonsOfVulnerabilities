package com.sap.engine.lib.xml.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.sap.engine.lib.jaxp.TransformerFactoryImpl;
import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class SignatureException extends com.sap.engine.lib.xsl.xslt.NestedException {
  public static boolean dumpFlag = Boolean.getBoolean("com.sap.xml.security.trace");
  public static boolean dumpPrivate = Boolean.getBoolean("com.sap.xml.security.trace.private");
  
  public static String PREFIX = "XML_SECURITY>> ";
  
  public static void traceMessage(String message){
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().println(message);
    }    
  }
  
  public static void traceByte(String message, byte[] bytes) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().print(message);
      LogWriter.getSystemLogWriter().print(": ");
      LogWriter.getSystemLogWriter().println(new String(BASE64Encoder.encode(bytes))); //$JL-I18N$
    }
  }
  
  public static void traceKey(String message, Key key){
    //$JL-SYS_OUT_ERR$
    if (dumpFlag){
      if ((key instanceof PrivateKey) && (!dumpPrivate)) {
        return;
      }
      try {
        traceByte(message, key.getEncoded());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void traceCertificate(String message, Certificate cert) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag){
      try {
        traceByte(message, cert.getEncoded());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }   
  }
  

  public static void traceCertificateArray(String message, Certificate[] certs) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag){
      try {
        for(int i=0;i<certs.length;i++){
          traceCertificate(message + i, certs[i]);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }   
  }
  
  public static void traceByteAsString(String message, byte[] bytes) {
    if (dumpFlag) {
      traceByte(message, new String(bytes)); //$JL-I18N$
    }
  }
  
  public static void traceNode(String message, Node domRepresentation) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      try {
        Transformer tr = (new TransformerFactoryImpl()).newTransformer();//TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tr.setOutputProperty(OutputKeys.INDENT, "no");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        Source source = new DOMSource(domRepresentation);
        tr.transform(source, result);
        String strRepr = new String(bos.toByteArray()); //$JL-I18N$
        traceByte(message, strRepr);
      } catch (Exception ex){
        ex.printStackTrace();
      }
    }
  }  

  public static void traceByte(String message, String bytes) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().print(message);
      LogWriter.getSystemLogWriter().print(": ");
      LogWriter.getSystemLogWriter().println(new String(BASE64Encoder.encode(bytes.getBytes()))); //$JL-I18N$
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().print(message);
      LogWriter.getSystemLogWriter().print(": ");
      LogWriter.getSystemLogWriter().println(bytes);
    }
  }

  protected static void throwing(String message, Throwable cause) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().println(message);
      LogWriter.getSystemLogWriter().print(PREFIX);
      cause.printStackTrace();
    }
  }

  protected static void errorT(String message) {
    //$JL-SYS_OUT_ERR$
    if (dumpFlag) {
      LogWriter.getSystemLogWriter().print(PREFIX);
      LogWriter.getSystemLogWriter().print(Thread.currentThread().getName());
      LogWriter.getSystemLogWriter().print(" ");
      LogWriter.getSystemLogWriter().println(message);
    }
  }

  public SignatureException() {
    dump(null, null, null, this);
  }

  public SignatureException(Throwable cause) {
    super(cause);
    dump(null, cause, null, this);
  }

  public SignatureException(String message) {
    super(message);
    dump(message, null, null, this);
  }

  public SignatureException(String message, Object[] additionalData) {
    super(message);
    dump(message, null, additionalData, this);
  }

  public SignatureException(Throwable cause, String message) {
    super(cause, message);
    dump(message, cause, null, this);
  }

  public SignatureException(String message, Throwable cause) {
    super(message, cause);
    dump(message, cause, null, this);
  }

  public SignatureException(String message, Object[] additionalData, Throwable cause) {
    super(message, cause);
    dump(message, cause, additionalData, this);
  }

  protected static StringBuffer additional = new StringBuffer();
  protected static StringBuffer dumpBuffer = new StringBuffer();

  protected synchronized static void dump(String message, Throwable cause, Object[] additionalData, SignatureException parent) {
    if (dumpFlag) {
      if (cause == null) {
        cause = parent;
      }
      if (message == null) {
        message = cause.getMessage();
      }
      throwing(message, cause);//loc.throwing(message, cause);
      if (additionalData != null) {
        for (int i = 0; i < additionalData.length; i++) {
          dumpBuffer.setLength(0);
          dump(additionalData[i]);
          additional.append("Argument(").append(i).append(")=").append(dumpBuffer).append('\n');
        }
      }
      if (additional.length() > 0) {
        errorT(additional.toString());//loc.errorT(additional.toString());
      }
      additional.setLength(0);
    }
  }
  
  protected static void dump(Object o) {
    if (o == null) {
      dumpBuffer.append("null");
      return;
    }
    dumpBuffer.append(o.getClass()).append(":");
    if (o instanceof String) {
      dumpBuffer.append('"').append((String) o).append('"');
    } else if (o instanceof byte[]) {
      dumpBuffer.append("byte[]");
      dumpByte((byte[]) o);
    } else if (o instanceof ByteArrayInputStream) {
      ByteArrayInputStream bis = (ByteArrayInputStream) o;
      byte[] ret = null;
      synchronized (bis) {
        int available = bis.available();
        bis.reset();
        int available2 = bis.available();
        ret = new byte[available];
        try {
          bis.read(ret);
        } catch (IOException e) {
          e.printStackTrace();
        }
        bis.reset();
        available2 -= available;
        bis.skip(available2);
        dumpBuffer.append("stream[@").append(available2).append(']');
        //dumps the contents of this byte array output stream, and positions
        // it
      }
      dumpByte(ret);
    } else if (o instanceof Node) {
      Node n = (Node) o;
      transform(n);
    } else if (o instanceof Key) {
      Key k = (Key) o;
      dumpBuffer.append("key[]");
      dumpByte(k.getEncoded());
    } else if (o instanceof Object[]) {
      dumpBuffer.append("{ ");
      Object[] ar = (Object[]) o;
      if ((ar != null) && (ar.length > 0)) {
        for (int i = 0; i < ar.length - 1; i++) {
          dump(ar[i]);
          dumpBuffer.append(", ");
        }
        for (int i = ar.length - 1; i < ar.length; i++) {
          dump(ar[i]);
        }
      }
      dumpBuffer.append("}");
    } else if (o instanceof GenericElement) {
      Node n = ((GenericElement) o).getDomRepresentation();
      transform(n);
    } else if (o instanceof Certificate) {
      Certificate k = (Certificate) o;
      dumpBuffer.append("certificate[]");

      try {
        dumpByte(k.getEncoded());
      } catch (CertificateEncodingException e) {
        // TODO Auto-generated catch block
        //e.printStackTrace();
        dumpBuffer.append("Unable to decode: ").append(e.getMessage());
      }
    } else if (o instanceof Transformation) {
      Transformation tr = (Transformation) o;
      dumpBuffer.append("URI=");
      dump(tr.uri);
      dumpBuffer.append(", additionalArgs=");
      dump(tr.additionalArgs);
      dumpBuffer.append(", hashmap=");
      dump(tr.dataHashmap);
    } else {
      dumpBuffer.append(o.toString());
    }

  }

  protected static void dumpByte(byte[] ar) {
    dumpBuffer.append("{ ");
    if ((ar != null) && (ar.length > 0)) {
      for (int i = 0; i < ar.length - 1; i++) {
        dumpBuffer.append(ar[i]).append(", ");
      }
      if (ar.length > 0) {
        for (int i = ar.length - 1; i < ar.length; i++) {
          dumpBuffer.append(ar[i]);
        }
      }
    }
    dumpBuffer.append("}");
  }

  protected static void transform(Node n) {
    try {
      Transformer tr = (new TransformerFactoryImpl()).newTransformer();//TransformerFactory.newInstance().newTransformer();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(bos);
      Source source = new DOMSource(n);
      tr.transform(source, result);
      dumpBuffer.append(new String(bos.toByteArray())); //$JL-I18N$
    } catch (Throwable ex) {
//    $JL-EXC$
    }
  }
}