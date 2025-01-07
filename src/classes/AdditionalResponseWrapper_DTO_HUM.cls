/**************************************************************************************************************************************************
    @Apex class Name    : AdditionalResponseWrapper_DTO_HUM.cls
    @Function           : This class serves as DTO class for Additional Detail Section from MemberPlan Page
    @Version            : 1.0
    @test class         : AdditionalResponseWrapper_DTO_HUM.cls

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kajal Namdev                                  07/01/2021                Additional Information wrapper 
* Ankima Srivastava                             09/01/2021                Cost Share Information related change added 
* Ankima Srivastava                             09/06/2021                Cost Share Information related change removed 
* Ankima Srivastava                             10/25/2021                Attribute added for Vendor Section    
************************************************************************************************************************************************* */
public class AdditionalResponseWrapper_DTO_HUM 
{
    @AuraEnabled public List<ValidOIWrapper_DTO_HUM> validOIWrapperList;
    @AuraEnabled public boolean isMemDualEligibleInLastTwelveMonth;
    @AuraEnabled public boolean isOnSwitch;
    @AuraEnabled public boolean isDualEligibleListHasValue;
    @AuraEnabled public Map<string,List<string>> ediMap = new Map<string,List<string>>();   
    @AuraEnabled public Map<String,String> gbeMap = new Map<String,String>();
    // below are the variables to store the Member Plan - Dual Eligibility Details 
    @AuraEnabled public List<DualEligibleDetails_DTO_HUM> lstDualStatusLevel;
    @AuraEnabled public List<DualEligibleDetails_DTO_HUM> lstDeemingPeriods;
    @AuraEnabled public boolean showVendorSection;
}