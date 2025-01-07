/**************************************************************************** 
Apex Class Name  : EAS_AddressStandardizationRequest_H_HUM 
Version          : 1.0  
Created Date     : November 09, 2016
Function         : request helper for Enterprise Address Service
Test Class       : EAS_AddressStandardizationRequest_T_HUM    
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Adil Mohammed                               		11/09/2016                    Original Version
* Rajesh Keswani               74554  	            03/03/2017                 REQ - 299426 Add and Edit address section
***************************************************************************************************/
public with sharing class EAS_AddressStandardizationRequest_H_HUM 
{
  public EAS_AddressStandardRequest_H_HUM StandardizeAddressRequest {get;set;}
  public class EAS_AddressStandardRequest_H_HUM 
  {
    public String AddressLine1 = '';  
    public String AddressLine2 = '';  
    public String City = '';
    public String StateCode = '';
    public String ZipCode = '';
  }
}