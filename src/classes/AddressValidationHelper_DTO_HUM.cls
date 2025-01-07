/**************************************************************************** 
Apex Class Name  : AddressValidationHelper_DTO_HUM 
Version          : 1.0  
Created Date     : November 15, 2016
Function         : DTo For Address update
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rajesh Keswani           74554                03/10/2017                  Original Version
* Sharan Shanmugam							 	07/27/2017					REQ - 323542 : Exception handling variable
***************************************************************************************************/

public with sharing class AddressValidationHelper_DTO_HUM {
 public EAS_AddressStandardizationRequest_H_HUM.EAS_AddressStandardRequest_H_HUM addressReq ;
 public string enterpriseID ; 
 public string statusValue ;
 public string addresstype ;
 public string addressValidatedFlag;
 public string overrideReasonCode;
 public string networkID;  
 public string sRecordId;  
}