/**************************************************************************** 
Apex Class Name  : HUMCaseCloneOverrideExtension
Version          : 1.0
Created Date     : OCTOBER 01 2014
Function         : This class is used as an extension of the Case controller to override the Clone button
Test Class       : HUMCaseCloneOverrideExtensionTest
****************************************************************************
Modification Log:

* Developer Name               Code Review                            Date                    Description
*-------------------------------------------------------------------------------------------------------------------------
* Manuel Johnson                  18329                            10/01/2014                   Original Version
* SuryaKumari Medicherla          19911                            12/10/2014                   Code modified to replace custom settings with custom object (REQ-72809)
* Avinash Chouey                  29930                            02/16/2016                   removed hardcoded case fields name by getting fields API name from custom setting HUMCaseCloneFields__c replaced labels with constants at  labels at line no 52 to 54
* Avinash Choubey                 29930                            02/18/2016                   removed try catch from Constructor as there are no scenarios of exception
* Avinash Choubey                 30426                            03/10/2016                   reset Classification and ontents field if CT is not same as original case line no 78 to 87 defect# 219084
* Avinash Choubey                 30485                            03/15/2016                   fix for Defect 219161
* Avinash Choubey                 33344                            07/08/2016                   removed Classification and Intent picklist fields usage (REQ - 255151)  
* Avinash Choubey                 33810                            07/27/2016                   Defect 235275 fix           
* Prachi Bhasin                                                    08/17/2016                   Fix for defect 236597.0001
* Sharan Shanmugam                                                 11/07/2016                   Added logic in cloneCase method to pick only active queues.
* Sharan Shanmugam                50226                            11/30/2106                   Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                                Case/Task/Interaction/Attachment to Work Queue Setup And Queue View
* Sharan Shanmugam                50226                            02/07/2017                   Added logic in clonecase method to set record types for the cloned case as part of REQ - 296936.                                                                                            
* Pallavi Ravishankar             80484                            03/30/2017                   Code modified as per new cict model - REQ 306360
* Sumit Sharma					  89120							   05/03/2017					Modified codes for REQ - 307653
* Vishal Verma					  116001						   08/01/2017					CA 6950067 : Added HP record types.
* Shruthi Karanth                 128967                           09/14/2017                   REQ - 329024: Made change to Not copy subtype for Medicare Enrollment cases
* Shruthi Karanth                 133731                           09/28/2017                   REQ - 329024: Made change to Not copy record type for Medicare Enrollment cases
* Pallavi Ravishankar             203238                           02/09/2018                   REQ - 352481 - Cloning appeal Cases 
* Pallavi Ravishankar                                              05/24/2018                   REQ - 363576 - Rollback Case clone Resolution Functionality     
* Mohammed Noor                              					   04/26/2021            		US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
* Mohammed Noor                              					   07/01/2021            		US2273201 - Case Management Checkmarx Security violations
* Kavya Bondada				                                       10/11/2021		            User Story 2737375: T1PRJ0001827 - MF 1 - Quality - Ability to capture Verbal Consent Obtained and making it required for certain case types (RF)
*************************************************************************************************************************/

public with sharing class HUMCaseCloneOverrideExtension
{
    private ApexPages.StandardController controller = null;
    
    /**
    * This is a constructor to the class 
    *
    * @param ApexPages.StandardController       
    * @return  None
    */
    public HUMCaseCloneOverrideExtension(ApexPages.StandardController controller)
    {
        this.controller = controller;
        List<String> lstFileds = new List<String>();
        lstFileds.addAll(HUMCaseCloneFields__c.getAll().keySet());
        if(!Test.isRunningTest()) { this.controller.addFields(lstFileds); }
    }
    
    public String caseID { get; set; }
    public String sCaseNumber { get; set; }
    
    /**
    * This method is used to check the user workqueue value and clone the case 
    *
    * @param None       
    * @return  PageReference
    */
    public PageReference cloneCase()
    { 
       try
       {
            //Get user initiating clone
            User oCurrentUser = [select Id, Name, Current_Queue__c from User where id =: Userinfo.getUserId()];
            
            //Check that the user has a work queue selected before creating the cloned case
            if(String.isBlank(oCurrentUser.Current_Queue__c)) 
            {
                PageReference pageRef = new PageReference(GLOBAL_CONSTANT_HUM.CASECLONE_QUEUE_SEL_PAGE_HUM);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_NEXT_PAGE_HUM, GLOBAL_CONSTANT_HUM.CASECLONE_REDIRECT_PAGE_HUM);
                pageRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASECLONE_OBJECT_TYPE_HUM,'Case');
                
                return pageRef;
            }
            else 
            {
                //Get record being cloned
                Case origCase = (Case) controller.getRecord();
                 
                //Create clone
                Case newCase = origCase.clone();
                if(Schema.SObjectType.Case.fields.Case_Owner__c.isCreateable())	newCase.Case_Owner__c = oCurrentUser.Name;
                if(Schema.SObjectType.Case.fields.Owner_Queue__c.isCreateable()) newCase.Owner_Queue__c = oCurrentUser.Current_Queue__c;
                
                // Map of record types from Schema Describe. 
                Map<Id,String> mapRecordTypeId = new Map<Id,String>();
                Map<String,Id> mapRecordTypeName = new Map<String,Id>();
				for(RecordTypeInfo recType : Schema.SObjectType.Case.getRecordTypeInfos())
				{
				  mapRecordTypeId.put(recType.getRecordTypeId(),recType.getName());
				  mapRecordTypeName.put(recType.getName(),recType.getRecordTypeId());  
				}
                
                if(mapRecordTypeId.containskey(origCase.RecordTypeId) && GLOBAL_CONSTANT_HUM.CLONECLOSEDCASE_RECORDTYPEMAP_HUM.containskey(mapRecordTypeId.get(origCase.RecordTypeId)))
                {
            		String sRecordTypeName = GLOBAL_CONSTANT_HUM.CLONECLOSEDCASE_RECORDTYPEMAP_HUM.get(mapRecordTypeId.get(origCase.RecordTypeId));
            		if(mapRecordTypeName.containsKey(sRecordTypeName) && Schema.SObjectType.Case.fields.RecordTypeId.isCreateable())
            		{
            			newCase.RecordTypeId = mapRecordTypeName.get(sRecordTypeName);
            		}
                }
                
                //Changes for medicare enrollment cases, where Subtype and Record Type should not be copied over to cloned cases
                if(mapRecordTypeId.containsKey(origCase.RecordTypeId) && (mapRecordTypeId.get(origCase.RecordTypeId) == GLOBAL_CONSTANT_HUM.MEDICARE_CASE || mapRecordTypeId.get(origCase.RecordTypeId) == GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE))
                {
                    Map<ID, Schema.RecordTypeInfo> mapAccountRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
                    String sAccRecordTypeName = (NULL != mapAccountRecordType.get(origCase.Account.RecordTypeId)) ? mapAccountRecordType.get(origCase.Account.RecordTypeId).getName() : NULL;
                
                    if(Schema.SObjectType.Case.fields.SubType__c.isCreateable())	newCase.SubType__c = '';
                    if(Schema.SObjectType.Case.fields.RecordTypeId.isCreateable())	newCase.RecordTypeId = HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);                    
                }
                           
                List<Work_Queue_Setup__c> lstOWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Name =: oCurrentUser.Current_Queue__c And IsActive__c = TRUE];
            
                if(!lstOWQS.isEmpty())
                {
                    Work_Queue_Setup__c oWQS = lstOWQS[0];
                    if(Schema.SObjectType.Case.fields.Service_Center__c.isCreateable())	newCase.Service_Center__c = oWQS.Service_Center__c; 
                    if(Schema.SObjectType.Case.fields.Department__c.isCreateable())	newCase.Department__c = oWQS.Department__c;           
                    if(oWQS.Classification_Type__c!=null && Schema.SObjectType.Case.fields.Classification_Type__c.isCreateable())         
                    newCase.Classification_Type__c = oWQS.Classification_Type__r.name; 
                }
                
                if(Schema.SObjectType.Case.fields.Verbal_Consent_Obtained__c.isCreateable())
                    newCase.Verbal_Consent_Obtained__c = origCase.Verbal_Consent_Obtained__c;
                
                insert newCase;   
                sCaseNumber=newCase.CaseNumber;
                caseID=newCase.Id; 
               
            }
        } 
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'HUMCaseCloneOverrideExtension','cloneCase');
        }
        return null;
    }
}