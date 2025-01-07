package com.sap.engine.services.scheduler.impl.cron;

import com.sap.scheduler.api.CronField;
import com.sap.scheduler.api.fields.*;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * @author Stefan Dimov
 * @version 1.0
 */


/**
 * This class represents a set of cron values, sorted by value
 */
class CronTimesGenerator implements Serializable {

  protected String persistable = "";
  protected CronSet valueSet = null;
  private int pointer = -1;
  protected int fldCode;
  CronField field;


  CronTimesGenerator(CronField field) {
    this.field = field;
    valueSet = new CronSet(field.getMinValue(), field.getMaxValue());
  	this.persistable = field.toString();
  	parseRecursively(field.toString());
  }

  CronTimesGenerator(CronField field, int min, int max) {
    this.field = field;
    valueSet = new CronSet(min, max);
  }

  CronTimesGenerator(int fieldCode, int singleValue) {
    this(produceCronField(fieldCode, "" + singleValue));
  }

  CronTimesGenerator(int fieldCode, int start, int end) {
    this(produceCronField(fieldCode, start + "-" + end));
  }

  CronTimesGenerator(int fieldCode, int start, int end, int step) {
    this(produceCronField(fieldCode, start + "-" + end + "/" + step));
  }

  private static CronField produceCronField(int fieldCode, String str) {
    if (fieldCode == CronField.FIELD_YEAR) {
      return new CronYearField(str);
    } else if (fieldCode == CronField.FIELD_MONTH) {
      return new CronMonthField(str);
    } else if (fieldCode == CronField.FIELD_MONTH_DAY) {
      return new CronDOMField(str);
    } else if (fieldCode == CronField.FIELD_WEEK_DAY) {
      return new CronDOWField(str);
    } else if (fieldCode == CronField.FIELD_HOUR) {
      return new CronHourField(str);
    } else if (fieldCode == CronField.FIELD_MINUTE) {
      return new CronMinuteField(str);
    }
    return null;
  }


  /**
   * Getter method
   * @return the minimal value of the set's domain
   */
  int getMinValue() {
    return field.getMinValue();
  }

  /**
   * Getter method
   * @return the maximal value of the set's domain
   */
  int getMaxValue() {
    return field.getMaxValue();
  }

  //public abstract String getStringValue(int val);

  /**
   * Fills all available values
   */

  void fillAllValues() {
    valueSet.fillAllValues();
  }
  /**
   * Returns the value, which is pointed by the pointer
   * @return the poited value; -1 if empty or the last value is already returned
   */
  int getNext() {
    if (valueSet == null) {
      return -1;
    } else if ( (valueSet.getMax() == -1) && (valueSet.getStep() != -1) ) {
      pointer++;
      int res = valueSet.getMin() + pointer*valueSet.getStep();
      if ( (valueSet.getMax() != -1) && (res > valueSet.getMax() )) {
        return -1;
      } else {
        return res;
      }
    } else if (valueSet.getMax() == -1) {
      pointer++;
      return valueSet.getMin() + pointer;
    } else {
      pointer++;
      return valueSet.getValueAt(pointer);  // -1 if no values
    }
  }

  /**
   * Checks if the value is in the set
   * @param val the value
   * @return TRUE if the value exists, FALSE if not
   */
  boolean contains(int val) {
    return valueSet.contains(val);
  }

  /**
   * Resets the set's pointer to the begining(the smaller value)
   */
  void reset() {
    pointer = -1;
  }

  /**
   * Returns the index of a value
   * @param value the value
   * @return the value index in the set; -1 if no such value
   */
  int indexOf(int value) {
    return valueSet.indexOf(value);
  }

  /**
   * Inserts a value in the set, sorted
   * @param value the value
   */
  void insert(int value) {
    valueSet.insert(value);
  }

  /**
   * Deletes a value from the set
   * @param value the value
   */
  void delete(int value) {
    valueSet.delete(value);
  }

  /**
   * Checks if the set is used
   * @return TRUE if there are values in the set, FALSE if not
   */
  boolean isSet() {
    return !(valueSet.getValues() == null);
  } 
  
  /**
   * Checks if the wild card "*" is used
   * @return TRUE if there is the wildcard used, FALSE if not
   */
  boolean isWildCard() {
    return asteriks.equals(persistable);
  } 

  /**
   * Getter method
   * @return the set values
   */
  int[] getValues() {
    return valueSet.getValues();
  }

  /*
  String toString() {
  	if (persistable.equalsIgnoreCase(".")) {
        return "not specified...";
      } else if (persistable.equalsIgnoreCase("*")) {
        return "all";
      } else if ( (valueSet.getMax() == -1) && (persistable.indexOf("/") != -1) )  {
        return persistable;
      }
      String result = "[";
      for (int i = 0; i < valueSet.getLength() - 1; i++) {
        result += getStringValue(valueSet.getValueAt(i)) + ",";
      }
      result += getStringValue(valueSet.getValueAt(valueSet.getLength() - 1)) + "]";
      return result;
  }
  */

  String persistableValue() {
  	return persistable;
  }

  static final char _asteriks = '*';
  static final String asteriks = "*";
  static final char _coma = ',';
  static final String coma = ",";
  static final char _dash = '-';
  static final String dash = "-";
  static final char _slash = '/';
  static final String slash = "/";



  protected void parseRecursively(String values_entry) {
    if (values_entry.equalsIgnoreCase(asteriks)) {
      valueSet.fillAllValues();
    } else if (values_entry.equalsIgnoreCase(".")){
      // not specified
    } else if (values_entry.indexOf(_coma) != -1) {   // ',' exists - complex set
      StringTokenizer tok = new StringTokenizer(values_entry, coma);
      while (tok.hasMoreTokens()) {
        parseRecursively(tok.nextToken());
      }
    } else if (values_entry.indexOf(_slash) != -1) {  // '/' exists
      parseModValue(values_entry);
    } else if (values_entry.indexOf(_dash) != -1) {   // '-' exists
      StringTokenizer tok = new StringTokenizer(values_entry, dash);
      int start = Integer.parseInt(tok.nextToken());
      int end = Integer.parseInt(tok.nextToken());
      for (int i = start; i < end + 1; i++) {
        insert(i);
      }
    } else {      // should be single number
      insert(Integer.parseInt(values_entry));
    }
  }

  /**
   * Parses a modulated value (x/y)
   * @param value_entry the string representation of cron value
//   * @return the cron set with inserted values
   */
  private void parseModValue(String value_entry) {
    StringTokenizer tok = new StringTokenizer(value_entry, slash);
    String _values = tok.nextToken();
    String mod = tok.nextToken();
    int modul = Integer.parseInt(mod);
    if (_values.equalsIgnoreCase(asteriks)) {  // all values will be checked by modul later
      valueSet.fillAllValues();
      valueSet.setStep(modul);
      for (int i = 0; i < valueSet.getLength(); i++) {
        if ( (valueSet.getValueAt(i) % modul) != 0) {
          delete(valueSet.getValueAt(i--));
        }
      }
    } else {
      parseRecursively(_values);
      CronSet cronSet = new CronSet(0, Integer.MAX_VALUE); // used only to store the values, which will be checked by modul later
      cronSet.extractValues(_values, cronSet);
      int[] vals = cronSet.getValues();
      for (int i = 0; i < vals.length; i++) {           
        StringTokenizer st = new StringTokenizer(_values, dash);
        int start = Integer.parseInt(st.nextToken());
        
        // save the start-value and the following steps according to the start-value
        if (  (vals[i]-start) != 0 && ((vals[i]-start) % modul) != 0) {
        	delete(vals[i]);
        }
      }
    }
  }

  private class CronSet implements Serializable {

    int[] vals = null;
    int min = 0;
    int max = 0;
    int step = -1;

//    public CronSet() {
//    }

    public CronSet(int min, int max) {
      this.min = min;
      this.max = max;
      vals = new int[0];
    }

    public void fillAllValues() {
      if (max != -1) {
        vals = new int[max - min + 1];
        int value = min;
        for (int i = 0; i < max - min + 1; i++) {
          vals[i] = value++;
        }
      }
    }

    int getStep() {
      return step;
    }

    void setStep(int step) {
      this.step = step;
    }

    int getMin() {
      return min;
    }

    int getMax() {
      return max;
    }

    int getValueAt(int pointer) {
      if (vals != null) {
        if (pointer < vals.length) {
          return vals[pointer];
        } else {
          return -1;
        }
      } else if (max == -1) {
        return min + pointer;
      } else {
        return -1;
      }
    }

    int[] getValues() {
      if (vals != null) {
        return vals;
      } else if (max == -1) {
        return new int[] {min, -1};
      } else {
        return null;
      }
    }

    int getLength() {
      if (vals != null) {
        return vals.length;
      } else if (max == -1) {
        return -1;
      } else {
        return 0;
      }
    }


    boolean contains(int val) {
      if (vals != null) {
        for (int i = 0; i < vals.length; i++) {
          if (vals[i] == val) {
            return true;
          }
        }
        return false;
      } else if (max == -1) {
        return (val >= min);
      } else {
        return false;
      }
    }

    int indexOf(int value) {
      if (vals != null) {
        for (int i = 0; i < vals.length; i++) {
          if (vals[i] == value)
            return i;
        }
        return -1;
      } else if (max == -1) {
        if (value >= min) {
          return value - min;
        } else {
          return -1;
        }
      } else {
        return -1;
      }
    }

    void insert(int value) {
      if ( (value < min) || ( (max != -1) && (value > max) )) {   // value out of range
        throw new IllegalArgumentException("Value " + value + " out of range [" + min + " - " + max + "]");
      }
      if (max == -1) {  // if a value is inserted then the max value is not infinity
        max = Integer.MAX_VALUE;
      }
      if (vals == null) {
        vals = new int[1];
        vals[0] = value;
      } else if (!contains(value)) {
        int[] temp = new int[vals.length + 1];
        for (int i = 0; i < vals.length; i++) {
          if (value < vals[i]) {
            System.arraycopy(vals, 0, temp, 0, i);
            System.arraycopy(vals, i, temp, i + 1, vals.length - i);
            temp[i] = value;
            vals = temp;
            return;
          }
        }
        System.arraycopy(vals, 0, temp, 0, vals.length);
        temp[vals.length] = value;
        vals = temp;
      }
    }

    void delete(int value) {
//      if (max == -1) {
//        throw new IllegalArgumentException("Can't insert in infinity ranged set");
//      }
      if ( (vals != null) && (contains(value)) ) {
        int ind = indexOf(value);
        int[] temp = new int[vals.length - 1];
        if (ind == 0) {
          System.arraycopy(vals, 1, temp, 0, temp.length);
        } else if (ind == (vals.length - 1)) {
          System.arraycopy(vals, 0, temp, 0, temp.length);
        } else {
          System.arraycopy(vals, 0, temp, 0, ind);
          System.arraycopy(vals, ind + 1, temp, ind, temp.length - ind);
        }
        vals = temp;
      }
    }

	void extractValues(String values_entry, CronSet cronSet) {
	    if (values_entry.equalsIgnoreCase(asteriks)) {
	    	cronSet.fillAllValues();
	      } else if (values_entry.indexOf(_dash) != -1) {   // '-' exists
	        StringTokenizer tok = new StringTokenizer(values_entry, dash);
	        int start = Integer.parseInt(tok.nextToken());
	        int end = Integer.parseInt(tok.nextToken());
	        for (int i = start; i < end + 1; i++) {
	        	cronSet.insert(i);
	        }
	      } else {      // should be single number
	      	cronSet.insert(Integer.parseInt(values_entry));
	      }
	    }

  }

//--------------------------------------------------- FOR TESTING ---------------------------------------------------
//
//  public static void main(String[] args) {
//    CronYearField cron = new CronYearField();
//    cron.parse("*/3");
//    System.out.println("CRON : " + cron);
//  }


}
