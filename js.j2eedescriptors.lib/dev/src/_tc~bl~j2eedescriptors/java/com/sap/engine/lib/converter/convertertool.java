/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.lib.converter.impl.AppClientConverter;
import com.sap.engine.lib.converter.impl.ApplicationConverter;
import com.sap.engine.lib.converter.impl.ConnectorConverter;
import com.sap.engine.lib.converter.impl.EJBConverter;
import com.sap.engine.lib.converter.impl.TldConverter;
import com.sap.engine.lib.converter.impl.WebConverter;

/**
 * Singleton that will convert any J2EE &lt; 1.4 descriptors into J2EE 1.4
 * compliant descriptors.
 * 
 * @author d037913
 */
public class ConverterTool {

  private Map converters = new HashMap();
  private static ConverterTool instance;

  public static synchronized ConverterTool getInstance() {
    if (instance != null) {
      return instance;
    }
    return instance = new ConverterTool();
  }

  private ConverterTool() {
  }

  public void convert(int containerType, ConversionContext context)
      throws ConversionException {
    IJ2EEDescriptorConverter converter = getConverter(containerType);
    if (converter == null) {
      throw new IllegalArgumentException(
          "no converter registered for container type: " + containerType);
    }
    converter.convert(context);
  }

  /**
   * Returns the descriptor converter for a given container type (or
   * <code>null</code>).
   * 
   * @param containerType
   * @see IJ2EEDescriptorConverter#EJB etc.
   * @return the descriptor converter for the given container (or
   *         <code>null</code>)
   */
  public IJ2EEDescriptorConverter getConverter(int containerType) {
    Integer typeWrapped = new Integer(containerType);
    IJ2EEDescriptorConverter converter = (IJ2EEDescriptorConverter) converters
        .get(typeWrapped);
    if (converter == null) {
      switch (containerType) {
      case IJ2EEDescriptorConverter.EJB:
        converter = new EJBConverter();
        break;

      case IJ2EEDescriptorConverter.WEB:
        converter = new WebConverter();
        break;

      case IJ2EEDescriptorConverter.APPLICATION:
        converter = new ApplicationConverter();
        break;

      case IJ2EEDescriptorConverter.APPCLIENT:
        converter = new AppClientConverter();
        break;

      case IJ2EEDescriptorConverter.CONNECTOR:
        converter = new ConnectorConverter();
        break;

      case IJ2EEDescriptorConverter.TLD:
        converter = new TldConverter();
        break;

      }
      if (converter != null) {
        converters.put(typeWrapped, converter);
      }
    }
    return converter;
  }

}