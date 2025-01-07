/******************************************************************************************************************
Apex class Name    : HIDCMSGroupSearchRequest_LDTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS LWC GroupSearch
Created On         : 03/03/2022
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Suraj Patil                                    03/03/2022             Original Version REQ - 2865657
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupSearchRequest_LDTO_HUM {

    @AuraEnabled public  GroupSearchRequestDTO   GroupSearchRequest ;

    public class  GroupSearchRequestDTO  {
        @AuraEnabled public String  GroupNumber = '';
        @AuraEnabled public String FromDate = '';
        @AuraEnabled public String ToDate = '';
    }

}