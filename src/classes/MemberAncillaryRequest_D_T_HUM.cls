/**************************************************************************** 
Apex Class Name  : MemberAncillaryRequest_D_T_HUM
Version          : 1.0  
Created Date     : 05/31/2019
Function         : Test class for MemberAncillaryRequest_D_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Manikanta                356372              05/31/2019                   Original Version - REQ - 379006 Ancillary service test methods
***************************************************************************************************/
@isTest
private Class MemberAncillaryRequest_D_T_HUM
{
    /*
    * Method Name : prepareTestData
    * Description : Test method to setup common test data for all test methods
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oCCSUser.Current_Queue__c = 'Cincinnati CRU';
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {       
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');                        
                                    
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR', ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
                        
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
                                   
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c='00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');                       
      
            Member_ID__c objMemberID1 = new Member_ID__c();
            objMemberID1.policy_member__c = objPolMem.id;
            objMemberID1.policy__c = objpol.Id;
            objMemberID1.Name = '12345';
            objMemberID1.Member_Card_External_ID__c = 'ODS12345'; 
            objMemberID1.Member_Dependent_Code__c  = '03'; 
            objMemberID1.Type__c = 'SSN';       
            insert objMemberID1;
            system.assertequals('12345',objMemberID1.Name );                        
           
            HUMConstants__c  objhumc = new HUMConstants__c ();
            objhumc.name ='ENCRYPTIONTYPE';
            objhumc.StringValue__c ='Platform';
            insert objhumc ;
            system.assertEquals(objhumc.id!=null,true);
        
        }
    }
    
     /*
     * Method Name : testgetMemberName
     * Description : Used to validate  getMemberName, getMemberIDObjDetails and getEnterpriseID methods
     * Return type : void
     * Parameter   : NA
     */
    static testMethod void testgetMemberName()
    {            
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  FROM Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            Policy_Member__c oPolMem = [SELECT Id, Policy__r.Client_Number__c,Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.name, Policy__r.Product__r.Sold_Product_Key_Value__c , policy__r.source_cust_cov_key__c ,Product_Type__c FROM POLICY_Member__C WHERE Member__c = :oAccount.Id];
           
            id sRecId=oPolMem.id;
            System.assertNotEquals(sRecId, null);
            List<POLICY_Member__C> oMemberList1 = MemberAncillaryRequest_D_HUM.getMemberName(sRecId);
            system.assertEquals(oMemberList1!=null,true);
            List<Member_ID__c> oMemberIDList = MemberAncillaryRequest_D_HUM.getMemberIDObjDetails(sRecId);
            system.assertEquals(oMemberIDList!=null,true);
            String EnterpriseID = MemberAncillaryRequest_D_HUM.getEnterpriseId(sRecId);
            system.assertEquals(EnterpriseID!=null,true);
            
            Test.StopTest();
        }        
    }    
}