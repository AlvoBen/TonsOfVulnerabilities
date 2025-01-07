/*****************************************************************************************
Apex class Name    : AuthRefferal_Wrapper_LDTO_HUM
Version            : 1.0
Function           : This is the wrapper Classs, to deserialize response.
Test Class         : Authsummary_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Rajesh Narode										14/07/2022			 User story 3362694 Authorization Summary table
  *********************************************************************************************************************************/
public with sharing class AuthRefferal_Wrapper_LDTO_HUM {

    @AuraEnabled public String sAuthorizationOrReferralNumber {set; get;}
    @AuraEnabled public String sAuthorizationType {set; get;}
    @AuraEnabled public String sOverallStatus {set; get;}              
    @AuraEnabled public String sAdmFirstDay {set; get;}
    @AuraEnabled public String sDischargeLastDay{set; get;}
    @AuraEnabled public String sServiceType {set; get;}
    @AuraEnabled public String sTreatingProvider {get; set;}
    @AuraEnabled public String sFacility {set; get;}
    @AuraEnabled public String sRequestingrovider {set; get;}
    @AuraEnabled public String sGroupId {set; get;}
    @AuraEnabled public Boolean bIsAccessible {set; get;}
    @AuraEnabled public Integer Totalcount {get;set;}
    @AuraEnabled public String recId {get;set;}
    @AuraEnabled public String sAtuhRefUrl {set; get;}
}