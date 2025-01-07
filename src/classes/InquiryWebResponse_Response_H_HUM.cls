/*******************************************************************************************************************************
Apex Class Name : InquiryWebResponse_Response_H_HUM.cls
Version         : 1.0
Created On      : 8/24/2020
Function        : Class contains Response Nodes for Inquiry Web Response Service.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 				                     08/27/2020                REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look) 
* Ashish Kumar										                     04/01/2021				   Added @AuraEnabled to all variables for Lightning strides application    
********************************************************************************************************************************/
public with sharing class InquiryWebResponse_Response_H_HUM 
{
    public DRWebResponse DRWebResponse; 
    
	public class DRWebResponse{
    @AuraEnabled public String WebQuestion = '';   
    @AuraEnabled public String ClaimNumber = '';
    @AuraEnabled public String ClaimDOS = ''; 
    @AuraEnabled public String SelectAResponse = '';
    @AuraEnabled public String WebResponse = '';
	@AuraEnabled public String AdditionalDetails = '';
  }        
}