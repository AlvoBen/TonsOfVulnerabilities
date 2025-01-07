/**
 *   name        : GenericReadCustomMetadat_LC_HUM
 *   Description : This class serves as controller for GenericReadCustomMetadataValues LWC component
 *   Version     : 1.0
 *   Test        : GenericReadCustomMetadat_LT_HUM
 *   Modification Log:
    
 * Developer Name        Code Review #         Date                       Description
 *---------------------------------------------------------------------------------------------------
 * Nirmal Garg                          	09/05/2022                  Original Version 
 ************************************************************************************************************************************************* */
 
public with sharing class GenericReadCustomMetadat_LC_HUM {
    public GenericReadCustomMetadat_LC_HUM() {

    }

    @AuraEnabled(cacheable=true)
    public static List<LightningPageControls__mdt> getMetadataValues(String pagename){
        try {
            if(String.isNotBlank(pagename)){
                List<LightningPageControls__mdt> lstCustomMetadavalues = [SELECT Id, Label, ControlType__c, Name__c, 
                DataId__c, Placeholder__c, Visible__c, Required__c, Disabled__c, Style__c, Multipicklist__c, Class__c,Variant__c,
                order__c, fieldname__c, label__c,
                LightningPage__r.PageName__c FROM LightningPageControls__mdt where LightningPage__r.PageName__c = :pagename
                order by order__c];
                return lstCustomMetadavalues != null && lstCustomMetadavalues.size() > 0 ? lstCustomMetadavalues : null;
            }else{
                return null;
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}