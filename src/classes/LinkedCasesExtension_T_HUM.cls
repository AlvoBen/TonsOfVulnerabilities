/******************************************************************************************************************************
Apex Class Name  : LinkedCasesExtension_T_HUM
Version          : 1.0 
Created Date     : NOV 17 2017
Function         : Test class for class LinkedCasesExtension_C_HUM

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Rakesh Patni       149179              11/17/2017           Original version.
 *    Santhosh Ganji                         04/19/2019           REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
 *********************************************************************************************************************************/

@isTest
private with sharing class LinkedCasesExtension_T_HUM
{
     /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
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
            lstCases.Add(New Case(AccountId=oAcc[2].Id,Policy_Member__c= oPolMem1.Id ,
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
        }
      }
      /*
      * Method Name : testFunctionality
      * Description : This method is used to test main functionality.
      * Return type : void
      * Parameter   : NA
      */
      static testmethod void method1(){
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c  from User where LastName = 'test User1'];
        List<Case> lCase = [Select id from case limit 10];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        System.runAs(oUser)
        {
            Test.StartTest();
            ApexPages.currentPage().getParameters().put('Id',lCase[0].id);
			LinkedCasesExtension_C_HUM cont = new LinkedCasesExtension_C_HUM();
            LinkedCasesExtension_C_HUM.isUnlinkVisible();
            LinkedCasesExtension_C_HUM.getLinkedCases(lCase[0].id);
            LinkedCasesExtension_C_HUM.unlinkCase(lCase[0].id,lCase[1].id);
            Test.stopTest();
        }
      } 
}