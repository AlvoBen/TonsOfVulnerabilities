/*********************************************************************************************************************************
Apex Class Name  : PharmacyCaseHistoryComponent_T_HUM
Version          : 1.0 
Created Date     : 01/18/2017 
Function         : Test class for PharmacyPharmacyCaseHistoryComponent_C_HUM.
Modification Log :
 *   Developer               Code Review            Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------           
 * Rajesh Chandgothia          59331                01/18/2017           Original Version.
 * Santhi Mandava                                   03/12/2019           Removed "DpaaSWebServiceMockImpl" usage.
  ********************************************************************************************************************************/ 
@isTest
private class PharmacyCaseHistoryComponent_T_HUM
{
     /*
         * Method Name : setup
         * Description : Create common test Data
         * Return type : void
         * Paramater   : NA
     */ 
      @testSetup static void setup() 
      {
           Test.loadData(HUMConstants__c.sObjectType,'HUMConsTestData');
           //loading HUMConstants Custom Settings data
           HUMTestDataHelperTest.getHUMConstantsData();
           User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
           u.Current_Queue__c = 'Cincinnati CRU';
           u.Network_User_Id__c = 'SXS2222';
           insert u;
           RecordType oRT = [select id from RecordType where sObjectType='Account' and Name='Group'];
           RecordType oRT1 = [select id from RecordType where sObjectType='Account' and Name='Member' limit 1];
           system.assertEquals(u.Id != Null,true);
           system.assertEquals(u.Lastname == 'test User1',true);
           System.runAs(u)
           {
               Work_Queue_Setup__c oQSetup = new Work_Queue_Setup__c(Name='Cincinnati CRU');
                insert oQSetup ;
                system.assertNotEquals(Null , oQSetup.id);
               
                //Creating Account Record
                Account acc = HUMTestDataHelperTest.getAccount();
                acc.Name = 'Test Account Cases';
                acc.RecordTypeId = oRT.Id;
                insert acc;
                system.assertEquals(acc.Id != Null,true);
                system.assertEquals(acc.Name == 'Test Account Cases',true);
                
                //Create List of Cases
                List<Case> csList = new List<Case>();
                for(Integer i=0;i<20;i++)
                {
                    Case cs = HUMTestDataHelperTest.getCase();
                    cs.Subject = 'Case Subject'+ i;
                    cs.AccountId = acc.Id;
                    cs.OwnerId = u.Id;
                    csList.add(cs);
                }
                //insert list of Cases
                insert csList;
                system.assertEquals(csList.Size() == 20,true);
                system.assertEquals(csList[1].Subject == 'Case Subject1',true);
                system.assertEquals(csList[19].Subject == 'Case Subject19',true);
                
                Case cs111 = HUMTestDataHelperTest.getCase();
                cs111.Subject = 'SubjectCase 111';
                cs111.AccountId = acc.Id;
                cs111.OwnerId = u.Id;
                insert cs111;
                system.assertEquals(cs111.Id != Null,true);
                system.assertEquals(cs111.Subject == 'SubjectCase 111',true);
                
                Case cs222 = HUMTestDataHelperTest.getCase();
                cs222.Subject = 'SubjectCase 222';
                cs222.AccountId = acc.Id;
                cs222.OwnerId = u.Id;
                insert cs222;
                system.assertEquals(cs222.Id != Null,true);
                system.assertEquals(cs222.Subject == 'SubjectCase 222',true);
                
                Case cs333 = HUMTestDataHelperTest.getCase();
                cs333.Subject = 'SubjectCase 333';
                cs333.AccountId = acc.Id;
                cs333.OwnerId = u.Id;
                insert cs333;    
                system.assertEquals(cs333.Id != Null,true);
                system.assertEquals(cs333.Subject == 'SubjectCase 333',true);
                   
                Case_Junction__c objCaseJ1 = new Case_Junction__c();
                objCaseJ1.IsLinked__c = true;
                objCaseJ1.Case__c = cs333.Id;
                objCaseJ1.Case_Master__c = cs111.Id;
                insert objCaseJ1;
                system.assertEquals(objCaseJ1.Id != Null,true);
                system.assertEquals(objCaseJ1.IsLinked__c,true);
                
                Case_Junction__c objCaseJ2 = new Case_Junction__c();
                objCaseJ2.IsLinked__c = true;
                objCaseJ2.Case__c = cs222.Id;
                objCaseJ2.Case_Master__c = cs333.Id;
                insert objCaseJ2;
                system.assertEquals(objCaseJ2.Id != Null,true);
                system.assertEquals(objCaseJ2.IsLinked__c,true);
                
                //Creating Account Record
                Account acc1 = HUMTestDataHelperTest.getAccount();
                acc1.Name = 'Test Account Cases1';   
                insert acc1;
                system.assertEquals(acc1.Id != Null,true);
                system.assertEquals(acc1.Name == 'Test Account Cases1',true);
                
                //Creating Policy Record
                Policy__c pol = HUMTestDataHelperTest.getPolicy();
                pol.Group_Name__c = acc1.Id;
                pol.Name = 'Test Policy';
                insert pol;
                system.assertEquals(pol.Id != Null,true);
                system.assertEquals(pol.Name,'Test Policy');
                system.assertEquals(pol.Group_Name__c == acc1.Id,true);
                
                //Insert Policy Member
                Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
                polMember.Name='Policy Member PM1';
                polMember.Policy__c=pol.Id;
                polMember.Member__c=acc1.Id;
                insert polMember;
                system.assertEquals(polMember.Id != Null,true);
                system.assertEquals(polMember.Name,'Policy Member PM1');
                system.assertEquals(polMember.Policy__c,pol.Id);
                
                //Insert list of Cases
                List<Case> csList1 = new List<Case>();
                for(Integer i=0;i<20;i++)
                {
                    Case cs = HUMTestDataHelperTest.getCase();
                    cs.Subject = 'Case Subjects'+ i;
                    cs.AccountId = acc1.Id;
                    cs.Policy__c  = pol.Id;
                    cs.OwnerId = u.id;
                    cs.Policy_Member__c = polMember.Id;
                    csList1.add(cs);
                }
                //Insert list of cases
                insert csList1;
                system.assertEquals(csList1.Size() == 20,true);
                system.assertEquals(csList1[0].Policy_Member__c == polMember.Id,true);
                
                Account acc2 = new Account();
                acc2.FirstName = 'Member account';
                acc2.LastName = 'Test';
                acc2.RecordTypeId = oRT1.Id;
                insert acc2;
                system.assertEquals(acc2.Id != Null,true);
                
                //Creating Policy Record
                Policy__c pol1 = HUMTestDataHelperTest.getPolicy();
                pol1.Group_Name__c = acc2.Id;
                pol1.Name = 'Test Policy1';
                insert pol1;
                system.assertEquals(pol1.Id != Null,true);
                system.assertEquals(pol1.Name,'Test Policy1');
                
                //Insert Policy Member
                Policy_Member__c polMember1 = HUMTestDataHelperTest.getPolicyMember();
                polMember1.Name='Policy Member PM2';
                polMember1.Policy__c=pol1.Id;
                polMember1.Member__c=acc2.Id;
                insert polMember1;
                system.assertEquals(polMember1.Id != Null,true);
                system.assertEquals(polMember1.Name,'Policy Member PM2');
                system.assertEquals(polMember1.Policy__c,pol1.Id);
                
                //Insert list of Cases
                List<Case> csList2 = new List<Case>();
                for(Integer i=0;i<20;i++)
                {
                    Case cs1 = HUMTestDataHelperTest.getCase();
                    cs1.Subject = 'Case Subject'+ i;
                    cs1.AccountId = acc2.Id;
                    cs1.Policy__c  = pol1.Id;
                    cs1.OwnerId = u.id;
                    cs1.Policy_Member__c = polMember1.Id;
                    csList2.add(cs1);
                }
                //Insert list of cases
                insert csList2;
                system.assertEquals(csList2.Size() == 20,true);
                system.assertEquals(csList2[0].Policy_Member__c == polMember1.Id,true);
            }
      }
     
    /*
     * Method Name : getAccountCases
     * Description : Test method for Account Related Cases
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getAccountCases() 
    {
        //Set Current Page
        Test.setCurrentPage(Page.CaseHistoryRelatedList_VF_HUM);
        Account acc = [Select Id from Account where Name = 'Test Account Cases' Limit 1];
        //Add ID to URL
        ApexPages.currentPage().getParameters().put('id',acc.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('id'),acc.Id);
        //Starting test execution
        // Run class as Customer Care Specialist
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        List<Case> csList = [select Id,CreatedDate from Case where AccountId =: acc.Id ];
        system.runAs(u)
        {
            Test.startTest();
           PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
           // system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            
            
            //Initialising from and to date field in test class          
            hCase.getCaseComments();            
            
            hCase.sStartDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            
            system.assertEquals(ApexPages.hasMessages(),false);
            
            //Throw error message if To Date is not provided
            hCase.sStartDate=String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate='';
            
            system.assertEquals(ApexPages.hasMessages(),false);
            //Throw error message if From Date is not provided
            hCase.sEndDate=String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year());
            hCase.sStartDate='';
            hcase.sCaseNumber = '';
            hcase.sRedirectURL = '';
            hcase.sCaseDetailsJSON = '';           
            system.assertEquals(ApexPages.hasMessages(),false);
            system.assertEquals(hCase.sEndDate,String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year()));
            //system.assertEquals(ApexPages.hasMessages(),true);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : getMemberAccountCases
     * Description : Test method for Member Account Related Cases
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getMemberAccountCases() 
    {
        //Set Current Page
        Test.setCurrentPage(Page.CaseHistoryRelatedList_VF_HUM);
        Account acc = [Select Id from Account where FirstName = 'Member account' Limit 1];
        //Add ID to URL
        ApexPages.currentPage().getParameters().put('id',acc.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('id'),acc.Id);
        //Starting test execution
        // Run class as Customer Care Specialist
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        List<Case> csList = [select Id,CreatedDate from Case where AccountId =: acc.Id ];
        system.runAs(u)
        {
            Test.startTest();
             PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
            //system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            
            //Initialising from and to date field in test class            
            hCase.getCaseComments();
            system.assertEquals(ApexPages.hasMessages(),false);
            hCase.sStartDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            
           
            //Throw error message if To Date is not provided
            hCase.sStartDate=String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate='';
           
            //Throw error message if From Date is not provided
            hCase.sEndDate=String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year());
            hCase.sStartDate='';     
            system.assertEquals(hCase.sEndDate,String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year()));
            //system.assertEquals(ApexPages.hasMessages(),true);
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : memberAccountNoCases
     * Description : Test method for Member Account with disabled case button
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void memberAccountNoCases() 
    {
        //Set Current Page
        Test.setCurrentPage(Page.CaseHistoryRelatedList_VF_HUM);
        Account acc = [Select Id from Account where FirstName = 'Member account' Limit 1];
        //Add ID to URL
        ApexPages.currentPage().getParameters().put('id',acc.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('id'),acc.Id);
        //Starting test execution
        // Run class as Customer Care Specialist
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        
        List<Case> csList = [select Id,CreatedDate from Case where AccountId =: acc.Id ];
        system.runAs(u)
        {
            Test.startTest();
            PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
           // system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            hCase.bpolCheck = true;      
            hCase.getCaseComments();
            system.assertEquals(ApexPages.hasMessages(),false);
            
            hCase.sStartDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());            
            
            //Throw error message if To Date is not provided
            hCase.sStartDate=String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate='';
            
            
            //Throw error message if From Date is not provided
            hCase.sEndDate=String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year());
            hCase.sStartDate='';
            // system.assertEquals(ApexPages.hasMessages(),true); 
            system.assertEquals(hCase.sEndDate,String.valueOf(System.today().month()+'/'+system.today().addDays(1).day()+'/'+system.today().year()));             
            Test.stopTest();
        }
    }
    
    /*
     * Method Name : getPolicyMemberCases
     * Description : Test method for Policy Member Related Cases
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void getPolicyMemberCases()
    {
        List<Case> csList = new List<Case>();
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        //set current page
        Test.setCurrentPage(Page.PolicyMemberCaseHistoryList_VF_HUM);
        Policy_Member__c polMember = [Select id from Policy_Member__c where Name = 'Policy Member PM1' limit 1];
        csList = [Select Id , CaseNumber from Case where Policy_Member__c = : polMember.Id ];
        //Add Parameters to URL
        ApexPages.currentPage().getParameters().put('id',polMember.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('id'),polMember.Id);
        ApexPages.currentPage().getParameters().put('viewAll','yes');
        system.assertEquals(ApexPages.currentPage().getParameters().get('viewAll'),'yes');
        //Starting execution
        system.runAs(u)
        {
            Test.startTest();
            PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
            system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            //Pagination method calls       
            hCase.sCaseNum ='';
            hCase.sStartDate ='';
            hCase.sEndDate='';
           
            system.assertEquals(ApexPages.hasMessages(),false);
            //result should return all records and throw page error message
            system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            //Use case when From to Date and Case Number both are provided
            hCase.sStartDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());           
            hCase.sCaseNum = String.valueOf(csList[0].CaseNumber);           
            Test.stopTest();
            //result should return only one record
            system.assertEquals(hCase.lstCaseDetails.size(),20);
            
        }
    }
    
    /*
     * Method Name : getPolicyCases
     * Description : Test method for Policy Related Cases
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void getPolicyCases()
    {
        List<Case> csList = new List<Case>();
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        //set current page
        Test.setCurrentPage(Page.PolicyCaseHistoryList_VF_HUM);
        Policy__c pol = [Select id from Policy__c where Name = 'Test Policy' limit 1];
        csList = [Select Id , CaseNumber from Case where Policy__c = : pol.Id ];
        //Add Parameters to URL
        ApexPages.currentPage().getParameters().put('id',pol.Id);
        system.assertEquals(ApexPages.currentPage().getParameters().get('id'),pol.Id);
        //Starting execution
        system.runAs(u)
        {
            Test.startTest();
            PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
           // system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            //Pagination method calls
            
            hCase.sCaseNum ='';
            hCase.sStartDate ='';
            hCase.sEndDate='';
            
           
            //result should return all records and throw page error message
           // system.assertEquals(hCase.lstCaseDetails.size(),csList.size());
            //Use case when From to Date and Case Number both are provided
            hCase.sStartDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            hCase.sEndDate = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+system.today().year());
            
            hCase.sCaseNum = String.valueOf(csList[0].CaseNumber);
           
            hCase.sStartDate ='';
            hCase.sEndDate='';
            hCase.sCaseNum = String.valueOf(csList[0].CaseNumber);
            //system.assertEquals(hCase.lstCaseDetails.size(),20);
            Test.stopTest();
            //result should return only one record
           // system.assertEquals(hCase.lstCaseDetails.size(),20);
           
            
            String sCases = '';
            For(Integer i =0 ;i<=1 ;i++)
            {
                sCases = sCases +',' + csList[i].Id+'_'+'true';
            }
            sCases = sCases.substring(1);
            ApexPages.currentPage().getParameters().put('sSelectedCases',sCases);

            hCase.linkSelectedCases();
           // system.assertEquals(true,hCase.bShowMessage);
            
            hCase.linkSelectedCases();
            system.assertEquals(false,hCase.bShowMessage);
            system.assertNotEquals(Null,hCase.sCaseDetailsJSON);
        }
    }
    
    /*** Negative Test Cases ***/

    /*
     * Method Name : raiseExceptionsInConstructor
     * Description : Test method to raise exception in constructor
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void raiseExceptionsInConstructor()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            //Adding invalid id to URL
            ApexPages.currentPage().getParameters().put('id','12345');
            system.assertEquals(ApexPages.currentPage().getParameters().get('id'),'12345');
            try
            {
                PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
                system.assertEquals(HUMExceptionHelper.bRestrictLog,true);
                system.assert(hCase.lstLogErrors.size()>0);
            }
            catch(Exception e)
            {
                system.assertEquals(e.getTypeName(),'StringException');
            }
            Test.stopTest();
        }
    }

    /*
     * Method Name : raiseExceptionInInit
     * Description : Test method for generating exception inside init method
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void raiseExceptionInInit()
    {
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.runAs(u)
        {
            Test.startTest();
            PharmacyCaseHistoryComponent_C_HUM hCase = new PharmacyCaseHistoryComponent_C_HUM();
            try
            {
                //invalid id to method      
                ApexPages.currentPage().getParameters().put('id','12345');
                system.assertEquals(ApexPages.currentPage().getParameters().get('id'),'12345');                
            }
            catch(Exception e)
            {
                system.assertEquals(e.getTypeName(),'StringException');
            }
            Test.stopTest();
        }
    }
/*
     * Method Name : getLinkCasesList
     * Description : Test method for Related Case List functionality
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getLinkCasesList() 
    {
        Account acc = [Select Id,Name from Account where Name = 'Test Account Cases' Limit 1];
        System.assertEquals(acc.Name == 'Test Account Cases',true);
        User u = [SELECT Id FROM User  WHERE Lastname='test User1' LIMIT 1];
        Case objCase = [select Id, CreatedDate,Subject from Case where AccountId =: acc.Id and Subject = 'SubjectCase 333' ];
        System.assertEquals(objCase.Subject == 'SubjectCase 333',true);
        
        system.runAs(u)
        {
            Test.startTest();
            PharmacyCaseHistoryComponent_C_HUM objCaseComp = new PharmacyCaseHistoryComponent_C_HUM();
            objCaseComp.sCaseLinkId = objCase.Id;
            objCaseComp.linkCasesList();
            System.assertNotEquals(objCaseComp.sLinkJson,NULL);
            Test.stopTest();
        }
    }
}