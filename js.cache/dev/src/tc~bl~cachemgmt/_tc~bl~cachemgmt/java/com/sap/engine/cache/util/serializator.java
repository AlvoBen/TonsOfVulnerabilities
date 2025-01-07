package com.sap.engine.cache.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;

/**
 * @author Petev, Petio, i024139
 */
public class Serializator {

  public static Object toObject(byte[] src) throws IOException, StreamCorruptedException, ClassNotFoundException {
    ByteArrayInputStream baIStream = new ByteArrayInputStream(src);
    ObjectInputStream oIStream = null;
    try {
      oIStream = new SubstituteObjectInputStream(baIStream);
      return oIStream.readObject();
    } finally {
      baIStream.close();
      if (oIStream != null) {
        oIStream.close();
      }
    }
  }

  /**
   * Serializes an object from an InputStream
   * 
   * @param stream
   * @return an deserialized object from the input stream passed as a parameter
   * @throws IOException
   * @throws StreamCorruptedException
   * @throws ClassNotFoundException
   */
  public static Object toObject(InputStream stream)  throws IOException, StreamCorruptedException, ClassNotFoundException {
    ObjectInputStream oIStream = null;
    try {
      oIStream = new SubstituteObjectInputStream(stream);
      return oIStream.readObject();
    } finally {
      if (oIStream != null) {
        oIStream.close();
      }
    }
  }

  public static byte[] toByteArray(Object src) throws IOException {
    ByteArrayOutputStream baOStream = new ByteArrayOutputStream();
    ObjectOutputStream oOStream = null;
    try {
      oOStream = new ObjectOutputStream(baOStream);
      oOStream.writeObject(src);
      return baOStream.toByteArray();
    } finally {
      baOStream.close();
      if (oOStream != null) {
        oOStream.close();
      }
    }
  }

}
