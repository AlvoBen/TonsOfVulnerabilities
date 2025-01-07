/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_DTO_HUM
Version          : 1.0
Created Date     : 09/04/2018
Function         : DTO to store Claim send statement Info
Test Class       : Claim_Send_Statement_DTO_T_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Vijaya Koriprolu                           08/28/2018          Original Version
***************************************************************************************************/


public with sharing class Claim_Send_Statement_DTO_HUM
{ 

public string sSuccessFlag{get;set;}
public String sAddressLine1{get;set;}  
public string sAddressLine2{get;set;} 
public string sCity{get;set;} 
public String sStateCode{get;set;}  
public String sZipCode{get;set;}  
public String sStateID{get;set;}  
public String sZipCodePlus{get;set;}
public String sCountyName{get;set;}  
public String sCountyID{get;set;}  
public String sValid{get;set;}  
public String sChangeIndicator{get;set;}  
public String sErrorCode{get;set;}  
public String sErrorMessage{get;set;}  
}