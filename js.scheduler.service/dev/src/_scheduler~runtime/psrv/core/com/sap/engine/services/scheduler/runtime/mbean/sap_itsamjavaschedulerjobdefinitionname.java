﻿

/*
This file is generated by Code Generator
for CIMClass SAP_ITSAMJavaSchedulerJobDefinitionName
WARNING:DO NOT CHANGE THE CODE MANUALLY
*/

package com.sap.engine.services.scheduler.runtime.mbean;	

import java.util.Date;
/* 
ManagedElement is an abstract class that provides a common superclass (or top of the inheritance tree) for the non-association classes in the CIM Schema. 
@version 2.7.0	
*/	

public class SAP_ITSAMJavaSchedulerJobDefinitionName{

private String ApplicationName=null;
	
private String JobName=null;
	
public SAP_ITSAMJavaSchedulerJobDefinitionName(){

}

public SAP_ITSAMJavaSchedulerJobDefinitionName(String ApplicationName,String JobName){

this.ApplicationName = ApplicationName;
	
this.JobName = JobName;
	
}

/*

@return String
*/
public String getApplicationName() 
{
return this.ApplicationName;
}

/*

@return String
*/
public String getJobName() 
{
return this.JobName;
}
	
}
