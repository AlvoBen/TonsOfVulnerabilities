package com.sap.sdo.impl.xml;

import javax.xml.namespace.NamespaceContext;

import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.DataObjectType;

public abstract class Reference {
    
    public static Reference createReference(PathReference pReference, SdoType pType, String pRefString, NamespaceContext pNamespaceContext) {
        if (pRefString == null) {
            return null;
        }
        String ref = pRefString.trim();
        if (ref.length() == 0) {
            return null;
        }
        if (ref.charAt(0) == '#') {
            return new PathReference(pReference, ref, pNamespaceContext);
        }
        SdoType typeForKeyUniqueness = null;
        if (pType != null) {
            typeForKeyUniqueness = pType.getTypeForKeyUniqueness();
        }
        if (typeForKeyUniqueness == null) {
            typeForKeyUniqueness = DataObjectType.getInstance();
        }
        return new Key(typeForKeyUniqueness, ref);
    }
    
    public abstract boolean isKeyReference();

}
