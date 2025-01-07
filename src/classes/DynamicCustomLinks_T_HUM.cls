/*******************************************************************************************************************************
Apex Class Name : DynamicCustomLinks_T_HUM
Version         : 1.0
Created On      : 10/20/2015
Function        : Test class for DynamicCustomLinks_C_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        27372                       10/20/2015                 Original version
* Manish Kumar                                          01/27/2016                 Modified test class to accomodate changes related to REQ - 237417, 237421
* Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Lakshmi Madduri                                       02/26/2021                 SonarQube Fixes and Coverage Improvement
*******************************************************************************************************************************/

@isTest
private with sharing class DynamicCustomLinks_T_HUM
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
            
            Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        
            Account acc = HUMTestDataHelperTest.getAccount();
            acc.name = 'Test Acc';
            acc.Source_Platform_Code__c ='EM';
            acc.Source_Customer_Key__c = 'XY';
            acc.Recordtypeid = grpRecordTypeId ;
            insert acc;
            system.assertNotEquals(Null,acc.id);
            system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
            
            Link_Layout__c oLL = new Link_Layout__c(Active__c=true, Target_Name__c='Account.Group', Target_Type__c='sObject');
            insert oLL;
            System.assertEquals(oLL.Active__c, true);
            
            Link_Layout_Section__c oLLS = new Link_Layout_Section__c(Index__c=0, Link_Layout__c=oLL.Id, Section_Name__c='Test');
            insert oLLS;
            System.assertEquals(oLLS.Index__c, 0);
            
            Link__c oLnk = new Link__c(Active__c=true, Expression__c='', Index__c=1, Layout_Section__c=oLLS.Id, Link_Action__c='Query String', Link_Label__c='Link', RequireSSO__c=false, 
                                     Target_Type__c='Window', Url__c='http://www.test.com/test');
            insert oLnk;
            System.assertEquals(oLnk.Active__c, true);
            
            List<Link_Criteria__c> lstLC = new List<Link_Criteria__c>();
            lstLC.add(new Link_Criteria__c(Condition__c='Equals', Criteria_Source__c='Name', Criteria_Type__c='Database Field', Expected_Value__c='Test Acc', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Not Equals', Criteria_Type__c='User Profile', Expected_Value__c='System Administrator', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Contains', Criteria_Type__c='Permission Set', Expected_Value__c='BME IT Developer', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Starts With', Criteria_Source__c='pageId:formId:pgBlockId:pbsClaimDetail:PlatformCodePBS:PlatformCodeTextId', Criteria_Type__c='VisualForce Page', Expected_Value__c='EM', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Does Not Start With', Criteria_Source__c='Static', Criteria_Type__c='App Name', Expected_Value__c='IT', Link__c=oLnk.Id));
            lstLC.add(new Link_Criteria__c(Condition__c='Ends With', Criteria_Type__c='Permission Set', Expected_Value__c='Developer', Link__c=oLnk.Id));
            insert lstLC;
            System.assertEquals(lstLC.size(), 6);
            
            //Insert Params
            List<Link_Parameter__c> lstparams = new List<Link_Parameter__c>();
            Link_Parameter__c objLinkParam = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=true,IsRequired__c=false,Variable_Name__c='Name',Variable_Source__c='Account',Variable_Type__c='Query By',Key_Name__c='Id');
            lstparams.add(objLinkParam);
            Link_Parameter__c objLinkParam1 = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=false,IsRequired__c=false,Variable_Name__c='EntityType',Variable_Source__c='Member',Variable_Type__c='Static');
            lstparams.add(objLinkParam1);
            Link_Parameter__c objLinkParam2 = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=false,IsRequired__c=false,Variable_Source__c='Type',Variable_Name__c='Type',Variable_Type__c='Database Field',key_name__c='Id');
            lstparams.add(objLinkParam2);
            insert lstparams;
            System.assertEquals(lstparams.size(), 3);
            
            Link_Conditional_Parameter__c lnkCondParam = new Link_Conditional_Parameter__c();     
            lnkCondParam.Conditional_Check__c = 'Name=\'Test Acc\'';
            lnkCondParam.Conditional_Type__c = 'If';
            lnkCondParam.Conditional_Value__c = 'Name';
            lnkCondParam.Variable_Type__c='Database Field';
            lnkCondParam.Link_Parameter__c = objLinkParam.id;
            lnkCondParam.Encode__c=false;
            insert lnkCondParam;
            System.assertEquals(lnkCondParam.Link_Parameter__c,objLinkParam.id);
            
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
     @isTest static void testlinkstodisplaysobject()
     {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            Test.startTest();
            Account acc = [select id,name,Recordtypeid  from Account where name = 'Test Acc'];
            System.assertEquals('Test Acc',acc.name);
            
            PageReference pageRef = Page.DisplayCustomLinks_VF_HUM;
            Test.setCurrentPage(pageRef);            
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            Id recID = ApexPages.CurrentPage().getParameters().get('id');
            system.assertequals(recID,acc.id);
            
            DynamicCustomLinks_C_HUM objController= new DynamicCustomLinks_C_HUM();
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
            
            Link__c lnk = [Select Id,Expression__c,Link_Label__c,createdbyid From Link__c where Link_Label__c='Link' and createdbyId=:oUser.Id Limit 1];
            Link_Layout_Section__c objLnkSec = [SELECT Id,Section_Name__c from Link_Layout_Section__c where Section_Name__c='Test' limit 1];
            Link_Criteria__c objLnkCrt1,objLnkCrt2,objLnkCrt3,objLnkCrt4,objLnkCrt5,objLnkCrt6;
            for(Link_Criteria__c oCrt : [SELECT Id,Criteria_Source__c,Name,Criteria_Type__c,Link__c,Expected_Value__c from Link_Criteria__c where Link__c=:lnk.Id])
            {
                if(oCrt.Criteria_Source__c=='Name'){
                    objLnkCrt1 = oCrt;
                }
                else if(oCrt.Criteria_Source__c=='pageId:formId:pgBlockId:pbsClaimDetail:PlatformCodePBS:PlatformCodeTextId'){
                    objLnkCrt2=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='User Profile'){
                    objLnkCrt3=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='Permission Set' && oCrt.Expected_Value__c=='BME IT Developer'){
                    objLnkCrt4=oCrt;
                }
                else if(oCrt.Criteria_Source__c=='Static' && oCrt.Criteria_Type__c=='App Name'){
                    objLnkCrt5=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='Permission Set' && oCrt.Expected_Value__c=='Developer'){
                    objLnkCrt6=oCrt;
                }
            }           
            ApexPages.CurrentPage().getParameters().put('slinkId', lnk.Id);
            ApexPages.CurrentPage().getParameters().put('sRecId',acc.id);
            ApexPages.CurrentPage().getParameters().put('sobjname',objController.sObjname);
            
            String slnkMap = '{"'+lnk.Id+'":{"attributes":{"type":"Link__c","url":"/services/data/v51.0/sobjects/Link__c/'+lnk.Id+'"},"Id":"'+lnk.Id+'","Active__c":true,"Expression__c":"'+lnk.Expression__c+'","Index__c":1,"Layout_Section__c":"'+objLnkSec.Id+'","Link_Action__c":"Query String","Link_Label__c":"Link","Display_Hover_Over__c":false,"Target_Type__c":"Window","RequireSSO__c":false,"Url__c":"http://www.test.com/test","Link_Criteria__r":{"totalSize":6,"done":true,"records":[{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt1.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt1.Id+'","Name":"'+objLnkCrt1.Name+'","Expected_Value__c":"Test Acc","Criteria_Type__c":"Database Field","Criteria_Source__c":"Name","Condition__c":"Equals"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt2.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt2.Id+'","Name":"'+objLnkCrt2.Name+'","Expected_Value__c":"Administrator","Criteria_Type__c":"User Profile","Condition__c":"Not Equals"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt3.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt3.Id+'","Name":"'+objLnkCrt3.Name+'","Expected_Value__c":"BME IT Developer","Criteria_Type__c":"Permission Set","Condition__c":"Contains"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt4.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt4.Id+'","Name":"'+objLnkCrt4.Name+'","Expected_Value__c":"EM","Criteria_Type__c":"VisualForce Page","Criteria_Source__c":"pageId:formId:pgBlockId:pbsClaimDetail:PlatformCodePBS:PlatformCodeTextId","Condition__c":"Starts With"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt5.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt5.Id+'","Name":"'+objLnkCrt5.Name+'","Expected_Value__c":"IT","Criteria_Type__c":"App Name","Criteria_Source__c":"Static","Condition__c":"Does Not Start With"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt6.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt6.Id+'","Name":"'+objLnkCrt6.Name+'","Expected_Value__c":"Developer","Criteria_Type__c":"Permission Set","Condition__c":"Ends With"}]},"Layout_Section__r":{"attributes":{"type":"Link_Layout_Section__c","url":"/services/data/v51.0/sobjects/Link_Layout_Section__c/'+objLnkSec.Id+'"},"Id":"'+objLnkSec.Id+'","Index__c":0,"Section_Name__c":"Test"}}}';
            ApexPages.CurrentPage().getParameters().put('lnkMap',slnkMap);
            ApexPages.CurrentPage().getParameters().put('actionType','LinkParam');
            Map<String, String> mapJSON = new Map<String, String>{objLnkCrt6.id + '.' + 'Platform_Code.'+lnk.Id => 'EM'};            
            String jsonStr = JSON.serialize(mapJSON);            
            ApexPages.CurrentPage().getParameters().put('linksJSONMap',jsonStr);
            
            objController.pageController = objMain ;
            objController.objServiceLayer = objServiceLayer;
            objController.initiate();                       
            objController.prepareLinkUrl();
            objController.populateVFVariables();
            objController.logError();
            
            System.assertEquals(lnk.Expression__c, objController.objLnk.Expression__c);
            System.assertEquals(lnk.Link_Label__c, objController.objLnk.Link_Label__c);            
            Test.stopTest();
        }
     }
     
     /*
     * Method Name : testlinkstodisplaysobject
     * Description : This method is used to test the main class functionality
     * Return type : void
     * Parameter   : NA
     */
     @isTest static void testVFLinks()
     {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            Test.startTest();
            Account acc = [select id,name,Recordtypeid  from Account where name = 'Test Acc'];
            System.assertEquals('Test Acc',acc.name);
            
            PageReference pageRef = Page.DisplayCustomLinks_VF_HUM;
            Test.setCurrentPage(pageRef);            
            ApexPages.CurrentPage().getParameters().put('id',acc.id);
            Id recID = ApexPages.CurrentPage().getParameters().get('id');
            system.assertequals(recID,acc.id);
            
            DynamicCustomLinks_C_HUM objController= new DynamicCustomLinks_C_HUM();
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

            Link__c lnk = [Select Id,Expression__c,Link_Label__c,createdbyid From Link__c where Link_Label__c='Link' and createdbyId=:oUser.Id Limit 1];
            Link_Layout_Section__c objLnkSec = [SELECT Id,Section_Name__c from Link_Layout_Section__c where Section_Name__c='Test' limit 1];
            Link_Criteria__c objLnkCrt1,objLnkCrt2,objLnkCrt3,objLnkCrt4,objLnkCrt5,objLnkCrt6;
            for(Link_Criteria__c oCrt : [SELECT Id,Criteria_Source__c,Name,Criteria_Type__c,Link__c,Expected_Value__c from Link_Criteria__c where Link__c=:lnk.Id])
            {
                if(oCrt.Criteria_Source__c=='Name'){
                    objLnkCrt1 = oCrt;
                }
                else if(oCrt.Criteria_Source__c=='pageId:formId:pgBlockId:pbsClaimDetail:PlatformCodePBS:PlatformCodeTextId'){
                    objLnkCrt2=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='User Profile'){
                    objLnkCrt3=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='Permission Set' && oCrt.Expected_Value__c=='BME IT Developer'){
                    objLnkCrt4=oCrt;
                }
                else if(oCrt.Criteria_Source__c=='Static' && oCrt.Criteria_Type__c=='App Name'){
                    objLnkCrt5=oCrt;
                }
                else if(oCrt.Criteria_Type__c=='Permission Set' && oCrt.Expected_Value__c=='Developer'){
                    objLnkCrt6=oCrt;
                }
            }           
            ApexPages.CurrentPage().getParameters().put('slinkId', lnk.Id + '');
            ApexPages.CurrentPage().getParameters().put('sRecId',acc.id);
            ApexPages.CurrentPage().getParameters().put('sobjname',objController.sObjname);
            String slnkMap = '{"'+lnk.Id+'":{"attributes":{"type":"Link__c","url":"/services/data/v51.0/sobjects/Link__c/'+lnk.Id+'"},"Id":"'+lnk.Id+'","Active__c":true,"Expression__c":"'+lnk.Expression__c+'","Index__c":1,"Layout_Section__c":"'+objLnkSec.Id+'","Link_Action__c":"Basic","Link_Label__c":"Link","Display_Hover_Over__c":false,"Target_Type__c":"Window","RequireSSO__c":false,"Url__c":"http://www.test.com/test","Link_Criteria__r":{"totalSize":6,"done":true,"records":[{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt1.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt1.Id+'","Name":"'+objLnkCrt1.Name+'","Expected_Value__c":"Test Acc","Criteria_Type__c":"Database Field","Criteria_Source__c":"Name","Condition__c":"Equals"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt2.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt2.Id+'","Name":"'+objLnkCrt2.Name+'","Expected_Value__c":"Administrator","Criteria_Type__c":"User Profile","Condition__c":"Not Equals"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt3.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt3.Id+'","Name":"'+objLnkCrt3.Name+'","Expected_Value__c":"BME IT Developer","Criteria_Type__c":"Permission Set","Condition__c":"Contains"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt4.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt4.Id+'","Name":"'+objLnkCrt4.Name+'","Expected_Value__c":"EM","Criteria_Type__c":"VisualForce Page","Criteria_Source__c":"pageId:formId:pgBlockId:pbsClaimDetail:PlatformCodePBS:PlatformCodeTextId","Condition__c":"Starts With"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt5.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt5.Id+'","Name":"'+objLnkCrt5.Name+'","Expected_Value__c":"IT","Criteria_Type__c":"App Name","Criteria_Source__c":"Static","Condition__c":"Does Not Start With"},{"attributes":{"type":"Link_Criteria__c","url":"/services/data/v51.0/sobjects/Link_Criteria__c/'+objLnkCrt6.Id+'"},"Link__c":"'+lnk.Id+'","Id":"'+objLnkCrt6.Id+'","Name":"'+objLnkCrt6.Name+'","Expected_Value__c":"Developer","Criteria_Type__c":"Permission Set","Condition__c":"Ends With"}]},"Layout_Section__r":{"attributes":{"type":"Link_Layout_Section__c","url":"/services/data/v51.0/sobjects/Link_Layout_Section__c/'+objLnkSec.Id+'"},"Id":"'+objLnkSec.Id+'","Index__c":0,"Section_Name__c":"Test"}}}';
            ApexPages.CurrentPage().getParameters().put('lnkMap',slnkMap);
            ApexPages.CurrentPage().getParameters().put('actionType','LinkCriteria');
            Map<String, String> mapJSON = new Map<String, String>{objLnkCrt6.id + '.' + 'Platform_Code.'+lnk.Id => 'EM'};            
            String jsonStr = JSON.serialize(mapJSON);            
            ApexPages.CurrentPage().getParameters().put('linksJSONMap',jsonStr);
            
            objController.objServiceLayer = objServiceLayer;
            objController.pageController = objMain ;
            objController.initiate();           
            objController.prepareLinkUrl();
            objController.populateVFVariables();
            
            List<DynamicLinksWrapper_H_HUM> lstLinks = new List<DynamicLinksWrapper_H_HUM>();
            DynamicCustomLinks_S_HUM objService = new DynamicCustomLinks_S_HUM();
            objService.putInList(lnk,lstLinks);
            System.assertEquals(lnk.Expression__c, objController.objLnk.Expression__c);
            System.assertEquals(lnk.Link_Label__c, objController.objLnk.Link_Label__c);            
            Test.stopTest();
        }
     }
}