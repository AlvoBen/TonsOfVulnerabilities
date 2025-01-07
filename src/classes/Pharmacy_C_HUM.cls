/*******************************************************************************************************************************
Apex Class Name : Pharmacy_C_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains methods for services to fetch Pharmacy detail information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani               57637                12/20/2016                 Original Version
* Rajesh Chandgothia           55992                12/29/2016                 Demographic functionality (REQ 291083)
* Rajesh Keswani               58175                01/22/2017                 Implementation of REQ - 296652 - Order Summary details from getOrder EPostRX
* Shiva Pasumarty              60234                02/02/2017                 OMS Log Notes Req# REQ - 272225
* Rajesh Keswani               66266                02/21/2017                 Implementation of REQ - 291082 - Edit Demographic section
* Rajesh Chandgothia           70352                02/14/2017                 Member prescription Req# REQ - 296752
* Sharan Shanmugam			   74899				03/09/2017				   Implementation of REQ - 296650 - Order details
* Rajesh Keswani               74554  	            03/03/2017                 REQ - 299426 Add and Edit address section 
* Rajesh Chandgothia           75938                03/01/2017                 Edit Order Req# REQ - 296651
* Rajesh Keswani        	   78299	            03/23/2017            	   REQ - 300050 Add Prescription to the order
* Rajesh Chandgothia                                03/28/2017                 Reason: Added back the variables To fix the QAS build
* Vishal Verma                 78304                03/22/2017                 REQ - 296926 : Added MTM Indicator
* Albin Arul                   78959                03/24/2017                 REQ - 296721 : Implemented Member Prescription section - Member Consent changes 
* Vamsi Kondragunta			   81140				03/31/2017				   REQ - 301578 Added for Autorefil and captype update; Defect 277309.0001 Rollback and merge issues
* Avinash Choubey                                   04/06/2017                 fixed rollback issue for REQ 301370 and req 301369
* Sumit Pharande               90051                05/08/2017                 REQ-308344  Bad and Change address modification
* Albin Arul                   94500                05/18/2017                 Defect fix 280917.0001 - Release and Cancel from Summary
* Rajesh Keswani               112036               07/20/2017                 Implementation of REQ - 323389 - adding networkId in service request
* Sharan Shanmugam									07/27/2017				   REQ - 323542 : Exception handling
* Joel George										10/31/2018				   REQ REQ - 367769
* AshokKumar Nutalapati        330644               01/25/2019                 REQ - 383716 & 383717: Ability to Read Rx#, Written&Dispensed Drug Included in Order from Humana Pharmacy Page (RxRF)
* AshokKumar Nutalapati        338598               02/21/2019                 Defect Fix 402874.0001
* Deepankar Binjola            42502                08/28/2019                 REQ - 408254 Remove PharmacyCreatEditOrder_CMP_HUM component
* Kiran Kumar										06/01/2020				   REQ - 1050513 Change Formatting of 'Balance Due' Field on Humana Pharmacy Page (RxRF)
* Himalay Patel									 	03/16/2021 			       REQ - 1711424: Display Expired and Expiring Soon Credit Card Icon (CRMS) (ID# 131)
* Ashok Kumar Nutalapati							04/23/2021				   US-2076036 T1PRJ0002517 - MF 2 - Display Balance Due Icon on the Humana Pharmacy Quick Start+ Page (RxRF) (ID# 131)
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pallavi Shewale				   06/08/2021				 US-1491784 Changes - Finance Queue
* AshokKumar Nutalapati   							07/12/2021           	   REQ - 1907149 T1PRJ0002517 - MF 2 - Display Icon When Pharmacy Authorization on File (RxRF) (ID# 131)
* Aaron Speakman                                    08/20/2021                 User Story 2421627: T1PRJ0002517 - MF 2 - Updating Addresses That Apply to More Than One Address Type (RxRF) (ID# 91)
* Nirmal Garg										09/16/2021				   Added browser logic for coveo
* Kiran Bhuvanagiri                					09/30/2021            	   REQ - 2507884 T1PRJ0002517 - MF 2 - Case Documentation for Demographic Updates (RXRF) (ID# 35a)
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
* Himalay Patel								        01/10/2022	               User Story 2755950: T1PRJ0002517 - MF 1 - Inactivate Rx: Launching Process (RxRF) (ID# 163)
* Aaron Speakman                                    05/06/2022                 User Story 2413142: T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Display Balance Due Icon on the Humana Pharmacy Quick Start+ Page (CRMS) (ID# 131)
* Ashok Kumar Nutalapati                        	05/20/2022                 REQ - 3074356 T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Switch Clean Up - Display Icon When Pharmacy Authorization on File (CRMS) (ID# 131)
* Bhavani Tati                                      05/20/2022                 US-3175332 T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Inactivate Rx: Launching Process (CRMS) (ID# 163) 
* Thaurn Madishetti                                 04/14/2023                 US-3837439: T1PRJ0714381- MF1: (PLT) 100 Day - CRM Supply Eligibility Indicator (Classic)
*****************************************************************************************************************************************/
public with sharing class Pharmacy_C_HUM extends SObjectDescriptorImpl_H_HUM
{

    private Pharmacy_S_HUM oServicePharmacy = New Pharmacy_S_HUM();
    Private Pharmacy_D_HUM oDataPharmacy = New Pharmacy_D_HUM();
    Private PharmacyOrderDetail_S_HUM oServicePharmacyOrderDetail = New PharmacyOrderDetail_S_HUM();	
    Public Static final String NONPHARMACY_USER_HUM = 'No';
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    
    /**
     * Constructor for the class , Intialize variables
     * 
     * @return NULL
     */
    public Pharmacy_C_HUM()
    {
        if (polObjForComp == null)
        {
            oServicePharmacy.init(this);
            polObjForComp = this.objPolMem;
        }
		else refilVariable();
        lstLogErrors = new List < Error_Log__c > ();
        bCusPopFlag = false;
        bCusAddPresPopFlag = false;
		bMTMRequestPopFlag = false;
        bConfirmFlag = false;
        showConfirmPopUpFlag = false;
        bCusOrderPopFlag = false;
        selectedOrderNumber = '';
		bShowMTMSection = Pharmacy_D_HUM.isMedicareMedicaid(polObjForComp);
        bsoftMsgFlag = false;
        bShowModCusPopFlag = false;
        //bPharmacyuser = Pharmacy_H_HUM.isPharmacyUser() == GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM ? false : true;
        bPharmacyuser = Pharmacy_H_HUM.isPharmacyUser() == NONPHARMACY_USER_HUM ? false : true;
        bExpiredCreditCard = false;
        bExpiringSoonCreditCard = false;
		//REQ - 2076036 Switch for new OMS and Case Comments sections
        bPharmacyAuth = false;
        bSwitchLightningAddress = HUMUtilityHelper.isCRMFunctionalityON('2421627');
         //User Story 3837439 100 Day - CRM Supply Eligibility Indicator (Classic)
        bSwitchSupplyEligibility = HUMUtilityHelper.isCRMFunctionalityON('3837439');
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}  
    }
    
	public static transient Policy_Member__c polObjForComp;
    public String sStartDate {get;set;}
    public String sEndDate {get;set;}
	public boolean bConfirmFlag {get;set;}
    public String PharmacyDetail {get;set;}
	public boolean bMTMRequestPopFlag {get;set;}
    public boolean bCusPopFlag {get;set;}
	public boolean bCusOrderPopFlag {get;set;}
    public Policy_Member__c objPolMem {Get;Set;}
    public String sPolMemId {Get; Set;}
    public String selectedOrderNumber {Get;Set;}
    public boolean showConfirmPopUpFlag {get;set;}
    public boolean showConfirmPopUpForEachOrderFlag{get;set;}
	public boolean showConfirmcaptypePopUpFlag {get;set;}
    public boolean showAutorefilPopupFlag {get;set;}
    public boolean bCusAddPresPopFlag {get;set;}
    public boolean bsoftMsgFlag {get;set;}
    public boolean bShowModCusPopFlag {get;set;}
	public boolean bShowMTMSection{get;set;}
    public boolean bPharmacyuser{get;set;}
    public boolean bExpiredCreditCard{get;set;}
    public boolean bExpiringSoonCreditCard{get;set;}
    public boolean bPharmacyAuth {get;set;}
    public boolean bSwitchLightningAddress {get;set;}
    public boolean bSwitchSupplyEligibility {get;set;}
    public List < Error_Log__c > lstLogErrors
    {
        get
        {
            if (lstLogErrors == null)
            {
                lstLogErrors =  new List < Error_Log__c > ();
            }
            return lstLogErrors;
        }
        set;
    }
          

    /**
     * Returns this instance of the class
     * <p>
     * Method to store the Current Page Instance for Side Panel
     * 
     * @return this
     */
    public Pharmacy_C_HUM getThis()
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
    public List < sObject > getRecords()
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
     * Returns void
     * <p>
     * Assigning all values to the controller variable
     *
     * @return NULL
     */
    public void refilVariable()
    {
        this.objPolMem = polObjForComp;
        this.sPolMemId = polObjForComp.id;
    }
    
    
     /**
     * Returns void
     * <p>
     * Flag to show pop up for upating the section
     *
     * @return NULL
     */
    public void toggleOrderCusPopFlag()
    {
        bCusOrderPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to show pop up for upating the section 
     *
     * @return NULL
     */
    public void toggleCusPopFlag()
    {
        bCusPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
     public void closePopup()
    {
        bCusPopFlag = false;
        bCusOrderPopFlag = false;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
     public void toggleConfirmFlag()
    {
        bConfirmFlag = true;
        bCusPopFlag = false;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
    public void cancelConfirmAddress()
    {
        bConfirmFlag = false;
        bCusPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to Cancel Confirm Address
     *
     * @return NULL
     */
    public void cancelModConfirmAddress()
    {
        bConfirmFlag = false;
        bShowModCusPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to close confirm pop up
     *
     * @return NULL
     */
    public void closeConfirmPopUp()
    {
        bConfirmFlag = false;
        bCusPopFlag = false;
       
    }
    
     /**
     * Returns void
     * <p>
     * Flag to show zip city message
     *
     * @return NULL
     */
    public void showZipCityMsg()
    {
       
        bConfirmFlag = false;
        bCusPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to open pop up
     *
     * @return NULL
     */
     public void openAddPrespopUp()
    {
        bCusAddPresPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
    public void closeAddPrespopUp()
    {
        bCusAddPresPopFlag = false;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
    public void cancelSoftMsgPopUp()
    {
        bsoftMsgFlag = false;
        bShowModCusPopFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
    public void openSoftMsgPopUp()
    {
        bsoftMsgFlag = true;
        bShowModCusPopFlag = false;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide pop up
     *
     * @return NULL
     */
     public void toggleModConfirmFlag()
    {
        bsoftMsgFlag = false;
        bShowModCusPopFlag = false;
        bConfirmFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to show Member Consent confirmation pop up
     *
     * @return NULL
     */
    public void toggleMemberConsentConfirmPopUpFlag()
    {
        showConfirmPopUpFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to show Member Consent For Each Order confirmation pop up
     *
     * @return NULL
     */
    public void toggleShowConfirmPopUpForEachOrderFlag()
    {
        showConfirmPopUpForEachOrderFlag = true;
    }
    
    /**
     * Returns void
     * <p>
     * Flag to hide Request Consent For Each Order confirmation pop up
     *
     * @return NULL
     */
    public void disableRequestConsentForEachOrderPopUp()
    {
        showConfirmPopUpForEachOrderFlag = false;      
    }
    
     /**
     * Returns void
     * <p>
     * Flag to hide Member Consent confirmation pop up 
     *
     * @return NULL
     */
    public void disableMemberConsentConfirmPopUpId()
    {
        showConfirmPopUpFlag = false;      
    }
    
    /**
     * Method to show popup for MTM
     **
     * @param NA
     * @return NA
     */
    public void showMTMPop()
    {
        bMTMRequestPopFlag = true;
    }
    
    /**
     * Method to hide popup for MTM
     **
     * @param NA
     * @return NA
     */
     public void hideMTMPop()
    {
        bMTMRequestPopFlag = false;
    }
    
    /**
     * Method to show popup for MTM
     **
     * @param NA
     * @return NA
     */
    public void showModCusPopUp()
    {
        bShowModCusPopFlag = true;
    }
    
    /**
     * Method to show popup for MTM
     **
     * @param NA
     * @return NA
     */
    public void disableModCusPopUp()
    {
        bShowModCusPopFlag = false;
    }
    
    /**
     * Method to show captype confirmation pop up
     **
     * @param NA
     * @return NA
     */
    public void showcaptypepopup()
    {
        showConfirmcaptypePopUpFlag = true;      
    }
    
    /**
     * Method to show captype confirmation pop up 
     **
     * @param NA
     * @return NA
     */
    public void hidecaptypepopup()
    {
        showConfirmcaptypePopUpFlag = false;      
    }
    
    /**
     * Method to show captype confirmation pop up 
     **
     * @param NA
     * @return NA
     */
    public void bcaptype()
    {
        showConfirmcaptypePopUpFlag = true;      
    }
    
    /**
     * Method to show captype confirmation pop up 
     **
     * @param NA
     * @return NA
     */
    public void showautorefilconfirm()
    {
        showAutorefilPopupFlag = true;      
    }
    
    /**
     * Method to show captype confirmation pop up
     **
     * @param NA
     * @return NA
     */
    public void hideautorefilconfirm()
    {
        showAutorefilPopupFlag = false;      
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the EditMember to update captype
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateCaptype(string enterprise,string captype, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.updatemembercaptype(enterprise,captype, networkID, sRecordId);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the EditMember to update captype
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateautorefil(string memId, string sScriptKey,String sAutoRefill,string startData, string EndDate, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.updatescriptautorefil(memId,sScriptKey, sAutoRefill,startData,EndDate, networkID, sRecordId);
    }

    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit (MTM) details continuation requests
     *
     /**
     * @param EnterpriseId
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateMTM(string enterprise, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.prepareUpdateMTM(enterprise, networkID,sRecordId);
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
    @RemoteAction
    public static object retrieveautorefil(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveautorefil(labels, State);
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
    @RemoteAction
    public static object retrieveUpdatedCapType(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveUpdatedCapType(labels, State);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the MTM service response
     *
     * @param List<String> Continuation Labels
     * @param Object State of Continuation
     * @return Object
     */
    @RemoteAction
    public static object retrieveUpdatedMTM(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveUpdatedMTM(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit (consentBeginDate) details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateConsentBeginDate(string enterprise, string networkId, String sRecordId)
    {
        return Pharmacy_S_HUM.prepareUpdateConsentBeginDate(enterprise, networkId, sRecordId);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit (consentEndDate) details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateConsentEndDate(string enterprise, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.prepareUpdateConsentEndDate(enterprise, networkID, sRecordId);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the Member Consent service response
     *
     * @return Object
     */
    @RemoteAction
    public static object retrieveUpdatedMemberConsent(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveUpdatedMemberConsent(labels, State);
    }
        
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateGetMember(string enterprise, string phone, string AltPhone, string Email, string captype, string networkId,String sRecordId,AddressValidationHelper_DTO_HUM addressDto,String IsDeclined)
    {
        return Pharmacy_S_HUM.prepareEditGetMember(enterprise,phone,AltPhone,Email,captype,networkId,sRecordId,addressDto,IsDeclined);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    @RemoteAction
    public static object retrieveUpdatedGetMember(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveUpdatedGetMember(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the EAS continuation requests
     *
     * @return continuation object
     */
    @RemoteAction 
    public static Continuation invokeEASService(AddressValidationHelper_DTO_HUM addressDto)
    {   
        return Pharmacy_S_HUM.validateAddress(addressDto);
    }
   
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMember details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeGetMemberService(String memID,string networkId, String sRecordId)
    {
        return Pharmacy_S_HUM.SendRequestGetMemberResponse(memID,networkId, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseRSGetMemberResponse(List < string > labels, object State)
    {
        return Pharmacy_D_HUM.parseRSGetMemberResponse(labels, State);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object addressResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.addressResponse(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeEpostOrder(String memID, String startDate, String endDate, string networkId, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestGetOrderResponse(memID, startDate, endDate,networkId, sRecordId);
    }
    
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Get Member details continuation requests for Log Notes
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeOMSLogNotes(String memID, String startDate, String endDate, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestGetMemberResponse(memID, startDate, endDate, networkID, sRecordId);
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseRSGetOrderResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseRSGetOrderResponse(labels, State);
    }
    
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Member Prescription details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeEpostMemberPrescription(String memID,String scriptKey, String startDate, String endDate, string networkId, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestGetScript(memID,scriptKey, startDate, endDate, networkId, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseRSGetScriptResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseRSGetScriptResponse(labels, State);
    }
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeOrderDetail(String orderKey, String OrderLevel, string networkID, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestGetOrderDetailResponse(orderKey,OrderLevel, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseRSGetOrderDetailResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseRSGetOrderDetailResponse(labels, State);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object processChildOrder(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.processChildOrder(labels, State);
    }
    
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost createEditOrder details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokecreateEditOrderDetail(RSCreateAndEditOrderRequest_DTO_HUM createEditObj, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestCreateEditOrderDetailRequest(createEditObj, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseCreateEditOrderResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseCreateEditOrderResponse(labels, State);
    }
   
      /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost createEditOrder details continuation requests
     *
     * @return continuation object
     */
     @RemoteAction
    public static Continuation sendRequestReleaseAndCancelOrder(String OrderKey, String EnterpriseId, boolean isCancel, string networkID, String sRecordId,boolean overrideconsent,Boolean copayconsent,string lstScriptKey)
    {
        return PharmacyOrderDetail_S_HUM.sendRequestReleaseAndCancelOrder( OrderKey,  EnterpriseId,isCancel, networkID, sRecordId,overrideconsent,copayconsent,lstScriptKey);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object  processRelOrderResponse(List < string > labels, Object State)
    {
        return PharmacyOrderDetail_S_HUM.processRelOrderResponse( labels,  State);
    }
       

      /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation callGetOrder(String orderKeyValue,String startDate,String endDate,String enterpriseId, String CurrentQueueName, string networkID, String sRecordId)
    {
        return PharmacyOrderDetail_S_HUM.callGetOrder( orderKeyValue, startDate, endDate, enterpriseId,  CurrentQueueName, networkID, sRecordId);
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object processGetOrderResponse(List < string > labels, Object State)
    {
        return PharmacyOrderDetail_S_HUM.processGetOrderResponse(labels, State);
    }

    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    Public Static Continuation getOrderData(String orderKeyValue,String source,String startDate,String endDate,String enterpriseId, String networkID, String sRecordId)
    {
        Return PharmacyOrderDetail_S_HUM.invokeOrderDetail(orderKeyValue, source, startDate, endDate, enterpriseId, networkID, sRecordId);
    }
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    Public Static Object ProcessOrderResponse(List < String > labels, Object State)
    {
        Return PharmacyOrderDetail_S_HUM.ProcessOrderResponse(labels, State);
    }
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    Public Static Object processChildOrderDetails(List < String > labels, Object State)
    {
        Return PharmacyOrderDetail_S_HUM.processChildOrder(labels, State);
    }
	
	/**
	 * Returns request object of continuation object
	 * <p>
	 * Method to invoke the getpAuth details continuation requests
	 *
	 * @param  String Enterprise Id
	 * @return continuation object
	 */
	@RemoteAction
	public static Continuation invokeGetpAuthService(String sMemID, String accID)
	{
		return PharmacyBenefits_S_HUM.SendRequestGetpAuthResponse(sMemID,accID);
	}

	/**
	 * Returns responses of continuation object
	 * <p>
	 * Callback Method to parse the PA hub service response
	 *
	 * @param  List<String> List of Continuation Labels
	 * @param  Object State of Continuation
	 * @return Object
	 */
	public static object parseGetPAuthResponse(list<string> Labels, object state)
	{
		return PharmacyBenefits_S_HUM.parseGetPAuthResponse(Labels, state);
	}
	
   	/*
	* Method Name : getInteractionQueryResults
	* Description : This method is used to get interaction details
	*/
	@AuraEnabled(cacheable=true)
	public static String getInteractionQueryResults(String interActionRecordId){
	   if(!String.isBlank(interActionRecordId)){
		   String recId = interActionRecordId.removeStart('"').removeEnd('"');        
			return [select id,Interacting_With_type__c,Caller_Name__c
							 from Interaction__c where Id =:recId limit 1].Interacting_With_type__c;
	   }
	   return '';
	}

    /**
     * Returns request object of continuation object
     * <p>
     * @param  String oPolMem Id
     * @return continuation object
     */
    
   @RemoteAction
    public static Continuation invokeMemElig100DayPrescripService(String oPolMem)
    { 
     return Pharmacy_S_HUM.sendRequestIsMemElig100DayPrescrip(oPolMem);
    }
    
    
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseIsMemElig100DayServiceResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseIsMemElig100DayServiceResponse(labels, State);
    }
    
}