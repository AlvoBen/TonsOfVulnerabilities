/*******************************************************************************************************************************
Apex Class Name : Go365KidsDelegate_LS_HUM
Version         : 1.0
Created On      : 12/30/2021
Function        : This class is used for performing Go365 kids delegation History data related work
Test Class      : Go365KidsDelegateHistory_LT_HUM
                 
Modification Log: 
* Developer Name                      Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                	  01/10/2022                  REQ# 2588478 -Go365 - Ability to View Go365 Kids Delegation History in a Lightning Experience
* Sagar Gulleve                       21/01/2022				  REQ# 2588464 -Go365 - Ability to View Go365 Kids Delegation	
*
*******************************************************************************************************************************************************************/
public with sharing class Go365KidsDelegate_LS_HUM {

    public Static final String GO365KIDSDEL_SERVICENAME = 'MemberDelegatesService';
    public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';

   /**
    * This method returns continuation object to controller
    * 
    * @param    string 	Enterprise ID of account
    * @param    string 	History flag
    * @param    string 	Delegate type value
    * @param    string 	Callback method name
    * @return   Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object callMemDelegateService(string sEnterpriseId, string sHistory, string sDelegateType, string sCallbackMethod)
    {

        HUM_Webservice_Callout__c oMemDelSer = HUM_Webservice_Callout__c.getValues(GO365KIDSDEL_SERVICENAME);
        integer iTIMEOUT_INT_SECS = Integer.valueof(oMemDelSer.Timeout__c) / 10000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        HttpRequest req = new HttpRequest();
        req.setMethod(oMemDelSer.Request_Type__c);  
        req.setHeader(HUM_PCP_SER_CONTENTTYPE, oMemDelSer.Content_Type__c);
        req.setTimeout(iTIMEOUT_INT_SECS);
        req.setEndpoint(oMemDelSer.End_Point_URL__c);      
        req.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
        if (!Test.isRunningTest() && String.isNotBlank(oMemDelSer.Certificate_Name__c)) req.setClientCertificateName(oMemDelSer.Certificate_Name__c);
        MemberDelegatesServiceRequest_DTO_HUM oRequest = new MemberDelegatesServiceRequest_DTO_HUM();
        MemberDelegatesServiceRequest_DTO_HUM.GetDelegateRequest getDelReq = new MemberDelegatesServiceRequest_DTO_HUM.GetDelegateRequest();
        getDelReq.EnterpriseID = sEnterpriseId;
        getDelReq.History = sHistory;
        getDelReq.DelegateType = sDelegateType;
        oRequest.GetDelegateRequest = getDelReq;
        string sbody = JSON.serialize(oRequest);
        req.setBody(sbody);
        con.state = con.addHttpRequest(req);

        con.continuationMethod = sCallbackMethod;
        
        return con; 
    }

    
}