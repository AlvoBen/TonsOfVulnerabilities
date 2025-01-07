/*******************************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Pinky Vijur                     					03/01/2024                User Story 5428942: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch from pharmacy
************************************************************************************************************************************/
public with sharing class PharmacyWebIssues_LC_HUM {
     @AuraEnabled (cacheable=true)
    public static String getWebIssuesTN(){
        List<Template__c> tn = [SELECT NAME FROM Template__c WHERE Description__c = 'Centerwell Pharmacy Web Issues' WITH USER_MODE Limit 1];
        if (!tn.isEmpty()){
            System.debug('tn:'+tn[0].Name);
            return tn[0].Name;
        } else {
            return null;
        }
      }

}