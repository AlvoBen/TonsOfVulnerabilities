/******************************************************************************************************************************
Apex Class Name  : CRMRetail_FirstTimeVisitor_T_HUM
Version          : 1.0 
Created Date     : 03/21/2023
Function         : To cover the CRMRetail_FirstTimeVisitor_B_HUM class 
Test Class For   : CRMRetail_FirstTimeVisitor_B_HUM
Modification Log :
* Developer              Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohamed Thameem      	                	    07/10/2023           US - 4807673 : T1PRJ0154546 / SF / MF9 Storefront - One-Time Update of Event Interactions
*********************************************************************************************************************************/
@isTest public class CRMRetail_FirstTimeVisitor_T_HUM {    
    @testSetup
    static void setup() 
    {
        Id crmRtlVstr =  SObjectType.Account.getRecordTypeInfosByDeveloperName().get('CRMRetail_Visitor').getRecordTypeId();        
        List<user> userList = new List<user>();
        
        User crmUser = HUMTestDataHelperTest.createTestUser('User1','CRMRetail User');
        userList.add(crmUser);

        User readOnly = HUMTestDataHelperTest.createTestUser('Read','Read Only');
        userList.add(readOnly);
        
        User deploymentUser = HUMTestDataHelperTest.createTestUser('Deployment','Deployment');
        userList.add(deploymentUser);
        
        User adminUser = HUMTestDataHelperTest.createTestUser('Admin','System Administrator');
        userList.add(adminUser);
        
        insert userList;
        assert.areEqual(4,userList.size());
        assert.areNotEqual(NULL,adminUser.Id);
        assert.areNotEqual(NULL,deploymentUser.Id);
        assert.areNotEqual(NULL,readOnly.Id);
        assert.areNotEqual(NULL,crmUser.Id);

        PermissionSet SuperAdmin = [SELECT Id FROM PermissionSet WHERE Name = :GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN];
        PermissionSetAssignment supAdmin = new PermissionSetAssignment(AssigneeId = userList[0].id, PermissionSetId = SuperAdmin.Id);
        insert supAdmin;        
        assert.areEqual(supAdmin.AssigneeId, crmUser.id);
        
        System.runAs(crmUser)
        {
            List<Account> accList = new List<Account>();
            date dob = System.today()-1000;
            Account acc1 = CRMRetail_TestDataFactory_H_HUM.createAccount(null,crmRtlVstr,dob,'02/17/1998', 'First Time', 'Visitor Account1','','',false,'','01928','8919838891','',System.today(),'','');             
            Account acc2 = CRMRetail_TestDataFactory_H_HUM.createAccount(null,crmRtlVstr,dob,'02/17/1998', 'First Time', 'Visitor Account2','','',false,'','81029','7878925772','',System.today(),'','');                         
            assert.areNotEqual(NULL,acc1.Id);
            assert.areNotEqual(NULL,acc2.Id);
            
            List<Storefront_Location__c> lstOfSfLocations = new List<Storefront_Location__c>();
            Storefront_Location__c location1 = CRMRetail_TestDataFactory_H_HUM.createLocation('Tampa','abc','xyz','12345','12345','Texas',20);
            insert location1;
            assert.areNotEqual(NULL,location1.Id);
            
            List<Storefront_I_Type__c> lstIntTypes = new List<Storefront_I_Type__c>();
            Storefront_I_Type__c interactionType1 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Community');
            Storefront_I_Type__c interactionType2 = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Inquiry');
            lstIntTypes.add(interactionType1);
            lstIntTypes.add(interactionType2);
            insert lstIntTypes;
            assert.areNotEqual(NULL,interactionType1.Id);
            assert.areNotEqual(NULL,interactionType2.Id);
            
            List<Storefront_I_Reason__c> lstReason = new List<Storefront_I_Reason__c>();
            Storefront_I_Reason__c interactionReason1 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In (1st Time)',interactionType1.Id);                        
            Storefront_I_Reason__c interactionReason2 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Visitor Check-In',interactionType1.Id);
            Storefront_I_Reason__c interactionReason3 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Inquiry',interactionType2.Id);
            Storefront_I_Reason__c interactionReason4 = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Community Practice',interactionType1.Id);
            lstReason.add(interactionReason1);
            lstReason.add(interactionReason2);
            lstReason.add(interactionReason3);
            lstReason.add(interactionReason4);
            insert lstReason;
            lstReason.clear();
            lstReason = [select id,name from Storefront_I_Reason__c];
            assert.areEqual(4,lstReason.size());
            
            List<Storefront_Interaction__c> lstSfInts = new List<Storefront_Interaction__c>();
            Storefront_Interaction__c sfInt1 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc1.Id,location1.Id,interactionReason1.Id,False,'Non-Member',Date.Today()-10);
            sfInt1.First_Time_Visitor__c = true;
            Storefront_Interaction__c sfInt2 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc1.Id,location1.Id,interactionReason2.Id,False,'Non-Member',Date.Today()-9);
            sfInt2.First_Time_Visitor__c = true;
            Storefront_Interaction__c sfInt3 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc1.Id,location1.Id,interactionReason3.Id,False,'Non-Member',Date.Today()-8);
            sfInt3.First_Time_Visitor__c = true;
            sfInt3.Storefront_Event_Starttime__c = System.now()-8;
            sfInt3.Storefront_Event_Endtime__c = System.now()-8;
            Storefront_Interaction__c sfInt4 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc1.Id,location1.Id,interactionReason4.Id,False,'Non-Member',Date.Today()-7);
            sfInt4.First_Time_Visitor__c = true;
            sfInt4.Storefront_Event_Starttime__c = System.now()-7;
            sfInt4.Storefront_Event_Endtime__c = System.now()-7;
            Storefront_Interaction__c sfInt5 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc1.Id,location1.Id,interactionReason4.Id,False,'Non-Member',Date.Today()-6);
            sfInt5.First_Time_Visitor__c = true;            
            sfInt5.Storefront_Event_Starttime__c = System.now()-6;
            sfInt5.Storefront_Event_Endtime__c = System.now()-6;
            Storefront_Interaction__c sfInt6 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,interactionReason1.Id,False,'Non-Member',Date.Today()-10);
            sfInt6.First_Time_Visitor__c = true;
            Storefront_Interaction__c sfInt7 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,interactionReason2.Id,False,'Non-Member',Date.Today()-9);
            sfInt7.First_Time_Visitor__c = true;
            Storefront_Interaction__c sfInt8 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,interactionReason3.Id,False,'Non-Member',Date.Today()-8);
            sfInt8.First_Time_Visitor__c = true;
            sfInt8.Storefront_Event_Starttime__c = System.now()-8;
            sfInt8.Storefront_Event_Endtime__c = System.now()-8;
            Storefront_Interaction__c sfInt9 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,interactionReason4.Id,False,'Non-Member',Date.Today()-7);
            sfInt9.First_Time_Visitor__c = true;
            sfInt9.Storefront_Event_Starttime__c = System.now()-7;
            sfInt9.Storefront_Event_Endtime__c = System.now()-7;
            Storefront_Interaction__c sfInt10 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,InteractionReason4.Id,False,'Non-Member',Date.Today()-7);
            sfInt10.First_Time_Visitor__c = true;
            sfInt10.Storefront_Event_Starttime__c = System.now()-7;
            sfInt10.Storefront_Event_Endtime__c = System.now()-7;
            sfInt10.Category__c = 'Virtual';
            Storefront_Interaction__c sfInt11 = CRMRetail_TestDataFactory_H_HUM.createInteraction(acc2.Id,location1.Id,interactionReason2.Id,False,'Non-Member',Date.Today()-9);
            sfInt11.First_Time_Visitor__c = true;
            lstSfInts.add(sfInt1);
            lstSfInts.add(sfInt2);
            lstSfInts.add(sfInt3);
            lstSfInts.add(sfInt4);
            lstSfInts.add(sfInt5);
            lstSfInts.add(sfInt6);
            lstSfInts.add(sfInt7);
            lstSfInts.add(sfInt8);
            lstSfInts.add(sfInt9);
            lstSfInts.add(sfInt10);
            lstSfInts.add(sfInt11);
            insert lstSfInts;
            lstSfInts.clear();
            lstSfInts = [select id,name from Storefront_Interaction__c];
            assert.areEqual(11,lstSfInts.size());
        }
    }
	@isTest
    static void testFirstTimeVisitorWithAdministrator()
    {    
        User adminUser = [select id,profile.name from user where name = 'test Admin' limit 1];
        assert.areEqual('System Administrator', adminUser.profile.name);
        
        System.runAs(adminUser)
        {
            Test.startTest();
            CRMRetail_FirstTimeVisitor_B_HUM batch= new CRMRetail_FirstTimeVisitor_B_HUM();
            Id jobid = Database.executeBatch(batch);
            Test.stopTest();
            
            List<Storefront_Interaction__c> lstSfInts = [SELECT Id,Reason__r.name,Interaction_Date__c, First_Time_Visitor__c FROM Storefront_Interaction__c];
            for(Storefront_Interaction__c sfInt: lstSfInts)
            {
                if(sfInt.Reason__r.Name == 'Community Practice' && sfInt.Interaction_Date__c == Date.today()-7)
                {
                	assert.areEqual(true, sfInt.First_Time_visitor__c); 
                }
                else
                {
                	assert.areEqual(false, sfInt.First_Time_visitor__c);     
                }
            }
        }                                                                             
    } 
    @isTest
    static void testFirstTimeVisitorWithDeployment()
    {    
        User depUser = [select id,profile.name from user where name = 'test Deployment' limit 1];
        assert.areEqual('Deployment', depUser.profile.name);
        
        System.runAs(depUser)
        {
            Test.startTest();
            CRMRetail_FirstTimeVisitor_B_HUM batch= new CRMRetail_FirstTimeVisitor_B_HUM();
            Id jobid = Database.executeBatch(batch);
            Test.stopTest();  
            Set<String> stOfRsnNames = new Set<String>{GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_INQUIRY_INTERACTION_TYPE,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN};
            List<Storefront_Interaction__c> lstSfInts = [SELECT Id,Reason__r.name,Interaction_Date__c, First_Time_Visitor__c FROM Storefront_Interaction__c where Reason__r.Name IN: stOfRsnNames];                         
            for(Storefront_Interaction__c sfInt: lstSfInts)
            {     
                assert.areNotEqual(false, sfInt.First_Time_visitor__c);                
            }
            List<ContentVersion> lstContVers = [Select Id from ContentVersion];
            assert.areEqual(1, lstContVers.size());
        }                                                                             
    }
    @isTest
    static void testFirstTimeVisitorWithNoAccess()
    {    
        User depUser = [select id,profile.name from user where name = 'Test Read' limit 1];
        assert.areEqual('Read Only', depUser.profile.name);
        
        System.runAs(depUser)
        {
            Test.startTest();
            CRMRetail_FirstTimeVisitor_B_HUM batch= new CRMRetail_FirstTimeVisitor_B_HUM();
            Id jobid = Database.executeBatch(batch);
            Test.stopTest(); 
            
            List<Error_Log__c> lstErrorLog = [Select Id,Error_Message__c,Object_Type__c,Method_Name__c from Error_Log__c];
            assert.isTrue(lstErrorLog[0].Error_Message__c.contains('User doesn\'t have required access to UPDATE')); 	           
        }                                                                             
    } 
}