/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.rmi_p4.server.command;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.ObjectEntry;
import com.sap.engine.services.rmi_p4.Skeleton;
import com.sap.engine.services.rmi_p4.server.P4ObjectBrokerServerImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This command lists exported in P4 protocol remote objects.
 * By default it counts similar objects and skip empty ones.
 * @see #getHelpMessage()
 * 
 * @author I041949
 */
public class ListP4Objects implements Command {
  
  private Hashtable portableRO = null;
  private boolean shortFormat = true;
  private boolean countEntries = true;
  private boolean filterByInterface = false;
  private String filteredInterfaceName = null;
  private int entryIndex = -1;

  private P4ObjectBroker broker = P4ObjectBroker.init();

  private String formatResult(String[] args) {
    //Get entries from Object Manager
    ObjectEntry[] entries = broker.objManager.getEntries();
    
    //Initialize parameters of P4_Objects command if there are parameters
    initializeParameters(args);
    
    if (countEntries) {
      return countByGroups(entries);
    } else {
      return listEntries(entries);
    }
  }
  
  /**
   * Initialize parameters of telnet command if there are any.
   * @param args Parameters as array of strings 
   */
  private void initializeParameters(String[] args) {
    //First restore default values:
    shortFormat = true;
    countEntries = true;
    filterByInterface = false;
    entryIndex = -1;
    
    if (args != null && args.length > 0){
      for (int i=0; i<args.length; i++){
        if (args[i].equals("-c")) {
          countEntries = true;
        }
        if (args[i].equals("-f")) {
          shortFormat = false;
          countEntries = false;
        }
        if (args[i].equals("-l")) {
          shortFormat = true;
          countEntries = false;
        }
        if (args[i].equals("-p")) {
          if(broker.isServerBroker()){
            portableRO = ((P4ObjectBrokerServerImpl)broker).getPortableROsCopy();
          }
        }
        if (args[i].equals("-i")) {
          if ((i < args.length) && (!args[i+1].startsWith("-"))) { 
            //means: if this is not the last argument and the next one does not start with "-"
            filteredInterfaceName = args[i+1];
            filterByInterface = true;
            i++;
          } 
        }
        if (args[i].equals("-n")) {
          if ((i < args.length) && (!args[i+1].startsWith("-"))) { 
            //means: if this is not the last argument and the next one does not start with "-"
            try {
            entryIndex = Integer.parseInt(args[i+1]);
            } catch (NumberFormatException e) { //$JL-EXC$ 
              //TODO - tell that it was unable to initialise this parameter. Currently ignore it.
            }
            i++;
          }
        }
      }
    }
  }
  
  /**
   * Format: p4_objects -f or p4_objects -l
   * Lists all not empty entries with full/short information for them. 
   * @param entries Entries exported in P4 Object Manager.
   * @return List of all not empty entries in specified format.
   */
  private String listEntries(ObjectEntry[] entries){
    String result = "  -*         List P4 Objects         *- \r\n";
    if(entries != null && entries.length > 0) {
      for(int i = 0; i < entries.length; i++) {
        if ((entryIndex >= 0) && (entryIndex != i)) {
          // filter by index is switched on and this is NOT the searched entry
          continue;
        }
        boolean found = false;
        String currentResult = ""; 
        if((entries[i].reference != null) && (entries[i].reference instanceof WeakReference)){
          currentResult += "P4 Object #" + i + "\r\n";
          Object obj =   ((WeakReference)entries[i].reference).get();
          if (!shortFormat) {
            currentResult += "\t> Reference:\r\n                 " + obj + "\r\n";
            currentResult += "\t> ObjectEntry details: \t links=" + entries[i].links + "; isRreferentNull=" + entries[i].isReferentNull() + "; isValid=" + entries[i].isValid() + "; counter=" + entries[i].getCounter() + "\r\n";
          }
          if (obj != null) {
            currentResult += "\t> " + obj.getClass() + "\r\n"; //e.g.: class com.sap.engine.services.rmi_p4.monitor.P4RuntimeControl
            if(obj instanceof Skeleton){
              String[] implInfo = ((Skeleton)obj).getImplemntsObjects();
              if(implInfo != null && implInfo.length > 0){
                currentResult += "\t> Implementations: \r\n";
                for (int j = 0; j < implInfo.length; j++) {
                  currentResult += "\t\t|- impl[" + j + "]=" + implInfo[j] + "\r\n"; //e.g.: |- impl[0]=com.sap.engine.services.jndi.RemoteServiceReference
                  if (filterByInterface && implInfo[j].equals(filteredInterfaceName)) {
                    found = true;
                  }
                }
              }
            }
          }
        } else {
          if (!shortFormat) {
            currentResult += "P4 Object #" + i + "\r\n";
            currentResult += "|---- next: " + entries[i].reference + "\r\n";
          }
        }
        if (entries[i].connectionStatistics != null && entries[i].connectionStatistics.size() > 0) {
          currentResult += "\tConnection statistic: \r\n\t" + entries[i].connectionStatistics + "\r\n";
        }
        if (!shortFormat) {
          currentResult += "-------------\r\n";
        }
        if (filterByInterface && !found) { // filtering is turned ON, and the object doesn't implement searched interface 
          // skip this entry - do not add to the result
        } else {
          result += currentResult;
        }
      }
    }

    if(portableRO != null) {
      Enumeration keys = portableRO.keys();
      result += "\r\n\r\n  -*    Portable Remote Objects      *- \r\n";
      while(keys.hasMoreElements()) {
        boolean found = false;
        String currentResult = ""; 
        Object key = keys.nextElement();
        Object value = portableRO.get(key);
        currentResult += "[" + key + "]\r\n";
        if(value instanceof WeakReference){
          Object oo = ((WeakReference)value).get();
          String[] impls = null;
          if(oo instanceof Skeleton){
            impls = ((Skeleton)oo).getImplemntsObjects();
          }
          if (!shortFormat) {
            currentResult += "  |-> value : [" + ((WeakReference)value).get() + "]\r\n";
          }
          if(impls != null){
            for (int i = 0; i < impls.length; i++) {
              currentResult += "    |--implements: " + impls[i] + "\r\n";
              if (filterByInterface && impls[i].equals(filteredInterfaceName)) {
                found = true;
              }
            }
          }
        } else {
          if (!shortFormat) {
            currentResult += "  |-> value : [" + value + "]\r\n";
          }
          String[] impls = null;
          if(value instanceof Skeleton){
            impls = ((Skeleton)value).getImplemntsObjects();
            if(impls != null){
              for (int i = 0; i < impls.length; i++) {
                currentResult += "    |--implements: " + impls[i] + "\r\n";
                if (filterByInterface && impls[i].equals(filteredInterfaceName)) {
                  found = true;
                }
              }
            }
          }
        }
        if (!shortFormat) {
          currentResult += "------------------------------\r\n";
        }
        if (filterByInterface && !found) { // filtering is turned ON, and the object doesn't implement searched interface 
          // skip this entry - do not add to the result
        } else {
          result += currentResult;
        }
      }
    }
    
    return result;
  }

  /**
   * Count exported objects with equal remote interfaces implementation.
   * Display result in groups with count for every group of objects. 
   * Many groups have only one object exported of their remote interface.
   * @param entries Entries exported in P4 Object Manager.
   * @return
   */
  private String countByGroups(ObjectEntry[] entries) {
    String result = "";
    
    Map<String, Integer> countStructure = new TreeMap();
    if(entries != null && entries.length > 0) {
      for(int i = 0; i < entries.length; i++) {
        boolean found = false;
        if((entries[i].reference != null) && (entries[i].reference instanceof WeakReference)){
          Object obj =   ((WeakReference)entries[i].reference).get();
          if (obj != null) {
            String entryClass = obj.getClass().toString() + "\r\n";
            if(obj instanceof Skeleton){
              String[] implInfo = ((Skeleton)obj).getImplemntsObjects();
              if(implInfo != null && implInfo.length > 0){
                for (int j = 0; j < implInfo.length; j++) {
                  entryClass += "\t|- impl[" + j + "]=" + implInfo[j] + "\r\n"; //e.g.: |- impl[0]=com.sap.engine.services.jndi.RemoteServiceReference
                  if (filterByInterface && implInfo[j].equals(filteredInterfaceName) ){
                    found = true;
                  }
                }
              }
            }
            if (filterByInterface && !found) {
              // Do nothing - don't append this object to putput
            } else {
              if (! countStructure.containsKey(entryClass)){
                countStructure.put(entryClass, 1);              
              } else {
                int currentCount = countStructure.get(entryClass);
                countStructure.put(entryClass, (currentCount+1) );
              }
              
            }
          }
        }
      }
      result += "  -*       Counted P4 Objects        *- \r\n";
      result += listCounted(countStructure);
      result += "\r\n\r\nCapacity: " + entries.length + "\r\n";
    }

    if(portableRO != null) {
      countStructure.clear();
      Enumeration keys = portableRO.keys();
      
      while(keys.hasMoreElements()) {
        boolean found = false;
        Object key = keys.nextElement();
        Object value = portableRO.get(key);
        String entryClass = key.toString();
        if (entryClass.contains("@")) {//Ignore different hash codes
          entryClass = entryClass.substring(0, entryClass.indexOf("@")); 
        }
        entryClass = entryClass + "\r\n";
        if(value instanceof WeakReference){
          Object oo = ((WeakReference)value).get();
          String[] impls = null;
          if(oo instanceof Skeleton) { //Separate DynamicSkeletons for different implementations
            impls = ((Skeleton)oo).getImplemntsObjects();
            if(impls != null){
              for (int i = 0; i < impls.length; i++) {
                entryClass += "\t|- impl[" + i + "]=" + impls[i] + "\r\n"; 
              }
            }
          }
        }
        if (filterByInterface && !found) {
          // Do nothing - don't append this object to putput
        } else {
          if (!countStructure.containsKey(entryClass)) {
            countStructure.put(entryClass, 1);
          } else {
            int currentCount = countStructure.get(entryClass);
            countStructure.put(entryClass, (currentCount + 1));
          }
        }
        
      }
      result += "\r\n  -* Counted Portable Remote Objects *- \r\n";
      result = result + listCounted(countStructure);
    }
    return result;
  }

  /* Non Java-Doc
   * This method can can be modified to list top X exported objects, 
   * but not only the maximum one.   
   */
  private String listCounted(Map<String, Integer> countStructure) {
    String DynamicSkeletonClass ="com.sap.engine.services.rmi_p4.P4DynamicSkeleton";
    String result = "";
    Set<String> it = countStructure.keySet(); 
    int dynSkeletons = 0;
    int maxCount = 0;
    String maxClass = "";
    int currentCount;
    for (String key : it) {
      currentCount = countStructure.get(key);
      result += key + " \t> Count: " + currentCount + "\r\n\r\n";
      
      if (key.contains(DynamicSkeletonClass)) {
        dynSkeletons += currentCount;
      }
      if (currentCount > maxCount) {
        maxCount = currentCount;
        maxClass = key;
      }
    }
    result += "Total " + DynamicSkeletonClass + ": " + dynSkeletons + "\r\n\r\n";
    result += "Maximum counted: " + maxCount + "\r\n" + maxClass + "\r\n\r\n";
    return result;
  }

  public void exec(Environment environment, InputStream input, OutputStream output, String[] strings) {
    PrintWriter pw = new PrintWriter(output, true);
    try {
        pw.println(formatResult(strings));
        portableRO = null;
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(ostr));
        pw.println("ERROR: " + ostr);
        pw.println(getHelpMessage());
      }
  }

  public String getName() {
    return "p4_objects";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  /**
   * This method display help message:
   * Command lists all exported in p4 objects with corresponding information.
   * Usage: 
   *  p4_objects    Count and display in groups exported entries;
   *  p4_objects -l Lists all entries without counting;
   *  p4_objects -f Full information of all entries without counting;
   *  p4_objects -p Include also portable remote objects that are exported.
   *  
   * @return Help message for this telnet command.
   */
  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Lists all exported in p4 objects with corresponding information " +
            nl + "Usage: " + 
            nl + getName() + "    Count and display in groups exported entries" +
            nl + getName() + " -l Lists all entries without counting" +
            nl + getName() + " -f Full information of all entries without counting" +
            nl + getName() + " -p Include also portable remote objects that are exported" +
            nl + getName() + " -i Full class name of remote interface to be listed";
  }
}
