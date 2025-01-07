package com.sap.httpclient.http.cache.persistent;

import com.sap.httpclient.http.cache.ItemID;
import com.sap.httpclient.http.cache.CacheObject;
import com.sap.httpclient.http.cache.CacheManager;
import com.sap.httpclient.utils.UtilConvert;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.StatusLine;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * @author: Mladen Droshev
 */

public class PersistentCacheObject extends CacheObject {

  protected int size;
  protected String dir;
  CacheObject co = null;

  public PersistentCacheObject(CacheObject co, long expiration_time, String dir) throws java.io.IOException {
    this.dir = dir;
    this.co = co;
    this.expiration_time = expiration_time;
    this.statusLine = co.getStatusLine();
    id = (id == null ? ItemID.NULL_ITEM_ID : id);
    this.id = co.getID();
    value = (value == null ? ZERO_BYTE_ARRAY : value);
    size = value.length;
    store(value);
  }

  public PersistentCacheObject(ItemID id, String dir){
    this.id = id;
    this.dir = dir;
    load();
  }

  static String getFilenameFromID(ItemID id) {
    return id.toString();
  }

  String getFilename() {
    return getFilenameFromID(id);
  }

  protected void store(byte[] value) throws java.io.IOException {
    BufferedOutputStream out = new java.io.BufferedOutputStream(new FileOutputStream(new File(dir, getFilenameFromID(id))));

    /* expiration Time */
    writeLongToStream(expiration_time, out);

    /* write Headers */
    List<Header> headers = null;
    if (co != null) {
      headers = co.getHeaderList();
    } else {
      System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
    }
    int header_count = headers.size();
    writeIntToStream(header_count, out);
    if (header_count > 0) {
      for (Header aHd : headers.toArray(new Header[0])) {
        writeHeader(aHd, out);
      }
    }

    /* write status Line */
    writeIntToStream(statusLine.toString().length(), out);
    out.write(statusLine.toString().getBytes());

    /* write value */
    writeIntToStream(value.length, out);
    out.write(value);

    out.close();
  }

  private void writeHeader(Header header, BufferedOutputStream out) throws java.io.IOException {
    String name = header.getName();
    String value = header.getValue();

    if (name != null && value != null) {
      byte[] headerBytes = new byte[4 + name.length() + 4 + value.length()];
      /* write name length */
      UtilConvert.writeIntToByteArr(headerBytes, 0, name.length());
      /* write name */
      System.arraycopy(name.getBytes(), 0, headerBytes, 4, name.length());
      /* write value length */
      UtilConvert.writeIntToByteArr(headerBytes, 4 + name.length(), value.length());
      /* write value */
      System.arraycopy(value.getBytes(), 0, headerBytes, 4 + name.length() + 4, value.length());
      out.write(headerBytes);
    }

  }

  public byte[] getValue(){
    return this.value;
  }

  public void load() {
    try {
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(dir, getFilenameFromID(id))));

      /* read Expiration Time */
      expiration_time = readLongFromStream(in);

      /* read Headers */
      int header_count = readIntFromStream(in);
      if (header_count > 0) {
        headers = new ArrayList<Header>();
        for (int i = 0; i < header_count; i++) {
          Header h = readHeader(in);
          if (h != null) {
            headers.add(h);
          }
        }
      }

      /* read Status Line */
      byte[] b_2 = new byte[readIntFromStream(in)];
      in.read(b_2);
      String sssss = new String(b_2);
      statusLine = new StatusLine(sssss);

      /*read value*/
      value = new byte[readIntFromStream(in)];
      in.read(value);

      in.close();
      //System.out.println(" LOAD COBJ : " + id + "<> exp : " + expiration_time);
    } catch (Throwable t) {
      // TODO log.error("PersistentHistory " + toString() + "---> bad getValue ", t);
    }
  }

  private Header readHeader(BufferedInputStream in) throws java.io.IOException {

    byte[] nameBytes = new byte[readIntFromStream(in)];
    in.read(nameBytes);
    String name = new String(nameBytes);
    byte[] valueBytes = new byte[readIntFromStream(in)];
    in.read(valueBytes);
    String value = new String(valueBytes);
    if (name != null && value != null) {
      return new Header(name, value);
    }
    return null;
  }

  private int readIntFromStream(InputStream in) throws java.io.IOException {
    byte[] b = new byte[4];
    in.read(b);
    return UtilConvert.byteArrToInt(b, 0);
  }

  private long readLongFromStream(InputStream in) throws java.io.IOException {
    byte[] b = new byte[8];
    in.read(b);
    return UtilConvert.byteArrToLong(b, 0);
  }

  private void writeIntToStream(int i, OutputStream out) throws java.io.IOException {
    byte[] b = new byte[4];
    UtilConvert.writeIntToByteArr(b, 0, i);
    out.write(b);
  }


  private void writeLongToStream(long l, OutputStream out) throws java.io.IOException {
    byte[] b = new byte[8];
    UtilConvert.writeLongToByteArr(b, 0, l);
    out.write(b);
  }

  public int size() {
    return size;
  }
}
