package com.sap.engine.services.dc.cm.utils.measurement;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

public interface DMeasurement extends Serializable {
	
	String getDcName();
	
	String getTagName();
	
	Set getStatistics();
	
	List getChildrenMeasurments();
	
	Document toDocument();
	
	String toDocumentAsString();
	
	Boolean hasNewThreadStarted();

}
