/******************************************************************************************************************
Apex class Name    : GetCarePlanRequestDTO
Version            : 1.0
Function           : This class is used as request for getting CarePlans for CarePlanService.
Created On         : May 28 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   1.0				   Original Version					
------------------------------------------------------------------------------------------------------------------- */
public with sharing class GetCarePlanRequestDTO {
    public string CarePlanType = '';
    public string PersonId = '';
}