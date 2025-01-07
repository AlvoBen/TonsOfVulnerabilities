/******************************************************************************************************************
Apex class Name    : CaseRestInteractionResponse_DTO_HUM
Version            : 1.0
Function           : This class isthe response class for Interaction  details
Created On         : March 7th 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar                           03/07/2019            Original Version            
------------------------------------------------------------------------------------------------------------------- */

Public with sharing class CreateInteractionResponse_DTO_HUM
{
    Public CreateInteractionInfo CreateInteractionInfo;
    Public class CreateInteractionInfo
    {
        Public String ErrorCode='';
        Public String ErrorMessage='';
        Public String InteractionNumber='';
        Public Boolean Success;
        Public list<String> CreatedCases;
    }
}