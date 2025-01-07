/**************************************************************************************************************************************************
    @Apex class Name    : MBEPluseResponseWrapper_DTO_Hum.cls
    @Function           : This class serves as DTO class for Plan member Tab from Pharmacy Page
    @Version            : 1.0
    @test class         : MBEPluseResponseWrapper_DTO_Hum.cls

Modification Log:
* Developer Name                 Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane              09/02/2021                UserStory:2508657 HP- Ability to add LIS and Other Insurance Details to the Plan Member card
************************************************************************************************************************************************* */
public class MBEPluseResponseWrapper_DTO_Hum 
{
    @AuraEnabled public List<ValidOIWrapper_DTO_HUM> validOIWrapperList;
       
}