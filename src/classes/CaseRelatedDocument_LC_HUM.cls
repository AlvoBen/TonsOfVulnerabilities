/**************************************************************************** 
Apex Class Name  : CaseRelatedDocument_LC_HUM
Version          : 1.0
Created Date     : 09/02/2022
Function         : Show the Case comments documents based on Document ID
Test Class       : CaseRelatedDocument_LC_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
* Kalyani Pachpol                              09/02/2022                   original Version
* Jonathan Dickinson                           06/28/2023                   User Story 4759866: T1PRJ0891415 Platform Management- SF - TECH - LEGO - Blocker - ViewDocuments_LS_HUM : Avoid Soql queries inside loops.
***************************************************************************************************/

public with sharing class CaseRelatedDocument_LC_HUM {

    /**
    * This remoting invoked method generates list of attachments associated to the case.
    * 
    * @return   List of attachment records
    */
    @AuraEnabled(continuation=true cacheable=true) 
    public static continuation getCaseDocuments(string requestBody) 
    {  
        try{
            return ViewDocuments_LS_HUM.sendViewDocumentsRequest(requestBody);
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'CaseRelatedDocument_LC_HUM', 'getCaseDocuments');
            throw new AuraHandledException(e.getMessage());
        }
    }
    
    /**
    * This continuation invoked method generates list of attachments associated to the case.
    * 
    * @param    labels   and State of continuation
    * @return   CaseTaskAttachment_Archive_DTO_HUM
    */
    @AuraEnabled(continuation=true)
    public static object processViewDocumentsResponse(List<string> labels, object State)
    { 
        return ViewDocuments_LS_HUM.processViewDocumentsResponse(labels,State);
    }
    
    @AuraEnabled
    public static object getdocumentURL()
    {
        HUM_Webservice_Callout__c oServiceCalloutEhub = HUM_Webservice_Callout__c.getValues('Medhok_getDocument');
        DocumentURL_DTO_HUM obj = new DocumentURL_DTO_HUM();
        obj.sMedhokUrl= oServiceCalloutEhub.End_Point_URL__c;
        obj.sMethod = oServiceCalloutEhub.Request_Type__c;
        obj.sAppKey = oServiceCalloutEhub.Password__c;
        return json.serialize(obj);
    }
    
}