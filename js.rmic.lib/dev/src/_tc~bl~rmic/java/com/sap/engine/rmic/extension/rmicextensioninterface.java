package com.sap.engine.rmic.extension;

import com.sap.engine.rmic.RMIC;

import java.util.Vector;

public interface RmicExtensionInterface {

  public boolean postProcess(RMIC ref, Vector toCompile, String type);

  
}
