package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.CovarianceA;
import com.sap.sdo.testcase.typefac.CovarianceB;
import com.sap.sdo.testcase.typefac.InheritenceA;
import com.sap.sdo.testcase.typefac.InheritenceA1;
import com.sap.sdo.testcase.typefac.InheritenceB;

import commonj.sdo.Type;

public class CovarianceTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public CovarianceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testOverwriteProp() {
        Type inheritenceA1Type = _helperContext.getTypeHelper().getType(InheritenceA1.class);
        assertEquals(inheritenceA1Type.toString(), 1, inheritenceA1Type.getProperties().size());

        Type inheritenceAType = _helperContext.getTypeHelper().getType(InheritenceA.class);
        assertEquals(inheritenceAType.getProperty("a"), inheritenceA1Type.getProperty("a"));

        InheritenceA1 a1 = (InheritenceA1)_helperContext.getDataFactory().create(inheritenceA1Type);
        a1.setA("neu");
        assertEquals("neu", a1.getA());
    }

    @Test
    public void testCovarianceType() {
        Type covarianceBType = _helperContext.getTypeHelper().getType(CovarianceB.class);
        assertEquals(covarianceBType.toString(), 1, covarianceBType.getProperties().size());

        Type covarianceAType = _helperContext.getTypeHelper().getType(CovarianceA.class);
        assertEquals(covarianceAType.getProperty("value"), covarianceBType.getProperty("value"));

        CovarianceB cB = (CovarianceB)_helperContext.getDataFactory().create(covarianceBType);
        InheritenceB b = (InheritenceB)_helperContext.getDataFactory().create(InheritenceB.class);

        cB.setValue(b);
        assertEquals(b, cB.getValue());

        cB.setValue(null);
        assertEquals(null, cB.getValue());

        cB.setValue((InheritenceA)b);
        assertEquals(b, cB.getValue());

        CovarianceA cA = (CovarianceA)_helperContext.getDataFactory().create(covarianceAType);
        cA.setValue(b);
        assertEquals(b, cA.getValue());
    }

}
