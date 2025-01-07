/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorney_LT_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This is the Test class for PowerOfAttorney_LC_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     10/15/2020                  Original Version
*    2.0           Ashish Kumar                                     13/05/2021                  Changed Policy_Member__c object references to MemberPlan Object references
*    3.0           Ashish Kumar                                     13/05/2021                  Removed policy object reference 
*    4.0           Ashish Kumar                                     28/09/2021                  Refactored code 
*******************************************************************************************************************************************************************/

@isTest
public class PowerOfAttorney_LT_HUM {
    
    private static Account oAcc;
    private static Account objAcc;
    
    /**
* Method to setup test data
* 
* @param    None
* @return   Void 
*/
    @testSetup
    static void setup() 
    {  
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        
        system.runAs(oUser)
        {   
            
            objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.Name = 'testacc';
            objAcc.Group_Number__c = '213654';
            insert objAcc;
            system.assertEquals(objAcc.Group_Number__c, '213654');
            
            oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'testacc1';
            oAcc.Group_Number__c = '213655';
            insert oAcc;
            system.assertEquals(oAcc.Group_Number__c, '213655');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR');
            insert oProd;
            System.assertEquals(oProd.Segment_Type__c, 'MCR');
            
            PurchaserPlan oPolicy = new PurchaserPlan(name = 'Test Policy VIS', PayerId = objAcc.Id, Policy_Group_Number__c='11111',Platform__c='LV');
            insert oPolicy;  
            system.assert(oPolicy.id != null);
            
            MemberPlan objPolMem1 = new MemberPlan();
            objPolMem1.Name = 'testpolmem';
            objPolMem1.Member_Id_Base__c = '';
            objPolMem1.MemberId = objAcc.Id;
            objPolMem1.Member_Dependent_Code__c = '01';
            objPolMem1.Member_Id_Base__c = 'H62753913';
            objPolMem1.PlanId = oPolicy.Id;
            objPolMem1.Plan = oPolicy;
            insert objPolMem1;
        }
    }
    
    
    /**
* Method to test POA Section - Positive Scenario
* 
* @param    None
* @return   Void 
*/
    static testMethod void testPOA()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
            
            test.startTest();
            
            Account oAccount = [Select Id from Account where Name = 'testacc' limit 1];
            
            PowerOfAttorney_LC_HUM oPOA = new PowerOfAttorney_LC_HUM();
        
            
            string lstPOAData='{"POAData":[{"DocumentType":"Power of Attorney","EffectiveDate":"10/1/2020","FirstName":"Aditi","GuardianAddress":"1239 KAST AVE, OWATONNA, MN, 55060","IndexNumber":8,"LastName":"Chowrasya","LevelOfConsent":"","PersonofAuthority":"Aditi Chowrasya","PhoneNumber":"5024761654","Relationship":"Parent","SubmissionMehtod":"Paper","TerminationDate":"3/30/2021","Type":"C"}]}';
            
            PowerOfAttorney_LC_HUM.processPOA(lstPOAData);
            
            
            PowerOfAttorney_LC_HUM.poaWrapper poaWrapper= new PowerOfAttorney_LC_HUM.poaWrapper();
            poaWrapper.FirstName='firstName';
            poaWrapper.LastName='lastName';
            poaWrapper.PhoneNumber='123456';
            poaWrapper.Type='XYZ';
            poaWrapper.TypeHelpText='HelpText';
            
            
            test.stopTest();
        }
    }
   }