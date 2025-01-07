/*******************************************************************************************************************************
Apex Class Name : MemberSearch_LT_HUM
Version         : 1.0
Created On      : 04/11/2018 
Function        : Test Class for MemberSearch_LC_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                        09/18/2020                  Original Version
* Ashish Kumar										 10/29/2020					 Version 2
* Ashish Kumar/Ritik Agarwal                         02/25/2021                  Increased code coverage
* Kajal Namdev                                       05/07/2021                  Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
* Ashish Kumar										 05/28/2021					 Removed references of Policy__c, Member_Id__c, Policy_Member__c
* Kajal Namdev                                       09/01/2021                  Test coverage fix
* Kajal Namdev                                       09/09/2021                  Test coverage fix
********************************************************************************************************************************/
@isTest
private class MemberSearch_LT_HUM
{ 
    /*
* Method Name : prepareTestData
* Description : Create common test Data
* Return type : void
* Parameter   : NA
*/
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
			list<account> acclist=new list<account>();
            RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            RecordType unKnownRecType = [SELECT Id FROM RecordType WHERE Name = 'Unknown Member' Limit 1];
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
            oAcc.ETL_Record_Deleted__c = true;
            
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
			acclist.add(oAcc);
            
            Account oAcc2 = HUMTestDataHelperTest.getAccount();
            oAcc2.Group_Number__c = 'Supermannn';
            oAcc2.recordtypeid = oMemRecType.Id;
            oAcc2.Birthdate__c = '01/01/1984';
            oAcc2.firstName = 'Daphiner';
            oAcc2.lastName = 'Supermannn';
            oAcc2.MiddleName = 'J';
            oAcc2.Enterprise_ID__c='1030693160';
            oAcc2.Home_Office_Account__c = false;
            oAcc2.ETL_Record_Deleted__c = true;
            insert oAcc2;
            System.AssertNotEquals(oAcc2, NULL);
			acclist.add(oAcc2);

            Account oacc1 = new Account();
            oacc1.firstName='firstName1';
            oacc1.LastName='lastName1';
            oacc1.Group_Number__c = '987654321';
            oacc1.MiddleName='middleName';
            oacc1.suffix='111111';
            oacc1.Deceased_Date__c='';
            oacc1.recordtypeid = unKnownRecType.Id;
            oacc1.Enterprise_ID__c='1030693161';
            oacc1.PersonEmail='';
            oacc1.PersonMailingCity='';
            oacc1.PersonMailingState='';
            oacc1.PersonMailingStateCode='';
            oacc1.PersonMailingStreet='';
            oacc1.PersonMailingPostalCode='123456';
            oacc1.PersonHomePhone='';
            oacc1.Home_Office_Account__c=true;
            oacc1.ETL_Record_Deleted__c=false;
            oacc1.Individual_First_Name__c='getWrapperLast';
            oacc1.Individual_Last_Name__c='getWrapper';
			oacc1.Mbr_Gen_Key__c = '12345';
            insert oacc1;
			acclist.add(oacc1);
            
            PurchaserPlan oDentalPolicy = new PurchaserPlan();
            oDentalPolicy.Policy_Group_Number__c='11111';
            oDentalPolicy.Name = 'Dummy PurchaserPlan';
            oDentalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oDentalPolicy.PayerId = oacc1.Id;
            oDentalPolicy.Major_LOB__c ='DCM';
            oDentalPolicy.Platform__c = 'LV';
            oDentalPolicy.ETL_Record_Deleted__c = false;
            insert oDentalPolicy;
        	MemberPlan omem2 = new MemberPlan();
            omem2.Name='MemberPlan Test';
            omem2.MemberId=oacc1.id;
            omem2.ContractID__c = '123456';
            omem2.PlanId=oDentalPolicy.id;
            omem2.CBIS_ALT_ID__C= '123456';            
            insert omem2;
            
            MemberPlan omem = new MemberPlan();
            omem.Name='MemberPlan2';
            omem.MemberId=oacc1.id;
            omem.CBIS_ALT_ID__C= '123456'; 
            omem.Member_Dependent_Code__c = '1234';
            omem.ETL_Record_Deleted__c = false;
            insert omem;
            
            MemberPlan omem1 = new MemberPlan();
            omem1.Name='MemberPlan3';
            omem1.MemberId=oacc1.id;
            omem1.Member_Dependent_Code__c ='01';
            insert omem1;
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HumMemberRecordType);
 
            ConsumerID__c oConsumerId = new ConsumerID__c();
            oConsumerId.Account__c = oAcc.ID;
            oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
            oConsumerId.Consumer_ID__c = '123456';
            oConsumerId.ID_Type__c = 'HumanaId';
            insert oConsumerId;
            
            System.AssertNotEquals(oConsumerId, NULL);

            ConsumerID__c oConsumerId1 = new ConsumerID__c();
            oConsumerId1.Account__c = oacc1.ID;
            oConsumerId1.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAZ';
            oConsumerId1.Consumer_ID__c = '123457';
            oConsumerId1.ID_Type__c = 'HumanaId1';
            insert oConsumerId1;
            System.AssertNotEquals(oConsumerId1, NULL);            
            
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
        User SUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'user1user1@humcrmstest.com');
        List<Account> lstAcc = new List<Account>();
        Account oAcc1 = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c,PersonBirthDate from Account where Group_Number__c = '987654321' limit 1];
        ConsumerID__c consumerId =[ Select id,Account__c,Account__r.ID,Account__r.name, Account__r.firstName, Account__R.lastName, Account__r.MiddleName,
                                   Account__r.suffix, Account__r.Deceased_Date__C, Account__r.recordTypeId ,Account__r.PersonEmail, Account__r.PersonMailingCity,
                                   Account__r.PersonMailingState,Account__r.PersonMailingStateCode, Account__r.PersonMailingStreet,
                                   Account__r.PersonHomePhone,Account__r.Home_Office_Account__c, Account__r.PersonMailingPostalCode,
                                   Account__r.Individual_First_Name__c ,Account__r.BirthDate__C,Account__R.Individual_Last_Name__c,Account__R.ETL_Record_Deleted__c,ID_Type__c,Account__r.RecordType.Name,Account__r.Enterprise_ID__c from ConsumerID__c where ID_Type__c='HumanaId1' LIMIT 1];
        
        MemberSearch_LD_HUM.createAccountObjConsumer(consumerId);

        MemberPlan omemPlan =[ Select id,Member.ID,Member.name, Member.firstName, Member.lastName, Member.MiddleName,
                                   Member.suffix, Member.Deceased_Date__C, Member.recordTypeId ,Member.PersonEmail, Member.PersonMailingCity,
                                   Member.PersonMailingState,Member.PersonMailingStateCode, Member.PersonMailingStreet,
                                   Member.PersonHomePhone,Member.Home_Office_Account__c, Member.PersonMailingPostalCode,Member.Mbr_Gen_Key__c,
                                   Member.Individual_First_Name__c ,Member.BirthDate__C,Member.Individual_Last_Name__c,Member.ETL_Record_Deleted__c
                                  ,Member.RecordType.Name,Member.Enterprise_ID__c from MemberPlan where name='MemberPlan2' LIMIT 1];        
         MemberSearch_LD_HUM.createAccountObj(omemPlan);
        
        Account acc = [select id,Enterprise_ID__c from account where Enterprise_ID__c='1030693161' LIMIT 1];
        MemberSearch_LD_HUM.buildEnterpriseIDQuery(acc.Enterprise_ID__c);
        
        MemberPlan memObj = [ Select id,ContractID__c from MemberPlan where name='MemberPlan Test' limit 1];
        Account accObj = [select id,name from Account where MiddleName = 'middleName' Limit 1];
        List<Id> ids = new List<Id>();
        ids.add(memObj.id);
        ids.add(accObj.id);
        Test.setFixedSearchResults(ids);
        List<String> recTypesLst = new List<String>();
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<MemberSearch_LC_HUM> lstHumMemberSearch = new List<MemberSearch_LC_HUM>();
        MemberSearch_LC_HUM oHumMemberSearch = new MemberSearch_LC_HUM();
        MemberSearchWrapper_LH_HUM oSearch = new MemberSearchWrapper_LH_HUM();
        
        MemberSearchWrapper_LH_HUM usewrapper = new MemberSearchWrapper_LH_HUM();
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
        usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sGroupNumber = '987654321';
        usewrapper.sUnknownMemCheck = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sMemberId = '123456';
        MemberSearch_LC_HUM.oMemberSearchWrapper =usewrapper;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        
        usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sPID = '987654321';
        usewrapper.sUnknownMemCheck = false;
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sMemberId = '';
        MemberSearch_LC_HUM.oMemberSearchWrapper =usewrapper;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        
        usewrapper = new MemberSearchWrapper_LH_HUM();
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
        MemberSearch_LC_HUM.oMemberSearchWrapper =usewrapper;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        
        usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sMemberId = '123456';
        usewrapper.sGroupNumber = '';
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sBirthdate = '';
        usewrapper.sPhone = '';
        usewrapper.sPostalCode = '';
        usewrapper.sState = '';
        usewrapper.sUnknownMemCheck = true;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        MemberSearch_LD_HUM.filterSOSLResultsConsumer(lstConIds,usewrapper);
        
        usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sMemberId = '987654321';
        usewrapper.sGroupNumber = '987654321';
        usewrapper.sFirstName = 'Superman';
        usewrapper.sLastName = 'Superman';
        usewrapper.sBirthdate = '08/05/1995';
        usewrapper.sPhone = '1234567890';
        usewrapper.sPostalCode = '40202';
        usewrapper.sState = 'Arizona';
        usewrapper.sUnknownMemCheck = True;
        MemberSearch_LC_HUM.oMemberSearchWrapper =usewrapper;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        
        usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sMemberId = '987654321';
        usewrapper.sGroupNumber = '';
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sUnknownMemCheck = True;
        MemberSearch_LC_HUM.oMemberSearchWrapper =usewrapper;
        MemberSearch_LC_HUM.searchRecords(usewrapper);
        MemberSearch_LC_HUM.sEnterpriseId = '987654321';
        
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
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs(oCCSUser)
        {   
            Test.startTest();
            MemberSearch_LS_HUM objMem = new MemberSearch_LS_HUM();
            MemberSearch_LC_HUM.sourcePersonId='0261350310165609';
            MemberSearch_LS_HUM memberVar=new MemberSearch_LS_HUM();
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='MBEPlusService',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                      Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
            insert mbeServiceRecord;
            List<String> lstLabels = new List<String>();
            lstLabels = new List<String>{'Continuation-1','label2'};
            continuation con =(continuation)MemberSearch_LC_HUM.getInvokeMBEPlusService2(MemberSearch_LC_HUM.sourcePersonId);
            String mockResposne = '{"GetMemberResponse":{"Body":{"Member":{"EnterpriseId":"12345678","Platform":"EM"}}}}';
            HttpResponse responseobj=new Httpresponse();
            responseobj.setBody(mockResposne);
            responseobj.setStatusCode(200);
            Test.setContinuationResponse('Continuation-1',responseobj);
            MemberSearch_LC_HUM.getparseMBEPlusResponse2(lstLabels);
            Test.StopTest();
        }                    
    }
    
    static testMethod void test(){
        List<List<sObject>> mList = new List<List<sObject>>();
        List<MemberPlan> memList = [Select id,CBIS_ALT_ID__C,CERT_ID__C,CLIENTID__C,CONTRACTID__C,CONTRACTID_ETL__C,EXCH_MBR_ID__C,MEMBER_ID_BASE__C,TPA_ID__C,VITALITY_ENTITY_ID__C,MEDICAID_ID__C from MemberPlan where name='MemberPlan2'];
        mList.add(memList);
        List<Account> accList = [select id,SSN__C,MedicareID__c,MedicareID_HICN__c,PERSONID__c from account];
        mList.add(accList);
        MemberSearch_LD_HUM.checkResultSizeLimit(accList);
        MemberSearchWrapper_LH_HUM usewrapper = new MemberSearchWrapper_LH_HUM();
        usewrapper.sMemberId = '123456789';
        usewrapper.sGroupNumber = '';
        usewrapper.sFirstName = '';
        usewrapper.sLastName = '';
        usewrapper.sUnknownMemCheck = True;
        MemberSearch_LD_HUM.filterSOSLResultsNewMemberPlan(mList,usewrapper);
    }
}