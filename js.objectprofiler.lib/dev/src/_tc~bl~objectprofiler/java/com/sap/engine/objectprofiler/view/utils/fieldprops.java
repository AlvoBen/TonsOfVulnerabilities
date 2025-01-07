package com.sap.engine.objectprofiler.view.utils;

import java.io.Serializable;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-7-28
 * Time: 11:30:10
 */
public class FieldProps implements Cloneable, Serializable {
  private String fieldName = null;
  private String fieldType = null;
  private boolean excluded = false;

  public FieldProps(String fieldName, String fieldType) {
    this(fieldName, fieldType, false);
  }

  public FieldProps(String fieldName, String fieldType, boolean excluded) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.excluded = excluded;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getFieldType() {
    return fieldType;
  }

  public boolean isExcluded() {
    return excluded;
  }

  public void setExcluded(boolean excluded) {
    this.excluded = excluded;
  }

  public Object clone() {
    FieldProps props = new FieldProps(fieldName, fieldType, excluded);

    return props;
  }
}
