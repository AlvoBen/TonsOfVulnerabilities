/*
 * Copyright (c) 2005 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.services.deploy.timestat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ProgressEvent;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.timestat.ContainerOperationTimeStat;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.TimeStatisticNode;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;

import junit.framework.TestCase;

public class TimeStatisticTest extends TestCase {
	
	private static final Location location = 
		Location.getLocation(TimeStatisticTest.class);

  private static final int COLUMN_NAME = 0;

  private static final int COLUMN_DURATION = 1;

  private static final int COLUMN_PERCENT = 2;

  private String sTestApplicationFullName = "com.sap/testApplication";
  
  public void testMain() {

    TransactionTimeStat transactionStat = new TransactionTimeStat(
        "DeployTransaction", sTestApplicationFullName, 1);
    assertSame(transactionStat, TransactionTimeStat.transactionStat.get());

    // progress listener for registering time statistic events
    ProgressListener progressListener = new ProgressListener() {
      public void handleProgressEvent(ProgressEvent arg0) {
        TransactionTimeStat.handleProgressEvent(arg0);
      }
    };
    // dummy array for container operations
    File[] files = new File[] {};

    try {
      TimeStatisticContainer tsc = new TimeStatisticContainer("servlet_jsp");
      tsc.addProgressListener(progressListener);
      tsc.deploy(files, null, null);
      ContainerOperationTimeStat deployOperation = new ContainerOperationTimeStat(
          "deploy", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      if (tsc.needUpdate(files, null, null)) {
        deployOperation = new ContainerOperationTimeStat("needUpdate", "com.sap/testApplication", 0, 10, 0, 6);
        TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
            deployOperation);

        tsc.makeUpdate(files, null, null);
        deployOperation = new ContainerOperationTimeStat("makeUpdate", "com.sap/testApplication", 0, 10, 0, 40);
        TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
            deployOperation);
      }

      deployOperation = new ContainerOperationTimeStat("prepareUpdate", "com.sap/testApplication", 0, 10, 0, 400);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      deployOperation = new ContainerOperationTimeStat("commitUpdate", "com.sap/testApplication", 0, 10, 0, 1000);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      deployOperation = new ContainerOperationTimeStat("remove", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      tsc = new TimeStatisticContainer("ejb");
      tsc.addProgressListener(progressListener);
      tsc.deploy(files, null, null);
      deployOperation = new ContainerOperationTimeStat("deploy", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      if (tsc.needUpdate(files, null, null)) {
        deployOperation = new ContainerOperationTimeStat("needUpdate", "com.sap/testApplication", 0, 10);
        TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
            deployOperation);

        tsc.makeUpdate(files, null, null);
        deployOperation = new ContainerOperationTimeStat("makeUpdate", "com.sap/testApplication", 0, 10);
        TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
            deployOperation);
      }

      deployOperation = new ContainerOperationTimeStat("prepareUpdate", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      deployOperation = new ContainerOperationTimeStat("commitUpdate", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      deployOperation = new ContainerOperationTimeStat("remove", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addContainerOp(tsc.getContainerInfo().getName(),
          deployOperation);

      try {
        Thread.sleep(597);
      } catch (InterruptedException e3) {
        // TODO Auto-generated catch block
        e3.printStackTrace();
      }
      long startCluster = System.currentTimeMillis();
      try {
        Thread.sleep(57);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      long endCluster = System.currentTimeMillis();
      try {
        TransactionTimeStat.addDeployOperation(new DeployOperationTimeStat(
            DeployOperationTimeStat.CLUSTER_COMMUNICATION_DURATION, startCluster, endCluster));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation(DeployOperationTimeStat.CLUSTER_COMMUNICATION_DURATION, new DeployOperationTimeStat(
            "Send to all", startCluster, endCluster, 0, 12));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeployOperation(new DeployOperationTimeStat(
            DeployOperationTimeStat.CLUSTER_COMMUNICATION_DURATION, startCluster, endCluster, 0, 24000));
      } catch (Exception e) {
        e.printStackTrace();
      }
            
      startCluster = System.currentTimeMillis();
      try {
        Thread.sleep(297);
      } catch (InterruptedException e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
      }
      endCluster = System.currentTimeMillis();
      DeployOperationTimeStat oNode = new DeployOperationTimeStat("preprocess",
          startCluster, endCluster, 80, 120);
      TransactionTimeStat.addJLinEEOperation(oNode);
      //createSubConfig(oNode, 4, 2);

      startCluster = System.currentTimeMillis();
      try {
        Thread.sleep(297);
      } catch (InterruptedException e3) {
        // TODO Auto-generated catch block
        e3.printStackTrace();
      }
      endCluster = System.currentTimeMillis();
      TransactionTimeStat.addJLinEEOperation(new DeployOperationTimeStat(
          "validation duration", startCluster, endCluster));
      
      try {
        TransactionTimeStat.addDeployOperation(new DeployOperationTimeStat(
            "Events", startCluster, endCluster));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events/incoming", new DeployOperationTimeStat(
            "A_in", 1, 2));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events/incoming", new DeployOperationTimeStat(
            "B2_in", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events", new CalculatedTimeStatisticNode(
            "incoming"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events/incoming", new DeployOperationTimeStat(
            "B2_in", 2, 4));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events/incoming", new DeployOperationTimeStat(
            "C_in", 4, 6));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events/outgoing", new DeployOperationTimeStat(
            "A_out", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }
      // this one should fail to be added
      try {
        TransactionTimeStat.addDeploySubOperation("Events/outgoing/missing", new DeployOperationTimeStat(
            "A_miss", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }
      // this one should fail to be added
      try {
        TransactionTimeStat.addDeploySubOperation("Events/outgoing/missing", new DeployOperationTimeStat(
            "B_miss", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }

      // this one should fail to be added
      try {
        TransactionTimeStat.addDeploySubOperation("Events/outgoing/missing_1", new DeployOperationTimeStat(
            "A_miss_1", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }
      // this one should fail to be added
      try {
        TransactionTimeStat.addDeploySubOperation("Events/outgoing/missing_1", new DeployOperationTimeStat(
            "B_miss_1", 2, 3));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      try {
        TransactionTimeStat.addDeploySubOperation("Events", new CalculatedTimeStatisticNode(
            "outgoing"));
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      transactionStat.finish();
      String sTable = transactionStat.toString();
      evaluateTable(null, sTable);
      try {
        Thread.sleep(197);
      } catch (InterruptedException e4) {
        // TODO Auto-generated catch block
        e4.printStackTrace();
      }
    } catch (DeploymentException e) {
      DSLog.logErrorThrowable(location, e);
    } catch (WarningException e) {
      DSLog.logErrorThrowable(location, e);
    }

    assertNull(TransactionTimeStat.transactionStat.get());
  }

  /**
   * Parses and evaluates if the data in the table is correct.
   * 
   * @param oRoot
   * @param sTable
   */
  private void evaluateTable(TimeStatisticNode oRoot, String sTable) {
    StringTokenizer tokenizer = new StringTokenizer(sTable, System
        .getProperty("line.separator"), false);
    boolean bHeader = true;
    int level = 0;
    TableRecord prevRec = null;
    TableRecord root = null;
    while (tokenizer.hasMoreTokens()) {
      String sLine = tokenizer.nextToken();
      if (sLine == null)
        continue;
      if (!sLine.startsWith("|"))
        continue;
      // skip header
      if (bHeader) {
        bHeader = false;
        continue;
      }
      TableRecord tableRec = parseTableRecord(sLine, prevRec);
      level = tableRec.getLevel();
      // root
      if (level == 0) {
        root = tableRec;
      }
      prevRec = tableRec;
    }
    // general structure evaluation - 
    // does not depend on specific structure
    // and should be always successfull
    evaluateGeneralStructure(root);
    // evaluation of the specific structure
    // created through the test
    evaluateSpecificStructure(root);
  }

  /**
   * Parses a table record and stores the data in a 
   * <code>TableRecord</code> object.
   * @param sLine
   * @param prevRec
   * @return
   */
  private TableRecord parseTableRecord(String sLine, TableRecord prevRec) {
    // precaution
    if (sLine == null)
      return null;

    String sName = null;
    long duration = -1;
    float percent = -1;
    int level = 0;
    TableRecord parent = null;

    String sLevelPref = "    ";
    int count = 0;
    StringTokenizer tokenizer = new StringTokenizer(sLine, "|", false);
    while (tokenizer.hasMoreTokens()) {
      // all records have a single space for better readability
      String sField = tokenizer.nextToken().substring(1);
      switch (count) {
      case COLUMN_NAME:
        StringBuffer sbLinePref = new StringBuffer(sLevelPref);
        while (true) {
          // extended condition for slight optimization
          if (sField.length() >= sbLinePref.length()
              && sField.charAt(sbLinePref.length() - 1) == sbLinePref
                  .charAt(sbLinePref.length() - 1)
              && sField.startsWith(sbLinePref.toString())) {
            level++;
            sbLinePref.append(sLevelPref);
          } else {
            break;
          }
        }
        // trim after level recognition
        sName = sField.trim();
        if (prevRec != null) {
          if (prevRec.getLevel() == level - 1) {
            parent = prevRec;
          } else {
            TableRecord parent1 = prevRec.getParent();
            while (parent1 != null) {
              if (parent1.getLevel() == level - 1) {
                parent = parent1;
                break;
              } else {
                parent1 = parent1.getParent();
              }
            }
          }
        }
        break;
      case COLUMN_DURATION:
        String sTrimmedField = sField.trim();
        if (sTrimmedField.endsWith(" ms"))
          duration = Long.parseLong(sTrimmedField.substring(0, sTrimmedField
              .length() - 3));
        else
          duration = Long.parseLong(sTrimmedField);
        break;
      case COLUMN_PERCENT:
        String trimmed = sField.trim();
        percent = Float.parseFloat(trimmed.substring(0, trimmed.length() - 2));
        break;
      }
      count++;
    }
    TableRecord timeRec = new TableRecord(sName, duration, percent, level);
    if (parent != null)
      parent.addSubRecord(timeRec);
    return timeRec;
  }

  /**
   * General structure evaluation. This evaluation is valid for all time
   * statistic tables. It evaluates duration.
   * 
   * @param rec
   */
  private void evaluateGeneralStructure(TableRecord rec) {
    List subRecs = rec.getSubRecords();
    if (subRecs == null)
      return;
    long duration = 0;
    float percent = 0;
    for (int i = 0; i < subRecs.size(); i++) {
      TableRecord subRec = (TableRecord) subRecs.get(i);
      duration += subRec.getDuration();
      percent += subRec.getPercent();
      evaluateGeneralStructure(subRec);
    }
    // sum of children duration and ratio must 
    // be less or equal to these of parent. 
    assertFalse(duration > rec.getDuration());
    // assume an acceptable difference in the percent calculation
    assertFalse(percent > rec.getPercent() + 0.01f);
  }

  /**
   * Evaluate complete specific structure. The evaluation relies on fixed node
   * names.
   * 
   * @param root
   */
  private void evaluateSpecificStructure(TableRecord root) {
    assertEquals(root.getName(), root.getPath());
    List subRecs = root.getSubRecords();
    assertEquals(subRecs.size(), 2); // 'Deploy Service' and 'All containers duration'
    for (int i = 0; i < subRecs.size(); i++) {
      TableRecord subRec = (TableRecord) subRecs.get(i);
      if (i == 0) {
        assertEquals(subRec.getName(), "Deploy Service");
        List dsDubList = subRec.getSubRecords();
        assertEquals(dsDubList.size(), 5); // 'Cluster Communication Duration'x2, 'JLinEE', 'Events' and 'Other'
        for (int j = 0; j < dsDubList.size(); j++) {
          TableRecord dsSubRec = (TableRecord) dsDubList.get(j);
          if (j == 0) {
            assertEquals(dsSubRec.getName(), "Cluster Communication Duration");
            List ccSubRecs = dsSubRec.getSubRecords();
            assertEquals(ccSubRecs.size(), 1); // 'preprocess' and 'validation duration'
            for (int k = 0; k < ccSubRecs.size(); k++) {
              TableRecord ccSubRec = (TableRecord) ccSubRecs.get(k);
              if (k == 0) {
                assertEquals(ccSubRec.getName(), "Send to all");
              }
            }
          }
          else if (j == 1) {
            assertEquals(dsSubRec.getName(), "Cluster Communication Duration");
          }
          else if (j == 2) {
            assertEquals(dsSubRec.getName(), "JLinEE");
            List jlSubRecs = dsSubRec.getSubRecords();
            assertEquals(jlSubRecs.size(), 2); // 'preprocess' and 'validation duration'
            for (int k = 0; k < jlSubRecs.size(); k++) {
              TableRecord jlSubRec = (TableRecord) jlSubRecs.get(k);
              if (k == 0) {
                assertEquals(jlSubRec.getName(), "preprocess");
              }
              else if (k == 1) {
                assertEquals(jlSubRec.getName(), "validation duration");
              }
            }
          }
          else if (j == 3) {
            assertEquals(dsSubRec.getName(), "Events");
            List evSubRecs = dsSubRec.getSubRecords();
            assertEquals(evSubRecs.size(), 2); // 'preprocess' and 'validation duration'
            for (int k = 0; k < evSubRecs.size(); k++) {
              TableRecord evSubRec = (TableRecord) evSubRecs.get(k);
              if (k == 0) {
                assertEquals(evSubRec.getName(), "incoming");
                List incSubRecs = evSubRec.getSubRecords();
                assertEquals(incSubRecs.size(), 4); // 'A_in', 'B2_in', 'B2_in' and 'C_in'
                for (int m = 0; m < incSubRecs.size(); m++) {
                  TableRecord incSubRec = (TableRecord) incSubRecs.get(m);
                  if (m == 0) {
                    assertEquals(incSubRec.getName(), "A_in");
                  }
                  else if (m == 1) {
                    assertEquals(incSubRec.getName(), "B2_in");
                  }
                  else if (m == 2) {
                    assertEquals(incSubRec.getName(), "B2_in");
                  }
                  else if (m == 3) {
                    assertEquals(incSubRec.getName(), "C_in");
                  }
                }
              }
              else if (k == 1) {
                assertEquals(evSubRec.getName(), "outgoing");
                List outSubRecs = evSubRec.getSubRecords();
                assertEquals(outSubRecs.size(), 1); // 'A_out'
                for (int m = 0; m < outSubRecs.size(); m++) {
                  TableRecord incSubRec = (TableRecord) outSubRecs.get(m);
                  if (m == 0) {
                    assertEquals(incSubRec.getName(), "A_out");
                  }
                }
              }
            }
          }
          else if (j == 4) {
            assertEquals(dsSubRec.getName(), "Other");
          }
        }
      }
      else if (i == 1) {
        assertEquals(subRec.getName(), "All containers duration");
        List allCTSubRecs = subRec.getSubRecords();
        assertEquals(allCTSubRecs.size(), 2); // 'servlet_jsp' and 'ejb'
        for (int j = 0; j < allCTSubRecs.size(); j++) {
          TableRecord ctRec = (TableRecord) allCTSubRecs.get(j);
          if (j == 0) {
            assertEquals(ctRec.getName(), "servlet_jsp");
            evaluateContainerOperations(ctRec);
          }
          else if (j == 1) {
            assertEquals(ctRec.getName(), "ejb");
            evaluateContainerOperations(ctRec);
          }
        }
      }
    }
  }

  /**
   * Specific evaluation for container operations.
   * 
   * @param coRec
   */
  private void evaluateContainerOperations(TableRecord coRec) {
    List coSubRecs = coRec.getSubRecords();
    assertEquals(coSubRecs.size(), 6); // deploy, needUpdate, makeUpdate, prepareUpdate, commitUpdate, remove
    for (int k = 0; k < coSubRecs.size(); k++) {
      TableRecord coSubRec = (TableRecord) coSubRecs.get(k);
      if (k == 0) {
        assertEquals(coSubRec.getName(), "deploy" + " (" + sTestApplicationFullName + ")");
        evaluateContainerOpWithSubOps(coSubRec, new int[] { 0 }, 0);
      }
      if (k == 1) {
        assertEquals(coSubRec.getName(), "needUpdate" + " (" + sTestApplicationFullName + ")");
        evaluateContainerOpWithSubOps(coSubRec, new int[] { 0 }, 0);
      }
      if (k == 2) {
        assertEquals(coSubRec.getName(), "makeUpdate" + " (" + sTestApplicationFullName + ")");
        evaluateContainerOpWithSubOps(coSubRec, new int[] { 0 }, 0);
      }
      if (k == 3) {
        assertEquals(coSubRec.getName(), "prepareUpdate" + " (" + sTestApplicationFullName + ")");
      }
      if (k == 4) {
        assertEquals(coSubRec.getName(), "commitUpdate" + " (" + sTestApplicationFullName + ")");
      }
      if (k == 5) {
        assertEquals(coSubRec.getName(), "remove" + " (" + sTestApplicationFullName + ")");
      }
    }
  }

  /**
   * Evaluate the sub operations of a container operation. These depend on the
   * progress listener of a container. current structure is:
   * +------------------------------------------+----------+----------+
   * |                 sub_1                    |   10 ms  |  0.79 %  |
   * +------------------------------------------+----------+----------+
   * |                     sub_1.1              |   4 ms   |  0.31 %  |
   * +------------------------------------------+----------+----------+
   * |                         sub_1.1.1        |   1 ms   |  0.07 %  |
   * +------------------------------------------+----------+----------+
   * |                         sub_1.1.2        |   3 ms   |  0.23 %  |
   * +------------------------------------------+----------+----------+
   * |                     sub_1.2              |   6 ms   |  0.47 %  |
   * +------------------------------------------+----------+----------+
   * |                 sub_2                    |   0 ms   |   0.0 %  |
   * +------------------------------------------+----------+----------+
   * @param ctRec
   */
  private void evaluateContainerOpWithSubOps(TableRecord ctRec,
      int[] niLevelSuffixes, int niDeepnes) {
    List ctSubRecs = ctRec.getSubRecords();
    if (ctSubRecs == null)
      return;
    // only two children are added per test
    assertEquals(ctSubRecs.size(), 2);
    for (int i = 0; i < ctSubRecs.size(); i++) {
      // create the name of the expected sub
      // record after the example shown above
      StringBuffer sSuffuxName = new StringBuffer("sub_");
      for (int j = 0; j < niLevelSuffixes.length; j++) {
        if (j != 0)
          sSuffuxName.append(".");
        sSuffuxName.append(niLevelSuffixes[j] + 1);
      }
      // check name match
      TableRecord subRec = (TableRecord) ctSubRecs.get(i);
      assertEquals(subRec.getName(), sSuffuxName.toString());
      // check time match - specific
      if (subRec.getName().equals("sub_1")) {
        assertEquals(subRec.getDuration(), 10);
      }
      if (subRec.getName().equals("sub_2")) {
        assertEquals(subRec.getDuration(), 0);
      }
      if (subRec.getName().equals("sub_1.1")) {
        assertEquals(subRec.getDuration(), 4);
      }
      if (subRec.getName().equals("sub_1.2")) {
        assertEquals(subRec.getDuration(), 6);
      }
      if (subRec.getName().equals("sub_1.1.1")) {
        assertEquals(subRec.getDuration(), 1);
      }
      if (subRec.getName().equals("sub_1.1.2")) {
        assertEquals(subRec.getDuration(), 3);
      }
      // first child on level contains sub nodes
      if (i == 0) {
        int[] niNewLevelIndexes = new int[niLevelSuffixes.length + 1];
        System.arraycopy(niLevelSuffixes, 0, niNewLevelIndexes, 0,
            niLevelSuffixes.length);
        niNewLevelIndexes[niNewLevelIndexes.length - 1] = 0;
        // call iteration for sub levels
        evaluateContainerOpWithSubOps(subRec, niNewLevelIndexes, niDeepnes + 1);
        niLevelSuffixes[niDeepnes]++;
      }
    }
  }

  /**
   * This is a class for the representation of a time statistics table record.
   * 
   * @author Todor Stoitsev
   */
  class TableRecord {
    private String sName;

    private String sPath;

    private long duration;

    private float percent;

    private int level;

    private List subRecords;

    private TableRecord parentRec;

    public TableRecord(String sName, long duration, float percent, int level) {
      this.sName = sName;
      this.sPath = sName;
      this.duration = duration;
      this.percent = percent;
      this.level = level;
    }

    public String getName() {
      return sName;
    }

    public String getPath() {
      return sPath;
    }

    public long getDuration() {
      return duration;
    }

    public float getPercent() {
      return percent;
    }

    private int getLevel() {
      return level;
    }

    private void setParent(TableRecord parent) {
      this.parentRec = parent;
      this.sPath = parent.getPath() + "/" + this.sName;
    }

    public TableRecord getParent() {
      return parentRec;
    }

    private void addSubRecord(TableRecord subRec) {
      if (subRecords == null) {
        subRecords = new ArrayList();
      }
      subRecords.add(subRec);
      subRec.setParent(this);
    }

    public List getSubRecords() {
      return this.subRecords;
    }

    public String toString() {
      return sPath;
    }
  }

  /**
   * Method iteratively creates sub configurations.
   * 
   * @param parent
   * @param niSubCount
   * @param niSubDeepness
   */
  private void createSubConfig(TimeStatisticNode parent, int niSubCount,
      int niSubDeepness) {
    int niDeep = niSubDeepness;
    for (int i = 0; i < niSubCount; i++) {
      ContainerOperationTimeStat subOperation = new ContainerOperationTimeStat(
          "SUB_[" + i + "]_opeartion", "com.sap/testApplication", 0, 10);
      TransactionTimeStat.addSubOperation(parent.getPath(), subOperation);
      if (niSubDeepness > 0)
        createSubConfig(subOperation, 1, niSubDeepness - 1);
    }
  }

  public void test2() throws Exception {
    TransactionTimeStat transactionStat = new TransactionTimeStat(
        "DeployTransaction", "com.sap/testApplication", 1);
    TransactionTimeStat transactionStat1 = new TransactionTimeStat(
        transactionStat);
    transactionStat.finish();
    transactionStat1.finish();
  }
}