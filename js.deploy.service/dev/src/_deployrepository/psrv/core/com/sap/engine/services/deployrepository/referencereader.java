/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/core/com/sap/engine/services/deployrepository/ReferenceReader.java#1 $
 * Last changelist: $Change: 217622 $
 * Last changed at: $DateTime: 2008/09/17 17:03:11 $
 * Last changed by: $Author: c5097724 $
 */
package com.sap.engine.services.deployrepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.ReferenceType;

public class ReferenceReader {
    public static final int INT_ENCODING_FALSE = 0;
    public static final int INT_ENCODING_TRUE = 1;
    public static final int INT_ENCODING_WEAK = 1;
    public static final int INT_ENCODING_HARD = 2;
    public static final String REFERENCES_TABLE = "BC_DEP_REFERENCES";
    public static final String RESOURCE_REFERENCES_TABLE = "BC_DEP_RES_REFERENCES";

    private static final String SQL_READ_REFERENCES = "SELECT TO_TYPE, TO_VENDOR, TO_NAME, FUNCTIONAL, CLASSLOADING, STRENGTH FROM " + REFERENCES_TABLE + " WHERE FROM_TYPE = 'application' AND FROM_VENDOR = ? AND FROM_NAME = ?";
    private static final String SQL_READ_RESOURCE_REFERENCES = "SELECT RESOURCETYPE, RESOURCENAME, FUNCTIONAL, CLASSLOADING, STRENGTH FROM " + RESOURCE_REFERENCES_TABLE + " WHERE FROM_VENDOR = ? AND FROM_NAME = ? AND FROM_CONTAINER = ?";
    public static final String UNSPECIFIED_PROVIDER = " ";

    public Set<ResourceReference> readResourceReferences(final Connection connection, final String vendor, final String appName, final String containerName)
            throws SQLException {
        final Set<ResourceReference> result = new HashSet<ResourceReference>();
        final PreparedStatement readResourceReferences = connection.prepareStatement(SQL_READ_RESOURCE_REFERENCES);
        try {
            readResourceReferences.setString(1, vendor);
            readResourceReferences.setString(2, appName);
            readResourceReferences.setString(3, containerName);
            final ResultSet rs = readResourceReferences.executeQuery();
            try {
                while (rs.next()) {
                    final String resourceType = rs.getString(1);
                    final String resourceName = rs.getString(2);
                    final boolean functional = (rs.getInt(3) == INT_ENCODING_TRUE);
                    final boolean classloading = (rs.getInt(4) == INT_ENCODING_TRUE);
                    final int strength = rs.getInt(5);
                    final String referenceType;
                    switch (strength) {
                    case INT_ENCODING_WEAK:
                        referenceType = ReferenceObjectIntf.REF_TYPE_WEAK;
                        break;
                    case INT_ENCODING_HARD:
                        referenceType = ReferenceObjectIntf.REF_TYPE_HARD;
                        break;
                    default:
                        referenceType = ReferenceObjectIntf.REF_TYPE_WEAK;
                    }
                    final ResourceReference resourceReference = new ResourceReference(resourceName, resourceType, referenceType, functional, classloading);
                    result.add(resourceReference);
                }
            } finally {
                rs.close();
            }
        } finally {
            readResourceReferences.close();
        }
        return result;
    }

    public Set<ReferenceObjectIntf> readReferences(final Connection connection, final String vendor, final String appName)
            throws SQLException {
        final Set<ReferenceObjectIntf> result = new HashSet<ReferenceObjectIntf>();
        final PreparedStatement readReferences = connection.prepareStatement(SQL_READ_REFERENCES);
        try {
            readReferences.setString(1, vendor);
            readReferences.setString(2, appName);
            final ResultSet rs = readReferences.executeQuery();
            try {
                while (rs.next()) {
                    final String toType = rs.getString(1);
                    final String toVendor = rs.getString(2);
                    final String toName = rs.getString(3);
                    final boolean functional = (rs.getInt(4) == INT_ENCODING_TRUE);
                    final boolean classloading = (rs.getInt(5) == INT_ENCODING_TRUE);
                    final int strength = rs.getInt(6);
                    final String referenceType;
                    switch (strength) {
                    case INT_ENCODING_WEAK:
                        referenceType = ReferenceObjectIntf.REF_TYPE_WEAK;
                        break;
                    case INT_ENCODING_HARD:
                        referenceType = ReferenceObjectIntf.REF_TYPE_HARD;
                        break;
                    default:
                        referenceType = ReferenceObjectIntf.REF_TYPE_WEAK;
                    }
                    final ReferenceObject reference = new ReferenceObject();
                    reference.setReferenceTargetType(toType);
                    if (!UNSPECIFIED_PROVIDER.equals(toVendor) && !"".equals(toVendor)) {
                        reference.setReferenceProviderName(toVendor);
                    } else {
                        reference.setReferenceProviderName(null);
                    }
                    reference.setReferenceTarget(toName);
                    reference.setReferenceType(referenceType);
                    reference.setCharacteristic(new ReferenceType(functional, classloading, true));
                    result.add(reference);
                }
            } finally {
                rs.close();
            }
        } finally {
            readReferences.close();
        }
        return result;
    }
}

