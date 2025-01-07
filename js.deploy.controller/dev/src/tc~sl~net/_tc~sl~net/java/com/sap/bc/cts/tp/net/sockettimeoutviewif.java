package com.sap.bc.cts.tp.net;

/**
 * Title:        Software Delivery Manager
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Software Logistics - here: D019309
 *
 */
public interface SocketTimeoutViewIF {
  int getSoTimeout() throws SocketTimeoutViewException;
  void setSoTimeout(int timeout) throws SocketTimeoutViewException;
}
