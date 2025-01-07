/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core;

import java.io.Serializable;

import javax.portlet.Event;
import javax.xml.namespace.QName;

/**
 * 
 * @author Diyan Yordanov
 * @version 7.12
 *
 */
public class EventImpl implements Event {

  private QName qName;
  private Serializable value;
  
  public EventImpl(QName qName, Serializable value) {
    this.qName = qName;
    this.value = value;
  }
  
  public String getName() {
    return qName.getLocalPart();
  }

  public QName getQName() {
    return qName;
  }

  public Serializable getValue() {
    return value;
  }

}
