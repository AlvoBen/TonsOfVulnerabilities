/******************************************************************************************************************************
Apex Class Name :   HumanaCodes_D_Hum
Version: 1.0 
Created Date     : 29th March 2019
Function         : Generic class for Humana Codes object And return SOSL                   
Test Class        
Modification Log : 
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Satyam Malviya                            03/29/2019            Original Version
 *  Yogesh Gupta							  05/29/2019		    check for double quotes and returns soql results 
 * Tharun Madishetti                          07/19/2022            US-3491274:T1PRJ0533784 - MF 10 - INC1809196 Restrict code selection to one service code on the GBO Authorization and Referral Requirements template
********************************************************************************************************************************/

Public with sharing Class HumanaCodes_D_Hum {

	/**
     * Method to get Humana_Codes_Desc__c records from database using SOSL And returning Name fields.
     * 
     * @param    String
     * @return   List<List<SObject>>
     */
	Public List<List<SObject>> soslForCodesOnly(String textToBeSearch) {
		
		//Check for double quotes And returns soql results if code only true value
	    String checkForQuotes = '\"';
        If(textToBeSearch.startsWith(checkForQuotes) && textToBeSearch.endsWith(checkForQuotes)) {
			textToBeSearch = textToBeSearch.remove(checkForQuotes);
			List<Humana_Codes_Desc__c> listOfCodes = [Select Name, Code_Set__c, Description__c, Status__c FROM Humana_Codes_Desc__c WHERE NAME =: textToBeSearch ORDER BY NAME, Code_Set__c DESC LIMIT :HUMConstants.maxCountOfSOSL];
			
			List<List<SObject>> listOfSobject = new List<List<SObject>>();
			listOfSobject.add(listOfCodes);
			return listOfSobject;
        } 
        else{
			Return [Find :textToBeSearch IN Name fields returning Humana_Codes_Desc__c(Name, Code_Set__c, Description__c, Status__c ORDER BY NAME, Code_Set__c DESC) LIMIT :HUMConstants.maxCountOfSOSL];
		}
	}
	
	/**
     * Method to get Humana_Codes_Desc__c records from database using SOSL And returning all fields.
     * 
     * @param    String
     * @return   List<List<SObject>>
     */
	Public List<List<SObject>> soslForAllFields(String textToBeSearch) { 
		
		Return [Find :textToBeSearch IN All fields returning Humana_Codes_Desc__c(Name, Code_Set__c, Description__c, Status__c 
                     ORDER BY NAME, Code_Set__c DESC) LIMIT :HUMConstants.maxCountOfSOSL];
	} 
	
	Public List<List<SObject>> soslForLimitedGBOCodes(String textToBeSearch) { 
        
		string search = textToBeSearch + '*' ;
		 Return [Find :search IN All fields returning Humana_Codes_Desc__c(Name, Code_Set__c, Description__c, Status__c where (Code_Set__c = 'CPT' OR Code_Set__c = 'HCPCS') AND Status__c IN ('A','C')
					  ORDER BY NAME, Code_Set__c DESC) LIMIT :HUMConstants.maxCountOfSOSL];
	 } 
}