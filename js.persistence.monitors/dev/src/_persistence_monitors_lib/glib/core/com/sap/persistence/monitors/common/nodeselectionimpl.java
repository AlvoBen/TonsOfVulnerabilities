package com.sap.persistence.monitors.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class NodeSelectionImpl implements NodeSelection {
	
 ArrayList<String> specifiedNodes;
 boolean allNodes;

public ArrayList<String> getNodes() {
	return specifiedNodes;
}

public void setNodes(ArrayList<String> nodes) {
	this.specifiedNodes = nodes;
}

public void addNode(String node){
	this.specifiedNodes.add(node);
}

public boolean isAllNodes() {
	return allNodes;
}

public void setAllNodes(boolean allNodes) {
	this.allNodes = allNodes;
}


public NodeSelectionImpl() {
	allNodes = true;
	specifiedNodes = null;
	
}

public NodeSelectionImpl(String node){
	allNodes = false;
	specifiedNodes = new ArrayList<String>(1);
	specifiedNodes.add(node);
}


public HashSet<String> getNodeSet(){
	HashSet<String> relevantNodeSet = new HashSet<String>();
	
	if (specifiedNodes == null){
		return relevantNodeSet;
	}
	
	Iterator<String> it = specifiedNodes.iterator();
	while (it.hasNext()){
		relevantNodeSet.add(it.next());
	}
	
	return relevantNodeSet;
}

 
}
