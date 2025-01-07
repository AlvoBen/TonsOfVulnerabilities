/**************************************************************************** 
Apex Class Name  : CaseRelatedDocument_C_HUM
Version          : 1.0
Created Date     : 11/07/2018
Function         : Show the Case comments documents based on Document ID
Test Class       : CaseRelatedDocument_C_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
* Rajesh narasimhan                           11/07/2018                   original Version
* Swapnali Sonawane                           04/09/2021                   Req 2119511 added ExRM Fields to MHK Web Service And Additional Data for G&A Status Page in CRM 
***************************************************************************************************/
global with sharing class CaseRelatedDocument_C_HUM
{
    public String medhokID { get; set; }
    public String vContext { get; set; }
    public string columnName{get;set;}
    public string columnHeader{get;set;}
    public String level {get; set;}
    public String message {get; set;}
    public boolean isRendered {get;set;}
    public String strHTML  {get;set;}
    Blob bodyBlob;
    String csvBody;
    public string RecordId{get;set;}
    public string sMedhokUrl{get;set;}
    public string sMethod{get;set;}
    public string sAppKey{get;set;}
    public String currentPosition {get;set;}
	public Boolean bSwitchCaseDoc{ get; set;}

    public CaseRelatedDocument_C_HUM()
    {         
        level ='';
        message = '';
        isRendered = true;
		bSwitchCaseDoc=  HUMUtilityHelper.isCRMFunctionalityON('2119511');
        
        if (bSwitchCaseDoc==true)
        {
        	columnHeader = json.serialize(new List < String > {'File Name','Type','Created By','Document Created'});
        	columnName = json.serialize(new List < String > {'sfileName','sDocumentType','sCreatedby','sCreateDate'});
        }
        else
        {
            columnHeader = json.serialize(new List < String > {'File Name','Type','Created By'});
        	columnName = json.serialize(new List < String > {'sfileName','sDocumentType','sCreatedby'});
        }
        HUM_Webservice_Callout__c oServiceCalloutEhub = HUM_Webservice_Callout__c.getValues('Medhok_getDocument');  
        sMedhokUrl = oServiceCalloutEhub.End_Point_URL__c;
        sMethod = oServiceCalloutEhub.Request_Type__c;
        sAppKey = oServiceCalloutEhub.Password__c;          
           
    }
    
    /**
    * This remoting invoked method generates list of attachments associated to the case.
    * 
    * @param    sCaseId         Case record ID
    * @return   List of attachment records
    */
    @RemoteAction
    global static continuation getCaseDocuments(string sContext,string sMedhokId) 
    {  
        return ViewDocuments_S_HUM.sendViewDocumentsRequest(sContext,sMedhokId);
    }
    
    /**
    * This continuation invoked method generates list of attachments associated to the case.
    * 
    * @param    labels   and State of continuation
    * @return   CaseTaskAttachment_Archive_DTO_HUM
    */
    public static object processViewDocumentsResponse(List < string > labels, object State)
    { 
        return ViewDocuments_S_HUM.processViewDocumentsResponse(labels,State);
     
    }
    /**
    * This Method show the message when no record found 
    * 
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