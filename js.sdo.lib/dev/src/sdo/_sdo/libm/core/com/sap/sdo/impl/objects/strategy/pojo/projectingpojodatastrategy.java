package com.sap.sdo.impl.objects.strategy.pojo;

import java.util.Map;

import com.sap.sdo.impl.objects.DataObjectDecorator;

public class ProjectingPojoDataStrategy extends PojoDataStrategy {

    private static final long serialVersionUID = -310196688671035809L;

	public ProjectingPojoDataStrategy(Object pPojo, Map<Object, DataObjectDecorator> pPojoToDataObject) {
		super(pPojo, pPojoToDataObject);
	}

}
