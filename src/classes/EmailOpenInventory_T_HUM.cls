/*******************************************************************************************************************************
Apex Class Name : EmailOpenInventory_D_HUM
Version         : 1.0
Created On      : 04/08/2021
Function        : Test Class for batch EmailOpenInventory_B_HUM

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vinay Lingegowda                                                   05/14/2021                 Initial version
* Vinay Lingegowda                                                   06/18/2021                 Updated Test Class
*******************************************************************************************************************************/

@isTest
private class EmailOpenInventory_T_HUM {
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData(){

        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
            
        //Creating Batch Custom Setting 
        EmailBatchCS_HUM__c oBatchSetup = New EmailBatchCS_HUM__c(BatchQueryLimit__c= 25, Email_Date_Format__c ='MM/dd/YYYY HH:mm:ss z', Email_Display_Count__c= 25,
                                                                        isBatchON__c= true, No_Reply_Address__c= 'noreplycrmservice@humana.com', Permission_Sets__c= 'CRM_RF_LouisvilleUser_Access',
                                                                        Profiles__c= 'Customer Care Specialist,Customer Care Supervisor,Humana Pharmacy Specialist', Query_Age_Limit__c= 0,
                                                                        Query_Case_Statuses__c= 'In Progress',Query_Limit__c= 100 , Query_Task_Statuses__c= 'In Progress', isShowClickHere__c=true);
        insert oBatchSetup;
            
        //Creating Classification type Record, Classification, Intend, CTCI record
        Classification_Type__c ctRec = New Classification_Type__c();
        ctRec.name = 'calls (RSO)';
        ctRec.Is_Active__c=true;
        insert ctRec;
            
        Classification__c classificationRec = New Classification__c();
        classificationRec.Name = 'Benefits';
        classificationRec.Is_Active__c = true;
        insert classificationRec; 
        
        Intent__c intentRec = New Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        insert intentRec;
            
        CTCI_Junction__c ctciRec = New CTCI_Junction__c();
        ctciRec.Classification_Type__c = ctRec.id;
        ctciRec.Classification__c = classificationRec.Id;
        ctciRec.Intent__c = intentRec.id;
        ctciRec.IsActive__c = true;
        insert ctciRec;
        
        //Creating Customer Care Specialist User
        User oUser1 = createTestUser('User1','Customer Care Supervisor');
        oUser1.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oUser1;

        User oUser2 = createTestUser('User2','Customer Care Specialist');
        oUser2.Current_Queue__c = 'Louisville RSO Calls Support';
        insert oUser2;

        System.runAs(new User(Id = Userinfo.getUserId())) {
            PermissionSet pRSOHP1 = [SELECT ID From PermissionSet WHERE Name = 'CRM_RF_LouisvilleUser_Access' LIMIT 1];
            list<PermissionSetAssignment> psa = new list<PermissionSetAssignment>();
            psa.add(new PermissionSetAssignment(AssigneeId = oUser1.id, PermissionSetId = pRSOHP1.id));
            psa.add(new PermissionSetAssignment(AssigneeId = oUser2.id, PermissionSetId = pRSOHP1.id));
            insert psa;
        }

  
        system.runAs(oUser1){
            //Creating Account Record
            Account accRec = new Account(Group_Number__c='11111', BillingState='New York', OwnerId = oUser1.id, FirstName = 'Test', LastName = 'Account', Enterprise_Id__c = '123456', recordtypeId = AccountRecordType.id);
            insert accRec;
            system.assertEquals(accRec.recordtypeId==AccountRecordType.id,true);

            //Creating Policy Record
            Policy__c policyRec = new Policy__c(Group_Name__c = accRec.Id, Product__c = null, Policy_Group_Number__c= '11111', Major_Lob__c= 'MED');
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accRec.Id,true);
            
            //Creating Policy Member Record
            Policy_Member__c policyMemRec = new Policy_Member__c(Name = '12345', Policy__c = policyRec.Id, Member__c = accRec.Id, Relationship__c = 'Self', Effective_Date__c=Date.today().addDays(-1).format(), End_Date__c=Date.today().addDays(1).format());
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
           //Creating Case record.
            Case oCase1 = new Case(AccountId=accRec.Id, Intent_Id__c= intentRec.id, Classification_Id__c= classificationRec.id, Policy_Member__c= policyMemRec.Id ,
                            Interacting_With_Type__c= 'Member', Interacting_With__c= accRec.Id, Due_Date__c= Date.Today(), Follow_up_Due_Date__c= Date.Today()+5, Response_Status__c= 'testing',
                            Case_Owner__c= oUser1.name, Owner_Queue__c= 'Louisville RSO Calls Support', Work_Queue_View_Name__c= 'Other',
                            Service_Center__c= 'Cincinnati', Department__c= 'CRU', status= 'In Progress', CTCI_List__c= ctciRec.id, OwnerId= oUser1.id );
            insert oCase1;
            
            Case oCase11 = new Case(AccountId=accRec.Id, Intent_Id__c= intentRec.id, Classification_Id__c= classificationRec.id, Policy_Member__c= policyMemRec.Id ,
                            Interacting_With_Type__c= 'Member', Interacting_With__c= accRec.Id, Due_Date__c= Date.Today(), Follow_up_Due_Date__c= Date.Today()+5, Response_Status__c= 'testing',
                            Case_Owner__c= oUser1.name, Owner_Queue__c= 'Louisville RSO Calls Support', Work_Queue_View_Name__c= 'Other',
                            Service_Center__c= 'Cincinnati', Department__c= 'CRU', status= 'In Progress', CTCI_List__c= ctciRec.id, OwnerId= oUser1.id );
            insert oCase11;

            //Creating Task record.
            Task oTask1 = new Task(WhatId = oCase1.id, type='Work Task',  Task_Owner__c=oUser1.name, Owner_Queue__c='Louisville RSO Calls Support', 
            Work_Queue_View_Name__c='Other', Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress' , Topic__c = 'SIU',QueueOrUserId__c = oUser1.id);
            insert oTask1;
        }
        
        system.runAs(oUser2){
            //Creating Account Record
            Account accRec = new Account(Group_Number__c='11111', BillingState='New York', OwnerId = oUser2.id, FirstName = 'Test', LastName = 'Account', Enterprise_Id__c = '123456', recordtypeId = AccountRecordType.id);
            insert accRec;
            system.assertEquals(accRec.recordtypeId==AccountRecordType.id,true);

            //Creating Policy Record
            Policy__c policyRec = new Policy__c(Group_Name__c = accRec.Id, Product__c = null, Policy_Group_Number__c= '11111', Major_Lob__c= 'MED');
            insert policyRec;
            system.assertEquals(policyRec.Group_Name__c==accRec.Id,true);
            
            //Creating Policy Member Record
            Policy_Member__c policyMemRec = new Policy_Member__c(Name = '12345', Policy__c = policyRec.Id, Member__c = accRec.Id, Relationship__c = 'Self', Effective_Date__c=Date.today().addDays(-1).format(), End_Date__c=Date.today().addDays(1).format());
            insert policyMemRec;
            system.assertEquals(policyMemRec.Policy__c==policyRec.Id,true);
           //Creating Case record.
            Case oCase2 = new Case(AccountId=accRec.Id, Intent_Id__c= intentRec.id, Classification_Id__c= classificationRec.id, Policy_Member__c= policyMemRec.Id ,
                            Interacting_With_Type__c= 'Member', Interacting_With__c= accRec.Id, Due_Date__c= Date.Today(), Follow_up_Due_Date__c= Date.Today()+5, Response_Status__c= 'testing',
                            Case_Owner__c= oUser2.name, Owner_Queue__c= 'Louisville RSO Calls Support', Work_Queue_View_Name__c= 'Other',
                            Service_Center__c= 'Cincinnati', Department__c= 'CRU', status= 'In Progress', CTCI_List__c= ctciRec.id, OwnerId= oUser2.id);
            insert oCase2;

            //Creating Task record.
            Task oTask2 = new Task(WhatId = oCase2.id, type='Work Task',  Task_Owner__c=oUser2.name, Owner_Queue__c='Louisville RSO Calls Support', 
            Work_Queue_View_Name__c='Other', Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress' , Topic__c = 'SIU',QueueOrUserId__c = oUser2.id);
            insert oTask2;
            
            Task oTask3 = new Task(WhatId = oCase2.id, type='Work Task',  Task_Owner__c=oUser2.name, Owner_Queue__c='Louisville RSO Calls Support', 
            Work_Queue_View_Name__c='Other', Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress' , Topic__c = 'SIU',QueueOrUserId__c = oUser2.id);
            insert oTask3;
        }
    }

    /**
    * Returns void
    * <p>
    * Execute the batch with default constructor 
    * @return  Void
    */
    static testmethod void sendEmail(){ 
        User testUser = [Select Id, Name, LastName, Username from User where Id=:UserInfo.getUserId()];
        System.runAs(testUser){
            Test.startTest();                     
            EmailOpenInventory_B_HUM oCaseBatch = New EmailOpenInventory_B_HUM();
            Database.executeBatch(oCaseBatch);
            Test.stopTest();
        }
    }

    /**
    * Returns void
    * <p>
    * Execute the batch with passing User Id to the constructor 
    * @return  Void
    */

    static testmethod void sendEmailWithSetId(){       
        User testUser = [Select Id, Name, LastName, Username from User where Id=:UserInfo.getUserId()];
        
        System.runAs(testUser){
            set<Id> usrid= new set<Id>();
            List<User> usr = [SELECT Id FROM User WHERE Email='User1@humana.com' OR Email='User2@humana.com' limit 5]; 
            for(user u: usr){
                usrid.add(u.id);
            }
            Test.startTest();                     
            EmailOpenInventory_B_HUM oCaseBatch1 = new EmailOpenInventory_B_HUM(usrid);
            Database.executeBatch(oCaseBatch1);
            Test.stopTest();
        }
    } 
    
    /**
    *  CRON expression: Everyday at 9 PM runs the job. Because this is a test, job executes immediately after Test.startTest() this class is for Positive testing.
    *  @name scheduleEmailBatchPositive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */ 
    static testMethod void scheduleEmailBatchPositive() {
            Test.startTest();
            string CRON_EXP = '0 0 21 ? * * *';
            EmailOpenInventory_SC_HUM emailSch = new EmailOpenInventory_SC_HUM ();
            String jobId = System.schedule('ScheduleApexClassTest',  CRON_EXP, emailSch );

            CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, NextFireTime FROM CronTrigger WHERE id = :jobId];
            System.assertEquals(CRON_EXP, ct.CronExpression);
            System.assertEquals(0, ct.TimesTriggered);

        Test.stopTest();
    }
    
   /**
    *  CRON expression: Everyday at 9 PM runs the job. Because this is a test, job executes immediately after Test.startTest() this class is for Negetive testing.
    *  @name scheduleEmailBatchNegetive
    *  @param NA
    *  @return NA
    *  @throws exception NA
    */  
    
   static testMethod void scheduleEmailBatchNegetive() {
       //Creating Batch Custom Setting 
       EmailBatchCS_HUM__c csEmailBatch = EmailBatchCS_HUM__c.getOrgDefaults();
       delete csEmailBatch;
       Test.startTest();
            string CRON_EXP = '0 0 21 ? * * *';
            EmailOpenInventory_SC_HUM emailSch = new EmailOpenInventory_SC_HUM ();
            String jobId = System.schedule('ScheduleApexClassTest',  CRON_EXP, emailSch );

            CronTrigger ct = [SELECT Id, CronExpression, TimesTriggered, NextFireTime FROM CronTrigger WHERE id = :jobId];
            System.assertEquals(CRON_EXP, ct.CronExpression);
            System.assertEquals(0, ct.TimesTriggered);
        Test.stopTest();
    }
    
    /*
    * Method Name   :    createTestUser
    * Description   :    create User Object record
    * Return Type   :   User
    * Parameters    :   String, String
    */
    Public Static User createTestUser(String name, String profileName ){
        Id profileId;
        Id cdoUserId;
        Try{
            profileId = [SELECT Id FROM Profile WHERE  name =: profileName LIMIT 1 ].Id;
        }catch (Exception ex) {
            System.assert( false, profileName + ' profile does not exist in the system');
        }
        User testUser = New User();
        testUser.LastName = 'test ' + name;
        testUser.Alias = (name.length() > 8 ) ?  name.substring(0 , 8) : name;
        testUser.Email = name+'@humana.com';
        testUser.Username = name+'@humana.com';
        testUser.ProfileId = profileId;
        testUser.CommunityNickname = 'a'+name;
        testUser.TimeZoneSidKey = 'America/New_York';
        testUser.LocaleSidKey = 'en_US';
        testUser.EmailEncodingKey = 'ISO-8859-1';
        testUser.LanguageLocaleKey = 'en_US';
        testUser.IsActive = true;
        Return testUser;
    }
}