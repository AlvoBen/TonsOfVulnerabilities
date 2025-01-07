/*****************************************************************************************************************************************************************************
@Apex Class Name  : GenericHelper_LightningStrides_LT_HUM
@Version          : 1.0
@Created Date     : 06/09/2022
@Test Class For  : GenericHelper_LightningStrides_LH_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/
@istest
public class GenericHelper_LightningStrides_LT_HUM
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
            
            //Product2 oProd1 = new Product2(Name = 'DEN',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c='PDP');
            //insert oProd1;
            //System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            //new addition of PurchaserPlan in place of poliy__c
            PurchaserPlan objpol = new PurchaserPlan();
            objpol.Name = 'testPolicy';
            objpol.Source_Cust_Cov_Key__c = '246002001001';//'123456789' ; 
            objpol.PayerId = objAcc.Id;
            objpol.Major_LOB__c ='MER';
            objpol.Platform__c = 'LV';
            objpol.ETL_Record_Deleted__c = false;
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.EffectiveFrom=Date.today().addDays(-1);
            insert objpol;
            
            /*PurchaserPlan objpol2 = new PurchaserPlan();
            objpol2.Name = 'testPolicy';
            objpol2.Source_Cust_Cov_Key__c = '246002001001';//'123456789' ; 
            objpol2.PayerId = objAcc.Id;
            objpol2.Major_LOB__c ='MER';
            objpol2.Platform__c = 'LV';
            objpol2.ETL_Record_Deleted__c = false;
            objpol2.Product__c = oProd1.Id;
            objpol2.Product__r = oProd1;
            objpol2.EffectiveFrom=Date.today().addDays(-1);
            insert objpol2;*/
            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name='testpolmem11';
            objPolMem.MemberId=objAcc.id;
           // objPolMem.Status__c = 'Active'; 
            objPolMem.RelationshipToSubscriber='Self';
            objPolMem.Relationship_Code__c = '00';
            objPolMem.SubscriberPlanId__c=objPolMem.Id;
            objPolMem.EffectiveTo= Date.parse(Date.today().addDays(1).format());
            objPolMem.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.GroupNumber = '737803';
            objPolMem.PlanId= objpol.Id;
            objPolMem.ETL_Record_Deleted__c = false;
            insert objPolMem;
            
            MemberPlan objPolMem2 = new MemberPlan();
            objPolMem2.Name='testpolmem21';
            objPolMem2.MemberId=objAcc.id;
            //objPolMem2.Status__c = 'Termed'; 
            objPolMem2.RelationshipToSubscriber='Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.SubscriberPlanId__c=objPolMem.Id;
            objPolMem2.EffectiveTo= Date.parse(Date.today().addDays(-9).format());
            objPolMem2.EffectiveFrom=Date.parse(Date.today().addDays(-10).format());
            objPolMem2.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-02';
            objPolMem2.GroupNumber = '737803';
            objPolMem2.PlanId= objpol.Id;
            objPolMem2.ETL_Record_Deleted__c = false;
            insert objPolMem2;
            
            /*
            MemberPlan objPolMem3 = new MemberPlan();
            objPolMem3.Name='testpolmem13';
            objPolMem3.MemberId=objAcc.id;
            objPolMem3.Status__c = 'Future'; 
            objPolMem3.RelationshipToSubscriber='Self';
            objPolMem3.Relationship_Code__c = '00';
            objPolMem3.SubscriberPlanId__c=objPolMem.Id;
            objPolMem3.EffectiveTo= Date.parse(Date.today().addDays(1).format());
            objPolMem3.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem3.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-03';
            objPolMem3.GroupNumber = '737803';
            objPolMem3.PlanId= objpol.Id;
            objPolMem3.ETL_Record_Deleted__c = false;
            insert objPolMem3;*/
            
            
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
                                   Member_Plan_Id__c = objPolMem.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                                   Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                                   Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                                   Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
                                   G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
            insert oCase1;
            Case oCase2 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                                   Member_Plan_Id__c = objPolMem.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                                   Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                                   Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                                   Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
                                   G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                   status='In Progress');
            insert oCase2;
            
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
        MemberPlan pmObj = [Select id,Member_Coverage_Status__c,EffectiveTo,EffectiveFrom from MemberPlan where Name = 'testpolmem11' limit 1];
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj.id);
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        system.runAs(testUser)
        {
            Test.StartTest();
            String personId = '1099500026';
            string emptyString = '';
            String enteredDate = '01-02-2021';
            string groupRecordObject = '{"productTypeCode":"PDP","productType":"MER","product":"MED","platformCode":"LV","majorLOB":"","groupId":"246002001001","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            GenericHelper_LightningStrides_LH_HUM objGenericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
            GenericHelper_LightningStrides_LH_HUM.checkIfDateInFuture(enteredDate);
            
            objGenericHelper.fetchPersonId(accountrec.Id);
            
            //pmObj.Status__c='Future';
            pmObj.EffectiveTo= Date.parse(Date.today().addDays(10).format());
            pmObj.EffectiveFrom=Date.parse(Date.today().addDays(2).format());
            update pmObj;
            objGenericHelper.fetchDetailsForOSA(caserec.id);
            objGenericHelper.fetchDetailsForOSA(accountrec.id);
            
            //pmObj.Status__c='Termed';
            pmObj.EffectiveTo= Date.parse(Date.today().addDays(-2).format());
            pmObj.EffectiveFrom=Date.parse(Date.today().addDays(-10).format());
            update pmObj;            
            objGenericHelper.fetchDetailsForOSA(caserec.id);
            objGenericHelper.fetchDetailsForOSA(accountrec.id);
            
            system.assertnotEquals(null, personId);
            
            string groupRecordObject1 = '{"details":[{"platform":"","primary":true,"policyMemberId":"","product":{"productTypeCode":"MED","productType":"PDP","product":"MED","platformCode":"LV","majorLOB":"","groupId":"237877","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}}]}';
            objGenericHelper.retrievePolicyMemberIds(groupRecordObject1,'1099500026');
            Test.StopTest();
        }
    }

    
    private static testmethod void testSuccessGenericHelpernew()
    {
        Account accountrec = [select id,name,Enterprise_id__c from account where Lastname = 'Account' limit 1];
        Case  caserec = [select id,Accountid,subject,description from case where DCN__c='12345678' limit 1];
        MemberPlan pmObj = [Select id,Member_Coverage_Status__c,EffectiveTo,EffectiveFrom from MemberPlan where Name = 'testpolmem11' limit 1];
        List<String> lstPMObj = new List<String>();
        lstPMObj.add(pmObj.id);
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        system.runAs(testUser)
        {
           CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '1645972',
                IsON__c = false,
                Release_Name__c ='12/04/2020',
                Scrum_Team__c ='Thunder',
                Story_Title__c ='PR00093155 - INC0717625 - eligibility error for Termed CBIS vs Active Medicare');
            insert objCS;
            Test.StartTest();
            String personId = '1099500026';
            string emptyString = '';
            String enteredDate = '01-02-2021';
            string groupRecordObject = '{"productTypeCode":"PDP","productType":"MER","product":"MED","platformCode":"LV","majorLOB":"","groupId":"246002001001","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            GenericHelper_LightningStrides_LH_HUM objGenericHelper = new GenericHelper_LightningStrides_LH_HUM();
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
            GenericHelper_LightningStrides_LH_HUM.checkIfDateInFuture(enteredDate);
            
            objGenericHelper.fetchPersonId(accountrec.Id);
            
           //pmObj.Status__c='Future';
            pmObj.EffectiveTo= Date.parse(Date.today().addDays(10).format());
            pmObj.EffectiveFrom=Date.parse(Date.today().addDays(2).format());
            update pmObj;
            objGenericHelper.fetchDetailsForOSA(caserec.id);
            objGenericHelper.fetchDetailsForOSA(accountrec.id);
            
            //pmObj.Status__c='Termed';
            pmObj.EffectiveTo= Date.parse(Date.today().addDays(-2).format());
            pmObj.EffectiveFrom=Date.parse(Date.today().addDays(-10).format());
            update pmObj;            
            objGenericHelper.fetchDetailsForOSA(caserec.id);
            objGenericHelper.fetchDetailsForOSA(accountrec.id);
            
            system.assertnotEquals(null, personId);
            
            string groupRecordObject1 = '{"details":[{"platform":"","primary":true,"policyMemberId":"","product":{"productTypeCode":"MED","productType":"PDP","product":"MED","platformCode":"LV","majorLOB":"","groupId":"237877","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}}]}';
            objGenericHelper.retrievePolicyMemberIds(groupRecordObject1,'1099500026');
            Test.StopTest();
        }
    }

   private static testmethod void testprocureSelfEligibleDetails()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        {
            Test.StartTest();
            String PersonId = '';
            GenericHelper_LightningStrides_LH_HUM objGenericHelper = new GenericHelper_LightningStrides_LH_HUM();
            PersonId = objGenericHelper.fetchPersonId(null);
            system.assertEquals('', PersonId);
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
            GenericHelper_LightningStrides_LH_HUM objGenericHelper = new GenericHelper_LightningStrides_LH_HUM();
            PersonId = objGenericHelper.fetchPersonId(null);
            system.assertEquals('', PersonId);
            Test.StopTest();
        }                                  
    }
    
    private static testmethod void testprocureDepEligibleDetailsList(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        MemberPlan subId = [Select Id,Member_Coverage_Status__c,EffectiveTo,EffectiveFrom,SubscriberPlanId__c from MemberPlan where Name = 'testpolmem11' limit 1];
        string groupRecordObject = '{"productTypeCode":"PDP","productType":"MER","product":"MED","platformCode":"LV","majorLOB":"","groupId":"246002001001","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
        List<String> lstSubId = new List<String>();
        lstSubId.add(subId.SubscriberPlanId__c);
		
        system.runAs(testUser){
            GenericHelper_LightningStrides_LH_HUM objGenericHelper = new GenericHelper_LightningStrides_LH_HUM();
            //subId.Status__c='Future';
            subId.EffectiveTo= Date.parse(Date.today().addDays(10).format());
            subId.EffectiveFrom=Date.parse(Date.today().addDays(2).format());
            update subId;
            objGenericHelper.retrievePolicyMemberId(groupRecordObject,'1099500026');
            
            //string groupRecordObject2 = '{"productTypeCode":"PDP","productType":"MER","product":"DEN","platformCode":"LV","majorLOB":"","groupId":"246002001001","exchangeType":"","exchangeIndicator":"false","ediGroupIndicator":"false","dualDemoIndicator":"false","asoIndicator":"false"}';
            //subId.Status__c='Termed';
            subId.EffectiveTo= Date.parse(Date.today().addDays(-2).format());
            subId.EffectiveFrom=Date.parse(Date.today().addDays(-10).format());
            update subId;
            objGenericHelper.retrievePolicyMemberId(groupRecordObject,'1099500026');
            
            CRMFunctionality_ONOFF_Switch__c objCS = [select id,IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '1645972' Limit 1];
            objCS.IsON__c = true;
            update objCS;
            objGenericHelper.retrievePolicyMemberId(groupRecordObject,'1099500026');
			GenericHelper_LightningStrides_LH_HUM.procureDepEligibleDetailsList(lstSubId);
        }
    }
    
    private static testmethod void testprocureSelfEligibleDetailsList(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        MemberPlan pmid = [Select Id from MemberPlan where Name = 'testpolmem11' limit 1];
        List<String> lstpmIds = new List<String>();
        lstpmIds.add(pmid.Id);
       string s='';
        system.runAs(testUser){
            test.startTest();
            try{
            GenericHelper_LightningStrides_LH_HUM.checkIfDateInFuture(s);
            }catch(exception e){
                system.assert(true, 'Date Input is blank');
            }
                test.stopTest();
        }
    }
}