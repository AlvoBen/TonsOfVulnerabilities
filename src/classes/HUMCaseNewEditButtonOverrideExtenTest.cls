/****************************************************************************
Apex Class Name  : HUMCaseNewEditButtonOverrideExtenTest
Version          : 1.0
Created On       : JULY 15 2014
Function         : Test class to HUMCaseNewEditButtonOverrideExtension Apex class

Modification Log :
* Developer Name                  Code Review                      Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------
* Suryakumari M                     17093                       15/07/2014             Original Version
* Suryakumari Medicherla            19335                       18/11/2014             Code modified to avoid test failure
* Suryakumari Medicherla            20579                       28/01/2015             Code modified to avoid test failure
* Avinash Choubey                   29247                       01/25/2016             Added Test Setup method
* Avinash Choubey                   33344                       07/11/2016             Changes made as part of REQ- 255151
* Pradeepkumar Dani                 99570                       06/08/2017             Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani                 100996                      06/13/2017             Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
* Satyam Malviya                    187433                      10/23/2017             Code Modified as a part of Req 333803
* Satyam Malviya                    239709                      06/07/2018             SF - Tech Case Edit Rollback
* Santhi Mandava                                                03/12/2019             Removed "DpaaSWebServiceMockImpl" usage.
* Sunil Kumar Reddy Y                                           09/06/2021             Code Coverage Improvement
* Vamshi Krishna Pemberthi                        06/08/2021        Code Coverage Improvements
* Vamshi Krishna Pemberthi                        06/22/2021        Code Coverage Improvements & Error Fix
**********************************************************************************************************************************/

@isTest
private class HUMCaseNewEditButtonOverrideExtenTest
{

    /**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setUpTestData()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        
        //check forEdit Exception
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oUser1;

        system.assertEquals(oUser.lastname == 'test User1',true);       
        system.runAs(oUser)
        {
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Case Account';
            oAccount.Group_Number__c  = '123';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAccount;
            System.assertEquals(oAccount.BillingState == 'New York', true);
            
            Product2 oProduct = HUMTestDataHelperTest.getProduct();
            oProduct.Name='Mediclaim';
            insert oProduct;
            system.assertEquals(oProduct.name == 'Mediclaim', true);
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'test123';
            oPolicy.Group_Name__c = oAccount.Id;
            oPolicy.Funding_Type_Code__c = 'Y';
            oPolicy.Product__c = oProduct.Id;
            oPolicy.Issue_State__c = 'IL'; 
            insert oPolicy;
            system.assertEquals(oPolicy.Group_Name__c == oAccount.Id, true);
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = '12345';
            oPolicyMem.Member__c = oAccount.Id;
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';
            insert oPolicyMem;
            system.assertEquals(oPolicyMem.Member__c == oAccount.Id, true);                        
            
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Claims';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Claims', true);
            
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'Filling', true);
            
            Case oCase = new Case(AccountId = oAccount.Id, Intent_ID__c = intentRec.id, Classification_ID__c = classificationRecDec.id, Policy_Member__c = oPolicyMem.Id ,
                             Interacting_With_Type__c = 'Member',Interacting_With__c= oAccount.Id, Due_Date__c =Date.Today(), 
                             Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c = 'Cincinnati CRU', Work_Queue_View_Name__c = 'Cincinati CRU', Service_Center__c = 'Cincinnati', Department__c = 'CRU', status = 'In Progress');
            oCase.RecordTypeId = Schema.sObjectType.Case.getRecordTypeInfosByName().get('Group Case').getRecordTypeId();
            insert oCase;
                                    
            System.assertEquals(oCase.Interacting_With_Type__c == 'Member', true);            
            
        }
    }
      
    /**
    * This method is used to create case from Account Related List 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void createCase() 
    {
        User oUser = [select id, name from user where lastname='test User1' limit 1];
        system.assert(oUser.Id!= null); 
        system.runAs(oUser)
        {
            Account oAccount = [select id,name from account where name ='New Case Account' limit 1];
            Product2 oProduct = [select id, name from Product2 where name= 'Mediclaim'  limit 1];
            Policy__c oPolicy = [select id, name from Policy__c where name= 'test123'  limit 1];
            Policy_Member__c oPolicyMem = [select id, name from Policy_Member__c where name= '12345'  limit 1];
            Case oCase = [select id ,RecordTypeId from case  where Policy_Member__c= :oPolicyMem.id  limit 1];                        
            
            Test.startTest();
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension = new  HUMCaseNewEditButtonOverrideExtension(oStdController); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, oCase.RecordTypeId ); 
            PageReference oPageRef = oEextension.reDirectPage();
            System.assert(oPageRef != null);
            
            //check for exception
            
            ApexPages.StandardController oStdController1 = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension1 = new  HUMCaseNewEditButtonOverrideExtension(oStdController1); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, null ); 
            PageReference oPageRef1 = oEextension1.reDirectPage();
                                   
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, oCase.RecordTypeId ); 
            oPageRef = oEextension.reDirectPage();
            System.assert(oPageRef != null);
            
            
            User oLoggedUser = [ select Id from User where Id =: UserInfo.getUserId()];
            System.runAs(oLoggedUser)
            {
                ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, oCase.RecordTypeId ); 
                oPageRef = oEextension.reDirectPage();
                System.assert(oPageRef != null);
            }
            //check for exception
            User oLoggedUser1 = [ select Id from User where lastname = 'test User2'];
            System.runAs(oLoggedUser1)
            {
                ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, oCase.RecordTypeId ); 
                oPageRef1 = oEextension.reDirectPage();
                //System.assert(oPageRef1 != null);
            }
            Test.stopTest();
        } 
    }
    
    /**
    * This method is used to edit case 
    *
    * @param  None      
    * @return  Void
    */
    private static testMethod void editCase() 
    {     
        User oUser = [select id, name from user where lastname='test User1' limit 1];
        system.runAs(oUser)
        {
            Account oAccount = [select id, name from account where name ='New Case Account' limit 1];
            Product2 oProduct = [select id, name from Product2 where name= 'Mediclaim'  limit 1];
            Policy__c oPolicy = [select id, name from Policy__c where name= 'test123'  limit 1];
            Policy_Member__c oPolicyMem = [select id, name from Policy_Member__c where name= '12345'  limit 1];
            Case oCase = [select id,RecordTypeId  from case  where Policy_Member__c= :oPolicyMem.id  limit 1];                      
  
            Test.startTest();
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension = new  HUMCaseNewEditButtonOverrideExtension(oStdController); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_CASEHISTORY);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            PageReference oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef != null);                        
            
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_ACCOUNTRELLIST);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef != null);
            
            
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, 'Edit');
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef != null);
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;
            System.assert(oUser.current_queue__c == 'queue1');                      
            
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMRecordType, oCase.RecordTypeId ); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, 'Edit');
            oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef != null);
            
            ApexPages.currentPage().getParameters().put('isHP','true' ); 
            ApexPages.currentPage().getParameters().put('isMedicaidPARProvider', 'true'); 
            ApexPages.currentPage().getParameters().put('isQAA', 'true');
            ApexPages.currentPage().getParameters().put('isPCP', 'true'); 
            ApexPages.currentPage().getParameters().put('isGoodCause', 'true');
            ApexPages.currentPage().getParameters().put('isOGO', 'true');
            oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef != null);
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to edit case Exception Test 
    *
    * @param  None      
    * @return  Void
    */
    private static testMethod void editCaseException() 
    {     
        User oUser = [select id, name from user where lastname='test User1' limit 1];

        
       

        system.runAs(oUser)
        {
            Account oAccount = [select id, name from account where name ='New Case Account' limit 1];
            Product2 oProduct = [select id, name from Product2 where name= 'Mediclaim'  limit 1];
            Policy__c oPolicy = [select id, name from Policy__c where name= 'test123'  limit 1];
            Policy_Member__c oPolicyMem = [select id, name from Policy_Member__c where name= '12345'  limit 1];
            Case oCase = [select id,RecordTypeId from case where Policy_Member__c= :oPolicyMem.id  limit 1]; 
                                 
            
            //HUMConstants hcon = new HUMConstants();

  
            Test.startTest();            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension = new  HUMCaseNewEditButtonOverrideExtension(oStdController); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, null); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_CASEHISTORY);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            PageReference oPageRef = oEextension.reDirectPageForEdit();
            System.assert(oPageRef == null);  
           

            oUser.current_queue__c = 'queue1' ;
            update oUser;

            System.assert(oUser.current_queue__c == 'queue1');
            ApexPages.StandardController oStdController1 = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension1 = new  HUMCaseNewEditButtonOverrideExtension(oStdController1); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_CASEHISTORY);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, 'abbb');
            PageReference oPageRef1 = oEextension1.reDirectPageForEdit();

            Test.stopTest();
        }
    }

    private static testMethod void editCaseException1() 
    {     
        User oUser = [select id, name from user where lastname='test User1' limit 1];

        system.runAs(oUser)
        {
            Account oAccount = [select id, name,RecordTypeId from account where name ='New Case Account' limit 1];
            Product2 oProduct = [select id, name from Product2 where name= 'Mediclaim'  limit 1];
            Policy__c oPolicy = [select id, name from Policy__c where name= 'test123'  limit 1];
            Policy_Member__c oPolicyMem = [select id, name from Policy_Member__c where name= '12345'  limit 1];
            Case oCase = [select id,RecordTypeId from case where Policy_Member__c= :oPolicyMem.id  limit 1]; 
                                 
            
            //HUMConstants hcon = new HUMConstants();
            Test.startTest();
            
            ApexPages.StandardController oStdController2 = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension2 = new  HUMCaseNewEditButtonOverrideExtension(oStdController2); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_CASEHISTORY);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            PageReference oPageRef2 = oEextension2.reDirectPageForEdit();
            
            oUser.current_queue__c = 'queue1' ;
            update oUser;

            System.assert(oUser.current_queue__c == 'queue1');
            ApexPages.StandardController oStdController1 = new ApexPages.StandardController(oCase);
            HUMCaseNewEditButtonOverrideExtension oEextension1 = new  HUMCaseNewEditButtonOverrideExtension(oStdController1); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMId, oCase.id); 
            ApexPages.currentPage().getParameters().put(HUMConstants.HUM_CASEEDIT_PAGEFROM, HUMConstants.HUM_CASEEDIT_CASEHISTORY);
            ApexPages.currentPage().getParameters().put(HUMConstants.HUMcaseRecordType, oCase.RecordTypeId);
            PageReference oPageRef1 = oEextension1.reDirectPageForEdit();

            Test.stopTest();
        }
    }
}