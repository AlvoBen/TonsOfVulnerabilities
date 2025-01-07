/******************************************************************************************************************
Apex class Name    : GroupSearchDetail_LC_Wrapper
Version            : 1.0
Function           : Wrapper DTO for HIDCMS GroupSearch
Created On         : February 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Suraj Patil                                    03/03/2022             Original Version REQ - 2865657
------------------------------------------------------------------------------------------------------------------*/
public class GroupSearchDetail_LC_Wrapper
{
    @AuraEnabled
    public String CardCount = '';
    @AuraEnabled
    public String CardRequestDate = '';
    @AuraEnabled
    public String Platform = '';
    @AuraEnabled
    public String GroupNbr = '';
}