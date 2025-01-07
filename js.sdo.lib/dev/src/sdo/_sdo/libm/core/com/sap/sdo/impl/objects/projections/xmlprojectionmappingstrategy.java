package com.sap.sdo.impl.objects.projections;

import java.util.List;

import com.sap.sdo.api.types.SapProperty;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class XmlProjectionMappingStrategy extends AbstractProjectionMappingStrategy {
	private XmlProjectionMappingStrategy() {
	}
	private static final XmlProjectionMappingStrategy _me = new XmlProjectionMappingStrategy();
	public static XmlProjectionMappingStrategy getInstance() {
		return _me;
	}
	@Override
	protected Property getProperty(Type main, Property pMine) {
		SdoProperty mine = (SdoProperty)pMine;
		return ((SdoType)main).getPropertyFromXmlName(mine.getUri(), 
				mine.getXmlName(), mine.isXmlElement());
	}
    
    @Override
    public Property getOpenProperty(HelperContext pMainContext, Property pMine) {
        final HelperContext mineContext = ((SapProperty)pMine).getHelperContext();
        if (mineContext != null) {
            XSDHelper mineXsdHelper = mineContext.getXSDHelper();
            String uri = mineXsdHelper.getNamespaceURI(pMine);
            if (uri != null) {
                String name = mineXsdHelper.getLocalName(pMine);
                boolean element = mineXsdHelper.isElement(pMine);
                XSDHelper mainXsdHelper = pMainContext.getXSDHelper();
                return mainXsdHelper.getGlobalProperty(uri, name, element);
            }
        }
        TypeHelper mainTypeHelper = pMainContext.getTypeHelper();
        DataObject mainProp = pMainContext.getDataFactory().create(Property.class);
        DataObject mineProp = (DataObject)pMine;
        for (Property propProp: (List<Property>)mineProp.getInstanceProperties()) {
            Object value = mineProp.get(propProp);
            if (URINamePair.TYPE.equalsUriName(propProp.getType()) && value != null) {
                Type mineType = (Type)value;
                value = mainTypeHelper.getType(mineType.getURI(), mineType.getName());
                if (value == null) {
                    throw new IllegalArgumentException("Type " + URINamePair.fromType(mineType) 
                        + " is unknown in HelperContext " + pMainContext);
                }
            }
            mainProp.set(propProp, value);
        }
        return mainTypeHelper.defineOpenContentProperty(null, mainProp);
    }


}
