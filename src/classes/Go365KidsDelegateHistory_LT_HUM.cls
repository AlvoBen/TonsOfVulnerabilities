/*******************************************************************************************************************************
Apex Class Name : Go365KidsDelegateHistory_LD_HUM
Version         : 1.0
Created On      : 12/30/2021
Function        : This class is used for performing Go365 kids delegation History data related work
Test Class      : Go365KidsDelegateHistory_LT_HUM
                 
Modification Log: 
* Developer Name                      Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Swapnali Sonawane                	  01/10/2022                  REQ# 2588478 -Go365 - Ability to View Go365 Kids Delegation History in a Lightning Experience
* Swapnali Sonawane                   07/14/2023                  US# 4812119  Critical- Lightning - Go365
***************************************************************************************************************************************************************/

@isTest
public class Go365KidsDelegateHistory_LT_HUM {
   
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
    * Method to test Go365 Kids Delegate History Section 
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
	    	Go365KidsDelegateHistory_LC_HUM oKidsDelHis = new Go365KidsDelegateHistory_LC_HUM();
	    	Account oAcc = [select Id, Name, Enterprise_Id__c from Account where Name = 'testacc' limit 1];
	    	oKidsDelHis.sEntIdHis = oAcc.Enterprise_Id__c;
	    	oKidsDelHis.sAgeHis = '17';
	    	Test.startTest();
	        Continuation oCon = (Continuation)Go365KidsDelegateHistory_LC_HUM.getKidsDelegateHistory(oKidsDelHis.sEntIdHis, oKidsDelHis.sAgeHis);
	        HttpResponse resMemDelHis = new Httpresponse ();
	        String sMemDelHis = '{ "GetDelegateResponseDTO":{ "DelegatesCollection":{ "Delegate":[ { "PersonId":"0266002655317", "Relationship":"subscriber", "PolicyType":"wellness",'+
	                         ' "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":"2017-07-01", "ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", '+
	                         '"Revoked":"false" }, { "PersonId":"0266002655317", "Relationship":"child", "PolicyType":"wellness", "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":'+
	                         '"2017-07-01", "ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", "Revoked":"false" }, { "PersonId":"0763507225889", '+
	                         '"Relationship":"child", "PolicyType":"wellness", "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":"2017-07-01", '+
	                         '"ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", "Status":"Added", "Revoked":"false" }, { "PersonId":"0884525058887", "Relationship":"child",'+
	                         ' "PolicyType":"wellness", "StartDate":"2016-03-26T23:07:19-05:00", "EndDate":"2017-07-01", "ModifiedDate":"2016-03-26T23:07:19-05:00", "AccessType":"read", '+
	                         '"Status":"Revoked", "Revoked":"false" } ] } } }';
	        resMemDelHis.setBody(sMemDelHis);
	        resMemDelHis.setStatusCode(200);
	        Test.setContinuationResponse('Continuation-1', resMemDelHis);
			Object oKidDelHis = Go365KidsDelegateHistory_LC_HUM.processMDHistory('Continuation-1');
			System.assertEquals(resMemDelHis.getBody(), sMemDelHis);
			Test.stopTest();
			//Go365KidsDelegateHistory_DTO_HUM:[Action=Added, Delegate=null null, EffectiveDate=03/26/2016], Go365KidsDelegateHistory_DTO_HUM:[Action=Added, Delegate=null null, EffectiveDate=03/26/2016]"
        }
    }
    
    /**
    * Method to test Go365 Kids Delegate History Section - negative scenario 
    * 
    * @param    None
    * @return   Void 
    */
    public static testMethod void testMemDelHisSectionNeg()
    {
    	User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        system.assert(ouser.id!=null);
        Go365KidsDelegateHistory_LC_HUM oKidsDelHis = new Go365KidsDelegateHistory_LC_HUM();
	   	oKidsDelHis.sAgeHis = '27';
        System.runAs (ouser)
        { 
        	Test.startTest();
        	Continuation oCon = (Continuation)Go365KidsDelegateHistory_LC_HUM.getKidsDelegateHistory('', oKidsDelHis.sAgeHis);
        	HttpResponse resMemDelHis = new Httpresponse ();
	        String sMemDelHis = '<><><>';
	        resMemDelHis.setBody(sMemDelHis);
	        resMemDelHis.setStatusCode(500);
	        system.assert(resMemDelHis.getBody() == sMemDelHis);
	        Test.setContinuationResponse('Continuation-1', resMemDelHis);
            Object oKidDelHis = Go365KidsDelegateHistory_LC_HUM.processMDHistory('Continuation-1');
            Continuation oConNeg = (Continuation)Go365KidsDelegateHistory_LC_HUM.getKidsDelegateHistory('ABC', '12');
            System.assertNotEquals(null, oConNeg);
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
             List<Account> lstAcc =(List<Account>)Go365KidsDelegateHistory_LC_HUM.getMemDelLst(lstAccMemDel);
             Assert.areEqual(2,lstAcc.size());
             Test.stopTest();
            
         }
    }
    
}