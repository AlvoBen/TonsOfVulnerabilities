/*****************************************************************************************************************************
Apex Class Name : PharmacyFinancial_LD_HUM
Version         : 1.0
Created On      : 09/24/2021
Function        : Class contains methods for services to fetch Pharmacy Financial information.
Test Class      : PharmacyFinancial_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Swapnali Sonawane                                    09/24/2021     
*    2.0          Jonathan Dickinson                                   s11/05/2021                  added functionality from non-lightning class to create and edit 
*	 3.0          Monali Jagtap									       07/17/2023 		            USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy                                                                                                 credit card information                                                                                                             credit card information           
*******************************************************************************************************************************************************************/
public with sharing class PharmacyFinancial_LD_HUM {
    /** for mem Adjustment
     * Returns RSGetAdjustmentAndPaymentResponseDTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetAdjustmentAndPaymentResponseservice .
     *
     * @param  String Enterprise Id
     * @param  String Start Date
     * @param  String End Date   
     * @return  RSGetAdjustmentAndPaymentRequestDTO_HUM  request structure of the wrapper class RSGetAdjustmentAndPaymentResponseDTO_HUM 
     */ 
    public static PharmacyEditMember_Order_DTO_HUM orderObj ;
    public static List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM> wrapperObjList;
    public static PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM wrapperObj;
    public static RightSourceEditMemberResponse_DTO_HUM.EditMemberResponse memberRes;
        
    @AuraEnabled(continuation=true cacheable=true) 
    public static RSGetAdjustmentAndPaymentRequestDTO_HUM RequestStructureGetAdjPay(String sMemberId , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID, String sRecordId)
    {
        
        RSGetAdjustmentAndPaymentRequestDTO_HUM  oRequest = new RSGetAdjustmentAndPaymentRequestDTO_HUM ();
        RSGetAdjustmentAndPaymentRequestDTO_HUM.AdjustmentsAndPaymentsRequest objAdjustmentsAndPaymentsRequest = new RSGetAdjustmentAndPaymentRequestDTO_HUM.AdjustmentsAndPaymentsRequest  ();
        objAdjustmentsAndPaymentsRequest.EnterprisePersonID = sMemberId;
        objAdjustmentsAndPaymentsRequest.StartDate = sStartDate; 
        
        objAdjustmentsAndPaymentsRequest.EndDate = sEndDate;
        objAdjustmentsAndPaymentsRequest.IncludeStaleData = 'true';
        objAdjustmentsAndPaymentsRequest.CustomerServiceId = networkID;
        objAdjustmentsAndPaymentsRequest.IncludePayments  =  sPayment;
        objAdjustmentsAndPaymentsRequest.IncludeAdjustments  = sAdjustment;
        oRequest.GetAdjustmentsAndPaymentsRequest = objAdjustmentsAndPaymentsRequest;
        
        return oRequest;
        
    }
       /** For mem Adjustment
     * Returns RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails objectfor populating payment details.
     * This method is to create the data for PaymentDetails from GetAdjustmentAndPayment .
     *
     * @param   RSGetAdjustmentAndPaymentResponseDTO_HUM DTO Response 
     * @return  RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails request structure of the wrapper class RSGetAdjustmentAndPaymentResponseDTO_HUM 
     */
    public static RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse preparePaymentDetails(RSGetAdjustmentAndPaymentResponseDTO_HUM oResponseGetMember)
    {
     
        RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse oResponse = new RSGetAdjustmentAndPaymentResponseDTO_HUM.AdjustmentsAndPaymentsResponse(); 
        if(oResponseGetMember != NULL && oResponseGetMember.GetAdjustmentsAndPaymentsResponse != NULL)
        {
            if(oResponseGetMember.GetAdjustmentsAndPaymentsResponse.Payments != NULL) oResponse.Payments = oResponseGetMember.GetAdjustmentsAndPaymentsResponse.Payments;
            if(oResponseGetMember.GetAdjustmentsAndPaymentsResponse.Adjustments != NULL) oResponse.Adjustments = oResponseGetMember.GetAdjustmentsAndPaymentsResponse.Adjustments;
            
        }
         
        return oResponse;
    }

        /**
     * Returns PharmacyGetMember_CreditCard_DTO_HUM objectfor populating Credit card details.
     * This method is to create the data for CreditCardDetails from GetMemberResponseservice .
     *
     * @param   RightSourceGetMemberResponse_DTO_HUM DTO Response 
     * @return  PharmacyGetMember_CreditCard_DTO_HUM request structure of the wrapper class RightSourceGetMemberRequest_DTO_HUM 
     */
    public static PharmacyGetMember_CreditCard_DTO_HUM prepareCreditCardDetails(RightSourceGetMemberResponse_DTO_HUM oResponseGetMember)
    {
        PharmacyGetMember_CreditCard_DTO_HUM wrapperObj ;
        if (oResponseGetMember != null && oResponseGetMember.GetMemberReponse != null)
        {
            RightSourceGetMemberResponse_DTO_HUM.MemberReponse memberRes = oResponseGetMember.GetMemberReponse;
            if (memberRes.Customers != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj = memberRes.Customers;     
                    
                    if (custObj != null && custObj.Customer != null && custObj.Customer.size() > 0)
                    {   
                        wrapperObj = new PharmacyGetMember_CreditCard_DTO_HUM();
                        if(custObj.Customer[0].AccountNumber != null)
                        {
                            wrapperObj.sAccNumber = custObj.Customer[0].AccountNumber.CustomerValue;
                        }
                        if(custObj.Customer[0].StaleData != null)
                        {
                            wrapperObj.staleData = custObj.Customer[0].StaleData;
                        }
                        if (custObj.Customer[0].CreditCards != null && custObj.Customer[0].CreditCards.CreditCard.size() > 0)   wrapperObj.CreditCard = custObj.Customer[0].CreditCards.CreditCard;
                    }        
                if(custObj !=null && custObj.Customer!=null && custObj.Customer[0].Finance !=null){
                     RightSourceGetMemberResponse_DTO_HUM.FinanceDetail custFinance = custObj.Customer[0].Finance;
                     if (string.isNotBlank(custFinance.OutstandingBalance)) 
                         wrapperObj.BalanceDueFromDemographic = custFinance.OutstandingBalance;
                }
            }
        }
        return wrapperObj;
    }
    
    /**
     * Method to create Request structure for OneTimeCreditCardPayment (EditMember RightSource service)     
     * @param  string enterpriseId, string netWorkId, string creditCardKeyVar,decimal amountVar, date paymentDateVar,boolean isPaymentAuthorizedVar     
     * @return RSCreateOneTimePaymentRequest_DTO_HUM
    */  
    public static RSCreateOneTimePaymentRequest_DTO_HUM RequestStructureGetEditMemberPaymentRequest(string enterpriseId, string netWorkId, string creditCardKeyVar,decimal amountVar, date paymentDateVar,boolean isPaymentAuthorizedVar)
    {
        RSCreateOneTimePaymentRequest_DTO_HUM oRequest = new RSCreateOneTimePaymentRequest_DTO_HUM();
        RSCreateOneTimePaymentRequest_DTO_HUM.EditMemberRequest getEditMembReqObj = new RSCreateOneTimePaymentRequest_DTO_HUM.EditMemberRequest();
        RSCreateOneTimePaymentRequest_DTO_HUM.memberDetail memDetailObj = new RSCreateOneTimePaymentRequest_DTO_HUM.memberDetail();
   
        RSCreateOneTimePaymentRequest_DTO_HUM.Payments paymentDetailsObj = new RSCreateOneTimePaymentRequest_DTO_HUM.Payments();
        RSCreateOneTimePaymentRequest_DTO_HUM.Payment paymentDetailObj = new RSCreateOneTimePaymentRequest_DTO_HUM.Payment();
        paymentDetailObj.amount = amountVar;
        paymentDetailObj.creditCardKey = creditCardKeyVar;
        paymentDetailObj.isPaymentAuthorized = isPaymentAuthorizedVar;
        paymentDetailObj.paymentDate = paymentDateVar;    
        paymentDetailsObj.payment = new List<RSCreateOneTimePaymentRequest_DTO_HUM.payment>();
        paymentDetailsObj.payment.add(paymentDetailObj);
        memDetailObj.payments = paymentDetailsObj;        
        
        memDetailObj.EnterprisePersonID = enterpriseId;
        getEditMembReqObj.members = new List<RSCreateOneTimePaymentRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.CustomerServiceId = netWorkId;
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;
        
        return oRequest;
    }  


    /**
     * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for EditMemberResponseservice for add and update credit card
     *
     * @param    
     * @return  RightSourceEditMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceEditMemberRequest_DTO_HUM 
     */
    public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetEditMemberResponse(string enterpriseId, String sCreditCardKey, String exMonth, String exYear, String sActive, String sAutoCharge, String sFSA, String sFirstName, String sMiddleName, String sLastName, String sTokenKey,String sCreditType, String isInsert, string networkID)
    {
        
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
            
        RightSourceEditMemberRequest_DTO_HUM.creditCardDetails creditCardsObj = new RightSourceEditMemberRequest_DTO_HUM.creditCardDetails();
        RightSourceEditMemberRequest_DTO_HUM.creditCardDetail creditCardObj = new RightSourceEditMemberRequest_DTO_HUM.creditCardDetail();
        if(isInsert == 'false')
        {
	        creditCardObj.creditCardKey = sCreditCardKey;
	        creditCardObj.expirationMonth = exMonth;
	        creditCardObj.expirationYear = exYear;
	        creditCardObj.isActive = sActive;
	        creditCardObj.isAutoCharge = sAutoCharge;
	        creditCardObj.isSpendingAccount = sFSA;
        }
        else
        {
        	creditCardObj.creditCardKey = sCreditCardKey;
	        creditCardObj.expirationMonth = exMonth;
	        creditCardObj.expirationYear = exYear;
	        creditCardObj.isActive = sActive;
	        creditCardObj.isAutoCharge = sAutoCharge;
	        creditCardObj.isSpendingAccount = sFSA;	
	        creditCardObj.firstName = sFirstName;
	        creditCardObj.middleName = sMiddleName;
	        creditCardObj.lastName = sLastName;
	        creditCardObj.tokenKey = sTokenKey;
	        creditCardObj.creditCardType = sCreditType;
        }
        
        
        creditCardsObj.creditCard = new List<RightSourceEditMemberRequest_DTO_HUM.creditCardDetail>();
        creditCardsObj.creditCard.add(creditCardObj);
        memDetailObj.creditCards = creditCardsObj;
        
        memDetailObj.EnterprisePersonID = enterpriseId;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.CustomerServiceId = networkID;
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;
        return oRequest;
    }
   

    public static List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM> getWrapperObjList(PharmacyEditMember_Order_DTO_HUM orderObj,RightSourceEditMemberResponse_DTO_HUM.EditMemberResponse memberRes){
          if (memberRes.Orders != null)
                {
                    orderObj= new PharmacyEditMember_Order_DTO_HUM();
                    RightSourceEditMemberResponse_DTO_HUM.Orders custObj = memberRes.Orders ;     
                        
                        if (custObj != null && custObj.Order.size() > 0)
                        {   
                          for(RightSourceEditMemberResponse_DTO_HUM.Order obj: custObj.Order)
                          {
                              wrapperObj = new PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM();
                              if(string.isNotBlank(obj.orderKey) && string.isNotBlank(obj.orderStatus)){
                                wrapperObj.orderKey=obj.orderKey;
                                wrapperObj.orderStatus=obj.orderStatus;
                          }
                            wrapperObjList.add(wrapperObj);
                          }
                        }
                 }
        return wrapperObjList;
    }

    /**
     * Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetMemberResponseservice .
     *
     * @param String Enterprise ID   
     * @return  RightSourceGetMemberResponse_DTO_HUM  request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM 
     */
    public static RightSourceGetMemberRequest_DTO_HUM RequestStructureGetMemberResponse(String sMemID, string networkID)
    {
        RightSourceGetMemberRequest_DTO_HUM oRequest = new RightSourceGetMemberRequest_DTO_HUM();
        RightSourceGetMemberRequest_DTO_HUM.MemberRequest memRequest = new RightSourceGetMemberRequest_DTO_HUM.MemberRequest();
        memRequest.EnterprisePersonID = sMemID;
        memRequest.IncludeActiveLogNotes ='true';
        memRequest.IncludeActiveAddresses = 'true';
        memRequest.IncludeInactiveAddresses = 'true';
        memRequest.IncludeFinances = 'true';
        memRequest.IncludeAccountAdjustments = 'true';
        memRequest.IncludeConsentStatus = 'true';
        memRequest.IncludeActiveCreditCards = 'true';
        memRequest.IncludeInactiveCreditCards = 'true';
        memRequest.IncludeStaleData = 'true';
        memRequest.IncludeCmrStatus = 'true';
        memRequest.CustomerServiceId = networkID;
        oRequest.GetMemberRequest = memRequest;
        return oRequest;
    }
}