/*******************************************************************************************************************************
Apex Class Name : CaseLinkingLogic_T_HUM
Version         : 1.0
Created On      : 07/08/2016
Function        : Test class for CaseLinkingLogic_S_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava             32594                     07/08/2016                 Original version
* Pradeepkumar Dani			102268   				   06/16/2017				  Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Harika Devi Kalidindi     112000                     07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Mohammed Noor										   05/03/2021				  Increase code coverage.
*******************************************************************************************************************************/

@isTest
private class CaseLinkingLogic_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('LinkUser1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test LinkUser1');
        system.runAs(oUser)
        {                        
            Account oAcc = new Account();
            oAcc.Group_Number__c='11111';
            oAcc.BillingState='New York';
            oAcc.Name = 'TestAccount1';            
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();            
            oAcc.Source_Platform_Code__c = 'CA';            
            insert oAcc;                        
            system.assertNotEquals(null , oAcc.Id, 'Value not matched'); 
           
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Name ='test123';
            oPolicy1.Group_Name__c= oAcc.Id;
            oPolicy1.Funding_Type_Code__c = 'Y';
            oPolicy1.Issue_State__c = 'IL';
            oPolicy1.Policy_Group_Number__c='11111'; 
            insert oPolicy1;            
            system.assertNotEquals(oPolicy1, null, 'Value not matched');
            System.assertEquals(oPolicy1.Funding_Type_Code__c, 'Y');
            
            Policy_Member__c oPolMem1 = new Policy_Member__c();
            oPolMem1.Relationship__c = 'Self';
            oPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem1.End_Date__c=Date.today().addDays(1).format();
            oPolMem1.Name = '12345';
            oPolMem1.Member__c = oAcc.Id;
            oPolMem1.Policy__c = oPolicy1.Id;            
            oPolMem1.Relationship__c = 'Self';            
            insert oPolMem1;            
            system.assertNotEquals(oPolMem1, null, 'Value not matched');
            System.assertEquals(oPolMem1.Relationship__c, 'Self');
			
			List<Classification_Type__c> ctList = new list<Classification_Type__c>();
			Classification_Type__c ctRec = new Classification_Type__c();
			ctRec.name = 'Claims (RSO)';
			ctRec.Is_Active__c = true;
			ctList.add(ctRec);
			insert ctList;
			system.assertEquals(ctList.size(), 1);

			Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
			wqsRecord.Name = 'Cincinnati CRU';
			wqsRecord.Classification_Type__c = ctRec.Id;
			wqsRecord.IsActive__c = true;
			insert wqsRecord;
			system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');

			List<Classification__c> lstClassification = new List<Classification__c>();
			Classification__c classificationRec = new Classification__c();
			classificationRec.Name = 'Claims';
			classificationRec.Is_Active__c = true;
			lstClassification.add(classificationRec);
			insert lstClassification;
			system.assertEquals(lstClassification[0].Name == 'Claims', true);

			List<Intent__c> lstIntent = new List<Intent__c>();
			Intent__c intentRec = new Intent__c();
			intentRec.Name = 'Filling';
			intentRec.Is_Active__c = true;
			lstIntent.add(intentRec);
			insert lstIntent;
			system.assertEquals(lstIntent[0].Name == 'Filling', true);

			List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
			CTCI_Junction__c ctciRec = new CTCI_Junction__c();
			ctciRec.Classification_Type__c = ctRec.id;
			ctciRec.Classification__c = classificationRec.Id;
			ctciRec.Intent__c = intentRec.id;
			ctciRec.IsActive__c = true;
			ctciList.add(ctciRec);
			insert ctciList;
			system.assertEquals(ctciList.size(), 1);
            List<Case> lstCases = new List<Case>();
            lstCases.Add(new Case(AccountId=oAcc.Id, Policy_Member__c= oPolMem1.Id , CTCI_List__c = ctciRec.Id,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
			lstCases.Add(new Case(AccountId=oAcc.Id, Policy_Member__c= oPolMem1.Id, CTCI_List__c = ctciRec.Id,
                             Interacting_With_Type__c = 'Group', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(),
                             Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                             Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
            
			lstCases.Add(new Case(AccountId=oAcc.Id, Policy_Member__c= oPolMem1.Id , CTCI_List__c = ctciRec.Id,
                              Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Interacting_With__c=oAcc.Id, Due_Date__c=Date.Today(), Case_Owner__c=UserInfo.getUserName(),
                              Owner_Queue__c='Cincinnati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed'));
            insert lstCases;
            system.assertNotEquals(null, lstCases[0].Id);

            Case_Junction__c objCaseJun = new Case_Junction__c();
            objCaseJun.Case_Master__c = lstCases[0].Id;
            objCaseJun.Case__c = lstCases[1].Id;
            objCaseJun.Work_Queue__c = wqsRecord.Id;
            insert objCaseJun;
            system.assertNotEquals(null, objCaseJun.Id);
            system.assertNotEquals(lstCases[1].Id, objCaseJun.Case_Master__c);
        }
    }
    
    /*
    * Method Name : testfunctionality
    * Description : This method is used to test the group billing functionality.
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testfunctionality()
    {
        User oUser = [Select Id, Current_Queue__c,Network_User_Id__c  from User where LastName = 'test LinkUser1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            List<Case> lstCases = [Select Id from Case];
            String sCases = '';
            For(Case obj : lstCases)
            {
                sCases = sCases +',' + obj.Id+'_'+'true';
            }
            sCases = sCases.substring(1);           
            
            Boolean bIsSuccess = CaseLinkingLogic_S_HUM.LinkCases(sCases );
            System.assertEquals(true, bIsSuccess);
            
            bIsSuccess = CaseLinkingLogic_S_HUM.LinkCases(Null);
            System.assertEquals(false, bIsSuccess);
        }
    }
}