/******************************************************************************************************************
Apex class Name    : ChatterGetFeedRequestDTO
Version            : 1.0
Function           : This class is used as request for chatter get feed Rest Service.
Created On         : May 21 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   05/21/2021		   Original Version					
*   1.1			   Moshitha Gunasekaran        06/14/2021          Made changes as per latest ICD to add FeedItemId and Date functionalities
*   1.2			   Moshitha Gunasekaran        07/09/2021          Made changes as per latest ICD to add Page Number
------------------------------------------------------------------------------------------------------------------- */
public with sharing class ChatterGetFeedRequestDTO {
    
    public string StartDate = '';
    public string EndDate = '';
    public string PersonId = '';
    public string FeedItemId = '';
    public string PageNo = '';
}