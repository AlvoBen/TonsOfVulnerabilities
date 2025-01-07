/*******************************************************************************************************************************
Apex Class Name : DynamicCustomLinks_DT_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for DynamicCustomLinks_D_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                                          10/23/2015                 Original version
* Lakshmi Madduri                                       03/19/2021                 US 1862825 SonarQube Fixes
*******************************************************************************************************************************/

@isTest
private class DynamicCustomLinks_DT_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        System.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
        }
    }
     
    /*
    * Method Name : testLinksToDisplaySObject
    * Description : This method is used to test the main class functionality
    * Return type : void
    * Parameter   : NA
    */
    @isTest static void testLinksToDisplaySObject()
    {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU', oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Account oAcc = [Select Id,name From Account Where Name = 'test'];
            
            Policy_Member__c opolicy = [select Id from Policy_Member__c where Name = 'HR0234'];
            
            Link__c lnk = [select id from Link__c where Link_Label__c = 'GroupContact' limit 1];
           
            List<Link_Criteria__c> lstCtri = [Select Name,Link__c,Criteria_Type__c,Criteria_Source__c  from Link_Criteria__c order by Link__c];
                                    
            DynamicCustomLinks_D_HUM oDisp = new DynamicCustomLinks_D_HUM();
            
            map<Id,Link__c> mapLinks = oDisp.getLinkLayoutDetails('Policy_Member__c', '', '');
            System.assertEquals(mapLinks.isEmpty(), false);
            
            map<Id,Link__c> mapLinks1 = oDisp.getLinkLayoutDetails('', '', 'HumGroupContact');
            System.assertEquals(mapLinks1.isEmpty(), true);
            
            sobject oAcct = oDisp.RetrieveLinkCriteriaDetails('Account',oAcc.Id, lstCtri);
            System.assertEquals(oAcct != Null, true);
            
            List<Link_Parameter__c> lstparam = oDisp.retrieveLinkParameterFields(String.valueOf(lnk.Id));
            System.assertEquals(lstparam.isEmpty(), false);
            
            sobject opol = oDisp.getLinkQueryStringData(lstparam , 'Policy_Member__c', opolicy.Id);
            System.assertEquals(opol != Null, true);
            
            string profile = oDisp.getUserProfile();
            System.assertEquals(profile != Null, true);
            
            oDisp.getSObjectRecordType('Account', oAcc.Id);
        }
    }
}