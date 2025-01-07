/****************************************************************************
Apex Class Name  : CreateAgentInteraction_T_HUM
Version          : 1.0 
Created Date     : May 15 2019
Function         : Test class to CreateAgentInteraction_H_HUM Apex class.
Modification Log :
  Developer Name          Code Review                Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
 Lakshmi vutukuri                                 05/13/2019                   Original Version
 Prasanthi Kandula           357233               05/24/2019                   Added assert statements as part of Security scan
 *******************************************************************************************************************************/

@isTest
private class CreateAgentInteraction_T_HUM
{
      @testSetup static void setup()
       {
           User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
           insert oUser;
           RecordType oRT = [select id from RecordType where sObjectType='Account' and Name='Agent/Broker'];
           system.assertEquals(oUser.Id != Null,true);
           system.assertEquals(oUser.Lastname == 'test User1',true);
           System.runAs(oUser)
           {
                Account oAccount = HUMTestDataHelperTest.getAccount();
                oAccount.Name = 'Test Account Cases1';
                oAccount.Agent_ID__c = '12345';
                oAccount.Agent_Type__c = 'Agency';
                oAccount.Birthdate__c = '01/15/1951';
                oAccount.Enterprise_ID__c = '1005447107';
                insert oAccount;
                system.assertEquals(oAccount.Id != Null,true);
                system.assertEquals(oAccount.Name == 'Test Account Cases1',true);
                
                Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
                oPolicy.Group_Name__c = oAccount.Id;
                oPolicy.Name = 'Test Policy';
                insert oPolicy;
                system.assertEquals(oPolicy.Id != Null,true);
                system.assertEquals(oPolicy.Name,'Test Policy');
                system.assertEquals(oPolicy.Group_Name__c == oAccount.Id,true);
                
                HUM_Webservice_Callout__c oHUMagencysearchService = new HUM_Webservice_Callout__c();
                oHUMagencysearchService.name='AgencySearchService';
                oHUMagencysearchService.Active__c=true;
                oHUMagencysearchService.Certificate_Name__c='test';
                oHUMagencysearchService.End_Point_URL__c='https://testtest-crmservicews.humana.com/';
                oHUMagencysearchService.Timeout__c='120000';
                oHUMagencysearchService.Content_Type__c='application/json';
                oHUMagencysearchService.Request_Type__c='POST';
                insert oHUMagencysearchService;
                system.assert(oHUMagencysearchService.id!=null);
                system.assert(oHUMagencysearchService.name == 'AgencySearchService');
                
                List<Account> lstAccount = new  List<Account>();
                Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
                Account acc1 = new Account( Enterprise_Id__c = '223300104', firstname = 'test2' ,lastname = 'method2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
                Account acc2 = new Account( Enterprise_Id__c = '002935049', firstname = 'test3', lastname = 'method3' ,Birthdate__c= '01/01/1990' ,ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
                Account acc3 = new Account( Enterprise_Id__c = '5478451', Agent_ID__c = '5478451', Agent_Type__c = 'Agency', Name = 'test4Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
                Account acc4 = new Account( Enterprise_Id__c = '5454451', Agent_ID__c = '430626199', Agent_Type__c = 'Agency', Name = 'test5Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
                lstAccount.Add(acc);
                lstAccount.Add(acc1);
                lstAccount.Add(acc2);
                lstAccount.Add(acc3);
                lstAccount.Add(acc4);
                Insert lstAccount;
                system.assertNotEquals(lstAccount, null , 'Accounts not created');               
                
                list<Product2> listProduct = new List<Product2>();
                Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
                Product2 testProduct1 = new Product2(Name ='testprod2', ProductCode ='MED');
                listProduct.Add(testProduct);
                listProduct.Add(testProduct1);
                Insert listProduct;
                system.assertNotEquals(listProduct, null , 'Products not created');
                
                list<Policy__c> listPolicy = new List<Policy__c>();             
                Policy__c testPolicy = new Policy__c(Group_Name__c = lstAccount[0].ID, Major_LOB__c = 'MER', Product__c = listProduct[0].ID,Policy_Group_Number__c='11111');
                Policy__c testPolicy1 = new Policy__c(Group_Name__c = lstAccount[1].ID, Major_LOB__c = 'MCD', Product__c = listProduct[1].ID,Policy_Group_Number__c='11111');
                listPolicy.Add(testPolicy);
                listPolicy.Add(testPolicy1);
                insert listPolicy; 
                system.assertNotEquals(listPolicy, null , 'Policies not created');
                
                List<Policy_Member__c>  listPolicyMember = new List<Policy_Member__c>();
                Policy_Member__c memeber = new Policy_Member__c(Policy__c = listPolicy[0].ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017' , End_Date__c = '12/31/2017');
                Policy_Member__c memeber1 = new Policy_Member__c(Policy__c = listPolicy[1].ID, Member__c = lstAccount[1].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2017', End_Date__c = '12/31/2017');
                listPolicyMember.Add(memeber);
                listPolicyMember.Add(memeber1);
                Insert listPolicyMember ;
                system.assertNotEquals(listPolicyMember, null ,  'Policies  not created for members');
    
                List<Member_ID__c> listmember = new List<Member_ID__c>();
                Member_ID__c member1 = new Member_ID__c(Policy__c = listPolicy[1].ID,Policy_Member__c = listPolicyMember[1].ID, Member_Card_External_ID__c= '123645' , name = '360306641' , Type__c ='Member-Id-Base',Member_Dependent_Code__c='00');
                listmember.Add(member1);
                insert listmember;
                system.assertNotEquals(listmember, null , 'Members  not created'); 
                
                HUMSearchLimit__c HumSLVar = new HUMSearchLimit__c();
                HumSLVar.name = 'Member_SearchLimit';
                HumSLVar.Search_Limit__c = 101;
                insert HumSLVar;
                system.AssertNotEquals(HumSLVar, NULL);
                
                HUMConstants__c  objhumc = New HUMConstants__c();
                objhumc.name ='UNKNOWN_MEMBER';
                objhumc.StringValue__c ='Unknown Member';
                insert objhumc ;
                system.assertEquals(objhumc.id != null, True);
                
                HUMConstants__c  objhumc1 = New HUMConstants__c();
                objhumc1.name ='UNKNOWN_MEMBER_EXTID';
                objhumc1.StringValue__c ='UKM|00|SFDC|';
                insert objhumc1 ;
                system.assertEquals(objhumc1.id != null, True);
                
                HUMConstants__c  objhumcPipe = New HUMConstants__c();
                objhumcPipe.name ='TENANTID_GEN';
                objhumcPipe.StringValue__c ='00';
                insert objhumcPipe;
                system.assertEquals(objhumcPipe.id != null, True);
                HUMConstants__c  objhumcP = New HUMConstants__c();
                objhumcP.name ='PIPE_CHARACTER';
                objhumcP.StringValue__c ='|';
                insert objhumcP;
                system.assertEquals(objhumcP.id != null, True);
            }
        } 
      /*
    * Method Name : testAgencyServiceClassIntegration
    * Description : This method is used for group search from external system
    * Return type : NA
    * Paramater   : NA
    */
    static testMethod void testAgencyServiceWebServiceResults() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {  
            CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();      
            CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();  
            list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();   
            intabout.AddressLine1='2501 RICHARDS AVENUE';
            intabout.City = 'TAMPA';
            intabout.DOB = '01/25/1936';
            intabout.EmailID='';
            intabout.FirstName = 'LOIS';
            intabout.HomePhone = '3027032536';
            intabout.ID = '360306641';
            intabout.IDType='Member-Id-Base';
            intabout.MemberIdDependentCode='00';
            intabout.LastName = 'ABBOTT';
            intabout.State = 'DE';
            intabout.EntityType='Agent';
            intabout.ZipCode='19958';
            List<Member_Id__c> memberacct = new List<Member_Id__c>();
            memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641'];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = memberacct[0].id;
            Test.setFixedSearchResults(fixedSearchResults);            
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
            intAbout2.EntityType= 'Agent';
            intAbout2.Id = '430626190';
            intAbout2.IdType = 'SSN'; 
            intabout2.AddressLine1='2501 RICHARDS AVENUE';
            intabout2.City = 'TAMPA';
            intabout2.DOB = '01/25/1936';
            intabout2.EmailID='';
            intabout2.FirstName = 'LOIS';
            intabout2.HomePhone = '7272073093';
            intabout2.LastName = 'ABBOTT';
            intabout2.State = 'DE';        
            intabout2.ZipCode='19958';
                            
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout5 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout5 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
            intabout5.AddressLine1='2501 RICHARDS AVENUE';
            intabout5.City = 'TAMPA';
            intabout5.DOB = '01/25/1936';
            intabout5.EmailID='';
            intabout5.FirstName = 'LOISAboutTax';
            intabout5.HomePhone = '3027032536';
            intabout5.ID = '001383753';
            intabout5.IDType='AgentId';
            intabout5.MemberIdDependentCode='';
            intabout5.LastName = 'ABBOTT';
            intabout5.State = 'DE';
            intabout5.EntityType='Agent';
            intabout5.ZipCode='19958';
            intabout5.ProviderClassification = '';  
            caseintabout.InteractingAbout = intabout;
            caseintabout.CaseIdentifier = '123';
            caseintabout2.InteractingAbout = intabout2;
            caseintabout2.CaseIdentifier = '124';
            intlist.add(caseintabout);
            intlist.add(caseintabout2);
            intreq.InteractingWith = intabout5;
            intreq.CaseInteractingAbout = intlist;   
            intreq.InteractionOrigin = 'Vantage'; 
            intreq.RequiredCasesInResponse= true;
            owrap.CreateInteractionRequest  = intreq; 
            Test.startTest();
            CreateAgentInteraction_H_HUM ocreateAgntInt = new CreateAgentInteraction_H_HUM();
            Test.setMock(WebServiceMock.class, new HUMAgencySearchServiceTest ());
            Map<String,Map<String,String>> mapResponse =  ocreateAgntInt.PrepareAgentAccountMap(owrap.CreateInteractionRequest);
            system.assertNotEquals(mapResponse,Null);
            Test.stopTest();
        }    
    }
    
    static testMethod void testAgencyService_Internal() 
    {        
        User u = [Select Id , Name from User where Username= :System.Label.HUMAgencyUser+System.Label.HUMAgencyUser+'@humcrmstest.com'  order by LastModifiedDate desc Limit 1]; 
        system.runAs(u)
        {  
            CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();      
            CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();  
            list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();   
            intabout.AddressLine1='2501 RICHARDS AVENUE';
            intabout.City = 'TAMPA';
            intabout.DOB = '01/25/1936';
            intabout.EmailID='';
            intabout.FirstName = 'LOIS';
            intabout.HomePhone = '3027032536';
            intabout.ID = '360306641';
            intabout.IDType='Member-Id-Base';
            intabout.MemberIdDependentCode='00';
            intabout.LastName = 'ABBOTT';
            intabout.State = 'DE';
            intabout.EntityType='Agent';
            intabout.ZipCode='19958';
            List<Member_Id__c> memberacct = new List<Member_Id__c>();
            memberacct = [SELECT ID ,Name FROM Member_Id__c WHERE Name = '360306641'];
            Id [] fixedSearchResults= new Id[1];
            fixedSearchResults[0] = memberacct[0].id;
            Test.setFixedSearchResults(fixedSearchResults);            
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout2 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout2 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
            intAbout2.EntityType= 'Agent';
            intAbout2.Id = '430626199';
            intAbout2.IdType = 'SSN'; 
            intabout2.AddressLine1='2501 RICHARDS AVENUE';
            intabout2.City = 'TAMPA';
            intabout2.DOB = '01/25/1936';
            intabout2.EmailID='';
            intabout2.FirstName = 'LOIS';
            intabout2.HomePhone = '7272073093';
            intabout2.LastName = 'ABBOTT';
            intabout2.State = 'DE';        
            intabout2.ZipCode='19958';
                            
            CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout5 = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
            CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout5 = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
            intabout5.AddressLine1='2501 RICHARDS AVENUE';
            intabout5.City = 'TAMPA';
            intabout5.DOB = '01/25/1936';
            intabout5.EmailID='';
            intabout5.FirstName = 'LOISAboutTax';
            intabout5.HomePhone = '3027032536';
            intabout5.ID = '001383753';
            intabout5.IDType='AgentId';
            intabout5.MemberIdDependentCode='';
            intabout5.LastName = 'ABBOTT';
            intabout5.State = 'DE';
            intabout5.EntityType='Agent';
            intabout5.ZipCode='19958';
            intabout5.ProviderClassification = '';  
            caseintabout.InteractingAbout = intabout;
            caseintabout.CaseIdentifier = '123';
            caseintabout2.InteractingAbout = intabout2;
            caseintabout2.CaseIdentifier = '124';
            intlist.add(caseintabout);
            intlist.add(caseintabout2);
            intreq.InteractingWith = intabout5;
            intreq.CaseInteractingAbout = intlist;   
            intreq.InteractionOrigin = 'Vantage'; 
            intreq.RequiredCasesInResponse= true;
            owrap.CreateInteractionRequest  = intreq; 
            Test.startTest();
            CreateAgentInteraction_H_HUM ocreateAgntInt = new CreateAgentInteraction_H_HUM();
            Map<String,Map<String,String>> mapResponse =  ocreateAgntInt.PrepareAgentAccountMap(owrap.CreateInteractionRequest);
            system.assertNotEquals(mapResponse,Null);
            Test.stopTest();
        }    
    }
}