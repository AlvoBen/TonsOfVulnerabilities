/*****************************************************************************************
Apex class Name    : ClinicalAuthDetails_C_HUM 
Version            : 1.0
Function           : Controller to VF Page:
                     This class is used to display Authorization Details for InPatient/Out Patient .
Created Date       : 15 Dec 2015
Test Class         : ClinicalAuthDetails_T_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Prasanthi Kandula            29039                12/15/2015          Original Version
 *   Ranjeeth N                   29646                02/01/2016          Moved the constants to static class 
 *   Manish Kumar                 24069                04/30/3016          Included logging changes.
 *   Santhi Mandava               40488                09/20/2016          Included logic to populate attachment key.
 *   Imran                        416071               12/03/2019          Implemented logic to handle old as well as new service.
 *   Prasanthi Kandula                                 12/27/2019          REQ - 416086 Added communication records section on details page.   
 *   Imran                                             12/27/2019          REQ - 416096 Added Letters records section on details page.   
 *   Santhi Mandava                                    03/06/2010          US - 903330 Display jump links on Auth/Referral Detail page 
 *   Amar Gadewar                                      03/18/2020          USER STORY 948109 PR00091574 - MF 1 - AHT - Add jump links on Auth/Referral Detail page (CRM)
 * Santhi Mandava                                      04/03/2020          US - 995046 Removed Rapid force switch
 *   RamaKrishna Gundeti          821950               05/18/2020          Added Accordion section for Communication records.
 * Trina Ghosh                                   02/09/2021                  US:1669845 - Low Security Fixes
 * Rahul Chaurasia										04/06/2021			 User Story 1279935: PR00094226 - SF- Auth/Referral Summary old version Decommission
 * Rahul Chaurasia									  19/06/2021			 User Story 1895286: T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues 
 *   Ranadheer                                             05/18/2021           Made @TestVisible
 ******************************************************************************************************************************/
public with sharing class ClinicalAuthDetails_C_HUM {
	  @TestVisible
      private String sRequest  = '';
      private String sResponse = '';
      private AuthReferral_Query_D_HUM oAuthReferral = new AuthReferral_Query_D_HUM();

     /*
     * Method Name : ClinicalAuthDetails_C_HUM
     * Description : Constructor for the class , initialize variable
     * Return type : NA
     * Parameter   : NA
     */
      public ClinicalAuthDetails_C_HUM() 
      {
         if( ApexPages.currentPage().getParameters().get('AuthId') != Null )
            sAuthID = ApexPages.currentPage().getParameters().get('AuthId').escapeHtml4();
          if( ApexPages.currentPage().getParameters().get('RecId') != Null )
            sPolicyMemRecID = ApexPages.currentPage().getParameters().get('RecId').escapeHtml4();
        if(string.isNotBlank(sPolicyMemRecID))
        {
            Account oAccount = oAuthReferral.getMemberAccount(ApexPages.currentPage().getParameters().get('RecId'));
            sEnterpriseID = oAccount.Enterprise_ID__c;
            sDateOfBirth = oAccount.Birthdate__c;
        }
        bSearchError = false; 
        bAuthTypeIP = false;
        bAuthTypeOP = false;
        bServiceError = false;
        standardMapvalue = '';               
             
        createFloatingLinksList();
      } 
      ClinicalAuthDetails_S_HUM oAuthDetailService = new ClinicalAuthDetails_S_HUM();
      public ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapper{get;set;}
      public ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapperIP{get;set;}
      public ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapperOP{get;set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvider{get;set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstDiagnosis{get;set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProcedure{get;set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProcedureOP{get;set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstCommunicationRecords {get; set;}
      public List<ClinicalAuthDetailsWrapper_DTO_HUM> lstLettersRecords {get; set;}
      public string sLettersPageNumber {get;set;}
      public string sCommunicationPageNumber {get;set;}
      public Boolean bServiceError{get;set;}
      public Boolean bSearchError{get;set;}
      public Boolean bAuthTypeIP{get;set;}
      public Boolean bAuthTypeOP{get;set;}
      public String sPolicyMemRecID {get; set;}
      public String standardMapvalue {get; set;}
      public String sAuthID{get;set;}
      public string sTotalCommunicationRecords {get;set;}
      public string sTotalLettersRecords {get;set;}      
      public string sEnterpriseID;
      public string sDateOfBirth;
      public list<string> lstFloatingLinks{get;set;}
      
      /*
      * This method is used to create the list of floating links which are visible at the top of the auth detail page
      * @param  NA
      * @return void
      */
      public void createFloatingLinksList()
      {
         lstFloatingLinks = new list<string>();
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail1);   
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail2);   
         if(bAuthTypeIP) lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail3);   
         if(bAuthTypeOP) lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail4);   
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail5); 
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail6); 
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail7); 
         lstFloatingLinks.add(FloatingLinkConstants_HUM.sfloatLinkAuthDetail8);   
      }

    /*
     * Method Name   :  authDetailsRequest
     * Description   :  This method is used to Call the Service while loading the page
     * Return Type   :  Object
     * Parameters    :  None
     */
    public Object authDetailsRequest() 
    {
        ClinicalAuthDetails_S_HUM oAuthDetailsService = new ClinicalAuthDetails_S_HUM();
        ClinicalAuthDetailsRequest_H_HUM_V2 oclinicalAuthdetailV2 = new ClinicalAuthDetailsRequest_H_HUM_V2();
        oclinicalAuthdetailV2 = oAuthDetailsService.assignValuesToHelperV2(sAuthID, sEnterpriseID, sDateOfBirth);           
 		
        if(oclinicalAuthdetailV2 != null)

        {
            HUM_Webservice_Callout__c oServiceCalloutClinicalV2 = HUM_Webservice_Callout__c.getValues('Clinical_AuthDetail_V2'); 
            Integer iTIMEOUT_INT_SECS;
            String sJsonRequest;
            HttpRequest oRequest = new HttpRequest();
             
			iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClinicalV2.Timeout__c) / 1000;                
            sJsonRequest = JSON.Serialize(oclinicalAuthdetailV2);                 
            oRequest = oauthDetailsService.setHttpRequest(oRequest, oServiceCalloutClinicalV2, sjsonRequest);              
 
            Continuation con = new Continuation(iTIMEOUT_INT_SECS );
            con.continuationMethod='processResponse';
            sRequest = con.addHttpRequest(oRequest);
            return con;
        }
        else {
                return null;
             }
    }
    
    /*
     * Method Name   :  processResponse
     * Description   :  This is the Callback method for Continous Object to process the Response data
     * Return Type   :  Object
     * Parameters    :  None
     */
    public Object processResponse() 
    {  
          HttpResponse oResponse = Continuation.getResponse(this.sRequest);
          if(oResponse != null) responseWrap(oResponse);
         return null;
    }
    
     /*
     * Method Name   :  responseParse
     * Description   :  This method is used to deserialize the response and diplay the response on Page
     * Return Type   :  void
     * Parameters    :  List<ClinicalAuthDetailsResponse_H_HUM.Authorization>
     */  
     public void  responseParse(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization)
     {
        ClinicalAuthDetails_S_HUM oService = new ClinicalAuthDetails_S_HUM();
        if(lstAuthorization[0].AuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_INP_HUM ||lstAuthorization[0].AuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_BHINP_HUM)
        {
           bAuthTypeIP = true;
           oAuthDetailsWrapperIP = oService.retrieveReferralInformationIP(lstAuthorization);
           lstProcedure = oService.retrieveProcedureCodes(lstAuthorization);
        }
        else if(lstAuthorization[0].AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_OUP_HUM || lstAuthorization[0].AuthType==GLOBAL_CONSTANT_HUM.AUTHTYPE_BHOUP_HUM)
        {
           bAuthTypeOP = true;
           oAuthDetailsWrapperOP = oService.retrieveReferralInformationOP(lstAuthorization);
           lstProcedureOP = oService.retrieveProcedureCodesOutPatient(lstAuthorization);
        }
        oAuthDetailsWrapper = oService.retrieveAuthDetail(lstAuthorization);
        lstProvider = oService.retrieveProviderDetails(lstAuthorization);
        lstDiagnosis = oService.retrieveDiagnosisCodes(lstAuthorization);
        standardMapvalue = JSON.serialize(oService.standardMapForLogging);
        createFloatingLinksList();
     }
    
    /*
    * Method Name   :  responseWrap
    * Description   :  This method is used to deserialize the response and diplay the response on Page
    * Return Type   :  HttpResponse sResponse
    * Parameters    :  HttpResponse
    */  
    public ClinicalAuthDetailsResponse_H_HUM responseWrap(HttpResponse sResponse)
    {
       try
       {
           oAuthDetailsWrapper = new ClinicalAuthDetailsWrapper_DTO_HUM();
           oAuthDetailsWrapperIP = new ClinicalAuthDetailsWrapper_DTO_HUM();
           oAuthDetailsWrapperOP = new ClinicalAuthDetailsWrapper_DTO_HUM();
           ClinicalAuthDetailsResponse_H_HUM oResponse = new ClinicalAuthDetailsResponse_H_HUM();
           if(String.isNotBlank(sResponse.getBody()))    oResponse = (ClinicalAuthDetailsResponse_H_HUM)JSON.deserialize(sResponse.getBody(),ClinicalAuthDetailsResponse_H_HUM.class);
           if(oResponse != NULL)
           {
           if(oResponse.AuthorizationsResponse != null)
           {
               bServiceError = false;
               ClinicalAuthDetailsResponse_H_HUM.AuthorizationsResponse oAuthResponse = oResponse.AuthorizationsResponse;
               ClinicalAuthDetailsResponse_H_HUM.Authorizations oAuthorizations = oAuthResponse.Authorizations;
               List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
               lstAuthorization = oAuthorizations.Authorization;
               responseParse(lstAuthorization );
           } 
           if(oResponse.CommunicationRecordsResponse != null)
                {
                    bServiceError = false;
                    ClinicalAuthDetailsResponse_H_HUM.CommunicationRecordsResponse oTotalCommRecords = oResponse.CommunicationRecordsResponse;
                    List<ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords> lstCommRecords = new List<ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords>();
                    sTotalCommunicationRecords = oTotalCommRecords.TotalRecordsFound;
                    lstCommRecords = oTotalCommRecords.CommunicationRecords;
                    lstCommunicationRecords = oAuthDetailService.parseCommunicationRecords(lstCommRecords);
                }
                if(oResponse.LettersResponse != null)
               {
                   bServiceError = false;
                   ClinicalAuthDetailsResponse_H_HUM.LettersResponse oTotalLettersRecords = oResponse.LettersResponse;
                   List<ClinicalAuthDetailsResponse_H_HUM.LettersRecords> lstLettersRecordsHum = new List<ClinicalAuthDetailsResponse_H_HUM.LettersRecords>();
                   sTotalLettersRecords = oTotalLettersRecords.TotalRecordsFound;
                   lstLettersRecordsHum = oTotalLettersRecords.LettersRecords;                   
                   lstLettersRecords = oAuthDetailService.parseLettersRecords(lstLettersRecordsHum);                       
               }
                   }

           else
           {
                bServiceError = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClinicalServiceError_HUM);
                ApexPages.addMessage(myMsg);  
           }
        }
        catch(Exception ex)
        {
            bServiceError = true;
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_C_HUM', 'responsewrap');
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClinicalServiceError_HUM);
            ApexPages.addMessage(myMsg);
        }  
        return null;
   }

                      /*
    * Method Name   :  callCommunicationService
    * Description   :  This method is used to call communication records service on click of Retrieve next records button
    * Return Type   :  object
    * Parameters    :  NA
    */  
    public object callCommunicationService()
    {
        try
        {
           CommunicationRecordsRequest_H_HUM  oCommReq = new CommunicationRecordsRequest_H_HUM();
            ocommReq = oAuthDetailService.assignValuesToCommReq(sAuthID,sCommunicationPageNumber,sEnterpriseID,sDateOfBirth);
            if(ocommReq != null)
            {
                HUM_Webservice_Callout__c oCommRecordsService = HUM_Webservice_Callout__c.getValues('AuthDetails_Communication_Records');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oCommRecordsService.Timeout__c)/1000;  
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processCommunicationResponse';
                string sJsonRequest = JSON.Serialize(ocommReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oAuthDetailService.setHttpRequestCommRecs(oRequest,oCommRecordsService,sJsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con; 
            }
            else 
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_C_HUM', 'callCommunicationService');
            return null;
        } 
    }
    
       /*
     * Method Name   :  call Letters Service
     * Description   :  This method is used to deserialize the response and diplay the response on Page
     * Return Type   :  void
     * Parameters    :  List<ClinicalAuthDetailsResponse_H_HUM.Authorization>
     */  
    
     public object callLettersService()
     {
         try
        {
            LettersRecordsRequest_H_HUM  oLettersReq = new LettersRecordsRequest_H_HUM();
            oLettersReq = oAuthDetailService.assignValuesToLettersReq(sAuthID,sPolicyMemRecID, sLettersPageNumber);              
            if(oLettersReq != null)
            {
                HUM_Webservice_Callout__c oLettersRecordsService = HUM_Webservice_Callout__c.getValues('AuthDetails_Letters');                  
                Integer iTIMEOUT_INT_SECSs = Integer.valueof(oLettersRecordsService.Timeout__c)/1000;  
                Continuation con = new Continuation(iTIMEOUT_INT_SECSs);
                con.continuationMethod = 'processLettersResponse';
                string sJsonRequest = JSON.Serialize(oLettersReq);                
                HttpRequest oRequest = new HttpRequest();
                oRequest = oAuthDetailService.setHttpRequestLettersRecs(oRequest,oLettersRecordsService,sJsonRequest);                
                sRequest = con.addHttpRequest(oRequest);
                return con;
            }
            else
            {
               return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V2', 'claimDetailsRequest');
            return null;
        }
     }

    
    /*
    * Method Name   :  processCommunicationResponse
    * Description   :  callback method for communication records service
    * Return Type   :  object
    * Parameters    :  NA
    */  
    public Object processCommunicationResponse() 
    {  
        try {
            HttpResponse oResponse = Continuation.getResponse(this.sRequest);
            CommunicationRecordsResponse_H_HUM oResp;
            if(oResponse != null) 
            {
                lstCommunicationRecords = oAuthDetailService.responseWrapCommRecs(oResponse,lstCommunicationRecords);
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_C_HUM', 'processCommunicationResponse');
            return null;
        }
    }
    
    /*
    * Method Name   :  processLettersResponse
    * Description   :  callback method for Letters records service
    * Return Type   :  object
    * Parameters    :  NA
    */  
     
     public Object processLettersResponse() 
    {   
          HttpResponse oResponse = Continuation.getResponse(this.sRequest);          
          if(oResponse != null)
          {            
            lstLettersRecords = oAuthDetailService.responseWrapLettersRecs(oResponse,lstLettersRecords);

          }
          return null;
    } 
 }