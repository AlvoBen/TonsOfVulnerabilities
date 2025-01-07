/***************************************************************************************************************************************************************************
Apex Class Name  : LastInteractionServiceRest_V5_HC_Test
Version          : 1.0
Created Date     : Feb 03 2022
Function         : Test Class for Unit testing and code coverage of LastInteractionServiceRest_V5_HC_HUM and CreateUpdateCaseServiceRest_V5_HC_HUM class. 
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name             Code Review          Date                    Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Manoj MK                                        02/03/2022                 User Story 2888387: T1PRJ0170850 - Lighting- VOC Implementation
****************************************************************************************************************************************************************************/
@isTest
public class LastInteractionServiceRest_V5_HC_Test {
    /**
* This method is used to insert the data only once and called automicticaly in all methods     
* @param  None      
* @return  Void
*/
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
       // User oUser1 = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
       // insert oUser1;
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
            
            Classification__c classificationObj = new Classification__c();
            classificationObj.Name = 'Eligibility';
            insert classificationObj;
                
            Classification_Type__c classificationTypObj = new Classification_Type__c();
            classificationTypObj.Name = 'Provider Calls (Dental)';
            insert classificationTypObj;
            
            Intent__c intentObj = new Intent__c();
            intentObj.Name = 'Termed Policy';
            insert intentObj;
            
            CTCI_Junction__c ctciObj = new CTCI_Junction__c();
            ctciObj.Classification__c = classificationObj.Id;
            ctciObj.Classification_Type__c = classificationTypObj.Id;           
            ctciObj.Intent__c = intentObj.Id;
            insert ctciObj;
                
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseObj.Interacting_With_Type__c = 'Group';
            caseObj.Created_By_Queue__c ='Louisville RSO Calls Support';
            caseObj.CTCI_List__c = ctciObj.Id;
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
            objInteraction.UUID__c = '011717000094440000310S8886667730';
            objInteraction.Interacting_With__c = oAcc.Id;
            objInteraction.Interacting_With_type__c ='Group';
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
    
    
 private static testMethod void testLastInteraction()
    {
        
        Interaction__c interaction = [Select id,Name,UUID__c,LastModifiedBy.Network_User_Id__c,LastModifiedDate,CreatedByID from  Interaction__c Where UUID__c = '011717000094440000310S8886667730' Limit 1];
        LastInteractionServiceRest_V5_HC_HUM.setLastInteractionDetails(interaction,null);
        Interaction_Member__c interactionMem =[Select id,Interaction__c from Interaction_Member__c Where Interaction__c =:interaction.Id Limit 1];
        List<Interaction_Member__c> interactionMemList = new List<Interaction_Member__c>();
        interactionMemList.add(interactionMem);
        Account acc= [SELECT Id, Name,Enterprise_ID__c,Group_Number__c,Source_Platform_Code__c,recordTypeId from account where Name ='test account' Limit 1];
        Case_Interaction__c caseInnteraction = [SELECT Id,Case__c,Case__r.Service_Center__c,Case__r.Department__c,Case__r.LastModifiedby_Queue__c,Case__r.CaseNumber,
                                                case__r.CreatedBy.Name,case__r.Created_By_Queue__c,Case__r.CTCI_List__c,Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Classification__c,
                                                Interaction__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name FROM Case_Interaction__c Where Interaction__c =:interaction.Id Limit 1];
        LastInteractionServiceRest_V5_HC_HUM.caseIntQuery(interaction.ID);
        LastInteractionServiceRest_V5_HC_HUM.getLstOfInteraction(interactionMemList);
        LastInteractionServiceRest_V5_HC_HUM.getCaseInteractionRecords(interaction.Id,System.today(),System.today()+10,acc.Id);
		LastInteractionServiceRest_V5_HC_HUM.getCaseInteractionRecords(interaction.Id,System.today(),System.today()+10);
        LastInteractionServiceRest_V5_HC_HUM.setLastInteractionDetails(interaction,caseInnteraction);
        LastInteractionServiceRest_V5_HC_HUM.getInteractionWithDetails(acc.Id,System.today(),System.today()+10);
    }

    
    /*  Test method to unit test the scenario where UUID only is in the Request 
* @ param    : None
* @ return   : void
*/
    private static testMethod void testUUIDRequest()
    {
        User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User1'];
        System.runAs(ETL_User)
        { 
            LastInteractionRequest_DTO_HUM echoRequest = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest request = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            request.UUID = '011717000094440000310S8886667730';
            echoRequest.LastInteractionDetailRequest = request;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"2000015493675","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);			
            //echoResponse = System.JSON.serialize(Resp);
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            System.assertNotEquals(echoResponse,null);
            System.assertEquals(res.statuscode,200);            
            Test.stopTest();
            
        }           
    } 
     private static testMethod void testUUIDRequest_Negative()
    {
        User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User1'];
        System.runAs(ETL_User)
        { 
            LastInteractionRequest_DTO_HUM echoRequest = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest request = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            request.UUID = '6667730';
            echoRequest.LastInteractionDetailRequest = request;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"2000015493675","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);			
            //echoResponse = System.JSON.serialize(Resp);
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            System.assertNotEquals(echoResponse,null);
            System.assertEquals(res.statuscode,200);            
            Test.stopTest();
            
        }           
    }   
   
    /*  Test method to unit test the scenario where MemberID only is in the Request 
    * @ param    : None
    * @ return   : void
    */
    private static testMethod void testMemberIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId, LastName from User where LastName = 'test User1' LIMIT 1 ] ;
       List<Account> acc= [SELECT Id, Name,Enterprise_ID__c,Group_Number__c,Source_Platform_Code__c,recordTypeId,CreatedById  from account where CreatedById  =:oUser.id]; 
	System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
           
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id =  acc[1].id;//360306641
            intAbout.IdType = 'MemberID';
            intAbout.EntityType = 'Member';
            intAbout.MemberIdDependentCode ='00';
            intAbout.Platform='LV';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id =  acc[1].id;//360306641
            intWith.IdType = 'MemberID';
            intWith.EntityType = 'Member';
            intWith.MemberIdDependentCode = '00';
            intWith.Platform='LV';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-2).format();
            lastIntReq.StartDate= sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();            
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();           
            
        }
    } 
    
     /*  Test method to unit test the scenario where MemberID only is in the Request 
    * @ param    : None
    * @ return   : void
    */
    private static testMethod void testMemberSSNRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '360306642';
            intAbout.IdType = 'SSN';
            intAbout.EntityType = 'Member';
            intAbout.MemberIdDependentCode ='00';
            intAbout.Platform='LV';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '360306642';
            intWith.IdType = 'SSN';
            intWith.EntityType = 'Member';
            intWith.MemberIdDependentCode = '00';
            intWith.Platform='LV';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate= sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();  
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();           
            
        }
    } 
  /*  Test method to unit test the scenario where MemberID only is in the Request 
    * @ param    : None
    * @ return   : void
    */
    private static testMethod void testMemberMedicareIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '1123456';
            intAbout.IdType = 'MedicareID';
            intAbout.EntityType = 'Member';
            intAbout.MemberIdDependentCode ='00';
            intAbout.Platform='LV';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '1123456';
            intWith.IdType = 'MedicareID';
            intWith.EntityType = 'Member';
            intWith.MemberIdDependentCode = '00';
            intWith.Platform='LV';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate= sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();           
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();           
            
        }
    } 
    /*  Test method to unit test the scenario where TaxID only is in the Request 
    * @ param    : None
    * @ return   : void
    */
	
	private static testMethod void testTAXIDRequest_IntAboutEmpty()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '';
            intAbout.IdType = '';
            intAbout.EntityType = '';
            intAbout.MemberIdDependentCode ='';
            intAbout.Platform='';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '5478451';
            intWith.IdType = 'TAXID';
            intWith.EntityType = 'Agent';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();         
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;  
            Test.stopTest();
            
        }
    }
	
     /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testAgentIDRequest_IntWith()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId from User where LastName = 'test User1' LIMIT 1 ] ;
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            //LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
           // lastIntReq.UUID = '';          
            intWith.Id = '5478451';
            intWith.IdType = 'AgentID';
            intWith.EntityType = 'Agent';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
             String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();           
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();           
            
        }
    } 
     /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testAgentIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '5478451';
            intAbout.IdType = 'AgentID';
            intAbout.EntityType = 'Agent';
            intAbout.MemberIdDependentCode ='';
            intAbout.Platform='';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '5478451';
            intWith.IdType = 'AgentID';
            intWith.EntityType = 'Agent';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();         
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();           
            
        }
    }
     /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testProviderIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '1234567890';
            intAbout.IdType = 'NPIID';
            intAbout.EntityType = 'Provider';
            intAbout.MemberIdDependentCode ='';
            intAbout.Platform='';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '1234567890';
            intWith.IdType = 'NPIID';
            intWith.EntityType = 'Provider';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
            String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();           
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();
                       
        }
    }  
      /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testProviderEnterpriseIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '111111111';
            intAbout.IdType = 'TaxID';
            intAbout.EntityType = 'Provider';
            intAbout.MemberIdDependentCode ='';
            intAbout.Platform='';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '123456';
            intWith.IdType = 'EnterpriseID';
            intWith.EntityType = 'Provider';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
             String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();         
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();
                       
        }
    }
       /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testGroupIDRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '632703';
            intAbout.IdType = 'GroupID';
            intAbout.EntityType = 'Group';
            intAbout.MemberIdDependentCode ='00';
            intAbout.Platform='EM';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '632704';
            intWith.IdType = 'GroupID';
            intWith.EntityType = 'Group';
            intWith.MemberIdDependentCode = '00';
            intWith.Platform='EM';
            lastIntReq.InteractingWithCriteria=intWith;
             String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();           
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"99999","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"99999","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();
                       
        }
    } 
       /*  Test method to unit test the scenario where MemberID only is in the Request 
	* @ param    : None
	* @ return   : void
	*/
    private static testMethod void testNullRequest()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intAbout.Id = '';
            intAbout.IdType = '';
            intAbout.EntityType = '';
            intAbout.MemberIdDependentCode ='';
            intAbout.Platform='';
            lastIntReq.InteractingAboutCriteria=intAbout;
            intWith.Id = '';
            intWith.IdType = '';
            intWith.EntityType = '';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
            lastIntReq.StartDate='';
            lastIntReq.EndDate='';
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();            
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();
                       
        }
    }
     private static testMethod void testProviderEnterpriseIDRequest_intWithOnly()
    {
        User oUser = [Select id,UserName,Current_Queue__c,ProfileId,LastName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oUser.LastName, 'test User1');
	System.runAs (oUser) {
            LastInteractionRequest_DTO_HUM owrap = new LastInteractionRequest_DTO_HUM();
            LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest lastIntReq = new LastInteractionRequest_DTO_HUM.LastInteractionDetailRequest(); 
            LastInteractionRequest_DTO_HUM.InteractingAboutCriteria intAbout = new LastInteractionRequest_DTO_HUM.InteractingAboutCriteria();
            LastInteractionRequest_DTO_HUM.InteractingWithCriteria intWith = new  LastInteractionRequest_DTO_HUM.InteractingWithCriteria();
            
            lastIntReq.SourceKey = '124565-124565-ewe54-746asfasdf';
            lastIntReq.SourceApp = 'Watson';
            lastIntReq.UUID = '';
            intWith.Id = '123456';
            intWith.IdType = 'EnterpriseID';
            intWith.EntityType = 'Provider';
            intWith.MemberIdDependentCode = '';
            intWith.Platform='';
            lastIntReq.InteractingWithCriteria=intWith;
             String sdT = system.today().addDays(-10).format();
            lastIntReq.StartDate=sdT;
            String dT = system.today().addDays(2).format();
            lastIntReq.EndDate=dT;
            owrap.LastInteractionDetailRequest = lastIntReq;
            RestResponse res = new RestResponse();
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(owrap);
            req.requestURI = '/LastInteractionServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest(); 
            CreateUpdateCaseServiceRest_V5_HC_HUM.getInteractionDetails();         
            LastInteractionResponse_DTO_HUM echoResponse = new LastInteractionResponse_DTO_HUM();
            String Resp = '{"LastInteractionDetailsResponse":{"TeamName":"Green Bay B2B","Site":"Louisville","sErrorMessage":null,"sErrorCode":null,"ReferenceType":"CRMS","Reason":"Verification","LastUpdated":"2019-09-11 05:45:12","InteractionId":"360306641","InquiryId":"","Disposition":"NA","Department":"After Hours Calls","CreatedTeamId":"Green Bay B2B","CreatedBy":"PowerAPI Data","ContactId":"","Category":"Eligibility","CaseId":"1000014927185","bIsSuccess":true,"AgentId":null}}';
            echoResponse= (LastInteractionResponse_DTO_HUM)System.JSON.deserialize(Resp, LastInteractionResponse_DTO_HUM.class);				    
            echoResponse.LastInteractionDetailsResponse.bIsSuccess=true;           
            Test.stopTest();
                       
        }
    }
}