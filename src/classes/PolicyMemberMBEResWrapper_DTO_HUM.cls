/**************************************************************************************************************************************************
    @Apex class Name    : PolicyMemberMBEResWrapper_DTO_HUM.cls
    @Function           : This class serves as DTO class for MBEPlus service response from Policy Member Page
    @Version            : 1.0
    @test class         : ValidOIPolicyMemberID_D_T_HUM.cls

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Raja Sekhar Reddy M V                     08/20/2020	                PR00095673_US_1276005_DualstatusIndicator
************************************************************************************************************************************************* */

public with sharing class PolicyMemberMBEResWrapper_DTO_HUM
{   
    public List<ValidOIWrapper_DTO_HUM> validOIWrapperList  {get; set;}
    public boolean isMemDualEligibleInLastTwelveMonth {get; set;}

}