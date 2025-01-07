/****************************************************************************
Apex Class Name  : CreateInteractionWithCaseRest_T_HUM
Version          : 1.0 
Created Date     : April 18 2019
Function         : Test class to CreateInteractionWithCaseRest_D_HUM Apex class.
Modification Log :
  Developer Name          Code Review                Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  Pallavi Ravishankar                              04/18/2019                   Original Version
  Prasanthi Kandula                                05/16/2019                   Fixed security scan defects
  Prasanthi Kandula                                05/17/2019                   Added New methods to cover unknown member functionality
  Prasanthi Kandula                                05/20/2019                   Updated test class to cover SOSL methods
  Seema Kaulgi                                         07/04/2019               REQ - 403811 GBO Watson chat (Group)
  Prudhvi Pamarthi                                 08/13/2020                   Fixed code coverage issue
  Sagar MN                                         05/18/2021                   Updated test class for CaseInteractionSearch_H_HUM checkmarx fix
  Vaishnavi Manda                                  05/20/2021                   Updated test class for Checkmarx fix
  Vamshi Krishna Pemberthi						   06/17/2021					Updated test class for code coverage
 *******************************************************************************************************************************/

@isTest
public class CreateInteractionWithCaseRest_T_HUM {
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
            
            HUMConstants__c  objhumc = New HUMConstants__c();
            objhumc.name ='UNKNOWN_MEMBER';
            objhumc.StringValue__c ='Unknown Member';
            insert objhumc ;
            system.assertEquals(objhumc.id != null, True);
            
            HUMConstants__c  objhumc1 = New HUMConstants__c();
            objhumc1.name ='UNKNOWN_MEMBER_EXTID';
            objhumc1.StringValue__c ='UKM|00|SFDC|';
            insert objhumc1 ;
            system.assertEquals(objhumc1.id != null, True);
            
            HUMConstants__c  objhumcPipe = New HUMConstants__c();
            objhumcPipe.name ='PIPE_CHARACTER';
            objhumcPipe.StringValue__c ='|';
            insert objhumcPipe;
            system.assertEquals(objhumcPipe.id != null, True);
            
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
    * This Is a test method for Create Interaction with Member
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForMember()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
       System.runAs (oUser) {
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
        
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641' OR Name='360306642'];
        Id [] fixedSearchResults= new Id[2];
        fixedSearchResults[0] = memberacct[0].id;
        fixedSearchResults[1] = memberacct[1].id;
        Test.setFixedSearchResults(fixedSearchResults);
        
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
        caseintabout3.CaseIdentifier = '125';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
         
        list<CreateCaseRequest_DTO_V4_HUM> request = new list<CreateCaseRequest_DTO_V4_HUM>();
      //Attachment attribute 1
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute> oattachAttributeList = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute>();
      CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute oattachAttribute = new CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute();
      oattachAttribute.AttachmentName = 'RequestId';
      oattachAttribute.AttachmentValue = '45678';
      oattachAttributeList.add(oattachAttribute);

      //Comment 1        
      List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentObjList1 = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
      CaseDetailRequest_DTO_V4_HUM.CaseComment caseCommentObj1 = new CaseDetailRequest_DTO_V4_HUM.CaseComment();
      caseCommentObj1.CommentBody = 'New comment';
      caseCommentObjList1.add(caseCommentObj1);

      //Attachment Log 1       
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> AttachmentLogList1 = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();        
      CaseDetailRequest_DTO_V4_HUM.AttachmentLog AttachmentLog1 = new CaseDetailRequest_DTO_V4_HUM.AttachmentLog();        
      AttachmentLog1.AttachmentKey = '3439403403';
      AttachmentLog1.AttachmentType = 'MRM';
      AttachmentLog1.AttachmentDescription = 'MRM attachment description';
      AttachmentLog1.AttachmentAttribute = oattachAttributeList;
      AttachmentLogList1.add(AttachmentLog1);

      //Attachment 1
      List<CaseDetailRequest_DTO_V4_HUM.Attachment> AttachmentList1 = new List<CaseDetailRequest_DTO_V4_HUM.Attachment>();        
      CaseDetailRequest_DTO_V4_HUM.Attachment Attachment1 = new CaseDetailRequest_DTO_V4_HUM.Attachment();        
      Attachment1.DocId = '66585745';
      Attachment1.attachmentURL ='ehub.com';
      Attachment1.fileName = 'ehub.com';
      Attachment1.versionNumber ='1';
      AttachmentList1.add(Attachment1);
      CaseDetailRequest_DTO_V4_HUM oCaseDetail = new CaseDetailRequest_DTO_V4_HUM();
      oCaseDetail.Classification = 'Enrollment';
      oCaseDetail.Intent = 'Agent Action Required/Email Sent';
      oCaseDetail.ClassificationType = 'Calls (RSO)';
      oCaseDetail.ClosedDate = String.ValueOf(Date.Today());   
      oCaseDetail.Complaint = 'Yes';    
      oCaseDetail.ComplaintReason = 'Test';  
      oCaseDetail.ComplaintType = 'Test';    
      oCaseDetail.Contract = 'Test'; 
      oCaseDetail.CreatedQueue = 'Louisville RSO Calls Support'; 
      oCaseDetail.DCN = 'Test';  
      oCaseDetail.Department = 'Test';   
      oCaseDetail.DocType = 'E12';  
      oCaseDetail.ElectionType = 'Test'; 
      oCaseDetail.ExternalTrackId = 'Test';        
      oCaseDetail.FollowupDueDate = System.today().format();   
      oCaseDetail.GAReason = 'Test'; 
      oCaseDetail.GARightsGiven = 'Test';   

      oCaseDetail.MilestoneId = 'Test';  
      oCaseDetail.Origin = 'Correspondence';   
      oCaseDetail.OwnerQueue = 'Louisville RSO Calls Support';              
      oCaseDetail.PendKey = 'Test';           
      oCaseDetail.Priority = 'Test';         
      oCaseDetail.SegmentNumber = 'Test';  
      oCaseDetail.ServiceCenter = 'Test';            
      oCaseDetail.CaseStatus = 'In Progress';   
      oCaseDetail.TopicName = 'Test';   
      oCaseDetail.CaseType = 'Correspondence Inquiry'; 
      oCaseDetail.CaseSubType = 'Creditable Coverage';   
      oCaseDetail.WorkQueueViewName = 'Test';
      oCaseDetail.CaseComments = caseCommentObjList1;
      oCaseDetail.AttachmentLog = AttachmentLogList1;
      oCaseDetail.Attachment = AttachmentList1;   
      oCaseDetail.AsynchronousProcess=false;
      oCaseDetail.Department = 'Louisville Medicare'; 
      oCreateCaseDetails.caseDetails = oCaseDetail;
      oCreateCaseDetails.caseIdentifier = '123';    
      lstCaseDetails.add(oCreateCaseDetails); 
        intreq.CreateCaseDetails = lstCaseDetails; 
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
     
         map<string, map<string,set<string>>> mapIDValue =new map<string, map<string,set<string>>>();  
         map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
         mapMemberIDValues.put(caseintabout.InteractingAbout.IDType, new set<string>{caseintabout.InteractingAbout.ID.toUppercase()});        
         mapIDValue.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM, mapMemberIDValues);
         List<InteractingAccount_DTO_HUM> listMemberIntAc = new List<InteractingAccount_DTO_HUM>();
         InteractingAccount_DTO_HUM newIntAccDto = new InteractingAccount_DTO_HUM();         
        newIntAccDto.AddressLine1='street 1';
        newIntAccDto.City = 'Test City';
        newIntAccDto.DOB = '01/01/1990';
        newIntAccDto.EmailID='test2';
        newIntAccDto.FirstName = 'test2';
        newIntAccDto.HomePhone = '11111';
        newIntAccDto.ID = '360306641';
        newIntAccDto.IDType='Member-Id-Base';
        newIntAccDto.MemberIdDependentCode='00';
        newIntAccDto.LastName = 'method2';
        newIntAccDto.State = 'teststate';
        newIntAccDto.EntityType='Member';
        newIntAccDto.ZipCode='12345';
        listMemberIntAc.add(newIntAccDto);
           
        List<Member_Id__c> membersIds = new List<Member_Id__c>();
        membersIds = [SELECT ID ,Name ,Type__c,Policy_Member__r.Member__c,Policy_Member__r.Member__r.FirstName, Policy_Member__r.Member__r.LastName FROM Member_Id__c limit 10];    
         map<string,string> retVal = new  map<string,string>();
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       
       retVal=  CaseInteractionSearch_H_HUM.getMatchingRecordsWithName(membersIds, mapMemberIDValues,'Interacting_About', listMemberIntAc); 
        //system.assertNotEquals(response1,NULL);
        Test.StopTest();
        }
    }

   /**
    * This Is a test method for Create Interaction with Provider having Interacting With And about
    *
    * @param  None      
    * @return  Void
    */
    Static testMethod void test_CreateIntProviderWithAbout()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '111111111';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout.CaseIdentifier = '1234'; 
        
        intlist.add(caseintabout2);
        lstCaseService.add(intabout2);
        intreq.InteractingWith = intabout2;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
     
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
            Test.setMock(HttpCalloutMock.class, New CaseServiceRestMock_T_HUM(EPSRes, 200));
            Test.startTest();             
            CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
            system.assertNotEquals(response1,NULL);
            Test.StopTest();
        }
    }
    
   /**
    * This Is a test method for Create Interaction with Provider having multiple interacting abouts
    *
    * @param  None      
    * @return  Void
    */
    Static testMethod void test_CreateIntProviderMutlipleId()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test1';
        intabout.FirstName = 'test1';
        intabout.HomePhone = '11111';
        intabout.ID = '002935049';
        intabout.IDType='MedicareID';
        intabout.MemberIdDependentCode='';
        intabout.LastName = 'method1';
        intabout.State = 'teststate';
        intabout.EntityType='Member';
        intabout.ZipCode='12345';
        caseintabout.InteractingAbout = intabout;
        caseintabout.CaseIdentifier = '123';           
        
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '111111111';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout.CaseIdentifier = '1234'; 
        
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        lstCaseService.add(intabout);
        lstCaseService.add(intabout2);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.CreateInteractionRequest  = intreq;
        
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
            CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
            system.assertNotEquals(response1,NULL);
            Test.StopTest();
           }
    }
    
    
   /**
    * This Is a test method for Create Interaction with Unknown provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateIntProviderUnknown()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='ProviderUnknown';
        intabout2.lastname='testUnknown';
        intabout2.ID = '1111111110';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout2.CaseIdentifier = '1234'; 
        intlist.add(caseintabout2);
        lstCaseService.add(intabout2);
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.AddressLine1='test add line 1';
        intabout3.City = 'Test City';
        intabout3.EmailID='';
        intabout3.firstname='ProviderUknonwn';
        intabout3.lastname='testUnknown';
        intabout3.ID = '12345678901';
        intabout3.IDType='NPIID';           
        intabout3.State = 'teststate';
        intabout3.EntityType='Provider';
        intabout3.ZipCode='12345';
        intabout3.ProviderClassification = 'Facility';    
        caseintabout3.InteractingAbout = intabout3;
        caseintabout3.CaseIdentifier = '124';
        intlist.add(caseintabout3);
        lstCaseService.add(intabout3); 
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.AddressLine1='test add line 1';
        intabout4.City = 'Test City';
        intabout4.EmailID='';
        intabout4.firstname='ProviderUnknown';
        intabout4.lastname='testUnknown';
        intabout4.ID = '12345678901';
        intabout4.IDType='NPIID';           
        intabout4.State = 'teststate';
        intabout4.EntityType='Provider';
        intabout4.ZipCode='12345';
        intabout4.ProviderClassification = 'Facility';  
        intreq.InteractingWith = intabout4;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       // system.assertNotEquals(response1,NULL);
        Test.StopTest();
       }
    }
    
    /**
    * This Is a test method for Create Interaction with Unknown provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateIntUnknownProvider()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        system.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '';
        intabout2.IDType='UnknownProvider';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility'; 
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='test2';
        intabout3.LastName = 'method2';
        intabout3.ID = '360306642';
        intabout3.IDType='Member-Id-Base';        
        intabout3.MemberIdDependentCode='00';   
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';                        
        caseintabout3.InteractingAbout = intabout3;
        caseintabout3.CaseIdentifier = '1234'; 
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name='360306642'];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = memberacct[0].id;
        Test.setFixedSearchResults(fixedSearchResults);
        intlist.add(caseintabout3);
        lstCaseService.add(intabout3);
        intreq.InteractingWith = intabout2;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       // system.assertNotEquals(response1,NULL);
        Test.StopTest();
        }
    }
    
  
   /**
    * This Is a test method for Create Interaction with UnknownMember in request
    *
    * @param  None      
    * @return  Void
    */  
    static testMethod void test_CreateInteractionWithCase_ForUM()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        system.runAs (oUser) {
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
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641'];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = memberacct[0].id;
        Test.setFixedSearchResults(fixedSearchResults);            
        
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='test3';
        intabout4.LastName = 'method3';
        intabout4.ID = '';
        intabout4.IDType='UnknownMember';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        
        
        list<CreateCaseRequest_DTO_V4_HUM> request = new list<CreateCaseRequest_DTO_V4_HUM>();
      //Attachment attribute 1
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute> oattachAttributeList = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute>();
      CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute oattachAttribute = new CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute();
      oattachAttribute.AttachmentName = 'RequestId';
      oattachAttribute.AttachmentValue = '45678';
      oattachAttributeList.add(oattachAttribute);

      //Comment 1        
      List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentObjList1 = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
      CaseDetailRequest_DTO_V4_HUM.CaseComment caseCommentObj1 = new CaseDetailRequest_DTO_V4_HUM.CaseComment();
      caseCommentObj1.CommentBody = 'New comment';
      caseCommentObjList1.add(caseCommentObj1);

      //Attachment Log 1       
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> AttachmentLogList1 = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();        
      CaseDetailRequest_DTO_V4_HUM.AttachmentLog AttachmentLog1 = new CaseDetailRequest_DTO_V4_HUM.AttachmentLog();        
      AttachmentLog1.AttachmentKey = '3439403403';
      AttachmentLog1.AttachmentType = 'MRM';
      AttachmentLog1.AttachmentDescription = 'MRM attachment description';
      AttachmentLog1.AttachmentAttribute = oattachAttributeList;
      AttachmentLogList1.add(AttachmentLog1);

      //Attachment 1
      List<CaseDetailRequest_DTO_V4_HUM.Attachment> AttachmentList1 = new List<CaseDetailRequest_DTO_V4_HUM.Attachment>();        
      CaseDetailRequest_DTO_V4_HUM.Attachment Attachment1 = new CaseDetailRequest_DTO_V4_HUM.Attachment();        
      Attachment1.DocId = '66585745';
      Attachment1.attachmentURL ='ehub.com';
      Attachment1.fileName = 'ehub.com';
      Attachment1.versionNumber ='1';
      AttachmentList1.add(Attachment1);
      CaseDetailRequest_DTO_V4_HUM oCaseDetail = new CaseDetailRequest_DTO_V4_HUM();
      oCaseDetail.Classification = 'Enrollment';
      oCaseDetail.Intent = 'Agent Action Required/Email Sent';
      oCaseDetail.ClassificationType = 'Calls (RSO)';
      oCaseDetail.ClosedDate = String.ValueOf(Date.Today());   
      oCaseDetail.Complaint = 'Yes';    
      oCaseDetail.ComplaintReason = 'Test';  
      oCaseDetail.ComplaintType = 'Test';    
      oCaseDetail.Contract = 'Test'; 
      oCaseDetail.CreatedQueue = 'Louisville RSO Calls Support'; 
      oCaseDetail.DCN = 'Test';  
      oCaseDetail.Department = 'Test';   
      oCaseDetail.DocType = 'E12';  
      oCaseDetail.ElectionType = 'Test'; 
      oCaseDetail.ExternalTrackId = 'Test';        
      oCaseDetail.FollowupDueDate = System.today().format();   
      oCaseDetail.GAReason = 'Test'; 
      oCaseDetail.GARightsGiven = 'Test';   
      oCaseDetail.MilestoneId = 'Test';  
      oCaseDetail.Origin = 'Correspondence';   
      oCaseDetail.OwnerQueue = 'Louisville RSO Calls Support';              
      oCaseDetail.PendKey = 'Test';           
      oCaseDetail.Priority = 'Test';         
      oCaseDetail.SegmentNumber = 'Test';  
      oCaseDetail.ServiceCenter = 'Test';            
      oCaseDetail.CaseStatus = 'In Progress';   
      oCaseDetail.TopicName = 'Test';   
      oCaseDetail.CaseType = 'Correspondence Inquiry'; 
      oCaseDetail.CaseSubType = 'Creditable Coverage';   
      oCaseDetail.WorkQueueViewName = 'Test';
      oCaseDetail.CaseComments = caseCommentObjList1;
      oCaseDetail.AttachmentLog = AttachmentLogList1;
      oCaseDetail.Attachment = AttachmentList1;   
      oCaseDetail.AsynchronousProcess=false;
      oCaseDetail.Department = 'Louisville Medicare'; 
      oCreateCaseDetails.caseDetails = oCaseDetail;
      oCreateCaseDetails.caseIdentifier = '126';    
      lstCaseDetails.add(oCreateCaseDetails); 
      caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        intreq.CreateCaseDetails = lstCaseDetails; 
        intlist.add(caseintabout);
        intlist.add(caseintabout4);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertNotEquals(response1,NULL);
        Test.StopTest();
      }
    }

    static testMethod void test_CreateInteractionWithCase_ForUM2()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        system.runAs (oUser) {
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
        intabout.IDType='UnknownProvider';
        intabout.MemberIdDependentCode='00';
        intabout.LastName = 'method2';
        intabout.State = 'teststate';
        intabout.EntityType='Provider';
        intabout.ZipCode='12345';                       
    
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='test3';
        intabout4.LastName = 'method3';
        intabout4.ID = '';
        intabout4.IDType='UnknownProvider';          
        intabout4.EntityType='Provider';
        intabout4.ZipCode='12345';
        
        
        list<CreateCaseRequest_DTO_V4_HUM> request = new list<CreateCaseRequest_DTO_V4_HUM>();
      //Attachment attribute 1
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute> oattachAttributeList = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute>();
      CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute oattachAttribute = new CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute();
      oattachAttribute.AttachmentName = 'RequestId';
      oattachAttribute.AttachmentValue = '45678';
      oattachAttributeList.add(oattachAttribute);

      //Comment 1        
      List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentObjList1 = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
      CaseDetailRequest_DTO_V4_HUM.CaseComment caseCommentObj1 = new CaseDetailRequest_DTO_V4_HUM.CaseComment();
      caseCommentObj1.CommentBody = 'New comment';
      caseCommentObjList1.add(caseCommentObj1);

      //Attachment Log 1       
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> AttachmentLogList1 = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();        
      CaseDetailRequest_DTO_V4_HUM.AttachmentLog AttachmentLog1 = new CaseDetailRequest_DTO_V4_HUM.AttachmentLog();        
      AttachmentLog1.AttachmentKey = '3439403403';
      AttachmentLog1.AttachmentType = 'MRM';
      AttachmentLog1.AttachmentDescription = 'MRM attachment description';
      AttachmentLog1.AttachmentAttribute = oattachAttributeList;
      AttachmentLogList1.add(AttachmentLog1);

      //Attachment 1
      List<CaseDetailRequest_DTO_V4_HUM.Attachment> AttachmentList1 = new List<CaseDetailRequest_DTO_V4_HUM.Attachment>();        
      CaseDetailRequest_DTO_V4_HUM.Attachment Attachment1 = new CaseDetailRequest_DTO_V4_HUM.Attachment();        
      Attachment1.DocId = '66585745';
      Attachment1.attachmentURL ='ehub.com';
      Attachment1.fileName = 'ehub.com';
      Attachment1.versionNumber ='1';
      AttachmentList1.add(Attachment1);
      CaseDetailRequest_DTO_V4_HUM oCaseDetail = new CaseDetailRequest_DTO_V4_HUM();
      oCaseDetail.Classification = 'Enrollment';
      oCaseDetail.Intent = 'Agent Action Required/Email Sent';
      oCaseDetail.ClassificationType = 'Calls (RSO)';
      oCaseDetail.ClosedDate = String.ValueOf(Date.Today());   
      oCaseDetail.Complaint = 'Yes';    
      oCaseDetail.ComplaintReason = 'Test';  
      oCaseDetail.ComplaintType = 'Test';    
      oCaseDetail.Contract = 'Test'; 
      oCaseDetail.CreatedQueue = 'Louisville RSO Calls Support'; 
      oCaseDetail.DCN = 'Test';  
      oCaseDetail.Department = 'Test';   
      oCaseDetail.DocType = 'E12';  
      oCaseDetail.ElectionType = 'Test'; 
      oCaseDetail.ExternalTrackId = 'Test';        
      oCaseDetail.FollowupDueDate = System.today().format();   
      oCaseDetail.GAReason = 'Test'; 
      oCaseDetail.GARightsGiven = 'Test';   
      oCaseDetail.MilestoneId = 'Test';  
      oCaseDetail.Origin = 'Correspondence';   
      oCaseDetail.OwnerQueue = 'Louisville RSO Calls Support';              
      oCaseDetail.PendKey = 'Test';           
      oCaseDetail.Priority = 'Test';         
      oCaseDetail.SegmentNumber = 'Test';  
      oCaseDetail.ServiceCenter = 'Test';            
      oCaseDetail.CaseStatus = 'In Progress';   
      oCaseDetail.TopicName = 'Test';   
      oCaseDetail.CaseType = 'Correspondence Inquiry'; 
      oCaseDetail.CaseSubType = 'Creditable Coverage';   
      oCaseDetail.WorkQueueViewName = 'Test';
      oCaseDetail.CaseComments = caseCommentObjList1;
      oCaseDetail.AttachmentLog = AttachmentLogList1;
      oCaseDetail.Attachment = AttachmentList1;   
      oCaseDetail.AsynchronousProcess=false;
      oCaseDetail.Department = 'Louisville Medicare'; 
      oCreateCaseDetails.caseDetails = oCaseDetail;
      oCreateCaseDetails.caseIdentifier = '126';    
      lstCaseDetails.add(oCreateCaseDetails); 
      caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        intreq.CreateCaseDetails = lstCaseDetails; 
        intlist.add(caseintabout);
        intlist.add(caseintabout4);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        CreateInteractionResponse_DTO_HUM response2 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        //system.assertNotEquals(response1,NULL);
        Test.StopTest();
      }
    }
    
    
    /**
    * This Is a test method for Create Interaction when asynshronous flag is true
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForAsync()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
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
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641'];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = memberacct[0].id;
        Test.setFixedSearchResults(fixedSearchResults);                     
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='test3';
        intabout4.LastName = 'method3';
        intabout4.ID = '';
        intabout4.IDType='UnknownMember';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        intlist.add(caseintabout);
        intlist.add(caseintabout4);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertNotEquals(response1,NULL);
        Test.StopTest();
      }
    }
    
    /**
    * This Is a test method for Create Interaction to cover exception for Asynchronous flag
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForAsyncException()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) { 
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
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='test3';
        intabout4.LastName = 'method3';
        intabout4.ID = '';
        intabout4.IDType='UnknownMember';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        intlist.add(caseintabout);
        intlist.add(caseintabout4);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertNotEquals(response1,NULL);
        Test.StopTest();
        }
    }
    
    /**
    * This Is a test method for Create Interaction of Unknown Member
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForMemberUnknown()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
       System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();       
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test2';
        intabout.FirstName = 'test2';
        intabout.HomePhone = '11111';
        intabout.ID = '112546359';
        intabout.IDType='Member-Id-Base';
        intabout.MemberIdDependentCode='00';
        intabout.LastName = 'method2';
        intabout.State = 'teststate';
        intabout.EntityType='Member';
        intabout.ZipCode='12345';                       
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='Unknown';
        intabout2.LastName = 'Member';
        intabout2.ID = '';
        intabout2.IDType='';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Member';
        intabout2.ZipCode='12345';    
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='Missing FN';
        intabout3.LastName = 'Missing LN';
        intabout3.ID = '002935011';
        intabout3.IDType='MedicareID';          
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='FN';
        intabout4.LastName = 'LN';
        intabout4.ID = '002935011';
        intabout4.IDType='EnterprisePersonID';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout5 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout5 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout5.firstname='Missing FN';
        intabout5.LastName = 'Missing LN';
        intabout5.ID = '002935011';
        intabout5.IDType='Medicaid-Id';          
        intabout5.EntityType='Member';
        intabout5.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout6 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout6 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout6.firstname='Missing FN';
        intabout6.LastName = 'Missing LN';
        intabout6.ID = '002935011';
        intabout6.IDType='SSN';          
        intabout6.EntityType='Member';
        intabout6.ZipCode='12345';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout2.CaseIdentifier = '124';           
        caseintabout2.InteractingAbout = intabout2;
        caseintabout3.CaseIdentifier = '125';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        caseintabout5.CaseIdentifier = '126';           
        caseintabout5.InteractingAbout = intabout5;
        caseintabout6.CaseIdentifier = '126';           
        caseintabout6.InteractingAbout = intabout6;
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
        intlist.add(caseintabout5);
        intlist.add(caseintabout6);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertEquals(response1,NULL);
        Test.StopTest();
        }
    }
    
    /**
    * This Is a test method for Create Interaction of Group
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForGroup()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
      
       System.runAs (oUser) {
        HUM_Webservice_Callout__c webServ =  new HUM_Webservice_Callout__c( Name = 'GroupSearchService' , Certificate_Name__c = 'TestCertificate', Active__c = true, End_Point_URL__c = 'http://www.google.com', Timeout__c = '12000');
        insert webServ;
        HUMTestDataHelperTest.getHUMConstantsData();
        List<Account> lstacc = new List<Account>();
            RecordType PersonAccountRecordType = [Select Id from RecordType where Name = 'Group' and sObjectType='Account'];
            for(integer i=1;i<11;i++)
            {
                Account acc = HUMTestDataHelperTest.getAccount();
                acc.RecordType = PersonAccountRecordType;
                acc.Name = 'TestGroup008';
                acc.Group_Number__c = 'TestGroup'+i;
                acc.BillingState = 'Alabama';
                acc.Home_Office_Account__c = true;
                
                
                lstacc.add(acc);
                
            }
            insert lstacc;
            system.assertEquals(lstacc.size() == 10,true);
        
        List<HUMGroupSearchServiceHelper.ResponseGroupSearch> lstResponseGroupSearch= new List<HUMGroupSearchServiceHelper.ResponseGroupSearch>();
            HUMGroupSearchServiceHelper.ResponseGroupSearch responseGroupSearch = new HUMGroupSearchServiceHelper.ResponseGroupSearch();
            responseGroupSearch.GroupName='test GroupName';
            lstResponseGroupSearch.add(responseGroupSearch);                         
            
            Test.setCurrentPage(Page.HUMCustomSearchPage);            
                
        CaseServiceGroup_H_HUM grpController = new CaseServiceGroup_H_HUM();
        
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();       
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test2';
        intabout.FirstName = 'MissingFN';
        intabout.HomePhone = '11111';
        intabout.ID = '632703';
        intabout.IDType='GroupID';
        intabout.MemberIdDependentCode='00';
        intabout.LastName = 'MissingLN';
        intabout.State = 'teststate';
        intabout.EntityType='Group';
        intabout.ZipCode='12345';                       
        intabout.AccountName = 'AAA TEXTILES';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='MissingFN';
        intabout2.LastName = 'MissingLN';
        intabout2.ID = '632704';
        intabout2.IDType='GroupID';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Group';
        intabout2.ZipCode='12345'; 
        intabout.AccountName = '3FORM';        
        caseintabout2.CaseIdentifier = '123';           
        caseintabout2.InteractingAbout = intabout2;
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intreq.InteractingWith = intabout;        
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Watson';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        system.assertequals(response1,NULL);    
        
        Map<String,String> requestDTOMap = new Map<String,String>();
        String grID = intabout2.Id;
        
        requestDTOMap.put('ProviderClassification',grID );
        CaseServiceGroup_H_HUM.mapReturnValues.put('Interacting_with',requestDTOMap );
        
       
        requestDTOMap.put('sTaxID',grID );
        requestDTOMap.put('sNPI',grID );
        CaseServiceGroup_H_HUM.prepareMapWithExtrnlId('632703','123456789123ju','Interacting_about');                
        grpController.processResponse();        
        Map<String,Account> acctMapToInsert = new Map<String,Account>();
        Account acc5 = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
        insert Acc5;
        CaseServiceRestCreateRequest_DTO_HUM casereq = new CaseServiceRestCreateRequest_DTO_HUM();
        
        acctMapToInsert.put('Interacting_About',Acc5);        
        CreateCaseRest_H_HUM.insertUnknownAccounts(acctMapToInsert,requestDTOMap);
        CreateCaseRest_H_HUM.upsertAccountRec(acctMapToInsert);
        CreateCaseRest_H_HUM.insertConsumerIds('1235452151',acc5.Id,'Provider');
        CreateCaseRest_H_HUM.upsertconsumerIds(requestDTOMap,acctMapToInsert,'Provider');
        CreateCaseRest_H_HUM.getInteractionWrapObj(casereq,'Interacting_With');
        String invalidId = 'Invalid Id';
        String invalidType = 'InvalidType';
        List<String> lstErrors = new list<String>();
        lstErrors.add(invalidId );
        lstErrors.add(invalidType );
        CreateCaseRestBulkified_D_V4_HUM.getErrorLogInstances(lstErrors);
        grpController.responseDTO = lstResponseGroupSearch;
        grpController.processResponse();
        Test.StopTest();
        }
    }
   /**
    * This Is a test method for Create Interaction of Unknown Member Excpetion Sceanrio
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForException()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
       System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();       
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test2';
        intabout.FirstName = 'test2';
        intabout.HomePhone = '11111';
        intabout.ID = '112546359';
        intabout.IDType='Member-Id';
        intabout.MemberIdDependentCode='00';
        intabout.LastName = 'method2';
        intabout.State = 'teststate';
        intabout.EntityType='Member';
        intabout.ZipCode='12345';                       
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='Unknown';
        intabout2.LastName = 'Member';
        intabout2.ID = '';
        intabout2.IDType='';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Member';
        intabout2.ZipCode='12345';    
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='Missing FN';
        intabout3.LastName = 'Missing LN';
        intabout3.ID = '002935011';
        intabout3.IDType='MedicareID';          
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='FN';
        intabout4.LastName = 'LN';
        intabout4.ID = '';
        intabout4.IDType='EnterprisePersonID';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout5 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout5 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout5.firstname='Missing FN';
        intabout5.LastName = 'Missing LN';
        intabout5.ID = '002935011';
        intabout5.IDType='Medicaid-Id';          
        intabout5.EntityType='Member';
        intabout5.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout6 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout6 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout6.firstname='Missing FN';
        intabout6.LastName = 'Missing LN';
        intabout6.ID = '002935011';
        intabout6.IDType='SSN';          
        intabout6.EntityType='Member';
        intabout6.ZipCode='12345';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout2.CaseIdentifier = '124';           
        caseintabout2.InteractingAbout = intabout2;
        caseintabout3.CaseIdentifier = '125';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        caseintabout5.CaseIdentifier = '126';           
        caseintabout5.InteractingAbout = intabout5;
        caseintabout6.CaseIdentifier = '126';           
        caseintabout6.InteractingAbout = intabout6;
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
        intlist.add(caseintabout5);
        intlist.add(caseintabout6);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
  
            List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponses = new List<CaseDetailResponse_DTO_V4_HUM>();
           CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
           List<CaseDetailResponse_DTO_V4_HUM> methResp = new List<CaseDetailResponse_DTO_V4_HUM>();
           resp.CaseId ='5007A000006Rep7';
           resp.CaseNumber='1000201850054';
           resp.ErrorCode='';
           resp.ErrorMessage='';
           resp.Success=true;
           lstCaseResponses.add(resp);
           List<Interaction__c> intId =[SELECT Id, Name FROM Interaction__c limit 1];
           String intractId = intID[0].id;
           list<case> listCases = new list<case>();
           listCases =[SELECT Id, CaseNumber, Type, Status FROM Case limit 10];
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        CaseInteractionSearch_H_HUM.getErrorLogInstance('204','Test Exception sceanrio',false);
        system.assertEquals(response1,NULL);
           
          methResp= CaseInteractionSearch_H_HUM.getCaseErrorLogs(lstCaseResponses);
           CaseInteractionSearch_H_HUM.insertConsumerTaxIds(null);           
           List<String> retValue= CaseInteractionSearch_H_HUM.createCaseInteractionRecords(lstCaseResponses, intractId, listCases);
        Test.StopTest();
        }
    }
    public static void setCheckMarxSwitch(boolean value){
        CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
        OSBONOFF.IsON__c=true;
        OSBONOFF.Name='2163327';
        OSBONOFF.Story_Title__c='2163327 - checkmarx';
        Insert OSBONOFF;
    }
    /**
    * This Is a test method for Create Interaction with Member
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateInteractionWithCase_ForMember2()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
       System.runAs (oUser) {
        setCheckMarxSwitch(true);
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
        
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641' OR Name='360306642'];
        Id [] fixedSearchResults= new Id[2];
        fixedSearchResults[0] = memberacct[0].id;
        fixedSearchResults[1] = memberacct[1].id;
        Test.setFixedSearchResults(fixedSearchResults);
        
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
        caseintabout3.CaseIdentifier = '125';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
         
        list<CreateCaseRequest_DTO_V4_HUM> request = new list<CreateCaseRequest_DTO_V4_HUM>();
      //Attachment attribute 1
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute> oattachAttributeList = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute>();
      CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute oattachAttribute = new CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute();
      oattachAttribute.AttachmentName = 'RequestId';
      oattachAttribute.AttachmentValue = '45678';
      oattachAttributeList.add(oattachAttribute);

      //Comment 1        
      List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentObjList1 = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
      CaseDetailRequest_DTO_V4_HUM.CaseComment caseCommentObj1 = new CaseDetailRequest_DTO_V4_HUM.CaseComment();
      caseCommentObj1.CommentBody = 'New comment';
      caseCommentObjList1.add(caseCommentObj1);

      //Attachment Log 1       
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> AttachmentLogList1 = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();        
      CaseDetailRequest_DTO_V4_HUM.AttachmentLog AttachmentLog1 = new CaseDetailRequest_DTO_V4_HUM.AttachmentLog();        
      AttachmentLog1.AttachmentKey = '3439403403';
      AttachmentLog1.AttachmentType = 'MRM';
      AttachmentLog1.AttachmentDescription = 'MRM attachment description';
      AttachmentLog1.AttachmentAttribute = oattachAttributeList;
      AttachmentLogList1.add(AttachmentLog1);

      //Attachment 1
      List<CaseDetailRequest_DTO_V4_HUM.Attachment> AttachmentList1 = new List<CaseDetailRequest_DTO_V4_HUM.Attachment>();        
      CaseDetailRequest_DTO_V4_HUM.Attachment Attachment1 = new CaseDetailRequest_DTO_V4_HUM.Attachment();        
      Attachment1.DocId = '66585745';
      Attachment1.attachmentURL ='ehub.com';
      Attachment1.fileName = 'ehub.com';
      Attachment1.versionNumber ='1';
      AttachmentList1.add(Attachment1);
      CaseDetailRequest_DTO_V4_HUM oCaseDetail = new CaseDetailRequest_DTO_V4_HUM();
      oCaseDetail.Classification = 'Enrollment';
      oCaseDetail.Intent = 'Agent Action Required/Email Sent';
      oCaseDetail.ClassificationType = 'Calls (RSO)';
      oCaseDetail.ClosedDate = String.ValueOf(Date.Today());   
      oCaseDetail.Complaint = 'Yes';    
      oCaseDetail.ComplaintReason = 'Test';  
      oCaseDetail.ComplaintType = 'Test';    
      oCaseDetail.Contract = 'Test'; 
      oCaseDetail.CreatedQueue = 'Louisville RSO Calls Support'; 
      oCaseDetail.DCN = 'Test';  
      oCaseDetail.Department = 'Test';   
      oCaseDetail.DocType = 'E12';  
      oCaseDetail.ElectionType = 'Test'; 
      oCaseDetail.ExternalTrackId = 'Test';        
      oCaseDetail.FollowupDueDate = System.today().format();   
      oCaseDetail.GAReason = 'Test'; 
      oCaseDetail.GARightsGiven = 'Test';   

      oCaseDetail.MilestoneId = 'Test';  
      oCaseDetail.Origin = 'Correspondence';   
      oCaseDetail.OwnerQueue = 'Louisville RSO Calls Support';              
      oCaseDetail.PendKey = 'Test';           
      oCaseDetail.Priority = 'Test';         
      oCaseDetail.SegmentNumber = 'Test';  
      oCaseDetail.ServiceCenter = 'Test';            
      oCaseDetail.CaseStatus = 'In Progress';   
      oCaseDetail.TopicName = 'Test';   
      oCaseDetail.CaseType = 'Correspondence Inquiry'; 
      oCaseDetail.CaseSubType = 'Creditable Coverage';   
      oCaseDetail.WorkQueueViewName = 'Test';
      oCaseDetail.CaseComments = caseCommentObjList1;
      oCaseDetail.AttachmentLog = AttachmentLogList1;
      oCaseDetail.Attachment = AttachmentList1;   
      oCaseDetail.AsynchronousProcess=false;
      oCaseDetail.Department = 'Louisville Medicare'; 
      oCreateCaseDetails.caseDetails = oCaseDetail;
      oCreateCaseDetails.caseIdentifier = '123';    
      lstCaseDetails.add(oCreateCaseDetails); 
        intreq.CreateCaseDetails = lstCaseDetails; 
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
    
         map<string, map<string,set<string>>> mapIDValue =new map<string, map<string,set<string>>>(); 
         map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
         mapMemberIDValues.put(caseintabout.InteractingAbout.IDType, new set<string>{caseintabout.InteractingAbout.ID.toUppercase()});     
         mapIDValue.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM, mapMemberIDValues); 
         List<InteractingAccount_DTO_HUM> listMemberIntAc = new List<InteractingAccount_DTO_HUM>();
         InteractingAccount_DTO_HUM newIntAccDto = new InteractingAccount_DTO_HUM();         
        newIntAccDto.AddressLine1='street 1';
        newIntAccDto.City = 'Test City';
        newIntAccDto.DOB = '01/01/1990';
        newIntAccDto.EmailID='test2';
        newIntAccDto.FirstName = 'test2';
        newIntAccDto.HomePhone = '11111';
        newIntAccDto.ID = '360306641';
        newIntAccDto.IDType='Member-Id-Base';
        newIntAccDto.MemberIdDependentCode='00';
        newIntAccDto.LastName = 'method2';
        newIntAccDto.State = 'teststate';
        newIntAccDto.EntityType='Member';
        newIntAccDto.ZipCode='12345';
        listMemberIntAc.add(newIntAccDto);
           
        List<Member_Id__c> membersIds = new List<Member_Id__c>();
        membersIds = [SELECT ID ,Name ,Type__c,Policy_Member__r.Member__c,Policy_Member__r.Member__r.FirstName, Policy_Member__r.Member__r.LastName FROM Member_Id__c limit 10];    
         map<string,string> retVal = new  map<string,string>(); 
        Test.startTest();  
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       
       retVal=  CaseInteractionSearch_H_HUM.getMatchingRecordsWithName(membersIds, mapMemberIDValues,'Interacting_About', listMemberIntAc);
        //system.assertNotEquals(response1,NULL);
        Test.StopTest();
        }
    }
    /**
    * This Is a test method for Create Interaction with Provider having Interacting With And about
    *
    * @param  None      
    * @return  Void
    */
    Static testMethod void test_CreateIntProviderWithAbout2()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        setCheckMarxSwitch(true);
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '111111111';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout.CaseIdentifier = '1234'; 
        
        intlist.add(caseintabout2);
        lstCaseService.add(intabout2);
        intreq.InteractingWith = intabout2;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
     
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
            Test.setMock(HttpCalloutMock.class, New CaseServiceRestMock_T_HUM(EPSRes, 200));
            Test.startTest();             
            CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
            system.assertNotEquals(response1,NULL);
            Test.StopTest();
        }
    }
/**
    * This Is a test method for Create Interaction with Provider having multiple interacting abouts
    *
    * @param  None      
    * @return  Void
    */
    Static testMethod void test_CreateIntProviderMutlipleId2()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        setCheckMarxSwitch(true);
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test1';
        intabout.FirstName = 'test1';
        intabout.HomePhone = '11111';
        intabout.ID = '002935049';
        intabout.IDType='MedicareID';
        intabout.MemberIdDependentCode='';
        intabout.LastName = 'method1';
        intabout.State = 'teststate';
        intabout.EntityType='Member';
        intabout.ZipCode='12345';
        caseintabout.InteractingAbout = intabout;
        caseintabout.CaseIdentifier = '123';           
        
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '111111111';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout.CaseIdentifier = '1234'; 
        
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        lstCaseService.add(intabout);
        lstCaseService.add(intabout2);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= false;
        owrap.CreateInteractionRequest  = intreq;
        
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
            CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
            system.assertNotEquals(response1,NULL);
            Test.StopTest();
           }
    }
    /**
    * This Is a test method for Create Interaction with Unknown provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateIntProviderUnknown2()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
        setCheckMarxSwitch(true);
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='ProviderUnknown';
        intabout2.lastname='testUnknown';
        intabout2.ID = '1111111110';
        intabout2.IDType='TaxId';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility';    
        caseintabout2.InteractingAbout = intabout2;
        caseintabout2.CaseIdentifier = '1234'; 
        intlist.add(caseintabout2);
        lstCaseService.add(intabout2);
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.AddressLine1='test add line 1';
        intabout3.City = 'Test City';
        intabout3.EmailID='';
        intabout3.firstname='ProviderUknonwn';
        intabout3.lastname='testUnknown';
        intabout3.ID = '12345678901';
        intabout3.IDType='NPIID';           
        intabout3.State = 'teststate';
        intabout3.EntityType='Provider';
        intabout3.ZipCode='12345';
        intabout3.ProviderClassification = 'Facility';    
        caseintabout3.InteractingAbout = intabout3;
        caseintabout3.CaseIdentifier = '124';
        intlist.add(caseintabout3);
        lstCaseService.add(intabout3); 
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.AddressLine1='test add line 1';
        intabout4.City = 'Test City';
        intabout4.EmailID='';
        intabout4.firstname='ProviderUnknown';
        intabout4.lastname='testUnknown';
        intabout4.ID = '12345678901';
        intabout4.IDType='NPIID';           
        intabout4.State = 'teststate';
        intabout4.EntityType='Provider';
        intabout4.ZipCode='12345';
        intabout4.ProviderClassification = 'Facility';  
        intreq.InteractingWith = intabout4;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       // system.assertNotEquals(response1,NULL);
        Test.StopTest();
       }
    }
     /**
    * This Is a test method for Create Interaction with Unknown provider
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_CreateIntUnknownProvider2()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        system.runAs (oUser) {
        setCheckMarxSwitch(true);
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> lstCaseService = new List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.AddressLine1='test add line 1';
        intabout2.City = 'Test City';
        intabout2.EmailID='';
        intabout2.firstname='Provider';
        intabout2.lastname='test';
        intabout2.ID = '';
        intabout2.IDType='UnknownProvider';           
        intabout2.State = 'teststate';
        intabout2.EntityType='Provider';
        intabout2.ZipCode='12345';
        intabout2.ProviderClassification = 'Facility'; 
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='test2';
        intabout3.LastName = 'method2';
        intabout3.ID = '360306642';
        intabout3.IDType='Member-Id-Base';        
        intabout3.MemberIdDependentCode='00';   
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';                        
        caseintabout3.InteractingAbout = intabout3;
        caseintabout3.CaseIdentifier = '1234'; 
        List<Member_Id__c> memberacct = new List<Member_Id__c>();
        memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name='360306642'];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = memberacct[0].id;
        Test.setFixedSearchResults(fixedSearchResults);
        intlist.add(caseintabout3);
        lstCaseService.add(intabout3);
        intreq.InteractingWith = intabout2;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true;
        owrap.CreateInteractionRequest  = intreq;
        Test.startTest();          
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
       // system.assertNotEquals(response1,NULL);
        Test.StopTest();
        }
    }

    static testMethod void testCodeCoverage()
    {
       User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
       System.runAs (oUser) {
        CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
        CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
        list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
        list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();       
        intabout.AddressLine1='street 1';
        intabout.City = 'Test City';
        intabout.DOB = '01/01/1990';
        intabout.EmailID='test2';
        intabout.FirstName = 'test2';
        intabout.HomePhone = '11111';
        intabout.ID = '112546359';
        intabout.IDType='Member-Id';
        intabout.MemberIdDependentCode='00';
        intabout.LastName = 'method2';
        intabout.State = 'teststate';
        intabout.EntityType='Member';
        intabout.ZipCode='12345';                       
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout2.firstname='Unknown';
        intabout2.LastName = 'Member';
        intabout2.ID = '';
        intabout2.IDType='';        
        intabout2.MemberIdDependentCode='00';   
        intabout2.EntityType='Member';
        intabout2.ZipCode='12345';    
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout3 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout3 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout3.firstname='Missing FN';
        intabout3.LastName = 'Missing LN';
        intabout3.ID = '002935011';
        intabout3.IDType='MedicareID';          
        intabout3.EntityType='Member';
        intabout3.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout4 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout4 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout4.firstname='FN';
        intabout4.LastName = 'LN';
        intabout4.ID = '';
        intabout4.IDType='EnterprisePersonID';          
        intabout4.EntityType='Member';
        intabout4.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout5 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout5 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout5.firstname='Missing FN';
        intabout5.LastName = 'Missing LN';
        intabout5.ID = '002935011';
        intabout5.IDType='Medicaid-Id';          
        intabout5.EntityType='Member';
        intabout5.ZipCode='12345';
        CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout6 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout6 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
        intabout6.firstname='Missing FN';
        intabout6.LastName = 'Missing LN';
        intabout6.ID = '002935011';
        intabout6.IDType='SSN';          
        intabout6.EntityType='Member';
        intabout6.ZipCode='12345';
        caseintabout.CaseIdentifier = '123';           
        caseintabout.InteractingAbout = intabout;
        caseintabout2.CaseIdentifier = '124';           
        caseintabout2.InteractingAbout = intabout2;
        caseintabout3.CaseIdentifier = '125';           
        caseintabout3.InteractingAbout = intabout3;
        caseintabout4.CaseIdentifier = '126';           
        caseintabout4.InteractingAbout = intabout4;
        caseintabout5.CaseIdentifier = '126';           
        caseintabout5.InteractingAbout = intabout5;
        caseintabout6.CaseIdentifier = '126';           
        caseintabout6.InteractingAbout = intabout6;
        intlist.add(caseintabout);
        intlist.add(caseintabout2);
        intlist.add(caseintabout3);
        intlist.add(caseintabout4);
        intlist.add(caseintabout5);
        intlist.add(caseintabout6);
        intreq.InteractingWith = intabout;
        intreq.CaseInteractingAbout = intlist;   
        intreq.InteractionOrigin = 'Vantage';
        intreq.RequiredCasesInResponse= true; 
        owrap.CreateInteractionRequest  = intreq;  
  
            List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponses = new List<CaseDetailResponse_DTO_V4_HUM>();
           CaseDetailResponse_DTO_V4_HUM resp = new CaseDetailResponse_DTO_V4_HUM();
           List<CaseDetailResponse_DTO_V4_HUM> methResp = new List<CaseDetailResponse_DTO_V4_HUM>();
           resp.CaseId ='5007A000006Rep7';
           resp.CaseNumber='1000201850054';
           resp.ErrorCode='';
           resp.ErrorMessage='';
           resp.Success=true;
           lstCaseResponses.add(resp);
           List<Interaction__c> intId =[SELECT Id, Name FROM Interaction__c limit 1];
           String intractId = intID[0].id;
           list<case> listCases = new list<case>();
           listCases =[SELECT Id, CaseNumber, Type, Status FROM Case limit 10];
        Test.startTest();      
        

        String proId = '002935011' +'-'+ 'Interacting_About'+'-' +'SSN';
        String proId1 = '360306642'+'-'+'Interacting_About'+ '-' +'Member-Id-Base'; 
        Map<Integer,String> mapProviderID = new Map<Integer,String>();
        mapProviderID.put(0,proId);
        mapProviderID.put(1,proId);
        mapProviderID.put(2,proId1);
        Map<String,List<Account>> mapUnknownAccounts = new Map<String,List<Account>>();
        List<Account> lstacc=new List<Account>();
        Account acc5 = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
        insert Acc5;
        lstacc.add(Acc5);

        CaseInteractionSearch_H_HUM.insertUnknownAccounts(mapProviderID,mapUnknownAccounts);    
        CreateInteractionResponse_DTO_HUM response1 = CreateInteractionWithCaseRest_D_HUM.createInteraction(owrap);
        CaseInteractionSearch_H_HUM.getErrorLogInstance('204','Test Exception sceanrio',false);
        system.assertEquals(response1,NULL);
           
          methResp= CaseInteractionSearch_H_HUM.getCaseErrorLogs(lstCaseResponses);
           CaseInteractionSearch_H_HUM.insertConsumerTaxIds(null);           
           List<String> retValue= CaseInteractionSearch_H_HUM.createCaseInteractionRecords(lstCaseResponses, intractId, listCases);
        Test.StopTest();
        }
    }
}