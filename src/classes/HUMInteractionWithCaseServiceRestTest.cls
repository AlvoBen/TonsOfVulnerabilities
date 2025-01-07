/***************************************************************************************************************************************************************************
Apex Class Name  : HUMInteractionWithCaseServiceRest
Version          : 1.0
Created Date     : March 14 2018
Function         : This Rest Webservice is used to for all the Interaction and case related services.
Test Class       : HUMInteractionWithCaseServiceRestTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Ranjeeth Nagishetty        215366                    04/15/2019                 Original Version
 * Mohammed Noor                                        07/11/2019                 Changes done for REQ - 403693. 
 ****************************************************************************************************************************************************************************/

@isTest
private class HUMInteractionWithCaseServiceRestTest {
    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
     {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser.lastname=='test User1',true);

        System.runAs(oUser) 
        {
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '123456';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
                //Provider Account
                Account ProviderAcc = HUMTestDataHelperTest.getAccount();
                ProviderAcc.Name = 'test account';
                ProviderAcc.Enterprise_ID__c = '123456';
                ProviderAcc.NPI_ID__c = '1234567890';
                ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
                insert ProviderAcc;
                System.assert(ProviderAcc.Name == 'test account');
                //Agent Account
                Account AgentAcc = new Account();
                AgentAcc = HUMTestDataHelperTest.getAccount();
                AgentAcc.Name = 'Agent Account';
                AgentAcc.Enterprise_ID__c = '123456';
                AgentAcc.NPI_ID__c = '1234567890';
                //AgentAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent').getRecordTypeId();
                insert AgentAcc;
                System.assert(AgentAcc.Name == 'Agent Account');
                //Member Account
                Account MembAcc = HUMTestDataHelperTest.getAccount();
                MembAcc.Name = 'test Member account';
                MembAcc.Enterprise_ID__c = '123456789';
                //MembAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();
                insert MembAcc;
                System.assert(MembAcc.Name == 'test Member account');
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
           
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';
           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
            
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Group';
            insert caseObj ;
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Group', true); 
            
            //for create case method
            Group testGroup = new group(name = 'Louisville RSO Enrollment' , type = 'Queue');
            insert testGroup;                     
            system.assertNotEquals(testGroup, null , 'Group  not created'); 
                      
            QueueSobject mappingObject = new QueueSobject(QueueId = testGroup.Id, SobjectType = 'Case');
            insert mappingObject;
            system.assertNotEquals(mappingObject, null, 'Value not matched');
             
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Louisville RSO Enrollment' , IsActive__c= true);
            insert oQSetup ;
            system.assertNotEquals(oQSetup, null, 'Value not matched');
            Queue_View__c oQv1 = new Queue_View__c(Queue_View_Name__c='Louisville RSO Enrollment', Department__c='Enrollment', Work_Queue_Name__c=oQSetup.Id); 
            insert oQv1;
        
            system.assertNotEquals(oQv1, null, 'Value not matched');
        
             HUMConstants__c test1  = new  HUMConstants__c();
             test1.name = 'UNKNOWN_MEMBER';
             test1.stringValue__C = 'Unknown Member';
                          
             insert test1;
             system.assertNotEquals(test1,null, 'Value not matched');
             
             List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc1 = new Account( Enterprise_Id__c = '223300104', firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc2 = new Account( Enterprise_Id__c = '002935049', firstname = 'test3', lastname = 'method3' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc3 = new Account( Enterprise_Id__c = '002935049', firstname = 'test4', lastname = 'method4' ,Birthdate__c= '01/04/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc4 = new Account( Enterprise_Id__c = '223366104', firstname = 'test5' ,lastname = 'method5' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc5 = new Account( Enterprise_Id__c = '223366104', firstname = 'test5' ,lastname = 'method5' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc6 = new Account( Enterprise_Id__c = '223366104', firstname = 'test6' ,lastname = 'method6' , Birthdate__c= '01/01/1990',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc7 = new Account( Enterprise_Id__c = '', firstname = 'test7' ,lastname = 'method7' , Birthdate__c= '01/01/1997',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId());
             Account acc8 = new Account( Enterprise_Id__c = '223366105', firstname = 'test8' ,lastname = 'method8' , Birthdate__c= '01/01/1997',ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'KY', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             
             lstAccount.Add(acc);
             lstAccount.Add(acc1);
             lstAccount.Add(acc2);
             lstAccount.Add(acc3);
             lstAccount.Add(acc4);
             lstAccount.Add(acc5);
             lstAccount.Add(acc6);
             lstAccount.Add(acc7);
             lstAccount.Add(acc8);
             
             Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');
                          
             list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
             Product2 testProduct1 = new Product2(Name ='testprod2', ProductCode ='MED');
             Product2 testProduct2 = new Product2(Name ='testprod3', ProductCode ='MED');
             Product2 testProduct3 = new Product2(Name ='testprod4', ProductCode ='MED');
             Product2 testProduct4 = new Product2(Name ='testprod5', ProductCode ='MED');
             Product2 testProduct5 = new Product2(Name ='testprod6', ProductCode ='MED');
             Product2 testProduct6 = new Product2(Name ='testprod8', ProductCode ='MED');
             
             listProduct.Add(testProduct);
             listProduct.Add(testProduct1);
             listProduct.Add(testProduct2);
             listProduct.Add(testProduct3);
             listProduct.Add(testProduct4);
             listProduct.Add(testProduct5);
             listProduct.Add(testProduct6);
             
             Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
             
                          
             list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy1 = new Policy__c(Group_Name__c = lstAccount[1].ID, Major_LOB__c = 'MCD', Product__c = listProduct[1].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy2 = new Policy__c(Group_Name__c = lstAccount[2].ID, Major_LOB__c = 'MEF', Product__c = listProduct[2].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy3 = new Policy__c(Group_Name__c = lstAccount[3].ID, Major_LOB__c = 'MEP', Product__c = listProduct[3].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy4 = new Policy__c(Group_Name__c = lstAccount[4].ID, Major_LOB__c = 'MES', Product__c = listProduct[4].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy5 = new Policy__c(Group_Name__c = lstAccount[5].ID, Major_LOB__c = 'MGP', Product__c = listProduct[5].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy6 = new Policy__c(Group_Name__c = lstAccount[6].ID, Major_LOB__c = 'MGP', Product__c = listProduct[5].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy7 = new Policy__c(Group_Name__c = lstAccount[8].ID, Major_LOB__c = 'MGP', Product__c = listProduct[6].ID,Policy_Group_Number__c='11111');
                     
             listPolicy.Add(testPolicy);
             listPolicy.Add(testPolicy1);
             listPolicy.Add(testPolicy2);
             listPolicy.Add(testPolicy3);
             listPolicy.Add(testPolicy4);
             listPolicy.Add(testPolicy5);
             listPolicy.Add(testPolicy6);
             listPolicy.Add(testPolicy7);
             
             insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
             
             List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
             Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber1 = new Policy_Member__c(Policy__c = listPolicy[1].ID, Member__c = lstAccount[1].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
             Policy_Member__c memeber2 = new Policy_Member__c(Policy__c = listPolicy[2].ID, Member__c = lstAccount[2].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber3 = new Policy_Member__c(Policy__c = listPolicy[3].ID ,Member__c = lstAccount[3].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber4 = new Policy_Member__c(Policy__c = listPolicy[4].ID ,Member__c = lstAccount[4].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber5 = new Policy_Member__c(Policy__c = listPolicy[5].ID ,Member__c = lstAccount[4].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber6 = new Policy_Member__c(Policy__c = listPolicy[6].ID ,Member__c = lstAccount[6].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber7 = new Policy_Member__c(Policy__c = listPolicy[7].ID ,Member__c = lstAccount[8].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber8 = new Policy_Member__c(Policy__c = listPolicy[7].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
             listPolicyMember.Add(memeber);
             listPolicyMember.Add(memeber1);
             listPolicyMember.Add(memeber2);
             listPolicyMember.Add(memeber3);
             listPolicyMember.Add(memeber4);
             listPolicyMember.Add(memeber5);
             listPolicyMember.Add(memeber6);
             listPolicyMember.Add(memeber7);
                listPolicyMember.Add(memeber8);
             Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
             
             List<Member_ID__c> listmember = new List<Member_ID__c>();
             Member_ID__c member = new Member_ID__c(Policy__c = listPolicy[0].ID,Policy_Member__c = listPolicyMember[0].ID, Member_Card_External_ID__c= '123345' , name = '360306641' , Type__c ='SSN');
             Member_ID__c member1 = new Member_ID__c(Policy__c = listPolicy[1].ID,Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c= '123645' , name = '360306641' , Type__c ='SSN');
             Member_ID__c member2 = new Member_ID__c(Policy__c = listPolicy[2].ID,Policy_Member__c = listPolicyMember[2].ID, Member_Card_External_ID__c= '123245' , name = '002935049' , Type__c ='MedicareID');
             Member_ID__c member3 = new Member_ID__c(Policy__c = listPolicy[3].ID,Policy_Member__c = listPolicyMember[3].ID , Member_Card_External_ID__c= '123154', name = '002935049' , Type__c ='MedicareID');
             Member_ID__c member4 = new Member_ID__c(Policy__c = listPolicy[4].ID,Policy_Member__c = listPolicyMember[4].ID , Member_Card_External_ID__c= '123654', name = '567234444' , Type__c ='SSN');
             Member_ID__c member5 = new Member_ID__c(Policy__c = listPolicy[5].ID,Policy_Member__c = listPolicyMember[4].ID , Member_Card_External_ID__c= '123674', name = '567234444' , Type__c ='SSN');
             Member_ID__c member6 = new Member_ID__c(Policy__c = listPolicy[6].ID,Policy_Member__c = listPolicyMember[6].ID , Member_Card_External_ID__c= '123664', name = '567234224' , Type__c ='MedicareID');
             Member_ID__c member7 = new Member_ID__c(Policy__c = listPolicy[7].ID,Policy_Member__c = listPolicyMember[7].ID , Member_Card_External_ID__c= '123684', name = 'H46738930' , Type__c ='Member-Id-Base');
             Member_ID__c member8 = new Member_ID__c(Policy__c = listPolicy[7].ID, Policy_Member__c = listPolicyMember[7].ID, Member_Card_External_ID__c = '123685', name = 'H46738930', Type__c = 'SSN');
             Member_ID__c member9 = new Member_ID__c(Policy__c = listPolicy[7].ID, Policy_Member__c = listPolicyMember[8].ID, Member_Card_External_ID__c = '123686', name = 'H46738933', Type__c = 'Member-Id-Base');
             Member_ID__c member10 = new Member_ID__c(Policy__c = listPolicy[7].ID, Policy_Member__c = listPolicyMember[8].ID, Member_Card_External_ID__c = '123687', name = 'H46738933', Type__c = 'Member-Id-Base');
             Member_ID__c member11 = new Member_ID__c(Policy__c = listPolicy[7].ID, Policy_Member__c = listPolicyMember[7].ID, Member_Card_External_ID__c = '123680', name = 'H43738934', Type__c = 'Member-Id-Base');
             Member_ID__c member12 = new Member_ID__c(Policy__c = listPolicy[1].ID, Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c = '123699', name = '300006641', Type__c = 'SSN');
             Member_ID__c member13 = new Member_ID__c(Policy__c = listPolicy[1].ID, Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c = '123699', name = '300006641', Type__c = 'SSN');
                
             listmember.Add(member);
             listmember.Add(member1);
             listmember.Add(member2);
             listmember.Add(member3);
             listmember.Add(member4);
             listmember.Add(member5);
             listmember.Add(member6);
             listmember.Add(member7);
                listmember.Add(member8);
                listmember.Add(member9);
                listmember.Add(member10);
                listmember.Add(member11);
                listmember.Add(member13);
             insert listmember;
             system.assertNotEquals(listmember, null , 'Members  not created'); 
             
                          
             List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes = new HUMAccountCaseRecordTypes__c(Name = 'Unknown Member' ,Account_RecordType__c = 'Unknown Member' , Case_RecordType__c = 'Unknown Case');
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes); 
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes1);         
             insert HUMAccountCaseRecordTypesList;
             system.assertNotEquals(HUMAccountCaseRecordTypesList, null , 'Recordtypes  not created');
           
             
           
             Classification__c classfication = new Classification__c(name = 'Chronic Care SNP' ,Is_Active__c = true);
             insert classfication ;
            
             Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
             insert classificationType ;
             system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
             Intent__c intent = new Intent__c(name = 'Denied' , Is_Active__c = true);
             insert intent ;
             system.assertNotEquals(intent, null ,'classificationType  not created');
            
             List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classfication.ID, Classification_Type__c = classificationType .ID, Intent__c = intent .ID , IsActive__c = true);
             listctciJunction.Add(ctciJunction);
             insert listctciJunction ;
             system.assertNotEquals(intent, null ,'CTCIJunctionlist  not created');            
             
            Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.AccountId = acc8.Id;
            caseObj1.Interacting_With__c = oAcc.Id;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseObj1.Classification_ID__c = classificationRecDec.ID;
            caseObj1.Intent_ID__c  = intentRec.id;
            caseobj1.Interacting_With_Type__c = 'Group';
            insert caseObj1 ;
            System.assertEquals(caseobj1.Interacting_With_Type__c == 'Group', true);  
                Case caseObjMem = HUMTestDataHelperTest.getCase();
                caseObjMem.AccountId = acc8.Id;
                caseObjMem.Interacting_With__c = oAcc.Id;
                caseObjMem.Interacting_With_Name__c = 'Test Member Name';
                caseObjMem.Classification_ID__c = classificationRecDec.ID;
                caseObjMem.Intent_ID__c = intentRec.id;
                insert caseObjMem;
                
                System.assertEquals(caseobj1.Interacting_With_Type__c == 'Group', true);
            Case caseObj2 = HUMTestDataHelperTest.getCase();
            caseObj2.AccountId = acc.Id;
            caseObj2.Interacting_With__c = oAcc.Id;
            caseObj2.Interacting_With_Name__c = 'Test Name';
            caseObj2.Classification_ID__c = classificationRecDec.ID;
            caseObj2.Intent_ID__c  = intentRec.id;
            caseobj2.Interacting_With_Type__c = 'Group';
            insert caseObj2 ;
            System.assertEquals(caseobj2.Interacting_With_Type__c == 'Group', true);   
            
            List<CaseComment> lstCaseComment = new List<CaseComment>();
            CaseComment objCaseComment = HUMTestDataHelperTest.getCaseComm();
            objCaseComment.ParentId = caseObj.Id;
            lstCaseComment.Add(objCaseComment);
            insert lstCaseComment ;
            System.assertNotEquals(lstCaseComment, null, 'CaseComment not created');  
            
            Attachment__c objAttachment = HUMTestDataHelperTest.getCustomAttachment();
            objAttachment.Attachment_URL_Text__c = 'ehub.com';
            objAttachment.Name = 'ehub.com';
            objAttachment.Related_To_Case__c = caseObj.Id;
            insert objAttachment;
            System.assertNotEquals(objAttachment, null, 'Attachment not created'); 
             
             Attachment_Log__c objAttachmentLog = HUMTestDataHelperTest.getAttachLog();
             objAttachmentLog.Attachment_Key__c = '1254567';
             objAttachmentLog.Attachment_Type__c = 'Test';
             objAttachmentLog.Case__C = caseObj.Id;
             insert objAttachmentLog;
            System.assertNotEquals(objAttachmentLog, null, 'AttachmentLog not created'); 
             
             Attachment_Log_Item__c objAttachmentLogItem = HUMTestDataHelperTest.getAttLogItem();
             objAttachmentLogItem.Name = 'Test';
             objAttachmentLogItem.Attachment_Log__c = objAttachmentLog.Id;
             insert objAttachmentLogItem;
             System.assertNotEquals(objAttachmentLogItem, null, 'AttachmentLogItem not created');
             
             Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
             insert objInteraction;
             System.assertNotEquals(objInteraction, null, 'Interaction not created');
             
             Task objTask = HUMTestDataHelperTest.getTaskRecord();
             objTask.WhatId = caseObj.Id;
             objTask.OwnerID = oUser.Id;
             insert objTask;
             System.assertNotEquals(objTask, null, 'Task not created');
             
             HUMConstants__c test2  = new  HUMConstants__c();
             test2.name = 'UNKNOWN_MEMBER_EXTID';
             insert test2;
             system.assertNotEquals(intent, null ,'Unknown member ext id  not created');
             
                //Custom Setting for EPASS Service which is used in provider search.
                HUM_Webservice_Callout__c EPSServiceDetails = new HUM_Webservice_Callout__c();
                EPSServiceDetails.Name = 'EPSServiceREST_V1';
                EPSServiceDetails.Certificate_Name__c = 'Humana_GuidanceCare_Mercury';
                EPSServiceDetails.Active__c = true;
                EPSServiceDetails.End_Point_URL__c = 'https://int-crmservicews.humana.com/EPSServiceREST_V1.0/EnterpriseProviderSearch';
                EPSServiceDetails.Timeout__c = '12000';
                EPSServiceDetails.Request_Type__c = 'POST';
                EPSServiceDetails.Content_Type__c = 'application/json';
                insert EPSServiceDetails;
                System.assert(EPSServiceDetails.id != null);

                //Test Data Created for Method testProviderSearchQueryBuildTest
                RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Provider'
                    and sObjectType = 'Account'
                ];
                System.assert(PersonAccountRecordType != null);
                //Test Data Created For Agent
                RecordType AgentAccountRecordType = [Select Id from RecordType where Name = 'Agent/Broker'
                    and sObjectType = 'Account'
                ];
                System.assert(AgentAccountRecordType != null);
                //Test Dara Created For Unknown Member
                RecordType MemberAccountRecordType = [Select Id from RecordType where Name = 'Unknown Member'
                    and sObjectType = 'Account'
                ];
                System.assert(MemberAccountRecordType != null);
                Account acc_queryBuildTestCase = HUMTestDataHelperTest.getAccount();
                acc_queryBuildTestCase.RecordType = PersonAccountRecordType;
                acc_queryBuildTestCase.Individual_First_Name__c = 'TEST';
                acc_queryBuildTestCase.Individual_Last_Name__c = 'TEST';
                acc_queryBuildTestCase.Name = 'TEST' + ' ' + 'TEST';
                acc_queryBuildTestCase.NPI_ID__c = '1245351000';
                acc_queryBuildTestCase.BillingState = 'Alabama';
                acc_queryBuildTestCase.BillingPostalCode = '54321';
                acc_queryBuildTestCase.Agent_Id__c = '111111111';
                acc_queryBuildTestCase.Provider_Classification__c = 'F';
                insert acc_queryBuildTestCase;

                ConsumerID__c cons_queryBuildTestCase = new ConsumerID__c();
                cons_queryBuildTestCase.Consumer_ID__c = '555555555';
                cons_queryBuildTestCase.ID_Type__c = Label.HumServiceTaxIDtag;
                cons_queryBuildTestCase.ETL_Record_Deleted__c = false;
                cons_queryBuildTestCase.Account__c = acc_queryBuildTestCase.id;
                insert cons_queryBuildTestCase;

                //Case Creation for Provider Test
                Case caseObjProvider = HUMTestDataHelperTest.getCase();
                caseObjProvider.Interacting_With__c = oAcc.Id;
                caseObjProvider.Interacting_With_Name__c = 'Test Name';
                caseObjProvider.Classification_ID__c = classificationRecDec.ID;
                caseObjProvider.Intent_ID__c = intentRec.id;
                caseObjProvider.Interacting_With_Type__c = 'Provider';
                insert caseObjProvider;
                System.assertEquals(caseObjProvider.Interacting_With_Type__c == 'Provider', true);
                //Case Creation for Unknown provider
                Case caseObjUnknwProvider = HUMTestDataHelperTest.getCase();
                caseObjUnknwProvider.Interacting_With__c = oAcc.Id;
                caseObjUnknwProvider.Interacting_With_Name__c = 'Test Name';
                caseObjUnknwProvider.Classification_ID__c = classificationRecDec.ID;
                caseObjUnknwProvider.Intent_ID__c = intentRec.id;
                caseObjUnknwProvider.Interacting_With_Type__c = 'UnknownProvider';
                insert caseObjUnknwProvider;
                System.assertEquals(caseObjUnknwProvider.Interacting_With_Type__c == 'UnknownProvider', true);
                // case Creation For Agent
                Case caseObjAgent = HUMTestDataHelperTest.getCase();
                caseObjAgent.Interacting_With__c = AgentAcc.Id;
                caseObjAgent.Interacting_With_Name__c = 'Test Name';
                caseObjAgent.Classification_ID__c = classificationRecDec.ID;
                caseObjAgent.Intent_ID__c = intentRec.id;
                caseObjAgent.Interacting_With_Type__c = 'Agent';
                insert caseObjAgent;
                System.assertEquals(caseObjAgent.Interacting_With_Type__c == 'Agent', true);

                //Case creation for Unknown Member
                Case caseObjUnknwMember = HUMTestDataHelperTest.getCase();
                caseObjUnknwMember.Interacting_With__c = MembAcc.Id;
                caseObjUnknwMember.Interacting_With_Name__c = 'Test Name';
                caseObjUnknwMember.Classification_ID__c = classificationRecDec.ID;
                caseObjUnknwMember.Intent_ID__c = intentRec.id;
                caseObjUnknwMember.Interacting_With_Type__c = 'Unknown Member';
                insert caseObjUnknwMember;
                System.assertEquals(caseObjUnknwMember.Interacting_With_Type__c == 'Unknown Member', true);

        
                ConsumerID__c oConsumerId = new ConsumerID__c();
                oConsumerId.Account__c = ProviderAcc.ID;
                oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
                oConsumerId.Consumer_ID__c = '12345';
                oConsumerId.ID_Type__c = 'HumanaId';
                insert oConsumerId;
                System.AssertNotEquals(oConsumerId, NULL);

            }
    }
    
     /**
    * This is a test method for Create Interaction with Case  Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_CreateInteraction()
    {
        
       HUMTestDataHelperTest.getHUMConstantsData();       
       User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User2' LIMIT 1];
       system.assertEquals(oUser.lastname == 'test User2',true);
               
        System.runAs(oUser) 
        {
                        
             CreateInteractionRequest_DTO_HUM createServiceRequest = new CreateInteractionRequest_DTO_HUM ();
            
             CreateInteractionRequest_DTO_HUM.CreateInteractionRequest request = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest();
             
             
                  
                  request.SourceApp ='AE';
                  request.SourceIdTypeCd = 'Test';
				  request.RequiredCasesInResponse = true;
				  request.InteractionOrigin = 'Email';
				  request.UUID = '1245232';
				  request.CreatedByQueue = 'Louisville RSO Enrollment';           
        
                  List<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> lstAttachmentLogs = new List<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();
                  CreateInteractionRequest_DTO_HUM.CaseInteractingAbout oattachLog = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
                  oattachLog.CaseIdentifier = '3439403403';
                  
                  CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO oattachAttribute = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
                  oattachAttribute.AddressLine1 = 'Kentucky';
                  oattachAttribute.AddressLine2 = '45678';
				  oattachAttribute.City = 'Louisville';
				  oattachAttribute.DOB = '06/17/2001';
				  oattachAttribute.EmailID = 'nklfk@humana.com';
                  oattachAttribute.FirstName = 'first';
				  oattachAttribute.HomePhone = 'name';
				  oattachAttribute.ID = 'ghdgjha17733455';
				  oattachAttribute.IDType = 'RequestId';
                  oattachAttribute.MemberIdDependentCode = '45678';
				  oattachAttribute.LastName = 'last';
				  oattachAttribute.State = 'kentu';
				  oattachAttribute.EntityType = 'member';
                  oattachAttribute.ZipCode = '45678';
				  oattachAttribute.ZipPlus = '45678';
				  oattachAttribute.ProviderClassification = 'cliams';
                  oattachAttribute.AccountName = 'Test45678';
                  oattachLog.InteractingAbout = oattachAttribute;   
                  lstAttachmentLogs.add(oattachLog);                   
                  request.CaseInteractingAbout = lstAttachmentLogs;                 
                  request.InteractingWith = oattachAttribute;
                  createServiceRequest.CreateInteractionRequest =  request;
                   
                      
                  ID testId = '500L0000008uOi1IAE';
                  CreateCaseRest_D_HUM.createCaseComment('test comment',testId);
                  system.assertNotEquals(testId, null , 'ID not created');
                    
                  case oCase = new case();
                  oCase.Id = testId;
                  oCase.AccountID = testId;
                  oCase.Interacting_About_Type__c = 'member';
                  oCase.Interacting_With__c = testId;
                  oCase.Interacting_With_Type__c = 'SSN';   
                                 
            
            
            Test.StartTest();  
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(createServiceRequest);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CreateInteractionInfo/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            HUMInteractionWithCaseServiceRest.createInteractionInfo();
            RestResponse response = RestContext.response;
            CreateInteractionResponse_DTO_HUM caseResponse = new CreateInteractionResponse_DTO_HUM();
            caseResponse = (CreateInteractionResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CreateInteractionResponse_DTO_HUM.class);
           //system.assertNotEquals(caseResponse ,NULL);            
            
            CreateInteractionResponse_DTO_HUM response1= CreateInteractionWithCaseRest_D_HUM.createInteraction(createServiceRequest);  
            //system.assertNotEquals(response1,NULL);          
            
            Test.StopTest();
        }
		
    }
    
    /**
    * This is a test method for Update Interaction with Case  Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateInteraction()
    {
        
       HUMTestDataHelperTest.getHUMConstantsData();       
       User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User2' LIMIT 1];
       system.assertEquals(oUser.lastname == 'test User2',true);    
        System.runAs(oUser) 
        {
      UpdateInteractionRequest_DTO_HUM owrap = new UpdateInteractionRequest_DTO_HUM();
          UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest intreq = new UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest ();
          UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintaboutv = new UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout();
          UpdateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new UpdateInteractionRequest_DTO_HUM.CreateCaseDetails();
          UpdateInteractionRequest_DTO_HUM.CaseInteraction ocaseInteraction= new UpdateInteractionRequest_DTO_HUM.CaseInteraction();
          list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout>();
          CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
          intabout.AddressLine1='street 1';
          intabout.City = 'Test City';
          intabout.DOB = '01/01/1990';
          intabout.EmailID='test2';
          intabout.FirstName = 'test2';
          intabout.HomePhone = '11111';
          intabout.ID = '360306641';
          intabout.IDType='Member-Id-Base';
          intabout.MemberIdDependentCode='00';
          intabout.LastName = 'method2';
          intabout.State = 'teststate';
          intabout.EntityType='Member';
          intabout.ZipCode='12345'; 
          caseintaboutv.CaseIdentifier='122';
          caseintaboutv.InteractingAbout=intabout;
          intlist.add(caseintaboutv );
          list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
          list<UpdateInteractionRequest_DTO_HUM.CaseInteraction> lstCaseInteraction = new list<UpdateInteractionRequest_DTO_HUM.CaseInteraction>();  
          intreq.SourceApp='';
          intreq.SourceIdTypeCd='';
          intreq.InteractionNumber='2000011676520';
          intreq.RequiredCasesInResponse=true;
          intreq.InteractionOrigin='Watson voice';
          intreq.CreatedByQueue='';
          intreq.CaseInteractingAbout=intlist;
          intreq.CreateCaseDetails= lstcasedetails;
          intreq.CaseInteractions= lstCaseInteraction ;
          owrap.UpdateInteractionRequest =  intreq;
           Test.StartTest();  
                        
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CreateInteractionInfo/*';  //Request URL
            req.httpMethod = 'PATCH';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            HUMInteractionWithCaseServiceRest.updateInteractionInfo();
            RestResponse response = RestContext.response;
            UpdateInteractionResponse_DTO_HUM  ouptResponse = new UpdateInteractionResponse_DTO_HUM ();
            ouptResponse = (UpdateInteractionResponse_DTO_HUM )System.JSON.deserialize(response.responseBody.toString(), UpdateInteractionResponse_DTO_HUM .class);
             
            UpdateInteractionResponse_DTO_HUM  response1= UpdateInteractionWithCaseRest_D_HUM.updateInteraction(owrap);  
            
            Test.StopTest();
        }
        
    }
	
	/**
    * This is a test method for GetInteractionInfo Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_GetInteraction()
    {
        
		HUMTestDataHelperTest.getHUMConstantsData();       
		User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User2' LIMIT 1];
		system.assertEquals(oUser.lastname == 'test User2',true);
		    
        system.runAs(oUser) 
        {			
      	  
			Test.StartTest();                         
        	GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
			GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
			reqeuestDTO.InteractionNumber = new list<string>{'2000000095899'};
			reqeuestDTO.InclusionCriteria = new list<string>{'Case','Search Unknown'};
			interactinInfoReq.GetInteractionInfo = reqeuestDTO;
			RestRequest req = new RestRequest();
			String JSONMsg = System.JSON.serialize(interactinInfoReq);
			RestResponse res = new RestResponse();
			req.requestURI = '/CreateInteractionInfo/*';  //Request URL
			req.httpMethod = 'POST';	//HTTP Request Type
			req.requestBody = Blob.valueof(JSONMsg);
			RestContext.request = req;
			RestContext.response= res;    
            HUMInteractionWithCaseServiceRest.getInteractionInfo();
            RestResponse response = RestContext.response;
            GetInteractionInfoResponse_DTO_HUM  ouptResponse = new GetInteractionInfoResponse_DTO_HUM();
            ouptResponse = (GetInteractionInfoResponse_DTO_HUM )System.JSON.deserialize(response.responseBody.toString(), GetInteractionInfoResponse_DTO_HUM.class);                         
            system.assertNotEquals(ouptResponse,null);              
            Test.StopTest();
        }
        
    }
    
}