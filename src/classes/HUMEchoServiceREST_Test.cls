/***************************************************************************************************************************************************************************
Apex Class Name  : HUMEchoServiceREST_Test
Version          : 1.0
Created Date     : September 04 2018
Function         : Test Class for Unit testing and code coverage of HUMEchoServiceREST class. 
****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name             Code Review          Date                    Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Mohammed Noor                296361             09/04/2018              Original Version
 ****************************************************************************************************************************************************************************/
@isTest
private class HUMEchoServiceREST_Test 
{
	/**
    * This method is used to insert the data only once and called automicticaly in all methods     
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
     {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
        insert oUser1;
        system.assertEquals(oUser.lastname=='test User1',true);
		
        System.runAs(oUser) 
        {
            //Create a Member Account
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.FirstName = 'test';
            oAcc.LastName = 'account';
            oAcc.Enterprise_ID__c = '1234567';
            oAcc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            insert oAcc;
            System.assert(oAcc.FirstName == 'test');
			            
            Classification_Type__c objcT = new Classification_Type__c();
            objcT.Name ='CallRSOtest';
            objcT.Is_Active__c = true;
            insert objcT;
            System.assertEquals(objcT.Name,'CallRSOtest');
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1 .Name = 'Claimstest';
            classificationRec1 .Is_Active__c = true;
            insert classificationRec1 ;
            System.assertEquals(classificationRec1.Name,'Claimstest');
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Fillingtest';
            intentRec1.Is_Active__c = true;
            insert intentRec1 ;
            System.assertEquals(intentRec1.Name,'Fillingtest');
            
            
            CTCI_Junction__c objCTCI = new CTCI_Junction__c();
            objCTCI.Classification__c=classificationRec1.id;
            objCTCI.Classification_Type__c= objcT.id;
            objCTCI.Intent__c =intentRec1.id;
            objCTCI.IsActive__c=true;
            insert objCTCI ;
            
            //Create a Case
            Case caseObj = HUMTestDataHelperTest.getCase();
	        caseObj.CTCI_List__c=objCTCI.id;
	        caseObj.accountID = oAcc.Id;
	        caseObj.Interacting_about_type__c = 'Member';
	        caseObj.Interacting_With__c = oAcc.Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRec1.ID;
            caseObj.Intent_ID__c  = intentRec1.id;
            caseobj.Interacting_With_Type__c = 'Member';
            insert caseObj ;
            System.assertNotEquals(caseObj, null);
            	        	                    
            //Create an Interaction
            Interaction__c oInteraction = HUMTestDataHelperTest.getInteraction();
		    oInteraction.Interacting_With__c = oAcc.Id;
		    oInteraction.Interacting_With_Type__c = 'Member';
		    oInteraction.UUID__c = '011717000094440000310S8886667730';
		    insert oInteraction;
		    
            //Create an Interaction Member
            Interaction_Member__c oIntMemb = HUMTestDataHelperTest.getInteractionMember();
		    oIntMemb.Interaction__c =  oInteraction.Id;           
		    oIntMemb.Interacting_About_Type__c = oInteraction.Interacting_With_type__c;
		    oIntMemb.Interacting_About__c = oInteraction.Interacting_With__c;
		    insert oIntMemb;		    
            
            //Create an Case Interaction
            Case_Interaction__c oCaseInt = new Case_Interaction__c();
		    oCaseInt.Case__c = caseObj.Id;
		    oCaseInt.Interaction__c = oInteraction.Id;
			insert oCaseInt;                        			
        }        
    }
    
    /*  Test method to unit test the scenario where UUID only is in the Request 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testUUIDRequest()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
    		HUMEchoRestRequestDTO_HUM echoRequest = new HUMEchoRestRequestDTO_HUM();
    		HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO request = new HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO(); 
    		request.sUUID = '011717000094440000310S8886667730';
    		echoRequest.LastInteractionDetails = request;
    		RestRequest req = new RestRequest();
        	String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/EchoServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMEchoServiceREST.getInteractionDetails();            
            Test.stopTest();
            HUMEchoRestResponseDTO_HUM echoResponse = (HUMEchoRestResponseDTO_HUM)System.JSON.deserialize(res.responseBody.toString(), HUMEchoRestResponseDTO_HUM.class);			
			System.assertNotEquals(echoResponse,null);
			System.assertEquals(res.statuscode,200);
    	}            
    }
    
    /*  Test method to unit test the scenario where Interacting with ID only is in the Request 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testInteracingWithID()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
    		HUMEchoRestRequestDTO_HUM echoRequest = new HUMEchoRestRequestDTO_HUM();
    		HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO request = new HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO(); 
    		request.sInteractingWithID = '1234567';
    		request.sInteractingWithType = 'Member';
    		echoRequest.LastInteractionDetails = request;
    		RestRequest req = new RestRequest();
        	String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/EchoServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMEchoServiceREST.getInteractionDetails();            
            Test.stopTest();
            HUMEchoRestResponseDTO_HUM echoResponse = (HUMEchoRestResponseDTO_HUM)System.JSON.deserialize(res.responseBody.toString(), HUMEchoRestResponseDTO_HUM.class);			
			System.assertNotEquals(echoResponse,null);
			System.assertEquals(res.statuscode,200);
    	}            
    }
    
    /*  Test method to unit test the scenario where Interacting About ID only is in the Request 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testInteracingAboutID()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
    		HUMEchoRestRequestDTO_HUM echoRequest = new HUMEchoRestRequestDTO_HUM();
    		HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO request = new HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO(); 
    		request.sInteractingAboutID = '1234567';
    		request.sInteractingAboutType = 'Member';
    		echoRequest.LastInteractionDetails = request;
    		RestRequest req = new RestRequest();
        	String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/EchoServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMEchoServiceREST.getInteractionDetails();            
            Test.stopTest();
            HUMEchoRestResponseDTO_HUM echoResponse = (HUMEchoRestResponseDTO_HUM)System.JSON.deserialize(res.responseBody.toString(), HUMEchoRestResponseDTO_HUM.class);			
			System.assertNotEquals(echoResponse,null);
			System.assertEquals(res.statuscode,200);
    	}            
    }
    
    /*  Test method to unit test the scenario with Empty Input Request 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testEmptyRequest()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
    		HUMEchoRestRequestDTO_HUM echoRequest = new HUMEchoRestRequestDTO_HUM();
    		HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO request = new HUMEchoRestRequestDTO_HUM.HUMEchoRestRequestDTO();     		
    		echoRequest.LastInteractionDetails = request;
    		RestRequest req = new RestRequest();
        	String JSONMsg = System.JSON.serialize(echoRequest);
            RestResponse res = new RestResponse();        
            req.requestURI = '/EchoServiceREST/*';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);        
            RestContext.request = req;
            RestContext.response= res;
            Test.startTest();
            HUMEchoServiceREST.getInteractionDetails();            
            Test.stopTest();
            HUMEchoRestResponseDTO_HUM echoResponse = (HUMEchoRestResponseDTO_HUM)System.JSON.deserialize(res.responseBody.toString(), HUMEchoRestResponseDTO_HUM.class);			
			System.assertNotEquals(echoResponse,null);
			System.assertEquals(res.statuscode,200);
			System.assertEquals(echoResponse.lastInteractionDetailsResponse.bIsSuccess,false);
    	}            
    }
    
    /* To Unit test setEchoOutput method 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testsetEchoOutputMethod()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
	    	list<Account> lstAccount = [Select Id from Account where Enterprise_ID__c = '1234567' limit 1];
	    	if(lstAccount!=null && !lstAccount.isEmpty())
	    	{
	    		list<Interaction__c> lstInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c,
	    											Interacting_With_type__c,Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Interacting_With__c = :lstAccount[0].Id];
	            
	            if(lstInteraction!=null && !lstInteraction.isEmpty())
	            {
		            list<Case_Interaction__c> lstCaseInt = [SELECT Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,
		                									Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,
		                									Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c,
		                									Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c, Case__r.LastModifiedby_Queue__c
		                									FROM Case_Interaction__c WHERE Interaction__c = :lstInteraction[0].Id ];
		            Test.startTest();
		            if(lstCaseInt!=null && !lstCaseInt.isEmpty())
		            {
		        		//Both Interaction and Case present
		        		HUMEchoRestResponseDTO_HUM testEchoResponse = HUMEchoServiceREST.setEchoOutput(lstInteraction[0], lstCaseInt[0]);	        		
		        		System.assertNotEquals(testEchoResponse,null);
		        		System.assertEquals(testEchoResponse.lastInteractionDetailsResponse.bIsSuccess,true);
		        		    	
		            }
		            //Only interaction present
		            HUMEchoRestResponseDTO_HUM testEchoResponse1 = HUMEchoServiceREST.setEchoOutput(lstInteraction[0], null);	            
		        	System.assertNotEquals(testEchoResponse1,null);    		            
		        	System.assertEquals(testEchoResponse1.lastInteractionDetailsResponse.bIsSuccess,true);
		        	
		        	//Both Interaction and Case absent
		        	HUMEchoRestResponseDTO_HUM testEchoResponse2 = HUMEchoServiceREST.setEchoOutput(null, null);	            
		        	System.assertNotEquals(testEchoResponse2,null);    		            
		        	System.assertEquals(testEchoResponse2.lastInteractionDetailsResponse.bIsSuccess,false);
		        	Test.stopTest();
	            }            
	    	}
    	}
    }
    
    /*  To Unit Test the getInteractionMemberDetails method 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testgetInteractionMemberDetailsMethod()
    {
	    User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{	
	    	list<Account> lstAccount = [Select Id from Account where Enterprise_ID__c = '1234567' limit 1];
	    	
	    	if(lstAccount!=null && !lstAccount.isEmpty())
	    	{
	    		Test.startTest();
	    		list<Interaction_Member__C> lstIntMem = HUMEchoServiceREST.getInteractionMemberDetails(lstAccount[0].Id,'Member',System.today(),System.today().addDays(3));	    		    	
	    		System.assertEquals(lstIntMem.isEmpty(),false);
	    		Test.stopTest();
	    	}
    	}
    }
    
    /*  To Unit Test the getCaseInteractionRecords method 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testgetCaseInteractionRecordsMethod()
    {
    	User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
	    	list<Account> lstAccount = [Select Id from Account where Enterprise_ID__c = '1234567' limit 1];	    	
	    	if(lstAccount!=null && !lstAccount.isEmpty())
	    	{
	    		list<Interaction__c> lstInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c,
	    											Interacting_With_type__c,Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Interacting_With__c = :lstAccount[0].Id];
	            	        	
	        	if(lstInteraction!=null && !lstInteraction.isEmpty())
	        	{    			
	    			list<Case_Interaction__c> lstCaseInt = HUMEchoServiceREST.getCaseInteractionRecords(lstInteraction[0].Id,System.today(),System.today().addDays(3));	    			
	    			System.assertNotEquals(lstCaseInt,null);
	    			System.assertEquals(lstInteraction[0].Id,lstCaseInt[0].Interaction__c);
	        	}
	    	}
    	}
    }
    
    /*  To Unit Test the locateAccount method 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void testlocateAccountMethod()
    {
	    User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{	
	    	String sAccountID = HUMEchoServiceREST.locateAccount('1234567', 'Member');    	  
	    	System.assertEquals(String.isNotBlank(sAccountID),true);
    	}
    }
    
    /*  To Unit Test the ErrorResponse method 
     * @ param    : None
     * @ return   : void
     */
    private static testMethod void  testErrorResponse()
    {
	    User ETL_User = [SELECT Id FROM User WHERE LastName = 'test User2'];
    	System.runAs(ETL_User)
    	{
	    	HUMEchoRestResponseDTO_HUM testErrResponse = HUMEchoServiceREST.createErrorResponse(null,false,System.Label.ECHOSERVICE_MISSINGINPUT_HUM);    	    
	    	System.assertNotEquals(testErrResponse,null);
	    	System.assertEquals(testErrResponse.lastInteractionDetailsResponse.bIsSuccess,false);
    	}
    }
              
}