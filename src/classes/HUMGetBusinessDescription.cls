/******************************************************************************************************************************
Apex Class Name  : HUMGetBusinessDescription
Version          : 1.0
Created Date     : JUL 10 2015
Function         : Used to get the the BusinessDescription for different objects
Test Class       : HUMGetBusinessDescriptionTest

Modification Log :
*  Developer            Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*  Manish Kumar         23831               06/10/2015           Original Version
*  Pooja Wadhwa         23888               06/12/2015           Updated Version
*  Kritika Popat        24093               06/18/2015           Modified method getPMBusinessDescription for Defect 187428.0001
*  Kritika Popat        24147               06/22/2015           Modified method getPMBusinessDescription for Defect 187756.0001
*  Harshith Mandya      24543               07/03/2015           Updated code for Pefomance Issue
*  Payal Yadav                              03/15/2016           Updated the code as part of REQ-215094
*  Mohammed Noor                            06/30/2020			 Fix CheckMarx Medium Issue.
*  Santhi Mandava                           01/29/2021           User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
*********************************************************************************************************************************/

public with sharing class HUMGetBusinessDescription
{
    /*
    * Method Name   :    getPMBusinessDescription
    * Description   :    Used to get the the BusinessDescription for PolicyMembers
    * Return Type   :    Boolean
    * Parameters    :    Id
    */
    public static Boolean getPMBusinessDescription(Id sPolicyMemId)
    {
        String sProductCode = '';
        String sMajorLOB = '';
        String sBusinessDesc = HUMConstants__c.getInstance('HUMRSOMedicare').StringValue__c;
        
        Policy_Member__c objPM = [Select Policy__r.Product__r.ProductCode, Policy__r.Product__r.Major_LOB__c
                                         From Policy_Member__c 
					 Where Id =: sPolicyMemId];
       
        
        if(NULL == objPM.Policy__r || NULL == objPM.Policy__r.Product__r)   return false;
        
        sProductCode = objPM.Policy__r.Product__r.ProductCode;
        sMajorLOB = objPM.Policy__r.Product__r.Major_LOB__c;
        
        if(String.isBlank(sProductCode) || String.isBlank(sMajorLOB) || String.isBlank(sBusinessDesc))  return false;
        
        sBusinessDesc = '%' + sBusinessDesc + '%';
        
        List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c FROM Reference_Business_Line_Product_Grouping__c
                                                                                                 Where ProductCode__c = :sProductCode And  Major_LOB__c = :sMajorLOB AND 
                                                                                                 Business_Description__c LIKE : sBusinessDesc];
        
        if(NULL!= lstRefBusinessLineProdGroups && !lstRefBusinessLineProdGroups.isEmpty())  return true;
        return false;
    }
    
    /*
    * Method Name   :    getCaseLaunchBusinessDescription
    * Description   :    Used to get the the BusinessDescription for Cases
    * Return Type   :    String
    * Parameters    :    Id,Case,Boolean
    */
    public static String getCaseLaunchBusinessDescription(Id sCaseId,Case objCaseRec,Boolean isCaseDetail)
    {
        String sProductCode = '';
        String sMajorLOB = '';
        String sBusinessDesc = '';
        
        try
		{
            Case objCase;
            if(!isCaseDetail)
            {
                // Getting "Product Code" and "Major LOB" from Product record related to Case
                objCase = [Select Policy_Member__r.Policy__r.Product__r.ProductCode, Policy_Member__r.Policy__r.Product__r.Major_LOB__c
                                    From Case Where Id =: sCaseId];
            }
            else {
                objCase = objCaseRec;
            }
			
			if(objCase.Policy_Member__r != null && 
			objCase.Policy_Member__r.Policy__r != null && 
			objCase.Policy_Member__r.Policy__r.Product__r != null)
			{
				sProductCode = objCase.Policy_Member__r.Policy__r.Product__r.ProductCode;
				sMajorLOB = objCase.Policy_Member__r.Policy__r.Product__r.Major_LOB__c;
				
				if(String.isNotBlank(sProductCode) && String.isNotBlank(sMajorLOB))
				{
					List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = new List<Reference_Business_Line_Product_Grouping__c>();
					
					/* Fetching Business_Description from "Reference_Business_Line_Product_Grouping__c" Custom object
					Querying from custom setting considering future it'll be moved to object also it runs faster */
					
					lstRefBusinessLineProdGroups = [SELECT Business_Description__c FROM Reference_Business_Line_Product_Grouping__c
														   Where ProductCode__c = :sProductCode And  Major_LOB__c = :sMajorLOB];
					
					if(lstRefBusinessLineProdGroups!=null && !lstRefBusinessLineProdGroups.isEmpty() && String.isNotBlank(lstRefBusinessLineProdGroups[0].Business_Description__c))
					{
						sBusinessDesc = lstRefBusinessLineProdGroups[0].Business_Description__c;
					}
				}
			}
		}
		catch(Exception ex)	//try-catch block added to fix CheckMarx error - Improper Error Handling.
		{
			return sBusinessDesc;	//returning the result and not logging error as this method is called from with in Constructor
		}
        return sBusinessDesc;
    }
}