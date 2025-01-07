/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsDetail_S_HUM
Version            : 1.0
Function           : This Class contains methods for services to get Pharmacy Rx Claims Detail information.
Created On         : July 24 2020
Test Class         : PharmacyRXClaimsDetail_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Goud                                      07/24/2020                 Original Version - REQ - 1151554 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claim Details page (RxXP)
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*******************************************************************************************************************/

Public with sharing Class PharmacyRXClaimsDetail_S_HUM {
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String HUM_RSS_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_RSS_SER_CONECTION = 'Connection';
    Public Static final String HUM_RSS_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_RSS_SER_SOAPACTION = 'SOAPAction';
    /**
* Returns request object of continuation object     
* Method to invoke the GetClaimDetails to show claim summary data
* @parameter memberGenKey, authorizationNumber   
* @return continuation object
*/
    @AuraEnabled(continuation=true )
    public static Continuation invokePharmacyClaimDetail(string memberGenKey, string authorizationNumber){
        try
        {
            HUM_Webservice_Callout__c pharmacyClaimSummaryService = HUM_Webservice_Callout__c.getValues('RxClaimSummary_GetClaimDetail');
            Integer iTIMEOUT_INT_SECS = Integer.valueof(pharmacyClaimSummaryService.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processPharmacyRXClaimDetailResponse';		       
            HttpRequest httpPharmacyRXClaimSummaryRequest = builtHttpRequest(pharmacyClaimSummaryService);
            httpPharmacyRXClaimSummaryRequest.setBody(JSON.serialize(PharmacyRXClaimsDetail_D_HUM.requestStructurePharmacyRXClaimDetail(memberGenKey, authorizationNumber)));
            con.addHttpRequest(httpPharmacyRXClaimSummaryRequest);		       
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsDetail_S_HUM', 'invokePharmacyClaimDetail');
            return null;
        }
    }
    
    /**
* Returns responses of continuation object    
* Callback Method to parse the getClaimDetails service response
* @parameter labels, object   
* @return object
*/
    @AuraEnabled
    public static Object processPharmacyRXClaimDetailResponse(List < string > labels, Object State) {
        try{
            HttpResponse response = Continuation.getResponse(labels[0]);
            String claimDetailResponse = response.getBody();
            If (!claimDetailResponse.containsIgnoreCase(VOB_FAULT_CODE) && response!= null && ((response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {

                PharmacyRXClaimsDetail_Response_DTO_HUM res = PharmacyRXClaimsDetail_Response_DTO_HUM.parse(response.getbody());
                if(response.getHeader('ResponseCode') == '00' ||  response.getHeader('ResponseCode') == '213'){
                    return res;

                }else{
                    if(response.getHeader('ResponseCode') !=null && response.getHeader('ResponseCode') != '00' && response.getHeader('ResponseCode') != '213' ){
                        PharmacyRXClaimsSummary_S_HUM.insertFaultExceptionToErrorLog(null,response.getHeader('ResponseCode'),'PharmacyRXClaimsDetail_S_HUM','processPharmacyRXClaimDetailResponse');
                        return 'Integration Error'; 
                    }                		           
                    else if(response.getHeader('ResponseCode') != null && response.getHeader('ResponseCode') == '213'){
                        PharmacyRXClaimsSummary_S_HUM.insertFaultExceptionToErrorLog(null,response.getHeader('ResponseCode'),'PharmacyRXClaimsDetail_S_HUM','processPharmacyRXClaimDetailResponse');
                        return 'No Data';
                    }else{
                        return null;
                    }
                }
            }
            else{
                Error_Log__c objErrorLog = new Error_Log__c();   
                objErrorLog.Class_Name__c = 'PharmacyRXClaimsDetail_S_HUM';
                objErrorLog.Method_Name__c = 'processPharmacyRXClaimDetailResponse';
                objErrorLog.Type__c ='Integration Error';
                objErrorLog.Status__c = String.valueOf(response.getStatusCode());
                string claimDetailFaultResponse = response.getbody().replace('-', '_');
                FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
                faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(claimDetailFaultResponse , FaultResponse_DTO_HUM.class);
                if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
                {                
                    if (faultResponse.Fault.detail.errorInfo != null)
                    {
                        objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
                        objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
                    }                                           
                }                
                insert objErrorLog; 
                return null;
           }
        }catch(Exception ex){            
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsDetail_S_HUM', 'processPharmacyRXClaimDetailResponse');
            return null;
        }
    }
    
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
}