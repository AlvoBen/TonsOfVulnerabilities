/*****************************************************************************************************************************
Apex Class Name : PharmacyFinancial_LC_HUM
Version         : 1.0
Created On      : 09/24/2021
Function        : Class contains methods for services to fetch Pharmacy Financial information.
Test Class      : PharmacyFinancial_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Swapnali Sonawane                                    09/24/2021               
*    1.1            Jonathan Dickinson                                   11/05/2021                 added functionality from non-lightning class to create and edit 
 *   1.2           Monali Jagtap                                       09/29/2023       US- 4429836 T1PRJ0891339 - MF 1 - SF -TECH - Error Handling - Make sure all exceptions are handled with appropriate messages                                                                                                 credit card information         
*******************************************************************************************************************************************************************/

public with sharing class PharmacyFinancial_LC_HUM {
    
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getAdjustmentsAndPayments details continuation requests
     *
     * @param  String Enterprise Id
     * @param  String Start Date
     * @param  String End Date
     * @return continuation object
     */
    @AuraEnabled(continuation=true cacheable=true)
    public static Object invokeGetAdjPayService(String sMemID , String sStartDate , String sEndDate , String sPayment , String sAdjustment, string networkID, String sRecordId)
    {         
        return PharmacyFinancial_LS_HUM.SendRequestGetMemberResponse(sMemID, sStartDate, sEndDate , sPayment , sAdjustment, networkID, sRecordId); 
    }
        
    /**
     * Returns request object of continuation object
     * Method to invoke the EditMember continuation requests
   * @param - enterpriseID, netWorkId, creditCardKeyVar, amountVar, paymentDateVar, isPaymentAuthorizedVar
     * @return continuation object
    */
    @AuraEnabled(continuation=true)
    public static Object invokeOneTimeCreditCardPayment(string enterpriseID, string netWorkId, string creditCardKeyVar, decimal amountVar, string paymentDateVar, boolean isPaymentAuthorizedVar)
    {
        Date paymentDateVarDateFormat =  date.valueOf(paymentDateVar);
        return PharmacyFinancial_LS_HUM.SendRequestOneTimeCreditCardPayment(enterpriseID, netWorkId, creditCardKeyVar, amountVar,paymentDateVarDateFormat, isPaymentAuthorizedVar);
    }
    
   /**
     * Returns responses of continuation object    
     * Callback Method to parse the One Time Credit Card Payment (EditMember) service response
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
    */
    @AuraEnabled
    public static object parseOneTimePaymentResponse(List < string > labels, object State)
    {
       return PharmacyFinancial_LS_HUM.parseOneTimePaymentResponse(labels, State);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the payment service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled(cacheable=true)
    public static string parseGetAdjPayResponse(list<string> Labels, object state)
    {        
        return JSON.serialize(PharmacyFinancial_LS_HUM.parseGetAdjPayResponse(Labels, state));
    }
    
    /**
     * Returns PCI url as a string
     * <p>
     * Method to get the PCI url to be used for getting the credit card token when creating a new card
     *
     * @return String
     */
    @AuraEnabled(cacheable=true)
    public static string getPCIURL(){
        String sPCIEndPoint = HUM_Webservice_Callout__c.getValues('PCI').End_Point_URL__c +HUM_Webservice_Callout__c.getValues('PCI').Service_Name__c;
        return sPCIEndPoint;
    }
    
    @AuraEnabled
    public static object processEditMember(List<string> labels, Object state){
        return PharmacyFinancial_LS_HUM.retrieveEditMember(labels, State);
    }
    
       /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the getMemberEdit details continuation requests
     *
     * @return continuation object
     */
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation invokeGetEditMemService(string enterprise, String sCreditCardKey, String exMonth, String exYear, String sActive, String sAutoCharge, String sFSA, String sFirstName, String sMiddleName, String sLastName, String sTokenKey, String sCreditType, String isInsert, string networkID, String sRecordId)
    {
        return PharmacyFinancial_LS_HUM.prepareEditGetMember(enterprise, sCreditCardKey, exMonth, exYear, sActive, sAutoCharge, sFSA, sFirstName, sMiddleName, sLastName, sTokenKey, sCreditType, isInsert, networkID, sRecordId);
    }
}