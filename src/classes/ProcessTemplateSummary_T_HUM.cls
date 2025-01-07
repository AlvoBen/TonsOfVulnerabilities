/*****************************************************************************************************************************************************************************
    @Apex Class Name  : ProcessTemplateSummary_T_HUM
    @Version          : 1.0
    @Created Date     : July 8th, 2020
    @Function         : Test class for ProcessTemplateSummary_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  07/08/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class ProcessTemplateSummary_T_HUM
{
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs(oCCSUser) 
        {
            id RecordTypeID=Schema.SObjectType.case.getrecordtypeinfosByname().get('Medicare Case').getRecordtypeID();
         
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'Test Account';
            insert oAcc;
        
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
			Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                                Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
								Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
								Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
								Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
								G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
								Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
			insert oCase1;
			            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Address Update - Medicare';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
            templateFieldRec1.Name = 'UserInterfaceData';
            templateFieldRec1.Data_Type__c = 'String';
            templateFieldRec1.Template__c = templateRec1.Id;
            insert templateFieldRec1;
            
            Template_Submission_Owner__c  templatesubOwnerRec1 = New Template_Submission_Owner__c();
            templatesubOwnerRec1.Object_Owner_ID__c = oCase1.id;
            templatesubOwnerRec1.Object_Owner_Type__c = 'Case';
            insert templatesubOwnerRec1;
            
            Template_Submission__c templsubRec1 = new Template_Submission__c();
            templsubRec1.Submission_Owner__c = templatesubOwnerRec1.Id;
            templsubRec1.Template__c = templateRec1.Id;    
            insert templsubRec1;
            
            Template_Submission_Data__c tempSubDataRec1 = new Template_Submission_Data__c ();
            tempSubDataRec1.Value__c = '{"title":"Medicare Address Update Summary","sections":[{"title":"Interaction and Address Update Request","fields":[{"label":"Person Speaking With","value":"shafi"},{"label":"Relationship to Member","value":"self"}]}]}';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            system.assertNotequals(tempSubDataRec1,null);
        }
    }
    
    /*
    * Method to test success for Process Template Summary
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessProcessTemplateSummary()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Case caserec = [select id,Accountid from case where DCN__c='12345678' limit 1];
        Template__c objTemplateRec1 = [Select Id from Template__c where Description__c = 'Address Update - Medicare' Limit 1];
        Template_Field__c objTempFields = [Select Id from Template_Field__c where Template__c=:objTemplateRec1.id AND Name = 'UserInterfaceData' LIMIT 1];
        Template_Submission__C objTempSub = [Select id from Template_Submission__C where Template__c=:objTemplateRec1.Id];
        system.runAs(testUser)
        { 
            Test.StartTest();
            	PageReference pageRef = Page.AddressUpdateMedicare_VF_HUM; 
                pageRef.getParameters().put('vTemplateId', objTempSub.Id);
            	Test.setCurrentPage(pageRef);	
            	ProcessTemplateSummary_C_HUM pts = new ProcessTemplateSummary_C_HUM();
            	pts.fetchUserInterfaceDetails();
            Test.StopTest();
        }
    }
}