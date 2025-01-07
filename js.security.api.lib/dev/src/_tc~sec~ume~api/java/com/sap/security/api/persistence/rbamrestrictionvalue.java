package com.sap.security.api.persistence;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Serializer and deserializer class for the storing of restriction data
 * in UME user attributes. 
 * 
 * This class manages a single attribute value and offers services for 
 * the representation into a single string value. 
 * 
 * @author d026337
 */
public class RBAMRestrictionValue {

    /* User attributes in UME that store the data */
    public static String NAMESPACE_RBAM = "com.sap.security.rbam";
    public static String ATTNAME_VALUES = "rbamrestrictions";

    private String _roleId;
    private String _workCenterId;
    private String _accessContextCode;
    private String _accessGroupNodeId;
    private boolean _withHierarchy;

    /**
     * Default constructor fills data with default values.
     */
    public RBAMRestrictionValue(
        String roleId,
        String workCenterId,
        String accessContextCode,
        String accessGroupNodeId,
        boolean withHierarchy) throws IllegalArgumentException {

        if ((roleId == null)
            || (workCenterId == null)
            || (accessContextCode == null)
            || (accessGroupNodeId == null)) {
            throw new IllegalArgumentException(
                "Constructor of RBAMRestrictionValue called with null argument");
        }

        _roleId = roleId;
        _workCenterId = workCenterId;
        _accessContextCode = accessContextCode;
        _accessGroupNodeId = accessGroupNodeId;
        _withHierarchy = withHierarchy;

    } // constructor

    /**
     * Create object using serialized value. 
     * 
     * @param 
     *   serializedValue
     *     The single value as stored by a user. 
     */
    public RBAMRestrictionValue(String serializedValue)
        throws IllegalArgumentException {

        if (serializedValue == null) {
            throw new IllegalArgumentException(
                "Input value for deserialization is null");
        }

        Iterator<String> tokens = parseString(serializedValue).iterator();

        /* Role ID must be present */
        if (tokens.hasNext()) {
            _roleId = tokens.next();
        } else {
            throw new IllegalArgumentException("Field \"RoleID\" not present");
        }

        /* Work center ID must be present */
        if (tokens.hasNext()) {
            _workCenterId = tokens.next();
        } else {
            throw new IllegalArgumentException(
                "Field \"WorkCenterID\" not present");
        }

        /* Access Context Code must be present */
        if (tokens.hasNext()) {
            _accessContextCode = tokens.next();
        } else {
            throw new IllegalArgumentException(
                "Field \"AccessContextCode\" not present");
        }

        /* Access Group Node ID must be present */
        if (tokens.hasNext()) {
            _accessGroupNodeId = tokens.next();
        } else {
            throw new IllegalArgumentException(
                "Field \"AccessContextNodeId\" not present");
        }

        /* Evaluation path. If present and X, set to true */
        if (tokens.hasNext()) {
            if (tokens.next().trim().length() > 0) {
                _withHierarchy = true;
            } else {
                _withHierarchy = false;
            }
        } else {
            _withHierarchy = false;
        }

    }

    /**
     * Return serialized representation of the object. 
     * 
     * @return
     *   Value to be stored at user in UME.  
     */
    public String getSerializedValue() {

        StringBuffer sb = new StringBuffer();

        sb.append(escapeRestrictionPart(_roleId));
        sb.append('#');
        sb.append(escapeRestrictionPart(_workCenterId));
        sb.append('#');
        sb.append(escapeRestrictionPart(_accessContextCode));
        sb.append('#');
        sb.append(escapeRestrictionPart(_accessGroupNodeId));
        sb.append('#');
        sb.append(_withHierarchy ? "X" : "");

        return sb.toString();

    } // getSerializedValue

    /**
     * Split string into tokens according to the escape rules.
     * 
     * @param 
     *   in
     *     Input string. 
     * 
     * @return
     *   List of String objects with the split and de-escaped tokens
     */
    private static List<String> parseString(String in) {

        List<String> result = new LinkedList<String>();
        boolean escapeMode = false;
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < in.length(); i++) {

            char c = in.charAt(i);

            if (escapeMode == false) {
                if (c == '#') {
                    result.add(buf.toString());
                    buf.replace(0, buf.length(), "");
                } else if (c == '\\') {
                    escapeMode = true;
                } else {
                    buf.append(c);
                }
            } else {
                buf.append(c);
                escapeMode = false;
            }

        }

        /* If in escape mode at the end of string, the last char was "\" */
        if (escapeMode) {
            buf.append('\\');
        }

        /* If content in buffer, append as last item */
        if (buf.length() > 0) {
            result.add(buf.toString());
        }

        return result;

    } // parseString

    /**
     * Escaping of org restriction component. 
     * 
     * Rules:
     *   \ --> \\
     *   # --> \#
     *   
     * Example:
     *   WC#1  --> WC\#1
     *   WC\1  --> WC\\1
     *   WC\#1 --> WC\\\#1 
     * 
     * @param 
     *   in
     *     String
     * 
     * @return
     *   Escaped String
     */
    private static String escapeRestrictionPart(String in) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '#') {
                sb.append("\\#");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    } // escapeRestrictionPart

    /**
     * String representation for debugger.
     */
    public String toString() {
        return "[RoleID="
            + _roleId
            + ", WorkCenterID="
            + _workCenterId
            + ", AccessContextCode="
            + _accessContextCode
            + ", AccessGroupNodeId="
            + _accessGroupNodeId
            + ", WithHierarchy="
            + _withHierarchy
            + "]";
    } //toString

    /* Getters and setters */
    public String getRoleId() {
        return _roleId;
    }

    public String getWorkCenterId() {
        return _workCenterId;
    }

    public String getAccessContextCode() {
        return _accessContextCode;
    }

    public String getAccessGroupNodeId() {
        return _accessGroupNodeId;
    }

    public boolean isWithHierarchy() {
        return _withHierarchy;
    }

} // class RBAMRestrictionValue
