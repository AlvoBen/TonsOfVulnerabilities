/*****************************************************************************************
Apex class Name     : ClaimImage_C_HUM
Version             : 1.0
Function            : Controller to VF Page:
                      Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
Created Date        :  29 feb 2016
Test Class          : ClaimImage_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Harika Devi Kalidindi          25398               12/15/2015          Original Version REQ - 244383:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
 * Harika Devi Kalidindi          27503               05/14/2016          Defect# 225410.0001 Added Error message that is to be displayed when claim image is not present for the selected claim
 * Prasanthi Kandula              211792              03/30/2018          REQ - 301521 Updated logic to handle mutliple claims images recevied from service
 * Prasanthi Kandula              216296              04/18/2018          REQ - 301521 Updated logic to display ECN/DCN number based on URL received
 * Prasanthi Kandula                                  02/01/2021          US-1669846 SonarQube critical security fixes  
*****************************************************************************************************************************/
    Public with sharing class ClaimImage_C_HUM 
    {
        @TestVisible private String sRequest ='';
        private ClaimImageService_S_HUM oclaimImg = new ClaimImageService_S_HUM();
     
        /*
        * Method Name : ClaimImage_C_HUM
        * Description : Constructor to initialize variables
        * Return type : NA
        * Parameter   : NA
        */
        public ClaimImage_C_HUM()
        {
            sClaimNumber = ApexPages.currentPage().getParameters().get('ClaimNumber').escapeHtml4();
            sPlatform = ApexPages.currentPage().getParameters().get('Platform').escapeHtml4();
            if(ApexPages.currentPage().getParameters().get('SrcLvCASPrefix') != null)
            sSrcLvCASPrefix = ApexPages.currentPage().getParameters().get('SrcLvCASPrefix').escapeHtml4();
            sStartDate = ApexPages.currentPage().getParameters().get('ServiceStartDate'); 
            sClient = setclientValue(sPlatform);
            bShowServiceError = false;
        }
       
        public String sClaimNumber{get; set;}
        public String sClient;
        public String sSrcLvCASPrefix;
        public String sPlatform;
        public Transient String stargetURL {get;set;} 
        public String sStartDate;
        public Transient Boolean bShowServiceError{get;set;}
        public Transient List<ImagingService_DTO_HUM> lstImagingDTO {get; set;}
       
       
        /*
        * Method Name   :  initiateRequest
        * Description   :  This method is used to Call the Service while loading the page
        * Return Type   :  object
        * Parameters    :  NA
        */
        public Object initiateRequest()
        { 
            ClaimImageService_S_HUM oClaimImageService = new ClaimImageService_S_HUM();
            ImagingServiceGetClaimURLRequest_H_HUM oClaimImageReq = new ImagingServiceGetClaimURLRequest_H_HUM ();
            oClaimImageReq = oClaimImageService.assignValuesToHelper(sClaimNumber,sClient,sStartDate); 
            if(oClaimImageReq != null)           
            {              
                HUM_Webservice_Callout__c oSvcHeaderClaimImage = HUM_Webservice_Callout__c.getValues('ImagingService_GetClaimURL_V2'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimImage.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod='processResponse';
                string sJsonRequest = JSON.Serialize(oClaimImageReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest =oclaimImg.setupRequest(oRequest,oSvcHeaderClaimImage,sjsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con; 
            } 
            else
            {
                return null;
            }   
        }
        
       /*
        * Method Name   :  processResponse
        * Description   :  This method is used to get response from service.
        * Return Type   :  void 
        * Parameters    :  NA
        */
        public void processResponse()
        {
            try 
            {
                lstImagingDTO = new List<ImagingService_DTO_HUM>();
                HttpResponse response = Continuation.getResponse(sRequest);
                lstImagingDTO = parseResponse(response);
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ClaimImage_C_HUM', 'processResponse');
            }
        }
    
        /*
        * Method Name   :  parseResponse
        * Description   :  This method is used to parse the response from service.
        * Return Type   :  List<ImagingService_DTO_HUM> 
        * Parameters    :  HttpResponse response
        */
        public List<ImagingService_DTO_HUM> parseResponse(HttpResponse response)
        {
            try
            {
                lstImagingDTO = new List<ImagingService_DTO_HUM>();
                Integer statusCode = response.getStatusCode();
                if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM || statusCode == 201)
                {
                    ImagingServiceGetClaimURLResponse_H_HUM oResponse = new ImagingServiceGetClaimURLResponse_H_HUM();
                    String sClaimResponse  = response.getbody();
                    oResponse = (ImagingServiceGetClaimURLResponse_H_HUM)System.JSON.deserialize(response.getbody(), ImagingServiceGetClaimURLResponse_H_HUM.class);
                    ImagingServiceGetClaimURLResponse_H_HUM oImgServiceResp = new ImagingServiceGetClaimURLResponse_H_HUM();
                    if(oResponse.GetClaimURLsResponse != null)
                    {
                        ImagingServiceGetClaimURLResponse_H_HUM.GetClaimURLsResponse oGetClaimResp = oResponse.GetClaimURLsResponse;
                        List<ImagingServiceGetClaimURLResponse_H_HUM.ClaimURLs> lstImagingResponse = oGetClaimResp.ClaimURLs;
                        for(ImagingServiceGetClaimURLResponse_H_HUM.ClaimURLs oClaimURL : lstImagingResponse)
                        {
                            ImagingService_DTO_HUM oImagingDTO = new ImagingService_DTO_HUM();
                            oImagingDTO.sDCN = oClaimURL.DCN;
                            oImagingDTO.sECN = oClaimURL.ECN;
                            oImagingDTO.Receipt_Date = oClaimURL.ReceiptDate.left(10).right(5).left(2) +'/'+oClaimURL.ReceiptDate.left(10).right(2)+'/'+oClaimURL.ReceiptDate.left(10).left(4);
                            oImagingDTO.bRepriced = oClaimURL.Repriced;
                            oImagingDTO.dTotal_Billed_Charges = oClaimURL.TotalBilledCharges;
                            oImagingDTO.sBilling_Provider_Last_Facility_Name  = oClaimURL.BillingProviderLastFacilityName;
                            oImagingDTO.sBilling_provider_id = oClaimURL.BillingProviderId;
                            oImagingDTO.sRendering_Provider_Last_Facility_Name = oClaimURL.RenderingProviderLastFacilityName;
                            oImagingDTO.sRendering_Provider_Id = oClaimURL.RenderingProviderId;
                            oImagingDTO.sClaim_Frequency_Code = oClaimURL.ClaimFrequencyCode;
                            oImagingDTO.sClaimURL = oClaimURL.URL;
                            if(oImagingDTO.sClaimURL.containsIgnoreCase('ecn'))
                                oImagingDTO.sECNOrDCN = oClaimURL.ECN;
                            else
                                oImagingDTO.sECNOrDCN =  oClaimURL.DCN;
                            lstImagingDTO.add(oImagingDTO);
                        }
                        if(lstImagingDTO.size()==1)
                        {
                            stargetURL= lstImagingDTO[0].sClaimURL;    
                        }
                        if(lstImagingDTO.isEmpty())
                        {
                            bShowServiceError = true;
                            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimImage_No_ClmImage);
                            ApexPages.addMessage(myMsg);
                        }
                    }
                    else
                    {
                        bShowServiceError = true;
                        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimImage_No_ClmImage);
                        ApexPages.addMessage(myMsg);    
                    }
                }
                else
                {
                    bShowServiceError = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                    ApexPages.addMessage(myMsg);
                }
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ClaimImage_C_HUM', 'parseResponse');
            }   
            return lstImagingDTO;
        }
      
              
       /*
        * Method Name   :  setclientValue
        * Description   :  This method is used to set the sClient value
        * Return Type   :  string
        * Parameters    :  sPlatform
        */
        public string setclientValue (string sPlatform)
        {
            if(sPlatform == GLOBAL_CONSTANT_HUM.Platformcode_HUM )
            {
                sClient = GLOBAL_CONSTANT_HUM.SrcMTCVPrefix_HUM;
            }
            else         
            {
                sClient = sSrcLvCASPrefix ;
            }  
            return sClient;
       } 
          
   }