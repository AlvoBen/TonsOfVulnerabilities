﻿

/*
This file is generated by Code Generator
for CIMClass SAP_ITSAMSAPCalConfig
WARNING:DO NOT CHANGE THE CODE MANUALLY
*/

package com.sap.i18n.itsam.compositedata;	

/* 
ManagedElement is an abstract class that provides a common superclass (or top of the inheritance tree) for the non-association classes in the CIM Schema. 
@version 3.3	
*/	

public class SAP_ITSAMSAPCalConfig{

private String DateFormat=null;
	
private String IslamicDate=null;
	
private String GregorianDate=null;
	
public SAP_ITSAMSAPCalConfig(){

}

public SAP_ITSAMSAPCalConfig(String DateFormat,String IslamicDate,String GregorianDate){

this.DateFormat = DateFormat;
	
this.IslamicDate = IslamicDate;
	
this.GregorianDate = GregorianDate;
	
}

/*
Description Missing
@return String
*/
public String getDateFormat() 
{
return this.DateFormat;
}

/*
Description Missing
@return String
*/
public String getIslamicDate() 
{
return this.IslamicDate;
}

/*
Description Missing
@return String
*/
public String getGregorianDate() 
{
return this.GregorianDate;
}
	
}

