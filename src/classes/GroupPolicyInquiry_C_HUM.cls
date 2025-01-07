/*******************************************************************************************************************************
Apex Class Name : GroupPolicyInquiry_C_HUM.cls
Version         : 1.0
Created On      : 2020
Function        : Extension controller for GroupPolicyInquiry_VF_HUM Page
Test Class      : GroupPolicyInquiry_T_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                       07/24/2020                  Added Switch for REQ - 1212111 : MF 3 - CRM User Access to CCP GCP Data in Repository (Legacy Contact History Section - Group)
* Sayali Nemade 				                      08/27/2020                  REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)
* Sayali Nemade 								      09/03/2020                  REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider and Agent/Broker
* Sayali Nemade 					   				  09/12/2020                 REQ - 1138575 Logging Component for Inquiry Details
* Sayali Nemade 					   				  09/18/2020                 REQ - 1170241 Sending Group Enterprise to Request 
* Suraj Patil								   		  04/09/2021				 REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues 
*Prasuna Pattabhi                                   11/11/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant - Account And Policy Pages
*****************************************************************************************************************************************/

public with sharing class GroupPolicyInquiry_C_HUM {

    public Boolean bSwitchlegacyContactHistory {get;set;}
	public String sPageName {get;set;}
	public String sInteractingAboutCCPGCP{get;set;}
    public String sSelectedPolicy{get;set;}
    public String sPolicyId{get;set;}
    public String sAccName{get;set;}
    public String sProductName{get;set;}
	public String sEnterpriseID {get; set;}
	public String sMemberId{get;set;}
       
   /**
     * Returns void
     * <p>
     * constructor to implement switch for Group Policy Inquiry Component
     * 
     */
    public GroupPolicyInquiry_C_HUM(ApexPages.StandardController controller)
    {
		sPolicyId = ApexPages.currentPage().getParameters().get('Id');
        if(sPolicyId != null) {
            List<Policy__c> currentGrpPolicyLst = [SELECT Id, Name, Group_Name__r.Enterprise_ID__c, Group_Name__r.Mbr_Gen_Key__c, Product__r.Name, Group_Number__c FROM Policy__c where Id=:ApexPages.currentPage().getParameters().get('Id') LIMIT 1];
            if(currentGrpPolicyLst != null && currentGrpPolicyLst.size() != 0) {
                sInteractingAboutCCPGCP = currentGrpPolicyLst[0].Group_Name__r.Mbr_Gen_Key__c;
                if(currentGrpPolicyLst[0].Name != null) {
                    sMemberId = '';
                    sSelectedPolicy = '';
					sAccName = currentGrpPolicyLst[0].Name;
                }
                sProductName = currentGrpPolicyLst[0].Product__r.Name;
				sEnterpriseID = currentGrpPolicyLst[0].Group_Name__r.Enterprise_ID__c;
            }
        }
		//Added Switch For REQ - 1212111
         bSwitchlegacyContactHistory = HUMUtilityHelper.isCRMFunctionalityON('1137828'); 
		 sPageName = 'GroupPolicy';
    }
}