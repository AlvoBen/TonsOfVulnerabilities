/**
 * <p>
 * Test class for AuthReferral_Security_LH_HUM
 * </p>
 * Apex class Name    : AuthReferral_Security_LT_HUM  
 * Version            : 1.0
 * Function           : Test class for AuthReferral_Security_LH_HUM
 * Created Date       : 12/07/2022 
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Anuradha Gajhbe                                  12/07/2022					User story 3362694 Authorization Summary table
 * Sagar G      										18/08/2023			 User story 4762116: T1PRJ0891415 - MF4439757 - Test Class Code Coverage
 ***************************************************************************************************************************/

@isTest
private class AuthReferral_Security_LT_HUM
{
   /*
     * Method Name : dataSetup
     * Description : To create test data
     * Return type : NA
     * Parameter   : NA
     */
    @testSetup static void dataSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
        insert oUser1 ;
        System.assertEquals(oUser1.LastName, 'test User2');
        
        list<Group> lstGrp = new List<Group>();
        lstGrp.add(new Group(DeveloperName='Cincinnati_Calls_PGQ', Name='Cincinnati Calls PGQ', Type='Queue'));
        lstGrp.add(new Group(DeveloperName='Test_General', Name='Humana General', Type='Regular'));
        insert lstGrp;
        System.assertEquals(lstGrp.size(), 2);
                
        list<GroupMember> lstGrpMem = new List<GroupMember>();
        for(Group oGrp : lstGrp)
        {
            lstGrpMem.add(new GroupMember(GroupId=oGrp.Id, UserOrGroupId=oUser.Id));
        }
        insert lstGrpMem;
        
        System.runAs(oUser)
        {
             for(Integer i = 0 ; i < lstGrpMem.size() ; i++)
        {
            System.assertEquals(lstGrpMem[i].UserOrGroupId, oUser.Id);
        }
        
        List<Account> lstAcc = new List<Account>();
            lstAcc.add(new Account(Name='Test0',Home_Office_Account__c=true,Group_Number__c='21313211',Tenant_Id__c='00'));
            lstAcc.add(new Account(Name='Test1',Home_Office_Account__c=true,Group_Number__c='21313211',Tenant_Id__c='00'));
            lstAcc.add(new Account(Name='Test2',Home_Office_Account__c=false,Group_Number__c='21313211',Tenant_Id__c='00',General_Account__c = true));
            lstAcc.add(new Account(Name='Test3',Home_Office_Account__c=true,Group_Number__c='000003',Tenant_Id__c='00'));
            lstAcc.add(new Account(Name='Test4',Home_Office_Account__c=true,Group_Number__c='000000',Tenant_Id__c='00',General_Account__c = true));
            insert lstAcc;
            
            for(Integer i = 0 ; i < lstAcc.size() ; i++)
            {
                System.assertEquals(lstAcc[i].Name,'Test' + i);
            }
            
            Policy__c polHO = HUMTestDataHelperTest.getPolicy();
            polHO.Group_Name__c = lstAcc[0].Id;
            polHO.Name = 'Test Policy HO';
            insert polHO ;
            system.assertEquals(polHO .Id != Null,true);
            system.assertEquals(polHO .Name,'Test Policy HO');
            system.assertEquals(polHO .Group_Name__c == lstAcc[0].Id,true);
            
            Policy__c polgen = HUMTestDataHelperTest.getPolicy();
            polgen.Group_Name__c = lstAcc[4].Id;
            polgen.Name = 'Test Policy gen';
            insert polgen;
            system.assertEquals(polgen.Id != Null,true);
            system.assertEquals(polgen.Name,'Test Policy gen');
            system.assertEquals(polgen.Group_Name__c == lstAcc[4].Id,true); 
            
            MemberPlan polMember = new MemberPlan();
            polMember.RelationshipToSubscriber = 'Self';
            polMember.EffectiveFrom=Date.today().addDays(-1);
            polMember.EffectiveTo=Date.today().addDays(1);
            polMember.Name='Policy Member PM1 HO';
            polMember.Policy__c=polHO.Id;
            polMember.Memberid = lstAcc[1].Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1 HO');
            system.assertEquals(polMember.Policy__c,polHO.Id);
           
            MemberPlan polMembergen = new MemberPlan();
            polMembergen.RelationshipToSubscriber = 'Self';
            polMembergen.EffectiveFrom=Date.today().addDays(-1);
            polMembergen.EffectiveTo=Date.today().addDays(1);
            
            polMembergen.Name='Policy Member PM1 Gen';
            polMembergen.Policy__c=polgen.Id;
            polMembergen.Memberid=lstAcc[2].Id;
            insert polMembergen ;
            system.assertEquals(polMembergen.Id != Null,true);
            system.assertEquals(polMembergen.Name,'Policy Member PM1 Gen');
            system.assertEquals(polMembergen.Policy__c,polgen.Id);
        }
    }
     /*
     * Method Name : checkSecurity
     * Description : To test total security class methods 
     * Return type : NA
     * Parameter   : NA
     */
    testMethod static void checkSecurity()
    {
        User oUser = [Select Id,LastName from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        system.runAs(oUser)
        {
         HUMTestDataHelperTest.getHUMConstantsData();
        
         List<MemberPlan> lstPolmem = [select Id,Memberid,Name from MemberPlan where Name = 'Policy Member PM1 HO'];
         system.assertEquals(lstPolmem.size(),1);
         system.assertEquals(lstPolmem[0].Name,'Policy Member PM1 HO');
         
         List<AuthRefferal_Wrapper_LDTO_HUM> Authlist = new List<AuthRefferal_Wrapper_LDTO_HUM>();
         for(integer i=0;i<=50;i++){
           AuthRefferal_Wrapper_LDTO_HUM obj = new AuthRefferal_Wrapper_LDTO_HUM();
           obj.sAuthorizationOrReferralNumber = '093033556';
           obj.sAuthorizationType ='Inpatient';
           obj.sOverallStatus ='Approved';
           obj.sAdmFirstDay ='2015-11-24';
           obj.sDischargeLastDay ='2015-12-24';
           obj.sServiceType ='Inpatient Detox';
           obj.sFacility ='Norton Hospital';
           obj.sRequestingrovider ='DASCO NORTON HME';
           obj.sTreatingProvider ='DASCO NORTON HME';
           obj.sGroupId  = '000000';
           obj.bIsAccessible  = true;
           Authlist.add(obj);
         }
         
         Test.startTest();
         AuthReferral_Security_LH_HUM objAuthSec = new AuthReferral_Security_LH_HUM(); 
         List<AuthRefferal_Wrapper_LDTO_HUM> Authlistsec = new List<AuthRefferal_Wrapper_LDTO_HUM>();
         Authlistsec = objAuthSec.applySecurityMeasures(Authlist,lstPolmem[0].Memberid); 
         system.assertEquals(Authlistsec.size(),51);
         
         system.assertEquals(objAuthSec.checkHOAccountMember(lstPolmem[0].Memberid),true);
         
         
         system.assertEquals(objAuthSec.filterHOAccountResults(Authlist).size(),1);
         Test.stopTest();
        }
    }
}