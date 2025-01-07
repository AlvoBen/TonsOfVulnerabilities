package com.sap.httpclient.http.cache;

import com.sap.httpclient.http.StatusLine;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HeaderElement;
import com.sap.httpclient.utils.DateParser;
import com.sap.httpclient.exception.DateParseException;
import com.sap.tc.logging.Location;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author: Mladen Droshev
 */

public class CacheObject implements Serializable { //$JL-EQUALS$

   /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(CacheObject.class);
  protected static final byte[] ZERO_BYTE_ARRAY = new byte[0];
  protected ArrayList<Header> headers;
  protected ItemID id;
  protected byte[] value;
  protected int searchCounter = 1;
  protected StatusLine statusLine;
  protected long creationTime = -1;
  protected long max_age = -1;
  protected long expiration_time = -1;

  public CacheObject() {
    this(null, null);
  }

  public CacheObject(ItemID id, byte[] value) {
    if (id == null) {
      this.id = ItemID.NULL_ITEM_ID;
    } else {
      this.id = id;
    }
    if (value == null) {
      this.value = ZERO_BYTE_ARRAY;
    } else {
      this.value = value;
    }
    this.creationTime = System.currentTimeMillis();
  }

  public ItemID getID() {
    return id;
  }

  public int size() {
    return value.length;
  }

  public String toString() {
    return new StringBuffer()
            .append("CacheObject id:")
            .append(id)
            .append(" searched:")
            .append(searchCounter)
            .append(" times. ")
            .append(" - size: ")
            .append(size())
            .toString();
  }

  public byte[] getValue() {
    return value;
  }

  public int hashCode() {
    return id.hashCode();
  }

  public void setHeaders(ArrayList<Header> headers) {
    this.headers = new ArrayList<Header>(headers);

    if(this.headers.size() > 0) {

      for (Header header : headers) {
        if (header.getName().equalsIgnoreCase(Header.__CACHE_CONTROL) && header.getValue().startsWith(Header.MAX_AGE)) {
          this.max_age = 1000 * Integer.parseInt((HeaderElement.parseElements(header.getValue()))[0].getValue()); // in miliseconds
        }

        if(header.getName().equalsIgnoreCase(Header._EXPIRES)){
          try {
            expiration_time = (DateParser.parse(header.getValue()).getTime());
          } catch (DateParseException e) {
            LOG.errorT(Header._EXPIRES + " header value is illegal : " + header.getValue());
          }
        }

        if(header.getName().equalsIgnoreCase(Header.AGE)){
          this.max_age = 1000 * Integer.parseInt((HeaderElement.parseElements(header.getValue()))[0].getValue()); // in miliseconds
        }
      }
    }
  }

  public ArrayList<Header> getHeaderList() {
    return headers;
  }

  public void increaseCounter() {
    this.searchCounter++;
  }

  public void setStatusLine(StatusLine sLine) {
    this.statusLine = sLine;
  }

  public int getCounter(){
    return this.searchCounter;
  }

  public StatusLine getStatusLine() {
    return this.statusLine;
  }

  public long getCreationTime(){
    return this.creationTime;
  }

  public long getMaxAge(){
    return this.max_age;
  }

  public long getExpirationTime(){
    return this.expiration_time;
  }
}
