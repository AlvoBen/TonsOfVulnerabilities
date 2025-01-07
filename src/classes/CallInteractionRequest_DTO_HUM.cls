/******************************************************************************************************************
Apex class Name    : CallInteractionRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for Call Interaction Service.
Created On         : June 24 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   06/24/2021		   Original Version	
*   1.1            Moshitha Gunasekaran		   11/09/2021		   Made changes to add additional parameters for CBIS
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CallInteractionRequest_DTO_HUM {
    
    public string InteractingWithType = '';
    public string InteractingWithID = '';
    public string InteractingWithDepCode = '';
    public string InteractingWithGenkey = '';
    public string InteractingWithFirstName = '';
    public string InteractingWithLastName = '';
    public string InteractingWithDOB = '';
    public string InteractingWithNPIID = '';
    public string InteractingWithPlatformCd = '';
    public string InteractingAboutType = '';
    public string InteractingAboutID = '';
    public string InteractingAboutDepCode = '';
    public string InteractingAboutGenkey = '';
    public string InteractingAboutFirstName = '';
    public string InteractingAboutLastName = '';
    public string InteractingAboutDOB = '';
    public string InteractingAboutNPIID = '';
    public string InteractingAboutPlatformCd = '';
    public string InteractingFirstName = '';
    public string InteractingLastName = '';
    public string UUID = '';
    public string CallCategory = '';
    public string InteractionOrigin = '';
    public string CallUserId = '';
    public string CallCategoryDescription = '';
    public string CallData = '';
    public boolean IsAuthenticated;

}