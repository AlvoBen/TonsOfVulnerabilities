/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_LC_HUM.cls
Version         : 1.0
Created On      : 10/14/2021
Function        : Class contains methods for services to fetch Benefits Information.
TestClass       : PharmacyBenefits_LT_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg											10/14/2021				  Initial Version - US26489489 - Adding for Pharmacy lightning
* Jonathan Dickinson                                    10/20/2021                Added methods from non-lightning classes 
*                                                                                 And modified them to work with the New lightning classes
* M K Manoj                                             11/04/2022                US-3578373, US-3578548
* Pinky Vijur                                           01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Tharun M                                               02/03/2023                User Story 4138214: T1PRJ0870026 Solutran Integrations - CRM FR2.01 - Update RxNova Link to Auto - Prefill SSO and Member's Details
* Pinky Vijur						                    04/03/2023				  User Story 4401068: C04; Authorization/Referral Verification; LIGHTNING--T1PRJ0891742-CRM IOP-2022-6037203—Pharmacy Authorization Details: add ‘Department’, ‘Coverage Start Date’ & ‘Coverage End Date’
*****************************************************************************************************************************************/
public with sharing class PharmacyBenefits_LC_HUM {

   
  private PharmacyBenefits_LD_HUM oDataPharmacyBen= new PharmacyBenefits_LD_HUM();
 

  /**
  * 
  * <p>
  * Constructor Method to for the class , Initialize variables
  */  
  public PharmacyBenefits_LC_HUM()
  {  
  } 
  	
	/**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getpAuth details continuation requests
     *
     * @param  String Enterprise Id
     * @return continuation object
     */
    @AuraEnabled(continuation=true cacheable=true)
    public static object invokeGetpAuthService(String sMemID)
    {
    return PharmacyBenefits_LS_HUM.SendRequestGetpAuthResponse(sMemID);
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
    @AuraEnabled
  	public static object parseGetPAuthResponse(list<string> labels, object state)
    {
    	return JSON.serialize(PharmacyBenefits_LS_HUM.parseGetPAuthResponse(labels, state));
    }

        /**
    * make a call to getPolicyMemberDatamethod of  PharmacyBenefits_D_HUM class to get Policy Member Data
    * <p>
    * @param  NA
    * 
    * @return void
    */
    @AuraEnabled(cacheable=true)
    public static List<MemberPlan> getPolicyMemberData(string sAccId)
    { 
        try
        {
            List<MemberPlan> listMemberPolicies;
            if(String.isNotBlank(sAccId)) 
            {
                listMemberPolicies = PoliciesSearchResults_LC_HUM.recordsSorting(PharmacyBenefits_LD_HUM.getPolicyMemberData(sAccId));
                return listMemberPolicies;
            } 
            return null ;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_LC_HUM', 'getPolicyMemberData');
            return null;
        }
    }

    @AuraEnabled(cacheable=true)
    public static String getServiceURL(String sMemberid, String dateOfB) {
        Map<string,string> mapserviceurl = new Map<string,string>();
        String memberID = String.isNotEmpty(sMemberid) ? sMemberid : '';
        String dob = String.isNotEmpty(dateOfB) ? EncodingUtil.urlEncode(dateOfB, 'UTF-8') : '';

        String rxNovaBaseURL = HUMCertificateURL__c.getValues('PharmacyBenefits_RxNovaUrl').CertificateListURL__c;

        String paramTemplate = '{0}?dob={1}&customerId=&customerSetId=22&altmemberId={2}';
        List<Object> params = new List<Object> {rxNovaBaseURL, dob, memberID};
        String rxNovaURL = String.format(paramTemplate, params);

        mapserviceurl.put('RxNova', rxNovaURL);
        mapserviceurl.put('InComm', HUMCertificateURL__c.getValues('PharmacyBenefits_IncommUrl').CertificateListURL__c);
        mapserviceurl.put('Solutran', HUMCertificateURL__c.getValues('PharmacyBenefits_SolutranUrl').CertificateListURL__c);
        return JSON.serialize(mapserviceurl);
    }
	
	/**
     * <p>
     * callout for OTC Soltrn service V3
     *
     * @param  String sMemberid, sPBPCode ,sContractNumber,sProductDescription
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static object getSoltrnOTCBalanceV3(String sMemberid, String sPBPCode, string sContractNumber, string sProductDescription )
    {  
        return PharmacyBenefits_LS_HUM.requestSoltrnOTCBalanceV3(sMemberid, sPBPCode,sContractNumber,sProductDescription);
    }


        	/**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse OTC balance service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return string
     */
    @AuraEnabled
    public static object parseSoltrnOTCBalanceV3(List<string> labels,object state)
    {
      	return JSON.serialize(PharmacyBenefits_LS_HUM.parseSoltrnOTCBalanceV3(labels, state));
    }
    @AuraEnabled
    public static Boolean paHUBUS4250871SwitchValue()
    {
    	return HUMUtilityHelper.isCRMFunctionalityON('4250871');
    }



}