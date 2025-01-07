/****************************************************************************************
Apex Class Name :  LinkedCases_T_HUM
Version: 1.0 
Created Date     : 07/14/2016
Function         : Test class to LinkedCases_C_HUM Apex class
Modification Log : 
 *      Developer               Code Review            Date                 Description
 * ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *  Harika Devi Kalidindi          33954            07/14/2016            REQ-269761 Original Version
 *  Harika Devi Kalidindi          37687            08/30/2016            Defect 238584.0001 Fix 
 *  Anupama tavva                  81949            04/04/2017            REQ - 306357- update to the CTCI model
 *  Shreya Choodamani              90487            04/28/2017            REQ#: 311397 Updated the test class for Pharmacy USers
 *  Pradeepkumar Dani              102268           06/16/2017            Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
 *  Harika Devi Kalidindi          112000           07/20/2017            REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
 *  Vinay Lingegowda				                09/17/2020			  User Story 1364284: PR00091574 - test class fix
 * Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
 ****************************************************************************************************************************************************************************/
@isTest
Private Class LinkedCases_T_HUM
{
    /*
     * Method Name : setup
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */ 
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Supervisor');
        oUser1.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser1.Network_User_Id__c = 'SXS2222';
        insert oUser1;
        System.assertEquals(oUser.LastName, 'test User1');
        
        User oUser2 = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');
        oUser2.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser2.Network_User_Id__c = 'SXS2222';
        insert oUser2;
               
        User oUser3 = HUMTestDataHelperTest.createTestUser('User4','Humana Pharmacy Specialist');
        oUser3.Current_Queue__c = 'Cincinnati Home Office Calls';
        oUser3.Network_User_Id__c = 'SXS2222';
        insert oUser3;
        
        PermissionSet ps = [Select Id, name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        
        PermissionSetAssignment psa = New PermissionSetAssignment();
        psa.AssigneeId = oUser3.Id;
        psa.PermissionSetId = ps.Id;
        insert psa;
        
        system.assertEquals(psa.PermissionSetId,ps.Id);
        
        System.runAs(oUser)
        {
            
            Classification_Type__c objcT = New Classification_Type__c();
            objcT.Name ='CallRSOpp';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOpp');
            
            List<Work_Queue_Setup__c> oQSetup = New List<Work_Queue_Setup__c>();
            oQSetup.add(New Work_Queue_Setup__c(Name='Cincinnati CRU',Classification_Type__c=objcT.id));
            oQSetup.add(New Work_Queue_Setup__c(Name='Clinical Intake',Classification_Type__c=objcT.id));
            oQSetup.add(New Work_Queue_Setup__c(Name='Dental Calls',Classification_Type__c =objct.id));
            
            insert oQSetup ;
            system.assertNotEquals(Null , oQSetup[0].id);
            
            List<Account> oAcc = New List<Account>();
            oAcc.add( New Account(Group_Number__c='11111',BillingState='New York',Name = 'TestAccount1',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            oAcc.add(New Account(Group_Number__c='13211',BillingState='Florida',Name = 'Account1',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            oAcc.add(New Account(Group_Number__c='21181',BillingState='New York',Name = 'TestAcc',recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId(),Source_Platform_Code__c = 'CA'));
            insert oAcc;                        
            system.assertNotEquals(null , oAcc[0].Id, 'Value not matched'); 
           
            Policy__c oPolicy1 = New Policy__c();
            oPolicy1.Name ='test123';
            oPolicy1.Group_Name__c= oAcc[0].Id;
            oPolicy1.Policy_Group_Number__c='11111';
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Issue_State__c = 'IL';  
            insert oPolicy1;            
            system.assertNotEquals(oPolicy1, null, 'Value not matched');
            System.assertEquals(oPolicy1.Funding_Type_Code__c, 'Y');
            
            
            List<Intent__c> oIntent= New List<Intent__c>();
            oIntent.add(New Intent__c(Name='Filling'));
            oIntent.add(New Intent__c(Name='payment to humana'));
            oIntent.add(New Intent__c(Name='Intial Build Request'));
            insert oIntent;
            system.assertNotEquals(Null , oIntent[0].id);
            System.assertEquals(oIntent.size(),3);
            
            
            
            Policy_Member__c oPolMem1 = New Policy_Member__c();
            oPolMem1.Relationship__c = 'Self';
            oPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem1.End_Date__c=Date.today().addDays(1).format();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc[0].Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assertNotEquals(oPolMem1, null, 'Value not matched');
            System.assertEquals(oPolMem1.Relationship__c, 'Self');
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claimskk';
            classificationRec.Is_Active__c = true;
            insert classificationRec;
            System.assertEquals(classificationRec.Name,'Claimskk');
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Fillingkk';
            intentRec.Is_Active__c = true;
            insert intentRec;
            System.assertEquals(intentRec.Name,'Fillingkk');
           
            
            CTCI_Junction__c objCTCI = New CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            system.assertNotEquals(Null,objCTCI.id);
            
            List<Case> lstCases = New List<Case>();
            
            lstCases.Add(New Case(AccountId=oAcc[0].Id, Policy_Member__c= oPolMem1.Id ,
                             Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id, Interacting_With__c=oAcc[0].Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', Created_By_Queue__c='Clinical Intake',
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(New Case(AccountId=oAcc[1].Id, Policy_Member__c= oPolMem1.Id,
                             Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id, Interacting_With__c=oAcc[1].Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',Created_By_Queue__c='Clinical Intake', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
            lstCases.Add(New Case(AccountId=oAcc[2].Id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',CTCI_List__c= objCTCI.id,Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc[2].Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Created_By_Queue__c='Cincinnati CRU',Owner_Queue__c='Cincinnati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed'));
            
            lstCases.Add(New Case(AccountId=oAcc[0].Id, Policy_Member__c= oPolMem1.Id ,
                              Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc[0].Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU',CTCI_List__c= objCTCI.id, Created_By_Queue__c='Dental Calls', Service_Center__c ='Cincinnati',Department__c='CRU', status='Pending'));
            insert lstCases;
            system.assertNotEquals(null, lstCases[0].Id);
            
            
            List<Case_Junction__c> lstCaseJun = New List<Case_Junction__c>();

            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[0].Id, Case__c = lstCases[1].Id,Work_Queue__c = oQSetup[0].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[2].Id, Case__c = lstCases[0].Id,Work_Queue__c = oQSetup[1].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[0].Id, Case__c = lstCases[3].Id,Work_Queue__c = oQSetup[2].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[1].Id, Case__c = lstCases[2].Id,Work_Queue__c = oQSetup[2].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[3].Id, Case__c = lstCases[1].Id,Work_Queue__c = oQSetup[1].Id));
            lstCaseJun.add(New Case_Junction__c(Case_Master__c = lstCases[2].Id, Case__c = lstCases[3].Id,Work_Queue__c = oQSetup[0].Id));
            
            insert lstCaseJun;   
          
            system.assertNotEquals(null, lstCaseJun[0].Id);
            system.assertEquals( lstCaseJun[0].Case__c,lstCases[1].Id);        
            
            Pharmacy_Supervisor_HUM__c objPharmacySup = New Pharmacy_Supervisor_HUM__c(name ='CRMS_300_Humana_Pharmacy_Supervisor');
                insert objPharmacySup;
                system.assertEquals(objPharmacySup.Name,'CRMS_300_Humana_Pharmacy_Supervisor');  
        }
      }
      
      
    /*
     * Method Name : testFunctionalityWithSpecialist
     * Description : This method Is used To test main functionality With Specialist profile.
     * Return type : void
     * Parameter   : NA
     */
     Static testMethod  void testFunctionalityWithSpecialist() 
     {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id,Case_Master__r.CTCI_List__r.Classification__c from Case_Junction__c where IsLinked__c=true  Limit 100];
            System.assert(lstCasejunction.size() >0);    
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC = New LinkedCases_C_HUM(controller);
            If (lstCasejunction!= NULL &&!lstCasejunction.isEmpty())
            {
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
            system.assertNotEquals(null,lstCasejunction [0].Case__r.Id);
            objLinkC.sSortExp= 'sCaseCaseNumber';
            objLinkC.setsortOrder(objLinkC .getsortOrder());
            System.assertEquals('sCaseCaseNumber',objLinkC .sSortExp );
            objLinkC.getListwrapper();
            objLinkC.lstwrapper=objLinkC.getRelatedCases();
            System.assert(objLinkC .lstwrapper.size() > 0);
            System.assertEquals(6, lstCasejunction.size() );
            objLinkC.dosort();
            objLinkC.getPrevious();
            objLinkC.getFirst();
            objLinkC.getLast();
            objLinkC.getNext(); 
            objLinkC.dosort();
            objLinkC.getDisablePrevious();
            objLinkC.getDisableNext();
            objLinkC.getTotal_size();
            objLinkC.getPageFirstRecord();
            objLinkC.getPageLastRecord();
            ApexPages.currentPage().getParameters().put('caseNo', objLinkC .lstwrapper[0].sCaseId);
            objLinkC .unlinking();
            }
            Test.StopTest();
        }
    }
    
    
    /*
     * Method Name : testsortFunctionality1
     * Description : This method Is used To test sorting functionality.
     * Return type : void
     * Parameter   : NA
     */
    Static testMethod  void testsortFunctionality1() 
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id, IsCloneCase__c from Case_Junction__c where IsLinked__c=true Limit 100];
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC = New LinkedCases_C_HUM(controller);
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
           
            objLinkC .sSortExp= 'sCaseCaseNumber';
            System.assertEquals('sCaseCaseNumber',objLinkC .sSortExp );
            objLinkC .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .getListwrapper();
            objLinkC .lstwrapper=objLinkC.getRelatedCases();

            
            LinkedCases_C_HUM objLinkC1 = New LinkedCases_C_HUM(controller);
            objLinkC1 .sSortExp= 'sCaseClassification';
            System.assertEquals('sCaseClassification',objLinkC1 .sSortExp );
            objLinkC1 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .sortableField = 'sCaseClassification';
            objLinkC1 .getListwrapper();
            objLinkC1 .lstwrapper=objLinkC1.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC2 = New LinkedCases_C_HUM(controller);
            objLinkC2 .sSortExp= 'sCaseClassification';
            System.assertEquals('sCaseClassification',objLinkC2 .sSortExp ); 
            objLinkC .sortableField = 'sCaseClassification';
            objLinkC2 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC2 .getListwrapper();
            objLinkC2 .lstwrapper=objLinkC2.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC3 = New LinkedCases_C_HUM(controller);
            objLinkC3 .sSortExp= 'sCaseIntent';
            System.assertEquals('sCaseIntent',objLinkC3.sSortExp );
            objLinkC3.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .sortableField = 'sCaseIntent';
            objLinkC3.getListwrapper();
            objLinkC3.lstwrapper=objLinkC3.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC4 = New LinkedCases_C_HUM(controller);
            objLinkC4.sSortExp= 'sCaseIntent';
            System.assertEquals('sCaseIntent',objLinkC4.sSortExp );
            objLinkC4.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC .sortableField = 'sCaseIntent';
            objLinkC4.getListwrapper();
            objLinkC4.lstwrapper=objLinkC4.getRelatedCases();
           
            LinkedCases_C_HUM objLinkC5 = New LinkedCases_C_HUM(controller);
            objLinkC5.sSortExp= 'sCaseInteractingAbout';
            System.assertEquals('sCaseInteractingAbout',objLinkC5.sSortExp );
            objLinkC5.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .sortableField = 'sCaseInteractingAbout';
            objLinkC5.getListwrapper();
            objLinkC5.lstwrapper=objLinkC5.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC6 = New LinkedCases_C_HUM(controller);
            objLinkC6 .sSortExp= 'sCaseInteractingAbout';
            System.assertEquals('sCaseInteractingAbout',objLinkC6.sSortExp );
            objLinkC .sortableField = 'sCaseInteractingAbout';
            objLinkC6.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC6.getListwrapper();
            objLinkC6.lstwrapper=objLinkC6.getRelatedCases();
           
            LinkedCases_C_HUM objLinkC7 = New LinkedCases_C_HUM(controller);          
            objLinkC7 .sSortExp= 'sCaseStatus';
            System.assertEquals('sCaseStatus',objLinkC7.sSortExp );
            objLinkC7 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .sortableField = 'sCaseStatus';
            objLinkC7 .getListwrapper();
            objLinkC7 .lstwrapper=objLinkC7.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC8 = New LinkedCases_C_HUM(controller);
            objLinkC8.sSortExp= 'sCaseStatus';
            System.assertEquals('sCaseStatus',objLinkC8.sSortExp );
            objLinkC8.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC .sortableField = 'sCaseStatus';
            objLinkC8.getListwrapper();
            objLinkC8.lstwrapper=objLinkC8.getRelatedCases();
           
            LinkedCases_C_HUM objLinkC9 = New LinkedCases_C_HUM(controller); 
            objLinkC9.sSortExp= 'dCaseCreatedDate';
            System.assertEquals('dCaseCreatedDate',objLinkC9.sSortExp );
            objLinkC9.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC .sortableField = 'dCaseCreatedDate';
            objLinkC9.getListwrapper();
            objLinkC9.lstwrapper=objLinkC9.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC10 = New LinkedCases_C_HUM(controller);
            objLinkC10 .sSortExp= 'dCaseCreatedDate';
            System.assertEquals('dCaseCreatedDate',objLinkC10.sSortExp );
            objLinkC10.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC .sortableField = 'dCaseCreatedDate';
            objLinkC10 .getListwrapper();
            objLinkC10 .lstwrapper=objLinkC10.getRelatedCases();
            
            Test.StopTest();
        }
    }


    
    /*
     * Method Name : testsortFunctionality2
     * Description : This method Is used To test sorting functionality.
     * Return type : void
     * Parameter   : NA
     */
    Static testMethod  void testsortFunctionality2() 
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id,IsCloneCase__c from Case_Junction__c where IsLinked__c=true Limit 100];
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC = New LinkedCases_C_HUM(controller);
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
           
            LinkedCases_C_HUM objLinkC11 = New LinkedCases_C_HUM(controller);
            objLinkC11.sSortExp= 'dCaseClosedDate';
            System.assertEquals('dCaseClosedDate',objLinkC11.sSortExp );
            objLinkC11 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC11 .getListwrapper();
            objLinkC11.lstwrapper=objLinkC11.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC12 = New LinkedCases_C_HUM(controller);
            objLinkC12 .sSortExp= 'dCaseClosedDate';
            System.assertEquals('dCaseClosedDate',objLinkC12.sSortExp );
            objLinkC12.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC12 .getListwrapper();
            objLinkC12 .lstwrapper=objLinkC12.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC13 = New LinkedCases_C_HUM(controller);
            objLinkC13.sSortExp= 'sCaseCreatedByName';
            System.assertEquals('sCaseCreatedByName',objLinkC13.sSortExp );
            objLinkC13 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC13 .getListwrapper();
            objLinkC13.lstwrapper=objLinkC13.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC14 = New LinkedCases_C_HUM(controller);
            objLinkC14 .sSortExp= 'sCaseCreatedByName';
            System.assertEquals('sCaseCreatedByName',objLinkC14.sSortExp );
            objLinkC14.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC14.getListwrapper();
            objLinkC14 .lstwrapper=objLinkC14.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC15 = New LinkedCases_C_HUM(controller);
            objLinkC15.sSortExp= 'sCaseCreatedByQueue';
            System.assertEquals('sCaseCreatedByQueue',objLinkC15.sSortExp );
            objLinkC15 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC15 .getListwrapper();
            objLinkC15.lstwrapper=objLinkC15.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC16 = New LinkedCases_C_HUM(controller);
            objLinkC16 .sSortExp= 'sCaseCreatedByQueue';
            System.assertEquals('sCaseCreatedByQueue',objLinkC16.sSortExp );
            objLinkC16.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC16 .getListwrapper();
            objLinkC16 .lstwrapper=objLinkC16.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC17 = New LinkedCases_C_HUM(controller);
            objLinkC17.sSortExp= 'dCreatedDate';
            System.assertEquals('dCreatedDate',objLinkC17.sSortExp );
            objLinkC17 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC17 .getListwrapper();
            objLinkC17.lstwrapper=objLinkC17.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC18 = New LinkedCases_C_HUM(controller);
            objLinkC18 .sSortExp= 'dCreatedDate';
            System.assertEquals('dCreatedDate',objLinkC18.sSortExp );
            objLinkC18.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC18 .getListwrapper();
            objLinkC18 .lstwrapper=objLinkC18.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC19 = New LinkedCases_C_HUM(controller);
            objLinkC19.sSortExp= 'sWorkQueueName';
            System.assertEquals('sWorkQueueName',objLinkC19.sSortExp );
            objLinkC19 .setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM);
            objLinkC19 .getListwrapper();
            objLinkC19.lstwrapper=objLinkC19.getRelatedCases();
            
            LinkedCases_C_HUM objLinkC20 = New LinkedCases_C_HUM(controller);
            objLinkC20.sSortExp= 'sWorkQueueName';
            System.assertEquals('sWorkQueueName',objLinkC20.sSortExp );
            objLinkC20.setsortOrder(GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM);
            objLinkC20.getListwrapper();
            objLinkC20.lstwrapper=objLinkC20.getRelatedCases();
        
            Test.StopTest();
        }
    }

    /*
     * Method Name : testFunctionalityWithSupervisor
     * Description : This method Is used To test main functionality With Supervisor profile.
     * Return type : void
     * Parameter   : NA
     */
     Static testMethod  void testFunctionalityWithSupervisor() 
     {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User2'];
        System.assertEquals('Cincinnati Home Office Calls', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id from Case_Junction__c where IsLinked__c=true limit 100];
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC = New LinkedCases_C_HUM(controller);
            If (lstCasejunction!= NULL &&!lstCasejunction.isEmpty())
            {
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
            system.assertNotEquals(null,lstCasejunction [0].Case__r.Id);
            objLinkC.sSortExp= 'sCaseCaseNumber';
            objLinkC.setsortOrder(objLinkC .getsortOrder());
            System.assertEquals('sCaseCaseNumber',objLinkC .sSortExp );
            objLinkC.getListwrapper();
            objLinkC.lstwrapper=objLinkC .getRelatedCases();
            System.assert(objLinkC .lstwrapper.size() > 0);
            System.assertEquals(6, lstCasejunction.size() );
            objLinkC.dosort();
            objLinkC.getPrevious();
            objLinkC.getFirst();
            objLinkC.getLast();
            objLinkC.getNext(); 
            objLinkC.dosort();
            objLinkC.getDisablePrevious();
            objLinkC.getDisableNext();
            objLinkC.getTotal_size();
            objLinkC.getPageFirstRecord();
            objLinkC.getPageLastRecord();
            }
          
            
            Test.StopTest();
        }
    }
    /*
     * Method Name : testFunctionalityWithPharmacySpecialist
     * Description : This method Is used To test main functionality With pharmacy specialist profile.
     * Return type : void
     * Parameter   : NA
     */
     Static testMethod  void testFunctionalityWithPharmacySpecialist() 
     {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User3'];
        System.assertEquals('Cincinnati Home Office Calls', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id from Case_Junction__c where IsLinked__c=true limit 100];
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC = New LinkedCases_C_HUM(controller);
            If (lstCasejunction!= NULL &&!lstCasejunction.isEmpty())
            {
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
            system.assertNotEquals(null,lstCasejunction [0].Case__r.Id);
            objLinkC.sSortExp= 'sCaseCaseNumber';
            objLinkC.setsortOrder(objLinkC .getsortOrder());
            System.assertEquals('sCaseCaseNumber',objLinkC .sSortExp );
            objLinkC.getListwrapper();
            objLinkC.lstwrapper=objLinkC .getRelatedCases();
            System.assert(objLinkC .lstwrapper.size() > 0);
            System.assertEquals(6, lstCasejunction.size() );
            objLinkC.dosort();
            objLinkC.getPrevious();
            objLinkC.getFirst();
            objLinkC.getLast();
            objLinkC.getNext(); 
            objLinkC.dosort();
            objLinkC.getDisablePrevious();
            objLinkC.getDisableNext();
            objLinkC.getTotal_size();
            objLinkC.getPageFirstRecord();
            objLinkC.getPageLastRecord();
            }
          
            
            Test.StopTest();
        }
    }
    
    /*
     * Method Name : testFunctionalityWithPharmacySupervisor
     * Description : This method is used to test main functionality with pharmacy supervisor profile.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod  void testFunctionalityWithPharmacySupervisor() 
     {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test User4'];
        System.assertEquals('Cincinnati Home Office Calls', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Case_Junction__c> lstCasejunction = [Select Id,Case__r.Id,Case_Master__r.Id from Case_Junction__c where IsLinked__c=true limit 100];
            ApexPages.StandardController controller;
            LinkedCases_C_HUM objLinkC  = new LinkedCases_C_HUM(controller);
            if(lstCasejunction != NULL && !lstCasejunction.isEmpty())
            {
            ApexPages.currentPage().getParameters().put('Id', lstCasejunction [0].Case__r.Id);
            system.assertNotEquals(null,lstCasejunction [0].Case__r.Id);
            objLinkC.sSortExp= 'sCaseCaseNumber';
            objLinkC.setsortOrder(objLinkC .getsortOrder());
            System.assertEquals('sCaseCaseNumber',objLinkC .sSortExp );
            objLinkC.getListwrapper();
            objLinkC.lstwrapper=objLinkC .getRelatedCases();
            System.assert(objLinkC .lstwrapper.size() > 0);
            System.assertEquals(6, lstCasejunction.size() );
            objLinkC.dosort();
            objLinkC.getPrevious();
            objLinkC.getFirst();
            objLinkC.getLast();
            objLinkC.getNext(); 
            objLinkC.dosort();
            objLinkC.getDisablePrevious();
            objLinkC.getDisableNext();
            objLinkC.getTotal_size();
            objLinkC.getPageFirstRecord();
            objLinkC.getPageLastRecord();
            }
            Test.StopTest();
        }
    }
}