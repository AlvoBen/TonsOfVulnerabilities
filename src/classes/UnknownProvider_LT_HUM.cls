/*******************************************************************************************************************************
Apex Class Name :  UnknownProvider_LT_HUM
Version: 1.0
Created On      : 11//2020
Function        : Test class for UnknownProvider_LC_HUM 
Modification Log
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Saikumar Boga										  06/14/2021			       Intial Version
* Saikumar Boga										  07/16/2021			       Code Coveregae for Unknwon Agent/Broker
*********************************************************************************************************************************************************************************************/

@isTest
Public Class UnknownProvider_LT_HUM {
    
      
    
    /**
* Method to test  creation of account And ConsumerId Object
* 
* @param    None
* @return   Void 
*/
    
   Static testMethod void prepareTestDataCreate()
    {                   
              
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser,null,'?Value not matched');
        insert New HumProviderSpecialtyMapping__c(Name='Allergy / Immunology',EpssSpec_Code__c='ALG');
        RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Unknown Provider' and sObjectType='Account'];
        RecordType agentAccountRecordType = [Select Id from RecordType where Name = 'Unknown Agent/Broker' and sObjectType='Account'];
        system.assert(agentAccountRecordType !=null);
        system.assert(PersonAccountRecordType !=null);
        list<account> lstaccount = New list<account>();
        Account oacc = HUMTestDataHelperTest.getAccount();
        oacc.recordtypeid = PersonAccountRecordType.id;
        oacc.name= 'duck';
        oacc.NPI_ID__c='0123456789';
        oacc.Individual_First_Name__c='testname';
        oacc.Individual_Last_Name__c='firstname';
        oacc.Provider_Classification__c='I';
        oacc.billingstate='Arizona';
        oacc.billingpostalcode='12345';
        insert oacc;
        Account oacc1 = HUMTestDataHelperTest.getAccount();
        oacc1.recordtypeid = agentAccountRecordType.id;
        oacc1.name= 'Apple';
        oacc1.Agent_ID__c='01234567';
        oacc1.Agent_Type__c='Agent';
        oacc1.billingstate='Arizona';
        oacc1.billingpostalcode='12346';
        insert oacc1;
        oacc1.Account_External_ID__c =  HUMConstants__c.getInstance('UNKNOWN_AGENT_EXTID').Stringvalue__c + oacc1.id;
        update oacc1;
        
        
        String sExternalID = 'UKP|00|SFDC|';
        String sTaxID = 'Taxid';
        String sPipeCharacter ='|';
        ConsumerID__c consmer = New ConsumerID__c();
        consmer.Consumer_ID__c = '777777777';
        consmer.ID_Type__c = Label.HumServiceTaxIDtag;
        consmer.ETL_Record_Deleted__c = false;
        consmer.Account__c = oacc1.id;            
        insert consmer;
        consmer.Consumer_External_Id__c = sExternalID + sTaxID + sPipeCharacter + consmer.id;
        update consmer;
        
        system.runAs(oUser )   {
            Test.starttest();
            UnknownProvider_LC_HUM.createAccountConsumerRecord(oacc);
            UnknownProvider_LC_HUM.createAccountConsumerRecord(oacc1);
            Test.stoptest();
        }
        
    }

}