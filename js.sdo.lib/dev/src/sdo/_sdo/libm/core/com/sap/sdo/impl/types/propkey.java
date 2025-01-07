/**
 * 
 */
package com.sap.sdo.impl.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.builtin.PropertyType;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

public class PropKey implements Serializable {
    private static final long serialVersionUID = -6123588769089561292L;
    public static final URINamePair DUMMY_UNP = new URINamePair("", "");
    private final HelperContext helperContext;
    private String name;
    private List<String> alias;
    private String xmlName;
    private URINamePair type;
    private boolean many;
    private boolean containment;
    private boolean xmlElement;
    private boolean simpleContentProperty;
    private URINamePair xsdType;
    private URINamePair ref;
    private boolean manyUnknown;
    
    public PropKey(HelperContext pContext) {
        helperContext = pContext;
    }

	
	@Override
	public boolean equals(Object o) {
        if (o == this) {
        	return true;
        }
        if (o == null || !(o instanceof PropKey)) {
        	return false;
        }
        PropKey p = (PropKey)o;
        return p.name.equals(name) &&
               p.alias.equals(alias) &&
               p.xmlName.equals(xmlName) &&
        	   p.type.equals(type) &&
        	   p.many == many &&
        	   p.containment == containment &&
               p.xmlElement == xmlElement &&
               p.simpleContentProperty == simpleContentProperty &&
               p.xsdType.equals(xsdType) &&
               p.ref.equals(ref) &&
               p.manyUnknown == manyUnknown;
               
	}
	@Override
	public int hashCode() {
		return name.hashCode() + alias.hashCode() + xmlName.hashCode() 
            + type.hashCode() + (many?1:0) + (containment?2:0) + (xmlElement?4:0) 
            + (simpleContentProperty?8:0) + ref.hashCode() + xsdType.hashCode() + (manyUnknown?16:0);
		
	}
	public DataObject getPropertyDescription() {
		DataObject ret = helperContext.getDataFactory().create(PropertyType.getInstance());
		ret.setString(PropertyType.NAME, name);
		ret.setList(PropertyType.ALIAS_NAME, alias);
        if (!name.equals(xmlName)) {
            ret.setString(PropertyType.getXmlNameProperty(), xmlName);
        }
		ret.set(PropertyType.TYPE, helperContext.getTypeHelper().getType(type.getURI(),type.getName()));
		ret.setBoolean(PropertyType.MANY, many);
		ret.setBoolean(PropertyType.CONTAINMENT, containment);
        ret.setBoolean(PropertyType.getXmlElementProperty(), xmlElement);
        if (simpleContentProperty) {
            ret.setBoolean(PropertyType.getSimpleContentProperty(), true);
        }
        if (xsdType != DUMMY_UNP) {
            ret.setString(PropertyType.getXsdTypeProperty(), xsdType.toStandardSdoFormat());
        }
        if (ref != DUMMY_UNP) {
            ret.setString(PropertyType.getReferenceProperty(), ref.toStandardSdoFormat());
        }
        if (manyUnknown) {
            ret.setBoolean(PropertyType.getManyUnknownProperty(), true);
        }
		return ret;
	}
    public HelperContext getHelperContext() {
        return helperContext;
    }

    public void setName(String pName) {
        name = pName;
    }

    public void setAlias(List<String> pAlias) {
        int size = pAlias.size();
        if (size==0) {
            alias = Collections.emptyList();
        } else if (size==1){
            alias = Collections.singletonList(pAlias.get(0));
        } else {
            alias = new ArrayList<String>(pAlias);
        }
    }

    public void setXmlName(String pXmlName) {
        xmlName = pXmlName;
    }

    public void setType(URINamePair pType) {
        type = pType;
    }

    public void setMany(boolean pMany) {
        many = pMany;
    }

    public void setContainment(boolean pContainment) {
        containment = pContainment;
    }

    public void setXmlElement(boolean pXmlElement) {
        xmlElement = pXmlElement;
    }

    public void setSimpleContentProperty(boolean pSimpleContentProperty) {
        simpleContentProperty = pSimpleContentProperty;
    }

    public void setXsdType(URINamePair pXsdType) {
        xsdType = pXsdType;
    }

    public void setRef(URINamePair pRef) {
        ref = pRef;
    }

    public void setManyUnknown(boolean pManyUnknown) {
        manyUnknown = pManyUnknown;
    }
    
    
}