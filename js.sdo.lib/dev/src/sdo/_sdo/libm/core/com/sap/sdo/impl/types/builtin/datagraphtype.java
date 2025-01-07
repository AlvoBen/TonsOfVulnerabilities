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
package com.sap.sdo.impl.types.builtin;

import java.util.Collections;
import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.ChangeSummaryImpl;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.IHasDelegator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.TypeHelperImpl;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
/**
 * Built-in type representing the {commonj.sdo}ChangeSummary interface.  This is the type
 * given to properties that will be serialized as a change summary.  Note that we this type
 * is not instanciated to create a change summary.  The current version of SAP's SDO implementation
 * does not store change information in a seperate data structure, rather associating it
 * with the objects being logged.
 */
public class DataGraphType extends MetaDataType<DataGraph> implements IHasDelegator {

	private static final long serialVersionUID = 2201577876394399378L;
	private static DataGraphType _instance = new DataGraphType();
	/**
	 * Typo-proof string representing the XML schema property.
	 */
	public static final String XSD = "xsd";
	/**
	 * Typo-proof string representing the ChangeSummary property.
	 */
	public static final String CHANGE_SUMMARY = "changeSummary";
    /**
     * Typo-proof string representing the OrphanHolder property.
     */
    public static final String ORPHAN_HOLDER = "orphanHolder";
	/**
	 * Return the singleton instance of this type (all "defined" types are singletons).
	 * @return
	 */
	public static DataGraphType getInstance() {
		return _instance;
	}
	@Override
    public DataGraph convertFromJavaClass(final Object data) {
        if (data==null) {
            return null;
        }
    	if (data instanceof DataGraph) {
    		return (DataGraph)data;
    	}
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
            " to " + getInstanceClass().getName());
	}
	@Override
	public <T> T convertToJavaClass(final DataGraph data, final Class<T> targetType) {
        if (data==null) {
            return null;
        }
		if (DataGraph.class.isAssignableFrom(targetType)) {
			return (T)data;
		}
        throw new ClassCastException("Can not convert from " + getInstanceClass().getName() +
            " to " + targetType.getName());
	}
	private DataGraphType() {
        _instance = this;
		setUNP(URINamePair.DATAGRAPH_TYPE);
		SdoProperty[] propsa = new SdoProperty[] {
			new MetaDataPropertyLogicFacade(new MetaDataProperty(XSD,XsdType.getInstance(),this,false,false,true,true)),
			new MetaDataPropertyLogicFacade(new MetaDataProperty(CHANGE_SUMMARY,ChangeSummaryType.getInstance(),this,true,false,true,true)),
            new MetaDataPropertyLogicFacade(new MetaDataProperty(ORPHAN_HOLDER,DataObjectType.getInstance(),this,true,true,true,true,true))
		};
		setDeclaredProperties(propsa);
		setOpen(true);
		setInstanceClass(DataGraph.class);
		setSequenced(false);
		useCache();
	}

	@Override
    public int getCsPropertyIndex() {
        return 1;
    }

    @Override
    public int getXsdPropertyIndex() {
        return 0;
    }

    @Override
    public List<SdoProperty> getOrphanHolderProperties() {
        return Collections.singletonList((SdoProperty)getDeclaredProperties().get(2));
    }

    public Class<? extends DataObject> getFacadeClass() {
		return DataGraphLogic.class;
	}

	@Override
    public Object readResolve() {
		return getInstance();
	}
	static public class XsdType extends MetaDataType<Object> {
		private static final long serialVersionUID = -6928438661309181460L;
		private XsdType() {
            _instance = this;
			setUNP(URINamePair.XSD_TYPE);
			setOpen(true);
			setSequenced(true);
			setDataType(false);
			useCache();
		}
		@Override
        public Object readResolve() {
			return getInstance();
		}
		private static XsdType _instance = new  XsdType();
		public static XsdType getInstance() {
			return _instance;
		}
		@Override
	    public Object convertFromJavaClass(Object data) {
	    	return data;
		}

	}
	public static class DataGraphLogic extends DelegatingDataObjectDecorator implements DataGraph,  DataObjectDecorator {
        public static final String DEFAULT_ROOT_NAME = "defaultRootName";
        private static final long serialVersionUID = 1958777027096303589L;
        private DataObjectDecorator _rootObject;
        private final HelperContext _helperContext;

		public DataObject createRootObject(final Type type) {
            _rootObject = (DataObjectDecorator)_helperContext.getDataFactory().create(type);
            // guess global Property
            List<Property> globalProps = ((TypeHelperImpl)_helperContext.getTypeHelper()).getPropertiesForNamespace(type.getURI());
            Property rootProp = null;
            for (Property property : globalProps) {
                if (property.getType().equals(type)) {
                    rootProp = property;
                    break;
                }
            }
            if (rootProp == null) {
                rootProp =
                    _helperContext.getTypeHelper().getOpenContentProperty(
                        URINamePair.PROP_SDO_DATA_OBJECT.getURI(),
                        URINamePair.PROP_SDO_DATA_OBJECT.getName());
            }

            final boolean readOnlyActivated = ((GenericDataObject)getDelegate()).isReadOnlyMode();
            ((GenericDataObject)getDelegate()).setReadOnlyMode(false);
            if (rootProp.isMany()) {
                getDelegate().getList(rootProp).add(_rootObject);
            } else {
                getDelegate().set(rootProp, _rootObject);
            }
            getDelegate().set(CHANGE_SUMMARY, new ChangeSummaryImpl(_rootObject));

            ((GenericDataObject)getDelegate()).getInstance().setReadOnlyMode(readOnlyActivated);

            return _rootObject;
		}

		public DataObject createRootObject(String namespaceURI, String typeName) {
            Type type = _helperContext.getTypeHelper().getType(namespaceURI, typeName);
            return createRootObject(type);
        }

		@Override
        public DataObject getRootObject() {
			if (_rootObject == null) {
				final int size = getDelegate().getType().getProperties().size();
				final List<Property> properties = getDelegate().getInstanceProperties();
                if (properties.size() > size) {
                    if (properties.get(size).isMany()) {
                        _rootObject = (DataObjectDecorator)getDelegate().getList(size).get(0);
                    } else {
                        _rootObject = (DataObjectDecorator)getDelegate().getDataObject(size);
                    }
				}
			}
			return _rootObject;
		}

        public Type getType(String namespaceURI, String typeName) {
            return _helperContext.getTypeHelper().getType(namespaceURI, typeName);
        }

		public DataGraphLogic(DataObject o) {
			super((GenericDataObject)o);
            _helperContext = ((GenericDataObject)o).getHelperContext();
		}
	}
}
