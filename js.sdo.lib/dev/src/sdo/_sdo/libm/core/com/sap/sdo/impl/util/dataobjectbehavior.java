package com.sap.sdo.impl.util;

import static com.sap.sdo.api.util.URINamePair.SCHEMA_BASE64BINARY;
import static com.sap.sdo.api.util.URINamePair.SCHEMA_Q_NAME;

import java.util.List;
import java.util.Set;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.PropertyType;
import com.sap.sdo.impl.types.builtin.TypeType;
import com.sap.sdo.impl.types.schema.SchemaTypeFactory;
import com.sap.sdo.impl.types.simple.JavaSimpleType;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public final class DataObjectBehavior {

    private DataObjectBehavior() {
    }

    public static Object getKey(DataObject pDataObject) {
        SdoType type = (SdoType)pDataObject.getType();
        SdoType keyType = (SdoType)type.getKeyType();
        if (keyType == null) {
            return null;
        }
        List<SdoProperty> keyProperties = type.getKeyProperties();
        if (keyProperties.size() == 1) {
            SdoProperty keyProperty = keyProperties.get(0);
            SdoType keyPropertyType = (SdoType)keyProperty.getType();
            if (keyType == keyPropertyType) {
                if (keyType.isDataType()) {
                    return pDataObject.getString(keyProperty);
                } else {
                    return getDataObjectFormProp(pDataObject, keyProperty);
                }
            }
            if (keyType == keyPropertyType.getKeyType()) {
                DataObjectDecorator dataObject = getDataObjectFormProp(pDataObject, keyProperty);
                if (dataObject == null) {
                    return null;
                }
                return getKey(dataObject);
            }
        }
        DataObject key = getHelperContext(pDataObject).getDataFactory().create(keyType);
        for (int i = 0; i < keyProperties.size(); i++) {
            SdoProperty keyProperty = keyProperties.get(i);
            Property keyTypeProperty = keyType.getPropertyFromXmlName(
                keyProperty.getUri(), keyProperty.getName(), keyProperty.isXmlElement());
            if (keyTypeProperty == null && keyProperty.isOppositeContainment()) {
                keyTypeProperty = keyType.getPropertyFromXmlName(
                    keyProperty.getUri(), keyProperty.getName(), !keyProperty.isXmlElement());
            }
            if (keyTypeProperty == null) {
                throw new IllegalArgumentException("Property " + keyProperty.getName()
                    + " not found on key type " + keyType);
            }
            SdoType keyPropertyType = (SdoType)keyProperty.getType();
            Type keyTypePropertyType = keyTypeProperty.getType();
            if (keyPropertyType == keyTypePropertyType) {
                Object keyValue = pDataObject.get(keyProperty);
                if (keyValue == null) {
                    return null;
                }
                key.set(keyTypeProperty, keyValue);
            } else {
                Type keyPropertyKeyType = keyPropertyType.getKeyType();
                if (keyPropertyKeyType == keyTypePropertyType) {
                    DataObjectDecorator dataObject = getDataObjectFormProp(pDataObject, keyProperty);
                    if (dataObject == null) {
                        return null;
                    }
                    key.set(keyTypeProperty, getKey(dataObject));
                }
            }
        }
        return key;
    }

    private static DataObjectDecorator getDataObjectFormProp(DataObject pDataObject, SdoProperty pProperty) {
        DataObject result = pDataObject.getDataObject(pProperty);
        if (result == null && pProperty.isOppositeContainment()) {
            //fallback for unfinished DataObjects
            result = pDataObject.getContainer();
        }
        return (DataObjectDecorator)result;
    }

    public static URINamePair getXsiTypeUnpForElement(
        GenericDataObject gdo,
        SdoType<?> type,
        SdoProperty pProp,
        SdoProperty valueProp,
        boolean isRootObject) {

        if (!URINamePair.MIXEDTEXT_TYPE.equalsUriName(type)) {
            Type propType = type;
            boolean isWrapper = valueProp != null && URINamePair.OPEN.equalsUriName(type);
            if (isWrapper) {
                propType = valueProp.getType();
            }
            if (pProp == null
                    || (propType != null && pProp.getType() != propType && ((SdoType)pProp.getType()).getKeyType() != propType)
                    || (pProp.isOpenContent() && isRootObject && pProp.getContainingType() == null)) {
                return getXsiTypeUnp(gdo, pProp, (isWrapper ? valueProp : null));
            }
        } else if (pProp != null && pProp.get(PropertyType.getXsdTypeProperty()) != null) {
            return getXsiTypeUnp(gdo, pProp, valueProp);
        }
        return null;
    }

    public static URINamePair getXsiTypeUnp(DataObject pDataObject, SdoProperty pProperty, SdoProperty pValueProp) {
        if (pValueProp != null && pDataObject.getInstanceProperties().size() > 1) {
            URINamePair unp = null;
            if (pProperty != null) {
                unp = pProperty.getXsdType();
            }
            SdoType<?> xsdType = null;
            if (unp != null) {
                TypeHelperImpl typeHelper =
                    (TypeHelperImpl)((DataObjectDecorator)pDataObject).getInstance().getHelperContext().getTypeHelper();
                xsdType = (SdoType<?>)typeHelper.getTypeByXmlName(unp.getURI(), unp.getName());
            }
            if (unp == null || (xsdType != null && (xsdType.isDataType() || xsdType.isLocal()))) {
                return null;
            }
            return unp;
        } else if (pValueProp != null) {
            // if not null found wrapper object with one property named "value"
            URINamePair unp = pValueProp.getXsdType();
            if (unp == null && pProperty != null) {
                unp = pProperty.getXsdType();
            }
            if (unp == null) {
                SdoType<?> type = (SdoType<?>)pValueProp.getType();
                if (type.isLocal()) {
                    return null;
                }
                unp = (type).getQName();
                URINamePair xUnp = SchemaTypeFactory.getInstance().getXsdName(unp);
                if (xUnp != null) {
                    unp = xUnp;
                }
            }
            return unp;
        } else {
            URINamePair xUnp = null;
            if (pProperty != null && pProperty.getType().isDataType()) {
                SdoType<?> type = (SdoType<?>)pProperty.getType();
                if (type.isLocal()) {
                    return null;
                }
                xUnp = SchemaTypeFactory.getInstance().getXsdName(type.getQName());
            }
            if (xUnp != null) {
                return xUnp;
            } else {
                if (pDataObject != null) {
                    SdoType<?> type = (SdoType<?>)pDataObject.getType();
                    if (type.isLocal()) {
                        return null;
                    }
                    return new URINamePair(type.getXmlUri(), type.getXmlName());
                } else {
                    SdoType<?> type = (SdoType<?>)pProperty.getType();
                    if (type.isLocal()) {
                        return null;
                    }
                    xUnp = SchemaTypeFactory.getInstance().getXsdName(type.getQName());
                    if (xUnp != null) {
                        return xUnp;
                    } else {
                        return new URINamePair(type.getXmlUri(), type.getXmlName());
                    }
                }
            }
        }
    }

    public static void addUsedTypes(final DataObject dataObject, final Set<Type> types, final String namespace) {
        addType(types, dataObject.getType(), namespace);
        List<Property> props = dataObject.getInstanceProperties();
        int size = props.size();
        for (int i=0; i<size; ++i) {
            final Property prop = props.get(i);
            final Type propType = prop.getType();
            addType(types, propType, namespace);
            if (!propType.isDataType() && dataObject.isSet(prop) && prop.isContainment()) {
                final Object value = dataObject.get(prop);
                if (value instanceof DataObjectDecorator) {
                    addUsedTypes(((DataObjectDecorator)value).getInstance(), types, namespace);
                } else if (value instanceof List) {
                    List<DataObjectDecorator> valueList = (List<DataObjectDecorator>)value;
                    for (int j=0; j<valueList.size(); ++j) {
                        addUsedTypes(valueList.get(j).getInstance(), types, namespace);
                    }
                }
            }
        }
    }

    private static void addType(final Set<Type> types, final Type type, final String namespace) {
        if (namespace.equals(((SdoType<?>)type).getXmlUri())) {
            types.add(type);
        }
    }

    public static HelperContext getHelperContext(DataObject pDataObject) {
        return ((DataObjectDecorator)pDataObject).getInstance().getHelperContext();
    }

    public static int hashCode(DataObject pDataObject) {
        int hash = 0;
        List<Property> properties = pDataObject.getInstanceProperties();
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(0);
            if (property.getType().isDataType()) {
                Object value = pDataObject.get(property);
                int propHash = value==null?0:value.hashCode();
                hash = hash * 37 + propHash;
            }
        }
        return hash;
    }

    public static boolean isOrphan(DataObject pDataObject, ChangeSummary pChangeSummary) {
        if (pDataObject == null || pChangeSummary == null) {
            return false;
        }
        return !(pDataObject.getChangeSummary() == pChangeSummary);
    }

    /**
     * @param pXmlStaxWriter
     * @param prop
     * @return
     */
    public static boolean isQnameProperty(SdoProperty prop) {
        return SCHEMA_Q_NAME.equals(prop.getXsdType())
        || SCHEMA_Q_NAME.toStandardSdoFormat().equals(
            prop.getType().get(TypeType.getSpecialBaseTypeProperty()));
    }

    /**
     * @param pXmlStaxWriter
     * @param pProperty
     * @param pValue
     * @return
     */
    public static String encodeBase64Binary(SdoProperty pProperty, String pValue) {
        if (isBase64BinaryProperty(pProperty)) {
            return Base64Util.encodeBase64(
                JavaSimpleType.BYTES.convertFromJavaClass(pValue));
        }
        return pValue;
    }

    public static boolean isBase64BinaryProperty(SdoProperty pProperty) {
        return SCHEMA_BASE64BINARY.equals(pProperty.getXsdType())
                || SCHEMA_BASE64BINARY.toStandardSdoFormat().equals(
                    pProperty.getType().get(TypeType.getSpecialBaseTypeProperty()));
    }
}
