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
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.internal.j1.sdo.IReservation;
import com.sap.sdo.testcase.internal.j1.sdo.ITrip;
import com.sap.sdo.testcase.internal.j1.sdo.ITripReservation;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;

/**
 * @author D042774
 *
 */
public class J1DemoBehaviorTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public J1DemoBehaviorTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /**
     *
     */
    private static final int ID = /*0*/815;
    /**
     *
     */
    private static final int CNO_ID = 4711;

    /*
     * @see TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testChangeSummaryReset() {
        ITrip trip = (ITrip)_helperContext.getDataFactory().create(ITrip.class);
        ITripReservation tripReservation = (ITripReservation)_helperContext.getDataFactory().create(ITripReservation.class);
        IReservation reservation = (IReservation)_helperContext.getDataFactory().create(IReservation.class);

        trip.getReservations().add(tripReservation);
        tripReservation.setReservation(reservation);

        reservation.setArrival(Calendar.getInstance().getTime());
        reservation.setCno(CNO_ID);
        reservation.setId(ID);
        reservation.setReservationType("suite");

        ChangeSummary cs = trip.getChangeSummary();
        cs.beginLogging();

        assertNotNull(trip.getReservations());
        assertFalse(trip.getReservations().isEmpty());
        assertEquals(1, trip.getReservations().size());
        assertNotNull(trip.getReservations().get(0).getReservation());
        IReservation returnedRes = trip.getReservations().get(0).getReservation();
        assertEquals((long)CNO_ID, returnedRes.getCno());
        assertEquals((long)ID, returnedRes.getId());
        assertEquals("suite", returnedRes.getReservationType());

        ((DataObject)tripReservation).delete();

        cs.undoChanges();

        assertNotNull(trip.getReservations());
        assertFalse(trip.getReservations().isEmpty());
        assertEquals(1, trip.getReservations().size());

        IReservation restoredRes = trip.getReservations().get(0).getReservation();
        assertNotNull(restoredRes);
        assertEquals(trip.getReservations().get(0), ((DataObject)restoredRes).getContainer());
        assertEquals((long)CNO_ID, restoredRes.getCno());
        assertEquals((long)ID, restoredRes.getId());
        assertEquals("suite", restoredRes.getReservationType());
    }

}
