/**************************************************************************** 
Apex Class Name  : Benefits_LC_HUM
Version          : 1.0  
Created Date     : 3/17/2022
Function         : Controller class benefit tab component 
Test class       : Benefits_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Aishwarya Pawar                               3/17/2022                  Original Version - US 3017464
* Swapnali Sonawane 							3/17/2022					US- 3017787
* Kalyani Pachpol								3/17/2022					US- 3149503
* Swapnali Sonawane 							5/27/2022					US- 3143662
* Kalyani Pachpol                               7/28/2022                   US- 3613352
* Jonathan Dickinson				            08/15/2022		            US-3699864
* Aishwarya Pawar				            	03/17/2023		            US-4286514 - Adding switch for Benefit Service Error
* Suraj Patil                             		05/08/2023                 	US#4542585 Dental Plan - Tooth History
* Vishal Shinde                                 06/09/2023                  US 4542629: Contact Servicing: Dental Plan - Snapshot (Surge)
* Raj Paliwal                                   05/19/2023                  US: 4542567 Dental Plan Information
* Vishal Shinde                                 05/22/2023                  US: 4542608 Dental Plan - Reimbursement tool link
* Nirmal Garg									07/13/2023					US-4785080
***************************************************************************************************/

public with sharing class Benefits_LC_HUM {

    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';  
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
    Public Static final String  VOB_RIDERS_HUM = 'Riders';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail';
	public static String  StateMemberPlanId;
    
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String BACKSLASH_HUM = '/';
    Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public static  String sReimbursementAppURL = '';
    Public static  String sOneClickURL = '';
    Public Static final String VOB_REMAPPURL_HUM = 'REMAPPURL';
    public transient PBEServiceTypeV2DTO_HUM ServiceTypeDTO{ get; set; }
   // PBE service DTO changes
		public Object DTOObj
		{
			get
		{
			return ServiceTypeDTO;
		}
			set
		{
			ServiceTypeDTO = (PBEServiceTypeV2DTO_HUM)value;
		}
		}
   

    
    
	/**       
    * method to make PBE service call
    * @param String           MemberPlanId
	* @param String           As Of Date
    * @return contiuation Object
    */    
    @AuraEnabled (continuation=true cacheable=true)
    public static object invokePBEService(String memberPlanId, String asOfDate)
    {
        return Benefits_LS_HUM.SendRequestPBEService(memberPlanId,asOfDate );
    }
    /**       
    * Returns responses of continuation method
    * Callback Method to parse the PBE service response
    * @return Object
    */
    @AuraEnabled(cacheable=true)
    public static Object parse_PBEResponse(List<String> labels, Object state)
    {
        return Benefits_LS_HUM.processPBEResponse(labels, state);
               
    }

    /**       
    * method to make GBE service call
    * @param String           MemberPlanId
	* @param String           As Of Date
    * @return contiuation Object
    */  
    @AuraEnabled (continuation=true cacheable=true)
    public static Continuation invokeGBEService(String memberPlanId, String asOfDate)
    {
        return Benefits_LS_HUM.SendRequestGBEService(memberPlanId,asOfDate );
        
    }
    
    /**       
    * Returns response of continuation method
    * Callback Method to parse the GBE service response
    * @return Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object parse_GBEResponse(List<String> labels, Object state)
    { 
        HttpResponse responseGBE = Continuation.getResponse(labels[0]);
        String result = Benefits_LS_HUM.parseGBE(responseGBE);
        return result;       
    }
    
   /**       
    * method to make MBE service call
    * @param String           MemberPlanId
	* @param String           As Of Date
    * @return contiuation Object
    */
    @AuraEnabled (continuation=true cacheable=true) 
    public static object invokeMBEService(String memberPlanId, String asOfDate){
		return Benefits_LS_HUM.SendRequestMBEService(memberPlanId,asOfDate );
	}
    
	/**       
    * Returns response of continuation method
    * Callback Method to parse the MBE service response
    * @return Object
    */
 	@AuraEnabled
	public static Object parseMBEResponse(List<string> labels, Object state)
	{
		return Benefits_LS_HUM.processMBEResponse(labels, state);
	
    }
    
    /**
    * This method is used to Call Billing Service
    * @param  String NetwordId
    * @return  Void
    */   
	@AuraEnabled (continuation=true cacheable=true) 
    public static Object getBillingProfiles(String sNetworkID, String sSearchID, String sPlatform, String sExchangeType, string sProductType,string sProduct)
    {
    	try
    	{
    		return Benefits_LS_HUM.processBillingProfiles(sNetworkID, sSearchID, sPlatform, sExchangeType, sProductType, sProduct);           
    	}
    	catch(Exception ex)
    	{
    		 HUMExceptionHelper.logErrors(ex,'Benefits_LC_HUM','getBillingProfiles');
    		 throw new AuraHandledException(ex.getMessage());
    	}
    }

    /**
	* Returns the response of Billing Profile
	* <p>
	* Method to Parse the Billing Profile response .
	*
	* @param                      Object State
	* @return                     Object
	*/
	@AuraEnabled
	public static Object parseBillingProfileResponse(List < string > labels, Object state)
	{
       return Benefits_LS_HUM.processBillingResponse(labels, state);		
	}
    
    
    @AuraEnabled (continuation=true cacheable=true) 
	public static Continuation mentorGridService(String memberPlanId)
    {
		return Benefits_LS_HUM.invokeMentorGridService(memberPlanId);
    }

	@AuraEnabled (continuation=true cacheable=true) 
	public static Object parseKMDResponse(List<string> labels, Object state)
    {
		return Benefits_LS_HUM.processKMDResponse(labels, state);
    }
    
    
    /**

	* Method to get the Benefits Search Data
	* @param String           Service Category Id
	* @param String           Type Of Service
	* @param String           Place of Service
	* @param String           Par /non Par
	* @param String           Platform Code
	* @param String           Product Key
	* @param String           Ref Date
	* @return                 Continuation Object
	*/
    @AuraEnabled (continuation=true cacheable=true)
    public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
	{
		return Benefits_LS_HUM.getBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode);
	}
    
    
    /**
	* Callback Method to get the Benefits Search Data
	*
	* @param Object           State of Continaution
	* @return                 Object Continuation response
	*/
     @AuraEnabled (continuation=true cacheable=true)
	public Static Object processBenefitSearch(List<string> labels, Object state)
	{
		return Json.serialize(Benefits_LS_HUM.processBenefitSearch(labels, state));
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Accums Data
	*
	* @param String           Enterprise Id of the policy Member
	* @param String           Type Of benfit
	* @param String           refDate
	* @return  Object         RemoteAction method
	*/
	@AuraEnabled(continuation=true)
	public static Object getAccumsDataREST(String personId, String typeOfbenefit, String refDate)
	{
		try
    	{
			return AccumsType_LD_HUM.getAccumsData(personId, typeOfbenefit, refDate);
		}
    	catch(Exception ex)
    	{
    		 HUMExceptionHelper.logErrors(ex,'Benefits_LC_HUM','getAccumsDataREST');
    		 throw new AuraHandledException(ex.getMessage());
    	}

	}

	/**
	* Returns the response of Accums Service
	* <p>
	* Method to Parse the Accums service response .
	*
	* @param                      Object State
	* @return                     Object
	*/
	@AuraEnabled(continuation=true)
	public static Object prepareServiceDataREST(Object state)
	{
       return Json.serialize(AccumsType_LD_HUM.prepareServiceDataREST(state));		
	}

	/**
    *  Gets list of tooth history from ToothHistory service
    *  @name getToothHistory
    *  @param String,String
    *  @return Continuation object
    */
    @auraEnabled(cacheable = true continuation=true)
    public static Continuation getToothHistory(String sPlanMemberId)
    {        
        List<MemberPlan> lstMemberId = [SELECT Id, Member_Dependent_Code__c, Member_Id_Base__c FROM MemberPlan WHERE id = : String.escapeSingleQuotes(sPlanMemberId) LIMIT 1];  
        if(!lstMemberId.isEmpty() && lstMemberId.size() > 0){
            return Benefits_LS_HUM.callToothHistoryService(lstMemberId[0].Member_Id_Base__c,lstMemberId[0].Member_Dependent_Code__c);
        }
        return null;
    }
    
	/**
    *  Parse list of tooth history data from ToothHistory service
    *  @name parseToothHistoryResponse
    *  @param List < string > ,Object
    *  @return object
    */
	@auraEnabled(cacheable = true continuation=true)
    public Static Object parseToothHistoryResponse(List<string> labels ,Object State)
    {
        try
        {
            return Benefits_LS_HUM.parseToothHistoryResponse(labels,State);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Benefits_LC_HUM','parseToothHistoryResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /*
    * Method Name   :    getswitchvalue()
    * Description   :    This method is called to get the switch value for req - 4542585.   
    * return Type   :    CRMFunctionality_ONOFF_Switch__c
    * Parameters    :    None
    */
    
    @AuraEnabled(cacheable=true)
    public static CRMFunctionality_ONOFF_Switch__c getswitchvalue() {
        try{
            CRMFunctionality_ONOFF_Switch__c crmFunctionalityONOFFSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4542585');
            return crmFunctionalityONOFFSwitch ;
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e,'Benefits_LC_HUM','getswitchvalue()');
			throw new AuraHandledException(e.getMessage());
        }
    }

    /*
    * Method Name   :    getswitchvalueSnapShot()
    * Description   :    This method is called to get the switch value for req - 4542629.   
    * return Type   :    CRMFunctionality_ONOFF_Switch__c
    * Parameters    :    None
    */
    
    @AuraEnabled(cacheable=true)
    public static CRMFunctionality_ONOFF_Switch__c getswitchvalueSnapShot() {
        try{
            CRMFunctionality_ONOFF_Switch__c crmFunctionalityONOFFSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4542629');
            return crmFunctionalityONOFFSwitch ;
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e,'Benefits_LC_HUM','getswitchvalueSnapShot()');
			throw new AuraHandledException(e.getMessage());
        }
    }

    /*
    * Method Name   :    getswitchvaluePlanInformation()
    * Description   :    This method is called to get the switch value for req - 4542567.   
    * return Type   :    CRMFunctionality_ONOFF_Switch__c
    * Parameters    :    None
    */
    
    @AuraEnabled(cacheable=true)
    public static CRMFunctionality_ONOFF_Switch__c getswitchvaluePlanInformation() {
        try{
            CRMFunctionality_ONOFF_Switch__c crmFunctionalityONOFFSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4542567');
            return crmFunctionalityONOFFSwitch ;
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e,'Benefits_LC_HUM','getswitchvaluePlanInformation()');
			throw new AuraHandledException(e.getMessage());
        }
    }
    /*
    * Method Name   :    getswitchvalueReimbursement()
    * Description   :    This method is called to get the switch value for req - 4542608.   
    * return Type   :    CRMFunctionality_ONOFF_Switch__c
    * Parameters    :    None
    */
    
    @AuraEnabled(cacheable=true)
    public static CRMFunctionality_ONOFF_Switch__c getswitchvalueReimbursement() {
        try{
            CRMFunctionality_ONOFF_Switch__c crmFunctionalityONOFFSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4542608');
            return crmFunctionalityONOFFSwitch ;
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e,'Benefits_LC_HUM','getswitchvalueReimbursement()');
			throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static object assignValues(String memberPlanId) {
        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = 'CRMS_520_Benefits_Read_Only' limit 1];
        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
        {
            HUMToothHistoryAppURL__c objAppURL = HUMToothHistoryAppURL__c.getValues(VOB_REMAPPURL_HUM);
            HUMUrlWrapperHelper objUrlWrapper = new HUMUrlWrapperHelper();
            if (objAppURL != null && null != objAppURL.CCPAppURL__C)
            {
                objUrlWrapper.sReimbursementAppURL = '' + objAppURL.CCPAppURL__C;
                objUrlWrapper.sOneClickURL = '' + objAppURL.oneClickURL__c;
            }

            if(string.isNotBlank(memberPlanId)){
                list<MemberPlan> lstMemPlan = [Select Id, ASO__c, Member.personmailingpostalcode From MemberPlan where Id=:memberPlanId];
                if(lstMemPlan.size() > 0)
                {
                    objUrlWrapper.sZipCode = lstMemPlan[0].Member.personmailingpostalcode;
                    objUrlWrapper.sASOcd =  lstMemPlan[0].ASO__c;
                }
            }
            return objUrlWrapper;
        }
        return null;
    }

    public class HUMUrlWrapperHelper{
        @AuraEnabled
        public String sReimbursementAppURL;
        @AuraEnabled
        public String sOneClickURL;
        @AuraEnabled
        public String sZipCode;
        @AuraEnabled
        public String sASOcd;
    }
}