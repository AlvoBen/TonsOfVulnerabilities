package com.sap.engine.services.dc.repo;

import java.util.Set;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Sda extends Sdu {

	/**
	 * @return <code>ScaId</code> which contains the current Sda. If the SDA was
	 *         deployed as a top level component then the method returns null.
	 */
	public ScaId getScaId();

	public SoftwareType getSoftwareType();

	public Set getDependencies();

	public void addDependency(Dependency dependency);

	/**
	 * 
	 * @return set with all <code>Dependency</code> from
	 */
	public Set getDependingFrom();

	public void addDependingFrom(Dependency dependingFrom);
	
	/*
	 * In hot-fix SDA scenario - there is a need parent SCA to be deleted 
	 * if this one doesn't exist neither in Repository or in the batch 
	 * in order not to persist wrong data for SCA id of the SDA (see RepositoryDeploymentObserverVisitor.class)     
	 */
	public void setScaId(ScaId scaId);
}
