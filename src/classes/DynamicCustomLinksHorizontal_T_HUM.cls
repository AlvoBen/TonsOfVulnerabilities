/*******************************************************************************************************************************
Apex Class Name : DynamicCustomLinks_T_HUM
Version         : 1.0
Created On      : 10/20/2015
Function        : Test class for DynamicCustomLinksHorizontal_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        27372                       10/20/2015                 Original version
* Manish Kumar                                          01/27/2016                 Modified test class to accomodate changes related to REQ - 237417, 237421
* Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* T Prasanna Sai Kumar                                  03/22/2021                 Req 1916929 - Dynamic Links Vulnerabilities - Dynamic Custom Link Components - DynamicCustomLinksHorizontal_C_HUM,DynamicCustomLinksHorizontal_T_HUM
*******************************************************************************************************************************/

@isTest
private with sharing class DynamicCustomLinksHorizontal_T_HUM
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
        System.assert(oUser.id != null);
        system.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
            
            Endpoint_Url_HUM__c ObjEndpoint = new Endpoint_Url_HUM__c();
            ObjEndpoint.name ='SSO_LOGINURL';
            ObjEndpoint.Remote_Url__c='https://wwww.example.com';
            insert ObjEndpoint;
            System.assertEquals(ObjEndpoint.name, 'SSO_LOGINURL');
            
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
            lstLC.add(new Link_Criteria__c(Condition__c='Not Equals', Criteria_Source__c='Name', Criteria_Type__c='User Profile', Expected_Value__c='Administrator', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Contains', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='BME IT Developer', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Starts With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='BME', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Does Not Start With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='IT', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Ends With', Criteria_Source__c='Name', Criteria_Type__c='Permission Set', Expected_Value__c='Developer', Link__c=oLnk.Id));
            insert lstLC;
            System.assertEquals(lstLC.size(), 6);
            
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
     * Method Name : testlinkstodisplaysobject
     * Description : This method is used to test the main class functionality
     * Return type : void
     * Parameter   : NA
     */
     private static testMethod void testlinkstodisplaysobject()
     {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            PageReference pageRef = Page.DisplayCustomLinks_VF_HUM;
            Test.setCurrentPage(pageRef);
            
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            Id recID = ApexPages.CurrentPage().getParameters().get('id');
            system.assertequals(recID,acc.id);
            
             ApexPages.currentPage().getParameters().put('actionType','Basic');
            
            DynamicCustomLinksHorizontal_C_HUM objController= new DynamicCustomLinksHorizontal_C_HUM();
            system.assert(objController!=Null);
            
            DynamicCustomLinks_S_HUM objServiceLayer = new DynamicCustomLinks_S_HUM();
            system.assert(objServiceLayer!=Null);
            
            objServiceLayer.mapParams = new Map<String, String>();
            
            DisplayCustomLinks_C_HUM objMain = new DisplayCustomLinks_C_HUM();
            system.assert(objMain!=Null);
            
            objController.sObjname='Account';
            System.assertEquals('Account',objController.sObjname);
            
            objController.recId=acc.id;
            System.assertEquals(acc.id,objController.recId);
            
            objController.prepareLinksToDisplay();
            
            ApexPages.CurrentPage().getParameters().put('slinkId', [Select Id From Link__c Limit 1].Id + '');
            ApexPages.CurrentPage().getParameters().put('sRecId',acc.id);
            
            objController.objServiceLayer = objServiceLayer;
            objController.prepareLinkUrl();
            objController.pageController = objMain ;
            objController.initiate();
            objController.logError();
        }
     }
     
     /*
     * Method Name : testlinkstodisplaysobject
     * Description : This method is used to test the main class functionality
     * Return type : void
     * Parameter   : NA
     */
     private static testMethod void testVFLinks()
     {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            Account acc = [select id,name,Recordtypeid  from Account where name = 'test'];
            System.assertEquals('test',acc.name);
            
            PageReference pageRef = Page.DisplayCustomLinks_VF_HUM;
            Test.setCurrentPage(pageRef);
            
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            Id recID = ApexPages.CurrentPage().getParameters().get('id');
            system.assertequals(recID,acc.id);
            
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            
            DynamicCustomLinksHorizontal_C_HUM objController= new DynamicCustomLinksHorizontal_C_HUM();
            system.assert(objController!=Null);
            
            DynamicCustomLinks_S_HUM objServiceLayer = new DynamicCustomLinks_S_HUM();
            system.assert(objServiceLayer!=Null);
            
            objServiceLayer.mapParams = new Map<String, String>();
            
            DisplayCustomLinks_C_HUM objMain = new DisplayCustomLinks_C_HUM();
            system.assert(objMain!=Null);
            
            objController.sObjname='Account';
            System.assertEquals('Account',objController.sObjname);
            
            objController.recId=acc.id;
            System.assertEquals(acc.id,objController.recId);
            
            objController.prepareLinksToDisplay();
            
            Link__c lnk = [Select Id From Link__c Limit 1];
            
            ApexPages.CurrentPage().getParameters().put('slinkId', lnk.Id + '');
            ApexPages.CurrentPage().getParameters().put('sRecId',acc.id);
            
            Map<String, String> mapJSON = new Map<String, String>{lnk.id + '.' + 'Platform_Code' => 'LV'};
            
            String jsonStr = JSON.serialize(mapJSON);
            
            ApexPages.CurrentPage().getParameters().put('linksJSONMap',jsonStr);
            
            objController.objServiceLayer = objServiceLayer;
            objController.prepareLinkUrl();
            objController.pageController = objMain ;
            objController.initiate();
            objController.populateVFVariables();
        }
     }
}