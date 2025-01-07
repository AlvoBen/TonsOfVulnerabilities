/**********************************************************************************************************
Apex Class Name  : GuidedProcessDetail_C_HUM
Version          : 1.0
Created Date     : 05/12/2016
Function         : This is the controller class for showing the details on the Case page. 
Test Class       : GuidedProcessDetail_T_HUM 
***********************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi        27942                05/12/2016                 Original Version
* Prudhvi Pamarthi            164129               01/19/2018            REQ - 328929 Fix for HUMExceptionHelper: DML 
*************************************************************************************************************/
public with sharing class GuidedProcessDetail_C_HUM 
{   
    public GuidedProcessDetail_C_HUM ()
    {
        try
         {
             sCaseID = ApexPages.currentPage().getParameters().get('CaseId');
             List<Case> LstPolMember = [Select ID,Policy_Member__c,Account.Name from Case where ID=:sCaseId];
             sPolicyname  =  LstPolMember[0].Account.Name;
         }
         catch(Exception e)
         {
             //Restrict insert of error Logs
             HUMExceptionHelper.bRestrictLog=true;
             listErrorLogs = HUMExceptionHelper.logErrors(e, 'GuidedProcessDetail_C_HUM', 'GuidedProcessDetail_C_HUM');
         }
    }
    
    public string sCaseId {get;set;}
    public string sPolicyname {get;set;}
    
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
        String sDetail = GuidedProcessDetail_D_HUM.queryDetailValue(sCaseId);
        return sDetail;
    }
    
    @TestVisible private String getPolicyMemberName()
    {
        String sPolName = GuidedProcessDetail_D_HUM.queryPolicyMemberName(sCaseId);
        return sPolName;
    }
}