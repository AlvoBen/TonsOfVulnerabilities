/*****************************************************************************************
Apex class Name    : AccLegacyContactHistory_LC_HUM
Version            : 1.0
Function           : Gets legacy History Records as Wrapper to LWC caller   
Created Date       : 03/08/2021 
Test Class         : AccLegacyContactHistory_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Ritik Agarwal                                      03/08/2021          Initial version(#2039867)
 *********************************************************************************************************************************/

public with sharing class AccLegacyContactHistory_LC_HUM {

     /**
     * <p>
     * callout for Inquiry Summary service
     *
     * @param  String String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sEnterpriseID, String sRecordType
     * @return Object
     */
    @AuraEnabled(continuation=true)
    public static Object getInquirySummary(String recId,Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sRecordType)
    {
        List<Account> currentAccountLst = AccLegacyContactHistory_LD_HUM.getAccDetails(recId);
        String sMembergenkey = currentAccountLst[0] ?.Mbr_Gen_Key__c;
        String sEnterpriseID =  currentAccountLst[0] ?.Enterprise_Id__c;
        String sMemberId = null;
        return AccLegacyContactHistory_LS_HUM.requestInquirySummary(sMembergenkey, sMemberid, startCount, endCount, sStartDate, sEndDate, sEnterpriseID,sRecordType);
    } 
    
       /**
     * <p>
     * callout for Inquiry Single Reference service
     *
     * @param  String sMembergenkey, sReferenceNbr,sEnterpriseID, String sRecordType
     * @return Object
     */
     @AuraEnabled(continuation=true)
    public static object getInquirySingleReference(String recId, String sReferenceNbr, String sRecordType)
    {
        List<Account> currentAccountLst = AccLegacyContactHistory_LD_HUM.getAccDetails(recId);
        String sMembergenkey = currentAccountLst[0] ?.Mbr_Gen_Key__c;
        String sEnterpriseID = currentAccountLst[0] ?.Enterprise_Id__c;
        return AccLegacyContactHistory_LS_HUM.requestInquirySingleReference(sMembergenkey, sReferenceNbr, sEnterpriseID,sRecordType);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse InquirySummary Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return string
     */
    @AuraEnabled
    public static object parseInquirySummary(List<string> labels,object state)
    {
         return AccLegacyContactHistory_LS_HUM.parseInquirySummary(labels, state);
    }
}