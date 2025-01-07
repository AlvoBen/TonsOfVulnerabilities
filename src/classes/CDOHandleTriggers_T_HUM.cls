/*****************************************************************************************************************************************************************************
Apex Class Name : CDOHandleTriggers_T_HUM 
Version         : 1.0
Created On      : 6/18/2019
Function        : Test Class for CDOHandleTriggers_C_HUM

Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Lakshmi Madduri												6/18/2019				CDO Implementation 
*T.Sai Kumar Reddy											    8/5/2021				REQ 2441688 CDO Patient Relations Region Administration: Change Picklists to Lookups
*Nikhil M														2/24/2022				REQ - 2824031 Assigning CDO Administrator Permission set to test User.
*Rahul Kumar                                                    09/13/2022              REQ - 3699542 T1PRJ0342659 - MF1 - PCO Make Assigned to Region Required on Contact
**************************************************************************************************************************************************/

@isTest
public class CDOHandleTriggers_T_HUM {
    /*
    * Method Name   :   setup
    * Description   :   Calls the CDO test data factory to create the records needed for testing.
    * Return Type   :   void
    * Parameters    :   NA
    */
    @testSetup static void setup () 
	{
        list<User> userList = new list<User>();
        userList.add(HUMTestDataHelperTest.createTestUser('User2', 'CDO User'));
        insert userList;
        System.assert(userList.size() == 1);
        
        list<String> psNameList = new list<String>{CDO_CONSTANT_HUM.STRING_CDO_ADMIN_PERMISSION_SET};
        list<PermissionSetAssignment> psAssgn = CDOTestDataFactory_H_HUM.prmsnSetAssgn(userList, psNameList, true);
        System.assert(psAssgn.size() == 1);
        
        System.runAs(userList[0])
        {
            list<string> regionList = new list<string>{CDO_CONSTANT_HUM.STRING_REGION_FLORIDA ,CDO_CONSTANT_HUM.STRING_REGION_HOUSTON};
            list<CDO_Region__c> objRgn = new list<CDO_Region__c>();
            objRgn =  CDOTestDataFactory_H_HUM.createRegion(regionList);
            if( objRgn!= null && !objRgn.isEmpty() )
            {
                insert objRgn;
            }
            System.assertEquals(2, ObjRgn.size());
            
            //Create Location
            CDO_Locations__c testLoc = CDOTestDataFactory_H_HUM.createLocation('TestLocHandle1',true);
            //Create a new lead
            List<Lead> allLeads = CDOTestDataFactory_H_HUM.createLeads(1,testLoc);
            //Create a patient (person account)
            List<Account> allPatients = CDOTestDataFactory_H_HUM.createPatients(1,testLoc);
            //Create an account for the agent to work for		
            List<Account> agentAccounts = CDOTestDataFactory_H_HUM.createAgentAccounts(1,testLoc);
            //Create a contact (agent)
            List<Contact> agents = CDOTestDataFactory_H_HUM.createContacts(1, agentAccounts[0],objRgn[0]);
            //Create a new gift 	        
            List<CDO_Gift__c> allGifts = CDOTestDataFactory_H_HUM.createGifts(1);
            //Create a gift given (junction between lead and gift) for a lead
            List<CDO_Gifts_Given__c> allGiftsGivenToLeads = CDOTestDataFactory_H_HUM.createGiftsGiven(1,allGifts[0],allLeads[0],10.99);
            //Create a gift given (junction between patient and gift) for a patient
            List<CDO_Gifts_Given__c> allGiftsGivenToPatients = CDOTestDataFactory_H_HUM.createGiftsGiven(1,allGifts[0],allPatients[0],5.99);        
            //Create an appointment for the lead
            List<CDO_Appointment__c> allAppointments = CDOTestDataFactory_H_HUM.createVisitedDoctorAppointment(1,allLeads[0]);
            system.assertEquals(1, allLeads.size());
        }       
    }
    /*
    * Method Name   :   testDeleteUsingLeads 
    * Description   :   This test ensures the deleting of a gift (which hits on the trigger and flow) works correctly.
		                It also ensures that the link betwen lead and gift given is correct.
    * Return Type   :   void
    * Parameters    :   NA
    */
    @isTest static void testDeleteUsingLeads() 
	{
        User testUser = [Select Id, Name from User where LastName = 'test User2'];
        System.runAs(testUser)
        {
            Test.startTest();
			//Grab the data created in the test setup
	        Lead theLead = [SELECT Id, CDO_Total_of_Gifts_Given_this_year__c FROM Lead LIMIT 1];
			String tempId = theLead.Id;
        	tempId = tempId.substring(0,tempId.length()-3);
        	//Internally salesforce uses the 18 digit ID but the parent Id field stores the 15 (the cost is too great in the formula to use
        	//18 digits there). We truncate the last 3 digits (for case insensitivity) for the query to return our lead. Salesforce
        	//handles the conversion autoamtically when using ID fields but our formula is a text return.
			List <CDO_Gifts_Given__c> theGiftsGiven = [SELECT Id, CDO_Internal_Parent_Record_Id__c FROM CDO_Gifts_Given__c WHERE CDO_Internal_Parent_Record_Id__c = :tempId];
    		delete(theGiftsGiven);		
			//After we have deleted the gift, we need to grab our lead again and see if the
			//total gifts value has changed.
	        Lead grabLeadAgain = [SELECT Id, CDO_Total_of_Gifts_Given_this_year__c FROM Lead LIMIT 1];
            Test.stopTest();       
            //Ensure the total is now 0 to ensure the flow and trigger worked. This actually
            //tests the delete trigger, CDO_HandleTriggers (apex class to keep logic out of 
            //the trigger) and the flow that calculates the gifts for the leads. For sanity,
            //we also test to ensure the lead gift total amount is correctly 10.99 to start (which is
            //the value when we create the gift given)
            system.assertEquals(10.99, theLead.CDO_Total_of_Gifts_Given_this_year__c);
            system.assertEquals(0, grabLeadAgain.CDO_Total_of_Gifts_Given_this_year__c);
        }       
    }
    
    /*
    * Method Name   :   testAdditionUsingPatient 
    * Description   :   This test ensures the addition of a gift adds up correctly and ensures the gift link to the
    	                patient works (person account)
    * Return Type   :   void
    * Parameters    :   NA
    */
    @isTest static void testAdditionUsingPatient() 
	{
        User testUser = [Select Id, Name from User where LastName = 'test User2'];
        System.runAs(testUser)
        {
            Test.startTest();
        	//Grab our pateint (person account)
        	Account thePatient = [SELECT Id,CDO_Total_of_Gifts_Given_this_year__pc FROM Account LIMIT 1];
        	//Create a new gift to give to the patient
	        //Get the gift record
	        CDO_Gift__c aGift = [SELECT Id FROM CDO_Gift__c LIMIT 1];
        	List<CDO_Gifts_Given__c> giftGiven = CDOTestDataFactory_H_HUM.createGiftsGiven(1,aGift,thePatient,2.02);
	        //Now that the gift has been added, we grab the patient record again
        	Account thePatientAgain = [SELECT Id,CDO_Total_of_Gifts_Given_this_year__pc FROM Account LIMIT 1];
            Test.stopTest();
            //We verify to make sure that the original gift (5.99) and the gift added above (2.02) matches the total correctly
            system.assertEquals(8.01, thePatientAgain.CDO_Total_of_Gifts_Given_this_year__pc);
        }       
    }
    /*
    * Method Name   :   testOldGiftsUsingAgents 
    * Description   :   This test ensures that gifts older than a year are not calculated and also ensure the link
		                to the agent (contact) works. This gets tricky as when you create the original gift given and insert it
		                the flow will be trigger and the gift total added (correctly). We can only change the created date after
		                the record was created. So we create a gift given (total calculated) and then set the created date back,
		                we then create a second gift to force the flow to run again. Now when it runs, the older record shold 
		                corectly be ignored. Everything is created before the test as the created date modification cannot occur
		                within the start test
    * Return Type   :   void
    * Parameters    :   NA
    */
    @isTest static void testOldGiftsUsingAgents() 
	{
        User testUser = [Select Id, Name from User where LastName = 'test User2'];
        System.runAs(testUser)
        {
            //Grab the agent
			Contact theAgent = [SELECT Id, CDO_Total_of_Gifts_Given_this_year__c, LastName FROM Contact WHERE LastName='Agent' Limit 1];
			//Get the gift record
			CDO_Gift__c aGift = [SELECT Id FROM CDO_Gift__c LIMIT 1];
			//Create an old gift
			List<CDO_Gifts_Given__c> oldGiftGiven = CDOTestDataFactory_H_HUM.createGiftsGiven(1,aGift,theAgent,2.00);
			//Gift given out during the calendar year are calculated so we set the date here to dec 31st of last
			//year and this gift amount should not be added.
			test.setCreatedDate(oldGiftGiven[0].Id, DateTime.newInstance(Date.today().year() - 1,12,31));

			//Create a new gift from Jan 1 of this year, this gift should count in the total.
			List<CDO_Gifts_Given__c> newGiftGiven = CDOTestDataFactory_H_HUM.createGiftsGiven(1,aGift,theAgent,4.01);
			
			test.setCreatedDate(newGiftGiven[0].Id, DateTime.newInstance(Date.today().year(),1,1));
			test.startTest();
				//Requery to get the cdo gift value
				Contact theAgentAgain = [SELECT Id, CDO_Total_of_Gifts_Given_this_year__c FROM Contact WHERE LastName='Agent' Limit 1];
				//Ensure the 2 gifts are correctly tied to the record
				String tempId = theAgentAgain.Id;
				tempId = tempId.substring(0,tempId.length()-3);
				//Internally salesforce uses the 18 digit ID but the parent Id field stores the 15 (the cost is too great in the formula to use
				//18 digits there). We truncate the last 3 digits (for case insensitivity) for the query to return our lead. Salesforce
				//handles the conversion autoamtically when using ID fields but our formula is a text return.
				List <CDO_Gifts_Given__c> theGifts = [SELECT Id FROM CDO_Gifts_Given__c WHERE CDO_Internal_Parent_Record_Id__c = :tempId];
				test.stopTest();
				//The original gift is 4.01 and this new gift should not count against the total so we verify the total
				//is 4.01 and that there are 2 gift given records attached. We test for the 2 gifts to ensure they are
				//both run through the flow. 
				system.assertEquals(4.01, theAgentAgain.CDO_Total_of_Gifts_Given_this_year__c);
				system.assertEquals(2, theGifts.size());
			}       
    }
    /*
    * Method Name   :   testLeadConversion 
    * Description   :   This test ensures that leads convert correctly and that the gifts and appointments they held
    *                   follow them correctly
    * Return Type   :   void
    * Parameters    :   NA
    */
    
    @isTest static void testLeadConversion() 
	{
        User testUser = [Select Id, Name from User where LastName = 'test User2'];
        System.runAs(testUser)
        {
             //Grab the lead
			List<Lead> theLeads = [SELECT Id FROM Lead];
			List<CDO_Appointment__c> theAppointments = [SELECT Id FROM CDO_Appointment__c WHERE CDO_Prospective_Patient__c = :theLeads[0].Id];
			List<CDO_Gifts_Given__c> theGifts = [SELECT Id FROM CDO_Gifts_Given__c WHERE CDO_Prospective_Patient__c = :theLeads[0].id];
			//Sanity check to ensure we have a lead and that lead has a gift and an appointment
			System.assertEquals(1, theLeads.size());
			System.assertEquals(1, theAppointments.size());
			System.assertEquals(1, theGifts.size());
			//Update lead fields that are required for convserion
			theLeads[0].CDO_Prospect_ID__c = 'QQQBatmanSymbol';
            theLeads[0].Rating = 'Hot';
			theLeads[0].CDO_Next_Steps__c = 'Schedule Appointment';
			theLeads[0].Status = 'PCP Appointment Scheduled';
			theLeads[0].LastName = 'ConvertTest';
            update theLeads[0];
			Test.startTest();
            CDOHandleTriggers_C_HUM.GiftGivenDelete(null);
        	CDO_Locations__c testLoc = CDOTestDataFactory_H_HUM.createLocation('TestLocHandle2',true);
            //Convert the lead
        	List<Account> patientAccount = CDOTestDataFactory_H_HUM.createPatients(1,testLoc);
            
        	Database.LeadConvert lc = new database.LeadConvert();
	        lc.setLeadId(theLeads[0].id);
            lc.setAccountId(patientAccount[0].id);
    	    LeadStatus conversionStatus = [SELECT Id, ApiName, MasterLabel FROM LeadStatus WHERE ApiName ='Closed - Converted' LIMIT 1];
        	lc.setConvertedStatus(conversionStatus.ApiName);
        	Database.LeadConvertResult lcr = Database.convertLead(lc);
			Test.stopTest();
			//Did the lead convert?
			System.assertEquals(lcr.isSuccess(), true);
        }            
    }
}