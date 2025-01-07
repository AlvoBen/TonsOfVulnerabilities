/****************************************************************************
Apex Class Name  : CreateInteractionRestService_T_HUM
Version          : 1.0 
Created Date     : May 15 2019
Function         : Test class to CreateInteractionRest_S_HUM Apex class.
Modification Log :
  Developer Name          Code Review                Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  Prasanthi Kandula          356491                05/15/2019                   Original Version
  Prasanthi Kandula          357233                05/24/2019                   Added assert statements as part of Security scan
 *******************************************************************************************************************************/

@isTest
public class CreateInteractionRestService_T_HUM
 {
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
            oAcc.Enterprise_ID__c = '1234567895425';
            oAcc.Group_Number__c = '123456';
            oAcc.Source_Platform_Code__c = 'LV';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
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
            
             Interaction_Member__c InteractionMember = new Interaction_Member__c();
             InteractionMember.Interacting_About__c = oAcc.id;
             InteractionMember.Interaction__c = objInteraction.id;
             InteractionMember.Interacting_About_Type__c ='Group';
             insert InteractionMember;
             System.assertNotEquals(InteractionMember, null, 'InteractionMember not created');
            
             Case_Interaction__c caseIntWthCallName = new Case_Interaction__c();
             caseIntWthCallName.Case__C = caseObj.Id;
             caseIntWthCallName.Interaction__c = objInteraction.Id;
             Insert caseIntWthCallName;
             System.assertNotEquals(caseIntWthCallName, null, 'Case-Interaction not created');
            
             Task objTask = HUMTestDataHelperTest.getTaskRecord();
             objTask.WhatId = caseObj.Id;
             objTask.OwnerID = oUser.Id;
             insert objTask;
             System.assertNotEquals(objTask, null, 'Task not created');
             
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
             List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc1 = new Account( Enterprise_Id__c = '223300104', firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc2 = new Account( Enterprise_Id__c = '002935049', firstname = 'test3', lastname = 'method3' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             Account acc3 = new Account( Enterprise_Id__c = '5478451', Agent_ID__c = '5478451', Agent_Type__c = 'Agency', Name = 'test4Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
             Account acc4 = new Account( Enterprise_Id__c = '5454451', Agent_ID__c = '5454451', Agent_Type__c = 'Agency', Name = 'test5Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
             Account acc5 = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
             Account acc6 = new Account( Enterprise_Id__c = '5451254', NPI_ID__c = '5457894561', DBA__c = 'TestProvider1', Name = 'Provider Test1', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
             Account acc7 = new Account( Enterprise_Id__c = '002935050', firstname = 'test4', lastname = 'method4' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             
             lstAccount.Add(acc);
             lstAccount.Add(acc1);
             lstAccount.Add(acc2);
             lstAccount.Add(acc3);
             lstAccount.Add(acc4);
             lstAccount.Add(acc5);
             lstAccount.Add(acc6);
             lstAccount.Add(acc7);
             Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');               
                     
             list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
             Product2 testProduct1 = new Product2(Name ='testprod2', ProductCode ='MED');
             Product2 testProduct2 = new Product2(Name ='testprod3', ProductCode ='MED');
             listProduct.Add(testProduct);
             listProduct.Add(testProduct1);
             listProduct.Add(testProduct2);
             Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
                                      
             list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy1 = new Policy__c(Group_Name__c = lstAccount[1].ID, Major_LOB__c = 'MCD', Product__c = listProduct[1].ID,Policy_Group_Number__c='11111');
             Policy__c testPolicy2 = new Policy__c(Group_Name__c = lstAccount[2].ID, Major_LOB__c = 'MEF',Platform__c = 'LV', Product__c = listProduct[2].ID,Policy_Group_Number__c='123456');
             listPolicy.Add(testPolicy);
             listPolicy.Add(testPolicy1);
             listPolicy.Add(testPolicy2);
             insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
             
             List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
             Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber1 = new Policy_Member__c(Policy__c = listPolicy[1].ID, Member__c = lstAccount[1].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
             Policy_Member__c memeber2 = new Policy_Member__c(Policy__c = listPolicy[2].ID, Member__c = lstAccount[2].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             Policy_Member__c memeber3 = new Policy_Member__c(Policy__c = listPolicy[2].ID, Member__c = lstAccount[7].ID, ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');             
             listPolicyMember.Add(memeber);
             listPolicyMember.Add(memeber1);
             listPolicyMember.Add(memeber2);
             listPolicyMember.Add(memeber3);
             Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
             
             List<Member_ID__c> listmember = new List<Member_ID__c>();
             Member_ID__c member = new Member_ID__c(Policy__c = listPolicy[0].ID,Policy_Member__c = listPolicyMember[0].ID, Member_Card_External_ID__c= '123345' , name = '360306642' , Type__c ='SSN');
             Member_ID__c member1 = new Member_ID__c(Policy__c = listPolicy[1].ID,Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c= '123645' , name = '360306641' , Type__c ='Member-Id-Base',Member_Dependent_Code__c='00');
             Member_ID__c member2 = new Member_ID__c(Policy__c = listPolicy[2].ID,Policy_Member__c = listPolicyMember[2].ID, Member_Card_External_ID__c= '123245' , name = '002935049' , Type__c ='MedicareID');
             Member_ID__c member3 = new Member_ID__c(Policy__c = listPolicy[2].ID,Policy_Member__c = listPolicyMember[3].ID, Member_Card_External_ID__c= '123646' , name = '360306642' , Type__c ='Member-Id-Base',Member_Dependent_Code__c='00');
             
             listmember.Add(member);
             listmember.Add(member1);
             listmember.Add(member2);
             listmember.Add(member3);
              insert listmember;
             system.assertNotEquals(listmember, null , 'Members  not created'); 
             
             ConsumerID__c consumerrec=new ConsumerID__c();
             consumerrec.ID_Type__c='MedicareID';
             consumerrec.Consumer_ID__c='1123456';
             consumerrec.Account__c=lstAccount[0].Id;
             insert consumerrec;
            system.assertNotEquals(consumerrec, null , 'Consumer record  not created');
            
            
            HUMSearchLimit__c HumSLVar = new HUMSearchLimit__c();
            HumSLVar.name = 'Member_SearchLimit';
            HumSLVar.Search_Limit__c = 101;
            insert HumSLVar;
            system.AssertNotEquals(HumSLVar, NULL);
            
            Account ProviderAcc = HUMTestDataHelperTest.getAccount();
            ProviderAcc.Name = 'test account';
            ProviderAcc.Enterprise_ID__c = '123456';
            ProviderAcc.NPI_ID__c = '1234567890';
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
            System.assert(ProviderAcc.Name == 'test account');
            
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
                
            HUMCaseInteractionAboutType__c objIntAbt = new HUMCaseInteractionAboutType__c();
            objIntAbt.Name = 'Provider';
            objIntAbt.CaseRecordType__c = 'Provider';
            objIntAbt.CaseInteraction_About_Type__c = 'Provider';
            insert objIntAbt;

            HUMCaseInteractionAboutType__c objIntAbt1 = new HUMCaseInteractionAboutType__c();
            objIntAbt1.Name = 'Unknown Provider';
            objIntAbt1.CaseRecordType__c = 'Unknown Provider';
            objIntAbt1.CaseInteraction_About_Type__c = 'Unknown-Provider';
            insert objIntAbt1;
            System.assertEquals(objIntAbt1.id != null, true);
            
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
    * This Is a test method for CreateInteractionRest_S_HUM
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionRestService()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
        // For MedicareID
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
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
                    
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='test2';
        intabout2.LastName = 'method2';
        intabout2.ID = '360306642';
        intabout2.IDType='Member-Id-Base';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Member';
        intabout2.ZipCode='12345';                        
        
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='test3';
        intabout3.LastName = 'method3';
        intabout3.ID = '002935049';
        intabout3.IDType='MedicareID';          
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';
        
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='test3';
        intabout4.LastName = 'method3';
        intabout4.ID = '002935049';
        intabout4.IDType='EnterprisePersonID';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout2.CaseIdentifier = '124';           
        caseintabout2.InteractingAbout = intabout2;
        caseintabout3.CaseIdentifier = '124';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '124';           
        caseintabout4.InteractingAbout = intabout4;
        
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertNotEquals(response1,null);
        Test.StopTest();
      }
    }
    
    
     /**
    * This Is a test method for CreateInteractionRest_S_HUM
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_UpdateInteractionRestService()
    {
        User oETLUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User2' LIMIT 1 ] ;
        system.runAs (oETLUser) {
        // For MedicareID
        UpdateInteractionRequest_DTO_HUM  owrap = new UpdateInteractionRequest_DTO_HUM ();
        UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest intreq = new UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest();
        UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        UpdateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new UpdateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
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
                    
        UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='test2';
        intabout2.LastName = 'method2';
        intabout2.ID = '360306642';
        intabout2.IDType='Member-Id-Base';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Member';
        intabout2.ZipCode='12345';                        
              
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout2.CaseIdentifier = '124';           
        caseintabout2.InteractingAbout = intabout2;
                
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intReq.InteractionNumber = '2000011676609';
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.UpdateInteractionRequest  = intreq;
        Test.startTest();          
        UpdateInteractionResponse_DTO_HUM response1 = UpdateInteractionWithCaseRest_D_HUM.updateInteraction(owrap);
        system.assertNotEquals(response1,null);
        Test.StopTest();
      }
    }
}