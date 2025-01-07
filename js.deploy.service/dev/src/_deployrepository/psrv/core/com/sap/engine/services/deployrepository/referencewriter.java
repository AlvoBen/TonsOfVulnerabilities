/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/core/com/sap/engine/services/deployrepository/ReferenceWriter.java#1 $
 * Last changelist: $Change: 217622 $
 * Last changed at: $DateTime: 2008/09/17 17:03:11 $
 * Last changed by: $Author: c5097724 $
 */
package com.sap.engine.services.deployrepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ReferenceCharacteristic;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;

public class ReferenceWriter {
    private static final String SQL_INSERT_REFERENCES = "INSERT INTO " + ReferenceReader.REFERENCES_TABLE + " (FROM_TYPE, FROM_VENDOR, FROM_NAME, TO_TYPE, TO_VENDOR, TO_NAME, FUNCTIONAL, CLASSLOADING, STRENGTH) VALUES ('application',?,?,?,?,?,?,?,?)";
    private static final String SQL_DELETE_REFERENCES = "DELETE FROM " + ReferenceReader.REFERENCES_TABLE + " WHERE FROM_TYPE = 'application' AND FROM_VENDOR = ? AND FROM_NAME = ?";
    private static final String SQL_INSERT_RESOURCE_REFERENCES = "INSERT INTO " + ReferenceReader.RESOURCE_REFERENCES_TABLE + " (FROM_VENDOR, FROM_NAME, FROM_CONTAINER, RESOURCETYPE, RESOURCENAME, FUNCTIONAL, CLASSLOADING, STRENGTH) VALUES (?,?,?,?,?,?,?,?)";
    private static final String SQL_DELETE_RESOURCE_REFERENCES = "DELETE FROM " + ReferenceReader.RESOURCE_REFERENCES_TABLE + " WHERE FROM_VENDOR = ? AND FROM_NAME = ?";
    private static final int BATCH_SIZE = 10;
    private static final ReferenceCharacteristic DEFAULT_REFERENCE_TYPE = new ReferenceCharacteristic(true, true);

    public void writeResourceReferences(final Connection connection, final String vendor, final String appName, final String containerName,
            final Set<ResourceReference> references) throws SQLException {
        final PreparedStatement insertResourceReferences = connection.prepareStatement(SQL_INSERT_RESOURCE_REFERENCES);
        try {
            int batchCount = 0;
            for (final ResourceReference reference : references) {
                insertResourceReferences.setString(1, vendor);
                insertResourceReferences.setString(2, appName);
                insertResourceReferences.setString(3, containerName);
                insertResourceReferences.setString(4, reference.getResRefType());
                insertResourceReferences.setString(5, reference.getResRefName());
                final ReferenceCharacteristic type = reference.getType();
                if (type.isFunctional()) {
                    insertResourceReferences.setInt(6, ReferenceReader.INT_ENCODING_TRUE);
                } else {
                    insertResourceReferences.setInt(6, ReferenceReader.INT_ENCODING_FALSE);
                }
                if (type.isFunctional()) {
                    insertResourceReferences.setInt(7, ReferenceReader.INT_ENCODING_TRUE);
                } else {
                    insertResourceReferences.setInt(7, ReferenceReader.INT_ENCODING_FALSE);
                }
                if (reference.getReferenceType().equals(ReferenceObjectIntf.REF_TYPE_HARD)) {
                    insertResourceReferences.setInt(8, ReferenceReader.INT_ENCODING_HARD);
                } else {
                    insertResourceReferences.setInt(8, ReferenceReader.INT_ENCODING_WEAK);
                }
                insertResourceReferences.addBatch();
                batchCount++;
                if (batchCount >= BATCH_SIZE) {
                    executeBatch(insertResourceReferences, batchCount);
                    batchCount = 0;
                }
            }
            if (batchCount > 0) {
                executeBatch(insertResourceReferences, batchCount);
                batchCount = 0;
            }
        } finally {
            insertResourceReferences.close();
        }
    }

    public void deleteResourceReferences(final Connection connection, final String vendor, final String appName) throws SQLException {
        final PreparedStatement deleteResourceReferences = connection.prepareStatement(SQL_DELETE_RESOURCE_REFERENCES);
        try {
            deleteResourceReferences.setString(1, vendor);
            deleteResourceReferences.setString(2, appName);
            deleteResourceReferences.executeUpdate();
        } finally {
            deleteResourceReferences.close();
        }
    }

    private void executeBatch(final PreparedStatement insertResourceReferences, int batchCount) throws SQLException {
        final int[] batchResult = insertResourceReferences.executeBatch();
        if (batchResult.length != batchCount) {
            throw new SQLException("failed to insert all rows in batch (trying to insert " + batchCount + " rows, but result has " + batchResult.length + "entries).");
        }
    }

    public void writeReferences(final Connection connection, final String vendor, final String appName, final Set<ReferenceObjectIntf> references)
            throws SQLException {
        final PreparedStatement insertReferences = connection.prepareStatement(SQL_INSERT_REFERENCES);
        try {
            int batchCount = 0;
            for (final ReferenceObjectIntf reference: references) {
                /* FROM_VENDOR, FROM_NAME, TO_TYPE, TO_VENDOR, TO_NAME, FUNCTIONAL, CLASSLOADING, STRENGTH */
                insertReferences.setString(1, vendor);
                insertReferences.setString(2, appName);
                insertReferences.setString(3, reference.getReferenceTargetType());
                if (reference.getReferenceProviderName() == null) {
                    insertReferences.setString(4, ReferenceReader.UNSPECIFIED_PROVIDER);
                } else {
                    insertReferences.setString(4, reference.getReferenceProviderName());
                }
                insertReferences.setString(5, reference.getReferenceTarget());
                final ReferenceCharacteristic type;
                if (reference instanceof ReferenceObject) {
                    final ReferenceObject referenceObject = (ReferenceObject) reference;
                    type = referenceObject.getCharacteristic();
                } else {
                    type = DEFAULT_REFERENCE_TYPE;
                }
                if (type.isFunctional()) {
                    insertReferences.setInt(6, ReferenceReader.INT_ENCODING_TRUE);
                } else {
                    insertReferences.setInt(6, ReferenceReader.INT_ENCODING_FALSE);
                }
                if (type.isClassloading()) {
                    insertReferences.setInt(7, ReferenceReader.INT_ENCODING_TRUE);
                } else {
                    insertReferences.setInt(7, ReferenceReader.INT_ENCODING_FALSE);
                }
                if (reference.getReferenceType().equals(ReferenceObjectIntf.REF_TYPE_HARD)) {
                    insertReferences.setInt(8, ReferenceReader.INT_ENCODING_HARD);
                } else {
                    insertReferences.setInt(8, ReferenceReader.INT_ENCODING_WEAK);
                }
                insertReferences.addBatch();
                batchCount++;
                if (batchCount >= BATCH_SIZE) {
                    executeBatch(insertReferences, batchCount);
                    batchCount = 0;
                }
            }
            if (batchCount > 0) {
                executeBatch(insertReferences, batchCount);
                batchCount = 0;
            }
        } finally {
            insertReferences.close();
        }
    }

    public void deleteReferences(final Connection connection, final String vendor, final String appName) throws SQLException {
        final PreparedStatement deleteReferences = connection.prepareStatement(SQL_DELETE_REFERENCES);
        try {
            deleteReferences.setString(1, vendor);
            deleteReferences.setString(2, appName);
            deleteReferences.executeUpdate();
        } finally {
            deleteReferences.close();
        }
    }
}

