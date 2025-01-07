package com.sap.sdo.impl.objects;

import commonj.sdo.DataObject;

public interface IHasDelegator {
	Class<? extends DataObject> getFacadeClass();
}
