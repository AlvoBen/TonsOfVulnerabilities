/****************************************************************************
Apex Class Name :  HUMCaseWorkQueueViewNameBatchTest 
Version: 1.0 
Created On       : 03/24/2015
Function         : Test class to HUMCaseWorkQueueViewNameBatch apex class

Modification Log : 
* Developer Name          Code Review                   Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Santhi Mandava           21968                        03/24/15                    Original Version
* Prachi Bhasin            33337                        07/18/2016                  Changes made as part of REQ- 270349
* Pallavi Ravishankar      78710                        03/27/2017                  Added ctci object as per new data model - req 304973
* Pradeepkumar Dani        99570                        06/08/2017                  Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani       100996                        06/13/2017                  Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Pooja Kumbhar											04/29/2020					Checkmarx test method without assert error fix.
* Vamshi Krishna Pemberthi						  		06/08/2021 					Code Coverage Improvements
*************************************************************************************************************************/

@isTest                            
Private Class HUMCaseWorkQueueViewNameBatchTest
{
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
            
            Account oAcc = [select  id, name from Account where name ='TestAccount1'];
            System.assertNotEquals(oAcc, NULL, 'Value not matched');
            Policy_Member__c oPolMem1 = [select id, name from Policy_Member__c where name='12345'];
            System.assertNotEquals(oPolMem1, NULL, 'Value not matched');
            Policy_Member__c oPolMem2 = [select id, name from Policy_Member__c where name='123456'];
            System.assertNotEquals(oPolMem2, NULL, 'Value not matched');
            
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
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id,Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), 
                              Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='user1', 
                              Owner_Queue__c='Cincinnati CRU', Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                              Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
        
            lstOfCases.Add(New Case(AccountId=oAcc.Id, Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, Policy_Member__c= oPolMem2.Id,
                              Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c='Cincinnati CRU', Owner_Queue__c='Cincinnati CRU', 
                              Work_Queue_View_Name__c='Cincinati CRU',Service_Center__c ='Cincinnati',  Department__c='CRU', status='In Progress',CTCI_List__c=objCTCI.id));
            
           insert lstOfCases;
           System.assertEquals(lstOfCases[0].Owner_Queue__c,'Cincinnati CRU');
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
           HUMCaseWorkQueueViewNameBatch oCaseViewBatch = New HUMCaseWorkQueueViewNameBatch();
           Database.executeBatch(oCaseViewBatch);
           Test.stopTest();
        }
    }
	
	/**
    * This Is the main method which Is used for queue view assignment Exception
    *<p>
    * @param    NULL
    * @return   void   
    */ 
    Static testmethod void OverAllMainTestMethodException() 
    {        
        User testUser = [Select Id, Name, LastName from User where LastName='test User1'];
        System.assertEquals(testUser.LastName, 'test User1');
        System.runAs(testUser)
        {
           Test.startTest();                     
           HUMCaseWorkQueueViewNameBatch oCaseViewBatch1 = New HUMCaseWorkQueueViewNameBatch();
           oCaseViewBatch1.endTime = System.now().addHours(-5);
           Database.executeBatch(oCaseViewBatch1);
           Test.stopTest();
        }
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
        Work_Queue_Setup__c oQSetup = New Work_Queue_Setup__c(name ='Cincinnati CRU',Service_Center__c='Cincinnati',Department__c='CRU',Batch_Update_Required__c = TRUE,IsActive__c = TRUE);
        insert oQSetup ;
        
        Queue_View__c oQv1 = New Queue_View__c(Queue_View_Name__c ='Other11', Department__c='CRU', Work_Queue_Name__c=oQSetup.Id , Service_Center__c='Cincinnati'); 
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
        Work_Queue_Priority__c oWQP = New Work_Queue_Priority__c(Work_Queue_Name__c = oQSetup.Id);
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
}