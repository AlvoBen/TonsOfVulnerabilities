/***************************************************************************************************
Apex Class Name  : ValidateAccessOnCRMRetailLocation_H_HUM 
Version          : 1.0
Created Date     : 08/12/2019
Function         : Helper class for ValidateAccessOnCRMRetailLocation_G_HUM Trigger
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               08/12/2019                    Original Version. 
*  Luke P. Cecil                                02/20/2021                    Sonar Qube security fixres. 
*  Abhishek Maurya                              07/03/2021                    USer Story 2146105 -CRM Storefront: Delete Calendar Event 
***************************************************************************************************/

public with sharing class ValidateAccessOnCRMRetailLocation_H_HUM{
    /**
    *  Checks for User Access on Location before Delete
    *  @name checkForLocationAccessOnDelete
    *  @param locations Holds the list of all locations records
    *  @return void
    *  @throws NA
    */
    public static void checkForLocationAccessOnDelete(List<Storefront_Location__c> locations)
    {
        try{
            List<String> permissionSetList = GenericHelper_CRMRetail_H_HUM.getUserPermissionSet();
            if(!permissionSetList.isEmpty()){
                if(permissionSetList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN)){
                    generateEvent(locations,true,Label.CRMRetail_Active_Location);
                }
                else if(permissionSetList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN)){
                    generateEvent(locations,false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LOCATION_DEL_MSG);
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'ValidateAccessOnCRMRetailLocation_G_HUM', 'ValidateAccessOnCRMRetailLocation_G_HUM');
        }
    }
    /**
     *  Checks for User Access on Location before Update
     *  @name checkForLocationAccessOnUpdate
     *  @param locations Holds the list of all locations records
     *  @return void
     *  @throws NA
     */
    public static void checkForLocationAccessOnUpdate(List<Storefront_Location__c> locations)
    {
            List<String> permissionSetList = GenericHelper_CRMRetail_H_HUM.getUserPermissionSet();
            if(!permissionSetList.isEmpty()){
                if(permissionSetList.CONTAINS(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN))
                {
                    generateEvent(locations,false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LOCATION_EDIT_MSG);
                }
            }
    }
    /**
     *  Generate Error in both Update and Delete Event
     *  @name generateEvent
     *  @param locations Holds the list of all locations records, Boolean
     *  @return void
     *  @throws NA
     */
    private static void generateEvent(List<Storefront_Location__c> locations,Boolean isSuperAdmin, String errorMessage)
    {
        for(Storefront_Location__c oRecord : locations)
        {   
            if(isSuperAdmin){
                if(!oRecord.Inactive_indicator__c){
                    oRecord.addError(errorMessage);
                }
            }
            else{
                oRecord.addError(errorMessage);
            }
        } 
    }
    
}