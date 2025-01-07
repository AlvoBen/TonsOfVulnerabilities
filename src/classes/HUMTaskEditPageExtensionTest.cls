/*****************************************************************************************
Apex class Name    : HUMTaskEditPageExtensionTest
Version            : 1.0
Function           : This is test class for HUMTaskEditPageExtension
Created Date       : 4 Aug 2014

Modification Log:
*   Developer                           Code Review             Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Manish Kumar                           17093             08/04/2014              Original Version
*   SuryaKumari Medicherla                 17930             09/15/2014              Modified for defect 153737
*   SuryaKumari Medicherla                 18081             09/10/2014              Added Recordtype coverage code
*   SuryaKumari Medicherla                 18121             09/10/2014              Code added to cover logic added to fix display users name when user clicks on edit button
*   SuryaKumari Medicherla                 18590             10/16/2014              Code added to update OwnerOrQueue Field of Task. Defect:151415
*   Mrudula Jaddu                          19256             11/17/2014              Modified as per defect 161280
*   SuryaKumari Medicherla                 19911             12/10/2014              Code modified to replace custom settings with custom object (REQ-72809)
*   Santhi Mandava                         20709             01/28/2014              Fixed test class failure issue.
*   Santhi Mandava                         21128             02/24/2015              Fixed test class failure issue related to req-81852.
*   SuryaKumari Medicherla                 21223             02/27/2015              Code modified to increase code coverage for Issue number 146 and business priority 0
*   Kritika P Popat                        21906             26/03/2015              Code Modified for REQ 82518 coverage
*   Prachi Bhasin                          33486             18/07/2016              Changes made as part of REQ- 270349
*   Shikhar Mehta                          40376             21/09/2016              Increased code coverage as the part of REQ 95982
*   vamsi kondragunta                      40981             09/23/2016              Increased code coverage as the part of REQ 95982
*   Shikhar Mehta                                            10/26/2016              REQ - 290098 MF3 RollBack
*   Sharan Shanmugam                                         11/07/2016              Modified to increase code coverage
*   Harshith Mandya                                          02/21/2017              REQ - 301510 : CA Ticket - Fixing the OWner Queue, Service Center And Department Mismatch ( Increased Code Coverage )
*   Gargey Sharma                          81181             04/01/2017              REQ - 306359 : Updates as per CTCI model changes
*   Shreya Choodamani                      90487             04/28/2017              REQ#: 311397 Updated the test class for Pharmacy USers
*   Rajesh Chandgothia                    140400             10/18/2017              REQ - 332357 increase Test Coverage
*   Prudhvi Pamarthi                      154304             01/19/2018              REQ - 328929 Fix for HUMExceptionHelper: DML
*   Ranjeeth Nagishetty                   268909             38/08/2018              Updated as per REQ 361926 
*   Santhi Mandava                                           03/12/2019              Removed "DpaaSWebServiceMockImpl" usage.
*   Pooja Kumbhar											 04/29/2020				 Checkmarx test method without assert error fix.
*   Samantha Tennyson                                        02/08/2021              User Story 1875448: PR00091574 - MF 1 - Quality - Ability to capture additional information related to Notification Task (RF)
*   Pooja Kumbhar											 05/12/2021				 Code Coverage fix
*   Samantha Tennyson   						             06/01/2021			     User Story 2325471: T1PRJ0001827 - MF 1 - AHT - Ability to prefill queue details on Task Edit page (CRM)
*   Samantha Tennyson       						 	     06/17/2021              User Story 2369230: T1PRJ0001827 - MF 1 - Quality - Ability to capture additional information related to Notification Task (CRM)
*   Samantha Tennyson       						 		 06/23/2021           	 User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
* Prasuna Pattabhi                           07/31/2023                 Test Class error fix - build fix
********************************************************************************************************************************/
@isTest
private class HUMTaskEditPageExtensionTest
{
   
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        Group oGrp = new Group(Type='Queue', Name='Cincinnati_CRU1', DeveloperName = 'Cincinnati_CRU1');
        insert oGrp;
        system.assert(oGrp.Type =='Queue');
        GroupMember gm= new GroupMember(); 
        gm.GroupId=oGrp.id;
        gm.UserOrGroupId = oUser.id;
        insert gm;
            
        User oCUser = [Select Id, Name, email from User where Id=:gm.UserOrGroupId LIMIT 1];
        system.assertEquals(gm.GroupId,oGrp.id);

        QueuesObject oQueueObject = new QueuesObject (QueueID = oGrp.id, SobjectType ='Work_Queue_Setup__c');
        insert oQueueObject ;  
        System.assert(oQueueObject.SobjectType == 'Work_Queue_Setup__c');
        
        System.runAs (oUser) 
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.BillingState == 'New York');
            
            Policy__c policyRec = New Policy__c();
            policyRec.Group_Name__c = oAccount.Id;
            policyRec.Product__c = null;
            policyRec.Policy_Group_Number__c='11111';
            policyRec.Major_Lob__c = 'MEF';
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==oAccount.Id,true);
            
            Policy_Member__c policyMemRec = New Policy_Member__c();
            policyMemRec.Name = '12345';
            policyMemRec.Policy__c = policyRec.Id;
            policyMemRec.Member__c = oAccount.Id; 
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true); 
            
            Member_ID__c memberId = new Member_ID__c();
            memberId.Name = '123456789';
            memberId.Member_Dependent_Code__c = '00';
            memberId.policy__c = policyRec.Id;
            memberId.Policy_Member__c = policyMemRec.Id;
            memberId.Member_Card_External_ID__c = '005436667';
            memberId.Type__c = HUMConstants.HUMMedicare;
            insert memberId;
            
            List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
            lstHUMConstants.add(new HUMConstants__c(Name='HUMConstantOne', StringValue__c='1', IntegerValue__c = 1));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMLISTSIZEZERO', StringValue__c='',IntegerValue__c = 0));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMWorkTask',StringValue__c='Work Task'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMNotificationTask',StringValue__c='Notification Task'));
            insert lstHUMConstants;
            System.assert(lstHUMConstants.size() > 0 );
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Guidance_Alert_Limit__c = 2;
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            Work_Queue_Setup__c oWqs = new Work_Queue_Setup__c(Name=oGrp.Name);
            oWqs.Public_Group_Name__c ='PG Cincinnati CRU1';
            oWqs.Service_Center__c ='Cincinnati';
            oWqs.Department__c ='CRU1';
            oWQS.CokY__c =true;
            oWqs.TenantID__c ='00';
            oWqs.Classification_Type__c = oClassType.Id;  
            oWqs.IsActive__c = TRUE;            
            oWqs.OwnerId = oGrp.Id; 
            insert oWqs;
            system.assert(oWqs.TenantID__c == '00');
            
            Work_Queue_Setup__c oWqs2 = new Work_Queue_Setup__c(Name='Provider Payment Integrity');
            oWqs2.Public_Group_Name__c ='PG Cincinnati CRU1';
            oWqs2.Service_Center__c ='Humana Pharmacy';
            oWqs2.Department__c ='CRU1';
            oWQS2.CokY__c =true;
            oWqs2.TenantID__c ='00';
            oWqs2.Classification_Type__c = oClassType.Id;  
            oWqs2.IsActive__c = TRUE;             
            insert oWqs2;
            system.assert(oWqs2.TenantID__c == '00');
            
            HUMTaskRecordTypes__c oRecTypes =  new HUMTaskRecordTypes__c(Name='RecordType');
            oRecTypes.ServiceCRMRecTypeName__c = 'Service CRM';
            oRecTypes.CSIRecordTypeName__c = 'Service CRM';
            insert oRecTypes;
            system.assert(oRecTypes.CSIRecordTypeName__c == 'Service CRM');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);           
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            insert lstIntent;
            system.assertEquals(lstIntent[0].name == 'Filling',true);
            
            CTCI_Junction__c oCTCIRec = new CTCI_Junction__c();
            oCTCIRec.Classification__c = lstClassification[0].id;
            oCTCIRec.Intent__c = lstIntent[0].id;
            oCTCIRec.Classification_Type__c = oClassType.id;
            oCTCIRec.IsActive__c = true;
            insert oCTCIRec;
            system.assertEquals(oCTCIRec.IsActive__c == true, true);
            
            Case oCase = new Case(AccountId=oAccount.Id, CTCI_List__c = oCTCIRec.Id, CoKY_Case__c = True,Interacting_With_Type__c = 'Group',
            Interacting_With__c=oAccount.Id, Policy_Member__c = policyMemRec.id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU1', Work_Queue_View_Name__c='Cincinati CRU1',
            Service_Center__c ='Cincinnati',Department__c='CRU1', status='In Progress',Tenant_Id__c = '00',Interacting_About_Type__c = 'Group');
            insert oCase;
            system.assert(oCase.AccountId == oAccount.Id);
            
           
            Group oGrp1 = [Select id,Name , DeveloperName from Group where type = 'Queue' AND DeveloperName = :System.label.HUMQMSMarketHOQueue];
            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c(Name= oGrp1.Name);
            oWQS1.Service_Center__c ='Market';
            oWQS1.Department__c ='Home Office Quality Operations Compliance';
            oWQS1.General__c =false;
            oWQS1.HO_Medical__c =true;
            oWQS1.TenantID__c ='00';       
            insert oWQS1;
            system.assertEquals(oWQS1.TenantID__c ,'00');

            Case oCaseRecord = new Case(AccountId=oAccount.Id, CTCI_List__c = oCTCIRec.Id, CoKY_Case__c = True,
                                   Interacting_With_Type__c = 'Group', Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),OwnerId = oUser.Id,
                                   Case_Owner__c=oUser.Name, Owner_Queue__c='TestAccount1', 
                                   Service_Center__c ='Market', Department__c='Home Office Quality Operations Compliance', status='In Progress',Tenant_Id__c = '00');
                                    oCaseRecord.G_A_Rights_Given__c = '';
                                    oCaseRecord.Complaint__c = '';
                                    oCaseRecord.Interacting_About_Type__c = 'Group';

            insert oCaseRecord;  
            system.assert(oCaseRecord.CoKY_Case__c == true);

            Case_Action__c oCaseAction = HUMTestDataHelperTest.getCaseAction('QAA');
            insert oCaseAction;
            system.assertEquals(oCaseAction.Active__c,true);   

            Case_Action_Association__c oCaseAssociation = HUMTestDataHelperTest.getCaseActionAssociation();
            oCaseAssociation.Case__c = oCaseRecord.Id;
            oCaseAssociation.Case_Action__c = oCaseAction.Id;
            insert oCaseAssociation;
            system.assertEquals(oCaseAssociation.Case__c,oCaseRecord.id);  

            Case_Action_Response__c oCaseResp = new Case_Action_Response__c();
            oCaseResp.Action__c = oCaseAssociation.Id;
            oCaseResp.Process_Question__c = system.label.HUMQMSDateIncidence;
            oCaseResp.Process_Question_Response__c=String.valueOf(system.today().month()+'/'+system.today().day()+'/'+system.today().year());
            insert oCaseResp;
            system.assertEquals(oCaseResp.Action__c ,oCaseAssociation.Id); 

            Work_Queue_Topic__c owrkqtopic = new Work_Queue_Topic__c();
            owrkqtopic.Name = 'Work Queue Topic';
            insert owrkqtopic;
            system.assertEquals(owrkqtopic.Name,'Work Queue Topic');


            Work_Queue_Junction__c owrkqjunction = new Work_Queue_Junction__c();
            owrkqjunction.Name = 'Work Queue Junction';
            owrkqjunction.Work_Queue_Setup_Name__c = oWQS.id;
            owrkqjunction.Work_Queue_Topic_Name__c = owrkqtopic.id;
            insert owrkqjunction;
      
            
            oCase.Tenant_Id__c = '00';
            update oCase;
            system.assert(oCase.Tenant_Id__c == '00');
            
            Task oTask1 = new Task();
            system.assertNotEquals(oTask1,null,'Value not matched');
            
            User oUser1 = HUMTestDataHelperTest.createTestUser('Use51','Customer Care Specialist');
            insert oUser1;
            System.assert(oUser1.LanguageLocaleKey == 'en_US');
            
            Task oTask = HUMTestDataHelperTest.getTaskRecord();
            oTask.WhatId = oCase.Id;
            oTask.Task_Owner__c = 'User1';
            oTask.Owner_Queue__c ='Cincinnati CRU1';            
            oTask.status = 'In Progress';
            oTask.Service_Center__c = 'Cincinnati';
            oTask.Department__c = 'CRU1';
            oTask.Callback_Requested__c = 'Yes';
            oTask.Time_Zone__c = 'EST - Eastern';
            oTask.Description = 'Test Comments1';
            oTask.QueueOrUserId__c = oUser.Id;
            oTask.Call_Back_Start__c = datetime.now().time();
            oTask.Call_Back_End__c= datetime.now().time();
            oTask.Type ='Work Task';
            insert oTask;
            System.assert(oTask.status == 'In Progress');
            
            Task oTask2 = HUMTestDataHelperTest.getTaskRecord();
            oTask2.WhatId = oCase.Id;
            oTask2.Task_Owner__c = 'User1';
            oTask2.Owner_Queue__c ='Cincinnati CRU1';            
            oTask2.status = 'Closed';
            oTask2.Service_Center__c = 'Cincinnati';
            oTask2.Department__c = 'CRU1';
            oTask2.Callback_Requested__c = 'No';
            oTask2.Time_Zone__c = 'EST - Eastern';
            oTask2.Call_Back_Start__c = datetime.now().time();
            oTask2.Call_Back_End__c= datetime.now().time();
            oTask2.QueueOrUserId__c = oUser.Id;
             oTask2.Type ='Work Task';
            insert oTask2;
            System.assert(oTask2.status == 'Closed');
            
            Task oTaskRec = HUMTestDataHelperTest.getTaskRecord();
            oTaskRec.WhatId = oCase.Id;
            oTaskRec.Task_Owner__c = 'Cincinnati CRU1';
            oTaskRec.Owner_Queue__c ='Cincinnati CRU1';
	    oTaskRec.Service_Center__c = 'Cincinnati';
            oTaskRec.Department__c = 'CRU1';
            oTaskRec.Callback_Requested__c = 'Yes';
            oTaskRec.Time_Zone__c = 'EST - Eastern';
	    oTaskRec.Description = 'Test Comments';            
            oTaskRec.status = 'In Progress';
            oTaskRec.QueueOrUserId__c = oUser1.Id;
            oTaskRec.Call_Back_Start__c = datetime.now().time();
            oTaskRec.Call_Back_End__c= datetime.now().time();
            oTaskRec.Type ='Work Task';
            insert oTaskRec;
            System.assert(oTaskRec.Owner_Queue__c == 'Cincinnati CRU1');
            
            Task oTaskRecEXT = HUMTestDataHelperTest.getTaskRecord();
            oTaskRecEXT.WhatId = oCase.Id;
            oTaskRecEXT.Task_Owner__c = 'Cincinnati CRU1';
            oTaskRecEXT.Owner_Queue__c ='Cincinnati CRU1';  
            oTaskRecEXT.Service_Center__c = 'Cincinnati';
            oTaskRecEXT.Department__c = 'CRU1';
            oTaskRecEXT.Callback_Requested__c = 'Yes';
            oTaskRecEXT.Time_Zone__c = 'EST - Eastern';
            oTaskRecEXT.Call_Back_Start__c = null;
            oTaskRecEXT.Call_Back_End__c= null;
            oTaskRecEXT.status = 'In Progress';
            oTaskRecEXT.QueueOrUserId__c = oUser1.Id;
            oTaskRecEXT.Type ='Extension Request';
            oTaskRecEXT.Call_Back_Start__c = datetime.now().time();
            oTaskRecEXT.Call_Back_End__c= datetime.now().time();
            insert oTaskRecEXT;
            System.assert(oTaskRecEXT.Owner_Queue__c == 'Cincinnati CRU1');
            
      	    Task oTasknt = HUMTestDataHelperTest.getTaskRecord();
            oTasknt.WhatId = oCase.Id;
            oTasknt.Task_Owner__c = 'User1';
            oTasknt.Owner_Queue__c ='Cincinnati CRU1';            
            oTasknt.status = 'In Progress';
            oTasknt.Service_Center__c = 'Cincinnati';
            oTasknt.Department__c = 'CRU1';
            oTasknt.Callback_Requested__c = 'Yes';
            oTasknt.Time_Zone__c = 'EST - Eastern';
            oTasknt.Call_Back_Start__c = null;
            oTasknt.Call_Back_End__c= null;
            oTasknt.Description = 'Test Comments1';
            oTasknt.QueueOrUserId__c = oUser.Id;
            oTasknt.Type ='Notification Task';
            oTasknt.Call_Back_Start__c = datetime.now().time();
            oTasknt.Call_Back_End__c= datetime.now().time();
            insert oTasknt;
            System.assert(oTasknt.status == 'In Progress');
            
            Task oTasknt1 = HUMTestDataHelperTest.getTaskRecord();
            oTasknt1.WhatId = oCase.Id;
            oTasknt1.Task_Owner__c = 'User1';
            oTasknt1.Owner_Queue__c ='Cincinnati CRU1';            
            oTasknt1.status = 'In Progress';
            oTasknt1.Service_Center__c = 'Cincinnati';
            oTasknt1.Department__c = 'CRU1';
            oTasknt1.Callback_Requested__c = 'No';
            oTasknt1.Time_Zone__c = 'EST - Eastern';
            oTasknt1.Call_Back_Start__c =datetime.now().time();
            oTasknt1.Call_Back_End__c= datetime.now().time();
            oTasknt1.Description = 'Test Comments1';
            oTasknt1.QueueOrUserId__c = oUser.Id;
            oTasknt1.Type ='Notification Task';
            insert oTasknt1;
            System.assert(oTasknt1.status == 'In Progress');
           
             HUM_Task_Heirarchy__c HUMHeirarchy1 = new HUM_Task_Heirarchy__c(Hum_CaseID__c=oCase.Id,HUM_Dependent_TaskID__c =oTask2.Id, HUM_Parent_TaskID__c=oTask.Id);
             insert HUMHeirarchy1;

            Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(Name ='CRMS_300_Humana_Pharmacy_Supervisor');
            insert objPharmacySup;
            system.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');
            
        }
    }     
  
    /**
    * testMethodForPositive
    *<p>
    * This method is used for test positive cases of HUMTaskEditPageExtension Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodForPositive()
    {       
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        System.runAs (oUserR) 
        {
            Account acc = [ select  id from account where Name = 'New Case Account' limit 1];
            
            Policy__c policyRec = [Select id from Policy__c where Policy_Group_Number__c='11111' limit 1];
            Policy_Member__c policyMemRec = [select id,Name from Policy_Member__c where Name = '12345' limit 1];
            Member_ID__c memberId = [select id,Name from Member_ID__c where Name = '123456789' limit 1];
            
            case oCaseR = [select AccountId,CTCI_List__r.Intent__c ,CTCI_List__r.Classification__c,General_Case__c,Interacting_With_Type__c,Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c,Service_Center__c,Department__c, status, Policy_Member__c from case where AccountId =: acc.id limit 1];
            Task oTaskR1 = [select id,type,Description,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where status = 'In Progress' and type ='Work Task' limit 1 ];
            Task oTaskR2 = [select id,type,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c  from task where status = 'closed' limit 1];
            Task oTaskR3 = [select id,type,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c  from task where Task_Owner__c = 'Cincinnati CRU1'limit 1 ];
            Task oTaskext = [select id,type from task where Task_Owner__c = 'Cincinnati CRU1' and type ='Extension Request'limit 1 ];
            
            Pharmacy_Supervisor_HUM__c phSup = [select id, Name from Pharmacy_Supervisor_HUM__c   where Name = 'CRMS_300_Humana_Pharmacy_Supervisor' Limit 1];
            Work_Queue_Setup__c oWqs = [ select id,Service_Center__c,Department__c from Work_Queue_Setup__c where Name = 'Cincinnati_CRU1' limit 1];
            
            Test.startTest();
            String sNextPage;
            
            Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);  
             
            Apexpages.currentPage().getParameters().put('taskType',oTaskext.type);
            ApexPages.currentPage().getParameters().put(HUMConstants.WHAT_ID, oCaseR.id); 
            ApexPages.currentPage().getParameters().put(System.label.HUMRecordType, sRecordTypeIdCSI15Char);            
            
            Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueue_Setup = new Map<String,Work_Queue_Setup__c>();
            mapServCenDeptWorkQueue_Setup.put(oWqs.Service_Center__c +'#' + oWqs.Department__c,oWqs);
                       
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTaskR1);
            HUMTaskEditPageExtension oTaskEPE = new HUMTaskEditPageExtension(oStdController);

            oTaskEPE.sWhatId= oCaseR.Id;
            oTaskEPE.sServiceValue = oTaskR1.Service_Center__c;
            oTaskEPE.sDepartValue = oTaskR1.Department__c;
            oTaskEPE.mapServCenDeptWorkQueueSetup = mapServCenDeptWorkQueue_Setup;
            oTaskEPE.bDisableStatus = true;
            oTaskEPE.sStartCallTime = '9:00 AM';
            oTaskEPE.sEndCallTime = '11:00 AM';
            oTaskEPE.sStartCallTime = null;
            oTaskEPE.sEndCallTime = null;
            oTaskEPE.setSelectedQueue(); 
            oTaskEPE.oTask = oTaskR1;
            oTaskEPE.bIsError = true;
            oTaskEPE.sComments = oTaskR1.Description;
            
            ApexPages.StandardController oStdController1 = new ApexPages.StandardController(oTaskR3);
            HUMTaskEditPageExtension oTaskEPE1 = new HUMTaskEditPageExtension(oStdController1);
            
            oTaskEPE1.sWhatId= oCaseR.Id;
            oTaskEPE1.sServiceValue = oTaskR3.Service_Center__c;
            oTaskEPE1.sDepartValue = oTaskR3.Department__c;
            oTaskEPE1.mapServCenDeptWorkQueueSetup = mapServCenDeptWorkQueue_Setup;
            oTaskEPE1.bDisableStatus = true;
            oTaskEPE1.sStartCallTime = '9:00 AM';
            oTaskEPE1.sEndCallTime = '11:00 AM';
            oTaskEPE1.setSelectedQueue(); 
            oTaskEPE1.oTask = oTaskR1;
            oTaskEPE1.sExtRequest = 'Extension Request';
            oTaskEPE1.bIsError = true;
            oTaskEPE1.sComments = oTaskR1.Description;
            
            if(phSup != null){
                oTaskEPE.bIsPharmacyUser = true;
            }
            
            Set<String> servCenterSet = new Set<String>();
            servCenterSet.add(oTaskR3.Service_Center__c);
            oTaskEPE1.setServiceCenter = servCenterSet;
            
            ApexPages.StandardController oStdController2 = new ApexPages.StandardController(oTaskR2);
            HUMTaskEditPageExtension oTaskEPE2 = new HUMTaskEditPageExtension(oStdController2);
            oTaskEPE2.sWhatId= oCaseR.Id;
            oTaskEPE2.sServiceValue = oTaskR2.Service_Center__c;
            oTaskEPE2.sDepartValue  = oTaskR2.Department__c;
            oTaskEPE2.mapServCenDeptWorkQueueSetup = mapServCenDeptWorkQueue_Setup;
            oTaskEPE2.bDisableStatus = true;
            oTaskEPE2.sStartCallTime = '9:00 AM';
            oTaskEPE2.sEndCallTime = '11:00 AM';
            oTaskEPE2.setSelectedQueue(); 
            oTaskEPE2.oTask = oTaskR2;
            oTaskEPE2.sTaskTypeParam = oTaskR1.Type;
            oTaskEPE2.sParentId = oTaskR1.id;
            oTaskEPE2.bVisible = true;
            oTaskEPE2.sComments = oTaskR1.Description;

            List<String> lstTimeInputString = new List<String>();
            lstTimeInputString .add(oTaskEPE2.sStartCallTime);
            oTaskEPE2.lstTimeInput = lstTimeInputString;
            
            Set<String> servCenterSet2 = new Set<String>();
            servCenterSet2.add(oTaskR2.Service_Center__c);
            oTaskEPE2.setServiceCenter = servCenterSet2;
            
            oUserR.Current_Queue__c = '';
            update oUserR;
            system.assertEquals(oUserR.Current_Queue__c,'',' Work Queue Should be null');
            oTaskEPE.reDirectPage();
            
            oUserR.Current_Queue__c = 'Cincinnati CRU1';
            update oUserR;
            system.assertEquals(oUserR.Current_Queue__c,'Cincinnati CRU1','Value not matched');
            
            oTaskEPE.doRender();
            system.assertNotEquals(oTaskEPE.bShowPanel,False,'Value not matched');
            
            oTaskEPE2.disableFields();
            system.assertNotEquals(oTaskEPE2.bDisablefield ,False,'Value not matched');
            
            oTaskEPE.getLstServiceCenter();
            system.assertNotEquals(oTaskEPE.getLstServiceCenter() ,NULL ,'Value not matched');
            
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.getLstDepartment();
            system.assertNotEquals(oTaskEPE.getLstDepartment() ,NULL ,'Value not matched');
            
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.getLstTopics();
            system.assertNotEquals(oTaskEPE.getLstTopics() ,NULL ,'Value not matched');
            
     
            oTaskEPE1.doRender();
            oTaskEPE1.getLstServiceCenter();
            oTaskEPE1.sServiceValue = 'Cincinnati';
            oTaskEPE1.getLstDepartment();
            oTaskEPE1.sDepartValue = 'CRU1';
            oTaskEPE1.getLstTopics();
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE1.populateGroupName();
            oTaskEPE1.saveTask();
          
            oTaskEPE.populateGroupName();
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskEPE.cancel();
            system.assertNotEquals(oTaskEPE.cancel(),null,'Value not matched');   
            
            oTaskR1.Task_Owner__c = null;
            oTaskEPE.sServiceValue = NULL;
            oTaskEPE.sDepartValue = NULL;
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();

            oTaskEPE.sTopicValue = 'Select a Topic';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
             oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = null;
            oTaskEPE.sTopicValue = 'Select a Topic';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = null;
            oTaskEPE.sTopicValue = 'SelectTopic';
            oTaskEPE.bShowTopic = false;
             oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskEPE.sTopicValue = '';
            oTaskEPE.assignValuesToTask();
            
            oTaskEPE.sTopicValue = '';
            
            oTaskR1.Task_Owner__c = null;
            oTaskEPE.sTopicValue = 'Select a Topic';
            oTaskEPE.bShowTopic = true;
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();    
            
            oTaskEPE.sTopicValue = 'Select a Topic';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sServiceValue = GLOBAL_CONSTANT_HUM.NONE_HUM;
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = null;
            oTaskEPE.sTopicValue = 'Select a Topic';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sDepartValue = GLOBAL_CONSTANT_HUM.NONE_HUM;
            oTaskEPE.sServiceValue = 'Cincinnati';
             oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            
            oTaskEPE.sTopicValue = NULL;
            oTaskR1.Owner_Queue__c = 'Cincinnati CRU1';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.assignValuesToTask();
            oTaskEPE.doRender();
            oTaskEPE.prefillQueue();
             
            oTaskEPE.populateDepartmentName();
            oTaskEPE.sTaskOwnerValue='Cincinnati CRU1';
            oTaskEPE.doRender();
            oTaskEPE.setTaskFieldsData();
            oTaskEPE.assignValuesToTask();
            oTaskEPE.prefillQueue();
            
            //added by ranjeeth 
            oTaskEPE.sExtRequest ='Extension Request';
            oTaskEPE.populateTaskTransferForExtTask();
           
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMTaskEditPageExtension');            
            lstLog.add(error);                        
            oTaskEPE.listErrorLogs = lstLog;            
            System.assert(oTaskEPE.listErrorLogs.size() > 0);  
        }
    }  
  
    /**
    * testMethodForNegative
    *<p>
    * This method is used for overall testing for HUMTaskEditPageExtension.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodForNegative()
    {
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        System.runAs (oUserR) 
        {
            Account acc = [ select  id from account where Name = 'New Case Account' limit 1];
            case oCaseR = [select AccountId,CTCI_List__r.Intent__c ,CTCI_List__r.Classification__c,General_Case__c,Interacting_With_Type__c,Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c,Service_Center__c,Department__c, status from case where AccountId =: acc.id limit 1];
            Task oTaskR1 = [select id,type,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where status = 'In Progress' limit 1 ];
            Task oTaskR2 = [select id,type,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c  from task where status = 'closed' limit 1 ];
            Task oTaskR3 = [select id,type,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where Task_Owner__c = 'Cincinnati CRU1' limit 1 ];
            Work_Queue_Setup__c oWqs = [ select id,Service_Center__c,Department__c from Work_Queue_Setup__c where Name = 'Cincinnati_CRU1' limit 1];
            
            Test.startTest();
            String sNextPage;
            
            Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sServiceCRMRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).ServiceCRMRecTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);  
	    
	    Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueue_Setup = new Map<String,Work_Queue_Setup__c>();
            mapServCenDeptWorkQueue_Setup.put(oWqs.Service_Center__c +'#' + oWqs.Department__c,oWqs);
           
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTaskR1);
            HUMTaskEditPageExtension oTaskEPE = new HUMTaskEditPageExtension(oStdController);
            oTaskEPE.sWhatId = oCaseR.Id;
            oTaskEPE.sServiceValue = '';
            oTaskEPE.sDepartValue = '';
            oTaskEPE.mapServCenDeptWorkQueueSetup = mapServCenDeptWorkQueue_Setup;
            oTaskEPE.bDisableStatus = true;
            oTaskEPE.sTaskTypeParam = oTaskR1.Type;
            oTaskEPE.sParentId = oTaskR1.id;
            oTaskEPE.sTopicValue = '';
            oTaskEPE.bIsPharmacyUser = true;
            oTaskEPE.sComments = oTaskR1.Description;
            oTaskEPE.saveTask();
            oTaskEPE.prefillQueue();
           
            ApexPages.currentPage().getParameters().put(HUMConstants.WHAT_ID, oCaseR.id);
            ApexPages.currentPage().getParameters().put(System.label.HUMRecordType, sRecordTypeIdCSI15Char);            
            ApexPages.StandardController oStdController2 = new ApexPages.StandardController(oTaskR2);
            HUMTaskEditPageExtension oTaskEPE2 = new HUMTaskEditPageExtension(oStdController2);
            oTaskEPE2.sWhatId= oCaseR.Id;
            oTaskEPE2.sServiceValue = oTaskR2.Service_Center__c;
            oTaskEPE2.sDepartValue  = oTaskR2.Department__c;
            oTaskEPE2.bDisableStatus = true;
            oTaskEPE2.sComments = oTaskR2.Description;
            oTaskEPE.saveTask();
             
            oUserR.Current_Queue__c = 'Cincinnati CRU1';
            update oUserR;
            system.assertEquals(oUserR.Current_Queue__c,'Cincinnati CRU1','Value not matched'); 
            oTaskEPE2.reDirectPage();
            
            oTaskEPE2.doRender();
            
            oTaskEPE2.disableFields();
           
            oTaskEPE.getLstServiceCenter();
          
            oTaskEPE.sServiceValue = '';
           
            
            oTaskEPE.doRender();
            oTaskEPE2.disableFields();
            oTaskEPE.getLstServiceCenter();
            oTaskEPE.getLstDepartment();
            oTaskEPE.getLstTopics(); 
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.sUserID = NULL;
            oTaskEPE.sUserName = 'Surya Kumari';
            oTaskEPE.sServiceValue = 'Cincinati';
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sTopicValue = 'None';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sComments='test';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = 'Cincinati CRU1';
            oTaskEPE.sUserID = Userinfo.getUserId();
            oTaskEPE.sUserName = Userinfo.getName();
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sTopicValue = 'None';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.sUserID = NULL;
            oTaskEPE.sUserName = NULL;
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sDepartValue = 'CRU1';
            oTaskEPE.sTopicValue = 'None';
            oTaskEPE.bShowTopic = false;
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.bGroupexists= true;
             oTaskEPE.saveTask();
            
             oTaskR1.Task_Owner__c = NULL;
            oTaskEPE.bGroupexists= false;
             oTaskEPE.assignValuesToTask();
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.bShowError = true;
            oTaskR1.status = 'closed';
            oTaskEPE.disableFields();
            oTaskEPE.saveTask();
            
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sUserID = 'NONE';            
            oTaskEPE.assignValuesToTask();
             oTaskEPE.sExtRequest ='Work Task';
            
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sUserID = 'NONE';           
            oTaskEPE.assignValuesToTask();
             oTaskEPE.sExtRequest ='Work Task';
            oTaskR1.Task_Owner__c = 'Cincinnati CRU1';
            oTaskEPE.sServiceValue = 'Cincinnati';
            oTaskEPE.sUserID = Userinfo.getUserId();  
            oTaskEPE.sUserName = Userinfo.getName();
            oTaskEPE.sUserName = 'Surya Kumari';        
            oTaskEPE.assignValuesToTask();
            
            oTaskEPE.sTopicValue = NULL;
            oTaskEPE.assignValuesToTask();
             oTaskEPE.sExtRequest ='Work Task';
            
            oTaskEPE.sTopicValue = '';
            oTaskEPE.sTaskOwnerValue = NULL;
            oTaskEPE.oCaseNum = new Case();
            oTaskEPE.sPublicGroupName = '';
            oTaskEPE.sDepartValue = 'Test SC';
            oTaskEPE.sServiceValue = 'Test DP';
            oTaskEPE.assignValuesToTask();
            
            oTaskEPE.sDepartValue = 'None';
            oTaskEPE.sServiceValue = 'None';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();
   
            oTaskEPE.sDepartValue = 'None';
            oTaskEPE.sServiceValue = 'Cincinati';
            oTaskEPE.sExtRequest ='Work Task';
            oTaskEPE.saveTask();

            oTaskR1.Task_Owner__c = NULL;
            update oTaskR1;
            system.assert(oTaskR1.Task_Owner__c == NULL);
            oTaskEPE.cancel();    
             Test.stopTest(); 
             
            oTaskEPE.populateDepartmentName();
            oTaskEPE.sTaskOwnerValue='Cincinnati CRU1';
            oTaskEPE.doRender();
            oTaskEPE.setTaskFieldsData();
            oTaskEPE.assignValuesToTask();
            oTaskEPE.sExtRequest ='Work Task';

        }
        
        User oUserR2 = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        oUserR2.Current_Queue__c = 'Cincinnati CRU1';
        update oUserR2;
        System.runAs (oUserR2) 
        {
            
            Task oTaskR_1 = [select id from task where status = 'In Progress' limit 1 ];
            ApexPages.StandardController oStdController_1 = new ApexPages.StandardController(oTaskR_1);
            HUMTaskEditPageExtension oTaskEPE_1 = new HUMTaskEditPageExtension(oStdController_1);
            
            Group oGrp_1 = new Group(Type='Queue', Name='CincinnatiCRU1', DeveloperName = 'CincinnatiCRU1');
            insert oGrp_1;
            system.assert(oGrp_1.Type =='Queue');
            
            oGrp_1.Name=NULL;
            
            oTaskEPE_1.sTaskOwnerValue='CincinnatiCRU1';
            oTaskEPE_1.sServiceValue = 'Cincinati';
            oTaskEPE_1.sUserName = Userinfo.getName();
            oTaskEPE_1.sUserName = 'Surya Kumari';
            oTaskEPE_1.assignValuesToTask();
            oTaskEPE_1.sExtRequest ='Work Task';
            
            List<Group> lstGrp = [Select Id, Name from Group Where Name = 'CincinnatiCRU1' LIMIT 1]; 
            
            oTaskEPE_1.sTaskOwnerValue='CincinnatiCRU1';
            oTaskEPE_1.assignValuesToTask();
            oTaskEPE_1.sExtRequest ='Work Task';
            
        }
    }
   static testMethod void testMethodForException()
    {
        User oUserR = [ select id,Current_Queue__c, LastName from user where LastName = 'test User1' limit 1 ];
        System.assertEquals(oUserR.LastName, 'test User1');
        Task oTaskR_1 = [select id,type  from task where status = 'In Progress' limit 1 ];
        System.runAs (oUserR) 
        {
            Test.startTest();
            
            ApexPages.StandardController oStdController_1 = new ApexPages.StandardController(oTaskR_1);
            HUMTaskEditPageExtension oTaskEPE_1 = new HUMTaskEditPageExtension(oStdController_1);
            oTaskEPE_1.sTopicValue = 'test';
            oTaskEPE_1.sExtRequest ='Work Task';
            oTaskEPE_1.populateQueueName();
            Test.stopTest();
        }
    }
 
    /**
    * testMethodNorificaitonTaskTimeNull
    *<p>
    * This method is used for test positive cases of HUMTaskEditPageExtension Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodNorificaitonTaskTimeNull()
    {       
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        
        PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = oUserR.id;
        psa.PermissionSetId = pCheckRF.Id;
        insert psa;
        
        System.runAs (oUserR) 
        {
            Account acc = [ select  id from account where Name = 'New Case Account' limit 1];
            Policy__c policyRec = [Select id from Policy__c where Policy_Group_Number__c='11111' limit 1];
            Policy_Member__c policyMemRec = [select id,Name from Policy_Member__c where Name = '12345' limit 1];
            Member_ID__c memberId = [select id,Name from Member_ID__c where Name = '123456789' limit 1];
            case oCaseR = [select AccountId,CTCI_List__r.Intent__c ,CTCI_List__r.Classification__c,General_Case__c,Interacting_With_Type__c,Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c,Service_Center__c,Department__c, status, Policy_Member__c from case where AccountId =: acc.id limit 1];
            Task oTaskR1 = [select id,type,Description,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where status = 'In Progress' and type ='Notification Task' limit 1 ];
            
            Pharmacy_Supervisor_HUM__c phSup = [select id, Name from Pharmacy_Supervisor_HUM__c where Name = 'CRMS_300_Humana_Pharmacy_Supervisor' Limit 1];
            Test.startTest();
            String sNextPage;
            
            Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);  
             
            Apexpages.currentPage().getParameters().put('taskType',oTaskR1.type);
            ApexPages.currentPage().getParameters().put(HUMConstants.WHAT_ID, oCaseR.id); 
            ApexPages.currentPage().getParameters().put(System.label.HUMRecordType, sRecordTypeIdCSI15Char);            
            
                       
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTaskR1);
            HUMTaskEditPageExtension oTaskEPE = new HUMTaskEditPageExtension(oStdController);

            oTaskEPE.sWhatId= oCaseR.Id;
            oTaskEPE.bDisableStatus = true;
            oTaskEPE.sStartCallTime = null;
            oTaskEPE.sEndCallTime = null;
            oTaskEPE.setSelectedQueue(); 
            oTaskEPE.oTask = oTaskR1;
            oTaskEPE.bIsError = true;
            oTaskEPE.sComments = oTaskR1.Description;
            oTaskEPE.saveTask();
            oTaskEPE.assignValuesToTask();
            
            HUMTaskEditPageExtension oTaskEPE1 = new HUMTaskEditPageExtension(oStdController);
            oTaskEPE1.sWhatId= oCaseR.Id;
            oTaskEPE1.bDisableStatus = true;
            oTaskEPE1.sStartCallTime = '9:00 AM';
            oTaskEPE1.sEndCallTime = '11:00 AM';
            oTaskEPE1.setSelectedQueue(); 
            oTaskEPE1.oTask = oTaskR1;
            oTaskEPE1.bIsError = true;
            oTaskEPE1.sComments = oTaskR1.Description;
            oTaskEPE1.saveTask();
            oTaskEPE1.assignValuesToTask();   
        }
    }  
    
     /**
    * testMethodNorificaitonTaskTimeNotNull
    *<p>
    * This method is used for test positive cases of HUMTaskEditPageExtension Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodNorificaitonTaskTimeNotNull()
    {       
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        
        PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = oUserR.id;
        psa.PermissionSetId = pCheckRF.Id;
        insert psa;
        
        System.runAs (oUserR) 
        {
            Account acc = [ select  id from account where Name = 'New Case Account' limit 1];
            Policy__c policyRec = [Select id from Policy__c where Policy_Group_Number__c='11111' limit 1];
            Policy_Member__c policyMemRec = [select id,Name from Policy_Member__c where Name = '12345' limit 1];
            Member_ID__c memberId = [select id,Name from Member_ID__c where Name = '123456789' limit 1];
            case oCaseR = [select AccountId,CTCI_List__r.Intent__c ,CTCI_List__r.Classification__c,General_Case__c,Interacting_With_Type__c,Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c,Service_Center__c,Department__c, status, Policy_Member__c from case where AccountId =: acc.id limit 1];
            Task oTaskR1 = [select id,type,Description,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where status = 'In Progress' and type ='Notification Task' and Callback_Requested__c = 'No' limit 1 ];
            
            Pharmacy_Supervisor_HUM__c phSup = [select id, Name from Pharmacy_Supervisor_HUM__c where Name = 'CRMS_300_Humana_Pharmacy_Supervisor' Limit 1];
            Test.startTest();
            String sNextPage;
            
            Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);  
             
            Apexpages.currentPage().getParameters().put('taskType',oTaskR1.type);
            ApexPages.currentPage().getParameters().put(HUMConstants.WHAT_ID, oCaseR.id); 
            ApexPages.currentPage().getParameters().put(System.label.HUMRecordType, sRecordTypeIdCSI15Char);            
            
                       
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTaskR1);
            HUMTaskEditPageExtension oTaskEPE = new HUMTaskEditPageExtension(oStdController);

            oTaskEPE.sWhatId= oCaseR.Id;
            oTaskEPE.bDisableStatus = true;
            oTaskEPE.sStartCallTime = null;
            oTaskEPE.sEndCallTime = null;
            oTaskEPE.setSelectedQueue(); 
            oTaskEPE.oTask = oTaskR1;
            oTaskEPE.bIsError = true;
            oTaskEPE.sComments = oTaskR1.Description;
            oTaskEPE.saveTask();
            oTaskEPE.assignValuesToTask();
            
            HUMTaskEditPageExtension oTaskEPE1 = new HUMTaskEditPageExtension(oStdController);
            oTaskEPE1.sWhatId= oCaseR.Id;
            oTaskEPE1.bDisableStatus = true;
            oTaskEPE1.sStartCallTime = '9:00 AM';
            oTaskEPE1.sEndCallTime = '11:00 AM';
            oTaskEPE1.setSelectedQueue(); 
            oTaskEPE1.oTask = oTaskR1;
            oTaskEPE1.bIsError = true;
            oTaskEPE1.sComments = oTaskR1.Description;
            oTaskEPE1.saveTask();
            oTaskEPE1.assignValuesToTask();   
        }
    } 
    /**
    * testMethodForNonNorificaitonTaskTimeNull
    *<p>
    * This method is used for test positive cases of HUMTaskEditPageExtension Class.
    * @param            NULL
    * @return           void
    */
    static testMethod void testMethodForNonNorificaitonTaskTimeNull()
    {       
        User oUserR = [ select id,Current_Queue__c from user where LastName = 'test User1' limit 1 ];
        
        PermissionSet pCheckRF =[SELECT ID From PermissionSet WHERE Name =:GLOBAL_CONSTANT_HUM.RAPIDFORCE_PERMISSIONSET_HUM LIMIT 1];
        
        PermissionSetAssignment psa = new PermissionSetAssignment();
        psa.AssigneeId = oUserR.id;
        psa.PermissionSetId = pCheckRF.Id;
        insert psa;
        
        System.runAs (oUserR) 
        {
            Account acc = [ select  id from account where Name = 'New Case Account' limit 1];
            Policy__c policyRec = [Select id from Policy__c where Policy_Group_Number__c='11111' limit 1];
            Policy_Member__c policyMemRec = [select id,Name from Policy_Member__c where Name = '12345' limit 1];
            Member_ID__c memberId = [select id,Name from Member_ID__c where Name = '123456789' limit 1];
            case oCaseR = [select id,AccountId,CTCI_List__r.Intent__c ,CTCI_List__r.Classification__c,General_Case__c,Interacting_With_Type__c,Interacting_With__c, Due_Date__c, Case_Owner__c, Owner_Queue__c, Work_Queue_View_Name__c,Service_Center__c,Department__c, status, Policy_Member__c from case where AccountId =: acc.id limit 1];
            Task oTaskR1 = [select id,type,Description,Callback_Requested__c,Time_Zone__c,Call_Back_End__c,Call_Back_Start__c,Service_Center__c,Department__c from task where status = 'In Progress' and type ='Work Task' and Time_Zone__c != null limit 1 ];
            
            Pharmacy_Supervisor_HUM__c phSup = [select id, Name from Pharmacy_Supervisor_HUM__c where Name = 'CRMS_300_Humana_Pharmacy_Supervisor' Limit 1];
            Test.startTest();
            String sNextPage;
            
            Map<String, HUMTaskRecordTypes__c> mapTaskRecordTypes = HUMTaskRecordTypes__c.getAll();
            DescribeSObjectResult oResult =  Schema.getGlobalDescribe().get(HUMConstants.OBJECT_TASK).getDescribe();
            MAP<String,Schema.RecordTypeInfo> mapRecordTypeInfo = oResult.getRecordTypeInfosByName();
            String sCSIRecName = mapTaskRecordTypes.get(System.label.HUMRecordType).CSIRecordTypeName__c;
            String sRecordTypeIdCSI  = mapRecordTypeInfo.get(sCSIRecName).getRecordTypeId();
            String sRecordTypeIdCSI15Char = sRecordTypeIdCSI.substring(0, 15);  
             
            Apexpages.currentPage().getParameters().put('taskType',oTaskR1.type);
            ApexPages.currentPage().getParameters().put(HUMConstants.WHAT_ID, oCaseR.id); 
            ApexPages.currentPage().getParameters().put(System.label.HUMRecordType, sRecordTypeIdCSI15Char);            
            
                       
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oTaskR1);
            HUMTaskEditPageExtension oTaskEPE = new HUMTaskEditPageExtension(oStdController);
            oTaskEPE.sWhatId= oCaseR.Id;
            oTaskEPE.bDisableStatus = true;
            oTaskEPE.sStartCallTime = null;
            oTaskEPE.sEndCallTime = null;
            oTaskEPE.setSelectedQueue(); 
            oTaskEPE.oTask = oTaskR1;
            oTaskEPE.bIsError = true;
            oTaskEPE.sComments = oTaskR1.Description;
            oTaskEPE.saveTask();
            oTaskEPE.assignValuesToTask();
            
            HUMTaskEditPageExtension oTaskEPE1 = new HUMTaskEditPageExtension(oStdController);
            oTaskEPE1.sWhatId= oCaseR.Id;
            oTaskEPE1.bDisableStatus = true;
            oTaskEPE1.sStartCallTime = '9:00 AM';
            oTaskEPE1.sEndCallTime = '11:00 AM';
            oTaskEPE1.setSelectedQueue(); 
            oTaskEPE1.oTask = oTaskR1;
            oTaskEPE1.bIsError = true;
            oTaskEPE1.sComments = oTaskR1.Description;
            oTaskEPE1.saveTask();
            oTaskEPE1.assignValuesToTask();   
        }
    }
 }