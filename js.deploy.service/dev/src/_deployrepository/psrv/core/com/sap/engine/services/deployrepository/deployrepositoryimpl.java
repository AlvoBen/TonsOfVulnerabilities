/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/core/com/sap/engine/services/deployrepository/DeployRepositoryImpl.java#1 $
 * Last changelist: $Change: 217622 $
 * Last changed at: $DateTime: 2008/09/17 17:03:11 $
 * Last changed by: $Author: c5097724 $
 */
package com.sap.engine.services.deployrepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;

public class DeployRepositoryImpl implements DeployRepository {
    private final ReferenceReader m_reader;
    private final ReferenceWriter m_writer;
    private final DataSource m_dataSource;

    public DeployRepositoryImpl(final DataSource dataSource) {
        m_reader = new ReferenceReader();
        m_writer = new ReferenceWriter();
        m_dataSource = dataSource;
    }

    public Set<ReferenceObjectIntf> readReferences(final String vendor, final String appName) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                final Set<ReferenceObjectIntf> result = m_reader.readReferences(connection, vendor, appName);
                return result;
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to retrieve references of " + vendor + " / " + appName, e);
        }
    }

    public Set<ResourceReference> readResourceReferences(final String vendor, final String appName, final String containerName) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                final Set<ResourceReference> result = m_reader.readResourceReferences(connection, vendor, appName, containerName);
                return result;
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to retrieve resource references of " + vendor + " / " + appName + " / " + containerName, e);
        }
    }

    public void deleteAllReferences(final String vendor, final String appName) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                m_writer.deleteReferences(connection, vendor, appName);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to delete all references of " + vendor + " / " + appName, e);
        }
    }

    public void deleteAllResourceReferences(final String vendor, final String appName) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                m_writer.deleteResourceReferences(connection, vendor, appName);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to delete all resource references of " + vendor + " / " + appName, e);
        }
    }

    public void writeReferences(final String vendor, final String appName, final Set<ReferenceObjectIntf> references) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                m_writer.writeReferences(connection, vendor, appName, references);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to write references of " + vendor + " / " + appName + ": " + references, e);
        }
    }

    public void writeResourceReferences(final String vendor, final String appName, final String containerName, final Set<ResourceReference> references) {
        try {
            final Connection connection = m_dataSource.getConnection();
            try {
                m_writer.writeResourceReferences(connection, vendor, appName, containerName, references);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to write resource references of " + vendor + " / " + appName + ": " + references, e);
        }
    }

}

