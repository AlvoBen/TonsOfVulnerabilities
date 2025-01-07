/**************************************************************************** 
Apex Class Name  : CaseArticleExtension_T_HUM
Version          : 1.0  
Created Date     : September 9 ,2017
Function         : test class for CaseArticleExtension_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasad Jandhyala        149162               09/12/2017                   Original Version
* Pradeepkumar Dani       149162               11/17/2017                      re-factoring
* Rakesh Patni			  350876			   04/11/2019					Commented References to Article Types before Migration
* Rakesh Patni			  351706			   04/15/2019					Updated Article Types to Knowledge__Kav post Migration
* Santhi Mandava                               08/28/2020          			Fixed code coverage issue
* Mithra Bharadwaj 									improved code coverage after code changes for Long running components issue
***************************************************************************************************/

@isTest
private class CaseArticleExtension_T_HUM
{
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('CaseArtUser','Customer Care Supervisor');
        objUser.UserPermissionsKnowledgeUser = true;
        insert objUser;
        system.assertEquals(objUser.lastname=='test CaseArtUser',true);
        Case caseRecord;
        system.runAs(objUser)
        {
            // Insert Member Account
            Account objMemAccount = HUMTestDataHelperTest.getAccount();
            objMemAccount.FirstName = 'Test FName';
            objMemAccount.LastName = 'LName'; 
            objMemAccount.source_platform_code__c = 'EM' ;
            objMemAccount.Enterprise_Id__c = String.valueof(2000);
            objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccount;
            system.assert(objMemAccount.LastName == 'LName');
            
            // Insert Member Account
            Account objMemAccountLV = HUMTestDataHelperTest.getAccount();
            objMemAccountLV.FirstName = 'Test FName';
            objMemAccountLV.LastName = 'LName2'; 
            objMemAccountLV.source_platform_code__c = 'LV' ;
            objMemAccountLV.Enterprise_Id__c = String.valueof(2001);
            objMemAccountLV.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objMemAccountLV.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
            insert objMemAccountLV;
            system.assert(objMemAccountLV.LastName == 'LName2');
            
            Account objGrpAccount = HUMTestDataHelperTest.getAccount();
            objGrpAccount.Name = 'New Case Account';
            objGrpAccount.BillingState = 'New York';
            objGrpAccount.Enterprise_Id__c = String.valueof(4000);
            objGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert objGrpAccount;
            system.assert(objGrpAccount.BillingState == 'New York');
            
            /*********************************** Medical *************************************/
            //Medical Product
            Product2 objMedProduct = new Product2();
            objMedProduct.Name ='MED';
            objMedProduct.Product_Type__c = 'Med';
            objMedProduct.productCode = 'MED';
            insert objMedProduct;
            system.assert(objMedProduct.Product_Type__c == 'Med');
            
            //Medical Policy
            Policy__c oMedicalPolicy = HUMTestDataHelperTest.getPolicy();
            oMedicalPolicy.Name = 'Medical Product';
            oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
            oMedicalPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Product__c = objMedProduct.Id;  
            oMedicalPolicy.Major_LOB__c ='EPO';          
            insert oMedicalPolicy;
            system.assert(oMedicalPolicy.Name == 'Medical Product');
            
            //Medical Policy Member
            Policy_Member__c objMedPolMem = HUMTestDataHelperTest.getPolicyMember();
            objMedPolMem.Member__c = objMemAccount.Id;
            objMedPolMem.Policy__c = oMedicalPolicy.Id;
            objMedPolMem.Relationship__c = 'self';    
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';       
            objMedPolMem.Policy_Member_External_ID__c = '00|ODS|123456789|89|89|02-01-2001';
            insert objMedPolMem;
            system.assert(objMedPolMem.Relationship__c == 'self');
            
            /*********************************** Dental *************************************/
            //Dental Product
            Product2 objDenProduct = new Product2();
            objDenProduct.Name ='DEN';
            objDenProduct.productCode = 'MED';
            objDenProduct.Product_Type__c = 'Den';
            insert objDenProduct;
            system.assert(objDenProduct.Product_Type__c == 'Den');
            
            //Dental Policy
            Policy__c oDentalPolicy = HUMTestDataHelperTest.getPolicy();
            oDentalPolicy.Name = 'Dental Product';
            oDentalPolicy.source_cust_cov_key__c = '12345678991234567899' ;
            oDentalPolicy.Group_Name__c = objMemAccount.Id;
            oDentalPolicy.Major_LOB__c ='EPO'; 
            oDentalPolicy.Product__c = objDenProduct.Id;   
            oDentalPolicy.Platform__c = 'LV';         
            insert oDentalPolicy;
            system.assert(oDentalPolicy.Name == 'Dental Product');
            
            //Dental Policy Member
            Policy_Member__c objDenPolMem = HUMTestDataHelperTest.getPolicyMember();
            objDenPolMem.Member__c = objMemAccount.Id;
            objDenPolMem.Policy__c = oDentalPolicy.Id;
            objDenPolMem.Relationship__c = 'self'; 
            objMedPolMem.end_date__c = '10/10/2016'; 
            objMedPolMem.Effective_Date__c='01/01/2016';
            objDenPolMem.name = 'TestDummydata';          
            objDenPolMem.Policy_Member_External_ID__c = '00|ODS|123456788|89|89|02-01-2001';
            insert objDenPolMem;
            system.assert(objDenPolMem.Relationship__c == 'self');
            
            /*********************************** Non Medical-Dental *************************************/
            // Non Medical - Dental Product     
            Product2 objProduct = new Product2();
            objProduct.Name ='MED';
            objProduct.Product_Type__c = 'test';
            insert objProduct;
            system.assert(objProduct.Product_Type__c == 'test');
            
            // Non Medical - Dental Policy   
            Policy__c objPolicy = HUMTestDataHelperTest.getPolicy();
            objPolicy.Name = 'Test Policy';
            objPolicy.source_cust_cov_key__c = '12345678911234567891' ;
            objPolicy.Group_Name__c = objMemAccount.Id;
            oMedicalPolicy.Major_LOB__c ='EPO';
            objPolicy.Product__c = objProduct.Id;            
            insert objPolicy;
            system.assert(objPolicy.Name == 'Test Policy');
            
            //Non Medical - Dental Policy Member
            Policy_Member__c objPolMem = HUMTestDataHelperTest.getPolicyMember();
            objPolMem.Member__c = objMemAccount.Id;
            objPolMem.Policy__c = objPolicy.Id;
            objPolMem.Relationship__c = 'self'; 
            objPolMem.end_date__c = '01/01/2013';
            objPolMem.name = '12345';                  
            objPolMem.Policy_Member_External_ID__c = '00|ODS|123456787|89|89|02-01-2001';
            insert objPolMem;
            system.assert(objPolMem.Relationship__c == 'self');
            
            /*********************************** Non Medical-Dental - LV *************************************/
            // Non Medical - Dental Product     
            Product2 objProductLV = new Product2();
            objProductLV.Name ='MED';
            objMedProduct.productCode = 'MED';
            objProductLV.Product_Type__c = 'test';
            insert objProductLV;
            system.assert(objProductLV.Product_Type__c == 'test');
            
            // Non Medical - Dental Policy   
            Policy__c objPolicyLV = HUMTestDataHelperTest.getPolicy();
            objPolicyLV.Name = 'Test Policy';
            oMedicalPolicy.Major_LOB__c ='EPO';
            objPolicyLV.source_cust_cov_key__c = '12345678911234567891' ;
            objPolicyLV.Group_Name__c = objMemAccountLV.Id;
            objPolicyLV.Product__c = objProductLV.Id;            
            insert objPolicyLV;
            system.assert(objPolicyLV.Name == 'Test Policy');
            
            //Non Medical - Dental Policy Member
            Policy_Member__c objPolMemLV = HUMTestDataHelperTest.getPolicyMember();
            objPolMemLV.Member__c = objMemAccountLV.Id;
            objPolMemLV.Policy__c = objPolicyLV.Id;
            objPolMemLV.Relationship__c = 'self'; 
            objPolMemLV.name = '12591';
            objMedPolMem.end_date__c = '12/31/2015'; 
            objMedPolMem.Effective_Date__c='01/01/2015';               
            objPolMemLV.Policy_Member_External_ID__c = '00|ODS|123456785|89|89|02-01-2001';
            insert objPolMemLV;
            system.assert(objPolMemLV.Relationship__c == 'self'); 
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Benefits';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Humana Pharmacy';
            classificationRecDec.Is_Active__c = true;
            lstClassification.add(classificationRecDec);
            insert lstClassification;
            
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRecDec = new Intent__c();
            intentRecDec.Name = 'Estimate';
            intentRecDec.Is_Active__c = true;
            lstIntent.add(intentRecDec);
            insert lstIntent;
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Guidance_Alert_Limit__c = 2;
            oClassType.Is_Active__c = true;
            insert oClassType;
            
            CTCI_Junction__c oCTCIRec = new CTCI_Junction__c();
            oCTCIRec.Classification__c = lstClassification[0].id;
            oCTCIRec.Intent__c = lstIntent[0].id;
            oCTCIRec.Classification_Type__c = oClassType.id;
            oCTCIRec.IsActive__c = true;
            insert oCTCIRec;
            system.assertEquals(oCTCIRec.IsActive__c == true, true); 
            
            caseRecord = new Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = objMemAccount.id;
            caseRecord.ownerId = objUser.Id;
            caseRecord.Interacting_With__c = objMemAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Policy_Member__c = objPolMemLV.Id;
            caseRecord.CTCI_List__c = oCTCIRec.Id;
            caseRecord.Type ='MHK Appeal Task';
            insert caseRecord;
            
            list<CaseComment> lstCC = new list<CaseComment>();
            CaseComment oCC1 = new CaseComment(ParentId = caseRecord.Id, CommentBody = 'TEST 1');
            lstCC.add(oCC1);
            CaseComment oCC2 = new CaseComment(ParentId = caseRecord.Id, CommentBody = 'TEST 2');
            lstCC.add(oCC2);
            insert lstCC;
            system.assert(lstCC[0].CommentBody == 'TEST 1');
            PermissionSet ps = [SELECT Id FROM PermissionSet WHERE Name = 'Knowledge_Team_Lead'];
            PermissionSetAssignment psa = new PermissionSetAssignment();
            psa.AssigneeId = objUser.Id;
            psa.PermissionSetId = ps.Id;
            insert psa;
        }  
        Knowledge__Kav con = HUMTestDataHelperTest.getKnowledgeArticle();
        insert con;

        con = [Select id, knowledgeArticleId from Knowledge__Kav where id = :con.id];

        CaseArticle ca = new CaseArticle();
        ca.caseId = caseRecord.id;
        ca.knowledgeArticleId = con.knowledgeArticleId;
        insert ca;
    }  
    
    /*
    * Method Name : testcomments
    * Description : This method Is used To test the Controller
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testArticles()
    {
        
        User testUser = [Select Id,Name from User where LastName='test CaseArtUser'];
        System.assertEquals(testUser.Name,'test CaseArtUser');
        Case CaseRec = [select id,type from Case where status='In Progress' and Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
        List<CaseArticle> caseArticlesList = [SELECT Id, CaseId FROM CaseArticle where caseId = :CaseRec.Id];
        System.runAs(testUser)
        {
            Test.startTest();
        
            
            CaseArticleExtension_C_HUM cArt= new CaseArticleExtension_C_HUM();           
            cArt.columnName='test1';
            cArt.columnHeader='test2';
            CaseArticleExtension_C_HUM.getCaseArticles(CaseRec.Id);
            if(caseArticlesList != Null && !caseArticlesList.isEmpty()) CaseArticleExtension_C_HUM.updatedCaseArticles(CaseRec.Id,caseArticlesList[0].Id);
            Test.stopTest();
        }
    }
}