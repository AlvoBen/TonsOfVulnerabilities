/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/core/com/sap/engine/services/deployrepository/DeployRepositoryFrame.java#1 $
 * Last changelist: $Change: 217622 $
 * Last changed at: $DateTime: 2008/09/17 17:03:11 $
 * Last changed by: $Author: c5097724 $
 */
package com.sap.engine.services.deployrepository;

import javax.sql.DataSource;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.registry.ObjectRegistry;
import com.sap.engine.frame.core.database.DatabaseException;
import com.sap.tc.logging.Location;

/**
 * @author d029696
 */
public final class DeployRepositoryFrame implements ApplicationServiceFrame {
    private static final Location LOCATION = Location.getLocation(DeployRepositoryFrame.class);
    private ApplicationServiceContext m_context = null;

    public void start(final ApplicationServiceContext context) throws ServiceException {
        final DeployRepository repository;
        try {
            final DataSource dataSource = context.getCoreContext().getDatabaseContext().getSystemDataSource();
            repository = new DeployRepositoryImpl(dataSource);
        } catch (DatabaseException e) {
            final ServiceException serviceException = new ServiceException("failed to obtain system data source", e);
            LOCATION.throwing(serviceException);
            throw serviceException;
        }
        context.getContainerContext().getObjectRegistry().registerInterface(repository);
        m_context = context;
    }

    public void stop() throws ServiceRuntimeException {
        final ObjectRegistry registry = m_context.getContainerContext().getObjectRegistry();
        m_context = null;
        registry.unregisterInterface();
    }
}

