/**
   @Author Gargey Sharma
   @name Go365KidsDelegate_S_HUM 
   @CreateDate 12/21/2016
   @Description Service Class for Go365 Kids Delegate Section and Go365 Kids Delegate History Section
   @Version 1.0
   @reference REQ - 290967 and REQ - 290969
   @Test Class Go365KidsDelegate_T_HUM 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         	   70895                  12/21/2016                     Original version
********************************************************************************************************************************/
public with sharing class Go365KidsDelegate_S_HUM 
{
    /**
    * This method returns continuation object to controller
    * 
    * @param    string 	Enterprise ID of account
    * @param    string 	History flag
    * @param    string 	Delegate type value
    * @param    string 	Callback method name
    * @return   Object
    */
    public static Object callMemDelegateService(string sEnterpriseId, string sHistory, string sDelegateType, string sCallbackMethod)
    {
        HUM_Webservice_Callout__c oMemDelSer = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.GO365KIDSDEL_SERVICENAME);
        integer iTIMEOUT_INT_SECS = Integer.valueof(oMemDelSer.Timeout__c) / 10000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        HttpRequest req = new HttpRequest();
        req.setMethod(oMemDelSer.Request_Type__c);  
        req.setHeader(HUMConstants.HUM_PCP_SER_CONTENTTYPE, oMemDelSer.Content_Type__c);
        req.setTimeout(iTIMEOUT_INT_SECS);
        req.setEndpoint(oMemDelSer.End_Point_URL__c);      
        req.setHeader(HUMConstants.HUM_PCP_SER_CONECTION, HUMConstants.HUM_PCP_SER_KEEPALIVE);
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