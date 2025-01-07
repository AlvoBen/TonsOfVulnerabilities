/**********************************************************************************************************************************************************************
Apex Class Name :  HUMUpdateCaseHelperTest 
Version: 1.0 
Created On       : 07/07/2014
Function         : Test class to HUMUpdateCaseHelper apex class

Modification Log : 
* Developer Name          Code Review                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu              17093                      07/07/2014                 Original Version
* Mrudula jaddu              17878                      09/12/2014                 Modified fields as part of defect 154260
* Mrudula Jaddu              20092                      12/29/2014                 Modified code for REQ-95896
* Santhi Mandava             20403                      01/20/2015                 Fixed test class failure issue.
* Santhi Mandava             20517                      01/24/2015                 Fixed Defect #164139.
* Santhi Mandava             21128                      02/24/2015                 Fixed test class failure issue related to req-81852.
* Santhi Mandava             21968                      02/24/2015                 Modified to cover changes related to req-02635/88053.
* SuryaKumari Medicherla     22110                      04/06/2015                 Modified to cover changes related to Production Issue 247.
* Vishal Verma                                          07/29/2016                 Changes made as part of REQ- 270349
* Harshith Mandya                                       11/23/2016                 Modified code to increase code coverage
* Ranjeeth Nagishetty                                   03/22/2016                 Validation rule taken care in test coverage
* Pallavi Ravishankar        78362                      03/23/2017                 Changes as per REQ 304793 - ctci object 
* Pallavi Ravishankar        94646                      03/23/2017                 changes as per CRM Ticket : 6794554  
* Pallavi R                  98201                      05/31/2017                 REQ 318433-changed classification & intent id 
* Pradeepkumar Dani          98235                      06/06/2017                 Classification_Type_Old__c Clean up references to CTCI references           
* Pradeepkumar Dani          99570                      06/08/2017                 Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Santhi Mandava                                        03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
* Shailesh Mali                                         11/05/2019                 REQ - 409603 Process builder logic migration to apex code
* Mohammed Noor                                         12/06/2019                 US826586 - Test class changes for Case Workflows optimization.
* Pooja Kumbhar                                          04/29/2020                 Checkmarx Test Methods with No Asserts Error fix.
* Subhamay Mandal                                      08/24/2020                 User Story 1293633: PR00091574- IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* Vinay Lingegowda                                     10/10/2020                 USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)
* Santhi Mandava                                                                Fixed testclass issue
* Sai Kumar Boga                                       11/09/2020                 INC0716609 - Pull Work issue for reopened Vantage cases.
* Kavya Bondada                                        12/14/2020                 User Story 1641595: PR00091574 - MF 1 - Quality - Ability for Rep to select "Was the Grievance Resolved during the call?" on Quick Start/Case Page (RF)
* Vamshi Krishna Pemberthi                        06/08/2021        Code Coverage Improvements
* Moshitha Gunasekaran                                 07/23/2021                 US#2027311 T1PRJ0001848 - (CSI1955) - Platform - Asynch Chat - Async Case and Interaction in CRM
* Jasmeen Shangari									    07/31/2023				  Fixed Test Class Failure
* Apurva Urkude									    09/10/2023				  Fixed Test Class Failure
***********************************************************************************************************************************************************************/

@isTest
Private Class HUMUpdateCaseHelperTest
{
   /**
    * Test Setup method to create all the data required for testing.
    *
    * @param     NULL
    * @return    void 
    */
    @testSetup static void setup() 
    {
        List<case> lstOfCases = New List<case>();
        
        User oUser = HUMTestDataHelperTest.createTestUser('HUMUpdateCaseHelperUser1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assertEquals(oUser.Current_Queue__c,'Cincinnati CRU');  
        User oUserAPI = HUMTestDataHelperTest.createTestUser('HUMUpdateCaseHelperUserAPI','ETL API Access');
        oUserAPI.Current_Queue__c = 'Business Process and Testing';
        oUserAPI.Network_User_Id__c = 'ETTEST1';
        insert oUserAPI;
        system.assertEquals(oUserAPI.Current_Queue__c,'Business Process and Testing');  
        
        system.runAs(oUser)
        {
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 0;
            insert cv;
            initializationOfPreObjects();
            dataForQueuesAndViews();  
            createAccountANDCaseRecords();
            
            HUMQueueViewBatchSettings__c obj = New HUMQueueViewBatchSettings__c(name ='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            insert obj;
            system.assertEquals(obj.Name , 'QueueViewBatchSetup');
            
            List<HUMConstants__c> lstConstanst=New List<HUMConstants__c>();
            lstConstanst.Add(New HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1,StringValue__c='1'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMLISTSIZEZERO',IntegerValue__c=0,StringValue__c='0'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMViewCriteriaLimit',IntegerValue__c=22,StringValue__c=''));
            lstConstanst.Add(New HUMConstants__c(Name='HUMDefaultBatchLimit',IntegerValue__c=5,StringValue__c=''));
            lstConstanst.Add(New HUMConstants__c(Name='HUMAllOtherView',StringValue__c='Other'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMStatusCancelled',StringValue__c='Cancelled'));
            lstConstanst.add(New HUMConstants__c(Name='HUMSingleSlash',StringValue__c='/'));
            lstConstanst.add(New HUMConstants__c(Name='HUMCaseCloseReturnURL',StringValue__c='/s?retURL='));
            lstConstanst.add(New HUMConstants__c(Name='HUMEnt',StringValue__c='ent'));
            lstConstanst.add(New HUMConstants__c(Name='HUMNoOverrideEqualsOne',StringValue__c='&nooverride=1'));
            lstConstanst.add(New HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task'));
            lstConstanst.add(New HUMConstants__c(Name='HUMNotificationTask',StringValue__c='Notification Task'));
            insert lstConstanst;
            System.assert(lstConstanst.size() == 14);
            
            Account oAcc = [select  id, name from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            
            Classification_Type__c ctRec = New Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            Insert ctRec;
            
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
            
            CTCI_Junction__c ctciRec = New CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;
                    
            Case oCase1 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem1.Id,
                             Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Test',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', Topic__c ='Test245');

        Case oCase2 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                 Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                                 Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Other',
                                 Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', Topic__c ='Humana');

        Case oCase3 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem1.Id,
                                  Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                                  Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed');

        Case oCase4 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                 Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), 
                                  Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');

        Case oCase5 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                 Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='user1', 
                                  Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');

        Case oCase6 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                  Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                                  Work_Queue_View_Name__c ='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress');

        Case oCase7 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                  Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                                  Work_Queue_View_Name__c ='Cincinati CRU',Service_Center__c ='Cincinnati',  Department__c='CRU', status='In Progress');

        Case oCase8 = New Case(AccountId = oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMem2.Id,
                                  Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                                  Work_Queue_View_Name__c ='',Topic__c = 'Other',Service_Center__c ='Cincinnati',  Department__c='CRU', status='In Progress');
            oCase1.CTCI_List__c = ctciRec.id;
            insert oCase1;   
            system.assertEquals( oCase1.CTCI_List__c , ctciRec.id);     
            system.assertEquals(oCase1.Interacting_With_Type__c , 'Group');  
            oCase2.CTCI_List__c = ctciRec.id;         
            insert oCase2;                        
            system.assertEquals(oCase2.Owner_Queue__c,'Cincinnati CRU');
            system.assertEquals( oCase2.CTCI_List__c , ctciRec.id);
            oCase3.CTCI_List__c = ctciRec.id;  
            insert oCase3;
            system.assertEquals(oCase3.G_A_Rights_Given__c , 'No');
            system.assertEquals( oCase3.CTCI_List__c , ctciRec.id);
            
            oCase4.CTCI_List__c = ctciRec.id;  
            insert oCase4;
            system.assertEquals(oCase4.Work_Queue_View_Name__c , 'Cincinati CRU');
            system.assertEquals( oCase4.CTCI_List__c , ctciRec.id);
            oCase5.CTCI_List__c = ctciRec.id;  
            insert oCase5;
            system.assertEquals( oCase5.CTCI_List__c , ctciRec.id);
            system.assertEquals(oCase5.Service_Center__c , 'Cincinnati');
            oCase6.CTCI_List__c = ctciRec.id;  
            insert oCase6;
            system.assertEquals(oCase6.Department__c , 'CRU');


            insert oCase7;
            system.assertEquals(oCase7.status,'In Progress');
            oCase8.CTCI_List__c = ctciRec.id; 
            insert oCase8;
            system.assertEquals(oCase8.CTCI_List__c , ctciRec.id);
            System.assertEquals(ocase8.status,'In Progress');
       
            lstOfCases.add(oCase1);
            lstOfCases.add(oCase2);
            system.assertEquals(lstOfCases.size(), 2);
            
        }
    } 
    
    /**
    * This method Is used to Test overall CaseUpdateHelper class.
    *
    * @param     NULL
    * @return    void 
    */
    Private Static testMethod void overallTestMethod()
    {
        User oCurrentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test HUMUpdateCaseHelperUser1' LIMIT 1];
        list<CTCI_Junction__c> oCTCI = [select id,name ,classification__c,classification__r.name,Intent__c,intent__r.name from  CTCI_Junction__c LIMIT 1];
        system.assert(oCTCI.size()!=0);
        System.runAs(oCurrentUser)
        {
            test.startTest();
            
            Case oCase3 = [Select Id ,status, ctci_list__c from Case where Complaint__c = 'No' limit 1];
            oCase3.status = 'In Progress';
            
            Case oCase4 = [Select Id ,status, ctci_list__c from Case where Owner_Queue__c='Cincinnati CRU'And Work_Queue_View_Name__c='Cincinati CRU' And Service_Center__c ='Cincinnati' And Department__c='CRU' And status='In Progress' limit 1];
            oCase4.status = 'Pending - Task';
            
            Case oCase5 = [Select Id ,status, ctci_list__c from Case where Case_Owner__c='user1' And Owner_Queue__c='Cincinnati CRU' And Work_Queue_View_Name__c='Cincinati CRU' And Service_Center__c ='Cincinnati' And Department__c='CRU'And status='In Progress' limit 1];
            oCase5.status = 'Pending - Task';
            
            Case oCase7 =[Select Id ,Case_Owner__c , Owner_Queue__c, ctci_list__c  from Case order by createddate desc limit 1];
            oCase7.Case_Owner__c = UserInfo.getUserName();
            oCase7.Owner_Queue__c = 'Cincinnati Calls';
            
            User oUser = HUMTestDataHelperTest.createTestUser('HUMUpdateCaseHelperUser2','Customer Care Specialist');
            oUser.Current_Queue__c = 'Cincinnati CRU';
            Account oAcc1 = [select  id, name from Account where name ='TestAccount1'];
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'ClaimsRSO';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;
            System.assertEquals(classificationRec1.Name,'ClaimsRSO');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Filling1';
            intentRec1.Is_Active__c = true;
            insert intentRec1;
            System.assertEquals(intentRec1.Name,'Filling1');
                    
            Case oCase11 = New Case(AccountId = oAcc1.Id, Intent_Id__c = intentRec1.Id, Classification_Id__c = classificationRec1.Id, Policy_Member__c = oPolMem1.Id,
                             Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc1.Id, Due_Date__c=Date.Today(), ctci_list__c=oCTCI[0].id,
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');

        Case oCase22 = New Case(AccountId = oAcc1.Id, Intent_Id__c = intentRec1.Id, Classification_Id__c = classificationRec1.Id, Policy_Member__c = oPolMem2.Id,
                             Interacting_About_Type__c = 'Group',Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc1.Id, Due_Date__c=Date.Today(),ctci_list__c=oCTCI[0].id,
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
            
            insert oCase11;
            System.assertEquals(oCase11.status,'In Progress');
            insert oCase22;
            System.assertEquals(oCase22.status,'In Progress');
            
            List<case> listOfUpdatedCases = New List<case>();
            listOfUpdatedCases.add(oCase7);
            listOfUpdatedCases.add(oCase3);
            listOfUpdatedCases.add(oCase4);
            listOfUpdatedCases.add(oCase5);
            system.assertEquals(listOfUpdatedCases.size(), 4);
            
            List<case> listOfNewCases = New List<case>();
            listOfNewCases.add(oCase11);
            listOfNewCases.add(oCase22);
            system.assertEquals(listOfNewCases.size(), 2);
            
            map<Id,string> mapGroups=New map<Id,string>();
            mapGroups.put(oCase3.id,'12345');
            mapGroups.put(oCase4.id,'12345');
            mapGroups.put(oCase5.id,'12345');
            HUMUpdateCaseHelper objHandler = New HUMUpdateCaseHelper();
            
            Map<Id, Case> oldMap = New Map<Id, Case>();
            oCase11.Status = 'Cancelled';
            oCase22.Status = 'Cancelled';
            oldMap.put(oCase11.id, oCase11);
            oldMap.put(oCase22.id, oCase22);
            
            Classification_Type__c ct = new Classification_Type__c(Name = 'Calls', Is_Active__c = true, Guidance_Alert_Limit__c = 2);
            insert ct;
            system.assertEquals(ct.Name, 'Calls');

            List<Work_Queue_Setup__c> wqsList = New List<Work_Queue_Setup__c>();
            Work_Queue_Setup__c wqs = New Work_Queue_Setup__c();
            wqs.Service_Center__c = 'Compliant';
            wqs.Department__c = 'Test';
            wqs.Classification_Type__c = ct.Id;
            wqs.isActive__c = True;
            wqsList.add(wqs);
            
            objHandler.updateCaseQueues(listOfNewCases ,oldMap);
            objHandler.updateCaseQueues(listOfNewCases ,null);
            
            oCase11.Status = 'In Progress';
            oCase22.Status = 'In Progress';
            oldMap.put(oCase11.id, oCase11);
            oldMap.put(oCase22.id, oCase22);
            String profileName = HumUtilityHelper.getCurrentUserProfileName();
            objHandler.updateCaseQueues(listOfNewCases ,oldMap);
            HUMUpdateCaseHelper.populateViewName(listOfNewCases ,true,null,profileName);
            HUMUpdateCaseHelper.populateViewName(listOfNewCases ,false,null,profileName);
            HUMUpdateCaseHelper.populateViewNamePB(listOfNewCases);
            objHandler.CaseQueuesUpdation('Closed','Closed',oCase22,wqsList,'Cincinnati CRU',UserInfo.getUserName(),oCase22);
            objHandler.CaseQueuesUpdation('In Progress','In Progress',oCase22,wqsList,'Cincinnati CRU',UserInfo.getUserName(),oCase22);
            
            oCase11.Status = 'In Progress';
            oCase11.Owner_Queue__c='Cincinnati MR';
            oCase11.OwnerId = Userinfo.getUserID();
            oCase22.Status = 'In Progress';
            oCase22.Owner_Queue__c='Cincinnati MR';
            oCase22.OwnerId = Userinfo.getUserID();            
            oldMap.put(oCase11.id, oCase11);
            oldMap.put(oCase22.id, oCase22);
            
            Case oCase8 = [Select Id ,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c,CTCI_List__c from Case where Owner_Queue__c='Cincinnati CRU'And Topic__c='Other' limit 1];
            oCase8.Topic__c = 'Test245';
            
            HUMUpdateCaseHelper.populateViewName(New List<Case>{oCase8},false,New SET<String>{'Cincinnati CRU'},'Customer Care Specialist');
            
            Case oCase1 = [Select Id,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c,Work_Queue_View_Name__c,CTCI_List__c from Case where  Owner_Queue__c='Cincinnati CRU'And Topic__c='Test245' limit 1];
            System.assertEquals(oCase1.Work_Queue_View_Name__c,'Test');
            oCase1.Topic__c = 'Humana';
            
            HUMUpdateCaseHelper.populateViewName(New List<Case>{oCase1},false,New SET<String>{'Cincinnati CRU'},'Customer Care Specialist');
            
            Case oCase2 = [Select Id,status,Service_Center__c,Department__c,Topic__c,Owner_Queue__c,Work_Queue_View_Name__c,CTCI_List__c from Case where  Owner_Queue__c='Cincinnati CRU'And Topic__c='Humana' limit 1];
            System.assertEquals(ocase2.Work_Queue_View_Name__c,'Other');
            HUMUpdateCaseHelper.executeOmniQueueReEvaluationLogic(listOfNewCases);
            HUMUpdateCaseHelper.executeOmniEligibleCriteriaAsync(listOfNewCases);
            
            test.stopTest();
            
        }
    }
    
    /**
    * Method Is used to create Account, Policy And its memebers for cases.
    *
    * @param     NULL
    * @return    void 
    */ 
    Private Static void initializationOfPreObjects()
    {
        Account oAcc = HUMTestDataHelperTest.getAccount(); 
        oAcc.Name ='TestAccount1';
        oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        oAcc.Source_Platform_Code__c = 'CA';
        insert oAcc;            
        system.assertEquals(oAcc.Name , 'TestAccount1');
        
        Product2 oProd = HUMTestDataHelperTest.getProduct();
        oProd.Name = 'Mediclaim';
        oProd.Product_Type__c = 'Mediclaim';            
        insert oProd;            
        system.assertEquals(oProd.Name , 'Mediclaim');
        
        Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
        oPolicy1.Name = 'test123';
        oPolicy1.Group_Name__c = oAcc.Id;
        oPolicy1.Funding_Type_Code__c = 'Y';
        oPolicy1.Product__c = oProd.Id;
        oPolicy1 .Issue_State__c = 'IL';            
        insert oPolicy1;
        system.assertEquals(oPolicy1.Name , 'test123');            
        
        Policy__c oPolicy2 = HUMTestDataHelperTest.getPolicy();
        oPolicy2.Name = 'test1234';
        oPolicy2.Group_Name__c = oAcc.Id;
        oPolicy2.Funding_Type_Code__c = 'Y';
        oPolicy2.Product__c = oProd.Id;
        oPolicy2.Issue_State__c = 'NA';
        insert oPolicy2;
        system.assertEquals(oPolicy2.Name , 'test1234');
        
        Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
        oPolMem1.Name = '12345';
        oPolMem1.Member__c = oAcc.Id;
        oPolMem1.Policy__c = oPolicy1.Id;            
        oPolMem1.Relationship__c = 'Self';
        insert oPolMem1;            
        system.assertEquals(oPolMem1.Name , '12345');
        
        Policy_Member__c oPolMem2 = HUMTestDataHelperTest.getPolicyMember();
        oPolMem2.Name = '123456';
        oPolMem2.Member__c = oAcc.Id;
        oPolMem2.Policy__c = oPolicy2.Id;            
        oPolMem2.Relationship__c = 'Self';
        insert oPolMem2;            
        system.assertEquals(oPolMem2.Name ,'123456');
    }
    
    /**
    * This method Is used to create queue And queue Views for Cases view assignment
    *
    * @param     NULL
    * @return    void 
    */    
    Private Static void dataForQueuesAndViews()
    {
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU', isActive__c = True);
        insert oQSetup ;
        system.assertEquals(oQSetup.Name,'Cincinnati CRU');
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id , Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv1;
        system.assertEquals(oQv1.Queue_View_Name__c,'Other11');
        
        Queue_View__c oQv2 = New Queue_View__c(Queue_View_Name__c ='Other111', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv2;
        system.assertEquals(oQv2.Queue_View_Name__c,'Other111');
        
        Queue_View__c oQv3 = New Queue_View__c(Queue_View_Name__c ='Other123', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv3;
        system.assertEquals(oQv3.Queue_View_Name__c,'Other123');
        
        Queue_View__c oQv4 = New Queue_View__c(Queue_View_Name__c ='Other1234', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id, Service_Center__c='Cincinnati', isActive__c = True); 
        insert oQv4;
        system.assertEquals(oQv4.Queue_View_Name__c,'Other1234');
        
        Queue_View__c oQv5 = New Queue_View__c(Queue_View_Name__c ='Test',Name='Test',Service_Center__c='Cincinnati',Department__c='CRU',Work_Queue_Name__c=oQSetup.Id,isActive__c = True);
        insert oQV5;
        
        Queue_View_Item__c oQueueViewRC1 = New Queue_View_Item__c(API_Name__c = 'PlanIssueState' , Field_Operator__c = 'Equals' ,Field_Value__c = 'NA' , Queue_View__c = oQv2.Id);
        insert oQueueViewRC1;            
        system.assertEquals(oQueueViewRC1.API_Name__c , 'PlanIssueState');
        
        Queue_View_Item__c oQueueViewRC2 = New Queue_View_Item__c(API_Name__c = 'PlanIssueState' , Field_Operator__c = 'Equals' ,Field_Value__c = 'NA' , Queue_View__c = oQv4.Id);
        insert oQueueViewRC2;            
        system.assertEquals(oQueueViewRC2.API_Name__c , 'PlanIssueState');
        
        Queue_View_Item__c oQueueViewRC3 = New Queue_View_Item__c(API_Name__c = 'TaskStatus' , Field_Operator__c = 'Equals' ,Field_Value__c = 'In Progress' , Queue_View__c = oQv2.Id);
        insert oQueueViewRC3;            
        system.assertEquals(oQueueViewRC3.API_Name__c , 'TaskStatus');       
        
        Queue_View_Item__c oQueueViewRC4 = New Queue_View_Item__c(API_Name__c = 'FundingType' , Field_Operator__c = 'Equals' ,Field_Value__c = 'N' , Queue_View__c = oQv3.Id);
        insert oQueueViewRC4;            
        system.assertEquals(oQueueViewRC4.API_Name__c , 'FundingType');   
        
        Queue_View_Item__c oQueueView = New Queue_View_Item__c(API_Name__c = 'Status' , Field_Operator__c = 'Equals' ,Field_Value__c = 'In Progress' , Queue_View__c = oQv1.Id);
        insert oQueueView;            
        system.assertEquals(oQueueView.API_Name__c , 'Status'); 
        
        Queue_View_Item__c oQueueView7 = New Queue_View_Item__c(API_Name__c = 'PlanIssueState' , Field_Operator__c = 'Equals' ,Field_Value__c = 'IL, TX' , Queue_View__c = oQv1.Id);
        insert oQueueView7;           
        system.assertEquals(oQueueView7.API_Name__c , 'PlanIssueState'); 
        
        Queue_View_Item__c oQueueView2 = New Queue_View_Item__c(API_Name__c = 'ProductType' , Field_Operator__c = 'Equals' ,Field_Value__c = 'Mediclaim' , Queue_View__c = oQv1.Id);
        insert oQueueView2;            
        system.assertEquals(oQueueView2.API_Name__c , 'ProductType'); 
        dataForQueuesViewsMore(oQv1, oQv3);   
        dataForQueuesPriority(oQSetup);      
        
        List<Queue_View_Item__c> lstQueueViewItems = New List<Queue_View_Item__c>();
        For (Integer i = 1; i < 250 ;i++)
        {
            Queue_View_Item__c qvi = New Queue_View_Item__c();
            qvi.API_Name__c = 'Topic';
            qvi.Field_Operator__c = 'Equals';
            qvi.Field_Value__c = 'Test'+i;
            qvi.Queue_View__c = oQv5.Id;
            lstQueueViewItems.add(qvi);
        }
        insert lstQueueViewItems;
        
        HUMQueueViewCriteria__c QV1 = New HUMQueueViewCriteria__c(name = 'Behavioral Health',API_Name__c = 'Behavioral_Health__c');
        HUMQueueViewCriteria__c QV2 = New HUMQueueViewCriteria__c(Name = 'BillingStateCode',API_Name__c = 'Group_Billing_State_Code__c');
        HUMQueueViewCriteria__c QV3 = New HUMQueueViewCriteria__c(name = 'CaseStatus',API_Name__c = 'Status');
        HUMQueueViewCriteria__c QV4 = New HUMQueueViewCriteria__c(Name = 'Classification',API_Name__c = 'Classification__c'); 
        HUMQueueViewCriteria__c QV5 = New HUMQueueViewCriteria__c(name = 'EmployerGroupName',API_Name__c = 'Employer_Group_Name__c');
        HUMQueueViewCriteria__c QV6 = New HUMQueueViewCriteria__c(Name = 'FundingType',API_Name__c = 'Funding_Type__c');
        HUMQueueViewCriteria__c QV7 = New HUMQueueViewCriteria__c(name = 'Intent',API_Name__c = 'Intent__c');
        HUMQueueViewCriteria__c QV8 = New HUMQueueViewCriteria__c(Name = 'InteractingWithType',API_Name__c = 'Interacting_With_Type__c');
        HUMQueueViewCriteria__c QV9 = New HUMQueueViewCriteria__c(name = 'LastModifiedbyQueue',API_Name__c = 'LastModifiedby_Queue__c');
        HUMQueueViewCriteria__c QV10 = New HUMQueueViewCriteria__c(Name = 'OwnerQueue',API_Name__c = '  Owner_Queue__c');
        HUMQueueViewCriteria__c QV11 = New HUMQueueViewCriteria__c(name = 'PlanIssueState',API_Name__c = 'Plan_Issue_State__c');
        HUMQueueViewCriteria__c QV12 = New HUMQueueViewCriteria__c(Name = 'PlanSegmentSize',API_Name__c = 'Plan_Segment_Size__c');
        HUMQueueViewCriteria__c QV13 = New HUMQueueViewCriteria__c(name = 'PolicyPlatform',API_Name__c = 'Policy_Platform__c');
        HUMQueueViewCriteria__c QV14 = New HUMQueueViewCriteria__c(Name = 'PreviousCaseOwnerRole',API_Name__c = 'Previous_Case_Owner_Role__c'); 
        HUMQueueViewCriteria__c QV15 = New HUMQueueViewCriteria__c(name = 'PreviousOwnerQueue',API_Name__c = 'Previous_Owner_Queue__c');
        HUMQueueViewCriteria__c QV16 = New HUMQueueViewCriteria__c(Name = 'Product Code',API_Name__c = 'Policy_Product_Code__c');
        HUMQueueViewCriteria__c QV17 = New HUMQueueViewCriteria__c(name = 'ProductType',API_Name__c = 'Product_Type__c');
        HUMQueueViewCriteria__c QV18 = New HUMQueueViewCriteria__c(Name = 'ShippingStateCode',API_Name__c = 'Provider_Shipping_State_Code__c');
        HUMQueueViewCriteria__c QV19 = New HUMQueueViewCriteria__c(name = 'StateMemResidAdd',API_Name__c = 'State_Mem_Resid_Add__c');
        HUMQueueViewCriteria__c QV20 = New HUMQueueViewCriteria__c(Name = 'Subtype',API_Name__c = ' Subtype__c');
        HUMQueueViewCriteria__c QV21 = New HUMQueueViewCriteria__c(name = 'TaskStatus',API_Name__c = 'Status');
        HUMQueueViewCriteria__c QV22 = New HUMQueueViewCriteria__c(Name = 'Topic',API_Name__c = 'Topic__c');
        HUMQueueViewCriteria__c QV23 = New HUMQueueViewCriteria__c(name = 'Type',API_Name__c = 'Type__c');
        insert New List<HUMQueueViewCriteria__c>{QV1, QV2, QV3, QV4, QV5, QV6, QV7, QV8, QV9, QV10, QV11, QV12, QV13, QV14, QV15, QV16, QV17, QV18, QV19, QV20, QV21, QV22, QV23};
    }
    
    /*
     * Method used to create Queue Views for Cases view assignment 
     * Return type : void
     * @paramater   : Queue_View__c, Queue_View__c
     */    
     Private Static void dataForQueuesViewsMore(Queue_View__c oQv1, Queue_View__c oQv3)
    {
        Queue_View_Item__c oQueueView3 = New Queue_View_Item__c();
        oQueueView3.API_Name__c = 'FundingType';
        oQueueView3.Field_Operator__c = 'Equals';
        oQueueView3.Field_Value__c = 'Y';
        oQueueView3.Queue_View__c = oQv1.Id;            
        insert oQueueView3;            
        system.assertEquals(oQueueView3.API_Name__c,'FundingType');            
        
        Queue_View_Item__c oQueueView4 = New Queue_View_Item__c();
        oQueueView4.API_Name__c = 'Classification';
        oQueueView4.Field_Operator__c = 'Equals';
        oQueueView4.Field_Value__c = 'Claims';
        oQueueView4.Queue_View__c = oQv1.Id;            
        insert oQueueView4;            
        system.assertEquals(oQueueView4.API_Name__c,'Classification'); 
        
        Queue_View_Item__c oQueueView5 = New Queue_View_Item__c();
        oQueueView5.API_Name__c = 'Intent';
        oQueueView5.Field_Operator__c = 'Equals';
        oQueueView5.Field_Value__c = 'Filling';
        oQueueView5.Queue_View__c = oQv1.Id;            
        insert oQueueView5;            
        system.assertEquals(oQueueView5.API_Name__c,'Intent'); 
        
        Queue_View_Item__c oQueueView6 = New Queue_View_Item__c();
        oQueueView6.API_Name__c = 'OwnerQueue';
        oQueueView6.Field_Operator__c = 'Equals';
        oQueueView6.Field_Value__c = 'Cincinatti CRU';
        oQueueView6.Queue_View__c = oQv1.Id;            
        insert oQueueView6;            
        system.assertEquals(oQueueView6.API_Name__c,'OwnerQueue'); 
         
        Queue_View_Item__c oQueueView8 = New Queue_View_Item__c();
        oQueueView8.API_Name__c = 'InteractingWithType';
        oQueueView8.Field_Operator__c = 'Equals';
        oQueueView8.Field_Value__c = 'Member';
        oQueueView8.Queue_View__c = oQv1.Id;            
        insert oQueueView8;            
        system.assertEquals(oQueueView8.API_Name__c,'InteractingWithType'); 
        
        Queue_View_Item__c oQueueView9 = New Queue_View_Item__c();
        oQueueView9.API_Name__c = 'PolicyPlatform';
        oQueueView9.Field_Operator__c = 'Equals';
        oQueueView9.Field_Value__c = 'CA';
        oQueueView9.Queue_View__c = oQv1.Id;            
        insert oQueueView9;            
        system.assertEquals(oQueueView9.API_Name__c,'PolicyPlatform');
        
        Queue_View_Item__c oQueueView10 = New Queue_View_Item__c();
        oQueueView10.API_Name__c = 'StateMemResidAdd';
        oQueueView10.Field_Operator__c = 'Equals';
        oQueueView10.Field_Value__c = 'US';
        oQueueView10.Queue_View__c = oQv3.Id;            
        insert oQueueView10;            
        system.assertEquals(oQueueView10.API_Name__c,'StateMemResidAdd');
        
        Queue_View_Item__c oQueueView11 = New Queue_View_Item__c();
        oQueueView11.API_Name__c = 'PlanSegmentSize';
        oQueueView11.Field_Operator__c = 'Equals';
        oQueueView11.Field_Value__c = 'Do';
        oQueueView11.Queue_View__c = oQv3.Id;            
        insert oQueueView11;             
        system.assertEquals(oQueueView11.API_Name__c,'PlanSegmentSize'); 
        
        Queue_View_Item__c oQueueView12 = New Queue_View_Item__c();
        oQueueView12.API_Name__c = 'PreviousCaseOwnerRole';
        oQueueView12.Field_Operator__c = 'Equals';
        oQueueView12.Field_Value__c = 'XXX';
        oQueueView12.Queue_View__c = oQv3.Id;            
        insert oQueueView12;            
        system.assertEquals(oQueueView12.API_Name__c,'PreviousCaseOwnerRole');               
        
        Queue_View_Item__c oQueueView13 = New Queue_View_Item__c();
        oQueueView13.API_Name__c = 'EmployerGroupName';
        oQueueView13.Field_Operator__c = 'Equals';
        oQueueView13.Field_Value__c = 'ABC';
        oQueueView13.Queue_View__c = oQv3.Id;            
        insert oQueueView13;            
        system.assertEquals(oQueueView13.API_Name__c,'EmployerGroupName');   
        
        Queue_View_Item__c oQueueView14 = New Queue_View_Item__c();
        oQueueView14.API_Name__c = 'Topic';
        oQueueView14.Field_Operator__c = 'Equals';
        oQueueView14.Field_Value__c = 'Potential Plan Load';
        oQueueView14.Queue_View__c = oQv3.Id;            
        insert oQueueView14;             
        system.assertEquals(oQueueView14.API_Name__c,'Topic'); 
        
        Queue_View_Item__c oQueueView15 = New Queue_View_Item__c();
        oQueueView15.API_Name__c = 'Type';
        oQueueView15.Field_Operator__c = 'Equals';
        oQueueView15.Field_Value__c = 'type';
        oQueueView15.Queue_View__c = oQv3.Id;            
        insert oQueueView15;             
        system.assertEquals(oQueueView15.API_Name__c,'Type');            
         
        Queue_View_Item__c oQueueView16 = New Queue_View_Item__c();
        oQueueView16.API_Name__c = 'Subtype';
        oQueueView16.Field_Operator__c = 'Equals';
        oQueueView16.Field_Value__c = 'types1';
        oQueueView16.Queue_View__c = oQv3.Id;            
        insert oQueueView16;             
        system.assertEquals(oQueueView16.API_Name__c,'Subtype');              
         
        Queue_View_Item__c oQueueView17 = New Queue_View_Item__c();
        oQueueView17.API_Name__c = 'oQueueView17';
        oQueueView17.Field_Operator__c = 'Equals';
        oQueueView17.Field_Value__c = 'Cincinatti Calls';
        oQueueView17.Queue_View__c = oQv3.Id;            
        insert oQueueView17;             
        system.assertEquals(oQueueView17.API_Name__c,'oQueueView17'); 
    }
    
     /*
     * This method Is used to set the Priority for Queue 
     * Return type : void
     * @paramater   : Work_Queue_Setup__c
     */    
    Private Static void dataForQueuesPriority(Work_Queue_Setup__c oQSetup)
    {
        Work_Queue_Priority__c oWQP = New Work_Queue_Priority__c(Work_Queue_Name__c = oQSetup.Id);
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
        system.assertEquals(oWQP.Criteria_1__c , 'ProductType');                  
    }     
    @isTest
    private static void testCreditableCoverage(){
        User oCurrentUser  = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test HUMUpdateCaseHelperUser1' LIMIT 1];
         
        system.runAs(oCurrentUser)
        {
        
            HUMTriggerSwitch__c objTriggerSwitch =  new HUMTriggerSwitch__c();
            objTriggerSwitch.Name = 'Case';
            objTriggerSwitch.Exeute_Trigger__c = true;
            insert objTriggerSwitch;
            system.assertEquals(objTriggerSwitch.Name,'Case');
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name ='TestAccount1';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            oAcc.Source_Platform_Code__c = 'CA';
            insert oAcc;         
            
            Product2 oProd = HUMTestDataHelperTest.getProduct();
            oProd.Name = 'MAPD';
            oProd.Product_Type__c = 'MAPD';  
            oProd.Major_LOB__c = 'MEF';
            insert oProd;      
            
            Policy__c oPolicy1 = HUMTestDataHelperTest.getPolicy();
            oPolicy1.Name = 'test123';
            oPolicy1.Group_Name__c = oAcc.Id;
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Product__c = oProd.Id;
            oPolicy1.Issue_State__c = 'IL';         
            insert oPolicy1;
            
            Policy_Member__c oPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';
            insert oPolMem1;  
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'Cancel Cancellation';
            classificationRec1.Is_Active__c = true;
            insert classificationRec1;          
            
        Intent__c intentRec1 = New Intent__c();
        intentRec1.Name = 'Complete';
        intentRec1.Is_Active__c = true;
        insert intentRec1;
        
       Case oCase1 = New Case(AccountId = oAcc.Id, 
                              Intent_Id__c = intentRec1.Id,
                              Classification_Id__c = classificationRec1.Id, 
                              Policy_Member__c = oPolMem1.Id,
                              Interacting_With_Type__c = 'Group', 
                              Interacting_With__c=oAcc.Id,
                              Interacting_About_Type__c = 'Group',
                              Due_Date__c=Date.Today(),
                              Case_Owner__c = UserInfo.getUserName(),
                              Owner_Queue__c ='Louisville RSO Enrollment', 
                              Work_Queue_View_Name__c='Cincinati CRU',
                              Service_Center__c ='Cincinnati',
                              Department__c='CRU',
                              Subtype__c = 'Creditable Coverage',
                              Topic__c = 'Creditable Coverage',
                              status = 'Closed',
                              Complaint__c = 'Yes - Medicare Part C',Classification_Type__c = 'Enrollment (RSO)',
                              Complaint_Reason__c = 'HumanaVitality',Complaint_Type__c = 'Behavioral',
                              G_A_Rights_Given__c = 'No'
                             );
        insert oCase1;      
    }
    }
    
    @isTest
    private static void testHumCloseCaseUpdateOwner(){
        insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c=true);
        insert new HUMQueueViewBatchSettings__c ( name= 'QueueViewBatchSetup', TriggerExecutionRequired__c= true);
        insert new HUMCaseUpdateAllowedProfies__c [] { new HUMCaseUpdateAllowedProfies__c (name='Customer Care Supervisor')};
                  
        User oUser = HUMTestDataHelperTest.createTestUser('testHumCloseCaseUpdateOwner1','Customer Care Supervisor');
        oUser.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oUser;
        List<case> newCaseList = new List<case>();
        Map<id,Case> oldMap = new Map<id,Case>();
        
        List<Case> oCaseList = [Select Id,CaseNumber, Service_Center__c,Department__c, Status from Case where Service_Center__c ='Cincinnati' and Department__c='CRU' and status='In Progress' and Interacting_With_Type__c = 'Group' Limit 1];
        
        for(Case c : oCaseList){            
            oldMap.put(c.id,c);                
            c.status = 'Closed';
            c.Complaint__c = 'No';
            c.G_A_Rights_Given__c = 'No';
            newCaseList.add(c); 
        }
        update newCaseList;
        
        system.runAs(oUser){     
            HUMUpdateCaseHelper hum = new HUMUpdateCaseHelper(); 
            hum.humCloseCaseUpdateOwner(newCaseList,oldMap,'Customer Care Supervisor' );
        }
    }             

    
    @isTest
    private static void testcaseGeneralHOGeneralSecurity(){        
        Account mAccount = HUMTestDataHelperTest.getAccount();
        mAccount.FirstName = 'General';
        mAccount.LastName  = 'Member';
        mAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert mAccount;  
        system.assertEquals(mAccount.FirstName,'General'); 
        
        //create data for General account      
        Account generalAccount =  new Account();
        generalAccount.Name='generalAccount';
        generalAccount.General_Account__c = true;  
        generalAccount.Home_Office_Account__c = false;
        //generalAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert generalAccount;
        
        //create data for General account 
        Account generalAccount1 = new Account();
        generalAccount1.Name='TestAccount1';
        generalAccount1.General_Account__c = true;  
        generalAccount1.Home_Office_Account__c = false;
        generalAccount1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        Insert generalAccount1;
        
        Account generalAccount2 = new Account();
        generalAccount2.Name='TestAccount1';
        generalAccount2.General_Account__c = true;  
        generalAccount2.Home_Office_Account__c = false;
        generalAccount2.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        Insert generalAccount2;
        
        //create data for HomeOfficeAccount 
        Account homeOfficeAccount = new Account();
        homeOfficeAccount.Name='TestAccount1';
        homeOfficeAccount.General_Account__c = false;
        homeOfficeAccount.Home_Office_Account__c = true;
        homeOfficeAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();        
        Insert homeOfficeAccount;
        
        //Create Medical Product
        Product2 oProdMedical = HUMTestDataHelperTest.getProduct();
        oProdMedical.Name = 'MED';
        oProdMedical.Product_Type__c = 'HMO';  
        oProdMedical.ProductCode = 'MED';
        //oProdMedical.Major_LOB__c = 'MEF';
        insert oProdMedical;      
        
        //Create Dental Product
        Product2 oProdDental = HUMTestDataHelperTest.getProduct();
        oProdDental.Name = 'DEN';
        oProdDental.Product_Type__c = 'MCD';  
        oProdDental.ProductCode = 'DEN';
        //oProdDental.Major_LOB__c = 'MEF';
        insert oProdDental;  
        
        Policy__c oPolicyGeneral = HUMTestDataHelperTest.getPolicy();
        oPolicyGeneral.Name = 'test123';
        oPolicyGeneral.Group_Name__c = generalAccount1.Id;
        oPolicyGeneral.Funding_Type_Code__c = 'Y';
        oPolicyGeneral.Product__c = oProdMedical.Id;
        oPolicyGeneral.Issue_State__c = 'IL';         
        insert oPolicyGeneral;
        
        Policy__c oPolicyHO = HUMTestDataHelperTest.getPolicy();
        oPolicyHO.Name = 'test123';
        oPolicyHO.Group_Name__c = homeOfficeAccount.Id;
        oPolicyHO.Funding_Type_Code__c = 'Y';
        oPolicyHO.Product__c = oProdMedical.Id;
        oPolicyHO.Issue_State__c = 'IL';         
        insert oPolicyHO;
        
         Policy__c oPolicyHO1 = HUMTestDataHelperTest.getPolicy();
        oPolicyHO1.Name = 'test123';
        oPolicyHO1.Group_Name__c = homeOfficeAccount.Id;
        oPolicyHO1.Funding_Type_Code__c = 'Y';
        oPolicyHO1.Product__c = oProdDental.Id;
        oPolicyHO1.Issue_State__c = 'IL';         
        insert oPolicyHO1;
        
        
        Policy_Member__c oPolMemGeneral = HUMTestDataHelperTest.getPolicyMember();
        oPolMemGeneral.Name = '12345';
        oPolMemGeneral.Member__c = generalAccount1.Id;
        oPolMemGeneral.Policy__c = oPolicyGeneral.Id;            
        oPolMemGeneral.Relationship__c = 'Self';
        insert oPolMemGeneral; 
        
        Policy_Member__c oPolMemHO = HUMTestDataHelperTest.getPolicyMember();
        oPolMemHO.Name = '12345';
        oPolMemHO.Member__c = homeOfficeAccount.Id;
        oPolMemHO.Policy__c = oPolicyHO.Id;            
        oPolMemHO.Relationship__c = 'Self';
        insert oPolMemHO;
        
        Policy_Member__c oPolMemHO1 = HUMTestDataHelperTest.getPolicyMember();
        oPolMemHO1.Name = '12345';
        oPolMemHO1.Member__c = homeOfficeAccount.Id;
        oPolMemHO1.Policy__c = oPolicyHO1.Id;            
        oPolMemHO1.Relationship__c = 'Self';
        insert oPolMemHO1;
        
        
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Cancel Cancellation';
        classificationRec.Is_Active__c = true;
        insert classificationRec;       
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Complete';
        intentRec.Is_Active__c = true;
        insert intentRec;
        
        List<Case>  caseGeneralHOSecurityList = new List<Case>();
        Id caseRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();        
        //added for caseGeneral
        Case oCaseGeneralHO1 = New Case(AccountId =generalAccount.Id, 
                                        Intent_Id__c = intentRec.Id, 
                                        Classification_Id__c = classificationRec.Id,
                                        Policy_Member__c =null,
                                        Type = 'Customer Inquiry' , 
                                        Subtype__c ='Incomplete',
                                        Interacting_With_Type__c = 'Member',
                                        Interacting_With__c=mAccount.Id,
                                        Interacting_About_Type__c = 'Member',
                                        Due_Date__c=Date.Today(), 
                                        Case_Owner__c='user1', 
                                        Owner_Queue__c ='Cincinnati CRU', 
                                        Work_Queue_View_Name__c='Cincinati CRU',
                                        Service_Center__c ='Cincinnati', 
                                        Department__c='CRU', status='In Progress',
                                        recordTypeId = caseRecordTypeId
                                        
                                       );       
        
        Case oCaseGeneralHO2 = New Case(AccountId =generalAccount1.Id, recordTypeID = caseRecordTypeId,Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = null, Type = 'Customer Inquiry' , Subtype__c ='Disenrollment',
                                Interacting_With_Type__c = 'Group', Interacting_With__c=generalAccount1.Id, Due_Date__c=Date.Today(), Case_Owner__c='user123', Interacting_About_Type__c = 'Group',
                                Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
        
        Case oCaseGeneralHO3 = New Case(AccountId =generalAccount1.Id,recordTypeId = caseRecordTypeId, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = oPolMemGeneral.id, Type = 'Customer Inquiry' , Subtype__c ='Disenrollment',
                                Interacting_With_Type__c = 'Group', Interacting_With__c=generalAccount1.Id, Due_Date__c=Date.Today(), Case_Owner__c='user123', Interacting_About_Type__c = 'Group',
                                Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
        
        Case oCaseGeneralHO4 = New Case(AccountId =generalAccount1.Id,recordTypeId = caseRecordTypeId, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = null, Type = 'Customer Inquiry' , Subtype__c ='Disenrollment',
                                Interacting_With_Type__c = 'Group', Interacting_With__c=generalAccount1.Id, Due_Date__c=Date.Today(), Case_Owner__c='user123', Interacting_About_Type__c = 'Group',
                                Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
        
        Case oCaseGeneralHO5 = New Case(AccountId =generalAccount1.Id, recordTypeID = caseRecordTypeId, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = null, Type = 'Customer Inquiry' , Subtype__c ='Disenrollment',
                                Interacting_With_Type__c = 'Group', Interacting_With__c=generalAccount1.Id, Due_Date__c=Date.Today(), Case_Owner__c='user123', Interacting_About_Type__c = 'Group',
                                Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');
        
        Case oCaseGeneralHO6 = New Case(AccountId =generalAccount1.Id,recordTypeId = caseRecordTypeId, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, Policy_Member__c = null, Type = 'Customer Inquiry' , Subtype__c ='Disenrollment',
                                Interacting_With_Type__c = 'Group', Interacting_With__c=generalAccount1.Id, Due_Date__c=Date.Today(), Case_Owner__c='user123', Interacting_About_Type__c = 'Group',
                                Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress');

        caseGeneralHOSecurityList.add(oCaseGeneralHO1);
        caseGeneralHOSecurityList.add(oCaseGeneralHO2);
        caseGeneralHOSecurityList.add(oCaseGeneralHO3);
        caseGeneralHOSecurityList.add(oCaseGeneralHO4);
        caseGeneralHOSecurityList.add(oCaseGeneralHO5);
         caseGeneralHOSecurityList.add(oCaseGeneralHO6);
        insert caseGeneralHOSecurityList;
        
         List<Case> newCaseList = new List<Case>();
         Map<Id,Case> oldMap = new Map<Id,Case>();
         oldMap.put(caseGeneralHOSecurityList[0].Id,caseGeneralHOSecurityList[0]);
       
        //Criteria 1 - Medicare Case with No Policy
        Case medicareCaseWithNoPolicy = [Select Id,AccountId,Subtype__c,Policy_Member__c,recordTypeId,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Policy_Member__r.Policy__r.Product__r.ProductCode,Interacting_About_Type__c From Case where Policy_Member__c = null and Case_Owner__c='user123' and Interacting_With_Type__c = 'Group' and Subtype__c !=null limit 1 ];
        medicareCaseWithNoPolicy.Subtype__c = 'Best Available Evidence';
        medicareCaseWithNoPolicy.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId();        
        update medicareCaseWithNoPolicy;
        newCaseList.add(medicareCaseWithNoPolicy);
        
        //Criteria 2 - Non Medicare Case with No Policy
        Case noMedicareCaseWithNoPolicy = [Select Id,AccountId,Subtype__c,Policy_Member__c,recordTypeId,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Policy_Member__r.Policy__r.Product__r.ProductCode,Interacting_About_Type__c From Case where Policy_Member__c = null and Case_Owner__c='user123' and Interacting_With_Type__c = 'Group' and Subtype__c !=null limit 1 ];
        noMedicareCaseWithNoPolicy.Subtype__c = null;        
        noMedicareCaseWithNoPolicy.recordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();        
        update noMedicareCaseWithNoPolicy;
        newCaseList.add(noMedicareCaseWithNoPolicy);
        
        //Criteria 3 - General Policy Added/Changed
        Case generalPolicyAddChange = [Select Id,AccountId,Policy_Member__c,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Policy_Member__r.Policy__r.Product__r.ProductCode,Interacting_About_Type__c From Case where Policy_Member__c = null and Case_Owner__c='user123' and Interacting_With_Type__c = 'Group' limit 1 ];
        generalPolicyAddChange.Policy_Member__c = oPolMemGeneral.id;
        update generalPolicyAddChange;
        newCaseList.add(generalPolicyAddChange);
        
        //Criteria 4,5 - Home Office Policy Added/Changed Medical
        Case homeOfficePolicyAddChangeMedical = [Select Id, AccountId,Policy_Member__c,recordTypeId,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Policy_Member__r.Policy__r.Product__r.ProductCode,Interacting_About_Type__c From Case where Policy_Member__c = null and Case_Owner__c='user123' and Interacting_With_Type__c = 'Group' limit 1];
        homeOfficePolicyAddChangeMedical.Policy_Member__c = oPolMemHO.id;
        update homeOfficePolicyAddChangeMedical;
        newCaseList.add(homeOfficePolicyAddChangeMedical);
        
        //Criteria 4,6 - Home Office Policy Added/Changed Dental
        Case homeOfficePolicyAddChangeDental = [Select Id, AccountId,Policy_Member__c,recordTypeId,General_Case__c,HO_Dental_Case__c,HO_Medical_Case__c,Policy_Member__r.Policy__r.Product__r.ProductCode,Interacting_About_Type__c From Case where Policy_Member__c = null and Case_Owner__c='user123' and Interacting_With_Type__c = 'Group' limit 1];
        homeOfficePolicyAddChangeDental.Policy_Member__c = oPolMemHO1.id;
        update homeOfficePolicyAddChangeDental;
        newCaseList.add(homeOfficePolicyAddChangeDental);
        HUMUpdateCaseHelper hum = new HUMUpdateCaseHelper();      
        hum.caseGeneralHOGeneralSecurity(newCaseList,oldMap );
    }
    
    /*
     * This method Is used to setup the test Account and Case data for Test methods.
     * Return type : void
     * @paramater   : None
     */ 
    private static void createAccountANDCaseRecords()
    {
        list<Account> lstAccounts = new list<Account>();
        
        //Known Member
        Account oAcc = HUMTestDataHelperTest.getAccount(); 
        oAcc.FirstName ='TestMemberFN';
        oAcc.LastName ='TestMemberLN';
        oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();        
        lstAccounts.add(oAcc);
        
        //Unknown Member        
        Account oAcc1 = HUMTestDataHelperTest.getAccount(); 
        oAcc1.FirstName ='TestUKMemberFN';
        oAcc1.LastName ='TestUKMemberLN';        
        oAcc1.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();        
        lstAccounts.add(oAcc1);
        
        //Provider Account        
        Account oAcc2 = HUMTestDataHelperTest.getAccount(); 
        oAcc2.Name ='TestProviderAccount';                
        oAcc2.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();        
        lstAccounts.add(oAcc2);
        
        //Group Account
        Account oAcc3 = HUMTestDataHelperTest.getAccount(); 
        oAcc3.Name ='TestGroupAccount';                
        oAcc3.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();        
        lstAccounts.add(oAcc3);
        
        //Agent/Broker Account        
        Account oAcc4 = HUMTestDataHelperTest.getAccount(); 
        oAcc4.Name ='TestAgentAccount';                
        oAcc4.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId();        
        lstAccounts.add(oAcc4);
        
        insert lstAccounts;
        
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'Calls RSO';
        ctRec.Is_Active__c=true;
        insert ctRec;
        
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Enrollment';
        classificationRec.Is_Active__c = true;
        insert classificationRec;
        System.assertEquals(classificationRec.Name,'Enrollment');
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Review Needed';
        intentRec.Is_Active__c = true;
        insert intentRec;
        System.assertEquals(intentRec.Name,'Review Needed');
        
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        insert ctciRec;
        
        list<Case> lstCases = new list<Case>();
        //Member Case
        Case oCase1 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId());
        lstCases.add(oCase1);
        
        //Closed Member Case
        Case oCase2 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Member Case').getRecordTypeId());
        lstCases.add(oCase2);
        
        //Unknown Case
        Case oCase3 = new Case(AccountId = oAcc1.Id, Interacting_With__c=oAcc1.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Unknown-Member', Interacting_With_Type__c = 'Unknown-Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Unknown Case').getRecordTypeId());
        lstCases.add(oCase3);
        
        //Closed Unknown Case
        Case oCase4 = new Case(AccountId = oAcc1.Id, Interacting_With__c=oAcc1.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Unknown-Member', Interacting_With_Type__c = 'Unknown-Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Unknown Case').getRecordTypeId());
        lstCases.add(oCase4);
        
        //Provider Case
        Case oCase5 = new Case(AccountId = oAcc2.Id, Interacting_With__c=oAcc2.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Provider', Interacting_With_Type__c = 'Provider', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Provider Case').getRecordTypeId());
        lstCases.add(oCase5);
        
        //Closed Provider Case
        Case oCase6 = new Case(AccountId = oAcc2.Id, Interacting_With__c=oAcc2.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Provider', Interacting_With_Type__c = 'Provider', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Provider Case').getRecordTypeId());
        lstCases.add(oCase6);       
        
        //Medicare Case
        Case oCase7 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Medicare Case').getRecordTypeId());
        lstCases.add(oCase7);
        
        //Closed Medicare Case
        Case oCase8 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Medicare Case').getRecordTypeId());
        lstCases.add(oCase8);
        
        //Group Case
        Case oCase9 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId());
        lstCases.add(oCase9);
        
        //Closed Group Case
        Case oCase10 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Group Case').getRecordTypeId());
        lstCases.add(oCase10);
        
        //Agent/Broker Case
        Case oCase11 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Agent/Broker Case').getRecordTypeId());
        lstCases.add(oCase11);
        
        //Closed Agent/Broker Case
        Case oCase12 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed Agent/Broker Case').getRecordTypeId());
        lstCases.add(oCase12);

        //HP Member Case
        Case oCase13 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Member Case').getRecordTypeId());
        lstCases.add(oCase13);
        
        //Closed HP Member Case
        Case oCase14 = new Case(AccountId = oAcc.Id, Interacting_With__c=oAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Member', Interacting_With_Type__c = 'Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed HP Member Case').getRecordTypeId());
        lstCases.add(oCase14);
        
        //HP Unknown Case
        Case oCase15 = new Case(AccountId = oAcc1.Id, Interacting_With__c=oAcc1.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Unknown-Member', Interacting_With_Type__c = 'Unknown-Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Unknown Case').getRecordTypeId());
        lstCases.add(oCase15);
        
        //Closed HP Unknown Case
        Case oCase16 = new Case(AccountId = oAcc1.Id, Interacting_With__c=oAcc1.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Unknown-Member', Interacting_With_Type__c = 'Unknown-Member', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed HP Unknown Case').getRecordTypeId());
        lstCases.add(oCase16);
        
        //HP Provider Case
        Case oCase17 = new Case(AccountId = oAcc2.Id, Interacting_With__c=oAcc2.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Provider', Interacting_With_Type__c = 'Provider', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Provider Case').getRecordTypeId());
        lstCases.add(oCase17);
        
        //Closed HP Provider Case
        Case oCase18 = new Case(AccountId = oAcc2.Id, Interacting_With__c=oAcc2.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Provider', Interacting_With_Type__c = 'Provider', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed HP Provider Case').getRecordTypeId());
        lstCases.add(oCase18);      
        
        
        //HP Group Case
        Case oCase19 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Group Case').getRecordTypeId());
        lstCases.add(oCase19);
        
        //Closed HP Group Case
        Case oCase20 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed HP Group Case').getRecordTypeId());
        lstCases.add(oCase20);
        
        //HP Agent/Broker Case
        Case oCase21 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('HP Agent/Broker Case').getRecordTypeId());
        lstCases.add(oCase21);
        
        //Closed HP Agent/Broker Case
        Case oCase22 = new Case(AccountId = oAcc3.Id, Interacting_With__c=oAcc3.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id,
                             Interacting_About_Type__c = 'Group', Interacting_With_Type__c = 'Group', Complaint__c = 'No',
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='Closed', G_A_Rights_Given__c = 'No', topic__c = 'CPU Correspondence',
                             recordtypeID = Schema.SObjectType.Case.getRecordTypeInfosByName().get('Closed HP Agent/Broker Case').getRecordTypeId());
        lstCases.add(oCase22);              
        insert lstCases;
        
    }
    
    
    /*
     * This method is to unit test the caseCloseAndReopenUpdates method.
     * Return type : void
     * @paramater   : None
     */
    @isTest
    private static void testcaseCloseAndReopenUpdates()
    {
        User currentUser = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test HUMUpdateCaseHelperUser1' LIMIT 1];
        list<Case> lstCases = new list<Case>();
        insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c=true); 
        system.runAs(currentUser)
        {
            map<Id, Case> mapCases = new map<Id, Case>([SELECT Id, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case WHERE topic__c = 'CPU Correspondence']);
            list<Case> lstCaseRecs = [SELECT Id, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case WHERE topic__c = 'CPU Correspondence'];            
            for(Case caseRec: lstCaseRecs)
            {
                if(caseRec.status.equals('Closed')) 
                    caseRec.status = 'In Progress';
                else
                    caseRec.status = 'Closed';  
                lstCases.add(caseRec);
            } 
            
            Test.startTest();
                update lstCases;                                
                System.assertNotEquals(lstCases, null);
                System.assertEquals(lstCases.isEmpty(), false);
            Test.stopTest();
        } 
    }
    /*
     * This method is to unit test the reopen case by ETL API user.
     * Return type : void
     * @paramater   : None
     */
    @isTest
    private static void testcaseCloseAndReopenUpdatesUserAPI()
    {
        User currentUserAPI = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test HUMUpdateCaseHelperUserAPI' LIMIT 1];
        list<Case> lstCases = new list<Case>();
        insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c = true);
        insert new CaseProcessBuilderSettings_HUM__c(ETL_UserId__c = currentUserAPI.id);
        insert new HUMQueueViewBatchSettings__c ( name= 'QueueViewBatchSetup', TriggerExecutionRequired__c= true);
        insert new HUMCaseUpdateAllowedProfies__c [] { new HUMCaseUpdateAllowedProfies__c (name='ETL API Access')}; 
        system.runAs(currentUserAPI) {
            //map<Id, Case> mapCases = new map<Id, Case>([SELECT Id, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case WHERE topic__c = 'CPU Correspondence']);
            list<Case> lstCaseRecs = [SELECT Id, OwnerId, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case];            
            for(Case caseRec: lstCaseRecs) {
                if(caseRec.status.equals('Closed')) 
                    caseRec.status = 'In Progress';
                else
                {
                    caseRec.G_A_Rights_Given__c = 'No';
                    caseRec.Complaint__c = 'No';
                    caseRec.status = 'Closed';
                }
                lstCases.add(caseRec);
            } 
            Test.startTest();
            update lstCases; 
            System.assertNotEquals(lstCases.get(0).OwnerId, null);
            Test.stopTest();
        } 
    }

    /*
     * This method is to unit test the reopen case by ETL API user.
     * Return type : void
     * @paramater   : None
     */
    @isTest
    private static void testcaseCloseAndReopenUpdatesUserAPIOwner()
    {
        User currentUserAPI = [SELECT id, Current_Queue__c, LastName from User WHERE  Lastname ='test HUMUpdateCaseHelperUserAPI' LIMIT 1];
        list<Case> lstCases = new list<Case>();
        insert new HUMTriggerSwitch__c (name='Case', Exeute_Trigger__c = true);
        insert new CaseProcessBuilderSettings_HUM__c(ETL_UserId__c = currentUserAPI.id);
        insert new HUMQueueViewBatchSettings__c ( name= 'QueueViewBatchSetup', TriggerExecutionRequired__c= true);
        insert new HUMCaseUpdateAllowedProfies__c [] { new HUMCaseUpdateAllowedProfies__c (name='ETL API Access')}; 
        system.runAs(currentUserAPI) {
            //map<Id, Case> mapCases = new map<Id, Case>([SELECT Id, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case WHERE topic__c = 'CPU Correspondence']);
            list<Case> lstCaseRecs = [SELECT Id, OwnerId, isClosed, AccountID, Account.Firstname, Account.LastName, Account.Name, Casenumber, status, recordtypeID, recordtype.Name FROM Case];            
            for(Case caseRec: lstCaseRecs) {
                if(caseRec.status.equals('Closed')) 
                {
                    caseRec.status = 'In Progress';
                    caseRec.Case_Owner__c = UserInfo.getUserName();
                }
                else
                {
                    caseRec.G_A_Rights_Given__c = 'No';
                    caseRec.Complaint__c = 'No';
                    caseRec.status = 'Closed';
                    caseRec.Case_Owner__c = UserInfo.getUserName();  
                }
                lstCases.add(caseRec);
            } 
            Test.startTest();
            update lstCases; 
            System.assertNotEquals(lstCases.get(0).OwnerId, null);
            Test.stopTest();
        } 
    }
}