/*****************************************************************************************
Apex class Name    : VeteranHistoryTracking_DTO_HUM
Version            : 1.0
Function           : This is the service class to hit the Veteran Serivce to get the history data in Source system and CRM and
					 To fetch the Tracking information from Veteran Service
This class is used to display the Veteran history tracking
Created Date       : 04/21/2021
Test Class         : 

Modification Log
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------  
*   Giri Yeddula                                      04/21/2021          Original Version
*   Visweswararao J                                   02/16/2023          User Story 3782478: T1PRJ0865978 -19972 Lightning- Veteran's Tracking link Section and fields (Jaguars)
****************************************************************************************************************************/

public class VeteranHistoryTracking_DTO_HUM {

    @AuraEnabled public  string sLastmodifiedDate {get;set;}
    @AuraEnabled public  String sModifiedBy {get;set;} 
    @AuraEnabled public  String sActionStatus {get;set;}
    @AuraEnabled public  String sApplicationUsed {get;set;}
}