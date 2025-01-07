/*******************************************************************************************************************************
Apex Class Name : Pharmacy_LD_HUM
Version         : 1.0
Created On      : 07/19/2021
Function        : This class is used for performing the Lightning Humana Pharmacy related work
Test Class      : Pharmacy_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Pallavi Shewale                                     07/19/2021                Calling GetMemberDetailsService
*    2.0           Swapnali Sonawane 		                           09/02/2021                UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
*    3.0		   Nirmal Garg										   10/05/2021			     US2504099 - Changes. Added edit member logic
*	 4.0		   Abhishek Mangutkar								   10/12/2021				 US - 2528482 - T1PRJ0078574 - MF 11812 - HP Lightning- 'Prescriptions' - Filters
*	 5.0		   Pallavi Shewale								       10/28/2021				 US - 2664846 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order Summary - Cards
*    6.0           Swapnali Sonawane                                   11/14/2021                US - 2766098  T1PRJ0078574 - MF 11811 - HP Lightning- Add 'Prescriptions' - Auto refill
*	 7.0		   Monali Jagtap									   07/17/2023 				 USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*******************************************************************************************************************************************************************/

public with sharing class Pharmacy_LD_HUM{
    Public Static final Integer PHARMACY_STARTMONTH_HUM = -3;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202;
    Public Static final String HUM_MEMBER_NOT_FOUND = 'Member not found';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT'; 
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM ='Member consent given on ';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM = ' est. Consent will expire in 365 days (Note EPOST WILL HANDLE CONSENT EXPIRATION)';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at ';
    Public Static final String PHARMACY_MEMBERCONSENT_ENDDATE_HUM = '01/01/1970';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM ='Member changed to Request Consent on Each Order on ';
    Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM = ' est.';
    public static String customerValue = '';
    public static PharmacyGetMember_DTO_HUM objPharGetMemDTO = null;
    public static PharmacyGetMember_DTO_HUM objPharGetMemDempgraphicDTO = null;
    public static PharmacyGetMember_Demographic_DTO_HUM objPharDemographicDetails;
    public static List<PharmacyOMSLogNotes_DTO_HUM> objPharOMSDetails = null;
    public static Object memberDemographics = null;
    public static HttpResponse responseDemographic;
    public static CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch;
    public static RightSourceGetMemberResponse_DTO_HUM oResponseDemographic;
    
    /**
    * Returns the formatted string by parsing the date 
    *
    * @param   Date 
    * @return  Formatted String Date 
    */
    private static String formatRequestDate(Date requestDate)
    {
        return requestDate.Month() + '/' + requestDate .Day() + '/' + requestDate.Year();    
    }
    
    /**
    * Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice .
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
    */
    public static RightSourceGetMemberRequest_DTO_HUM RequestStructureGetMemberResponse(String memID, string userNetworkID)
    {
        RightSourceGetMemberRequest_DTO_HUM oRequest = new RightSourceGetMemberRequest_DTO_HUM();
        RightSourceGetMemberRequest_DTO_HUM.MemberRequest memRequest = new RightSourceGetMemberRequest_DTO_HUM.MemberRequest();
        memRequest.EnterprisePersonID = memID;
        memRequest.CustomerServiceId = userNetworkID;
        memRequest.IncludeActiveLogNotes ='true';
        memRequest.IncludeActiveAddresses = 'true';
        memRequest.IncludeInactiveAddresses = 'true';
        memRequest.IncludeFinances = 'true';
        memRequest.IncludeAccountAdjustments = 'false';
        memRequest.IncludeConsentStatus = 'true';
        memRequest.IncludeActiveCreditCards = 'true';
        memRequest.IncludeInactiveCreditCards = 'true';
        memRequest.IncludeStaleData = 'true';
        memRequest.includeCommunicationPreferences = 'true';
        memRequest.LogNoteStartDate = formatRequestDate(System.today().addMonths(PHARMACY_STARTMONTH_HUM));
        memRequest.LogNoteEndDate = formatRequestDate(System.today());
        memRequest.returnDuplicateCustomers = 'true';//"r" has be lower case as DP is mapped to lower.
        memRequest.IncludeSpecialty = 'false';
        oRequest.GetMemberRequest = memRequest;
        return oRequest;   
    }
    
    
    /**
    * Returns responses of continuation object
    * <p>
    * parseRSGetMemberResponse Method to parse the service response
    *
    * @param  object state,List<string> labels .
    * @return Object
    */
    public static Object parseRSGetMemberResponse(List <string> labels, Object State)
    {        
        responseDemographic = Continuation.getResponse(labels[0]);
        objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('3939434_Switch');
        boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
        if(switchvalue){
            objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(null,null,null,null,null,null,null,true,null);
        }
        else{
            String DemographyresposeBody =(responseDemographic != null && !responseDemographic.getbody().containsIgnoreCase(VOB_FAULT_CODE) && ((responseDemographic.getStatusCode() == HTTP_OK || responseDemographic.getStatusCode() == HTTP_ACCEPTED) || Test.isRunningTest()))?responseDemographic.getBody():Pharmacy_H_HUM.logHpFaultError(responseDemographic,'Pharmacy_LD_HUM', 'parseRSGetMemberResponse', State != null ? String.valueof(State) : '');
            oResponseDemographic = (RightSourceGetMemberResponse_DTO_HUM) System.JSON.deserialize(DemographyresposeBody, RightSourceGetMemberResponse_DTO_HUM.class);            
            customerValue = (null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND && oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue != null)?(oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue):null ;
            memberDemographics =(null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND && oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue != null)? (oResponseDemographic.GetMemberReponse.Customers.Customer[0]):null;
            RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail objCustomerInfo = (null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND && oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue != null)?prepareCustomerKey(oResponseDemographic):null;               
            objPharDemographicDetails = (null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND && oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue != null)?Pharmacy_D_HUM.prepareDemographicDetails(oResponseDemographic):null;
            objPharOMSDetails =(null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND && oResponseDemographic.GetMemberReponse.Customers.Customer[0].AccountNumber.CustomerValue != null)? Pharmacy_D_HUM.OMSLogNotesResponse(oResponseDemographic):null;
            //Adding parameter to hide error message on UI for member not found
            objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(objPharDemographicDetails,getAllergiesAndHCondiotionFrmResponse(oResponseDemographic),objPharOMSDetails,null,getPayerName( oResponseDemographic), ((oResponseDemographic != null && oResponseDemographic.GetMemberReponse != null && oResponseDemographic.GetMemberReponse.DuplicateCustomers != null) ? oResponseDemographic.GetMemberReponse.DuplicateCustomers : null),null,false,objCustomerInfo);
            objPharGetMemDempgraphicDTO  = new PharmacyGetMember_DTO_HUM(objPharDemographicDetails,null,null,null,null,null,null,false,null);            
            //Add error message to show on UI for member not found message            
            objPharGetMemDTO = (null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription == HUM_MEMBER_NOT_FOUND)?new PharmacyGetMember_DTO_HUM(null,null,null,null,null,null,null,true,null):objPharGetMemDTO;
        }        
        return JSON.serialize(objPharGetMemDTO);
    }
    
    
    public static RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail prepareCustomerKey(RightSourceGetMemberResponse_DTO_HUM oResponse){
        boolean foundrecord=false;
        RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail sCustomerInfo;        
        sCustomerInfo=prepareCustKey_Sec(oResponse,sCustomerInfo,foundRecord);        
        return sCustomerInfo;
    }
    
    
    public static  RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail prepareCustKey_Sec(RightSourceGetMemberResponse_DTO_HUM oResponse,RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail sCustomerInfo,boolean foundrecord){
        RightSourceGetMemberResponse_DTO_HUM.MemberReponse memberRes =(oResponse != null && oResponse.GetMemberReponse != null)? oResponse.GetMemberReponse:null;
        RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj =(memberRes.Customers != null)? memberRes.Customers:null;
        if(custObj.Customer != null && custObj.Customer.size() > 0)
        {
            for(Integer i=0; i<custObj.Customer.size(); i++)
            {
                sCustomerInfo=checkRecord(custObj,i,foundRecord,sCustomerInfo);
                if(foundRecord) break;             
            }          
        }
        return sCustomerInfo;
    }
    
    
    public static RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail checkRecord( RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj,Integer i, boolean foundrecord,RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail sCustomerInfo){
        RightSourceGetMemberResponse_DTO_HUM.CustomerKeysDetail objCustKeys =(custObj.Customer[i].CustomerIdentity.CustomerKeys != null)? (custObj.Customer[i].CustomerIdentity.CustomerKeys):null;
        for(Integer j=0; j<objCustKeys.CustomerKey.size();j++)
        {
            RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail objCustomerKeyInfo=(objCustKeys.CustomerKey != null)?objCustKeys.CustomerKey[j]:null;
            if(objCustomerKeyInfo != null)
            {
                foundRecord=(objCustomerKeyInfo.CustomerType != null && objCustomerKeyInfo.CustomerType =='OC' && objCustomerKeyInfo.CustomerTypeLiteral=='OTC CARD')?true:false;
                sCustomerInfo =(objCustomerKeyInfo.CustomerType != null && objCustomerKeyInfo.CustomerType =='OC' && objCustomerKeyInfo.CustomerTypeLiteral=='OTC CARD')? objCustomerKeyInfo:null;
            }            
            if(foundRecord) break;
        }
        return sCustomerInfo;        
    }
    
    public static PharmacyAllergyConditions_DTO_HUM getAllergiesAndHCondiotionFrmResponse(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse)
    {   
        PharmacyAllergyConditions_DTO_HUM objAllergiesHConDTO= null;        
        objAllergiesHConDTO=getAllergyConditions_Sec(objMemberRseponse,objAllergiesHConDTO);        
        return objAllergiesHConDTO;
    }
    
    
    public static PharmacyAllergyConditions_DTO_HUM getAllergyConditions_Sec(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse,PharmacyAllergyConditions_DTO_HUM objAllergiesHConDTO)
    {
        if(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse && null !=  objMemberRseponse.GetMemberReponse.Customers && null != objMemberRseponse.GetMemberReponse.Customers.Customer)
        {
            List<String> lstAllergies = new List<String>();
            List<String> lstHelConditions = new List<String>();
            for(RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer : objMemberRseponse.GetMemberReponse.Customers.Customer)
            {   
                lstAllergies=getAllergiesList(objCustomer,lstAllergies);
                lstHelConditions= getHelConditionsList(objCustomer,lstHelConditions);
            }
            lstAllergies.sort();
            lstHelConditions.sort();
            objAllergiesHConDTO = (!lstHelConditions.isEmpty() || !lstAllergies.isEmpty())?new PharmacyAllergyConditions_DTO_HUM(lstAllergies,lstHelConditions):null;
        }
        return objAllergiesHConDTO;
    }
    
    
    public static  List<String> getHelConditionsList(RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer, List<String> lstHelConditions){
        objCustomer.HealthConditions=(null != objCustomer.HealthConditions && null!= objCustomer.HealthConditions.HealthCondition)?objCustomer.HealthConditions:null;
        for(RightSourceGetMemberResponse_DTO_HUM.HealthConditionDetail objHelCondition : objCustomer.HealthConditions.HealthCondition)
        {
            if(null != objHelCondition && String.isNotBlank(objHelCondition.HealthDescription))
            {
                lstHelConditions.add(objHelCondition.HealthDescription);
            }
        }
        return lstHelConditions;               
    }
    
    
    public static List<String> getAllergiesList(RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer,List<String> lstAllergies){
        objCustomer.Allergies=(null != objCustomer.Allergies && null!= objCustomer.Allergies.Allergy)?objCustomer.Allergies:null;
        for(RightSourceGetMemberResponse_DTO_HUM.AllergyDetail objAllergy : objCustomer.Allergies.Allergy)
        {
            if(null != objAllergy && String.isNotBlank(objAllergy.HealthDescription))
            {
                lstAllergies.add(objAllergy.HealthDescription);
            }
        }
        return lstAllergies;
    }
    
    
    /**
    * Returns PharmacyPayer_DTO_HUM object for populating Payer Name in Order details.
    * 
    *
    * @param RightSourceGetMemberResponse_DTO_HUM   
    * @return  PharmacyPayer_DTO_HUM 
    */
    public static PharmacyPayer_DTO_HUM getPayerName(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse)
    {   
        PharmacyPayer_DTO_HUM objPayer = null;
        objPayer= getPayer_Sec(objPayer,objMemberRseponse);
        return objPayer;
    }
    
    public static PharmacyPayer_DTO_HUM  getPayer_Sec(PharmacyPayer_DTO_HUM objPayer,RightSourceGetMemberResponse_DTO_HUM objMemberRseponse){
        RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer =(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse && null !=  objMemberRseponse.GetMemberReponse.Customers && null != objMemberRseponse.GetMemberReponse.Customers.Customer && !objMemberRseponse.GetMemberReponse.Customers.Customer.isEmpty() )? objMemberRseponse.GetMemberReponse.Customers.Customer[0]:null;
        RightSourceGetMemberResponse_DTO_HUM.PlanDetail custPlan =(null != objCustomer && null != objCustomer.Plans && null != objCustomer.Plans.plan && !objCustomer.Plans.plan.isEmpty())? objCustomer.Plans.Plan[0]:null;
        objPayer=(null!= custPlan && null != custPlan.Payer )? new PharmacyPayer_DTO_HUM(  custPlan.Payer):null;
        return objPayer;
    }
    
    /*
    *  This method  is used to return details of a Member Plan
    *  @name   getMemberName
    *  @param  ID - Record ID of Member Plan for which claims are to be displayed
    *  @return List<MemberPlan> -  List of MemberPlan records
    *  @throws getMemberName
    */
    public static List<MemberPlan> getMemberName(String sRecId)
    {   
        List<MemberPlan> lstpolmem = new List<MemberPlan>();
        if(sRecId != null && String.isNotBlank(sRecId))
        {
            lstpolmem = [SELECT Id, Name, Member.Enterprise_ID__c,Plan.Client_Number__c,Medicaid_Id__c, GroupNumber, Policy_Platform__c, Member.Name, Plan.Product__r.Sold_Product_Key_Value__c, Plan.Payer.Source_Customer_Key__c, Product_Type__c, Issue_State__c, Member_Dependent_Code__c,Member_Id_base__c ,effectiveFrom, 
                         Product__c,Product_Description__c,ETL_Record_Deleted__c,Plan.Selling_Market_Number__c, Plan.iab_description__c ,Plan.Name ,Display_Group_Name__c , Plan.Benefit_Coverage__c , Product_Type_Code__c ,Exchange__c , EHB_Term_Date__c ,SubscriberPlanId__c , Plan.SourceSystem , RelationshipToSubscriber ,EffectiveTo,PlanId, Member_Coverage_Status__c ,Plan.Payer.Source_Platform_Code__c , ASO__c, Metallic_Tier__c , Exchange_Type__c , Plan.Source_Cust_Cov_Key__c,SubscriberPlanId__r.Name,Policy__c
                         FROM MemberPlan WHERE ID=:sRecId WITH USER_MODE];
        }
        return lstpolmem;
    } 
    
    /*
    *  This method  is used to return details of a policy PLAN
    *  @name   getPolicyPlanVendor
    *  @param  Policy Member 
    *  @return List<PurchaserPlan> -  List of Policy pLAN records
    *  @throws getPolicyPlanVendor
    */
    public static List<PurchaserPlan> getPurchaserPlanOtherInsurance(set<Id> planIds)
    { 
        return [SELECT Id, Name, EffectiveFrom, EffectiveTo, Policy__c, 
                Product__c,Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c 
                FROM PurchaserPlan Where Id IN :planIds WITH USER_MODE];
    } 
    
    /* Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice for OMS Log Notes when Start Date and End Date are selected.
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
    */
    public static RightSourceGetMemberRequest_DTO_HUM requestStructureGetMemberResponse(String memID, String startDate, String endDate, string networkId)
    {
        RightSourceGetMemberRequest_DTO_HUM oRequest = new RightSourceGetMemberRequest_DTO_HUM();
        RightSourceGetMemberRequest_DTO_HUM.MemberRequest  memberRequest = new RightSourceGetMemberRequest_DTO_HUM.MemberRequest ();
        memberRequest.EnterprisePersonID = memID;
        memberRequest.CustomerServiceId = networkId;
        memberRequest.LogNoteStartDate = startDate; 
        memberRequest.LogNoteEndDate = endDate;
        memberRequest.IncludeActiveLogNotes ='true';
        memberRequest.IncludeActiveAddresses = 'false';
        memberRequest.IncludeInactiveAddresses = 'false';
        memberRequest.IncludeFinances = 'true';
        memberRequest.IncludeAccountAdjustments = 'false';
        memberRequest.IncludeConsentStatus = 'true';
        memberRequest.IncludeActiveCreditCards = 'true';
        memberRequest.IncludeInactiveCreditCards = 'false';
        memberRequest.IncludeStaleData = 'true';
        memberRequest.includeCommunicationPreferences = 'true';
        oRequest.GetMemberRequest = memberRequest;
        return oRequest;
    }
    
    /* created for AccountDetails component
    * Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice for OMS Log Notes when Start Date and End Date are selected.
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
    */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetEditMemberResponse(string enterpriseId, string phone, string AltPhone, string Email, string captype,string networkId, AddressValidationHelper_DTO_HUM addressDto, String IsDeclined)
    {
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();        
        RightSourceEditMemberRequest_DTO_HUM.communicationPreferences ComPrefObjs = new RightSourceEditMemberRequest_DTO_HUM.communicationPreferences ();        
        RightSourceEditMemberRequest_DTO_HUM.communicationPreference ComPrefObj = new RightSourceEditMemberRequest_DTO_HUM.communicationPreference ();
        RightSourceEditMemberRequest_DTO_HUM.addressDetails addressesObj = new RightSourceEditMemberRequest_DTO_HUM.addressDetails();
        RightSourceEditMemberRequest_DTO_HUM.addressDetail addressObj = new RightSourceEditMemberRequest_DTO_HUM.addressDetail();
        if(addressDto != null && addressDto.addressReq != null)
        {
            addressObj.addressLine1 = addressDto.addressReq.AddressLine1;
            addressObj.addressLine2 = addressDto.addressReq.AddressLine2;
            addressObj.city = addressDto.addressReq.city;
            addressObj.stateCode = addressDto.addressReq.StateCode;
            addressObj.zipCode = addressDto.addressReq.ZipCode;
            addressObj.addressType = addressDto.addresstype;
            addressObj.addressKey ='-1';
            addressObj.overrideReasonCode =(string.isNotBlank(addressDto.overrideReasonCode))? addressDto.overrideReasonCode:'';            
            addressObj.isActive = (string.isNotBlank(addressDto.statusValue) && addressDto.statusValue == 'Active' ) ?  'true' :'false';
            addressObj.uspsValidate = addressDto.addressValidatedFlag;
            addressesObj.address = new List<RightSourceEditMemberRequest_DTO_HUM.addressDetail>();
            addressesObj.address.add(addressObj);
            memDetailObj.addresses = addressesObj;
            getEditMembReqObj.CustomerServiceId = addressDto.networkID;
        }        
        else
        {
            RightSourceEditMemberRequest_DTO_HUM.customerDetail customerObj = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
            RightSourceEditMemberRequest_DTO_HUM.phoneDetails phDetails = new RightSourceEditMemberRequest_DTO_HUM.phoneDetails();
            RightSourceEditMemberRequest_DTO_HUM.customerPreference custPrefObj = new RightSourceEditMemberRequest_DTO_HUM.customerPreference();
            phone =(string.isNotBlank(phone))?phone:'';
            AltPhone = (string.isNotBlank(AltPhone)) ? AltPhone : '';
            getEditMembReqObj.CustomerServiceId = networkId;            
            custPrefObj.capType = captype;            
            customerObj.email = Email;
            memDetailObj.customerDetail = customerObj;
            memDetailObj.customerPreference = custPrefObj;
            RightSourceEditMemberRequest_DTO_HUM.phoneDetail phDetail; 
            phDetails.phone = new List<RightSourceEditMemberRequest_DTO_HUM.phoneDetail>();
            phDetail = new RightSourceEditMemberRequest_DTO_HUM.phoneDetail();
            phDetail.phoneType = 'PP';
            phDetail.phoneValue = phone;
            phDetails.phone.add(phDetail);
            phDetail = new RightSourceEditMemberRequest_DTO_HUM.phoneDetail();
            phDetail.phoneType = 'CN';
            phDetail.phoneValue = AltPhone;
            phDetails.phone.add(phDetail);
            memDetailObj.phones = phDetails;
        }
        
        if(IsDeclined != ''){
            ComPrefObj.commPrefType ='EMDCL';
            
            ComPrefObj.commPrefValue = (IsDeclined == 'true')?'true':'false';
            ComPrefObjs.communicationPreference = new List<RightSourceEditMemberRequest_DTO_HUM.communicationPreference>();
            ComPrefObjs.communicationPreference.add(ComPrefObj);
            memDetailObj.communicationPreferences = ComPrefObjs;
        }
        memDetailObj.EnterprisePersonID = enterpriseId;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;
        return oRequest;
    }
    
    /**
    * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice for OMS Log Notes when Start Date and End Date are selected.
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
    */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetcaptypeResponse(String sMemId,string captype, string networkID)
    {
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.customerPreference custPrefObj = new RightSourceEditMemberRequest_DTO_HUM.customerPreference();
        custPrefObj.capType = captype;
        memDetailObj.customerPreference = custPrefObj;
        memDetailObj.EnterprisePersonID = sMemId;
        getEditMembReqObj.CustomerServiceId = networkID;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;        
        return oRequest;
    }
    
    /**
    * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for RightSourceEditMemberRequest to update Consent Begin date.
    *
    * @param    
    * @return  RightSourceEditMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceEditMemberRequest_DTO_HUM 
    */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetEditMemberRequestForConsentBeginDate(String sMemId, string networkId)
    {
        String userNetworkId = networkId;
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.customerDetail custDetail = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetail logDetail = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetails logNoteDetails = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetails();
        List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail> logDetailList = new  List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail>();
        custDetail.consentBeginDate = formatRequestDate(System.today());
        memDetailObj.customerDetail = custDetail;
        memDetailObj.EnterprisePersonID = sMemId;
        logDetail.logNoteCode = PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM;
        logDetail.logNoteKey ='-1';
        logDetail.logNoteMessage = PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM + formatRequestDate(System.today()) + PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM + getCurrentTime() + PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM ;
        if(userNetworkId != null)
            logDetail.logNoteUser = userNetworkId;
        logDetailList.add(logDetail);
        logNoteDetails.logNote = logDetailList;
        memDetailObj.logNotes = logNoteDetails;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.CustomerServiceId = userNetworkId;
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;        
        return oRequest;
    }
    
    /**
    * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for RightSourceEditMemberRequest to update Consent End date.
    *
    * @param    
    * @return  RightSourceEditMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceEditMemberRequest_DTO_HUM 
    */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetEditMemberRequestForConsentEndDate(String sMemId, string networkID)
    {        
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.customerDetail custDetail = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetail logDetail = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetails logNoteDetails = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetails();
        List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail> logDetailList = new  List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail>();
        custDetail.consentEndDate = PHARMACY_MEMBERCONSENT_ENDDATE_HUM ;
        memDetailObj.customerDetail = custDetail;
        memDetailObj.EnterprisePersonID = sMemId;
        logDetail.logNoteCode = PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM;
        logDetail.logNoteKey ='-1';
        logDetail.logNoteMessage = PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM + formatRequestDate(System.today()) + PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM + getCurrentTime() + PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM;
        if(networkID != null)
            logDetail.logNoteUser = networkID;
        logDetailList.add(logDetail);
        logNoteDetails.logNote = logDetailList;
        memDetailObj.logNotes = logNoteDetails;
        getEditMembReqObj.CustomerServiceId = networkID;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;        
        return oRequest;
    }
    
    /**
    * Returns the formatted current time 
    *
    * @param   None
    * @return  Formatted current time 
    */
    private static String getCurrentTime()
    {
        Datetime currentDateTime = Datetime.now();
        String currentTime = currentDateTime.format('h:mm a');
        return currentTime;    
    }
    
    /**
    * Returns RightSourceGetScriptRequest_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetScriptResponseservice .
    *
    * @param    
    * @return  RightSourceGetScriptRequest_DTO_HUM request structure of the wrapper class RightSourceGetScriptRequest_DTO_HUM 
    */
    public static RightSourceGetScriptRequest_DTO_HUM requestStructureGetScriptResponse(String memID,String scriptKey, String startDate, String endDate, String networkID)
    {
        RightSourceGetScriptRequest_DTO_HUM oRequest = new RightSourceGetScriptRequest_DTO_HUM ();
        RightSourceGetScriptRequest_DTO_HUM.ScriptsRequest scriptRequest = new RightSourceGetScriptRequest_DTO_HUM.ScriptsRequest();
        scriptRequest.EnterprisePersonID = memID;
        scriptRequest.ScriptStartDate = startDate ;
        scriptRequest.ScriptEndDate = endDate ;
        scriptRequest.ScriptKey = scriptKey;
        scriptRequest.IncludeStaleData = 'true';
        scriptRequest.CustomerServiceId = networkID;
        oRequest.GetScriptsRequest= scriptRequest ;
        return oRequest;
    }
    
    /**
    * Returns List<PharmacyMemberPrescription_DTO_HUM> object that can then be utilized for parsing the webservice request.
    * This method is to parse the request Structure for GetScriptResponseservice .
    *
    * @param    
    * @return  List<PharmacyMemberPrescription_DTO_HUM> response structure of the wrapper class RightSourceGetScriptResponse_DTO_HUM 
    */
    public static List<PharmacyMemberPrescription_DTO_HUM> preparememerPrescriptionResponse(RightSourceGetScriptResponse_DTO_HUM oResponseGetMemberPrescription)
    {
        list <PharmacyMemberPrescription_DTO_HUM> wrapperObjList;
        PharmacyMemberPrescription_DTO_HUM wrapperObj;
        RightSourceGetScriptResponse_DTO_HUM.ScriptsResponse prescriptionRes = (oResponseGetMemberPrescription != null && oResponseGetMemberPrescription.GetScriptsResponse != null) ? oResponseGetMemberPrescription.GetScriptsResponse:null;
        RightSourceGetScriptResponse_DTO_HUM.ScriptsDetail prescriptionObj =(oResponseGetMemberPrescription != null && oResponseGetMemberPrescription.GetScriptsResponse != null && prescriptionRes.Scripts != null)? prescriptionRes.Scripts:null ;
                if (oResponseGetMemberPrescription != null && oResponseGetMemberPrescription.GetScriptsResponse != null && prescriptionRes.Scripts != null && prescriptionObj.Script != null && prescriptionObj.Script.size() > 0)
                {
                    wrapperObjList =  new list <PharmacyMemberPrescription_DTO_HUM> ();  
                    for (RightSourceGetScriptResponse_DTO_HUM.ScriptDetail obj: prescriptionObj.Script)
                    {
                        wrapperObj = new PharmacyMemberPrescription_DTO_HUM();
                        wrapperObj.staleData = prescriptionRes.StaleData;
                        wrapperObj.Consent = (string.isNotBlank(obj.ConsentStatus)) ? obj.ConsentStatus:'';
                        
                         wrapperObj.RXNumber =(string.isNotBlank(obj.ScriptKey)) ? obj.ScriptKey:'';
                         wrapperObj.Status =(string.isNotBlank(obj.ScriptStatus)) ? obj.ScriptStatus:'';
                         wrapperObj.AutoRefillEnrolled =(string.isNotBlank(obj.IsAutoRefill) && obj.IsAutoRefill =='true')? 'Yes':'No';
                          
                         wrapperObj.AutoRefillEligible =(string.isNotBlank(obj.AutoRefillEligible) && obj.AutoRefillEligible =='true') ?'Yes':'No';
                         wrapperObj.ControlledRX =(string.isNotBlank(obj.DrugType) && obj.DrugType == 'CS') ? 'Yes':'No';
                        
                         wrapperObj.Quantity =(string.isNotBlank(obj.Quantity))? obj.Quantity:'';
                         wrapperObj.RefillsRemaining =(string.isNotBlank(obj.RefillsRemaining ))? obj.RefillsRemaining:'' ;
                         wrapperObj.DaysSupply =(string.isNotBlank(obj.DaysSupply)) ? obj.DaysSupply:'';
                         wrapperObj.LastFillDate =(string.isNotBlank(obj.LastFillDate)) ? obj.LastFillDate:'';
                         wrapperObj.NextFillDate =(string.isNotBlank(obj.NextFillDate)) ? obj.NextFillDate:'';
                         wrapperObj.NextFillMinDate =(string.isNotBlank(obj.NextFillMinDate)) ? obj.NextFillMinDate:'';
                         wrapperObj.AutoRefillEligible =(string.isNotBlank(obj.AutoRefillEligible)) ? obj.AutoRefillEligible:'';
                         wrapperObj.ExpirationDate =(string.isNotBlank(obj.ScriptExpirationDate)) ? obj.ScriptExpirationDate:'';
                        RightSourceGetScriptResponse_DTO_HUM.ItemsDetail itemsObj = obj.Items;
                        if(itemsObj.Item != null && itemsObj.Item.size() > 0)
                        {
                           getDrugs(wrapperObj,itemsObj);
                        }
                        if(obj.physician != null )
                        {
                          RightSourceGetScriptResponse_DTO_HUM.PhysicianDetail phyObj = obj.physician;
                          String prescriberInformation ='';
                          string phone = '';
                          string fax = '';
                          prescriberInformation =(string.isNotBlank(phyObj.FirstName))? phyObj.FirstName:'';
                        
                          prescriberInformation =(string.isNotBlank(phyObj.LastName) && string.isNotBlank(prescriberInformation)) ? (prescriberInformation + ' ' + phyObj.LastName) : (prescriberInformation + phyObj.LastName);
                         
                          wrapperObj.PrescriberInformation = prescriberInformation ;
                          RightSourceGetScriptResponse_DTO_HUM.PhonesDetail phonesObj = phyObj.Phones ;
                          if(phonesObj.Phone != null && phonesObj.Phone.size() > 0)
                            {
                              getPhoneFax(phone,fax,wrapperObj,phonesObj);
                             }
                        }
                        wrapperObjList.add(wrapperObj);
                    }
                }
                else if((oResponseGetMemberPrescription != null && oResponseGetMemberPrescription.GetScriptsResponse != null) && (prescriptionRes.Scripts != null) && prescriptionObj.Script != null && prescriptionObj.Script.size() == 0) 
                {
                    wrapperObjList =  new list <PharmacyMemberPrescription_DTO_HUM> (); 
                }
        
        return wrapperObjList;
    }
    public static void getPhoneFax(string phone,string fax,PharmacyMemberPrescription_DTO_HUM wrapperObj,RightSourceGetScriptResponse_DTO_HUM.PhonesDetail phonesObj){
         for(RightSourceGetScriptResponse_DTO_HUM.PhoneDetail phoneObj : phonesObj.Phone)
            {
              if(String.isNotBlank(phoneObj.PhoneType) && phoneObj.PhoneType == 'FN' && fax == '' && string.isNotBlank(phoneObj.PhoneValue))
                {
                      wrapperObj.Fax = phoneObj.PhoneValue;
                      fax = phoneObj.PhoneValue;
                 }
             if(String.isNotBlank(phoneObj.PhoneType) && phoneObj.PhoneType == 'WP' && phone == '' && string.isNotBlank(phoneObj.PhoneValue))
                {
                       wrapperObj.Phone = phoneObj.PhoneValue;
                       phone = phoneObj.PhoneValue;
                }
            }
    }
    public static void getDrugs(PharmacyMemberPrescription_DTO_HUM wrapperObj, RightSourceGetScriptResponse_DTO_HUM.ItemsDetail itemsObj){
        for(RightSourceGetScriptResponse_DTO_HUM.ItemDetail itemObj : itemsObj.Item)
        {
             if(String.isNotBlank(itemObj.ItemType) && itemObj.ItemType == 'D')
                {
                  wrapperObj.DispensedDrug =(string.isNotBlank(itemObj.ItemLabel))? itemObj.ItemLabel:'';
                        }
                if(String.isNotBlank(itemObj.ItemType) && itemObj.ItemType == 'W')
                {
                  wrapperObj.WrittenDrug =(string.isNotBlank(itemObj.ItemLabel))? itemObj.ItemLabel:'';
                }
        }
    } 
    /**
    * Returns RightSourceGetOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice .
    *
    * @param    
    * @return  RightSourceGetOrderRequest_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
    */
    public static RightSourceGetOrderRequest_DTO_HUM requestStructureGetOrderResponse(String memID, String startDate, String endDate, string networkId)
    {
        RightSourceGetOrderRequest_DTO_HUM oRequest = new RightSourceGetOrderRequest_DTO_HUM();
        RightSourceGetOrderRequest_DTO_HUM.OrdersRequest orderRequest = new RightSourceGetOrderRequest_DTO_HUM.OrdersRequest();
        orderRequest.EnterprisePersonID = memID;
        orderRequest.OrderStartDate = startDate; 
        orderRequest.IncludeStaleData ='true';
        orderRequest.OrderEndDate = endDate;
        orderRequest.CustomerServiceId = networkId;
        oRequest.GetOrdersRequest = orderRequest;
        return oRequest;
    }
    
    /**
    * Returns PharmacyOrderSummary_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetOrderResponseservice .
    *
    * @param    
    * @return  PharmacyOrderSummary_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
    */
    public static PharmacyOrderSummary_DTO_HUM prepareOrderSummaryResponse(RightSourceGetOrderResponse_DTO_HUM oResponseGetOrder)
    {
        PharmacyOrderSummary_DTO_HUM orderObject ; 
        list < PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  > wrapperObjList =  new list < PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM  > ();
        PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM wrapperObj;
        if (oResponseGetOrder != null && oResponseGetOrder.GetOrdersReponse != null)
        {
            orderObject = new PharmacyOrderSummary_DTO_HUM(); 
            RightSourceGetOrderResponse_DTO_HUM.OrdersReponse orderRes = oResponseGetOrder.GetOrdersReponse;
            RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj =(orderRes.Orders != null)?orderRes.Orders:null;
            orderObject.staleData =(string.isNotBlank(orderRes.StaleData))? orderRes.StaleData:'';
            if (orderObj.Order != null && orderObj.Order.size() > 0)
            {
                for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
                {
                    wrapperObj = new PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM();
                    wrapperObj.OrderNumber = (string.isNotBlank(obj.OrderNumber))?obj.OrderNumber:'';
                    wrapperObj.OrderCreationDate = (string.isNotBlank(obj.OrderCreationDate))?obj.OrderCreationDate:'';
                    wrapperObj.OrderReleaseDate = (string.isNotBlank(obj.OrderReleaseDate) && obj.OrderCreationDate != obj.OrderReleaseDate)?obj.OrderReleaseDate:'';
                    wrapperObj.OrderSource =(string.isNotBlank(obj.OrderSource)) ?obj.OrderSource:'';
                    wrapperObj.QueueName = (string.isNotBlank(obj.QueueName))?obj.QueueName:'';
                    wrapperObj.OrderTotalAmount =(string.isNotBlank(obj.OrderTotalAmount))?obj.OrderTotalAmount:'';
                    wrapperObj.OrderpayAmount =(string.isNotBlank(obj.OrderpayAmount))? obj.OrderpayAmount:'';
                    wrapperObj.OrderTaxAmount =(string.isNotBlank(obj.OrderTaxAmount)) ? obj.OrderTaxAmount:'';
                    wrapperObj.OCP = (string.isNotBlank(obj.ExternalOrderKey))?obj.ExternalOrderKey:'';
                    wrapperObj.OrderType = (string.isNotBlank(obj.OrderType)) ?obj.OrderType:'';
                    wrapperObj.OrderStatusLiteral = (string.isNotBlank(obj.OrderStatusLiteral))?obj.OrderStatusLiteral:'';
                    wrapperObj.OrderStatus = (string.isNotBlank(obj.OrderStatus))?obj.OrderStatus:'';
                    wrapperObj.OrderShipDate = (string.isNotBlank(obj.OrderShipDate))?obj.OrderShipDate:'';
                    wrapperObjList.add(wrapperObj);
                }
            }            
        }
        orderObject.dtoList =  wrapperObjList;        
        return orderObject;
    }
    
    /**
    * Returns RightSourceEDITscriptResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for GetMemberResponseservice for OMS Log Notes when Start Date and End Date are selected.
    *
    * @param    
    * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
    */
    public static RightSourceEditScriptRequest_DTO_HUM RequestStructureGetAutorefilResponse(String sScriptKey,String sAutoRefill, string networID)
    {
        RightSourceEditScriptRequest_DTO_HUM oRequest = new RightSourceEditScriptRequest_DTO_HUM();
        RightSourceEditScriptRequest_DTO_HUM.EditScriptRequest EditScriptRequestObj = new RightSourceEditScriptRequest_DTO_HUM.EditScriptRequest();
        EditScriptRequestObj.ScriptKey = sScriptKey;
        EditScriptRequestObj.AutoRefill = sAutoRefill;
        EditScriptRequestObj.CustomerServiceId = networID;
        oRequest.EditScriptRequest = EditScriptRequestObj;        
        return oRequest;
    } 
}