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
package commonj.sdo.impl;

import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.impl.data.DataHelperDelegator;
import com.sap.sdo.impl.objects.CopyHelperDelegator;
import com.sap.sdo.impl.objects.DataFactoryDelegator;
import com.sap.sdo.impl.objects.EqualityHelperDelegator;
import com.sap.sdo.impl.types.TypeHelperDelegator;
import com.sap.sdo.impl.xml.XMLHelperDelegator;
import com.sap.sdo.impl.xml.XSDHelperDelegator;

import commonj.sdo.helper.CopyHelper;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.DataHelper;
import commonj.sdo.helper.EqualityHelper;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.ExternalizableDelegator.Resolvable;

public class HelperProviderImpl extends HelperProvider
{
    public HelperProviderImpl() {
        super();
    }

    CopyHelper copyHelper() {
        return CopyHelperDelegator.getInstance();
    }

    DataFactory dataFactory() {
        return DataFactoryDelegator.getInstance();
    }

    DataHelper dataHelper() {
        return DataHelperDelegator.getInstance();
    }

    EqualityHelper equalityHelper() {
        return EqualityHelperDelegator.getInstance();
    }

    TypeHelper typeHelper() {
        return TypeHelperDelegator.getInstance();
    }

    XMLHelper xmlHelper() {
        return XMLHelperDelegator.getInstance();
    }

    XSDHelper xsdHelper() {
        return XSDHelperDelegator.getInstance();
    }

    Resolvable resolvable() {
        return null;
    }

    Resolvable resolvable(Object target) {
        return null;
    }

    @Override
    HelperContext helperContext() {
        return SapHelperProvider.getDefaultContext();
    }

}
