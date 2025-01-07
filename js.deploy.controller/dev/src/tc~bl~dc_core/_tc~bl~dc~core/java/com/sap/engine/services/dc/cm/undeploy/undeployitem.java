package com.sap.engine.services.dc.cm.undeploy;

import java.util.Map;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sda;

public interface UndeployItem extends GenericUndeployItem{

	public Sda getSda();

	public void setSda(Sda sda);

	public Set getDepending();

	public void addDepending(UndeployItem sdaUndeployItem);

	public void removeDepending(UndeployItem sdaUndeployItem);

	public Set getDependingOnThis();

	public void addDependingOnThis(UndeployItem sdaUndeployItem);

	public void removeDependingOnThis(UndeployItem sdaUndeployItem);

	public void removeAllDependingOnThis();

	public Map getProperties();

	public void setProperties(Map propsMap);
	
}
