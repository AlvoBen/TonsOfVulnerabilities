/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.typefac;

import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

/**
 * The EmployeeSdo is the {@link DataObject} that represents an employee. It has
 * a subset of the properties of the {@link Employee} entity bean.
 * @author D042807
 *
 */
public interface EmployeeSdo {
	
	public int getEmployeeId();
	public void setEmployeeId(int employeeId);

	public String getEmail();
	public void setEmail(String email);

	public String getSalutation();
	public void setSalutation(String salutation);

	public String getLastName();
	public void setLastName(String lastName);

	public String getFirstName();
	public void setFirstName(String firstName);
	
	/**
     * This property holds the referenced projects.
     * It is defined as a non-containment property because a {@link ProjectSdo}
     * is already contained by the {@link EmployeeUtilizationSdo}.
     * The Property {@link ProjectSdo#getEmployees()} is defined as the
     * opposite so that the maintenance of these properties by 
     * {@link ProjectSdo#setEmployees(List)} and
     * {@link EmployeeSdo#setProjects(List)}influences each other.
     * @return The related projects.
	 */
    @SdoPropertyMetaData(containment=false, opposite="employees")
	public List<ProjectSdo> getProjects();
    
    /**
     * Sets the related projects. Because this property has an opposite, it
     * influences {@link ProjectSdo#getEmployees()}.
     * Note that you may get back not the same List instance by
     * {@link #getProjects()}.
     * @param projects The new list of projects.
     */
	public void setProjects(List<ProjectSdo> projects);
    
    public int getVersion();
    public void setVersion(int version);

}
