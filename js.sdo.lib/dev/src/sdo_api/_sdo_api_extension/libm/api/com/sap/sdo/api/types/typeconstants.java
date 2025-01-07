package com.sap.sdo.api.types;

import com.sap.sdo.api.util.URINamePair;

public interface TypeConstants {

	public static final String ABSTRACT_STR = "abstract";

	public static final int ABSTRACT = 8;

	public static final String SEQUENCED_STR = "sequenced";

	public static final int SEQUENCED = 7;

	public static final String OPEN_STR = "open";

	public static final int OPEN = 6;

	public static final String DATA_TYPE_STR = "dataType";

	public static final int DATA_TYPE = 5;

	public static final String URI_STR = "uri";

	public static final int URI = 4;

	public static final String NAME_STR = "name";

	public static final int NAME = 3;

	public static final String ALIAS_NAME_STR = "aliasName";

	public static final int ALIAS_NAME = 2;

	public static final String PROPERTY_STR = "property";

	public static final int PROPERTY = 1;

	public static final String BASE_TYPE_STR = "baseType";

	public static final int BASE_TYPE = 0;

    public static final String KEY_TYPE_STR = "keyType";

    public static final int KEY_TYPE = 9;

    public static final String JAVA_CLASS = "javaClass";

	public static final String MIXED = URINamePair.PROP_XML_MIXED.getName();;

	public static final String XML_NAME = URINamePair.PROP_XML_XML_NAME.getName();;

	public static final String VALUE = "value";

	public static final String SPECIAL_BASE_TYPE = "specialBaseType";

	public static final String SCHEMA_REFERENCE = URINamePair.PROP_CTX_SCHEMA_REFERENCE.getName();

	public static final String PACKAGE = "package";

	public static final String FACETS = URINamePair.PROP_CTX_FACETS.getName();

	public static final String FACET_MININCLUSIVE = "minInclusive";

	public static final String FACET_MAXINCLUSIVE = "maxInclusive";

	public static final String FACET_MINEXCLUSIVE = "minExclusive";

	public static final String FACET_MAXEXCLUSIVE = "maxExclusive";

	public static final String FACET_ENUMERATION = "enumeration";

	public static final String FACET_LENGTH = "length";

	public static final String FACET_MINLENGTH = "minLength";

	public static final String FACET_MAXLENGTH = "maxLength";

	public static final String FACET_PATTERN = "pattern";

	public static final String FACET_TOTALDIGITS = "totalDigits";

	public static final String FACET_FRACTIONDIGITS = "fractionDigits";

	public static final String ELEMENT_FORM_DEFAULT = URINamePair.PROP_XML_ELEMENT_FORM_DEFAULT.getName();;

	public static final String ATTRIBUTE_FORM_DEFAULT = URINamePair.PROP_XML_ATTRIBUTE_FORM_DEFAULT.getName();

	public static final String SUGGESTED_JAVA_NAME = "suggestedNameForJavaInstanceClass";

}