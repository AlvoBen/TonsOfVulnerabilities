/*******************************************************************************************************************************
Apex Class Name : PharmacyFinancial_C_HUM.cls
Version         : 1.0
Created On      : 20th Dec 2016
Function        : Class contains methods for services to fetch Financial information.
TestClass       : PharmacyFinancial_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey           70274                 	12/20/2016                   Original Version (REQ 291096)
* Sharan Shanmugam			70274				  	02/16/2017					 Implementation of REQ - 296663 : View credit card information from getMember details 
* Vishal Verma				70274					02/20/2017					 Implementation of REQ - 296664 : View payment information from GetMemberFinanceAndAdjustments details
* Vishal Verma				74192			        08/03/2017					 Implementation of REQ - 296665 : View adjustment information from GetMemberFinanceAndAdjustments details
* Sharan Shanmugam                               	03/21/2017                   REQ - 296877 - Update Credit Card information
* Sharan Shnamugam                                  03/22/2017                   REQ - 300147 : Create finance request 		
* Vishal Verma              78834                   03/24/2017                   REQ - 296881: Add New Credit Card 		   
* Vishal Verma              78834                   04/10/2017                   270149.0001 : Added PCI URL to Custom Setting 
* Vishal Verma              82890                   04/07/2017                   REQ - 301358 : Send Email for financial request
* Rajesh Keswani            112036	                07/20/2017                   Implementation of REQ - 323389 - adding networkId in service request
* Sharan Shanmugam									07/27/2017					 REQ - 323542 : Exception handling
* Sumit Pharande            345615                  03/27/2019                   REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that�s attached to an order in progress
* Shailesh Mali                                     08/26/2020                   REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
* Pallavi Shewale				    				02/10/2021	                 User Story 1669828 - PR00094226 - CR AEP readiness -Research - Critical Security Issues
* Kiran Kumar										03/08/2021				   	 User Story - 1961520: Remove the 'Create Finance Request' Link - Clean Up (CRMS)
* Kiran Bhuvanagiri									07/06/2021					 US 2367864 - Auto Case Comments for One Time Charge Payments: Add Logging Component to Submit One-Time Payment Popup (RXRF) 
* Prasanthi Kandula                                 07/28/2021                   US 2031568 - PR00094226 - CR AEP readiness - Low Security Issues       
* Nirmal Garg										09/16/2021					 Added browser logic for coveo
* Bhavani Tati                                      02/14/2022                   US 3127100 - T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Auto Case Comments for One Time Charge Payments: Add Logging Component To Submit One-Time Payment Popup (CRMS) (ID#148)
*****************************************************************************************************************************************/
public with sharing class PharmacyFinancial_C_HUM extends SObjectDescriptorImpl_H_HUM 
{
    
    private PharmacyFinancial_D_HUM oDataPharmacyFin= new PharmacyFinancial_D_HUM();
    private String sPolMemId  = '' ;
    public String sPolMemRecordId{get;set;}
    public string sEnterpriseID{get;set;}
    public decimal amount {get;set;}
    public String cardType {get;set;}
    public String creditCardLast4Digit {get;set;}
    public String creditCardLastFourDigit {get;set;}
    public String creditCardExpYear {get;set;}
    public boolean DisplayPopup {get; set;}
    public boolean displayErrorMsg {get; set;}
    public boolean displaySucessMsg {get; set;}
    public boolean oneTimeCreditCardPaymentSwitch{get;set;}
    List<Policy_Member__c> lstPolicyMem;
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public PharmacyFinancial_C_HUM()
    { 
       sPolMemRecordId = sPolMemId  = ApexPages.currentpage().getParameters().get('id'); 
       bNewCreditCardPopFlag = false;
       bUpdateCreditCardPopFlag = false;
       sPCIEndPoint = HUM_Webservice_Callout__c.getValues('PCI').End_Point_URL__c +HUM_Webservice_Callout__c.getValues('PCI').Service_Name__c;
       oneTimeCreditCardPaymentSwitch =  HUMUtilityHelper.isCRMFunctionalityON('1152078');
        lstPolicyMem = getEnterpriseIdOfMember();
        if(lstPolicyMem != Null && !lstPolicyMem.isEmpty())    
        {
            if(lstPolicyMem[0].Member__r !=null){           
                sEnterpriseID = lstPolicyMem[0].Member__r.Enterprise_ID__c;
            }           
        }
		
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    } 
  
    @testvisible
    private List<Policy_Member__c> getEnterpriseIdOfMember()
    {
        List<Policy_Member__c> lstPolicyMem = new List<Policy_Member__c>();
        try
        { 
            lstPolicyMem = [SELECT Id, Member__r.name, Member__r.Enterprise_ID__c FROM POLICY_Member__C WHERE ID=:ApexPages.currentpage().getParameters().get('id')];
            if(lstPolicyMem != Null && !lstPolicyMem.isEmpty())    
            {
                return lstPolicyMem;
            }
            return NULL;
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PharmacyRXClaimsSummary_C_HUM', 'getMemberNameAndGenKey');
            return NULL;
        }
    }
    
    public Policy_Member__c objPolMem{ get; set; }
    public boolean bNewCreditCardPopFlag{get;set;}
    public boolean bUpdateCreditCardPopFlag{get;set;}
    public boolean bFinancialRequestPopFlag {get;set;}
    public String sPCIEndPoint {get;set;}
    
    /**
    * flag to show pop up for creating Financial Request
    * <p>
    * @param  NA
    * 
    * @return void
    */
     public void ClosePopup() {       
        DisplayPopup = false;   
    }
    public void ShowPopup() {       
        DisplayPopup = true;   
    }
    
     public void CloseErrorMsg() {       
        displayErrorMsg = false;   
    }
     public void showSucessMessage() { 
        displaySucessMsg = true; 
         displayErrorMsg = false;
     }
    
    public void showErrorMessage() { 
        displayErrorMsg = true;
        displaySucessMsg = false;  
     }
    public void hideSuccessAndErrorMessage(){
        displaySucessMsg = false;
        displayErrorMsg = false;
    }
    
    
    /**
    * flag to close pop up for creating Financial Request
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void closeUpdatePopUp()
    {
        bUpdateCreditCardPopFlag = false;
        bNewCreditCardPopFlag = false;
    }
    
    /**
    * flag to show pop up for updating the credit card section
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void showUpdateCreditCardSection()
    {
        bUpdateCreditCardPopFlag = true;
    }
    
    /**
    * make a call to getPolicyMemberDatamethod of  PharmacyFinancial_D_HUM class to get Policy Member Data
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void getPolicyMemberData()
    { 
        try
        {
                if (String.isNotBlank(sPolMemId)) objPolMem = oDataPharmacyFin.getPolicyMemberData(ApexPages.currentpage().getParameters().get('id').escapeHtml4());
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_C_HUM', 'getPolicyMemberData');
        }
    }
   
    /**
    * Returns this instance of the class
    * <p>
    * Method to store the Current Page Instance for Side Panel
    * 
    * @return this
    */
    public PharmacyFinancial_C_HUM  getThis()
    {
        return this;
    }
    
    /**
    * Returns List<sObject> of record
    * <p>
    * Virtual required Method
    *
    * @return NULL
    */
    public List<sObject> getRecords()
    {
        return NULL;
    }

    /**
    * Returns sObject of record
    * <p>
    * Virtual required Method
    *
    * @return NULL
    */
    public sObject getRecord()
    {
        return NULL;
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMember details continuation requests
     *
     * @param  String Enterprise Id
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeGetMemService(String sMemID, string networkID, String sRecordId)
    {
        return PharmacyFinancial_S_HUM.SendRequestGetMemberResponse(sMemID, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get member service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    public static object parseRSGetMemberCreditResponse(list<string> Labels, object state)
    {
        return PharmacyFinancial_S_HUM.parseRSGetMemberCreditResponse(Labels, state);
    }
    
        
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getAdjustmentsAndPayments details continuation requests
     *
     * @param  String Enterprise Id
     * @param  String Start Date
     * @param  String End Date
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeGetAdjPayService(String sMemID , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID, String sRecordId)
    {
        return PharmacyFinancial_S_HUM.SendRequestGetMemberResponse(sMemID, sStartDate, sEndDate , sPayment , sAdjustment, networkID, sRecordId); 
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the payment service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    public static object parseGetAdjPayResponse(list<string> Labels, object state)
    {
        return PharmacyFinancial_S_HUM.parseGetAdjPayResponse(Labels, state);
    }
    
    public void showNewCreditCardSection()
    {
        bNewCreditCardPopFlag = true;
    }
    
     public void hideNewCreditCardSection()
    {
        bNewCreditCardPopFlag = false;
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeGetEditMemService(string enterprise, String sCreditCardKey, String exMonth, String exYear, String sActive, String sAutoCharge, String sFSA, String sFirstName, String sMiddleName, String sLastName, String sTokenKey, String sCreditType, String isInsert, string networkID, String sRecordId)
    {
        return PharmacyFinancial_S_HUM.prepareEditGetMember(enterprise, sCreditCardKey, exMonth, exYear, sActive, sAutoCharge, sFSA, sFirstName, sMiddleName, sLastName, sTokenKey, sCreditType, isInsert, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get member edit service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    public static object retrieveEditMember(List < string > labels, object State)
    {
        return PharmacyFinancial_S_HUM.retrieveEditMember(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Email Utility Service to create financial request
     *
     * @param sMemName                      String
     * @param sHumanaId                     String
     * @param sAccNumber                    String
     * @param sOrderNumber                  String
     * @param sRxNumber                     String
     * @param sConcern                      String
     * @param sNetworkUserId                String
     * @return continuation object
    */
    @RemoteAction
    public static Continuation createFinancialRequest(String sMemName , String sHumanaIdNum , String sAccNum , String sOrderNumber , String sRxNumber , String sConcern , String sNetworkUserId , String sEnterpriseId, String sRecordId)
    {
        return PharmacyFinancial_S_HUM.SendRequestEmailUtilityService(sMemName , sHumanaIdNum , sAccNum , sOrderNumber , sRxNumber , sConcern , sNetworkUserId ,sEnterpriseId,  sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get email utility service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseEmailUtilityResponse(List < string > labels, object State)
    {
        return PharmacyFinancial_S_HUM.parseEmailUtilityResponse(labels, State);
    }
    
     
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get log Note service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseLogNoteResponse(List < string > labels, object State)
    {
        return PharmacyFinancial_S_HUM.parseLogNoteResponse(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the get Order Details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    Public Static Continuation createGetOrderDetailRequest(String sOrderNumber , String snetworkId)
    {
         Return PharmacyFinancial_S_HUM.prepareGetOrderDetail(sOrderNumber, snetworkId);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get log Note service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */    
     public static object processOrder(List < string > labels, object State)
     {
        return PharmacyFinancial_S_HUM.processOrder(labels, State);
     }
    
   
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the get Order continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeEpostOrder(String memID, string networkId, String sRecordId)
    {
        return PharmacyFinancial_S_HUM.sendRequestGetOrderResponse(memID,networkId, sRecordId);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get log Note service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseRSGetOrderResponse(List < string > labels, object State)
    {
        return PharmacyFinancial_S_HUM.parseRSGetOrderResponse(labels, State);
    }
    
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the get Member continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeGetMemServiceFinance(String sMemID, string networkID, String sRecordId)
    {
        return Pharmacy_CreateEditOrder_S_HUM.SendRequestGetMemberResponseFinance(sMemID, networkID, sRecordId);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Get log Note service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseRSGetMemberResponseFinance(List < string > labels, object State)
    {
        return Pharmacy_CreateEditOrder_S_HUM.parseRSGetMemberResponseFinance(labels, State);
    }  
    
    
    /**
     * Returns request object of continuation object
     * Method to invoke the EditMember continuation requests
     * @param - enterpriseID, netWorkId, creditCardKeyVar, amountVar, paymentDateVar, isPaymentAuthorizedVar
     * @return continuation object
    */
    @RemoteAction
    public static Continuation invokeOneTimeCreditCardPayment(string enterpriseID, string netWorkId, string creditCardKeyVar, decimal amountVar, string paymentDateVar, boolean isPaymentAuthorizedVar)
    {
        Date paymentDateVarDateFormat =  date.valueOf(paymentDateVar);
        return PharmacyFinancial_S_HUM.SendRequestOneTimeCreditCardPayment(enterpriseID, netWorkId, creditCardKeyVar, amountVar,paymentDateVarDateFormat, isPaymentAuthorizedVar);
    }
    
     /**
     * Returns responses of continuation object    
     * Callback Method to parse the One Time Credit Card Payment (EditMember) service response
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    public static object parseOneTimePaymentResponse(List < string > labels, object State)
    {
       return PharmacyFinancial_S_HUM.parseOneTimePaymentResponse(labels, State);
    }

}