/*
Class Name       : PolicyProductTypeWrapper_DTO_HUM
Version          : 1.0 
Created Date     : Feb 03 2016
Function         : Wrapper class for ProductType_C_HUM 
    Modification Log

  Developer       Code Review        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Payal Yadav                                                3/1/2016                           Original Version
* Sagar MN												 08/06/2020				    USER STORY 1329433 EDW Reporting changes
*/
public with sharing class PolicyProductTypeWrapper_DTO_HUM
{
    public PolicyProductTypeWrapper_DTO_HUM(Policy__c oPolicy , string sProductType )
    {
        this.oPolicy = oPolicy ;
        this.sProductType = sProductType ;
        if(String.isNotBlank(oPolicy.Platform__C)) this.sPlatfrom = oPolicy.Platform__C;
        if(String.isNotBlank(oPolicy.Source_Cust_Cov_Key__c)){
            if(String.isNotBlank(oPolicy.Platform__C) && this.sPlatfrom == 'LV'){                
                // sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c; 
                 this.sSourceCustKey = oPolicy.Source_Cust_Cov_Key__c.substring(0,6);
                 this.sSourceDivKey = oPolicy.Source_Cust_Cov_Key__c.substring(9,12);
           }else if(String.isNotBlank(oPolicy.Platform__C) && this.sPlatfrom == 'EM'){
               //sSourceCustKey = policyMember.policy__r.Source_Cust_Cov_Key__c;
                this.sSourceCustKey = oPolicy.Source_Cust_Cov_Key__c.substring(0,6);
                this.sSourceDivKey = oPolicy.Source_Cust_Cov_Key__c.substring(0,9);
           }else if(String.isNotBlank(oPolicy.Platform__C) && (this.sPlatfrom == 'CB' || this.sPlatfrom == 'PR')){
                this.sSourceCustKey = oPolicy.Source_Cust_Cov_Key__c;
                this.sSourceDivKey = '';
           }           
        
       }          
        if(String.isNotBlank(oPolicy.Benefit_Coverage__c)) this.sBenefitCoverage = oPolicy.Benefit_Coverage__c;
        if(String.isNotBlank(oPolicy.Coverage_Plan_Effective_Date__c)) this.sCoverageplanDate = oPolicy.Coverage_Plan_Effective_Date__c;
    
    }
        
    public Policy__c oPolicy {get;set;}
    public string sProductType {get;set;}
    public String sPlatfrom {get;set;}
    public String sSourceCustKey {get;set;}
    public String sSourceDivKey {get;set;}
    public String sBenefitCoverage {get;set;}
    public String sCoverageplanDate {get;set;} 
}