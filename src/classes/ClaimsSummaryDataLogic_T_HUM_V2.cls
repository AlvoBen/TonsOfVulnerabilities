/**************************************************************************** 
Apex Class Name  : ClaimsSummaryDataLogic_T_HUM_V2
Version          : 1.0  
Created Date     : 4th April 2018
Function         : Test class for ClaimsSummary_D_HUM_v2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula         212620              04/04/2018         Claims V3 Original version
* Suresh Gottipati                              06/15/2018         REQ - 366902 - Claim Summary section in Claim Summary page.
* Praveen Kumar Parimi      230338              07/12/2018         Adding CAS web UI references for their coverage.
* Santhi Mandava            354419              04/15/2019         Replaced "Member_Id_Value__c" with Name
* Santhi Mandava            355559              05/10/2019         Removed "ENCRYPTIONTYPE" custom setting creation
* Santhi Mandava            356422              05/17/2019         Fixed test class issue
* Prasanthi Kandula                             07/02/2021         US - 2356070 Claims Summary performance improvement 
***************************************************************************************************/
@isTest
private Class ClaimsSummaryDataLogic_T_HUM_V2
{
   /*
    *  This method  is used to create test data 
    *  @name   prepareTestData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        { 
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test again';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1005577525';
            objAcc1.General_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test memberid';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005587525';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name='testPolicy';
            objpol1.Group_Name__c = objAcc1.Id;
            objpol1.Major_LOB__c = 'MER';
            objpol1.Source_Cust_Cov_Key__c = '123456789';
            objpol1.Product__c = oProd.Id;
            objpol1.Product__r = oProd;
            objpol1.Platform__c = 'LV';
            insert objpol1;
            system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy1';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            insert objpol2;
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c objPolMem1 = new Policy_Member__c();
            objPolMem1.Name = 'testpolmem111';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.Subscriber__c = objPolMem.Id;
            objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem1.End_Date__c=Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c='00|ODS|7925004434817|12345678|34567|2013-07-01';
            objPolMem1.Member__c = objAcc1.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Relationship__c, 'Self');
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem114';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.Subscriber__c = objPolMem1.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=Date.today().addDays(1).format();
            objPolMem2.Policy_Member_External_ID__c='00|ODS|7925004435817|12345678|34567|2013-07-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Relationship__c, 'Self');
            
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03';
            objMemberID1.Type__c = 'SSN';        
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );
            
            Member_ID__c objMemberID2 = new Member_ID__c();
            objMemberID2.policy_member__c = objPolMem1.id;
            objMemberID2.policy__c = objpol1.Id;
            objMemberID2.Name = 'H12345';
            objMemberID2.Member_Card_External_ID__c = 'ODS123456'; 
            objMemberID2.Member_Dependent_Code__c  = '03';
            objMemberID2.Type__c = 'Member-Id-Base';        
            insert objMemberID2;
            system.assertequals('H12345',objMemberID2.Name);
            
            Member_ID__c objMemberID3 = new Member_ID__c();
            objMemberID3.policy_member__c = objPolMem2.id;
            objMemberID3.policy__c = objpol2.Id;
            objMemberID3.Name = 'H123453';
            objMemberID3.Member_Card_External_ID__c = 'ODS1234564'; 
            objMemberID3.Member_Dependent_Code__c  = '03';
            objMemberID3.Type__c = 'Member-Id-Base';        
            insert objMemberID3;
            system.assertequals('H123453',objMemberID3.Name);
            
            LVRelationShipCodeToCAS__c LVRelationCode = new LVRelationShipCodeToCAS__c();
            LVRelationCode.Name = '00';
            LVRelationCode.CASTranslation__c = 'EE';
            insert LVRelationCode;
            system.assertequals(LVRelationCode.CASTranslation__c,'EE');
             
        }
    }

   /*
    *  This method  is used to test the methods of ClaimsSummary_D_HUM
    *  @name   testData
    *  @param  NA
    *  @return NA
    *  @throws NA
    */
     static testMethod void testData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Account oAccount1 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account1'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account2'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c, status__c, Policy_platform__c , Product__c, Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Policy_Member__c oPolMem1 =  [Select Member__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem1.Member__c,oAccount.id);
            
            Policy_Member__c oPolMem2 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount1.Id];
            System.assertEquals(oPolMem2.Relationship__c, 'Self');
            
            Policy_Member__c oPolMem3 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount2.Id];
            System.assertEquals(oPolMem3.Relationship__c, 'Self');
            
            Member_ID__c oMemberId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where Member_Card_External_ID__c = 'ODS12345' LIMIT 1];
            System.assertEquals(oMemberId.Name ,'12345');
            
            List<String> memberList = new List<string>();
            memberList.add(oMemberId.Name );
            
           
            
            id sRecId=oPolMem.id;

            Test.StartTest();
                        
             List<LVRelationShipCodeToCAS__c> olstlvcas = ClaimsSummary_D_HUM_v2.getLVCASTranslationDetails(oPolMem.Relationship_Code__c);           
            System.assertEquals(olstlvcas.size(),1);
            List<Member_ID__c> oMemId = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem.id);
            System.assertEquals(oMemId.size(),1);
            List<Member_ID__c> oMemId1 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem2.id);
            System.assertEquals(oMemId1.size(),1); 
            List<Member_ID__c> oMemId2 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem3.id);
            System.assertEquals(oMemId2.size(),1);
            Policy_Member__c oPolMem4 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount2.Id];
            System.assertEquals(oPolMem4.Relationship__c, 'Self');
             oPolMem4.Subscriber__c = null;
             update   oPolMem4; 
             List<Member_ID__c> oMemId3 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem4.id);           
           set<String> Memberfromaccount = ClaimsSummary_D_HUM_v2.getPolicyMembersFromAccount(oAccount.id);
            System.assertEquals(Memberfromaccount!=null, true);          
            List<Member_ID__c> oMemberparametersList  = ClaimsSummary_D_HUM_v2.getMemberParameters(memberList);
           System.assertEquals(oMemberparametersList!=null,true);
            
           
            
            Test.stopTest(); 
        }
    } 
    
      static testMethod void testDatanegative()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Account oAccount1 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account1'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account2'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Policy_Member__c oPolMem1 =  [Select Member__c from Policy_Member__c where Member__c =: oAccount.Id];
            System.assertEquals(oPolMem1.Member__c,oAccount.id);
            
            Policy_Member__c oPolMem2 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount1.Id];
            System.assertEquals(oPolMem2.Relationship__c, 'Self');
            
            Policy_Member__c oPolMem3 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount2.Id];
            System.assertEquals(oPolMem3.Relationship__c, 'Self');
            
            Member_ID__c oMemberId = [select Member_Dependent_Code__c, Policy_Member__r.Effective_Date__c, Policy_Member__r.End_Date__c, Policy_Member__c, Name from Member_ID__c where Member_Card_External_ID__c = 'ODS12345' LIMIT 1];
            System.assertEquals(oMemberId.Name,'12345');
            
            List<String> memberList = new List<string>();
            memberList.add(oMemberId.name);
            
            id sRecId=oPolMem.id;

            Test.StartTest();
            List<Policy_Member__c> lstPolMem = ClaimsSummary_D_HUM_v2.getPolicyMemberDetails(oPolMem.id);            
            List<LVRelationShipCodeToCAS__c> olstlvcas = ClaimsSummary_D_HUM_v2.getLVCASTranslationDetails(oPolMem.Relationship_Code__c);           
            System.assertEquals(olstlvcas.size(),1);
            List<Member_ID__c> oMemId = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem.id);
            System.assertEquals(oMemId.size(),1); 
            List<Member_ID__c> oMemId1 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem2.id);
            System.assertEquals(oMemId1.size(),1); 
            List<Member_ID__c> oMemId2 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem3.id);
            System.assertEquals(oMemId2.size(),1);
            Policy_Member__c oPolMem4 =  [Select Id,Subscriber__c ,Relationship__c, Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode from Policy_Member__c where Member__c =: oAccount2.Id];
            System.assertEquals(oPolMem4.Relationship__c, 'Self');
             oPolMem4.Subscriber__c = null;
             update   oPolMem4; 
             List<Member_ID__c> oMemId3 = ClaimsSummary_D_HUM_v2.getMemberIDObjDetails(oPolMem4.id);          
             Id [] fixedSearchResults= new Id[1];
           fixedSearchResults[0] = oMemberId.Id;
           Test.setFixedSearchResults(fixedSearchResults);           
            List<Member_ID__c> oMemberparametersList  = ClaimsSummary_D_HUM_v2.getMemberParameters(memberList);
            System.assertEquals(oMemberparametersList.size(),1);
            
            Test.stopTest(); 
        }
    }
     
}