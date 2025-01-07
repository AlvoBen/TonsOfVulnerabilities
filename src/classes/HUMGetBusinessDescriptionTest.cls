/**********************************************************************************************************************************************
Apex Class Name  : HUMGetBusinessDescriptionTest
Version          : 1.0
Created Date     : JUN 10 2015
Function         : Test Class for "HUMGetBusinessDescription" Class

Modification Log :
 * Developer Name            Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                                         04/06/2015                   Original Version
 * Tanushree Hazari              24093                  06/18/2015                   Test class coverage for Defect 187428.0001 
 * Tanushree Hazari              24147                  06/22/2015                   Test class coverage for Defect 187756.0001
 * Naveen B Kanth                30848                  04/05/2016                   REQ - 252538 : Updated test class to accomodate the valiation rule.
 * Prachi Bhasin                 33337                  07/18/2016                   Changes made as part of REQ- 270349 
 * Pradeepkumar Dani             99570                  06/08/2017                   Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
 * Pradeepkumar Dani            100996                  06/13/2017                   Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
 * Santhi Mandava                                       03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
**********************************************************************************************************************************************/

@isTest
private class HUMGetBusinessDescriptionTest
{
    private static Policy_Member__c objPolMem;
    private static Case oCase;

    /**
    * Method to set up test data
    *<p>
    * @param    NULL
    * @return   void
    */ 
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('BusinessDescriptionUser1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.id != null);
        
        System.runAs(oUser){        
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Group_Number__c = '213654';
        insert objAcc; 
        system.assertEquals(objAcc.Name, 'testacc');
        
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c ='MED');
        insert oProd;
        System.assertEquals(oProd.Name, 'Laptop X200');
        
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objAcc.Id;
        objpol.Source_Cust_Cov_Key__c = '123456789'; 
        objpol.Product__c = oProd.Id;
        objpol.Product__r = oProd;
        insert objpol; 
        system.assertEquals(objpol.Name,'testPolicy');
        
        objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol.Id];
        
        objpol.Product__r.Segment_Type__c = oProd.Segment_Type__c;
        update objPol;
        system.assertEquals(objpol.Product__r.Segment_Type__c, oProd.Segment_Type__c);
        
        objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From Policy__c Where Id = : objpol.Id];
          
        objPolMem = new Policy_Member__c();
        objPolMem.Name = 'testpolmem';
        objPolMem.Relationship__c = 'Self';
        objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMem.End_Date__c=Date.today().addDays(1).format();
        objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
        objPolMem.Member__c = objAcc.Id;
        objPolMem.Policy__c = objpol.Id;
        objPolMem.Policy__r = objpol;
        insert objPolMem;
        system.assertEquals(objPolMem.Name, 'testpolmem');
        
         Account oAccount = HUMTestDataHelperTest.getAccount();
         oAccount.Name = 'New Case Account';
         oAccount.Group_Number__c  = '123';
         oAccount.BillingState = 'New York';
         oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
         insert oAccount;
         System.assertEquals(oAccount.Name, 'New Case Account');
                   
         Account mAccount = HUMTestDataHelperTest.getAccount();
         mAccount.FirstName = 'General';
         mAccount.LastName  = 'Member';
         mAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
         insert mAccount;
         System.assertEquals(mAccount.FirstName, 'General');
         
         Product2 oProduct = new Product2();
         oProduct.Name='Mediclaim';
         oProduct.ProductCode= 'MED';
         oProduct.Major_LOB__c='MGR';
         oProduct.Segment_Type__c='COM';
         insert oProduct;
         System.assertEquals(oProduct.Name,'Mediclaim'); 
           
         Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
         oPolicy.Name = 'test123';
         oPolicy.Group_Name__c = oAccount.Id;
         oPolicy.Funding_Type_Code__c = 'Y';
         oPolicy.Product__c = oProduct.Id;
         oPolicy.Issue_State__c = 'IL'; 
         insert oPolicy;
         System.assertEquals(oPolicy.Name, 'test123'); 
            
         Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
         oPolicyMem.Name = '12345';
         oPolicyMem.Member__c = mAccount.Id;
         oPolicyMem.Policy__c = oPolicy.Id;            
         oPolicyMem.Relationship__c = 'Self';
         insert oPolicyMem;
         System.assertEquals(oPolicyMem.Name, '12345'); 
         
         Member_ID__c oMemID = new Member_ID__c();  
         oMemID.Policy__c = oPolicy.id;
         oMemID.Policy_Member__c = oPolicyMem.id; 
         oMemID.Type__c = 'MedicareID';
         oMemID.Member_Card_External_ID__c = '1231';
         insert oMemID;  
         System.assertEquals(oMemID.Policy__c, oPolicy.id);  
         
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
         system.assertEquals(intentRec.Name == 'Filling', true);
         Account m1Account = [Select Id,Name from Account where Name = 'New Case Account'];
         Policy_Member__c o1PolicyMem = [Select Id,Name from Policy_Member__c where Name ='12345'];
         oCase = new Case(AccountId=m1Account.Id, Interacting_About_Type__c = 'Member', Origin = 'Inbound Call', 
                           Type = 'Customer Inquiry', Intent_Id__c = lstIntent[0].id, Classification_Id__c =lstClassification[0].id, 
                           Policy_Member__c= o1PolicyMem.Id, Interacting_With_Type__c = 'Member',
                           Interacting_With__c=m1Account.Id, Due_Date__c=Date.Today(), Classification_Type__c = 'Calls',
                           Priority = 'Normal', Line_of_Business__c = 'Commercial', G_A_Rights_Given__c = 'Yes', 
                           G_A_Reason__c = 'All Others', Complaint__c = 'Yes - Medicare Part C', Complaint_Reason__c = 'Clinical Programs',
                           Behavioral_Health__c = 'None');
         oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Member Case').getRecordTypeId();
       
         insert oCase;
         System.assertEquals(oCase.AccountId,m1Account.Id);
        
         List<HUMReferenceBusinessLineProductGroup__c> lstRBLP = new List<HUMReferenceBusinessLineProductGroup__c>();
         lstRBLP.add(new HUMReferenceBusinessLineProductGroup__c(Name='RSO- Medicare',Business_Description__c='RSO- Medicare', Major_LOB__c='MGR', ProductCode__c='MED'));
         insert lstRBLP;
         System.assertEquals(lstRBLP.size(), 1, 'Value is not matched');
       
        
         List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
         lstHUMConstants.add(new HUMConstants__c(Name='HUMConstantOne',StringValue__c='1',IntegerValue__c=1));
         lstHUMConstants.add(new HUMConstants__c(Name='HUMLimit1000',StringValue__c='1000',IntegerValue__c=1000));
         lstHUMConstants.add(new HUMConstants__c(Name='HUMRSOMedicare',StringValue__c='RSO- Medicare'));
         insert lstHUMConstants;
         System.assertEquals(lstHUMConstants.size(), 3);
       }
    }
        
    /**
    * Test method to get business description for PolicyMembers
    *<p>
    * @param    NULL
    * @return   void   
    */
    public static testMethod void testPMBusinessDescription()
    {
        User testUser = [Select Id,Name from User where LastName='test BusinessDescriptionUser1'];
        
        System.runAs(testUser)
        {
            Policy_Member__c objPolMem1 = [Select id, name from Policy_Member__c where Name = '12345'];
            String objPMId = objPolMem1.Id;
            Test.startTest();
            Boolean bCheck = HUMGetBusinessDescription.getPMBusinessDescription(objPMId);
            System.assertEquals(bCheck, false);
            Test.stopTest();
        }
    }
    
    /**
    * Test method to get business description for Cases
    *<p>
    * @param    NULL
    * @return   void   
    */
    public static testMethod void testCaseActionBusinessDescription()
    {
        User testUser = [Select Id,Name from User where LastName='test BusinessDescriptionUser1'];
        
        System.runAs(testUser)
        {   
            Case oCase1 = [Select id from case]; 
            String objcaseId = oCase1.Id;
            Test.startTest();
            String str = HUMGetBusinessDescription.getCaseLaunchBusinessDescription(objcaseId,null,false);
            System.assertEquals(str, '');
            Test.stopTest();
        }
    }
}