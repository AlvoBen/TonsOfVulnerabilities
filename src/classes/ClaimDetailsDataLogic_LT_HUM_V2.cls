/**************************************************************************** 
Apex Class Name  : ClaimDetailsDataLogic_LT_HUM_V2
Version          : 1.0  
Created Date     : 06/01/2022
Function         : Test class for ClaimDetails_LD_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private Class ClaimDetailsDataLogic_LT_HUM_V2
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
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
            objAcc.Mbr_Gen_Key__c = '12345';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test again';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1005577525';
            objAcc1.General_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test memberid';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005587525';
            objAcc2.General_Account__c = true;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
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
            
            Policy__c objpol1 = HUMTestDataHelperTest.getPolicy();
            objpol1.Name='testPolicy';
            objpol1.Group_Name__c = objAcc1.Id;
            objpol1.Major_LOB__c = 'MER';
            objpol1.Source_Cust_Cov_Key__c = '123456789';
            objpol1.Product__c = oProd.Id;
            objpol1.Product__r = oProd;
            objpol1.Platform__c = 'LV';
            insert objpol1;
            system.assertEquals(objpol1.Source_Cust_Cov_Key__c,'123456789');
            
            Policy__c objpol2 = HUMTestDataHelperTest.getPolicy();
            objpol2.Name='testPolicy1';
            objpol2.Group_Name__c = objAcc2.Id;
            objpol2.Major_LOB__c = 'MER';
            objpol2.Source_Cust_Cov_Key__c = '123456789';
            objpol2.Product__c = oProd.Id;
            objpol2.Product__r = oProd;
            objpol2.Platform__c = 'LV';
            insert objpol2;
            system.assertEquals(objpol2.Source_Cust_Cov_Key__c,'123456789');
            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'testpolmem11';
            //objPolMem.Relationship__c = 'Self';
            objPolMem.SubscriberPlanId__c = objPolMem.Id;
            objPolMem.EffectiveFrom=date.parse(Date.today().addDays(-1).format());
            objPolMem.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.MemberId = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            objPolMem.Relationship_Code__c='00';
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship_Code__c, '00');
            
            MemberPlan objPolMem1 = new MemberPlan();
            objPolMem1.Name = 'testpolmem111';
            //objPolMem1.Relationship__c = 'Self';
            objPolMem1.Relationship_Code__c = '00';
            objPolMem1.SubscriberPlanId__c = objPolMem.Id;
            objPolMem1.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem1.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem1.Member_Plan_External_Id__c='00|ODS|7925004434817|12345678|34567|2013-07-01';
            objPolMem1.MemberId = objAcc1.Id;
            objPolMem1.Policy__c = objpol1.Id;
            objPolMem1.Policy__r = objpol1;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Name, 'testpolmem111');
            
            MemberPlan objPolMem2 = new MemberPlan();
            objPolMem2.Name = 'testpolmem114';
            //objPolMem2.Relationship__c = 'Self';
            objPolMem2.Relationship_Code__c = '00';
            objPolMem2.SubscriberPlanId__c = objPolMem1.Id;
            objPolMem2.EffectiveFrom=Date.parse(Date.today().addDays(-1).format());
            objPolMem2.EffectiveTo=Date.parse(Date.today().addDays(1).format());
            objPolMem2.Member_Plan_External_Id__c='00|ODS|7925004435817|12345678|34567|2013-07-01';
            objPolMem2.MemberId = objAcc2.Id;
            objPolMem2.Policy__c = objpol2.Id;
            objPolMem2.Policy__r = objpol2;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Name, 'testpolmem114');
      
          
        
        }
    }
     /*
     * Method Name : testGetEnterpriseID
     * Description : Used to validate  getEnterpriseID functionality.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testGetEnterpriseID()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            MemberPlan oPolMem =  [Select Id,SubscriberPlanId__c ,Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, MemberId, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode from MemberPlan where MemberId =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship_Code__c, '00');
            id sRecId=oPolMem.id;
            List<MemberPlan> oMemberList1 =ClaimDetails_LD_HUM_V2.getEnterpriseID(oCCSUser.Id);
            List<MemberPlan> oMemberList=ClaimDetails_LD_HUM_V2.getEnterpriseID(sRecId);
            List<MemberPlan> oMemberList2=ClaimDetails_LD_HUM_V2.getEnterpriseID('0SqZ00000004CL6abc');
            List<MemberPlan> oMemberGenKey=ClaimDetails_LD_HUM_V2.getMemberGenKey(sRecId);
            List<MemberPlan> oMemberGenKey2=ClaimDetails_LD_HUM_V2.getMemberGenKey('0SqZ00000004CL6abc');
            List<MemberPlan> oMemberPolicyMember=ClaimDetails_LD_HUM_V2.getPolicyMember(sRecId);
            List<LVRelationShipCodeToCAS__c> oMembergetLVCASTranslationDetails=ClaimDetails_LD_HUM_V2.getLVCASTranslationDetails('00');
            System.assertNotEquals(oMemberList.size(),0 );
        }  
    }    
    
    /*
     * Method Name : testGetLoggedinUserQueue
     * Description : Used to validate getLoggedinUserQueue functionality.
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testGetLoggedinUserQueue()
    {    
        User oCCSUser = [Select id,UserName,Current_Queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            MemberPlan oPolMem =  [Select Id,SubscriberPlanId__c ,Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, MemberId, Policy__c, Member_Plan_External_ID__c , Policy__r.Product__r.ProductCode from MemberPlan where MemberId =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship_Code__c, '00');
            Id oUserId = String.valueOf(oCCSUser.Id);
            Id oAccountId = oAccount.Id;
            User oUser=ClaimDetails_LD_HUM_V2.getLoggedinUserQueue(oUserId);
            User oUser1=ClaimDetails_LD_HUM_V2.getLoggedinUserQueue(oAccountId);
            system.assertEquals(oUser.Current_Queue__c,'Cincinnati CRU');
        }
    }    
    
    /*
     * Method Name : testPolicyDetailsPositive
     * Description : Used to validate getPolicyMember,getLVCASTranslationDetails,getMemberIDObjDetails
     * Return type : void
     * Parameter   : NA
     */
     static testMethod void testPolicyDetailsPositive()
    {    
        User oCCSUser = [Select id,UserName,Current_Queue__c from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        {   
            set<id> PID=new set<id>();
            
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            Account oAccount1 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account1'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');            
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account2'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            String sAccRecType = oAccount.RecordType.Name;
            System.assertEquals(sAccRecType,'Member');
            
            MemberPlan oPolMem =  [Select Id,SubscriberPlanId__c ,Relationship_Code__c,Policy__r.Group_Name__r.Home_Office_Account__c, MemberId, Policy__c, Member_Plan_External_ID__c , Policy__r.Product__r.ProductCode from MemberPlan where MemberId =: oAccount.Id];
            System.assertEquals(oPolMem.Relationship_Code__c, '00');
            PID.add(oPolMem.id);
            
            
            ClaimDetails_D_HUM_V2 oDetails=new ClaimDetails_D_HUM_V2();
            
           
            
            list<LVRelationShipCodeToCAS__c>  oLVList=ClaimDetails_D_HUM_V2.getLVCASTranslationDetails(oPolMem.Relationship_Code__c);
            system.assertNotEquals(oPolMem.Relationship_Code__c,null);
            
           
        }
    }
    
    
}