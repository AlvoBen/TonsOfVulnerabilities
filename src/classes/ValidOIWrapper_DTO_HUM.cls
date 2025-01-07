/**************************************************************************************************************************************************
    @Apex class Name    :  ValidOIWrapper_DTO_HUM
    @Function           :  This class serves as DTO wrapper for MBEPlus Service.
    @Version            : 1.0
    @reference          : ValidOIPolicyMemberID_D_HUM

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Riddhi Mittal                             06/19/2020                  Original Version of MBEPlus Response  
* Kajal Namdev                              06/21/2021                  Added Auraenabled  
************************************************************************************************************************************************* */

public with sharing class ValidOIWrapper_DTO_HUM 
{
    @Auraenabled public string sCarrierName {get; set;}
    @Auraenabled public string sEffectiveDate {get; set;}
    @Auraenabled public String sEndDate {get; set;}
    @Auraenabled public string sPrimary {get; set;}{}
    @Auraenabled public string sOILastVerifiedDate {get; set;}
    @Auraenabled public string sLastUpdatedDate {get; set;}
}