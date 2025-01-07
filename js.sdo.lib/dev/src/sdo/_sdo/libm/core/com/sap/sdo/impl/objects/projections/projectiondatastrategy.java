package com.sap.sdo.impl.objects.projections;

import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.api.types.SapType;
import com.sap.sdo.impl.context.HelperContextImpl;
import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.builtin.PropertyType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
/**
 * A projection might not match 100% to the original data strategy, or to the delegating data strategy.  This class decorates the data
 * coming from the original data strategy, so that it fits with the (type) of the GDO with which it is associated.
 */
public class ProjectionDataStrategy extends AbstractDataStrategy {

    private static final long serialVersionUID = -6766085418127021653L;
	private DelegatingDataStrategy _delegate;
    protected ArrayList<Property> _openProperties;
	public ProjectionDataStrategy(GenericDataObject projection, DelegatingDataStrategy delegate) {
		setDataObject(projection);
		_delegate = delegate;
        initOpenProperties();
        
	}
    
    private void initOpenProperties() {
        final List<Property> otherOpenProperties = _delegate.getOpenProperties();
        if (otherOpenProperties == null) {
            return;
        }
        if (!_delegate.getDataObject().getType().isOpen()) {
            throw new IllegalArgumentException("type " + 
                ((SapType)_delegate.getDataObject().getType()).getQName() + 
                " in HelperContext " + getDataObject().getHelperContext() + " is not open");
        }
        ArrayList<Property> myOpenProperties = new ArrayList<Property>(otherOpenProperties.size());
        HelperContext myHelperContext = getDataObject().getHelperContext();
        AbstractProjectionMappingStrategy mappingStrategy = getToProjectionMappingStrategy();
        for (Property otherProperty: otherOpenProperties) {
            Property myProperty = mappingStrategy.getOpenProperty(myHelperContext, otherProperty);
            myOpenProperties.add(myProperty);
        }
        setOpenProperties(myOpenProperties);
    }

    public AbstractProjectionMappingStrategy getToProjectionMappingStrategy() {
        return getMappingStrategy(_delegate.getHelperContext(), getDataObject().getHelperContext());
    }
    
    public AbstractProjectionMappingStrategy getFromProjectionMappingStrategy() {
        return getMappingStrategy(getDataObject().getHelperContext(), _delegate.getHelperContext());
    }
    
	public DelegatingDataStrategy getDelegate() {
		return _delegate;
	}
    public Sequence getSequence() {
        return new ProjectedSequence(this);
    }
    public Sequence getOldSequence() {
        return new ProjectedSequence(this);
    }
	@Override
	public void addOpenProperty(Property pProperty) {
//    TODO Auto-generated method stub
        //throw new UnsupportedOperationException("addOpenProperty");
	}
	public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
		return
		wrap(pPropIndex, _delegate.createPropValue(getPropertyMap()[pPropIndex], null));
	}
	private int[] getPropertyMap() {
		return getPropertyMap(getDataObject().getType(), _delegate.getDataObject().getType());
	}
	public PropValue<?> wrap(int pPropIndex, PropValue<?> del) {
	       SdoProperty property = (SdoProperty)getPropertyByIndex(pPropIndex);
	       return wrap(property, del);
	}
    
    public PropValue<?> wrap(Property pProperty, PropValue<?> del) {
        
        if (pProperty.isMany()) {
            return new ProjectionPropMultiValue(this, pProperty, (PropValue<List<Object>>)del);
        }
        return new ProjectionPropSingleValue(this, pProperty, del);
    }
    

    @Override
    public void removeOpenPropValue(int pPropertyIndex) {
//      TODO Auto-generated method stub
        //throw new UnsupportedOperationException("removeOpenPropValue");
    }

    @Override
    public void reactivateOpenPropValue(PropValue<?> pPropValue) {
//      TODO Auto-generated method stub
        //throw new UnsupportedOperationException("reactivateOpenPropValue");
    }

	public DataObject createDataObject(Property property, Type type) {
		Type mainType = _delegate.getDataObject().getHelperContext().getTypeHelper().getType(type.getURI(), type.getName());
		final DataObject createdDo = _delegate.createDataObject(
        				_delegate.getDataObject().getInstanceProperties().get(getPropertyMap()[((SdoProperty)property).getIndex()]), mainType);
        return ((SapDataFactory)getDataObject().getHelperContext().getDataFactory()).project(createdDo);
	}

	public PropValue<?> getPropValue(int pIndex, Property pProperty, boolean pCreate) {
        if (pIndex < getPropertyMap().length) {
    		int delInd = getPropertyMap(getDataObject().getType(), _delegate.getDataObject().getType())[pIndex];
    		PropValue d = _delegate.getPropValue(delInd, null, pCreate);
    		if (d == null) {
    			return null;
    		}
    		return wrap(pIndex, d);
        }
        Property myProperty = getPropertyByIndex(pIndex);
        AbstractProjectionMappingStrategy mappingStrategy = getFromProjectionMappingStrategy();
        return _delegate.getPropValue(mappingStrategy.getOpenProperty(_delegate.getHelperContext(), myProperty), pCreate);
	}

	public int[] getPropertyMap(Type me, Type main) {
		AbstractProjectionMappingStrategy s = getMappingStrategy(((SdoType)me).getHelperContext(),
				((SdoType)main).getHelperContext());
		return s.getPropertyMap(me, main);
	}
	private AbstractProjectionMappingStrategy getMappingStrategy(HelperContext hc1, HelperContext hc2) {
		Property p = ((HelperContextImpl)hc1).getMappingStrategyProperty();
		if (p != null) {
			return getStrategyFromProperty(p);
		}
		return getStrategyFromProperty(((HelperContextImpl)hc2).getMappingStrategyProperty());
	}
	private AbstractProjectionMappingStrategy getStrategyFromProperty(Property matchingProperty) {
		AbstractProjectionMappingStrategy x = DefaultProjectionMappingStrategy.getInstance();
		if (matchingProperty!=null && matchingProperty.getName().equals(PropertyType.XML_NAME)) {
			x = XmlProjectionMappingStrategy.getInstance();
		}
		return x;
	}
	public int[] getInversePropertyMap(Type me, Type main) {
		AbstractProjectionMappingStrategy s = getMappingStrategy(((SdoType)me).getHelperContext(),
				((SdoType)main).getHelperContext());
		return s.getInversePropertyMap(me, main);
	}
	public DataStrategy refineType(Type pOldType, Type pNewType) {
		return this;
	}
    
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
        _openProperties = pOpenProperties;
    }

    public List<Property> getOpenProperties() {
        return _openProperties;
    }

    @Override
    public void trimMemory() {
        super.trimMemory();
        if (_openProperties != null) {
            _openProperties.trimToSize();
        }
    }

}
