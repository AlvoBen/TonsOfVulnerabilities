package com.sap.sdo.impl.xml;

public class SchemaLocation {
    
    private final String _nameSpace;
    private final String _absoluteSchemaLocation;
    
    public SchemaLocation(final String pNameSpace, final String pAbsoluteSchemaLocation) {
        super();
        _nameSpace = (pNameSpace == null) ? "": pNameSpace;
        _absoluteSchemaLocation = pAbsoluteSchemaLocation;
    }

    /**
     * @return The absolute schema location or null.
     */
    public String getAbsoluteSchemaLocation() {
        return _absoluteSchemaLocation;
    }

    /**
     * @return The namespace, never null, rather "".
     */
    public String getNameSpace() {
        return _nameSpace;
    }

    @Override
    public boolean equals(Object pObj) {
        if (this == pObj) {
            return true;
        }
        if (!(pObj instanceof SchemaLocation)) {
            return false;
        }
        if (_absoluteSchemaLocation == null) {
            return false;
        }
        SchemaLocation other = (SchemaLocation)pObj;
        return _nameSpace.equals(other.getNameSpace()) && _absoluteSchemaLocation.equals(other.getAbsoluteSchemaLocation());
    }

    @Override
    public int hashCode() {
        if (_absoluteSchemaLocation == null) {
            return super.hashCode();
        }
        return _nameSpace.hashCode() ^ _absoluteSchemaLocation.hashCode();
    }

    @Override
    public String toString() {
        return _nameSpace + " -> " + _absoluteSchemaLocation;
    }
    
    

}
