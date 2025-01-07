/*****************************************************************************************
Apex class Name    : DualEligibleDetails_DTO_HUM
Version            : 1.0
Function           : This is the DTO class for GetDualInfo Service 
Created Date       : 05/18/2019 
Test Class         : DualEligibleDetails_DTO_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnam                                    05/18/2019          Dual Status & Deeming period sections on Dual Eligible Details Page   
*  Kajal Namdev                                    06/21/2021           Added AuraEnabled
*********************************************************************************************************************************/

public class DualEligibleDetails_DTO_HUM
{
    @AuraEnabled Public String sEffectiveDate{get; set;}
    @AuraEnabled public String sEndDate{get; set;}
    @AuraEnabled public String sValue{get; set;}
    @AuraEnabled public String sIndicator{get; set;}
    @AuraEnabled public String sEffectiveDateDeeming {get; set;} 
    @AuraEnabled public String sEndDateDeeming {get; set;}
    @AuraEnabled public String sIndicatorDeeming {get; set;}
      
}