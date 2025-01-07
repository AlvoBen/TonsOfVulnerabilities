/****************************************************************************
Apex Class Name  : GetInteractionInfoRest_T_HUM
Version          : 1.0 
Created Date     : July 01 2019
Function         : Test class to GetInteractionInfoRest_D_HUM Apex class.
Modification Log :
  Developer Name          Code Review                Date                       Description
-------------------------------------------------------------------------------------------------------------------------------
 Mohammed Noor                                     07/01/2019                  Original Version
 Mohammed Noor                                     07/26/2019                  Test Class changes for Defect#430969
 Moshitha Gunasekaran                              08/14/2019                   REQ - 404608 - Support TaxID for Known and Unknown Agent
********************************************************************************************************************************/
@isTest
public class GetInteractionInfoRest_T_HUM 
{
	/**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('humcrmsCaseServiceUser1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('humcrmsCaseServiceUser2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser1.lastname=='test humcrmsCaseServiceUser2',true);
        system.assertEquals(oUser.lastname=='test humcrmsCaseServiceUser1',true); 
        System.runAs(oUser) 
        {
            //Insert Group Account
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'test account';
            oAcc.Enterprise_ID__c = '1234567895425';
            oAcc.Group_Number__c = '123456';
            oAcc.Source_Platform_Code__c = 'LV';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.Name == 'test account');
                                               
            Classification__c classfication = new Classification__c(name = 'Chronic Care SNP' ,Is_Active__c = true);
			insert classfication ;
			system.assertNotEquals(classfication, null, 'classfication  not created');
            
			Classification_Type__c classificationType = new Classification_Type__c(name = 'Enrollment (RSO)', Is_Active__c = true);
			insert classificationType ;
			system.assertNotEquals(classificationType, null , 'classificationType  not created');
            
			Intent__c intent = new Intent__c(name = 'Denied' , Is_Active__c = true);
			insert intent ;
			system.assertNotEquals(intent, null ,'classificationType  not created');
            
			List<CTCI_Junction__c> listctciJunction = new List<CTCI_Junction__c>();
			CTCI_Junction__c ctciJunction  =  new CTCI_Junction__c(Classification__c =  classfication.ID, Classification_Type__c = classificationType .ID, Intent__c = intent .ID , IsActive__c = true);
			listctciJunction.Add(ctciJunction);
			insert listctciJunction ;
			system.assertNotEquals(intent, null ,'CTCIJunctionlist  not created');
			
			Product2 testProduct = new Product2(Name ='testprod1',  ProductCode ='MED');
            insert testProduct;
            
            Policy__c testPolicy = new Policy__c(Group_Name__c = oAcc.ID, Major_LOB__c = 'MER', Product__c = testProduct.ID,Policy_Group_Number__c='11111');
            insert testPolicy;
			
			List<Account> lstAccount = new  List<Account>();
			Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'Account1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
			Account acc1 = new Account( Enterprise_Id__c = '223300105', firstname = 'test2' ,lastname = 'Account2' , Birthdate__c= '01/02/1990', ETL_Record_Deleted__c = false , PERSONMAILINGSTATECODE= 'WI', recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());			
			Account acc2 = new Account( Enterprise_Id__c = '5478451', Agent_ID__c = '5478451', Agent_Type__c = 'Agency', Name = 'test3Agent', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Agent/Broker').getRecordTypeId());
			Account acc3 = new Account( Enterprise_Id__c = '5454451', Agent_ID__c = '5454451', Agent_Type__c = 'Agency', Name = 'test4UKAgent', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId());
			Account acc4 = new Account( Enterprise_Id__c = '1254785', NPI_ID__c = '1235452151', DBA__c = 'TestProvider', Name = 'Provider Test', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
			Account acc5 = new Account( Enterprise_Id__c = '5451254', NPI_ID__c = '5457894561', DBA__c = 'TestProvider1', Name = 'Provider Test1', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Provider').getRecordTypeId());
			Account acc6 = new Account( Enterprise_Id__c = '5451354', NPI_ID__c = '5457893561', DBA__c = 'TestProvider2', Name = 'Provider Test2', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Provider').getRecordTypeId());
			Account acc7 = new Account( Enterprise_Id__c = '5478351', Agent_ID__c = '5478351', Agent_Type__c = 'Agency', Name = 'test5Broker', ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Agent/Broker').getRecordTypeId());					
			lstAccount.Add(acc);
			lstAccount.Add(acc1);
			lstAccount.Add(acc2);
			lstAccount.Add(acc3);
			lstAccount.Add(acc4);
			lstAccount.Add(acc5);
			lstAccount.Add(acc6);
			lstAccount.Add(acc7);					
			insert lstAccount;
			system.assertNotEquals(lstAccount, null , 'Accounts not created');
			
			Policy_Member__c policyMem = new Policy_Member__c(Policy__c = testPolicy.ID, Member__c = lstAccount[0].ID , ETL_Record_Deleted__c = false, Effective_Date__c = '01/01/2019' , End_Date__c = '12/31/2019');
			insert policyMem;
			
			List<Member_ID__c> listmember = new List<Member_ID__c>();
			Member_ID__c member = new Member_ID__c(Policy__c = testPolicy.ID,Policy_Member__c = policyMem.ID, Member_Card_External_ID__c= '123345' , name = '360306641' , Type__c ='SSN');             
			Member_ID__c member1 = new Member_ID__c(Policy__c = testPolicy.ID,Policy_Member__c = policyMem.ID, Member_Card_External_ID__c= '123245' , name = '002935049' , Type__c ='MedicareID');                          
			Member_ID__c member2 = new Member_ID__c(Policy__c = testPolicy.ID,Policy_Member__c = policyMem.ID , Member_Card_External_ID__c= '123684', name = 'H46738930' , Type__c ='Member-Id-Base', Member_Dependent_Code__c = '00');            
			listmember.Add(member);
			listmember.Add(member1);
			listmember.Add(member2);            
			insert listmember;
			system.assertNotEquals(listmember, null , 'Members  not created');
			
			list<ConsumerID__c> lstConsumerRecs = new list<ConsumerID__c>();
			//Known Provider TAXID
			ConsumerID__c consumerrec=new ConsumerID__c();  
			consumerrec.ID_Type__c='TAXID';
			consumerrec.Consumer_ID__c='112345678';
			consumerrec.Account__c=lstAccount[4].Id;
			lstConsumerRecs.add(consumerrec);
			
			//UnKnown Provider TAXID
			ConsumerID__c consumerrec1=new ConsumerID__c();  
			consumerrec1.ID_Type__c='TAXID';
			consumerrec1.Consumer_ID__c='112345677';
			consumerrec1.Account__c=lstAccount[5].Id;
			lstConsumerRecs.add(consumerrec1);
			
			//Known Agent TAXID
			ConsumerID__c consumerrec2=new ConsumerID__c();  
			consumerrec2.ID_Type__c='TAXID';
			consumerrec2.Consumer_ID__c='112345679';
			consumerrec2.Account__c=lstAccount[2].Id;
			lstConsumerRecs.add(consumerrec2);
			
			//UnKnown Agent TAXID
			ConsumerID__c consumerrec3=new ConsumerID__c();  
			consumerrec3.ID_Type__c='TAXID';
			consumerrec3.Consumer_ID__c='112345667';
			consumerrec3.Account__c=lstAccount[3].Id;
			lstConsumerRecs.add(consumerrec3);
			
			insert lstConsumerRecs;
			
			//Insert Interaction Records
			list<Interaction__c> lstInteractions = new list<Interaction__c>();			
			//Known Member Interaction
			Interaction__c intRec1 = HUMTestDataHelperTest.getInteraction();
			intRec1.Interacting_With__c = lstAccount[0].Id;
			intRec1.Interacting_With_type__c = 'Member';
			intRec1.Interaction_External_ID__c = '1234567890';
			lstInteractions.add(intRec1);
			
			//Known Agent Interaction
			Interaction__c intRec3 = HUMTestDataHelperTest.getInteraction();
			intRec3.Interacting_With__c = lstAccount[2].Id;
			intRec3.Interacting_With_type__c = 'Agent';
			intRec3.Interaction_External_ID__c = '1234567892';
			lstInteractions.add(intRec3);
			
			//UnKnown Agent Interaction
			Interaction__c intRec4 = HUMTestDataHelperTest.getInteraction();
			intRec4.Interacting_With__c = lstAccount[3].Id;
			intRec4.Interacting_With_type__c = 'Unknown-Agent';
			intRec4.Interaction_External_ID__c = '1234567893';
			lstInteractions.add(intRec4);
			
			//Group Interaction
			Interaction__c intRec5 = HUMTestDataHelperTest.getInteraction();
			intRec5.Interacting_With__c = oAcc.Id;
			intRec5.Interacting_With_type__c = 'Group';
			intRec5.Interaction_External_ID__c = '1234567894';
			lstInteractions.add(intRec5);
			
			//Provider Interaction
			Interaction__c intRec6 = HUMTestDataHelperTest.getInteraction();
			intRec6.Interacting_With__c = lstAccount[4].Id;
			intRec6.Interacting_With_type__c = 'Provider';
			intRec6.Interaction_External_ID__c = '1234567895';
			lstInteractions.add(intRec6);
			
			//Unknown Provider Interaction
			Interaction__c intRec7 = HUMTestDataHelperTest.getInteraction();
			intRec7.Interacting_With__c = lstAccount[5].Id;
			intRec7.Interacting_With_type__c = 'Unknown-Provider';
			intRec7.Interaction_External_ID__c = '1234567896';
			lstInteractions.add(intRec7);
					
			insert lstInteractions;
			System.assertNotEquals(lstInteractions, null, 'Interactions not created');
            
            //Insert Interaction Member records           
            list<Interaction_Member__c> lstIntMemebrs = new list<Interaction_Member__c>();
			
			//Known Member Account
			Interaction_Member__c intMemRec1 = new Interaction_Member__c();
			intMemRec1.Interacting_About__c = lstAccount[0].id;
			intMemRec1.Interaction__c = lstInteractions[0].id;
			intMemRec1.Interacting_About_Type__c ='Member';
			lstIntMemebrs.add(intMemRec1);
			
			//Known Agent Account
			Interaction_Member__c intMemRec3 = new Interaction_Member__c();
			intMemRec3.Interacting_About__c = lstAccount[2].id;
			intMemRec3.Interaction__c = lstInteractions[1].id;
			intMemRec3.Interacting_About_Type__c ='Agent';
			lstIntMemebrs.add(intMemRec3);
			
			//Unknown Agent Account
			Interaction_Member__c intMemRec4 = new Interaction_Member__c();
			intMemRec4.Interacting_About__c = lstAccount[3].id;
			intMemRec4.Interaction__c = lstInteractions[2].id;
			intMemRec4.Interacting_About_Type__c ='Unknown-Agent';
			lstIntMemebrs.add(intMemRec4);
			
			//Group Account
			Interaction_Member__c intMemRec5 = new Interaction_Member__c();
			intMemRec5.Interacting_About__c = oAcc.id;
			intMemRec5.Interaction__c = lstInteractions[3].id;
			intMemRec5.Interacting_About_Type__c ='Group';
			lstIntMemebrs.add(intMemRec5);
			
			//Provider Account
			Interaction_Member__c intMemRec6 = new Interaction_Member__c();
			intMemRec6.Interacting_About__c = lstAccount[4].id;
			intMemRec6.Interaction__c = lstInteractions[4].id;
			intMemRec6.Interacting_About_Type__c ='Provider';
			lstIntMemebrs.add(intMemRec6);
			
			//Unknown Provider Account
			Interaction_Member__c intMemRec7 = new Interaction_Member__c();
			intMemRec7.Interacting_About__c = lstAccount[5].id;
			intMemRec7.Interaction__c = lstInteractions[5].id;
			intMemRec7.Interacting_About_Type__c ='Unknown-Provider';
			lstIntMemebrs.add(intMemRec7);
			
			insert lstIntMemebrs;
			System.assertNotEquals(lstIntMemebrs, null, 'InteractionMembers not created');
			
			//Insert Case records
			list<Case> lstCaseRecs = new list<Case>();
			Case case1 = HUMTestDataHelperTest.getCase();
            case1.Interacting_With__c = lstAccount[0].id;
            case1.Interacting_With_Name__c = 'test1 Account1';
            case1.Interacting_With_Type__c = 'Member';
            case1.AccountID = lstAccount[0].id;
            case1.Interacting_About_Type__c = 'Member';
            case1.Classification_ID__c = classfication.ID;
            case1.Intent_ID__c  = intent.id;            
            lstCaseRecs.add(case1);
            insert lstCaseRecs ;
            
            //Insert Case Interaction records
            list<Case_Interaction__c> lstCaseInt = new list<Case_Interaction__c>();
            Case_Interaction__c caseInt = new Case_Interaction__c();
			caseInt.Case__C = lstCaseRecs[0].Id;
			caseInt.Interaction__c = lstInteractions[0].id;
			lstCaseInt.add(caseInt);             
			insert lstCaseInt;            
			            								                       
        }
    }
    
    /**
    * This is a test method for Interaction Number Input 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_InteractionNumber()
    {    	
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    	list<Interaction__c> lstInteractions = [SELECT Id,Name, Interacting_with__r.lastname, Interacting_with__r.recordtype.name FROM Interaction__c WHERE Interacting_with__r.lastname = 'Account1' and Interacting_with__r.recordtype.name = 'Member'];    	    	
		if(lstInteractions!=null && !lstInteractions.isEmpty())
		{
			GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
			GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
			reqeuestDTO.InteractionNumber = new list<String>{lstInteractions[0].Name};
			reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
			interactinInfoReq.GetInteractionInfo = reqeuestDTO;
		   	 
	    	system.runAs(tstUser)
	    	{
	    		Test.startTest();
	    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);	    
	    		system.assertNotEquals(response,NULL);
	    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
	    		Test.stopTest();
	    	}
		}
    }
    
    /**
    * This is a test method for Known Member Entity and ID Type SSN 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_KnownMember_SSN()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');   		
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Member';
		entity1.IdValue = '360306641';
		entity1.IdValueType = 'SSN';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();		
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);    		
    		system.assertNotEquals(response,NULL);
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for known Member Entity and ID Type EnterpriseID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_KnownMember_EnterpriseID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Member';
		entity1.IdValue = '223300104';
		entity1.IdValueType = 'EnterpriseID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Agent Entity and ID Type AgentID 
    *
    * @param  None      
    * @return  Void
    */    
    static testMethod void test_KnownAgent_AgentID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Agent';
		entity1.IdValue = '5478451';
		entity1.IdValueType = 'AgentID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Agent Entity and ID Type TaxId 
    *
    * @param  None      
    * @return  Void
    */    
    static testMethod void test_KnownAgent_TaxID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Agent';
		entity1.IdValue = '112345679';
		entity1.IdValueType = 'TaxID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Unknown Agent Entity and ID Type AgentID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_UnKnownAgent_AgentID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Agent';
		entity1.IdValue = '5454451';
		entity1.IdValueType = 'AgentID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Unknown Agent Entity and ID Type TaxID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_UnKnownAgent_TaxID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Agent';
		entity1.IdValue = '112345667';
		entity1.IdValueType = 'TaxID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Group Entity and ID Type GroupNumber 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_Group_GroupNumber()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Group';
		entity1.IdValue = '123456';
		entity1.Platform = 'LV';
		entity1.IdValueType = 'GroupNumber';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Group Entity and ID Type EnterpriseID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_Group_CustGenKey()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Group';
		entity1.IdValue = '1234567895425';
		entity1.IdValueType = 'CustomerGenKey';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Provider Entity and ID Type ProviderID (EnterpriseID) 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_Provider_ProviderID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '1254785';
		entity1.IdValueType = 'ProviderID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Provider Entity and ID Type NPIID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_Provider_NPIID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '1235452151';
		entity1.IdValueType = 'NPIID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Provider Entity and ID Type TAXID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_Provider_TAXID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '112345678';
		entity1.IdValueType = 'TaxID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Unknown Provider Entity and ID Type NPIID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_UnknownProvider_NPIID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '5457894561';
		entity1.IdValueType = 'NPIID';
		entityDetails.add(entity1);
		EntityIdentifierDTO entity2 = new EntityIdentifierDTO();
		entity2.EntityType = 'Provider';
		entity2.IdValue = '5457893561';
		entity2.IdValueType = 'NPIID';
		entityDetails.add(entity2);		
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for Unknown Provider Entity and ID Type TaxID 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_UnknownProvider_TAXID()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '112345677';
		entity1.IdValueType = 'TaxID';
		entityDetails.add(entity1);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test method for more than 5 Entity Entries in Interacting With 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_multipleEntities()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '112345677';
		entity1.IdValueType = 'TaxID';		
		entityDetails.add(entity1);
		EntityIdentifierDTO entity2 = new EntityIdentifierDTO();
		entity2.EntityType = 'Member';
		entity2.IdValue = '223300104';
		entity2.IdValueType = 'EnterpriseID';
		entityDetails.add(entity2);
		EntityIdentifierDTO entity3 = new EntityIdentifierDTO();
		entity3.EntityType = 'Provider';
		entity3.IdValue = '5457894561';
		entity3.IdValueType = 'NPIID';
		entityDetails.add(entity3);
		EntityIdentifierDTO entity4 = new EntityIdentifierDTO();
		entity4.EntityType = 'Provider';
		entity4.IdValue = '1235452151';
		entity4.IdValueType = 'NPIID';
		entityDetails.add(entity4);
		EntityIdentifierDTO entity5 = new EntityIdentifierDTO();
		entity5.EntityType = 'Group';
		entity5.IdValue = '1234567895425';
		entity5.IdValueType = 'CustomerGenKey';
		entityDetails.add(entity5);
		EntityIdentifierDTO entity6 = new EntityIdentifierDTO();
		entity6.EntityType = 'Agent';
		entity6.IdValue = '5454451';
		entity6.IdValueType = 'AgentID';
		entityDetails.add(entity6);
		GetInteractionInfoRequest_DTO_HUM interactinInfoReq = new GetInteractionInfoRequest_DTO_HUM();
		GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO reqeuestDTO = new GetInteractionInfoRequest_DTO_HUM.GetInteractionInfoRequestDTO();
		reqeuestDTO.InteractingWithCriteria = entityDetails;
		reqeuestDTO.StartDate = '06/11/2019';
		reqeuestDTO.EndDate = '07/11/2019';
		reqeuestDTO.StartPaging = '1';		
		reqeuestDTO.InclusionCriteria = new list<String>{'Case','Search Unknown'};
		interactinInfoReq.GetInteractionInfo = reqeuestDTO;
	   	 
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		GetInteractionInfoResponse_DTO_HUM response = GetInteractionInfoRest_D_HUM.getInteractionInfo(interactinInfoReq);
    		system.assertNotEquals(response,NULL);
    		system.assertNotEquals(response.GetInteractionInfoResponse.InteractionCount, '0');
    		Test.stopTest();
    	}
    }
    
    /**
    * This is a test Entity Search for more than 5 Entity Entries in Interacting About and Interacting With 
    *
    * @param  None      
    * @return  Void
    */
    static testMethod void test_EntitySearch()
    {
    	User tstUser = [SELECT Id, profile.Name FROM User WHERE lastname='test humcrmsCaseServiceUser2' limit 1];    	
    	system.assertEquals(tstUser.profile.Name,'ETL API Access');
    			
		list<EntityIdentifierDTO> entityDetails = new list<EntityIdentifierDTO>();
		list<EntityIdentifierDTO> entityDetails1 = new list<EntityIdentifierDTO>();
		
		EntityIdentifierDTO entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Member';
		entity1.IdValue = '223300104';
		entity1.IdValueType = 'EnterpriseID';
		entityDetails.add(entity1);				
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Agent';
		entity1.IdValue = '5454451';
		entity1.IdValueType = 'AgentID';
		entityDetails.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Group';
		entity1.IdValue = '123456';
		entity1.Platform = 'LV';
		entity1.IdValueType = 'GroupNumber';
		entityDetails.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Group';
		entity1.IdValue = '1234567895425';
		entity1.IdValueType = 'CustomerGenKey';
		entityDetails.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '1254785';
		entity1.IdValueType = 'ProviderID';
		entityDetails.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '1235452151';
		entity1.IdValueType = 'NPIID';
		entityDetails.add(entity1);
		entityDetails1.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '112345678';
		entity1.IdValueType = 'TaxID';
		entityDetails1.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '5457894561';
		entity1.IdValueType = 'NPIID';
		entityDetails1.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '5457893561';
		entity1.IdValueType = 'NPIID';
		entityDetails1.add(entity1);
		
		entity1 = new EntityIdentifierDTO();
		entity1.EntityType = 'Provider';
		entity1.IdValue = '112345677';
		entity1.IdValueType = 'TaxID';
		entityDetails1.add(entity1);
		
    	system.runAs(tstUser)
    	{
    		Test.startTest();
    		map<string,set<string>> resultMap  = EntitySearch_H_HUM.performEntitySearch(entityDetails, entityDetails1, true);
    		system.assertNotEquals(resultMap,NULL);    		
    		Test.stopTest();
    	}
    }
    
                         
}