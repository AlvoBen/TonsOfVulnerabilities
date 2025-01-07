/*****************************************************************************************************************************************************************************
    @Apex Class Name  : GenericHelper_CRMService_LTNG_T_HUM
    @Version          : 1.0
    @Created Date     : May 12th, 2020
    @Function         : Test class for GenericHelper_CRMService_LTNG_H_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  12/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@istest
public class GenericHelper_CRMService_LTNG_T_HUM
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
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1099500026';
            objAcc.General_Account__c = true;
            objAcc.ETL_Record_Deleted__c = false;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='PDP');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MPD';
            objpol.Source_Cust_Cov_Key__c = '246002001001';
            objpol.Platform__c = 'LV';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'246002001001');
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.ETL_Record_Deleted__c = false;
            objPolMem.Relationship__c = 'Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c = '01/01/2019';
            objPolMem.End_Date__c = '12/31/2019';
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
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
            
			Case oCase1 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
								Policy_Member__c = objPolMem.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
								Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
								Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
								Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
								G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
			insert oCase1;
            system.assertNotEquals(oCase1.Id,Null);

            List<Member_Maintenance_Generics__c> lstObjMMG = new List<Member_Maintenance_Generics__c>();
			Member_Maintenance_Generics__c objMMG1 = new Member_Maintenance_Generics__c();
				objMMG1.Name = 'Update Member';
				objMMG1.Applicable_Platform_List__c = 'LV,EM';
            	lstObjMMG.add(objMMG1);
            
            Member_Maintenance_Generics__c objMMG2 = new Member_Maintenance_Generics__c();
				objMMG2.Name = 'Termed Months';
				objMMG2.Message__c = '19';
            	lstObjMMG.add(objMMG2);
            
				insert lstObjMMG;
            
            CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1645972',
                IsON__c = false,
                Release_Name__c ='12/04/2020',
                Scrum_Team__c ='Thunder',
                Story_Title__c ='PR00093155 - INC0717625 - eligibility error for Termed CBIS vs Active Medicare');
                insert objCS;
        }
    }
    
    /*
    * Method to test success result for GenericHelper
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessGenericHelper()
    {
        Account accountrec = [select id,name,Enterprise_id__c from account where Lastname = 'Account' limit 1];
        Case  caserec = [select id,Accountid,subject,description from case where DCN__c='12345678' limit 1];
        Policy_Member__c pmObj = [Select id,Status__c,End_Date__c,Effective_Date__c from Policy_Member__c where Name = 'testpolmem11' limit 1];
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj.id);
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        system.runAs(testUser)
        {
            Test.StartTest();
            String personId = '1099500026';
            string emptyString = '';
            String enteredDate = '01-02-2021';
            string groupRecordObject = '{"productTypeCode":"PDP","productType":"MPD","product":"MED","platformCode":"LV","majorLOB":"","groupId":"246002001001","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            GenericHelper_CRMService_LTNG_H_HUM objGenericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            objGenericHelper.retrievePolicyMemberId(groupRecordObject,personId);
            objGenericHelper.generateGenericsForRequest(5);
            objGenericHelper.procureEligibleDetails(pmObj.Id,true);
            objGenericHelper.retrievePolicyMemberIds(groupRecordObject,personId);
            objGenericHelper.getStateDropdown();
            objGenericHelper.fetchAccountDetails(caserec.id);
            personId = objGenericHelper.fetchPersonId(caserec.id);
            personId = objGenericHelper.fetchPersonId(emptyString);
            objGenericHelper.fetchDetailsForOSA(caserec.id);
            objGenericHelper.fetchDetailsForOSA(accountrec.id);
            objGenericHelper.procureEligibleDetailsList(lstPMObj,true);
            GenericHelper_CRMService_LTNG_H_HUM.checkIfDateInFuture(enteredDate);
            system.assertnotEquals(null, personId);
            Test.StopTest();
        }
    }
    
    /*
    * Method to test failure result for GenericHelper
    * @param        None
    * @return       None
    */
    private static testmethod void testFailureGenericHelper()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            Test.StartTest();
            String PersonId = '';
            GenericHelper_CRMService_LTNG_H_HUM objGenericHelper = new GenericHelper_CRMService_LTNG_H_HUM();
            PersonId = objGenericHelper.fetchPersonId(null);
            system.assertEquals('', PersonId);
            Test.StopTest();
        }                                  
     }

        private static testmethod void testprocureDepEligibleDetailsList(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Policy_Member__c subId = [Select Subscriber__c from Policy_Member__c where Name = 'testpolmem11' limit 1];
        List<String> lstSubId = new List<String>();
        lstSubId.add(subId.Subscriber__c);
        system.runAs(testUser){
            GenericHelper_CRMService_LTNG_H_HUM.procureDepEligibleDetailsList(lstSubId);
        }
    }
    
    private static testmethod void testprocureSelfEligibleDetailsList(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        Policy_Member__c pmid = [Select Id from Policy_Member__c where Name = 'testpolmem11' limit 1];
        List<String> lstpmIds = new List<String>();
        lstpmIds.add(pmid.Id);
        system.runAs(testUser){
            GenericHelper_CRMService_LTNG_H_HUM.procureSelfEligibleDetailsList(lstpmIds);
        }
    }
}