/**
 *   name        : GrievanceAndAppealComponent_C_RT_HUM
 *   CreateDate  : 08/28/2018
 *   Description : This class serves as controller for GrievanceAndAppeal_CMP_RT_HUM component
 *   Version     : 1.0
 *   reference   : GrievanceAndAppealComponent_C_RT_HUM
 *   Test        : GrievanceAndAppealComponent_T_RT_HUM
 *   Modification Log:
    
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Ajay Chakradhar                            10/15/2018                  Original Version
 * Swapnali Sonawane                          04/09/2021                  Req 2119511 added ExRM Fields to MHK Web Service and Additional Data for G&A Status Page in CRM 
 * Nirmal Garg							      09/16/2021					Added browser logic for coveo
 * 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
 ************************************************************************************************************************************************* */
public with sharing class GrievanceAndAppealComponent_C_RT_HUM
{
    public string columnName{get;set;}
    public string columnHeader{get;set;}
    public string sRecId {get; set;}
    public string sAccountId {get; set;}
    public string sAccountName {get; set;}
    public String level {get; set;}
    public String message {get; set;}
    public boolean isRendered {get;set;}
    public Boolean bSwitchGNAStatus{ get; set;}
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    public GrievanceAndAppealComponent_C_RT_HUM()
    {
        level ='';
        message = '';
        isRendered = true;
     	bSwitchGNAStatus=  HUMUtilityHelper.isCRMFunctionalityON('2119511');
        if(bSwitchGNAStatus == true)
        {
     		columnHeader = json.serialize(new List < String > {'Id', 'Case Number','Type','Category','Status','Priority','Group','Plan','LOB','Humana Received Date','Provider Name'});
        	columnName = json.serialize(new List < String > {'Id', 'sCase','sType','sCategory','sStatus','sPriority','sMemberElgGrp','sMemberElgPlan','sMemberLOB','sManualReceivedDate','sProviderDisplayName'});
        }
        else
        {
        	columnHeader = json.serialize(new List < String > {'Id', 'Case Number','Type','Category','Status','Priority','Due Date'});
        	columnName = json.serialize(new List < String > {'Id', 'sCase','sType','sCategory','sStatus','sPriority','sDueDate'});
        }
        sRecId = ApexPages.currentpage().getParameters().get('Id'); //26/11 - id to Id
        if(String.isNotBlank(sRecId))
        {
            sAccountId = [select member__c from policy_member__c where id=:sRecId].Member__c;
            if(sAccountId != Null)
            {
                Account objAccount = [Select id,Name from Account where id =:sAccountId LIMIT 1];
                sAccountName = objAccount.Name;
            }
        }
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
        if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    } 
    
    /**
    * This remoting invoked method generates list of Greivance and Appeal cases associated to the account.
    * @Name getArchiveAttachments
    * @param    sRecordID - Account ID
    * @return   List of Greivance and Appeal Case records
    */
    @RemoteAction
    public static continuation getArchiveAttachments(string sRecordID) 
    {   
        return GrievanceAndAppeal_S_HUM.sendGreivanceAndAppealRequest(sRecordID);
    }
    
       
    /**
    * This continuation invoked method generates list of Greivance and Appeal cases associated to the account.
    * @Name processGreivanceAndAppealResponse 
    * @param    labels and State of continuation
    * @return   GreivanceAndAppeal_DTO_HUM 
    */
    public static object processGreivanceAndAppealResponse(List < string > labels, object State)
    {
        return GrievanceAndAppeal_S_HUM.processGreivanceAndAppealResponse(labels, State);
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
        else if(level =='FAILURE')
        {
             ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.error, message));
             isRendered = false;
        }
    }
    
}