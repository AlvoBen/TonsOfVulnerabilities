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
 * The EmployeeUtilizationSdo is the container for all projects and all
 * employees.
 * @author D042807
 *
 */
public interface EmployeeUtilizationSdo {
	
    /**
     * Returns the List of all employees.
     * This property is defined as a containment property.
     * @return The list of all employees.
     */
    @SdoPropertyMetaData(containment=true)
	List<EmployeeSdo> getEmployees();
    
    /**
     * Sets the new List of all employees.
     * Note that you may get back not the same List instance by
     * {@link #getEmployees()}.
     * @param employees The new list.
     */
	void setEmployees(List<EmployeeSdo> employees);
	
    /**
     * Returns the List of all projects.
     * This property is defined as a containment property.
     * @return The list of all projects.
     */
	@SdoPropertyMetaData(containment=true)
	List<ProjectSdo> getProjects();

    /**
     * Sets the new List of all projects.
     * Note that you may get back not the same List instance by
     * {@link #getProjects()()}.
     * @param projects The new list.
     */
	void setProjects(List<ProjectSdo> projects);

}
