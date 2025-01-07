/**************************************************************************** 
Apex Class Name  : AuthReferral_SecurityHO_LT_HUM 
Version          : 1.0  
Created Date     : 
Function         : Test class for AuthReferral_SecurityHO_LH_HUM
*****************************************************************************
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Harika Devi Kalidindi          103812                06/21/2017                Original Version
 * Anuradha Gajbhe                                      06/18/2021                User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
 * Sagar G      										18/08/2023			      User story 4762116: T1PRJ0891415 - MF4439757 - Test Class Code Coverage
*********************************************************************************************************************************/  
@isTest
private Class AuthReferral_SecurityHO_LT_HUM
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
            string Certificate_Name =''; 
            String orgId = UserInfo.getOrganizationId(); 
            if(orgId == '00D4C0000000TunUAE')
                {
                    Certificate_Name='Humana_GuidanceCare_TestFull';
                }
                //QA
                else if(orgId == '00Dc0000003uIioEAE')
                {
                   Certificate_Name = 'Humana_GuidanceCare_QA';                            
                }
                //QAS
                else if(orgId == '00DL0000005rQrAMAU')
                {
                    Certificate_Name = 'Humana_GuidanceCare_QAS';                           
                } 
                //PROD
                else if(orgId == '00DF00000005DjnMAE')
                {
                    Certificate_Name = 'Humana_GuidanceCare';                           
                }
                //Pharmacy
                else if(orgId == '00DZ0000009T1qeMAC')
                {
                    Certificate_Name = 'Humana_GuidanceCare_Pharmacy';                            
                }
            

            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = Certificate_Name, Timeout__c = '120000', End_Point_URL__c = 'https://int-crmservicews.humana.com/InquirySearchService.svc', Active__c=true);
            insert vfs;
            System.assertEquals(vfs.Name,'RetrieveInquiry');
            System.assertEquals(vfs.Certificate_Name__c,Certificate_Name);
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = Certificate_Name, Timeout__c = '3000', End_Point_URL__c = 'http://test-gcp.humana.com/utilities/LaunchGCP.aspx', Active__c=true);
            insert cstGCP ;
            System.assertEquals(cstGCP.Name,'HUMLaunchGCP');
            System.assertEquals(cstGCP.Certificate_Name__c,Certificate_Name);
            
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
            oAcc.Home_Office_Account__c  = true;            
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
           
            PurchaserPlan pplan = new PurchaserPlan();
            pplan.Name='MED';
            pplan.Product__c=oProd.Id;
            insert pplan;
            
            MemberPlan objPolMem = new MemberPlan();
            objPolMem.Name = 'testpolmem11';            
            objPolMem.RelationshipToSubscriber = 'Self';            
            objPolMem.Subscriberid = objPolMem.Id;            
            objPolMem.EffectiveFrom=Date.today().addDays(-1);            
            objPolMem.EffectiveTo=Date.today().addDays(1);            
            objPolMem.Member_Plan_External_Id__c='00|ODS|6925004434817|12345678|34567|2013-07-01';            
            objPolMem.Memberid = objAcc.Id;            
            objPolMem.Policy__c = objpol.Id;           
            objPolMem.Policy__r = objpol;
            //objPolMem.OSB_Indicator__c='O';
            objPolMem.Coverage_Type__c='FA';
            objPolMem.PlanId=pplan.Id;
            insert objPolMem;
             
            System.assertEquals(objPolMem.Name,'testpolmem11');
            system.assertEquals(objPolMem.RelationshipToSubscriber, 'Self');
            System.assertEquals(objPolMem.Subscriberid ,NULL);
            System.assertEquals(objPolMem.Memberid,objAcc.Id);
            
            /*Member_Id__c oMember = new Member_Id__c(Name='Tester',MemberPlan=objPolMem.Id,Member_Card_External_ID__c='MemberCard',Policy__c=objpol.Id);
            insert oMember;
            System.assertEquals(oMember.Name, 'Tester');
            System.assertNotEquals(oMember.Id, NULL);*/
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
            
            Account oAccount2 = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c from Account where LastName = 'Account' limit 1];
            
            Account oAcc = [Select Id,Name,Tenant_Id__c,RecordType.Name,ownerid,Enterprise_ID__c from Account where recordtype.name = 'Group'];  
            System.assertEquals(oAccount.Tenant_Id__c, '00');
            
            //MemberPlan oPolMem =  [Select Id,Subscriberid ,Display_Group_Name__c,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c, Memberid, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name,Policy__r.Group_Number__C from MemberPlan where Memberid =: oAccount.Id and PRODUCT__C IN ('MED','DEN') and policy__r.Group_Name__r.Home_Office_Account__c =: True];
            MemberPlan oPolMem =  [Select Id,Subscriberid ,Policy__r.Group_Name__r.name,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c, Memberid, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name,PRODUCT__C from MemberPlan where name='testpolmem11' ];         
          	
            system.debug('oPolMem'+oPolMem);
            //List<MemberPlan> oPolM = new List<MemberPlan>();
            //oPolM.add(oPolMem);
	            
            String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
            String vGroupName;
            group grp = [Select id,name from Group where name =: sgrpName];
            GroupMember vuserAccess=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp.id limit 1];
            List<GroupMember> vuserAccesss=[Select Id, UserOrGroupId From GroupMember Where UserOrGroupId=:vuserAccess.UserOrGroupId and GroupId = :grp.id];
            Boolean bHomeOff;
            AuthReferral_SecurityHO_LH_HUM authSecurity = new AuthReferral_SecurityHO_LH_HUM();
            bHomeOff = authSecurity.isHomeOfficeDentalUser(oCCSUser.id);
            System.assertEquals(false,bHomeOff );
            //authSecurity.getCCSUserAccessPolicies(oPolMem.Id);
            bHomeOff = authSecurity.isHomeOfficeDentalUser(oAccount.id);
            System.assertEquals(false,bHomeOff );
            
            List<MemberPlan> lst = authSecurity.getCCSUserAccessPolicies(oPolMem.Memberid);
            oPolMem.PlanId=null;
            authSecurity.getCCSUserAccessPolicyNumbers(oPolMem.Memberid);
            //System.assertEquals(1,lst.size());
            
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
            System.assertEquals(objAccId ,oPolMem.Memberid);
            
            setVals  = authSecurity.getCCSUserAccessPolicyNumbers(oAcc.id);
             System.assertEquals(0,setVals.size());
             
            objAccId = authSecurity.getMemberAccountID(oPolMem.id);
            System.assertEquals(objAccId ,oPolMem.Memberid);
            
            objAccId = authSecurity.getMemberAccountID(oCCSUser.id);
            System.assertNotEquals(objAccId ,oPolMem.Memberid);
            
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
            //MemberPlan oPolMem =  [Select Id,Subscriberid ,Policy__r.Group_Name__r.name,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c, Memberid, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name from MemberPlan where Memberid =: oAccount.Id and PRODUCT__C IN ('MED','DEN')];         
            //MemberPlan oPolMem =  [Select Id,Subscriberid ,Policy__r.Group_Name__r.name,RelationshipToSubscriber,Policy__r.Group_Name__r.Home_Office_Account__c, Memberid, Policy__c, Member_Plan_External_Id__c , Policy__r.Product__r.ProductCode,Policy__r.Product__r.name from MemberPlan where Memberid =: oAccount.Id ];         
            //System.assertEquals(oPolMem.RelationshipToSubscriber, 'Self');            
            //List<MemberPlan> oPolM = new List<MemberPlan>();
            //oPolM.add(oPolMem);            
            AuthReferral_SecurityHO_LH_HUM  authSecurity = new AuthReferral_SecurityHO_LH_HUM();
                      
            AuthRefferal_Wrapper_LDTO_HUM objectWrapper = new AuthRefferal_Wrapper_LDTO_HUM();
                      
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAccount2.Id); 
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAccount.Id);  
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAcc.Id);  
                    
            authSecurity.getCCSUserAccessPolicyNumbers(oAccount2.Id);
            authSecurity.getCCSUserAccessPolicyNumbers(oAcc.Id);
            authSecurity.getCCSUserAccessPolicyNumbers(oAccount.Id);
            string objectWrapperelement = objectWrapper.sAuthorizationOrReferralNumber;
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAccount2.Id);
            String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
            group grp = [Select id,name from Group where name =: sgrpName];
            GroupMember vuserAccess=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp.id limit 1];
            oCCSUser.Id=vuserAccess.UserOrGroupId;
 			authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAccount.Id);            
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAccount.Id);
            
            String sgrpName_2 = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_DENTAL').StringValue__c);
            group grp_2 = [Select id,name from Group where name =: sgrpName_2];
            GroupMember vuserAccess_2=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp_2.id limit 1];
            oCCSUser.Id=vuserAccess_2.UserOrGroupId;
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAcc.Id);
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAccount.Id);
            
            String sgrpName_1 = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_MEDICAL').StringValue__c);
            group grp_1 = [Select id,name from Group where name =: sgrpName_1];
            GroupMember vuserAccess_1=[Select Id, UserOrGroupId From GroupMember Where GroupId = :grp_1.id limit 1];
            oCCSUser.Id=vuserAccess_1.UserOrGroupId;
            authSecurity.getFilteredAuths(new List<AuthRefferal_Wrapper_LDTO_HUM>{objectWrapper},oCCSUser.Id,oAccount.Id);
            authSecurity.filteredAuthsCheck( objectWrapperelement,oCCSUser.Id,oAccount.Id);
			
        	
            
            MemberPlan objPLan = [Select Id,MemberId from MemberPlan limit 1];
            authSecurity.getMemberAccountID(objPLan.Id);
            authSecurity.getMemberAccountID(null);
            authSecurity.isMemberHaveHomeOfficePolicies(oAccount.Id);
            //authSecurity.getCCSUserAccessPolicies(oPolMem.Id);
            authSecurity.getCCSUserAccessPolicyNumbers(oAccount2.Id);
            test.stopTest();
        }
    }
    
    
}