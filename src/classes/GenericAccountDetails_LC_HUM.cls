/*
* Apex Class Name        : GenericAccountDetails_LC_HUM.cls
* Function               : For Generic Account details
* Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     09/20/2023                   Initial version                          
****************************************************************************************************************************/
public with sharing class GenericAccountDetails_LC_HUM {

    @AuraEnabled(cacheable=true)
    public static List<Account> getAccountList(String recordId){
        try {
            if(String.isNotBlank(recordId)){
                Return [select id ,Mbr_Gen_Key__c,Security_Groups__c , RecordType.Name, PersonMailingAddress,PersonMailingStreet,PersonMailingCity,PersonMailingStateCode,PersonMailingState,PersonMailingPostalCode,PersonMailingCountry, PersonEmail, PersonHomePhone from Account where Id=: recordId WITH USER_MODE ];
            }else{
                throw new HUMCustomException('Error');
            }
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    }
}