/****************************************************************************
Apex Class Name  : HUMSearchControllerTest
Version          : 1.0 
Created Date     : AUGUST 08 2014 
Function         : Test class to HUMSearchController Apex class
Modification Log :
 * Developer Name        Code Reveiw                  Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Ninad Patil           17172                       08/06/2014             Original Version   
 * Anil P                17474                       08/21/2014             Updated code to RUNAS a user and also added record type for accounts
 * Chaitanya Kumar       17824                       09/04/2014             Created testConsumerTaxIDProvider
 * Shreya Choodamani     20358                       01/15/2015             updated the code to handle code changes for REQ -72781(bPopUp Variable)
 * Shreya Choodamani     20566                       01/28/2015             Added code to handle defect fix for 169904
 * Sagar Tapadia         20604                       01/29/2015             Changed Interacting With Type from Agency to Agent
 * Praveen Kumar Parimi  25224                       07/31/2015             Added the Custom Setting for getting the Data of Interaction__C Encryption Utility.
 * Praveen Kumar Parimi  27530                       12/09/2015             Removed the call for caller_name encryption. 
 * Prudhvi Pamarthi      105627                      06/27/2017             Commented member, group, provider ,agent/broker search related calls
 * Santhi Mandava                                    03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
 * Moshitha Gunasekaran                              04/20/2021             US#2163329 - Cleanup old version reference of HumInteractionLogComponentController for checkmarx issues
 **********************************************************************************************************************************/
@isTest
private class HUMSearchControllerTest 
{
    @testSetup static void Datasetup() 
    {
     //Create Interaction__c List and insert them
      list<Interaction__c> lstinteraction  = new list<Interaction__c>();      
      lstinteraction.add(new Interaction__c(caller_Name__c = 'TestCaller', Interaction_Origin__c = 'Email'));         
      insert lstinteraction;
      
    }
    /*
     * Method Name   :    testSearchInteractionMember
     * Description   :    This method tests Search functionality/softphone routing for Member type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testSearchInteractionMember() 
    {         

        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        HUMSearchLimit__c searchlimittest = new HUMSearchLimit__c(Name = 'Member_SearchLimit');           
        searchlimittest.Search_Limit__c = 123;
        insert searchlimittest;

        System.runas(u) 
        {               
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.LastName = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();
            
            //HUMMemberSearchController oHUMMem = new HUMMemberSearchController();   

            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchController oHUMSearch = new HUMSearchController();
            oHUMSearch.switchTab();
            oHUMSearch.getThis();  

            //Search results retunrns record withing salesforce.
            oHUMSearch = new HUMSearchController();
            oHUMSearch.closePopup();
            
            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');

            //Softphone Routing
            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName = ';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jimmy&lastName=Thong&callerType = ';
            oHUMSearch.transferJsonObject+= 'InBoundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=M&iAboutID=1234&firstName=Jeremy&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=A';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Member');

            Test.stopTest();
        }  
    }

    /*
     * Method Name   :    testSearchInteractionProvider
     * Description   :    This method tests Search functionality/softphone routing for Provider type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testSearchInteractionProvider() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();      
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            //HUMProviderSearchController oHUMProv = new HUMProviderSearchController();

            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchController oHUMSearch = new HUMSearchController(); 

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=P&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Provider');

            Test.stopTest();

        }  
    }

    /*
     * Method Name   :    testSearchInteractionAgency
     * Description   :    This method tests Search functionality/softphone routing for Agency type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testSearchInteractionAgency() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
                
        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();        
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            //HUMAgencySearchController oHUMAgent = new HUMAgencySearchController();

            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchController oHUMSearch = new HUMSearchController(); 

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=A&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=12343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Agent');

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=A&iAboutID=123456789&firstName=Jimmy&lastName=&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=234324211&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.transferJsonObject = EncodingUtil.base64Encode(blob.valueof(oHUMSearch.transferJsonObject));
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
            //system.assert(oHUMSearch.interactionLogController.oFetchInteraction.Interacting_with_type__c == 'Agent');


            Test.stopTest();
        } 
    }

    /*
     * Method Name   :    testSearchInteractionGroup
     * Description   :    This method tests Search functionality/softphone routing for Group type of accounts.   
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testSearchInteractionGroup() 
    {
        User u = HUMTestDataHelperTest.createTestUser('TestUser1','System Administrator'); 
        insert u;
        
        System.runas(u) 
        { 
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Account Test';
            oAcc.Account_External_ID__c = '12345';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            HUMTestDataHelperTest.getHUMConstantsData();

            //HUMGroupSearchController oHUMGroup = new HUMGroupSearchController();
            Test.startTest();

            //Created Custom setting records for test context. 
            HUMTestDataHelperTest.getHUMConstantsData();  

            HUMSearchController oHUMSearch = new HUMSearchController(); 

            oHUMSearch.transferJsonObject = 'uuID=070714000093919000040X8886667730&iAboutFirstName=';
            oHUMSearch.transferJsonObject+= 'Daisy&iAboutLastName=Duck&iAboutType=G&iAboutID=1234&firstName=James&lastName=Thong&callerType=';
            oHUMSearch.transferJsonObject+= 'OutboundCall&iWithID=2343242&iWithFirstName=Daisy&iWithLastName=Duck&iWithType=G';
            oHUMSearch.softPhoneRoutingSearch();

            //Assert whether softphone details are populated in the interaction.
        //    system.assert(oHUMSearch.interactionLogController.oFetchInteraction.caller_name__c == 'James Thong');

            Test.stopTest();
        }          
    }
}