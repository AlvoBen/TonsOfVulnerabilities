﻿

/*
This file is generated by Code Generator
for CIMClass SAP_ITSAMSAPTimeZone
WARNING:DO NOT CHANGE THE CODE MANUALLY
*/

package com.sap.i18n.itsam.compositedata;	


public class SAP_ITSAMSAPTimeZone{

private String TimeZoneID=null;
	
private String DST=null;
	
private String TimeZoneText=null;
	
private String DiffFromUTC=null;
	
 /**
   * constructor that does the defaulting of those attributes which have default values given
   */
public SAP_ITSAMSAPTimeZone(){

}

public SAP_ITSAMSAPTimeZone(String TimeZoneID,String DST,String TimeZoneText,String DiffFromUTC){

this.TimeZoneID = TimeZoneID;
	
this.DST = DST;
	
this.TimeZoneText = TimeZoneText;
	
this.DiffFromUTC = DiffFromUTC;
	
}

/*

@return String
*/
public String getTimeZoneID() 
{
return this.TimeZoneID;
}

/*

@return String
*/
public String getDST() 
{
return this.DST;
}

/*

@return String
*/
public String getTimeZoneText() 
{
return this.TimeZoneText;
}

/*

@return String
*/
public String getDiffFromUTC() 
{
return this.DiffFromUTC;
}
	
}
