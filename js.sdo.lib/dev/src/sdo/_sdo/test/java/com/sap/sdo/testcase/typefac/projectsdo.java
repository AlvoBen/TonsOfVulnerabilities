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

import java.util.Date;
import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

/**
 * The ProjectSdo is the {@link DataObject} that represents a project. It has
 * a subset of the properties of the {@link Project} entity bean.
 * @author D042807
 *
 */
public interface ProjectSdo {
	
	public int getProjectId();
	public void setProjectId(int projectId);

	public int getStatus();
	public void setStatus(int status);

	public String getDescription();
	public void setDescription(String description);

	public Date getStartDate();
	public void setStartDate(Date startDate);

	public String getTitle();
	public void setTitle(String title);

	public Date getEndDate();
	public void setEndDate(Date endDate);
	
    /**
     * This property holds the referenced employees.
     * It is defined as a non-containment property because an {@link EmployeeSdo}
     * is already contained by the {@link EmployeeUtilizationSdo}.
     * The Property {@link EmployeeSdo#getProjects()} is defined as the
     * opposite so that the maintenance of these properties by 
     * {@link ProjectSdo#setEmployees(List)} and
     * {@link EmployeeSdo#setProjects(List)}influences each other.
     * @return The related projects.
     */
	@SdoPropertyMetaData(containment=false, opposite="projects")
	List<EmployeeSdo> getEmployees();

    /**
     * Sets the related employees. Because this property has an opposite, it
     * influences {@link EmployeeSdo#getProjects()}.
     * Note that you may get back not the same List instance by 
     * {@link #getEmployees()}.
     * @param projects The new list of projects.
     */
	void setEmployees(List<EmployeeSdo> employees);

    public int getVersion();
    public void setVersion(int version);

}
