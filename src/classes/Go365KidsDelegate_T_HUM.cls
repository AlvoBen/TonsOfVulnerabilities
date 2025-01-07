/**
   @Author Gargey Sharma
   @name Go365KidsDelegate_T_HUM
   @CreateDate 12/21/2016
   @Description Test class for Go365 Member Delegate Section
   @Version 1.0
   @reference REQ - 290967
   @Test Class 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		70895                 12/21/2016                     Original version
 * Gargey Sharma         		70895                 02/17/2017                     Changes as per REQ - 301565
********************************************************************************************************************************/
@isTest
private class Go365KidsDelegate_T_HUM
{
    
    /**
    * Method to setup test data
    * 
    * @param    None
    * @return   Void 
    */
    @testSetup static void setup() 
    {  
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        
        system.runAs(oUser)
        {
	        HUM_Webservice_Callout__c oMemDelSer = new HUM_Webservice_Callout__c();
	        oMemDelSer.Name = 'MemberDelegatesService';
	        oMemDelSer.Certificate_Name__c = 'Test_Certificate';
	        oMemDelSer.Active__c = true;
	        oMemDelSer.End_Point_URL__c ='https://dev-crmservicews.humana.com/MemberDelegates_V1.0/GetMemberDelegates';
	        oMemDelSer.Timeout__c = '12000';
	        oMemDelSer.Request_Type__c = 'POST';
	        oMemDelSer.Content_Type__c ='application/json'; 
	        insert oMemDelSer;
	        system.assert(HUM_Webservice_Callout__c.getValues('MemberDelegatesService').Certificate_Name__c == 'Test_Certificate');
	        
	        // Fetching and inserting Account Record
	        Account objAcc = HUMTestDataHelperTest.getAccount();
	        objAcc.Name = 'testacc';
	        objAcc.Group_Number__c = '213654';
	        objAcc.Enterprise_Id__c = '0266002655317';
	        objAcc.Birthdate__c = '01/01/2017';
	        insert objAcc; 
	        system.assertEquals(objAcc.Group_Number__c=='213654',true);
        }
    } 
    
    /**
    * Method to test Go365 Kids Delegate Section 
    * 
    * @param    None
    * @return   Void 
    */
    public static testMethod void testMemDelSection()
    {
    	User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
	    	Go365KidsDelegate_C_HUM oKidsDel = new Go365KidsDelegate_C_HUM();
	    	Account oAcc = [select Id, Name, Enterprise_Id__c from Account where Name = 'testacc' limit 1];
	    	oKidsDel.sEntId = oAcc.Enterprise_Id__c;
	    	oKidsDel.sAge = '17';
	    	Test.startTest();
	        Continuation oCon = (Continuation)Go365KidsDelegate_C_HUM.getKidsDelegateDetails(oKidsDel.sEntId, oKidsDel.sAge);
	        HttpResponse resMemDel = new Httpresponse ();
	        String sMemDel = '{ "GetDelegateResponseDTO":{ "DelegatesCollection":{ "Delegate":[ { "PersonId":"0266002655317", "Relationship":"subscriber", "PolicyType":"wellness",'+
	                         ' "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":"2017-07-01", "ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", '+
	                         '"Revoked":"false" }, { "PersonId":"0884525058887", "Relationship":"child", "PolicyType":"wellness", "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":'+
	                         '"2017-07-01", "ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", "Revoked":"false" }, { "PersonId":"0763507225889", '+
	                         '"Relationship":"child", "PolicyType":"wellness", "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":"2017-07-01", '+
	                         '"ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", "Revoked":"false" } ] } } }';
	        resMemDel.setBody(sMemDel);
	        resMemDel.setStatusCode(200);
	        system.assert(resMemDel.getBody() == sMemDel);
	        Test.setContinuationResponse('Continuation-1', resMemDel);
	        Object oKidDel = Go365KidsDelegate_C_HUM.processMDS('Continuation-1');
	        Test.stopTest();
        }
    }
    
    /**
    * Method to test Go365 Kids Delegate Section - negative scenario 
    * 
    * @param    None
    * @return   Void 
    */
    public static testMethod void testMemDelSectionNeg()
    {
    	User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        Go365KidsDelegate_C_HUM oKidsDel = new Go365KidsDelegate_C_HUM();
	   	oKidsDel.sAge = '27';
        System.runAs (ouser)
        { 
        	Test.startTest();
        	Continuation oCon = (Continuation)Go365KidsDelegate_C_HUM.getKidsDelegateDetails('', oKidsDel.sAge);
        	HttpResponse resMemDel = new Httpresponse ();
	        String sMemDel = '<><><>';
	        resMemDel.setBody(sMemDel);
	        resMemDel.setStatusCode(500);
	        system.assert(resMemDel.getBody() == sMemDel);
	        Test.setContinuationResponse('Continuation-1', resMemDel);
	        Object oKidDel = Go365KidsDelegate_C_HUM.processMDS('Continuation-1');
	        Continuation oConNeg = (Continuation)Go365KidsDelegate_C_HUM.getKidsDelegateDetails('', 'ABC');
	        Test.stopTest();
        }
    }
    
    /**
    * Method to test display page message method
    * 
    * @param    None
    * @return   Void 
    */
    public static testMethod void testDisplayPgMsg()
    {
    	User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
        	Go365KidsDelegate_C_HUM oKidsDel = new Go365KidsDelegate_C_HUM();
        	Test.startTest();
        	oKidsDel.sAge = '9';
	        Pagereference pgRef = oKidsDel.displayPageMessage();
	        oKidsDel.sAge = '29';
	        Pagereference pgRefNeg = oKidsDel.displayPageMessage();
	        Test.stopTest();
        }
    }
}