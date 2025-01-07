/***************************************************************************************************************************************************************************
    Apex Class Name  : HUMCaseServiceRestTest
    Version          : 1.0
    Created Date     : March 14 2018
    Function         : This Rest Webservice is used to for all the case related services.
    ****************************************************************************************************************************************************************************

    Modification Log:

    * Developer Name              Code Review                Date                        Description
    *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    * Moshitha Gunasekaran         215366                    04/07/2018                  Original Version
    * Srvinas Damera               229608                    05/16/2018                  Updated Test case for UpdateCaseInfo
    * Sunil Bukya                  263423                    07/30/2018                  REQ - 371288 - Implemented Account fields platform encryption changes.
    * Moshitha Gunasekaran         292111                    08/09/2018                  To add attachment and attachment attibute details for inserting in database
    * Charan Konakandla            314128                    12/3/2018                   Added Changes for REQ-377179.
    * Sunil Bukya                                            12/26/2018                  Removed Dpass callout code changes
    * Shailesh Mali                                          01/30/2019                  REQ - 380714
    * Santhi Mandava                                         03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
    * Santhi Mandava                                         04/15/2019                  Replaced "Member_Id_Value__c" with Name  
	* Pooja Kumbhar											 04/29/2020					 Checkmarx test method without assert error fix.
    * Vaishnavi Manda                                        05/20/2021                  Updated checkmarx test method
****************************************************************************************************************************************************************************/

@isTest
private class HUMCaseServiceRestTest {
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
                Member_ID__c member13 = new Member_ID__c(Policy__c = listPolicy[1].ID, Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c = '123699', name = '1234', Type__c = 'SSN');
                
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
             
             List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> MedEnrollmentSubTypesList = new List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c>();
             MEDENROLLMENT_CASE_SUBTYPE_HUM__c MedEnrollmentSubTypes = new MEDENROLLMENT_CASE_SUBTYPE_HUM__c(Name = 'Routine' ,SubType__c = 'Routine');
             MedEnrollmentSubTypesList.add(MedEnrollmentSubTypes);        
             insert MedEnrollmentSubTypesList ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null ,  'MedEnrollmentSubTypesLists  not created');
                      
              
             List<HUMCaseInteractionAboutType__c > HUMCaseInteractionAboutTypesList = new List<HUMCaseInteractionAboutType__c>();
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes = new HUMCaseInteractionAboutType__c(Name = 'Unknown Member' ,CaseInteraction_About_Type__c = 'Unknown-Member' );
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes1 = new HUMCaseInteractionAboutType__c(Name = 'Member' ,CaseInteraction_About_Type__c = 'Member' );
                HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes2 = new HUMCaseInteractionAboutType__c(Name = 'Group', CaseInteraction_About_Type__c = 'Group');
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes);
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes1);        
                HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes2);
             insert HUMCaseInteractionAboutTypesList;
             system.assertNotEquals(HUMCaseInteractionAboutTypesList, null ,'HUMCaseInteractionAboutTypesLists  not created');
                        
             Classification__c classfication = new Classification__c(name = 'Chronic Care SNP' ,Is_Active__c = true);
             insert classfication ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null, 'classfication  not created');
            
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

                HUMCaseInteractionAboutType__c objIntAbt = new HUMCaseInteractionAboutType__c();
                objIntAbt.Name = 'Provider';
                objIntAbt.CaseRecordType__c = 'Provider';
                objIntAbt.CaseInteraction_About_Type__c = 'Provider';
                insert objIntAbt;
                System.assertEquals(objIntAbt.id != null, true);

                HUMCaseInteractionAboutType__c objIntAbt1 = new HUMCaseInteractionAboutType__c();
                objIntAbt1.Name = 'Unknown Provider';
                objIntAbt1.CaseRecordType__c = 'Unknown Provider';
                objIntAbt1.CaseInteraction_About_Type__c = 'Unknown-Provider';
                insert objIntAbt1;
                System.assertEquals(objIntAbt1.id != null, true);

                HUMCaseInteractionAboutType__c objIntAbt2 = new HUMCaseInteractionAboutType__c();
                objIntAbt2.Name = 'Agent';
                objIntAbt2.CaseRecordType__c = 'Agent';
                objIntAbt2.CaseInteraction_About_Type__c = 'Agent';
                insert objIntAbt2;
                System.assertEquals(objIntAbt2.id != null, true);

                ConsumerID__c oConsumerId = new ConsumerID__c();
                oConsumerId.Account__c = ProviderAcc.ID;
                oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
                oConsumerId.Consumer_ID__c = '12345';
                oConsumerId.ID_Type__c = 'HumanaId';
                insert oConsumerId;
                System.AssertNotEquals(oConsumerId, NULL);

                HUMSearchLimit__c HumSLVar = new HUMSearchLimit__c();
                HumSLVar.name = 'Member_SearchLimit';
                HumSLVar.Search_Limit__c = 101;
                insert HumSLVar;
                system.AssertNotEquals(HumSLVar, NULL);

                Endpoint_Url_HUM__c EndVar = new Endpoint_Url_HUM__c();
                EndVar.Name = 'CipherCloudUrl';
                EndVar.Remote_Url__c = 'https://ws.dev-crms.humana.com/ws';
                insert EndVar;
                system.AssertNotEquals(EndVar, NULL);
            }
    }
    
    /**
    * This is a test method for Get Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_GetCase()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User2',true);
        
        System.runAs(oUser) 
        {
            HUMTestDataHelperTest.getHUMConstantsData();
            insert new HUMCipherCloudSettings__c( Name = 'APPLICATION_NAME' , Value__c = 'TEST');
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            CaseServiceRestGetRequest_DTO_HUM request = new CaseServiceRestGetRequest_DTO_HUM();
            CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO getcaseInfoRequest = new CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO();
            CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO getcaseRequest = new CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO();
            List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> lstInquireAboutCriteria = new List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO>();
            getcaseRequest.CaseId = objCase.CaseNumber;
            getcaseInfoRequest.CaseInfoRequest = getcaseRequest;
            getcaseInfoRequest.getCaseInfo = true;
            getcaseInfoRequest.IntAboutCaseAvailabilityCheck = lstInquireAboutCriteria;
            request.GetCaseInfo = getcaseInfoRequest;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseServiceRest/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMCaseServiceRest.getCaseInfo();
            
            CaseServiceRestGetRequest_DTO_HUM request1 = new CaseServiceRestGetRequest_DTO_HUM();
            CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO getcaseInfoRequest1 = new CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO();
            CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO getcaseRequest1 = new CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO();
            List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> lstInquireAboutCriteria1 = new List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO>();
            CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO entityIdentifierRequest = new CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO();
            entityIdentifierRequest.EntityType = 'Member';
            entityIdentifierRequest.IdValue ='223366105';
            lstInquireAboutCriteria1.add(entityIdentifierRequest);
            getcaseInfoRequest1.CaseInfoRequest = getcaseRequest1;
            getcaseInfoRequest1.getCaseInfo = false;
            getcaseInfoRequest1.IntAboutCaseAvailabilityCheck = lstInquireAboutCriteria1;
            request1.GetCaseInfo = getcaseInfoRequest1;
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(request1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/CaseServiceRest/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            HUMCaseServiceRest.getCaseInfo();
            
            CaseServiceRestGetRequest_DTO_HUM request2 = new CaseServiceRestGetRequest_DTO_HUM();
            CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO getcaseInfoRequest2 = new CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO();
            CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO getcaseRequest2 = new CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO();
            List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> lstInquireAboutCriteria2 = new List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO>();
            getcaseRequest2.CaseId = '675368';
            getcaseRequest2.InteractionId = '123456';
            getcaseInfoRequest2.CaseInfoRequest = getcaseRequest2;
            getcaseInfoRequest2.getCaseInfo = true;
            getcaseInfoRequest2.IntAboutCaseAvailabilityCheck = lstInquireAboutCriteria2;
            request2.GetCaseInfo = getcaseInfoRequest2;
            
            CaseServiceRestGetRequest_DTO_HUM request3 = new CaseServiceRestGetRequest_DTO_HUM();
            CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO getcaseInfoRequest3 = new CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO();
            CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO getcaseRequest3 = new CaseServiceRestGetRequest_DTO_HUM.CaseInfoRequestDTO();
            List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO> lstInquireAboutCriteria3 = new List<CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO>();
            CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO entityIdentifierRequest1 = new CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO();
            entityIdentifierRequest1.EntityType = 'Member';
            entityIdentifierRequest1.IdValue ='223366105';
            lstInquireAboutCriteria3.add(entityIdentifierRequest1);
            entityIdentifierRequest1 = new CaseServiceRestGetRequest_DTO_HUM.EntityIdentifierDTO();
            entityIdentifierRequest1.EntityType = 'Member';
            entityIdentifierRequest1.IdValue ='223300104';
            lstInquireAboutCriteria3.add(entityIdentifierRequest1);
            getcaseRequest3.InquireAboutCriteria = lstInquireAboutCriteria3;
            getcaseRequest3.InquireForCriteria = lstInquireAboutCriteria3;
            getcaseRequest3.StartDate = '01/01/2000';
            getcaseRequest3.EndDate = '04/12/2018';
            getcaseRequest3.CaseId = '';
            getcaseInfoRequest3.CaseInfoRequest = getcaseRequest3;
            getcaseInfoRequest3.getCaseInfo = true;
            getcaseInfoRequest3.IntAboutCaseAvailabilityCheck = lstInquireAboutCriteria3;
            request3.GetCaseInfo = getcaseInfoRequest3;
            
            Interaction__c objInteraction = [Select Id, Caller_Name__c ,Interaction_Origin__C from Interaction__c Where Caller_Name__c = 'Test' limit 1];
            System.assertNotEquals(objInteraction, null);
            List<String> lstInteractions = new List<String>();
            lstInteractions.Add(objInteraction.Id);
                        
            CaseServiceRestGetResponse_DTO_HUM response = GetCaseInfoRest_D_HUM.getCaseRelatedInfo(null);
            system.assertNotEquals(response ,NULL); 
            CaseServiceRestGetResponse_DTO_HUM response1 = GetCaseInfoRest_D_HUM.getCaseRelatedInfo(request2);
            system.assertNotEquals(response1,NULL); 
            CaseServiceRestGetResponse_DTO_HUM response2 = GetCaseInfoRest_D_HUM.getCaseRelatedInfo(request3);
            system.assertNotEquals(response2,NULL);
            List<String> lstCases = new List<String>();
            lstCases.add(objCase.CaseNumber);
            List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> response3 = GetCaseInfoRest_D_HUM.getInfoResults(lstCases,null,null);
            system.assertNotEquals(response3 ,NULL); 
            Boolean checkCases = GetCaseInfoRest_D_HUM.checkInquiredAboutCases(request1);
           // system.assertNotEquals(checkCases ,false); 
            Boolean checkCases1 = GetCaseInfoRest_D_HUM.checkInquiredAboutCases(request3);
            system.assertNotEquals(checkCases1 ,false);
            List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> response4 = GetCaseInfoRest_D_HUM.populateCaseInfoCase();
            system.assertNotEquals(response4 ,NULL);
            List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> response5 = GetCaseInfoRest_D_HUM.populateCaseInfoInteractionID();
            system.assertNotEquals(response5 ,NULL);
            List<CaseServiceRestGetResponse_DTO_HUM.CaseInfoResponseDTO> response6 = GetCaseInfoRest_D_HUM.populateCriteriaWithNoData();
            system.assertNotEquals(response6 ,NULL);
            map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentDTO>> response8 = GetCaseInfoRest_D_HUM.getAttachmentDTO(lstCases);
            system.assertNotEquals(response8 ,NULL);
            map<ID,List<CaseServiceRestGetResponse_DTO_HUM.AttachmentLogDTO>> response9 = GetCaseInfoRest_D_HUM.getAttachmentLogItemRecord(lstCases);
            system.assertNotEquals(response9 ,NULL);
            map<ID,CaseServiceRestGetResponse_DTO_HUM.InteractionDTO> response10 = GetCaseInfoRest_D_HUM.getInteractionLogDTO(lstInteractions);
            system.assertNotEquals(response10 ,NULL);
            map<ID,List<CaseServiceRestGetResponse_DTO_HUM.TaskDTO>> response11 = GetCaseInfoRest_D_HUM.getTaskDTO(lstCases);
            system.assertNotEquals(response11 ,NULL);
            
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Get Case Info Service -HUMCaseService Negative Scenarios
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_GetCase_Negative()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id ,lastname FROM User  WHERE Lastname='test User2' LIMIT 1];
        system.assertEquals(oUser.lastname=='test User2',true);
        
        System.runAs(oUser) 
        {
           CaseServiceRestGetRequest_DTO_HUM request = new CaseServiceRestGetRequest_DTO_HUM();
            CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO getcaseInfoRequest = new CaseServiceRestGetRequest_DTO_HUM.GetCaseInfoDTO();
            getcaseInfoRequest.CaseInfoRequest = null;
            getcaseInfoRequest.getCaseInfo = null;
            getcaseInfoRequest.IntAboutCaseAvailabilityCheck = null;
            request.GetCaseInfo = getcaseInfoRequest;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseServiceRest/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMCaseServiceRest.getCaseInfo();
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCase()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User2' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User2',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
            String caseStatus = 'O';
            
            CaseServiceRestUpdateRequest_DTO_HUM request = new CaseServiceRestUpdateRequest_DTO_HUM();
            CaseServiceRestUpdateRequest_DTO_HUM.UpdateCaseInfoDTO updateCaseInfoRequest = new CaseServiceRestUpdateRequest_DTO_HUM.UpdateCaseInfoDTO();
            CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO updateCaseRequest = new CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO();
            
            updateCaseRequest.CaseNumber = caseNumber;
            updateCaseRequest.CaseStatus = caseStatus;
            updateCaseRequest.Comment = ' New Comment';
            updateCaseRequest.OwnerId = oUser.Id;
            
            List<CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO> listAttachmentDTO = new List<CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO>();
            
            CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO attachmentDTO1 = new CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO();
            attachmentDTO1.FileName = 'New Attachment';
            attachmentDTO1.AttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);
            
            CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO attachmentDTO2 = new CaseServiceRestUpdateRequest_DTO_HUM.lstAttachmentDTO();
            listAttachmentDTO.add(attachmentDTO2);
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentDTO>  logAttachemntRequest = new List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentDTO>();
            CaseServiceRestUpdateRequest_DTO_HUM.AttachmentDTO  logAttachemntRequest1 = new CaseServiceRestUpdateRequest_DTO_HUM.AttachmentDTO();
            
            List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO> listAttachemntAttribute = new List<CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO>();
            
            CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO attachemntAttribute1 = new CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO attachemntAttribute2 = new CaseServiceRestUpdateRequest_DTO_HUM.AttachmentAttributeDTO();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            updateCaseInfoRequest.CaseUpdateRequest = updateCaseRequest;
            request.UpdateCaseInfo = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseServiceRest/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            HUMCaseServiceRest.updateCaseInfo();
            RestResponse response = RestContext.response;
            CaseServiceRestUpdateResponse_DTO_HUM caseResponse = new CaseServiceRestUpdateResponse_DTO_HUM();
            caseResponse = (CaseServiceRestUpdateResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CaseServiceRestUpdateResponse_DTO_HUM.class);
            system.assert(caseResponse.UpdateCaseInfoResponse.Status == true);
            
            CaseServiceRestUpdateRequest_DTO_HUM request1 = new CaseServiceRestUpdateRequest_DTO_HUM();
            CaseServiceRestUpdateRequest_DTO_HUM.UpdateCaseInfoDTO updateCaseInfoRequest1 = new CaseServiceRestUpdateRequest_DTO_HUM.UpdateCaseInfoDTO();
            CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO updateCaseRequest1 = new CaseServiceRestUpdateRequest_DTO_HUM.CaseUpdateRequestDTO();
            
            updateCaseRequest1.CaseStatus = caseStatus;
            updateCaseRequest1.Comment = ' New Comment';
            updateCaseRequest1.OwnerId = oUser.Id;
            updateCaseInfoRequest1.CaseUpdateRequest = updateCaseRequest1;
            request1.UpdateCaseInfo = updateCaseInfoRequest1;
            
            CaseServiceRestUpdateResponse_DTO_HUM caseResponse1 =UpdateCaseInfoRest_D_HUM.updateCaseAndAttachment(request1);
            system.assertnotEquals(caseResponse1,null);
            User objuser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');

                Database.SaveResult objResult = Database.insert(objuser, false);

            String error = UpdateCaseInfoRest_D_HUM.errorMessage(objResult);
            system.assertnotEquals(error,'');
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Create Case  Service -HUMCaseService 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_CreateCase()
    {
        
       HUMTestDataHelperTest.getHUMConstantsData();       
       User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User2' LIMIT 1];
       system.assertEquals(oUser.lastname == 'test User2',true);
               
        System.runAs(oUser) 
        {
                        
             CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM ();
            
             CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
             
             
                  request.AsynchronousProcess = true;
                  request.SourceApp ='AE';
                  request.SourceKey = 'test';
                  request.InteractingAboutId = '002935049';
                  request.InteractingAboutIdType = 'MedicareID';
                  request.InteractingAboutType = 'Member';
                  request.InteractingAboutFirstName = 'test3';
                  request.InteractingAboutLastName = 'method3';
                  request.InteractingAboutDOB = '01/01/1990';
                  request.InteractingWithId = '002935049';
                  request.InteractingWithIdType ='MedicareID';
                  request.InteractingWithType = 'Member';
                  request.InteractingWithFirstName = 'test3';
                  request.InteractingWithLastName = 'method3';
                  request.InteractingWithDOB = '01/01/1990';
                  request.Origin = 'AEDUASNP';
                  request.CaseType = 'Customer Inquiry';
                  request.CaseSubType = 'Routine';
                  request.CaseStatus = 'Closed';
                  request.CreatedQueue = 'Louisville RSO Enrollment';
                  request.OwnerQueue = 'Louisville RSO Enrollment';
                
                  request.ServiceCenter = 'testcenter';
                  request.Department = 'Retail Service Operations (RSO) Enrollment';
                  request.ClassificationType = 'Enrollment (RSO)';
                  request.Priority = 'Normal';
                  request.Complaint = 'test';
                  request.ComplaintReason = 'test';
                  request.ComplaintType = 'test';
                  
                  request.GARightsGiven ='yes' ;
                  request.GAReason = 'test';
                  request.FollowupDueDate = '10/01/2017';
                  request.Classification = 'Chronic Care SNP';
                  request.Intent = 'Denied';  
                  request.DocType= 'pdf';
                  request.DCN= 'test';
                  CaseServiceRestCreateRequest_DTO_HUM.CaseCommentsList caseComments = new CaseServiceRestCreateRequest_DTO_HUM.CaseCommentsList();
                  List<String> listCommentDTO = new List<String>();
                  String comment1  = 'New comment';
                  listCommentDTO.add(comment1);  
                  caseComments.CaseComment = listCommentDTO;                          
                  request.CaseComments  = caseComments;           
        
                  List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO> lstAttachmentLogs = new List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO>();
                  CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO oattachLog = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO();
                  oattachLog.AttachmentKey = '3439403403';
                  oattachLog.AttachmentType = 'MRM';
                  oattachLog.AttachmentDescription = 'MRM attachment description';
                  
                  List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO> lstAttachmentAttributes = new List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO>();
                  CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO oattachAttribute = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO();
                   oattachAttribute.AttachmentName = 'RequestId';
                  oattachAttribute.AttachmentValue = '45678';
                  lstAttachmentAttributes.add(oattachAttribute);
                  oattachAttribute = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO();
                   oattachAttribute.AttachmentName = 'DocId';
                  oattachAttribute.AttachmentValue = '453432';
                  lstAttachmentAttributes.add(oattachAttribute);
                  oattachLog.AttachmentAttribute = lstAttachmentAttributes;   
                  lstAttachmentLogs.add(oattachLog);                   
                  request.AttachmentLog = lstAttachmentLogs;                 
                               
                  request.ClosedDate = '10/10/2017';
                  request.Contract = 'test';
                  request.ExternalTrackId = '1234';
                  request.InteractingAboutAddressLine1 = '1100 employers blvd';
                  request.InteractingAboutCity = 'depere';
                  request.InteractingAboutEmailID = 'test@humana.com';
                  request.InteractingAboutHomePhone = '9203375562';
                  request.InteractingAboutState = 'WI';
                  request.InteractingAboutZipCode = '54115';                  
                  request.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request.InteractingWithCity = 'de pere';
                  request.InteractingWithEmailID = 'test@humana.com';
                  request.InteractingWithHomePhone = '9203375562';
                  request.InteractingWithState = 'WI';
                  request.InteractingWithZipCode = '54115';                    
                  request.MbrSignDate = '06/05/2017';
                  request.MemberSSN = '360306641';
                  request.MilestoneId = '123';
                  request.PBP = 'test';
                  request.PendKey = 'test';
                  request.SegmentNumber = 'test';
                  request.SourceIdTypeCd = 'test';  
                  request.TopicName = 'test topic';
                  request.WorkQueueViewName = 'Other';
                  request.FollowupDueDate = '10/1/2017';
                  request.RecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case');
                  createServiceRequest.CreateCaseRequest =  request;
                   
                  //new request
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest1 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request1 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request1.SourceApp ='AE';
                  request1.AsynchronousProcess= true;
                  request1.SourceKey = 'test';
                  request1.InteractingAboutId = '223338104';
                  request1.InteractingAboutIdType = 'SSN';
                  request1.InteractingAboutType = 'Member';
                  request1.InteractingAboutFirstName = 'test5';
                  request1.InteractingAboutLastName = 'method5';
                  request1.InteractingAboutDOB = '01/10/1990';
                  request1.InteractingWithId = '002978049';
                  request1.InteractingWithIdType ='Medicaid-Id';
                  request1.InteractingWithType = 'Member';
                  request1.InteractingWithFirstName = 'test10';
                  request1.InteractingWithLastName = 'method10';
                  request1.InteractingWithDOB = '01/10/1991';
                  request1.Origin = 'AEDUASNP';
                  request1.CaseType = 'Customer Inquiry';
                  request1.CaseSubType = 'Routine';
                  request1.CaseStatus = 'Closed';
                  request1.CreatedQueue = 'Louisville RSO Enrollment';
                  request1.OwnerQueue = 'Louisville RSO Enrollment';
                  request1.ServiceCenter = 'testcenter';
                  request1.Department = 'Retail Service Operations (RSO) Enrollment';
                  request1.ClassificationType = 'Enrollment (RSO)';
                  request1.Priority = 'Normal';
                  request1.Complaint = 'test';
                  request1.ComplaintReason = 'test';
                  request1.ComplaintType = 'test';                  
                  request1.GARightsGiven ='yes' ;
                  request1.GAReason = 'test';
                  request1.FollowupDueDate = '10/01/2017';
                  request1.Classification = 'Chronic Care SNP';
                  request1.Intent = 'Denied';  
                  request1.DocType= 'pdf';
                  request1.DCN= 'test';
                  request1.CaseComments  = caseComments; 
                  request1.Contract = 'test';
                  request1.ExternalTrackId = '1234';
                  request1.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request1.InteractingAboutCity = 'depere';
                  request1.InteractingAboutEmailID = 'test@humana.com';
                  request1.InteractingAboutHomePhone = '9203375562';
                  request1.InteractingAboutState = 'WI';
                  request1.InteractingAboutZipCode = '54115';                  
                  request1.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request1.InteractingWithCity = 'de pere';
                  request1.InteractingWithEmailID = 'test@humana.com';
                  request1.InteractingWithHomePhone = '9203375562';
                  request1.InteractingWithState = 'WI';
                  request1.InteractingWithZipCode = '54115';                    
                  request1.MbrSignDate = '06/05/2017';
                  request1.MemberSSN = '567234444';
                  request1.MilestoneId = '123';
                  request1.PBP = 'test';
                  request1.PendKey = 'test';
                  request1.SegmentNumber = 'test';
                  request1.SourceIdTypeCd = 'test';  
                  request1.TopicName = 'test topic';
                  request1.WorkQueueViewName = 'Other';
                  createServiceRequest1.CreateCaseRequest =  request1;
                  //new request
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest2 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request2 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request2.SourceApp ='AE';
                  request2.AsynchronousProcess= true;
                  request2.SourceKey = 'test';
                  request2.InteractingAboutId = '223366104';
                  request2.InteractingAboutIdType = 'EnterprisePersonID';
                  request2.InteractingAboutType = 'Member';
                  request2.InteractingAboutFirstName = 'test5';
                  request2.InteractingAboutLastName = 'method5';
                  request2.InteractingAboutDOB = '01/01/1990';
                  request2.InteractingWithId = 'H46738930';
                  request2.InteractingWithIdType ='Member-Id-Base';
                  request2.InteractingWithType = 'Member';
                  request2.InteractingWithFirstName = 'test55';
                  request2.InteractingWithLastName = 'method55';
                  request2.InteractingWithDOB = '01/01/1990';
                  request2.Origin = 'AEDUASNP';
                  request2.CaseType = 'Customer Inquiry';
                  request2.CaseSubType = 'Routine';
                  request2.CaseStatus = 'Closed';
                  request2.CreatedQueue = 'Louisville RSO Enrollment';
                  request2.OwnerQueue = 'Louisville RSO Enrollment';                
                  request2.ServiceCenter = 'testcenter';
                  request2.Department = 'Retail Service Operations (RSO) Enrollment';
                  request2.ClassificationType = 'Enrollment (RSO)';
                  request2.Priority = 'Normal';
                  request2.Complaint = 'test';
                  request2.ComplaintReason = 'test';
                  request2.ComplaintType = 'test';                  
                  request2.GARightsGiven ='yes' ;
                  request2.GAReason = 'test';
                  request2.FollowupDueDate = '10/01/2017';
                  request2.Classification = 'Chronic Care SNP';
                  request2.Intent = 'Denied';  
                  request2.DocType= 'pdf';
                  request2.DCN= 'test';                
                                     
                  request2.CaseComments  = caseComments; 
                  request2.Contract = 'test';
                  request2.ExternalTrackId = '1234';
                  request2.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request2.InteractingAboutCity = 'depere';
                  request2.InteractingAboutEmailID = 'test@humana.com';
                  request2.InteractingAboutHomePhone = '9203375562';
                  request2.InteractingAboutState = 'WI';
                  request2.InteractingAboutZipCode = '54115';                  
                  request2.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request2.InteractingWithCity = 'de pere';
                  request2.InteractingWithEmailID = 'test@humana.com';
                  request2.InteractingWithHomePhone = '9203375562';
                  request2.InteractingWithState = 'WI';
                  request2.InteractingWithZipCode = '54115';                    
                  request2.MbrSignDate = '06/05/2017';
                  request2.MemberSSN = '567234444';
                  request2.MilestoneId = '123';
                  request2.PBP = 'test';
                  request2.PendKey = 'test';
                  request2.SegmentNumber = 'test';
                  request2.SourceIdTypeCd = 'test';  
                  request2.TopicName = 'test topic';
                  request2.WorkQueueViewName = 'Other';
                  createServiceRequest2.CreateCaseRequest =  request2;
                  
                  //new request
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest3 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request3 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request3.SourceApp ='AE';
                  request3.AsynchronousProcess= true;
                  request3.SourceKey = 'test';
                  request3.InteractingAboutId = '223366104';
                  request3.InteractingAboutIdType = 'Medicaid-Id';
                  request3.InteractingAboutType = 'Member';
                  request3.InteractingAboutFirstName = 'test5';
                  request3.InteractingAboutLastName = 'method5';
                  request3.InteractingAboutDOB = '01/01/1990';
                  request3.InteractingWithId = '223355104';
                  request3.InteractingWithIdType ='SSN';
                  request3.InteractingWithType = 'Member';
                  request3.InteractingWithFirstName = 'test55';
                  request3.InteractingWithLastName = 'method55';
                  request3.InteractingWithDOB = '01/01/1990';
                  request3.Origin = 'AEDUASNP';
                  request3.CaseType = 'Customer Inquiry';
                  request3.CaseSubType = 'Routine';
                  request3.CaseStatus = 'Closed';
                  request3.CreatedQueue = 'Louisville RSO Enrollment';
                  request3.OwnerQueue = 'Louisville RSO Enrollment';                
                  request3.ServiceCenter = 'testcenter';
                  request3.Department = 'Retail Service Operations (RSO) Enrollment';
                  request3.ClassificationType = 'Enrollment (RSO)';
                  request3.Priority = 'Normal';
                  request3.Complaint = 'test';
                  request3.ComplaintReason = 'test';
                  request3.ComplaintType = 'test';                  
                  request3.GARightsGiven ='yes' ;
                  request3.GAReason = 'test';
                  request3.FollowupDueDate = '10/01/2017';
                  request3.Classification = 'Chronic Care SNP';
                  request3.Intent = 'Denied';  
                  request3.DocType= 'pdf';
                  request3.DCN= 'test';                
                                     
                  request3.CaseComments  = caseComments; 
                  request3.Contract = 'test';
                  request3.ExternalTrackId = '1234';
                  request3.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request3.InteractingAboutCity = 'depere';
                  request3.InteractingAboutEmailID = 'test@humana.com';
                  request3.InteractingAboutHomePhone = '9203375562';
                  request3.InteractingAboutState = 'WI';
                  request3.InteractingAboutZipCode = '54115';                  
                  request3.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request3.InteractingWithCity = 'de pere';
                  request3.InteractingWithEmailID = 'test@humana.com';
                  request3.InteractingWithHomePhone = '9203375562';
                  request3.InteractingWithState = 'WI';
                  request3.InteractingWithZipCode = '54115';                    
                  request3.MbrSignDate = '06/05/2017';
                  request3.MemberSSN = '567234444';
                  request3.MilestoneId = '123';
                  request3.PBP = 'test';
                  request3.PendKey = 'test';
                  request3.SegmentNumber = 'test';
                  request3.SourceIdTypeCd = 'test';  
                  request3.TopicName = 'test topic';
                  request3.WorkQueueViewName = 'Other';
                  createServiceRequest3.CreateCaseRequest =  request3;
                  
                  //new request
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest4 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request4 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request4.SourceApp ='AE';
                  request4.AsynchronousProcess= true;
                  request4.SourceKey = 'test';
                  request4.InteractingAboutId = '223369104';
                  request4.InteractingAboutIdType = 'EnterprisePersonID';
                  request4.InteractingAboutType = 'Member';
                  request4.InteractingAboutFirstName = 'test57';
                  request4.InteractingAboutLastName = 'method57';
                  request4.InteractingAboutDOB = '02/01/1990';
                  request4.InteractingWithId = '002935049';
                  request4.InteractingWithIdType ='MedicareID';
                  request4.InteractingWithType = 'Member';
                  request4.InteractingWithFirstName = 'test65';
                  request4.InteractingWithLastName = 'method65';
                  request4.InteractingWithDOB = '11/01/1990';
                  request4.Origin = 'AEDUASNP';
                  request4.CaseType = 'Customer Inquiry';
                  request4.CaseSubType = 'Routine';
                  request4.CaseStatus = 'Closed';
                  request4.CreatedQueue = 'Louisville RSO Enrollment';
                  request4.OwnerQueue = 'Louisville RSO Enrollment';                
                  request4.ServiceCenter = 'testcenter';
                  request4.Department = 'Retail Service Operations (RSO) Enrollment';
                  request4.ClassificationType = 'Enrollment (RSO)';
                  request4.Priority = 'Normal';
                  request4.Complaint = 'test';
                  request4.ComplaintReason = 'test';
                  request4.ComplaintType = 'test';                  
                  request4.GARightsGiven ='yes' ;
                  request4.GAReason = 'test';
                  request4.FollowupDueDate = '10/01/2017';
                  request4.Classification = 'Chronic Care SNP';
                  request4.Intent = 'Denied';  
                  request4.DocType= 'pdf';
                  request4.DCN= 'test';                
                                     
                  request4.CaseComments  = caseComments; 
                  request4.Contract = 'test';
                  request4.ExternalTrackId = '1234';
                  request4.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request4.InteractingAboutCity = 'depere';
                  request4.InteractingAboutEmailID = 'test@humana.com';
                  request4.InteractingAboutHomePhone = '9203375562';
                  request4.InteractingAboutState = 'WI';
                  request4.InteractingAboutZipCode = '54115';                  
                  request4.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request4.InteractingWithCity = 'de pere';
                  request4.InteractingWithEmailID = 'test@humana.com';
                  request4.InteractingWithHomePhone = '9203375562';
                  request4.InteractingWithState = 'WI';
                  request4.InteractingWithZipCode = '54115';                    
                  request4.MbrSignDate = '06/05/2017';
                  request4.MemberSSN = '567200444';
                  request4.MilestoneId = '123';
                  request4.PBP = 'test';
                  request4.PendKey = 'test';
                  request4.SegmentNumber = 'test';
                  request4.SourceIdTypeCd = 'test';  
                  request4.TopicName = 'test topic';
                  request4.WorkQueueViewName = 'Other';
                  createServiceRequest4.CreateCaseRequest =  request4;
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest5 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request5 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
                  request5.AsynchronousProcess = true;
                  request5.SourceApp ='AE';
                  request5.SourceKey = 'test';
                  request5.InteractingAboutId = '567234224';
                  request5.InteractingAboutIdType = 'MedicareID';
                  request5.InteractingAboutType = 'Member';
                  request5.InteractingAboutFirstName = 'test6';
                  request5.InteractingAboutLastName = 'method6';
                  request5.InteractingAboutDOB = '01/01/1990';
                  request5.InteractingWithId = '002935049';
                  request5.InteractingWithIdType ='MedicareID';
                  request5.InteractingWithType = 'Member';
                  request5.InteractingWithFirstName = 'test3';
                  request5.InteractingWithLastName = 'method3';
                  request5.InteractingWithDOB = '01/01/1990';
                  request5.Origin = 'AEDUASNP';
                  request5.CaseType = 'Customer Inquiry';
                  request5.CaseSubType = 'Routine';
                  request5.CaseStatus = 'Closed';
                  request5.CreatedQueue = 'Louisville RSO Enrollment';
                  request5.OwnerQueue = 'Louisville RSO Enrollment';
                  request5.CaseComments  = caseComments;
                  request5.ServiceCenter = 'testcenter';
                  request5.Department = 'Retail Service Operations (RSO) Enrollment';
                  request5.ClassificationType = 'Enrollment (RSO)';
                  request5.Priority = 'Normal';
                  request5.Complaint = 'test';
                  request5.ComplaintReason = 'test';
                  request5.ComplaintType = 'test';
                  
                  request5.GARightsGiven ='yes' ;
                  request5.GAReason = 'test';
                  request5.FollowupDueDate = '10/01/2017';
                  request5.Classification = 'Chronic Care SNP';
                  request5.Intent = 'Denied';  
                  request5.DocType= 'pdf';
                  request5.DCN= 'test';
                   
                  request5.ClosedDate = '10/10/2017';
                  request5.Contract = 'test';
                  request5.ExternalTrackId = '1234';
                  request5.InteractingAboutAddressLine1 = '1100 employers blvd';
                  request5.InteractingAboutCity = 'depere';
                  request5.InteractingAboutEmailID = 'test@humana.com';
                  request5.InteractingAboutHomePhone = '9203375562';
                  request5.InteractingAboutState = 'WI';
                  request5.InteractingAboutZipCode = '54115';                  
                  request5.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request5.InteractingWithCity = 'de pere';
                  request5.InteractingWithEmailID = 'test@humana.com';
                  request5.InteractingWithHomePhone = '9203375562';
                  request5.InteractingWithState = 'WI';
                  request5.InteractingWithZipCode = '54115';                    
                  request5.MbrSignDate = '06/05/2017';
                  request5.MemberSSN = '360306641';
                  request5.MilestoneId = '123';
                  request5.PBP = 'test';
                  request5.PendKey = 'test';
                  request5.SegmentNumber = 'test';
                  request5.SourceIdTypeCd = 'test';  
                  request5.TopicName = 'test topic';
                  request5.WorkQueueViewName = 'Other';
                  request5.FollowupDueDate = '10/1/2017';
                  request5.RecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case'); 
                  createServiceRequest5.CreateCaseRequest =  request5;
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest6 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request6 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
                     
                  request6.AsynchronousProcess = false;
                  request6.SourceApp ='AE';
                  request6.SourceKey = 'test';
                  request6.InteractingAboutId = '';
                  request6.InteractingAboutIdType = 'Member-Id-Base';
                  request6.InteractingAboutType = 'Member';
                  request6.InteractingAboutFirstName = 'test8';
                  request6.InteractingAboutLastName = 'method8';
                  request6.InteractingAboutDOB = '01/01/1997';
                  request6.InteractingWithId = '';
                  request6.InteractingWithIdType ='Member-Id-Base';
                  request6.InteractingWithType = 'Member';
                  request6.InteractingWithFirstName = 'test8';
                  request6.InteractingWithLastName = 'method8';
                  request6.InteractingWithDOB = '01/01/1997';
                  request6.Origin = 'AEDUASNP';
                  request6.CaseType = 'Customer Inquiry';
                  request6.CaseSubType = 'Routine';
                  request6.CaseStatus = 'Closed';
                  request6.CreatedQueue = 'Louisville RSO Enrollment';
                  request6.OwnerQueue = 'Louisville RSO Enrollment';
                
                  request6.ServiceCenter = 'testcenter';
                  request6.Department = 'Retail Service Operations (RSO) Enrollment';
                  request6.ClassificationType = 'Enrollment (RSO)';
                  request6.Priority = 'Normal';
                  request6.Complaint = 'test';
                  request6.ComplaintReason = 'test';
                  request6.ComplaintType = 'test';
                  
                  request6.GARightsGiven ='yes' ;
                  request6.GAReason = 'test';
                  request6.FollowupDueDate = '10/01/2017';
                  request6.Classification = 'Chronic Care SNP';
                  request6.Intent = 'Denied';  
                  request6.DocType= 'pdf';
                  request6.DCN= 'test';
                                
                  request6.CaseComments  = caseComments;                           
                    
                  request6.ClosedDate = '10/10/2017';
                  request6.Contract = 'test';
                  request6.ExternalTrackId = '1234';
                  request6.InteractingAboutAddressLine1 = '1100 employers blvd';
                  request6.InteractingAboutCity = 'depere';
                  request6.InteractingAboutEmailID = 'test@humana.com';
                  request6.InteractingAboutHomePhone = '9203375562';
                  request6.InteractingAboutState = 'KY';
                  request6.InteractingAboutZipCode = '54115';                  
                  request6.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request6.InteractingWithCity = 'de pere';
                  request6.InteractingWithEmailID = 'test@humana.com';
                  request6.InteractingWithHomePhone = '9203375562';
                  request6.InteractingWithState = 'KY';
                  request6.InteractingWithZipCode = '54115';                    
                  request6.MbrSignDate = '06/05/2017';
                  request6.MemberSSN = '360306641';
                  request6.MilestoneId = '123';
                  request6.PBP = 'test';
                  request6.PendKey = 'test';
                  request6.SegmentNumber = 'test';
                  request6.SourceIdTypeCd = 'test';  
                  request6.TopicName = 'test topic';
                  request6.WorkQueueViewName = 'Other';
                  request6.FollowupDueDate = '10/1/2017';
                  request6.RecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(),'Closed Medicare Case');
                  createServiceRequest6.CreateCaseRequest =  request6;
                  
                  //new request
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest7 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request7 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request7.AsynchronousProcess = true;
                  request7.SourceApp ='AE';
                  request7.SourceKey = 'test';
                  request7.InteractingAboutId = '';
                  request7.InteractingAboutIdType = 'EnterprisePersonID';
                  request7.InteractingAboutType = 'Member';
                  request7.InteractingAboutFirstName = 'test1';
                  request7.InteractingAboutLastName = 'method1';
                  request7.InteractingAboutDOB = '01/01/1990';
                  request7.InteractingWithId = '';
                  request7.InteractingWithIdType ='SSN';
                  request7.InteractingWithType = 'Member';
                  request7.InteractingWithFirstName = 'test1';
                  request7.InteractingWithLastName = 'method1';
                  request7.InteractingWithDOB = '01/01/1990';
                  request7.Origin = 'AEDUASNP';
                  request7.CaseType = 'Customer Inquiry';
                  request7.CaseSubType = 'Routine';
                  request7.CaseStatus = 'Closed';
                  request7.CreatedQueue = 'Louisville RSO Enrollment';
                  request7.OwnerQueue = 'Louisville RSO Enrollment';                
                  request7.ServiceCenter = 'testcenter';
                  request7.Department = 'Retail Service Operations (RSO) Enrollment';
                  request7.ClassificationType = 'Enrollment (RSO)';
                  request7.Priority = 'Normal';
                  request7.Complaint = 'test';
                  request7.ComplaintReason = 'test';
                  request7.ComplaintType = 'test';                  
                  request7.GARightsGiven ='yes' ;
                  request7.GAReason = 'test';
                  request7.FollowupDueDate = '10/01/2017';
                  request7.Classification = 'Chronic Care SNP';
                  request7.Intent = 'Denied';  
                  request7.DocType= 'pdf';
                  request7.DCN= 'test';                
                                     
                  request7.CaseComments  = caseComments; 
                  request7.Contract = 'test';
                  request7.ExternalTrackId = '1234';
                  request7.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request7.InteractingAboutCity = 'depere';
                  request7.InteractingAboutEmailID = 'test@humana.com';
                  request7.InteractingAboutHomePhone = '9203375562';
                  request7.InteractingAboutState = 'WI';
                  request7.InteractingAboutZipCode = '54115';                  
                  request7.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request7.InteractingWithCity = 'de pere';
                  request7.InteractingWithEmailID = 'test@humana.com';
                  request7.InteractingWithHomePhone = '9203375562';
                  request7.InteractingWithState = 'WI';
                  request7.InteractingWithZipCode = '54115';                    
                  request7.MbrSignDate = '06/05/2017';
                  request7.MemberSSN = '567200444';
                  request7.MilestoneId = '123';
                  request7.PBP = 'test';
                  request7.PendKey = 'test';
                  request7.SegmentNumber = 'test';
                  request7.SourceIdTypeCd = 'test';  
                  request7.TopicName = 'test topic';
                  request7.WorkQueueViewName = 'Other'; 
                  createServiceRequest7.CreateCaseRequest =  request7;
                                          
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest8 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request8 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request8.AsynchronousProcess = false;
                  request8.SourceApp ='CORR';
                  request8.SourceKey = 'test';
                  request8.InteractingAboutId = 'H46738930';
                  request8.InteractingAboutIdType = 'Member-Id-Base';
                  request8.InteractingAboutType = 'Member';
                  request8.InteractingAboutFirstName = 'test8';
                  request8.InteractingAboutLastName = 'method8';
                  request8.InteractingAboutDOB = '01/01/1997';
                  request8.InteractingWithId = 'H46738930';
                  request8.InteractingWithIdType ='Member-Id-Base';
                  request8.InteractingWithType = 'Member';
                  request8.InteractingWithFirstName = 'test8';
                  request8.InteractingWithLastName = 'method8';
                  request8.InteractingWithDOB = '01/01/1997';
                  request8.Origin = 'Correspondence';
                  request8.CaseType = 'Correspondence Inquiry';
                  request8.CaseSubType = 'Creditable Coverage';
                  request8.CaseStatus = 'In Progress';
                  request8.CreatedQueue = 'Louisville RSO Enrollment';
                  request8.OwnerQueue = 'Louisville RSO Enrollment';                
                  request8.ServiceCenter = 'testcenter';
                  request8.Department = 'Retail Service Operations (RSO) Enrollment';
                  request8.ClassificationType = 'Enrollment (RSO)';
                  request8.Priority = 'Normal';
                  request8.Complaint = 'test';
                  request8.ComplaintReason = 'test';
                  request8.ComplaintType = 'test';                  
                  request8.GARightsGiven ='yes' ;
                  request8.GAReason = 'test';
                  request8.FollowupDueDate = '10/01/2017';
                  request8.Classification = 'Chronic Care SNP';
                  request8.Intent = 'Denied';  
                  request8.DocType= 'pdf';
                  request8.DCN= 'test';                
                                     
                  request8.CaseComments  = caseComments; 
                  request8.Contract = 'test';
                  request8.ExternalTrackId = '1234';
                  request8.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request8.InteractingAboutCity = 'depere';
                  request8.InteractingAboutEmailID = 'test@humana.com';
                  request8.InteractingAboutHomePhone = '9203375562';
                  request8.InteractingAboutState = 'KY';
                  request8.InteractingAboutZipCode = '54115';                  
                  request8.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request8.InteractingWithCity = 'de pere';
                  request8.InteractingWithEmailID = 'test@humana.com';
                  request8.InteractingWithHomePhone = '9203375562';
                  request8.InteractingWithState = 'WI';
                  request8.InteractingWithZipCode = '54115';                    
                  request8.MbrSignDate = '06/05/2017';
                  request8.MemberSSN = '';
                  request8.MilestoneId = '123';
                  request8.PBP = 'test';
                  request8.PendKey = 'test';
                  request8.SegmentNumber = 'test';
                  request8.SourceIdTypeCd = 'test';  
                  request8.TopicName = 'test topic';
                  request8.WorkQueueViewName = 'Other';
                  createServiceRequest8.CreateCaseRequest =  request8;
                  Map<String,String> mapAccountNamePlainTextValues8 = new Map<String,String>();
                  mapAccountNamePlainTextValues8.Put('WithFirstName',request8.InteractingWithFirstName);
                  mapAccountNamePlainTextValues8.Put('AboutFirstName',request8.InteractingAboutFirstName);
                  mapAccountNamePlainTextValues8.Put('WithLastName',request8.InteractingWithLastName);
                  mapAccountNamePlainTextValues8.Put('AboutLastName',request8.InteractingAboutLastName);
                  
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest9 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request9 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request9.AsynchronousProcess = True;
                  request9.SourceApp ='CORR';
                  request9.SourceKey = 'test';
                  request9.InteractingAboutId = 'H46738930';
                  request9.InteractingAboutIdType = 'Member-Id-Base';
                  request9.InteractingAboutType = 'Member';
                  request9.InteractingAboutFirstName = 'test8';
                  request9.InteractingAboutLastName = 'method8';
                  request9.InteractingAboutDOB = '01/01/1997';
                  request9.InteractingWithId = 'H46738930';
                  request9.InteractingWithIdType ='Member-Id-Base';
                  request9.InteractingWithType = 'Member';
                  request9.InteractingWithFirstName = 'test8';
                  request9.InteractingWithLastName = 'method8';
                  request9.InteractingWithDOB = '01/01/1997';
                  request9.Origin = 'Correspondence';
                  request9.CaseType = 'Correspondence Inquiry';
                  request9.CaseSubType = 'Creditable Coverage';
                  request9.CaseStatus = 'In Progress';
                  request9.CreatedQueue = 'Louisville RSO Enrollment';
                  request9.OwnerQueue = 'Louisville RSO Enrollment';                
                  request9.ServiceCenter = 'testcenter';
                  request9.Department = 'Retail Service Operations (RSO) Enrollment';
                  request9.ClassificationType = 'Enrollment (RSO)';
                  request9.Priority = 'Normal';
                  request9.Complaint = 'test';
                  request9.ComplaintReason = 'test';
                  request9.ComplaintType = 'test';                  
                  request9.GARightsGiven ='yes' ;
                  request9.GAReason = 'test';
                  request9.FollowupDueDate = '10/01/2017';
                  request9.Classification = 'Chronic Care SNP';
                  request9.Intent = 'Denied';  
                  request9.DocType= 'pdf';
                  request9.DCN= 'test';                
                                     
                  request9.CaseComments  = caseComments; 
                  request9.Contract = 'test';
                  request9.ExternalTrackId = '1234';
                  request9.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request9.InteractingAboutCity = 'depere';
                  request9.InteractingAboutEmailID = 'test@humana.com';
                  request9.InteractingAboutHomePhone = '9203375562';
                  request9.InteractingAboutState = 'KK';
                  request9.InteractingAboutZipCode = '54115';                  
                  request9.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request9.InteractingWithCity = 'de pere';
                  request9.InteractingWithEmailID = 'test@humana.com';
                  request9.InteractingWithHomePhone = '9203375562';
                  request9.InteractingWithState = 'KK';
                  request9.InteractingWithZipCode = '54115';                    
                  request9.MbrSignDate = '06/05/2017';
                  request9.MemberSSN = '';
                  request9.MilestoneId = '123';
                  request9.PBP = 'test';
                  request9.PendKey = 'test';
                  request9.SegmentNumber = 'test';
                  request9.SourceIdTypeCd = 'test';  
                  request9.TopicName = 'test topic';
                  request9.WorkQueueViewName = 'Other';
                  createServiceRequest9.CreateCaseRequest =  request9;
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest10 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request10 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
                  request10.AsynchronousProcess = false;
                  request10.SourceApp ='CORR';
                  request10.SourceKey = 'test';
                  request10.InteractingAboutId = 'H46738930';
                  request10.InteractingAboutIdType = 'Member-Id-Base';
                  request10.InteractingAboutType = 'Member';
                  request10.InteractingAboutFirstName = 'test8';
                  request10.InteractingAboutLastName = 'method8';
                  request10.InteractingAboutDOB = '01/01/1997';
                  request10.InteractingWithId = 'H46738930';
                  request10.InteractingWithIdType ='Member-Id-Base';
                  request10.InteractingWithType = 'Member';
                  request10.InteractingWithFirstName = 'test8';
                  request10.InteractingWithLastName = 'method8';
                  request10.InteractingWithDOB = '01/01/1997';
                  request10.Origin = 'Correspondence';
                  request10.CaseType = 'Correspondence Inquiry';
                  request10.CaseSubType = 'Creditable Coverage';
                  request10.CaseStatus = 'In Progress';
                  request10.CreatedQueue = 'Louisville RSO Enrollment';
                  request10.OwnerQueue = 'Louisville RSO Enrollment';                
                  request10.ServiceCenter = 'testcenter';
                  request10.Department = 'Retail Service Operations (RSO) Enrollment';
                  request10.ClassificationType = 'Enrollment (RSO)';
                  request10.Priority = 'Normal';
                  request10.Complaint = 'test';
                  request10.ComplaintReason = 'test';
                  request10.ComplaintType = 'test';                  
                  request10.GARightsGiven ='yes' ;
                  request10.GAReason = 'test';
                  request10.FollowupDueDate = '10/01/2017';
                  request10.Classification = 'Chronic Care SNP';
                  request10.Intent = 'Denied';  
                  request10.DocType= 'pdf';
                  request10.DCN= 'test';                
                                     
                  request10.CaseComments  = caseComments; 
                  request10.Contract = 'test';
                  request10.ExternalTrackId = '1234';
                  request10.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request10.InteractingAboutCity = 'depere';
                  request10.InteractingAboutEmailID = 'test@humana.com';
                  request10.InteractingAboutHomePhone = '9203375562';
                  request10.InteractingAboutState = 'KK';
                  request10.InteractingAboutZipCode = '54115';                  
                  request10.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request10.InteractingWithCity = 'de pere';
                  request10.InteractingWithEmailID = 'test@humana.com';
                  request10.InteractingWithHomePhone = '9203375562';
                  request10.InteractingWithState = 'KK';
                  request10.InteractingWithZipCode = '54115';                    
                  request10.MbrSignDate = '06/05/2017';
                  request10.MemberSSN = '';
                  request10.MilestoneId = '123';
                  request10.PBP = 'test';
                  request10.PendKey = 'test';
                  request10.SegmentNumber = 'test';
                  request10.SourceIdTypeCd = 'test';  
                  request10.TopicName = 'test topic';
                  request10.WorkQueueViewName = 'Other';
                  createServiceRequest10.CreateCaseRequest =  request10;
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest11 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request11 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
                  request11.AsynchronousProcess = false;
                  request11.SourceApp ='CORR';
                  request11.SourceKey = 'test';
                  request11.InteractingAboutId = 'H46938930';
                  request11.InteractingAboutIdType = 'Member-Id-Base';
                  request11.InteractingAboutType = 'Member';
                  request11.InteractingAboutFirstName = 'test8';
                  request11.InteractingAboutLastName = 'method8';
                  request11.InteractingAboutDOB = '01/01/1997';
                  request11.InteractingWithId = '127489378';
                  request11.InteractingWithIdType ='EnterprisePersonID';
                  request11.InteractingWithType = 'Member';
                  request11.InteractingWithFirstName = 'test8';
                  request11.InteractingWithLastName = 'method8';
                  request11.InteractingWithDOB = '01/01/1997';
                  request11.Origin = 'Correspondence';
                  request11.CaseType = 'Correspondence Inquiry';
                  request11.CaseSubType = 'Creditable Coverage';
                  request11.CaseStatus = 'In Progress';
                  request11.CreatedQueue = 'Louisville RSO Enrollment';
                  request11.OwnerQueue = 'Louisville RSO Enrollment';                
                  request11.ServiceCenter = 'testcenter';
                  request11.Department = 'Retail Service Operations (RSO) Enrollment';
                  request11.ClassificationType = 'Enrollment (RSO)';
                  request11.Priority = 'Normal';
                  request11.Complaint = 'test';
                  request11.ComplaintReason = 'test';
                  request11.ComplaintType = 'test';                  
                  request11.GARightsGiven ='yes' ;
                  request11.GAReason = 'test';
                  request11.FollowupDueDate = '10/01/2017';
                  request11.Classification = 'Chronic Care SNP';
                  request11.Intent = 'Denied';  
                  request11.DocType= 'pdf';
                  request11.DCN= 'test';                
                                     
                  request11.CaseComments  = caseComments; 
                  request11.Contract = 'test';
                  request11.ExternalTrackId = '1234';
                  request11.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request11.InteractingAboutCity = 'depere';
                  request11.InteractingAboutEmailID = 'test@humana.com';
                  request11.InteractingAboutHomePhone = '9203375562';
                  request11.InteractingAboutState = 'KK';
                  request11.InteractingAboutZipCode = '54115';                  
                  request11.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request11.InteractingWithCity = 'de pere';
                  request11.InteractingWithEmailID = 'test@humana.com';
                  request11.InteractingWithHomePhone = '9203375562';
                  request11.InteractingWithState = 'KK';
                  request11.InteractingWithZipCode = '54115';                    
                  request11.MbrSignDate = '06/05/2017';
                  request11.MemberSSN = '';
                  request11.MilestoneId = '123';
                  request11.PBP = 'test';
                  request11.PendKey = 'test';
                  request11.SegmentNumber = 'test';
                  request11.SourceIdTypeCd = 'test';  
                  request11.TopicName = 'test topic';
                  request11.WorkQueueViewName = 'Other';
                  createServiceRequest11.CreateCaseRequest =  request11;
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest12 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request12 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request12.SourceApp ='AE';
                  request12.AsynchronousProcess= true;
                  request12.SourceKey = 'test';
                  request12.InteractingAboutId = '';
                  request12.InteractingAboutIdType = 'Member-Id-Base';
                  request12.InteractingAboutType = 'Member';
                  request12.InteractingAboutFirstName = 'Maths';
                  request12.InteractingAboutLastName = 'English';
                  request12.InteractingAboutDOB = '05/01/1980';
                  request12.InteractingWithId = 'H46738930';
                  request12.InteractingWithIdType ='Member-Id-Base';
                  request12.InteractingWithType = 'Member';
                  request12.InteractingWithFirstName = 'test55';
                  request12.InteractingWithLastName = 'method55';
                  request12.InteractingWithDOB = '01/01/1990';
                  request12.Origin = 'AEDUASNP';
                  request12.CaseType = 'Customer Inquiry';
                  request12.CaseSubType = 'Routine';
                  request12.CaseStatus = 'Closed';
                  request12.CreatedQueue = 'Louisville RSO Enrollment';
                  request12.OwnerQueue = 'Louisville RSO Enrollment';                
                  request12.ServiceCenter = 'testcenter';
                  request12.Department = 'Retail Service Operations (RSO) Enrollment';
                  request12.ClassificationType = 'Enrollment (RSO)';
                  request12.Priority = 'Normal';
                  request12.Complaint = 'test';
                  request12.ComplaintReason = 'test';
                  request12.ComplaintType = 'test';                  
                  request12.GARightsGiven ='yes' ;
                  request12.GAReason = 'test';
                  request12.FollowupDueDate = '10/01/2017';
                  request12.Classification = 'Chronic Care SNP';
                  request12.Intent = 'Denied';  
                  request12.DocType= 'pdf';
                  request12.DCN= 'test';                
                                     
                  request12.CaseComments  = caseComments; 
                  request12.Contract = 'test';
                  request12.ExternalTrackId = '1234';
                  request12.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request12.InteractingAboutCity = 'depere';
                  request12.InteractingAboutEmailID = 'test@humana.com';
                  request12.InteractingAboutHomePhone = '9203375562';
                  request12.InteractingAboutState = 'WI';
                  request12.InteractingAboutZipCode = '54115';                  
                  request12.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request12.InteractingWithCity = 'de pere';
                  request12.InteractingWithEmailID = 'test@humana.com';
                  request12.InteractingWithHomePhone = '9203375562';
                  request12.InteractingWithState = 'WI';
                  request12.InteractingWithZipCode = '54115';                    
                  request12.MbrSignDate = '06/05/2017';
                  request12.MemberSSN = '567234444';
                  request12.MilestoneId = '123';
                  request12.PBP = 'test';
                  request12.PendKey = 'test';
                  request12.SegmentNumber = 'test';
                  request12.SourceIdTypeCd = 'test';  
                  request12.TopicName = 'test topic';
                  request12.WorkQueueViewName = 'Other';
                  createServiceRequest12.CreateCaseRequest =  request12;
                  Map<String,String> mapAccountNamePlainTextValues12 = new Map<String,String>();
                  mapAccountNamePlainTextValues12.Put('WithFirstName',request12.InteractingWithFirstName);
                  mapAccountNamePlainTextValues12.Put('AboutFirstName',request12.InteractingAboutFirstName);
                  mapAccountNamePlainTextValues8.Put('WithLastName',request12.InteractingWithLastName);
                  mapAccountNamePlainTextValues8.Put('AboutLastName',request12.InteractingAboutLastName);
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest13 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request13 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request13.SourceApp ='AE';
                  request13.AsynchronousProcess= true;
                  request13.SourceKey = 'test';
                  request13.InteractingAboutId = '223366104';
                  request13.InteractingAboutIdType = 'EnterprisePersonID';
                  request13.InteractingAboutType = 'Member';
                  request13.InteractingAboutFirstName = 'test5';
                  request13.InteractingAboutLastName = 'method5';
                  request13.InteractingAboutDOB = '01/01/1990';
                  request13.InteractingWithId = '';
                  request13.InteractingWithIdType ='Member-Id-Base';
                  request13.InteractingWithType = 'Member';
                  request13.InteractingWithFirstName = 'test55';
                  request13.InteractingWithLastName = 'method55';
                  request13.InteractingWithDOB = '01/01/1990';
                  request13.Origin = 'AEDUASNP';
                  request13.CaseType = 'Customer Inquiry';
                  request13.CaseSubType = 'Routine';
                  request13.CaseStatus = 'Closed';
                  request13.CreatedQueue = 'Louisville RSO Enrollment';
                  request13.OwnerQueue = 'Louisville RSO Enrollment';                
                  request13.ServiceCenter = 'testcenter';
                  request13.Department = 'Retail Service Operations (RSO) Enrollment';
                  request13.ClassificationType = 'Enrollment (RSO)';
                  request13.Priority = 'Normal';
                  request13.Complaint = 'test';
                  request13.ComplaintReason = 'test';
                  request13.ComplaintType = 'test';                  
                  request13.GARightsGiven ='yes' ;
                  request13.GAReason = 'test';
                  request13.FollowupDueDate = '10/01/2017';
                  request13.Classification = 'Chronic Care SNP';
                  request13.Intent = 'Denied';  
                  request13.DocType= 'pdf';
                  request13.DCN= 'test';                
                                     
                  request13.CaseComments  = caseComments; 
                  request13.Contract = 'test';
                  request13.ExternalTrackId = '1234';
                  request13.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request13.InteractingAboutCity = 'depere';
                  request13.InteractingAboutEmailID = 'test@humana.com';
                  request13.InteractingAboutHomePhone = '9203375562';
                  request13.InteractingAboutState = 'WI';
                  request13.InteractingAboutZipCode = '54115';                  
                  request13.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request13.InteractingWithCity = 'de pere';
                  request13.InteractingWithEmailID = 'test@humana.com';
                  request13.InteractingWithHomePhone = '9203375562';
                  request13.InteractingWithState = 'WI';
                  request13.InteractingWithZipCode = '54115';                    
                  request13.MbrSignDate = '06/05/2017';
                  request13.MemberSSN = '567234444';
                  request13.MilestoneId = '123';
                  request13.PBP = 'test';
                  request13.PendKey = 'test';
                  request13.SegmentNumber = 'test';
                  request13.SourceIdTypeCd = 'test';  
                  request13.TopicName = 'test topic';
                  request13.WorkQueueViewName = 'Other';
                  createServiceRequest13.CreateCaseRequest =  request13;                                   
                  
                  CaseServiceRestCreateRequest_DTO_HUM createServiceRequest14 = new CaseServiceRestCreateRequest_DTO_HUM ();
                  CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request14 = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO(); 
                  request14.SourceApp ='AE';
                  request14.AsynchronousProcess= true;
                  request14.SourceKey = 'test';
                  request14.InteractingAboutId = '';
                  request14.InteractingAboutIdType = 'UnknownMember';
                  request14.InteractingAboutType = 'Member';
                  request14.InteractingAboutFirstName = 'test7';
                  request14.InteractingAboutLastName = 'method7';
                  request14.InteractingAboutDOB = '01/01/1997';
                  request14.InteractingWithId = '';
                  request14.InteractingWithIdType ='UnknownMember';
                  request14.InteractingWithType = 'Member';
                  request14.InteractingWithFirstName = 'test7';
                  request14.InteractingWithLastName = 'method7';
                  request14.InteractingWithDOB = '01/01/1997';
                  request14.Origin = 'AEDUASNP';
                  request14.CaseType = 'Customer Inquiry';
                  request14.CaseSubType = 'Routine';
                  request14.CaseStatus = 'Closed';
                  request14.CreatedQueue = 'Louisville RSO Enrollment';
                  request14.OwnerQueue = 'Louisville RSO Enrollment';                
                  request14.ServiceCenter = 'testcenter';
                  request14.Department = 'Retail Service Operations (RSO) Enrollment';
                  request14.ClassificationType = 'Enrollment (RSO)';
                  request14.Priority = 'Normal';
                  request14.Complaint = 'test';
                  request14.ComplaintReason = 'test';
                  request14.ComplaintType = 'test';                  
                  request14.GARightsGiven ='yes' ;
                  request14.GAReason = 'test';
                  request14.FollowupDueDate = '10/01/2017';
                  request14.Classification = 'Chronic Care SNP';
                  request14.Intent = 'Denied';  
                  request14.DocType= 'pdf';
                  request14.DCN= 'test';                
                                     
                  request14.CaseComments  = caseComments; 
                  request14.Contract = 'test';
                  request14.ExternalTrackId = '1234';
                  request14.InteractingAboutAddressLine1 = '1100 employers blvd';                    
                  request14.InteractingAboutCity = 'depere';
                  request14.InteractingAboutEmailID = 'test@humana.com';
                  request14.InteractingAboutHomePhone = '9203375562';
                  request14.InteractingAboutState = 'KY';
                  request14.InteractingAboutZipCode = '54115';                  
                  request14.InteractingWithAddressLine1 = '1100 employers blvd';                    
                  request14.InteractingWithCity = 'de pere';
                  request14.InteractingWithEmailID = 'test@humana.com';
                  request14.InteractingWithHomePhone = '9203375562';
                  request14.InteractingWithState = 'KY';
                  request14.InteractingWithZipCode = '54115';                    
                  request14.MbrSignDate = '06/05/2017';
                  request14.MemberSSN = '567234444';
                  request14.MilestoneId = '123';
                  request14.PBP = 'test';
                  request14.PendKey = 'test';
                  request14.SegmentNumber = 'test';
                  request14.SourceIdTypeCd = 'test';  
                  request14.TopicName = 'test topic';
                  request14.WorkQueueViewName = 'Other';
                  createServiceRequest14.CreateCaseRequest =  request14;
                       
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
        
            req.requestURI = '/CaseServiceRest/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            HUMCaseServiceRest.createCaseInfo();
            RestResponse response = RestContext.response;
            CaseServiceRestCreateResponse_DTO_HUM caseResponse = new CaseServiceRestCreateResponse_DTO_HUM();
            caseResponse = (CaseServiceRestCreateResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
            system.assertNotEquals(caseResponse ,NULL);            
                        
            
            CreateCaseRest_D_HUM.updateCaseAndAccount(createServiceRequest5,testId);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL);   
            
            CreateCaseRest_D_HUM.updateMemberAndPolicy(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL); 
            
            CreateCaseRest_D_HUM.updateCaseDetails(oCase);
            system.assertNotEquals([Select Id from case where id = '500L0000008uOi1IAE' ] ,NULL); 
            
            CreateCaseRest_D_HUM.logError(new List<Database.Error>(),'testmethod','testobject','123');
            system.assertNotEquals([Select method_name__c from error_log__c where method_name__c='testmethod'] ,NULL);             
                        
            CaseServiceRestCreateResponse_DTO_HUM  logResponse =  CreateCaseRest_D_HUM.getErrorLogInstance('00','test error',true);
            system.assertNotEquals(logResponse ,NULL);
            
            CreateCaseRest_D_HUM.createCaseComment('test coment', testId);            
            //system.assertNotEquals([SELECT commentBody   FROM CaseComment   WHERE commentBody ='test coment' ], Null);  
                      
            Map<String,String> memeberResult = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest1,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult ,NULL);
          
            Map<String,String> memeberResult1 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest1,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult1 ,NULL);
          
            Map<String,String> memeberResult2 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest2,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult2 ,NULL);
            
            Map<String,String> memeberResult3 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest2,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult3 ,NULL);                      
            
             Map<String,String> memeberResult4 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest3,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult4 ,NULL);
            
            Map<String,String> memeberResult5 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest3,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult5 ,NULL); 
            
            Map<String,String> memeberResult6 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest4,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult6 ,NULL);
            
            Map<String,String> memeberResult7 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest4,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult7 ,NULL); 
            
            Map<String,String> memeberResult8 = CreateCaseRestAccount_D_HUM.createAccount(createServiceRequest13,'InteractingAboutType','', testId);
            system.assertNotEquals(memeberResult8 ,NULL); 
            
            Map<String,String> memeberResult9 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest7,'InteractingAboutType',testId);
            system.assertNotEquals(memeberResult9 ,NULL);
            
            String MemberId = CreateCaseRest_D_HUM.locateMemberByName(request7.InteractingAboutFirstName, request7.InteractingAboutLastName, request7.InteractingAboutDOB, request7.InteractingAboutState);  
            //system.assertNotEquals(MemberId ,'');
            
            String MemberId1 = CreateCaseRest_D_HUM.locateMemberByName('test7', 'method7', '01/01/1997', 'KY');  
            //system.assertNotEquals(MemberId1 ,'');
            
            CaseServiceRestCreateResponse_DTO_HUM response2= CreateCaseRest_D_HUM.createCase(NULL);  
            system.assertNotEquals(response2,NULL);
            
            CaseServiceRestCreateResponse_DTO_HUM response3 = CreateCaseRest_D_HUM.createCaseRecord(createServiceRequest5);
            system.assertNotEquals(response3 ,NULL);
            
            CaseServiceRestCreateResponse_DTO_HUM response4 = CreateCaseRest_D_HUM.createCaseRecord(null);
            system.assertNotEquals(response4 ,NULL);
            
            Map<String,String> memeberResult10 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest8,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult10 ,NULL); 
            
            Map<String,String> memeberResult11 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest9,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult11 ,NULL); 
            
            Map<String,String> memeberResult12 = CreateCaseRestAccount_D_HUM.createAccount(createServiceRequest12, NULL,'', testId);
            system.assertNotEquals(memeberResult12 ,NULL); 
            
            Map<String,String> memeberResult13 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest6,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult13 ,NULL);
            
            Map<String,String> memeberResult14 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest11,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult14 ,NULL);
            
            Map<String,String> memeberResult15 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest11,'InteractingWithType', testId);
            system.assertNotEquals(memeberResult15 ,NULL);
            
            Map<String,String> memeberResult16 = CreateCaseRest_D_HUM.getMemberAccountMaps(createServiceRequest12,'InteractingAboutType', testId);
            system.assertNotEquals(memeberResult16 ,NULL);
            
            String memberId2 = CreateCaseRest_D_HUM.locateMember('H46738930','Member-Id-Base', '01/01/1997', 'test8', 'method8');
            system.assertNotEquals(memberId2 ,NULL);
            
            CreateCaseRest_D_HUM.updateCaseAndAccount(null, oCase.Id);
            
            ID  recorId = CreateCaseRest_D_HUM.getCaseRecordType(createServiceRequest11, '');
            system.assertNotEquals(recorId ,NULL);
            
            Map<String,String> memeberResult17 = CreateCaseRestAccount_D_HUM.createAccount(createServiceRequest12,'InteractingAboutType','UKM|00|ETL|SFDC|', testId);
            system.assertNotEquals(memeberResult17 ,NULL);            

            Map<String,String> memeberResult18 = CreateCaseRestAccount_D_HUM.createAccount(createServiceRequest14,'InteractingAboutType','UKM|00|ETL|SFDC|', testId);
            system.assertNotEquals(memeberResult18 ,NULL);            
            
            
            CaseServiceRestCreateResponse_DTO_HUM response10= CreateCaseRest_D_HUM.createCase(createServiceRequest10);  
            system.assertNotEquals(response10,NULL);          
            
            RestRequest req1 = new RestRequest();
            String JSONMsg1 = System.JSON.serialize(createServiceRequest1);
            RestResponse res1 = new RestResponse();
        
            req1.requestURI = '/CaseServiceRest/*';  //Request URL
            req1.httpMethod = 'POST';//HTTP Request Type
            req1.requestBody = Blob.valueof(JSONMsg1);
        
            RestContext.request = req1;
            RestContext.response= res1;
            
            HUMCaseServiceRest.createCaseInfo();
            RestResponse response1 = RestContext.response;
            CaseServiceRestCreateResponse_DTO_HUM caseResponse1 = new CaseServiceRestCreateResponse_DTO_HUM();
            caseResponse1 = (CaseServiceRestCreateResponse_DTO_HUM)System.JSON.deserialize(response1.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
            system.assertNotEquals(caseResponse1 ,NULL); 
 
            Test.StopTest();
        }
        
    }
    
      /*Method add for testing create attachment from input*/
          private static testMethod void createAttachmentFromInputTest() {
             
               CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO > lstAttachmentLogs = new List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO > ();
                List <CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO> lstAttachment = new List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO> ();
                CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO oattachLog = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO();
                CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO oattachLog1 = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO();         
               CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO attachment = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO();
               
               attachment.AttachmentUrl = 'New Attachment Url';
               attachment.FileName = 'New Attachment';
               attachment.VersionId = '1';
               attachment.DocId ='12345';
               lstAttachment.add(attachment);

                oattachLog.AttachmentKey = '3439403403';
                oattachLog.AttachmentType = 'MRM';
                oattachLog.AttachmentDescription = 'MRM attachment description';
                oattachLog.CreatedBy='test';
                //oattachLog.VersionId='1';
                //oattachLog.DocId='66585745';
                
                
                 List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO > lstAttachmentAttributes = new List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO > ();
                  List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO > lstAttachmentAttributes1 = new List < CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO > ();
                CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO oattachAttribute = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO();
                CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO oattachAttribute1 = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO();
                oattachAttribute.AttachmentName = 'RequestId';
                oattachAttribute.AttachmentValue = '45678';
                lstAttachmentAttributes1.add(oattachAttribute1);
                oattachAttribute = new CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO();
                oattachAttribute.AttachmentName = 'DocId';
                oattachAttribute.AttachmentValue = '453432';
                lstAttachmentAttributes.add(oattachAttribute);
                oattachLog.AttachmentAttribute = lstAttachmentAttributes;
                lstAttachmentLogs.add(oattachLog);
                
               
                
                lstAttachmentLogs.add(oattachLog1);
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                request.ClosedDate = '10/10/2017';
                request.AttachmentLog = lstAttachmentLogs;
                request.FollowupDueDate = '10/1/2017';
                request.Attachment = lstAttachment;
                request.RecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(), 'Closed Medicare Case');
                createServiceRequest.CreateCaseRequest = request;
                
              case caseid=new case();
              caseid=[select id from case where Interacting_With_Name__c = 'Test Name' limit 1];
              ID testId=caseid.id;
              
               
                Test.StartTest();

                RestRequest req = new RestRequest();
                String JSONMsg = System.JSON.serialize(createServiceRequest);
                RestResponse res = new RestResponse();

                req.requestURI = '/CaseServiceRest/*'; //Request URL
                req.httpMethod = 'POST'; //HTTP Request Type
                req.requestBody = Blob.valueof(JSONMsg);

                RestContext.request = req;
                RestContext.response = res;
                
                 HUMCaseServiceRest.createCaseInfo();
                RestResponse response = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponse = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponse = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(response.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                system.assertNotEquals(caseResponse, NULL);
                
               CreateCaseRest_D_HUM.createAttachmentFromInput(lstAttachmentLogs,testId,createServiceRequest); 
                Test.StopTest();
                
          
          }
        /*Test method for findMatchingMember*/
        private static testMethod void findMatchingMemberTest() {
            
           List < Member_ID__c > listmember = new List < Member_ID__c > ();
          
            Account acc = new Account();
            acc=[select firstname, lastname,Birthdate__c from Account where firstname='test1' limit 1];
            System.assertEquals(acc.firstname, 'test1');
			
            CreateCaseRest_D_HUM.findMatchingMember(listmember,acc.Birthdate__c,acc.firstname,acc.lastname);  
        }
                /**
         * This method is to get DTO Request CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO
         *
         * @param  None      
         * @return  Void
         */
           static testMethod CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO getRequestDTO()
        {   
            CaseServiceRestCreateRequest_DTO_HUM.CaseCommentsList caseComments = new CaseServiceRestCreateRequest_DTO_HUM.CaseCommentsList();
                List < String > listCommentDTO = new List < String > ();
                String comment1 ='New comment';
                listCommentDTO.add(comment1);
                caseComments.CaseComment = listCommentDTO;
                
            CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = new CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO();
                request.AsynchronousProcess = true;
                request.SourceApp = 'AE';
                request.SourceKey = 'test';
                request.InteractingAboutId = '002935049';
                request.InteractingAboutIdType = 'MedicareID';
                request.InteractingAboutType = 'Member';
                request.InteractingAboutFirstName = 'test3';
                request.InteractingAboutLastName = 'method3';
                request.InteractingAboutDOB = '01/01/1990';
                request.InteractingWithId = '002935049';
                request.InteractingWithIdType = 'MedicareID';
                request.InteractingWithType = 'Member';
                request.InteractingWithFirstName = 'test3';
                request.InteractingWithLastName = 'method3';
                request.InteractingWithDOB = '01/01/1990';
                request.Origin = 'AEDUASNP';
                request.CaseType = 'Customer Inquiry';
                request.CaseSubType = 'Routine';
                request.CaseStatus = 'Closed';
                request.CreatedQueue = 'Louisville RSO Enrollment';
                request.OwnerQueue = 'Louisville RSO Enrollment';
                request.ServiceCenter = 'testcenter';
                request.Department = 'Retail Service Operations (RSO) Enrollment';
                request.ClassificationType = 'Enrollment (RSO)';
                request.Priority = 'Normal';
                request.Complaint = 'test';
                request.ComplaintReason = 'test';
                request.ComplaintType = 'test';
                request.GARightsGiven = 'yes';
                request.GAReason = 'test';
                request.FollowupDueDate = '10/01/2017';
                request.Classification = 'Chronic Care SNP';
                request.Intent = 'Denied';
                request.DocType = 'pdf';
                request.DCN = 'test';
                request.CaseComments = caseComments;
                request.Contract = 'test';
                request.ExternalTrackId = '1234';
                request.InteractingAboutAddressLine1 = '1100 employers blvd';
                request.InteractingAboutCity = 'depere';
                request.InteractingAboutEmailID = 'test@humana.com';
                request.InteractingAboutHomePhone = '9203375562';
                request.InteractingAboutState = 'WI';
                request.InteractingAboutZipCode = '54115';
                request.InteractingWithAddressLine1 = '1100 employers blvd';
                request.InteractingWithCity = 'de pere';
                request.InteractingWithEmailID = 'test@humana.com';
                request.InteractingWithHomePhone = '9203375562';
                request.InteractingWithState = 'WI';
                request.InteractingWithZipCode = '54115';
                request.MbrSignDate = '06/05/2017';
                request.MemberSSN = '360306641';
                request.MilestoneId = '123';
                request.PBP = 'test';
                request.PendKey = 'test';
                request.SegmentNumber = 'test';
                request.SourceIdTypeCd = 'test';
                request.TopicName = 'test topic';
                request.WorkQueueViewName = 'Other';
                //request.FollowupDueDate = '10/1/2017';
                //request.RecordTypeID = HUMUtilityHelper.getRecordTypeID(Case.getsobjectType(), 'Closed Medicare Case');
                return request;
        }
        
       
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *
         * @param  None      
         * @return  Void
         */
        static testMethod void testHUMCaseServiceRest_CreateCase2() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = 'H46738930';
                    request.InteractingAboutIdType = 'Member-Id-Base';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = 'H46738930';
                    request.InteractingWithIdType = 'Member-Id-Base';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = 'Correspondence';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';

                createServiceRequest.CreateCaseRequest = request;
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *Create case2
         * @param  None      
         * @return  Void
         */
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = '';
                    request.InteractingAboutIdType = '';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = 'H46738930';
                    request.InteractingWithIdType = 'Member-Id-Base';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';

                createServiceRequest.CreateCaseRequest = request;
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *Create case2
         * @param  None      
         * @return  Void
         */
        // Case2_Neg2
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg2() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = '';
                    request.InteractingAboutIdType = '';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = 'H46738933';
                    request.InteractingWithIdType = 'Member-Id-Base';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';
                
                createServiceRequest.CreateCaseRequest = request;
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        
        
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *Create case2
         * @param  None      
         * @return  Void
         */
        // Case2_Neg3
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg3() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = 'H43738934';
                    request.InteractingAboutIdType = 'Member-Id-Base';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = 'H43738934';
                    request.InteractingWithIdType = 'Member-Id-Base';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';
                
                createServiceRequest.CreateCaseRequest = request;
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *Create case2
         * @param  None      
         * @return  Void
         */
        // Case2_Neg4
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg4() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = '567234444';
                    request.InteractingAboutIdType = 'SSN';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = '567234444';
                    request.InteractingWithIdType = 'SSN';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';
                
                createServiceRequest.CreateCaseRequest = request;
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        /**
         * This is a test method for Create Case  Service -HUMCaseService 
         *Create case2
         * @param  None      
         * @return  Void
         */
        //CreateCase2_neg5
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg5() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    
                    request.InteractingAboutId = '300006641';
                    request.InteractingAboutIdType = 'SSN';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingAboutState = 'KK';
                    
                    request.InteractingWithId = '300006641';
                    request.InteractingWithIdType = 'SSN';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.InteractingWithState = 'KK';
                    
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    
                    request.OwnerQueue = '';
                    request.Priority = '';
                
                createServiceRequest.CreateCaseRequest = request;
                
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
        /**
         * This is a test method for Create Case for Unknown Member
         *Create case2
         * @param  None      
         * @return  Void
         */
        static testMethod void testHUMCaseServiceRest_CreateCase3() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];
            system.assertEquals(EndVar.name == 'CipherCloudUrl', true);
            Case CaseVar = [Select id, Interacting_With__c, Interacting_With_Name__c, Classification_ID__c, Intent_ID__c, Interacting_With_Type__c from Case where Interacting_With_Type__c = 'Unknown Member'
                Limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    
                    request.InteractingAboutId = 'H46738930';
                    request.InteractingAboutIdType = 'Member-Id-Base';
                    request.InteractingAboutType = 'Unknown Member';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingAboutState = 'KK';
                    
                    request.InteractingWithId = 'H46738930';
                    request.InteractingWithIdType = 'Member-Id-Base';
                    request.InteractingWithType = 'Unknown Member';
                    request.InteractingWithFirstName = 'test8';
                    request.InteractingWithLastName = 'method8';
                    request.InteractingWithDOB = '01/01/1997';
                    request.InteractingWithState = 'KK';
                    
                    request.Origin = 'Correspondence';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';

                createServiceRequest.CreateCaseRequest = request;

                
                CreateCaseRest_D_HUM.updateCaseAndAccount(createServiceRequest, CaseVar.id);

                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                //CaseServiceRestCreateResponse_DTO_HUM response2= CreateCaseRest_D_HUM.createCase(null);  

            }
        }
        /**
         * This is a test Method for provider search
         *Create case2
         * @param  None      
         * @return  Void
         */
        private static testMethod void testProviderSearch() {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User1'
                LIMIT 1
            ];
            System.assertEquals(oUser.lastname == 'test User1', true);

            Case testCase = [SELECT ID, Interacting_With_Type__c FROM Case WHERE Interacting_With_Type__c = 'Provider'
                LIMIT 1
            ];
            System.assertNotEquals(testCase.id, null);
            ID testId = testCase.id;
            System.runAs(oUser) {
                //Provider Search testing
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    
                    request.InteractingAboutId = '1234567892';
                    request.InteractingAboutIdType = 'NPIID';
                    request.InteractingAboutType = 'Provider';
                    request.InteractingAboutFirstName = 'test7';
                    request.InteractingAboutLastName = 'method7';
                    request.InteractingAboutDOB = '01/01/1997';
                    
                    request.InteractingWithId = '1234567890';
                    request.InteractingWithIdType = 'NPIID';
                    request.InteractingWithType = 'Provider';
                    request.InteractingWithFirstName = 'test7';
                    request.InteractingWithLastName = 'method7';
                    request.InteractingWithDOB = '01/01/1997';
                    
                    request.InteractingAboutState = 'KY';
                    request.InteractingWithState = 'KY';
                    request.MemberSSN = '567234444';
                createServicerequest.CreateCaseRequest = request;
                
                String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"7982118", "UniqueID":{ "Source":"PIM" }, "ProviderName":' +
                    '{ "Last":"Norton", "First":"Artis", "Degree":"CRNA" }, "DbaName":"Norton, Artis A CRNA", "Classification":"Individual", "Demographics":' +
                    '{ "Addresses":[ { "AddressType":"Billing", "Line1":"DEPT 52519", "Line2":"PO BOX 950123", "City":"Louisville", "State":"KY", ' +
                    '"Zip":"402950123", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"6066725243", "Extension":"" }, { "PhoneType":"Phone", ' +
                    '"PhoneNumber":"2035737137", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2035746428", "Extension":"" } ] }, ' +
                    '{ "AddressType":"Service", "Line1":"3605 Northgate Ct", "Line2":"Ste 101", "City":"New Albany", "State":"IN", "Zip":"471506400", ' +
                    '"Phones":[ { "PhoneType":"Phone", "PhoneNumber":"8129444263", "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 23354", ' +
                    '"Line2":"", "City":"LEXINGTON", "State":"KY", "Zip":"405233354", "Phones":[ ] }, { "AddressType":"Service", "Line1":"225 Abraham Flexner Way",' +
                    ' "Line2":"Ste 700", "City":"Louisville", "State":"KY", "Zip":"402021846", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"5025614263",' +
                    ' "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 950286", "Line2":"", "City":"LOUISVILLE", "State":"KY", "Zip":"402950286",' +
                    ' "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2706866281", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"5025626810", ' +
                    '"Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2708526600", "Extension":"" } ] } ], "DateOfBirth":"1956-02-23", "Gender":"Female" },' +
                    ' "Identifiers":[ { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"610677948" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"611230160" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"273645560" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { ' +
                    '"ID":"611029626" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":' +
                    '[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"273645560" }, { "ID":"611230160" } ] },' +
                    ' { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"371431151" }, { "ID":"611029626" }, { "ID":"611230160" } ] }, ' +
                    '{ "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, ' +
                    '{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1841284874" } ] } ], "Specialties":[ { "Description":"Nurse Anesthetist [PIMS]" } ], ' +
                    '"SourceProviders":[ { "Source":"CAS", "ProviderID":"000001225KD" }, { "Source":"CAS", "ProviderID":"000025864AD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000192008ED" }, { "Source":"CAS", "ProviderID":"000206339TD" }, { "Source":"CAS", "ProviderID":"000211314OD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000217327 D" }, { "Source":"CAS", "ProviderID":"001110565ZD" }, { "Source":"CAS", "ProviderID":"371431151CD" }, { "Source":"MTV",' +
                    ' "ProviderID":"10654338098" }, { "Source":"MTV", "ProviderID":"187710093124" }, { "Source":"NPI", "ProviderID":"1841284874" }, { "Source":"PIM",' +
                    ' "ProviderID":"2045182" }, { "Source":"PIM", "ProviderID":"737548" } ], "Taxonomy":[ { "Code":"367500000X" }, { "Code":"163W00000X" }, ' +
                    '{ "Code":"207L00000X" } ] } ] } }';
                Test.setMock(HttpCalloutMock.class, new CaseServiceRestMock_T_HUM(EPSRes, 200));
                Test.startTest();
                //Testing Provider search
                RestRequest reqProvider = new RestRequest();
                String JSONMsgProvider = System.JSON.serialize(createServicerequest);
                RestResponse resProvider = new RestResponse();

                reqProvider.requestURI = '/CaseServiceRest/*'; //Request URL
                reqProvider.httpMethod = 'POST'; //HTTP Request Type
                reqProvider.requestBody = Blob.valueof(JSONMsgProvider);

                RestContext.request = reqProvider;
                RestContext.response = resProvider;

                HUMCaseServiceRest.createCaseInfo();
                RestResponse responseProvider = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponseProvider = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponseProvider = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(responseProvider.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                System.assertNotEquals(caseResponseProvider, NULL);

                HUMTestDataHelperTest.getHUMConstantsData();
                

                Map < String, String > providerResultMap = CreateCaseRest_D_HUM.getproviderAccountMap(createServicerequest, 'InteractingAboutType', testId);
                //System.assertNotEquals(providerResultMap ,NULL);
                Test.stopTest();
            }
        }
        /**
         * This is a test method for interactingwith = known provider and  interactingabout != known provider 
         *Create case2
         * @param  None      
         * @return  Void
         */
        private static testMethod void testProviderSearch2() {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User1'
                LIMIT 1
            ];
            System.assertEquals(oUser.lastname == 'test User1', true);

            Case testCase = [SELECT ID, Interacting_With_Type__c FROM Case WHERE Interacting_About_Type__c = 'Member'
                LIMIT 1
            ];
            System.assertNotEquals(testCase.id, null);
            ID testId = testCase.id;
            System.runAs(oUser) {
                //Provider Search testing
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    
                    request.InteractingAboutId = '123345';
                    request.InteractingAboutIdType = 'SSN';
                    request.InteractingAboutType = 'Member';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    
                    request.InteractingWithId = '1234567890';
                    request.InteractingWithIdType = 'NPIID';
                    request.InteractingWithType = 'Provider';
                    request.InteractingWithFirstName = 'test7';
                    request.InteractingWithLastName = 'method7';
                    request.InteractingWithDOB = '01/01/1997';
                    
                    request.InteractingAboutState = 'KY';
                    request.InteractingWithState = 'KY';
                    request.MemberSSN = '567234444';
                    Member_Id__c memberacct = [SELECT ID ,Name
                                              FROM Member_Id__c WHERE Name = '1234' LIMIT 1];
                    Id [] fixedSearchResults= new Id[1];
                    fixedSearchResults[0] = memberacct.id;
                    Test.setFixedSearchResults(fixedSearchResults);
                
                createServicerequest.CreateCaseRequest = request;
                String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"7982118", "UniqueID":{ "Source":"PIM" }, "ProviderName":' +
                    '{ "Last":"Norton", "First":"Artis", "Degree":"CRNA" }, "DbaName":"Norton, Artis A CRNA", "Classification":"Individual", "Demographics":' +
                    '{ "Addresses":[ { "AddressType":"Billing", "Line1":"DEPT 52519", "Line2":"PO BOX 950123", "City":"Louisville", "State":"KY", ' +
                    '"Zip":"402950123", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"6066725243", "Extension":"" }, { "PhoneType":"Phone", ' +
                    '"PhoneNumber":"2035737137", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2035746428", "Extension":"" } ] }, ' +
                    '{ "AddressType":"Service", "Line1":"3605 Northgate Ct", "Line2":"Ste 101", "City":"New Albany", "State":"IN", "Zip":"471506400", ' +
                    '"Phones":[ { "PhoneType":"Phone", "PhoneNumber":"8129444263", "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 23354", ' +
                    '"Line2":"", "City":"LEXINGTON", "State":"KY", "Zip":"405233354", "Phones":[ ] }, { "AddressType":"Service", "Line1":"225 Abraham Flexner Way",' +
                    ' "Line2":"Ste 700", "City":"Louisville", "State":"KY", "Zip":"402021846", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"5025614263",' +
                    ' "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 950286", "Line2":"", "City":"LOUISVILLE", "State":"KY", "Zip":"402950286",' +
                    ' "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2706866281", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"5025626810", ' +
                    '"Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2708526600", "Extension":"" } ] } ], "DateOfBirth":"1956-02-23", "Gender":"Female" },' +
                    ' "Identifiers":[ { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"610677948" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"611230160" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"273645560" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { ' +
                    '"ID":"611029626" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":' +
                    '[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"273645560" }, { "ID":"611230160" } ] },' +
                    ' { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"371431151" }, { "ID":"611029626" }, { "ID":"611230160" } ] }, ' +
                    '{ "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, ' +
                    '{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1841284874" } ] } ], "Specialties":[ { "Description":"Nurse Anesthetist [PIMS]" } ], ' +
                    '"SourceProviders":[ { "Source":"CAS", "ProviderID":"000001225KD" }, { "Source":"CAS", "ProviderID":"000025864AD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000192008ED" }, { "Source":"CAS", "ProviderID":"000206339TD" }, { "Source":"CAS", "ProviderID":"000211314OD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000217327 D" }, { "Source":"CAS", "ProviderID":"001110565ZD" }, { "Source":"CAS", "ProviderID":"371431151CD" }, { "Source":"MTV",' +
                    ' "ProviderID":"10654338098" }, { "Source":"MTV", "ProviderID":"187710093124" }, { "Source":"NPI", "ProviderID":"1841284874" }, { "Source":"PIM",' +
                    ' "ProviderID":"2045182" }, { "Source":"PIM", "ProviderID":"737548" } ], "Taxonomy":[ { "Code":"367500000X" }, { "Code":"163W00000X" }, ' +
                    '{ "Code":"207L00000X" } ] } ] } }';
                Test.setMock(HttpCalloutMock.class, new CaseServiceRestMock_T_HUM(EPSRes, 200));
                Test.startTest();
                //Testing Provider search
                RestRequest reqProvider = new RestRequest();
                String JSONMsgProvider = System.JSON.serialize(createServicerequest);
                RestResponse resProvider = new RestResponse();

                reqProvider.requestURI = '/CaseServiceRest/*'; //Request URL
                reqProvider.httpMethod = 'POST'; //HTTP Request Type
                reqProvider.requestBody = Blob.valueof(JSONMsgProvider);

                RestContext.request = reqProvider;
                RestContext.response = resProvider;

                HUMCaseServiceRest.createCaseInfo();
                RestResponse responseProvider = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponseProvider = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponseProvider = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(responseProvider.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                System.assertNotEquals(caseResponseProvider, NULL);

                HUMTestDataHelperTest.getHUMConstantsData();

                Map < String, String > providerResultMap = CreateCaseRest_D_HUM.getproviderAccountMap(createServicerequest, 'InteractingAboutType', testId);
                //System.assertNotEquals(providerResultMap ,NULL);
                Test.stopTest();
            }
        }
        /**
         * This is a test method for unknown Provider
         *Create case2
         * @param  None      
         * @return  Void
         */
        private static testMethod void testUnknownProviderSearch() {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User1'
                LIMIT 1
            ];
            System.assertEquals(oUser.lastname == 'test User1', true);
            HUMSearchLimit__c HumVar = [select name, Search_Limit__c From HUMSearchLimit__c where name = 'Member_SearchLimit'
                limit 1
            ];
            System.assertNotEquals(HumVar.name, null);
            Case testCase = [SELECT ID, Interacting_With_Type__c FROM Case WHERE Interacting_With_Type__c = 'UnknownProvider'
                LIMIT 1
            ];
            System.assertNotEquals(testCase.id, null);
            ID testId = testCase.id;
            System.runAs(oUser) {
                //Provider Search testing
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    
                    request.InteractingAboutId = '1234567892';
                    request.InteractingAboutIdType = 'UnknownProvider';
                    request.InteractingAboutType = 'UnknownProvider';
                    request.InteractingAboutFirstName = 'test7';
                    request.InteractingAboutLastName = 'method7';
                    request.InteractingAboutDOB = '01/01/1998';

                    request.InteractingWithId = '1234567890';
                    request.InteractingWithIdType = 'UnknownProvider';
                    request.InteractingWithType = 'UnknownProvider';
                    request.InteractingWithFirstName = 'test7';
                    request.InteractingWithLastName = 'method7';
                    request.InteractingWithDOB = '01/01/1997';
                    
                    request.InteractingAboutState = 'KY';
                    request.InteractingWithState = 'KY';
                    request.MemberSSN = '567234444';
                    
                createServicerequest.CreateCaseRequest = request;
                String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[ { "EnterpriseID":"7982118", "UniqueID":{ "Source":"PIM" }, "ProviderName":' +
                    '{ "Last":"Norton", "First":"Artis", "Degree":"CRNA" }, "DbaName":"Norton, Artis A CRNA", "Classification":"Individual", "Demographics":' +
                    '{ "Addresses":[ { "AddressType":"Billing", "Line1":"DEPT 52519", "Line2":"PO BOX 950123", "City":"Louisville", "State":"KY", ' +
                    '"Zip":"402950123", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"6066725243", "Extension":"" }, { "PhoneType":"Phone", ' +
                    '"PhoneNumber":"2035737137", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2035746428", "Extension":"" } ] }, ' +
                    '{ "AddressType":"Service", "Line1":"3605 Northgate Ct", "Line2":"Ste 101", "City":"New Albany", "State":"IN", "Zip":"471506400", ' +
                    '"Phones":[ { "PhoneType":"Phone", "PhoneNumber":"8129444263", "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 23354", ' +
                    '"Line2":"", "City":"LEXINGTON", "State":"KY", "Zip":"405233354", "Phones":[ ] }, { "AddressType":"Service", "Line1":"225 Abraham Flexner Way",' +
                    ' "Line2":"Ste 700", "City":"Louisville", "State":"KY", "Zip":"402021846", "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"5025614263",' +
                    ' "Extension":"" } ] }, { "AddressType":"Billing", "Line1":"PO BOX 950286", "Line2":"", "City":"LOUISVILLE", "State":"KY", "Zip":"402950286",' +
                    ' "Phones":[ { "PhoneType":"Phone", "PhoneNumber":"2706866281", "Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"5025626810", ' +
                    '"Extension":"" }, { "PhoneType":"Phone", "PhoneNumber":"2708526600", "Extension":"" } ] } ], "DateOfBirth":"1956-02-23", "Gender":"Female" },' +
                    ' "Identifiers":[ { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"610677948" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"611230160" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"273645560" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { ' +
                    '"ID":"611029626" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ ' +
                    '{ "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":' +
                    '[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"273645560" }, { "ID":"611230160" } ] },' +
                    ' { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"260353034" }, { "ID":"371431151" }, { "ID":"611029626" }, { "ID":"611230160" } ] }, ' +
                    '{ "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, { "IdentifierType":"TaxID", "Identifiers":[ { "ID":"371431151" } ] }, ' +
                    '{ "IdentifierType":"NpiID", "Identifiers":[ { "ID":"1841284874" } ] } ], "Specialties":[ { "Description":"Nurse Anesthetist [PIMS]" } ], ' +
                    '"SourceProviders":[ { "Source":"CAS", "ProviderID":"000001225KD" }, { "Source":"CAS", "ProviderID":"000025864AD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000192008ED" }, { "Source":"CAS", "ProviderID":"000206339TD" }, { "Source":"CAS", "ProviderID":"000211314OD" }, { "Source":"CAS", ' +
                    '"ProviderID":"000217327 D" }, { "Source":"CAS", "ProviderID":"001110565ZD" }, { "Source":"CAS", "ProviderID":"371431151CD" }, { "Source":"MTV",' +
                    ' "ProviderID":"10654338098" }, { "Source":"MTV", "ProviderID":"187710093124" }, { "Source":"NPI", "ProviderID":"1841284874" }, { "Source":"PIM",' +
                    ' "ProviderID":"2045182" }, { "Source":"PIM", "ProviderID":"737548" } ], "Taxonomy":[ { "Code":"367500000X" }, { "Code":"163W00000X" }, ' +
                    '{ "Code":"207L00000X" } ] } ] } }';
                
                Test.startTest();
                //Testing Provider search
                RestRequest reqProvider = new RestRequest();
                String JSONMsgProvider = System.JSON.serialize(createServicerequest);
                RestResponse resProvider = new RestResponse();

                reqProvider.requestURI = '/CaseServiceRest/*'; //Request URL
                reqProvider.httpMethod = 'POST'; //HTTP Request Type
                reqProvider.requestBody = Blob.valueof(JSONMsgProvider);

                RestContext.request = reqProvider;
                RestContext.response = resProvider;

                HUMCaseServiceRest.createCaseInfo();
                RestResponse responseProvider = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponseProvider = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponseProvider = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(responseProvider.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                System.assertNotEquals(caseResponseProvider, NULL);

                HUMTestDataHelperTest.getHUMConstantsData();
                
                Map < String, String > providerResultMap = CreateCaseRest_D_HUM.getproviderAccountMap(createServicerequest, 'InteractingAboutType', testId);
                //System.assertNotEquals(providerResultMap ,NULL);

                Test.stopTest();
            }
        }
        /**
         * This is a test method for Fetch Account Records- Provider Search 
         *Create case2
         * @param  None      
         * @return  Void
         */
        private static testMethod void testfetchAccountRecordsProviderSearch() {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User1'
                LIMIT 1
            ];
            System.assertEquals(oUser.lastname == 'test User1', true);
            ConsumerID__c ConsId = [SELECT ID, Consumer_ID__c From ConsumerID__c where Consumer_ID__c = '555555555'
                LIMIT 1
            ];
            System.assertEquals(ConsId.Consumer_ID__c == '555555555', true);
            Id[] fixedSearchResults = new Id[1];
            fixedSearchResults[0] = ConsId.Id;
            Account ProviderAcc = [SELECT ID, recordTypeId, NPI_ID__c FROM Account WHERE NPI_ID__c = '1234567890'
                LIMIT 1
            ];
            System.assertEquals(ProviderAcc.NPI_ID__c == '1234567890', true);
            Case testCase = [SELECT ID, Interacting_With_Type__c FROM Case WHERE Interacting_With_Type__c = 'Provider'
                LIMIT 1
            ];
            System.assertNotEquals(testCase.id, null);
            ID testId = testCase.id;
            System.runAs(oUser) {
                Test.setFixedSearchResults(fixedSearchResults);
                //Provider Search testing
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    
                    request.InteractingAboutId = '1234567892';
                    request.InteractingAboutIdType = 'NPIID';
                    request.InteractingAboutType = 'Provider';
                    request.InteractingAboutFirstName = 'test7';
                    request.InteractingAboutLastName = 'method7';
                    request.InteractingAboutDOB = '01/01/1997';
                    
                    request.InteractingWithId = '1234567890';
                    request.InteractingWithIdType = 'TaxId';
                    request.InteractingWithType = 'Provider';
                    request.InteractingWithFirstName = 'test7';
                    request.InteractingWithLastName = 'method7';
                    request.InteractingWithDOB = '01/01/1997';
                    
                    request.InteractingAboutState = 'KY';
                    request.InteractingWithState = 'KY';
                    request.MemberSSN = '567234444';
                
                
                
                createServicerequest.CreateCaseRequest = request;
                String EPSRes = '{ "SearchProviderResponse":{ "EnterpriseProviders":[  ] } }';

                Test.setMock(HttpCalloutMock.class, new CaseServiceRestMock_T_HUM(EPSRes, 200));
                Test.startTest();
                //Testing Provider search
                RestRequest reqProvider = new RestRequest();
                String JSONMsgProvider = System.JSON.serialize(createServicerequest);
                RestResponse resProvider = new RestResponse();

                reqProvider.requestURI = '/CaseServiceRest/*'; //Request URL
                reqProvider.httpMethod = 'POST'; //HTTP Request Type
                reqProvider.requestBody = Blob.valueof(JSONMsgProvider);

                RestContext.request = reqProvider;
                RestContext.response = resProvider;

                HUMCaseServiceRest.createCaseInfo();
                RestResponse responseProvider = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponseProvider = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponseProvider = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(responseProvider.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                System.assertNotEquals(caseResponseProvider, NULL);

                HUMTestDataHelperTest.getHUMConstantsData();
                
                Map < String, String > providerResultMap = CreateCaseRest_D_HUM.getproviderAccountMap(createServicerequest, 'InteractingAboutType', testId);
                System.assertNotEquals(providerResultMap ,NULL);

                Test.stopTest();
            }
        }
        /**
         * This is a test method for interactingwith = known provider and  interactingabout != known provider 
         *Create case2
         * @param  None      
         * @return  Void
         */
        private static testMethod void testMemberSearch() {
            HUMTestDataHelperTest.getHUMConstantsData();
            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User1'
                LIMIT 1
            ];
            System.assertEquals(oUser.lastname == 'test User1', true);

            Case testCase = [SELECT ID, Interacting_With_Type__c FROM Case WHERE Interacting_With_Type__c = 'Provider'
                LIMIT 1
            ];
            System.assertNotEquals(testCase.id, null);
            ID testId = testCase.id;
            System.runAs(oUser) {
                //Provider Search testing
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    
                    request.InteractingAboutId = '';
                    request.InteractingAboutIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM;
                    request.InteractingAboutType = 'Member';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = '';
                    request.InteractingAboutDOB = '01/01/1997';
                    
                    request.InteractingWithId = '1234567890';
                    request.InteractingWithIdType = 'NPIID';
                    request.InteractingWithType = 'Provider';
                    request.InteractingWithFirstName = 'test7';
                    request.InteractingWithLastName = 'method7';
                    request.InteractingWithDOB = '01/01/1997';
                    
                    request.InteractingAboutState = 'KY';
                    request.InteractingWithState = 'KY';
                    request.MemberSSN = '567234444';
                    
                
                createServicerequest.CreateCaseRequest = request;
                String EPSRes = '';
                Test.setMock(HttpCalloutMock.class, new CaseServiceRestMock_T_HUM(EPSRes, 200));
                Test.startTest();
                //Testing Provider search
                RestRequest reqProvider = new RestRequest();
                String JSONMsgProvider = System.JSON.serialize(createServicerequest);
                RestResponse resProvider = new RestResponse();

                reqProvider.requestURI = '/CaseServiceRest/*'; //Request URL
                reqProvider.httpMethod = 'POST'; //HTTP Request Type
                reqProvider.requestBody = Blob.valueof(JSONMsgProvider);

                RestContext.request = reqProvider;
                RestContext.response = resProvider;
                Account acct = [SELECT ID FROM Account LIMIT 1];
                System.assert(acct.id!=null);
                 Account acctname=[SELECT FirstName,LastName FROM Account WHERE FirstName='test8' LIMIT 1];
                List<Id> fixedResultId=new List<Id>();
                fixedResultId.add(acctname.id);
                Test.setFixedSearchResults(fixedResultId);
            
                HUMCaseServiceRest.createCaseInfo();
                RestResponse responseProvider = RestContext.response;
                CaseServiceRestCreateResponse_DTO_HUM caseResponseProvider = new CaseServiceRestCreateResponse_DTO_HUM();
                caseResponseProvider = (CaseServiceRestCreateResponse_DTO_HUM) System.JSON.deserialize(responseProvider.responseBody.toString(), CaseServiceRestCreateResponse_DTO_HUM.class);
                System.assertNotEquals(caseResponseProvider, NULL);

                HUMTestDataHelperTest.getHUMConstantsData();

                Map < String, String > providerResultMap = CreateCaseRest_D_HUM.getproviderAccountMap(createServicerequest, 'InteractingAboutType', testId);
                //System.assertNotEquals(providerResultMap ,NULL);
                Test.stopTest();
            }
        }
        static testMethod void testHUMCaseServiceRest_CreateCase2_neg_RP() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = '';
                    request.InteractingAboutIdType = '';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = '';
                    request.InteractingWithIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM;
                    request.InteractingWithFirstName = '';
                    request.InteractingWithLastName = '';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';

                createServiceRequest.CreateCaseRequest = request;
                 Account acctname=[SELECT FirstName,LastName FROM Account WHERE FirstName='test8' LIMIT 1];
                List<Id> fixedResultId=new List<Id>();
                fixedResultId.add(acctname.id);
                Test.setFixedSearchResults(fixedResultId);
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }

         static testMethod void testHUMCaseServiceRest_CreateCase_locateMember() {

            HUMTestDataHelperTest.getHUMConstantsData();
            User oUser = [SELECT Id, lastname FROM User WHERE Lastname ='test User2'
                LIMIT 1
            ];
            system.assertEquals(oUser.lastname == 'test User2', true);
            Endpoint_Url_HUM__c EndVar = [select name, Remote_Url__c from Endpoint_Url_HUM__c where name = 'CipherCloudUrl'
                limit 1
            ];

            System.runAs(oUser) {
                CaseServiceRestCreateRequest_DTO_HUM createServiceRequest = new CaseServiceRestCreateRequest_DTO_HUM();
                //Getting DTO Request
                CaseServiceRestCreateRequest_DTO_HUM.CreateCaseRequestDTO request = getRequestDTO();
                    request.AsynchronousProcess = false;
                    request.SourceApp = 'CORR';
                    request.InteractingAboutId = '';
                    request.InteractingAboutIdType = '';
                    request.InteractingAboutFirstName = 'test8';
                    request.InteractingAboutLastName = 'method8';
                    request.InteractingAboutDOB = '01/01/1997';
                    request.InteractingWithId = '';
                    request.InteractingWithIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM;
                    request.InteractingWithFirstName = '';
                    request.InteractingWithLastName = '';
                    request.InteractingWithDOB = '01/01/1997';
                    request.Origin = '****';
                    request.CaseType = 'Correspondence Inquiry';
                    request.CaseSubType = 'Creditable Coverage';
                    request.CaseStatus = 'In Progress';
                    request.DocType = 'pdf99999';
                    request.InteractingAboutState = 'KK';
                    request.InteractingWithState = 'KK';
                    request.OwnerQueue = '';
                    request.Priority = '';

                createServiceRequest.CreateCaseRequest = request;
                //add
                List<Member_Id__c> memberAcc1list2= [SELECT ID, Name FROM Member_Id__c WHERE Name = '360306641' LIMIT 2];
                Id [] fixedSearchResults3= new Id[2];
                fixedSearchResults3[0] = memberAcc1list2[0].id;
                fixedSearchResults3[1] = memberAcc1list2[1].id;
                Test.setFixedSearchResults(fixedSearchResults3);
                CreateCaseRest_D_HUM.locateMember('H46738930', 'Member-Id-Base', '01/01/1997','test7', 'method7') ;
                
                List<Member_ID__c> listmember = [Select id, Name, Type__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.name,Policy_Member__r.Member__r.firstname,Policy_Member__r.Member__r.lastname, Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonBirthdate from Member_ID__c limit 2];
                CreateCaseRest_D_HUM.findMatchingMember(listmember,'01/01/1990','Test','Data');
                //end
                 Account acctname=[SELECT FirstName,LastName FROM Account WHERE FirstName='test8' LIMIT 1];
                List<Id> fixedResultId=new List<Id>();
                fixedResultId.add(acctname.id);
                Test.setFixedSearchResults(fixedResultId);
                CaseServiceRestCreateResponse_DTO_HUM response = CreateCaseRest_D_HUM.createCase(createServiceRequest);
                system.assertNotEquals(response, NULL);
                CaseServiceRestCreateResponse_DTO_HUM response2 = CreateCaseRest_D_HUM.createCase(null);

            }
        }
    }