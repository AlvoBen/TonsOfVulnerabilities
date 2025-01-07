/*******************************************************************************************************************************************************
Apex Class Name  : MatchCondition_T_HUM
Version          : 1.0
Created Date     : 01/13/2016
Function         : This is test class for MatchCondition_H_HUM class
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              20725                    01/13/2016                 Original Version
* Manish Kumar                                       01/27/2016                 Modified test class to accomodate changes related to REQ - 237417, 237421
* Prerna Prasad             222286					 03/28/2016                REQ - 357293 - Link criteria for "Not Contains" option
* Pradeep Dani				246226					 06/20/2016                Adding "Not In" functionality to add multple values in expected value.
* Suraj Patil                                        09/01/2021                Increased code coverage 
**********************************************************************************************************************************************/

@isTest
private class MatchCondition_T_HUM
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
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        system.runAs(oUser)
        {
            Account oAcc = new Account(Name='Test Acc');
            insert oAcc;
            System.AssertEquals(oAcc.Name, 'Test Acc');
            
            Link_Layout__c oLL = new Link_Layout__c(Active__c=true, Target_Name__c='Account.Group', Target_Type__c='sObject');
            insert oLL;
            System.assertEquals(oLL.Active__c, true);
            
            Link_Layout_Section__c oLLS = new Link_Layout_Section__c(Index__c=0, Link_Layout__c=oLL.Id, Section_Name__c='');
            insert oLLS;
            System.assertEquals(oLLS.Index__c, 0);
            
            Link__c oLnk = new Link__c(Active__c=true, Expression__c='', Index__c=0, Layout_Section__c=oLLS.Id, Link_Action__c='Basic', Link_Label__c='Link', RequireSSO__c=false, 
                                     Target_Type__c='Window', Url__c='http://www.test.com/test');
            insert oLnk;
            System.assertEquals(oLnk.Active__c, true);
            
            List<Link_Criteria__c> lstLC = new List<Link_Criteria__c>();
            lstLC.add(new Link_Criteria__c(Condition__c='Equals', Criteria_Source__c='Name', Criteria_Type__c='Database Field', Expected_Value__c='123', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Equals', Criteria_Source__c='CreatedBy.Name', Criteria_Type__c='Database Field', Expected_Value__c='Test', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Not Equals', Criteria_Source__c='Name', Criteria_Type__c='User Profile', Expected_Value__c='Administrator', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Contains', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='BME IT Developer', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Starts With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='BME', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Does Not Start With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='IT', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Ends With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='Developer', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Equals', Criteria_Source__c='Name', Criteria_Type__c='VisualForce Page', Expected_Value__c='Developer', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Not Contains', Criteria_Source__c='CreatedBy.Name', Criteria_Type__c='Database Field', Expected_Value__c='Test', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c = 'Not In', Criteria_Source__c = 'CreatedBy.Name', Criteria_Type__c = 'Database Field', Expected_Value__c = 'Test1,test2', Link__c = oLnk.Id));
            insert lstLC;
            System.assertEquals(lstLC.size(), 10);
            
            Set<ID> setID = new Set<ID>();
            
            for(Link_Criteria__c oLC : lstLC)
            {
                setID.add(oLC.Id);
            }
            
            for(Link_Criteria__c oLC : [Select Id, Name From Link_Criteria__c Where Id  IN: setID])
            {
                oLnk.Expression__c += oLC.Name + ' AND ';
            }
            oLnk.Expression__c = oLnk.Expression__c.removeEnd(' AND ');
            update oLnk;
            
            System.assert(oLnk.Expression__c != '');
        }
    }
    
    /*
    * Method Name : testfunctionality
    * Description : This method is used to test the Link criteria evaluation
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testfunctionality()
    {
        User oUser = [Select Id, Current_Queue__c, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            Account oAcc = [Select Id, name From Account];
            System.assertEquals(oAcc.Name, 'Test Acc');
            
            Map<Id, Link__c> mapLnk = new Map<Id, Link__c>([Select Active__c, Expression__c, Index__c, Layout_Section__c, Link_Action__c, Link_Label__c, RequireSSO__c, Target_Type__c, Url__c, 
                                                            (Select Name, Condition__c, Criteria_Source__c, Criteria_Type__c, Expected_Value__c, Link__c From Link_Criteria__r) From Link__c]);
            System.assertEquals(mapLnk.size(), 1);
            
            List<Link_Criteria__c> lstLC = [Select Name, Condition__c, Criteria_Source__c, Criteria_Type__c, Expected_Value__c, Link__c From Link_Criteria__c];
            System.assertEquals(lstLC.size(), 10);
            
            Map<String, Boolean> mapLnkCriteria = MatchCondition_H_HUM.validateLinkExpression(lstLC, new Map<String, Boolean>(), 'Account', String.valueOf(oAcc.Id), mapLnk);
            System.assertEquals(mapLnkCriteria.size(), 2);
            
            Link__c lnk = [Select Id From Link__c Limit 1];
            
            Map<String, String> mapVFResults = new Map<String, String>{lnk.id + '.' + 'Platform_Code' => ''};
            
            mapLnkCriteria = MatchCondition_H_HUM.validateLinkExpressionForVF(lstLC, new Map<String, Boolean>(), 'Account', String.valueOf(oAcc.Id), mapLnk, mapVFResults);
            list<string> lstPR = new List<string>();
            boolean bflag1 = MatchCondition_H_HUM.checkUserGroupPermissionSet('abc','Equals',lstPR);
            boolean bflag12 = MatchCondition_H_HUM.checkUserGroupPermissionSet('abc','Not Equals',lstPR);
            lstPR.add('abc');
            boolean bflagEquals = MatchCondition_H_HUM.checkUserGroupPermissionSet('abc','Equals',lstPR);
            boolean bflagNotEquals = MatchCondition_H_HUM.checkUserGroupPermissionSet('abc','Not Equals',lstPR);
            
            Link__c lnkLink = [select id, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r limit 1) from  link__c Limit 1];
            Map<string,boolean> mapLnkCrtev = new Map<string,boolean>();
            mapLnkCrtev.put(lnkLink.Link_Criteria__r[0].Name,true);
            Map<String, Boolean> mapTempLnks = new Map<String, Boolean>();
            MatchCondition_H_HUM.putInMap(lnkLink,mapLnkCrtev,mapTempLnks);
            System.assertEquals(mapLnkCriteria.size(), 1);
        }
    }
}