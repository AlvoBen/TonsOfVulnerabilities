/**********************************************************************************************************
Apex Class Name  : ClaimsSendUIProcessDetail_C_HUM
Version          : 1.0
Created Date     : 25/10/2018
Function         : This is controller class for visual force component'AttachGuidedPrcessCase_Claims_C_HUM '.
                   This is used to attach Guided Process to either new case or to an existing case.
***********************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Shivam Anand                                            11/05/2018            REQ - 371426- Logging for Member Claims
* Vijaya K                                                11/05/2018            REQ - 371427- Logging for Provide claims
* Sagar MN					                              01/31/2020			Checkmarx issue fix
* Prasanthi Kandula                                       02/01/2021            US-1669846 SonarQube critical security fixes  
* Suraj Patil								   			  04/09/2021			REQ - 1905147 - PR00094226 - CR AEP readiness - Low Security Issues 
*************************************************************************************************************/
public with sharing class ClaimsSendUIProcessDetail_C_HUM
{   
    public ClaimsSendUIProcessDetail_C_HUM()
    {
        try
         {
             sCaseID = ApexPages.currentPage().getParameters().get('CaseId').escapeHtml4();
             sRelatedProcId = ApexPages.currentPage().getParameters().get('relatedProID');
             List<Case> LstPolMember = [Select ID,Policy_Member__c,Account.Name from Case where ID=:ApexPages.currentPage().getParameters().get('CaseId')];
             sPolicyname  =  LstPolMember[0].Account.Name;
                 
             List<Template_Submission_Data__c> lstTempSubdata = [SELECT ID, Value__c,Template_Submission__c  FROM Template_Submission_Data__c where Template_Field__r.Name='data' and Template_Submission__c =: sRelatedProcId limit 1];
             if(lstTempSubdata != null)
             {
             processId =lstTempSubdata[0].id;
             }
         }
         catch(Exception e)
         {
             //Restrict insert of error Logs
             HUMExceptionHelper.bRestrictLog=true;
             listErrorLogs = HUMExceptionHelper.logErrors(e, 'GuidedProcessDetail_C_HUM', 'GuidedProcessDetail_C_HUM');
         }
    }
    
    public string sCaseId {get;set;}
     public string sRelatedProcId {get;set;}
    public string sPolicyname {get;set;}
    public Id processId{get;set;}
    
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs
    {
        get
        {
            if(listErrorLogs == null)  return new List<Error_Log__c>();
            return listErrorLogs;
        }
        set;
    }
    
    public String getDetailValue()
    {
        String retValue = ''; 
        if(Schema.sObjectType.Template_Submission_Data__c.fields.Value__c.isAccessible())
        {
        List<Template_Submission_Data__c> lstTempSubmissionData = [select ID,Value__c from Template_Submission_Data__c where Id =:processId];
        retValue = lstTempSubmissionData[0].Value__c;
        } 
        return retValue;
    }
    
    @TestVisible private String getPolicyMemberName()
    {
        String sPolName = GuidedProcessDetail_D_HUM.queryPolicyMemberName(sCaseId);
        return sPolName;
    }
}