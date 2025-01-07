package com.sap.sdo.testcase.internal.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapDataFactory;
import com.sap.sdo.api.helper.SapDataHelper;
import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;

public class PojoEnhancerTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public PojoEnhancerTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testManyValuedProperty() throws IOException {
        DataFactory dataFactory = _helperContext.getDataFactory();
        POOrder order = (POOrder)dataFactory.create(POOrder.class);
        POOrderItem orderItem1 = (POOrderItem)dataFactory.create(POOrderItem.class);
        POOrderItem orderItem2 = (POOrderItem)dataFactory.create(POOrderItem.class);

        List<POOrderItem> lineItems = new ArrayList<POOrderItem>();
        lineItems.add(orderItem1);
        order.setLineItems(lineItems);
        lineItems = order.getLineItems();
        assertNotNull(lineItems);
        assertEquals(1, lineItems.size());
        assertSame(orderItem1, lineItems.get(0));

        lineItems.add(orderItem2);
        //test, if the list was life
        lineItems = order.getLineItems();
        assertSame(orderItem2, lineItems.get(1));

        assertSame(orderItem1, lineItems.remove(0));

        lineItems.set(0, orderItem1);
        assertEquals(1, lineItems.size());
        assertSame(orderItem1, lineItems.get(0));

        HelperContext newHelperContext = SapHelperProvider.getNewContext();

        final String schemaFileName = PACKAGE + "j1.xsd";
        URL xsdUrl = getClass().getClassLoader().getResource(schemaFileName);
        newHelperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());


        SapDataFactory dataFactory2 = (SapDataFactory)newHelperContext.getDataFactory();
        DataObject order2 = dataFactory2.project((DataObject)order);

        SapDataHelper dataHelper = (SapDataHelper)newHelperContext.getDataHelper();
        assertSame(newHelperContext, dataHelper.getHelperContext(order2));

        assertEquals(false, order2 instanceof POOrder);

        List lineItems2 = order2.getList("lineItems");
        assertEquals(1, lineItems2.size());
        DataObject lineItem2 = (DataObject)lineItems2.get(0);
        assertEquals(false, lineItem2 instanceof POOrderItem);

    }

}
