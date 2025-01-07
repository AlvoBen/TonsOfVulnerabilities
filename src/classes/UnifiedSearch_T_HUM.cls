/*******************************************************************************************************************************
Apex Class Name : UnifiedSearch_C_HUM_T_HUM.cls
Version         : 1.0
Created On      : Jan, 28, 2021
Function        : Class contains test methods UnifiedSearch_C_HUM class.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                               01/28/2021                Original Version
********************************************************************************************************************************/
@isTest
private class UnifiedSearch_T_HUM {
    public UnifiedSearch_T_HUM() {

    }
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @TestSetup
    static void setUpTestData(){
            User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
            insert oCCSUser;
            system.assertEquals(oCCSUser.LastName,'test User1');
            system.runAs(oCCSUser)
            {
                Account objAcc = HUMTestDataHelperTest.getAccount(); 
                objAcc.FirstName = 'Test';
                objAcc.LastName = 'Account';
                objAcc.Tenant_Id__c = '00';
                objAcc.Enterprise_ID__c = '1005577525';
                objAcc.General_Account__c = true;
                objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
                insert objAcc;
                System.assertEquals(objAcc.Tenant_Id__c,'00');
                
                Interaction__C oInt = HUMTestDataHelperTest.getInteraction();
                oInt.Interacting_with_type__C = 'Member';
                oInt.Interacting_with__c = objAcc.Id;
                insert oInt ;
                system.assertEquals(oInt.id, [Select Id from Interaction__c where Interacting_With__c=:objAcc.Id LIMIT 1].Id);
             
                
                Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER',Product_Type__c  = 'MED');
                insert oProd;
                System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
                
                Policy__c objpol = HUMTestDataHelperTest.getPolicy();
                objpol.Name='testPolicy';
                objpol.Group_Name__c = objAcc.Id;
                objpol.Major_LOB__c = 'MER';
                objpol.Source_Cust_Cov_Key__c = '123456789';
                objpol.Product__c = oProd.Id;
                objpol.Product__r = oProd;            
                objpol.Platform__c = 'EM';
                objpol.Benefit_Coverage__c = '1234';               
                insert objpol;
                system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
                
                Policy_Member__c objPolMem = new Policy_Member__c();
                objPolMem.Name = 'Policy Member PM1';
                objPolMem.Relationship__c = 'Self';
                objPolMem.Subscriber__c = objPolMem.Id;
                objPolMem.Effective_Date__c='12/01/2015';
                objPolMem.End_Date__c='11/16/2016';
                objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
                objPolMem.Member__c = objAcc.Id;
                objPolMem.Policy__c = objpol.Id;
                objPolMem.Policy__r = objpol;                
                insert objPolMem;
                
                 Classification__c classificationRecDec = new Classification__c();
                classificationRecDec.Name = 'Affidavit';
               
                classificationRecDec.Is_Active__c = true;
                insert classificationRecDec;
                system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
                
                
                Intent__c intentRec = new Intent__c();
                intentRec.Name = 'New';
               
                intentRec.Is_Active__c = true;
                insert intentRec;
                system.assertEquals(intentRec.name == 'New',true);
                
                Case caseObj = HUMTestDataHelperTest.getCase();
                caseObj.Interacting_With__c = objAcc.Id;
                caseObj.Interacting_With_Name__c = 'Test Name';
                caseObj.Classification_ID__c = classificationRecDec.ID;
                caseObj.Intent_ID__c  = intentRec.id;
                caseobj.Interacting_With_Type__c = 'Group';
                insert caseObj ;
                System.assertEquals(caseobj.Interacting_With_Type__c == 'Group', true);
                
                
                 Case_Interaction__c cc1= new Case_Interaction__c(Case__c = caseobj.Id, Interaction__c=oInt.Id);
                insert cc1;
                system.assertNotEquals(cc1,null,'Value not matched');
                List<PermissionSet> ps = [SELECT Id FROM PermissionSet WHERE Name = 'CRMS_310_UnifiedDocumentSearch' limit 1];
                if(ps != null && ps.size() > 0)
                {
                    PermissionSetAssignment psa = new PermissionSetAssignment();
					psa.AssigneeId = oCCSUser.Id;
					psa.PermissionSetId = ps[0].Id;
					insert psa;
                }
				
            }
            
    }

    /*
* Method Name : testsearch
* Description : This method is used to test the unified coveo search funtionality
* Return type : None
* Parameter   : None
*/

    

    @IsTest
    static void testcreatecoveomessage(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        System.runAs(testUser){
            Test.startTest();
            List<policy_Member__c > lstMemId =[SELECT Id, Name, Policy__r.Benefit_Coverage__c,  Policy__r.Major_LOB__c, Product__c ,
                                             Product_Type_Code__c, Product_Type__c, policy_platform__c, member__r.firstname, member__r.lastname
                                            FROM policy_Member__c 
                                             WHERE Name = 'Policy Member PM1' LIMIT 1];
            UnifiedSearch_C_HUM objcontroller = new UnifiedSearch_C_HUM();
            objcontroller.sPolicyMemberId = lstMemId[0].Id;
            objcontroller.sPageName = 'Claim Details';
            List<Interaction__c> lstInts = [select Id from interaction__c where interacting_with__r.enterprise_id__c = '1005577525' limit 1];
            if(lstInts.size() > 0)
            {
                objcontroller.sInteractionId = lstInts[0].Id;
            }
            
            List<Case> lstCases = [select Id from Case where Interacting_With__r.Enterprise_Id__c = '1005577525' limit 1];
            if(lstCases.size() > 0)
            {
                objcontroller.sCaseId = lstCases[0].Id;
            }
            objcontroller.createCoveoMessage();
            system.assertNotEquals('', objcontroller.sCoveoMessage);
        }
        Test.stopTest();
    }
    
    @IsTest
    static void testcreatecoveomessage1()
    {
         User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        System.runAs(testUser){
            Test.startTest();
            List<policy_Member__c > lstMemId =[SELECT Id, Name, Policy__r.Benefit_Coverage__c,  Policy__r.Major_LOB__c, Product__c ,
                                             Product_Type_Code__c, Product_Type__c, policy_platform__c, member__r.firstname, member__r.lastname
                                            FROM policy_Member__c 
                                             WHERE Name = 'Policy Member PM1' LIMIT 1];
            UnifiedSearch_C_HUM objcontroller = new UnifiedSearch_C_HUM();
            objcontroller.sPolicyMemberId = lstMemId[0].Id;
            objcontroller.sPageName = 'Claim Details';
            List<Interaction__c> lstInts = [select Id from interaction__c where interacting_with__r.enterprise_id__c = '1005577525' limit 1];
            if(lstInts.size() > 0)
            {
                objcontroller.sInteractionId = lstInts[0].Id;
            }
            objcontroller.sCaseId = null;
            objcontroller.createCoveoMessage();
            system.assertNotEquals('', objcontroller.sCoveoMessage);
        }
    }
    
    @IsTest
    static void testcontructor(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        System.runAs(testUser){
            Test.startTest();
            UnifiedSearch_C_HUM objcontroller = new UnifiedSearch_C_HUM();
            objcontroller.sPageNameToGet = 'Test';
            system.assertNotEquals('', objcontroller.sPageNameToGet);
            objcontroller.sMemberFirstName = 'Test';
            system.assertNotEquals('', objcontroller.sMemberFirstName);
            objcontroller.sMemberLastName = 'Test';
            system.assertNotEquals('', objcontroller.sMemberLastName);
            objcontroller.sUnifiedSearchJSON = 'Test';
            system.assertNotEquals('', objcontroller.sUnifiedSearchJSON);
            //System.assertEquals(false, objcontroller.bCoveoSearchUser);
        }
        Test.stopTest();
    }

    @IsTest
    static void testdecodeTabLink()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        
        System.runAs(testUser){
            Test.startTest();
            UnifiedSearch_C_HUM objcontroller = new UnifiedSearch_C_HUM();
            objcontroller.sTabURL = 'Hello';
            objcontroller.decodeTabLink();
            System.assertNotEquals('', objcontroller.sDecodedTabUrl);
        }
        Test.stopTest();
    }
}