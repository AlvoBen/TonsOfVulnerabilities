/******************************************************************************************************************
Apex class Name    : UpdateInteractionResponse_DTO_HUM
Version            : 1.0
Function           : This class is the response class for Interaction  details update
Created On         : May 2nd, 2019
Test Class         : UpdateInteractionResponse_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar                           05/02/2019            Original Version            
------------------------------------------------------------------------------------------------------------------- */

Public with sharing class UpdateInteractionResponse_DTO_HUM
{
    Public updateInteractionInfo UpdateInteractionResponse;
    Public Class updateInteractionInfo 
    {
        Public String ErrorCode='';
        Public String ErrorMessage='';
        Public String InteractionNumber='';
        Public Boolean Success;
        Public list<String> CreatedCases;
    }
}