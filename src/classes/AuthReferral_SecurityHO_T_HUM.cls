/**************************************************************************** 
Apex Class Name  : AuthReferral_SecurityHO_T_HUM 
Version          : 1.0  
Created Date     : 
Function         : Test class for AuthReferral_SecurityHO_H_HUM
*****************************************************************************
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Harika Devi Kalidindi          103812                06/21/2017                Original Version
 * Anuradha Gajbhe                                      06/18/2021                User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
 *********************************************************************************************************************************/  
@isTest
private Class AuthReferral_SecurityHO_T_HUM
{

    /*
     * Method Name   :    prepareTestData
     * Description   :    This method is for preparing test data
     * Return Type   :    void
     * Parameters    :    NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {   
            Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c=true);
            insert vfs;
            System.assertEquals(vfs.Name,'RetrieveInquiry');
            System.assertEquals(vfs.Certificate_Name__c,'TestCertificate');
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c=true);
            insert cstGCP ;
            System.assertEquals(cstGCP.Name,'HUMLaunchGCP');
            System.assertEquals(cstGCP.Certificate_Name__c,'TestCertificate1');
            
            Group oGroup1 = HUMTestDataHelperTest.getQueues('Group one');
            //insert oGroup1;
            System.assertEquals(oGroup1.Name,'Group one');
            
            GroupMember GroupMem = HUMTestDataHelperTest.getGrpMems(oGroup1.id, oCCSUser.id);
           // insert GroupMem;
            System.assertEquals(GroupMem.GroupId,oGroup1.id);

            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            objAcc.Enterprise_ID__c = '1005577525';
            System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
            objAcc.Group_Number__c = '123412312';
            System.assertEquals(objAcc.Group_Number__c,'123412312');
            objAcc.Home_Office_Account__c  = true;
            System.assertEquals(objAcc.Home_Office_Account__c,true);
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            
              
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1 .FirstName = 'Test';
            objAcc1 .LastName = 'Account1';
            objAcc1 .Tenant_Id__c = '00';
            system.assertEquals(objAcc1 .Tenant_Id__c,'00');
            objAcc1 .Enterprise_ID__c = '1005577525';
            System.assertEquals(objAcc.Enterprise_ID__c,'1005577525');
            objAcc1 .Group_Number__c = '1234123124';
            system.assertEquals(objAcc1 .Group_Number__c,'1234123124');
            objAcc1 .General_Account__c  = true;
            System.assertEquals(objAcc1 .General_Account__c,True);
            objAcc1 .RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1 ;          
           
            
            Account oAcc = HUMTestDataHelperTest.getAccount(); 
            oAcc.Name = 'Test';            
            oAcc.Tenant_Id__c = '00';            
            oAcc.Enterprise_ID__c = '7420004174017';            
            oAcc.Group_Number__c = '123412312';                      
            oAcc.Home_Office_Account__c  = false;            
            oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUM_Group_Account_Record_Type).getRecordTypeId();
            insert oAcc;
            System.assertEquals(oAcc.Name,'Test');
            System.assertEquals(oAcc.Tenant_Id__c ,'00');
            System.assertEquals(oAcc.Enterprise_ID__c,'7420004174017');
            
            
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            System.assertEquals(oProd.Segment_Type__c,'MCR');
            System.assertEquals(oProd.ProductCode,'MED');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';            
            objpol.Group_Name__c = objAcc.Id;            
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';            
            objpol.Product__c = oProd.Id;            
            objpol.Source_Cust_Cov_Key__c = '123456789';            
            objpol.Product__r = oProd;            
            objpol.Platform__c = 'LV';
           
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            System.assertEquals(objpol.Name,'testPolicy');System.assertEquals(objpol.Name,'testPolicy');
            System.assertEquals(objpol.Group_Name__c,objAcc.Id);
            System.assertEquals(objpol.Product__c,oProd.Id);
            System.assertEquals(objpol.Product__r ,oProd);            
            
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
            insert objPolMem;
            System.assertEquals(objPolMem.Name,'testpolmem11');
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            System.assertEquals(objPolMem.Subscriber__c ,NULL);
            System.assertEquals(objPolMem.Member__c,objAcc.Id);
            
            Member_Id__c oMember = new Member_Id__c(Name='Tester',POlicy_Member__c=objPolMem.Id,Member_Card_External_ID__c='MemberCard',Policy__c=objpol.Id);
            insert oMember;
            System.assertEquals(oMember.Name, 'Tester');
            System.assertNotEquals(oMember.Id, NULL);
        }
    }
    
    /*
     * Method Name   :    runPositiveTestCases
     * Description   :    This method is used to validate positive scenarios
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void runPositiveTestCases()
    {        
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            test.startTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where Home_Office_Account__c =  True limit 1];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Account oAccount1 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where Home_Office_Account__c =  False AND Enterprise_ID__c= '1005577525' limit 1];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            User oCCSUsers = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
            system.assertEquals(oCCSUsers.UserName,'user1user1@humcrmstest.com');
            
            Account oAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,ownerid,Enterprise_ID__c from Account where recordtype.name = 'Group'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Policy__r.Group_Name__r.name,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name,Policy__r.Group_Number__C from Policy_Member__c where Member__c =: oAccount.Id and PRODUCT__C IN ('MED','DEN') and policy__r.Group_Name__r.Home_Office_Account__c =: True];
                        
            List<Policy_Member__c> oPolM = new List<Policy_Member__c>();
            oPolM.add(oPolMem);
            
            String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
            String vGroupName;
            group grp = [Select id,name from Group where name =: sgrpName];
            GroupMember vuserAccess=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp.id limit 1];
            List<GroupMember> vuserAccesss=[Select Id, UserOrGroupId From GroupMember Where UserOrGroupId=:vuserAccess.UserOrGroupId and GroupId = :grp.id];
            Boolean bHomeOff;
            AuthReferral_SecurityHO_H_HUM authSecurity = new AuthReferral_SecurityHO_H_HUM();
            bHomeOff = authSecurity.isHomeOfficeDentalUser(oCCSUser.id);
            System.assertEquals(false,bHomeOff );
            
            bHomeOff = authSecurity.isHomeOfficeDentalUser(oAccount.id);
            System.assertEquals(false,bHomeOff );
            
            List<Policy_Member__c> lst = authSecurity.getCCSUserAccessPolicies(oAccount.id);
            System.assertEquals(1,lst.size());
            
            bHomeOff  = authSecurity.isHomeOfficeMedicalUser(oCCSUser.id);
            System.assertEquals(false,bHomeOff );
            
            bHomeOff  = authSecurity.isHomeOfficeAllUser(oCCSUser.id);
            System.assertEquals(false,bHomeOff );
            
            bHomeOff  = authSecurity.isHomeOfficeAllUser(oAccount.id);
            System.assertEquals(false,bHomeOff );
            
            bHomeOff  = authSecurity.isMemberHaveHomeOfficePolicies(oAccount.id);
            System.assertEquals(true,bHomeOff );
            
           bHomeOff  = authSecurity.isMemberHaveHomeOfficePolicies(oAccount1.id);
            System.assertEquals(false,bHomeOff );
            
            SET<String> setVals = authSecurity.getCCSUserAccessPolicyNumbers(oAcc.id);
            System.assertEquals(0,setVals.size());
            
            Id objAccId = authSecurity.getMemberAccountID(oPolMem.id);
            System.assertEquals(objAccId ,oPolMem.Member__c);
            
            setVals  = authSecurity.getCCSUserAccessPolicyNumbers(oAcc.id);
             System.assertEquals(0,setVals.size());
             
            objAccId = authSecurity.getMemberAccountID(oPolMem.id);
            System.assertEquals(objAccId ,oPolMem.Member__c);
            
            objAccId = authSecurity.getMemberAccountID(oCCSUser.id);
            System.assertNotEquals(objAccId ,oPolMem.Member__c);
            
            bHomeOff = authSecurity.isHavingAccessToGroup(vuserAccess.UserOrGroupId,sgrpName);
            System.assertEquals(true,bHomeOff );
            
            
            test.stopTest();
       }        
    }
    
    /*
     * Method Name   :    getPolicyMemberRecords
     * Description   :    This method is used to test claim security features.
     * Return Type   :    void
     * Parameters    :    NA
     */
    private static testMethod void getPolicyMemberRecords()
    {        
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        
        System.runAs(oCCSUser)
        {
            test.startTest();
            Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where Home_Office_Account__c =  True limit 1];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');            
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where Home_Office_Account__c =  false limit 1];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');            
            Account oAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where recordtype.name = 'Group'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');            
            Policy_Member__c oPolMem =  [Select Id,Subscriber__c ,Policy__r.Group_Name__r.name,Relationship__c,Policy__r.Group_Name__r.Home_Office_Account__c, Member__c, Policy__c, Policy_Member_External_ID__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name from Policy_Member__c where Member__c =: oAccount.Id and PRODUCT__C IN ('MED','DEN') and policy__r.Group_Name__r.Home_Office_Account__c =: True];         
            System.assertEquals(oPolMem.Relationship__c, 'Self');            
            List<Policy_Member__c> oPolM = new List<Policy_Member__c>();
            oPolM.add(oPolMem);            
            AuthReferral_SecurityHO_H_HUM  authSecurity = new AuthReferral_SecurityHO_H_HUM();
                      
            AuthRefferal_Wrapper_DTO_HUM objectWrapper = new AuthRefferal_Wrapper_DTO_HUM();
                      
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_DTO_HUM>{objectWrapper},oCCSUser.Id,oAccount2.Id); 
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_DTO_HUM>{objectWrapper},oCCSUser.Id,oAccount.Id);  
                    
            authSecurity.getCCSUserAccessPolicyNumbers(oAccount2.Id);
            authSecurity.getCCSUserAccessPolicyNumbers(oAccount.Id);
            
            string objectWrapperelement = objectWrapper.sAuthorizationOrReferralNumber;
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAccount2.Id);
            String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
            group grp = [Select id,name from Group where name =: sgrpName];
            GroupMember vuserAccess=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp.id limit 1];
	        oCCSUser.Id=vuserAccess.UserOrGroupId;            
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAccount.Id);
            test.stopTest();
        }
    }
}