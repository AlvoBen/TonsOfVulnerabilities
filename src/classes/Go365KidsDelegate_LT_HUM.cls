/**
   @Author Sagar Gulleve
   @name Go365KidsDelegate_LT_HUM
   @CreateDate  21/01/2022
   @Description Test class for Go365 Member Delegate Section 2588464
   @Version 1.0
   @reference REQ - 2588464
   @Test Class 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Sagar Gulleve        		                       21/01/2022                   Original version
 * Swapnali Sonawane                                   07/14/2023                   US# 4812119  Critical- Lightning - Go365
********************************************************************************************************************************/
@isTest
private class Go365KidsDelegate_LT_HUM
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
            objAcc.Individual_First_Name__c = 'Test Name';
            objAcc.Individual_Last_Name__c = 'Test2 Name';
	        insert objAcc; 
	        system.assertEquals(objAcc.Group_Number__c=='213654',true);
           
            Account objAcc1 = HUMTestDataHelperTest.getAccount();
	        objAcc1.Name = 'testacc1';
	        objAcc1.Group_Number__c = '213654';
	        objAcc1.Enterprise_Id__c = '02660026553172';
	        objAcc1.Birthdate__c = '01/01/2017';
            objAcc1.Individual_First_Name__c = 'Test Name1';
            objAcc1.Individual_Last_Name__c = 'Test2 Name1';
	        insert objAcc1; 
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
	    	Go365KidsDelegate_LC_HUM oKidsDel = new Go365KidsDelegate_LC_HUM();
	    	Account oAcc = [select Id, Name, Enterprise_Id__c from Account where Name = 'testacc' limit 1];
	    	oKidsDel.sEntId = oAcc.Enterprise_Id__c;
	    	oKidsDel.sAge = '17';
	    	Test.startTest();
	        Continuation oCon = (Continuation)Go365KidsDelegate_LC_HUM.getKidsDelegateDetails(oKidsDel.sEntId, oKidsDel.sAge);
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
	        Object oKidDel = Go365KidsDelegate_LC_HUM.processMDS('Continuation-1');
			system.assertNotEquals(null, sMemDel);
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
        Go365KidsDelegate_LC_HUM oKidsDel = new Go365KidsDelegate_LC_HUM();
	   	oKidsDel.sAge = '27';
        System.runAs (ouser)
        { 
        	Test.startTest();
        	Continuation oCon = (Continuation)Go365KidsDelegate_LC_HUM.getKidsDelegateDetails('', oKidsDel.sAge);
        	HttpResponse resMemDel = new Httpresponse ();
	        String sMemDel = '<><><>';
	        resMemDel.setBody(sMemDel);
	        resMemDel.setStatusCode(500);
	        system.assert(resMemDel.getBody() == sMemDel);
	        Test.setContinuationResponse('Continuation-1', resMemDel);
	        Object oKidDel = Go365KidsDelegate_LC_HUM.processMDS('Continuation-1');
	        Continuation oConNeg = (Continuation)Go365KidsDelegate_LC_HUM.getKidsDelegateDetails('ABC','12');
			system.assertNotEquals(null, sMemDel);
	        Test.stopTest();
        }
    }
    
     static testMethod void getMemDelLstpositive()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
         System.runAs (ouser)
         {
             Test.startTest();
             List<String> lstAccMemDel = new List<String>{'02660026553172','0266002655317'};
             List<Account> lstAcc =(List<Account>)Go365KidsDelegate_LC_HUM.getMemDelLst(lstAccMemDel);
             Assert.areEqual(2,lstAcc.size());
             Test.stopTest();  
         }       
    }
}