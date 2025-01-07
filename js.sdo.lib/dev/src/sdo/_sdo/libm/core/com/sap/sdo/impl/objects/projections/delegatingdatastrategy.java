package com.sap.sdo.impl.objects.projections;

import java.util.ArrayList;
import java.util.List;

import com.sap.sdo.impl.objects.DataStrategy;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.PropValue;
import com.sap.sdo.impl.objects.strategy.AbstractDataStrategy;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
/**
 * This decorator serves two purposes.  First, it holds the pointers to the other projections of the same underlying data.  Second, it will
 * generate PropValues that also delegate to all data strategies that require separate copies of the data (eg, PojoDataStrategy).
 */
public class DelegatingDataStrategy extends AbstractDataStrategy {

    private static final long serialVersionUID = -2880722904442669276L;
	private AbstractDataStrategy _main;
	private final List<GenericDataObject> _contextProjections = new ArrayList<GenericDataObject>();
	
	public GenericDataObject findProjection(HelperContext ctx) {
		for (GenericDataObject facade: _contextProjections) {
			if (((SdoType)facade.getType()).getHelperContext().equals(ctx)) {
				return facade;
			}
		}
		return null;
	}
	public List<GenericDataObject> getProjections() {
		return _contextProjections;
	}
	public DelegatingDataStrategy(AbstractDataStrategy main) {
		_main = main;
		setDataObject(_main.getDataObject());
	}
	public void addProjection(GenericDataObject facade) {
		_contextProjections.add(facade);
	}
	@Override
	public void addOpenProperty(Property pProperty) {
		_main.addOpenProperty(pProperty);
		
	}
	@Override
	public PropValue<?> createPropValue(int pPropIndex, Property pProperty) {
        PropValue mainPropValue = _main.createPropValue(pPropIndex, pProperty);
        if (mainPropValue.isMany()) {
            return mainPropValue;
        }
        return new DelegatingPropSingleValue(this, mainPropValue);
	}
	@Override
	public void reactivateOpenPropValue(PropValue<?> pPropValue) {
		_main.reactivateOpenPropValue(pPropValue);
		
	}
	@Override
	public void removeOpenPropValue(int pPropertyIndex) {
		_main.removeOpenPropValue(pPropertyIndex);
		
	}
	public DataObject createDataObject(Property property, Type type) {
		DataObject ret = _main.createDataObject(property, type);
		return ret;
	}
	public PropValue<?> getPropValue(int pIndex, Property pProperty, boolean pCreate) {
        PropValue mainPropValue = _main.getPropValue(pIndex, pProperty, pCreate);
        if (mainPropValue == null) {
            return null;
        }
        if (mainPropValue.isMany()) {
            return mainPropValue;
        }
        return new DelegatingPropSingleValue(this, mainPropValue);
	}
	public DataStrategy refineType(Type pOldType, Type pNewType) {
		_main = (AbstractDataStrategy)_main.refineType(pOldType, pNewType);
		return this;
	}
	public DataStrategy getMain() {
		return _main;
	}
	public Sequence getSequence() {
		return _main.getSequence();
	}
	public Sequence getOldSequence() {
		return _main.getOldSequence();
	}

	public List<PropValue> getPropValuesForEachProjection(PropValue value) {
		List<PropValue> ret = new ArrayList<PropValue>();
		ret.add(value);
		for (GenericDataObject gdo: _contextProjections) {
				int[] map = ((ProjectionDataStrategy)gdo.getDataStrategy()).getInversePropertyMap(gdo.getType(),value.getDataObject().getType());
				int i = map[value.getProperty().getIndex()];
				if (i >=0 ) {
					ret.add(gdo.getDataStrategy().getPropValue(i, null, true));
				}
		}
		return ret;
	}
    @Override
    public List<Property> getOpenProperties() {
        return _main.getOpenProperties();
    }
    @Override
    public void setOpenProperties(ArrayList<Property> pOpenProperties) {
    }
    
    @Override
    public void trimMemory() {
        super.trimMemory();
        _main.trimMemory();
    }

}
