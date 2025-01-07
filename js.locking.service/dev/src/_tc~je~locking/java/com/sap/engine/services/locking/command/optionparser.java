/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/OptionParser.java#6 $ SAP*/
package com.sap.engine.services.locking.command;


import java.util.HashMap;

import com.sap.engine.services.locking.exception.SAPOptionsIllegalArgumentException;


/**
 * A class to parse commandline-options.
 * Example: java -classpath . -verbose Test.class 
 */
public class OptionParser
{
  /**
   * The prefix, with which all Options start.
   */
  public static char OPTION_PREFIX = '-';
  
  /**
   * Base interface for all Options.
   */
  public interface Option 
  { 
    public String[] getNames(); 
  }
  
  /**
   * An option, which requires another argument, which will be 
   * set as value. 
   * <p>
   * Every option can have multiple names.
   * Example: "java -cp" equals "java -classpath" 
   */
  public static class ValueOption implements Option
  {
    private String _names[]; 
    private String _value = null;
    public ValueOption(String names[]) 
    { 
      if (names == null || names.length == 0)
        throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_WITHOUT_NAME);
      _names = names; 
    }
    public ValueOption(String name) { this(new String[] { name }); }
    public String[] getNames() { return _names; }
    public String getValue() { return _value; }
  }

  /**
   * An option, which does not require another argument.
   * The value is true, if the option appears.
   * <p>
   * Every option can have multiple names.
   * Example: "java -cp" equals "java -classpath" 
   */
  public static class FlagOption implements Option
  {
    private String _names[]; 
    private boolean _value = false;
    public FlagOption(String names[]) 
    { 
      if (names == null || names.length == 0)
        throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_WITHOUT_NAME);
      _names = names; 
    }
    public FlagOption(String name) { this(new String[] { name }); }
    public String[] getNames() { return _names; }
    public boolean getValue() { return _value; }
  }
  
  
  /**
   * Parses the arguments and sets the values for all found options.
   * Returns the remaining arguments, which were not options.
   */
  public static String[] parse(String arguments[], Option options[]) throws IllegalArgumentException
  {
    if (arguments == null || options == null || arguments.length == 0 || options.length == 0)
      return new String[0];
      
    // put all names in one HashMap
    HashMap optionsForNames = new HashMap();
    for (int i = 0; i < options.length; i++)
    {
      String names[] = options[i].getNames();
      if (names == null || names.length == 0)
        continue; // shouldn't happen, was already checked in Option-classes
     
      for (int j = 0; j < names.length; j++)
      {
        Object existing = optionsForNames.put(names[j], options[i]);
        if (existing != null)
          throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_NAME_MULTIPLE_TIMES, new Object[] { OPTION_PREFIX + names[j] });
      }
    }
    
    // parse the arguments and set the values for found options
    int i = 0;
    for ( ; i < arguments.length; i++)
    {
      if (arguments[i].charAt(0) == OPTION_PREFIX)
      {
        String optionName = arguments[i].substring(1);
        Object option = optionsForNames.get(optionName);
        if (option == null)
          throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_UNKNOWN, new Object[] { OPTION_PREFIX + optionName });
        if (option instanceof FlagOption)
          ((FlagOption) option)._value = true;
        else // instanceof ValueOption
        {
          ValueOption vo = (ValueOption) option;
          if (vo._value != null)
            throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_WITH_MULTIPLE_VALUES, new Object[] { OPTION_PREFIX + optionName });
          i++;
          if (i == arguments.length)
            throw new SAPOptionsIllegalArgumentException(SAPOptionsIllegalArgumentException.OPTION_MUST_HAVE_VALUE, new Object[] { OPTION_PREFIX + optionName });
          vo._value = arguments[i];
        }
      }
      else
        break; // no option -> the rest are the remaining arguments
    }
    
    // return remaining arguments
    String result[] = new String[arguments.length - i];
    System.arraycopy(arguments, i, result, 0, result.length);
    return result;
  }
}
