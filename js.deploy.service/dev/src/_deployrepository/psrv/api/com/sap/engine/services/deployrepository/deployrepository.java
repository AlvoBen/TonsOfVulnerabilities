/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/api/com/sap/engine/services/deployrepository/DeployRepository.java#1 $
 * Last changelist: $Change: 217622 $
 * Last changed at: $DateTime: 2008/09/17 17:03:11 $
 * Last changed by: $Author: c5097724 $
 */
package com.sap.engine.services.deployrepository;

import java.util.Set;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;

public interface DeployRepository {
    Set<ReferenceObjectIntf> readReferences(String vendor, String appName);

    Set<ResourceReference> readResourceReferences(String vendor, String appName, String containerName);

    void deleteAllReferences(String vendor, String appName);

    void deleteAllResourceReferences(String vendor, String appName);

    void writeReferences(String vendor, String appName, Set<ReferenceObjectIntf> references);

    void writeResourceReferences(String vendor, String appName, String containerName, Set<ResourceReference> references);
}

