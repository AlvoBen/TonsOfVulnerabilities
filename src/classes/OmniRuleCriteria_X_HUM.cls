/***************************************************************************************************************************************************
Apex Class Name  : OmniRuleCriteria_X_HUM
Version          : 1.0
Created Date     : 12/22/2017
Function         : Extension created to display the Omni Rule Criteria table on the Omni Case Assignment Rule detail page.
Test Class       : OmniRuleCriteria_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date             Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor             161414                 12/22/2017         Original Version 
* Syed Mubarak T									03/19/2019			  REQ - 386810 - Omni Configuration Item changes
****************************************************************************************************************************************************/

public with sharing class OmniRuleCriteria_X_HUM 
{
    public string ruleID {get; set;}
    public List<Omni_Rule_Criteria__c> ruleEntries { get; set;} 
    public String jsonData {get; set;}  
    public String fieldListJSON {get; set;} 
    
    /*    
    * Constructor
    * @param  NA
    * @return void     
    */
    public OmniRuleCriteria_X_HUM(ApexPages.StandardController controller) 
    {
        ruleID = ApexPages.currentPage().getParameters().get('id');
        ruleEntries = new List<Omni_Rule_Criteria__c>();                
    }
    
    /*
    * This method is used to fetch the Omni Rule Criteria related to Omni Case Assignment rule.
    * @param NA   
    * @return void
    */                  
    public void getRuleCriterias()
    {                
        ruleEntries  = [SELECT Id, Name, Criteria_Field__c, Criteria_Operator__c, Criteria_Field_Value__c
                        FROM Omni_Rule_Criteria__c WHERE Omni_Case_Assignment_Rule__c = :ruleID
                        ORDER BY Name];
       List<OmniRuleCriteria_Fields__mdt> listRCFields = [SELECT MasterLabel, Field_APIName__c, Field_Type__c,Object_Name__c FROM OmniRuleCriteria_Fields__mdt ORDER BY MasterLabel ];
                        
        jsonData = JSON.serialize(ruleEntries);
        jsonData = '{"data":' + jsonData + '}';
        jsonData = jsonData.replace('\'', '\\\''); 
        
        fieldListJSON = JSON.serialize(listRCFields);
        fieldListJSON = fieldListJSON.replace('\'', '\\\'');
                                        
    }
            
}