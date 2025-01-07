package com.sap.engine.lib.constants;

import java.util.AbstractMap;

import javax.naming.RefAddr;

public class MapRefAddr extends RefAddr {

  private static final long serialVersionUID = 1L;
  
  //ensure that toString() is human readable	
  private AbstractMap props;	
 	
  public MapRefAddr(AbstractMap props) {
	super(AbstractMap.class.getName());
	this.props = props;
  }
	
  public Object getContent() {
	return props;  
  }
}
