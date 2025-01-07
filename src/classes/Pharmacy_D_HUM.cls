/*******************************************************************************************************************************
Apex Class Name : Pharmacy_D_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Data Layer Class for Pharmacy_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani               57637                12/20/2016                 Original Version
* Rajesh Chandgothia           55992                12/29/2016                 Demographic functionality (REQ 291083)
* Rajesh Keswani               58175                01/22/2017                 Implementation of REQ - 296652 - Order Summary details from getOrder EPostRX
* Avinash Kumar Choubey        59057                01/17/2017                 REQ-291114 
* Rajesh Keswani               58348                01/27/2017                 Implementation of REQ - 296662 - View Address
* Shiva Pasumarty              60234                02/02/2017                 OMS Log Notes Req# REQ - 272225
* Rajesh Keswani               60569                02/03/2017                 Fixing Demo comments to put table in single section
* Rajesh Chandgothia           61773                02/09/2017                 Fixing Defect 258957.0001 ,258959.0001 
* Sumit Pharande               65665      	        02/21/2017                 REQ-300749 Change address presentation on the demographics section
* Rajesh Keswani               66266                02/21/2017                 Implementation of REQ - 291082 - Edit Demographic section
* Rajesh Chandgothia           70352                02/14/2017                 Member prescription Req# REQ - 296752
* Rajesh Chandgothias          70818                02/24/2017                 Defect 258965,258968 added plan type from policy member
* Rajesh Keswani	           73502                03/09/2017                 Defect 262758.0001 removing comma b/w state and zip
* Rajesh Chandgothias          74185                03/8/2017                  Defect 258973 member consent status
* Vishal Verma                 74544                03/09/2017                 Defect 263119.0001 : Added values to Start and End date while calling GetMember
* Sharan Shanmugam			   74899				03/09/2017				   Implementation of REQ - 296650 - Order details
* Rajesh Keswani               74554  	            03/03/2017                 REQ - 299426 Add and Edit address section
* Rajesh Chandgothia           75938                03/08/2017                 Edit order Req# REQ - 296651
* Avinash Choubey			   77115    			03/20/2017				   REQ 305004 Added method to get Payer Name
* Vishal Verma                 78304                03/16/2017                 REQ-296926 : Added MTM Indicator
* Albin Arul                   78959                03/24/2017                 REQ - 296721 : Implemented Member Prescription section - Member Consent changes
* Vamsi Kondragunta				81140   		    03/31/2017				   REQ - 301578 Added for Autorefil and captype update
* Albin Arul                   84917                04/17/2017                 REQ - 296721 : Member Consent Log note date and time formate changed
* Rajesh Keswani               74554  	            04/27/2017                 5/11 merge offline mode
* Rajesh Chandgothia           82476                04/06/2017                 REQ - 301571 : Member Prescription section-- color indicator
* Vamsi Kondragunta			   92532				05/12/2017				   277307.0001 updated the mapping for autorefil
* Rajesh Keswani               93585                05/12/2017                 Offline mode including IncludeStaledata -> REQ - 313437 
* Sumit Pharande               97655                05/31/2017                 Defect Fix 287189.0001
* Rajesh Keswani               112036               07/20/2017                 Implementation of REQ - 323389 - adding networkId in service request
* Rajesh Chandgothia		   114002   		    07/25/2017				   REQ - 323485, CMO CR 5227 (CSI 587)-- 1) remove MTM hover; 2) add 'order status' column; 3) stop translating to OPEN; 4) add 'finished date' to summary
* Sharan Shanmugam									07/27/2017				   REQ - 323542 : Exception handling
* Pradeep Dani				   302363		 		08/13/2018				   Added response node for duplicate customers.
* Joel George										10/31/2018				   REQ - 367769
* Joel George										11/29/2018				   REQ - 367769 v2 Added a check to ensure Address Validation is not changed
* Melkisan Selvaraj			   341652			    03/04/2019                 REQ - 387544 : Splunk error logging Issues
* Sumit Pharande               345615               03/27/2019                 REQ - 362063 - Payment Card Project: Do Not allow users to inactivate a cc that's attached to an order in progress
* Yogesh Gupta										05/28/2019				   REQ - 400046 Add error Message for member not found
* Lakshmi Madduri									10/30/2019					Parsing right source get member for customerkey
* AshokKumar Nutalapati								12/06/2019				   REQ - 408319 Disable 'Create Order' Button When HP Page Accessed from Legacy Deleted Policy (RxRF)
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
* Pallavi Shewale				    06/11/2021			US-1491784 Changes - Finance Queue
* Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant And Global Constant Fix
* Thaurn Madishetti                                 04/14/2023                 US-3837439: T1PRJ0714381- MF1: (PLT) 100 Day - CRM Supply Eligibility Indicator (Classic)
*********************************************************************************************************************************/
    public with sharing Class Pharmacy_D_HUM
    {
          Public Static final String MED = 'MED';
          Public Static Set<String> MEDICARE_SET = New Set<String>{'MCD','MEF','MEP','MER','MES','MGP','MGR','MPD','MRO','MRP','MSP','PDP','MAPD'};
          Public Static final Integer PHARMACY_STARTMONTH_HUM = -3;
          Public Static final String VOB_FAULT_CODE = 'faultcode';
          Public Static final Integer HTTP_OK = 200;
          Public Static final Integer HTTP_ACCEPTED = 202;
          Public Static final String HUM_MEMBER_NOT_FOUND = 'Member not found';
          Public Static final String DNC_PHARMACY_HUM = 'DNC';
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTECODE_HUM = 'CONSENT'; 
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGEFIRSTHALF_HUM ='Member consent given on ';
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEMESSAGESECONDHALF_HUM = ' est. Consent will expire in 365 days (Note EPOST WILL HANDLE CONSENT EXPIRATION)';
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEAT_HUM = ' at ';
          Public Static final String PHARMACY_MEMBERCONSENT_ENDDATE_HUM = '01/01/1970';
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERFIRSTHALF_HUM ='Member changed to Request Consent on Each Order on ';
          Public Static final String PHARMACY_MEMBERCONSENT_LOGNOTEREQUESTFOREACHORDERSECONDHALF_HUM = ' est.';
          Public Static final String PHARMACY_CURRENT_QUEUE = 'FINANCE';
               
        /**
         * Returns Policy_Member__C object that can then be utilized for showing pharmacy Information.    *
         * This method returns SOQL result of policy member.
         *
         * @param    pharmacyCls instance of the Controller class.
         * @return  Policy_Member__c  return the policy member object
         */
        public Policy_Member__C getPolicyMember_SOQL(Pharmacy_C_HUM pharmacyCls)
        {
            Policy_Member__c polId;
        Try
            {
                if(pharmacyCls != null && string.isNotBlank(pharmacyCls.sPolMemId))
                {
                    polId = [Select Id,  Name, Member__c, Member__r.Enterprise_Id__c,Member__r.Name,Product_Type__c,Product__c,Effective_Date__c,End_Date__c, ETL_Record_Deleted__c from  Policy_Member__c Where Id =: pharmacyCls.sPolMemId limit 1];
                }
            }
            catch (Exception ex)
            {
                HUMExceptionHelper.bRestrictLog = true;
                pharmacyCls.lstLogErrors = HUMExceptionHelper.logErrors(ex,'Pharmacy_D_HUM', 'getPolicyMember_SOQL');
            }
            return polId;
        }
    
        /**
         * Returns Boolean to determine if MTM should be shown on UI
         * This method is to verify if policy is Medicare / Medicaid
         *
         * @param    Policy member Object
         * @return  Boolean isMedicareMedicaid 
         */ 
        public static boolean isMedicareMedicaid(Policy_Member__c objPolMem)
        {
            boolean bMedicareMedicaid = false;
            if(objPolMem.Product_Type__c != NULL && objPolMem.Product__c != NULL)
            {
                if (objPolMem.Product__c == MED && MEDICARE_SET.contains(objPolMem.Product_Type__c)) bMedicareMedicaid = True;
               
            }
            return bMedicareMedicaid;
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
            memRequest.IncludeInactiveCreditCards = 'false';
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
         * Returns RightSourceGetMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
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
    
        /**
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
            
            if(addressDto != null)
            {
                RightSourceEditMemberRequest_DTO_HUM.addressDetails addressesObj = new RightSourceEditMemberRequest_DTO_HUM.addressDetails();
                RightSourceEditMemberRequest_DTO_HUM.addressDetail addressObj = new RightSourceEditMemberRequest_DTO_HUM.addressDetail();
                if(addressDto.addressReq != null)
                {
                    addressObj.addressLine1 = addressDto.addressReq.AddressLine1;
                    addressObj.addressLine2 = addressDto.addressReq.AddressLine2;
                    addressObj.city = addressDto.addressReq.city;
                    addressObj.stateCode = addressDto.addressReq.StateCode;
                    addressObj.zipCode = addressDto.addressReq.ZipCode;
                    addressObj.addressType = addressDto.addresstype;
                    addressObj.addressKey ='-1';
                    if(string.isNotBlank(addressDto.overrideReasonCode)) addressObj.overrideReasonCode = addressDto.overrideReasonCode;
                    
                    addressObj.isActive = (string.isNotBlank(addressDto.statusValue) && addressDto.statusValue == 'Active' ) ?  'true' :'false';
                    addressObj.uspsValidate = addressDto.addressValidatedFlag;
                    addressesObj.address = new List<RightSourceEditMemberRequest_DTO_HUM.addressDetail>();
                    addressesObj.address.add(addressObj);
                    memDetailObj.addresses = addressesObj;
                    getEditMembReqObj.CustomerServiceId = addressDto.networkID;
                }
            }
            else
            {
                RightSourceEditMemberRequest_DTO_HUM.customerDetail customerObj = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
                RightSourceEditMemberRequest_DTO_HUM.phoneDetails phDetails = new RightSourceEditMemberRequest_DTO_HUM.phoneDetails();
                RightSourceEditMemberRequest_DTO_HUM.customerPreference custPrefObj = new RightSourceEditMemberRequest_DTO_HUM.customerPreference();
                if(string.isNotBlank(phone)) phone = formatPhone(phone);
                if(string.isNotBlank(AltPhone)) AltPhone = formatPhone(AltPhone);
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
                if(IsDeclined == 'true'){
                    ComPrefObj.commPrefValue = 'true';
                }
                else{
                    ComPrefObj.commPrefValue = 'false';
                }
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
         * Returns string.
         * This method is to format the phone number for edit memeber request.
         *
         * @param    
         * @return string
         */
        private static string formatPhone(string phone)
            {
                If (String.isNotBlank(phone)) 
                {
                    list<string> phonelist = phone.split('-');
                    phone='';
                    for(string str : phonelist)
                    {
                        phone += str;
                    }
                }
                return phone;
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
            PharmacyGetMember_DTO_HUM objPharGetMemDTO = null;
            PharmacyGetMember_Demographic_DTO_HUM objPharDemographicDetails;
            List<PharmacyOMSLogNotes_DTO_HUM> objPharOMSDetails = null;
    
            HttpResponse responseDemographic = Continuation.getResponse(labels[0]);
            
            if (responseDemographic != null && !responseDemographic.getbody().containsIgnoreCase(VOB_FAULT_CODE) && ((responseDemographic.getStatusCode() == HTTP_OK || responseDemographic.getStatusCode() == HTTP_ACCEPTED) || Test.isRunningTest()))
            {
                String DemographyresposeBody = responseDemographic.getBody();
                RightSourceGetMemberResponse_DTO_HUM oResponseDemographic;
                oResponseDemographic = (RightSourceGetMemberResponse_DTO_HUM) System.JSON.deserialize(DemographyresposeBody, RightSourceGetMemberResponse_DTO_HUM.class);
                if (null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription != HUM_MEMBER_NOT_FOUND)
                {
                    RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail objCustomerInfo = Pharmacy_D_HUM.prepareCustomerKey(oResponseDemographic);				
                    objPharDemographicDetails = Pharmacy_D_HUM.prepareDemographicDetails(oResponseDemographic);
                    objPharOMSDetails = Pharmacy_D_HUM.OMSLogNotesResponse(oResponseDemographic);
                   //Adding parameter to hide error message on UI for member not found
                    objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(objPharDemographicDetails,getAllergiesAndHCondiotionFrmResponse(oResponseDemographic),objPharOMSDetails,null,getPayerName( oResponseDemographic), ((oResponseDemographic != null && oResponseDemographic.GetMemberReponse != null && oResponseDemographic.GetMemberReponse.DuplicateCustomers != null) ? oResponseDemographic.GetMemberReponse.DuplicateCustomers : null),null,false,objCustomerInfo);
                }
                //Add error message to show on UI for member not found message
                else if(null != oResponseDemographic && null != oResponseDemographic.GetMemberReponse && null != oResponseDemographic.GetMemberReponse.Customers && oResponseDemographic.GetMemberReponse.Customers.ErrorDescription == HUM_MEMBER_NOT_FOUND){
                    objPharGetMemDTO = new PharmacyGetMember_DTO_HUM(null,null,null,null,null,null,null,true,null);
                    
                }
            }
            else
            {
                // Fault Code
                Pharmacy_H_HUM.logHpFaultError(responseDemographic,'Pharmacy_D_HUM', 'parseRSGetMemberResponse', State != null ? String.valueof(State) : '');
            }
            return objPharGetMemDTO;
        }
        private static RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail prepareCustomerKey(RightSourceGetMemberResponse_DTO_HUM oResponse){
            boolean foundrecord=false;
            RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail sCustomerInfo;
            if(oResponse != null && oResponse.GetMemberReponse != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.MemberReponse memberRes = oResponse.GetMemberReponse;
                if(memberRes.Customers != null)
                {
                    RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj = memberRes.Customers;
                    if(custObj.Customer != null && custObj.Customer.size() > 0)
                    {
                        for(Integer i=0; i<custObj.Customer.size(); i++)
                        {
                            if(custObj.Customer[i].CustomerIdentity.CustomerKeys != null)
                            {
                                RightSourceGetMemberResponse_DTO_HUM.CustomerKeysDetail objCustKeys = custObj.Customer[i].CustomerIdentity.CustomerKeys;
                                for(Integer j=0; j<objCustKeys.CustomerKey.size();j++)
                                {
                                    if(objCustKeys.CustomerKey != null)
                                    {
                                        RightSourceGetMemberResponse_DTO_HUM.CustomerKeyDetail objCustomerKeyInfo= objCustKeys.CustomerKey[j];
                                        if(objCustomerKeyInfo != null)
                                        {
                                            if(objCustomerKeyInfo.CustomerType != null && objCustomerKeyInfo.CustomerType =='OC' && objCustomerKeyInfo.CustomerTypeLiteral=='OTC CARD')
                                            {
                                                foundRecord=true;
                                                sCustomerInfo = objCustomerKeyInfo;
                                            }
                                        }
                                    }
                                    if(foundRecord) break;
                                }
                            }
                            if(foundRecord) break;   					
                        }
                    }
                }
            }
            return sCustomerInfo;
        }
    
        /**
         * Returns PharmacyGetMember_Demographic_DTO_HUM objectfor populating Demographic details.
         * This method is to create the data for DemographicDetails from GetMemberResponseservice .
         *
         * @param    
         * @return  PharmacyGetMember_Demographic_DTO_HUM request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
         */
        public static PharmacyGetMember_Demographic_DTO_HUM prepareDemographicDetails(RightSourceGetMemberResponse_DTO_HUM oResponseGetMember)
        {
            PharmacyGetMember_Demographic_DTO_HUM wrapperObj = new PharmacyGetMember_Demographic_DTO_HUM();
            if (oResponseGetMember != null && oResponseGetMember.GetMemberReponse != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.MemberReponse memberRes = oResponseGetMember.GetMemberReponse;
                if (memberRes.Customers != null)
                {
                    RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custObj = memberRes.Customers;
                    if (custObj.Customer != null && custObj.Customer.size() > 0)
                    {
                        wrapperObj = new PharmacyGetMember_Demographic_DTO_HUM();
                        for (integer i = 0; i < custObj.Customer.size(); i++)
                        {                       
                            if (custObj.Customer[i].communicationPreferences.communicationPreference != null && custObj.Customer[i].communicationPreferences.communicationPreference.size() > 0)
                            {							
                                 for (integer j = 0; j < custObj.Customer[i].communicationPreferences.communicationPreference.size(); j++)
                                {
    
                                    if(custObj.Customer[i].communicationPreferences.communicationPreference[j].commPrefType =='EMDCL'){
                                        if(custObj.Customer[i].communicationPreferences.communicationPreference[j].commPrefValue == 'true'){
                                             
                                             wrapperObj.IsDeclined = true;
                                         }
                                    }
                                }
                            }
                            wrapperObj = wrapMemberInformation(custObj.Customer[i], wrapperObj);
                        }
                    }
                }
            }
            return wrapperObj;
        }
    /**
         * Returns RightSourceEDITMemberResponse_DTO_HUM  object that can then be utilized for sending the webservice request.
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
    
        /**
         * Returns PharmacyOMSLogNotes_DTO_HUM  object that can then be utilized for sending the webservice request.
         * This method is to create the request Structure for GetMemberrResponseservice .
         *
         * @param    
         * @return  PharmacyAMSLogNotes_DTO_HUM request structure of the wrapper class RightSourceGetMemberResponse_DTO_HUM
         */
    
        public static list < PharmacyOMSLogNotes_DTO_HUM > OMSLogNotesResponse(RightSourceGetMemberResponse_DTO_HUM oResponseGetMem)
        {
            List<PharmacyOMSLogNotes_DTO_HUM> wrapperObjList = new List<PharmacyOMSLogNotes_DTO_HUM>();
            PharmacyOMSLogNotes_DTO_HUM wrapperObj = null;
    
            if (oResponseGetMem != null && oResponseGetMem.GetMemberReponse != null)
            {
                if (oResponseGetMem.GetMemberReponse.Customers != null)
                {
                    RightSourceGetMemberResponse_DTO_HUM.CustomersDetail custDetails = oResponseGetMem.GetMemberReponse.Customers;
                    RightSourceGetMemberResponse_DTO_HUM.LogNotesDetail logNotes = null;
                    RightSourceGetMemberResponse_DTO_HUM.LogNoteDetail logNote = null;
    
                    if(custDetails.customer != null)
                    {   
                        for(integer i = 0 ; i < custDetails.customer.size() ; i++)
                        {
                            logNotes = custDetails.customer[i].LogNotes;
                            if(logNotes != null && logNotes.logNote != null)
                            {   
                                for(integer j = 0 ; j < logNotes.logNote.size() ; j++)
                                {
                                    logNote = logNotes.logNote[j];
                                    wrapperObj = new PharmacyOMSLogNotes_DTO_HUM();
                                    if (string.isNotBlank(logNote.LogNoteCode)) wrapperObj.LogNoteCode = logNote.LogNoteCode;
                                    if (string.isNotBlank(logNote.LogNoteDate)) wrapperObj.LogNoteDate = logNote.LogNoteDate;
                                    if (string.isNotBlank(logNote.LogNoteMessage)) wrapperObj.LogNoteMessage = logNote.LogNoteMessage;
                                    if (string.isNotBlank(logNote.LogNoteKey)) wrapperObj.LogNoteKey = logNote.LogNoteKey;
                                    if (string.isNotBlank(logNote.LogNoteMessage)) wrapperObj.LogNoteMessage = logNote.LogNoteMessage;
                                    if (string.isNotBlank(logNote.LogNoteUser)) wrapperObj.LogNoteUser = logNote.LogNoteUser;
                                    wrapperObjList.add(wrapperObj);
                                }
                            }
                        }
                    }
                  }
               }
               return wrapperObjList;
        }
    
        /**
         * Returns PharmacyGetMember_Demographic_DTO_HUM object for populating Demographic details.
         * This method is to create the data for DemographicDetails from GetMemberResponseservice .
         *
         * @param    
         * @return  PharmacyGetMember_Demographic_DTO_HUM request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
         */
        private static PharmacyGetMember_Demographic_DTO_HUM wrapMemberInformation(RightSourceGetMemberResponse_DTO_HUM.CustomerDetail obj, PharmacyGetMember_Demographic_DTO_HUM wrapperObj)
        {
            wrapperObj.staleData = obj.StaleData;
            if (obj.CustomerIdentity != null)  wrapperObj = getCustomerIdentity(obj.CustomerIdentity, wrapperObj);
            if (obj.AccountNumber != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.AccountNumberDetail custAccount = obj.AccountNumber;
                if (string.isNotBlank(custAccount.CustomerValue)) wrapperObj.AccountNumber = custAccount.CustomerValue;
            }
            if (obj.Finance != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.FinanceDetail custFinance = obj.Finance;
                if (string.isNotBlank(custFinance.OutstandingBalance)) wrapperObj.BalanceDue = custFinance.OutstandingBalance;
                if (string.isNotBlank(custFinance.CreditLimit)) wrapperObj.AccountLimit = custFinance.CreditLimit;
            }
            if (obj.Addresses != null && obj.Addresses.Address.size() > 0) wrapperObj = getAddressDetails(obj.Addresses, wrapperObj);
            if (obj.Phones != null && obj.Phones.Phone.size() > 0)
            {
                RightSourceGetMemberResponse_DTO_HUM.PhonesDetail custPhone = obj.Phones;
                for (RightSourceGetMemberResponse_DTO_HUM.PhoneDetail custPhones: custPhone.Phone)
                {
                    if (custPhones.PhoneType == 'PP') wrapperObj.PhoneNumber = custPhones.PhoneValue;
                    if (custPhones.PhoneType == 'CN') wrapperObj.AltPhone = custPhones.PhoneValue;
                }
            }
            if (obj.CreditCards != null && obj.CreditCards.CreditCard.size() > 0)  
            wrapperObj.CreditCardsDetail  = obj.CreditCards;
            if (obj.CustomerDetail != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.CustomerDetails custDetail = obj.CustomerDetail;
                if (string.isNotBlank(custDetail.MemberConsentStatus)) wrapperObj.MemberConsent = custDetail.MemberConsentStatus;
                if (string.isNotBlank(custDetail.CMRStatus)) wrapperObj.MTMIndicator = custDetail.CMRStatus;
                if (string.isNotBlank(custDetail.consentBeginDate)) wrapperObj.MemberConsentApprovedDate = custDetail.consentBeginDate;
                if (string.isNotBlank(custDetail.NeedsMemberConsent)) wrapperObj.NeedsMemberConsent = custDetail.NeedsMemberConsent;
            }
            if (obj.CustomerPreference != null)
            {
                RightSourceGetMemberResponse_DTO_HUM.CustomerPreference custvarientDetail = obj.CustomerPreference;
                if (string.isNotBlank(custvarientDetail.CapType)) wrapperObj.CapType = custvarientDetail.CapType;
            }
            return wrapperObj;
        }
    
        /**
         * Returns PharmacyGetMember_Demographic_DTO_HUM object for populating Demographic details.
         * This method is to create the data for DemographicDetails from GetMemberResponseservice .
         *
         * @param    
         * @return  PharmacyGetMember_Demographic_DTO_HUM request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
         */
        private static PharmacyGetMember_Demographic_DTO_HUM getCustomerIdentity(RightSourceGetMemberResponse_DTO_HUM.CustomerIdentityDetail custIdentity, PharmacyGetMember_Demographic_DTO_HUM wrapperObj)
        {   
            if (custIdentity != null)
            {
                if (string.isNotBlank(custIdentity.FirstName)) wrapperObj.FirstName = custIdentity.FirstName;
                if (string.isNotBlank(custIdentity.LastName)) wrapperObj.LastName = custIdentity.LastName;
                if (string.isNotBlank(custIdentity.MiddleName)) wrapperObj.MiddleName = custIdentity.MiddleName.substring(0, 1);
                if (string.isNotBlank(custIdentity.BirthDate)) wrapperObj.DOB = custIdentity.BirthDate;
                if (string.isNotBlank(custIdentity.Email)) wrapperObj.Email = custIdentity.Email;
            }
            return wrapperObj;
        }
    
          /**
         * Returns PharmacyGetMember_Demographic_DTO_HUM object for populating Demographic details.
         * This method is to create the data for DemographicDetails from GetMemberResponseservice .
         *
         * @param    
         * @return  PharmacyGetMember_Demographic_DTO_HUM request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
         */
         private static PharmacyGetMember_Demographic_DTO_HUM getAddressDetails(RightSourceGetMemberResponse_DTO_HUM.AddressesDetail custAdd, PharmacyGetMember_Demographic_DTO_HUM wrapperObj)
        {
            if (custAdd != null && custAdd.Address != null)
            {
                 wrapperObj.Address = custAdd.Address;
                string address = '';
                string address1 = '';
                for (RightSourceGetMemberResponse_DTO_HUM.AddressDetail custAddress: custAdd.Address)
                {
                    if (custAddress.AddressType == 'P')
                    {
                        if (string.isNotBlank(custAddress.AddressLine1))
                            address = custAddress.AddressLine1 ;
                        if (string.isNotBlank(custAddress.AddressLine2) && string.isNotBlank(address) )
                            address = address + ',' + custAddress.AddressLine2 ;
                        else
                            address = address + custAddress.AddressLine2 ;
                        if (string.isNotBlank(custAddress.City) && string.isNotBlank(address))
                            address1 = address1  + custAddress.City ;
                        else
                            address1 = address1  + custAddress.City ;
                        if (string.isNotBlank(custAddress.StateCode) && string.isNotBlank(address1))
                            address1 = address1 + ', ' + custAddress.StateCode ;
                        else
                            address1 = address1 + custAddress.StateCode ;
                        if (string.isNotBlank(custAddress.ZipCode) && string.isNotBlank(address1))
                            address1 = address1 +' '+ custAddress.ZipCode ;
                        else
                            address1 = address1 + custAddress.ZipCode ;
                        wrapperObj.MailingAddress = address;
                        wrapperObj.MailingCity = address1;
                    }
                }
            }
            return wrapperObj;
        }
    
    
        /***
         * Returns PharmacyGetMember_Demographic_DTO_HUM object for populating Demographic details.
         * This method is to create the data for DemographicDetails from GetMemberResponseservice .
         *
         * @param    
         * @return  PharmacyAllergyConditions_DTO_HUM returns DTO calss for Allergies adn Health Conditions Table
         */
        private static PharmacyAllergyConditions_DTO_HUM getAllergiesAndHCondiotionFrmResponse(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse)
        {   
            PharmacyAllergyConditions_DTO_HUM objAllergiesHConDTO= null;
            if(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse && null !=  objMemberRseponse.GetMemberReponse.Customers && null != objMemberRseponse.GetMemberReponse.Customers.Customer)
            {
                List<String> lstAllergies = new List<String>();
                List<String> lstHelConditions = new List<String>();
                for(RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer : objMemberRseponse.GetMemberReponse.Customers.Customer)
                {   
                    if(null != objCustomer.Allergies && null!= objCustomer.Allergies.Allergy)
                    {
                        for(RightSourceGetMemberResponse_DTO_HUM.AllergyDetail objAllergy : objCustomer.Allergies.Allergy)
                        {
                            if(null != objAllergy && String.isNotBlank(objAllergy.HealthDescription))
                            {
                                lstAllergies.add(objAllergy.HealthDescription);
                            }
                        }
                    }
                    if(null != objCustomer.HealthConditions && null!= objCustomer.HealthConditions.HealthCondition)
                    {
                        for(RightSourceGetMemberResponse_DTO_HUM.HealthConditionDetail objHelCondition : objCustomer.HealthConditions.HealthCondition)
                        {
                            if(null != objHelCondition && String.isNotBlank(objHelCondition.HealthDescription))
                            {
                                lstHelConditions.add(objHelCondition.HealthDescription);
                            }
                        }
                    }
                }
                lstAllergies.sort();
                lstHelConditions.sort();
                if(!lstHelConditions.isEmpty() || !lstAllergies.isEmpty())
                {
                    objAllergiesHConDTO = new PharmacyAllergyConditions_DTO_HUM(lstAllergies,lstHelConditions);
                }
            }
            return objAllergiesHConDTO;
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
            list < PharmacyMemberPrescription_DTO_HUM> wrapperObjList;
            PharmacyMemberPrescription_DTO_HUM wrapperObj;
            if (oResponseGetMemberPrescription != null && oResponseGetMemberPrescription.GetScriptsResponse != null)
            {
                RightSourceGetScriptResponse_DTO_HUM.ScriptsResponse prescriptionRes = oResponseGetMemberPrescription.GetScriptsResponse;
                if (prescriptionRes.Scripts != null)
                {
                    RightSourceGetScriptResponse_DTO_HUM.ScriptsDetail prescriptionObj = prescriptionRes.Scripts ;
                    if (prescriptionObj.Script != null && prescriptionObj.Script.size() > 0)
                    {
                        wrapperObjList =  new list < PharmacyMemberPrescription_DTO_HUM> ();  
                        for (RightSourceGetScriptResponse_DTO_HUM.ScriptDetail obj: prescriptionObj.Script)
                        {
                            wrapperObj = new PharmacyMemberPrescription_DTO_HUM();
                            wrapperObj.staleData = prescriptionRes.StaleData;
                            if (string.isNotBlank(obj.ConsentStatus)) wrapperObj.Consent = obj.ConsentStatus;
                            //if (string.isNotBlank(obj.ConsentDateTime)) 
                            if (string.isNotBlank(obj.ScriptKey)) wrapperObj.RXNumber = obj.ScriptKey;
                            if (string.isNotBlank(obj.ScriptStatus)) wrapperObj.Status = obj.ScriptStatus;
                            if (string.isNotBlank(obj.IsAutoRefill)) 
                            {
                               if(obj.IsAutoRefill =='true')
                               {
                                wrapperObj.AutoRefillEnrolled = 'Yes';
                               }
                               else
                               {
                                wrapperObj.AutoRefillEnrolled = 'No';
                               }
                            }
                            if (string.isNotBlank(obj.AutoRefillEligible)) 
                                {
                               if(obj.AutoRefillEligible =='true')
                               {
                                wrapperObj.AutoRefillEligible = 'Yes';
                               }
                               else
                               {
                                wrapperObj.AutoRefillEligible = 'No';
                               }
                              }    
                            if (string.isNotBlank(obj.DrugType))
                            {
                                if(obj.DrugType == 'CS')
                                wrapperObj.ControlledRX = 'Yes';
                                else
                                wrapperObj.ControlledRX = 'No';
                            }
                            if (string.isNotBlank(obj.Quantity)) wrapperObj.Quantity = obj.Quantity;
                            if (string.isNotBlank(obj.RefillsRemaining )) wrapperObj.RefillsRemaining = obj.RefillsRemaining ;
                            if (string.isNotBlank(obj.DaysSupply)) wrapperObj.DaysSupply = obj.DaysSupply;
                            if (string.isNotBlank(obj.LastFillDate)) wrapperObj.LastFillDate = obj.LastFillDate;
                            if (string.isNotBlank(obj.NextFillDate)) wrapperObj.NextFillDate = obj.NextFillDate;
                            if (string.isNotBlank(obj.NextFillMinDate)) wrapperObj.NextFillMinDate = obj.NextFillMinDate;
                            if (string.isNotBlank(obj.AutoRefillEligible)) wrapperObj.AutoRefillEligible = obj.AutoRefillEligible;
                            if (string.isNotBlank(obj.ScriptExpirationDate)) wrapperObj.ExpirationDate = obj.ScriptExpirationDate;
                            RightSourceGetScriptResponse_DTO_HUM.ItemsDetail itemsObj = obj.Items;
                            if(itemsObj.Item != null && itemsObj.Item.size() > 0)
                            {
                                for(RightSourceGetScriptResponse_DTO_HUM.ItemDetail itemObj : itemsObj.Item)
                                {
                                    if(String.isNotBlank(itemObj.ItemType) )
                                    {
                                        if(itemObj.ItemType == 'D')
                                        {
                                          if (string.isNotBlank(itemObj.ItemLabel))wrapperObj.DispensedDrug = itemObj.ItemLabel;
                                        }
                                        if(itemObj.ItemType == 'W')
                                        {
                                          if (string.isNotBlank(itemObj.ItemLabel))wrapperObj.WrittenDrug = itemObj.ItemLabel;
                                        }
                                    }
                                }
                            }
                            if(obj.physician != null )
                            {
                              RightSourceGetScriptResponse_DTO_HUM.PhysicianDetail phyObj = obj.physician;
                              String prescriberInformation ='';
                              string phone = '';
                              string fax = '';
                              if (string.isNotBlank(phyObj.FirstName))prescriberInformation = phyObj.FirstName;
                              if (string.isNotBlank(phyObj.LastName) && string.isNotBlank(prescriberInformation))
                                  prescriberInformation = prescriberInformation + ' ' + phyObj.LastName;
                              else
                                  prescriberInformation = prescriberInformation + phyObj.LastName;
                              wrapperObj.PrescriberInformation = prescriberInformation ;
                              RightSourceGetScriptResponse_DTO_HUM.PhonesDetail phonesObj = phyObj.Phones ;
                              if(phonesObj.Phone != null && phonesObj.Phone.size() > 0)
                                {
                                    for(RightSourceGetScriptResponse_DTO_HUM.PhoneDetail phoneObj : phonesObj.Phone)
                                    {
                                        if(String.isNotBlank(phoneObj.PhoneType))
                                        {
                                            if(phoneObj.PhoneType == 'FN' && fax == '')
                                            {
                                              if(string.isNotBlank(phoneObj.PhoneValue))
                                              {
                                                  wrapperObj.Fax = phoneObj.PhoneValue;
                                                  fax = phoneObj.PhoneValue;
                                              }
                                            }
                                            if(phoneObj.PhoneType == 'WP' && phone == '')
                                            {
                                              if(string.isNotBlank(phoneObj.PhoneValue))
                                              {
                                                  wrapperObj.Phone = phoneObj.PhoneValue;
                                                  phone = phoneObj.PhoneValue;
                                              }
                                            }
                                            
                                        }
                                    }
                                 }
                            }
                            wrapperObjList.add(wrapperObj);
                        }
                    }
                    else if(prescriptionObj.Script != null && prescriptionObj.Script.size() == 0) 
                    {
                        wrapperObjList =  new list < PharmacyMemberPrescription_DTO_HUM> (); 
                    }
                }
            }
            return wrapperObjList;
        }
    
        /**
         * Returns RightSourceGetOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
         * This method is to create the request Structure for GetMemberResponseservice .
         *
         * @param    
         * @return  RightSourceGetOrderRequest_DTO_HUM  request structure of the wrapper class RightSourceGetOrderRequest_DTO_HUM 
         */
        public static RightSourceGetOrderDetRequest_DTO_HUM requestStructureGetOrderDetailResponse(String sOrderKey, string networkID)
        {
            RightSourceGetOrderDetRequest_DTO_HUM oRequest = new RightSourceGetOrderDetRequest_DTO_HUM();
            RightSourceGetOrderDetRequest_DTO_HUM.GetOrderDetailsRequest orderDetailRequest = new RightSourceGetOrderDetRequest_DTO_HUM.GetOrderDetailsRequest();
            orderDetailRequest.OrderKey = sOrderKey;
            orderDetailRequest.CustomerServiceId = networkID;
            oRequest.GetOrderDetailsRequest = orderDetailRequest;
            return oRequest;
        }
    
        /**
         * Returns RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails objectfor populating payment details.
         * This method is to create the data for PaymentDetails from GetAdjustmentAndPayment .
         *
         * @param   RSGetAdjustmentAndPaymentResponseDTO_HUM DTO Response 
         * @return  RSGetAdjustmentAndPaymentResponseDTO_HUM.PaymentDetails request structure of the wrapper class RSGetAdjustmentAndPaymentResponseDTO_HUM 
         */
        public static RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse prepareOrderDetails(RightSourceGetOrderDetResponse_DTO_HUM oResponseGetMember)
        {
            RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse oResponse = new RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse(); 
            if(oResponseGetMember != NULL && oResponseGetMember.GetOrderDetailResponse != NULL)
            {
                if(oResponseGetMember.GetOrderDetailResponse.OrderKey != NULL) oResponse.OrderKey = oResponseGetMember.GetOrderDetailResponse.OrderKey;
                if(oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey != NULL) oResponse.ExternalOrderKey = oResponseGetMember.GetOrderDetailResponse.ExternalOrderKey;
                if(oResponseGetMember.GetOrderDetailResponse.OrderCreationDate != NULL) oResponse.OrderCreationDate = oResponseGetMember.GetOrderDetailResponse.OrderCreationDate;
                if(oResponseGetMember.GetOrderDetailResponse.OrderShipDate != NULL) oResponse.OrderShipDate = oResponseGetMember.GetOrderDetailResponse.OrderShipDate;
                if(oResponseGetMember.GetOrderDetailResponse.OrderReleaseDate!= NULL) oResponse.OrderReleaseDate = oResponseGetMember.GetOrderDetailResponse.OrderReleaseDate ;
                if(oResponseGetMember.GetOrderDetailResponse.CurrentQueue != NULL) oResponse.CurrentQueue = oResponseGetMember.GetOrderDetailResponse.CurrentQueue;
                if(oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate != NULL) oResponse.CurrentQueueDate = oResponseGetMember.GetOrderDetailResponse.CurrentQueueDate;
                if(oResponseGetMember.GetOrderDetailResponse.OrderSource != NULL) oResponse.OrderSource = oResponseGetMember.GetOrderDetailResponse.OrderSource;
                if(oResponseGetMember.GetOrderDetailResponse.CancelReasonCode != NULL) oResponse.CancelReasonCode = oResponseGetMember.GetOrderDetailResponse.CancelReasonCode;
                if(oResponseGetMember.GetOrderDetailResponse.CreditCardKey != NULL) oResponse.CreditCardKey = oResponseGetMember.GetOrderDetailResponse.CreditCardKey;
                if(oResponseGetMember.GetOrderDetailResponse.Queues != NULL) oResponse.Queues = oResponseGetMember.GetOrderDetailResponse.Queues;            
                if(oResponseGetMember.GetOrderDetailResponse.ShippingInformation != NULL) oResponse.ShippingInformation = oResponseGetMember.GetOrderDetailResponse.ShippingInformation;
                if(oResponseGetMember.GetOrderDetailResponse.Addresses != NULL) oResponse.Addresses = oResponseGetMember.GetOrderDetailResponse.Addresses;
                if(oResponseGetMember.GetOrderDetailResponse.FirstName != NULL) oResponse.FirstName = oResponseGetMember.GetOrderDetailResponse.FirstName;
                if(oResponseGetMember.GetOrderDetailResponse.LastName != NULL) oResponse.LastName = oResponseGetMember.GetOrderDetailResponse.LastName;
                if(oResponseGetMember.GetOrderDetailResponse.OrderItems != NULL) oResponse.OrderItems = oResponseGetMember.GetOrderDetailResponse.OrderItems;
                if(oResponseGetMember.GetOrderDetailResponse.CreditCards != NULL) oResponse.CreditCards = oResponseGetMember.GetOrderDetailResponse.CreditCards;
                if(oResponseGetMember.GetOrderDetailResponse.StaleData != NULL) oResponse.StaleData = oResponseGetMember.GetOrderDetailResponse.StaleData;
                   RightSourceGetOrderDetResponse_DTO_HUM.Queues oQueues = new RightSourceGetOrderDetResponse_DTO_HUM.Queues(); 
                    RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails oQueueDet =  new RightSourceGetOrderDetResponse_DTO_HUM.QueueDetails();
                    List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail> lstQueueDetail = new List<RightSourceGetOrderDetResponse_DTO_HUM.QueueDetail>();
                
                    oQueues = oResponse.Queues;
                 if(oQueues!= null){ 
                 for(RightSourceGetOrderDetResponse_DTO_HUM.Queue objQueue : oQueues.Queue)
                    {
                        if(objQueue != null){   
                        oQueueDet = objQueue.QueueDetails;
                         if(oQueueDet!= null){
                        lstQueueDetail = oQueueDet.QueueDetail;
                         }
                        }
                    } 
                  }     
                    if(oResponse.CurrentQueue == PHARMACY_CURRENT_QUEUE){ 
                      for(Integer i=0; i < lstQueueDetail.size(); i++){                  
                      if(string.isNotBlank(lstQueueDetail[i].exceptionIdentifier)){
                        if(!oResponse.exceptionIdentifier.Contains(lstQueueDetail[i].exceptionIdentifier)){
                            oResponse.exceptionIdentifier += lstQueueDetail[i].exceptionIdentifier +',' ; 
                        }
                      }
                     }
    
                    }
              }
            return oResponse;
        }
    
             /**
         * Returns PharmacyPayer_DTO_HUM object for populating Payer Name in Order details.
         * 
         *
         * @param RightSourceGetMemberResponse_DTO_HUM   
         * @return  PharmacyPayer_DTO_HUM 
         */
        private static PharmacyPayer_DTO_HUM getPayerName(RightSourceGetMemberResponse_DTO_HUM objMemberRseponse)
        {   
           PharmacyPayer_DTO_HUM objPayer = null;
    
            if(null != objMemberRseponse &&  null !=  objMemberRseponse.GetMemberReponse && null !=  objMemberRseponse.GetMemberReponse.Customers && null != objMemberRseponse.GetMemberReponse.Customers.Customer && !objMemberRseponse.GetMemberReponse.Customers.Customer.isEmpty() )
            {
                RightSourceGetMemberResponse_DTO_HUM.CustomerDetail objCustomer = objMemberRseponse.GetMemberReponse.Customers.Customer[0];
    
                if (null != objCustomer && null != objCustomer.Plans && null != objCustomer.Plans.plan && !objCustomer.Plans.plan.isEmpty())
                {
                    RightSourceGetMemberResponse_DTO_HUM.PlanDetail custPlan = objCustomer.Plans.Plan[0];
                    if (null!= custPlan && null != custPlan.Payer )
                    {
                        objPayer= new PharmacyPayer_DTO_HUM(  custPlan.Payer);
                    }
                }
            }
            return objPayer;
        }
    
        /**
        * Returns RightSourceEditMemberRequest_DTO_HUM  object that can then be utilized for sending the MTM webservice request.
        * This method is to create the request Structure for RightSourceEditMemberRequest to update MTM.
        *
        * @param   Strinf Enterprise Id 
        * @return  RightSourceEditMemberRequest_DTO_HUM  request structure of the wrapper class RightSourceEditMemberRequest_DTO_HUM 
        */
        public static RightSourceEditMemberRequest_DTO_HUM RequestStructureGetMTMResponse(String sMemId, string networkID)
        {
            RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
            RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
            RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
            RightSourceEditMemberRequest_DTO_HUM.customerDetail custDetail = new RightSourceEditMemberRequest_DTO_HUM.customerDetail();
            custDetail.cmrStatus = DNC_PHARMACY_HUM;
            memDetailObj.customerDetail = custDetail;
            memDetailObj.EnterprisePersonID = sMemId; 
            getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
            getEditMembReqObj.CustomerServiceId = networkID;
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
         * Returns RSCreateAndEditOrderRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
         * This method is to create the request Structure for RSCreateAndEditOrder .
         *
         * @param    
         * @return  RSCreateAndEditOrderRequest_DTO_HUM  request structure of the wrapper class RSCreateAndEditOrderRequest_DTO_HUM  
         */
        public static RSCreateAndEditOrderRequest_DTO_HUM  requestStructureRSReleaseAndCancelOrder(String orderKey,String EnterpriseId, boolean isCancel)
        {
          RSCreateAndEditOrderRequest_DTO_HUM oRequest = null;
           if(null != orderKey && null != EnterpriseId)
               {
                oRequest = new RSCreateAndEditOrderRequest_DTO_HUM ();
                RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest objCreateAndEditOrderRequest = new RSCreateAndEditOrderRequest_DTO_HUM.CreateAndEditOrderRequest();           
                objCreateAndEditOrderRequest.EnterprisePersonID =  EnterpriseId;              
                objCreateAndEditOrderRequest.orderKey = orderKey;
                if(isCancel)
                {
                     objCreateAndEditOrderRequest.cancelOrder ='true';
                     objCreateAndEditOrderRequest.orderPlatform = 'trad';            
                }
                else
                {
                    RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting moveToRouting = new RSCreateAndEditOrderRequest_DTO_HUM.MoveToRouting();
                    moveToRouting.orderKey  = new List<String>();
                    moveToRouting.orderKey.add(orderKey);
                    objCreateAndEditOrderRequest.moveToRouting = moveToRouting;
                }
                oRequest.CreateAndEditOrderRequest = objCreateAndEditOrderRequest;
              }
              return oRequest;
        }

        /**
         * Returns IsMemElig100DayPrescripRequest_DTO_HUM  object that can then be utilized for sending the webservice request.
         * This method is to create the request Structure for requestStructureIsMemElig100DayPrescrip .
         *
         * @param    
         * @return  IsMemElig100DayPrescripRequest_DTO_HUM  request structure of the wrapper class IsMemElig100DayPrescripRequest_DTO_HUM  
         */
        public static IsMemElig100DayPrescripRequest_DTO_HUM requestStructureIsMemElig100DayPrescrip(String oPolMem)
        {        
            try
            {             
             IsMemElig100DayPrescripRequest_DTO_HUM oRequest = new IsMemElig100DayPrescripRequest_DTO_HUM();
             IsMemElig100DayPrescripRequest_DTO_HUM.SearchRequest oReqSearch = new IsMemElig100DayPrescripRequest_DTO_HUM.SearchRequest();
             
              List<Policy_Member__c> policyList = [Select Name, Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Policy__r.Medicare_Segment_ID__c, Policy__r.Coverage_Plan_Effective_Date__c, Status__c,Member__r.personMailingPostalCode, End_Date__c from Policy_Member__c where id =:oPolMem limit 1];
             if(!policyList.isEmpty() && policyList!=null ) {
                 Integer StartYear = Date.Today().Year();
                 oReqSearch.PlanID=  policyList[0].Policy__r.Contract_Number__c + '-' +policyList[0].Policy__r.PBP_Code__c + '-' + policyList[0].Policy__r.Medicare_Segment_ID__c + '-' +StartYear; 
             }
           
             oRequest.SearchRequest = oReqSearch;
             return oRequest; 
                         
            }
            catch (Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'Pharmacy_D_HUM', 'requestStructureIsMemElig100DayPrescrip');
                return null;
            }
        }
       
}