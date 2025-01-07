/*******************************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Isaac Chung                       12/12/2023                 US 5107579 Mail Order Management; Pharmacy- Guided Flow- Inactivate Rx- Implementation (Lightning)
************************************************************************************************************************************/

public with sharing class PharmacyInactivateRx_LC_HUM {
    /*
    * Method Name : getInactivateRxTN
    * Description : this method Is used To get tn for inactivaterx
    * Return type : Template__c.Name
    * Parameter   : template Name
    */
    @AuraEnabled (cacheable=true)
    public static String getInactivateRxTN(){
        List<Template__c> tn = [SELECT Id, Name FROM Template__c WHERE Description__c='Inactivate Rx' with USER_MODE Limit 1];
        if (!tn.isEmpty()){
            return tn[0].Name;
        } else {
            return null;
        }
      }
}