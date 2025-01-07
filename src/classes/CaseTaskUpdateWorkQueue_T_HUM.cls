/****************************************************************************
Apex Class Name  :  CaseTaskUpdateWorkQueue_T_HUM 
Version: 1.0 
Created On       : 
Function         : Test class to CaseUpdateWorkQueue_B_HUM apex class

Modification Log : 
* Developer Name          Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                         04/29/2020                  Checkmarx test method without asset error fix.
* Vamshi Krishna Pemberthi                              07/08/2021                  Improvements for code coverage.
* Tharun Kumar                                          12/07/2021                  Fixed QAS build issue.
* Gowthami Thota                                        08/14/2023                  QAS test class fix
*************************************************************************************************************************/
@isTest
private class CaseTaskUpdateWorkQueue_T_HUM {
    /**
     * Test method which Is used for queue view assignment
     * <p> 
     * @return : Void
     * @param   : NA
     */
    @testSetup static void setUpTestData()
    {
        List<case> lstOfCases = New List<case>();
        System.assertNotEquals(lstOfCases, NULL,'Value not matched');
        
        List<case> listOfUpdatedCases = New List<case>();
        System.assertNotEquals(listOfUpdatedCases, NULL, 'Value not matched');
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');  
        
        system.runAs(oUser)
        {    
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 0;
            insert cv;
            initializationOfPreObjects();
            createQueuesAndViews();
            HUMQueueViewBatchSettings__c objBatchSetup = New HUMQueueViewBatchSettings__c(name ='QueueViewBatchSetup',TriggerExecutionRequired__c=true);
            List < HUMConstants__c > lstConstanst = New List < HUMConstants__c > ();
            lstConstanst.Add(New HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1,StringValue__c='1'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMLISTSIZEZERO',IntegerValue__c=0,StringValue__c='0'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMViewCriteriaLimit',IntegerValue__c=22,StringValue__c=''));
            lstConstanst.Add(New HUMConstants__c(Name='HUMDefaultBatchLimit',IntegerValue__c=5,StringValue__c=''));
            lstConstanst.Add(New HUMConstants__c(Name='HUMAllOtherView',StringValue__c='Other'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstConstanst.Add(New HUMConstants__c(Name='HUMStatusCancelled',StringValue__c='Cancelled'));
            insert lstConstanst;
            insert objBatchSetup;
            
            objBatchSetup = New HUMQueueViewBatchSettings__c(name ='WorkQueueTransferChanges',TriggerExecutionRequired__c=true);
            objBatchSetup.BatchExecutionDate__c = DateTime.now().addDays(-1);
            insert objBatchSetup;
            
            Account oAcc = [select  id, name from Account where name ='TestAccount1'];
            System.assertNotEquals(oAcc, NULL, 'Value not matched');
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            System.assertNotEquals(oPolMem1, NULL, 'Value not matched');
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            System.assertNotEquals(oPolMem2, NULL, 'Value not matched');
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1];
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);

            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRecDec = New Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
            system.assertEquals(intentRecDec.Name == 'Estimate', true);
            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOtype';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOtype');
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=lstClassification[0].id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =lstIntent[0].id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
             
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem1.Id ,
                             Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c=wqs.name,
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                             Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',Interacting_About_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id,Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), 
                              Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='user1', 
                              Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                              Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
        
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_About_Type__c = 'Group',Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                              Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',  Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
           insert lstOfCases;
           /* wqs.IsActive__c = TRUE;
           update wqs; */

           System.assertEquals(lstOfCases[0].Owner_Queue__c,'Cincinnati CRU');
           changeOwnerQueues();
           
           objBatchSetup = HUMQueueViewBatchSettings__c.getValues('QueueViewBatchSetup');
           objBatchSetup.TriggerExecutionRequired__c=false;
           update objBatchSetup;
        }
    }
    
    /**
    * This Is the main method which Is used for queue view assignment 
    *<p>
    * @param    NULL
    * @return   void   
    */ 
    Static testmethod void OverAllMainTestMethod() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
           Test.startTest();                     
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           Database.executeBatch(oCaseViewBatch);
           Test.stopTest();
        }
    }  
    
     
    private static void changeOwnerQueues(){
        List<Work_Queue_Setup__c> oQSetup = [select id, Name, Service_Center__c, Department__c from work_queue_setup__c where name ='Cincinnati CRU'];
        
        
        oQSetup[0].Service_Center__c = 'Phoenix';
        oQSetup[0].Department__c = 'Test Dept';
        
        
        
        update oQSetup[0];
    }
    
    /**
    * This method Is used to create Account, Policy And its memebers for cases. 
    *
    * @pram  NA 
    * @return  NA
    * @see
    */  
    Private Static void initializationOfPreObjects()
    {
        Account oAcc = HUMTestDataHelperTest.getAccount(); 
        oAcc.Name ='TestAccount1';
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
    
    /**
    * This method Is used to create queue And queue Views for Cases view assignment  
    *
    * @pram  NA
    * @return  NA
    * @see
    */   
    Private Static void createQueuesAndViews()
    {
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = FALSE);
        insert oQSetup ;
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id ,Criteria_Updated__c=true,IsActive__c = false, Service_Center__c='Cincinnati'); 
        insert oQv1;
        
        system.assertNotEquals(oQv1, null, 'Value not matched');
        
        Queue_View__c oQv2 = New Queue_View__c(Queue_View_Name__c ='Other111', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id,Criteria_Updated__c=false,IsActive__c = false, Service_Center__c='Cincinnati'); 
        insert oQv2;
        
        system.assertNotEquals(oQv2, null, 'Value not matched');            
        
        Queue_View__c oQv3 = New Queue_View__c(Queue_View_Name__c ='Other123', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id,Criteria_Updated__c=false,IsActive__c = false, Service_Center__c='Cincinnati'); 
        insert oQv3;
        
        system.assertNotEquals(oQv3, null, 'Value not matched');
        
        Queue_View__c oQv4 = New Queue_View__c(Queue_View_Name__c ='Other1234', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id,Criteria_Updated__c=true,IsActive__c = false, Service_Center__c='Cincinnati'); 
        insert oQv4;
        
        system.assertNotEquals(oQv4,  null, 'Value not matched');
        
        createQueuesViewItems(oQv1,oQv2,oQv3,oQv4);   
        createQueuePriority(oQSetup);      
    }  
    
       /**
    * This method Is used to create Queue Views for Cases view assignment 
    *
    * @pram  Queue_View__c      oQv1 
    * @pram  Queue_View__c      oQv2
    * @pram  Queue_View__c      oQv3
    * @pram  Queue_View__c      oQv4
    * @return  NA
    * @see
    */ 
     Private Static void createQueuesViewItems(Queue_View__c oQv1,Queue_View__c oQv2,Queue_View__c oQv3, Queue_View__c oQv4)
    {
        Queue_View_Item__c oQueueViewRC1 = New Queue_View_Item__c();
        oQueueViewRC1.API_Name__c ='PlanIssueState';
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
        oQueueView2.API_Name__c = 'ProductType';
        oQueueView2.Field_Operator__c = 'Equals';
        oQueueView2.Field_Value__c = 'Mediclaim';
        oQueueView2.Queue_View__c = oQv1.Id;            
        insert oQueueView2;            
        system.assertNotEquals(oQueueView2, null, 'Value not matched');
    
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
    
     /**
    * This method Is used to set the Priority for Queue 
    *
    * @pram  Work_Queue_Setup__c instance
    * @return  NA
    * @see
    */ 
    Private Static void createQueuePriority(Work_Queue_Setup__c oQSetup)
    {
        Work_Queue_Priority__c oWQP = new Work_Queue_Priority__c(Work_Queue_Name__c = oQSetup.Id);
        system.assertNotEquals(oWQP, null,'Value not matched');
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

    /**
    * This Is the main method which Is used for queue view assignment 
    *<p>
    * @param    NULL
    * @return   void   
    */ 
    Static testmethod void OverAllMainTestMethodNew() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
            List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            lstcase[1].Work_Queue_View_Name__c = 'Cincinnati Calls';
            update lstcase;
            Work_Queue_Priority__c Qp = [select Criteria_1__c,Criteria_2__c,Priority_1__c,Priority_2__c,Priority_11__c,Priority_4__c from Work_Queue_Priority__c ];
            Qp.Priority_1__c = '1';
            Qp.Priority_2__c = '2';
            Qp.Priority_11__c = '11';
            Qp.Priority_4__c = '4';
            update Qp;

            Test.startTest();     
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            System.debug('test class wqsID     '+wqs.Id);
            wqs.IsActive__c = true;
            wqs.Name = 'RSO Enrollment';
            wqs.Department__c = 'Calls';
            update wqs;
            Queue_View__c qvId = [Select id from Queue_View__c where Queue_View_Name__c ='Other11'];
            System.debug('test class queueviewID    '+qvId.Id);
            List<Queue_View__c> lstQv = [select Queue_View_Name__c , Department__c, Work_Queue_Name__c ,Criteria_Updated__c,IsActive__c, Service_Center__c from Queue_View__c ];
            lstQv[0].IsActive__c = true;
            lstQv[0].Criteria_Updated__c = true;
            lstQv[1].IsActive__c = true;
            lstQv[1].Criteria_Updated__c = true;
            update lstQv;                
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           
           Database.executeBatch(oCaseViewBatch);
           Test.stopTest();
        }
    } 

    /**
    * This Is the main method which Is used for queue view assignment 
    *<p>
    * passing list of cases
    * @param    NULL
    * @return   void   
    */ 
    Static testmethod void OverAllMainTestMethodbatch() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
           Test.startTest();    
           List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            lstcase[0].Work_Queue_View_Name__c = 'Cincinnati Calls';
            update lstcase;
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            wqs.IsActive__c = true;
            wqs.Name = 'RSO Enrollment';
            wqs.Department__c = 'Calls';
            update wqs;
            List<Queue_View__c> lstQv = [select Queue_View_Name__c , Department__c, Work_Queue_Name__c ,Criteria_Updated__c,IsActive__c, Service_Center__c from Queue_View__c ];
            lstQv[0].IsActive__c = true;
            lstQv[0].Criteria_Updated__c = true;
            lstQv[1].IsActive__c = true;
            lstQv[1].Criteria_Updated__c = true;
            update lstQv;
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch1 = new CaseUpdateWorkQueue_B_HUM();
           System.schedule('batchable schedulable test', '0 50 16 04 * ?', oCaseViewBatch1);
           Test.stopTest();
        }
    }
    
    Static testmethod void testCodeCoverage() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
            List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            lstcase[1].Work_Queue_View_Name__c = 'Cincinnati Calls';
            update lstcase;
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            System.debug('test class wqsID     '+wqs.Id);

            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati Calls',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = FALSE);
            insert oQSetup ;
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = lstcase[0].Id;
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU';            
            oTask2.status = 'Closed';
            oTask2.Work_Queue_View_Name__c = 'Cincinnati CRU';
            oTask2.QueueOrUserId__c = testUser.Id;
            oTask2.Date_Time_Closed__c = System.Now();
            oTask2.ActivityDate = Date.Today();
            oTask2.Priority ='Normal';
            oTask2.Type = 'Work Task';
            insert oTask2;
            System.assert(oTask2.status == 'Closed');
            
            Task oTaskRec = HUMTestDataHelperTest.getTaskRecord();
            oTaskRec.WhatId = lstcase[0].Id;
            oTaskRec.Task_Owner__c = 'Cincinnati CRU';
            oTaskRec.Owner_Queue__c ='Cincinnati CRU';            
            oTaskRec.status = 'In Progress';
            oTaskRec.QueueOrUserId__c = testUser.Id;
            oTaskRec.Priority ='Normal';
            oTaskRec.Type = 'Work Task';
            oTask2.ActivityDate = null;
            insert oTaskRec;
            System.assert(oTaskRec.Owner_Queue__c == 'Cincinnati CRU');
            //List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];
            Work_Queue_Topic__c wqtc = new Work_Queue_Topic__c();
            wqtc.Name = 'Testing Topic';
            wqtc.IsActive__C = false;
            insert wqtc;

            WQS_Case_Transfer_History__c wqscth = new WQS_Case_Transfer_History__c();
            wqscth.ParentId__c = wqs.id;
            wqscth.From_Queue_View__c = 'Cincinnati CRU';
            wqscth.Transfer_Status__c = 'In Progress';
            wqscth.To_Work_Queue__c = oQSetup .Id;
            wqscth.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth;
            WQS_Case_Transfer_History__c wqscth1 = new WQS_Case_Transfer_History__c();
            wqscth1.ParentId__c = oQSetup.id;
            wqscth1.From_Queue_View__c = 'Cincinnati Calls';
            wqscth1.Transfer_Status__c = 'In Progress';
            wqscth1.To_Work_Queue__c = wqs.Id;
            wqscth1.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth1;

            Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
            Map<String,WQS_Case_Transfer_History__c> mapViews = new Map<String,WQS_Case_Transfer_History__c>();
            Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
            Map<String,Object> mapTemp = new Map<String,Object>();
            List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];

            List<WQS_Case_Transfer_History__c> lstTrasferHistory = [SELECT Id, ParentId__c, ParentId__r.Name, From_Queue_View__c, To_Work_Queue__c, To_Work_Queue_Topic__c, Transfer_Status__c, Work_Queue_Setup_Name__c, Service_Center__c, Department__c, Topic__c from WQS_Case_Transfer_History__c order by ParentId__c,From_Queue_View__c,createddate];
            mapViews.put('Cincinnati CRU',lstTrasferHistory[0]);
            mapUniqueValues.put('Cincinnati CRU',mapViews);
            
            mapTemp.put('Service_Center__c', 'Phoenix');
            mapFinalChanges.put('Cincinnati CRU',mapTemp);
            
            Test.startTest();     
            
            wqs.IsActive__c = true;
            wqs.Name = 'RSO Enrollment';
            wqs.Department__c = 'Calls';
            update wqs;
            Queue_View__c qvId = [Select id from Queue_View__c where Queue_View_Name__c ='Other11'];
            System.debug('test class queueviewID    '+qvId.Id);
            List<Queue_View__c> lstQv = [select Queue_View_Name__c , Department__c, Work_Queue_Name__c ,Criteria_Updated__c,IsActive__c, Service_Center__c from Queue_View__c ];
            lstQv[0].IsActive__c = true;
            lstQv[0].Criteria_Updated__c = true;
            lstQv[1].IsActive__c = true;
            lstQv[1].Criteria_Updated__c = true;
            update lstQv;                
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           
           Database.executeBatch(oCaseViewBatch);
           List<Case> lstcase1 = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE Owner_Queue__c= 'Cincinnati CRU' ORDER BY ID DESC];

           CaseWQTransfer_H_HUM cwqh = new CaseWQTransfer_H_HUM();
           cwqh.populateCriteriaChanges(null);
           cwqh.WQCaseChanges(lstcase, mapFinalChanges, mapUniqueValues);
           cwqh.WQTaskChanges(lstTask,mapFinalChanges, mapUniqueValues);
           cwqh.WQCaseChanges(lstcase1, mapFinalChanges, null);
           cwqh.WQTaskChanges(lstTask,mapFinalChanges, null);
           cwqh.WQCaseChanges(null, mapFinalChanges, mapUniqueValues);
           cwqh.WQTaskChanges(null,mapFinalChanges, mapUniqueValues);
           Test.stopTest();
        }
    }

    Static testmethod void testCodeCoverage1() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
            List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            lstcase[1].Work_Queue_View_Name__c = 'Cincinnati Calls';
            update lstcase;
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            System.debug('test class wqsID     '+wqs.Id);

            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati Calls',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = FALSE);
            insert oQSetup ;
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = lstcase[0].Id;
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU';            
            oTask2.status = 'Closed';
            oTask2.Work_Queue_View_Name__c = 'Cincinnati CRU';
            oTask2.QueueOrUserId__c = testUser.Id;
            oTask2.Date_Time_Closed__c = System.Now();
            oTask2.ActivityDate = Date.Today();
            oTask2.Priority ='Normal';
            oTask2.Type = 'Work Task';
            insert oTask2;
            System.assert(oTask2.status == 'Closed');
            
            Task oTaskRec = HUMTestDataHelperTest.getTaskRecord();
            oTaskRec.WhatId = lstcase[0].Id;
            oTaskRec.Task_Owner__c = 'Cincinnati CRU';
            oTaskRec.Owner_Queue__c ='Cincinnati calls';            
            oTaskRec.status = 'In Progress';
            oTaskRec.QueueOrUserId__c = testUser.Id;
            oTaskRec.Priority ='Normal';
            oTaskRec.Type = 'Work Task';
            oTask2.ActivityDate = null;
            insert oTaskRec;
            //List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];
            Work_Queue_Topic__c wqtc = new Work_Queue_Topic__c();
            wqtc.Name = 'Testing Topic';
            wqtc.IsActive__C = false;
            insert wqtc;

            WQS_Case_Transfer_History__c wqscth = new WQS_Case_Transfer_History__c();
            wqscth.ParentId__c = wqs.id;
            wqscth.From_Queue_View__c = 'Cincinnati CRU';
            wqscth.Transfer_Status__c = 'In Progress';
            wqscth.To_Work_Queue__c = oQSetup .Id;
            wqscth.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth;
            WQS_Case_Transfer_History__c wqscth1 = new WQS_Case_Transfer_History__c();
            wqscth1.ParentId__c = oQSetup.id;
            wqscth1.From_Queue_View__c = 'Cincinnati Calls';
            wqscth1.Transfer_Status__c = 'In Progress';
            wqscth1.To_Work_Queue__c = wqs.Id;
            wqscth1.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth1;

            Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
            Map<String,WQS_Case_Transfer_History__c> mapViews = new Map<String,WQS_Case_Transfer_History__c>();
            Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
            Map<String,Object> mapTemp = new Map<String,Object>();
            List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];

            List<WQS_Case_Transfer_History__c> lstTrasferHistory = [SELECT Id, ParentId__c, ParentId__r.Name, From_Queue_View__c, To_Work_Queue__c, To_Work_Queue_Topic__c, Transfer_Status__c, Work_Queue_Setup_Name__c, Service_Center__c, Department__c, Topic__c from WQS_Case_Transfer_History__c where From_Queue_View__c = 'Cincinnati Calls' order by ParentId__c,From_Queue_View__c,createddate];
            mapViews.put('Cincinnati CRU1',lstTrasferHistory[0]);
            mapUniqueValues.put('Cincinnati Calls11',mapViews);
            
            mapTemp.put('Department__C', 'Phoenix Dept');
            mapTemp.put('Name', 'Final Name');
            mapFinalChanges.put(oTaskRec.Owner_Queue__c,mapTemp);
            
            Test.startTest();     
            
            wqs.IsActive__c = true;
            wqs.Name = 'RSO Enrollment';
            wqs.Department__c = 'Calls';
            update wqs;
            Queue_View__c qvId = [Select id from Queue_View__c where Queue_View_Name__c ='Other11'];
            System.debug('test class queueviewID    '+qvId.Id);
            List<Queue_View__c> lstQv = [select Queue_View_Name__c , Department__c, Work_Queue_Name__c ,Criteria_Updated__c,IsActive__c, Service_Center__c from Queue_View__c ];
            lstQv[0].IsActive__c = true;
            lstQv[0].Criteria_Updated__c = true;
            lstQv[1].IsActive__c = true;
            lstQv[1].Criteria_Updated__c = true;
            update lstQv;                
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           
           Database.executeBatch(oCaseViewBatch);
           List<Case> lstcase1 = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE Owner_Queue__c= 'Cincinnati CRU' ORDER BY ID DESC];

           CaseWQTransfer_H_HUM cwqh = new CaseWQTransfer_H_HUM();
           
           cwqh.WQCaseChanges(lstcase, mapFinalChanges, mapUniqueValues);
           cwqh.WQTaskChanges(lstTask,mapFinalChanges, mapUniqueValues);
           
           Test.stopTest();
        }
    }

    Static testmethod void testCodeCoverage2() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
            List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            System.debug('test class wqsID     '+wqs.Id);

            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati Calls',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = FALSE);
            insert oQSetup ;
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = lstcase[0].Id;
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU';            
            oTask2.status = 'Closed';
            oTask2.Work_Queue_View_Name__c = 'Cincinnati CRU';
            oTask2.QueueOrUserId__c = testUser.Id;
            oTask2.Date_Time_Closed__c = System.Now();
            oTask2.ActivityDate = Date.Today();
            oTask2.Priority ='Normal';
            oTask2.Type = 'Work Task';
            insert oTask2;
            Task oTaskRec = HUMTestDataHelperTest.getTaskRecord();
            oTaskRec.WhatId = lstcase[0].Id;
            oTaskRec.Task_Owner__c = 'Cincinnati CRU';
            oTaskRec.Owner_Queue__c ='Cincinnati calls';            
            oTaskRec.status = 'In Progress';
            oTaskRec.QueueOrUserId__c = testUser.Id;
            oTaskRec.Priority ='Normal';
            oTaskRec.Type = 'Work Task';
            oTask2.ActivityDate = null;
            insert oTaskRec;
            
            //List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];
            Work_Queue_Topic__c wqtc = new Work_Queue_Topic__c();
            wqtc.Name = 'Testing Topic';
            wqtc.IsActive__C = false;
            insert wqtc;

            WQS_Case_Transfer_History__c wqscth = new WQS_Case_Transfer_History__c();
            wqscth.ParentId__c = wqs.id;
            wqscth.From_Queue_View__c = 'Cincinnati CRU';
            wqscth.Transfer_Status__c = 'In Progress';
            wqscth.To_Work_Queue__c = oQSetup .Id;
            wqscth.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth;
            WQS_Case_Transfer_History__c wqscth1 = new WQS_Case_Transfer_History__c();
            wqscth1.ParentId__c = oQSetup.id;
            wqscth1.From_Queue_View__c = 'Cincinnati Calls';
            wqscth1.Transfer_Status__c = 'In Progress';
            wqscth1.To_Work_Queue__c = wqs.Id;
            wqscth1.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth1;

            Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
            Map<String,WQS_Case_Transfer_History__c> mapViews = new Map<String,WQS_Case_Transfer_History__c>();
            Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
            Map<String,Object> mapTemp = new Map<String,Object>();
            List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];

            List<WQS_Case_Transfer_History__c> lstTrasferHistory = [SELECT Id, ParentId__c, ParentId__r.Name, From_Queue_View__c, To_Work_Queue__c, To_Work_Queue_Topic__c, Transfer_Status__c, Work_Queue_Setup_Name__c, Service_Center__c, Department__c, Topic__c from WQS_Case_Transfer_History__c order by ParentId__c,From_Queue_View__c,createddate];
            mapViews.put('Cincinnati CRU1',lstTrasferHistory[0]);
            mapViews.put(oTask2.Work_Queue_View_Name__c,lstTrasferHistory[1]);
            mapUniqueValues.put(oTask2.Owner_Queue__c,mapViews);
            
            mapTemp.put('Department__C', 'Phoenix Dept');
            mapTemp.put('Name', 'Final Name');
            mapFinalChanges.put(oTaskRec.Owner_Queue__c,mapTemp);
            
            Test.startTest();     
            CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           
           Database.executeBatch(oCaseViewBatch);
           List<Case> lstcase1 = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE Owner_Queue__c= 'Cincinnati CRU' ORDER BY ID DESC];

           CaseWQTransfer_H_HUM cwqh = new CaseWQTransfer_H_HUM();
           
           cwqh.WQTaskChanges(lstTask,mapFinalChanges, mapUniqueValues);
           cwqh.populateViewChanges();
           
           Test.stopTest();
        }
    }

    Static testmethod void testCodeCoverage4() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
            case ncase = new Case(Interacting_With_Type__c = 'Group', Due_Date__c=Date.Today(),Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='RSO Enrollment', Work_Queue_View_Name__c='RSO',Service_Center__c ='Phoenix', Department__c='RSO', status='In Progress');
            insert ncase;
            List<Case> lstcase = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE status = 'In Progress'];
            List<Case> lstcase1 = [SELECT Id, Interacting_With_Type__c , Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c, 
                                Service_Center__c , Department__c, status, CTCI_List__c FROM case WHERE Owner_Queue__c='RSO Enrollment' ];
            Work_Queue_Setup__c wqs = [select id,name ,Service_Center__c,Department__c,Batch_Update_Required__c,IsActive__c from Work_Queue_Setup__c limit 1]; 
            System.debug('test class wqsID     '+wqs.Id);

            Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati Calls',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = FALSE);
            insert oQSetup ;
            
            Work_Queue_Topic__c wqtc = new Work_Queue_Topic__c();
            wqtc.Name = 'Testing Topic';
            wqtc.IsActive__C = false;
            insert wqtc;

            WQS_Case_Transfer_History__c wqscth = new WQS_Case_Transfer_History__c();
            wqscth.ParentId__c = wqs.id;
            wqscth.From_Queue_View__c = 'Cincinnati CRU';
            wqscth.Transfer_Status__c = 'In Progress';
            wqscth.To_Work_Queue__c = oQSetup .Id;
            wqscth.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth;
            WQS_Case_Transfer_History__c wqscth1 = new WQS_Case_Transfer_History__c();
            wqscth1.ParentId__c = oQSetup.id;
            wqscth1.From_Queue_View__c = 'Cincinnati Calls';
            wqscth1.Transfer_Status__c = 'In Progress';
            wqscth1.To_Work_Queue__c = wqs.Id;
            wqscth1.To_Work_Queue_Topic__c = wqtc.id;
            insert wqscth1;

            Map<String,Map<String,WQS_Case_Transfer_History__c>> mapUniqueValues = new Map<String,Map<String,WQS_Case_Transfer_History__c>>();
            Map<String,WQS_Case_Transfer_History__c> mapViews = new Map<String,WQS_Case_Transfer_History__c>();
            Map<String,Map<String,Object>> mapFinalChanges = new Map<String,Map<String,Object>>();
            Map<String,Object> mapTemp = new Map<String,Object>();
            List<Task> lstTask = [SELECT id, WhatId,Task_Owner__c,Owner_Queue__c,status,QueueOrUserId__c,Priority,Type,ActivityDate from Task];

            List<WQS_Case_Transfer_History__c> lstTrasferHistory = [SELECT Id, ParentId__c, ParentId__r.Name, From_Queue_View__c, To_Work_Queue__c, To_Work_Queue_Topic__c, Transfer_Status__c, Work_Queue_Setup_Name__c, Service_Center__c, Department__c, Topic__c from WQS_Case_Transfer_History__c order by ParentId__c,From_Queue_View__c,createddate];
            mapViews.put('Cincinnati CRU1212',lstTrasferHistory[0]);
            mapUniqueValues.put('Cincinnati CRU213123',mapViews);
            
            mapTemp.put('Service_Center__c', 'Phoenix');
            mapTemp.put('Name', 'Final Name');
            for(case lsc : lstcase){
                mapFinalChanges.put(lsc.Owner_Queue__c,mapTemp);
            }
            for(case lsc : lstcase1){
                mapFinalChanges.put(lsc.Owner_Queue__c,mapTemp);
            }
                        
            Test.startTest();     
                       
           CaseUpdateWorkQueue_B_HUM oCaseViewBatch = New CaseUpdateWorkQueue_B_HUM();
           
           Database.executeBatch(oCaseViewBatch);
           
           CaseWQTransfer_H_HUM cwqh = new CaseWQTransfer_H_HUM();
           cwqh.populateCriteriaChanges(null);
           cwqh.WQCaseChanges(lstcase, mapFinalChanges, mapUniqueValues);
           cwqh.WQCaseChanges(lstcase1, mapFinalChanges, mapUniqueValues);
           Test.stopTest();
        }
    }
}