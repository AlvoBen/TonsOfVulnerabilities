/*******************************************************************************************************************************
Apex Class Name : PharmacyFinancial_D_HUM.cls
Version         : 1.0
Created On      : 20th Dec 2016
Function        : Data Class for PharmacyFinancial_C_HUM.
TestClass       : PharmacyFinancial_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey           70274              		12/20/2016                   Original Version (REQ 291096)
* Sharan Shanmugam			70274				  	02/16/2017					 Implementation of REQ - 296663 :  View credit card information from getMember details 
* Vishal Verma				70274					02/20/2017					 Implementation of REQ - 296664 : View payment information from GetMemberFinanceAndAdjustments details
* Vishal Verma				74192			        08/03/2017					 Implementation of REQ - 296665 : View adjustment information from GetMemberFinanceAndAdjustments details
* Sharan Shanmugam                               	03/21/2017                   REQ - 296877 - Update Credit Card information
* Sharan Shnamugam          78304                   03/22/2017                   REQ - 300147 : Create finance request
* Rajesh Keswani            82227                   03/24/2017               	 REQ - 302791: financial offline service mode 
* Vishal Verma              82890                   04/07/2017                   REQ - 301358 : Send Email for financial request
* Rajesh Keswani            93585		            05/12/2017                   Offline mode including IncludeStaledata -> REQ - 313437
* Rajesh Keswani            112036	                07/20/2017                   Implementation of REQ - 323389 - adding networkId in service request
* Sharan Shanmugam									07/27/2017					 REQ - 323542 : Exception handling 
* Sumit Pharande            345615                  03/27/2019                   REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that’s attached to an order in progress
* Shailesh Mali                                     08/26/2020                   REQ - 1152078 - PR00094920- MF 1  - Humana Pharmacy- Ability to create realtime one time credit card charges in CRM  (RxXP) (ID# 13)
* Ashok Kumar Nutalapati                            10/28/2020                   REQ - 1488293 - Disable Credit Card Information Link & Button When HP Page Accessed from Legacy Deleted Policy (RxRF)
* Suraj Patil                            			08/09/2020                   REQ - 2085772 - T1PRJ0002736 - 2021 MF6 - Security (PR00094226) - CR AEP Readiness - High Security Issues
* Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix
*****************************************************************************************************************************************/
public with sharing class PharmacyFinancial_D_HUM 
{
	Public Static final String PHARMACY_FINREQUESTROW1_HUM = 'Member Name'; 
	Public Static final String PHARMACY_FINREQUESTROW2_HUM = 'Humana Id Number'; 
	Public Static final String PHARMACY_FINREQUESTROW3_HUM = 'Account  Number'; 
	Public Static final String PHARMACY_FINREQUESTROW4_HUM = 'Order Number'; 
	Public Static final String PHARMACY_FINREQUESTROW5_HUM = 'Rx# and Name'; 
	Public Static final String PHARMACY_FINREQUESTROW6_HUM = 'Request/Concern'; 
	Public Static final String PHARMACY_FINREQUESTROW7_HUM = 'Submitted By'; 
	Public Static final String PHARMACY_FINREQUESTBEGINTABLE_HUM = '<table border ="1" width="60%"><tbody>';
	Public Static final String PHARMACY_FINREQUESTBODY1_HUM = '<tr><td>&nbsp;';
	Public Static final String PHARMACY_FINREQUESTBODY2_HUM = '</td><td >&nbsp';
	Public Static final String PHARMACY_FINREQUETSBODY3_HUM = '</td></tr>';
	Public Static final String PHARMACY_FINREQUESTENDTABLE_HUM = '</tbody></table>';
	Public Static final String PHARMACY_FINREQUESTSUBJECT_HUM = 'Traditional Mail- Member Invoice Copy/ Reimbursement Requests';
	Public Static final String PHARMACY_FINREQUESTEMAIL_HUM = 'HumanaPharmacyFinance@humana.com';
	Public Static final String NORMAL_HUM = 'Normal';
	Public Static final Integer NUMBER_CONSTANT_ONE_HUM = 1; 
	Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT'; 
	Public Static final Integer NUMBER_CONSTANT_MINUSONE_HUM = -1; 
    /**
    * Returns Policy_Member__C object that can then be utilized for showing Policy member Information.  
    * Exception is handled form the calling method  *
    * This method returns SOQL result of policy member.
    *
    * @param    sPolMemId       policy member ID.
    * @return  Policy_Member__c    return the policy member object
    */
    public Policy_Member__C getPolicyMemberData(String sPolMemId)
    {
		if(string.IsNotBlank(sPolMemId)) sPolMemId = sPolMemId.escapehtml4();
        return[Select Id,  Name,Member__c, Member__r.Enterprise_Id__c , Member__r.Name, ETL_Record_Deleted__c from  Policy_Member__c Where Id = :sPolMemId];
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
    
     /**
     * Returns RSGetAdjustmentAndPaymentResponseDTO_HUM  object that can then be utilized for sending the webservice request.
     * This method is to create the request Structure for GetAdjustmentAndPaymentResponseservice .
     *
     * @param  String Enterprise Id
     * @param  String Start Date
     * @param  String End Date   
     * @return  RSGetAdjustmentAndPaymentRequestDTO_HUM  request structure of the wrapper class RSGetAdjustmentAndPaymentResponseDTO_HUM 
     */
    public static RSGetAdjustmentAndPaymentRequestDTO_HUM RequestStructureGetAdjPay(String sMemberId , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID)
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
	
	/**
     * Returns email utility request  object
     * <p>
     * Method to call create structure  for email utility .
     *
     * @param sMemName                      String
     * @param sHumanaId                     String
     * @param sAccNumber                    String
     * @param sOrderNumber                  String
     * @param sRxNumber                     String
     * @param sConcern                      String
     * @param sNetworkUserId                String
     * @return                              request structure for email utility
    */
    public static EmailUtiltyRequest_DTO_HUM RequestStructureEmailUtilityService(String sMemName , String sHumanaIdNum , String sAccNum , String sOrderNumber , String sRxNumber , String sConcern , String sNetworkUserId)
    {
        Map<String,String> mapEmailBody = new Map<String,String>();
        mapEmailBody.put(PHARMACY_FINREQUESTROW1_HUM,sMemName);
        mapEmailBody.put(PHARMACY_FINREQUESTROW2_HUM,sHumanaIdNum);
        mapEmailBody.put(PHARMACY_FINREQUESTROW3_HUM,sAccNum);
        mapEmailBody.put(PHARMACY_FINREQUESTROW4_HUM,sOrderNumber);
        mapEmailBody.put(PHARMACY_FINREQUESTROW5_HUM,sRxNumber);
        mapEmailBody.put(PHARMACY_FINREQUESTROW6_HUM,sConcern);
        mapEmailBody.put(PHARMACY_FINREQUESTROW7_HUM,UserInfo.getName() +' (' +  sNetworkUserId + ') ' + System.now().format('MM/dd/yyyy hh:mm:ss a z',UserInfo.getTimeZone().toString()));
        
        String sTableBody  = PHARMACY_FINREQUESTBEGINTABLE_HUM;
        for(String s : mapEmailBody.keyset())
        {
            sTableBody += PHARMACY_FINREQUESTBODY1_HUM+s+ PHARMACY_FINREQUESTBODY2_HUM+mapEmailBody.get(s)+PHARMACY_FINREQUETSBODY3_HUM;
        }
        sTableBody += PHARMACY_FINREQUESTENDTABLE_HUM;
        
        EmailUtiltyRequest_DTO_HUM oRequest = new EmailUtiltyRequest_DTO_HUM();
        EmailUtiltyRequest_DTO_HUM.SendToDetail sendTo = new EmailUtiltyRequest_DTO_HUM.SendToDetail();
        List<String> lst = new List<String>{PHARMACY_FINREQUESTEMAIL_HUM};
        sendTo.MailTo = lst;
        EmailUtiltyRequest_DTO_HUM.EmailRequest emailRequest = new EmailUtiltyRequest_DTO_HUM.EmailRequest();
        emailRequest.FromAddress = UserInfo.getUserEmail();
        emailRequest.SendTo = sendTo;
        emailRequest.Subject = PHARMACY_FINREQUESTSUBJECT_HUM ;
        emailRequest.Content = sTableBody;
        emailRequest.Priority = NORMAL_HUM;
        emailRequest.IsHTMLContent= String.valueof(NUMBER_CONSTANT_ONE_HUM);
        oRequest.SendEmailRequest = emailRequest;
        return oRequest;
    }
    
    /**
     * Returns eedit member log note struture request  object
     * <p>
     * Method to call create structure  for Edit member log note call .
     *
     * @param sMemName                      String
     * @param sEnterpriseId                 String
     * @param sNetworkUserId                String
     * @return                              request structure for log note
    */
    public static RightSourceEditMemberRequest_DTO_HUM RequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId)
    {
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetail logNote = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetail();
        logNote.logNoteCode = PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM;
        logNote.logNoteDate = system.now().format('MM/dd/yyyy ');
        logNote.logNoteKey = String.valueof(NUMBER_CONSTANT_MINUSONE_HUM);
        logNote.logNoteMessage = sComment;
        logNote.logNoteUser = sNetworkUserId;
        List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail>  lstLogNote = new List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail> ();
        lstLogNote.add(logNote);
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetails logNotesObject = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetails ();
        logNotesObject.logNote = lstLogNote;
        memDetailObj.logNotes   = logNotesObject; 
        memDetailObj.EnterprisePersonID = sEnterpriseId;
        getEditMembReqObj.CustomerServiceId = sNetworkUserId;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;
        return oRequest;
    }
    
    /**
    
     * <p>
     * Method to call create structure  for Order summary .
     *
     * @param sMemName                      RightSourceGetOrderResponse_DTO_HUM 
     * @return                              request Order summary Wrapper Object
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
            if (orderRes.Orders != null)
            {
                RightSourceGetOrderResponse_DTO_HUM.OrdersDetail orderObj = orderRes.Orders;
                if (string.isNotBlank(orderRes.StaleData)) orderObject.staleData = orderRes.StaleData;
                if (orderObj.Order != null && orderObj.Order.size() > 0)
                {
                    for (RightSourceGetOrderResponse_DTO_HUM.OrderDetail obj: orderObj.Order)
                    {
                        wrapperObj = new PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM();
                        if (string.isNotBlank(obj.OrderNumber)) wrapperObj.OrderNumber = obj.OrderNumber;
                        if (string.isNotBlank(obj.OrderCreationDate)) wrapperObj.OrderCreationDate = obj.OrderCreationDate;
                        if (string.isNotBlank(obj.OrderReleaseDate) && obj.OrderCreationDate != obj.OrderReleaseDate) wrapperObj.OrderReleaseDate = obj.OrderReleaseDate;
                        if (string.isNotBlank(obj.OrderSource)) wrapperObj.OrderSource = obj.OrderSource;
                        if (string.isNotBlank(obj.QueueName)) wrapperObj.QueueName = obj.QueueName;
                        if (string.isNotBlank(obj.OrderTotalAmount)) wrapperObj.OrderTotalAmount = obj.OrderTotalAmount;
                        if (string.isNotBlank(obj.OrderpayAmount)) wrapperObj.OrderpayAmount = obj.OrderpayAmount;
                        if (string.isNotBlank(obj.OrderTaxAmount)) wrapperObj.OrderTaxAmount = obj.OrderTaxAmount;
                        if (string.isNotBlank(obj.ExternalOrderKey)) wrapperObj.OCP = obj.ExternalOrderKey;
                        if (string.isNotBlank(obj.OrderType)) wrapperObj.OrderType = obj.OrderType;
                        if (string.isNotBlank(obj.OrderStatusLiteral)) wrapperObj.OrderStatusLiteral = obj.OrderStatusLiteral;
                        if (string.isNotBlank(obj.OrderStatus)) wrapperObj.OrderStatus = obj.OrderStatus;
                        if (string.isNotBlank(obj.OrderShipDate)) wrapperObj.OrderShipDate = obj.OrderShipDate;
                        wrapperObjList.add(wrapperObj);
                    }
                }
            }
        }
       orderObject.dtoList =  wrapperObjList;
       
       return orderObject;
    } 
    
     public static PharmacyEditMember_Order_DTO_HUM prepareOrderDetailsFinance(RightSourceEditMemberResponse_DTO_HUM oResponseEditMember)
    {
          try
         {
            PharmacyEditMember_Order_DTO_HUM orderObj ;
            List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM> wrapperObjList = new List<PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM>(); 
            PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM wrapperObj;
            if (oResponseEditMember != null && oResponseEditMember.EditMemberResponse != null)
            {
                orderObj = new PharmacyEditMember_Order_DTO_HUM(); 
                RightSourceEditMemberResponse_DTO_HUM.EditMemberResponse memberRes = oResponseEditMember.EditMemberResponse;
                if (memberRes.Orders != null)
                {
                    orderObj= new PharmacyEditMember_Order_DTO_HUM();
                    RightSourceEditMemberResponse_DTO_HUM.Orders custObj = memberRes.Orders ;     
                        
                        if (custObj != null && custObj.Order.size() > 0)
                        {   
                          for(RightSourceEditMemberResponse_DTO_HUM.Order obj: custObj.Order)
                          {
                            wrapperObj = new PharmacyEditMember_Order_DTO_HUM.PharmacyEditMember_Order_wrapper_HUM();
                            if(string.isNotBlank(obj.orderKey)) wrapperObj.orderKey=obj.orderKey;
                            if(string.isNotBlank(obj.orderStatus)) wrapperObj.orderStatus=obj.orderStatus;
                            wrapperObjList.add(wrapperObj);
                          }
                        }
                 }
            }
            orderObj.Orders=wrapperObjList;
            return orderObj ;
        }
          catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex, 'PharmacyFinancial_S_HUM', 'prepareOrderDetailsFinance');
                return null;
            }
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
   
}