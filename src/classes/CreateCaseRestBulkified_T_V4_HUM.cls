/******************************************************************************************************************
Apex class Name    : CreateCaseRestBulkified_T_V4_HUM
Version            : 1.0
Function           : This is test class 
Created On         : March 2019
**Modification Log
* Developer Name          Code Review #         Date                       Description
* Deepankar Binjola								10/04/2019				   REQ - 410518	Increasing the test class coverage
* Moshitha Gunasekaran                          02/04/2020                 US754949 - Ability for expanded the population of cases containing Case Initiated Date for Medicare Enrollment processing  
* Pooja Kumbhar									04/29/2020					Checkmarx test method without assert error fix.
------------------------------------------------------------------------------------------------------------------- ------------------------------------------------------------------------------------------*/
@isTest
private class CreateCaseRestBulkified_T_V4_HUM{
	/*
	 * This method creates test data for test class
	 * @param None
	 * @return None
	 */
	@testSetup static void setUpTestData()
	{
		User testUserSetUp = HUMTestDataHelperTest.createTestUser('CaseServiceV4User2','System Administrator');
		testUserSetUp.UserPermissionsLiveAgentUser = true;
		insert testUserSetUp;
		system.assertEquals(testUserSetUp.lastname =='test CaseServiceV4User2',true);
		Id memeberRecordtypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();

		system.runAs(testUserSetUp)
		{ 
			//Account Creation 
			//Creating Account Record
			Account oAcc = HUMTestDataHelperTest.getAccount();
			oAcc.OwnerId = testUserSetUp.id;
			oAcc.FirstName = 'Test';
			oAcc.LastName = 'Account';
			oAcc.Enterprise_Id__c = '123456';
			oAcc.recordtypeId = memeberRecordtypeId;
			insert oAcc;
			system.assertEquals(oAcc.recordtypeId,memeberRecordtypeId);
			Account oAcc1 = New Account();
            oAcc1.Name = 'TNO LLC';
            oAcc1.Source_Customer_Key__c ='682577';
            insert oAcc1;
			Policy__c policyRec = New Policy__c();
			policyRec.Group_Name__c = oAcc1.Id;
			policyRec.Product__c = null;
			policyRec.Major_Lob__c = 'MEF';
			insert policyRec;
			system.assertEquals(policyRec.Group_Name__c==oAcc1.Id,true);
			Policy_Member__c policyMemRec = New Policy_Member__c();
			policyMemRec.Name = '12345';
			policyMemRec.Policy__c = policyRec.Id;
			policyMemRec.Member__c = oAcc.Id; 
			policyMemRec.Effective_Date__c = system.today().adddays(-365).format();
            policyMemRec.End_Date__c = system.today().adddays(365).format();
			insert policyMemRec;
			system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
			Classification_Type__c ctRec = New Classification_Type__c();
			ctRec.name = 'Calls (RSO)';
			ctRec.Is_Active__c=true;
			insert ctRec;        
			system.assertNotEquals(ctRec.id,null);
			Classification__c  classificationRecDec1 = New Classification__c();
			classificationRecDec1.Name = 'Enrollment';
			classificationRecDec1.Is_Active__c = true;
			insert classificationRecDec1;
			system.assertNotEquals(classificationRecDec1.id,null);
			Intent__c intentRec = New Intent__c();
			intentRec.Name = 'Agent Action Required/Email Sent';
			intentRec.Is_Active__c = true;
			insert intentRec; 
			system.assertNotEquals(intentRec.id,null);
			CTCI_Junction__c ctciRec = New CTCI_Junction__c();
			ctciRec.Classification_Type__c = ctRec.id;
			ctciRec.Classification__c = classificationRecDec1.Id;
			ctciRec.Intent__c = intentRec.id;
			ctciRec.IsActive__c = true;
			insert ctciRec ; 
			system.assertNotEquals(ctciRec.id,null);
			Work_Queue_Setup__c wq=new Work_Queue_Setup__c();
			wq.name='Louisville RSO Calls Support';
		    wq.Service_Center__c = 'Louisville';
			wq.Department__c='Louisville Medicare';
            wq.Public_Group_Name__c = 'Louisville RSO Calls Support';
			wq.Classification_Type__c= ctRec.id;
			insert wq;
		}
	}  
	private static testMethod void testCreateCase(){
		User testUser = [Select Id,Name,LastName from User where LastName= 'test CaseServiceV4User2'];
        System.assertEquals(testUser.LastName, 'test CaseServiceV4User2');
		List<Account> accList = [Select id from Account where name = 'Test Account' limit 1];
		system.runAs(testUser){
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

			CaseDetailRequest_DTO_V4_HUM createObjDTO1 = new CaseDetailRequest_DTO_V4_HUM();
			createObjDTO1.Classification = 'Enrollment';
			createObjDTO1.Intent = 'Agent Action Required/Email Sent';
			createObjDTO1.ClassificationType = 'Calls (RSO)';
			createObjDTO1.ClosedDate = String.ValueOf(Date.Today());   
			createObjDTO1.Complaint = 'Yes';    
			createObjDTO1.ComplaintReason = 'Test';  
			createObjDTO1.ComplaintType = 'Test';    
			createObjDTO1.Contract = 'Test'; 
			createObjDTO1.CreatedQueue = 'Louisville RSO Calls Support'; 
			createObjDTO1.DCN = 'Test';  
			createObjDTO1.Department = 'Test';   
			createObjDTO1.DocType = 'E12';  
			createObjDTO1.ElectionType = 'Test'; 
			createObjDTO1.ExternalTrackId = 'Test';        
			createObjDTO1.FollowupDueDate = System.today().format();  
			createObjDTO1.CaseInitiatedDate = System.today().format();  
			createObjDTO1.GAReason = 'Test'; 
			createObjDTO1.GARightsGiven = 'Test';   

			createObjDTO1.MilestoneId = 'Test';  
			createObjDTO1.Origin = 'Correspondence';   
			createObjDTO1.OwnerQueue = 'Louisville RSO Calls Support';              
			createObjDTO1.PendKey = 'Test';           
			createObjDTO1.Priority = 'Test';         
			createObjDTO1.SegmentNumber = 'Test';  
			createObjDTO1.ServiceCenter = 'Test';            
			createObjDTO1.CaseStatus = 'In Progress';   
			createObjDTO1.TopicName = 'Test';   
			createObjDTO1.CaseType = 'Correspondence Inquiry'; 
			createObjDTO1.CaseSubType = 'Creditable Coverage';   
			createObjDTO1.WorkQueueViewName = 'Test';
			createObjDTO1.CaseComments = caseCommentObjList1;
			createObjDTO1.AttachmentLog = AttachmentLogList1;
			createObjDTO1.Attachment = AttachmentList1;   
			createObjDTO1.AsynchronousProcess=false;
			createObjDTO1.Department = 'Louisville Medicare';           

			CreateCaseRequest_DTO_V4_HUM mulCase = new CreateCaseRequest_DTO_V4_HUM();
			if(accList.size()>0){
            	mulCase.sInteractingWith = accList[0].id;
            }
			mulCase.sInteractingWithType= 'Member';
            if(accList.size()>0){
				mulCase.sInteractingAbout= accList[0].id;
            }
			mulCase.sInteractingAboutType= 'Member';
			mulCase.caseCreateRequest=createObjDTO1;
			request.add(mulCase);           
			Test.startTest();
			CreateCaseRestBulkified_D_V4_HUM.createCases(request,false);
			CreateCaseRestBulkified_D_V4_HUM.createCases(null,null);
			Test.stopTest();

		}
	}

	private static testMethod void testCreateCaseAsync(){
		User testUser = [Select Id,Name,LastName from User where LastName='test CaseServiceV4User2'];
        System.assertEquals(testUser.LastName, 'test CaseServiceV4User2');
		List<Account> accList = [Select id from Account where name = 'Test Account' limit 1];
		system.runAs(testUser){
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

			//Asynchronous process
			CaseDetailRequest_DTO_V4_HUM createObjDTO2 = new CaseDetailRequest_DTO_V4_HUM();
			createObjDTO2.Classification = 'Enrollment';
			createObjDTO2.intent = 'Agent Action Required/Email Sent';
			createObjDTO2.ClassificationType = 'Calls (RSO)';
			createObjDTO2.ClosedDate = 'Test';   
			createObjDTO2.Complaint = 'Test';    
			createObjDTO2.ComplaintReason = 'Test';  
			createObjDTO2.ComplaintType = 'Test';    
			createObjDTO2.Contract = 'Test'; 
			createObjDTO2.CreatedQueue = 'Louisville RSO Calls Support'; 
			createObjDTO2.DCN = 'Test';  
			createObjDTO2.Department = 'Test';   
			createObjDTO2.DocType = 'E12';  
			createObjDTO2.ElectionType = 'Test'; 
			createObjDTO2.ExternalTrackId = 'Test';        
			createObjDTO2.FollowupDueDate = System.today().format();  
			createObjDTO2.GAReason = 'Test'; 
			createObjDTO2.GARightsGiven = 'Test';   
			createObjDTO2.Intent = 'Unassigned';        
			createObjDTO2.MilestoneId = 'Test';  
			createObjDTO2.Origin = 'Correspondence';   
			createObjDTO2.OwnerQueue = 'Louisville RSO Calls Support';             
			createObjDTO2.PendKey = 'Test';             
			createObjDTO2.Priority = 'Test';         
			createObjDTO2.SegmentNumber = 'Test';  
			createObjDTO2.ServiceCenter = 'Test';            
			createObjDTO2.CaseStatus = 'In Progress';   
			createObjDTO2.TopicName = 'Test';   
			createObjDTO2.CaseType = 'Correspondence Inquiry'; 
			createObjDTO2.CaseSubType = 'Creditable Coverage';
			createObjDTO2.WorkQueueViewName = 'Test';
			createObjDTO2.CaseComments = caseCommentObjList1;
			createObjDTO2.AttachmentLog = AttachmentLogList1;
			createObjDTO2.Attachment = AttachmentList1;
			createObjDTO2.AsynchronousProcess=true;

			CreateCaseRequest_DTO_V4_HUM mulCase2 = new CreateCaseRequest_DTO_V4_HUM();
            if(accList.size()>0){
				mulCase2.sInteractingWith= accList[0].id;
            }
			mulCase2.sInteractingWithType='Member';
            if(accList.size()>0){
				mulCase2.sInteractingAbout=accList[0].id;
            }
			mulCase2.sInteractingAboutType='Member';
			mulCase2.caseCreateRequest=createObjDTO2;		
			request.add(mulCase2);

		
			Test.startTest();
			CreateCaseRestBulkified_D_V4_HUM.createCases(Null,false);
			CreateCaseRestBulkified_D_V4_HUM.createCases(request,true);         
			Test.stopTest();           
		}
	}
	
	private static testMethod void testDeepCreateCaseAsync(){
    User testUser = [Select Id,Name from User where LastName='test CaseServiceV4User2'];
        testuser.Network_User_Id__c='DAB2730'; 
        update testUser;
		System.assertEquals(testuser.Network_User_Id__c, 'DAB2730');
       
    List<Account> accList = [Select id from Account where name = 'Test Account' limit 1];
    List<Work_Queue_Setup__c> wrqueue = [Select id,Name from Work_Queue_Setup__c where Name ='Louisville RSO Calls Support'];  
        List<Intent__c> intent = [Select Id,Name from Intent__c where Name = 'Agent Action Required/Email Sent' Limit 1];
        Group grp = new Group();
        grp.Name = 'Louisville RSO Calls Support';
        insert grp;    
        GroupMember gmr=new GroupMember();
          gmr.GroupId=grp.Id;
          gmr.UserOrGroupId=testUser.Id;
          insert gmr;
        
        system.runAs(testUser){
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

      //Asynchronous process
      CaseDetailRequest_DTO_V4_HUM createObjDTO2 = new CaseDetailRequest_DTO_V4_HUM();
      createObjDTO2.Classification = 'Enrollment';
      createObjDTO2.intent = 'Agent Action Required/Email Sent';
      createObjDTO2.ClassificationType = 'Calls (RSO)';
      createObjDTO2.ClosedDate = 'Test';   
      createObjDTO2.Complaint = 'Test';    
      createObjDTO2.ComplaintReason = 'Test';  
      createObjDTO2.ComplaintType = 'Test';    
      createObjDTO2.Contract = 'Test'; 
      createObjDTO2.CreatedQueue = 'Louisville RSO Calls Support'; 
      createObjDTO2.DCN = 'Test';  
      createObjDTO2.Department = 'Test';   
      createObjDTO2.DocType = 'E12';  
      createObjDTO2.ElectionType = 'Test'; 
      createObjDTO2.ExternalTrackId = 'Test';        
      createObjDTO2.FollowupDueDate = System.today().format();  
      createObjDTO2.GAReason = 'Test'; 
      createObjDTO2.GARightsGiven = 'Test';   
      createObjDTO2.Intent = 'Unassigned';        
      createObjDTO2.MilestoneId = 'Test';  
      createObjDTO2.Origin = 'Correspondence';   
      createObjDTO2.OwnerQueue = 'Louisville RSO Calls Support';             
      createObjDTO2.PendKey = 'Test';             
      createObjDTO2.Priority = 'Test';         
      createObjDTO2.SegmentNumber = 'Test';  
      createObjDTO2.ServiceCenter = 'Test';            
      createObjDTO2.CaseStatus = 'In Progress';   
      createObjDTO2.TopicName = 'Test';   
      createObjDTO2.CaseType = 'Correspondence Inquiry'; 
      createObjDTO2.CaseSubType = 'Creditable Coverage';
      createObjDTO2.WorkQueueViewName = 'Test';
      createObjDTO2.CaseComments = caseCommentObjList1;
      createObjDTO2.AttachmentLog = AttachmentLogList1;
      createObjDTO2.Attachment = AttachmentList1;
      createObjDTO2.AsynchronousProcess=true;
      createobjDTO2.caseOwnerId=testUser.Network_User_Id__c;
      CreateCaseRequest_DTO_V4_HUM mulCase2 = new CreateCaseRequest_DTO_V4_HUM();
            if(accList.size()>0){
        mulCase2.sInteractingWith= accList[0].id;
            }
      mulCase2.sInteractingWithType='Member';
            if(accList.size()>0){
        mulCase2.sInteractingAbout=accList[0].id;
            }
      mulCase2.sInteractingAboutType='Member';
      mulCase2.caseCreateRequest=createObjDTO2;    
      request.add(mulCase2);
      
              
      Test.startTest();
      CreateCaseRestBulkified_D_V4_HUM.createCases(request,true); 
            
            createObjDTO2.AttachmentLog = null;            
            createObjDTO2.FollowupDueDate = '12345';
            mulCase2.caseCreateRequest=createObjDTO2;
            list<CreateCaseRequest_DTO_V4_HUM> requestCatch = new list<CreateCaseRequest_DTO_V4_HUM>();
            requestCatch.add(mulCase2);
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,false);
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,true);
            
      Test.stopTest();           
    }
  }
  	Private Static testMethod void testDeepCreateCaseAsync2(){
    User testUser = [Select Id, Name from User where LastName='test CaseServiceV4User2'];
        testuser.Network_User_Id__c='DAB2730'; 
        update testUser;
		System.assertEquals(testuser.Network_User_Id__c, 'DAB2730');
       
    List<Account> accList = [Select id,Name,firstName from Account where firstName = 'Test' limit 1];
    List<Account> accList1 = [Select id,Name,firstName,Source_Customer_Key__c from Account where  Name = 'TNO LLC' limit 1];   
    List<Policy__c> policyList = [Select id,Group_Name__c,Group_Name__r.Source_Customer_Key__c from Policy__c where Group_Name__c =:accList1[0].id];
    List<Policy_Member__c> policyMemList = [Select id,Name,Policy__c,Effective_Date__c, End_Date__c from Policy_Member__c where Policy__c =:policyList[0].id]; 
        
    List<Work_Queue_Setup__c> wrqueue = [Select id,Name from Work_Queue_Setup__c where Name ='Louisville RSO Calls Support'];  
        List<Intent__c> intent = [Select Id,Name from Intent__c where Name = 'Agent Action Required/Email Sent' Limit 1];
         List<Classification_Type__c> classificationTypeList =[Select id,Name,Is_Active__c from Classification_Type__c where name = 'Calls (RSO)'];
        List<Classification__c> classificationList =[Select id,Name,Is_Active__c from Classification__c where Name = 'Enrollment'];
        List<CTCI_Junction__c> ctciList = [Select id,Name,Classification_Type__c,Intent__c,IsActive__c from CTCI_Junction__c where Intent__c =:intent[0].Id];
       List<MET_Milestone__c> mMStoneList =[SELECT Id, Name, Case_Subtype__c,  Is_Active__c, Source_Name__c FROM MET_Milestone__c where Case_Subtype__c ='Creditable Coverage'];
        
        Group grp = New Group();
        grp.Name = 'Louisville RSO Calls Support';
        insert grp;
        
        GroupMember gmr = New GroupMember();
          gmr.GroupId=grp.Id;
          gmr.UserOrGroupId=testUser.Id;
          insert gmr;
        
        system.runAs(testUser){
      //      setCheckMarxSwitch(true);
      list<CreateCaseRequest_DTO_V4_HUM> request = New list<CreateCaseRequest_DTO_V4_HUM>();
      //Attachment attribute 1
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute> oattachAttributeList = New List<CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute>();
      CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute oattachAttribute = New CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute();
      oattachAttribute.AttachmentName = 'RequestId';
      oattachAttribute.AttachmentValue = '45678';
      oattachAttributeList.add(oattachAttribute);

      //Comment 1        
      List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentObjList1 = New List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
      CaseDetailRequest_DTO_V4_HUM.CaseComment caseCommentObj1 = New CaseDetailRequest_DTO_V4_HUM.CaseComment();
      caseCommentObj1.CommentBody = 'New comment';
      caseCommentObjList1.add(caseCommentObj1);

      //Attachment Log 1       
      List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> AttachmentLogList1 = New List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();        
      CaseDetailRequest_DTO_V4_HUM.AttachmentLog AttachmentLog1 = New CaseDetailRequest_DTO_V4_HUM.AttachmentLog();        
      AttachmentLog1.AttachmentKey = '3439403403';
      AttachmentLog1.AttachmentType = 'MRM';
      AttachmentLog1.AttachmentDescription = 'MRM attachment description';
      AttachmentLog1.AttachmentAttribute = oattachAttributeList;
      AttachmentLogList1.add(AttachmentLog1);

      //Attachment 1
      List<CaseDetailRequest_DTO_V4_HUM.Attachment> AttachmentList1 = New List<CaseDetailRequest_DTO_V4_HUM.Attachment>();        
      CaseDetailRequest_DTO_V4_HUM.Attachment Attachment1 = New CaseDetailRequest_DTO_V4_HUM.Attachment();        
      Attachment1.DocId = '66585745';
      Attachment1.attachmentURL ='ehub.com';
      Attachment1.fileName = 'ehub.com';
      Attachment1.versionNumber ='1';
      AttachmentList1.add(Attachment1);

      //Asynchronous process
      CaseDetailRequest_DTO_V4_HUM createObjDTO2 = New CaseDetailRequest_DTO_V4_HUM();
            createObjDTO2.ClassificationType = classificationTypeList[0].Name;
            createObjDTO2.Classification = classificationList[0].Name;
            createObjDTO2.Intent = intent[0].Name; 
            createObjDTO2.ClosedDate = '';   
            createObjDTO2.Complaint = 'No';    
            //createObjDTO2.ComplaintReason = 'Enrollment';  
            createObjDTO2.ComplaintType = 'Medical';    
            createObjDTO2.Contract = ''; 
            createObjDTO2.CreatedQueue = 'Louisville RSO Calls Support'; 
            createObjDTO2.DCN = '';  
            createObjDTO2.DCNType ='';
            createObjDTO2.DCNURL = '';
            createObjDTO2.Department = 'Louisville Medicare';   
            createObjDTO2.DocType = 'E12';  
            createObjDTO2.ElectionType = ''; 
            createObjDTO2.ExternalTrackId = '';   
            createObjDTO2.FollowupDueDate = '';  
            createObjDTO2.GAReason = ''; 
            createObjDTO2.GARightsGiven = 'No';             
            createObjDTO2.MilestoneId = '123';  
            createObjDTO2.Origin = 'Correspondence';   
            createObjDTO2.OwnerQueue = 'Louisville RSO Calls Support';             
            createObjDTO2.PendKey = '';             
            createObjDTO2.Priority = 'Normal';         
            createObjDTO2.SegmentNumber = '';  
            createObjDTO2.ServiceCenter = 'Louisville';            
            createObjDTO2.CaseStatus = 'In Progress'; 
            createObjDTO2.CaseInitiatedDate = ''; 
            createObjDTO2.TopicName = '';   
            createObjDTO2.CaseType = 'Correspondence Inquiry'; 
            createObjDTO2.CaseSubType = 'Routine';
            createObjDTO2.WorkQueueViewName = 'other';
            createObjDTO2.PolicyMember=policyMemList[0].Name;
            createObjDTO2.EndDate = policyMemList[0].End_Date__c;
            createObjDTO2.EffectiveDate = policyMemList[0].Effective_Date__c;
            createObjDTO2.PolicyGroupID = policyList[0].Group_Name__r.Source_Customer_Key__c ; 
            createObjDTO2.MedicareDefaultPolicy = False;
           createObjDTO2.CaseComments = caseCommentObjList1;
            createObjDTO2.AttachmentLog = AttachmentLogList1;
            createObjDTO2.Attachment = AttachmentList1;
            createObjDTO2.AsynchronousProcess=true;
            createobjDTO2.caseOwnerId=testUser.Network_User_Id__c;
            CreateCaseRequest_DTO_V4_HUM mulCase2 = New CreateCaseRequest_DTO_V4_HUM();
            If (accList.size() > 0){
                mulCase2.sInteractingWith= accList[0].id;
            }
            mulCase2.sInteractingWithType='Member';
            If (accList.size() > 0){
                mulCase2.sInteractingAbout=accList[0].id;
            }
            mulCase2.sInteractingAboutType='Member';
            mulCase2.sInteractionId = '123456';
            mulCase2.sInteractingWithName = 'Test Account';
            mulCase2.sCaseIdentifier = 'H68300945';
            mulCase2.caseCreateRequest=createObjDTO2;    
            request.add(mulCase2);
            
            Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>  caseIdCommentsMap = New Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();

            Test.startTest();
            CreateCaseRestBulkified_D_V4_HUM.createCases(request,true); 
            
            //createObjDTO2.AttachmentLog = null;            
            //createObjDTO2.FollowupDueDate = '01/02/2022';
            mulCase2.caseCreateRequest=createObjDTO2;
 
            list<CreateCaseRequest_DTO_V4_HUM> requestCatch = New list<CreateCaseRequest_DTO_V4_HUM>();
            requestCatch.add(mulCase2);
            
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,false);
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,true);
            CreateCaseRestBulkified_D_V4_HUM.createCaseComments(caseIdCommentsMap);
            Test.stopTest();       
        }
    }   
    private static testMethod void testDeepCreateCaseAsync3(){
    User testUser = [Select Id,Name from User where LastName='test CaseServiceV4User2'];
        testuser.Network_User_Id__c='DAB2730'; 
        update testUser;
		System.assertEquals(testuser.Network_User_Id__c, 'DAB2730');
       
    List<Account> accList = [Select id,Name,firstName from Account where firstName = 'Test' limit 1];
    List<Account> accList1 = [Select id,Name,firstName,Source_Customer_Key__c from Account where  Name = 'TNO LLC' limit 1];   
    List<Policy__c> policyList = [Select id,Group_Name__c,Group_Name__r.Source_Customer_Key__c from Policy__c where Group_Name__c =:accList1[0].id];
    List<Policy_Member__c> policyMemList = [Select id,Name,Policy__c,Effective_Date__c, End_Date__c from Policy_Member__c where Policy__c =:policyList[0].id]; 
        
    List<Work_Queue_Setup__c> wrqueue = [Select id,Name from Work_Queue_Setup__c where Name ='Louisville RSO Calls Support'];  
        List<Intent__c> intent = [Select Id,Name from Intent__c where Name = 'Agent Action Required/Email Sent' Limit 1];
         List<Classification_Type__c> classificationTypeList =[Select id,Name,Is_Active__c from Classification_Type__c where name = 'Calls (RSO)'];
        List<Classification__c> classificationList =[Select id,Name,Is_Active__c from Classification__c where Name = 'Enrollment'];
        List<CTCI_Junction__c> ctciList = [Select id,Name,Classification_Type__c,Intent__c,IsActive__c from CTCI_Junction__c where Intent__c =:intent[0].Id];
       List<MET_Milestone__c> mMStoneList =[SELECT Id, Name, Case_Subtype__c,  Is_Active__c, Source_Name__c FROM MET_Milestone__c where Case_Subtype__c ='Creditable Coverage'];
        
        Group grp = new Group();
        grp.Name = 'Louisville RSO Calls Support';
        insert grp;
        
        GroupMember gmr=new GroupMember();
          gmr.GroupId=grp.Id;
          gmr.UserOrGroupId=testUser.Id;
          insert gmr;
        
        system.runAs(testUser){
	//		setCheckMarxSwitch(true);
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

      //Asynchronous process
      CaseDetailRequest_DTO_V4_HUM createObjDTO2 = new CaseDetailRequest_DTO_V4_HUM();
            createObjDTO2.ClassificationType = classificationTypeList[0].Name;
            createObjDTO2.Classification = classificationList[0].Name;
            createObjDTO2.Intent = intent[0].Name; 
            createObjDTO2.ClosedDate = '';   
            createObjDTO2.Complaint = 'No';    
            createObjDTO2.ComplaintReason = 'Enrollment';  
            createObjDTO2.ComplaintType = 'Medical';    
            createObjDTO2.Contract = ''; 
            createObjDTO2.CreatedQueue = 'Louisville RSO Calls Support'; 
            createObjDTO2.DCN = '';  
            createObjDTO2.DCNType ='';
            createObjDTO2.DCNURL = '';
            createObjDTO2.Department = 'Louisville Medicare';   
            createObjDTO2.DocType = 'E12';  
            createObjDTO2.ElectionType = ''; 
            createObjDTO2.ExternalTrackId = '';   
            createObjDTO2.FollowupDueDate = '';  
            createObjDTO2.GAReason = ''; 
            createObjDTO2.GARightsGiven = 'No';             
            createObjDTO2.MilestoneId = '123';  
            createObjDTO2.Origin = 'Correspondence';   
            createObjDTO2.OwnerQueue = 'Louisville RSO Calls Support';             
            createObjDTO2.PendKey = '';             
            createObjDTO2.Priority = 'Normal';         
            createObjDTO2.SegmentNumber = '';  
            createObjDTO2.ServiceCenter = 'Louisville';            
            createObjDTO2.CaseStatus = 'In Progress'; 
            createObjDTO2.CaseInitiatedDate = ''; 
            createObjDTO2.TopicName = '';   
            createObjDTO2.CaseType = 'Correspondence Inquiry'; 
            createObjDTO2.CaseSubType = 'Routine';
            createObjDTO2.WorkQueueViewName = 'other';
            createObjDTO2.PolicyMember=policyMemList[0].Name;
            createObjDTO2.EndDate = policyMemList[0].End_Date__c;
            createObjDTO2.EffectiveDate = policyMemList[0].Effective_Date__c;
            createObjDTO2.PolicyGroupID = policyList[0].Group_Name__r.Source_Customer_Key__c ; 
            createObjDTO2.MedicareDefaultPolicy = False;
           createObjDTO2.CaseComments = caseCommentObjList1;
            createObjDTO2.AttachmentLog = AttachmentLogList1;
            createObjDTO2.Attachment = AttachmentList1;
            createObjDTO2.AsynchronousProcess=true;
            createobjDTO2.caseOwnerId=testUser.Network_User_Id__c;
            CreateCaseRequest_DTO_V4_HUM mulCase2 = new CreateCaseRequest_DTO_V4_HUM();
            if(accList.size()>0){
                mulCase2.sInteractingWith= accList[0].id;
            }
            mulCase2.sInteractingWithType='Member';
            if(accList.size()>0){
                mulCase2.sInteractingAbout=accList[0].id;
            }
            mulCase2.sInteractingAboutType='Member';
            mulCase2.sInteractionId = '123456';
            mulCase2.sInteractingWithName = 'Test Account';
            mulCase2.sCaseIdentifier = 'H68300945';
            mulCase2.caseCreateRequest=createObjDTO2;    
            request.add(mulCase2);
            
            Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>  caseIdCommentsMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();

            Test.startTest();
            CreateCaseRestBulkified_D_V4_HUM.createCases(request,true);              
            
            //createObjDTO2.AttachmentLog = null;            
            //createObjDTO2.FollowupDueDate = '01/02/2022';
            mulCase2.caseCreateRequest=createObjDTO2;
 
            list<CreateCaseRequest_DTO_V4_HUM> requestCatch = new list<CreateCaseRequest_DTO_V4_HUM>();
            requestCatch.add(mulCase2);
        
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,false);
            CreateCaseRestBulkified_D_V4_HUM.createCases(requestCatch,true);
            CreateCaseRestBulkified_D_V4_HUM.createCaseComments(caseIdCommentsMap);
            Test.stopTest();       
        }
    }  
}