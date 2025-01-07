/*******************************************************************************************************************************************************
Apex Class Name  : PhoneBook_LS_HUM
Version          : 1.0
Created Date     : 02/09/2021
Function         : This service class contains GetCallData and PhoneBook related webservices
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            		 Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                              02/09/2021                 Original Version
* Nikhil                                                 02/22/2021                 Implementing softphone logging.
* Arpit Jain/Navajit Sarkar                              03/09/2021                 PhoneBook webservices
* Arpit Jain/Navajit Sarkar                              07/12/2021                 US-2357061 : IVR Call End or Transfer
* Harshada Kamble                                        01/02/2023                 US 3994079 : PCC Call Transfer Phonebook Transfers- Added IsSoftPhoneIndicator in input request to filter softphone routing rules
**********************************************************************************************************************************************/
public with sharing class PhoneBook_LS_HUM 
{
    /**
    * 
    * <p>
    * Constructor Method to for the class PhoneBook_LS_HUM
    */ 
    public PhoneBook_LS_HUM(){}
    
    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for GetCallData services .
     *
     * @param String uuid
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object getCallData(String uuid) 
    {
        HUM_Webservice_Callout__c oGetCalldataService = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_GETCALLDATA);
        Http http = new Http();
        HttpRequest request = new HttpRequest();
        request.setEndpoint(oGetCalldataService.End_Point_URL__c);
        request.setMethod(oGetCalldataService.Request_Type__c);
        request.setBody('{"GetCallData": {"UUID":"'+ uuid+'"}}');
       	if(!Test.isRunningTest() && String.isNotBlank(oGetCalldataService.Certificate_Name__c))
        {
          request.setClientCertificateName(oGetCalldataService.Certificate_Name__c);
        }
        Continuation con = new Continuation(Integer.valueof(oGetCalldataService.Timeout__c));
        con.continuationMethod = PhoneBookConstants_H_HUM.CALLBACK_GETCALLDATA;
        con.state=con.addHttpRequest(request);       
        return con;
    }

    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for RoutingXML services .
     *
     * @param String body
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object getDefaultPH(String body)
    {
        HUM_Webservice_Callout__c DefaultServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ROUTEXML);
        HttpRequest request = new HttpRequest();
        request.setEndpoint(DefaultServiceDetails.End_Point_URL__c);
        request.setMethod(DefaultServiceDetails.Request_Type__c);
        request.setBody(body);
       	if(!Test.isRunningTest() && String.isNotBlank(DefaultServiceDetails.Certificate_Name__c))
        {
        request.setClientCertificateName(DefaultServiceDetails.Certificate_Name__c);
        }
        Continuation con = new Continuation(Integer.valueof(DefaultServiceDetails.Timeout__c));
        con.continuationMethod = PhoneBookConstants_H_HUM.CALLBACK_DEFAULTPHONEBOOK;
        con.state = con.addHttpRequest(request);
        return con;
    }
    
    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for getAllPhonebookRules services .
     *
     * @param void
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object getAllPhoneBookRules()
    {        	
        HUM_Webservice_Callout__c AllPhRulesServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ALLPHONEBOOKRULES);
        HttpRequest request = new HttpRequest();
        request.setEndpoint(AllPhRulesServiceDetails.End_Point_URL__c);
        request.setMethod(AllPhRulesServiceDetails.Request_Type__c);
        request.setBody('{"GetAllPhoneBookRulesRequest": ""}');
        if(!Test.isRunningTest() && String.isNotBlank(AllPhRulesServiceDetails.Certificate_Name__c))
        {
        request.setClientCertificateName(AllPhRulesServiceDetails.Certificate_Name__c);
        }
        Continuation con = new Continuation(Integer.valueof(AllPhRulesServiceDetails.Timeout__c));            
        con.continuationMethod = PhoneBookConstants_H_HUM.CALLBACK_ALLPHONEBOOKRULES;
        con.state = con.addHttpRequest(request);
        return con;
    }
    
    /**
     * Returns Object
     * <p>
     * Method to form object and call calltransferCon method .
     *
     * @param Object callTransferData
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object callTransfer(Object callTransferData)
    {
        Map<String,String> PolicyCaseDataMap = new Map<String,String>();
		String searchDataString = JSON.serialize(callTransferData);
        Map<String, object> searchDataMap =   (Map<String, object>) JSON.deserializeUntyped(searchDataString);
		String accId = (String)searchDataMap.get('accId');
        String memberPlanID = (String)searchDataMap.get('MemberPlanId');
        String uuid = (String)searchDataMap.get('uuid');
        PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInteraction, '');
        PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInqFor, '');
        PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInqAb, accId);
        PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjPolicyMember, memberPlanID);
        SoftPhoneCallTransfer_LH_HUM oSFResponce = SoftPhoneCallTransfer_LS_HUM.createResponse(PolicyCaseDataMap);
        oSFResponce.uuid = uuid;
        Object con = calltransferCon(oSFResponce);
        return con;
    }
    
    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for RoutingXML services .
     *
     * @param SoftPhoneCallTransfer_LH_HUM oSFResponce
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object calltransferCon(SoftPhoneCallTransfer_LH_HUM oSFResponce)
    {
        //US 3994079 : PCC Call Transfer Phonebook Transfers- Added IsSoftPhoneIndicator in input request to filter softphone routing rules 
        String reqBodyForRoutingXML = JSON.serialize(CreateRouteXMLRequest_DTO_HUM.requestStructureRouteXML(oSFResponce.InquiredAboutPlatformCode, oSFResponce.MemberId, oSFResponce.InquiredAboutDOB, oSFResponce.SellingLedger, oSFResponce.InquiredAboutGroupId, oSFResponce.ASOIndicator, oSFResponce.MajorLineOfBusiness,  oSFResponce.SellingLedger, '', '', '',oSFResponce.StateOfIssue, oSFResponce.BusinessSegmentIndicator, oSFResponce.HdphIndicator,oSFResponce.MtvBusinessLevel5, PhoneBookConstants_H_HUM.IsSoftPhoneIndicator,oSFResponce.MtvBusinessLevel7,oSFResponce.ProductTypeCode, 'Softphone', '',oSFResponce.VENDOR_SUB_GROUPID));
        HUM_Webservice_Callout__c xmlServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_ROUTEXML);
		HttpRequest requestxml = new HttpRequest();
        requestxml.setEndpoint(xmlServiceDetails.End_Point_URL__c);
        requestxml.setMethod(xmlServiceDetails.Request_Type__c);
        requestxml.setBody(reqBodyForRoutingXML);
        if(!Test.isRunningTest() && String.isNotBlank(xmlServiceDetails.Certificate_Name__c))
        {
        requestxml.setClientCertificateName(xmlServiceDetails.Certificate_Name__c); 
        }
        Continuation concalltransfer = new Continuation(Integer.valueof(xmlServiceDetails.Timeout__c));
        concalltransfer.continuationMethod = PhoneBookConstants_H_HUM.CALLBACK_CALLTRANSFER;
        concalltransfer.state=concalltransfer.addHttpRequest(requestxml);
        return concalltransfer;
    }
    
    /**
     * Returns void
     * <p>
     * method to create logging records based on conditions
     *
     * @param  String callAction
     * @param  String callInfoLog
     * @param  String callLogType
     * @param  String SPExtrnlId
     * @param  String reason
     * @param  String category
     * @param  String caller
     * @return void
     */
    @AuraEnabled
    public static void createRecord(String callAction,String callInfoLog,String callLogType,String SPExtrnlId,String reason, String category, String caller, String UUID) 
    {
        CRMFunctionality_ONOFF_Switch__c level1 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL1);
        CRMFunctionality_ONOFF_Switch__c level2 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL2);
        CRMFunctionality_ONOFF_Switch__c level3 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL3);
        if(level3.IsON__c && level3.IsON__c != null)
        {
            if( reason == PhoneBookConstants_H_HUM.STATUS|| reason ==  PhoneBookConstants_H_HUM.LOGOUT || reason ==  PhoneBookConstants_H_HUM.LOGIN || reason ==  PhoneBookConstants_H_HUM.STATION || reason ==PhoneBookConstants_H_HUM.INTERACTION|| reason == PhoneBookConstants_H_HUM.CONNECTED || reason == PhoneBookConstants_H_HUM.CAT_ROUTING_STATUS || category == PhoneBookConstants_H_HUM.CAT_VIEW|| reason == PhoneBookConstants_H_HUM.ACW_COMPLETED || reason == PhoneBookConstants_H_HUM.ACW_REQUIRED || reason == PhoneBookConstants_H_HUM.DISCONNECTED|| category ==PhoneBookConstants_H_HUM.CAT_CHANGE || category == PhoneBookConstants_H_HUM.CAT_CONNECT)
            {
                createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,caller,UUID);
            }
        } 
        else if(level2.IsON__c && level2.IsON__c != null)
        {
            if(reason ==PhoneBookConstants_H_HUM.INTERACTION|| reason == PhoneBookConstants_H_HUM.STATUS || reason ==PhoneBookConstants_H_HUM.LOGOUT || reason ==  PhoneBookConstants_H_HUM.LOGIN || reason ==  PhoneBookConstants_H_HUM.STATION || reason ==  PhoneBookConstants_H_HUM.CONNECTED || category == PhoneBookConstants_H_HUM.CAT_CHANGE|| category == PhoneBookConstants_H_HUM.CAT_CONNECT)
            {
                createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,caller,UUID);
            }
        }
        else if(level1.IsON__c && level1.IsON__c != null)
        {
            if( (reason == PhoneBookConstants_H_HUM.STATUS || reason ==PhoneBookConstants_H_HUM.LOGOUT || reason == PhoneBookConstants_H_HUM.LOGIN  || reason == PhoneBookConstants_H_HUM.STATION) && category == '' )
            {
                createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,caller,UUID);
            }
        }
    }
    
   /**
     * Returns void
     * <p>
     * method to create logging records based on conditions
     *
     * @param  String callAction
     * @param  String callInfoLog
     * @param  String callLogType
     * @param  String SPExtrnlId
     * @param  String reason
     * @param  String category
     * @param  String caller
     * @return void
     */
    @AuraEnabled
    public static void createSoftPhoneLog(String callAction,String callInfoLog,String callLogType,String SPExtrnlId, String caller, String UUID)
    {
        SoftPhone_Call_Log__c sphn = new SoftPhone_Call_Log__c();
        sphn.Call_Action__c = callAction;
        sphn.Call_Information_Log__c = callInfoLog;
        sphn.Call_Log_Type__c = callLogType;
        sphn.SP_External_Id__c = SPExtrnlId;
        sphn.Agent_Id__c = UserInfo.getUserId();
        sphn.Account_Id__c = caller;
        sphn.IVR_UUID__c = UUID;
        insert sphn;
    }  

    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for saveCalldata webservice on call disconnect
     *
     * @param Object disconnectattributes
     * @return Object
     */
    @AuraEnabled(continuation = true)
    public static Object disconnectSaveCallData(Object disconnectattributes) 
    {
        Map < String, String > disconnectCallDatamapping = new Map < String, String > ();
        Map < String, String > interactionMappings = new Map < String, String > ();
        String searchDataString = JSON.serialize(disconnectattributes);
        Map < String, object > searchDataMap = (Map < String, object > ) JSON.deserializeUntyped(searchDataString);
        Boolean isConsultBlindClicked = (Boolean) searchDataMap.get(PhoneBookConstants_H_HUM.isConsultBlindClicked);
        Boolean isTransferredCall = (Boolean) searchDataMap.get(PhoneBookConstants_H_HUM.isTransferredCall);
        String UUID = (String) searchDataMap.get(PhoneBookConstants_H_HUM.UUID);
        String AGENT_CALL_START_TIME = (String) searchDataMap.get(PhoneBookConstants_H_HUM.AGENT_CALL_START_TIME);
        String AGENT_CALL_END_TIME = (String) searchDataMap.get(PhoneBookConstants_H_HUM.AGENT_CALL_END_TIME);
        Boolean isOutbound = (Boolean) searchDataMap.get(PhoneBookConstants_H_HUM.OUTBOUND);
        User userRec = [select id, name, Network_User_Id__c from User where id =: UserInfo.getUserId()];
        
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.CALL_TYPE, isOutbound ? PhoneBookConstants_H_HUM.OUTBOUND : PhoneBookConstants_H_HUM.INBOUND);
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.MODULE_NAME, PhoneBookConstants_H_HUM.Genesys);
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.AGENT_CALL_START_TIME, AGENT_CALL_START_TIME);
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.AGENT_CALL_END_TIME, AGENT_CALL_END_TIME);
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.isConsultBlindClicked, isConsultBlindClicked ? 'Y' : 'N');
        disconnectCallDatamapping.put(PhoneBookConstants_H_HUM.isTransferredCall, isTransferredCall ? 'Y' : 'N');

        interactionMappings = PhoneBook_LD_HUM.getInteractionSaveCallDataFields(UUID);
        interactionMappings.put(PhoneBookConstants_H_HUM.USER_ID, userRec.Network_User_Id__c);
        interactionMappings.put(PhoneBookConstants_H_HUM.USER_NAME, userRec.Name);
        interactionMappings.put(PhoneBookConstants_H_HUM.AGENT_CALL_END_TIME, AGENT_CALL_END_TIME);
        String reqBodyForSaveCallData = JSON.serialize(CreateSaveCallDataRequest_DTO_HUM.requestStructuresaveCallDatalightning(disconnectCallDatamapping, interactionMappings, UUID));
        HUM_Webservice_Callout__c savecallServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_SAVECALLDATA);
        HttpRequest requestsave = new HttpRequest();
        requestsave.setEndpoint(savecallServiceDetails.End_Point_URL__c);
        requestsave.setMethod(savecallServiceDetails.Request_Type__c);
        requestsave.setBody(reqBodyForSaveCallData);
        if (!Test.isRunningTest() && String.isNotBlank(savecallServiceDetails.Certificate_Name__c)) 
        {
        requestsave.setClientCertificateName(savecallServiceDetails.Certificate_Name__c);
        }
        Continuation condisconnect = new Continuation(Integer.valueof(savecallServiceDetails.Timeout__c));
        condisconnect.continuationMethod = PhoneBookConstants_H_HUM.respdisconnectSaveCallData;
        condisconnect.state = condisconnect.addHttpRequest(requestsave);
        return condisconnect;
    }

    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for saveCalldata webservice on Consult/Blind transfers .
     *
     * @param Object transferattributes
     * @return Object
     */
    @AuraEnabled(continuation = true)
    public static Object consultBlindSaveCallData(Object transferattributes) 
    {
        Map < String, String > disconnectCallDatamapping = new Map < String, String > ();
        Map < String, String > transfersaveCallDatamapping = new Map < String, String > ();
        Map < String, String > interactionMappings = new Map < String, String > ();
        Map < String, String > policytransferDatamapping = new Map < String, String > ();
        String searchDataString = JSON.serialize(transferattributes);
        Map < String, object > searchDataMap = (Map < String, object > ) JSON.deserializeUntyped(searchDataString);
        String UUID = (String) searchDataMap.get(PhoneBookConstants_H_HUM.UUID);
        transfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER, (String) searchDataMap.get(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER));
        transfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_STIM, (String) searchDataMap.get(PhoneBookConstants_H_HUM.TRANSFER_STIM));
        transfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_NUMBER, (String) searchDataMap.get(PhoneBookConstants_H_HUM.TRANSFER_NUMBER));
        transfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_TYPE, (String) searchDataMap.get(PhoneBookConstants_H_HUM.TRANSFER_TYPE));
        transfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_ETIM, '');
        interactionMappings = PhoneBook_LD_HUM.getInteractionSaveCallDataFields(UUID);
        policytransferDatamapping = PhoneBook_LD_HUM.getCallTransferData(UUID);
        if(interactionMappings.size() > 0)
        {
            transfersaveCallDatamapping.putAll(interactionMappings);
        }
        if(policytransferDatamapping.size() > 0)
        {
            transfersaveCallDatamapping.putAll(policytransferDatamapping);
        }
        String reqBodyForSaveCallData = JSON.serialize(CreateSaveCallDataRequest_DTO_HUM.requestStructuresaveCallDatalightning(disconnectCallDatamapping, transfersaveCallDatamapping, UUID));
        HUM_Webservice_Callout__c savecallServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_SAVECALLDATA);
        HttpRequest requestsave = new HttpRequest();
        requestsave.setEndpoint(savecallServiceDetails.End_Point_URL__c);
        requestsave.setMethod(savecallServiceDetails.Request_Type__c);
        requestsave.setBody(reqBodyForSaveCallData);
        if (!Test.isRunningTest() && String.isNotBlank(savecallServiceDetails.Certificate_Name__c)) 
        {
        requestsave.setClientCertificateName(savecallServiceDetails.Certificate_Name__c);
        }
        Continuation contransfer = new Continuation(Integer.valueof(savecallServiceDetails.Timeout__c));
        contransfer.continuationMethod = PhoneBookConstants_H_HUM.respConsultBlindSaveCallData;
        contransfer.state = contransfer.addHttpRequest(requestsave);
        return contransfer;
    }

    /**
     * Returns Object
     * <p>
     * Method to call continuation service for the service for saveCalldata webservice on survey transfers .
     *
     * @param Object transferattributes
     * @return Object
     */
    @AuraEnabled(continuation = true)
    public static Object surveyTransferSaveCallData(Object transferattributes) 
    {
        Map < String, String > disconnectCallDatamapping = new Map < String, String > ();
        Map < String, String > surveytransfersaveCallDatamapping = new Map < String, String > ();
        Map < String, String > interactionMappings = new Map < String, String > ();
        Map < String, String > policytransferDatamapping = new Map < String, String > ();
        String searchDataString = JSON.serialize(transferattributes);
        Map < String, object > searchDataMap = (Map < String, object > ) JSON.deserializeUntyped(searchDataString);
        String UUID = (String) searchDataMap.get(PhoneBookConstants_H_HUM.UUID);
        surveytransfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER, (String) searchDataMap.get(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER));
        surveytransfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_NUMBER, (String) searchDataMap.get(PhoneBookConstants_H_HUM.TRANSFER_NUMBER));
        surveytransfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.TRANSFER_TYPE, (String) searchDataMap.get(PhoneBookConstants_H_HUM.TRANSFER_TYPE));
        surveytransfersaveCallDatamapping.put(PhoneBookConstants_H_HUM.WATSON_TRANSFER_TS,  (String) searchDataMap.get(PhoneBookConstants_H_HUM.WATSON_TRANSFER_TS));
        interactionMappings = PhoneBook_LD_HUM.getInteractionSaveCallDataFields(UUID);
        policytransferDatamapping = PhoneBook_LD_HUM.getCallTransferData(UUID);
        if(interactionMappings.size() > 0)
        {
            surveytransfersaveCallDatamapping.putAll(interactionMappings);
        }
        if(policytransferDatamapping.size() > 0)
        {
            surveytransfersaveCallDatamapping.putAll(policytransferDatamapping);
        }
        String reqBodyForSaveCallData = JSON.serialize(CreateSaveCallDataRequest_DTO_HUM.requestStructuresaveCallDatalightning(disconnectCallDatamapping, surveytransfersaveCallDatamapping, UUID));
        Continuation contransfer = null;
        HUM_Webservice_Callout__c savecallServiceDetails = HUM_Webservice_Callout__c.getValues(PhoneBookConstants_H_HUM.SERVICE_SAVECALLDATA);
        if (savecallServiceDetails != null && savecallServiceDetails.Active__c)
        {
            HttpRequest requestsave = new HttpRequest();
            requestsave.setEndpoint(savecallServiceDetails.End_Point_URL__c);
            requestsave.setMethod(savecallServiceDetails.Request_Type__c);
            requestsave.setBody(reqBodyForSaveCallData);
            if (!Test.isRunningTest() && String.isNotBlank(savecallServiceDetails.Certificate_Name__c)) 
            {
            requestsave.setClientCertificateName(savecallServiceDetails.Certificate_Name__c);
            }
            contransfer = new Continuation(Integer.valueof(savecallServiceDetails.Timeout__c));
            contransfer.continuationMethod = PhoneBookConstants_H_HUM.respConsultBlindSaveCallData;
            contransfer.state = contransfer.addHttpRequest(requestsave);
        }
        return contransfer;
    }
}