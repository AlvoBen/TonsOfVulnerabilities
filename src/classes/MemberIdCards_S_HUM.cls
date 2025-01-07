/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_S_HUM.cls
Version         : 1.0
Created On      : 6th March 2018
Function        : Service Class to fetch Member Id Card Information.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                  207348               12/20/2018                Original Version (REQ 357335)
* Rajesh Keswani               207348               04/02/2018                Possibility to show entries based on Parent records
* Joel George                  236553               05/30/2018                Defect 359816
* Subhamay Mandal                                   02/18/2019                REQ - 386033 Display Additional Warning Message on ID Card Mgmt Page (RF)
* Amar Gadewar                                      04/16/2019                REQ - 396366 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (RF) 
* Amar Gadewar                 357688               05/30/2019                REQ - 401996 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (CRM)
* Harish Gowda                                      08/21/2019                REQ - 408204 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (CRM)
* Atia Uzma                                         09/28/2020                User Story 1563575: PR00091574 - MF 1 - Quality - Display updated information messages on the ID Card Management page to reflect accurate guidance as per the revised business process (CRM)
* Atia Uzma                                         02/25/2021               User Story 1956960: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
* Satyanarayana chenna                              04/09/2021                 US 1918663 PR00094226 - CR AEP readiness - Low Security Issues 
* Pallavi Shewale                                   04/30/2021                User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
* Samantha Tennyson                                 10/28/2021                User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
*****************************************************************************************************************************************/

public with sharing class MemberIdCards_S_HUM 
{

        Public Static final String MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE_HUM = 'parseOrderIdCardResponse';
        Public Static final Integer HTTP_OK_HUM = 200;
        Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
        Public Static final String VOB_FAULT_CODE = 'faultcode';
        Public Static final Integer HUMServiceTimeOut = 120;
        Public Static final String MEMBERIDCARDMGT_PROCESSMEMLEVELRESPONSE_HUM = 'processMemberLevelResponse';
        Public Static final String Content_type_HUM = 'content-type';
        Public Static final String Keep_alive_HUM = 'keep-alive';
        Public Static final String Connection_HUM = 'Connection';
        Public Static final String SOAPAction_HUM = 'SOAPAction';
        Public Static final String MEDICARECALLS_MAPD_HUM = 'MAPD';
        Public Static final String MEDICARECALLS_PDP_HUM = 'PDP';
        Public Static final String MEDICARECALLS_MA_HUM = 'MA';
        Public Static final String MAJOR_LOB_MCD = 'MCD';
        Public Static final String MAJOR_LOB_MES = 'MES';

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for Order Id Card services .
     *
     * @param   List<String>          Lst Member Id
     * @return  Continuation
     */
    public static Continuation sendRequestOrderIdCard(List<String> lstMemberId, String polMemId)
    {
        HUM_Webservice_Callout__c oHIDServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_OrderIdCard');
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(oHIDServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con.continuationMethod = MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE_HUM;    
        con.state = polMemId;
        HttpRequest httpOrderIdCardReqObj = builtHttpRequest(oHIDServiceDetails);
        httpOrderIdCardReqObj.setBody(JSON.serialize(MemberIdCards_D_HUM.buildOrderIdCardREquest(lstMemberId))); 
        con.addHttpRequest(httpOrderIdCardReqObj);
        return con;
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseOrderIdCardResponse Method to parse the service response
     *
     * @param  object state,list<string> lstLabels .
     * @return Object
     */
    public static Object parseOrderIdCardResponse(list<string> lstLabels, Object oState)
    {
        HttpResponse oHttpResponse = Continuation.getResponse(lstLabels[0]);
        String IdCardresponseBody;
        HIDCMSOrderIdCardResponse_DTO_HUM oResponseIdCard;
        if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oHttpResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            IdCardresponseBody = oHttpResponse.getBody();
            oResponseIdCard = (HIDCMSOrderIdCardResponse_DTO_HUM) System.JSON.deserialize(IdCardresponseBody, HIDCMSOrderIdCardResponse_DTO_HUM.class);
        }
        else
        {
            // Fault Code
            string recordId = string.valueOf(oState);
            HUMCalloutUtilityHelper.logFaultError(oHttpResponse,'MemberIdCards_S_HUM', 'parseOrderIdCardResponse', recordId, 'Policy_Member__c', 'INFO');
        }
        return oResponseIdCard;
    }
    
    /**
    * Returns responses of continuation  method
    * <p>
    * Method to get the Member level search Data
    *
    * @param String           grpNumber
    * @param String           memberId
    * @return                 Object
    */
    public static Object processMemberLevelService(String grpNumber, string memberId , string polMemId)
    {
        try
        {
            HUM_Webservice_Callout__c IDCardServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_MemberSearch');
            HttpRequest httpPBEReqObj = builtHttpRequest(IDCardServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(MemberIdCards_D_HUM.RequestStructureMemberLevelService(grpNumber,memberId)));
            integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = MEMBERIDCARDMGT_PROCESSMEMLEVELRESPONSE_HUM;
            con.state = polMemId;
            System.debug(httpPBEReqObj.getBody());
            con.addHttpRequest(httpPBEReqObj);
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_S_HUM', 'processMemberLevelService');
            return null;
        }
    }
    
    /**
    * Returns the response for Member level search
    * <p>
    * Method to Parse the Benefit Search response .
    *
    * @param benefitsCls          Object State
    * @return                     Object
    */
    public Static Object processMemberLevelResponse(List<string> labels,Object State)
    {
        Object dtoObj;
        try
        {
           
            HttpResponse responseMemberLevel = Continuation.getResponse(labels[0]);
            if (responseMemberLevel != null && ((responseMemberLevel.getStatusCode() == HTTP_OK_HUM || responseMemberLevel.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseMemberLevel.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                HIDCMSMemberSearchResponse_DTO_HUM oResponse = (HIDCMSMemberSearchResponse_DTO_HUM)System.JSON.deserialize(responseMemberLevel.getBody(), HIDCMSMemberSearchResponse_DTO_HUM.class);
                dtoObj = MemberIdCards_D_HUM.prepareMemberLevelSerchResponse(oResponse);
                dtoObj = (dtoObj==null) ? oResponse : dtoObj;
            }
            else
            {
                // Fault Code
                string recordId = string.valueOf(State);
                HUMCalloutUtilityHelper.logFaultError(responseMemberLevel,'MemberIdCards_S_HUM', 'processMemberLevelResponse', recordId, 'Policy_Member__c', 'INFO');
            }
            return dtoObj;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberIdCards_S_HUM', 'processMemberLevelResponse');
            return null;
        }
        
    }
        
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c oServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (oServiceObj != null && oServiceObj.Active__c)
        {
            request.setMethod(oServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(oServiceObj.Certificate_Name__c)) request.setClientCertificateName(oServiceObj.Certificate_Name__c);
            if (String.isNotBlank(oServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, oServiceObj.Content_Type__c);
            request.setHeader(Connection_HUM, Keep_alive_HUM);
            request.setEndpoint(oServiceObj.End_Point_URL__c);
            request.setHeader(SOAPAction_HUM,'');
        }
        return request;
    } 
    
    
	
    /**
    *  Method will check last 7 days cases for ID Card Request.
    * @param Policy Member: lstPolicyMember
    * @param String: sInteractionId
    * @return Boolean
    * */
    public static Boolean checkIDCardRequestInL7Days(List<Policy_Member__c> lstPolicyMember, String sInteractionId) {
        Boolean bIdCardRequest;
        bIdCardRequest = MemberIdCards_D_HUM.getIdCardRequestInL7Days(lstPolicyMember,sInteractionId);
        return bIdCardRequest;
    }
    /**
     * Method will check segment indicator for id card request
     * @para Policy Member: objPolicyMember
     * @return Boolean 
     * 
     **/
    public static Boolean checkMedicareMedicaid(Policy_Member__c objPolicyMember) {
        
        Boolean bCheckSegmentIndicator = false;
        List<String> lstSegmentIndicator = new List<String>{'Individual-Medicare','LargeGroup-Medicare','Individual-Medicaid'};
        if(String.isNotBlank(objPolicyMember.Segment_Indicator__c) && lstSegmentIndicator.contains(objPolicyMember.Segment_Indicator__c)) {
            bCheckSegmentIndicator = true;
        }
        return bCheckSegmentIndicator; 
    }
            
    /*
    * Assigns general Case fields
    * 
    * @param Case: New Case to assign to
    * @param string: Saved Case ID
    * @see Values assigned here may be populated on the Case Edit page
    */  
    public static String assignCaseDefaults(Policy_Member__c objPolMem, ID objID, String sInteractionId, String sStandardUrlForTab)
    {
        User sUserQueue = MemberIdCards_D_HUM.queryUserQueue();
        List<Case> lstCaseDefaults = new List<Case>();
        String sSavedCaseId;
		Boolean securitySwitchCall;
        securitySwitchCall = HUMUtilityHelper.isCRMFunctionalityON('3003419');

        try{
        	if(UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess() && sUserQueue.Current_Queue__c != GLOBAL_CONSTANT_HUM.RCC_WebChat_Users && (objPolMem.Product_Type_Code__c == 'MA' || objPolMem.Product_Type_Code__c == 'MAPD' || objPolMem.Product_Type_Code__c == 'PDP')){

                HUMNewCaseRedirectExtension caseExtn = new HUMNewCaseRedirectExtension(null);
                caseExtn.objectID = objID;
                caseExtn.sObjectName = objID.getSobjectType().getDescribe().getName();
                caseExtn.sStandardUrlForTab = sStandardUrlForTab;
                caseExtn.funcOnPageLoad();
                caseExtn.saveCase();
                List<Case> lstCases = new List<Case>();
                case updateCase = MemberIdCards_D_HUM.queryCaseDetails(caseExtn.caseID);
                updateCase.Id = caseExtn.caseID;
                sSavedCaseId = updateCase.Id;
                
                //Update more default Fields post save
                List<CTCI_Junction__c> lstCtci = MemberIdCards_D_HUM.queryActiveCTCI(updateCase.Classification_Type__c);
                
                if(!lstCtci.isEmpty()){
                    updateCase.CTCI_List__c = lstCtci[0].id;
                    updateCase.Classification_Id__c = lstCtci[0].Classification__c;
                    updateCase.Intent_Id__c = lstCtci[0].Intent__c;
                }
                
                updateCase.G_A_Rights_Given__c = 'No';
            	updateCase.Complaint__c = 'No';
                
                if(objPolMem.Product_Type_Code__c == 'MA' || objPolMem.Product_Type_Code__c == 'MAPD')
                    updateCase.Call_Benefit_Category__c = 'Medicare Part C';
                else if(objPolMem.Product_Type_Code__c == 'PDP')
                    updateCase.Call_Benefit_Category__c = 'Medicare Part D';
                
                if(updateCase.Interacting_With_Type__c == 'Member Representative or Caregiver')
                    updateCase.Verbal_Consent_Obtained__c = 'Not Required';
                else
                    updateCase.Verbal_Consent_Obtained__c = '';
                
                lstCases.add(updateCase);
                
               	if(securitySwitchCall==true)
        		{
					SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.UPDATABLE, lstCases );
					lstCases = securityDecision.getRecords();
					if(lstCases.size() > 0){ update lstCases;}   
                    
         		}
        		else
        		{
           			if(lstCases.size() > 0){ update lstCases; } 
        		}
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_S_HUM', 'assignCaseDefaults');
        }
        return sSavedCaseId;
    }
		
}