package com.sap.bc.cts.tp.net;

/**
 * Title:        Software Delivery Manager
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Software Logistics - here: D019309
 *
 */
public interface TimeoutControlledServiceFactory extends ServiceFactory {
   public Service makeService(
    Manager manager,
    SocketTimeoutViewIF socketTimeoutView);
}
