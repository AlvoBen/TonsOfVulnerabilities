/******************************************************************************************************************************       
Apex Class Name  : ChangeHasVitality_T_HUM
Version          : 1.0  
Created Date     : Nov 30, 2015

****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               11/30/2015                    Original Version
*************************************************************************************************************************/

@isTest
public class ChangeHasVitality_T_HUM
{

/*
* Method Name   :    datasetup() 
* Description   :    Method to set the test data. 
* return Type   :    list
* Parameters    :    null
*/   
    @testsetup static void datasetup() 
    {       
        list<id> listid = new list<id>();
        
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        insert objAcc;
        listid.add(objAcc.id);
        
        Policy__c policy = HUMTestDataHelperTest.getPolicy();
        policy.Group_Name__c = objAcc.id;
        insert policy;
        
        Policy_Member__c oPolicymem = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem.Name='testpolmem';
        oPolicymem.Policy__c = policy.id;
        oPolicymem.Member__c = objAcc.id;
        oPolicymem.Has_Vitality__c = 'N';
        insert oPolicymem ;
        listid.add(oPolicymem.id);
        
        Member_Cache__c memCacheObject = new Member_Cache__c();
        memCacheObject.AccountID__c = objAcc.id;
        insert memCacheObject  ;
        listid.add(memCacheObject.id);
    }

/*
* Method Name   :    testdelete() 
* Description   :    Method to test "MemberCacheDelete_X_HUM" ,"MemCacheSelDel_X_HUM" and "ChangeHasVitality_G_HUM" functionality. 
* return Type   :    null
* Parameters    :    null
*/        
    @istest static void testdelete() 
    { 
        list<Account> lstAccount = [select id from account];

        Member_Cache__c[] oMemberCache = [Select id,AccountID__c from Member_Cache__c where AccountID__c =: lstAccount.get(0).id];
        
        PageReference pageRef = Page.MemberCacheDelete_VF_HUM; 
        Test.setCurrentPage(pageRef);
        PageReference pageRef1 = Page.MemCacheSelDel_VF_HUM; 
        Test.setCurrentPage(pageRef);

        ApexPAges.StandardController sc = new ApexPages.StandardController(oMemberCache[0]);
        ApexPAges.StandardSetController ssc = new ApexPages.StandardSetController(oMemberCache);
        
        Test.startTest();
            MemberCacheDelete_X_HUM oMemberCacheDelete = new MemberCacheDelete_X_HUM(sc);
            oMemberCacheDelete.cachedelete();
            oMemberCacheDelete.getDisplayMessage();
            
            MemCacheSelDel_X_HUM oMemberCacheSelDelete = new MemCacheSelDel_X_HUM(ssc);
            oMemberCacheSelDelete.cachedelete();
            oMemberCacheSelDelete.getDisplayMessage();
        Test.stopTest();
    }
}