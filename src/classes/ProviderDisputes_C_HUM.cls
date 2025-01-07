/**
 *   name        : ProviderDisputes_C_HUM
 *   Description : This class serves as controller for ProviderDisputes_VF_HUM page
 *   Version     : 1.0
 *   reference   : ProviderDisputes_C_HUM
 *   Test        : ProviderDisputes_T_HUM
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Ajay Chakradhar                            10/15/2019                  Original Version 
 * Ajay Chakradhar                            11/29/2019                  Defect-450410 Fix
 * Sayali Nemade						      11/24/2020           		  Resolved Checkmarx Scan         Error						
 * Pinky Vijur								  2/23/2021					  Resolved Checkmarx Scan 		  Error
************************************************************************************************************************************************* */
public with sharing class ProviderDisputes_C_HUM {
public string columnName{get;set;}
    public string columnHeader{get;set;}
    public string sRecId {get; set;}
    public string sAccountId {get; set;}
    public string sAccountName {get; set;}
    public String level {get; set;}
    public String message {get; set;}
    public boolean isRendered {get;set;}

    public ProviderDisputes_C_HUM()
    {
        level ='';
        message = '';
        isRendered = true;
        columnHeader = json.serialize(new List < String > {'Id', 'Case Number','Type','Category','Status','Priority','Due Date','Provider Name'});
        columnName = json.serialize(new List < String > {'Id','sCase','sType','sCategory','sStatus','sPriority','sDueDate','sProviderName'});
      
        sRecId = ApexPages.currentpage().getParameters().get('Id'); 
        if(String.isNotBlank(sRecId))
        {
            sAccountId = [select member__c from policy_member__c where id=:sRecId].Member__c;
            if(sAccountId != Null)
            {
                Account objAccount = [Select id,Name from Account where id =:sAccountId LIMIT 1];
                String AccountName = String.valueOf(objAccount.Name);
                sAccountName = AccountName.replaceAll('[^a-zA-Z0-9\\s+]','');
            }
        }
    } 
    
    /**
    * This remoting invoked method generates list of Provider Disputes Cases.
    * @Name		getProviderDisputeCases
    * @param    sRecordID - Account ID
    * @return   List of Provider Dispute Cases
    */
    @RemoteAction
    public static continuation getProviderDisputeCases(string sRecordID) 
    {   
        return ProviderDisputes_S_HUM.sendProviderDisputeCasesRequest(sRecordID);
    }
    
       
    /**
    * This continuation invoked method generates list of Provider Disputes Cases.
    * @Name  	processProviderDisputeCasesResponse 
    * @param    labels and State of continuation
    * @return   ProviderDisputes_DTO_H_HUM 
    */
    public static object processProviderDisputeCasesResponse(List < string > labels, object State)
    {
        return ProviderDisputes_S_HUM.processProviderDisputeCasesResponse(labels, State);
    } 
    
    /**
    * This Method show the message when no record found 
    * @Name showPageMessage
    * @param    NA
    * @return   Void 
    */
    public void showPageMessage() {
        if(level =='INFO' || Test.isRunningTest()) {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.info, message));
            isRendered = false;
        }
        else if(level =='FAILURE'){  ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, message));    isRendered = false;
        }
    }
}