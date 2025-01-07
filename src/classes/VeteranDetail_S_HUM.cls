/*****************************************************************************************
Apex class Name    : VeteranDetail_S_HUM
Version            : 1.0
Function           : This is the service class to hit the Veteran Serivce to update the data in Source system and CRM and
					 To fetch the Tracking information from Veteran Service
                     This class is used to display the Veteran Details
Created Date       : 03/04/2021
Test Class         : VeteranDetail_T_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Anjani Vadadi                                      03/04/2021          Original Version
 *   Ajay Chakradhar                           		    05/05/2021	        REQ- 2184294 - Replacing constants with Consumer Management Constants
 *   Giri Babu                                          05/25/2021          US - 2015850 Veterans Update History Tracking  
*   Giri Babu                                          06/18/2021          DF-3235 defect fix

 ****************************************************************************************************************************/
public with sharing class VeteranDetail_S_HUM {
    @testVisible private string sRequest;
    @testVisible private string loggedInUserId;
    Private string sCRM_SOURCE ='crm-service-api'; 
    Private String sTIMESTAMP  ='yyyy-MM-dd\'T\'HH:mm:ss';
    Private Integer iService_Timeout_HUM  = 120000;
    
/* Method Name  :  buildRequest
* Description   :  This method is used to assign values to the VeteranSaveInfoService 
* Return Type   :  VeteranGetRequest_DTO_HUM
* Parameters    :  Veteran_DTO_HUM
*/
    
    @testVisible public VeteranSaveRequest_DTO_HUM buildRequest(Veteran_DTO_HUM oVetParams){
        VeteranSaveRequest_DTO_HUM sRequestBody = new VeteranSaveRequest_DTO_HUM();
        sRequestBody.memberIdentityQualifier = !String.isBlank(oVetParams.sMemberGenKey)?oVetParams.sMemberGenKey:fetchGenKey(oVetParams.sAccountId);
        sRequestBody.modifiedBy = string.isBlank(loggedInUserId) ? fetchUserName() : loggedInUserId;
        sRequestBody.sourceId = sCRM_SOURCE;
        sRequestBody.veteranStatus = oVetParams.sVeteranStatus;
        sRequestBody.isEnrolledInVA = oVetParams.sVAHealthEnrollee != null ? Boolean.valueOf(oVetParams.sVAHealthEnrollee) : null; 
        sRequestBody.sourceSystemTimestamp = DateTime.Now().format(sTIMESTAMP);   
        return sRequestBody;
    }
    
    /*
    * Returns an HttpRequest
    * <p>
    * This method is used to generate the request
    *
    * @param  HttpRequest,HUM_Webservice_Callout__c,String
    * @return HttpRequest
    */ 
      
    @testVisible public HttpRequest setHttpRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oVetSaveService, String sRequestBody)
    {        
        oRequest.setMethod(oVetSaveService.Request_Type__c);
        oRequest.setBody(sRequestBody);        
        oRequest.setHeader('content-type',oVetSaveService.Content_Type__c);    
        if(!Test.isRunningTest())oRequest.setClientCertificateName(oVetSaveService.Certificate_Name__c);
        oRequest.setTimeout(iService_Timeout_HUM);
        oRequest.setEndpoint(oVetSaveService.End_Point_URL__c);
        return oRequest;   
    }    
    
/* Method Name  :  fetchUserName
* Description   :  This method is used to fetch current userName 
* Return Type   :  string
* Parameters    :  NA
*/  
    @testVisible private string fetchUserName(){
        User oCurrentUser = [select network_User_Id__c from User where Id=:UserInfo.getUserId()];
		if (oCurrentUser != null)
        {
            loggedInUserId = oCurrentUser.Network_User_Id__c;
            return loggedInUserId;
        }
        else { 
            return null;
        }
    }

/* Method Name    fetchGenKey
* Description   :  This method Is used to fetch Member GenKey
* Return Type   :  String
* Parameters    :  NA
*/  
@testVisible
    Private static string fetchGenKey(String AccountId){
        
        Account oAccount = [select Mbr_Gen_Key__c from Account where id=:AccountId];       
        Return oAccount.Mbr_Gen_Key__c;
    }

  /* Method Name  :  getVeteranData
    * Description   :  This method is used to make http request 
    * Return Type   :  object
    * Parameters    :  sMemberGenKey
    */   
    public static Continuation getVeteranData(string sMemberGenKey, string sAccountId)
    {   
        HUM_Webservice_Callout__c oVetGetService = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.SVETERAN_GET_SERVICE);
        Integer iTIMEOUT_INT_SECS = Integer.valueof(oVetGetService.Timeout__c)/1000;
        Continuation continuation = new Continuation(iTIMEOUT_INT_SECS);
        try
        {  
            VeteranGetRequest_DTO_HUM oVeteranGetReqDTO = buildVeteranGetRequest(sMemberGenKey, sAccountId);
            if(oVeteranGetReqDTO != null)
            {
                String sjsonRequest = JSON.Serialize(oVeteranGetReqDTO);
                HttpRequest request = new HttpRequest();
                request.setMethod(oVetGetService.Request_Type__c); 
                request.setHeader('content-type',oVetGetService.Content_Type__c);   
           
                
                request.setBody(sjsonRequest);
                if(!Test.isRunningTest())    
                request.setClientCertificateName(oVetGetService.Certificate_Name__c);
                request.setTimeout(Integer.valueof(oVetGetService.Timeout__c));
                request.setEndpoint(oVetGetService.End_Point_URL__c);
                continuation.continuationMethod = GLOBAL_CONSTANT_HUM.VETERAN_GETSERVICE_CALLBACK;
                continuation.State = sMemberGenKey;
                continuation.addHttpRequest(request);
            }
            else
            {
                return null;
            }          
        }
        catch(Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_S_HUM', 'getVeteranData');
            return null;
        } 
        return continuation;
    }
    
    /* Method Name  :  buildVeteranGetRequest
    * Description   :  This method is used to build the request for veteran get service 
    * Return Type   :  VeteranGetRequest_DTO_HUM
    * Parameters    :  VeteranHistoryTracking_DTO_HUM
    */
    
    @testVisible 
    public static VeteranGetRequest_DTO_HUM buildVeteranGetRequest(string sMemberGenKey, string sAccountId){
        VeteranGetRequest_DTO_HUM oVetGetReqDTO = new VeteranGetRequest_DTO_HUM();
        oVetGetReqDTO.memberIdentityQualifier = !String.isBlank(sMemberGenKey) ? sMemberGenKey : fetchGenKey(sAccountId); 
        oVetGetReqDTO.masterPersonId = '';
        return oVetGetReqDTO;
    }
    
    /*
    *  This method is used to Parse response from MBEPlus service
    *  @name   processVeteranGetResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processVeteranGetResponse(List<string> labels,object state)
    {
        list<VeteranHistoryTracking_DTO_HUM>  vetHistTrackDTO;
        try
        {
            HttpResponse oResponse= Continuation.getResponse(labels[0]);
             if(oResponse != null){
                 vetHistTrackDTO = parseVeteranGetResponse(oResponse);
             }
        } 
        catch (Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_S_HUM', 'processVeteranGetResponse');
            return null;   
        }
        return vetHistTrackDTO;    
    }
    
    /*
    *  This method is used to Parse response 
    *  @name   parseVeteranGetResponse
    *  @param  HttpResponse
    *  @return List<VeteranHistoryTracking_DTO_HUM>
    */   
    @testvisible 
    public static List<VeteranHistoryTracking_DTO_HUM> parseVeteranGetResponse(HttpResponse objResponse)
    {
        try
        {
            List<VeteranHistoryTracking_DTO_HUM> oListHistoryTrackDTO;
            Integer sStatusCode  = objResponse.getStatusCode();
            if (sStatusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sStatusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            {
                String sResponse = objResponse.getBody();
                VeteranGetResponse_DTO_HUM oVetResponse = new VeteranGetResponse_DTO_HUM();
                oVetResponse = (VeteranGetResponse_DTO_HUM)System.JSON.deserialize(sResponse,VeteranGetResponse_DTO_HUM.class); 
                if (oVetResponse != null && oVetResponse.latestAudits !=null )
                {
                    oListHistoryTrackDTO = new List<VeteranHistoryTracking_DTO_HUM>();
                    for(VeteranGetResponse_DTO_HUM.Audits ovetResAudit : oVetResponse.latestAudits){
                        VeteranHistoryTracking_DTO_HUM sVeteranTrack=new VeteranHistoryTracking_DTO_HUM();
                        if (ovetResAudit.jsonInput != null)
                        {
                            sVeteranTrack.sModifiedBy =ovetResAudit.jsonInput.modifiedBy;
                        }
                        sVeteranTrack.sActionStatus = ovetResAudit.veteranStatusAction;
                        sVeteranTrack.sApplicationUsed = ovetResAudit.sourceId;
                        if (!String.isBlank(ovetResAudit.timestamp))
                        { 
                            String sODateTime = ovetResAudit.timestamp.replace('T',' ' );
                            datetime dLastMod = Datetime.valueOf(sODateTime);
                            sVeteranTrack.sLastmodifiedDate = dLastMod.format('MM/dd/yyyy HH:mm');
                        }
                        oListHistoryTrackDTO.add(sVeteranTrack);
                    }
                }
            }
            return oListHistoryTrackDTO;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'VeteranDetail_S_HUM', 'parseVeteranGetResponse');
            return null;
        }
    } 
}