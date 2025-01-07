/*******************************************************************************************************************************
Apex Class Name : Pharmacy_LC_HUM
Version         : 1.0
Created On      : 07/19/2021
Function        : This class is used for performing the Lightning Humana Pharmacy related work
Test Class      : Pharmacy_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Pallavi Shewale                                     07/19/2021                Calling GetMemberDetailsService
*    2.0           Pallavi Shewale                                     07/29/2021                Getting Personal Account Details

*                  Swapnali Sonawane								   07/26/2021                Added method getPolicyList to get policy details for Pharmacy Plan Member tab 
*    3.0           Himalay Patel                                       08/20/2021                User Story 2421627: T1PRJ0002517 - MF 2 - Updating Addresses That Apply to More Than One Address Type (RxRF) (ID# 91)
*                  Swapnali Sonawane                                   09/02/2021                UserStory:2508657 Added method to get other insurance Details
* 	 4.0		   Ashok Kumar Nutalapati           				   09/30/2021            	 REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
*	 5.0		   Nirmal Garg										   10/04/2021				 Added logic for invoke oms service for oms notes
*    6.0		   Nirmal Garg										   10/05/2021			     US2504099 - Changes. Added edit member logic
*	 7.0		   Abhishek Mangutkar								   10/12/2021				 US - 2528482 - T1PRJ0078574 - MF 11812 - HP Lightning- 'Prescriptions' - Filters
*	 8.0		   Pallavi Shewale								       10/28/2021				 US - 2664846 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order Summary - Cards
*	 9.0		   Abhishek Mangutkar								   11/08/2021				 US - 2674165 - T1PRJ0078574  - MF 11811 - HP- Lightning- Order - Rx Panel
*   10.0           Swapnali Sonawane                                   11/14/2021                US - 2766098  T1PRJ0078574 - MF 11811 - HP Lightning- Add 'Prescriptions' - Auto refill
*   11.0           Swapnali Sonawane                                   08/23/2022				 US#3631288 Use purchaser plan object
*   12.0           Abhishek Mangutkar                                  10/28/2022		 		 US#3897898
*   13.0           Monali Jagtap                                       09/29/2023      			 US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages
*	14.0		   Monali Jagtap									   07/17/2023 				 USER STORY -4812246 -T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
*******************************************************************************************************************************************************************/

public with sharing class Pharmacy_LC_HUM {
   
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    public static Boolean Generic_Security_Switch = HUMUtilityHelper.isCRMFunctionalityON('Generic_Security_Switch');
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMember details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true)
       public static Object invokeGetMemberService(String memID,string networkId, String sRecordId){
           return Pharmacy_LS_HUM.SendRequestGetMemberResponse(memID,networkId,sRecordId);
       } 
       
   /**
   * Returns responses of continuation object
   * <p>
   * Callback Method to parse the service response
   *
   * @return Object
   */
   @AuraEnabled
        public static object parseRSGetMemberResponse(List < string > labels, object State){
           return Pharmacy_LD_HUM.parseRSGetMemberResponse(labels, State);
        }
        
        
   /**    
   *  Method to get Account Details
   *
   * @return List<Account>
   */   
   @auraEnabled
       public static List<Account> getAccountList(String recordID){
           return [select id ,Security_Groups__c , RecordType.Name, PersonMailingAddress,PersonMailingStreet,PersonMailingCity,PersonMailingStateCode,PersonMailingState,PersonMailingPostalCode,PersonMailingCountry, PersonEmail, PersonHomePhone from Account where Id=: recordID WITH USER_MODE ];                      
       }
       
  
   /*
    * Method Name   :    getOIData
    * Description   :    This method makes call to MBE when the Member policy page is loaded 
    * Return Type   :    Continuation
    * Parameters    :    MemberPlan Id
    */ 
   @AuraEnabled(continuation=true)  
    public static Object getOIData(String sMemberPlanId, String sEnterpriceId) 
   {
       return Pharmacy_LS_HUM.getOtherInsuranceData(sMemberPlanId,sEnterpriceId); 
   }
   
   /*
    * Method Name   :    processMBEOtherInsuranceResponse
    * Description   :    This method process Http Response from MBE Service 
    * Return Type   :    Continuation
    * Parameters    :    NA
    */
   @AuraEnabled(continuation=true cacheable=true) 
   public static object processMBEOtherInsuranceResponse(Object state)
   {
       return Pharmacy_LS_HUM.processMBEOtherInsuranceResponse(state);           
   } 
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMemberEdit details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true) 
   public static object updateGetMemberAddresses(string enterprise, string networkId, String sRecordId, String addressRequestJSON)
   {
       return Pharmacy_LS_HUM.prepareEditGetMemberAddresses(enterprise,networkId,sRecordId,addressRequestJSON);
   }

   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
   @AuraEnabled
   public static object retrieveUpdatedGetMemberAddresses(List < string > labels, object State)
   {
       return Pharmacy_LS_HUM.retrieveUpdatedGetMemberAddresses(labels, State);
   }
   
   /*
   * Method Name : createPharmacyCaseAndRedirect
   * Description : This method is used to create new case from Address component on Humana pharmacy page
   * Return type : sCaseId
   * Parameter   : NA
   */
   @AuraEnabled
   public static String createPharmacyCaseAndRedirect(string sInteractionId, String sObjectId, string sAccountId) 
   {   
       String sCaseIdNumber;
       String sCaseId;
       String sCaseNumber;
       try{
           LoggingProcessLogic_S_HUM.sPolicyMemberId = sObjectId;
           LoggingProcessLogic_S_HUM.bAddressLWC = true;
           if (String.isNotBlank(sInteractionId)) {
               List<Interaction_Member__c> interactionRecord = [select id,Interacting_About_Type__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c FROM Interaction_Member__c Where Interaction__c = : sInteractionId AND Interacting_About__c = :sAccountId WITH USER_MODE];
               if(interactionRecord.size()>0) LoggingProcessLogic_S_HUM.interactionRecord = interactionRecord[0];
           }
           sCaseId = LoggingProcessLogic_S_HUM.createNewPharmacyAddressCase(sObjectId);
           LoggingProcessLogic_S_HUM.bAddressLWC= false;
           
           sCaseNumber = (String.IsNotBlank(sCaseId))?LoggingDataLogic_D_HUM.getCaseNumber(sCaseId):'';
           insertCaseInteraction_Sec(sInteractionId,sCaseId);
           sCaseIdNumber = sCaseId + ' ' + sCaseNumber; 
       }
       catch(Exception ex)
       {
           HUMExceptionHelper.logErrors(ex, 'Pharmacy_LC_HUM', 'createPharmacyCaseAndRedirect');
       }
       return sCaseIdNumber;
   }  
  
   public static void insertCaseInteraction_Sec(String sInteractionId,String sCaseId){
       List<Case_interaction__c> lstCaseInt1 = [SELECT Id FROM Case_interaction__c WHERE interaction__c = :sInteractionId AND Case__c = :sCaseId WITH USER_MODE Limit 1];
       List<Case_interaction__c> lstCaseInt=(String.isNotBlank(sInteractionId))?lstCaseInt1:null;
       Case_Interaction__c oCaseInt1;
       Case_Interaction__c oCaseInt =(lstCaseInt.isEmpty())?(new Case_Interaction__c(Case__c = sCaseId, Interaction__c = sInteractionId)):null;             
                       insert oCaseInt;
                   
       }
   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
   @AuraEnabled(continuation=true)
   public static object invokeOMSLogNotes(String enterpriseId, String startDate, String endDate, string networkID, String sRecordId)
   {
       return Pharmacy_LS_HUM.sendRequestGetMemberResponse(enterpriseId, startDate, endDate, networkID, sRecordId);
   }
   
   /**
    * Returns request object of continuation object
    * <p    ----->
    * Method to invoke the getMemberEdit details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true)
    public static object updateGetMember(string enterprise, string phone, string AltPhone, string Email, string captype, string networkId,String sRecordId,AddressValidationHelper_DTO_HUM addressDto,String IsDeclined)
   {
       return Pharmacy_LS_HUM.prepareEditGetMember(enterprise,phone,AltPhone,Email,captype,networkId,sRecordId,addressDto,IsDeclined);
   }
   
   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
   @AuraEnabled
   public static string retrieveUpdatedGetMember(List <string> labels, object State)
   {
       return Pharmacy_LS_HUM.retrieveUpdatedGetMember(labels, State);
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the EditMember to update captype
    *
    * @return continuation object
    */
   @AuraEnabled (continuation=true cacheable=true)
   public static object updateCaptype(string enterprise,string captype, string networkID, String sRecordId)
   {  
       return Pharmacy_LS_HUM.updatemembercaptype(enterprise, captype, networkID, sRecordId);
   }
   
  
   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the CapType service response
    *
    * @param List<String> Continuation Labels
    * @param Object State of Continuation
    * @return Object
    */
  @AuraEnabled
   public static object retrieveUpdatedCapType (List < string > labels, object State)
   { 
       return Pharmacy_LS_HUM.retrieveUpdatedCapType(labels, State);
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMemberEdit (consentBeginDate) details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled( continuation=true cacheable=true)
   public static object updateConsentBeginDate(string enterprise, string networkId, String sRecordId)
   {
       return Pharmacy_LS_HUM.prepareUpdateConsentBeginDate(enterprise, networkId, sRecordId);
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMemberEdit (consentEndDate) details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled( continuation=true cacheable=true)
   public static Continuation updateConsentEndDate(string enterprise, string networkID, String sRecordId)
   {
       return Pharmacy_LS_HUM.prepareUpdateConsentEndDate(enterprise, networkID, sRecordId);
   }
   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the Member Consent service response
    *
    * @return Object
    */
   @AuraEnabled
   public static object retrieveUpdatedMemberConsent(List < string > labels, object State)
   {
       return Pharmacy_LS_HUM.retrieveUpdatedMemberConsent(labels, State);
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the Epost Get Member Prescription details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true) 
   public static Continuation invokeEpostMemberPrescription(String memID,String scriptKey, String startDate, String endDate, string networkId, String sRecordId)
   {
       return Pharmacy_LS_HUM.sendRequestGetScript(memID,scriptKey, startDate, endDate, networkId, sRecordId);
   }
   
   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
   @AuraEnabled( continuation=true)
   public static string parseRSGetScriptResponse(List < string > labels, object State)
   {
       list <PharmacyMemberPrescription_DTO_HUM> responseDTOList = Pharmacy_LS_HUM.parseRSGetScriptResponse(labels, State);
       return json.serialize(responseDTOList);
   }
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the Epost Get Order details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true)
   public static Continuation invokeEpostOrder(String memID, String startDate, String endDate, string networkId, String sRecordId)
   {
       return Pharmacy_LS_HUM.sendRequestGetOrderResponse(memID, startDate, endDate,networkId, sRecordId);
   }
   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
   @AuraEnabled(continuation=true) 
   public static object parseRSGetOrderResponse(List < string > labels, object State)
   {
       return Pharmacy_LS_HUM.parseRSGetOrderResponse(labels, State);
       
       
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the Epost Get Order details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true cacheable=true)
   Public Static object getOrderData(String orderKeyValue,String source,String startDate,String endDate,String enterpriseId, String networkID, String sRecordId)
   {
       Return PharmacyOrderDetail_LS_HUM.invokeOrderDetail(orderKeyValue, source, startDate, endDate, enterpriseId, networkID, sRecordId);
   }
   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
    @AuraEnabled
   Public Static Object ProcessOrderResponse(List < String > labels, Object State)
   {
       Return Json.serialize(PharmacyOrderDetail_LS_HUM.ProcessOrderResponse(labels, State));
   }
   
   /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the CapType service response
    *
    * @param List<String> Continuation Labels
    * @param Object State of Continuation
    * @return Object
    */
   @AuraEnabled(continuation=true cacheable=true)
   public static object retrieveautorefil(List <string> labels, object State)
   {
       return Pharmacy_LS_HUM.retrieveautorefil(labels, State);
   }
   
   /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the EditMember to update captype
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true)
   public static object updateautorefil(string sScriptKey,String sAutoRefill,string networkID)
   {
       return Pharmacy_LS_HUM.updatescriptautorefil(sScriptKey, sAutoRefill, networkID);
   }
   
   @AuraEnabled(continuation=true)
   public static Continuation invokeRightSourceService(string request,String type)
   {        
       return Pharmacy_LS_HUM.invokeRightSourceService(request,type);        
   }
   
   @AuraEnabled(continuation=true)
   public static object retriveRightSourceData(List <string> labels, object State)
   {
       return Pharmacy_LS_HUM.retriveRightSourceData(labels, State);
   }
   
   @AuraEnabled(cacheable=true continuation=true)
   public static Object invokeMemElig100DayPrescripService(string request){
    try {
        return Pharmacy_LS_HUM.sendRequestIsMemElig100DayPrescrip(request);
    } catch (Exception e) {
        throw new AuraHandledException(e.getMessage());
    }
   }

    @AuraEnabled
    public static Object process100DayResponse(List <string> labels, object state){
        try {
            return Pharmacy_LS_HUM.parseIsMemElig100DayServiceResponse(labels, state);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }

}