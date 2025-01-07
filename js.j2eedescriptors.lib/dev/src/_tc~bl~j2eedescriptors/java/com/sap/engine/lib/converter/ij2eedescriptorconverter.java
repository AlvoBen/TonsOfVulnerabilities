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

/**
 * Interface implemented by all J2EE descriptor converters.
 * 
 * @author d037913
 *  
 */
public interface IJ2EEDescriptorConverter {

  /**
   * type constants used in
   * 
   * @see #getType()
   */
  int EJB = 0;
  int WEB = 1;
  int APPCLIENT = 2;
  int CONNECTOR = 3;
  int APPLICATION = 4;
  int TLD = 5;

  /**
   * int constant indicating the J2EE container type
   * 
   * @see #EJB etc.
   */
  public int getType();

  /**
   * Converts all descriptors in the context to the latest J2EE release.
   * CAUTION: Implementations should take care about the thread safety of XML
   * parser/transformer calls.
   * 
   * @param context
   *          the converter gets all InputStreams of unconverted descriptors
   *          from the context and puts all converted Documents into the
   *          context.
   * @throws ConversionException
   *           thrown if anything goes wrong during conversion (wraps several
   *           other causing exception(s)).
   */
  public void convert(ConversionContext context) throws ConversionException;

}