/****************************************************************************************
Apex Class Name  : CaseTransferService_T_HUM
Version          : 1.0 
Created Date     : 10-01-2019
Function         : Test class to CaseTransferService_S_HUM  Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 10/01/2019              Original Version
*   Ajay Chakradhar                                 03/12/2019              02/28/2020 Release
*   Santhi Mandava                                  06/15/2020              User Story 1137169: PR00094268 - CaseTransferService_S_HUM performance concerns
*   Sumanth Naredla                                 08/10/2020              08/04 Fix Merge
*   Sunil Kumar Redy                                11/05/2020              US - 1641500 - Coverage for CaseRoutingProcessHelper_HUM, Which calls service class.
*   Atia Uzma                                       01/20/2021              User Story 1186358: PR00091574 - MF 1 - Quality - "Medicare Expedited Appeals Template" Updates (CRM)
*   Jasmeen Shangari                                02/01/2021              Remove switch for REQ-1137169 & Filter CaseComment and Attachment for MHK against REQ - 1414643
 *   Ravikanth Samudrala                                05-18-2023          US-4531288 - T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK 
 * Gowthami Thota                                    07/31/2023              Test Class Fix for UAT  
*   Nirmal Garg                                        02/24/2024          US-5221803 - T1PRJ0865978 - C06 - Lightning - Case Mgt - INC2165142 - Grievance Appeals Provider Disputes Systems - Case transfers from CRM to MHK are creating using the incorrect Eligibility 
***********************************************************************************************************/
@isTest
private class CaseTransferService_T_HUM
{
    /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('CaseServiceUser1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assertEquals(oUser.Current_Queue__c,'Cincinnati CRU');          
        
        system.runAs(oUser)
        {
            HUMTestDataHelperTest.initializationOfPreObjects();
      
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            
           
      HUM_Webservice_Callout__c calloutSettings = new HUM_Webservice_Callout__c();
      calloutSettings.Name = 'CaseTransferService';
      calloutSettings.Active__c = True;
      calloutSettings.Enable_Service_Tester__c = True;
      calloutSettings.Certificate_Name__c = 'Humana_GuidanceCare_Pileus';
      calloutSettings.Content_Type__c = 'application/json';
      calloutSettings.Request_Type__c = 'Post';
      calloutSettings.Timeout__c = '12000';
      calloutSettings.End_Point_URL__c = 'https://dev-crmservicews.humana.com/XXX';
      insert calloutSettings;
                
        }
    }
    
    /*
     * Method Name : testCaseTransferServiceSuccess
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceSuccess()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
           
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1137169';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'Switch Test';
            crm1.Release_Name__c = '08/14/2020';
            insert crm1;
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
      Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='PPI',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
      insert oCase1;
            oCase1.Status = 'Closed';
            update oCase1;
            
            oCase1.Status = 'In Progress';
            oCase1.Re_Open_Case_Date__c = Date.Today();
            update oCase1;
            
             Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'DCN';
            objLI1.Value__c = '123456';
            insert objLI1;
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'URL';
            objLI2.Value__c = 'www.test.com';
            insert objLI2;
            
      Case_Junction__c caseJunctionRec1 = New Case_Junction__c();
            caseJunctionRec1.Case__c =oCase1.id;
            caseJunctionRec1.Case_Master__c=oCase1.id;
            insert caseJunctionRec1;
      
      Attachment__c oAtt1 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt1.Name = 'New Text Document.txt';
            oAtt1.Related_To_Case__c = oCase1.Id; 
            oAtt1.DocId__c = '91030790';
            oAtt1.Version_Id__c ='1';
            insert oAtt1;
            
            Attachment_Log__c oAttLog1 = HUMTestDataHelperTest.getAttachLog();
            oAttLog1.User_Selected_Value__c = 'Claims Summary.Begin DOS_1~02/27/2017^Claims Summary.End DOS_1~02/27/2017^Claims Summary.Claim/Pre-D #_1~185850477^Claims Summary.Provider Name_1~HOBBS, RONALD P MD^Claims Summary.TIN_1~562589722^Claims Summary.Billed Amt_1~265.00^Claims Summary.Mbr Resp_1~30.00^Claims Summary.Paid Amt_1~118.60^Claims Summary.Status_1~Paid^Claims Summary.Last Proc_1~03/04/2017^Claims Summary.Adj_1~N^Claims Summary.Type_1~A^Claims Summary.Receipt_1~03/02/2017';
            oAttLog1.Attachment_Type__c = 'Claims';
            oAttLog1.Case__c = oCase1.Id;
            insert oAttLog1;
            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Medicare/Medicaid Expedited Appeal';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
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
            tempSubDataRec1.Value__c = 'Template Value';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            Case_Action__c objAction = new Case_Action__c(Name='QAA',Active__c=true);
            insert objAction;
            System.assertNotEquals(null, objAction.id);

            Case_Action_Association__c  objAssoc = new Case_Action_Association__c(Case__c=oCase1.id,Case_Action__c=objAction.Id);
            insert objAssoc;
            System.assertNotEquals(null, objAssoc.id);

            Case_Action_Response__c objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Is the Member willing to provide their QAA Complaint details verbally?';
            objResp1.Process_Question_Response__c = 'Yes';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Who is submitting the complaint?';
            objResp1.Process_Question_Response__c = 'CARMEN RODRIGUEZ';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Date of the incident/service';
            objResp1.Process_Question_Response__c = '01/01/2015';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Complaint Details';
            objResp1.Process_Question_Response__c = 'test';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Provider/Facility/Group Tax ID';
            objResp1.Process_Question_Response__c = '123456789';
            insert objResp1;
            

            Test.StartTest();
      
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase1.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
            
            
           
          Test.StopTest();
      }
    }
    
    
     /*
     * Method Name : testCaseTransferServiceSuccess
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceSuccess2()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
           
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =false;
            crm1.Name = '1137169';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'Switch Test';
            crm1.Release_Name__c = '08/14/2020';
            insert crm1;
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
      Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='PPI',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
      insert oCase1;
            oCase1.Status = 'Closed';
            update oCase1;
            
            oCase1.Status = 'In Progress';
            oCase1.Re_Open_Case_Date__c = Date.Today();
            update oCase1;
            
             Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'DCN';
            objLI1.Value__c = '123456';
            insert objLI1;
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'URL';
            objLI2.Value__c = 'www.test.com';
            insert objLI2;
            
      Case_Junction__c caseJunctionRec1 = New Case_Junction__c();
            caseJunctionRec1.Case__c =oCase1.id;
            caseJunctionRec1.Case_Master__c=oCase1.id;
            insert caseJunctionRec1;
      
      Attachment__c oAtt1 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt1.Name = 'New Text Document.txt';
            oAtt1.Related_To_Case__c = oCase1.Id; 
            oAtt1.DocId__c = '91030790';
            oAtt1.Version_Id__c ='1';
            insert oAtt1;
            
            Attachment_Log__c oAttLog1 = HUMTestDataHelperTest.getAttachLog();
            oAttLog1.User_Selected_Value__c = 'Claims Summary.Begin DOS_1~02/27/2017^Claims Summary.End DOS_1~02/27/2017^Claims Summary.Claim/Pre-D #_1~185850477^Claims Summary.Provider Name_1~HOBBS, RONALD P MD^Claims Summary.TIN_1~562589722^Claims Summary.Billed Amt_1~265.00^Claims Summary.Mbr Resp_1~30.00^Claims Summary.Paid Amt_1~118.60^Claims Summary.Status_1~Paid^Claims Summary.Last Proc_1~03/04/2017^Claims Summary.Adj_1~N^Claims Summary.Type_1~A^Claims Summary.Receipt_1~03/02/2017';
            oAttLog1.Attachment_Type__c = 'Claims';
            oAttLog1.Case__c = oCase1.Id;
            insert oAttLog1;
            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Medicare/Medicaid Expedited Appeal';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
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
            tempSubDataRec1.Value__c = 'Template Value';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            Case_Action__c objAction = new Case_Action__c(Name='QAA',Active__c=true);
            insert objAction;
            System.assertNotEquals(null, objAction.id);

            Case_Action_Association__c  objAssoc = new Case_Action_Association__c(Case__c=oCase1.id,Case_Action__c=objAction.Id);
            insert objAssoc;
            System.assertNotEquals(null, objAssoc.id);

            Case_Action_Response__c objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Is the Member willing to provide their QAA Complaint details verbally?';
            objResp1.Process_Question_Response__c = 'Yes';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Who is submitting the complaint?';
            objResp1.Process_Question_Response__c = 'CARMEN RODRIGUEZ';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Date of the incident/service';
            objResp1.Process_Question_Response__c = '01/01/2015';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Complaint Details';
            objResp1.Process_Question_Response__c = 'test';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Provider/Facility/Group Tax ID';
            objResp1.Process_Question_Response__c = '123456789';
            insert objResp1;
            

            Test.StartTest();
      
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase1.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
            
            
           
          Test.StopTest();
      }
    }
    
    /*
     * Method Name : testCaseTransferServiceSuccess3
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceSuccess3()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
            Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
            Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='03/19/2021',Scrum_Team__c='CRM Transformers',Story_Title__c='PR00094923: MF2.6.2: CRM - RETURN CASE - CRM to CRE Returning the task and call back');         

            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =false;
            crm1.Name = '1137169';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'Switch Test';
            crm1.Release_Name__c = '08/14/2020';
            insert crm1;
            
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
      Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='Provider CRU/Corr',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='MHK Dispute Task',Origin='Correspondence',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Provider Correspondence to MHK ',DCN__c='12345678', 
                Work_Queue_View_Name__c='Provider Correspondence Task to MHK',Interacting_About_Type__c='Group',Type='PPI',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Provider', Department__c='Correspondence to MHK', status='External Transfer in Progress');
                insert oCase1;
           
            oCase1.Status = 'Closed';
            update oCase1;
            
            oCase1.Status = 'In Progress';
            oCase1.Re_Open_Case_Date__c = Date.Today();
            update oCase1;
            
            Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'DCN';
            objLI1.Value__c = '123456';
            insert objLI1;
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'URL';
            objLI2.Value__c = 'www.test.com';
            insert objLI2;
            
            Case_Junction__c caseJunctionRec1 = New Case_Junction__c();
            caseJunctionRec1.Case__c =oCase1.id;
            caseJunctionRec1.Case_Master__c=oCase1.id;
            insert caseJunctionRec1;
      
            Attachment__c oAtt1 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt1.Name = 'New Text Document.txt';
            oAtt1.Related_To_Case__c = oCase1.Id; 
            oAtt1.DocId__c = '91030790';
            oAtt1.Version_Id__c ='1';
            insert oAtt1;
            
            Attachment_Log__c oAttLog1 = HUMTestDataHelperTest.getAttachLog();
            oAttLog1.User_Selected_Value__c = 'Claims Summary.Begin DOS_1~02/27/2017^Claims Summary.End DOS_1~02/27/2017^Claims Summary.Claim/Pre-D #_1~185850477^Claims Summary.Provider Name_1~HOBBS, RONALD P MD^Claims Summary.TIN_1~562589722^Claims Summary.Billed Amt_1~265.00^Claims Summary.Mbr Resp_1~30.00^Claims Summary.Paid Amt_1~118.60^Claims Summary.Status_1~Paid^Claims Summary.Last Proc_1~03/04/2017^Claims Summary.Adj_1~N^Claims Summary.Type_1~A^Claims Summary.Receipt_1~03/02/2017';
            oAttLog1.Attachment_Type__c = 'Claims';
            oAttLog1.Case__c = oCase1.Id;
            insert oAttLog1;
            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Medicare Expedited Appeal Request';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
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
            tempSubDataRec1.Value__c = 'Template Value';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            Case_Action__c objAction = new Case_Action__c(Name='QAA',Active__c=true);
            insert objAction;
            System.assertNotEquals(null, objAction.id);

            Case_Action_Association__c  objAssoc = new Case_Action_Association__c(Case__c=oCase1.id,Case_Action__c=objAction.Id);
            insert objAssoc;
            System.assertNotEquals(null, objAssoc.id);

            Case_Action_Response__c objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Is the Member willing to provide their QAA Complaint details verbally?';
            objResp1.Process_Question_Response__c = 'Yes';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Who is submitting the complaint?';
            objResp1.Process_Question_Response__c = 'CARMEN RODRIGUEZ';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Date of the incident/service';
            objResp1.Process_Question_Response__c = '01/01/2015';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Complaint Details';
            objResp1.Process_Question_Response__c = 'test';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Provider/Facility/Group Tax ID';
            objResp1.Process_Question_Response__c = '123456789';
            insert objResp1;
            
            Test.StartTest();      
            List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase1.id);             
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);                                  
            Test.StopTest();
      }
    }
                    
    /*
     * Method Name : testCaseTransferServiceFailure
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceFailure()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferFailureMockResponse_T_HUM());
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            
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
            
      Case oCase2 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='',Topic__c='Good Cause Reinstatement', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='PPI',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
      insert oCase2;
             
          Test.StartTest();
      
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase2.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
            
            
          Test.StopTest();
      }
    }
    static testMethod void testCallService()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
            Test.setMock(HttpCalloutMock.class, new CaseTransferFailureMockResponse_T_HUM());
            Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            
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
            
             Case oCase2 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='',Topic__c='Good Cause Reinstatement', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='PPI',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='External Transfer in Progress');
            insert oCase2;
             
            Test.StartTest();
            List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase2.id); 
            CaseRoutingProcessHelper_HUM.callService(listOfNewCases); 
            Test.StopTest();
      }
    }       
    /*
     * Method Name : testCaseTransferServiceInvalidData
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceInvalidData()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferFailureMockResponse_T_HUM());
      
            
            Account oAcc1 = HUMTestDataHelperTest.getAccount(); 
            oAcc1.Name ='Test Account1';
            oAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            oAcc1.Source_Platform_Code__c = 'CA';
            insert oAcc1;
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name = 'Mediclaim';
            oProd.Product_Type__c = 'Mediclaim';            
            insert oProd;            
            system.assertEquals(oProd.Name , 'Mediclaim');
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name = 'test123';
            oPolicy1.Group_Name__c = oAcc1.Id;
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Product__c = oProd.Id;
            oPolicy1 .Issue_State__c = 'IL';    
            oPolicy1.ORIGINAL_MAJOR_LOB__C = 'MED';     
            oPolicy1.Platform__c ='LV';
            insert oPolicy1;
            system.assertEquals(oPolicy1.Name , 'test123');
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Relationship__c = 'Self';
          oPolMem1.Effective_Date__c = '';
          oPolMem1.End_Date__c = '';
            oPolMem1.Name = 'test 1234';
            oPolMem1.Relationship_Code__c = '';
            oPolMem1.Policy__c=oPolicy1.Id;
            insert oPolMem1;
            
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
            
      Case oCase3 = New Case(AccountId = oAcc1.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='',Interacting_With_Type__c = '',
                Interacting_With__c=oAcc1.Id,Due_Date__c=Date.Today(),Subtype__c='',Origin='',Topic__c='',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='', Priority='',
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='',
                G_A_Rights_Given__c='',G_A_Reason__c='',Complaint__c='',Complaint_Reason__c='',
                Complaint_Type__c='',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',
                                Oral_Grievance_Category__c='Medicare Part C',Oral_Grievance_Sub_Category__c='Marketing');
      insert oCase3;
      
            Attachment__c oAtt3 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt3.Name = 'New Text Document.txt';
            oAtt3.Related_To_Case__c = oCase3.Id; 
            oAtt3.DocId__c = '';
            oAtt3.Version_Id__c ='';
            insert oAtt3;
            
            Attachment_Log__c oAttLog3 = HUMTestDataHelperTest.getAttachLog();
            oAttLog3.User_Selected_Value__c = '';
            oAttLog3.Attachment_Type__c = 'Claims';
            oAttLog3.Case__c = oCase3.Id;
            insert oAttLog3;
            
            Attachment_Log__c oAttLog4 = HUMTestDataHelperTest.getAttachLog();
            oAttLog4.User_Selected_Value__c = 'test';
            oAttLog4.Attachment_Type__c = 'Claims';
            oAttLog4.Case__c = oCase3.Id;
            insert oAttLog4;
            
            Attachment_Log__c oAttLog5 = HUMTestDataHelperTest.getAttachLog();
            oAttLog5.User_Selected_Value__c = 'Claim/Pre-Dtest';
            oAttLog5.Attachment_Type__c = 'Claims';
            oAttLog5.Case__c = oCase3.Id;
            insert oAttLog5;
            
          Test.StartTest();
            
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase3.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
           
            
          Test.StopTest();
      }
    }
    
    static testMethod void testCaseTransferServiceSuccessCC()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
           
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1137169';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'Switch Test';
            crm1.Release_Name__c = '08/14/2020';
            insert crm1;
            
            CRMFunctionality_ONOFF_Switch__c crm12 = new CRMFunctionality_ONOFF_Switch__c();
            crm12.IsON__c =true;
            crm12.Name = '1348280';
            crm12.Scrum_Team__c = 'CRM Transformers';
            crm12.Story_Title__c = 'Switch Test';
            crm12.Release_Name__c = '09/10/2021';
            insert crm12;

            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='09/10/2021',Scrum_Team__c='CRM Transformers',Story_Title__c='Test Switch');         

            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
      Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='MHK Dispute Task',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
      insert oCase1;
            oCase1.Status = 'Closed';
            update oCase1;
            
            oCase1.Status = 'In Progress';
            oCase1.Re_Open_Case_Date__c = Date.Today();
            update oCase1;
            
             Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'DCN URL';
            objLI1.Value__c = 'www.test.com';
            insert objLI1;
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'URL';
            objLI2.Value__c = 'www.test.com';
            insert objLI2;

            Attachment_Log_Item__c objLI11 = new Attachment_Log_Item__c();
            objLI11.Attachment_Log__c = objLog.Id;
            objLI11.Name = 'DCN';
            objLI11.Value__c = '123456';
            insert objLI11;
            
      Case_Junction__c caseJunctionRec1 = New Case_Junction__c();
            caseJunctionRec1.Case__c =oCase1.id;
            caseJunctionRec1.Case_Master__c=oCase1.id;
            insert caseJunctionRec1;
      
      Attachment__c oAtt1 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt1.Name = 'New Text Document.txt';
            oAtt1.Related_To_Case__c = oCase1.Id; 
            oAtt1.DocId__c = '91030790';
            oAtt1.Version_Id__c ='1';
            insert oAtt1;
            
            Attachment_Log__c oAttLog1 = HUMTestDataHelperTest.getAttachLog();
            oAttLog1.User_Selected_Value__c = 'Claims Summary.Begin DOS_1~02/27/2017^Claims Summary.End DOS_1~02/27/2017^Claims Summary.Claim/Pre-D #_1~185850477^Claims Summary.Provider Name_1~HOBBS, RONALD P MD^Claims Summary.TIN_1~562589722^Claims Summary.Billed Amt_1~265.00^Claims Summary.Mbr Resp_1~30.00^Claims Summary.Paid Amt_1~118.60^Claims Summary.Status_1~Paid^Claims Summary.Last Proc_1~03/04/2017^Claims Summary.Adj_1~N^Claims Summary.Type_1~A^Claims Summary.Receipt_1~03/02/2017';
            oAttLog1.Attachment_Type__c = 'Claims';
            oAttLog1.Case__c = oCase1.Id;
            insert oAttLog1;
            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Medicare/Medicaid Expedited Appeal';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
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
            tempSubDataRec1.Value__c = 'Template Value';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            Case_Action__c objAction = new Case_Action__c(Name='QAA',Active__c=true);
            insert objAction;
            System.assertNotEquals(null, objAction.id);

            Case_Action_Association__c  objAssoc = new Case_Action_Association__c(Case__c=oCase1.id,Case_Action__c=objAction.Id);
            insert objAssoc;
            System.assertNotEquals(null, objAssoc.id);

            Case_Action_Response__c objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Is the Member willing to provide their QAA Complaint details verbally?';
            objResp1.Process_Question_Response__c = 'Yes';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Who is submitting the complaint?';
            objResp1.Process_Question_Response__c = 'CARMEN RODRIGUEZ';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Date of the incident/service';
            objResp1.Process_Question_Response__c = '01/01/2015';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Complaint Details';
            objResp1.Process_Question_Response__c = 'test';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Provider/Facility/Group Tax ID';
            objResp1.Process_Question_Response__c = '123456789';
            insert objResp1;
            

            Test.StartTest();
      
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase1.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
            CaseTransferService_S_HUM.CaseTransferProcess(null);
           
          Test.StopTest();
      }
    }

    static testMethod void prepareCaseDCNandURLDataException()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];                
        system.runAs(oUser)
        { 
      Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
      Account oAcc = [select  id, name,Lastname,Firstname from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
           
            
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claims');
            
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.IsON__c =true;
            crm1.Name = '1137169';
            crm1.Scrum_Team__c = 'CRM Incredibles';
            crm1.Story_Title__c = 'Switch Test';
            crm1.Release_Name__c = '08/14/2020';
            insert crm1;
            
            CRMFunctionality_ONOFF_Switch__c crm12 = new CRMFunctionality_ONOFF_Switch__c();
            crm12.IsON__c =true;
            crm12.Name = '1348280';
            crm12.Scrum_Team__c = 'CRM Transformers';
            crm12.Story_Title__c = 'Switch Test';
            crm12.Release_Name__c = '09/10/2021';
            insert crm12;

            CRMFunctionality_ONOFF_Switch__c crm11 = new CRMFunctionality_ONOFF_Switch__c();
            crm11.IsON__c =true;
            crm11.Name = '1348280_1';
            crm11.Scrum_Team__c = 'CRM Transformers';
            crm11.Story_Title__c = 'Switch Test';
            crm11.Release_Name__c = '09/10/2021';
            insert crm11;

            insert new CRMFunctionality_ONOFF_Switch__c(Name = '1414643',IsON__c=true,Release_Name__c='09/10/2021',Scrum_Team__c='CRM Transformers',Story_Title__c='Test Switch');         

            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Filling');
            
      Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                Policy_Member__c = oPolMem1.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                Interacting_With__c=oAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Group',Type='MHK Dispute Task',
                G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Complaint__c='Yes',Complaint_Reason__c='Other',
                Complaint_Type__c='Medical',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
      insert oCase1;
            oCase1.Status = 'Closed';
            update oCase1;
            
            /*oCase1.Status = 'In Progress';
            oCase1.Re_Open_Case_Date__c = Date.Today();
            update oCase1;*/
            
             Attachment_Log__c objLog = new Attachment_Log__c();
            objLog.case__c = oCase1.Id;
            insert objLog;
            /*Attachment_Log_Item__c objLI1 = new Attachment_Log_Item__c();
            objLI1.Attachment_Log__c = objLog.Id;
            objLI1.Name = 'DCN URL';
            objLI1.Value__c = '123456';
            insert objLI1;*/
            
            Attachment_Log_Item__c objLI2 = new Attachment_Log_Item__c();
            objLI2.Attachment_Log__c = objLog.Id;
            objLI2.Name = 'URL';
            objLI2.Value__c = 'www.test.com';
            insert objLI2;
            
      Case_Junction__c caseJunctionRec1 = New Case_Junction__c();
            caseJunctionRec1.Case__c =oCase1.id;
            caseJunctionRec1.Case_Master__c=oCase1.id;
            insert caseJunctionRec1;
      
      Attachment__c oAtt1 = HUMTestDataHelperTest.getCustomAttachment();
            oAtt1.Name = 'New Text Document.txt';
            oAtt1.Related_To_Case__c = oCase1.Id; 
            oAtt1.DocId__c = '91030790';
            oAtt1.Version_Id__c ='1';
            insert oAtt1;
            
            Attachment_Log__c oAttLog1 = HUMTestDataHelperTest.getAttachLog();
            oAttLog1.User_Selected_Value__c = 'Claims Summary.Begin DOS_1~02/27/2017^Claims Summary.End DOS_1~02/27/2017^Claims Summary.Claim/Pre-D #_1~185850477^Claims Summary.Provider Name_1~HOBBS, RONALD P MD^Claims Summary.TIN_1~562589722^Claims Summary.Billed Amt_1~265.00^Claims Summary.Mbr Resp_1~30.00^Claims Summary.Paid Amt_1~118.60^Claims Summary.Status_1~Paid^Claims Summary.Last Proc_1~03/04/2017^Claims Summary.Adj_1~N^Claims Summary.Type_1~A^Claims Summary.Receipt_1~03/02/2017';
            oAttLog1.Attachment_Type__c = 'Claims';
            oAttLog1.Case__c = oCase1.Id;
            insert oAttLog1;
            
            CaseComment oCaseCmmt1 = HUMTestDataHelperTest.getCaseComm();
            oCaseCmmt1.ParentId = oCase1.Id;
            oCaseCmmt1.CommentBody = 'Test comments';
            insert oCaseCmmt1;
            
            Template__c templateRec1 = New Template__c();
            templateRec1.Description__c = 'Medicare/Medicaid Expedited Appeal';
            insert templateRec1;
            
            Template_Field__c templateFieldRec1 = new Template_Field__c();
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
            tempSubDataRec1.Value__c = 'Template Value';
            tempSubDataRec1.Template_Field__c = templateFieldRec1.Id;
            tempSubDataRec1.Template_Submission__c = templsubRec1.Id;
            insert tempSubDataRec1;
            
            Case_Action__c objAction = new Case_Action__c(Name='QAA',Active__c=true);
            insert objAction;
            System.assertNotEquals(null, objAction.id);

            Case_Action_Association__c  objAssoc = new Case_Action_Association__c(Case__c=oCase1.id,Case_Action__c=objAction.Id);
            insert objAssoc;
            System.assertNotEquals(null, objAssoc.id);

            Case_Action_Response__c objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Is the Member willing to provide their QAA Complaint details verbally?';
            objResp1.Process_Question_Response__c = 'Yes';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Who is submitting the complaint?';
            objResp1.Process_Question_Response__c = 'CARMEN RODRIGUEZ';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Date of the incident/service';
            objResp1.Process_Question_Response__c = '01/01/2015';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Complaint Details';
            objResp1.Process_Question_Response__c = 'test';
            insert objResp1;

            objResp1 = new Case_Action_Response__c();
            objResp1.Action__c = objAssoc.id;
            objResp1.Process_Question__c = 'Provider/Facility/Group Tax ID';
            objResp1.Process_Question_Response__c = '123456789';
            insert objResp1;
            

            Test.StartTest();
      
      List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(oCase1.id); 
            
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);
            CaseTransferService_S_HUM.CaseTransferProcess(null);
           
          Test.StopTest();
      }
    }
    
    
     /*
     * Method Name : testCaseTransferServiceSuccess3
     * Description : Test method for CaseTransferRequest_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testCaseTransferServiceCaseAdditionaltoMHK()
    {            
        User oUser = [select Id,Name from User where name = 'test CaseServiceUser1' limit 1];   
        User testAgent = HUMTestDataHelperTest.createTestUser('Agent1','Customer Care Specialist');
        insert testAgent;
        system.runAs(oUser)
        { 
            Test.setMock(HttpCalloutMock.class, new CaseTransferSuccessMockResponse_T_HUM());
            CRMFunctionality_ONOFF_Switch__c crm1 = new CRMFunctionality_ONOFF_Switch__c();
            crm1.Name = '4531288';
            crm1.IsON__c = true;
            crm1.Release_Name__c = '06/23/2023';
            crm1.Scrum_Team__c = 'MasterMinds';
            crm1.Story_Title__c = 'User Story 4531288: T1PRJ0901110 MF1.11 - Additional Fields (blue marked) to the CRM PDF - CRM and MHK';
            insert crm1;
            
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            
            Case objCase = HUMTestDataHelperTest.getCase();
            objCase.Call_Benefit_Category__c = 'Medicare Part D';
            insert objCase;
            system.assertNotEquals(objCase,null,'Value not matched');
            
            Task objTask = HUMTestDataHelperTest.getTaskRecord();
            objTask.WhatId = objCase.Id;
            objTask.Type='Work Task';
            insert objTask;
            system.assertNotEquals(objTask,null,'Value not matched');
            
            Interaction__c interaction = new Interaction__c ();
            interaction.Caller_Name__c ='TEST';
            interaction.Interacting_With_type__c='NEW';
            interaction.Interaction_Origin__c='Inbound Call';
            insert interaction;
            system.assertNotEquals(interaction,null,'Value not matched');
            
            Case_Interaction__c cc1= new Case_Interaction__c(Case__c = objCase.Id, Interaction__c=interaction.Id);
            insert cc1;
            system.assertNotEquals(cc1,null,'Value not matched');
            
            LiveChatVisitor visit = new LiveChatVisitor();
            insert visit ;
            
            LiveChatTranscript livechat = new LiveChatTranscript();
            livechat.AccountId__c = objMemAccount.Id;
            
            livechat.CaseId = objCase.Id;
            livechat.StartTime =DateTime.now();
            livechat.EndTIme =DateTime.now();
            livechat.Status = 'Completed';
            livechat.LiveChatVisitorID=visit.Id;
            insert livechat ;
            system.assertNotEquals(livechat,null,'Value not matched');
            
            LiveChatTranscriptEvent livechatEvent = new LiveChatTranscriptEvent();            
            livechatEvent.Time =DateTime.now();
            livechatEvent.AgentId = testAgent.Id;
            livechatEvent.Type  = 'ChatRequest';
            livechatEvent.Detail = 'Completed';
            livechatEvent.LiveChatTranscriptId = livechat.Id;
            insert livechatEvent ;
              
            Test.StartTest();  
            
            LoggingTestDataSetup_T_HUM.createTestData();
            List<Case> lCase = [Select id from case limit 5];
            LoggingTestDataSetup_T_HUM.createAttachmentLog(lCase[0].id);
            List<Case> lstCases = [Select Id from Case limit 5];
            System.assertEquals(4,lstCases.size());
            Attachment_log__c atch = [Select id from Attachment_log__c where Case__c=:lstCases[0].id limit 1];
          
            List<id> listOfNewCases = New List<id>();
            listOfNewCases.add(objCase.id); 
            listOfNewCases.add(lstCases[0].Id);
            CaseTransferService_S_HUM.CaseTransferInvoke(listOfNewCases);                                  
            Test.StopTest();
      }
    }
}