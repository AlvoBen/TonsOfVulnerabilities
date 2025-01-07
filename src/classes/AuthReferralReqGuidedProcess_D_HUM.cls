/*******************************************************************************************************************************
Apex Class Name :  AuthReferralReqGuidedProcess_D_HUM.cls
Version: 1.0
Created On      : 01/13/2021
Function        : Data Class for AuthReferralReqGuidedProcess_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Riya Agarwal                                      01/05/2021                    REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
* Suraj Patil										05/07/2021					  DF 3003 and DF 3001 in Qtest
*********************************************************************************************************************************/
public with sharing class AuthReferralReqGuidedProcess_D_HUM
{
    public static boolean bKMD{ get; set;}  
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String sEM_Value= 'EM';
    Public Static final String sHiphan = '-';
    Public Static final String SUBGROUP_MENTORREDIRECT_HUM = 'subgroup:';
    Public Static final String MED = 'MED';
    Public Static final String  VOB_MEDICARE ='RSO- Medicare';
    Public Static final String HUM_PARTD_DRUGREQUESTOR = 'CRM';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String KMDOCUMENT = 'KMDocument';

    	
   
 @testVisible public static MentorServiceRequest_DTO_HUM RequestStructureKDMService(MentorServiceRequest_DTO_HUM KDMRequestDTO, Id sAccountId)
    {  
        string sContractNum;
        string sPBPCode;
        string sMedicaresegId; 
        string sQueryText = '';
        string sProduct; 
        string sProductType;
        string sPlatform; 
        string sBenCoverage;
        string sGrpNumber;           
        String sPolMemId = sAccountId;
            if(NULL!= sPolMemId)
            {
                Policy_Member__c objPolMem = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Benefit_coverage__c,
                                             Policy__r.Group_Number__c,Policy__r.Medicare_Segment_ID__c From Policy_Member__c Where Id =: sPolMemId];
                if(objPolMem != null)
                {   
                    sPlatform = objPolMem.Policy_Platform__c;
                    sBenCoverage = objPolMem.Policy__r.Benefit_coverage__c;
                    sContractNum = objPolMem.Policy__r.Contract_Number__c;
                    sPBPCode = objPolMem.Policy__r.PBP_Code__c;
                    sMedicaresegId = objPolMem.Policy__r.Medicare_Segment_ID__c;  
                    sGrpNumber = objPolMem.Policy__r.Group_Number__c;
                    sProduct = objPolMem.Product__c;
                    sProductType = objPolMem.Product_Type__c;
                   
                   List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                              Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :VOB_MEDICARE];
                    if(sProduct  == MED && !lstRefBusinessLineProdGroups.isEmpty())
                    {
                        sQueryText = sContractNum+sHiphan+sPBPCode;
                        if(String.isNotBlank(sMedicaresegId)) sQueryText += sHiphan +  sMedicaresegId;
                    }
                    else 
                    {
                        if( sPlatform == sEM_Value || sPlatform == sMTV_Value )
                        {
                            sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sBenCoverage;
                        } 
                        else if( sPlatform == sLV_Value || sPlatform == sCI_Value || sPlatform == sCAS_Value )
                        {
                            if(String.isNotBlank(sGrpNumber))
                            {
                                sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sGrpNumber;
                            }
                        } 

                    }
                 }
            }
        
        MentorServiceRequest_DTO_HUM oRequest = new MentorServiceRequest_DTO_HUM();      
        MentorServiceRequest_DTO_HUM.Request oHasSearch = new MentorServiceRequest_DTO_HUM.Request();
        oHasSearch.BenefitGridsOnly = true;
        oHasSearch.ConsumingApp = HUM_PARTD_DRUGREQUESTOR;
        oHasSearch.Query = sQueryText;
        oRequest.Request = oHasSearch;
        return oRequest;
  }  
    public static Boolean ParseStructureKMDocument(MentorServiceResponse_DTO_HUM KMServiceResponse)
    {
        Boolean sKMDocuStatus = false;
        if (KMServiceResponse != null && KMServiceResponse.HasSearchResultsResponse != null)
        {
                if (KMServiceResponse.HasSearchResultsResponse != null)
                {
                     sKMDocuStatus = Boolean.valueOf(KMServiceResponse.HasSearchResultsResponse.HasMatches);
                     
                }
            
        }

        return sKMDocuStatus;
    }
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUM_PCP_SER_SOAPACTION,'');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
    
    public static Object MentorGridService(Id sAccount)
    {
    continuation oConMentor;
         try
         { 
        MentorServiceRequest_DTO_HUM KMDRequestDTO = new MentorServiceRequest_DTO_HUM();            
        HUM_Webservice_Callout__c KMDServiceDetails = HUM_Webservice_Callout__c.getValues(KMDOCUMENT);
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(KMDServiceDetails.Timeout__c) / 1000;
        oConMentor = new Continuation(iTIMEOUT_INT_SECS);
        // Set callback method should be common for KM Document services 
        oConMentor.continuationMethod = 'parseKMDResponse';
        HttpRequest httpKMDReqObj = builtHttpRequest(KMDServiceDetails);
        String sjsonRequest = JSON.serialize(RequestStructureKDMService(KMDRequestDTO, sAccount));
        httpKMDReqObj.setBody(sjsonRequest);
        MentorServiceResponse_DTO_HUM responseKMD = new MentorServiceResponse_DTO_HUM();
        oConMentor.state = responseKMD;
        oConMentor.addHttpRequest(httpKMDReqObj);
        }
        catch(Exception e)
        {           
             HUMExceptionHelper.logErrors(e, 'AuthReferralReqGuidedProcess_D_HUM', 'MentorGridService');          
        }

        return oConMentor;
    }

     public static Object parseKMDResponse(List<string> labels,object state)
    {  
        try
        {
         MentorServiceResponse_DTO_HUM oResponseKMD;
          HttpResponse responseKMD = Continuation.getResponse(labels[0]);           
            if (responseKMD != null && (responseKMD.getStatusCode() == HTTP_OK_HUM || responseKMD.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest())
            {
                oResponseKMD = (MentorServiceResponse_DTO_HUM) System.JSON.deserialize(responseKMD.getBody(), MentorServiceResponse_DTO_HUM.class);
               bKMD = ParseStructureKMDocument(oResponseKMD);
            }
            else
            {
                bKMD = false;
            }
 
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'AuthReferralReqGuidedProcess_D_HUM', 'parseKMDResponse');
        }
        
      return bKMD;
    }
}