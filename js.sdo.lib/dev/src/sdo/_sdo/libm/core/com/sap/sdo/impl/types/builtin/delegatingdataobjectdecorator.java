package com.sap.sdo.impl.types.builtin;

import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.objects.GenericDataObject;
import com.sap.sdo.impl.objects.InternalDataObjectModifier;

import commonj.sdo.DataObject;

public class DelegatingDataObjectDecorator extends DelegatingDataObject implements DataObjectDecorator {
	private static final long serialVersionUID = -2910429674442617934L;
	private GenericDataObject _delegate;
	protected DelegatingDataObjectDecorator(GenericDataObject gdo) {
		_delegate = gdo;
	}
	@Override
	protected DataObject getDelegate() {
		return _delegate;
	}

	public GenericDataObject getInstance() {
		return _delegate;
	}
    
    public InternalDataObjectModifier getInternalModifier() {
        return _delegate;
    }

}
