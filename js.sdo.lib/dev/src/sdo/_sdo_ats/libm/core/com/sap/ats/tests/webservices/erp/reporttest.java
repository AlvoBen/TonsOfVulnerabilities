/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import junit.framework.TestCase;
import report.consumption.es.Report;
import report.consumption.es.TestRun;
import report.consumption.es.TestType;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

/**
 * @author D042774
 *
 */
public class ReportTest extends TestCase {
    private static final String REPORT_TYPE = ".xml";
    private static final String REPORT_TITLE = "sdo-report-cw";
    private final static String REPORT_FOLDER = "C:/sandbox/ats";
    
    private final List<String> failedInAll = Arrays.asList(
        "ecc_batchplantassignchgrc",
        "ecc_customercontactpersonupdrc",
        "ecc_fmprogrambyidqr",
        "ecc_individualmaterialselqr",
        "ecc_maintenancerequest001qr",
        "ecc_msmtrdngcrtchkqr",
        "ecc_ovhdcostldgraccterpqr",
        "ecc_productionordercrtrc",
        "ecc_productionorderupdrc",
        "ecc_purchaseorderchgrc",
        "ecc_purchaseordercrtrc",
        "ecc_purchaserequestchgrc",
        "ecc_purchaserequestcrtrc",
        "ecc_srvcreqcrtchkqr",
        "finb_ic_issue_by_id_qr",
        "finb_ic_issue_remplan_qr",
        "umb_esax_bscm_query",
        "umb_esax_bscmass_cancel",
        "umb_esax_bscmass_change",
        "umb_esax_bscmass_create",
        "umb_esax_bscmmst_change",
        "umb_esax_bscms_change",
        "umb_esax_bsco_query",
        "umb_esax_bscoass_cancel",
        "umb_esax_bscoass_change",
        "umb_esax_bscoass_create",
        "umb_esax_bscomst_change",
        "umb_esax_bscos_change",
        "umb_esax_pbsc"
    );

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFailures() throws Exception {
        File reportFolder = new File(REPORT_FOLDER);
        assertNotNull(reportFolder);
        assertEquals(true, reportFolder.isDirectory());
        String[] names =
            reportFolder.list(
                new FilenameFilter() {
                    public boolean accept(File pDir, String pName) {
                        return pName != null && pName.startsWith(REPORT_TITLE) && pName.endsWith(REPORT_TYPE);
                    }});
        int cw = 0;
        for (String name : names) {
            int reportCw = Integer.parseInt(name.substring(REPORT_TITLE.length(), name.lastIndexOf(REPORT_TYPE)));
            if (reportCw > cw) {
                cw = reportCw;
            }
        }
        assertTrue(cw > 0);
        //cw = 19;
        File reportFile = new File(reportFolder, REPORT_TITLE+cw+REPORT_TYPE);
        assertNotNull(reportFile);
        
        HelperContext context = HelperProvider.getDefaultContext();
        // init type system
        context.getTypeHelper().getType(Report.class);
        
        XMLDocument doc = context.getXMLHelper().load(new FileInputStream(reportFile));
        assertNotNull(doc);
        DataObject root = doc.getRootObject();
        assertNotNull(root);
        assertEquals(Report.class, root.getType().getInstanceClass());
        Report report = (Report)root;
        List<TestType> tests = report.getTest();
        int count = 0;
        Map<String,List<String>> exceptions = new HashMap<String,List<String>>();
        for (TestType test : tests) {
            if ("Error".equals(test.getStatus())) {
                if (!failedInAll.contains(test.getService().toLowerCase())) {
                    ++count;
                    
                    TestRun lastFailed = null;
                    List<TestRun> testRuns = test.getTestRun();
                    for (TestRun run : testRuns) {
                        if ("Error".equals(run.getStatus())) {
                            lastFailed = run;
                        }
                    }
                    assertNotNull(lastFailed);
                    String errorDetail = lastFailed.getErrorDetails().getValue();
                    assertNotNull(errorDetail);
                    if (errorDetail.contains("Caused by: java.lang.IllegalArgumentException: invalid date string")) {
                        errorDetail = "Caused by: java.lang.IllegalArgumentException: invalid date string";
                    }
                    synchronized (exceptions) {
                        List<String> failedServices = exceptions.get(errorDetail);
                        if (failedServices == null) {
                            failedServices = new ArrayList<String>();
                            exceptions.put(errorDetail, failedServices);
                        }
                        failedServices.add(test.getService());
                    }
                }
            }
        }
        Set<Entry<String, List<String>>> entries = exceptions.entrySet();
        for (Entry<String, List<String>> entry : entries) {
          System.out.println(entry.getKey());
          for (String service : entry.getValue()) {
            System.out.println("* " + service);
          }
          System.out.println("--------------------------------------------------");
        }
        System.out.println(count);
        
        Set<String> sorted = new TreeSet<String>();
        for (List<String> services : exceptions.values()) {
            for (String string : services) {
                sorted.add(string);
            }
        }
        for (String string : sorted) {
            System.out.println(string);
        }
    }
}
