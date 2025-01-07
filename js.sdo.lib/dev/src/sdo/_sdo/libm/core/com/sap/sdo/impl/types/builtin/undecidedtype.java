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

import com.sap.sdo.api.util.URINamePair;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class UndecidedType extends MetaDataType<DataObject> {
	
	private static final long serialVersionUID = 2201577876394399378L;
	private static final UndecidedType INSTANCE = new UndecidedType();

	public static UndecidedType getInstance()
	{
		return INSTANCE;
	}
	private UndecidedType() {
		super();
		super.setUNP(URINamePair.MIXEDTEXT_TYPE);
		super.setOpen(true);
		super.setSequenced(true);
        super.setInstanceClass(DataObject.class);
	}
	@Override
	public boolean isMixedContent() {
		return true;
	}
	public Object readResolve() {
		return getInstance();
	}
    /* (non-Javadoc)
     * @see com.sap.sdo.impl.types.builtin.MetaDataType#isAssignableType(commonj.sdo.Type)
     */
    @Override
    public boolean isAssignableType(Type pAssignableFrom) {
        return true;
    }
}
