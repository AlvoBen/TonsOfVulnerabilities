/**************************************************************************************************************************************************
Apex Class Name :   RTIPrintSummary_S_HUM 
Version:  1.0
Function           : This Is the service class for RTI Print Details View Link
Created Date       :   20 June 2017
Test Class         :  RTIPrintService_T_HUM

        Modification Log:  
    
 *   Developer                   Code Review             Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------------------------  
 *  Shivam Anand                    106733              06/20/2017         Original Version
 *  Imran                                               13/03/2020         Req#436960 RTI VAT Changes Logo Issue
 *  Prasanthi Kandula                                   04/16/2020         US 1003374 - To display send link on RTI Detail page
 *  Prasanthi Kandula                                   05/18/2021         US - 2082425 Added new card for channel Email
 *  Anuradha Gajbhe                                     05/18/2021         US - 2082398 Added logic to display new card for channel Text
 *  Rahul Chaurasia                                     05/19/2021         US - 2082372 Added logic to display new card for channel Web
***************************************************************************************************************************************************/

Public with sharing Class RTIPrintSummary_S_HUM
{
    @TestVisible private String sBody = '' ;
    Private List<RTIPrintSummary_DTO_HUM> lstRTIDto = New List<RTIPrintSummary_DTO_HUM>();      
    /* 
    *  This Is the Constructor for the class RTIPrintSummary_S_HUM
    *  @name   RTIPrintSummary_S_HUM
    *  @param  NA
    *  @return NA
    *  @throws NA     
    */
     Public RTIPrintSummary_S_HUM()
     {
     }
    
   /*
    *  This method Is used to form http request for RTI print service
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
                             sInteractionKey, channelCode, iSkipNmbr, recordLimit                        
    *  @return HttpRequest - request for RTI print service
    *  @throws setupRequest exception
    */
    Public HttpRequest setupRequest(HttpRequest oRequest, HUM_Webservice_Callout__c oSvcHeaderRTI, String sInteractionKey, String sChannelCode, Integer iSkipNmbr, Integer iRecordLimit)
    { 
        HttpRequest oHttpRequest = New HttpRequest(); 
        try
        { 
            sBody = buildRequest(sInteractionKey, sChannelCode, iSkipNmbr, iRecordLimit);
            If (String.isNotBlank(sBody))   
                            {
                oRequest.setMethod(oSvcHeaderRTI.Request_Type__c);
                oRequest.setHeader('content-type', oSvcHeaderRTI.Content_Type__c);  
                oRequest.setHeader('Connection', 'keep-alive');
                If (!Test.isRunningTest())  
                {
                    oRequest.setClientCertificateName(oSvcHeaderRTI.Certificate_Name__c);
                }
                oRequest.setTimeout(Integer.valueof(oSvcHeaderRTI.Timeout__c));
                oRequest.setEndpoint(oSvcHeaderRTI.End_Point_URL__c);
                oRequest.setBody(sBody);  
                oHttpRequest = oRequest;
            }  
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'RTIPrintSummary_S_HUM' , 'setupRequest');
        }
        return oHttpRequest; 
    }
    
   /*
    *  This method Is used to build the required inputs needed to hit the RTI print service
    *  @name   buildRequest
    *  @param  String sInteractionKey
               String channelCode, String iSkipNmbr, Integer recordLimit            
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    @testvisible private string buildRequest(String sInteractionKey, String sChannelCode, Integer iSkipNmbr, Integer iRecordLimit)
    {   
        String sJsonbody = '';
        try
        { 
            RTIServiceDetailRequest_H_HUM  oRequest = New RTIServiceDetailRequest_H_HUM(); 
            RTIServiceDetailRequest_H_HUM.GetInteractionDetails getRTISummary = New RTIServiceDetailRequest_H_HUM.GetInteractionDetails();
            getRTISummary.Interactionkey = sInteractionKey;
            if (sChannelCode == 'Text' ){
               sChannelCode = 'SMS'; 
            }
            getRTISummary.channelCode = sChannelCode;
            getRTISummary.RecordLimit = iRecordLimit;
            getRTISummary.skip = iSkipNmbr;
            oRequest.GetInteractionDetails = getRTISummary;
            sJsonbody = JSON.serialize(oRequest);
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTIPrintSummary_S_HUM' , 'buildRequest');
        }
        Return sJsonbody;
    }
      
   /*
    *  This method Is used to Process the Json response from service to Wrapper List
    *  @name   processResponse
    *  @param  string(Json response) - Body of the response in JSON format
    *  @return List<RTIPrintSummary_DTO_HUM> - WrapperList containing the response details
    *  @throws NA
    */ 
    Public List<RTIPrintSummary_DTO_HUM> processResponse(String response) 
    {          
        RTIServiceDetailResponse_H_HUM objRTIresp = New RTIServiceDetailResponse_H_HUM();
        List<RTIPrintSummary_DTO_HUM> listRTIResp = new List<RTIPrintSummary_DTO_HUM>();
        objRTIresp = RTIServiceDetailResponse_H_HUM.parse(response);     
        if(objRTIresp.InteractionDetailResponse != Null)
        {
            lstRTIDto = parseServiceResponse(objRTIresp); 
        }     
        return lstRTIDto;
    }
      
   /*
    *  This method is used to parse the reponse and assign it to the wrapper class objects
    *  @name   parseServiceResponse
    *  @param  RTIServiceDetailResponse_H_HUM - The Response object
    *  @return List<RTIPrintSummary_DTO_HUM> - List of Wrapper class objects
    *  @throws NA
    */
    private List<RTIPrintSummary_DTO_HUM> parseServiceResponse(RTIServiceDetailResponse_H_HUM objRTIresp)
    {        
        List<RTIPrintSummary_DTO_HUM> tempListRTISummary = new List<RTIPrintSummary_DTO_HUM>();            
        RTIServiceDetailResponse_H_HUM.InteractionDetailResponse oInteractionSummaryResponse = objRTIresp.InteractionDetailResponse;        
        RTIServiceDetailResponse_H_HUM.InteractionDetailResult oInteractionSummaryResult = oInteractionSummaryResponse.InteractionDetailResult;                
  
        if(ApexPages.currentPage().getParameters().get('CategoryDesc') =='VAT' )        
        {
            List<RTIServiceDetailResponse_H_HUM.VAT> oInteractionsListVT = oInteractionSummaryResult.vaTs;            
            tempListRTISummary = buildWrapperListVT(oInteractionsListVT);           
        }        
        if(ApexPages.currentPage().getParameters().get('sChannelCode') =='PRINT' )         
        { 
            List<RTIServiceDetailResponse_H_HUM.PRINT> oInteractionsList = oInteractionSummaryResult.prints;
            tempListRTISummary = buildWrapperList(oInteractionsList);
        }  
        if(ApexPages.currentPage().getParameters().get('CategoryDesc') =='EMAIL' )
        { 
            List<RTIServiceDetailResponse_H_HUM.EMAILs> oInteractionsList = oInteractionSummaryResult.emails;
            tempListRTISummary = buildWrapperListEmail(oInteractionsList);
        }  
        if(ApexPages.currentPage().getParameters().get('CategoryDesc') =='EM')
        { 
            List<RTIServiceDetailResponse_H_HUM.eMs> oInteractionsList = oInteractionSummaryResult.eMs;
            tempListRTISummary = buildWrapperListEM(oInteractionsList);
        }               
        if(ApexPages.currentPage().getParameters().get('CategoryDesc') == 'Text' )
        { 
            List<RTIServiceDetailResponse_H_HUM.SMS> oInteractionsList = oInteractionSummaryResult.smss;
            tempListRTISummary = buildWrapperListSms(oInteractionsList);
        }
        if(ApexPages.currentPage().getParameters().get('sChannelCode') =='WEB')        
        { 
            List<RTIServiceDetailResponse_H_HUM.WEB> oInteractionsList = oInteractionSummaryResult.webs;
            tempListRTISummary = buildWrapperListWeb(oInteractionsList);

        }
        if(ApexPages.currentPage().getParameters().get('sChannelCode') =='WB' ) 
        { 
            List<RTIServiceDetailResponse_H_HUM.WB> oInteractionsList = oInteractionSummaryResult.wbs;
            tempListRTISummary = buildWrapperListWb(oInteractionsList);
        }   
        return tempListRTISummary; 
    }
   
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  List<RTIServiceDetailResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTIPrintSummary_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
    private List<RTIPrintSummary_DTO_HUM> buildWrapperList(List<RTIServiceDetailResponse_H_HUM.PRINT> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.PRINT obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     } 
     
     
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperListVT
    *  @param  List<RTIServiceDetailResponse_H_HUM.Interaction> -List of RTIServiceSummaryResponse_H_HUM.Interaction objects with the interaction details 
    *  @return List<RTIPrintSummary_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */  
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListVT(List<RTIServiceDetailResponse_H_HUM.VAT> oInteractionsListVT)
    {        
        List<RTIPrintSummary_DTO_HUM> listRTISummaryVT = new List<RTIPrintSummary_DTO_HUM>();  
        for(RTIServiceDetailResponse_H_HUM.VAT obj : oInteractionsListVT)
        {   
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;            
            listRTISummaryVT.add(objWrapper);
        }
        return listRTISummaryVT;   
     } 
     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListEmail(List<RTIServiceDetailResponse_H_HUM.Emails> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.Emails obj : oInteractionsList)
        {
           RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }  
     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListEM(List<RTIServiceDetailResponse_H_HUM.eMs> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.eMs obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }    
    
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListSms(List<RTIServiceDetailResponse_H_HUM.SMS> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.SMS obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            objWrapper.sdocumentType = obj.documentType;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     } 
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListWb(List<RTIServiceDetailResponse_H_HUM.WB> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.WB obj : oInteractionsList) 
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }     
    private List<RTIPrintSummary_DTO_HUM> buildWrapperListWeb(List<RTIServiceDetailResponse_H_HUM.WEB> oInteractionsList)
    { 
        List<RTIPrintSummary_DTO_HUM> listRTISummary = new List<RTIPrintSummary_DTO_HUM>();
        for(RTIServiceDetailResponse_H_HUM.WEB obj : oInteractionsList)
        {
            RTIPrintSummary_DTO_HUM objWrapper = new RTIPrintSummary_DTO_HUM();
            objWrapper.sdocumentViewUrl = obj.DocumentViewUrl;
            listRTISummary.add(objWrapper);
        }
        return listRTISummary;   
     }  
 }