/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.admin;

import com.sap.engine.admin.model.jsr77.J2EEManagedObjectAdapter;
import com.sap.engine.admin.model.jsr77.JSR77ObjectNameFactory;
import com.sap.engine.admin.model.jsr77.RMI_IIOPResource;
import com.sap.engine.admin.model.ManagementModelManager;

import javax.management.ObjectName;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.InstanceNotFoundException;

/**
 *
 * @author Ivan Atanassov
 */
public class RMIIIOPResource extends J2EEManagedObjectAdapter implements RMI_IIOPResource {

  private ManagementModelManager mmManager;
  private ObjectName objectName = null;
  private String resourceName;

  public RMIIIOPResource(String resourceName, ManagementModelManager mmManager) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
    super(JSR77ObjectNameFactory.getRMI_IIOPResourceName(resourceName), resourceName);

    this.mmManager = mmManager;
    this.resourceName = resourceName;

    objectName = mmManager.registerManagedObject(this, RMI_IIOPResource.class);
  }

  public String getRegisteredObjectName() {
    return objectName == null ? null : objectName.toString();
  }

  public void destroy() throws InstanceNotFoundException {
    mmManager.unregisterManagedObject(objectName);
  }

}
