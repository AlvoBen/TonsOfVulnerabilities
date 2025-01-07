/**********************************************************************************************************************
Apex Class:   HUMTestDataHelperTest
Description: Utility Class for creating test data that can be used In Test Classes across the Application.
             This Class is made Public since it Is referenced by other classes.
Created By: Sagar Tapadia
Created On:  07/09/2014

Modification Log :  
-----------------------------------------------------------------------------------------------------------------------
* Developer         Code Review        Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Sagar Tapadia                       07/09/2014               Original version
* Sagar Tapadia                       07/10/2014               Changing all the methods so that it returns object record instance
                                                               And doesnt create it
* Chaitanya Kumar                      07/13/2014              Created getErrorLookUpRecord
* Sagar Tapadia         17119          08/07/2014              Renamed the Class Name as per Best Practice
* Pradee Raju           17394          08/09/2014              Updated the class with alerts methods
* Sagar Tapadia         18341          06/10.2014              Added methods for concept Article
* Vishal Verma          19758          12/08/2014              Added caller Name for Interaction
* Asha Deepika Koyyana  22312          4/13/2015               Added method dataForQueuesAndViews  
* Praveen Kumar Parimi  22420          04/16/2015              Added method to get the Member ID
* Shruthi Karanth       22485          04/20/2015              Added code to default Task Type in method getTaskRecord for PROD test class failure issue
* Harika Devi Kalidindi 11200          07/20/2017              REQ - 323435 :Added Policy_Group_Number__c field value in getPolicy() method   
* Rakesh Patni			350876		   04/11/2019			   Commented References to Article Types before Migration
* Rakesh Patni			351706		   04/15/2019			   Updated Article Type Reference with Knowledge__Kav post migration.
* Joel George						   02/19/2021			   Added Livechat transcript
*T. Sai Kumarv                         03/9/2022               Appended random value to username to make it unique when ever called from CDO test classes.
* Ekta Bist                            03/07/2022              User Story 3153697 - T1PRJ0154546 MF10 Storefront - Update 'Reason for Visit' Picklist Values on Health Educator 1x1 Tracking Form  
* Lakshmi Madduri					   02/23/2024              US-5604142 PCO App Deprecation
***********************************************************************************************************************/
@isTest
Public Class HUMTestDataHelperTest
{
    
    /*
    * Method Name   : getAccount
    * Description   : create Accunt Object record
    * Return Type   : Account
    * Parameters    : NA
    */
    Public Static Account getAccount()
    {
        Account account = New Account();
        account.Group_Number__c='11111';
        account.BillingState='New York';
        Return account;
    }
    
    /*
    * Method Name   :    getPolicy
    * Description   :    create Policy Object record
    * Return Type   :   Policy__c
    * Parameters    :   NA
    */
    Public Static Policy__c getPolicy()
    {
        Policy__c policy = New Policy__c();
        policy.Policy_Group_Number__c='11111';
        Return policy;
    }
    
    /*
    * Method Name   :    getProduct
    * Description   :    create Product Object record
    * Return Type   :   Product2
    * Parameters    :   NA
    */
    Public Static Product2 getProduct()
    {
        Product2 prd = New Product2();
        Return prd;
    }
    
    /*
    * Method Name   :    getPolicyMember
    * Description   :    create Policy Member Object record
    * Return Type   :   Policy_Member__c
    * Parameters    :   NA
    */
    Public Static Policy_Member__c getPolicyMember()
    {
        Policy_Member__c policymember = New Policy_Member__c();
        policymember.Relationship__c = 'Self';
        policymember.Effective_Date__c=Date.today().addDays(-1).format();
        policymember.End_Date__c=Date.today().addDays(1).format();
        Return policymember;
    }
    
    /*
    * Method Name   :    getCase
    * Description   :    create Case object record
    * Return Type   :   Case
    * Parameters    :   NA
    */
    Public Static Case getCase()
    {
        Case c = New Case();
        c.Complaint__c = 'No';
        c.Interacting_About_Type__c = 'Member';
        c.Interacting_With_Type__c = 'Agent';
        c.Interacting_With_Name__c = 'Test';
        c.G_A_Rights_Given__c = 'No';
        Return c;
    }
    
    /*
    * Method Name   :    getContact
    * Description   :    create Contact Object record
    * Return Type   :   Contact
    * Parameters    :   NA
    */
    Public Static Contact getContact()
    {
        Contact con = New Contact();
        con.BirthDate = system.today();
        con.LastName = 'Test Contact Last';
        Return con;
    }
    
    /*
    * Method Name   :    getInteraction
    * Description   :    create Interaction__c Object record
    * Return Type   :   Interaction__c
    * Parameters    :   NA
    */
    Public Static Interaction__c getInteraction()
    {        
        Interaction__c inte = New Interaction__c(Caller_Name__c = 'Test', Interaction_Origin__c = 'Inbound Call');
        return inte;
    }
    
    /*
    * Method Name   :    getInteractionMember
    * Description   :    create Interaction Member Object record
    * Return Type   :   Interaction_Member__c
    * Parameters    :   NA
    */
    Public Static Interaction_Member__c getInteractionMember()
    {        
        Interaction_Member__c intMem = New Interaction_Member__c();
        Return intMem;
    }
    
    
    /*
    * Method Name   :   getQueues
    * Description   :   create Queue Object record
    * Return Type   :   Group
    * Parameters    :   String
    */
    Public Static Group getQueues(String sQueueName)
    {
        Group grp = New Group(Type = 'Queue', Name = sQueueName);
        return grp;
    }

     Public Static Group insertGroup()
    {
       Group oGrp = new Group(Type='Queue',Name = 'Cincinati CRU1', DeveloperName='Cincinati_CRU1');
        insert oGrp ;
        return oGrp;
    }
   
    Public Static QueueSObject insertQueue(Id GroupId)
    {
      QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=GroupId);
      insert oQueue ;
        return oQueue;
    }
    
    Public Static GroupMember insertGroupMember(Id GroupId,Id userId)
    {
      GroupMember oGrpMem = new GroupMember(GroupId=GroupId, UserOrGroupId=userId);
        insert oGrpMem;
        return oGrpMem;
    }
    
    /*
    * Method Name   :   getCaseQueues
    * Description   :   create QueueSObject Object record
    * Return Type   :   QueueSObject
    * Parameters    :   Id
    */
    Public Static QueueSObject getCaseQueues(Id  sQueueId)
    {
        QueueSObject caseQueue = New QueueSObject(SobjectType = 'Case', QueueId = sQueueId);
        return caseQueue;
    }
    
    /*
    * Method Name   :   getGrpMems
    * Description   :   create GroupMember Object record
    * Return Type   :   GroupMember
    * Parameters    :   NA
    */
    Public Static GroupMember getGrpMems(Id gId, Id userOrGrpId)
    {
        GroupMember grpMem = New GroupMember(GroupId = gId, UserOrGroupId = userOrGrpId);
        Return grpMem ;
    }
    
    /*
    * Method Name   :    createTestUser
    * Description   :    create User Object record
    * Return Type   :   User
    * Parameters    :   String, String
    */
    Public Static User createTestUser(String userName, String profileName )
    {
        Id profileId;
        Try{
            profileId = [SELECT Id FROM Profile WHERE  name =: profileName LIMIT 1 ].Id;
        }catch (Exception ex) {
            System.assert( false, profileName + ' profile does not exist in the system');
        }
        User testUser = New User();
        testUser.LastName = 'test ' + UserName;
        testUser.Alias = (userName.length() > 8 ) ?  userName.substring(0 , 8) : userName;
        testUser.Email = UserName+'@test.com';
        testUser.Username = UserName+UserName+'@humcrmstest.com';
        testUser.ProfileId = profileId;
        testUser.CommunityNickname = 'a'+UserName;
        testUser.TimeZoneSidKey = 'America/New_York';
        testUser.LocaleSidKey = 'en_US';
        testUser.EmailEncodingKey = 'ISO-8859-1';
        testUser.LanguageLocaleKey = 'en_US';
        testUser.IsActive = true;        
        Return testUser;
    }
    
    /*
    * Method Name   :    getGroup
    * Description   :    create Queue record
    * Return Type   :   Group
    * Parameters    :   NA
    */
    Public Static Group getGroup()
    {
        Group grp = New Group();
        Return grp;
    }
    
    /*
    * Method Name   :    getGroupMember
    * Description   :    create Queue Member record
    * Return Type   :   GroupMember
    * Parameters    :   NA
    */
    Public Static GroupMember getGroupMember()
    {
        GroupMember grpMem = New GroupMember();
        Return grpMem;
    }
    
    /*
    * Method Name : getAttachmentRecord
    * Description : Initialize field values For Custom attachment record
    * Return type : Attachment__c
    * Paramater   : NA
    */
    Public Static Attachment__c getCustomAttachment()
    {
        Attachment__c objAttach = New Attachment__c();
        Return objAttach;
    }
    
    /*
    * Method Name : getTaskRecord
    * Description : Initialize field values For standard Task record
    * Return type : Task
    * Paramater   : NA
    */
    Public Static Task getTaskRecord()
    {
        Task tsk = New Task();
        tsk.Status = 'In Progress';
        tsk.Subject = 'Test Task Record';
        tsk.ActivityDate= System.today();
        tsk.Type = 'Work Task';
        Return tsk;
    }
    
    /*
    * Method Name : getAttachmentRecord
    * Description : Initialize field values For standard attachment record
    * Return type : Attachment
    * Paramater   : NA
    */
    Public Static Attachment getAttachmentRecord()
    {
        Attachment attach = New Attachment();
        attach.Name = 'TestAttachment'+String.valueOf(Math.random());
        Blob bodyBlob = Blob.valueOf('Test Document');
        attach.contentType ='text/plain';
        attach.body = bodyBlob;
        Return attach;
    }
    
    /*
    * Method Name : getCompassRecord
    * Description : Initialize field values For custom compass record
    * Return type : Compass__c
    * Paramater   : NA
    */
    Public Static Compass__c getCompassRecord()
    {
        Case cs = New Case();
        cs.Interacting_With_Name__c = 'TEST';
        insert(cs);
        Compass__c cmp = New Compass__c();
        cmp.Case__c = cs.Id;
        Return cmp;
    }
    
    /*
    * Method Name : getHUMConstantsData
    * Description : Insert record For Custom Setting:HUMConstants in test class
    * Return type : NA
    * Paramater   : NA
    */
    Public Static void getHUMConstantsData()
    {
        List<HUMConstants__c> lstConstants = Test.loadData(HUMConstants__c.sObjectType,'HUMConstantsTestData');
    }
    
    /*
    * Method Name : getErrorLookUpRecord
    * Description : Initialize Error_Code_Lookup record
    * Return type : Error_Code_Lookup__c
    * Paramater   : void
    */
    Public Static Error_Code_Lookup__c getErrorLookUpRecord()
    {
        Error_Code_Lookup__c errorLookup = New Error_Code_Lookup__c();
        Return errorLookup;
    }
    
    /*
    * Method Name : getQuestionRecord
    * Description : Initialize Question record For Case Action.
    * Return type : Question__c
    * Paramater   : void
    */
    Public Static Question__c getQuestionRecord()
    {
        Question__c questionRecord = New Question__c();
        Return questionRecord;
    }
    
    /*
    * Method Name : getAttachLog
    * Description : Initialize Attachment Log custom Object record.
    * Return type : Attachment_Log__c
    * Paramater   : NA
    */
    Public Static Attachment_Log__c getAttachLog()
    {
        Attachment_Log__c attLog = New Attachment_Log__c();
        attLog.Attachment_Description__c = 'Test Description';
        Return attLog;
    }
    
    /*
    * Method Name : getAttLogItem
    * Description : Initialize Attachment Log Item custom Object record.
    * Return type : Attachment_Log_Item__c
    * Paramater   : NA
    */
    Public Static Attachment_Log_Item__c getAttLogItem()
    {
        Attachment_Log_Item__c attLogItem = New Attachment_Log_Item__c();
        attLogItem.Value__c='Test Log Item';
        Return attLogItem;
    }
    
    /*
    * Method Name : getCaseComm
    * Description : Initialize Case Comment Record.
    * Return type : CaseComment
    * Paramater   : NA
    */
    Public Static CaseComment getCaseComm()
    {
        CaseComment cCom = New CaseComment();
        cCom.CommentBody='Test Comment';
        Return cCom;
    }
    
    /*
    * Method Name : getQuestion
    * Description : Initialize Question Record.
    * Return type : Question__c
    * Paramater   : NA
    */
    Public Static Question__c getQuestion(String questionName, String questionType, String questionLabel, Integer sequenceNum)
    {
        Question__c ques = New Question__c();
        ques.Name=questionName;
        ques.Question_Label__c = questionLabel;
        ques.Question_Type__c = questionType;
        ques.Sequence_Number__c = sequenceNum;
        ques.Active__c = true;
        Return ques;
    }
    
    /*
    * Method Name : getCOBColumn
    * Description : Initialize HUMTemplateCOBColumn custom setting.
    * Return type : HUMTemplateCOBColumns__c
    * Paramater   : NA
    */
    Public Static HUMTemplateCOBColumns__c getCOBColumn()
    {
        HUMTemplateCOBColumns__c cobColumn = New HUMTemplateCOBColumns__c();
        Return cobColumn;
    }
    
    
    /*
    * Method Name : getTabularQuestion
    * Description : Initialize Tabular Question Record.
    * Return type : Tabular_Question__c
    * Paramater   : NA
    */
    Public Static Tabular_Question__c getTabularQuestion(String questionName, String questionType, String columnTitle, Integer sequenceNum)
    {
        Tabular_Question__c tabQues = New Tabular_Question__c();
        tabQues.Name=questionName;
        tabQues.column_Title__c = columnTitle;
        tabQues.Question_Type__c = questionType;
        tabQues.Sequence_Number__c = sequenceNum;
        Return tabQues;
    }
    
    /*
    * Method Name : getCaseAction
    * Description : Initialize record For Case Action Object
    * Return type : Case_Action__c
    * Paramater   : temaplte Name
    */
    Public Static Case_Action__c getCaseAction(String templateName)
    {
        Case_Action__c oCaseAction = New Case_Action__c();
        oCaseAction.Active__c = true;
        oCaseAction.Name = templateName;
        Return oCaseAction;
    }
    
    
    
    
          
     /*
    * Method Name : getCaseActionAssociation
    * Description : Initialize Case_Action_Association__c.
    * Return type : Case_Action_Association__c
    * Paramater   : NA
    */
    Public Static Case_Action_Association__c getCaseActionAssociation()
    {
        Case_Action_Association__c caseAssociation = New Case_Action_Association__c();
        Return caseAssociation;
    }   
	/*
	* Method Name : getKnowledgeArticle
	* Description : Initialize record For Knowledge Article Knowledge__Kav
	* Return type : Knowledge__Kav
	* Paramater   : NA
	*/
	public Static Knowledge__Kav getKnowledgeArticle()
	{
		Knowledge__Kav kaArticle = New Knowledge__Kav();
		kaArticle.Title = 'test Knowledge';
		kaArticle.UrlName = 'test-feedback-Knowledge-url';
		return kaArticle;
	}   
    /*
     * Method Name : dataForQueuesAndViews
     * Description : This method Is used To create queue And queue Views For Cases view assignment  
     * Return type : void
     * Paramater   : NA
     */           
    Public Static void dataForQueuesAndViews()
     {
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(Name ='Cincinnati CRU');
        insert oQSetup ;
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati'); 
        insert oQv1;
        
        system.assertNotEquals(oQv1, null, 'Value not matched');
        
        Queue_View__c oQv2 = New Queue_View__c(Queue_View_Name__c ='Other111', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati'); 
        insert oQv2;
        
        system.assertNotEquals(oQv2, null, 'Value not matched');
        
        
        Queue_View__c oQv3 = New Queue_View__c(Queue_View_Name__c ='Other123', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati'); 
        insert oQv3;
        
        system.assertNotEquals(oQv3, null, 'Value not matched');
        
        Queue_View__c oQv4 = New Queue_View__c(Queue_View_Name__c ='Other1234', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati'); 
        insert oQv4;
        
        system.assertNotEquals(oQv4, null, 'Value not matched');
        
        Queue_View_Item__c oQueueViewRC1 = New Queue_View_Item__c();
        oQueueViewRC1.API_Name__c = 'PlanIssueState';
        oQueueViewRC1.Field_Operator__c = 'Equals';
        oQueueViewRC1.Field_Value__c = 'NA';
        oQueueViewRC1.Queue_View__c = oQv2.Id;            
        insert oQueueViewRC1;            
        system.assertNotEquals(oQueueViewRC1, null, 'Value not matched');
        
        Queue_View_Item__c oQueueViewRC2 = New Queue_View_Item__c();
        oQueueViewRC2.API_Name__c = 'PlanIssueState';
        oQueueViewRC2.Field_Operator__c = 'Equals';
        oQueueViewRC2.Field_Value__c = 'NA';
        oQueueViewRC2.Queue_View__c = oQv4.Id;            
        insert oQueueViewRC2;            
        system.assertNotEquals(oQueueViewRC2, null, 'Value not matched');
        
        Queue_View_Item__c oQueueViewRC3 = New Queue_View_Item__c();
        oQueueViewRC3.API_Name__c = 'TaskStatus';
        oQueueViewRC3.Field_Operator__c = 'Equals';
        oQueueViewRC3.Field_Value__c = 'In Progress';
        oQueueViewRC3.Queue_View__c = oQv2.Id;            
        insert oQueueViewRC3;            
        system.assertNotEquals(oQueueViewRC3, null, 'Value not matched');           
        
        Queue_View_Item__c oQueueViewRC4 = New Queue_View_Item__c();
        oQueueViewRC4.API_Name__c = 'FundingType';
        oQueueViewRC4.Field_Operator__c = 'Equals';
        oQueueViewRC4.Field_Value__c = 'N';
        oQueueViewRC4.Queue_View__c = oQv3.Id;            
        insert oQueueViewRC4;            
        system.assertNotEquals(oQueueViewRC4, null, 'Value not matched');            
        
        Queue_View_Item__c oQueueView = New Queue_View_Item__c();
        oQueueView.API_Name__c = 'Status';
        oQueueView.Field_Operator__c = 'Equals';
        oQueueView.Field_Value__c = 'In Progress';
        oQueueView.Queue_View__c = oQv1.Id;            
        insert oQueueView;            
        system.assertNotEquals(oQueueView, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView7 = New Queue_View_Item__c();
        oQueueView7.API_Name__c = 'PlanIssueState';
        oQueueView7.Field_Operator__c = 'Equals';
        oQueueView7.Field_Value__c = 'IL, TX';
        oQueueView7.Queue_View__c = oQv1.Id;            
        insert oQueueView7;            
        system.assertNotEquals(oQueueView7, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView2 = New Queue_View_Item__c();
        // modified field from product to product type as part of defect 154260                   
        oQueueView2.API_Name__c = 'ProductType';
        oQueueView2.Field_Operator__c = 'Equals';
        oQueueView2.Field_Value__c = 'Mediclaim';
        oQueueView2.Queue_View__c = oQv1.Id;            
        insert oQueueView2;            
        system.assertNotEquals(oQueueView2, null, 'Value not matched');
        
        dataForQueuesViewsMore(oQv1, oQv3);
        dataForQueuesPriority(oQSetup);
     }
    
    /*
     * Method Name : dataForQueuesViewsMore
     * Description : This method Is used To create Queue Views For Cases view assignment 
     * Return type : void
     * Paramater   : Queue_View__c, Queue_View__c
     */    
     Public Static void dataForQueuesViewsMore(Queue_View__c oQv1, Queue_View__c oQv3)
     {
        Queue_View_Item__c oQueueView3 = New Queue_View_Item__c();
        oQueueView3.API_Name__c = 'FundingType';
        oQueueView3.Field_Operator__c = 'Equals';
        oQueueView3.Field_Value__c = 'Y';
        oQueueView3.Queue_View__c = oQv1.Id;            
        insert oQueueView3;            
        system.assertNotEquals(oQueueView3, null, 'Value not matched');            
        
        Queue_View_Item__c oQueueView4 = New Queue_View_Item__c();
        oQueueView4.API_Name__c = 'Classification';
        oQueueView4.Field_Operator__c = 'Equals';
        oQueueView4.Field_Value__c = 'Claims';
        oQueueView4.Queue_View__c = oQv1.Id;            
        insert oQueueView4;            
        system.assertNotEquals(oQueueView4, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView5 = New Queue_View_Item__c();
        oQueueView5.API_Name__c = 'Intent';
        oQueueView5.Field_Operator__c = 'Equals';
        oQueueView5.Field_Value__c = 'Filling';
        oQueueView5.Queue_View__c = oQv1.Id;            
        insert oQueueView5;            
        system.assertNotEquals(oQueueView5, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView6 = New Queue_View_Item__c();
        oQueueView6.API_Name__c = 'OwnerQueue';
        oQueueView6.Field_Operator__c = 'Equals';
        oQueueView6.Field_Value__c = 'Cincinatti CRU';
        oQueueView6.Queue_View__c = oQv1.Id;            
        insert oQueueView6;            
        system.assertNotEquals(oQueueView6, null, 'Value not matched');
         
        Queue_View_Item__c oQueueView8 = New Queue_View_Item__c();
        oQueueView8.API_Name__c = 'InteractingWithType';
        oQueueView8.Field_Operator__c = 'Equals';
        oQueueView8.Field_Value__c = 'Member';
        oQueueView8.Queue_View__c = oQv1.Id;            
        insert oQueueView8;            
        system.assertNotEquals(oQueueView8, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView9 = New Queue_View_Item__c();
        oQueueView9.API_Name__c = 'PolicyPlatform';
        oQueueView9.Field_Operator__c = 'Equals';
        oQueueView9.Field_Value__c = 'CA';
        oQueueView9.Queue_View__c = oQv1.Id;            
        insert oQueueView9;
        system.assertNotEquals(oQueueView9, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView10 = New Queue_View_Item__c();
        oQueueView10.API_Name__c = 'StateMemResidAdd';
        oQueueView10.Field_Operator__c = 'Equals';
        oQueueView10.Field_Value__c = 'US';
        oQueueView10.Queue_View__c = oQv3.Id;            
        insert oQueueView10;            
        system.assertNotEquals(oQueueView10, null, 'Value not matched');
        
        Queue_View_Item__c oQueueView11 = New Queue_View_Item__c();
        oQueueView11.API_Name__c = 'PlanSegmentSize';
        oQueueView11.Field_Operator__c = 'Equals';
        oQueueView11.Field_Value__c = 'Do';
        oQueueView11.Queue_View__c = oQv3.Id;            
        insert oQueueView11;             
        system.assertNotEquals(oQueueView11, null, 'Value not matched');  
        
        Queue_View_Item__c oQueueView12 = New Queue_View_Item__c();
        oQueueView12.API_Name__c = 'PreviousCaseOwnerRole';
        oQueueView12.Field_Operator__c = 'Equals';
        oQueueView12.Field_Value__c = 'XXX';
        oQueueView12.Queue_View__c = oQv3.Id;            
        insert oQueueView12;            
        system.assertNotEquals(oQueueView12, null, 'Value not matched');     
        
        Queue_View_Item__c oQueueView13 = New Queue_View_Item__c();
        oQueueView13.API_Name__c = 'EmployerGroupName';
        oQueueView13.Field_Operator__c = 'Equals';
        oQueueView13.Field_Value__c = 'ABC';
        oQueueView13.Queue_View__c = oQv3.Id;            
        insert oQueueView13;             
        system.assertNotEquals(oQueueView13, null, 'Value not matched');  
        
        Queue_View_Item__c oQueueView14 = New Queue_View_Item__c();
        oQueueView14.API_Name__c = 'Topic';
        oQueueView14.Field_Operator__c = 'Equals';
        oQueueView14.Field_Value__c = 'Potential Plan Load';
        oQueueView14.Queue_View__c = oQv3.Id;            
        insert oQueueView14;             
        system.assertNotEquals(oQueueView14, null, 'Value not matched'); 
        
        Queue_View_Item__c oQueueView15 = New Queue_View_Item__c();
        oQueueView15.API_Name__c = 'Type';
        oQueueView15.Field_Operator__c = 'Equals';
        oQueueView15.Field_Value__c = 'type';
        oQueueView15.Queue_View__c = oQv3.Id;            
        insert oQueueView15;              
        system.assertNotEquals(oQueueView15, null, 'Value not matched');            
         
        Queue_View_Item__c oQueueView16 = New Queue_View_Item__c();
        oQueueView16.API_Name__c = 'Subtype';
        oQueueView16.Field_Operator__c = 'Equals';
        oQueueView16.Field_Value__c = 'types1';
        oQueueView16.Queue_View__c = oQv3.Id;            
        insert oQueueView16;             
        system.assertNotEquals(oQueueView16, null, 'Value not matched');            
         
        Queue_View_Item__c oQueueView17 = New Queue_View_Item__c();
        oQueueView17.API_Name__c = 'LastModifiedbyQueue';
        oQueueView17.Field_Operator__c = 'Equals';
        oQueueView17.Field_Value__c = 'Cincinatti Calls';
        oQueueView17.Queue_View__c = oQv3.Id;            
        insert oQueueView17;             
        system.assertNotEquals(oQueueView17, null, 'Value not matched');
        
    }
    
    /*
     * Method Name : dataForQueuesPriority
     * Description : This method Is used To Set the Priority For Queue 
     * Return type : void
     * Paramater   : NA
     */     
    Public Static void dataForQueuesPriority(Work_Queue_Setup__c oQSetup)
    {
        Work_Queue_Priority__c oWQP = New Work_Queue_Priority__c(Work_Queue_Name__c = oQSetup.Id);
        system.assertNotEquals(oWQP, NULL, 'Value not matched');
        // modified field from product to product type as part of defect 154260
        oWQP.Criteria_1__c = 'ProductType';
        oWQP.Criteria_2__c = 'FundingType';
        oWQP.Criteria_3__c = 'PlanSegmentSize';
        oWQP.Criteria_4__c = 'PlanIssueState';
        oWQP.Criteria_5__c = 'StateMemResidAdd';
        oWQP.Criteria_6__c = 'EmployerGroupName';
        oWQP.Criteria_7__c = 'PolicyPlatform';
        oWQP.Criteria_8__c = 'Subtype';
        oWQP.Criteria_9__c = 'CaseStatus';
        oWQP.Criteria_10__c = 'InteractingWithType';
        oWQP.Criteria_11__c = 'OwnerQueue';
        oWQP.Criteria_12__c = 'PreviousCaseOwnerRole';
        oWQP.Criteria_13__c = 'Classification';
        oWQP.Criteria_14__c = 'Intent';
        oWQP.Criteria_15__c = 'Type';
        oWQP.Criteria_16__c = 'Topic';
        oWQP.Criteria_17__c = 'LastModifiedbyQueue';
        oWQP.Criteria_18__c = 'TaskStatus'; 
        oWQP.Criteria_19__c = 'Product Code';
        oWQP.Criteria_20__c = 'Behavioral Health';
        oWQP.Criteria_21__c = 'BillingStateCode';
        oWQP.Criteria_22__c = 'ShippingStateCode';
        oWQP.Priority_1__c = '11';
        oWQP.Priority_2__c = '4';
        oWQP.Priority_3__c = '8';
        oWQP.Priority_4__c = '2';
        oWQP.Priority_5__c = '7';
        oWQP.Priority_6__c = '3';
        oWQP.Priority_7__c = '5';
        oWQP.Priority_8__c = '6';
        oWQP.Priority_9__c = '9';
        oWQP.Priority_10__c = '10';
        oWQP.Priority_11__c = '1';
        oWQP.Priority_12__c = '12';
        oWQP.Priority_13__c = '14';
        oWQP.Priority_14__c = '13';
        oWQP.Priority_15__c = '16';
        oWQP.Priority_16__c = '15';
        oWQP.Priority_17__c = '18';
        oWQP.Priority_18__c = '17';  
        oWQP.Priority_19__c = '19';  
        oWQP.Priority_20__c = '20';  
        oWQP.Priority_21__c = '21';  
        oWQP.Priority_22__c = '22';  
                
        insert oWQP;            
        system.assertNotEquals(oWQP, null, 'Value not matched');
    } 
    
    /*
    * Method Name   : getmemberID
    * Description   : create member ID Object record
    * Return Type   : member ID
    * Parameters    : NA
    */
    Public Static Member_ID__c getmemberID()
    {
        Member_ID__c memid = New Member_ID__c();
        Return memid;
    }
    
     /*
     * Method Name : insertTestDataForViews
     * Description : This method Is used To create queue And queue Views For Cases view assignment  
     * Return type : void
     * Paramater   : NA
     */    
    Public Static List<Queue_View__c> insertTestDataForViews(Work_Queue_setup__c oQSetup)
    {   
        List<Queue_View__c> lsNewViews = New List<Queue_View__c>();
        lsNewViews.Add(New Queue_View__c(Name='Other', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true)); 
        lsNewViews.Add(New Queue_View__c(Name='Other111', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true));        
        lsNewViews.Add(New Queue_View__c(Name='Other123', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true)); 
        lsNewViews.Add(New Queue_View__c(Name='Other1234', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true)); 
        lsNewViews.Add(New Queue_View__c(Name='Acess To Care', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true));         
        lsNewViews.Add(New Queue_View__c(Name='Member Alerts', Work_Queue_Name__c=oQSetup.Id,IsActive__c=true));         
        Insert  lsNewViews ;
        system.assertNotEquals(lsNewViews , null, 'Value not matched');
        Return lsNewViews;    
    }
	
	  
    /*
    * Method Name   :    getWorkQueue
    * Description   :    create Work_Queue_Setup__c Object record
    * Return Type   :    Work_Queue_Setup__c
    * Parameters    :    NA
    */
    Public Static Work_Queue_Setup__c getWorkQueue()
    {        
        Work_Queue_Setup__c wqs = New Work_Queue_Setup__c(Name = 'Test');
        return wqs;
    }
	
	
      /*
     * Method Name : insertTestDataForViewsItems
     * Description : This method Is used To create Queue Views For Cases view assignment 
     * Return type : void
     * Paramater   : Queue_View__c, Queue_View__c
     */    
     Public Static List<Queue_View_Item__c> insertTestDataForViewsItems(List<Queue_View__c> lsViews)
    {
        List<Queue_View_Item__c> lstQVI = New List<Queue_View_Item__c>();
        
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Access to Care',
                    Queue_View__c = lsViews[0].Id));            
        
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Member Alert',
                    Queue_View__c = lsViews[1].Id));            

        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Pharmacy Document Request',
                    Queue_View__c = lsViews[2].Id));            
        
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'CAS Document Request',
                    Queue_View__c = lsViews[3].Id));            
       
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Access to Care',
                   Queue_View__c = lsViews[3].Id));   
                   
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Intent',Field_Operator__c = 'Equals',Field_Value__c = 'Info Given',
                           Queue_View__c = lsViews[3].Id));   
                           
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Intent',Field_Operator__c = 'Equals',Field_Value__c = 'Script Error',
                           Queue_View__c = lsViews[3].Id)); 
        
        
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'OwnerQueue',Field_Operator__c = 'Equals',Field_Value__c = 'Cincinnati CRU',
                           Queue_View__c = lsViews[4].Id));
                           
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'LastModifiedbyQueue',Field_Operator__c = 'Equals',Field_Value__c = 'Cincinnati CRU',
                           Queue_View__c = lsViews[4].Id));
                           
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Test CRU',
                           Queue_View__c = lsViews[4].Id));
                                   
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Classification',Field_Operator__c = 'Equals',Field_Value__c = 'COB',
                    Queue_View__c = lsViews[5].Id));            
       
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'Topic',Field_Operator__c = 'Equals',Field_Value__c = 'Access to Care',
                   Queue_View__c = lsViews[5].Id));   
                           
        lstQVI.Add(New Queue_View_Item__c(API_Name__c= 'CaseStatus',Field_Operator__c = 'Equals',Field_Value__c = 'In Progress',
                           Queue_View__c = lsViews[5].Id));       
        insert lstQVI;      
              
        system.assertNotEquals(lstQVI, null, 'Value not matched');
        Return lstQVI;        
    }

    /*
    * Method Name   :    getQueueView
    * Description   :    create Queue_View__c Object record
    * Return Type   :    Queue_View__c
    * Parameters    :    NA
    */
    Public Static Queue_View__c getQueueView()
    {        
        Queue_View__c queueView = New Queue_View__c(Name = 'Other');
        return queueView;
    }

                /*
     * Method Name : insertTestDataForQueuesPriority
     * Description : This method Is used To Set the Priority For Queue 
     * Return type : void
     * Paramater   : NA
     */    
    Public Static Work_Queue_Priority__c insertTestDataForQueuesPriority(Work_Queue_Setup__c oQSetup)
    {
        Work_Queue_Priority__c oWQP = New Work_Queue_Priority__c(Work_Queue_Name__c = oQSetup.Id);
        system.assertNotEquals(oWQP, null, 'Value not matched');
        // modified field from product to product type as part of defect 154260                   
        oWQP.Criteria_1__c = 'ProductType';
        oWQP.Criteria_2__c = 'FundingType';
        oWQP.Criteria_3__c = 'PlanSegmentSize';
        oWQP.Criteria_4__c = 'PlanIssueState';
        oWQP.Criteria_5__c = 'StateMemResidAdd';
        oWQP.Criteria_6__c = 'EmployerGroupName'; 
        oWQP.Criteria_7__c = 'PolicyPlatform';
        oWQP.Criteria_8__c = 'Subtype';
        oWQP.Criteria_9__c = 'CaseStatus';
        oWQP.Criteria_10__c = 'InteractingWithType';
        oWQP.Criteria_11__c = 'OwnerQueue';
        oWQP.Criteria_12__c = 'PreviousCaseOwnerRole';
        oWQP.Criteria_13__c = 'Classification';
        oWQP.Criteria_14__c = 'Intent';
        oWQP.Criteria_15__c = 'Type';
        oWQP.Criteria_16__c = 'Topic';
        oWQP.Criteria_17__c = 'LastModifiedbyQueue';
        oWQP.Criteria_18__c = 'TaskStatus';
        oWQP.Criteria_19__c = 'Product Code';
        oWQP.Criteria_20__c = 'Behavioral Health';
        oWQP.Criteria_21__c = 'BillingStateCode';
        oWQP.Criteria_22__c = 'ShippingStateCode';            
        oWQP.Priority_1__c = '11';
        oWQP.Priority_2__c = '4';
        oWQP.Priority_3__c = '8';
        oWQP.Priority_4__c = '13';
        oWQP.Priority_5__c = '7';
        oWQP.Priority_6__c = '3';
        oWQP.Priority_7__c = '5';
        oWQP.Priority_8__c = '6';
        oWQP.Priority_9__c = '17';
        oWQP.Priority_10__c = '10';
        oWQP.Priority_11__c = '22';
        oWQP.Priority_12__c = '12';
        oWQP.Priority_13__c = '14';
        oWQP.Priority_14__c = '2';
        oWQP.Priority_15__c = '16';
        oWQP.Priority_16__c = '1';
        oWQP.Priority_17__c = '18';
        oWQP.Priority_18__c = '9';
        oWQP.Priority_19__c = '19';  
        oWQP.Priority_20__c = '20';  
        oWQP.Priority_21__c = '21';  
        oWQP.Priority_22__c = '15';    
        insert oWQP;            
        system.assertNotEquals(oWQP, null, 'Value not matched');
        Return oWQP;                 
    }
	
	/*
     * Method Name : initializationOfPreObjects
     * Description : This method is used to create Account, Policy and its memebers for cases. 
     * Return type : void
     * Paramater   : NA
     */    
     public static  void initializationOfPreObjects()
     {
        Account oAcc = HUMTestDataHelperTest.getAccount(); 
        oAcc.Name = 'TestAccount1';
        oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        oAcc.Source_Platform_Code__c = 'CA';
        insert oAcc;            
        system.assertNotEquals(oAcc, null, 'Value not matched');
        
        Product2 oProd = HUMTestDataHelperTest.getProduct();
        oProd.Name = 'Mediclaim';
        oProd.Product_Type__c = 'Mediclaim';            
        insert oProd;            
        system.assertNotEquals(oProd, null, 'Value not matched');
        
        Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
        oPolicy1.Name = 'test123';
        oPolicy1.Group_Name__c = oAcc.Id;
        oPolicy1.Funding_Type_Code__c = 'Y';
        oPolicy1.Product__c = oProd.Id;
        oPolicy1 .Issue_State__c = 'IL';            
        insert oPolicy1;
        system.assertNotEquals(oPolicy1, null, 'Value not matched');            
        
        Policy__c oPolicy2 = HUMTestDataHelperTest.getPolicy();
        oPolicy2.Name = 'test1234';
        oPolicy2.Group_Name__c = oAcc.Id;
        oPolicy2.Funding_Type_Code__c = 'Y';
        oPolicy2.Product__c = oProd.Id;
        oPolicy2.Issue_State__c = 'NA';
        insert oPolicy2;
        system.assertNotEquals(oPolicy2, null, 'Value not matched');
        
        Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
        oPolMem1.Name = '12345';
        oPolMem1.Member__c = oAcc.Id;
        oPolMem1.Policy__c = oPolicy1.Id;            
        oPolMem1.Relationship__c = 'Self';
        insert oPolMem1;            
        system.assertNotEquals(oPolMem1, null, 'Value not matched');
        
        Policy_Member__c oPolMem2 = HUMTestDataHelperTest.getPolicyMember();
        oPolMem2.Name = '123456';
        oPolMem2.Member__c = oAcc.Id;
        oPolMem2.Policy__c = oPolicy2.Id;            
        oPolMem2.Relationship__c = 'Self';
        insert oPolMem2;            
        system.assertNotEquals(oPolMem2, null, 'Value not matched');
    } 
	
    /*
    * Method Name   : getHealthEducator
    * Description   : create One on One Tracking Object record
    * Return Type   : CRMRetail_OOO_Tracking__c
    * Parameters    : NA
    */
    Public Static CRMRetail_OOO_Tracking__c getHealthEducator()
    {
        CRMRetail_OOO_Tracking__c trackingObj = New CRMRetail_OOO_Tracking__c();
        trackingObj.Appointment_Date__c = system.today();
        trackingObj.Appointment_Time__c = Time.newInstance(1, 2, 3, 4);
        trackingObj.Number_of_People__c = 1;
        trackingObj.Length_of_Visit_in_minutes__c = 30;
        trackingObj.Reason_for_Visit__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_REASONTOVISIT;
        return trackingObj;
    }
    
     /*
    * Method Name   :    getInteraction
    * Description   :    create Interaction__c Object record
    * Return Type   :   Interaction__c
    * Parameters    :   NA
    */
    public static Livechattranscript getLiveChatTranscript()
    {        
        LiveChatVisitor olcv = new LivechatVisitor();
        insert olcv;

        Livechattranscript oLCT = new Livechattranscript();
        oLCT.LiveChatVisitorId = olcv.Id;
        oLCT.Secondary_Chat_ID__c = 'test';

        return oLCT ;     
    }
}