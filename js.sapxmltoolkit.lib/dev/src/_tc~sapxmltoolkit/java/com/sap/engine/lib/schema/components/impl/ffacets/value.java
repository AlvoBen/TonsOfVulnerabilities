package com.sap.engine.lib.schema.components.impl.ffacets;

import com.sap.engine.lib.schema.Constants;
import com.sap.engine.lib.schema.util.Duration;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.math.BigDecimal;
import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-26
 * Time: 10:34:19
 * To change this template use Options | File Templates.
 */
public final class Value implements Constants {

  private ValueSpace valueSpace;
  private int length;
  private int usersCount;

  public Value() {
    valueSpace = new ValueSpace();
    length = -1;
    usersCount = 0;
  }
  
  protected void addValue(String valueSpaceId, Object value) {
    valueSpace.addValue(valueSpaceId, value);
  }

  protected void setLengthDeterminigValue(Object lengthDeterminigObj) {
    if(lengthDeterminigObj.getClass().isArray()) {
      length = Array.getLength(lengthDeterminigObj);
    } else if(lengthDeterminigObj instanceof String) {
      length = ((String)lengthDeterminigObj).length();
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  protected void setLength(int length) {
    this.length = length;
  }
  
  public int getLength() {
    if(length == -1) {
      throw new IllegalArgumentException("Length is not supported as a contraning facet.");
    }
    return(length);
  }

  public int compare(Value value) {
    int restrictionLevel = valueSpace.intersect(value.valueSpace);
    if(restrictionLevel == -1) {
      return(COMPARE_RESULT_NOT_EQUAL);
    }
    return(compare(valueSpace.getValue(restrictionLevel), value.valueSpace.getValue(restrictionLevel)));
  }

  private int compare(Object obj1, Object obj2) {
    if(obj1 instanceof Duration) {
      Duration duration1 = (Duration)obj1;
      Duration duration2 = (Duration)obj2;
      int compareResult = duration1.compare(duration2);
      if(compareResult == Duration.COMPARE_EQUAL) {
        return(COMPARE_RESULT_EQUAL);
      }
      if(compareResult == Duration.COMPARE_LESS) {
        return(COMPARE_RESULT_LESS);
      }
      if(compareResult == Duration.COMPARE_GREATER) {
        return(COMPARE_RESULT_GREATER);
      }
      return(COMPARE_RESULT_NOT_EQUAL);
    }
    if(obj1 instanceof Calendar) {
      Calendar calendar1 = (Calendar)obj1;
      Calendar calendar2 = (Calendar)obj2;
      if(calendar1.before(calendar2)) {
        return(COMPARE_RESULT_LESS);
      }
      if(calendar1.after(calendar2)) {
        return(COMPARE_RESULT_GREATER);
      }
      return(COMPARE_RESULT_EQUAL);
    }
    if(obj1 instanceof Boolean) {
      Boolean boolean1 = (Boolean)obj1;
      Boolean boolean2 = (Boolean)obj2;
      if(boolean1.equals(boolean2)) {
        return(COMPARE_RESULT_EQUAL);
      }
      if(boolean1.booleanValue()) {
        return(COMPARE_RESULT_GREATER);
      }
      return(COMPARE_RESULT_LESS);
    }
    if(obj1 instanceof String) {
      if(obj1.equals(obj2)) {
        return(COMPARE_RESULT_EQUAL);
      }
      return(COMPARE_RESULT_NOT_EQUAL);
    }
    if(obj1 instanceof BigDecimal) {
      BigDecimal bigDecimal1 = (BigDecimal)obj1;
      BigDecimal bigDecimal2 = (BigDecimal)obj2;
      int compareResult = bigDecimal1.compareTo(bigDecimal2);
      if(compareResult < 0) {
        return(COMPARE_RESULT_LESS);
      } else if(compareResult == 0) {
        return(COMPARE_RESULT_EQUAL);
      }
      return(COMPARE_RESULT_GREATER);
    }
    if(obj1 instanceof byte[]) {
      byte[] byteArray1 = (byte[])obj1;
      byte[] byteArray2 = (byte[])obj2;
      if(byteArray1.length != byteArray2.length) {
        return(COMPARE_RESULT_NOT_EQUAL);
      }
      for(int i = 0; i < byteArray1.length; i++) {
        if(byteArray1[i] != byteArray2[i]) {
          return(COMPARE_RESULT_NOT_EQUAL);
        }
      }
      return(COMPARE_RESULT_EQUAL);
    }
    return(COMPARE_RESULT_NOT_EQUAL);
  }
  
  public String getValue() {
    return(valueSpace.getValue());
  }
  
  public void use() {
    usersCount++;
  }
  
  public void reuse() {
    if(usersCount > 0) {
      usersCount--;
    } 
    if(usersCount == 0) {
      valueSpace.reuse();
      length = -1;
    }
  }
  
  public boolean isReusable() {
    return(usersCount == 0);
  }
}
