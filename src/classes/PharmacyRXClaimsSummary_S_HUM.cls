/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_S_HUM
Version            : 1.0
Function           : This Class contains methods for services to fetch Pharmacy Rx Claims Summary information.
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*******************************************************************************************************************/
public with sharing Class PharmacyRXClaimsSummary_S_HUM {
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
    Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    /**
* Returns request object of continuation object     
* Method to invoke the GetClaims to show claim summary data
* @parameter memberGenKey, policyStartDate, policyEndDate     
* @return continuation object
*/
    @AuraEnabled(continuation=true)
    public static Continuation invokePharmacyClaimSummary(string memberGenKey,string policyStartDate, string policyEndDate){
        try
        {
            HUM_Webservice_Callout__c pharmacyClaimSummaryService = HUM_Webservice_Callout__c.getValues('RxClaimSummary_GetClaims');
            integer iTIMEOUT_INT_SECS = Integer.valueof(pharmacyClaimSummaryService.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processPharmacyRXClaimSummaryResponse';		       
            HttpRequest httpPharmacyRXClaimSummaryRequest = builtHttpRequest(pharmacyClaimSummaryService);
            httpPharmacyRXClaimSummaryRequest.setBody(JSON.serialize(PharmacyRXClaimsSummary_D_HUM.requestStructurePharmacyRXClaimSummary(memberGenKey,policyStartDate,policyEndDate)));
            con.addHttpRequest(httpPharmacyRXClaimSummaryRequest);		       
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsSummary_S_HUM', 'invokePharmacyClaimSummary');
            return null;
        }
    }
    
    
    /**
     * Returns responses of continuation object    
     * Callback Method to parse the getClaims service response
     * @parameter labels, object   
     * @return object
     */
    @AuraEnabled
    public static Object processPharmacyRXClaimSummaryResponse(List < string > labels, Object State) {
        try
        {
            HttpResponse response = Continuation.getResponse(labels[0]);
            String claimSummaryResponse = response.getBody();                   
            List<PharmacyRXClaimsSummary_wrapper_HUM> wrapperObjList = new List<PharmacyRXClaimsSummary_wrapper_HUM>() ;
            
            if (!claimSummaryResponse.containsIgnoreCase(VOB_FAULT_CODE) && response != null && ((response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                wrapperObjList = PharmacyRXClaimsSummary_D_HUM.responseStructurePharmacyRXClaimSummary(claimSummaryResponse);
                if(wrapperObjList.size()>0){
                    return wrapperObjList;
                }
                else{
                    if(response.getHeader('ResponseCode') !=null && response.getHeader('ResponseCode') != '00' && response.getHeader('ResponseCode') != '213' ){
                        insertFaultExceptionToErrorLog(null,response.getHeader('ResponseCode'),'PharmacyRXClaimsSummary_S_HUM','processPharmacyRXClaimSummaryResponse');
                        return 'Integration Error'; 
                    }                		           
                    else if(response.getHeader('ResponseCode') != null && response.getHeader('ResponseCode') == '213'){
                        insertFaultExceptionToErrorLog(null,response.getHeader('ResponseCode'),'PharmacyRXClaimsSummary_S_HUM','processPharmacyRXClaimSummaryResponse');
                        return 'No Data';
                    }
                }
                return null;
            }
            else{                
                if(claimSummaryResponse != null && claimSummaryResponse != '' && claimSummaryResponse != '[  ]' && response.getHeader('ResponseCode') != '00')
                    insertFaultExceptionToErrorLog(claimSummaryResponse,response.getHeader('ResponseCode'),'PharmacyRXClaimsSummary_S_HUM','processPharmacyRXClaimSummaryResponse');
                return 'Integration Error';                
            }
        } catch (Exception ex)
        {                
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsSummary_S_HUM', 'processPharmacyRXClaimSummaryResponse');
            return 'Integration Error';
        }
    }
    
 
    /**
* Returns HttpRequest object    
* builtHttpRequest Method to parse HUM_Webservice_Callout__c RxClaimSummary_GetClaims
* @parameter HUM_Webservice_Callout__c ServiceObj  
* @return HttpRequest
*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj) {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c) 
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) {                 
                request.setClientCertificateName(ServiceObj.Certificate_Name__c);                
            }
            
            if (String.isNotBlank(ServiceObj.Content_Type__c))
                request.setHeader(HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
            request.setHeader(HUM_RSS_SER_CONECTION, HUM_RSS_SER_KEEPALIVE);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(HUM_RSS_SER_SOAPACTION, '');
        }
        return request;
    }
    
	/**
	* insertFaultExceptionToErrorLog Method to insert exception to Error_Log object
	* @parameter String claimSummaryResponse,string statusCode
	* @return void
	*/
	public static void insertFaultExceptionToErrorLog( String claimSummaryResponse,string statusCode,String className,String methodName){            
        Error_Log__c objErrorLog = new Error_Log__c();   
        objErrorLog.Class_Name__c = className;
        objErrorLog.Method_Name__c = methodName;
        objErrorLog.Type__c ='Integration Error';
        objErrorLog.Status__c = statusCode;          
        if(claimSummaryResponse != null){
            claimSummaryResponse = claimSummaryResponse.replace('-', '_');
            FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
            faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(claimSummaryResponse , FaultResponse_DTO_HUM.class);            
            if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
            {                
                if (faultResponse.Fault.detail.errorInfo != null)
                {
                    objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
                    objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
                }                                           
            }  
        }
        else{
            objErrorLog.Error_Code__c = statusCode;
            objErrorLog.Error_Message__c = returnExceptionMesssage(statusCode);
        }
        insert objErrorLog; 
    }
	
	/**
	* returnExceptionMesssage Method to insert return to exception message based on statusCode
	* @parameter String claimSummaryResponse,string statusCode
	* @return void
	*/
    public static string returnExceptionMesssage(string statusCode){
        if(statusCode== '99') return 'InternalErrorUnknown';
        else if(statusCode== '100') return 'RequestParameterMissing';
        else if(statusCode== '101') return 'RequestorDataMissing';
        else if(statusCode== '102') return 'MemberIdentifierMissing';
        else if(statusCode== '103') return 'KeyDataMissing';
        else if(statusCode== '104') return 'ValueDataMissing';
        else if(statusCode== '107') return 'MemberGenKeyValueMissing';
        else if(statusCode== '119') return 'AuthorizationNumberMissing';
        else if(statusCode== '200') return 'InvalidRequestParameter';
        else if(statusCode== '201') return 'InvalidRequestXml';
        else if(statusCode== '203') return 'InvalidKeyData';
        else if(statusCode== '204') return 'InvalidValueData';
        else if(statusCode== '213') return 'InvalidMemberGenKey';
        else if(statusCode== '220') return 'ToDate should be greater than FromDate';
        else if(statusCode== '221') return 'Difference of FromDate and ToDate should not be more than 2 years';
        else if(statusCode== '222') return 'InvalidAuthorizationNumber';
        else if(statusCode== '300') return 'NoDataRetrievedForTheRequest';
        else if(statusCode== '301') return 'InternalServiceError';
        else if(statusCode== '500') return 'CouldNotConnectToDatabase';
        else if(statusCode== '501') return 'ErrorWhileRetrievingDataFromDatabase';
        else if(statusCode== '600') return 'Invalid Requestor';
        else if(statusCode== '601') return 'RequestorThresholdReached';
        else if(statusCode== '302') return 'ClientKeyMissing';
        else return 'Unknown Exception Occurred';
    }
}