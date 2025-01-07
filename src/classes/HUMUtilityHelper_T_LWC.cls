/************************************************************************************************************************
Apex class Name    : HUMUtilityHelper_T_LWC
Version            : 1.0
Function           : Test Class for HUMUtilityHelper_LWC.
Created On         : Feb 15 2022

Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Manoj M K                						2/15/2022          Original Version
*   Muthukumar Velmurugan							04/14/2023		   US# 4313675
*   Gowthami Thota                                  07/31/2023         Test Class Fix for UAT 
***************************************************************************************************************************/

@isTest
private class HUMUtilityHelper_T_LWC
{
    
    /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup static void setup() 
    {    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__C = 'Louisville Code Editing';
        oUser.Mentor_Sub_Function__c='Individual Medicare';
        insert oUser; 
        System.assertEquals(oUser.LastName,'test User1');
          
        User objUserHumanaSpecialist = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert objUserHumanaSpecialist;
        
         User objUserHumanaSupervisor = HUMTestDataHelperTest.createTestUser('User3','Humana Pharmacy Specialist');   
        insert objUserHumanaSupervisor;
        List<PermissionSet> humanaSupervisorPS = [Select id , Name from PermissionSet where name ='CRMS_300_Humana_Pharmacy_Supervisor'];
        if(humanaSupervisorPS != null	)
        {
        	insert new PermissionSetAssignment(assigneeId = objUserHumanaSupervisor.id , PermissionSetId =humanaSupervisorPS[0].id);
        }
        system.runAs(oUser)
        {
        Pharmacy_Supervisor_HUM__c supervisorCS = New Pharmacy_Supervisor_HUM__c(Name = 'CRMS_300_Humana_Pharmacy_Supervisor');
	        insert supervisorCS;
        }
        User objUserPDPPilot = HUMTestDataHelperTest.createTestUser('User4', 'Humana Pharmacy Specialist');
		insert objUserPDPPilot;
        
        List<PermissionSet> hpPDPPS = [Select id, Name from PermissionSet where name = 'CRMS_205_CCSPDPPharmacyPilot'];
		if (hpPDPPS!= null)
		{
			insert new PermissionSetAssignment(assigneeId = objUserPDPPilot.id, PermissionSetId = hpPDPPS[0].id);
		}
        
        User oAdminUser = HUMTestDataHelperTest.createTestUser('Admin','system Administrator');
        oAdminUser.Current_Queue__C = 'Cincinati CRU1';
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test Admin');
        
        system.runAs(oAdminUser)
        {
            User oCCS = [ SELECT Id, UserName , Name FROM User WHERE LastName = 'test User1' ];
            
			List<Classification__c> lstClassificationNew = New List<Classification__c>();
			Classification__c classificationRecf1 = New Classification__c();
			classificationRecf1.Name = 'Complaints or Compliments';
			classificationRecf1.Is_Active__c = true;
			lstClassificationNew.add(classificationRecf1);
			insert lstClassificationNew;

			List<Intent__c> intentList = New List<Intent__c>();
			Intent__c intentRef1 = New Intent__c();
			intentRef1.Name = 'QAA';
			intentRef1.Is_Active__c = true;
			intentList.add(intentRef1);
			insert intentList;
			
			Work_Queue_Setup__c wQueue = new Work_Queue_Setup__c();
			wQueue.Name = 'Louisville Code Editing';
			insert wQueue;
		   
            List<RecordType> lstRecordTypeMemberIds = [SELECT Id  FROM RecordType WHERE Name = 'Member' and sobjectType = 'Account' LIMIT 1 ];
            id idMemberAccountRecordType = lstRecordTypeMemberIds[0].Id;
            
            Account oAccount = new Account(FirstName='Test' , LastName='Account1', recordtypeid=idMemberAccountRecordType);
            insert oAccount;
            system.assertEquals(oAccount.FirstName,'Test','Value not matched');
            
			List<SlaProcess> objSlaList = [Select Id,Name,createddate,VersionNumber,IsVersionDefault,VersionMaster from SlaProcess Where IsActive = True and Name = 'Member Issue Resolution Process' order by VersionNumber desc];

            
			Entitlement entl = new Entitlement();
			entl.Name = 'Member Issue Resolution SLA';
		    entl.StartDate = System.today();
		    entl.EndDate =  System.today().addYears(2);
		    entl.Type = 'Phone Support';
		    entl.AccountId = oAccount.Id;
		    entl.SlaProcessId = objSlaList[0].Id;
			insert entl;
			
            Case caseRecord = New Case();
            caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
            caseRecord.Status = 'In Progress';
            caseRecord.AccountId = oAccount.id;
            caseRecord.ownerId = oUser.id;
            caseRecord.Classification_Type__c = 'Calls (RSO)';
            caseRecord.Classification_id__c=lstClassificationNew[0].id;
            caseRecord.Intent_id__c = intentList[0].id;
            caseRecord.Interacting_With__c = oAccount.id;
            caseRecord.Interacting_With_Type__c = 'Member';
            caseRecord.Interacting_About_Type__c = 'Member';
            caseRecord.Policy_Member__c = null;
            caseRecord.Call_Benefit_Category__c = 'Medicare Part C';
            caseRecord.Autoroute_Status__c = 'Evaluate';
            caseRecord.Origin = 'Inbound Call';
			caseRecord.EntitlementId = entl.Id;
			caseRecord.Work_Queue_View_Name__c = entl.Id;
            insert caseRecord;
        }
        
	}
 
 
 
    /*
     * Method Name : testisCRMfunctionalityON
     * Description : This method is used to validate CRM Functionality ON switch
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testisCRMfunctionalityON()
    {
        User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        {
            CRMFunctionality_ONOFF_Switch__c OSBONOFF= new CRMFunctionality_ONOFF_Switch__c();
            OSBONOFF.Name='738134';
            Insert OSBONOFF;
			
            Boolean deduceosb;
            Test.startTest();
            
			deduceosb = HUMUtilityHelper_LWC.isCRMFunctionalityON('738134');
			deduceosb = HUMUtilityHelper_LWC.isCRMFunctionalityON(null);
			
			List<String> str = new List<String>();
            str.add('738134');
            str.add('738135');
            HUMUtilityHelper_LWC.isCRMFunctionalityONJS(str);
            HUMUtilityHelper_LWC.isCRMFunctionalityONJS(null);
                
           Test.stopTest();
         }
    }
	
	
	
     /*
     * Method Name : testisCRMfunctionalityOFF
     * Description : This method is used to validate CRM Functionality OFF switch
     * Return type : void
     * Paramater   : void
     */
    static testMethod void testisCRMfunctionalityOFF()
    {
        Boolean deduceosb;
		Test.startTest();		
			deduceosb = HUMUtilityHelper_LWC.isCRMFunctionalityON('0x0112345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890');	
	    Test.stopTest();
    }
    
    
	 /**
    * Returns void
    * <p>
    * test method for doSort() method of HUMUtilityHelper_LWC class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestSortMethod()
    {
         User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            List<String> lstStrings = new List<String>();
            Set<String> setStrings = new Set<String>();
            setStrings.add('Calls');
            setStrings.add('CRU');
            setStrings.add('eCommerce');
            Test.startTest();
            lstStrings = HUMUtilityHelper_LWC.doSort(setStrings);
            Test.stopTest();
            System.assert(lstStrings != NULL); 
        }
    }
	

    /**
    * Returns void
    * <p>
    * test method for getlstSubFunctions() method of HUMUtilityHelper_LWC class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestgetlstSubFunctions()
    {
        User oUser = [select id, name,lastname,Current_Queue__C,Mentor_Sub_Function__c from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            Test.startTest();
            List<String> lstSelectOptn = HUMUtilityHelper_LWC.getlstSubFunctions('Contact Center GBO',oUser,'');
            System.assert(lstSelectOptn!=null); 
            Test.stopTest(); 
        }
    }
	

	 /**
    * Returns void
    * <p>
    * test method for fetchUserMentorFunction() method of HUMUtilityHelper_LWC class  
    *
    * @return  Void
    * @see
    */
    static testMethod void TestfetchUserMentorFunction()
    {
        User oUser = [select id, name,lastname,Current_Queue__C,Mentor_Sub_Function__c from user where lastname='test User1' limit 1];
        system.assertEquals(oUser.lastname =='test User1',true); 
        system.runAs(oUser)
        { 
            Test.startTest();
            String sFunction = HUMUtilityHelper_LWC.fetchUserMentorFunction(oUser);
            user userDetails= HUMUtilityHelper_LWC.fetchUserDetails();
            Test.stopTest(); 
        }
    }
	

	/*
	* Method Name : testHumanaPharmacyUserNegative
	* Description : This method Is used To test the Negative scenarios for Humana Pharmacy User
	* Return type : None
	* Parameter   : None
	*/
    Static testMethod void testHumanaPharmacyUserNegative()
    {
        User testUser = [Select Id, Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
        	String sProfile = HUMUtilityHelper_LWC.isPharmacyUser();
        	system.assert(sProfile == HumConstants_LWC.NONPHARMACY_USER_HUM);
        }
     }
    
    /*
	* Method Name : testHumanaPharmacySpecialistUser
	* Description : This method Is used To test Humana Pharmacy Specialist User
	* Return type : None
	* Parameter   : None
	*/ 
    Static testMethod void testHumanaPharmacySpecialistUser()
    {
        User testUser = [Select Id, Name from User where LastName='test User2'];
        System.runAs(testUser)
        {
        	String sProfile = HUMUtilityHelper_LWC.isPharmacyUser();
        	system.assert(sProfile == HumConstants_LWC.PHARMACYSPECIALIST_USER_HUM);	
        }
     }
    
    /*
	* Method Name : testHumanaPharmacySupervisorUser
	* Description : This method Is used To test Humana Pharmacy Supervisor User
	* Return type : None
	* Parameter   : None
	*/  
    Static testMethod void testHumanaPharmacySupervisorUser()
    {
        User testUser = [Select Id, Name from User where LastName='test User3'];
        
        System.runAs(testUser)
        {
        	String sProfile = HUMUtilityHelper_LWC.isPharmacyUser();
            
        	system.assert(sProfile == HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM);
        }
     }
	
	 /*
	* Method Name : testHPPDPUser
	* Description : This method Is used To test PDP Pilot user
	* Return type : None
	* Parameter   : None
	*/
	Static testMethod void testHPPDP()
	{
		boolean sProfile1 = HUMUtilityHelper_LWC.isPDPPilotUser();
		User testUser = [Select Id, Name from User where LastName = 'test User4'];
		System.runAs(testUser)
		{
			boolean sProfile = HUMUtilityHelper_LWC.isPDPPilotUser();
			system.assert(sProfile == true);
		}
	}
	
	
	/**
    * Returns void
    * test method for checkCustomPermissionAccess method of HUMUtilityHelper_LWC class 
    *
    * @return  Void
    * @see
    */
	 static testMethod void testCheckCustomPermissionAccessValid()
    {
      	User oUser = [select id, name,lastname from user where lastname='test User1' limit 1];
		Boolean bHasAccessPS_ANY = HUMUtilityHelper_LWC.isUserhavingPermissionSetAccess(null);
        
        system.runAs(oUser)
        {   
            Test.StartTest();
			
						
			//isUserhavingPermissionSetAccess
			Boolean bHasAccessPS_ANY1 = HUMUtilityHelper_LWC.isUserhavingPermissionSetAccess(HumConstants_LWC.MIR_PERMISSIONSET_HUM);
			
			//isUserHavingMedicareCustomerServiceAccess
			Boolean bHasAccessMedicare = HUMUtilityHelper_LWC.isUserHavingMedicareCustomerServiceAccess();
			
			//isMIRPilotUser
			Boolean bHasAccessMIR = HUMUtilityHelper_LWC.isMIRPilotUser();
			
			//formatMinutes
			Case oCase = [select id from Case where Owner_Queue__c = 'Louisville RSO Calls Support' limit 1];
					  
			List<CaseMilestone> listOfCaseMilestones = [Select id,caseId,milestoneType.name from CaseMilestone where caseId =:oCase.id];
			
			HUMUtilityHelper_LWC.formatMinutes(new CaseMilestone());
	
            Test.stopTest();
            
	    }
	}
}