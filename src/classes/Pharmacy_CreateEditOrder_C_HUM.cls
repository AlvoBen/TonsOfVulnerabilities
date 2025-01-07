/*******************************************************************************************************************************
Apex Class Name : Pharmacy_CreateEditOrder_C_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Class contains methods for services to fetch Pharmacy CreateeditOrder information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rajesh Chandgothia                                   04/18/2017                 REQ - 305689: Original Version
* Rajesh Keswani                                       05/02/2017                 REQ - 310758: Add a New item link for Converting Order Edit to Subtab
* Rajesh Keswani                                       05/02/2017                 REQ - 310759: Modify Shipping address in Converting Order Edit/create to Subtab
* Rajesh Keswani                                       05/02/2017                 REQ - 310760: add And update credit cards -- Converting Order Edit/create to Subtab
* Sharan Shanmugam									   07/27/2017				  REQ - 323542: Exception handling
* Ravi Prakash kantamneni   345989                     03/21/2019                 REQ - 383718: Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Sumit Pharande                                       03/20/2019                 REQ - 362063: Payment Card Project: do Not allow users to inactivate a cc that’s attached to an order in progress
* Ashok Kumar                                          04/18/2019                 Defect 411231.0001 fix
* Prathyu Tushar			355095					   05/02/2019				  REQ - 397913: CA Incident # 8244067, Removed select order attribute.
* Riya Agarwal										   06/08/2020				  Added switch functionality for REQ - 1041836 and REQ - 941028
* Kiran Kumar										   04/24/2021				  User Story - 2066174: Automatically Record Credit Card Additions to Case: Create Order Page (RxRF)
* Pallavi Shewale                                      06/08/2021                 USER STORY 1491784: T1PRJ0003815- CR 01 - Edit/Cancel/Release Links for Finance
* Kiran Kumar								 		   06/07/2021				  User Story - 2273666: Switch Clean Up - Automatically Record Credit Card Additions to Case: Create Order Page (CRMS)
* AshokKumar Nutalapati								   10/19/2021				  User Story - 660769: T1PRJ0002517 - MF 4 - Update Order Payment Method Fields (RxRF) (ID# 74)
* Bhavani Tati								 		   05/20/2022				  User Story - 3175324: T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Update Order Payment Method Fields (CRMS) (ID# 74)
*****************************************************************************************************************************************/

 public with sharing class Pharmacy_CreateEditOrder_C_HUM
 {
   public string capType {get;set;}
   public string orderNo{get;set;}
   public string createEditflag{get;set;}
   public string osStartDate{get;set;}
   public string osEndDate{get;set;}
   public string payer{get;set;}
   public string enterpriseID{get;set;}
   public string selectedScript {get;set;} 
   public boolean bCusAddPresPopFlag {get;set;}
   public boolean bShowModCusPopFlag {get;set;}
   public boolean bConfirmFlag {get;set;}
   public boolean bUpdateCreditCardPopFlag {get;set;}
   public boolean bsoftMsgFlag {get;set;}
   public boolean bNewCreditCardPopFlag {get;set;}
   Public String sPCIEndPoint{Get;Set;}
   Public String recordId{Get;Set;}
   
   Public String pmId{Get;Set;}
   Public String rxMap{Get;Set;}
   //REQ-1041836
   public Boolean bSwitchCreditCard{ get; set;}
   //REQ-941028
   public Boolean bSwitchConsentStatus{ get; set;}
   public Boolean bSwitchERC {get;set;}
   

   
    /**
    * constructor of the class
    * <p>
    * @param  NA
    * 
    * @return NA
    */
   public Pharmacy_CreateEditOrder_C_HUM()
    {
        bSwitchCreditCard=  HUMUtilityHelper.isCRMFunctionalityON('1041836');
        bSwitchConsentStatus=  HUMUtilityHelper.isCRMFunctionalityON('941028');
        bSwitchERC  = HUMUtilityHelper.isCRMFunctionalityON('1491784');
		


        sPCIEndPoint = HUM_Webservice_Callout__c.getValues('PCI').End_Point_URL__c +HUM_Webservice_Callout__c.getValues('PCI').Service_Name__c;
        bCusAddPresPopFlag = false;
        bShowModCusPopFlag = false;
        bConfirmFlag = false;
        bsoftMsgFlag = false;
        bNewCreditCardPopFlag = false;
        bUpdateCreditCardPopFlag = false;
        capType ='';
        orderNo='';
        osStartDate = '';
        osEndDate='';
        payer='';
        selectedScript ='';
        recordId = '';

        pmId = '';

        createEditflag=  ApexPages.currentPage().getParameters().get('CEflag');    
          if(!String.IsBlank(createEditflag)) 
           {
             createEditflag = EncodingUtil.urlDecode(createEditflag, 'UTF-8');
            if (createEditflag.indexOf('editOrderSummary') != -1 || createEditflag.indexOf('pharmacyEditOrder')!= -1 )
            {
             orderNo = ApexPages.currentPage().getParameters().get('orderNo');
             if(!String.IsBlank(orderNo)) orderNo= EncodingUtil.urlDecode(orderNo, 'UTF-8');
             enterpriseID = ApexPages.currentPage().getParameters().get('EnterpriseID');
             if(!String.IsBlank(enterpriseID)) enterpriseID = EncodingUtil.urlDecode(enterpriseID , 'UTF-8'); 
             osStartDate = ApexPages.currentPage().getParameters().get('osStartDate');
             if(!String.IsBlank(osStartDate)) osStartDate = EncodingUtil.urlDecode(osStartDate, 'UTF-8'); 
             osEndDate = ApexPages.currentPage().getParameters().get('osEndDate');
             if(!String.IsBlank(osEndDate)) osEndDate = EncodingUtil.urlDecode(osEndDate , 'UTF-8'); 
             payer = ApexPages.currentPage().getParameters().get('payer');
             if(!String.IsBlank(payer)) payer = EncodingUtil.urlDecode(payer, 'UTF-8');   
             recordId = ApexPages.currentPage().getParameters().get('recordId');
             if(!String.IsBlank(recordId)) recordId = EncodingUtil.urlDecode(recordId, 'UTF-8');
             
             pmId =  ApexPages.currentPage().getParameters().get('pmId');
             if (!String.IsBlank(pmId)) pmId = EncodingUtil.urlDecode(pmId, 'UTF-8');

             }
             else if (createEditflag.indexOf('create') != -1 )
            {
             selectedScript = ApexPages.currentPage().getParameters().get('scripts');
             if(!String.IsBlank(selectedScript)) selectedScript = EncodingUtil.urlDecode(selectedScript , 'UTF-8');
             enterpriseID = ApexPages.currentPage().getParameters().get('EnterpriseID');
             if(!String.IsBlank(enterpriseID)) enterpriseID = EncodingUtil.urlDecode(enterpriseID , 'UTF-8'); 
             osStartDate = ApexPages.currentPage().getParameters().get('osStartDate');
             if(!String.IsBlank(osStartDate)) osStartDate = EncodingUtil.urlDecode(osStartDate, 'UTF-8'); 
             osEndDate = ApexPages.currentPage().getParameters().get('osEndDate');
             if(!String.IsBlank(osEndDate)) osEndDate = EncodingUtil.urlDecode(osEndDate , 'UTF-8'); 
             payer = ApexPages.currentPage().getParameters().get('payer');
             if(!String.IsBlank(payer)) payer = EncodingUtil.urlDecode(payer, 'UTF-8');   
             recordId = ApexPages.currentPage().getParameters().get('recordId');
             if(!String.IsBlank(recordId)) recordId = EncodingUtil.urlDecode(recordId, 'UTF-8');
             
             pmId =  ApexPages.currentPage().getParameters().get('pmId');
             if (!String.IsBlank(pmId)) pmId = EncodingUtil.urlDecode(pmId, 'UTF-8');

             }
          
            }
         }
         
         
     /*
     * Method Name : openAddPrespopUp
     * Description : flag to open pop up  
     * Return type : Void
     * Parameter   : NA
     */
     public void openAddPrespopUp()
    {
        bCusAddPresPopFlag = true;
    }
    /*
     * Method Name : cancelModConfirmAddress
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void cancelModConfirmAddress()
    {
        bConfirmFlag = false;
        bShowModCusPopFlag = true;
    }
    
    /*
     * Method Name : toggleModConfirmFlag
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
     public void toggleModConfirmFlag()
    {
        bsoftMsgFlag = false;
        bShowModCusPopFlag = false;
        bConfirmFlag = true;
    }
    /*
     * Method Name : closeAddPrespopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void closeAddPrespopUp()
    {
        bCusAddPresPopFlag = false;
    }
    
    /*
     * Method Name : showModCusPopUp
     * Description : flag to open pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void showModCusPopUp()
    {
        bShowModCusPopFlag = true;
    }
    
    /*
     * Method Name : closeConfirmPopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void closeConfirmPopUp()
    {
        bConfirmFlag = false;
    }
    
    /*
     * Method Name : showZipCityMsg
     * Description : flag to show zip city message pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void showZipCityMsg()
    {
        bsoftMsgFlag = false;
        bConfirmFlag = false;
        bShowModCusPopFlag = true;
    }
    
    /*
     * Method Name : disableModCusPopUp
     * Description : flag to open pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void disableModCusPopUp()
    {
        bShowModCusPopFlag = false;
    }   
    
    /*
     * Method Name : toggleConfirmFlag
     * Description : flag to open pop up  
     * Return type : Void
     * Parameter   : NA
     */ 
    public void toggleConfirmFlag()
    {
        bConfirmFlag = true;
    }
    
    /*
     * Method Name : cancelConfirmAddress
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void cancelConfirmAddress()
    {
        bConfirmFlag = false;
    } 
    
    /*
     * Method Name : cancelSoftMsgPopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void cancelSoftMsgPopUp()
    {
        bsoftMsgFlag = false;
        bShowModCusPopFlag = true;
    }
    
    /*
     * Method Name : openSoftMsgPopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void openSoftMsgPopUp()
    {
        bsoftMsgFlag = true;
        bShowModCusPopFlag = false;
    }
    
    /*
     * Method Name : openSoftMsgPopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void showNewCreditCardSection()
    {
        bNewCreditCardPopFlag = true;
    }
    
    /*
     * Method Name : openSoftMsgPopUp
     * Description : flag to hide pop up  
     * Return type : Void
     * Parameter   : NA
     */
    public void hideNewCreditCardSection()
    {
        bNewCreditCardPopFlag = false;
        bUpdateCreditCardPopFlag = false;
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
    * flag to close pop up for creating Financial Request
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void closeUpdatePopUp()
    {
        bUpdateCreditCardPopFlag = false;
    }
    
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get member and script details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation getMemberAndScriptData(String memID,String scriptKey,String startDate,String endDate, string networkID, String sRecordId)
    {
        return Pharmacy_CreateEditOrder_S_HUM.SendRequestCreateEditOrderPage(memID,scriptKey,startDate,endDate, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseMemberAndScriptResponse(List < string > labels, object State)
    {
        return Pharmacy_CreateEditOrder_S_HUM.parseMemberAndScriptResponse(labels, State);
    }
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeOrderDetail(String orderKey, String OrderLevel, string networkID, String sRecordId)
    {
        return Pharmacy_CreateEditOrder_S_HUM.sendRequestGetOrderDetailResponse(orderKey,OrderLevel, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseGetOrderDetailResponse(List < string > labels, object State)
    {
        return Pharmacy_CreateEditOrder_S_HUM.parseGetOrderDetailResponse(labels, State);
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
        return Pharmacy_CreateEditOrder_S_HUM.sendRequestCreateEditOrderDetailRequest(createEditObj, sRecordId);
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
        return Pharmacy_CreateEditOrder_S_HUM.parseCreateEditOrderResponse(labels, State);
    }
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Log Note continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeLogNoteRequest(Integer numberOfConsentGiven, string sMemID, string networkID, String sRecordId)
    {
        return Pharmacy_CreateEditOrder_S_HUM.sendRequestGetEditMemberForScripts(numberOfConsentGiven, sMemID, networkID, sRecordId);
    }

     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to the Log note service response
     *
     * @return Object
     */
    public static object logNoteResponse(List < string > labels, object State)
    {
        return  Pharmacy_CreateEditOrder_S_HUM.parseLogNoteResponse(labels, State);
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
     * Method to invoke the getMemberEdit details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation updateGetMember(string enterprise, string phone, string AltPhone, string Email, string captype, string networkId, String sRecordId, AddressValidationHelper_DTO_HUM addressDto)
    {
        return Pharmacy_S_HUM.prepareEditGetMember(enterprise,phone,AltPhone,Email,captype,networkId,sRecordId,addressDto,'');
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object retrieveUpdatedGetMember(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.retrieveUpdatedGetMember(labels, State);
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
     * Method to invoke the get Order Details continuation requests
     *
     * @return continuation object
     */
	 @RemoteAction
    public static Continuation createGetOrderDetailRequest(String sOrderNumber , String snetworkId)
    {
        return PharmacyFinancial_S_HUM.prepareGetOrderDetail(sOrderNumber,snetworkId);
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
        return Pharmacy_CreateEditOrder_S_HUM.sendRequestGetOrderResponse(memID,networkId, sRecordId);
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
    public static object parseRSGetOrderResponse(List < string > labels, object State)
    {
        return Pharmacy_CreateEditOrder_S_HUM.parseRSGetOrderResponse(labels, State);
    }
     /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMember details continuation requests
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
     * Callback Method to parse the Get member service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    public static object parseRSGetMemberResponseFinance(List < string > labels, object State)
    {
        return Pharmacy_CreateEditOrder_S_HUM.parseRSGetMemberResponseFinance(labels, State);
    }
   }