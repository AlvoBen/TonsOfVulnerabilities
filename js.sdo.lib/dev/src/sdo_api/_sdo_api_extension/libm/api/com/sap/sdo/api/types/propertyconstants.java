package com.sap.sdo.api.types;

import com.sap.sdo.api.util.URINamePair;

public interface PropertyConstants {

    public static final int ALIAS_NAME = 0;
    public static final String ALIAS_NAME_STR = "aliasName";

    public static final int NAME = 1;
    public static final String NAME_STR = "name";

    public static final int MANY = 2;
    public static final String MANY_STR = "many";

    public static final int CONTAINMENT = 3;
    public static final String CONTAINMENT_STR = "containment";

    public static final int DEFAULT = 4;
    public static final String DEFAULT_STR = "default";

    public static final int READ_ONLY = 5;
    public static final String READ_ONLY_STR = "readOnly";

    public static final int TYPE = 6;
    public static final String TYPE_STR = "type";

    public static final int OPPOSITE = 7;
    public static final String OPPOSITE_STR = "opposite";

    public static final int NULLABLE = 8;
    public static final String NULLABLE_STR = "nullable";

    public static final int CONTAINING_TYPE = 9;
    public static final String CONTAINING_TYPE_STR = "containingType";

    public static final int KEY = 10;
    public static final String KEY_STR = "key";

    /** SAP specific extension. The value behind this constant might be changed
     *  in future SDO versions. */ 
    public static final int OPPOSITE_INTERNAL = 11;

    /** SAP specific extension. The value behind this constant might be changed
     *  in future SDO versions. */ 
    public static final int JAVA_CLASS = 12;
    public static final String JAVA_CLASS_STR = "javaClass";

	public static final String XML_ELEMENT = URINamePair.PROP_XML_XML_ELEMENT.getName();

	public static final String JAVA_NAME = "javaName";

	public static final String XSD_TYPE = URINamePair.PROP_XML_XSD_TYPE.getName();

	public static final String XML_NAME = URINamePair.PROP_XML_XML_NAME.getName();

    public static final String URI = URINamePair.PROP_XML_URI.getName();;

    public static final String XML_SIMPLE_CONTENT = URINamePair.PROP_XML_SIMPLE_CONTENT.getName();;

	public static final String SUBSTITUTES = "substitutes";

	public static final String REF = "ref";

	public static final String FORM_QUALIFIED = "form";

	public static final String IS_FIELD = "isField";

	public static final String PROJECTION = "projection";

}