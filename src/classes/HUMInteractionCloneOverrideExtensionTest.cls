/****************************************************************************
Apex Class Name  : HUMInteractionCloneOverrideExtensionTest
Version          : 1.0
Created On       : 01/13/2015
Function         : Test class to HUMInteractionCloneOverrideExtension Apex class

Modification Log :
*    Developer                 CodeReview            Date                 Description
* -----------------------------------------------------------------------------------------------------------------------------
*    Gargey Sharma                20337            01/13/2014             Original Version
*    Ranjeeth Nagishetty                           06/23/2014             updated test class
**********************************************************************************************************************************/

@isTest
private class HUMInteractionCloneOverrideExtensionTest
{
    /*
    * Method Name : cloneInteraction
    * Description : Test method to clone an Interaction
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod void cloneInteraction() 
    {
        //Create test user
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Test';
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {   
            //Insert Account Record
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'AccTest';
            oAcc.LastName = 'AccTest';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.id != null);
            
                   
            List<HUMEncryptedFields__c>lstCS4 = new List<HUMEncryptedFields__c>();
            lstCS4.add(new HUMEncryptedFields__c(Name='CALLER_NAME__C.Interaction__C',ObjectName__c='Interaction__c', FieldName__c='CALLER_NAME__C', EncryptionScheme__c = 'Stateless_AFPE_for_Alphabetic_Filtering'));
            insert lstCS4;
            List<HUMConstants__c> lstConstantsRecords = Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
            System.assert(lstConstantsRecords.size() > 0 );
                       
            //Insert Interaction Record
            Interaction__c oInt = HUMTestDataHelperTest.getInteraction();
            insert oInt;
            System.assert(oInt.id != null);

            //Insert Custom Setting
            List<HUMConstants__c> lstCstSettingInt = new List<HUMConstants__c> ();        
            lstCstSettingInt.add(new HUMConstants__c(Name = 'INTERACTION_URL_ID',StringValue__c = 'id'));
            insert lstCstSettingInt;
              
            //Insert Interaction Members
            Interaction_Member__c oIntMemOne = HUMTestDataHelperTest.getInteractionMember();
            oIntMemOne.Interacting_About__c = oAcc.Id;
            oIntMemOne.Interaction__c = oInt.Id;
            oIntMemOne.Interacting_About_Type__c = 'Member';
            insert oIntMemOne;
            System.assert(oIntMemOne.id != null);
            
            Interaction_Member__c oIntMemTwo = HUMTestDataHelperTest.getInteractionMember();
            oIntMemTwo.Interacting_About__c = oAcc.Id;
            oIntMemTwo.Interaction__c = oInt.Id;
            oIntMemTwo.Interacting_About_Type__c = 'Member';
            insert oIntMemTwo;
            System.assert(oIntMemTwo.id != null);

            
            Test.startTest();
                ApexPages.currentPage().getParameters().put('id', oInt.id);
                HUMInteractionCloneOverrideExtension cloneInt = new HUMInteractionCloneOverrideExtension(new ApexPages.StandardController(oInt));
                cloneInt.queryInteraction();
                cloneInt.saveIntRecord();
                List<HUMIntMemberCloneWrapperHelper> lstIntWrapp = new List<HUMIntMemberCloneWrapperHelper>();
                HUMIntMemberCloneWrapperHelper intMemOne  = new HUMIntMemberCloneWrapperHelper(oIntMemOne,true);
                lstIntWrapp.add(intMemOne);
                HUMIntMemberCloneWrapperHelper intMemTwo  = new HUMIntMemberCloneWrapperHelper(oIntMemTwo,false);
                lstIntWrapp.add(intMemTwo);               
                cloneInt.lstInqAbtWrapp = lstIntWrapp;
                cloneInt.queryInteraction();
                cloneInt.saveIntRecord();
                cloneInt.lstInqAbtWrapp = null;
                cloneInt.queryInteraction();
                cloneInt.saveIntRecord();
            Test.stopTest();     
        }
    }
    
    static testMethod void cloneInteractionFail() 
    {
        //Create test user
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Test';
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {   
            //Insert Account Record
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'AccTest';
            oAcc.LastName = 'AccTest';
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.id != null);
                       
            //Insert Interaction Record
            Interaction__c oInt = HUMTestDataHelperTest.getInteraction();
            insert oInt;
            System.assert(oInt.id != null);

            //Insert Custom Setting
            List<HUMConstants__c> lstCstSettingInt = new List<HUMConstants__c> ();        
            lstCstSettingInt.add(new HUMConstants__c(Name = 'INTERACTION_URL_ID',StringValue__c = 'id'));
            insert lstCstSettingInt;
              
            //Insert Interaction Members
            Interaction_Member__c oIntMemOne = HUMTestDataHelperTest.getInteractionMember();
            oIntMemOne.Interacting_About__c = oAcc.Id;
            oIntMemOne.Interaction__c = oInt.Id;
            oIntMemOne.Interacting_About_Type__c = 'Member';
            insert oIntMemOne;
            System.assert(oIntMemOne.id != null);
            
            Interaction_Member__c oIntMemTwo = HUMTestDataHelperTest.getInteractionMember();
            oIntMemTwo.Interacting_About__c = oAcc.Id;
            oIntMemTwo.Interaction__c = oInt.Id;
            oIntMemTwo.Interacting_About_Type__c = 'Member';
            insert oIntMemTwo;
            System.assert(oIntMemTwo.id != null);

            
            Test.startTest();
                ApexPages.currentPage().getParameters().put('id', oInt.Name);
                HUMInteractionCloneOverrideExtension cloneInt = new HUMInteractionCloneOverrideExtension(new ApexPages.StandardController(oInt));
                cloneInt.queryInteraction();
                cloneInt.saveIntRecord();
                List<HUMIntMemberCloneWrapperHelper> lstIntWrapp = new List<HUMIntMemberCloneWrapperHelper>();
                HUMIntMemberCloneWrapperHelper intMemOne  = new HUMIntMemberCloneWrapperHelper(oIntMemOne,true);
                lstIntWrapp.add(intMemOne);
                HUMIntMemberCloneWrapperHelper intMemTwo  = new HUMIntMemberCloneWrapperHelper(oIntMemTwo,false);
                lstIntWrapp.add(intMemTwo);               
                cloneInt.lstInqAbtWrapp = lstIntWrapp;
                cloneInt.saveIntRecord();
            Test.stopTest();     
        }
    }
}