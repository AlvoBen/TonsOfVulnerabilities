public class PurchaserPlanDetail_LS_HUM {
    
    private static AdditionalResponseWrapper_DTO_HUM addVendorWrapper = new AdditionalResponseWrapper_DTO_HUM();
    private static String sNoneValue = 'NONE';  
    private static String sNullValue = 'Uninitialized';  
    private static String sLVPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM;
    private static String sEMPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM; 
    public static  Boolean switchOnOffSubGroupId = false;
    Private static   String sDummyBenValue = 'XXXXXXXX';
    @TestVisible private static String sBodyVendor ='' ;    
    Private static PurchaserPlan policyPlan = New PurchaserPlan();
    public static list<PurchaserPlan> policyPlanList = new List<PurchaserPlan>();
    @TestVisible private static MemberPlanDetail_LC_HUM oServiceVendorPolicyMem = New MemberPlanDetail_LC_HUM ();
    public static MemberPlan policyMember = New MemberPlan();
    public static String sId;
    public static String comma = ',';
    public static Boolean isCRMSGBOPermissionSetUser  = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CRMS_240_GBO_Access_HUM);
    public static GBEServiceResponse_H_HUM respond = New GBEServiceResponse_H_HUM();
    public class ServiceDownException extends Exception {}
    @testVisible private static GBEServiceResponse_H_HUM.AssociationList associationDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.OrganizationTypeList organizationTypeDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.VendorProgramOfferListGroup vendorProgramGroupDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = NULL;
    
    public static string sRequest;
    public static Map<String,String> stateMap= new Map<String,String>();
    
    /*
* Method Name   :    GetGroupInfoFromGBE
* Description   :    This method makes call to GBE when the Member policy page is loaded 
* Return Type   :    Continuation
* Parameters    :    String
*/  
    @AuraEnabled(continuation=true)
    public static Object getGroupInfoFromGBE(String sRecId) 
    {  
        try{
            
            //method for fetching the PurchaserPlan record
            PurchaserPlan policyPlan = getPurchaserPlanValues(sRecId);
            
            if(policyPlan!=null)
            {  
                HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
                Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
                Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
                cont.continuationMethod=GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PROCESSRESPONSE_HUM; //processResponseVendor
                HttpRequest request = new HttpRequest();
                //calling method to form request
                request= setupRequest(request, ServiceObj, policyPlan); 
                if(request!=null)
                {
                    sRequest = cont.addHttpRequest(request);
                    stateMap.put('sRequest',sRequest);
                    cont.state=stateMap;
                    return cont; 
                }
                else
                {
                    AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM(); 
                    addRespWrapper.isOnSwitch = false;           
                    return addRespWrapper;
                }
            }
            else
            {	
                AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM(); 
                addRespWrapper.isOnSwitch = false; 
                return addRespWrapper;
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'PurchaserPlanDetail_LS_HUM','GetGroupInfoFromGBE');     
        } 
        return NULL;
    }
    
    /*
* Method Name   :    processResponseVendor
* Description   :    This method process Http Response from GBE Service 
* Return Type   :    Continuation
* Parameters    :    NA
*/
    @AuraEnabled
    public static object processResponseVendor(Object state)
    {   
        try 
        {	
            Map<String,String> mapState=(map<String, String>)state; 
            HttpResponse oResponse = Continuation.getResponse(mapState.get('sRequest'));  
            //reusing the method
            boolean isResponseValid = MemberPlanDetail_LS_HUM.checkResponseValidity(oResponse);
            addVendorWrapper = new AdditionalResponseWrapper_DTO_HUM();
            addVendorWrapper.isOnSwitch = true;
            
            if(isResponseValid)
            {	
                //String responseBody='{"GroupInfo":{"GroupId":"810314","Platform":"EM","BenefitPackageId":"XXXXXXXX","StartDate":"","EndDate":"","AsOfDate":"10/17/2020","BenfitSequenceNumber":"","GroupName":"5 NEW","EffectiveDate":"11/01/2019","EffectiveStatus":"Active","EnrolledSubscriberCountDental":"0","EnrolledSubscriberCountMedical":"0","NextRenewalDate":"11/01/2020","DentalOriginalEffectiveDate":"11/01/2019","VendorProgramOfferListSubGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"ACCOLADE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AEMB","VendorBenefitCodeDescription":"ACCOLADE EMBEDDED","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"12/31/9999"}]},"VendorProgramOfferListGroup":{"VendorProgramOffer":[{"VendorIdentifier":"ACEL","VendorIdentifierDescription":"ACCOLADCCLE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":"02/02/2020"},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""},{"VendorIdentifier":"ACCL","VendorIdentifierDescription":"ACCOLADE","VendorType":"CE","VendorTypeDescription":"CONSUMER EXPERIENCE","VendorBenefitCode":"V-AIMP","VendorBenefitCodeDescription":"Humana Impact with Accolade","ProgramOfferEffectiveDate":"01/01/2020","ProgramOfferEndDate":""}]},"AssociationList": {"Association": [{"AssociationType": "GPEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/09/2021"},{"AssociationType": "PEO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/07/2022"},{"AssociationType": "GPCO","AssociationName": "PREFERRED EMPLOYER ORG.","AssociationEffectiveDate": "01/01/2009","AssociationEndDate": "02/19/2021"}]},"DivisionRecordsTotalCount":"0","DivisionList":{"Division":[]}}}';
                respond = (GBEServiceResponse_H_HUM)System.JSON.deserialize(oResponse.getBody(),GBEServiceResponse_H_HUM.class);
                if(respond<>null && respond.GroupInfo<>null )
                {
                    if(respond.GroupInfo.Platform != NULL )
                    {
                        if(respond.GroupInfo.VendorProgramOfferListSubGroup<>null )
                        {
                            vendorInfoValue(respond);
                        }
                    }
                }
                return addVendorWrapper;
            }
            else
            {
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+oResponse.getBody()), 'MemberPlanDetail_LS_HUM', 'processResponseVendor');
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','processResponseVendor');    
        }
        return null;  
    }
    
        /*
* Method Name   :    getPurchaserPlanValues
* Description   :    This method returns Purchaser Plan record based on conditions
* Return Type   :    PurchaserPlan
* Parameters    :    String
*/
    public static PurchaserPlan getPurchaserPlanValues(String sRecId)
    {
        List<PurchaserPlan> lstPurchaserPlan = PurchaserPlanDetail_LD_HUM.getPurchaserPlan(sRecId);
        if(sRecId != NULL && lstPurchaserPlan.size()>0)
        {	
            //all the condition for purchaser Plan vendor section
            if(lstPurchaserPlan[0]!=null && lstPurchaserPlan[0].payer?.Group_Number__c != null && String.isNotBlank(lstPurchaserPlan[0].Product__r?.Name) &&  lstPurchaserPlan[0].Product__r?.Name == 'MED' && isCRMSGBOPermissionSetUser )
            {
                return lstPurchaserPlan[0];
            }
        }
        return null;
    }
    
    /*
* Method Name   :  setupRequest
* Description   :  This method is used to form http request.
* Return Type   :  HttpRequest 
* Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Plan Id)
*/
    public static HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy,PurchaserPlan policyPlan)
    {
        HttpRequest orequest = new HttpRequest();    

            sBodyVendor = buildRequest(policyPlan);
            if(String.isNotBlank(sBodyVendor))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Connection_HUM,GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Keep_alive_HUM);
                request.setBody(sBodyVendor);
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcHeaderPolicy.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderPolicy.Timeout__c));
                request.setEndpoint(oSvcHeaderPolicy.End_Point_URL__c);  
                orequest = request;
            }
            else
            {
                orequest = null;  
            }
        return orequest; 
    }
    
    
    /*
*  This method is used to Process the Json response from service to Wrapper List
*  @name   buildRequest
*  @param  PurchaserPlan
*  @return string - Returns the serialized body of the request
*/ 
    @testvisible private static string buildRequest(PurchaserPlan policyPlan)
    {    
        string sJsonbody = '';
        switchOnOffSubGroupId = HUMUtilityHelper.isCRMFunctionalityON('1789465');
        try
        {
            GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM(); 
            GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
            
            //Replaced existing condition to fetch SubGroupID from 8 digits to 9 digits. For SubGroupId having more than 8 chars was not getting fetched properly. Hence replaced subString() to fetch 9 digits and for 8 chars will trim the space
            if(switchOnOffSubGroupId)
            {
                if(!String.isBlank(policyPlan?.Source_Cust_Cov_Key__c))
                {
                    grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6): policyPlan.payer.Group_Number__c);          
                    grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,9).trim() : policyPlan.Platform__c ==   sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');
                }
            }
            else
            {
                grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyPlan.payer.Group_Number__c);
                grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,8) : policyPlan.Platform__c ==   sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');  
            }
            grpDetailDTO.BenefitPackageId = policyPlan.Platform__c == sEMPlatForm ? sDummyBenValue : ''; 
            grpDetailDTO.Platform = policyPlan.Platform__c;
            grpDetailDTO.StartDate =  (policyPlan.effectiveFrom != null ? MemberPlanDetail_LS_HUM.returnStringDate(policyPlan.effectiveFrom) : sNoneValue);                   
            grpDetailDTO.EndDate = policyPlan.EffectiveTo != null ? MemberPlanDetail_LS_HUM.returnStringDate(policyPlan.EffectiveTo) : sNoneValue;
            grpDetailDTO.BenefitSequenceNumber='';
            grpDetailDTO.DivisionStartPosition ='';
            grpDetailDTO.DivisionRecordsToReturn ='';
            grpDetailDTO.InclusionCriteria = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.GROUPSTRING;
            grpDetailDTO.VendorOfferProgramGroup = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.STRING_TRUE;
            grpDetailDTO.VendorOfferProgramSubGroup = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.STRING_TRUE;
            
            oRequest.GroupDetailDTO = grpDetailDTO;
            //sJsonbody='{"GroupDetailDTO":{"GroupId":"708174","SubGroupId":"1","BenefitPackageId":"CR6VTPG1","Platform":"EM","StartDate":"01/01/2014","EndDate":"01/01/2015","BenefitSequenceNumber":"","InclusionCriteria":"GROUP","DivisionStartPosition":"","DivisionRecordsToReturn":""}}';
            sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }

    /*
* Method Name   :  vendorInfoValue()
* Description   :  This is the used to populate Vendor Info 
* Return Type   :  void
* Parameters    :  HttpResponse Object
*/
    public static void vendorInfoValue(GBEServiceResponse_H_HUM resObj){
        if(resObj != NULL){
            if(resObj.GroupInfo.VendorProgramOfferListSubGroup != NULL) { 
                vendorProgramSubGroupDTO = resObj.GroupInfo.VendorProgramOfferListSubGroup;
            }
            if(vendorProgramSubGroupDTO != NULL && vendorProgramSubGroupDTO.VendorProgramOffer != NULL)
            {          
                for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorProgramSubGroupDTO.VendorProgramOffer)
                {
                    if((!String.ISBLANK(programoffer.VendorIdentifier)) && (programoffer.VendorIdentifier.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.ACCL)))
                    {	
                        
                        addVendorWrapper.gbeMap.put('vendorName', programoffer.VendorIdentifierDescription);
                        addVendorWrapper.gbeMap.put('vendorProgram', programoffer.VendorBenefitCodeDescription);
                        addVendorWrapper.gbeMap.put('programEffective', programoffer.ProgramOfferEffectiveDate);
                        addVendorWrapper.gbeMap.put('programEnd', programoffer.ProgramOfferEndDate);
                        break;
                    }
                }               
            } 
        }
    }
    
}