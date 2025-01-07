/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.objects.path;

import java.util.List;
import java.util.regex.Pattern;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;

import commonj.sdo.DataObject;

/**
 * Abstract implementation to resolve namespace independent parts of xpath definitions.
 * 
 * @author D042774
 *
 */
public abstract class AbstractPathResolver implements IPathResolver {
    private static final Pattern NC_NAME  = Pattern.compile(
        "^[\\p{Alpha}_"
        + "\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD\\u10000-\\uEFFFF"
        + "]"
        + "[\\p{Alnum}\\-_\\.\\\\"
        + "\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD\\u10000-\\uEFFFF"
        + "\\xB7\\u0300-\\u036F\\u203F-\\u2040"
        + "]*$");

    private int _p; // _root position
    private GenericDataObject _root = null;
    private PropertyData _propData;

    protected String _path;
    protected int _currentpathIndex;

    
    /**
     * Contructor for a path resolver instance.
     * 
     * @param pRoot root element to use as starting point of path navigation.
     * @param path a path to resolve.
     * @param p points to path after namespace and root declaration.
     */
    public AbstractPathResolver(GenericDataObject root, String path, int p) {
        _root = root;
        _path = path;
        _p = p;
        _currentpathIndex = p;
        
        _propData = resolveSubPath(new PropertyData(_root), _path, _p);
    }

    /**
     * Container of addressed property of path.
     */
    public GenericDataObject getDataObject() {
        return _propData.getContainer();
    }

    /**
     * PropValue of addressed property of path.
     */
    public PropValue<?> getPropValue() {
        return _propData.getPropValue();
    }

    /**
     * Index of addressed property.
     * -1 if property is single valued.
     */
    public int getIndex() {
        return _propData.getIndex();
    }

    /**
     * True if property is single valued.
     */
    public boolean isPlain() {
        return _propData.getIndex() < 0;
    }

    /**
     * Extracts property data from given data object.
     * 
     * @param step name of property including index or attribute value information.
     * @param dataObject data object where property should extracted from.
     * @return property data that represents a single step from path.
     */
    protected abstract PropertyData extractPropertyData(String step, GenericDataObject dataObject);

    /**
     * Iterates over data graph along an xpath expression.
     * 
     * @param propData starting point of iteration.
     * @param path xpath expression.
     * @param startIndex TODO
     * @return result of iteration as property data.
     */
    private PropertyData resolveSubPath(PropertyData propData, String path, int startIndex) {
        int index = getIndexOfNonEscapedChar(path, startIndex, '/');
        if (index > 0) {
            PropertyData parent = resolvePropValue(propData, path.substring(startIndex, index));
            _currentpathIndex =  index + 1;
            return resolveSubPath(parent, path, index + 1);
        } else {
            try {
                PropertyData child = resolvePropValue(propData, path.substring(startIndex));
                _currentpathIndex = path.length();
                return child;
            } catch (PropertyNotDefinedException e) {
                e.setEndOfPath(true);
                throw e;
            }
        }
    }
    
    /**
     * Single iteration step to resolve property and value.
     * 
     * @param parent result of last step of iteration.
     * @param step current step of iteration.
     * @return property data that represents this step of iteration.
     */
    private PropertyData resolvePropValue(PropertyData parent, String step) {
        // reference containing DataObject
        if ("..".equals(step)) {
            return new PropertyData(parent.getContainer());
        }
        
        final PropertyData propData = extractPropertyData(step, parent.getDataObject());
        return propData;
    }

    /**
     * Get index of an character that is not escaped.
     * 
     * @param path String where character is searched in.
     * @param startIndex TODO
     * @param c character.
     * @return index of first non-escaped occurrence of character in path, otherwise -1.
     */
    protected int getIndexOfNonEscapedChar(final String path, int startIndex, final char c) {
        int s = path.indexOf(c, startIndex);
        if (s > 0 && path.charAt(s - 1) == '\\') {
            int subIndex = getIndexOfNonEscapedChar(path, s +1, c);
            if (subIndex < 0) {
                return subIndex;
            }
            s += subIndex;
            // add 1 because of zero based index
            ++s;
        }
        return s;
    }

    /**
     * Get index of an character that is not escaped.
     * 
     * @param path String where character is searched in.
     * @param c character.
     * @return index of last non-escaped occurrence of character in path, otherwise -1.
     */
    protected int getLastIndexOfNonEscapedChar(final String path, final char c) {
        int s = path.lastIndexOf(c);
        if (s > 0 && path.charAt(s - 1) == '\\') {
            return getLastIndexOfNonEscapedChar(path.substring(0, s), c);
        }
        return s;
    }

    /**
     * Resolve single step of path iteration that contains a pair of non-escaped '[ ]'.
     * 
     * @param step single step of iteration contains '[' and ']'.
     * @param dataObject parent of this iteration step.
     * @param bracket index of opening bracket.
     * @return property data that represents this step of iteration.
     */
    protected PropertyData matchPathWithBrackets(String step, GenericDataObject dataObject, int bracket) {
        if (!step.endsWith("]")) {
            throw new IllegalArgumentException(
                "missing ] at end of \""+step+"\" in \""+_path+"\"");
        }
    
        String property = step.substring(0, bracket);
        String ixRef = step.substring(bracket + 1, step.length() - 1);
        
        int equal = getIndexOfNonEscapedChar(ixRef, 0, '=');
        if (equal > 0) {
            final Object value;
            String v = ixRef.substring(equal + 1).trim();
            if ((v.charAt(0)=='\'') && (v.endsWith("'"))
                || (v.charAt(0)=='"') && (v.endsWith("\""))) {
    
                value = v.substring(1,v.length()-1);
            } else {
                value = v;
            }
            return new PropertyData(property, ixRef.substring(0, equal).trim(), value, _path, dataObject);
        } else {
            try {
                return new PropertyData(property, Integer.parseInt(ixRef) - 1, _path, dataObject);
            } catch (NumberFormatException nfe) { //$JL-EXC$
                throw new IllegalArgumentException(
                    "bad index \""+ixRef+"\" at "+(_currentpathIndex+bracket+1)+" in \""+_path+"\"");
            }
        }
    }

    /**
     * Class to collect data of property and value of iteration step.
     * 
     * @author D042774
     *
     */
    protected static class PropertyData {
        private static enum ExpressionType { PLAIN, INDEX, ATTRIBUTE };
        
        private String _name = null;
        private String _path = null;
    
        private GenericDataObject _container = null;
        private PropValue<?> _propValue = null;
        private Object _result = null;
        private String _attribute = null;
        private Object _value = null;
        private int _index = -1;
        private ExpressionType _expressionType = null;
    
        /**
         * Constructor with property name, path and data object containing this property.
         * @param name name of property.
         * @param path complete xpath expression.
         * @param dataObject data object containing property.
         */
        public PropertyData(String name, String path, GenericDataObject dataObject) {
            // check syntax
//            if (!isValidIdentifierName(name)) {
//                throw new IllegalArgumentException("invalid property name \""+name+"\" in \""+path+"\"");
//            }
    
            _expressionType = ExpressionType.PLAIN;
            _name = name;
            _path = path;
            _container = dataObject;
            try {
                _propValue = dataObject.getPropValueByPropNameOrAlias(normalizeString(name));
            } catch (IllegalArgumentException ex) {
                String msg = "invalid property name \""+name+"\" in \""+path+"\".\n" + ex.getMessage();
                throw new PropertyNotDefinedException(dataObject, name, ex, msg);
            }
        }

        /**
         * Constructor with property name, path and data object containing this property.
         * 
         * @param name name of multi-valued property.
         * @param index index of property value.
         * @param path complete xpath expression.
         * @param dataObject data object containing property.
         */
        public PropertyData(String name, int index, String path, GenericDataObject dataObject) {
            this(name, path, dataObject);
    
            // check index
            if (_propValue.isMany()) {
                _index = index;
                _expressionType = ExpressionType.INDEX;
            } else {
                if (index > 0) {
                    throw new IllegalStateException("property \""+name+"\" in \""+path+"\" is not multi-value");
                }
            }
        }
    
        /**
         * Constructor with property name, path, attribute with value and data object containing this property.
         * 
         * @param name name of property.
         * @param attribute name of attribute of property. 
         * @param value value of attribute.
         * @param path complete xpath expression.
         * @param dataObject data object containing property.
         */
        public PropertyData(String name, String attribute, Object value, String path, GenericDataObject dataObject) {
            this(name, path, dataObject);
    
            // check attribute
//            if (!isValidIdentifierName(attribute)) {
//                throw new IllegalArgumentException("invalid attribute name \""+attribute+"\" in \""+path+"\"");
//            }
    
            _attribute = attribute;
            _value = value;
            _expressionType = ExpressionType.ATTRIBUTE;
        }
        
        /**
         * Constructor as wrapper of a data object.
         * 
         * @param dataObject data object to present as property data.
         */
        public PropertyData(GenericDataObject dataObject) {
            _result = dataObject;
            _propValue = dataObject.getContainmentPropValue();
            if (_propValue != null) {
                _container = _propValue.getDataObject();
            }
        }
        
        /**
         * Checks if property name is valid.
         * Checks against NCName definition of W3C @link{http://www.w3.org/TR/xml-names11/#NT-NCName}.
         * 
         * @param propName name of property
         * @return true is valid NCName.
         */
        private boolean isValidIdentifierName(String propName) {
            // TODO THIS IS TOO EXPENSIVE
            return NC_NAME.matcher(propName).find();
        }
        
        /**
         * Removed all non-escaped occurrences of '\' from string.
         * 
         * @param name string to normalize.
         * @return normalized non-escaped name.
         */
        private String normalizeString(String name) {
            int index = name.indexOf('\\');
            if (index >= 0) {
                final StringBuilder normalized = new StringBuilder(name.length() -1);
                normalized.append(name.substring(0, index));
                int endIndex;
                do {
                    endIndex = name.indexOf('\\', index + 2);
                    if (endIndex == -1) {
                        endIndex = name.length();
                    }
                    normalized.append(name.substring(index + 1, endIndex));
                    index = endIndex;
                } while (endIndex != name.length());
                return normalized.toString();
            }
            return name;
        }
        
        /**
         * Initialize this instance of PropValue.
         * Calculate internal fields.
         *
         */
        private void init() {
            switch (_expressionType) {
                case INDEX:
                    List values = (List)_propValue.getValue();
                    if (values == null || _index >= values.size()) {
                        throw new IndexOutOfBoundsException(
                            "property \""+_name+"\" in \""+_path+"\" has no element of index "+_index);
                    }
                    _result = values.get(_index);
                    break;
                case ATTRIBUTE:
                    _index = _propValue.getIndexByPropertyValue(_attribute, _value);
                    if (_index < 0) {
                        throw new IllegalArgumentException(
                            "property \""+_name+"\" in \""+_path+"\" has no element with "+_attribute+" = "+_value);
                    } else {
                        _result = _propValue.getConvertedValue(_index, DataObject.class);
                    }
                    break;
                case PLAIN:
                    // don't break
                default:
                    _result = _propValue.getValue();
                    break;
            }
        }
    
        /**
         * Get PropValue of resolved path step.
         * @return PropValue
         */
        public PropValue<?> getPropValue() {
            if (_result == null) {
                init();
            }
            return _propValue;
        }
        
        /**
         * Get index of resolved path step.
         * @return index or -1 if property is single valued.
         */
        public int getIndex() {
            if (_result == null) {
                init();
            } else if (_index < 0 && _propValue.isMany() && _propValue != _result) {
                _index = ((List)_propValue.getValue()).indexOf(_result);
            }
            return _index;
        }
        
        /**
         * Get resolved path step as DataObject.
         * @return DataObject or null if result isn't a DataObject.
         */
        public GenericDataObject getDataObject() {
            if (_result == null) {
                init();
            }
            if (_result instanceof DataObjectDecorator) {
                return ((DataObjectDecorator)_result).getInstance();
            }
            return null;
        }
    
        /**
         * Get container of resolved path step.
         * @return DataObject
         */
        public GenericDataObject getContainer() {
            if (_result == null) {
                init();
            }
            return _container;
        }
    }
    
    /**
     * Exception to indicate an failure during step of path iteration.
     * 
     * @author D042774
     *
     */
    public static class PropertyNotDefinedException extends IllegalArgumentException {
        private static final long serialVersionUID = 7379281023334039489L;

        private DataObject _dataObject;
        private String _propertyName;
        private boolean _isEndOfPath = false;
        
        /**
         * Constructor with information about failed path step.
         * @param pDataObject data object to find property at.
         * @param pPropertyName name of property to resolve.
         * @param pCause nested exception if available.
         * @param pMsg error message.
         */
        public PropertyNotDefinedException(DataObject pDataObject, String pPropertyName, Throwable pCause, String pMsg) {
            super(pMsg, pCause);
             _dataObject = pDataObject;
            _propertyName = pPropertyName;
        }

        /**
         * Data object where error occurred.
         * @return DataObject
         */
        public DataObject getDataObject() {
            return _dataObject;
        }

        /**
         * property name where error occurred.
         * @return nae of property
         */
        public String getPropertyName() {
            return _propertyName;
        }

        /**
         * Indicates if error occurred at end of path.
         * @return true if last path step failed, otherwise false.
         */
        public boolean isEndOfPath() {
            return _isEndOfPath;
        }

        /**
         * Set if end of path was reached.
         * @param pIsEndOfPath
         */
        public void setEndOfPath(boolean pIsEndOfPath) {
            _isEndOfPath = pIsEndOfPath;
        }
        
    }

}
