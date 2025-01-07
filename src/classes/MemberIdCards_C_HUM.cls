/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_C_HUM.cls
Version         : 1.0
Created On      : 6th March 2018
Function        : Controller Class to fetch Member Id Card Information.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                 207348               12/20/2016                Original Version (REQ 357335)
* Rajesh Keswani              207348               04/02/2018                Possibility to show entries based on Parent records
* Melkisan Selvaraj           220731               05/04/2018                REQ-350311  Added ID Card/Debit Card section
* Subhamay Mandal                                  02/18/2019                REQ - 386033 Display Additional Warning Message on ID Card Mgmt Page (RF)
* Asish Behera                                     03/7/2019                 REQ - 385286 User Story - Add link to ID Card Center (RF)
* Amar Gadewar                                     04/16/2019                REQ - 396366 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (RF) 
* Harish Gowda                                     08/21/2019                REQ - 408204 Quality - RCC/Pharmacy - Display Additional Warning Message on ID Card Mgmt Page (CRM)
* Kapil Arora                                      03/30/2020                REQ - 967881 Quality - PR00091574 - MF 1 - AHT - Automatically launch PCP/PCD tab on clicking ID Card Management link for HMO plans (RF)
* Suresh Kokkeragadda                              05/25/2020                User Story 1110664: PR00091574 - MF 1 - AHT - Automatically launch PCP/PCD tab on clicking ID Card Management link for HMO plans (CRM)
* Amit Agarwal                                     06/03/2020                User Story 1110881: PR00091574 - MF 1 - Quality - Switch the sections on ID Card Management page (RF)
* Sunil Kumar Reddy Y                              06/03/2020                User Story 1110896: PR00091574 - MF 1 - Quality - Highlight last 14 days request on ID Card Management page (RF)
* Suresh Kokkeragadda                              06/24/2020                User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
* Suresh Kokkeragadda                              07/15/2020                User Story 1276583: PR00091574 - MF 1 - Quality - Switch the sections on ID Card Management page (CRM)
* Suresh Kokkeragadda                              07/29/2020                User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
* Atia Uzma                                        09/28/2020                User Story 1563575: PR00091574 - MF 1 - Quality - Display updated information messages on the ID Card Management page to reflect accurate guidance as per the revised business process (CRM)
* Prasanthi Kandula                                02/01/2021                US-1669849 SonarQube critical security fixes
* Atia Uzma                                        02/25/2021               User Story 1956960: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)  
* Suraj Patil								   	   04/09/2021				 REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
* Satyanarayana chenna                             04/09/2021                US-1918663 PR00094226 - CR AEP readiness - Low Security Issues 
* Pallavi Shewale                                  04/30/2021                User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
* Atia Uzma                                        07/12/2021                User Story 2451411: T1PRJ0001827 - MF 1 - Quality - Highlight last 7 days request on ID Card Management page (CRM)
* Atia Uzma                                        08/05/2021                User Story 2551785: T1PRJ0001827 - MF 1 - SF - TECH - On/Off switch cleanup for 8/20/21 Release (CRM)
* Atia Uzma                                        09/22/2021                User Story 2696519: T1PRJ0001827 - MF 1 - AHT - Arrange the requests in ID Card Status section from Newest to Oldest 
* Samantha Tennyson                                10/28/2021                User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request Is successfully submitted (RF)
* Tharun Madishetti                                09/26/2022                US-3468584: T1PRJ0054938 - FR0 9.07 CRM Service must deactivate the function to trigger an ID Card for members who are on the OH Medicaid Pending Plan
*****************************************************************************************************************************************/
public with sharing class MemberIdCards_C_HUM extends SObjectDescriptorImpl_H_HUM
{    
    private MemberIdCards_D_HUM oDataMemberIdCard= new MemberIdCards_D_HUM();
    private String sPolMemId  = '' ;
    Public Static final String GROUPIDCARDMGT_GRPNUMMISSING_HUM = 'ID Card Group Search failed. Reason:Group Number is missing for Account ';
    Public Static final String MEMBERIDCARDMGT_MEMIDMISSING_HUM = 'ID Card Member Search failed. Reason:Member_ID__c record is missing for Policy_Member__c ';

    
    
        /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public MemberIdCards_C_HUM()
    { 
        sPolMemId  = ApexPages.currentpage().getParameters().get('id').escapeHtml4(); 
       
        if (String.isNotBlank(sPolMemId))
        {
            objPolMem = oDataMemberIdCard.getPolicyMemberData(ApexPages.currentpage().getParameters().get('id').escapeHtml4());
            sMemIdWithoutDepCode = oDataMemberIdCard.getMemIdWithoutDepCode(objPolMem);
            bDispReqIdCard = oDataMemberIdCard.processEffectiveDate(objPolMem);
            bDispSPAATransferError = oDataMemberIdCard.isHSANonHomeOffice(objPolMem);
            bMedicareMedicaid = MemberIdCards_S_HUM.checkMedicareMedicaid(objPolMem);
            bDisableIdRequest = MemberIdCards_D_HUM.checkPendingPlan(sPolMemId);
        }
    } 
    
    public Policy_Member__c objPolMem{ get; set; }
    public Boolean bDispReqIdCard{ get; set; }
    public Boolean bDispSPAATransferError{ get; set; } 
    public string sMemIdWithoutDepCode{ get; set; }
    
    public String sInteractionId{get;set;}
    public Boolean bMedicareMedicaid {get;set;}
    public Boolean bIdCardRequestMayBeL7Days{get; set;}
    public String sStandardUrlForTab {get;set;}
    public string sSavedCaseId {get;set;}
    public Boolean bDisableIdRequest {get;set;}
    
	public Boolean bIdCardRequestCreateCaseSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2786833');
        }
        set;
    }
    public Boolean bIdCardSortSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2696519');
        }
        set;
    }
    


	
    
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the order id cards continuation requests
    *
    * @param  List<String> lstMemberId
    * @return continuation object
    */
    @RemoteAction
    public static Continuation invokeOrderIdCardService(List<String> lstMemberId, String polMemId)
    {
        Continuation retObj = null;
        try
        {
            retObj = MemberIdCards_S_HUM.sendRequestOrderIdCard(lstMemberId, polMemId);
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_C_HUM', 'invokeOrderIdCardService');
            retObj = null;
        }
        return retObj;
    }

    public void checkForMemberIDCardRequest()
    {
        if(bMedicareMedicaid){
			List<Policy_Member__c> lstPolMem = new List<Policy_Member__c>();
			lstPolMem.add(objPolMem);
            this.bIdCardRequestMayBeL7Days = MemberIdCards_S_HUM.checkIDCardRequestInL7Days(lstPolMem, sInteractionId);
        }
    }
    
    public void assignCaseValues()
    {
        ID objID = ApexPages.currentPage().getParameters().get('id');
        sSavedCaseId = MemberIdCards_S_HUM.assignCaseDefaults(objPolMem, objID, sInteractionId, sStandardUrlForTab);
    }
    
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the Order Id Card service response
    *
    * @param  List<String> List of Continuation Labels
    * @param  Object State of Continuation
    * @return Object
    */
    public static object parseOrderIdCardResponse(list<string> lstLabels, object oState)
    {
        object retObj = null;
        try
        {
            retObj = MemberIdCards_S_HUM.parseOrderIdCardResponse(lstLabels, oState);
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_C_HUM', 'parseOrderIdCardResponse');
            retObj = null;
        }
        return retObj;
    }

    /**
    * <p>
    * Method to get Active Policy Members
    *
    * @param  string sPolicyMemberId
    * @param  string sSubscriberPolicyMember
    * @param  string sPolicyId
    * @return RequestIdCard_DTO_HUM.PolicyMembersListDTO retObj
    */
    @RemoteAction
    public static RequestIdCard_DTO_HUM.PolicyMembersListDTO getActivePolicyMembers(String sPolicyMemberId, String sSubscriberPolicyMember, String sPolicyId)
    {
        RequestIdCard_DTO_HUM.PolicyMembersListDTO retObj = null;
        try
        {
            List<Policy_Member__c> policyMembers = getActivePolicyMembersRelated(sPolicyMemberId, sSubscriberPolicyMember, sPolicyId);
            retObj = new RequestIdCard_DTO_HUM.PolicyMembersListDTO(policyMembers);
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_C_HUM', 'getActivePolicyMembers');
            retObj = null;
        }
        return retObj;
    }

    /**
    * <p>
    * Method to return related Active Policy Members
    *
    * @param  string sPolicyMemberId
    * @param  string sSubscriberPolicyMember
    * @param  string sPolicyId
    * @return List<Policy_Member__c> activePolMembers
    */
    public static List<Policy_Member__c> getActivePolicyMembersRelated(String sPolicyMemberId, String sSubscriberPolicyMember, String sPolicyId) 
    {
        try
        {
            List<Policy_Member__c> activePolMembers;
            if (sSubscriberPolicyMember == null || String.isBlank(sSubscriberPolicyMember))
            {
                activePolMembers = MemberIdCards_D_HUM.getActivePolicyMembersData(sPolicyMemberId, sPolicyId);
            }
            else
            {
                activePolMembers = MemberIdCards_D_HUM.getActivePolicyMembersData(sSubscriberPolicyMember, sPolicyId);
            }
            return activePolMembers;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_C_HUM', 'getActivePolicyMembersRelated');
            return null;
        }
    }

    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the order id cards continuation requests
    *
    * @param  string grpNumber
    * @param  string memberId
    * @return continuation object
    */
    @RemoteAction
    public static Object getMemberLevelDetails(String grpNumber, string memberId, string polMemId)
    {
        Object ret = null;
        try
        {
            if (String.isBlank(memberId))
            {
                CalloutException e = new CalloutException();
                e.setMessage(MEMBERIDCARDMGT_MEMIDMISSING_HUM + '(' + polMemId + ')');
                throw e;
            }
            else if (String.isBlank(grpNumber))
            {
                CalloutException e = new CalloutException();
                e.setMessage(GROUPIDCARDMGT_GRPNUMMISSING_HUM + '(' + polMemId + ')');
                throw e;
            }
            else
            {
                ret = MemberIdCards_S_HUM.processMemberLevelService(grpNumber, memberId, polMemId);
            }

        }
        catch (exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberIdCards_C_HUM', 'getMemberLevelDetails');
        }
        return ret;
    }

    /**
    * Returns responses of RemoteAction method
    * <p>
    * Method to get the Member level search Data
    *
    * @param Object           State of Continaution
    * @return                 Object Continuation response
    */
    public static Object processMemberLevelResponse(List<string> labels, Object State)
    {
        return MemberIdCards_S_HUM.processMemberLevelResponse(labels, State);
    }
    /**
    * Returns this instance of the class
    * <p>
    * Method to store the Current Page Instance for Side Panel
    * 
    * @return this
    */
    public MemberIdCards_C_HUM getThis()
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
}