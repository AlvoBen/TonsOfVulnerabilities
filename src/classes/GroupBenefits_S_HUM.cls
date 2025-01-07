/**********************************************************************************************************************************************************
Name        	 : GroupBenefits_S_HUM.cls
Version          : 1.0
Created On       : 2018
Function         : Service class for Group Benefits

Modification Log:
	
* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Joel George										7/02/2018					 Original Version
* Anjali Kumar                                      4/08/2018                    REQ 363358 - Made changes for Maternity code in service category
************************************************************************************************************************************************************/
public with sharing class GroupBenefits_S_HUM
{
    private GroupBenefits_D_HUM oDataGrpBenefits = new GroupBenefits_D_HUM();
    private static Boolean isFaultCodePBERes;
     private static Boolean isFaultCodeGBERes;
    
    public static Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>> mapServiceCategory;
    public  static Map<string,string> mapServiceCategoryName;
	Public Static final String VOB_BENEFITSEARCH_PROCESSCALL_HUM = 'processBenefitSearch';
    Public Static final String  HUM_BENEFITS_DEN = 'DEN';
    Public Static final String VOB_REMAPPURL_HUM = 'REMAPPURL';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String VOB_PBE_FAULT_CODE = 'faultcode';
    Public Static final Integer HUMServiceTimeOut = 120;
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202; 
    
    
    /**
    * constructor method for BenefitsV2_C_HUM
    * <p>
    * Method to set the values for BenefitsV2_C_HUM.
    *
    @param grpgrpBenefitsCls              BenefitsV2_C_HUM class instance
    */
    public void init(GroupBenefits_X_HUM grpBenefitsCls)
    {
        try
        {
            grpBenefitsCls.oEventObj = new Event();
            assignValues(grpBenefitsCls);
        }
        catch (Exception ex)
        {
			 HUMExceptionHelper.logErrors(ex,'GroupBenefits_S_HUM', 'init');
        }
    }
    
    /**
    * assigning values to variables on load of page
    * <p>
    * Method to set the values for BenefitsV2_C_HUM.
    *
    @param grpBenefitsCls              BenefitsV2_C_HUM class instance
    */
    public void assignValues(GroupBenefits_X_HUM grpBenefitsCls)
    {
        if (ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            string sPolObjId = ApexPages.currentpage().getParameters().get('Id');

        }
       
        if (NULL != grpBenefitsCls.policyObject && grpBenefitsCls.policyObject.Product__r.Name == HUM_BENEFITS_DEN)
        {
           
            List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = 'CRMS_520_Benefits_Read_Only' limit 1];
            if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
            {
                grpBenefitsCls.bHasCRMS520Per = true;
                grpBenefitsCls.sSFDCBaseURL = System.URL.getSalesforceBaseUrl().toExternalForm();
                HUMToothHistoryAppURL__c objAppURL = HUMToothHistoryAppURL__c.getValues(VOB_REMAPPURL_HUM);
                 if(objAppURL!= null && null!= objAppURL.CCPAppURL__C)
                 {
                    grpBenefitsCls.sReimbursementAppURL = ''+objAppURL.CCPAppURL__C;
                    grpBenefitsCls.sOneClickURL = ''+objAppURL.oneClickURL__c;
                 }
            }
        }
       
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
    
     /**
    * Returns continuation object
    * <p>
    * Method to call continuation serive for the serive for MBE,PBE and GBE services .
    *
    * @param benefitsCls              BenefitsV2_C_HUM class instance
    * @return                                continuation
    */
    public static Continuation SendRequestPBEGBEService(GroupBenefits_RequestDTO_HUM requestStructure)
    {
           HUM_Webservice_Callout__c GBEServiceDetails = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
            HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(GBEServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            // Set callback method should be common for all three services (MBE,GBE,PBE)
            con.continuationMethod = 'ProcessDataPBEGBEResponse';
            Policy_Plan_Details__c planObj = GroupBenefits_D_HUM.getPolicyPlan_SOQL(requestStructure.policyObject, requestStructure.asOfdate);
            //Build GBE Request 
            HttpRequest httpGBEReqObj = builtHttpRequest(GBEServiceDetails);
            httpGBEReqObj.setBody(JSON.serialize(GroupBenefits_D_HUM.RequestStructureGBEService(requestStructure.policyObject,planObj, requestStructure.asOfdate)));
            // Build PBE Request 
            HttpRequest httpPBEReqObj = builtHttpRequest(PBEServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(GroupBenefits_D_HUM.RequestStructurePBEService(requestStructure.policyObject,planObj, requestStructure.asOfdate)));

            // Add callout requests to continuation for all three requests
            con.addHttpRequest(httpPBEReqObj);
            con.addHttpRequest(httpGBEReqObj);
            if(planObj != null && string.isNotBlank(planObj.Benefit_Coverage__c))con.state = planObj.Benefit_Coverage__c;
            return con;
            
        
       
    }
    
    /**
    * Returns null but sets the values of responses to UI
    * <p>
    * Callback method to process the serive for MBE,PBE and GBE services .
    *
    * @param benefitsCls          BenefitsV2_C_HUM class instance
    * @return                                   NULL
    */
    public static Object ProcessDataPBEGBEResponse(List<string> labels,Object State)
    {
        try
        {
            //escapeEcmaScript method is used to escape all hidden special characters
            HttpResponse responsePBE = Continuation.getResponse(labels[0]);
            
           
            HttpResponse responseGBE = Continuation.getResponse(labels[1]);

            GroupBenefits_ResponseDTO_HUM groupResp = new GroupBenefits_ResponseDTO_HUM();
            groupResp.vPBEResponse = parsePBE(responsePBE);
            groupResp.vGBEResponse = parseGBE(responseGBE);
            PBEServiceTypeV2DTO_HUM  s = new PBEServiceTypeV2DTO_HUM();
            s.mapServiceCatServicetype = mapServiceCategory;
            s.mapServiceCatCode = mapServiceCategoryName;
            groupResp.serviceDTOStr = s;
            if(state != null && string.isNotBlank((string)state))groupResp.covPolicyPlan = (string)state;
            else groupResp.covPolicyPlan ='';
            if(isFaultCodePBERes == true){
            	groupResp.bIsPBERes = true;
            }else {
            	groupResp.bIsPBERes = false;
            }
             if(isFaultCodeGBERes == true){
            	groupResp.bIsGBERes = true;
            }else {
            	groupResp.bIsGBERes = false;
            }
           
            return groupResp;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e,'GroupBenefits_S_HUM', 'ProcessDataHumanaOneResponse');
            return null;
        }
    }
    
         /**
    * void Method
    * <p>
    * Method to Parse PBE response
    *
    * @param  benefitsCls Class Instance .
    * @param  HttpResponse for PBE
    */
    public static PBEResponseV2_DTO_HUM parsePBE(HttpResponse responsePBE)
    {
        //PBE reponse started 
		String PBEresposeBody = responsePBE.getBody();
         isFaultCodePBERes = false;
       
        if (String.isNotBlank(PBEresposeBody) && PBEresposeBody.indexOf(VOB_PBE_FAULT_CODE) > 0)
        {
            isFaultCodePBERes = true;
        }
            ProductBusinessEntityResponseV2_H_HUM oResponsePBE = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(PBEresposeBody, ProductBusinessEntityResponseV2_H_HUM.class);
            PBEResponseV2_DTO_HUM DTOobj = GroupBenefits_D_HUM.getPBEDTOData(oResponsePBE);
            List<ProductBusinessEntityResponseV2_H_HUM.ServiceType> sameServiceCategoryTypeList;

            mapServiceCategoryName = new Map<String,String>();
            mapServiceCategory = new Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>>();
            if (DTOobj != null && DTOobj.ServiceCategoryList != null && DTOobj.ServiceCategoryList.ServiceCategory != null && DTOobj.ServiceCategoryList.ServiceCategory.size() > 0 && DTOobj.ServiceTypeList != null && DTOobj.ServiceTypeList.ServiceType != null)
            {
                
                String sCodeVal;
                for (ProductBusinessEntityResponseV2_H_HUM.ServiceCategory serviceCatObj : DTOobj.ServiceCategoryList.ServiceCategory)
                {
                    sameServiceCategoryTypeList = new List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>(); 
                    
                    if(serviceCatObj.CodeList.Code !=null){
                        sCodeVal ='';
                        for(String s : serviceCatObj.CodeList.Code){
                            if(sCodeVal != ''){
                                sCodeVal = sCodeVal + ','+s;
                            }else{
                                sCodeVal = s;
                            }
                        }
                    }
                    
                    for (ProductBusinessEntityResponseV2_H_HUM.ServiceType serviceTypeObj : DTOobj.ServiceTypeList.ServiceType)
                    {
                       
                        if (serviceCatObj.CodeList.Code.contains(serviceTypeObj.ServiceCategoryCode)) {
                            sameServiceCategoryTypeList.add(serviceTypeObj);
                        }
                    }
                    
                    mapServiceCategory.put(sCodeVal, sameServiceCategoryTypeList);
                    mapServiceCategoryName.put(serviceCatObj.Name,sCodeVal);
                }
            }
            

            return DTOobj;
   
        
    }
    
        /**
    * void Method
    * <p>
    * Method to Parse GBE response
    *
    * @param  benefitsCls Class Instance .
    * @param  HttpResponse for GBE
    */
    public static GBEResponse_DTO_HUM parseGBE(HttpResponse responseGBE)
    {
        // prepare GBE DTO data 
         isFaultCodeGBERes = false;
        String GBEresposeBody = responseGBE.getBody();
        if (String.isNotBlank(GBEresposeBody) && GBEresposeBody.indexOf(VOB_PBE_FAULT_CODE) > 0)
        {
            isFaultCodeGBERes = true;
        }
            GBEServiceResponse_H_HUM oResponseGBE = (GBEServiceResponse_H_HUM)System.JSON.deserialize(GBEresposeBody, GBEServiceResponse_H_HUM.class);
            GBEResponse_DTO_HUM objResponse = GroupBenefits_D_HUM.getGBEDTOData(oResponseGBE);
            
            return objResponse;
           
    }
    //============================== Group Search changes ==================================================
    /**
    * Returns responses of continuation  method
    * <p>
    * Method to get the Benefits Search Data
    *
    * @param String           Service Category Id
    * @param String           Type Of Service
    * @param String           Place of Service
    * @param String           Par /non Par
    * @param String           Platform Code
    * @param String           Product Key
    * @param String           Ref Date
    * @return                 Object
    */
      public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
    {
        try
        {
           
            HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
            HttpRequest httpPBEReqObj = builtHttpRequest(PBEServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(GroupBenefits_D_HUM.RequestStructureBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode))); 
            integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = VOB_BENEFITSEARCH_PROCESSCALL_HUM;
            con.addHttpRequest(httpPBEReqObj);
            con.state = sPar;

            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GroupBenefits_S_HUM', 'getBenefitSearch');
            return null;
        }
    }
    
     /**
    * Returns the response for Benefit Search
    * <p>
    * Method to Parse the Benefit Search response .
    *
    * @param benefitsCls          Object State
    * @return                     Object
    */
    public Static Object processBenefitSearch(List<string> labels, Object State)
    {

        try
        {
            HttpResponse responsePBE = Continuation.getResponse(labels[0]);
            String PARValue = (String)State;
            if (responsePBE != null && (responsePBE.getStatusCode() == HTTP_OK || responsePBE.getStatusCode() == HTTP_ACCEPTED))
            {
                
                ProductBusinessEntityResponseV2_H_HUM oResponse = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(responsePBE.getBody(), ProductBusinessEntityResponseV2_H_HUM.class);
                PBEBenefitsSearch_DTO_HUM benefitsSearchDTOobj = GroupBenefits_D_HUM.getBenefitsSearchDTO(oResponse, PARValue);
                return benefitsSearchDTOobj;
            }
            else
            {
                return null;
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'GroupBenefits_S_HUM', 'processBenefitSearch');
            return null;
        }
    }
    //==============================End Group Search changes ==================================================
}