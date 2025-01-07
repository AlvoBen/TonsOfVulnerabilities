/**
 * <p>
 * Test class for AuthReferral_Security_H_HUM
 * </p>
 * Apex class Name    : Authsummary_c_Hum  
 * Version            : 1.0
 * Function           : Test class for AuthReferral_Security_H_HUM
 * Created Date       : 12/08/2015 
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                29042                12/08/2015                 Original Version
 * Sruthi Adivikolanu          29646                01/27/2016                 Renamed AuthRefferal_Wrapper_H_HUM to AuthRefferal_Wrapper_DTO_HUM
 * Sruthi Adivikolanu          29646                02/02/2016                 Changes made to test class to increse code coverage
 * Santhi Mandava                                   03/12/2019                 Removed DpaaSWebServiceMockImpl uage
 ***************************************************************************************************************************/

@isTest
private class AuthReferral_Security_T_HUM
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
            lstAcc.add(new Account(Name='Test4',Home_Office_Account__c=false,Group_Number__c='000003',Tenant_Id__c='00',General_Account__c = true));
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
            
            Policy_Member__c polMember = HUMTestDataHelperTest.getPolicyMember();
            polMember.Name='Policy Member PM1 HO';
            polMember.Policy__c=polHO.Id;
            polMember.Member__c=lstAcc[1].Id;
            insert polMember;
            system.assertEquals(polMember.Id != Null,true);
            system.assertEquals(polMember.Name,'Policy Member PM1 HO');
            system.assertEquals(polMember.Policy__c,polHO.Id);
            
            Policy_Member__c polMembergen = HUMTestDataHelperTest.getPolicyMember();
            polMembergen.Name='Policy Member PM1 Gen';
            polMembergen.Policy__c=polgen.Id;
            polMembergen.Member__c=lstAcc[2].Id;
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
        
         List<Policy_Member__c> lstPolmem = [select Id,Member__c,Name from Policy_Member__c where Name = 'Policy Member PM1 HO'];
         system.assertEquals(lstPolmem.size(),1);
         system.assertEquals(lstPolmem[0].Name,'Policy Member PM1 HO');
         
         List<AuthRefferal_Wrapper_DTO_HUM> Authlist = new List<AuthRefferal_Wrapper_DTO_HUM>();
         for(integer i=0;i<=50;i++){
           AuthRefferal_Wrapper_DTO_HUM obj = new AuthRefferal_Wrapper_DTO_HUM();
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
         AuthReferral_Security_H_HUM objAuthSec = new AuthReferral_Security_H_HUM(); 
         List<AuthRefferal_Wrapper_DTO_HUM> Authlistsec = new List<AuthRefferal_Wrapper_DTO_HUM>();
         Authlistsec = objAuthSec.applySecurityMeasures(Authlist,lstPolmem[0].Member__c); 
         system.assertEquals(Authlistsec.size(),51);
         
         system.assertEquals(objAuthSec.checkHOAccountMember(lstPolmem[0].Member__c),true);
         
         system.assertEquals(objAuthSec.filterHOAccountResults(Authlist).size(),1);
         Test.stopTest();
        }
    }
}