/*******************************************************************************************************************************
Apex Class Name : ApplicationEnrollSearch_LC_HUM
Version         : 1.0
Created On      : 10/12/2020
Function        : This class is used for showing Application search data table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        10/12/2020               Original Version
*    2.0           Kajal Namdev                                         05/07/2021               Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
*******************************************************************************************************************************************************************/
public class ApplicationEnrollSearch_LC_HUM 
{
    public static String contstate;
    /* 
    * Method name : AMSearchService
    * Description : This method will return the list of Application search Data
    * Parameter   : Application search input data which comes from UI
    */
    @AuraEnabled(continuation=true)
    public static Continuation AMSearchService(String appSearchModal)
    {
        AppEnrollSearch  objAppSearch = (AppEnrollSearch) JSON.deserialize(appSearchModal, AppEnrollSearch.class); 
        // Setup Request
        HUM_Webservice_Callout__c AMSearch = HUM_Webservice_Callout__c.getValues('Application_Search');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(AMSearch.Timeout__c);
        HttpRequest request = new HttpRequest();
        request.setMethod(AMSearch.Request_Type__c);
        request.setHeader(Label.HUMServiceTesterController_content_type,AMSearch.Content_Type__c);
        request.setTimeout(Integer.valueOf(AMSearch.Timeout__c));
        request.setEndpoint(AMSearch.End_Point_URL__c);
        if (!Test.isRunningTest()) request.setClientCertificateName(AMSearch.Certificate_Name__c);
        
        ApplicationSearch_Request_DTO_HUM.EnrollmentRequestClass reqFields = new ApplicationSearch_Request_DTO_HUM.EnrollmentRequestClass();
        if(String.isNotBlank(objAppSearch.medicareId)) reqFields.MedicareId = objAppSearch.medicareId;
        if(String.isNotBlank(objAppSearch.fName)) reqFields.FirstName = objAppSearch.fName;
        if(String.isNotBlank(objAppSearch.lName)) reqFields.LastName =objAppSearch.lName;
        if(String.isNotBlank(objAppSearch.DOB)) reqFields.MemberDOB = objAppSearch.DOB;
        if(String.isNotBlank(objAppSearch.sApplicationId)) reqFields.ApplicationId = objAppSearch.sApplicationId;
        if(String.isNotBlank(objAppSearch.sOECConfirmationId)) reqFields.OECConfirmationId = objAppSearch.sOECConfirmationId;
        if(String.isNotBlank(objAppSearch.sBarCode)) reqFields.BarCode = objAppSearch.sBarCode;
        if(String.isNotBlank(objAppSearch.sMedicaidId)) reqFields.MedicaidId = objAppSearch.sMedicaidId;
        if(String.isNotBlank(objAppSearch.sEffectiveDateFrom)) reqFields.EffectiveDateFrom = objAppSearch.sEffectiveDateFrom;
        if(String.isNotBlank(objAppSearch.sEffectiveDateTo)) reqFields.EffectiveDateTo = objAppSearch.sEffectiveDateTo;
        if(String.isNotBlank(objAppSearch.sReceivedDateFrom)) reqFields.ReceivedDateFrom = objAppSearch.sReceivedDateFrom;
        if(String.isNotBlank(objAppSearch.sReceivedDateTo)) reqFields.ReceivedDateTo = objAppSearch.sReceivedDateTo;
        reqFields.SearchType = 'M';
        
        ApplicationSearch_Request_DTO_HUM requestBody = new ApplicationSearch_Request_DTO_HUM();
        requestBody.EnrollmentRequest = reqFields;
        
        String sBody = JSON.serialize(requestBody);
        request.setBody(sBody);
        
        Continuation oConAM;
        if(AMSearch != null)
        {
            if(AMSearch.Active__c)
            {
                oConAM = new Continuation(120);
                oConAM.continuationMethod ='AMSearchServiceResponse';
                contstate = oConAM.addHttpRequest(request);
                oConAM.state = contstate;
            }
        }
        return oConAM;      
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
    public class AppEnrollSearch extends MemberSearchEnrollment_LC_HUM.TRRSearchModal
    {
        public String sMedicaidId;
        public string sApplicationId;
        public string sOECConfirmationId;
        public string sBarCode;
        public string sEffectiveDateFrom;
        public string sEffectiveDateTo;
        public string sReceivedDateFrom;
        public string sReceivedDateTo;
        public appEnrollSearch(){}
    }
    
    /* 
    * Method name : AMSearchServiceResponse
    * Description : This method will process the response from callout 
    * Parameter   : state for maintain previous data across async transaction
    */
    @AuraEnabled
    public static Object AMSearchServiceResponse(Object state)
    {
        try
        {
            String contId = String.valueOf(state);
            List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();
            HttpResponse res = Continuation.getResponse(contId);
            String response = res.getBody();
            ApplicationSearch_Response_DTO_HUM responseBody = null;          
            Boolean successResponse = false;
            
            if(res.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('EnrollmentResponse'))
            {
                responseBody = (ApplicationSearch_Response_DTO_HUM) System.JSON.deserialize(res.getbody(), ApplicationSearch_Response_DTO_HUM.class);
                successResponse = true;
            }
            else
            {
                HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),HUM_Webservice_Callout__c.getValues('Application_Search').End_Point_URL__c,'Connection Error');
                throw new HUMCustomException(h);
            }
            
            if(successResponse)
            {
                ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper resultWrapper = new ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper();
                resultWrapper = responseBody.EnrollmentResponse;
                if(resultWrapper.EnrollmentDetail != null && !resultWrapper.EnrollmentDetail.isEmpty())
                {
                    List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper> lstEnrollmentDetails =  new List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper>();
                    lstEnrollmentDetails = resultWrapper.EnrollmentDetail;
                    Integer amResultCounter = 1;
                    for(ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetail: resultWrapper.EnrollmentDetail)
                    {
                        lstLegacyService.add(new MemberSearchEnrollmentHelper_LC_HUM(eDetail,amResultCounter));
                        amResultCounter++;
                    }
                    return lstLegacyService;
                }
                else
                {
                    return lstLegacyService;
                }
            }
            else
            {
                throw new HUMCustomException('callout Exception'); 
            }
        }
        catch(Exception e)
        {
            throw new HUMCustomException(e.getMessage()); 
        }
    }
}