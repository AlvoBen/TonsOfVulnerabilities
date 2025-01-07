package com.sap.sdo.impl.objects.projections;

import java.util.List;

import com.sap.sdo.api.types.SapProperty;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class DefaultProjectionMappingStrategy extends AbstractProjectionMappingStrategy {
	private DefaultProjectionMappingStrategy() {
	}
	private static final DefaultProjectionMappingStrategy _me = new DefaultProjectionMappingStrategy();
	public static DefaultProjectionMappingStrategy getInstance() {
		return _me;
	}
	@Override
	protected Property getProperty(Type main, Property mine) {
		return main.getProperty(mine.getName());
	}
    @Override
    public Property getOpenProperty(HelperContext pMainContext, Property pMine) {
        TypeHelper mainTypeHelper = pMainContext.getTypeHelper();
        final HelperContext mineContext = ((SapProperty)pMine).getHelperContext();
        if (mineContext != null) {
            XSDHelper mineXsdHelper = mineContext.getXSDHelper();
            String uri = mineXsdHelper.getNamespaceURI(pMine);
            mainTypeHelper = pMainContext.getTypeHelper();
            if (uri != null) {
                return mainTypeHelper.getOpenContentProperty(uri, pMine.getName());
            }
        }
        DataObject mainProp = pMainContext.getDataFactory().create(Property.class);
        DataObject mineProp = (DataObject)pMine;
        for (Property propProp: (List<Property>)mineProp.getInstanceProperties()) {
            mainProp.set(propProp, mineProp.get(propProp));
        }
        return mainTypeHelper.defineOpenContentProperty(null, mainProp);
    }
}
