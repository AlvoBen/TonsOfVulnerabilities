/**
   @Author      : Pradeepkumar Dani
   @name        : AttachFiles_C_HUM
   @CreateDate  : 10/30/2017
   @Description : This class serves as test class for AttachFiles_C_HUM component
   @Version     : 1.0
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani          146713                  11/10/2017                     Original version   
 * Pradeepkumar Dani		  160881				  12/22/2017                     Enhancements
 * Mohammed Noor              268720                  08/08/2018                     Code Coverage for REQ - 362124.
********************************************************************************************************************************/
@isTest
private with sharing class AttachFiles_T_HUM 
{ 
    /*
    * This is the test method which loads data for AttachFiles_T_HUM. 
    * @param     NA
    * @return    void 
    */ 
    @testSetup static void testData()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.Lastname == 'test User1',true);
         
        System.runAs(u)
        {        
            Account acc1 = HUMTestDataHelperTest.getAccount();
            acc1.Name = 'Test Account Cases1';
            acc1.Birthdate__c='01/15/1951';
            acc1.Enterprise_ID__c = '1005447107';
            insert acc1;        
            system.assertEquals(acc1.Name == 'Test Account Cases1',true);            
        
            Policy__c pol = HUMTestDataHelperTest.getPolicy();
            pol.Group_Name__c = acc1.Id;
            pol.Name = 'Test Policy';
            insert pol;
            system.assertEquals(pol.Name,'Test Policy');
            system.assertEquals(pol.Group_Name__c == acc1.Id,true);            
        
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1';
            polMember.Policy__c=pol.Id;
            polMember.Member__c=acc1.Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1');
            system.assertEquals(polMember.Policy__c,pol.Id);  
            
            List<Classification_Type__c> ctList= new list<Classification_Type__c>();
	        Classification_Type__c ctRec = new Classification_Type__c();
	        ctRec.name = 'Claims (RSO)';
	        ctRec.Is_Active__c=true;
	        ctList.add(ctRec);
	        
	        insert ctList;
	        system.assertEquals(ctList.size(),1);
	        
	        Work_Queue_Setup__c wqsRecord = new Work_Queue_Setup__c();
            wqsRecord.Name = 'Cincinnati CRU';
            wqsRecord.Classification_Type__c = ctRec.Id;
            insert wqsRecord;
            system.assertEquals(wqsRecord.Name, 'Cincinnati CRU');
            
            List<Classification__c> lstClassification = new List<Classification__c>();
            Classification__c classificationRec = new Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);    
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
                
            List<Intent__c> lstIntent = new List<Intent__c>();
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);                
            insert lstIntent;
            system.assertEquals(lstIntent[0].Name=='Filling',true);
            
            List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
	        CTCI_Junction__c ctciRec = new CTCI_Junction__c();
	        ctciRec.Classification_Type__c = ctRec.id;
	        ctciRec.Classification__c = classificationRec.Id;
	        ctciRec.Intent__c = intentRec.id;
	        ctciRec.IsActive__c = true;
	        ctciList.add(ctciRec);
	        insert ctciList;
	        system.assertEquals(ctciList.size(),1);
            
            String sCaseRecordTypeName = 'Medicare Case';      
            Id caseRecordTypeId = Schema.SObjectType.Case.getRecordTypeInfosByName().get(sCaseRecordTypeName).getRecordTypeId();
			Case oCase = new Case(	AccountId=acc1.Id,
									recordTypeId = caseRecordTypeId,
            						CTCI_List__c = ctciRec.Id,
            						General_Case__c = true,
            						Interacting_With_Type__c = 'Group',
            						Interacting_With__c=acc1.Id, 
            						Due_Date__c=Date.Today(), 
            						Case_Owner__c=UserInfo.getUserName(), 
            						Owner_Queue__c='Cincinnati CRU1', 
            						Work_Queue_View_Name__c='Cincinati CRU1',
            						Service_Center__c ='Cincinnati',
            						Department__c='CRU1', 
            						status='In Progress',
									Type = 'Customer Inquiry',                                    
                                    Subtype__c = 'Incomplete DE SNP');
            insert oCase;
            system.assertNotEquals(oCase,null,'Value not matched');
            System.assert(oCase.Id == [SELECT Id FROM Case LIMIT 1].Id);
        }
    }
    
    /*
     * This is the test method which is used to test the AttachFiles_C_HUM. 
     * Return type : Void
     * Paramater   : NA
     */     
	private static testMethod void testAttachFiles()
	{
		User u = [SELECT Id FROM User  WHERE Lastname = 'test User1' LIMIT 1];
		system.runAs(u)
		{
			Test.StartTest();
			case cs = [SELECT Id, status FROM Case LIMIT 1];
				System.assertEquals(cs.status, 'In Progress');

				String attId = AttachFiles_C_HUM.createAttRecord(cs.Id, 'Test File');
				System.assertEquals(attId, [SELECT Id FROM Attachment__c LIMIT 1].Id);

				list<String> attIds = new list<String>();
				attIds.add(attId);
				boolean bTestResult = AttachFiles_C_HUM.createAttachmentMstone(attIds, cs.Id);
				system.assertEquals(bTestResult, true);
				
				boolean result = AttachFiles_C_HUM.deleteAttRecords(attIds);
				System.assertEquals(result, true);

				try
				{
					attId = AttachFiles_C_HUM.createAttRecord('False ID', 'Test File');
				}
				catch (Exception e)
				{
					System.assertEquals(attId, null);
				}
				Test.StopTest();
		}
	}
}