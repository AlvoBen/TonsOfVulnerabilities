/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.rmic.log;

import java.util.ArrayList;

/**
 * @author Mladen Droshev
 */

public class RMICOutputObject implements RMICConstants{


  
  private byte current_status = NOT_STARTED;
  
  private String messages = "";
  
  private ArrayList listExc = null;
  
  public RMICOutputObject(){
    this.listExc = new ArrayList();
  }
  
  public byte getStatus(){
    return this.current_status;
  } 
  
  public synchronized void setPassed(){
    this.current_status = PASSED;
  }
  
  public synchronized void setWorking(){
    this.current_status = WORKING;
  }
  
  public synchronized void setError(){
    this.current_status = ERROR;
  }
  
  public synchronized void init(){
    this.current_status = NOT_STARTED;
    this.messages = "";
  }
  
  public String getMessages(){
    return this.messages;
  }
  
  public ArrayList getExceptions(){
    return this.listExc;
  }
  
  

}