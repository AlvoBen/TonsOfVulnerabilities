/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefits_C_HUM.cls
Version         : 1.0
Created On      : 20th June 2017
Function        : Class contains methods for services to fetch Benefits Information.
TestClass       : PharmacyBenefits_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          06/20/2017                   Original Version (REQ 305017)
* Joel George               125710                     09/05/2017                   Req 322460  
* Lakshmi Madduri                                      10/30/2019                   Pharmacy Mail Order Section
* Subhamay Mandal                                      04/06/2020                   REQ - 988582 AHT/Quality - Ability to display Outcome Verbiage in the Pharmacy Authorization section - Authorization Summary (CRM)
* Sunil Kumar Reddy                                    04/24/2020                   User Story 1042378 - PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 5/8 and 5/22 Release (CRM)
* Riya Agarwal									       30/04/2020				    REQ - 729137 - MF 23 - Humana Pharmacy InComm- adding the hyperlink for the OTC ‘Benefit Type’ field when it equals ‘RxNova’
* Sayali Nemade										   4/29/2020					REQ - 842472 - Pharmacy InComm Service V2
* Nirmal Garg										   09/16/2021					Added browser logic for coveo
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
* Kalyani Pachpol                                      11/04/2022                   US-3578534
* Pinky Vijur                                          01/23/2023                User Story 3578587: T1PRJ0870026 Solutran Integrations - CRM FR5.01 - TECH - Remove CRM Service coding related to InComm OTC Balance
* Tharun Madishetti                                    03/31/2023                US-4250871:T1PRJ0891742- CRM IOP-2022-6037203-- Pharmacy Authorization Details: add Department,Coverage Start Date & Coverage End Date(Classic)
*****************************************************************************************************************************************/
public with sharing class PharmacyBenefits_C_HUM 
{
    
    private PharmacyBenefits_D_HUM oDataPharmacyBen= new PharmacyBenefits_D_HUM();
    private String sPolMemId  = '' ;
    public String sPolicyMemberId{get;
                              set {
                                    if(sPolicyMemberId==Null)
                                    {
                                        sPolicyMemberId= value;
                                        GetPolicyMemberName();
                                    }
                                  }  
                             }
    public string sPolicyMemberName{get;set;}
	public boolean bCoveoUser {get;set;}
    public boolean bPaHubswitch_4250871 {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
	
	/**
    * Method to get Member Data
    * <p>
    * @param  NA
    * 
    * @return void
    */	
    public void GetPolicyMemberName()
    {
        list<Member_ID__c > lstMemId =[SELECT Id, Name FROM Member_ID__c WHERE policy_Member__c=: sPolicyMemberId AND Type__c = 'Member-Id-Base' LIMIT 1];
        if(lstMemId.size()>0)
        {
        sPolicyMemberName = lstMemId[0].Name;
        }
        else
        {
        sPolicyMemberName = '';
        }
		
    }
	
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public PharmacyBenefits_C_HUM()
    { 
       sPolMemId  = ApexPages.currentpage().getParameters().get('id'); 
        //Added for logging
        bdisableButton=false;
        string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		sBrowserContext = ESAPI.encoder().SFDC_HTMLENCODE(sBrowserContext);
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
        bPaHubswitch_4250871 = HUMUtilityHelper.isCRMFunctionalityON('4250871');
    } 
    
    public Policy_Member__c objPolMem { get; set; }
    public Boolean bdisableButton {get;set;}
    public boolean bPharmacyMailOrderVisible{get;set;}
    public boolean bSwitchFunctionality{get;set;}
   
    
    /**
    * make a call to getPolicyMemberDatamethod of  PharmacyBenefits_D_HUM class to get Policy Member Data
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void getPolicyMemberData()
    { 
        try
        {
            if (String.isNotBlank(sPolMemId)) objPolMem = oDataPharmacyBen.getPolicyMemberData(sPolMemId);
            if(objPolMem != null && objPolMem.Product__c != null && objPolMem.Product_Type_Code__c != null && objPolMem.Policy_Platform__c != null && 
            objPolMem.Product__c =='MED' && (objPolMem.Product_Type_Code__c=='MA' || objPolMem.Product_Type_Code__c=='MAPD') && (objPolMem.Policy_Platform__c != 'CB' && objPolMem.Policy_Platform__c != 'CarePlus'))
            {
            	bPharmacyMailOrderVisible = true;
            }
            else{
            	bPharmacyMailOrderVisible = false;
            }
            bSwitchFunctionality = HUMTriggerSwitch__c.getValues('PharmacyBenefits').Exeute_Trigger__c;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyBenefits_C_HUM', 'getPolicyMemberData');
        }
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
}