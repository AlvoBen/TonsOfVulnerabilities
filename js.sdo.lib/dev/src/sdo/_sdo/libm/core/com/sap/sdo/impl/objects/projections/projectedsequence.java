package com.sap.sdo.impl.objects.projections;

import java.io.Serializable;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.impl.objects.DataObjectDecorator;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;

public class ProjectedSequence implements Sequence, Serializable {

    private static final long serialVersionUID = -9160644406941873758L;
	private ProjectionDataStrategy _delegate;
	public ProjectedSequence(ProjectionDataStrategy sequence) {
		_delegate = sequence;
	}

	public boolean add(String propertyName, Object value) {
		return _delegate.getDelegate().getSequence().add(propertyName, value);
	}

	public boolean add(int pIndex, Object pValue) {
		int delInd = _delegate.getPropertyMap(_delegate.getDataObject().getType(), _delegate.getDelegate().getDataObject().getType())[pIndex];
		SdoType targetType = (SdoType)_delegate.getDelegate().getDataObject().getType();
        Object value = pValue;
        if (!targetType.isDataType()) {
            value = ((SapDataFactory)targetType.getHelperContext().getDataFactory()).project((DataObject)pValue);
        }
		return _delegate.getDelegate().getSequence().add(delInd,value);
	}

	public boolean add(Property property, Object value) {
		if (property.isOpenContent()) {
			// TODO
		}
		return add(((SdoProperty)property).getIndex(), value);
	}

	public void add(int index, String propertyName, Object value) {
		_delegate.getDelegate().getSequence().add(index, propertyName, value);

	}

	public void add(int index, int pIndex, Object pValue) {
		int delInd = _delegate.getPropertyMap(_delegate.getDataObject().getType(), _delegate.getDelegate().getDataObject().getType())[pIndex];
		SdoType targetType = (SdoType)_delegate.getDelegate().getDataObject().getType();
        Object value = pValue;
        if (!targetType.isDataType()) {
            value = ((SapDataFactory)targetType.getHelperContext().getDataFactory()).project((DataObject)pValue);
        }
		_delegate.getDelegate().getSequence().add(index, delInd,value);
	}

	public void add(int index, Property property, Object value) {
		if (property.isOpenContent()) {
			// TODO
		}
		add(index, ((SdoProperty)property).getIndex(), value);

	}

	public void add(String text) {
		_delegate.getDelegate().getSequence().addText(text);

	}

	public void add(int index, String text) {
		_delegate.getDelegate().getSequence().addText(index, text);

	}

	public void addText(String text) {
		_delegate.getDelegate().getSequence().addText(text);
	}

	public void addText(int index, String text) {
		_delegate.getDelegate().getSequence().addText(index, text);
	}

	public Property getProperty(int index) {
		SdoProperty dProp = (SdoProperty)_delegate.getDelegate().getSequence().getProperty(index);
		if (dProp.isOpenContent()) {
			// TODO:
		}
		int mInd = _delegate.getInversePropertyMap(_delegate.getDataObject().getType(), _delegate.getDelegate().getDataObject().getType())[dProp.getIndex()];
		return (Property)_delegate.getDataObject().getType().getProperties().get(mInd);
	}

	public Object getValue(int index) {
		Object o = _delegate.getDelegate().getSequence().getValue(index);
        if (o instanceof DataObjectDecorator) {
            // TODO this is the wrong HelperContext!
            SapDataFactory dataFactory = (SapDataFactory)((SdoType)_delegate.getDataObject().getType()).getHelperContext().getDataFactory();
            return dataFactory.project((DataObject)o);
        }
		return o;
	}

	public void move(int toIndex, int fromIndex) {
        _delegate.getDelegate().getSequence().move(toIndex, fromIndex);
	}

	public void remove(int index) {
        _delegate.getDelegate().getSequence().remove(index);
	}

	public Object setValue(int index, Object value) {
        if (value instanceof DataObject) {
            // TODO implement this!
            throw new UnsupportedOperationException("Not implemented: setValue(int, Object)");
        }
		return _delegate.getDelegate().getSequence().setValue(index, value);
	}

	public int size() {
		return _delegate.getDelegate().getSequence().size();
	}

}
