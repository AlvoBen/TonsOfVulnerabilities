package com.sap.engine.core.service630.container;

import com.sap.engine.frame.container.monitor.LibraryMonitor;

import java.util.Properties;

/**
 * Implements ComponentMonitor.
 *
 * @see com.sap.engine.frame.container.monitor.LibraryMonitor
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class LibraryWrapper extends ComponentWrapper implements LibraryMonitor {

  LibraryWrapper(MemoryContainer memoryContainer, Properties props) {
    super(memoryContainer, props);
  }

  //constructor for core_lib
  LibraryWrapper(MemoryContainer memoryContainer, String[] jars) {
    super(memoryContainer, jars);
  }

  String getType() {
    return "library";
  }

  byte getByteType() {
    return TYPE_LIBRARY;
  }

}

