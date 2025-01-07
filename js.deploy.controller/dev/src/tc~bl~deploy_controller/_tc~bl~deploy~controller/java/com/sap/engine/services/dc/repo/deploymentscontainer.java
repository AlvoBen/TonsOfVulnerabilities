package com.sap.engine.services.dc.repo;

import java.util.Set;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-15
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DeploymentsContainer {

	public Sdu[] getAllDeployments();

	public Sdu getDeployment(SduId sduId);

	public Sdu getDeployment(SduRepoLocation sduRepoLocation);

	public Set getDeploymentsWithNoReferencesTo();

	public void addDeployment(Sdu sdu);

	public void removeDeployment(Sdu sdu);

	public void modifyDeployment(Sdu sdu);

	public Set getDependingFrom(Sdu sdu);

	public Set getDependingFrom(SduId sduId);

	public Set getRecursiveAllDependingFrom(Sdu sdu);

	public Set getRecursiveAllDependingFrom(SduId sduId);

	public void clear();

	public void init(Set allDeployments);

	public SduVisitor getInitialSduLoaderVisitor();

}
