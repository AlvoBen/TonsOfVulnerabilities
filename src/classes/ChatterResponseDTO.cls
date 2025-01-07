/******************************************************************************************************************
Apex class Name    : ChatterResponseDTO
Version            : 1.0
Function           : This class is used as request for chatter post feed Rest Service.
Created On         : May 21 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   05/21/2021		   Original Version		
*   1.1			   Moshitha Gunasekaran        06/08/2021          Made changes as per latest ICD to add FeedItemId			
------------------------------------------------------------------------------------------------------------------- */
public with sharing class ChatterResponseDTO {
    public boolean Success = false;
    public string FeedItemId = '';
    public string ErrorMessage = '';
}