/*****************************************************************************************
Apex class Name     : ClaimImage_LC_HUM
Version             : 1.0
Function            : Controller to Claims Image LWC Component:
                      Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
Created Date        :  29 feb 2016
Test Class          : ClaimImage_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Sagar.G                                                                Original Version REQ - 3856190:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
 *****************************************************************************************************************************/
    Public with sharing class ClaimImage_LC_HUM 
    {
        @TestVisible private static String sRequest ='';
        public static String sClaimNumber{get; set;}
        public static String sClient;
        public static String sSrcLvCASPrefix;
        public static String sPlatform;
        public static Transient String stargetURL {get;set;} 
        public static String sStartDate;
        public static Transient Boolean bShowServiceError{get;set;}
        public static Transient List<ImagingService_LDTO_HUM> lstImagingLDTO {get; set;}
		@TestVisible private static String continuationLabel;
    	@TestVisible private static String continuationState;
       
       
        /*
        * Method Name   :  initiateRequest
        * Description   :  This method is used to Call the Service while loading the page
        * Return Type   :  object
        * Parameters    :  NA
        */
        @AuraEnabled (continuation=true)        
        public static object initiateRequest(string ClaimNumber,string Platform,string StartDate,string SrcLvCASPrefix)
        { 
            sClaimNumber=ClaimNumber;
            sPlatform=Platform;
            sStartDate=StartDate;
			sSrcLvCASPrefix=SrcLvCASPrefix;
            sClient = setclientValue(sPlatform);
            ImagingServiceGetClaimURLRequest_H_HUM oClaimImageReq = new ImagingServiceGetClaimURLRequest_H_HUM ();
            oClaimImageReq = ClaimImageService_LS_HUM.assignValuesToHelper(sClaimNumber,sClient,sStartDate);
            if(oClaimImageReq != null)           
            {              
                HUM_Webservice_Callout__c oSvcHeaderClaimImage = HUM_Webservice_Callout__c.getValues('ImagingService_GetClaimURL_V2');
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimImage.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod='processResponse';
                string sJsonRequest = JSON.Serialize(oClaimImageReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest =ClaimImageService_LS_HUM.setupRequest(oRequest,oSvcHeaderClaimImage,sjsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                continuationLabel = con.addHttpRequest(oRequest);
                continuationState = 'data';
                con.state = continuationState;
                return con;
            } 
            else
            {
                return '';
            } 
                 

        }
        
       /*
        * Method Name   :  processResponse
        * Description   :  This method is used to get response from service.
        * Return Type   :  void 
        * Parameters    :  NA
        */
        @AuraEnabled 
        public static  Object processResponse(List<string> labels, Object State)
        {
            try 
            {
                HttpResponse response = Continuation.getResponse(labels[0]);
                return parseResponse(response,State);
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ClaimImage_LC_HUM', 'processResponse');
                return null;
            }
        }
    
        /*
        * Method Name   :  parseResponse
        * Description   :  This method is used to parse the response from service.
        * Return Type   :  List<ImagingService_LDTO_HUM> 
        * Parameters    :  HttpResponse response
        */
        
        public static Object parseResponse(HttpResponse response,Object state)
        {
            try
            {
                lstImagingLDTO = new List<ImagingService_LDTO_HUM>();
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
                            ImagingService_LDTO_HUM oImagingDTO = new ImagingService_LDTO_HUM();
                            oImagingDTO.sDCN = oClaimURL.DCN;
                            oImagingDTO.sECN = oClaimURL.ECN;
                            oImagingDTO.Receipt_Date = oClaimURL.ReceiptDate.left(10).right(5).left(2) +'/'+oClaimURL.ReceiptDate.left(10).right(2)+'/'+oClaimURL.ReceiptDate.left(10).left(4);
                            oImagingDTO.bRepriced = oClaimURL.Repriced;
                            oImagingDTO.dTotal_Billed_Charges = '$'+oClaimURL.TotalBilledCharges;
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
                            lstImagingLDTO.add(oImagingDTO);
                        }
                        if(lstImagingLDTO.size()==1)
                        {
                            stargetURL= lstImagingLDTO[0].sClaimURL;    
                        }
                        if(lstImagingLDTO.isEmpty())
                        {
                            return Label.ClaimImage_No_ClmImage;
                        }
                    }
                    else
                    {
                        return Label.ClaimImage_No_ClmImage;
                    }
                }
                else
                {
                    return Label.ClaimsSummary_Service_Error;
                }
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'ClaimImage_LC_HUM', 'parseResponse');
            }   
            return lstImagingLDTO;
        }
      
              
       /*
        * Method Name   :  setclientValue
        * Description   :  This method is used to set the sClient value
        * Return Type   :  string
        * Parameters    :  sPlatform
        */
        public static string setclientValue (string sPlatform)
        {
            if(sPlatform == GLOBAL_CONSTANT_HUM.PLATFORMCODE_HUM )
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