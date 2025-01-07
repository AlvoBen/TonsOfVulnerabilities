/**************************************************************************** 
Apex Class Name  : SearchCodeSet_LC_HUM 
Version          : 1.0  
Function         : This class is used to Retrive HealthCareProcedure records from Search Code Components.

 * Developer Name           Code Review                  Date                       Description
 *---------------------------------------------------------------------------------------------------------------------
 * Aishwarya Pawar                                       01/05/2023                 REQ-4080893
********************************************************************************************/
public with sharing class SearchCodeSet_LC_HUM {
    public SearchCodeSet_LC_HUM() {

    }
    // Method to get HealthCareProcedure records from database using SOSL And returning Name fields. 
    
    @AuraEnabled (cacheable=true)
    public static String searchDetailsForCodesOnly(String textTobeSearched){
        try {
            String checkForQuotes ='\"';
            List<List<SObject>> listOfSobject = new List<List<SObject>>();
            List<HealthCareProcedure> listOfCodes =new  List<HealthCareProcedure>();
            if(textTobeSearched.startsWith(checkForQuotes) && textTobeSearched.endsWith(checkForQuotes)){
                textTobeSearched = textTobeSearched.remove(checkForQuotes);
                listOfCodes = [Select Name, Code, CodeDescription, Status__c FROM HealthCareProcedure WHERE NAME =: textTobeSearched ORDER BY NAME ASC LIMIT :HUMConstants.maxCountOfSOSL];
               
                
            }else{
                listOfSobject= [Find :textTobeSearched IN Name fields returning HealthCareProcedure(Name, Code, CodeDescription, Status__c ORDER BY NAME ASC) LIMIT :HUMConstants.maxCountOfSOSL];
                listOfCodes =(List<HealthCareProcedure>)listOfSobject[0];
            }
            return JSON.serializePretty(listOfCodes);
            
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
       }
    }

	//Method to get HealthCareProcedure records from database using SOSL And returning all fields.
    @AuraEnabled (cacheable=true)
     public static String searchDetailsForAll(String textTobeSearched){
        try{
        List<List<SObject>> listOfSobject = new List<List<SObject>>();
        List<HealthCareProcedure> listOfCodes =new  List<HealthCareProcedure>();
        listOfSobject= [Find :textTobeSearched IN All fields returning HealthCareProcedure(Name, Code, CodeDescription, Status__c 
        ORDER BY NAME ASC) LIMIT :HUMConstants.maxCountOfSOSL];
        listOfCodes =(List<HealthCareProcedure>)listOfSobject[0];
        return JSON.serializePretty(listOfCodes);
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
    }
    }
}