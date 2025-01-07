@isTest
private class HUMCaseServiceRestTest_V4 {
 /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
	{
    
        User oUser = HUMTestDataHelperTest.createTestUser('V4ServiceUser1','System Administrator');
        insert oUser;
      
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
            ProviderAcc.ETL_Record_Deleted__c = false;
            ProviderAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert ProviderAcc;
            System.assert(ProviderAcc.Name == 'test account');
			
			//Member Account
            Account MembAcc = HUMTestDataHelperTest.getAccount();
            MembAcc.Name = 'test Member account';
            MembAcc.Enterprise_ID__c = '123456789';
            //MembAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();
            insert MembAcc;
            System.assert(MembAcc.Name == 'test Member account');
			
			//Classification
			Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
			classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
			system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
			
			//Intent
			Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
			
			//Case Details
			Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Group';
            insert caseObj ;
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Group', true);
            
            //classification Type Calls
             Classification_Type__c classificationType1 = new Classification_Type__c(name = 'Calls', Is_Active__c = true);
             insert classificationType1 ;
             system.assertNotEquals(classificationType1, null , 'classificationType  not created');
            
            //Classification
			Classification__c classificationRecDec1 = new Classification__c();
            classificationRecDec1.Name = 'Billing Issues';
			classificationRecDec1.Is_Active__c = true;
            insert classificationRecDec1;
			system.assertEquals(classificationRecDec1.Name == 'Billing Issues',true);
			
			//Intent
			Intent__c intentRec1 = new Intent__c();
            intentRec1.Name = 'Info Given';           
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            system.assertEquals(intentRec1.name == 'Info Given',true);
            
             List<CTCI_Junction__c> listctciJunction1 = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction1  =  new CTCI_Junction__c(Classification__c =  classificationRecDec1.ID, Classification_Type__c = classificationType1.ID, Intent__c = intentRec1.ID , IsActive__c = true);
             listctciJunction1.Add(ctciJunction1);
             insert listctciJunction1 ;
             system.assertNotEquals(intentRec1, null ,'CTCIJunctionlist  not created');
			
			//Case Details
			Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.Interacting_With__c = oAcc.Id;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseObj1.Classification_ID__c = classificationRecDec1.ID;
            caseObj1.Intent_ID__c  = intentRec1.id;
            caseobj1.Interacting_With_Type__c = 'Group';
            insert caseObj1 ;
            System.assertEquals(caseobj1.Interacting_With_Type__c == 'Group', true);
			
			//for create case method
            Group testGroup = new group(name = 'Louisville RSO Enrollment' , type = 'Queue');
            insert testGroup;                     
            system.assertNotEquals(testGroup, null , 'Group  not created'); 
            
			//Queue			
            QueueSobject mappingObject = new QueueSobject(QueueId = testGroup.Id, SobjectType = 'Case');
            insert mappingObject;
            system.assertNotEquals(mappingObject, null, 'Value not matched');
            
			//WorkQueue			
            Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Louisville RSO Enrollment' , IsActive__c= true);
            insert oQSetup ;
            system.assertNotEquals(oQSetup, null, 'Value not matched');
            
			//QueueView
			Queue_View__c oQv1 = new Queue_View__c(Queue_View_Name__c='Louisville RSO Enrollment', Department__c='Enrollment', Work_Queue_Name__c=oQSetup.Id); 
            insert oQv1;        
            system.assertNotEquals(oQv1, null, 'Value not matched');
        
			//Constant
             HUMConstants__c test1  = new  HUMConstants__c();
             test1.name = 'UNKNOWN_MEMBER';
             test1.stringValue__C = 'Unknown Member';                          
             insert test1;
             system.assertNotEquals(test1,null, 'Value not matched');
			 
			 //Add Account
			 List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             lstAccount.Add(acc);
			 Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');
			 
			 //Product
			 list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
			 listProduct.Add(testProduct);
			 Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
			 
			 //Policy
			 list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
             listPolicy.Add(testPolicy);
			 insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
			 
			 //Policy Member
			 List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
             Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             listPolicyMember.Add(memeber);
			 Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
			 
			 //Members
			 List<Member_ID__c> listmember = new List<Member_ID__c>();
             Member_ID__c member = new Member_ID__c(Policy__c = listPolicy[0].ID,Policy_Member__c = listPolicyMember[0].ID, Member_Card_External_ID__c= '123345' , name = '360306641' , Type__c ='SSN');
             listmember.Add(member);
			 insert listmember;
             system.assertNotEquals(listmember, null , 'Members  not created'); 

             //Account
			 List<Account> lstAccount2 = New  List<Account>();
             Account acc2 = New Account(firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             lstAccount2.Add(acc2);
			 Insert lstAccount2;
             system.assertNotEquals(lstAccount2, null , 'Accounts not created');
            
             //Policy Member
			 List<Policy_Member__c>  listPolicyMember2 = New List<Policy_Member__c>();
             Policy_Member__c memeber2 = New Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount2[0].ID , ETL_Record_Deleted__c = False, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
             listPolicyMember2.Add(memeber2);
			 Insert listPolicyMember2 ;
             system.assertNotEquals(listPolicyMember2, null ,  'Policies  not created for members');
            
             //MemberId
			 List<Member_ID__c> listmember2 = New List<Member_ID__c>();
             Member_ID__c member2 = New Member_ID__c(Policy__c = listPolicy[0].ID, Policy_Member__c = listPolicyMember2[0].ID, Member_Card_External_ID__c = '12345' , name = 'H03412567' , Type__c ='Member-Id-Base',Member_Dependent_Code__c='00');
             listmember2.Add(member2);
			 insert listmember2;
             system.assertNotEquals(listmember2, null , 'Members  not created'); 
			 
			 //Record types
			 List<HUMAccountCaseRecordTypes__c> HUMAccountCaseRecordTypesList = new List<HUMAccountCaseRecordTypes__c>();
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes = new HUMAccountCaseRecordTypes__c(Name = 'Unknown Member' ,Account_RecordType__c = 'Unknown Member' , Case_RecordType__c = 'Unknown Case');
             HUMAccountCaseRecordTypes__c HUMAccountCaseRecordTypes1 = new HUMAccountCaseRecordTypes__c(Name = 'Member' ,Account_RecordType__c = 'Member' , Case_RecordType__c = 'Member Case');
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes); 
             HUMAccountCaseRecordTypesList.add(HUMAccountCaseRecordTypes1);         
             insert HUMAccountCaseRecordTypesList;
             system.assertNotEquals(HUMAccountCaseRecordTypesList, null , 'Recordtypes  not created');
			 
			 //Enrollment
			 List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c> MedEnrollmentSubTypesList = new List<MEDENROLLMENT_CASE_SUBTYPE_HUM__c>();
             MEDENROLLMENT_CASE_SUBTYPE_HUM__c MedEnrollmentSubTypes = new MEDENROLLMENT_CASE_SUBTYPE_HUM__c(Name = 'Routine' ,SubType__c = 'Routine');
             MedEnrollmentSubTypesList.add(MedEnrollmentSubTypes);        
             insert MedEnrollmentSubTypesList ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null ,  'MedEnrollmentSubTypesLists  not created');
			 
			 //Interaction About
			 List<HUMCaseInteractionAboutType__c > HUMCaseInteractionAboutTypesList = new List<HUMCaseInteractionAboutType__c>();
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes = new HUMCaseInteractionAboutType__c(Name = 'Unknown Member' ,CaseInteraction_About_Type__c = 'Unknown-Member' );
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes1 = new HUMCaseInteractionAboutType__c(Name = 'Provider' ,CaseInteraction_About_Type__c = 'Provider' );
             HUMCaseInteractionAboutType__c HUMCaseInteractionAboutTypes2 = new HUMCaseInteractionAboutType__c(Name = 'Group', CaseInteraction_About_Type__c = 'Group');
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes);
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes1);        
             HUMCaseInteractionAboutTypesList.add(HUMCaseInteractionAboutTypes2);
             insert HUMCaseInteractionAboutTypesList;
             system.assertNotEquals(HUMCaseInteractionAboutTypesList, null ,'HUMCaseInteractionAboutTypesLists  not created');
			 
			 //Classification
			 Classification__c classfication = new Classification__c(name = 'Chronic Care SNP' ,Is_Active__c = true);
             insert classfication ;
             system.assertNotEquals(MedEnrollmentSubTypesList, null, 'classfication  not created');
            
			//classification Type 
             Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
             insert classificationType ;
             system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
			//Intent
             Intent__c intent = new Intent__c(name = 'Denied' , Is_Active__c = true);
             insert intent ;
             system.assertNotEquals(intent, null ,'classificationType  not created');
            
			//CTCI Junction 
             List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
             CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classfication.ID, Classification_Type__c = classificationType .ID, Intent__c = intent .ID , IsActive__c = true);
             listctciJunction.Add(ctciJunction);
             insert listctciJunction ;
             system.assertNotEquals(intent, null ,'CTCIJunctionlist  not created');
			 
			 List<CaseComment> lstCaseComment = new List<CaseComment>();
             CaseComment objCaseComment = HUMTestDataHelperTest.getCaseComm();
             objCaseComment.ParentId = caseObj.Id;
             lstCaseComment.Add(objCaseComment);
             insert lstCaseComment ;
             System.assertNotEquals(lstCaseComment, null, 'CaseComment not created');  
            
             List<CaseComment> lstCaseComment1 = new List<CaseComment>();
             CaseComment objCaseComment1 = HUMTestDataHelperTest.getCaseComm();
             objCaseComment1.ParentId = caseObj1.Id;
             lstCaseComment1.Add(objCaseComment1);
             insert lstCaseComment1 ;
             System.assertNotEquals(lstCaseComment1, null, 'CaseComment not created');  
            
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
            
            Attachment__c objAttachment1 = HUMTestDataHelperTest.getCustomAttachment();
             objAttachment1.Attachment_URL_Text__c = 'ehub.com';
             objAttachment1.Name = 'ehub.com';
             objAttachment1.Related_To_Case__c = caseObj1.Id;
             insert objAttachment1;
             System.assertNotEquals(objAttachment1, null, 'Attachment not created'); 
             
             Attachment_Log__c objAttachmentLog1 = HUMTestDataHelperTest.getAttachLog();
             objAttachmentLog1.Attachment_Key__c = '1254567';
             objAttachmentLog1.Attachment_Type__c = 'Test';
             objAttachmentLog1.Case__C = caseObj1.Id;
             insert objAttachmentLog1;
             System.assertNotEquals(objAttachmentLog1, null, 'AttachmentLog not created'); 
             
             Attachment_Log_Item__c objAttachmentLogItem1 = HUMTestDataHelperTest.getAttLogItem();
             objAttachmentLogItem1.Name = 'Test';
             objAttachmentLogItem1.Attachment_Log__c = objAttachmentLog1.Id;
             insert objAttachmentLogItem1;
             System.assertNotEquals(objAttachmentLogItem1, null, 'AttachmentLogItem not created');
             
               CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c();
                objCS.Name = '2027311';
                objCS.IsON__c=true;
                objCS.Release_Name__c='09/24/2021';
                objCS.Scrum_Team__c='Transformers';
                objCS.Story_Title__c='Test flag';
                insert objCS;
               System.assertNotEquals(objCS, null, 'Flag not created');
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
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
              updateCaseInfoRequest.CaseNumber = caseNumber;            
              updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='Yes';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='Yes';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate=dateTime.now().format('**dd/MM/yyyy**');
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = 'New Attachment';
            attachmentDTO1.AttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();     
           
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
         
           
            Test.StopTest();
        }
    }	
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCase_Negative()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
              updateCaseInfoRequest.CaseNumber = caseNumber;            
              updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='Yes';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='Yes';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate='08/20/2020';
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = null;
            attachmentDTO1.AttachmentUrl = 'abc';
            attachmentDTO1.VersionNumber = '123456';
            attachmentDTO1.DocId = '231548';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();     
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest1 = new UpdateCaseDetails_DTO_V4_HUM();
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
         
           
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailsRequestNull()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM(); 
          request = NULL;
          UpdateCase_D_V4_HUM.updateCaseDetails(request);
        }
    }
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailComplaintUpdates()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
              updateCaseInfoRequest.CaseNumber = caseNumber;            
              updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='No';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='No';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate='09/23/2020';
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = 'New Attachment';
            attachmentDTO1.AttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();   
			           
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
         
           
            Test.StopTest();
        }
    }
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailsNegitiveScenarios()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
              updateCaseInfoRequest.CaseNumber = ''; 
          	  updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='No';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='No';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate='09/23/2020';
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = 'New Attachment';
            attachmentDTO1.AttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();   
			           
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            updateCaseInfoRequest.CaseNumber = '1000057149014'; 
         	CreateUpdateCaseServiceRest_V4_HUM.updateCase();
           
            Test.StopTest();
        }
    }
    
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailsdifferentCase()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
               updateCaseInfoRequest.CaseNumber = '1000057149014';
          	  updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='No';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='No';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate=dateTime.now().format('**dd/MM/yyyy**');
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = 'New Attachment';
            attachmentDTO1.AttachmentUrl = 'New Attachment Url';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = 'Test';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = 'Description';
            logAttachemntRequest1.AttachmentKey = 'Key';
            logAttachemntRequest1.AttachmentType = 'Type';
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();   
			           
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
            Test.StopTest();
        }
    }
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailsDatabaseUpdtaeFailure()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
               updateCaseInfoRequest.CaseNumber = '1000057149014';
          	  updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='No';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='No';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate=dateTime.now().format('**dd/MM/yyyy**');
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            List<UpdateCaseDetails_DTO_V4_HUM.Attachment> listAttachmentDTO = new List<UpdateCaseDetails_DTO_V4_HUM.Attachment>();
            
            UpdateCaseDetails_DTO_V4_HUM.Attachment attachmentDTO1 = new UpdateCaseDetails_DTO_V4_HUM.Attachment();
            attachmentDTO1.FileName = null;
            attachmentDTO1.AttachmentUrl = 'abc';
            attachmentDTO1.VersionNumber = '123456';
            attachmentDTO1.DocId = '231548';
            listAttachmentDTO.add(attachmentDTO1);                     
            
            updateCaseRequest.Attachment = listAttachmentDTO; 
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>  logAttachemntRequest = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentLog>();
            UpdateCaseDetails_DTO_V4_HUM.AttachmentLog  logAttachemntRequest1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentLog();
            
            List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute> listAttachemntAttribute = new List<UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute>();
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute1 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            attachemntAttribute1.AttachmentName = '';
            attachemntAttribute1.AttachmentValue = 'Test Value';
            listAttachemntAttribute.add(attachemntAttribute1);
            
            UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute attachemntAttribute2 = new UpdateCaseDetails_DTO_V4_HUM.AttachmentAttribute();
            listAttachemntAttribute.add(attachemntAttribute2);
            
            logAttachemntRequest1.AttachmentAttribute = listAttachemntAttribute;
            //logAttachemntRequest1.CreatedBy = oUser.Id;
            logAttachemntRequest1.AttachmentDescription = '';
            logAttachemntRequest1.AttachmentKey = '';
            logAttachemntRequest1.AttachmentType = '';
           
            logAttachemntRequest.add(logAttachemntRequest1);
            
            updateCaseRequest.AttachmentLog = logAttachemntRequest;
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();   
			case casenew = new case();    
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            UpdateCase_D_V4_HUM.createCaseComments(updateCaseRequest,casenew);
            UpdateCase_D_V4_HUM.updateAttachmentsLogs(updateCaseRequest,casenew);
            UpdateCase_D_V4_HUM.createCaseAttachments(updateCaseRequest,casenew);
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
            Test.StopTest();
        }
    }
    /**
    * This is a test method for Update Case Info Service -HUMCaseServiceRest 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testHUMCaseServiceRest_UpdateCaseDetailseNoTemplate()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            Case objCase = [Select CaseNumber,Interacting_With_Type__c from Case Where Interacting_With_Type__c = 'Group' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c == 'Group', true); 
            String caseNumber  = objCase.CaseNumber ;
           // String caseStatus = 'O';
            
            UpdateCaseRequest_V4_HUM request = new UpdateCaseRequest_V4_HUM();
            UpdateCaseRequest_V4_HUM.UpdateCaseRequest updateCaseInfoRequest = new UpdateCaseRequest_V4_HUM.UpdateCaseRequest();
            UpdateCaseDetails_DTO_V4_HUM updateCaseRequest = new UpdateCaseDetails_DTO_V4_HUM();
            
               updateCaseInfoRequest.CaseNumber = '1000057149014';
          	  updateCaseRequest.CaseType='Correspondence Inquiry';
              updateCaseRequest.CaseStatus='In Progress';
              updateCaseRequest.Origin='Correspondence';
              updateCaseRequest.CaseSubType='Creditable Coverage';
              updateCaseRequest.Priority='Test';
              updateCaseRequest.TopicName='Test';
              updateCaseRequest.WorkQueueViewName='Test';
              updateCaseRequest.OwnerQueue='Louisville RSO Calls Support';
              updateCaseRequest.ClassificationType='Calls';
              updateCaseRequest.Classification='Billing Issues';
              updateCaseRequest.Intent='Info Given';
              updateCaseRequest.Department='Retail Service Operations (RSO) Calls Support';
              updateCaseRequest.ServiceCenter='Louisville';
              updateCaseRequest.Complaint='No';
              updateCaseRequest.ComplaintReason='Enrollment';
              updateCaseRequest.ComplaintType='Medical';
              updateCaseRequest.GARightsGiven='No';
              updateCaseRequest.GAReason='Test';
              updateCaseRequest.FollowupDueDate=dateTime.now().format('**dd/MM/yyyy**');
              updateCaseRequest.ElectionType='Test';
              updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
              request.UpdateCaseRequest=updateCaseInfoRequest;
                      
            
            
            
            List<UpdateCaseDetails_DTO_V4_HUM.CaseComment> caseCommentslist=new List<UpdateCaseDetails_DTO_V4_HUM.CaseComment>();
            UpdateCaseDetails_DTO_V4_HUM.CaseComment  caseCommentsDetails= new UpdateCaseDetails_DTO_V4_HUM.CaseComment();
            caseCommentsDetails.CommentBody='New Comment Test';
            caseCommentslist.add(caseCommentsDetails);
            updateCaseRequest.CaseComments = caseCommentslist;
            
          
            updateCaseInfoRequest.UpdateCaseDetails = updateCaseRequest;	
            request.UpdateCaseRequest = updateCaseInfoRequest;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();   
			
            CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            
            Test.StopTest();
        }
    }
   
     /**
    * This is a test Clss for CreateUpdtaeCaseserviceRest_D_HUM
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCreateUpdtaeCaseserviceRest_D()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          
           
            Test.startTest();
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
                cretaeCaseReq1 =null;
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'ASSOC INC';
      			interactingAccountDTO.IDType = 'Member-Id-Base';
      			interactingAccountDTO.ID = 'H68300945';
                interactingAccountDTO.FirstName = 'ADAM FLYNN';
                interactingAccountDTO.EntityType = 'Member';
                interactingAccountDTO.AccountName = 'ADAM FLYNN & ASSOC INC';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'ASSOC INC';
      			interactingAccountInternalDTO.IDType = 'Member-Id-Base';
      			interactingAccountInternalDTO.ID = 'H68300945';
                interactingAccountInternalDTO.FirstName = 'ADAM FLYNN';
                interactingAccountInternalDTO.EntityType = 'Internal';
                interactingAccountInternalDTO.AccountName = 'ADAM FLYNN & ASSOC INC';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
                CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
                 CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
            Test.stopTest();
        }
    }
     /**
    * This is a test Clss for CreateCaseRest_S_V4_HUM
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void testCreateCaseRest_S_V4()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            List<case> case1 = new List<case>();
            case1 = [select id from case limit 1];
            List<id> caseids = new List<id>();
            for(integer i=0;i<case1.size();i++)
            {
                caseids.add(case1[i].id); 
            }
            List<CreateCaseRequest_DTO_V4_HUM> lstcreateCase = new List<CreateCaseRequest_DTO_V4_HUM>();
            CreateCaseRequest_DTO_V4_HUM createCase = new CreateCaseRequest_DTO_V4_HUM();
            CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';
                
            
            createCase.caseCreateRequest = casedetailDTO;
            createCase.sInteractingWith = 'H68300945';
    		createCase.sInteractingAbout = 'H68300945';
   			createCase.sInteractingAboutType = 'Member-Id-Base';
    		createCase.sInteractingWithType = 'Member-Id-Base';
   			createCase.sInteractionId = 'H68300945';
			createCase.sInteractingWithName = 'ADAM FLYNN & ASSOC INC';
    		createCase.sCaseIdentifier = 'H68300945';
            
            lstcreateCase.add(createCase);
            map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM> mapRequestDTO = new map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM>(); 
            
            CreateMultipleCaseWrapper oCaseWrap = new CreateMultipleCaseWrapper();
            oCaseWrap.sCaseIdentifier = 'H68300945';
            oCaseWrap.caseRecord = case1[0];
            mapRequestDTO.put(oCaseWrap, createCase);
            
            CreateCaseRest_S_V4_HUM createcaserestS = new CreateCaseRest_S_V4_HUM(caseids,lstcreateCase);
            //CreateCaseRest_S_V4_HUM createcaserestS1 = new CreateCaseRest_S_V4_HUM(mapRequestDTO);
            Test.startTest();
            		system.enqueueJob(createcaserestS);
            test.stopTest();
            
            
        }
    }
    
    static testMethod void testCreateCaseRequest_V4()
    {
          HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
            
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
            
            
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'ASSOC INC';
      			interactingAccountDTO.IDType = 'Member-Id-Base';
      			interactingAccountDTO.ID = 'H68300945';
                interactingAccountDTO.FirstName = 'ADAM FLYNN';
                interactingAccountDTO.EntityType = 'Member';
                interactingAccountDTO.AccountName = 'ADAM FLYNN & ASSOC INC';
            
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                createcase.SourceApp =  'testSourceApp';
                createcase.SourceKey = 'testSourceKey';
                cretaeCaseReq1.CreateCaseRequest = createcase;
            
        }
        
    }

        
     /*This method is used for known member without dependent code for member-id-base
      * 
      * @param  None      
      * @return  Void
      */
    static testMethod void testCreateUpdateCaseserviceRest_D_Member()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          
           
            Test.startTest();
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '';
                interactingAccountDTO.LastName = 'method2';
      			interactingAccountDTO.IDType = 'Member-Id-Base';
      			interactingAccountDTO.ID = 'H03412567';
                interactingAccountDTO.FirstName = 'test2';
                interactingAccountDTO.EntityType = 'Member';
                interactingAccountDTO.AccountName = 'test2 method2';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '';
                interactingAccountInternalDTO.LastName = 'method2';
      			interactingAccountInternalDTO.IDType = 'Member-Id-Base';
      			interactingAccountInternalDTO.ID = 'H03412567';
                interactingAccountInternalDTO.FirstName = 'test2';
                interactingAccountInternalDTO.EntityType = 'Member';
                interactingAccountInternalDTO.AccountName = 'test2 method2';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
            //start
            //request.UpdateCaseRequest = updateCaseInfoRequest;
            CreateCaseRequest_V4_HUM request = new CreateCaseRequest_V4_HUM();
            request = cretaeCaseReq;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            List<Member_ID__c> memList = [Select Name from Member_ID__c where Name = 'H03412567'
                and Type__c = 'Member-Id-Base'
            ];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = memList[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            system.assert(memList.size() ==1, 'list size is one');
           // Test.StartTest();               
            //CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            CreateUpdateCaseServiceRest_V4_HUM.createCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            //end
              //  CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
                // CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
            Test.stopTest();
        }
    }
    
     /*This method is used for known member with dependent code for member-id-base
      * 
      * @param  None      
      * @return  Void
      */
    static testMethod void testCreateUpdateCaseserviceRest_D_Member2()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          
           
            Test.startTest();
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'method2';
      			interactingAccountDTO.IDType = 'Member-Id-Base';
      			interactingAccountDTO.ID = 'H03412567';
                interactingAccountDTO.FirstName = 'test2';
                interactingAccountDTO.EntityType = 'Member';
                interactingAccountDTO.AccountName = 'test2 method2';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'method2';
      			interactingAccountInternalDTO.IDType = 'Member-Id-Base';
      			interactingAccountInternalDTO.ID = 'H03412567';
                interactingAccountInternalDTO.FirstName = 'test2';
                interactingAccountInternalDTO.EntityType = 'Member';
                interactingAccountInternalDTO.AccountName = 'test2 method2';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
            //start
            //request.UpdateCaseRequest = updateCaseInfoRequest;
            CreateCaseRequest_V4_HUM request = new CreateCaseRequest_V4_HUM();
            request = cretaeCaseReq;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            List<Member_ID__c> memList = [Select Name from Member_ID__c where Name = 'H03412567'
                and Type__c = 'Member-Id-Base'
            ];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = memList[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            system.assert(memList.size() ==1, 'list size is one');
           // Test.StartTest();               
            //CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            CreateUpdateCaseServiceRest_V4_HUM.createCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            //end
              //  CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
                // CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
            Test.stopTest();
        }
    }

    static testMethod void testCreateUpdtaeCaseserviceRest_D_Provider()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {  
           
           List<Account> acclst = [Select id ,name,Enterprise_ID__c,NPI_ID__c,recordTypeId from account where NPI_ID__c ='1234567890'];     
                          
              ConsumerID__c oConsumerId = new ConsumerID__c();
            oConsumerId.Account__c = acclst[0].ID;
            oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
            oConsumerId.Consumer_ID__c = '12345';
            oConsumerId.ID_Type__c = 'TaxID';
            insert oConsumerId;
            System.AssertNotEquals(oConsumerId, NULL);
			        
           ConsumerID__c consumeracct = [SELECT ID,Account__c,Consumer_ID__c,ID_Type__c FROM ConsumerID__c WHERE Consumer_ID__c = '12345' LIMIT 1];
            Id [] fixedSearchResults2= new Id[1];
            fixedSearchResults2[0] = consumeracct.id;
            Test.setFixedSearchResults(fixedSearchResults2); 
            
            HUMSearchLimit__c humSearch = new HUMSearchLimit__c();
            humSearch.Name = 'Member_SearchLimit';
            humSearch.Search_Limit__c = 101;
            insert humSearch;
            
            HUM_Webservice_Callout__c oHumEPSSservice = new HUM_Webservice_Callout__c();
            oHumEPSSservice.Name = 'EPSServiceREST_V1';
            oHumEPSSservice.Request_Type__c ='POST';
            oHumEPSSservice.End_Point_URL__c = 'https://crmservicews.humana.com/EPSServiceREST_V17.12/EnterpriseProviderSearch';
            oHumEPSSservice.Active__c = true;
            oHumEPSSservice.Certificate_Name__c = 'Humana_GuidanceCare';
            oHumEPSSservice.Content_Type__c = 'application/json';
            oHumEPSSservice.Timeout__c = '12000';
            oHumEPSSservice.Enable_Service_Tester__c = true;
            insert oHumEPSSservice;
            
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'ASSOC INC';
      			interactingAccountDTO.IDType = 'TaxID';
      			interactingAccountDTO.ID = '12345';
                interactingAccountDTO.FirstName = 'ADAM FLYNN';
                interactingAccountDTO.EntityType = 'Provider';
                interactingAccountDTO.AccountName = 'ADAM FLYNN & ASSOC INC';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'ASSOC INC';
      			interactingAccountInternalDTO.IDType = 'TaxID';
      			interactingAccountInternalDTO.ID = '12345';
                interactingAccountInternalDTO.FirstName = 'ADAM FLYNN';
                interactingAccountInternalDTO.EntityType = 'Provider';
                interactingAccountInternalDTO.AccountName = 'ADAM FLYNN & ASSOC INC';	

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
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                cretaeCaseReq.CreateCaseRequest = createcase;    
                      
            CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
            CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
                 Test.stopTest();
        }
    }  
    static testMethod void testCreateUpdateCaseserviceRest_D_Agent()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          
              RecordType AgentAccountRecordType = [Select Id from RecordType where Name = 'Agent/Broker'
                    and sObjectType = 'Account'
                ];
               Account acc_queryBuildTestCase = HUMTestDataHelperTest.getAccount();
                acc_queryBuildTestCase.RecordType = AgentAccountRecordType;
                acc_queryBuildTestCase.Individual_First_Name__c = 'TEST';
                acc_queryBuildTestCase.Individual_Last_Name__c = 'TEST';
                acc_queryBuildTestCase.Name = 'TEST' + ' ' + 'TEST';
                acc_queryBuildTestCase.NPI_ID__c = '1245351000';
                acc_queryBuildTestCase.BillingState = 'Alabama';
                acc_queryBuildTestCase.BillingPostalCode = '54321';
                acc_queryBuildTestCase.Agent_Id__c = '111111111';
                acc_queryBuildTestCase.Provider_Classification__c = 'F';
                insert acc_queryBuildTestCase;
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'method2';
      			interactingAccountDTO.IDType = 'AgentID';
      			interactingAccountDTO.ID = '111111111';
                interactingAccountDTO.FirstName = 'test2';
                interactingAccountDTO.EntityType = 'Agent';
                interactingAccountDTO.AccountName = 'test2 method2';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'method2';
      			interactingAccountInternalDTO.IDType = 'AgentID';
      			interactingAccountInternalDTO.ID = '111111111';
                interactingAccountInternalDTO.FirstName = 'test2';
                interactingAccountInternalDTO.EntityType = 'Agent';
                interactingAccountInternalDTO.AccountName = 'test2 method2';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
            //start
            //request.UpdateCaseRequest = updateCaseInfoRequest;
            CreateCaseRequest_V4_HUM request = new CreateCaseRequest_V4_HUM();
            request = cretaeCaseReq;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
            List<Member_ID__c> memList = [Select Name from Member_ID__c where Name = 'H03412567'
                and Type__c = 'Member-Id-Base'
            ];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = memList[0].Id;
            Test.setFixedSearchResults(fixedSearchResults);
            
            HUM_Webservice_Callout__c objWebserviceCallout = new HUM_Webservice_Callout__c();
            objWebserviceCallout.Name ='AgencySearchService';
            objWebserviceCallout.End_Point_URL__c ='https://crmservicews.humana.com/Producer/Entity/Producer.svc';
            objWebserviceCallout.Certificate_Name__c = 'Humana_GuidanceCare';
            objWebserviceCallout.Timeout__c = '12000';
            insert objWebserviceCallout;
            
            system.assert(memList.size() ==1, 'list size is one');
         
            Test.startTest();
            Test.setMock(WebServiceMock.class, new HUMAgencySearchServiceTest ());
            
               CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
               CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
            Test.stopTest();
        }
    }
          static testMethod void testCreateUpdateCaseserviceRest_D_UnknownAgent()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {
          
           
            Test.startTest();
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'Test';
      			interactingAccountDTO.IDType = 'AgentID';
      			interactingAccountDTO.ID = '';
                interactingAccountDTO.FirstName = 'UnknownAgentnew';
                interactingAccountDTO.EntityType = 'Agent';
                interactingAccountDTO.AccountName = 'UnknownAgentnew Test';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'Test';
      			interactingAccountInternalDTO.IDType = 'AgentID';
      			interactingAccountInternalDTO.ID = '';
                interactingAccountInternalDTO.FirstName = 'UnknownAgent1';
                interactingAccountInternalDTO.EntityType = 'Agent';
                interactingAccountInternalDTO.AccountName = 'UnknownAgent1 Test';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
            //start
            //request.UpdateCaseRequest = updateCaseInfoRequest;
            CreateCaseRequest_V4_HUM request = new CreateCaseRequest_V4_HUM();
            request = cretaeCaseReq;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
           //// List<Member_ID__c> memList = [Select Name from Member_ID__c where Name = 'H03412567'
           //     and Type__c = ''
           // ];
           // Id [] fixedSearchResults= new Id[1];
           // fixedSearchResults[0] = memList[0].Id;
           // Test.setFixedSearchResults(fixedSearchResults);
            
           // system.assert(memList.size() ==1, 'list size is one');
          // Test.StartTest();               
            //CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            CreateUpdateCaseServiceRest_V4_HUM.createCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            //end
              //  CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
            // CreateUpdateCaseServiceRest_D_HUM.createCaseRecord(request);
                
            Test.stopTest();
        }
    }
    static testMethod void testCreateUpdateCaseserviceRest_D_UnknownProvider()
    {
         HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test V4ServiceUser1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test V4ServiceUser1',true);
        
        System.runAs(oUser) 
        {         
           
            Test.startTest();
            	
            	CreateCaseRequest_V4_HUM cretaeCaseReq = New CreateCaseRequest_V4_HUM();
                CreateCaseRequest_V4_HUM cretaeCaseReq1 = New CreateCaseRequest_V4_HUM();
            	CreateCaseRequest_V4_HUM.CreateCaseRequestDTO createcase = new CreateCaseRequest_V4_HUM.CreateCaseRequestDTO();
                CaseDetailRequest_DTO_V4_HUM casedetailDTO = new CaseDetailRequest_DTO_V4_HUM();
               
                casedetailDTO.CaseType = 'Correspondence Inquiry';
				casedetailDTO.CaseStatus = 'In Progress';
				casedetailDTO.Origin = 'Correspondence';
				casedetailDTO.Priority = 'High';
				casedetailDTO.WorkQueueViewName = 'Louisville RSO Calls Support';
				casedetailDTO.CreatedQueue = 'Louisville RSO Calls Support';
				casedetailDTO.OwnerQueue = 'Louisville RSO Calls Support';
				casedetailDTO.ServiceCenter = 'Louisville';
				casedetailDTO.Department = 'After Hours Calls';
				casedetailDTO.ClassificationType = 'Calls (RSO)';
				casedetailDTO.Classification = 'Enrollment';
				casedetailDTO.Intent = 'AOR Issues';

                InteractingAccount_DTO_HUM interactingAccountDTO = new InteractingAccount_DTO_HUM();
                interactingAccountDTO.MemberIdDependentCode = '00';
                interactingAccountDTO.LastName = 'Test';
      			interactingAccountDTO.IDType = 'NPIID';
      			interactingAccountDTO.ID = '12345678';
                interactingAccountDTO.FirstName = 'UnknownProvider';
                interactingAccountDTO.EntityType = 'Provider';
                interactingAccountDTO.AccountName = 'UnknownProvider Test';
            
            	InteractingAccount_DTO_HUM interactingAccountInternalDTO = new InteractingAccount_DTO_HUM();
                interactingAccountInternalDTO.MemberIdDependentCode = '00';
                interactingAccountInternalDTO.LastName = 'Test';
      			interactingAccountInternalDTO.IDType = 'NPIID';
      			interactingAccountInternalDTO.ID = '1234567890';
                interactingAccountInternalDTO.FirstName = 'UnknownProvider';
                interactingAccountInternalDTO.EntityType = 'Provider';
                interactingAccountInternalDTO.AccountName = 'UnknownProvider Test';	
            
               
                createcase.CaseDetails = casedetailDTO;
                createcase.InteractingWith = interactingAccountInternalDTO;
                createcase.InteractingAbout = interactingAccountDTO;
                 cretaeCaseReq.CreateCaseRequest = createcase;
            //CreateUpdateCaseServiceRest_D_HUM temp =new  CreateUpdateCaseServiceRest_D_HUM();
           // String providerNpiid='1234567890';
           // CreateUpdateCaseServiceRest_D_HUM.getAccountRecord(providerNpiid);
            //start
            //request.UpdateCaseRequest = updateCaseInfoRequest;
            CreateCaseRequest_V4_HUM request = new CreateCaseRequest_V4_HUM();
            request = cretaeCaseReq;
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();
        
            req.requestURI = '/CaseService_V4/*';  //Request URL
            req.httpMethod = 'PUT';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);
        
            RestContext.request = req;
            RestContext.response= res;
            
           //// List<Member_ID__c> memList = [Select Name from Member_ID__c where Name = 'H03412567'
           //     and Type__c = ''
           // ];
           // Id [] fixedSearchResults= new Id[1];
           // fixedSearchResults[0] = memList[0].Id;
           // Test.setFixedSearchResults(fixedSearchResults);
            
           // system.assert(memList.size() ==1, 'list size is one');
           // Test.StartTest();               
            //CreateUpdateCaseServiceRest_V4_HUM.updateCase();
            CreateUpdateCaseServiceRest_V4_HUM.createCase();
            RestResponse response = RestContext.response;
            CaseDetailResponse_DTO_V4_HUM caseResponse = new CaseDetailResponse_DTO_V4_HUM();
            String Resp = '{"UpdateCaseResponse": { "Success": true, "CaseNumber": "1000057149013"}}';
            caseResponse = (CaseDetailResponse_DTO_V4_HUM)System.JSON.deserialize(Resp, CaseDetailResponse_DTO_V4_HUM.class);
            caseResponse.Success=true;
            system.assert(caseResponse.Success == true);
            //end
              //  CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq);
                // CreateUpdateCaseServiceRest_D_HUM.createCase(cretaeCaseReq1);
            Test.stopTest();
        }
    }
 }