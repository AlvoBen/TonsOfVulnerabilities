package com.sap.jms.util.compat.engine.interfaces.cross;

import java.io.Serializable;

public interface ObjectIdentifier {

	public String _getFactoryName();
	public Serializable _getObjectId();
}
