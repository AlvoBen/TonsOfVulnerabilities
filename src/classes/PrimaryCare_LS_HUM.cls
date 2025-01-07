/************************************************************************************************************************************************
Apex Class Name :  PrimaryCare_LS_HUM
Version: 1.0
Created Date     : APR 04 2022
Function         : Service Layer Class for PrimaryCarePhysicanHum component.
Test Class       :
*************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                                 04/04/2022                 Original Version as per REQ - 3196414
* Aishwarya Pawar                                    05/31/2023                 DF - 7703
*************************************************************************************************************************************************/

Public with sharing Class PrimaryCare_LS_HUM {
    Public Static final Integer VOB_ServiceTimeOut_HUM = 120; 
    Public Static final String PCP_PROCESSRESMETHOD_HUM ='parseMbeResponse';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String PCP_EPS_PROCESSRESMETHOD_HUM ='pcpActiveHistoryFinalResponse';
    Public Static final String PCP_EPS_PROCESS_PHONE_RESPONSE_HUM ='processPcpPhoneNumberResponse';
    Public Static final String RESTV1SERVICENAME_PROVIDERSEARCH_HUM = 'EPSServiceREST_V1';
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String SOAPAction_HUM = 'SOAPAction';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    public Static final String MBEFaultCode= '83.1.1';
    Public Static final Integer MINDIFF_PROVIDERSEARCH_HUM = 10000000;
    public class ServiceDownException extends Exception {}
    public static object getPcpPcdData(String memberPlanId,String asOfDate){
        try
        {
            HUM_Webservice_Callout__c MBEServiceDetails = HUM_Webservice_Callout__c.getValues('MBEPlusService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(MBEServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseMBEResponse';
            HttpRequest httpMBEReqObj = builtHttpRequest(MBEServiceDetails);
            httpMBEReqObj.setBody(JSON.serialize(PrimaryCare_LD_HUM.getMemberMBERequest(memberPlanId, asOfDate)));
            con.state=memberPlanId+'$#'+asOfDate;
            con.addHttpRequest(httpMBEReqObj);    
            return con;
            
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'SendRequestMBEService');
            return Null;
        }
    }
    
    @AuraEnabled
    public static object processMBEResponse(List<string> labels, Object State){
        try
        {
            HttpResponse response = Continuation.getResponse(Labels[0]);
            if (response != null && (response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) && 
			!response.getBody().containsIgnoreCase(VOB_FAULT_CODE))
            {
                MBEPlusResponse_DTO_HUM oResponse = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(response.getBody(), MBEPlusResponse_DTO_HUM.class);
                MBEPlusResponse_DTO_HUM.GetMemberResponse GetMemberResponse = oResponse.GetMemberResponse;
                MBEPlusResponse_DTO_HUM.Body body = GetMemberResponse.body;
                MBEPlusResponse_DTO_HUM.Member member = Body.member;
                MBEPlusResponse_DTO_HUM.SoldProductList objSoldProductList = member.SoldProductList;
				MBEPlusResponse_DTO_HUM.PlatformPointerList platformPointerList = member.PlatformPointerList;
                Map<String,Object> mapMBEResponse = new Map<String,Object>();
				mapMBEResponse.put('SoldProduct', objSoldProductList);
				mapMBEResponse.put('platformpointerlist', platformPointerList);
                return  mapMBEResponse;
            }
            else if(response.getBody().containsIgnoreCase(VOB_FAULT_CODE) && response.getBody().contains(MBEFaultCode) )
            {
                return null;                
            }
            else{
                throw New ServiceDownException('SERVICEERROR'+response.getBody());
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PrimaryCare_D_HUM', 'ProcessMbeResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }
    
    /**
* Returns HttpRequest request with request body
* <p>
* Method to prepare httpRequest details and certification details
*
* @param  HUM_Webservice_Callout__c record .
* @return HttpRequest
*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        try
        {
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, ServiceObj.Content_Type__c);
                request.setHeader(Connection_HUM, Keep_alive_HUM);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(SOAPAction_HUM, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'builtHttpRequest');
            return Null;
        }
    }
    
    /*
* Returns HttpRequest Object
* <p>
* Method to prepare EPS service call
*
* @param  String npID coming from MBE.
* @return HttpRequest
*/
    public static Continuation makeEPSServiceCall(String providerNumber)
    {
        try
        {
            HUM_Webservice_Callout__c EPSServiceDetails = HUM_Webservice_Callout__c.getValues(RESTV1SERVICENAME_PROVIDERSEARCH_HUM);
            integer iTIMEOUT_INT_SECS = Integer.valueof(EPSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processEPSSData';
            HttpRequest httpEPSReqObj = builtHttpRequest(EPSServiceDetails);
            httpEPSReqObj.setBody(JSON.serialize(PrimaryCare_LD_HUM.getMemberEPSRequest(providerNumber)));
            con.state=providerNumber;
            con.addHttpRequest(httpEPSReqObj);    
            return con;
            
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PrimaryCare_LS_HUM', 'makeEPSServiceCall');
            return Null;
        }
    }
    
    /**
* Returns an object
* <p>
* This method Process the response from Service
*
* @pram  List<String> : Request Headers
* @param  Object : State
* @return  object
* @see
*/
    public static Object processPcpPhoneNumberResponse(List < String > Labels, Object State)
    {
        try
        {
            PrimaryCare_DTO_HUM primaryDto;
            HttpResponse response = Continuation.getResponse(Labels[0]);
            if (response != null && (response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM))
            {
                EPSSRESTV1SearchProviderResponse_H_HUM oResponse = (EPSSRESTV1SearchProviderResponse_H_HUM)System.JSON.deserialize(response.getBody(), EPSSRESTV1SearchProviderResponse_H_HUM.class);
                String phoneNumber = PrimaryCare_LD_HUM.getPhoneNumber(oResponse);
                return phoneNumber;
            }
            else
            {
                return primaryDto;
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PrimaryCare_LS_HUM', 'processPcpPhoneNumberResponse');
            return null;
        }
    }
}