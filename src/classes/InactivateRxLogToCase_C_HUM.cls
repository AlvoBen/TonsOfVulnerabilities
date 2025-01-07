/******************************************************************************************************************************
Apex Class Name  : InactivateRxLogToCase_C_HUM
Version          : 1.0 
Created Date     : 01/10/2022
Function         : Controller to handle Inactivte Rx Guided Process.       
Test Class       : InactivateRxLogToCase_T_HUM
Modification Log :
*   Developer                   Date                 Description
------------------------------------------------------------------------------------------------------------------------------                
*   Ashok Kumar Nutalapati		01/10/2022           REQ-2843422 Original  Version.
*   Ashok Kumar Nutalapati		05/09/2022           User Story 2934951: T1PRJ0200895 - MF 1 - Inactivate Rx: Create New Case (CRMS) (ID# 163)
*********************************************************************************************************************************/

public with sharing class InactivateRxLogToCase_C_HUM
{    
    public String sPolicyId {get;set;}
    public String sAccountId {get;set;}
    public String ClinicalTransfer {get;set;}
    public String sInteractionId {get;set;}
    public String sCaseId{get;set;}
    public String sCaseNumber{get;set;}
    public String SubmissionID {get; set;}
    
    public InactivateRxLogToCase_C_HUM()
    {
        sPolicyId = ApexPages.currentPage().getParameters().get('PolicyId'); 
        sAccountId = ApexPages.currentPage().getParameters().get('AccountId');
        ClinicalTransfer = ApexPages.currentPage().getParameters().get('ClinicalTransfer');
        sInteractionId = ApexPages.currentPage().getParameters().get('InteractionId');
        SubmissionID = ApexPages.currentPage().getParameters().get('SubmissionID');       
    }
    
     public void createPharmacyCaseAndRedirect() 
    {   
       
        try{
            LoggingProcessLogic_S_HUM.sPolicyMemberId = sPolicyId;
            LoggingProcessLogic_S_HUM.bInactivateRx = true;
            LoggingProcessLogic_S_HUM.sClinicalTransfer = ClinicalTransfer;
            if (String.isNotBlank(sInteractionId)) {
                List<Interaction_Member__c> interactionRecord = [select id,Interacting_About_Type__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c FROM Interaction_Member__c Where Interaction__c = :sInteractionId AND Interacting_About__c = :sAccountId];
                if(interactionRecord.size()>0) LoggingProcessLogic_S_HUM.interactionRecord = interactionRecord[0];
            }
            if(!Test.isRunningTest()) {
                sCaseId = LoggingProcessLogic_S_HUM.createNewPharmacyAddressCase(sPolicyId);
            }
            LoggingProcessLogic_S_HUM.bInactivateRx= false;
            
            if(String.IsNotBlank(sCaseId))
            {	
				updateWhoID();
				List<Case> caseRecord = [Select CaseNumber from Case where Id=:sCaseId];
				if(caseRecord.size()>0) {
					sCaseNumber = caseRecord[0].CaseNumber;
					if (String.isNotBlank(sInteractionId)) {
						List<Case_interaction__c> lstCaseInt = [SELECT Id FROM Case_interaction__c WHERE interaction__c = :sInteractionId AND Case__c = :sCaseId Limit 1];
						if(lstCaseInt.isEmpty()){
							Case_Interaction__c oCaseInt = new Case_Interaction__c(Case__c = sCaseId, Interaction__c = sInteractionId);
							if (Case_Interaction__c.SObjectType.getDescribe().isCreateable() && Schema.sObjectType.Case_Interaction__c.fields.Case__c.isCreateable() && Schema.sObjectType.Case_Interaction__c.fields.Interaction__c.isCreateable()) {
								insert oCaseInt;
							}
						}
					}
				} else {
					sCaseNumber = '';
				}
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InactivateRxLogToCase_C_HUM', 'createPharmacyCaseAndRedirect');
        }
    }  
    
    
	/*
	* Method Name : updateWhoID
	* Description : This method will launch the auto save flow.
	* Return type : void
	* Parameter   : NA
	*/
	public void updateWhoID() 
	{     
	  ID iSubmissionID = id.Valueof(SubmissionID);
	  list<Template_Submission__C> OwnertoUpdate =  [Select ID,  Submission_Owner__r.Object_Owner_ID__c  from Template_Submission__C where ID =: iSubmissionID LIMIT 1];     
	  list<Template_submission_Owner__c> lstUpdate =[select  ID ,Object_Owner_ID__c from Template_submission_Owner__c where id =:OwnertoUpdate[0].Submission_Owner__c];         
	  if(OwnertoUpdate != NULL && !OwnertoUpdate.isEmpty() && lstUpdate != NULL && !lstUpdate.isEmpty())
	  {     
		  if(Schema.SObjectType.Template_submission_Owner__c.fields.Object_Owner_ID__c.isUpdateable()){
			  lstUpdate[0].Object_Owner_ID__c  = sCaseId;        
			  update lstUpdate;                  
		  }     
	  }
	}
    
}