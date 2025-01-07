/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearch_T_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : Test Class for CoachMemberSearch_C_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0               Jasmeen Shangari                                 03/08/2021                 Original Version
********************************************************************************************************************************/
@isTest
private class CoachMemberSearch_T_HUM
{ 
    /*
    * Method Name : prepareTestData
    * Description : Create common test Data
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('CoachUser1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test CoachUser1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='MembersearchConstantZero',StringValue__c='0',IntegerValue__c=0));
            lstConstants.add(new HUMConstants__c(Name='HUMServiceTimeOut',StringValue__c='12',IntegerValue__c=12));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 2);
            
            HUMSearchLimit__c oSearchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit', Search_Limit__c=101);
            insert oSearchLimit;
            System.assertNotEquals(oSearchLimit, NULL, 'Value not matched');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.Birthdate__c = '01/01/1984';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Home_Office_Account__c = false;
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(HUMConstants.HumMemberRecordType);
            
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Group_Name__c = oAcc.id;
            oPolicy1.Policy_Group_Number__c='11111';
            insert oPolicy1;
            System.AssertNotEquals(oPolicy1, NULL);
            
            List<MemberPlan> lstMemPlan = new List<MemberPlan>();
            MemberPlan oMemPlan = new MemberPlan(Name='MemberPlan',MemberId = oAcc.Id, Policy__c =oPolicy1.id, Member_Dependent_Code__c = '01' );
            lstMemPlan.add(oMemPlan);
            insert lstMemPlan;
            
            Policy_Member__c oPolicyMember1 = new Policy_Member__c();
            oPolicyMember1.Member__c = oAcc.ID;
            oPolicyMember1.Policy__c = oPolicy1.ID;
            oPolicyMember1.Name = '666';
            insert oPolicyMember1;
            System.AssertNotEquals(oPolicyMember1, NULL);
            
            Member_ID__c oMember = new Member_ID__c();
            oMember.Policy_Member__c = oPolicyMember1.ID;
            oMember.Name = '123456';
            oMember.Member_Dependent_Code__c ='01';
            oMember.Policy__c = oPolicy1.ID;
            oMember.Member_Card_External_ID__c = 'M123';
            insert oMember;
            System.AssertNotEquals(oMember, NULL);
            
            Member_ID__c oMember1 = new Member_ID__c();
            oMember1.Policy_Member__c = oPolicyMember1.ID;
            oMember1.Name = '12@345';
            oMember1.Member_Dependent_Code__c ='06';
            oMember1.Policy__c = oPolicy1.ID;
            oMember1.Member_Card_External_ID__c = 'M1283';
            insert oMember1;
            System.AssertNotEquals(oMember1, NULL);
            
            ConsumerID__c oConsumerId = new ConsumerID__c();
            oConsumerId.Account__c = oAcc.ID;
            oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
            oConsumerId.Consumer_ID__c = '123456';
            oConsumerId.ID_Type__c = 'HumanaId';
            insert oConsumerId;
            System.AssertNotEquals(oConsumerId, NULL);
            
            Map<String,String> stateLst = SearchUtilty_H_HUM.getStateValues();
            Map<ID, HC_LWC_Permission_Details__mdt> mapPermDetails = new Map<ID, HC_LWC_Permission_Details__mdt>();
            PermissionSetAssignment psa = new PermissionSetAssignment();
        }
    }
    
    /*
    * Method Name : prepareTestSearchResults()
    * Description : Test Search Criteria
    * Return type : void
    * Parameter   : NA
    */
    static testmethod void prepareTestSearchResults()
    {
        User SUser = [Select id,UserName from User where LastName = 'test CoachUser1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'coachuser1coachuser1@humcrmstest.com');
        List<Account> lstAcc = new List<Account>();
        Account oAcc1 = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c,PersonBirthDate from Account where Group_Number__c = '987654321' limit 1];
        List<String> recTypesLst = new List<String>();
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<CoachMemberSearch_C_HUM> lstHumMemberSearch = new List<CoachMemberSearch_C_HUM>();
        CoachMemberSearch_C_HUM oHumMemberSearch = new CoachMemberSearch_C_HUM();
        CoachMemberSearchWrapper_DTO_HUM oSearch = new CoachMemberSearchWrapper_DTO_HUM();
        
        CoachMemberSearchWrapper_DTO_HUM usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sUnknownMemCheck = true;
        usewrapper.sFirstName = 'Daphiner';
        usewrapper.sLastName = 'Superman';
        usewrapper.sMemberId = '';
        
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Group_Number__c = '987654321';
        oAcc.recordtypeid = oMemRecType.Id;
        oAcc.Birthdate__c = '01/01/1984';
        oAcc.firstName = 'Daphiner';
        oAcc.lastName = 'Superman';
        oAcc.MiddleName = 'J';
        oAcc.Enterprise_ID__c='1030693160';
        oAcc.Home_Office_Account__c = false;
        insert oAcc;
        
        List<ConsumerID__c> lstConsumerId = new List<ConsumerID__c>(); 
        ConsumerID__c conId = new ConsumerID__c();
        conId.Account__c = oAcc1.ID;
        conId.Consumer_External_ID__c = 'UKM|03|SFDC|HumanaId|a0uL00000058F50IAE';
        conId.Consumer_ID__c = '123456';
        conId.ID_Type__c = 'HumanaId';
        lstConsumerId.add(conId);
        insert lstConsumerId;
        
        Test.startTest();
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sGroupNumber = '987654321';
        usewrapper.sUnknownMemCheck = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sMemberId = '';
        CoachMemberSearch_C_HUM.oMemberSearchWrapper =usewrapper;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sPID = '987654321';
        usewrapper.sUnknownMemCheck = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sMemberId = '';
        CoachMemberSearch_C_HUM.oMemberSearchWrapper =usewrapper;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sMemberId = '123456';
        usewrapper.sFirstName = '987654321';
        usewrapper.sLastName = 'Superman';
        usewrapper.sUnknownMemCheck = True;
        
        List<ConsumerID__c> lstConIds = new List<ConsumerID__c>();
        conId = new ConsumerID__c();
        conId.Account__c = oAcc1.ID;
        conId.Consumer_External_ID__c = 'UKM|02|SFDC|HumanaId|a0uL00000058F50IAE';
        conId.Consumer_ID__c = '123456';
        conId.ID_Type__c = 'HumanaId';
        lstConIds.add(conId);
        insert lstConIds;
        CoachMemberSearch_C_HUM.oMemberSearchWrapper =usewrapper;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sMemberId = '123456';
        usewrapper.sGroupNumber = '';
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sBirthdate = '';
        usewrapper.sPhone = '';
        usewrapper.sPostalCode = '';
        usewrapper.sState = '';
        usewrapper.sUnknownMemCheck = true;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        CoachMemberSearch_D_HUM.filterSOSLResultsConsumer(lstConIds,usewrapper);
        
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sMemberId = '987654321';
        usewrapper.sGroupNumber = '987654321';
        usewrapper.sFirstName = 'Superman';
        usewrapper.sLastName = 'Superman';
        usewrapper.sBirthdate = '08/05/1995';
        usewrapper.sPhone = '1234567890';
        usewrapper.sPostalCode = '40202';
        usewrapper.sState = 'Arizona';
        usewrapper.sUnknownMemCheck = True;
        CoachMemberSearch_C_HUM.oMemberSearchWrapper =usewrapper;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        
        usewrapper = new CoachMemberSearchWrapper_DTO_HUM();
        usewrapper.sMemberId = '987654321';
        usewrapper.sGroupNumber = '';
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sUnknownMemCheck = True;
        CoachMemberSearch_C_HUM.oMemberSearchWrapper =usewrapper;
        CoachMemberSearch_C_HUM.searchRecords(usewrapper);
        CoachMemberSearch_C_HUM.sEnterpriseId = '987654321';
        
        Test.stopTest();
    }
    
    /*
    * Method Name : testCreateAccObj()
    * Description : Test Create Account methods
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testCreateAccObj()
    {
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];  
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Group_Number__c = '987654321';
        oAcc.recordtypeid = oMemRecType.Id;
        oAcc.Birthdate__c = '01/01/1984';
        oAcc.firstName = 'Daphiner';
        oAcc.lastName = 'Superman';
        oAcc.MiddleName = 'J';
        oAcc.Enterprise_ID__c='1030693160';
        oAcc.Home_Office_Account__c = false;
        insert oAcc;
        
        List<ConsumerID__c> lstConsumerId = new List<ConsumerID__c>(); 
        ConsumerID__c conId = new ConsumerID__c();
        conId.Account__c = oAcc.ID;
        conId.Consumer_External_ID__c = 'UKM|04|SFDC|HumanaId|a0uL00000058F50IAE';
        conId.Consumer_ID__c = '123456';
        conId.ID_Type__c = 'HumanaId';
        lstConsumerId.add(conId);
        insert lstConsumerId;
        
        
        
        Policy__c oPolicy1 = new Policy__c();
        oPolicy1.Group_Name__c = oAcc.id;
        oPolicy1.Policy_Group_Number__c='11111';
        insert oPolicy1;
        System.AssertNotEquals(oPolicy1, NULL);
        
        Policy_Member__c oPolicyMember1 = new Policy_Member__c();
        oPolicyMember1.Member__c = oAcc.ID;
        oPolicyMember1.Policy__c = oPolicy1.ID;
        oPolicyMember1.Name = '666';
        insert oPolicyMember1;
        System.AssertNotEquals(oPolicyMember1, NULL);
        
        List<Member_Id__c> lstMemberId = new List<Member_Id__c>();
        Member_ID__c oMember = new Member_ID__c();
        oMember.Policy_Member__c = oPolicyMember1.ID;
        oMember.Name = '1234567';
        oMember.Member_Dependent_Code__c ='01';
        oMember.Policy__c = oPolicy1.ID;
        oMember.Member_Card_External_ID__c = 'M1234567';
        lstMemberId.add(oMember);
        insert lstMemberId;
        
        List<MemberPlan> lstMemPlan = new List<MemberPlan>();
        MemberPlan oMemPlan = new MemberPlan(Name='MemberPlan',MemberId = oAcc.Id);
        lstMemPlan.add(oMemPlan);
        insert lstMemPlan;
    }
    
    /*
    * Method Name : testMBEPlusService
    * Description : Test method to cover invokeMBEPlusService 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testMBEPlusService()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test CoachUser1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'coachuser1coachuser1@humcrmstest.com');
        System.runAs(oCCSUser)
        {   
            Test.startTest();
            CoachMemberSearch_S_HUM objMem = new CoachMemberSearch_S_HUM();
            CoachMemberSearch_C_HUM.sourcePersonId='0261350310165609';
            CoachMemberSearch_S_HUM memberVar=new CoachMemberSearch_S_HUM();
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='MBEPlusService',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                      Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
            insert mbeServiceRecord;
            List<String> lstLabels = new List<String>();
            lstLabels = new List<String>{'label1','label2'};
            continuation con =(continuation)CoachMemberSearch_C_HUM.getInvokeMBEPlusService2(CoachMemberSearch_C_HUM.sourcePersonId);
            String mockResposne = '{"GetMemberResponse":{"Body":{"Member":{"EnterpriseId":"12345678","Platform":"EM"}}}}';
            HttpResponse responseobj=new Httpresponse();
            responseobj.setBody(mockResposne);
            responseobj.setStatusCode(200);
            Test.setContinuationResponse(CoachMemberSearch_S_HUM.sRequestLabelPID,responseobj);
            CoachMemberSearch_C_HUM.getparseMBEPlusResponse2(lstLabels);
            CoachMemberSearch_D_HUM.buildEnterpriseIDQuery('1030693160');
            Test.StopTest();
        }                    
    } 
    
     /*
    * Method Name : testgetCustomFieldsToDisplay
    * Description : 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testgetCustomFieldsToDisplay()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test CoachUser1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'coachuser1coachuser1@humcrmstest.com');
        System.runAs(oCCSUser)
        {   
            Test.startTest();
            List<string> Result = CoachMemberSearch_C_HUM.getCustomFieldsToDisplay('Input','Screentype');
            test.stopTest();
        }
    }
    
    /*
    * Method Name : testgetStateValues
    * Description : 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testgetStateValues()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test CoachUser1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'coachuser1coachuser1@humcrmstest.com');
        System.runAs(oCCSUser)
        {   
            Test.startTest();
            Map<string, string> Result = CoachMemberSearch_C_HUM.getStateValues();
            test.stopTest();
        }
    }
    
     /*
    * Method Name : testCreateAccountObject
    * Description : 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testCreateAccountObject()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test CoachUser1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'coachuser1coachuser1@humcrmstest.com');
        
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];  
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.Group_Number__c = '987654321';
        oAcc.recordtypeid = oMemRecType.Id;
        oAcc.Birthdate__c = '01/01/1984';
        oAcc.firstName = 'Daphiner';
        oAcc.lastName = 'Superman';
        oAcc.MiddleName = 'J';
        oAcc.Enterprise_ID__c='1030693160';
        oAcc.Home_Office_Account__c = false;
        insert oAcc;
        System.AssertNotEquals(oAcc, NULL);
        
        ConsumerID__c conId = new ConsumerID__c();
        conId.Account__c = oAcc.ID;
        conId.Consumer_External_ID__c = 'UKM|04|SFDC|HumanaId|a0uL00000058F50IAE';
        conId.Consumer_ID__c = '123456';
        conId.ID_Type__c = 'HumanaId';
        insert conId;
        System.AssertNotEquals(conId, NULL);
        
        MemberPlan oMemPlan = new MemberPlan(Name='MemberPlan',MemberId = oAcc.Id);
        insert oMemPlan;
        System.AssertNotEquals(oMemPlan, NULL);
        
        System.runAs(oCCSUser)
        {   
            Test.startTest();
            CoachMemberSearch_D_HUM.createAccountObj(oMemPlan);
            CoachMemberSearch_D_HUM.createAccountObjConsumer(conId);
            test.stopTest();
        }
    }
    
}