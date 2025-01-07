/**************************************************************************** 
Apex Class Name  : Benefits_LS_HUM
Version          : 1.0  
Created Date     : 3/17/2022
Function         : Service class benefit tab component 
Test class       : Benefits_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Aishwarya Pawar                               3/17/2022                  Original Version - US 3017464
* Swapnali Sonawane 							3/17/2022					US- 3017787
* Kalyani Pachpol								3/17/2022					US- 3149503
* Aishwarya Pawar				            	03/17/2023		            US-4286514 - Adding switch for Benefit Service Error
* Suraj Patil                             		05/08/2023                 	US#4542585 Dental Plan - Tooth History
* Suraj Patil                             		06/22/2023                 	DefectFix-7769
* Anusha C                                      07/19/2023                  US: 4785118 Critical- Lightning - Benefits
***************************************************************************************************/
	public with sharing class Benefits_LS_HUM {
    public Static final String PBEFaultCode= '111111';
    public Static final String PBEFaultCode1= '000305';
	Public Static final String VOB_PBE_FAULT_CODE = 'faultcode';
	Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final String HUM_PBE_Response = 'parse_PBEResponse';
    //MBE
    Public Static final String HUM_MBE_Response = 'parseMBEResponse';
	public Static final String MBEFaultCode= '83.1.1';
    public static String  StateMemberPlanId;
    public static final string KMDOCUMENT = 'KMDocument';
    //Benefit Search
    Public Static final Integer HUMServiceTimeOut = 120;
    Public Static final String VOB_BENEFITSEARCH_PROCESSCALL_HUM = 'processBenefitSearch';
    Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202; 
	
    //Accum Service
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String SOAPAction_HUM = 'SOAPAction';

	//Billing Service
	Public Static final String DEFAULT_MIN_DATE='01/01/0001';

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
		try
		{
			if (ServiceObj != null && ServiceObj.Active__c)
			{
				request.setMethod(ServiceObj.Request_Type__c);
				if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
				if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
				request.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
				request.setEndpoint(ServiceObj.End_Point_URL__c);
				request.setHeader(HUM_PCP_SER_SOAPACTION, '');
				if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
			}
			return request;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'builtHttpRequest');
			return Null;
		}
	}

    /**
	* Returns continuation object
	* <p>
	* Method to call continuation service for PBE service.
	* @return                                continuation
	*/
	public static object SendRequestPBEService(String memberPlanId, String asOfDate)
	{
		try
		{
			CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('BenefitErrorSwitch');
        	boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
      		if(switchValue==false){
				HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
				// Create continuation with a timeout
				integer iTIMEOUT_INT_SECS = Integer.valueof(PBEServiceDetails.Timeout__c) / 1000;       
				Continuation con = new Continuation(iTIMEOUT_INT_SECS);
				con.continuationMethod=HUM_PBE_Response;
				// Build PBE Request 
				HttpRequest httpPBEReqObj = builtHttpRequest(PBEServiceDetails);
				httpPBEReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestStructurePBEService(memberPlanId, asOfDate)));
				con.addHttpRequest(httpPBEReqObj);    
				return con;
			}else{
                throw new HUMCustomException('Service Error');
            }
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'SendRequestPBEService');
			return e;
		}
	}
       
    /**
	* Returns PBE responses to UI
	* <p>
	* Callback method for PBE service.
	*/
	public static Object processPBEResponse(List<string> labels, Object State)
	{
		try
		{		  
		   	HttpResponse responsePBE = Continuation.getResponse(labels[0]);
        	String result = Benefits_LS_HUM.parsePBE(responsePBE);
        	return result;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'processMBEResponse');
			return null;
		}
	}
    
    /**
	* Method to Parse PBE response
	* @param  HttpResponse for PBE 
	*/
	public static String parsePBE(HttpResponse responsePBE)
	{
		String PBEresposeBody = responsePBE.getBody();
		if (String.isNotBlank(PBEresposeBody) && PBEresposeBody.indexOf(VOB_PBE_FAULT_CODE) > 0)
		{
			String PBEFaultString=null;
            PBEresposeBody = PBEresposeBody.replace('-', '_');
            FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
            faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(PBEresposeBody , FaultResponse_DTO_HUM.class);
            if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
            {      
				PBEFaultString = (faultResponse.Fault.detail.errorInfo.error_message != null && faultResponse.Fault.detail.errorInfo.error_message.contains(PBEFaultCode))?PBEFaultCode:
				                 (faultResponse.Fault.detail.errorInfo.error_message != null && faultResponse.Fault.detail.errorInfo.error_message.contains(PBEFaultCode1) && String.isBlank(PBEFaultCode))? PBEFaultCode1:null;                                             
             }
             return PBEFaultString;           
		}
		else
		{
			ProductBusinessEntityResponseV2_H_HUM oResponsePBE = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(PBEresposeBody, ProductBusinessEntityResponseV2_H_HUM.class);
			PBEResponseV2_DTO_HUM DTOobj = Benefits_LD_HUM.getPBEDTOData(oResponsePBE);
			String vPBEServiceRes = JSON.serialize(DTOobj);
            return vPBEServiceRes;
		}
	}
	/**
	* Returns continuation object
	* Method to call continuation service for GBE service.
	* @return                                continuation
	*/
	public static Continuation SendRequestGBEService(String memberPlanId, String asOfDate)
	{
		try
		{
			HUM_Webservice_Callout__c GBEServiceDetails = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
			// Create continuation with a timeout
			integer iTIMEOUT_INT_SECS = Integer.valueof(GBEServiceDetails.Timeout__c) / 1000;       
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod='parse_GBEResponse';
			// Build GBE Request 
			HttpRequest httpPBEReqObj = builtHttpRequest(GBEServiceDetails);
			httpPBEReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestStructureGBEService(memberPlanId, asOfDate)));
			con.addHttpRequest(httpPBEReqObj);    
			return con;	
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'SendRequestPBEService');
			return Null;
		}
	}
       
	/**
	* Method to Parse GBE response
	* @param  HttpResponse for GBE 
	*/
	public static String parseGBE(HttpResponse responseGBE)
	{
		//GBE reponse started 
		String GBEDTOResponseFlag;
		String GBEresposeBody = responseGBE.getBody();
		if (String.isNotBlank(GBEresposeBody) && GBEresposeBody.indexOf(VOB_PBE_FAULT_CODE) > 0)
		{
			GBEDTOResponseFlag = 'false';
            return GBEDTOResponseFlag;
		}
		else
		{
			GBEDTOResponseFlag = 'true';
			GBEServiceResponse_H_HUM oResponseGBE = (GBEServiceResponse_H_HUM)System.JSON.deserialize(GBEresposeBody, GBEServiceResponse_H_HUM.class);
			GBEResponse_DTO_HUM objResponse = Benefits_LD_HUM.getGBEDTOData(oResponseGBE);
			String vGBEServiceRes = JSON.serialize(objResponse);
			return vGBEServiceRes;
		}
	}
    
    
   /**
	* Returns continuation object
	* <p>
	* Method to call continuation service for MBE service .
	*/
	public static Object SendRequestMBEService(String memberPlanId, String asOfDate)
	{
		try
		{
			CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('BenefitErrorSwitch');
        	boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
      		if(switchValue==false){
				HUM_Webservice_Callout__c MBEServiceDetails = HUM_Webservice_Callout__c.getValues('MBEPlusService');
				// Create continuation with a timeout
				integer iTIMEOUT_INT_SECS = Integer.valueof(MBEServiceDetails.Timeout__c) / 1000;
				Continuation con = new Continuation(iTIMEOUT_INT_SECS);
				// Set callback method
				con.continuationMethod = HUM_MBE_Response;
				con.state=memberPlanId;
				// Build PBE Request 
				HttpRequest httpMBEReqObj = builtHttpRequest(MBEServiceDetails);
				httpMBEReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestStructureMBEService(memberPlanId, asOfDate)));
				// Add callout requests to continuation for MBE requests
				con.addHttpRequest(httpMBEReqObj);    
				return con;
			}else{
                throw new HUMCustomException('Service Error');
            }
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'SendRequestMBEService');
			return e;
		}
	}
    
    /**
	* Returns null but sets the values of responses to UI
	* <p>
	* Callback method to process the serive MBE service.
	*/
	public static Object processMBEResponse(List<string> labels, Object State)
	{
		try
		{
			HttpResponse responseMBE = Continuation.getResponse(labels[0]);
            String memberPlanId= (String)State;
        	String result = parseMBE(responseMBE,memberPlanId);
        	return result;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'processMBEResponse');
			return null;
		}
	}
   
		
    public static String parseMBE(HttpResponse responseMBE , String memberPlanId)
	{
		//MBE reponse started 
		String MBEresposeBody = responseMBE.getBody();
        if (String.isNotBlank(MBEresposeBody) && MBEresposeBody.indexOf(VOB_PBE_FAULT_CODE) > 0)
		{
            	String MBEFaultString=null;
                MBEresposeBody = MBEresposeBody .replace('-', '_');
                FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
                faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(MBEresposeBody , FaultResponse_DTO_HUM.class); 
				MBEFaultString = faultResponse.Fault != null  && faultResponse.Fault.faultcode != null 
				                 && faultResponse.Fault.faultcode.contains(MBEFaultCode)?faultResponse.Fault.faultcode.stripHtmlTags():null;               
            return MBEFaultString;           
		}
		else
		{
			MBEPlusResponse_DTO_HUM  oResponseMBE = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(MBEresposeBody, MBEPlusResponse_DTO_HUM.class);
            MBEResponseV1_DTO_HUM objResponse = Benefits_LD_HUM.getMBEDTOData(oResponseMBE, memberPlanId);
			String vMBEServiceRes = JSON.serialize(objResponse);
			return vMBEServiceRes;
		}
	}

	/**
	* Returns the Continuation object
	* <p>
	* Method to get Paid thru date from Billing Profile serice .
	*
	* @param String          sNetworkID
	* @param String          sSearchID
	* @param String          sPlatform
	* @return                Object
	*/
	public static Continuation processBillingProfiles(String sNetworkID, String sSearchID, String sPlatform, String sExchangeType, string sProductType, string sProduct)
	{
		HUM_Webservice_Callout__c oBillingServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2_GetBillingAccountInfo');
		// Create continuation with a timeout
		integer iTIMEOUT_INT_SECS = Integer.valueof(oBillingServiceDetails.Timeout__c) / 1000;
		Continuation con = new Continuation(iTIMEOUT_INT_SECS);
		con.continuationMethod = 'parseBillingProfileResponse';
		HttpRequest httpBillingReqObj = builtHttpRequest(oBillingServiceDetails);
		BillingProfileRequestV2_DTO_HUM billingRequest = new BillingProfileRequestV2_DTO_HUM();
		billingRequest = Benefits_LD_HUM.RequestStructureBillingService(sNetworkID, sPlatform, sSearchID, sExchangeType,sProductType,sProduct);
		httpBillingReqObj.setBody(JSON.serialize(billingRequest));
		con.addHttpRequest(httpBillingReqObj);
		return con;
	}

	/**
	* Returns null but sets the values of responses to UI
	* <p>
	* Callback method to process the Billing response
	*/
	public static Object processBillingResponse(List<string> labels, Object State)
	{
		try
		{
			HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);
			String sPaidThoughDate;
			if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oHttpResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
			{
				BillingProfileResponseV2_DTO_HUM billingResponse = (BillingProfileResponseV2_DTO_HUM)System.JSON.deserialize(oHttpResponse.getBody(), BillingProfileResponseV2_DTO_HUM.class);
				sPaidThoughDate = Benefits_LD_HUM.ParseStructureBillingService(billingResponse);
			}
			if(sPaidThoughDate == DEFAULT_MIN_DATE)
            {
                return '';
            }
            else
            {
                return JSON.serialize(sPaidThoughDate);
                
            }
			
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LS_HUM', 'processBillingResponse');
			return null;
		}
	}
    
    
    
    
	public static Continuation invokeMentorGridService(String memberPlanId){
		try
      	{
     		MentorServiceRequest_DTO_HUM KMDRequestDTO = new MentorServiceRequest_DTO_HUM();
      		HUM_Webservice_Callout__c KMDServiceDetails = HUM_Webservice_Callout__c.getValues(KMDOCUMENT);
        	integer iTIMEOUT_INT_SECS = Integer.valueof(KMDServiceDetails.Timeout__c) / 1000;
        	Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        	con.continuationMethod = 'parseKMDResponse';
        	HttpRequest httpKMDReqObj = builtHttpRequest(KMDServiceDetails);
        	httpKMDReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestStructureKDMService(KMDRequestDTO,memberPlanId)));
            con.addHttpRequest(httpKMDReqObj);
        	return con;
      }
      catch (exception ex)
      {
         HUMExceptionHelper.logErrors(ex,'Benefits_LS_HUM','invokeMentorGridService');
         return null;
      }
	}
	
	public static Object processKMDResponse(List<string> labels, Object State){
	try
        {
 			MentorServiceResponse_DTO_HUM responseKMD;
            HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);        
            if (oHttpResponse != null && (oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest())
            {
                responseKMD =(MentorServiceResponse_DTO_HUM)System.JSON.deserialize(oHttpResponse.getBody(), MentorServiceResponse_DTO_HUM.class);
                return JSON.serialize(Benefits_LD_HUM.ParseStructureKMDocument(responseKMD));
            }
			else{
					return false;
			}
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Benefits_LS_HUM', 'processKMDResponse');
            return null;
        }
	}
    
    
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
			httpPBEReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestStructureBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode)));
			integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod = VOB_BENEFITSEARCH_PROCESSCALL_HUM;
			con.addHttpRequest(httpPBEReqObj);
			con.state = sPar;
			return con;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'Benefits_LS_HUM', 'getBenefitSearch');
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
				PBEBenefitsSearch_DTO_HUM benefitsSearchDTOobj = Benefits_LD_HUM.getBenefitsSearchDTO(oResponse, PARValue);
				return benefitsSearchDTOobj;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'Benefits_LS_HUM', 'processBenefitSearch');
			return null;
		}
	}

    /**
    * Returns an object of HttpRequest
    * <p>
    * Method to get the request Details from custom setting for Benefits History and current section
    *
    * @pram  HttpRequest object
    * @param  HUM_Webservice_Callout__c object
    * @param  request body in string format 
    * @return  HttpRequest object
    * @see
    */
    public static HttpRequest builtAccumsRequest(HttpRequest request,HUM_Webservice_Callout__c accumServiceObj,string sRequestBody)
    {
        if(!String.isBlank(sRequestBody) && accumServiceObj != null && accumServiceObj.Active__c)
        {
            request.setMethod(accumServiceObj.Request_Type__c);
            request.setBody(sRequestBody);
            if(!Test.isRunningTest() && String.isNotBlank(accumServiceObj.Certificate_Name__c))
            {
                request.setClientCertificateName(accumServiceObj.Certificate_Name__c);
            }
            if(String.isNotBlank(accumServiceObj.Content_Type__c))
            {
                request.setHeader(Content_type_HUM, accumServiceObj.Content_Type__c);
            }
            request.setHeader(Connection_HUM,Keep_alive_HUM);
            request.setEndpoint(accumServiceObj.End_Point_URL__c);
            request.setHeader(SOAPAction_HUM,'');
            if(accumServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(accumServiceObj.Timeout__c));
        }
        return request;
    }

	/**
	* Method to get tooth history data from tooth history service
	* @param String          sMemberId
	* @param String          sMemberDependentCode
	* @return                con Object
	*/
	@auraEnabled(cacheable = true continuation=true)
	public Static Continuation callToothHistoryService(String sMemberId, String sMemberDependentCode)
	{
		HUM_Webservice_Callout__c oToothHistoryServiceDetails = HUM_Webservice_Callout__c.getValues('ToothHistory_GetToothHistory');
		// Create continuation with a timeout
		integer iTIMEOUT_INT_SECS = Integer.valueof(oToothHistoryServiceDetails.Timeout__c) / 1000;
		Continuation con = new Continuation(iTIMEOUT_INT_SECS);
		con.continuationMethod = 'parseToothHistoryResponse';
		HttpRequest httpToothHistoryReqObj = builtHttpRequest(oToothHistoryServiceDetails);
		httpToothHistoryReqObj.setBody(JSON.serialize(Benefits_LD_HUM.RequestToothHistoryService(sMemberId, sMemberDependentCode)));
		con.addHttpRequest(httpToothHistoryReqObj);
		return con;
	}

	/**
	* Returns the response of Tooth History Profile
	* Method to Parse the Tooth History response
	* @param     Object State
	* @return    Object
	*/
	@auraEnabled(cacheable = true continuation=true)
	public Static Object parseToothHistoryResponse(List < string > labels, Object State)
	{
		HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);
		if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == 200 || oHttpResponse.getStatusCode() == 202) && (!oHttpResponse.getBody().containsIgnoreCase('faultcode')) || Test.isRunningTest()))
		{
			ToothServiceResponse_LH_HUM ToothHistoryResponse = (ToothServiceResponse_LH_HUM)System.JSON.deserialize(oHttpResponse.getBody(), ToothServiceResponse_LH_HUM.class);
			return Benefits_LD_HUM.ParseToothHistoryRes(ToothHistoryResponse );
		}
		return null;
	}
}