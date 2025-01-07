package com.sap.engine.core.service630.context.container.deploy;

import com.sap.engine.core.Names;
import com.sap.engine.frame.container.deploy.zdm.*;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.core.service630.container.MemoryContainer;
import com.sap.tc.logging.Location;

/**
 * Interface <code>RollingPatch</code> implementation.
 *
 * @see com.sap.engine.frame.container.deploy.zdm.RollingPatch
 * @version 1.00
 * @since 7.10
 * @author Dimitar Kostadinov
 */
public class RollingPatchImpl implements RollingPatch {

  private MemoryContainer memoryContainer;
  private String initiator;
  private static final Location location = Location.getLocation(RollingPatchImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  RollingPatchImpl(MemoryContainer memoryContainer, String initiator) {
    this.memoryContainer = memoryContainer;
    this.initiator = initiator;
  }

  public RollingResult updateInstanceAndDB(RollingComponent rollingComponent) throws RollingException {
    try {
      return memoryContainer.updateInstanceAndDB(rollingComponent, initiator);
    } catch (ServiceException e) {
      throw new RollingException(location, e);
    }
  }

  public RollingResult syncInstanceWithDB(RollingName rollingName) throws RollingException {
    try {
      return memoryContainer.syncInstanceWithDB(rollingName, initiator);
    } catch (ServiceException e) {
      throw new RollingException(location, e);
    }
  }

}