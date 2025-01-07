/****************************************************************************
Apex Class Name  : HUMGroupContactsControllerTest
Version          : 1.0
Created On       : Aug 06 2015
Function         : Test class for HUMGroupContactsController apex class

Modification Log :
     Developer Name            Code Review                  Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------
     Vijay Bhasker P             25540                   06/08/2015             Original Version
**********************************************************************************************************************************/

@isTest
private class HUMGroupContactsControllerTest
{    
    /* Method name : setup
    * Description : This Method is used to created Account, Policy & Policy Member data 
    * Return Type : Void  
    * Parameter   : NA 
    */ 
    @testSetup static void setup()  
    {  
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assertNotEquals(oUser,null,'Value not matched');
        system.assert(oUser.lastname=='test User1');
        HUMGroupContactsControllerTest gc = new HUMGroupContactsControllerTest();        
        system.runAs(oUser)
        {                                                                    
            Account oGrpAccount = HUMTestDataHelperTest.getAccount();
            oGrpAccount.Name = 'New Group Account';
            oGrpAccount.Group_Number__c  = '123';
            oGrpAccount.BillingState = 'New York';
            oGrpAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
            insert oGrpAccount;
            System.assert(oGrpAccount.id!= null);
            System.assert(oGrpAccount.Name == 'New Group Account');
            System.assert(oGrpAccount.Group_Number__c  == '123');
            System.assert(oGrpAccount.BillingState == 'New York');
            System.assert(oGrpAccount.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId());
            
            Account oAccount = HUMTestDataHelperTest.getAccount();
            oAccount.Name = 'New Group Account2';
            oAccount.Group_Number__c  = '124';
            oAccount.BillingState = 'New York';
            oAccount.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId();
            insert oAccount;
            System.assert(oAccount.id != null);
            System.assert(oAccount.Name == 'New Group Account2');
            System.assert(oAccount.Group_Number__c  == '124');
            System.assert(oAccount.BillingState == 'New York');
            System.assert(oAccount.recordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get('Provider').getRecordTypeId());
                                    
            Policy__c oGrpPolicy = HUMTestDataHelperTest.getPolicy();
            oGrpPolicy.Name = 'testpolicy123';
            oGrpPolicy.Group_Name__c = oGrpAccount.Id;            
            oGrpPolicy.Issue_State__c = 'IL'; 
            insert oGrpPolicy;
            System.assert(oGrpPolicy.id != null);
            System.assert(oGrpPolicy.Name == 'testpolicy123');
            System.assert(oGrpPolicy.Group_Name__c  == oGrpAccount.Id);
            System.assert(oGrpPolicy.Issue_State__c == 'IL');           
            
            Policy__c oPolicy = HUMTestDataHelperTest.getPolicy();
            oPolicy.Name = 'testpolicy124';
            oPolicy.Group_Name__c = oAccount.Id;                                     
            insert oPolicy;
            System.assert(oPolicy.id != null);
            System.assert(oPolicy.Name == 'testpolicy124');
            System.assert(oPolicy.Group_Name__c  == oAccount.Id);                      
            
            Policy_Member__c oGrpPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oGrpPolicyMem.Name = 'policymem12345';           
            oGrpPolicyMem.Policy__c = oGrpPolicy.Id;            
            oGrpPolicyMem.Relationship__c = 'Self';                        
            insert oGrpPolicyMem;
            System.assert(oGrpPolicyMem.id != null);
            System.assert(oGrpPolicyMem.Name == 'policymem12345');
            System.assert(oGrpPolicyMem.Policy__c == oGrpPolicy.Id);
            System.assert(oGrpPolicyMem.Relationship__c == 'Self');
            
            Policy_Member__c oPolicyMem = HUMTestDataHelperTest.getPolicyMember();
            oPolicyMem.Name = 'policymem12346';          
            oPolicyMem.Policy__c = oPolicy.Id;            
            oPolicyMem.Relationship__c = 'Self';                        
            insert oPolicyMem;
            System.assert(oPolicyMem.id != null);
            System.assert(oPolicyMem.Name == 'policymem12346');
            System.assert(oPolicyMem.Policy__c == oPolicy.Id);
            System.assert(oPolicyMem.Relationship__c == 'Self');
        }
    }
    
    /*
    * Method Name : groupContactsSuccess
    * Description : Test method validates group contact details on success scenario
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod void groupContactsSuccess() 
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assert(oUser.id != null);
        system.assert(oUser.lastname=='test User1');
        HUMGroupContactsControllerTest gc = new HUMGroupContactsControllerTest();               
        System.runAs (oUser) 
        {                       
            Account oGrpAccount = [Select id,recordTypeId from Account where recordTypeId=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.GROUP_TYPE).getRecordTypeId() limit 1];                        
            ApexPages.currentPage().getParameters().put('Id', oGrpAccount.id);
            Id objectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(objectId!= null);
             system.assert(objectId == oGrpAccount.id);
            HUmGroupContactsController oGrpAccRecord = new HUmGroupContactsController();                                                                                
            
            Policy__c oGrpPolicy = [Select id,Group_Name__r.RecordTypeId from Policy__c where Group_Name__r.RecordTypeId=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.GROUP_TYPE).getRecordTypeId() limit 1];
            ApexPages.currentPage().getParameters().put('Id', oGrpPolicy.id);
            Id policyObjectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(policyObjectId!= null);
            system.assert(policyObjectId == oGrpPolicy.id);
            HUmGroupContactsController oGrpPolicyRec = new HUmGroupContactsController();         

            Policy_Member__c oGrpPolicyMem = [Select id,Policy__r.Group_Name__r.recordtypeid from Policy_Member__c where Policy__r.Group_Name__r.recordtypeid=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.GROUP_TYPE).getRecordTypeId() limit 1];
            ApexPages.currentPage().getParameters().put('Id', oGrpPolicyMem.id);
            Id policyMemObjectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(policyMemObjectId!= null);
            system.assert(policyMemObjectId == oGrpPolicyMem.id);
            HUmGroupContactsController oGrpPolicyMemRec = new HUmGroupContactsController();                      	            
            
            Test.startTest();             
            oGrpAccRecord.getGroupContactDetails();                                    
            System.assert(oGrpAccRecord.oGroupAccount.recordTypeId==Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId());           
            oGrpPolicyRec.getGroupContactDetails();            
            System.assert(oGrpAccRecord.oGroupAccount.recordTypeId==Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId());                                   
            oGrpPolicyMemRec.getGroupContactDetails();            
            System.assert(oGrpAccRecord.oGroupAccount.recordTypeId==Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId());                       
            Test.stopTest();
        } 
    }
    
    /*
    * Method Name : groupContactsFailure
    * Description : Test method validates group contact details on failure scenario
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod void groupContactsFailure() 
    {
        User oUser = [SELECT Id,lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        System.assert(oUser.id != null);
        system.assert(oUser.lastname=='test User1');        
        System.runAs (oUser)
        { 
            HUmGroupContactsController oGrpPolicyRec1 = new HUmGroupContactsController();
            oGrpPolicyRec1.getGroupContactDetails();
            
            Account oAccount1 = [Select id from Account where recordTypeId=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HumProviderRecordType).getRecordTypeId() limit 1];                        
            ApexPages.currentPage().getParameters().put('Id', oAccount1.id);
            Id objectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(objectId!= null);
            system.assert(objectId == oAccount1.id);
            HUmGroupContactsController oAccRecord = new HUmGroupContactsController();        
            
            Policy__c oPolicy = [Select id from Policy__c where Group_Name__r.RecordTypeId=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HumProviderRecordType).getRecordTypeId() limit 1];
            ApexPages.currentPage().getParameters().put('Id', oPolicy.id);
            Id policyObjectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(policyObjectId!= null);
            system.assert(policyObjectId == oPolicy.id);
            HUmGroupContactsController oPolicyRec = new HUmGroupContactsController();  
            
            Policy_Member__c oPolicyMem1 = [Select id from Policy_Member__c where Policy__r.Group_Name__r.recordtypeid=:Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HumProviderRecordType).getRecordTypeId() limit 1];
            ApexPages.currentPage().getParameters().put('Id', oPolicyMem1.id);
            Id policyMemObjectId = ApexPages.currentPage().getParameters().get('Id');
            system.assert(policyMemObjectId!= null);
            system.assert(policyMemObjectId == oPolicyMem1.id);
            HUmGroupContactsController oPolicyMemRec = new HUmGroupContactsController();            
            
            Test.startTest();                      
            oAccRecord.getGroupContactDetails();                                
            System.assertequals('Group Contact Information is not currently available for the selected member/group.',ApexPages.GetMessages().get(0).getSummary());            
            oPolicyRec.getGroupContactDetails();            
            System.assertequals('Group Contact Information is not currently available for the selected Policy.',ApexPages.GetMessages().get(2).getSummary());                          
            oPolicyMemRec.getGroupContactDetails();            
            System.assertequals('Group Contact Information is not currently available for the selected Member.',ApexPages.GetMessages().get(3).getSummary());            
            Test.stopTest();
        }
    }    
}