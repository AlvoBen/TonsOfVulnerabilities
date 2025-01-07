/******************************************************************************************************************
Apex class Name    : HIDCMSGroupSearchRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for HIDCMS GroupSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Prasad Jandhyala             212450           02/27/2018             Original Version -> REQ - 355978
------------------------------------------------------------------------------------------------------------------*/
public with sharing class HIDCMSGroupSearchRequest_DTO_HUM {

    public  GroupSearchRequestDTO   GroupSearchRequest ;

    public class  GroupSearchRequestDTO  {
        public String  GroupNumber = '';
        public String FromDate = '';
        public String ToDate = '';
    }

}