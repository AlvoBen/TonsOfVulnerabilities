/******************************************************************************************************************
Apex class Name    : EntityIdentifierDTO
Version            : 1.0
Function           : Test class is Entity Identifier DTO generic for GetInteractionInfo & GetCaseInfo methods of Case Service.
Created On         : July 01 2019
Test Class         : GetInteractionInfoRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Mohammed Noor                                  07/01/2019        Original Version
* ------------------------------------------------------------------------------------------------------------------- */
public class EntityIdentifierDTO
{
   	public String EntityType = '';  
    public String IdValue = ''; 
    public string IdValueType='';
    public string Platform='';
    public string MemberIdDependentCode='';
}