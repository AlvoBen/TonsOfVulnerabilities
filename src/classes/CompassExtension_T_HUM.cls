/*****************************************************************************************************
Apex Class Name  	: CompassExtension_T_HUM
Version   			: 1.0 
Created Date  		: 07/23/2017
Function  			: Controller to fetch compass related to cases
Function         	: Test class for CompassExtension_C_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande            149213             09/12/2017                   Original Version
* Pradeepkumar Dani			149213		       11/17/2017				     re-factoring
* Moshitha Gunasekaran                         02/02/2021                   US#1921246 - Sync CPU Time Limit Analysis - Optimized the code to improve the performance
*******************************************************************************************************/
@isTest
private with sharing class CompassExtension_T_HUM
{
    /*
	* This method creates test data for test class
	* @param None
	* @return None
	*/
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('CompassUser1','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname=='test CompassUser1',true);
        system.runAs(testUser)
        { 
            
            //Group Account
            Id rtId = [SELECT Id FROM RecordType WHERE IsPersonType = TRUE AND SObjectType='Account' LIMIT 1].Id;
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'testGroup';
            oAcc.LastName = 'accountGroup';
            oAcc.Enterprise_ID__c = '123456';
            oAcc.recordTypeId = rtId;
            insert oAcc;
            System.assert(oAcc.LastName == 'accountGroup');
            //Member Account
            Account MembAcc = HUMTestDataHelperTest.getAccount();
            MembAcc.FirstName = 'testMember';
            MembAcc.LastName = 'accountMember';
            MembAcc.Enterprise_ID__c = '123456789';
            MembAcc.ETL_Record_Deleted__c = true;
            MembAcc.recordTypeId = rtId;
            insert MembAcc;
            System.assert(MembAcc.LastName == 'accountMember');
            list<Product2> listProduct = new List<Product2>();
             Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
             listProduct.Add(testProduct);
             Insert listProduct;
             system.assertNotEquals(listProduct, null , 'Products not created');
             
                          
             list<Policy__c> listPolicy = new List<Policy__c>();             
             Policy__c testPolicy = new Policy__c(Group_Name__c = oAcc.ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
              listPolicy.Add(testPolicy);
              insert listPolicy; 
             system.assertNotEquals(listPolicy, null , 'Policies not created');
             
             List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
             Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = MembAcc.ID , ETL_Record_Deleted__c = true, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
            listPolicyMember.Add(memeber);
            Insert listPolicyMember ;
             system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
             
             Case caseObj1 = HUMTestDataHelperTest.getCase();
            caseObj1.AccountId = MembAcc.Id;
            caseObj1.Interacting_With__c = MembAcc.Id;
            caseObj1.Interacting_With_Name__c = 'Test Name';
            caseObj1.Policy_Member__c = listPolicyMember[0].ID;
            caseobj1.Interacting_With_Type__c = 'Member';
            insert caseObj1 ;
             
            List<Compass__c> cmpsList = new List <Compass__c>();
            Compass__c cc1= new Compass__c(Case__c = caseObj1.Id, Document_Name__c= 'TEST COmpass');
            cmpsList.add(cc1);
            Compass__c cc2 = new Compass__c(Case__c = caseObj1.Id, Document_Name__c= 'TEST COmpass 2');
            cmpsList.add(cc2);
            insert cmpsList;
            system.assert(cmpsList[0].Document_Name__c== 'TEST COmpass');
        }
    }  
    
    /*
	* Method Name : testcomments
	* Description : This method Is used To test the Controller
	* Return type : None
	* Parameter   : None
	*/
    Static testMethod void testcompass()
    {
        
        User testUser = [Select Id,Name from User where LastName='test CompassUser1'];
        System.assertEquals(testUser.Name,'test CompassUser1');
        System.runAs(testUser)
        {
            Test.startTest();
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            CompassExtension_C_HUM.getCaseCompassData(caseObj.Id);
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testverifylegacydelete
    * Description : This method Is used To test the Controller
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testverifylegacydelete()
    {
        
        User testUser = [Select Id,Name from User where LastName='test CompassUser1'];
        System.assertEquals(testUser.Name,'test CompassUser1');
        System.runAs(testUser)
        {
            Test.startTest();
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            ApexPages.currentpage().getParameters().put('Id',caseObj.Id);
            CompassExtension_C_HUM compass = new CompassExtension_C_HUM();
            compass.verifyLegacyDelete();
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : verifyMedicareCase
    * Description : This method Is used To test the Controller
    * Return type : None
    * Parameter   : None
    */
    static testMethod void verifyMedicareCase()
    {
        
        User testUser = [Select Id,Name from User where LastName='test CompassUser1'];
        System.assertEquals(testUser.Name,'test CompassUser1');
        System.runAs(testUser)
        {
            Test.startTest();
            Case caseObj = [SELECT Id FROM Case LIMIT 1];
            ApexPages.currentpage().getParameters().put('Id',caseObj.Id);
            CompassExtension_C_HUM compass = new CompassExtension_C_HUM();
            compass.verifyMedicareCase();
            Test.stopTest();
        }
    }
}