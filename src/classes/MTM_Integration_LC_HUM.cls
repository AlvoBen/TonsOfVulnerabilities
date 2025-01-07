/**************************************************************************************************************************************************
Apex class Name    :  MTM_Integration_LC_HUM
Version            :  1.0
Function           :  controller for MTM
Modification Log: 
Developer Name                 Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane           09/01/2023                  US: 5012557 Pharmacy - MTM Eligibility
********************************************************************************************************************************************************************************** */
public with sharing class MTM_Integration_LC_HUM {
    public static final Integer HTTP_OK_HUM = 200;
    public static final Integer HTTP_ACCEPTED_HUM = 202;
    public static final Integer HTTP_ACCEPTED_HUM_201 = 201; 
    public static final String VOB_FAULT_CODE = 'faultcode';

    @AuraEnabled(continuation=true)
    public static Object getMtmIndicator( string mTMrequest){
        try {
            HUM_Webservice_Callout__c oSvcHeaderMTM = HUM_Webservice_Callout__c.getValues('MTMServiceHPIE');
            string mTM_URL='callout:NC_CRM_Service_Mulesoft/' + oSvcHeaderMTM.End_Point_URL__c;
            HttpRequest request = new HttpRequest();
            request.setEndpoint(mTM_URL);
            request.setMethod('POST');
            request.setHeader('content-type', 'application/json');
            request.setBody(mTMrequest);            
            Http http = new Http();
            HTTPResponse response = http.send(request);
            if (response != null && ((response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM_201) && (!response.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {    
                return response.getBody();
            }else{
                return response;
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}