/********************************************************************************************************************************************************
Apex Class Name  : CRMRetail_Custom_Notification_C_HUM
Version          : 1.0 
Created Date     : APR 25 2022
Function         : Apex Class to support CRMRetail_Custom_Notification_Section_LWC_HUM lwc component
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Vivek Sharma                   04/25/2022              User Story 3316820: T1PRJ0154546 / SF / MF9 Storefront - SNP: Change Notifications Section to Custom
*******************************************************************************************************************************************************/
public with sharing class CRMRetail_Custom_Notification_C_HUM {
    /*
    * Method Name   :    getVisitorIndicators
    * Description   :    returns the indicators field value as a string.
    * Return Type   :    String
    * Parameters    :    current record id.
    */
    @AuraEnabled (cacheable = true)
    public static string getVisitorIndicators(string recId){
        string SNPVar;
        try {
            Storefront_Interaction__c intObj = [SELECT Id, Storefront_Vstr_Indicator__c FROM Storefront_Interaction__c WHERE Id =: recId LIMIT 1];
            if(String.isNotBlank(intObj.Storefront_Vstr_Indicator__c)){
                SNPVar = intObj.Storefront_Vstr_Indicator__c;
            }
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CUSTOMNOTIF_HUM, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_GETINDICATOR);
            throw new AuraHandledException(e.getMessage());
        }
        return SNPVar;
    }
    /*
    * Method Name   :    validateAccess
    * Description   :    validates if the current user is super admin or not.
    * Return Type   :    boolean
    * Parameters    :    N/A.
    */
    @AuraEnabled
    public static boolean validateAccess(){
        return [SELECT Id FROM PermissionSetAssignment WHERE PermissionSet.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN AND AssigneeId =: UserInfo.getUserId()].size() > 0;
    }
}