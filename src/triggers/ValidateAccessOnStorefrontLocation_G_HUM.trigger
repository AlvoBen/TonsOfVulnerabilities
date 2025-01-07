/*******************************************************************************************************************************
Trigger Name    : ValidateAccessOnStorefrontLocation_G_HUM 
Version         : 1.0
Created On      : 08/12/2019 
Function        : Trigger execution logic to restrict delete access for coordinator on Location object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               08/12/2019            REQ -401378 Original Version.
*  Luke P. Cecil		                        02/18/2021            User Story 1909843: PR00091869 - Storefront Security Vulnerabilities from SonarQube -1
*  Abhishek Maurya                              07/03/2021                    USer Story 2146105 -CRM Storefront: Delete Calendar Event 
*******************************************************************************************************************************/
trigger ValidateAccessOnStorefrontLocation_G_HUM on Storefront_Location__c (before delete,before update) {
   if(Trigger.isBefore){
        if(Trigger.isUpdate){
            ValidateAccessOnCRMRetailLocation_H_HUM.checkForLocationAccessOnUpdate(Trigger.new);
        }
        if(Trigger.isDelete){
            ValidateAccessOnCRMRetailLocation_H_HUM.checkForLocationAccessOnDelete(Trigger.old);
        }
    }
}