/******************************************************************************************************************************
Apex Class Name  : CRMRetail_SDoH_Helper_HUM
Version          : 1.0 
Created Date     : 06/22/2021
Function         : Handler to get SDoH data using API call  
Test Class       : CRMRetail_SDoH_Helper_T_HUM

Modification Log :
*   Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Abhishek Maurya                               06/26/2021          User Story 2308421 -CRM Storefront SDoH: Action Required Prompt
*  Vinoth L                      				 06/26/2021          User Story 2308568 -CRM Storefront SDoH: Track DH&A Screening Results
*  Vinoth L                                      09/02/2021          User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
*  Abhishek Maurya                               09/02/2021          User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
*  Lakshmi Madduri                               06/14/2022          User Story - 3258682 T1PRJ0154546 / SF / MF9 Storefront - SNP: Indicators Field - Data Population
*  Vinoth L                       				 11/23/2023          User Story - 5318748 T1PRJ0154546 / SF / MF9 Storefront - DP Migration to Mulesoft (SDOH)  
*  Deepak khandelwal                             05/03/2024          User story - 5318748 T1PRJ0154546 / SF / MF9 Storefront - DP Migration to Mulesoft (SDOH)  
*********************************************************************************************************************************/
public with Sharing class  CRMRetail_SDoH_Helper_HUM {  
    Public static Map<String,String> enterpriseIdToSDohMetricAcronym=new Map<String,String>();
    public static List<CRMRetailSDoH_DTO_HUM> lstOfSDoHData = new List<CRMRetailSDoH_DTO_HUM>();
    private static Set<String> setOfAccountSDoHKey = new Set<String>();
    private static Map<String,Boolean> mapOfServiceUnavailabilityCheck = new Map<String,Boolean>();
    static Map<String,String> metricNameToAcronym=new Map<STring,String>();
    @testVisible private static Map<String,CRMRetailSDoH_DTO_HUM> mapToRemoveDuplicateDetermnt=new Map<String,CRMRetailSDoH_DTO_HUM>();
    @testVisible private static HUM_Webservice_Callout__c sDoHCstSetting =new HUM_Webservice_Callout__c ();    
    @testVisible private static List<CRMRetail_SDoHDetermt__c> listOfDetermntToInsert= new List<CRMRetail_SDoHDetermt__c>();
    static List<Error_Log__c> listOfErrorLog = new List<Error_Log__c>();
    @testVisible private static HUM_Webservice_Callout__c dhnaCreateCstSetting =new HUM_Webservice_Callout__c ();
    @testVisible private static HUM_Webservice_Callout__c dhnaUpdateCstSetting =new HUM_Webservice_Callout__c ();
    @testVisible private static HUM_Webservice_Callout__c dhnaDeleteCstSetting =new HUM_Webservice_Callout__c ();
    @testVisible private static HUM_Webservice_Callout__c getAllDataCstSetting =new HUM_Webservice_Callout__c (); 
    public static string sJSONResponse='';
    static{
        List<CRMRetail_SDoh_Mapping__mdt> lst = [Select DeveloperName,Id,MasterLabel,Metric_Name__c from CRMRetail_SDoh_Mapping__mdt];
        for(CRMRetail_SDoh_Mapping__mdt mdtRef:lst){
            metricNameToAcronym.put(mdtRef.MasterLabel.toUpperCase(),mdtRef.Metric_Name__c);
            metricNameToAcronym.put(mdtRef.developerName.toUpperCase(),mdtRef.Metric_Name__c);
        }
        if(HUMUtilityHelper.isCRMFunctionalityON('5318748'))
        {
        	sDoHCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDOH_CUSTOM_SETTING_MULESOFT);
            dhnaCreateCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE_MULESOFT);
            dhnaUpdateCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE_MULESOFT);        
            dhnaDeleteCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE_MULESOFT);
            getAllDataCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GETALLDATA_MULESOFT);            
        } 
        else{
        	sDoHCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOH_CUSTOM_SETTING);
            dhnaCreateCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);
            dhnaUpdateCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);        
            dhnaDeleteCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE);
            getAllDataCstSetting = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_GETALLDATA);     
        }        
    }
    /*
* Method Name   :    getSDoHData
* Description   :    method to get SDoH Data Using SDoh API call
* Return Type   :    Void  
*/  
    public static void getSDoHData(Map<String,String> mapOfEnterpriseIdtoAccount,Boolean isCreateSDoHData,Date currentInteractionDate){  
        Http http = new Http(); 
        string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';
        string JSON_EXCEPTION = HUMConstants__c.getInstance('JSON_EXCEPTION') != null ? HUMConstants__c.getInstance('JSON_EXCEPTION').StringValue__c : '';
        Boolean Switch_2654300 = HUMUtilityHelper.isCRMFunctionalityON('2654300');
        Map<String,Object> mapTest = new Map<String,Object>();
        Boolean srvcParamsVldtn = HUMUtilityHelper.isCRMFunctionalityON('5318748') ? (String.isNotBlank(sDoHCstSetting.Request_Type__c) && String.isNotBlank(sDoHCstSetting.Timeout__c)) : String.isNotBlank(sDoHCstSetting.Request_Type__c) && String.isNotBlank(sDoHCstSetting.Certificate_Name__c) && String.isNotBlank(sDoHCstSetting.Timeout__c);
        for(String enterpriseId:mapOfEnterpriseIdtoAccount.keySet()){
            if(sDoHCstSetting != NULL && srvcParamsVldtn){
                HttpRequest request = new HttpRequest();
                request.setMethod(sDoHCstSetting.Request_Type__c);                                  
                if(HUMUtilityHelper.isCRMFunctionalityON('5318748'))
                {                 	
                    request.setEndpoint(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDOH_NAMEDCREDS_MULESOFT+sDoHCstSetting.End_Point_URL__c);  
                }
                else
                {
                    if(!Test.isRunningTest())
                    {
                        request.setClientCertificateName(sDoHCstSetting.Certificate_Name__c);
                    }                                     
                 	request.setEndpoint(sDoHCstSetting.End_Point_URL__c); 
                    request.setHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
                }                
                request.setTimeout(Integer.valueOf(sDoHCstSetting.Timeout__c));
                CRMRetailSDoH_DTO_HUM.CRMRetail_SDoH_Request_DTO_HUM reqBody = new CRMRetailSDoH_DTO_HUM.CRMRetail_SDoH_Request_DTO_HUM();
                reqBody.EnterpriseId = enterpriseId;                
                request.setBody(JSON.serialize(reqBody)); 
                HttpResponse response = http.send(request); 
                if(response != null && ((response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE || response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INTEGER_HTTP_ACCEPTED_HUM) && (String.isNotBlank(response.getBody()) && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE)) )) {                    
                    if(response.getBody().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_KEY)){                                                                                                  
                        if(Switch_2654300){
                            mapTest = (Map<String,Object>)JSON.deserializeUntyped(response.getBody()); 
                            if(mapTest.containsKey(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_KEY)){                        
                                listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog(String.valueOf(mapTest.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_KEY)),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH_HELPER_METHODNAME, String.valueOf(mapTest.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_MESSAGE_KEY)), ERROR_INTEGRATION));
                            }   
                        }
                    }                    
                    else{
                        if(response.getBody().contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDRPERSONID)){
                            lstOfSDoHData.addALL((List<CRMRetailSDoH_DTO_HUM>)JSON.deserialize(response.getBody(), List<CRMRetailSDoH_DTO_HUM>.class));                        
                        }
                        else if(Switch_2654300){
                            listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog('',GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDoH_HELPER_METHODNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_JSON_EXCEPTION_KEY,JSON_EXCEPTION));
                        }
                    }
                }                 
                else{
                    mapOfServiceUnavailabilityCheck.put(enterpriseId,true);
                    listOfErrorLog.add(HUMCalloutUtilityHelper.logBulkFaultError(response,'CRMRetail_SDoH_Helper_HUM', 'CRMRetail_SDoH_Helper_HUM.getSDoHData', '', 'CRMRetail_SDoHDetermt__c', 'INFO'));                      
                }
            }
        }
        if(lstOfSDoHData.size()>0){
            processResponseData(lstOfSDoHData,mapOfEnterpriseIdtoAccount,isCreateSDoHData,currentInteractionDate);
        }
        if(mapOfServiceUnavailabilityCheck.size()>0){
            createSDoHDeterminantDataServiceUnavailable(mapOfServiceUnavailabilityCheck,isCreateSDoHData,mapOfEnterpriseIdtoAccount);
        }
        if(listOfDetermntToInsert.size()>0){
            insert listOfDetermntToInsert;
        }
        if(listOfErrorLog.size()>0){            
            insert listOfErrorLog;
        }
    }
    /*
    * Method Name   :    requestConsolidatedService
    * Description   :    form request for GetAllData Service
    * Return Type   :    void
    * Parameters    :    List<CRMRetail_ConsolidatedReq_DTO_HUM>, boolean, Date
    */
    public static void requestConsolidatedService(List<CRMRetail_ConsolidatedReq_DTO_HUM> lstRequestParams,boolean isCreateSDoHData, Date currentInteractionDate)
	{
		Datetime dtCurrentDate = Datetime.now();
		String sCurrentDate = dtCurrentDate.format(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_DATEFORMAT);
		for(CRMRetail_ConsolidatedReq_DTO_HUM objReq : lstRequestParams){
			CRMRetail_ServicesReq_DTO_HUM objRequest = new CRMRetail_ServicesReq_DTO_HUM();
			CRMRetail_ServicesReq_DTO_HUM.GetSDoHInfo objSDOHInfo = new CRMRetail_ServicesReq_DTO_HUM.GetSDoHInfo();
			CRMRetail_ServicesReq_DTO_HUM.GetEngagementIndexInfo objEngIndexInfo = new CRMRetail_ServicesReq_DTO_HUM.GetEngagementIndexInfo();                        
			if(objReq.enterpriseId != null) {
				objSDOHInfo.EnterpriseId = objEngIndexInfo.EnterpriseId = objReq.enterpriseId;
				objSDOHInfo.toDate='';
				objSDOHInfo.fromDate='';
				objRequest.GetSDoHInfo = objSDOHInfo;
				objRequest.GetEngagementIndexInfo = objEngIndexInfo;
			}
			if(objReq.makeSNPRequest)
			{		           
                CRMRetail_ServicesReq_DTO_HUM.GetMedicareMemberInfo objMedicareMemberInfo = new CRMRetail_ServicesReq_DTO_HUM.GetMedicareMemberInfo();
				CRMRetail_ServicesReq_DTO_HUM.GetMemberInfo objGetmemberInfo = new CRMRetail_ServicesReq_DTO_HUM.GetMemberInfo();
				List<CRMRetail_ServicesReq_DTO_HUM.MemberDetails> lstMemberDetails = new List<CRMRetail_ServicesReq_DTO_HUM.MemberDetails>();
				CRMRetail_ServicesReq_DTO_HUM.MemberDetails objMemberDetails = new CRMRetail_ServicesReq_DTO_HUM.MemberDetails();
				
				objMemberDetails.MedicareID='';
				objMemberDetails.MemberCardID=objReq.memberId;
				objMemberDetails.GroupID = objReq.groupId;
				objMemberDetails.BenefitSequenceNumber=objReq.benefitSequenceNumber;
				objMemberDetails.AsOfDate = sCurrentDate;
				objMemberDetails.LISInfo = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_N;
				objMemberDetails.LTSSInfo = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_N;
				objMemberDetails.ESRDInfo = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_N;
				objMemberDetails.SPAPInfo = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_N;
				objMemberDetails.SNPInfo=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_Y;
				objMemberDetails.POAInfo = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_N;
				lstMemberDetails.add(objMemberDetails);
				objGetmemberInfo.MemberDetails = lstMemberDetails;
				objMedicareMemberInfo.getMemberInfo = objGetmemberInfo;
				objRequest.GetMedicareMemberInfo = objMedicareMemberInfo;               
			}

			if(objRequest.GetMedicareMemberInfo != null || objRequest.GetSDoHInfo != null){
				String sBody = JSON.serialize(objRequest,true);
				Http http = new Http(); 
				HttpRequest request = new HttpRequest();
				request.setMethod(getAllDataCstSetting.Request_Type__c);
				
                if(HUMUtilityHelper.isCRMFunctionalityON('5318748'))
                {
                    request.setEndpoint(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GETALLDATA_NAMEDCREDS_MULESOFT+getAllDataCstSetting.End_Point_URL__c);   
                }
                else
                {
                    if(!Test.isRunningTest()){
                        request.setClientCertificateName(getAllDataCstSetting.Certificate_Name__c);
                    }
                 	request.setEndpoint(getAllDataCstSetting.End_Point_URL__c);   
                    request.setHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
                }
                request.setHeader(HUMConstants.HUM_PCP_SER_CONECTION, HUMConstants.HUM_PCP_SER_KEEPALIVE);				
				request.setTimeout(Integer.valueOf(getAllDataCstSetting.Timeout__c));                
				request.setBody(sBody); 
				HttpResponse response = http.send(request);
				parseServiceResponse(response,isCreateSDoHData,currentInteractionDate);
			}            
		}        
    }
    /*
    * Method Name   :    parseServiceResponse
    * Description   :    parse GetAllData service response
    * Return Type   :    void
    * Parameters    :    HttpResponse, boolean, Date
    */
    public static void parseServiceResponse(HttpResponse response,boolean isCreateSDoHData,Date currentInteractionDate)
    {    
        Map<String,String> mapOfResponseValues = new Map<String,String>();
        if(response != null && ((response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE || response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INTEGER_HTTP_ACCEPTED_HUM) && (String.isNotBlank(response.getBody()) && (response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOHSUCCESS) || !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE))) ))
        { 
            string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';
            Map<String,Object> mapOfResponse = (Map<String,Object>)Json.deserializeUntyped(response.getBody());                  
            if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOHSUCCESS)!=null){
                String sJSON = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOHSUCCESS));           
                lstOfSDoHData.addAll((List<CRMRetailSDoH_DTO_HUM>)JSON.deserialize(sJSON, List<CRMRetailSDoH_DTO_HUM>.class));
                if(lstOfSDoHData != null && !lstOfSDoHData.isEmpty()){
                    processResponseData(lstOfSDoHData,null,isCreateSDoHData,currentInteractionDate);
                }
            }
            else if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOHERROR) != null){
                String sSDOHError = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SDOHERROR));
                if(sSDOHError.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE)){
                    listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.logFaultError(sSDOHError,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR));
                }
                else{
                	CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError objSDOHError = (CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError)System.JSON.deserialize(sSDOHError, CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError.class);
                	listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog(String.valueOf(objSDOHError.status),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE,objSDOHError.message,ERROR_INTEGRATION));
                }                
            }            
            if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ENGAGEMENTSUCESS) != null){                
                String sEngagementSuccessResponse = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ENGAGEMENTSUCESS));
                CRMRetail_ConsolidatedResponse_DTO_HUM.EngagementIndexSuccess objEngagementIndexSuccess = (CRMRetail_ConsolidatedResponse_DTO_HUM.EngagementIndexSuccess)System.JSON.deserialize(sEngagementSuccessResponse, CRMRetail_ConsolidatedResponse_DTO_HUM.EngagementIndexSuccess.class);
                if(objEngagementIndexSuccess != null && objEngagementIndexSuccess.supplementalFields != null && objEngagementIndexSuccess.supplementalFields.ind_new_mbr != null && objEngagementIndexSuccess.supplementalFields.ind_new_mbr==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_0){
                    mapOfResponseValues.put(System.Label.CRMRetail_Engagement_Prediction_Key,objEngagementIndexSuccess.finalPrediction);
                }                
            }
            else if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ENGAGEMENTERROR)!=null) {
                String sEngagementError = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ENGAGEMENTERROR));
                if(sEngagementError.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE)){
                      listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.logFaultError(sEngagementError,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR));
                }
                else{
                    CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError objEngagementError = (CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError)System.JSON.deserialize(sEngagementError, CRMRetail_ConsolidatedResponse_DTO_HUM.SDoHError.class);
                	listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog(String.valueOf(objEngagementError.status),GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE,objEngagementError.message,ERROR_INTEGRATION));
                }                
            }
            if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_MEDICAREMEMBERINFOSUCCESS) != null){
                String sSNPResponse = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_MEDICAREMEMBERINFOSUCCESS));
                CRMRetail_ConsolidatedResponse_DTO_HUM.MedicareMemberInfoSuccess objResponse = (CRMRetail_ConsolidatedResponse_DTO_HUM.MedicareMemberInfoSuccess)System.JSON.deserialize(sSNPResponse, CRMRetail_ConsolidatedResponse_DTO_HUM.MedicareMemberInfoSuccess.class);
                if(objResponse.CIMedicareResponse != null){
                CRMRetail_ConsolidatedResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse objMemberResponse = objResponse.CIMedicareResponse.GetMedicareMemberInfo[0].MedicareMemberLISSNPInfoResponse;
                
                CRMRetail_ConsolidatedResponse_DTO_HUM.Error objError = objMemberResponse.Errors.Error;
                if(objError != null && objError.Code != null && objError.Description !=null)
                {
                    listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.createErrorLog(objError.Code,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE,objError.Description,ERROR_INTEGRATION));
                }
                else 
                {
                    if(String.isNotBlank(objMemberResponse.SNPTypeCode) && (objMemberResponse.SNPTypeCode == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SNP_DE || objMemberResponse.SNPTypeCode == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SNP_CC)){
                        mapOfResponseValues.put(System.Label.CRMRetail_SNP_Text,objMemberResponse.SNPTypeCode);
                    }
                } 
                }
            }	
            else if(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_MEDICAREMEMEBERINFOERROR) != null){
                String sResponse = JSON.serialize(mapOfResponse.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_MEDICAREMEMEBERINFOERROR));
                listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.logFaultError(sResponse,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR));
            }
        }
        else{
            String sResponse = response.getBody();
            listOfErrorLog.add(GenericHelper_CRMRetail_H_HUM.logFaultError(sResponse,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_PARSESERVICERESPONSE, '', '', GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR));
        } 
        if(mapOfResponseValues != null && !mapOfResponseValues.isEmpty()){
            sJSONResponse = JSON.serialize(mapOfResponseValues);
        }        
        if(!listOfErrorLog.isEmpty() && listOfErrorLog.size()>0){
            insert listOfErrorLog;
        }
    }  
    /*
* Method Name   :    populateSDoHForHealthEducatorInteraction
* Description   :    method to get SDoH Data Using SDoh API call for Health Educator Interaction
* Return Type   :    Void  
*/
    @future(callout=true)
    public static void populateSDoHForHealthEducatorInteraction(Map<String,String> mapOfEnterpriseIdtoAccount,Boolean isCreateSDoHData,Date currentInteractionDate){ 
        getSDoHData(mapOfEnterpriseIdtoAccount,isCreateSDoHData,currentInteractionDate);
    }
    
    
    private static void processResponseData(List<CRMRetailSDoH_DTO_HUM> lstOfSDoHData,Map<String,String> mapOfEnterpriseIdtoAccount,Boolean isCreateSDoHData,Date currentInteractionDate){  
        if(isCreateSDoHData){                         
            for(CRMRetail_SDoHDetermt__c sfDetermt : [Select Id,Name,CreatedDate,Determinant__c,Account__C from CRMRetail_SDoHDetermt__c where DAY_ONLY(CreatedDate) =: currentInteractionDate AND Account__c IN : mapOfEnterpriseIdtoAccount.values()]){
                setOfAccountSDoHKey.add(sfDetermt.Determinant__c+'@'+sfDetermt.Account__c);
            }   
        }
        for(CRMRetailSDoH_DTO_HUM wrapRef:lstOfSDoHData){
            String uniqueKey=wrapRef.sdrPersonId+'@'+wrapRef.metricName;
            if(!mapToRemoveDuplicateDetermnt.containsKey(uniqueKey)){
                mapToRemoveDuplicateDetermnt.put(uniqueKey,wrapRef);
            }else{
                if(mapToRemoveDuplicateDetermnt.get(uniqueKey).scoreDate != NULL && wrapRef.scoreDate != NULL){
                    if(Date.valueOf(mapToRemoveDuplicateDetermnt.get(uniqueKey).scoreDate) <  Date.valueOf(wrapRef.scoreDate)){
                        mapToRemoveDuplicateDetermnt.put(uniqueKey,wrapRef);
                    }
                }
            }
        } 
        if(mapToRemoveDuplicateDetermnt.size()>0){
            for(CRMRetailSDoH_DTO_HUM wrapRef:mapToRemoveDuplicateDetermnt.values()){ 
                if(wrapRef.metricDescription == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LIKELY || wrapRef.metricDescription == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                    sDoHNotificationValidity(wrapRef);
                    if(isCreateSDoHData){
                        createSDoHDeterminantData(wrapRef,mapOfEnterpriseIdtoAccount);    
                    }                
                }
            }
        }
    }
    /*
* Method Name   :    sDoHNotificationValidity
* Description   :    method to process SDoH Data which returns from SDoh API call
* Return Type   :    Void  
*/
    private static void sDoHNotificationValidity(CRMRetailSDoH_DTO_HUM wrapRef){ 
        if(!enterpriseIdToSDohMetricAcronym.containsKey(wrapRef.sdrPersonId)){
            if(metricNameToAcronym.containsKey(wrapRef.metricName.toUpperCase())){
                enterpriseIdToSDohMetricAcronym.put(wrapRef.sdrPersonId,metricNameToAcronym.get(wrapRef.metricName.toUpperCase()));  
            }
        }else{  
            if(metricNameToAcronym.containsKey(wrapRef.metricName.toUpperCase())){
                String tempStr= enterpriseIdToSDohMetricAcronym.get(wrapRef.sdrPersonId);
                tempStr=tempStr+'/'+metricNameToAcronym.get(wrapRef.metricName.toUpperCase());
                enterpriseIdToSDohMetricAcronym.put(wrapRef.sdrPersonId,tempStr);
            }
        }    
    }
    /*
* Method Name   :    createSDoHDeterminantData
* Description   :    method to insert SDoH Data into CRMRetail_SDoHDetermt__c which returns from SDoh API call
* Return Type   :    Void  
*/
    private static void createSDoHDeterminantData(CRMRetailSDoH_DTO_HUM wrapRef,Map<String,String> mapOfEnterpriseIdToAccId){     
        CRMRetail_SDoHDetermt__c SDoHDetrmt = new CRMRetail_SDoHDetermt__c();
        if(!setOfAccountSDoHKey.contains(wrapRef.metricName+'@'+mapOfEnterpriseIdToAccId.get(wrapRef.sdrPersonId))){                                       
            SDoHDetrmt.Name = wrapRef.metricName;
            SDoHDetrmt.Account__c = mapOfEnterpriseIdToAccId.get(wrapRef.sdrPersonId);
            SDoHDetrmt.Description__c = wrapRef.metricDescription;
            SDoHDetrmt.Rating__c = wrapRef.metricRating;
            SDoHDetrmt.Source__c = wrapRef.metricSource;
            SDoHDetrmt.Score_Date__c =  Date.valueOf(wrapRef.scoreDate);
            SDoHDetrmt.Determinant__c =wrapRef.metricName;
            listOfDetermntToInsert.add(SDoHDetrmt);            
        }                                        
    }
    /*
* Method Name   :    createSDoHDeterminantDataServiceUnavailable
* Description   :    method to insert SDoH Data into CRMRetail_SDoHDetermt__c when service is not available
* Return Type   :    Void  
*/
    private static void createSDoHDeterminantDataServiceUnavailable(Map<String,Boolean> mapOfServiceUnavailabilityCheck,Boolean isCreateSDoHData,Map<String,String> mapOfEnterpriseIdToAccId){ 
        if(isCreateSDoHData){ 
            for(String enterpriseId:mapOfEnterpriseIdToAccId.keyset()){
                CRMRetail_SDoHDetermt__c SDoHDetrmt = new CRMRetail_SDoHDetermt__c();
                SDoHDetrmt.Name = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SERVICE_NOT_AVAILABLE;
                SDoHDetrmt.Account__c = mapOfEnterpriseIdToAccId.get(enterpriseId);
                SDoHDetrmt.Description__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UNKNOWN;                
                SDoHDetrmt.Source__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UNKNOWN;                                
                SDoHDetrmt.Determinant__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SERVICE_NOT_AVAILABLE; 
                listOfDetermntToInsert.add(SDoHDetrmt);
            }
        }
    }
    /*
* Method Name   :    callSDoHServiceAPIOnShare
* Description   :    method to get SDoH Data Using SDoh API call
* Return Type   :    Void  
*/  
    public static Boolean callSDoHServiceAPIOnShare(List<CRMRetailSDoH_DTO_HUM> listOfSDohToProcessOnDHNA,STRING serviceType){ 
        List<CRMRetailSDoH_DTO_HUM> tempAddApiBuffer = new List<CRMRetailSDoH_DTO_HUM>();
        List<CRMRetailSDoH_DTO_HUM> tempUpdateApiBuffer = new List<CRMRetailSDoH_DTO_HUM>();
        List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO> lstOfDHNAResponse = new List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO>();        
        Boolean apiResponse=false; 
        HttpResponse response = new HTTPResponse();
        if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE){
            response = setHTTPRequest(listOfSDohToProcessOnDHNA,dhnaCreateCstSetting);  
        }
        if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE){
            response = setHTTPRequest(listOfSDohToProcessOnDHNA,dhnaDeleteCstSetting); 
        } 
        if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE){
            response = setHTTPRequest(listOfSDohToProcessOnDHNA,dhnaUpdateCstSetting);
        }
        if(response != null && ((response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE || response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INTEGER_HTTP_ACCEPTED_HUM || response.getStatusCode() == GLOBAL_CONSTANT_STOREFRONT_HUM.INTEGER_HTTP_STATUS_OK) && (response.getBody() != NULL && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VOB_FAULT_CODE)) && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_API_NOT_FOUND_STATUS))){              
            if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE){	
                return apiResponse = true;
            }
            else if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE){
                lstOfDHNAResponse.addALL((List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO>)JSON.deserialize(response.getBody(), List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO>.class ));
                for(CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO sDoHDTOref : lstOfDHNAResponse){
                    if(sDoHDTOref.status == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_API_CONFLICT_RESPONSE){                            
                        tempUpdateApiBuffer.add(sDoHDTOref.payload);                            
                    }                                         
                    else{
                        continue;
                    }
                }
                if(tempUpdateApiBuffer.isEmpty()){
                    return apiResponse = true;
                }                                        
                else{
                    apiResponse = callSDoHServiceAPIOnShare(tempUpdateApiBuffer,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);                                               
                }    
            }
            else if(serviceType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE){
                lstOfDHNAResponse.addAll((List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO>)Json.deserialize(response.getBody(), List<CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO>.class ));
                for(CRMRetailSDoH_DTO_HUM.CRMRetail_DHNA_Response_DTO sDoHDTOref : lstOfDHNAResponse){ 
                    if(sDoHDTOref.status == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_API_NOT_FOUND_RESPONSE){ 
                        tempAddApiBuffer.add(sDoHDTOref.payload);
                    }                                        
                    else{
                        continue;
                    }
                }
                if(tempAddApiBuffer.isEmpty()){
                    return apiResponse = true;
                }                    
                else{
                    apiResponse = callSDoHServiceAPIOnShare(tempAddApiBuffer,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);                                               
                }     
            }
        } 
        else{
            apiResponse = false;
            listOfErrorLog.add(HUMCalloutUtilityHelper.logBulkFaultError(response,'CRMRetail_SDoH_Helper_HUM', 'CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare', '', 'CRMRetail_SDoHDetermt__c', 'INFO'));  
            if(listOfErrorLog.size()>0){
                insert listOfErrorLog;
            }
        }
        return apiResponse;
    }          
    /*
* Method Name   :    setHTTPRequest
* Description   :    method to set the HTTP Request 
* Return Type   :    HTTPResponse  
*/
    private static HttpResponse setHTTPRequest(List<CRMRetailSDoH_DTO_HUM> listOfSDohToScreen,HUM_Webservice_Callout__c dhnaCstSetting){
        Http http = new Http();
        HttpResponse response = new HttpResponse();
        if(dhnaCstSetting != NULL && String.isNotBlank(dhnaCstSetting.Request_Type__c) && String.isNotBlank(dhnaCstSetting.Certificate_Name__c) && String.isNotBlank(dhnaCstSetting.Timeout__c)){
            HttpRequest request = new HttpRequest();
            request.setMethod(dhnaCstSetting.Request_Type__c); 
            if(HUMUtilityHelper.isCRMFunctionalityON('5318748'))
            {
                String endpointpath;
                
                if(dhnaCstSetting.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE)
                {
                    endpointpath = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_ADDMEMBER_KEY; 
                }
                else if(dhnaCstSetting.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE)
                {
                    endpointpath = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UPDATEMEMBER_KEY;
                }
                else if(dhnaCstSetting.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE)
                {                    
                    endpointpath = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_DELETEMEMBER_KEY;
                }                   
                request.setEndpoint(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_SDOH_NAMEDCREDS_MULESOFT+endpointpath);
            }
            else
            {
                if(!Test.isRunningTest())
                {
                    request.setClientCertificateName(dhnaCstSetting.Certificate_Name__c);
                }
                request.setHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
                request.setEndpoint(dhnaCstSetting.End_Point_URL__c);  
            }
            request.setTimeout(Integer.valueOf(dhnaCstSetting.Timeout__c));
            request.setBody(JSON.serialize(listOfSDohToScreen)); 
            response = http.send(request);
        }
        return response;
    }
}