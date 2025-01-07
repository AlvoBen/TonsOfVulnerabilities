package com.sap.jmx.monitoring.api;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * This class is used by <code>ConfigurationResourceMBean</code> as return 
 * type of the method {@link ConfigurationResourceMBean#getConfigurationParameters}.
 * It represents a list of configuration parameters described by their name and 
 * their value. The list is alphabetically ordered by the parameter names.
 */
public final class ConfigurationList implements TwinIterator
{
  /**
   * Maximal length of a parameter name (40 characters).
   */  
  public static final int MAX_PARAMETER_NAME_LENGTH = 40;
  /**
   * Maximal length of a parameter value (255 characters).
   */  
  public static final int MAX_PARAMETER_VALUE_LENGTH = 255;

	private TreeMap properties;
	
  /**
   * Constructs a new <code>ConfigurationList</code> object.
   */
	public ConfigurationList()
	{
		properties = new TreeMap();
	}
	
  /**
   * Constructs a new <code>ConfigurationList</code> object
   * based on the values of the given <code>Properties</code> object.
   * 
   * @param configurationData a list of configuration parameters.
   * 
   * @throws IllegalArgumentException if the configuration data is not valid.
   */
	public ConfigurationList(final Properties configurationData) 
    throws IllegalArgumentException
	{
		this();
		
    if (configurationData == null)
    {
      throw new IllegalArgumentException("ConfigurationData is null.");
    }
    
		final Iterator iter = configurationData.entrySet().iterator();
		while (iter.hasNext())
    {
      final Entry element = (Entry) iter.next();
      setConfigurationParameter((String) element.getKey(), (String) element.getValue());
    }
	}
	
  /**
   * Adds a new configuration parameter to this configuration list.
   * 
   * @param name the name of the configuration parameter to be placed into 
   * this configuration list (max. {@link #MAX_PARAMETER_NAME_LENGTH}
   * characters).
   * 
   * @param value the value corresponding to <code>name</code> (max.
   * {@link #MAX_PARAMETER_VALUE_LENGTH} characters, truncated
   * otherwise).
   * 
   * @return the previous value of the specified name in this configuration list,
   * or <code>null</code> if it did not have one.
   * 
   * @throws IllegalArgumentException if the name is null or has more than 
   * {@link #MAX_PARAMETER_NAME_LENGTH} characters.
   * 
   * @see #getConfigurationParameter
   */
  public Object setConfigurationParameter(
    final String name, 
    final String value) 
    throws IllegalArgumentException
  {
    checkName(name);
    
    return properties.put(name, value);
  }
  
  /**
   * Searches for the value of the configuration parameter with the 
   * specified name in this configuration list. The method returns 
   * <code>null</code> if the configuration parameter is not found.
   * 
   * @param name the name of the configuration parameter.
   * 
   * @return the value of the configuration parameter with the specified name, 
   * or <code>null</code> if the configuration parameter is not found.
   * 
   * @see #setConfigurationParameter
   */
  public String getConfigurationParameter(final String name)
  {
    return (String) properties.get(name);
  }
  
  /**
   * @see com.sap.jmx.monitoring.api.TwinIterator#getKeys()
   */
  public Iterator getKeys()
  {
    return properties.keySet().iterator();
  }
  
  /**
   * @see com.sap.jmx.monitoring.api.TwinIterator#getValues()
   */
  public Iterator getValues()
  {
    return properties.values().iterator();
  }
  
  /**
   * Returns all parameters.
   * 
   * @return String[][]
   */
  public String[][] getParameters()
  {
    final String[][] parameters = new String[2][properties.size()];
    final Iterator iter = properties.entrySet().iterator();
    int i = 0;
    while (iter.hasNext())
    {
      final Entry entry = (Entry) iter.next();
      parameters[0][i] = (String) entry.getKey();
      parameters[1][i] = (String) entry.getValue();
      i++;
    }
    return parameters; 
  }
  
  /**
   * Returns whether this configuration list is empty or not.
   * 
   * @return whether this configuration list is empty or not.
   */
  public boolean isEmpty()
  {
    return properties.isEmpty();
  }
  
  /**
   * Returns whether this configuration list contains a parameter with the
   * specified name or not.
   * 
   * @param name the name of the configuration parameter.
   * 
   * @return whether this configuration list contains a parameter with the
   * specified name or not.
   */
  public boolean contains(String name)
  {
    return properties.containsKey(name);
  }
  
  private void checkName(final String name)
    throws IllegalArgumentException
  {
    if (name == null)
    {
      throw new IllegalArgumentException("Name is null.");
    }
    else if (name.length() < 1)
    {
      throw new IllegalArgumentException("Name is empty.");
    }
  }
}
