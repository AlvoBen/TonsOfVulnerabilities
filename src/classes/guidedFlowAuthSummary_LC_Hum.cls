/*******************************************************************************************************************************
Apex Class Name : guidedFlowAuthSummary_LC_Hum
Version         : 1.0
Created On      : 05/11/2022
Test Class      : guidedFlowAuthSummary_LC_Hum
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Pinky Vijur                                               05/11/2022          Original version
* Aishwarya Pawar                                          10/31/2022          REQ-  3819554
* Anusha C                                                  07/17/2023          US-4812126 Critical- Lightning - Generic             
*********************************************************************************************************************************/
public with sharing class guidedFlowAuthSummary_LC_Hum {


    @AuraEnabled
   public static string getSID(string authMessage){
       String templateCode;  
       List<Template__c> authTemplate = [select id,name from Template__c where Description__c = 'Authorization/Referral Requirements' WITH USER_MODE];
       templateCode = authTemplate[0].name;
       Map<String, Object> myMap = new Map<String, Object>();
       myMap.put('SID', '');
       myMap.put('AuthMessage',authMessage);
       myMap.put('TN',templateCode);

   
       Flow.Interview.IsAuthGuidedProcessFlow myflow =  new Flow.Interview.IsAuthGuidedProcessFlow(myMap);
         myflow.start();

          if(myflow == null) 
         {
           return null;
         }
         else
         {                      
             return string.valueOf(myflow.SID);
             
         }
          
   }
   
   @AuraEnabled(continuation=true cacheable=true)
   public static string searchDetails(String textToBeSearch){
       List<Humana_Codes_Desc__c> listOfHumanaCodesDesc = new List<Humana_Codes_Desc__c>();
       HumanaCodes_D_Hum humanaCodesDHumInstance = new HumanaCodes_D_Hum();
       List<List<sObject>> searchInQuery = new List<List<SObject>>();
       List<string> error = new List<String>();
       string sSearchDetailsJSONreturn = '';
       List<HumanaCodes_DTO_HUM> listOfSearchWrap = new List<HumanaCodes_DTO_HUM>();
       try {
           searchInQuery = humanaCodesDHumInstance.soslForLimitedGBOCodes(textToBeSearch);
           listOfHumanaCodesDesc = (List<Humana_Codes_Desc__c>)searchInQuery[0];
           if(listOfHumanaCodesDesc.size() > 100){
                listOfHumanaCodesDesc.remove(100);
           }else if(listOfHumanaCodesDesc.size() == 0){
          sSearchDetailsJSONreturn  = JSON.serializePretty(System.Label.CodeSetNoResultMsg);

               return sSearchDetailsJSONreturn ;
           }
               Set<String> setOfUniqueCode = new Set<String>();
               for(Humana_Codes_Desc__c oCodes: listOfHumanaCodesDesc){
                   HumanaCodes_DTO_HUM oSearchWrap = new HumanaCodes_DTO_HUM();
                   oSearchWrap.Id = oCodes.Id;
                   oSearchWrap.sCode = oCodes.Name;
                   oSearchWrap.sCodeSet = oCodes.Code_Set__c;
                   oSearchWrap.sDescription = oCodes.Description__c;
                   oSearchWrap.sStatus = oCodes.Status__c;
                   setOfUniqueCode.add(oCodes.Code_Set__c);
                   listOfSearchWrap.add(oSearchWrap);
               }
               sSearchDetailsJSONreturn = JSON.serializePretty(listOfSearchWrap);
               sSearchDetailsJSONreturn = '{"data":'+sSearchDetailsJSONreturn+'}';
               return sSearchDetailsJSONreturn;
          
       } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'guidedFlowAuthSummary_LC_Hum', 'searchDetails');
           throw new AuraHandledException(e.getMessage());
       }
   }
   
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMember details continuation requests
    *
    * @return continuation object
    */
   @AuraEnabled(continuation=true)
     public static Continuation invokeAuthRefRequest(String sRecordId,string jObj){
             AuthRefRequest_DTO_HUM creteAuthRequest = (AuthRefRequest_DTO_HUM)system.JSON.deserialize(jObj,AuthRefRequest_DTO_HUM.class);
             return guidedFlowAuthSummary_LS_Hum.invokeAuthRefRequest(sRecordId,creteAuthRequest );              
       } 
       
   /**
   * Returns responses of continuation object
   * <p>
   * Callback Method to parse the service response
   *
   * @return Object
   */
   @AuraEnabled
        public static object parseAuthRefResponse(List < string > labels, object State){
        
           return guidedFlowAuthSummary_LS_Hum.parseAuthRefResponse(labels, State);
        
        }
}