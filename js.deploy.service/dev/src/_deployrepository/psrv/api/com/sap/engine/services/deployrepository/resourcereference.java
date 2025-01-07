/* $Id: //engine/js.deploy.service/dev/src/_deployrepository/psrv/api/com/sap/engine/services/deployrepository/ResourceReference.java#2 $
 * Last changelist: $Change: 263669 $
 * Last changed at: $DateTime: 2009/06/01 15:32:20 $
 * Last changed by: $Author: i047355 $
 */
package com.sap.engine.services.deployrepository;

import java.io.Serializable;
import com.sap.engine.services.deploy.container.ResourceReferenceType;
import com.sap.engine.services.deploy.container.util.PrintIt;

public class ResourceReference implements Serializable, PrintIt {
    private static final long serialVersionUID = 1L;
    private final String m_resRefName;
    private final String m_resRefType;
    private final String m_referenceType;
    private final ResourceReferenceType m_type;

    public ResourceReference(final String resRefName, final String resRefType, final String referenceType,
            final boolean isFunctional, final boolean isClassloading) {
        m_resRefName = resRefName;
        m_resRefType = resRefType;
        m_referenceType = referenceType;
        m_type = new ResourceReferenceType(isFunctional, isClassloading);
    }

    public String getReferenceType() {
        return m_referenceType;
    }

    public String getResRefName() {
        return m_resRefName;
    }

    public String getResRefType() {
        return m_resRefType;
    }

    public ResourceReferenceType getType() {
        return m_type;
    }

    private static int hashCodeOf(final Object obj) {
        if (null == obj) {
            return 0;
        }
        return obj.hashCode();    
      }
    private static boolean equalObjects(final Object s1, final Object s2) {
        if (s1 == null) {
            return s2 == null;
        }
        if (s2 == null) {
            return false;
        }
        return s1.equals(s2);
      }

    @Override
    public int hashCode() {
        final int offset = 17;
        final int multiplier = 59;
        int result = offset + hashCodeOf(m_resRefName);
        result = result * multiplier + hashCodeOf(m_resRefType);
        result = result * multiplier + hashCodeOf(m_referenceType);
        result = result * multiplier + hashCodeOf(m_type);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourceReference)) {
            return false;
        }
        final ResourceReference other = (ResourceReference) obj;
        if (!equalObjects(m_resRefName, other.m_resRefName)) {
            return false;
        }
        if (!equalObjects(m_resRefType, other.m_resRefType)) {
            return false;
        }
        if (!equalObjects(m_referenceType, other.m_referenceType)) {
            return false;
        }
        if (!equalObjects(m_type, other.m_type)) {
            return false;
        }
        return true;
    }

    public String print(final String shift) {
        return shift + m_referenceType + " to " + m_resRefType + ":" + m_resRefName + " (f="
                + m_type.isFunctional() + ", cl=" + m_type.isClassloading() + ")";
    }

    @Override
    public String toString() {
        return print("");
    }
}
