/*******************************************************************************************************************************
Apex Class Name : Pharmacy_LS_HUM
Version         : 1.0
Created On      : 07/19/2021
Function        : This class is used for performing the Lightning Humana Pharmacy related work
Test Class      : Pharmacy_LT_HUM

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Pallavi Shewale                                     07/19/2021                Calling GetMemberDetailsService
*    2.0              Himalay Patel                                      08/20/2021                User Story 2421627: T1PRJ0002517 - MF 2 - Updating Addresses That Apply to More Than One Address Type (RxRF) (ID# 91)
*    3.0           Swapnali Sonawane                                   09/02/2021                UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
*    4.0		   Nirmal Garg										   10/05/2021			     US2504099 - Changes. Added edit member logic
*	 5.0		   Abhishek Mangutkar								   10/12/2021				 US - 2528482 - T1PRJ0078574 - MF 11812 - HP Lightning- 'Prescriptions' - Filters
*	 6.0		   Pallavi Shewale								       10/28/2021				 US - 2664846 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order Summary - Cards
*    7.0           Swapnali Sonawane                                   11/14/2021                US - 2766098  T1PRJ0078574 - MF 11811 - HP Lightning- Add 'Prescriptions' - Auto refill
*    8.0           Nirmal Garg                                         02/13/2023                US - 4134646 Changes
*    9.0           Monali Jagtap                                       09/29/2023       US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*	 10.0		   Monali Jagtap									   07/17/2023 				 USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*******************************************************************************************************************************************************************/

public with sharing class Pharmacy_LS_HUM{

	//====Other Insurance====
    @TestVisible private static String continuationLabel;
    public static map<String, MemberPlan> mapPolicyMembers = new map<string, MemberPlan>();
    public static map<String, list<PurchaserPlan>> mapPolicyPlanDetails = new map<string, list<PurchaserPlan>>();
    Private Static String sNumOne = '1';
    Private Static String sNumZero = '0';
	Public Static final String PHARMACY_EDITMEMBER_HUM='retrieveUpdatedGetMember';
	Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202;
    Public Static final String PHARMACY_ZIP_CITY_ERROR =  'IncompatibleZipCity';
	Public Static final String HUM_GETORDERRES = 'parseRSGetOrderResponse';
    public static final string HUM_MEMBER_NOT_FOUND = 'Member not found';
    Public Static final String HUMADDRESSERROR='CONFIRM';
    public static String sBodyJSON;
    public static map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> mapCOBDetails;
    public static Continuation con;
    public static  List<MemberPlan> oPolMemberInfo;
    public static HttpResponse oResponse;
    public static Boolean Generic_Security_Switch = HUMUtilityHelper.isCRMFunctionalityON('Generic_Security_Switch');
    public class ServiceDownException extends Exception {}

	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param memID                      String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
        @AuraEnabled(continuation=true)
        public static Object SendRequestGetMemberResponse(String memID,string networkId, String sRecordId){
            try{
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseRSGetMemberResponse';
            con.state = sRecordId;
            HttpRequest httpRSGetMemberReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemberReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetMemberResponse(memID, networkId)));
            con.addHttpRequest(httpRSGetMemberReqObj);
            return con;

            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'SendRequestGetMemberResponse');
                throw new AuraHandledException(ex.getMessage());
            }
        }

		 /* Returns HttpRequest request with request body
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
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUMConstants.HUM_RSS_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUMConstants.HUM_RSS_SER_CONECTION, HUMConstants.HUM_RSS_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUMConstants.HUM_RSS_SER_SOAPACTION, '');
            }
            return request;
        }
          /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services for Addresses Only.
     *
     * @param memID                  	 String
     * @param networkId                  String
     * @param sRecordId                  String
     * @param addressData                AddressValidationHelper_DTO_HUM
     * @return                           continuation
     */
    @AuraEnabled(continuation=true)
    public static Object prepareEditGetMemberAddresses(string memID, string networkId, String sRecordId, String addressRequestJSON)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedGetMemberAddresses';
            con.state = memID+'_'+networkId+'_'+sRecordId;
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(addressRequestJSON);
            con.addHttpRequest(httpEditGetMemberReqObj);
	        return con;

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'prepareEditGetMemberAddresses');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedGetMemberAddresses Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    @AuraEnabled
    public static object retrieveUpdatedGetMemberAddresses(List <string> labels, Object state)
    {
        try
        {
            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();

	        if (!editMemberResponseBody.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || responseEditMember.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
	        {
	             return editMemberResponseBody;
	        }
	        else if(editMemberResponseBody.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && (editMemberResponseBody.containsIgnoreCase('ADDR0') || (editMemberResponseBody.containsIgnoreCase('SERV00001') && editMemberResponseBody.containsIgnoreCase('does not exist in ePostRx CityStateZip table'))))
	        {
	        	return GLOBAL_CONSTANT_HUM.PHARMACY_ZIP_CITY_ERROR;
	        }
	        else return editMemberResponseBody;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retrieveUpdatedGetMemberAddresses');
            return null;
        }
    }

   /**
    * Returns an object and method calls the MBEPlus web service
    * @pram  Enterprise Id in string format
    * @param  type in string format
    * @return  object
    */
    public static Object getOtherInsuranceData(String sMemberPlanId,string sEnterpriseId)
    {
        MBEPluseResponseWrapper_DTO_Hum  oPolMemberDetails = new MBEPluseResponseWrapper_DTO_Hum ();
        try
        {
         	processMBEResponse_Sec(sMemberPlanId,sEnterpriseId);
          	return oPolMemberDetails;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Pharmacy_LS_HUM', 'getOtherInsuranceData');
        }
        return oPolMemberDetails;
    }
   
    public static Continuation processMBEResponse_Sec(String sMemberPlanId,string sEnterpriseId){
        		con = new Continuation(GlobalCommonConstants_HUM.SERVICETIMEOUT);
                MBEPlusOIRequest_DTO_HUM oReq = new MBEPlusOIRequest_DTO_HUM();
                oReq = (String.isNotBlank(sEnterpriseId))?createMBEPlusRequest(sEnterpriseID):null; // construct MBE+ Request //JOELCHECK_Shift
                sBodyJSON =(oReq!=null)? JSON.serialize(oReq):'';
            	HttpRequest mbeRequest =(String.isNotBlank(sBodyJSON))? HUMProcessUtilityHelper.builtHttpRequest():null;
                con=(mbeRequest!=null)?setMBERequest(sMemberPlanId,mbeRequest):null;
                return con;
    }
    public static Continuation setMBERequest(String sMemberPlanId, HttpRequest mbeRequest)
    {
         mbeRequest.setBody(sBodyJSON);
                        con.continuationMethod = 'processMBEOtherInsuranceResponse';
					    continuationLabel = con.addHttpRequest(mbeRequest);
                        Map<String,String> stateMapOI= new Map<String,String>();
                        stateMapOI.put('sPolicyMember',sMemberPlanId);
                        stateMapOI.put('sRequest',continuationLabel);
                        con.state=stateMapOI;
        return con;
    }
    /*
    * Method Name : createMBEPlusRequest
    * Description : This method is used to Create the MBEPLus Request DTO for COB Template
    * Return type : MBEPlusOIRequest_DTO_HUM
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request, Boolean to add OICOB node in the request
    */
    public static MBEPlusOIRequest_DTO_HUM createMBEPlusRequest(String sEnterpriseID)
    {
        MBEPlusOIRequest_DTO_HUM req;
        try
        {
            if(String.isNotBlank(sEnterpriseID) )
            {
                req = new MBEPlusOIRequest_DTO_HUM();
                MBEPlusOIRequest_DTO_HUM.GetMember getMember = new MBEPlusOIRequest_DTO_HUM.GetMember();
                MBEPlusOIRequest_DTO_HUM.Member   mem = new MBEPlusOIRequest_DTO_HUM.Member();
                MBEPlusOIRequest_DTO_HUM.DualStatusList objDualStatusList = new MBEPlusOIRequest_DTO_HUM.DualStatusList();
                mem.DualStatusList  = objDualStatusList;
                mem.SoldProductList = createSoldProductList();

                mem.GetCriteria = createGetCriteriaList(sEnterpriseID);

                getMember.Member = mem;
                req.GetMember = getMember;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Pharmacy_LS_HUM', 'createMBEPlusRequest');
        }
        return req;
    }
    /*
    * Method Name : createSoldProductList
    * Description : This method is used to create the input request for fetching the COB Detals
    * Return type : MBEPlusOIRequest_DTO_HUM.SoldProductList
    * Paramater   :
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.SoldProductList createSoldProductList()
    {
        /* Start of Create SOld product list */
        MBEPlusOIRequest_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusOIRequest_DTO_HUM.SoldProductList();
        List<MBEPlusOIRequest_DTO_HUM.SoldProduct> listObjSoldProduct = new List<MBEPlusOIRequest_DTO_HUM.SoldProduct>();
        MBEPlusOIRequest_DTO_HUM.SoldProduct ObjSoldProduct = new MBEPlusOIRequest_DTO_HUM.SoldProduct();
        MBEPlusOIRequest_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusOIRequest_DTO_HUM.InsuranceProduct();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetailList soldProDetailList = new MBEPlusOIRequest_DTO_HUM.SoldProductDetailList();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetail  soldProductDetail  = new MBEPlusOIRequest_DTO_HUM.SoldProductDetail();
        MBEPlusOIRequest_DTO_HUM.InsuranceCoverage  insuranceCoverage  = new MBEPlusOIRequest_DTO_HUM.InsuranceCoverage();
        MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList coveredRolPlatPointList = new MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList();
        MBEPlusOIRequest_DTO_HUM.CoveredRoleList coverRoleList= new MBEPlusOIRequest_DTO_HUM.CoveredRoleList();
        MBEPlusOIRequest_DTO_HUM.CoveredRole coveredRol = new MBEPlusOIRequest_DTO_HUM.CoveredRole();
        MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList COBList = new MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList();
        insuranceCoverage.CoordinationOfBenefitList = COBList;
        MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList OICOBList = new MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList();
        insuranceCoverage.OICoordinationOfBenefitList = OICOBList;

        MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList grpList = new MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList();
        insuranceCoverage.GroupPlatformPointerList = grpList;


        coveredRol.CoveredRolePlatformPointerList= coveredRolPlatPointList;
        coverRoleList.CoveredRole= coveredRol;
        insuranceCoverage.CoveredRoleList =coverRoleList;
        soldProductDetail.InsuranceCoverage = insuranceCoverage;
        soldProDetailList.SoldProductDetail = soldProductDetail;
        objInsuranceProduct.SoldProductDetailList = soldProDetailList;
        ObjSoldProduct.InsuranceProduct = objInsuranceProduct;
        listObjSoldProduct.add(objSoldProduct);
        objSoldProductList.SoldProduct = listObjSoldProduct;
        /* End of Create SOld product list */

        return objSoldProductList;
    }


    /*
    * Method Name : createGetCriteriaList
    * Description : This method is used to create the input request Criteria for MBEPlus Service
    * Return type : MBEPlusOIRequest_DTO_HUM.GetCriteria
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.GetCriteria createGetCriteriaList(String sEnterpriseID)
    {
        /* Start of Create getCriteriaList*/
        MBEPlusOIRequest_DTO_HUM.GetCriteria  getCriteriaList = new MBEPlusOIRequest_DTO_HUM.GetCriteria();
        MBEPlusOIRequest_DTO_HUM.PersonIdCriteria  personIdCriteria  = new MBEPlusOIRequest_DTO_HUM.PersonIdCriteria();
        personIdCriteria.PersonId = sEnterpriseID;
        MBEPlusOIRequest_DTO_HUM.PeriodList periodValues = new MBEPlusOIRequest_DTO_HUM.PeriodList();
        periodValues.Period = new list<String>{ GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_CURRENT_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PAST_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FUTURE_HUM };
        personIdCriteria.PeriodList = periodValues;
        personIdCriteria.AsOfDate = DateTime.now().format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PCP_REFDATEFORMT_HUM);
        getCriteriaList.PersonIdCriteria  = personIdCriteria;
        /* End of Create getCriteriaList*/

        return getCriteriaList;
    }

    /*
    *  This method is used to Parse response from MBEPlus service
    *  @name   processMBEOtherInsuranceResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processMBEOtherInsuranceResponse(object state)
    {
        MBEPluseResponseWrapper_DTO_Hum  oPolMemberDetails = new MBEPluseResponseWrapper_DTO_Hum ();
        try
        {
             Map<String,String> mapState=(map<String, String>)state;
            oResponse = Continuation.getResponse(mapState.get('sRequest'));
            String sMPID =  mapState.get('sPolicyMember');
       		 oPolMemberInfo = Pharmacy_LD_HUM.getMemberName(sMPID);
            retrieveCoverageDetails(sMPID); // retrive details of Policy Member
       	    processMBEResponse_Sec(state,oPolMemberDetails);
           
         }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_LS_HUM', 'processMBEOtherInsuranceResponse');
            throw new AuraHandledException(ex.getMessage());
        }

        return oPolMemberDetails;
    }
    public static MBEPluseResponseWrapper_DTO_Hum processMBEResponse_Sec(object state, MBEPluseResponseWrapper_DTO_Hum  oPolMemberDetails)
   {
            MBEPlusOIResponse_DTO_HUM outResponse =  ((oResponse != null && !oResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) &&
               				 (oResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || oResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM )
               				 ) || Test.isRunningTest() )?(MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(oResponse.getbody(), MBEPlusOIResponse_DTO_HUM.class):null;
            MemberPlan oPolicyMember =(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null &&
                  			 outResponse.GetMemberResponse.Body.Member != null && String.isNotBlank(outResponse.GetMemberResponse.Body.Member.MasterId) &&
                 			 mapPolicyMembers.containsKey(outResponse.GetMemberResponse.Body.Member.MasterId))? mapPolicyMembers.get(outResponse.GetMemberResponse.Body.Member.MasterId):null;
            list<PurchaserPlan> lstPlans = new list<PurchaserPlan>();
            lstPlans =(mapPolicyPlanDetails!=null && !mapPolicyPlanDetails.isEmpty() && mapPolicyPlanDetails.containsKey(oPolicyMember.PlanId))?mapPolicyPlanDetails.get(oPolicyMember.PlanId):null;
                     //Retrieve COB details from MBE Response
                    //Below code will parse MBE+ repsonse with new node OICoordinationOfBenefit
             list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBFromService = parseMBERespForOICOBDetails(outResponse, oPolicyMember, lstPlans);
             oPolMemberDetails.validOIWrapperList = (lstOICOBFromService!=null && !lstOICOBFromService.isEmpty() && !oPolMemberInfo[0].ETL_Record_Deleted__c) ? processOICOBData(lstOICOBFromService) : Null ;
         return oPolMemberDetails;
   }
    /*
    * Method Name : retrieveCoverageDetails
    * Description : This method is used to retrieve the coverage details for a PolicyMember to be dispalyed on COB template
    * Return type : Void
    * Paramater   : None
    */
   public static void retrieveCoverageDetails(String polMemId)
    {
        try
        {   
            getCoverage_Sec(polMemId);            
        }
       catch(Exception e)
        {
           HUMExceptionHelper.logErrors(e, 'Pharmacy_LS_HUM', 'retrieveCoverageDetails');
        }
    }
  
    public static void getCoverage_Sec(String polMemId){
          list<MemberPlan> lstPolicyMem = new list<MemberPlan>();
                set<Id> setPolicyIds = new set<Id>();
                lstPolicyMem = (String.isNotBlank(polMemId))? Pharmacy_LD_HUM.getMemberName(polMemId):null;
                boolean isTrue;
			    if(lstPolicyMem!=null && !lstPolicyMem.isEmpty() && String.isNotBlank(lstPolicyMem[0].Member.Enterprise_Id__c))
                {
                    mapPolicyMembers.put(lstPolicyMem[0].Member.Enterprise_Id__c, lstPolicyMem[0]);
           		    isTrue =(lstPolicyMem[0].PlanId !=null)?(setPolicyIds.add(lstPolicyMem[0].PlanId)):false;
                }

                     mapPolicyMembers=(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty())?mapPolicyMembers:null;
                     list<PurchaserPlan> lstPolicyPlans = new list<PurchaserPlan>();
                     lstPolicyPlans =(setPolicyIds!=null && !setPolicyIds.isEmpty())? Pharmacy_LD_HUM.getPurchaserPlanOtherInsurance(setPolicyIds):null;
                     mapPolicyPlanDetails = (lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())? getPolicyPlanDetails(lstPolicyPlans):null;      
    }
    public static map<String, list<PurchaserPlan>> getPolicyPlanDetails(list<PurchaserPlan> lstPolicyPlans)
    {
         for(PurchaserPlan oPlan : lstPolicyPlans)
                            {
                                if(mapPolicyPlanDetails.containsKey(oPlan.Id)){
                                    mapPolicyPlanDetails.get(oPlan.Id).add(oPlan);
                                }else{
                                    mapPolicyPlanDetails.put(oPlan.Id, new list<PurchaserPlan>{oPlan});
                                }
                            }
        return mapPolicyPlanDetails;
    }

    /*
    * Method Name : parseMBERespForOICOBDetails
    * Description : This method is used to fetch the COB details DTO from MBEPlus Response
    * Return type : list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> - list of COB details wrapper
    * Paramater   : policyMemRec - Policy_Member__c record for the COB details, oMBEResponse - MBEPlus Response, list<Policy_Plan_Details__c>
    */
    public static list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> parseMBERespForOICOBDetails(MBEPlusOIResponse_DTO_HUM oMBEResponse, MemberPlan policyMemRec, list<PurchaserPlan> lstPolicyPlans)
    {
        list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
        map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> mapCOBDetails = new map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
        try
        {
            if(oMBEResponse!=null && policyMemRec!=null && oMBEResponse.GetMemberResponse != null && oMBEResponse.GetMemberResponse.Body != null && oMBEResponse.GetMemberResponse.Body.Member != null && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
            {
                MBEPlusOIResponse_DTO_HUM.Member oMemberDTO = oMBEResponse.GetMemberResponse.Body.Member;
                list<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProducts = new list<MBEPlusOIResponse_DTO_HUM.SoldProduct>();
                MBEPlusOIResponse_DTO_HUM.SoldProductList oSoldProductList = oMemberDTO.SoldProductList;
                lstSoldProducts =(oMemberDTO!=null && oMemberDTO.SoldProductList!=null && oMemberDTO.SoldProductList.SoldProduct!=null)? oSoldProductList.SoldProduct:null;
                
                if(lstSoldProducts!=null && !lstSoldProducts.isEmpty())
                {
                    for(MBEPlusOIResponse_DTO_HUM.SoldProduct oSoldProduct : lstSoldProducts)
                    {
                        if(oSoldProduct.InsuranceProduct != null && oSoldProduct.InsuranceProduct.SoldProductDetailList != null && oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail !=null
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.Source) && String.isNotBlank(policyMemRec.Policy_Platform__c) && oSoldProduct.InsuranceProduct.Source.equals(policyMemRec.Policy_Platform__c)
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.ProductId) && checkProductIDFromCRM(oSoldProduct.InsuranceProduct.ProductId, lstPolicyPlans))
                        {
                            String sGroupIDCRM = policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM) || policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HumHPRecType) ? policyMemRec.Plan.Source_Cust_Cov_Key__c.subString(0,6) : policyMemRec.GroupNumber;
							 list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail;
                            if(lstSoldProductDetail!=null && !lstSoldProductDetail.isEmpty())
                            {
                                for(MBEPlusOIResponse_DTO_HUM.SoldProductDetail oSoldProductDetail : lstSoldProductDetail)
                                {
                                    String sGroupID='';
                                    String sMemberRelationship='';
                                    String sMemberIDBase='';
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList!=null &&
                                       oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey !=null && !oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey grpPlatformKey : oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey)
                                        {
                                                sGroupID =(String.isNotBlank(grpPlatformKey.KeyType) && grpPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))? grpPlatformKey.KeyValue:'';
                                                break;
                                        }
                                    }
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole!=null && !oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.CoveredRole oCoveredRole: oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole)
                                        {
                                            if(String.isNotBlank(oCoveredRole.MemberMasterId) && oCoveredRole.MemberMasterId.equals(policyMemRec.Member.Enterprise_ID__c))
                                            {
                                                 for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey memPlatformKey : oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey)
                                                    {
                                                        sMemberRelationship =(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDDEPENDENTCODE))? memPlatformKey.KeyValue:'';
                                                        sMemberIDBase =(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDBASE))? memPlatformKey.KeyValue:'';
                                                    }
                                                
                                                break;
                                            }
                                        }
                                    }
                                    String endDate = '';
                                    String startDate = '';
                                    if(policyMemRec.EffectiveTo != null){
                                        if(policyMemRec.EffectiveTo == date.newInstance(4000,12,31)){
                                            endDate = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DefaultEffectiveDate;
                                        }else{
                                            Datetime endDateTime = datetime.newInstance(policyMemRec.EffectiveTo.year(), policyMemRec.EffectiveTo.month(),policyMemRec.EffectiveTo.day());
                                            endDate = endDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);
                                        }
                                    }
                                    if(policyMemRec.EffectiveFrom != null){
                                        Datetime startDateTime = datetime.newInstance(policyMemRec.EffectiveFrom.year(), policyMemRec.EffectiveFrom.month(),policyMemRec.EffectiveFrom.day());
                                        startDate = startDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);
                                    }

                                    if(oSoldProductDetail.InsuranceCoverage != null && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.StartDate)
                                       && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.EndDate) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList != null
                                       && oSoldProductDetail.InsuranceCoverage.StartDate.equals(startDate) && oSoldProductDetail.InsuranceCoverage.EndDate.equals(endDate)
                                       && String.isNotBlank(sMemberIDBase) && String.isNotBlank(sMemberRelationship) && String.isNotBlank(policyMemRec.Name) && policyMemRec.Name.equalsIgnoreCase(sMemberIDBase+sMemberRelationship)
                                       && String.isNotBlank(sGroupID) && String.isNotBlank(sGroupIDCRM) && sGroupID.equalsIgnoreCase(sGroupIDCRM) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit!=null
                                       && !oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit.isEmpty())
                                   	 {
                                        String sMapKey = '';
                                        for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCOBDTO : oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit)
                                        {
                                           oCOBDTO= (oCOBDTO!=null)?getCOBData(sMapKey,oCOBDTO):null;
                                        }       
                                     }
                                    
                                }
                            }
                        }
                    }
                }
            }
            if(mapCOBDetails!=null && !mapCOBDetails.isEmpty()) lstCOBDetails.addAll(mapCOBDetails.values());
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Pharmacy_LS_HUM', 'parseMBERespForOICOBDetails');
        }
        return lstCOBDetails;
    }
    public static MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit getCOBData(String sMapKey,MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCOBDTO){
         sMapKey = '';
                               sMapKey =(String.isNotBlank(oCOBDTO.CarrierName))? sMapKey + oCOBDTO.CarrierName:'';
                                                sMapKey =(String.isNotBlank(oCOBDTO.EffectiveDate))?sMapKey+ oCOBDTO.EffectiveDate:'';
                                                sMapKey =(String.isNotBlank(oCOBDTO.EndDate))?sMapKey +oCOBDTO.EndDate:'';
                                                sMapKey =(String.isNotBlank(oCOBDTO.Primary))?sMapKey + oCOBDTO.Primary:'';
                                                sMapKey =(String.isNotBlank(oCOBDTO.OILastVerifiedDate))?sMapKey + oCOBDTO.OILastVerifiedDate:'';
                                                sMapKey =(String.isNotBlank(oCOBDTO.LastUpdatedDate))?sMapKey + oCOBDTO.LastUpdatedDate:'';
                                                oCOBDTO =(String.isNotBlank(sMapKey))? mapCOBDetails.put(sMapKey, oCOBDTO):null;
   return oCOBDTO;
    }
   
    /**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processOICOBData
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of ValidOIWrapper_DTO_HUM
    */
    public static list<ValidOIWrapper_DTO_HUM> processOICOBData(list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCoordinationOfBenefit)
    {
        list<ValidOIWrapper_DTO_HUM>  lstOtherInsuranceData = new list<ValidOIWrapper_DTO_HUM>();
        for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCoordinationOfBenefit : lstCoordinationOfBenefit)
        {
            ValidOIWrapper_DTO_HUM oOIWrap = new ValidOIWrapper_DTO_HUM();

            oOIWrap.sCarrierName = oCoordinationOfBenefit.CarrierName;
            oOIWrap.sEffectiveDate = oCoordinationOfBenefit.EffectiveDate;
            oOIWrap.sEndDate = oCoordinationOfBenefit.EndDate;
            oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == sNumOne) ? 'Y' : (oCoordinationOfBenefit.Primary == sNumZero) ? 'N' : '';
            oOIWrap.sOILastVerifiedDate = oCoordinationOfBenefit.OILastVerifiedDate;
            oOIWrap.sLastUpdatedDate = oCoordinationOfBenefit.LastUpdatedDate;

            lstOtherInsuranceData.add(oOIWrap);
        }
        return lstOtherInsuranceData;
    }

    /*
    * Method Name : checkProductIDFromCRM
    * Description : Method is used to match ProducId from MBE+ reponse with the ProductId in CRM.
    * Return type : Boolean
    * Paramater   : String, list<PurchaserPlan>
    */
    @testVisible
    private static Boolean checkProductIDFromCRM(String sProductId, list<PurchaserPlan> lstPolicyPlans)
    {
        Boolean bProductIDMatched = false;
        if(String.isNotBlank(sProductId) && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
        {
            for(PurchaserPlan oPolicyPlan : lstPolicyPlans)
            {
                if(oPolicyPlan.Product__c!=null && String.isNotBlank(oPolicyPlan.Product__r.Sold_Product_Key_Value__c) && sProductId.equals(oPolicyPlan.Product__r.Sold_Product_Key_Value__c))
                {
                    bProductIDMatched = true;
                    break;
                }
            }
        }
        return bProductIDMatched;
    }

	/**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param memID                      String
     * @param startDate                  String
     * @param endDate                    String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Object sendRequestGetMemberResponse(String memID, String startDate, String endDate, string networkId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUMConstants.HUM_RSGetMember_Response;
            con.state = sRecordId;
            HttpRequest httpRSGetMemReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetMemReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.requestStructureGetMemberResponse(memID, startDate, endDate,networkId)));
            con.addHttpRequest(httpRSGetMemReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'sendRequestGetMemberResponse');
             throw new AuraHandledException(ex.getMessage());
        }
    }

	/** created for AccountDetails
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetMemberResponse services .
     *
     * @param enterprise                String
     * @param phone                     String
     * @param AltPhone                  String
     * @param Email                     String
     * @param captype                   String
     * @param networkId                  String
     * @param sRecordId                  String
     * @param IsDeclined                 String
     * @return                           continuation
     */
      @AuraEnabled(continuation=true)
     public static Object prepareEditGetMember(string memID, string phone, string AltPhone, string Email, string captype, string networkId, String sRecordId, AddressValidationHelper_DTO_HUM addressData,string IsDeclined)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = PHARMACY_EDITMEMBER_HUM;
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetEditMemberResponse(memID,phone,AltPhone,Email,captype,networkId,addressData,IsDeclined)));
            con.state = memID+'_'+networkId+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'prepareEditGetMember');
            throw new AuraHandledException(ex.getMessage());
        }
    }

	/**
     * Returns responses of continuation object  for Account Details
     * <p>
     * retrieveUpdatedGetMember Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    @AuraEnabled
    public static string retrieveUpdatedGetMember(List <string> labels, object State)
    {
        try
        {

            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                 return editMemberResponseBody;

            }
            else if(editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && (editMemberResponseBody.containsIgnoreCase('ADDR0') || (editMemberResponseBody.containsIgnoreCase('SERV00001') && editMemberResponseBody.containsIgnoreCase('does not exist in ePostRx CityStateZip table'))))
            {
                return PHARMACY_ZIP_CITY_ERROR;
            }


            return editMemberResponseBody;

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retrieveUpdatedGetMember');
            return null;
        }
	}

	/**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit member.
     *
     * @param sMemId                     String
     * @param capType                    String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */

    public static object updatemembercaptype(string sMemId, string capType, string networkID, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedCapType';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetcaptypeResponse(sMemID,capType, networkID)));
            con.state = sMemID+'_'+networkID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'updatemembercaptype');
            throw new AuraHandledException(ex.getMessage());
        }
    }

     /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedCapType Method to parse the CapType service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */

    public static object retrieveUpdatedCapType(List < string > labels, object State)
    {
        try
        {
			HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            string result = null;
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                 result = 'success';
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_LS_HUM', 'retrieveUpdatedCapType', '');
            }
            return result;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retrieveUpdatedCapType');
            throw new AuraHandledException(ex.getMessage());
        }
    }

	 /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit Member.
     *
     * @param sMemId                     String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */

    public static object prepareUpdateConsentBeginDate(string sMemId, string networkId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedMemberConsent';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetEditMemberRequestForConsentBeginDate(sMemID, networkId)));
            con.state = sMemID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'prepareUpdateConsentBeginDate');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit member.
     *
     * @param sMemId                     String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */

    public static continuation prepareUpdateConsentEndDate(string sMemId, string networkID, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'retrieveUpdatedMemberConsent';
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetEditMemberRequestForConsentEndDate(sMemID, networkID)));
            con.state = sMemID+'_'+sRecordId;
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'prepareUpdateConsentEndDate');
            return null;
        }
    }

    /**
     * Returns responses of continuation object
     * <p>
     * retrieveUpdatedMemberConsent Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */

    public static string retrieveUpdatedMemberConsent(List <string> labels, object State)
    {
        try
        {
            HttpResponse responseEditMember = Continuation.getResponse(labels[0]);
            String editMemberResponseBody = responseEditMember.getBody();
            string result = null;
            if (!editMemberResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseEditMember != null && ((responseEditMember.getStatusCode() == HTTP_OK_HUM || responseEditMember.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                result = 'success';
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseEditMember,'Pharmacy_LS_HUM', 'retrieveUpdatedMemberConsent', '');
            }

            return result;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retrieveUpdatedMemberConsent');
            return null;
        }
    }

	 /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetScript services .
     *
     * @param memID                      String
     * @param scriptKey                  String
     * @param startDate                  String
     * @param endDate                    String
     * @param networkID                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    public static Continuation sendRequestGetScript(String memID, String scriptKey, String startDate, String endDate, string networkID, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetScripts');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.HUM_GETSCRIPTS;
            con.state = sRecordId;
            HttpRequest httpRSGetScriptObj = builtHttpRequest(RSServiceDetails);
            httpRSGetScriptObj.setBody(JSON.serialize(Pharmacy_LD_HUM.requestStructureGetScriptResponse(memID,scriptKey,startDate, endDate, networkID)));
            con.addHttpRequest(httpRSGetScriptObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'sendRequestGetScript');
            return null;
        }
    }

	/**
     * Returns responses of continuation object
     * <p>
     * parseRSGetScriptResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    public static list<PharmacyMemberPrescription_DTO_HUM> parseRSGetScriptResponse(List < string > labels, Object State)
    {
        try
        {
            list <PharmacyMemberPrescription_DTO_HUM> responseDTOList;
            HttpResponse responsePrescriptionSummary = Continuation.getResponse(labels[0]);
            String prescriptionSummaryrResponseBody = responsePrescriptionSummary.getBody();

            RightSourceGetScriptResponse_DTO_HUM oResponseGetMemberPrescription;
            if (responsePrescriptionSummary != null && ((responsePrescriptionSummary.getStatusCode() == HTTP_OK_HUM || responsePrescriptionSummary.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responsePrescriptionSummary.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                oResponseGetMemberPrescription= (RightSourceGetScriptResponse_DTO_HUM) System.JSON.deserialize(prescriptionSummaryrResponseBody, RightSourceGetScriptResponse_DTO_HUM.class);
                responseDTOList = Pharmacy_LD_HUM.preparememerPrescriptionResponse(oResponseGetMemberPrescription);
           }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responsePrescriptionSummary,'Pharmacy_LS_HUM', 'parseRSGetScriptResponse', '');
            }
            return responseDTOList;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'parseRSGetScriptResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }

	    /**
     * Returns continuation object
     * <p>
     * Method to call continuation serive for the serive for GetOrderResponse services .
     *
     * @param memID                      String
     * @param startDate                  String
     * @param endDate                    String
     * @param networkId                  String
     * @param sRecordId                  String
     * @return                           continuation
     */
    @AuraEnabled
    public static Continuation sendRequestGetOrderResponse(String memID, String startDate, String endDate, string networkId, String sRecordId)
    {
        try
        {
            HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetOrders');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = HUM_GETORDERRES;
            con.state = sRecordId;
            HttpRequest httpRSGetOrderReqObj = builtHttpRequest(RSServiceDetails);
            httpRSGetOrderReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.requestStructureGetOrderResponse(memID, startDate, endDate,networkId)));

            con.addHttpRequest(httpRSGetOrderReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_S_HUM', 'sendRequestGetOrderResponse');
            return null;
        }
    }

     /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetOrderResponse Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
     @AuraEnabled

    public static object parseRSGetOrderResponse(List < string > labels, Object State)
    {
        try
        {
            PharmacyOrderSummary_DTO_HUM  responseDTOList;
            HttpResponse responseOrderSummary = Continuation.getResponse(labels[0]);
            String orderSummaryrResponseBody = responseOrderSummary.getBody();
            RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder;
            if (!orderSummaryrResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseOrderSummary != null && ((responseOrderSummary.getStatusCode() == HTTP_OK_HUM || responseOrderSummary.getStatusCode() ==HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                oResponseGetOrder = (RightSourceGetOrderResponse_DTO_HUM) System.JSON.deserialize(orderSummaryrResponseBody, RightSourceGetOrderResponse_DTO_HUM.class);
                if (null != oResponseGetOrder && null != oResponseGetOrder.GetOrdersReponse && oResponseGetOrder.GetOrdersReponse.ErrorDescription != HUM_MEMBER_NOT_FOUND)
                    responseDTOList = Pharmacy_LD_HUM.prepareOrderSummaryResponse(oResponseGetOrder);
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseOrderSummary,'Pharmacy_LS_HUM', 'parseRSGetOrderResponse', State != null ? String.valueof(State) : '');
            }

                    return JSON.serialize(responseDTOList);

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'parseRSGetOrderResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for Edit script.
     *

     * @param sScriptKey                 String
     * @param sAutoRefill                String
     * @param networkID                  String
     * @return                           continuation
     */
    @AuraEnabled( continuation=true cacheable=true)
    public static object updatescriptautorefil(String sScriptKey,String sAutoRefill, string networkID)
    {
        try
        {
	        HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditScript');
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = 'retrieveautorefil';
	        HttpRequest httpEditscriptautorefilReqObj = builtHttpRequest(serviceDetails);
	        httpEditscriptautorefilReqObj.setBody(JSON.serialize(Pharmacy_LD_HUM.RequestStructureGetAutorefilResponse(sScriptKey,sAutoRefill,networkID)));
	        con.state = sScriptKey+'_'+networkID+'_'+sAutoRefill;
	        con.addHttpRequest(httpEditscriptautorefilReqObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'updatescriptautorefil');
           throw new AuraHandledException(ex.getMessage());
        }

    }
    /**
     * Returns responses of continuation object
     * <p>
     * retrieveautorefil Method to parse the service response
     *
     * @param  object state,List<string> labels .
     * @return Object
     */
    @AuraEnabled
    public static object retrieveautorefil(List <string> labels, object State)
    {
        try
        {
	        HttpResponse responseEditScript = Continuation.getResponse(labels[0]);
	        String editScriptResponseBody = responseEditScript.getBody();
	        string inputvalues = string.valueOf(state);
            continuation conObj;

            if (!editScriptResponseBody.containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && responseEditScript != null && ((responseEditScript.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || responseEditScript.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                    return 'success';
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseEditScript,'Pharmacy_LS_HUM', 'retrieveautorefil', '');
            }
            return conObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retrieveautorefil');
            return null;
        }
    }
    //
    /**
    * Request Structure for EAS service for Account Details
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    @param addressDto AddressValidationHelper_DTO_HUM class instance
    */
     @AuraEnabled
    public static object validateAddress(AddressValidationHelper_DTO_HUM addressDto)
    {
        try
        {
            HUM_Webservice_Callout__c serviceAddressDetails = HUM_Webservice_Callout__c.getValues('Enterprise_Address_Service');
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceAddressDetails.Timeout__c) / 1000;
            Continuation conAddObj;
            if(addressDto != null)
            {
                conAddObj = new Continuation(iTIMEOUT_INT_SECS);
                conAddObj.continuationMethod = 'addressResponse';
                HttpRequest httpAddReqObj = builtHttpRequest(serviceAddressDetails);
                conAddObj.state = addressDto;
                EAS_AddressStandardizationRequest_H_HUM requestObj = new EAS_AddressStandardizationRequest_H_HUM();
                requestObj.StandardizeAddressRequest = addressDto.addressReq;
                httpAddReqObj.setBody(JSON.serialize(requestObj));
                conAddObj.addHttpRequest(httpAddReqObj);
            }
            return conAddObj;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Pharmacy_LS_HUM', 'validateAddress');
            throw new AuraHandledException(ex.getMessage());
        }
    }

     //
    /**
    * Response Structure for EAS service for Account Details
    * <p>
    * Method to set the values for Pharmacy_C_HUM.
    *
    *@param object state,List<string> labels .
    *@return Object
    */
    @AuraEnabled(continuation=true cacheable=true)
     public static object addressResponse(List <string> labels, object state)
    {
        try
        {
            HttpResponse responseAddress = Continuation.getResponse(labels[0]);
            String AddressResponseBody = responseAddress.getBody();
            EAS_AddressStandardizationRespone_H_HUM oResponseValidateAddress;
            AddressValidationHelper_DTO_HUM addressDto = (AddressValidationHelper_DTO_HUM)state;
            Continuation conresObj = null;
              if (!AddressResponseBody.containsIgnoreCase(VOB_FAULT_CODE) && responseAddress != null && ((responseAddress.getStatusCode() == HTTP_OK_HUM || responseAddress.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest()))

            {
                oResponseValidateAddress = (EAS_AddressStandardizationRespone_H_HUM) System.JSON.deserialize(AddressResponseBody, EAS_AddressStandardizationRespone_H_HUM.class);
                if(oResponseValidateAddress != null && oResponseValidateAddress.StandardizeAddressResponse != null && string.isNotBlank(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) &&'Good'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator))
                {
                     addressDto.addressValidatedFlag = 'true';
                     addressDto.overrideReasonCode = '0';
                     return prepareEditGetMember(addressDto.enterpriseID,'','','','','',addressDto.networkID,addressDto,'');
                }
                else if(oResponseValidateAddress != null && oResponseValidateAddress.StandardizeAddressResponse != null && string.isNotBlank(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) && ('Bad'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator) || 'Change'.equalsIgnoreCase(oResponseValidateAddress.StandardizeAddressResponse.ChangeIndicator)))
                {
                     return HUMADDRESSERROR+'_'+1;
                }
                else return HUMADDRESSERROR+'_'+3;
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseAddress,'Pharmacy_LS_HUM', 'addressResponse', addressDto.sRecordId);
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'addressResponse');
            return null;
        }
    }

	@AuraEnabled( continuation=true)
    public static continuation invokeRightSourceService(String requestBody, String type)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails;
            if(type == 'GetMember'){
                serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_GetMember');
            }
	        // Create continuation with a timeout
	        integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
	        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
	        con.continuationMethod = 'retriveRightSourceData';
	        HttpRequest httpEditscriptautorefilReqObj = builtHttpRequest(serviceDetails);
	        httpEditscriptautorefilReqObj.setBody(requestBody);
	        con.addHttpRequest(httpEditscriptautorefilReqObj);
	        return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'HPIEAutoRefill');
            throw new AuraHandledException(ex.getMessage());
        }
    }

	@AuraEnabled
    public static object retriveRightSourceData(List <string> labels, object State)
    {
        string response ;
        try
        {
            HttpResponse sResponse = Continuation.getResponse(labels[0]);
            response = sResponse.getbody();
            CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('4134646');
            boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
          	if ((sResponse != null && !sResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE) &&
            (sResponse.getStatusCode() == HTTP_OK_HUM || sResponse.getStatusCode() == HTTP_ACCEPTED_HUM)
            ) || Test.isRunningTest())
         	{
                if(switchvalue){
                    throw new ServiceDownException('SERVICEERROR');
                }else{
                    if(sResponse.getbody() == '' && Test.isRunningTest())  throw new ServiceDownException('SERVICEERROR'+sResponse.getBody());
                 	return sResponse.getbody();
                }
         	}
         	else{
             	//throw New ServiceDownException('SERVICEERROR'+sResponse.getBody());
                 return sResponse.getbody();
			 }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'retriveHPIEData');
            return ex;
        }
    }
	
	@AuraEnabled
    public static continuation sendRequestIsMemElig100DayPrescrip(string request){
        try {
            HUM_Webservice_Callout__c webServiceCallOut = HUM_Webservice_Callout__c.getValues('MemElig100DayPrescrip');
            integer timeout = Integer.valueof(webServiceCallOut.Timeout__c) / 1000;
            Continuation con = new Continuation(timeout);
            HttpRequest httpIsMemElig100DayPrescrip = builtHttpRequest(webServiceCallOut);
            httpIsMemElig100DayPrescrip.setBody(request);
            con.addHttpRequest(httpIsMemElig100DayPrescrip);
            con.ContinuationMethod = 'process100DayResponse';
            return con;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_LS_HUM', 'sendRequestIsMemElig100DayPrescrip');
            throw new AuraHandledException(e.getMessage());
        }
    }

    public static object parseIsMemElig100DayServiceResponse(List <string> labels, Object state)
    {
        try
        {     
			 HttpResponse oResponse = Continuation.getResponse(labels[0]);       
			 if(oResponse != null && ((oResponse.getStatusCode() == HTTP_OK_HUM || oResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
			 {
				return oResponse.getBody();          
			 }else{
				throw new HUMCustomException('callout Exception');
			 }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Pharmacy_LS_HUM', 'parseIsMemElig100DayServiceResponse');
            throw new AuraHandledException(ex.getMessage());
        }
    }
}