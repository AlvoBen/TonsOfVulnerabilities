package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.ParamType;
import com.sap.sdm.api.remote.ParamTypes;
import com.sap.sdm.api.remote.RemoteException;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-15
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class P4ParamTypeImpl implements ParamType {

	private int type = ParamTypes.UNKNOWN;

	P4ParamTypeImpl(int type) {
		if (-1 < type && type < ParamTypes.MAX_TYPE) {
			this.type = type;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ParamType#getTypeAsInt()
	 */
	public int getTypeAsInt() throws RemoteException {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.ParamType#getTypeAsString()
	 */
	public String getTypeAsString() throws RemoteException {
		switch (this.type) {
		case ParamTypes.BOOLEAN:
			return ParamTypes.BOOLEAN_S;
		case ParamTypes.BYTE:
			return ParamTypes.BYTE_S;
		case ParamTypes.DOUBLE:
			return ParamTypes.DOUBLE_S;
		case ParamTypes.FLOAT:
			return ParamTypes.FLOAT_S;
		case ParamTypes.INT:
			return ParamTypes.INT_S;
		case ParamTypes.LONG:
			return ParamTypes.LONG_S;
		case ParamTypes.SHORT:
			return ParamTypes.SHORT_S;
		case ParamTypes.STRING:
			return ParamTypes.STRING_S;
		default:
			return ParamTypes.UNKNOWN_S;
		}
	}
}
