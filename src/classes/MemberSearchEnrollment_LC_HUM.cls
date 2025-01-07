/*******************************************************************************************************************************
Apex Class Name : MemberSearchEnrollment_LC_HUM
Version         : 1.0
Created On      : 10/07/2020
Function        : This class is used for showing TRR search data table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        10/07/2020               Original Version
*    2.0           Kajal Namdev                                         05/07/2021               Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
*    3.0           Visweswararao Jayavarapu                             04/07/2023               User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
*******************************************************************************************************************************************************************/
public class MemberSearchEnrollment_LC_HUM
{
    
    public static String contstate;
    /* 
    * Method name : TRRSearchService
    * Description : This method will return the list of TRR search Data
    * Parameter   : TRR search input data which comes from UI
    */
    @AuraEnabled(continuation=true)
    public static Continuation TRRSearchService(String TRRSearchData)
    {
        TRRSearchModal searchModal = (TRRSearchModal) JSON.deserialize(TRRSearchData, TRRSearchModal.class);
        
        // Setup Request
        HUM_Webservice_Callout__c TRRSearch= HUM_Webservice_Callout__c.getValues('TRR_Search');    
        
        integer TIMEOUT_INT_SECS = Integer.ValueOf(TRRSearch.Timeout__c);
        
        HttpRequest request= new HttpRequest();    
        request.setMethod(TRRSearch.Request_Type__c);  
        request.setHeader(Label.HUMServiceTesterController_content_type,TRRSearch.Content_Type__c);
        request.setTimeout(Integer.valueof(TRRSearch.Timeout__c));
        request.setEndpoint(TRRSearch.End_Point_URL__c);
        if(!Test.isRunningTest()) request.setClientCertificateName(TRRSearch.Certificate_Name__c);   
        
        TRRServiceRestRequest_DTO_HUM.TRRRequestClass oRequest= new TRRServiceRestRequest_DTO_HUM.TRRRequestClass();
        if((string.isNotBlank(searchModal.medicareId))) oRequest.MedicareId = searchModal.medicareId;
        if((string.isNotBlank(searchModal.fName))) oRequest.FirstName = searchModal.fName;
        if((string.isNotBlank(searchModal.lName))) oRequest.LastName = searchModal.lName;
        if((string.isNotBlank(searchModal.Hum_ReceivedDateFrom ))) oRequest.HumanaReceivedDateFrom =searchModal.Hum_ReceivedDateFrom;
        if((string.isNotBlank(searchModal.Hum_ReceivedDateTo ))) oRequest.HumanaReceivedDateTo =searchModal.Hum_ReceivedDateTo;
        if((string.isNotBlank(searchModal.DOB))) oRequest.DateOfBirth = searchModal.DOB;
        
        TRRServiceRestRequest_DTO_HUM oRequestbody = new TRRServiceRestRequest_DTO_HUM();
        oRequestbody.TRRRequest = oRequest;
        
        string sBody = JSON.serialize(oRequestbody); 
        request.setBody(sBody);
        
        Continuation oConTRR;
        if(TRRSearch != null)
        {
            if(TRRSearch.Active__c)
            {                
                oConTRR = new Continuation(120);
                oConTRR.continuationMethod ='TRRServiceResponse';
                contstate = oConTRR.addHttpRequest(request);
                oConTRR.state = contstate;
            }
        }
        return oConTRR;    
    }
    
    /* 
    * Method name : TRRServiceResponse
    * Description : This method will return response
    * Parameter   : state 
    */
    @AuraEnabled
    public static Object TRRServiceResponse(Object state)
    {
        try
        {
            String continId = String.valueOf(state);
            List<MemberSearchEnrollmentHelper_LC_HUM> lstTRRServiceData = new list<MemberSearchEnrollmentHelper_LC_HUM>();
            HttpResponse res = Continuation.getResponse(continId);              
            string response = res.getBody();      
            TRRServiceRestResponse_DTO_HUM TRRRDetres = null;
            boolean successResponse = false;
            
            if(res.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('TRRResponse'))
            {
                
                TRRRDetres = (TRRServiceRestResponse_DTO_HUM)System.JSON.deserialize(res.getbody(), TRRServiceRestResponse_DTO_HUM.class);
                successResponse = true;  
            }
            else
            {
                successResponse = false;		
                HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),HUM_Webservice_Callout__c.getValues('TRR_Search').End_Point_URL__c,'Connection Error');
                throw new HUMCustomException(h);
            }
            
            if(successResponse)
            {
                TRRServiceRestResponse_DTO_HUM.TRRResponse TRRResponse = new TRRServiceRestResponse_DTO_HUM.TRRResponse();
                TRRResponse= TRRRDetres.TRRResponse;            
                TRRServiceRestResponse_DTO_HUM.TRRDetails TRRDetails = new TRRServiceRestResponse_DTO_HUM.TRRDetails();
                TRRDetails = TRRResponse.TRRDetails ; 
                
                if(TRRDetails.TRRDetail != null && !TRRDetails.TRRDetail.isEmpty())
                {               
                    List<TRRServiceRestResponse_DTO_HUM.TRRDetail> lstTRRDetail = new List<TRRServiceRestResponse_DTO_HUM.TRRDetail>();                      
                    lstTRRDetail = TRRDetails.TRRDetail ;                              
                    Integer TRRResultCounter = 1;
                    
                    For(TRRServiceRestResponse_DTO_HUM.TRRDetail objTRRDetail : lstTRRDetail){                                         
                        lstTRRServiceData.add(new MemberSearchEnrollmentHelper_LC_HUM(objTRRDetail,TRRResultCounter));
                        TRRResultCounter++;
                    }               
                    return lstTRRServiceData;                
                }
                else
                { 
                    return lstTRRServiceData;    
                }
            }
            else
            {
                throw new HUMCustomException('Callout Exception');
            }
        }
        catch(Exception e) 
        {
            throw new HUMCustomException(e.getMessage());	
        }
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
    public virtual class TRRSearchModal
    {
        public String medicareId;
        public String fName;
        public String lName;
        public String DOB;
        public String Hum_ReceivedDateFrom;
        public String Hum_ReceivedDateTo;
		public String sSSN;
        public TRRSearchModal(){}
    }
}