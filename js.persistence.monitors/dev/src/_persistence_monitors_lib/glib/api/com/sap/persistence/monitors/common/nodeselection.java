package com.sap.persistence.monitors.common;

import java.util.ArrayList;
import java.util.HashSet;


public interface NodeSelection {
 

public ArrayList<String> getNodes(); 

public void setNodes(ArrayList<String> nodes) ;

public void addNode(String node);

public boolean isAllNodes(); 

public void setAllNodes(boolean allNodes);

public HashSet<String> getNodeSet();




 
}
