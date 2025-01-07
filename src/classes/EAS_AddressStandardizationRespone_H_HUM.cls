/**************************************************************************** 
Apex Class Name  : EAS_AddressStandardizationRespone_H_HUM 
Version          : 1.0  
Created Date     : November 09, 2016
Function         : response helper for Enterprise Address Service
Test Class       : EAS_AddressStandardizationRespone_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Adil Mohammed                               11/09/2016                    Original Version
* Rajesh Keswani               74554  	      03/03/2017                 REQ - 299426 Add and Edit address section
***************************************************************************************************/
public with sharing class EAS_AddressStandardizationRespone_H_HUM 
{
  public EAS_AddressStandardResponse_H_HUM StandardizeAddressResponse;
  public class EAS_AddressStandardResponse_H_HUM 
  {
    public String SuccessFlag = '';  
    public String AddressLine1 = '';  
    public String AddressLine2 = '';
    public String City = '';
    public String StateCode = '';
    public String ZipCode = '';
    public String StateID = '';
    public String ZipCodePlus = '';
    public String CountyName = '';
    public String CountyID = '';
    public string Valid ;
    public String ChangeIndicator = '';
    public String ErrorCode = '';
    public String ErrorMessage = '';
  }
}