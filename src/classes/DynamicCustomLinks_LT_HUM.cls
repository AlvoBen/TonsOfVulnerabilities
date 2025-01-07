/*******************************************************************************************************************************
Apex Class Name : DynamicCustomLinks_LT_HUM
Version         : 1.0
Created On      : 24/06/2021
Function        : Test class for DynamicCustomLinks_LC_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                      06/24/2021                  Original version
* Ranadheer Alwal                                      09/01/2021                  Increased code coverage
*******************************************************************************************************************************/

@isTest
private with sharing class DynamicCustomLinks_LT_HUM
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
        oUser.Network_User_Id__c = 'PAB4806';
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
                                     Target_Type__c='Window', Url__c='http://www.test.com/test', DefaultUrl__c = 'http://www.test.com/test');
            insert oLnk;
            System.assertEquals(oLnk.Active__c, true);
            
             Link__c oLnk1 = new Link__c(Active__c=true, Expression__c='', Index__c=1, Layout_Section__c=oLLS.Id, Link_Action__c='Basic', Link_Label__c='Link1', RequireSSO__c=false, 
                                     Target_Type__c='Window', Url__c='http://www.test.com/test', DefaultUrl__c = 'http://www.test.com/test');
            insert oLnk1;
            System.assertEquals(oLnk1.Active__c, true);
            
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
            Link_Parameter__c objLinkParam3 = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=false,IsRequired__c=false,Variable_Source__c='Type',Variable_Name__c='Type',Variable_Type__c='VisualForce Page',key_name__c='Id');
            lstparams.add(objLinkParam3);
            Link_Parameter__c objLinkParam4 = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=false,IsRequired__c=false,Variable_Source__c='Type',Variable_Name__c='Type',Variable_Type__c='Child Collection',key_name__c='Id');
            lstparams.add(objLinkParam4);
            Link_Parameter__c objLinkParam5 = new Link_Parameter__c(Link__c=oLnk.Id,Encode__c=false,IsConditional__c=false,IsRequired__c=false,Variable_Source__c='Type',Variable_Name__c='Type',Variable_Type__c='Cache',key_name__c='Id');
            lstparams.add(objLinkParam5);
            insert lstparams;
            System.assertEquals(lstparams.size(), 6);
            
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
        User oUser = [Select Id,Current_Queue__c, Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            Test.startTest();
            Account acc = [select id,name,Recordtypeid  from Account where name = 'Test Acc'];
            System.assertEquals('Test Acc',acc.name);
            
            DynamicCustomLinks_LC_HUM.DynamicCustomLinksResultWrapper result = DynamicCustomLinks_LC_HUM.initiate(acc.id, '');
            System.assertEquals(result == null,false);  

            Link__c lnk = [SELECT Id, OwnerId, IsDeleted, Name, CreatedDate, CreatedById, LastModifiedDate, Active__c, Expression__c, Index__c, Layout_Section__c, Link_Action__c, Link_Label__c, RequireSSO__c, Target_Type__c, Url__c, Display_Hover_Over__c, Hover_Over_Text__c, Icon_Path__c, DefaultUrl__c FROM Link__c where Link_Label__c = 'Link'];
            System.assertEquals('Link',lnk.Link_Label__c);
            
            Link__c lnk1 = [SELECT Id, OwnerId, IsDeleted, Name, CreatedDate, CreatedById, LastModifiedDate,  Active__c, Expression__c, Index__c, Layout_Section__c, Link_Action__c, Link_Label__c, RequireSSO__c, Target_Type__c, Url__c, Display_Hover_Over__c, Hover_Over_Text__c, Icon_Path__c, DefaultUrl__c FROM Link__c where Link_Label__c = 'Link1'];
            System.assertEquals('Link1',lnk1.Link_Label__c);
            
            DynamicCustomLinks_LC_HUM.prepareLinkUrlResultWrapper linkResult = DynamicCustomLinks_LC_HUM.prepareLinkUrlOnClick(lnk.Id,acc.id,'Account',JSON.serialize(result.mapLinks),result.mapLinks);
            System.assertEquals(linkResult == null,false);
            
            DynamicCustomLinks_LC_HUM.prepareLinkUrlResultWrapper linkResult1 = DynamicCustomLinks_LC_HUM.prepareLinkUrlOnClick(lnk1.Id,acc.id,'Account',JSON.serialize(result.mapLinks),result.mapLinks);
            System.assertEquals(linkResult == null,false);

            DynamicCustomLinks_LC_HUM.DynamicCustomLinksConsolidatedWrapper finalresult = DynamicCustomLinks_LC_HUM.populateVFVariables(null,lnk,linkResult.sVFJSON,false,'LinkParam',
            result.mapLinks,result.maplnktorf,'Account',acc.Id,result.lstFinalCustomLinks, linkResult.mapParams);
            System.assertEquals(finalresult == null,false);
            
            DynamicCustomLinks_LC_HUM.DynamicCustomLinksConsolidatedWrapper linkCriteriaFinalResult = DynamicCustomLinks_LC_HUM.populateVFVariables(null,lnk,linkResult.sVFJSON,false,'LinkCriteria',
            result.mapLinks,result.maplnktorf,'Account',acc.Id,result.lstFinalCustomLinks, linkResult.mapParams);
            System.assertEquals(linkCriteriaFinalResult == null,false);
            List<Link_Parameter__c> lstlinkparam = [SELECT Id, IsDeleted, Name, CreatedDate, CreatedById, LastModifiedDate, Link__c, IsConditional__c, Variable_Name__c, Variable_Source__c, Variable_Type__c, Key_Name__c, Encode__c, IsRequired__c FROM Link_Parameter__c limit 2];
            LinkParameterLogic_LH_HUM.processVFParameterType(lstlinkparam[0], null, acc.Id, false);
            LinkParameterLogic_LH_HUM.processChildCollectionParameterType(lstlinkparam[0], null, acc.Id);
            LinkParameterLogic_LH_HUM.processCacheParameterType(lstlinkparam[0], new List<User>{new User(id=ouser.Id,Network_User_Id__c = 'abcd')});
            LinkParameterLogic_LH_HUM.getHierarchicalParameterValue('a\\.b\\.c', null);
            LinkParameterLogic_LH_HUM.getFinalParamValue(true, 'svalue');
            LinkParameterLogic_LH_HUM.processStaticParameterType(new Link_Parameter__c(Variable_Source__c=null));
            LinkParameterLogic_LH_HUM.processQueryByParameterType(new Link_Parameter__c(IsConditional__c = false), null, null);
            
            DynamicCustomLinks_LS_HUM.getCurrentPage();
            DynamicCustomLinks_LD_HUM.getLinkParameters(new set<String>{lnk.Id});
            sObject objInstance  =new Account();
            objInstance.put('Id',acc.Id);
            Link_Conditional_Parameter__c conditional = [select Id,Link_Parameter__c, Conditional_Check__c, Conditional_Type__c, Conditional_Value__c, Variable_Type__c, Encode__c, Priority__c FROM Link_Conditional_Parameter__c where Conditional_Check__c = 'Name=\'Test Acc\'' limit 1];
            
            Link_Parameter__c lnkparam3 = new Link_Parameter__c();
            lnkparam3.Link__c = lnk.Id;
            lnkparam3.Variable_Name__c = 'target';
            lnkparam3.Variable_Source__c = 'Policy';
            lnkparam3.Variable_Type__c = 'Static';
            insert lnkparam3;
            Link_Conditional_Parameter__c lnkCondParam = new Link_Conditional_Parameter__c();     
            lnkCondParam.Conditional_Check__c = 'EM';
            lnkCondParam.Conditional_Type__c = 'ELSE';
            lnkCondParam.Conditional_Value__c = 'E';
            lnkCondParam.Variable_Type__c = 'sObject';
            lnkCondParam.Link_Parameter__c = lnkparam3.id;
            insert lnkCondParam;
            
            Link_Parameter__c linkparamWithElse = [SELECT Id, (select Conditional_Check__c,Encode__c,Conditional_Value__c,Conditional_Type__c from Link_Conditional_Parameters__r) ,Name FROM Link_Parameter__c where id=:lnkparam3.id];
            LinkParameterLogic_LH_HUM.checkParameterCondition(null, linkparamWithElse, true, acc.Id, null);
            LinkParameterLogic_LH_HUM.processIfConditionalType(lnkCondParam, lstlinkparam, null, true, acc.Id, null);
            lnkCondParam.Variable_Type__c = 'Static';
            LinkParameterLogic_LH_HUM.processIfConditionalType(lnkCondParam, lstlinkparam, 'EM', true, acc.Id, null);
            //DynamicCustomLinks_LS_HUM.prepareLinkUrl(lnk.id, acc.Id, 'Account', new Map<Id,Link__c>{lnk.Id=>lnk} , false, new Map<Id,Link__c>{lnk.Id=>lnk}, new Map<String,String>{'Id'=>'Id'}, lstlinkparam, true, true);
            
            DynamicCustomLinks_LD_HUM.getChildCollection('Account', conditional, acc.Id, 'Id', false,objInstance);
            DynamicCustomLinks_LD_HUM.getLinkLayoutDetails('Account', 'Member', 'AccountVfPage','Vertical');
            DynamicCustomLinks_LC_HUM.includeLinkParameterFormVF(linkResult.mapParams, new Map<String,String>{'001Z000001VaV5hIAF'=>null}, acc.Id);
            DynamicCustomLinks_LS_HUM.includeVFCriteriaLinks(new Map<String,String>{null=>null}, acc.Id, 'Account', result.mapLinks, result.maplnktorf);
            DynamicCustomLinks_LC_HUM.includeVFParameterValues(new Map<String,String>{acc.Id+'\\.'+acc.Id+'\\.'+acc.Id=>null}, acc.Id);
            Test.stopTest();
        }
     }
     
     
}