/******************************************************************************************************************
Apex class Name    : CallInteractionResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as response for Call Interaction Service.
Created On         : June 24 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   06/24/2021		   Original Version	
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CallInteractionResponse_DTO_HUM {
    
    public boolean Success;
    public string InteractionNumber = '';
    public string ErrorMessage = '';
}