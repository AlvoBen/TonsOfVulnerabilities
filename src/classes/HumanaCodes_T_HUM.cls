/******************************************************************************************************************************
Apex Class Name  : HumanaCodes_T_HUM 
Version          : 1.0 
Created Date     : 29th March 2019
Function         : Test Class for HumanaCodes_C_Hum                    
Test Class       : 
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Satyam Malviya                            03/29/2019            Original Version     
* Yogesh Gupta							   	  05/29/2018			Defect - 419080  check for double quotes and returns soql results and bypass results 
********************************************************************************************************************************/

@isTest
private class  HumanaCodes_T_HUM {
    @testSetup  static void setUpTestData() {
        List<Humana_Codes_Desc__c> listOfHumanaCodesDesc = new List<Humana_Codes_Desc__c>();  
        Humana_Codes_Desc__c humCode = new Humana_Codes_Desc__c();
        humCode.Code_Set__c='wewewe';
        humCode.Description__c='Add to pharmacy lifetime maximum';
        humCode.Name='C-2127';
        humCode.Status__c='Active';
        humCode.Code_External_ID__c = 'ExternalID test';
        insert humCode;
    }
    
    static testMethod void testDTOResponse() {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
        insert objUser; 
        System.runAs(objUser) {
            Test.startTest();
            Humana_Codes_Desc__c humCode = new Humana_Codes_Desc__c();
            humCode.Code_Set__c='wewewe';
            humCode.Description__c='Add to pharmacy lifetime maximum';
            humCode.Name='C2127';
            humCode.Status__c='Active';
            humCode.Code_External_ID__c = 'ExternalID test2';
            insert humCode;
            
            string[] SearchStrings = new string[1];
            SearchStrings[0] = humCode.Id;
            test.setFixedSearchResults(SearchStrings);
            
            HumanaCodes_C_Hum humanaCode = new HumanaCodes_C_Hum();
            humanaCode.textToBeSearch='C2127';
            PageReference pRef = humanaCode.searchDetailsJSON();
            Test.stopTest();  
            System.assertEquals(pRef!=null, false);        
        }
    }
   
    Static testmethod void testinitialiseValues() {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
        insert objUser; 
        System.runAs(objUser) {
            Test.startTest();
            Humana_Codes_Desc__c humCode = New Humana_Codes_Desc__c();
            humCode.Code_Set__c='wewewe';
            humCode.Description__c='Add to pharmacy lifetime maximum';
            humCode.Name='C2127';
            humCode.Status__c='Active';
            humCode.Code_External_ID__c = 'ExternalID test1';
            insert humCode;
            
            HumanaCodes_C_Hum humanaCode = New HumanaCodes_C_Hum();
            humanaCode.textToBeSearch='C2127';
            humanaCode.searchCodeOnly = false;
            humanaCode.searchDetailsJSON();
            humanaCode.reset(); 
            Test.stopTest();   
            System.assertEquals(humanaCode.searchCodeOnly, false);               
        }
    }
    
    Static testmethod void TexttoSearchisBlank() {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
        insert objUser; 
        System.runAs(objUser){
            Test.startTest();
            Humana_Codes_Desc__c humCode = New Humana_Codes_Desc__c();
            humCode.Code_Set__c='wewewe';
            humCode.Description__c='Add to pharmacy lifetime maximum';
            humCode.Name='C2127';
            humCode.Status__c='Active';
            humCode.Code_External_ID__c = 'ExternalID test1';
            insert humCode;
            
            HumanaCodes_C_Hum humanaCode = New HumanaCodes_C_Hum();
            humanaCode.textToBeSearch='';
            PageReference pRef = humanaCode.searchDetailsJSON();
            Test.stopTest();  
            System.assertEquals(pRef!=null, false);                          
        }
    }  

	static testmethod void texttoSearchinDoubleQuotes() {
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist'); 
        insert objUser; 
        System.runAs(objUser){
            Test.startTest();
            Humana_Codes_Desc__c humCode = New Humana_Codes_Desc__c();
            humCode.Code_Set__c='wewewe';
            humCode.Description__c='Add to pharmacy lifetime maximum';
            humCode.Name='C2127';
            humCode.Status__c='Active';
            humCode.Code_External_ID__c = 'ExternalID test1';
            
            insert humCode;
            
            HumanaCodes_C_Hum humanaCode = New HumanaCodes_C_Hum();
            humanaCode.textToBeSearch='"c"';
            humanaCode.searchCodeOnly = true;
            humanaCode.searchDetailsJSON();
            Test.stopTest();  
            System.assertEquals(humanaCode.searchCodeOnly, true);                       
        }
    }	
}