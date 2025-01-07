/*******************************************************************************************************************************
Apex Class Name : GroupSearchPolicies_LD_HUM
Version         : 1.0
Created On      : 09/24/2020
Function        : This class is used for performing the Lightning Group Search for Policies
Test Class      : GroupSearchPolicies_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     09/24/2020                  Original Version
*    2.0            Kajal Namdev                                    05/05/2021                  Add query limit in SOQL
*    3.0           Ashish/Ritik                                     05/17/2021                  Replaced references of Policy__c object with PurchaserPlan Object
*    4.0           Ashish Kumar                                  	08/12/2021                	Added requestGroupPlan method for getting PurchaserPlan for Group Plan detail page
*    5.0           Kajal Namdev                                     09/29/2021          	    Added limitOne in query
*    6.0           Ritik Agarwal                                    10/22/2021          	    Added Benefit_Coverage__c in purchaserplan query of requestGroupPlan method
*******************************************************************************************************************************************************************/

public without sharing class GroupSearchPolicies_LD_HUM 
{
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    public static List<PurchaserPlan> requestPoliciesForGroup(String sAccId)
    {
        List<PurchaserPlan> lstPolicies= new List<PurchaserPlan>();
        
        lstPolicies=[Select Id,Name,Product__c,Product__r.Name ,Product__r.Major_LOB__c,EffectiveTo,EffectiveFrom, Product_Type__c,Platform__c, 
                     PayerId,Issue_State__c from PurchaserPlan where PayerId=:sAccId ORDER BY Product__r.Name ASC LIMIT: limitValue ];  
        
        return lstPolicies;
    }
    
    public static planWrapper requestGroupPlan(String sPlanId)
    {
        PurchaserPlan oPlan = new PurchaserPlan();
        String sProductType='';
        oPlan =[Select Id,Product__c,Product__r.Name ,EffectiveTo,EffectiveFrom,Major_LOB__c, Product_Type__c,
                Plan_Status__c,iab_description__c,Payer.Name,Benefit_Coverage__c from PurchaserPlan where Id= :sPlanId LIMIT 1]; 
        
        return new planWrapper(oPlan);
    }
    
    public class planWrapper{   
        @AuraEnabled public String effectiveTo;
        @AuraEnabled public String effectiveFrom;
        @AuraEnabled public String productType;
        @AuraEnabled public String product;
        @AuraEnabled public String benefitPackage;
        @AuraEnabled public String planStatus;
        @AuraEnabled public String accountName;
        
        public planWrapper(PurchaserPlan oPlan)
        {	
            effectiveTo = String.isNotBlank(String.valueOf(oPlan.EffectiveTo)) ? String.valueOf(oPlan.EffectiveTo) : '';
            effectiveFrom   = String.isNotBlank(String.valueOf(oPlan.EffectiveFrom)) ? String.valueOf(oPlan.EffectiveFrom) : '';
            product = String.isNotBlank(oPlan?.Product__r?.Name) ? oPlan.Product__r.Name: '';
            benefitPackage    = String.isNotBlank(oPlan.Benefit_Coverage__c) ? oPlan.Benefit_Coverage__c: '';
            planStatus  = String.isNotBlank(oPlan.Plan_Status__c) ? oPlan.Plan_Status__c: '';
            productType = (oPlan?.product__r?.name == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_FIELDVALUE_PRODUCTNAME ) ? (String.isNotBlank(oPlan.Product_Type__c) ? oPlan.Product_Type__c: '') : (String.isNotBlank(oPlan.Major_LOB__c) ? oPlan.Major_LOB__c: '');
            accountName  = String.isNotBlank(oPlan?.Payer?.Name) ? oPlan.Payer.Name: '';
        }
    }
}