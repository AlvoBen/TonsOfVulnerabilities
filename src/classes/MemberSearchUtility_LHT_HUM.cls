/*******************************************************************************************************************************
Apex Class Name : MemberSearchUtility_LHT_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : This is the Test class for MemberSearchUtility_LH_HUM


Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     09/25/2020                Original Version
*    2.0           Kajal Namdev                                     04/06/2021                Added a few lines of code to cover else if block
*******************************************************************************************************************************************************************/

@isTest
private class MemberSearchUtility_LHT_HUM {
    @testSetup static void setup() 
    {    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser; 
        System.runas(oUser) 
        {
            Integration_Mapping__c intMap = new Integration_Mapping__c();
            intMap.Mapping__c = 'Mapping:test;HUM';
            insert intMap;
            system.assertEquals(intMap.Mapping__c == 'Mapping:test;HUM',true);
            
            HUMMember_Search_State_Mapping__c cstsetting = new HUMMember_Search_State_Mapping__c(Name = 'test', State__c = 'California', State_Code__c = 'CA');
            insert cstsetting;
            system.assertEquals(cstsetting.Name == 'test',true);
            
            Account oacc = HUMTestDataHelperTest.getAccount();
            oacc.Name = 'Test Account';
            insert oacc;
            system.assertEquals(oacc.Name == 'Test Account',true);
            
            Interaction__c objInterac = new Interaction__c();
            objInterac.Caller_Name__c = 'Test Caller';
            objInterac.Interaction_Origin__c = 'Inbound Call';
            objInterac.UUID__c = '12345';
            insert objInterac;
            system.assertEquals(objInterac.Interaction_Origin__c == 'Inbound Call',true);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Benefits';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Benefits',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Rewards';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Rewards',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Classification_ID__c = classificationRecDec.id;
            caseObj.Intent_ID__c = intentRec.id;
            caseObj.Interaction_ID__c = objInterac.Id;
            insert caseObj;
            system.assertEquals(caseObj.Interacting_With_Name__c == 'Test',true);
            
            Integration__c inte = new Integration__c();
            inte.Name = String.escapeSingleQuotes('Soft Phone Routing');
            insert inte;
            
            List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Group' ,Account_RecordType__c = 'Group' , Case_RecordType__c = 'Group Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes1);
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes2 = new HUMAccountCaseRecordTypes__c(Name = 'Agent/Broker' ,Account_RecordType__c = 'Agent/Broker' , Case_RecordType__c = 'Agent/Broker Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes2);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes3 = new HUMAccountCaseRecordTypes__c(Name = 'Provider' ,Account_RecordType__c = 'Provider' , Case_RecordType__c = 'Provider Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes3);        
            HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes4 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
            HUMAccountCaseRecordTypesList .add(HUMAccountCaseRecordTypes4);        
            insert HUMAccountCaseRecordTypesList;
            system.assertEquals(HUMAccountCaseRecordTypesList[0].Name == 'Group',true);
            
            HUMConstants__c cstHumConst = new HUMConstants__c(Name ='testcustomsetting',StringValue__c = 'test',IntegerValue__c = 123);
            insert cstHumConst;
            system.assertEquals(cstHumConst.Name == 'testcustomsetting',true);
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'Test Account';
            insert oaccount;
            
            HUMTestDataHelperTest.getHUMConstantsData();
            Guidance_Alerts__c guidanceAlert = new Guidance_Alerts__c(Name = 'test', Alert_Reason__c = 'Not Delivered', Guidance_Alerts__c = 'Caller in Hurry');
            insert guidanceAlert;
            system.assertEquals(guidanceAlert.Name == 'test',true);
            
            List<HUMCipherCloudSettings__c> lstApplicationName = new List<HUMCipherCloudSettings__c> ();
            lstApplicationName.add(new HUMCipherCloudSettings__c(Name = 'APPLICATION_NAME',  Value__c = 'CRMR1'));
            insert lstApplicationName;
            system.assertEquals(lstApplicationName[0].Name == 'APPLICATION_NAME',true);
            
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.Name='Test Field';
            acc.Account_External_Id__c='1231';
            acc.RecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();
            insert acc;
            system.assert(acc.Name == 'Test Field'); 
            
            PermissionSet permissionSet = new PermissionSet(Label = 'mockPs', Name = 'mockPs');
            insert permissionSet;
            
            PermissionSetAssignment permissionSetAssignment = new PermissionSetAssignment(
                AssigneeId = oUser.Id,
                PermissionSetId = permissionSet.Id);
            insert permissionSetAssignment;
            
        }
    }
       /*
    * Method Name : testgetQueryMapSoftphone
    * Description : This method is used to validate combinational search
    * Return type : void
    * Paramater   : void
    */
    static testMethod void testgetQueryMapSoftphone() 
    {   
        Map<String,String> mapOfString= new Map<String,String>();
        mapOfString.put('FirstName', 'test');
        // to cover else condition 
        Map<String,String> mapOfString2= new Map<String,String>();
        mapOfString2.put('TestName', 'test');
        
        Test.startTest();
        MemberSearchUtility_LH_HUM.getQueryMapSoftphone(mapOfString);
        // to cover else condition 
         MemberSearchUtility_LH_HUM.getQueryMapSoftphone(mapOfString2);
        Test.stopTest();
    }
    
}