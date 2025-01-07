/************************************************************************************************************
Apex Class Name  : HUMPolicyAccessDetailsWrapperHelper 
Version          : 1.0  
Created Date     : September 29 2014
Function         : Wrapper class for HUMPolicyMemberRelatedListController
Test Class      :  HUMPolicyAccessDetailsWrapperHelperTest


 * Developer Name           Code Review                  Date                       Description
 *----------------------------------------------------------------------------------------------------------
 * Shruthi Karanth             18234                     09/29/2014                  Original Version
 * Shikhar Mehta               24223                                                 3/31/2016 Defect#221159
 * Amar Gadewar                                          07/21/2017                  Poicy Sub Tab Name
 * Subhamay Mandal                                       07/24/2018                 REQ - 323537 Ability of a system to bring Medicare ID on the Policy mini Page
 * Dinesh Subramaniyan                                   04/05/2018                 REQ - 359993 - AHT/Quality - Ability to Prefill Part C/Part D in Quick Start(RF)
 * Suresh Gottipati            240518                    05/31/2018                 REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
 * Darshan Sharma                                        06/26/2018                 REQ-367008 - AHT - Preselect Policy from the Search Page (RF)
 * Santhosh Ganji                                        02/13/2019                 REQ - 379032 view all policies, but disable non-MED policy hyper-links for Pharmacy Users
 * Santhi Mandava                                        04/15/2019                 Replaced "Member_Id_Value__c" with Name
 * Keerti Durbha                                         04/23/2020                738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness
 * Keerti Durbha                                         05/12/2020                 738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness- on/off switch
 * Keerti Durbha                                         07/27/2020                 1039760- PR00091318 - CR05  - Enabling Call Trasnfer button for OSB policies
 * Sagar MN												 08/06/2020				    USER STORY 1329433 EDW Reporting changes
 * Asish Behera                                          09/04/2020                 PR00090588 - CR1727 - Display Medicare PDP Plan Name - Policy Section (Person Account Page)
 * Asish Behera                                          09/04/2020                 Build fix for   PR00090588 - CR1727
 * Amar Gadewar                                          04/06/2021                 User Story 2056116: T1PRJ0001827 - MF 1 - AHT - Ability to display Termed Policies information on the Member Search tab within the Search page (RF)
 * Ajay Chakradhar                           		     05/05/2021	                REQ - 2184294 - Replacing constants with Consumer Management Constants
 * Amar Gadewar                                          06/14/2021                 User Story 2369318: T1PRJ0001827 - MF 1 - AHT - Ability to default the value of Medicare Call Part C/D field in QS to 'Not Medicare' for Medicaid Policies (RF)
 * Amar Gadewar                                          07/07/2021                 User Story 2451265: T1PRJ0001827 - MF 1 - AHT - Ability to default the value of Medicare Call Part C/D field in QS to 'Not Medicare' for Medicaid Policies (CRM)
 * Atia Uzma                                             08/05/2021                 User Story 2551785: T1PRJ0001827 - MF 1 - SF - TECH - On/Off switch cleanup for 8/20/21 Release (CRM)
 * Akash Surwase                                         08/17/2022                 USER STORY 2819667 : T1PRJ0100059- MF 3 FR 10 - Case Creation (HP)
 -----------------------------------------------------------------------------------------------------------*/ 
public with sharing class HUMPolicyAccessDetailsWrapperHelper
{
    private static set<String> setMedicarePartCProductTypes = new set<String> {'MEF', 'MER', 'MEP', 'MGP', 'MGR', 'IND'};
    private static set<String> setMedicarePartDProductTypes = new set<String> {'PDP', 'MPD'};
    private String sNONPHARMACY_USER_HUM = 'No';
    private string sOSB_Indicator_value = 'O';
    private String sMEDICARE_POLICY = 'MED';
    private String sPRODUCT_TYPE_MA = 'MA';
    private String sPRODUCT_TYPE_PDP = 'PDP';
    private String sPRODUCT_TYPE_MCD = 'MCD';
    
    public HUMPolicyAccessDetailsWrapperHelper(){
            
    }
    public HUMPolicyAccessDetailsWrapperHelper(Policy_Member__c policyMember , boolean hasAccess, string sProductType)
    {
        this.policyMember  = policyMember ;
        sPolicyId = policyMember!=null?policyMember.policy__c:'';
        this.hasAccess = hasAccess ;
        this.sProductType = sProductType;
        this.nPolicyLink = false;
        this.isCallTransferDisabled = false;
        this.sProduct = policyMember.Product__c;

        //Agnostic customer
        if(String.isNotBlank(policyMember.Product__c) && String.valueOf(policyMember.Product__c) == 'TRA')
        {
        	this.bNonHumanaPolicy = true;
	    }
        if(String.isNotBlank(policyMember.Product__c) && String.valueOf(policyMember.Product__c) != 'MED' && Pharmacy_H_HUM.isPharmacyUser() != sNONPHARMACY_USER_HUM)
        {
           this.nPolicyLink = true;
        }

        // On/Off switch functionality for OSB Products policy member id hyperlink And Call Transfer button

         Boolean OSBONOFF= HUMUtilityHelper.isCRMFunctionalityON('738134');
      
          if(OSBONOFF)
          {
         // For OSB Product, disabling the Policy Member ID hyper link And Call Transfer button 
      
          if(String.isNotBlank(policyMember.OSB_Indicator__c) && policyMember.OSB_Indicator__c.equalsIgnoreCase(sOSB_Indicator_value))

	{
          Boolean OSBCalltransferOnOFF=HUMUtilityHelper.isCRMFunctionalityON('1039544');   
               this.nPolicyLink = true;
               this.isCallTransferDisabled = (OSBCalltransferOnOFF) ? false : true;
          }
              
         }
        if(String.isNotBlank(policyMember.Product__c) && policyMember.Product__c.trim().length() < 4 )
        {
            sSubTabName = policyMember.Id + '|'+ policyMember.Product__c + '-' + policyMember.Name;
            
        }
        else 
        {
            sSubTabName = policyMember.Id + '|' + policyMember.Name;
        }
        
        List<Member_ID__c> listMemberIds = policyMember.Member_Ids__r;
        
        if(!listMemberIds.isEmpty()) {
                this.sMemberIdName = listMemberIds.get(0).Name;  
        }
        
        if(String.isNotBlank(policyMember.Product__c) && String.isNotBlank(sProductType) && policyMember.Product__c == sMEDICARE_POLICY) {
            
            if(String.isNotBlank(policyMember.policy__r.Product_Type__c) 
                    && setMedicarePartCProductTypes.contains(sProductType) 
                        && policyMember.policy__r.Product_Type__c == sPRODUCT_TYPE_MA ) {
                
                sMedicareValue = 'Medicare Part C';
                
            } else if(String.isNotBlank(policyMember.policy__r.Product_Type__c) 
                        && setMedicarePartDProductTypes.contains(sProductType) && policyMember.policy__r.Product_Type__c == sPRODUCT_TYPE_PDP)  {
                
                sMedicareValue = 'Medicare Part D';

            } else if(sProductType == sPRODUCT_TYPE_MCD)  {
                
                sMedicareValue = 'Not Medicare';
            } 
            
        }
	Boolean bSwitch1279870 = HUMUtilityHelper.isCRMFunctionalityON('1279870');
        // set variable only for PDP policy
        if(bSwitch1279870 && policyMember.Product__c == sMEDICARE_POLICY && sProductType == sPRODUCT_TYPE_PDP && policyMember.policy__r.Product_Type__c == sPRODUCT_TYPE_PDP){
             IsPDPPolicy = 'true';
        } 
            if(String.isNotBlank(policyMember.policy__r.Platform__C)) this.sPlatfrom = policyMember.policy__r.Platform__C;
            if(String.isNotBlank(policyMember.policy__r.Source_Cust_Cov_Key__c)){
            if(String.isNotBlank(policyMember.policy__r.Platform__C) && this.sPlatfrom == 'LV'){                
                // sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c; 
                 this.sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c.substring(0,6);
                 this.sSourceDivKey = policyMember.policy__r.Source_Cust_Cov_Key__c.substring(9,12);
           }else if(String.isNotBlank(policyMember.policy__r.Platform__C) && this.sPlatfrom == 'EM'){
               //sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c;
                this.sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c.substring(0,6);
                this.sSourceDivKey = policyMember.policy__r.Source_Cust_Cov_Key__c.substring(0,9);
           }else if(String.isNotBlank(policyMember.policy__r.Platform__C) && (this.sPlatfrom == 'CB' || this.sPlatfrom == 'PR')){
                this.sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c;
                this.sSourceDivKey = '';
           }           
        
       } 
            if(String.isNotBlank(policyMember.policy__r.Benefit_Coverage__c)) this.sBenefitCoverage = policyMember.policy__r.Benefit_Coverage__c;
            if(String.isNotBlank(policyMember.policy__r.Coverage_Plan_Effective_Date__c)) this.sCoverageplanDate = policyMember.policy__r.Coverage_Plan_Effective_Date__c;
        
    }       
    public Policy_Member__c policyMember {get;set;}
    public boolean hasAccess {get;set;} 
    public boolean hasHumanaProfileAccess {get;set;}
    public string sProduct {get;set;} // Agnostic Customer
    public boolean bNonHumanaPolicy {get;set;} //Agnostic Customer   
    public string sProductType {get;set;} 
    public String sSubTabName {get;set;}
    public String sMemberIdName{get; set;}
    public string sMedicareValue{get;set;}
    public String sPreselect{get;set;}
    public String sPolicyId{get;set;}
    public boolean nPolicyLink {get;set;}
    public boolean isCallTransferDisabled {get;set;}
    public Integer rowNumber{get;set;}
    public String IsPDPPolicy {get;set;}
    public string sPlanName {get;set;}
    public string sEffectiveDate {get;set;}
    public string sEndDate {get;set;}
   
   public String sPlatfrom {get;set;}
   public String sSourceCustKey {get;set;}
   public String sSourceDivKey {get;set;}
   public String sBenefitCoverage {get;set;}
   public String sCoverageplanDate {get;set;} 
    
    
}