/**************************************************************************** 
Apex Class Name  : QuickStartCI_DTO_AHT_HUM 
Version          : 1.0  
Created Date     : March 18, 2018
Function         : DTO for Classification and Intent for logged in User
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Subhamay Mandal                               03/18/2018                  Original Version
* Subhamay Mandal                               06/14/2019                  REQ - 402004 SF - TECH - Quick Start Performance Enhancements (RF)
* Subhamay Mandal                               06/25/2019                  REQ - 402804 Quality - Add Informational Message above Callback Number (RF)
* Subhamay Mandal								01/28/2020				    731580|729526|729566 PR00091574 - Code merged for 2/28
* Samantha Tennyson                             12/28/2020                  User Story 1745934: Prevent Complaint/G&A Rights Given fields to be selected on Cases with specific Classification and Intent Combinations (CRM)
* Samantha Tennyson				   				06/14/2021	               User Story 2372808: T1PRJ0001827 - MF 1 - Quality/FCR - Ability to display "Recommended Actions" in Quick Start (RF)
***************************************************************************************************/
public with sharing class QuickStartCI_DTO_AHT_HUM {
    public Id idClassification{get; set;}
    public String sClassificationLabel{get; set;}
    public Map<Id, Intent_DTO_AHT_HUM> mapIntentInfo{get; set;}
    
    public QuickStartCI_DTO_AHT_HUM(CTCI_Junction__c ctci, Map<Id, Intent_DTO_AHT_HUM> mapIntentInfo) {
        this.idClassification = ctci.Classification__c;
        this.sClassificationLabel = ctci.Classification__r.Name;
        this.mapIntentInfo = mapIntentInfo;
    }
    
    public class Intent_DTO_AHT_HUM {
        public Id idCTCI{get; set;}
        public Id idIntent{get; set;}
        public String sIntentLabel{get; set;}
        
        public Intent_DTO_AHT_HUM(CTCI_Junction__c ctci) {
            this.idCTCI = ctci.Id;
            this.idIntent = ctci.Intent__c;
            this.sIntentLabel = ctci.Intent__r.Name;
        } 
    }
    
    public class AdditionalInfo_DTO_AHT_HUM {
    	public String sInformationalMessage{get; set;}
    	public String sSoftWarningMessage{get; set;}
    	public String sOGOFieldSetup{get; set;}
    	public Boolean bAllowMultipleCase{get; set;}
        public String sHeaderMessageType{get; set;}
        public String sHeaderMessage{get; set;}
        public Boolean bNeverComplaint{get; set;}
        public Boolean bNeverGA{get; set;}
        public String sRecommendActions {get; set;}
    	public AdditionalInfo_DTO_AHT_HUM(Quick_Start_Pretext__c qsp) {
    		if(qsp != null) {
    			this.sInformationalMessage = qsp.Informational_Message__c;
    			this.sSoftWarningMessage = qsp.Soft_Warning_Message__c;
    			this.sOGOFieldSetup = qsp.Complaint_GA_Setup__c;
    			this.bAllowMultipleCase = qsp.Is_Allow_Multiple_Case__c;
                this.sHeaderMessageType = qsp.Header_Message_Type__c;
                this.sHeaderMessage = qsp.Header_Message__c;
                this.bNeverComplaint = qsp.CTCI_List_lkp__r.Never_a_Complaint__c;
                this.bNeverGA = qsp.CTCI_List_lkp__r.Never_a_G_A_Rights_Given__c;
                this.sRecommendActions = qsp.Recommended_Actions__c;
    		}
    	}
    }
}