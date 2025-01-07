/*******************************************************************************************************************************
Apex Class Name : LinkParameterLogic_T_HUM
Version         : 1.0
Created On      : 04/18/2016
Function        : Test class for LinkParameterLogic_H_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                                         04/18/2016                 Original version
* Kritika Popat             38698                      09/08/2016                 REQ for Enable/Disable URL encoding
* Anupama Tavva             78689                      03/22/2016                 Added isRequired field for Link paramter for the REQ-304159
* Pradeepkumar Dani         88534                      04/28/2017                 REQ - 311469 - Rx Lookup PROD issue - Framework updates for link param 'ChildCollection' w.r.t isRequired flag
* Prasanthi Kandula         102588                     06/16/2017                 REQ - 318478 - CAS Link - Framework updates to handle more than one link conditional paramater records for link param 'Child collection'
* Santhi Mandava                                       03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Lakshmi Madduri                                      05/05/2021                 2235996 - Code coverage Improvement
*******************************************************************************************************************************/

@isTest
private class LinkParameterLogic_T_HUM
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
        System.assert(oUser.id != null);
        
        System.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
            
            Link__c lnk = [Select Id,Link_Action__c From Link__c Where Link_Action__c = 'Query String' Limit 1];
            System.assertEquals(lnk.Link_Action__c,'Query String');
            
            List<Link_Parameter__c> lstparam = new List<Link_Parameter__c>();
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Encode__c = true, Variable_Name__c = 'Id', Variable_Source__c = '123', Variable_Type__c = 'VisualForce Page'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Variable_Name__c = 'AKA_Name', Variable_Source__c = 'AKAName', Variable_Type__c = 'Static'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, IsConditional__c = true, Variable_Name__c = 'SSN', Variable_Source__c = 'Member_Id__c', Variable_Type__c = 'Child Collection', Key_Name__c = 'Policy_Member__c'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, IsConditional__c = true, Variable_Name__c = 'MemberGenKey', Variable_Source__c = 'Member_Cache__c', Variable_Type__c = 'Query By', Key_Name__c = 'Member__c'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, IsConditional__c = false, Variable_Name__c = 'Policy', Variable_Source__c = 'Policy_Member__c', Variable_Type__c = 'Query By', Key_Name__c = 'Policy__c'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id,Variable_Name__c = 'AKA_Name',IsRequired__c=false,Variable_Type__c = 'Static'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Variable_Name__c = 'Id',IsRequired__c=true,Variable_Source__c = '',Variable_Type__c = 'VisualForce Page'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Variable_Name__c = 'Id',Variable_Source__c = 'sxs2222',Variable_Type__c = 'Cache'));
            insert lstparam;
            system.assertEquals(8,lstparam.size());
           
            List<Link_Conditional_Parameter__c> lstCondParam = new List<Link_Conditional_Parameter__c>();
            lstCondParam.add(new Link_Conditional_Parameter__c(Link_Parameter__c=lstparam[2].Id,Conditional_Value__c='Name',Conditional_Check__c='Type__c=\'SSN\'',Variable_Type__c='Database Field'));
            lstCondParam.add(new Link_Conditional_Parameter__c(Link_Parameter__c=lstparam[3].Id,Conditional_Value__c='Member_Gen_Key__c',Conditional_Check__c='AccountID__c=#',Variable_Type__c='Database Field'));
            insert lstCondParam;
            system.assertEquals(2,lstCondParam.size());
            
            Endpoint_Url_HUM__c ObjEndpoint = new Endpoint_Url_HUM__c();
            ObjEndpoint.name ='SSO_LOGINURL';
            ObjEndpoint.Remote_Url__c='https://wwww.example.com';
            insert ObjEndpoint;
            System.assertEquals(ObjEndpoint.name, 'SSO_LOGINURL');
        }
    }
    
    /*
    * Method Name : createLinkParameters
    * Description : This method is used to test the Link Parameter conditions
    * Return type : void
    * Parameter   : NA
    */
    @isTest static void createLinkParameters()
    {
        User oUser = [Select Id,LastName,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals(oUser.LastName,'test User1');
        
        System.runAs(oUser)
        {
            Policy_Member__c objPolMem = [Select Id from Policy_Member__c Limit 1];
            Account acc = [Select Id from Account where name ='test'];
            
            Link__c lnk = [Select Id From Link__c Where Link_Action__c = 'Query String' Limit 1];
            System.assertNotEquals(lnk.Id, NULL);
            
            Link_Parameter__c oLnkParams = [SELECT ID,variable_source__c,variable_type__c from link_parameter__c where variable_type__c='Database Field' and variable_source__c='Id' limit 1];
            oLnkParams.Variable_Source__c='Policy__r.Name';
            update oLnkParams;
            System.assertEquals(oLnkParams.Variable_Source__c, 'Policy__r.Name');
            Map<Id, Link_Parameter__c> mapLnkParam = new Map<Id, Link_Parameter__c>([select Encode__c,IsRequired__c,Link__c,IsConditional__c,Variable_Name__c,Variable_Source__c,Variable_Type__c,Key_Name__c,
                                                                (select Conditional_Check__c,Encode__c, Conditional_Type__c,Conditional_Value__c, Variable_Type__c 
                                                                from Link_Conditional_Parameters__r order by Conditional_Type__c ASC) 
                                                                from Link_Parameter__c where Link__c =: lnk.id]);
            System.assertEquals(mapLnkParam.size(), 9);
            
            Map<String, String> mapLnkParams = LinkParameterLogic_H_HUM.getQueryParameters(mapLnkParam.values(), objPolMem.Id, 'Policy_Member__c', false);
            System.assertEquals(mapLnkParams.size(), 6);
            
            Link_parameter__c lnkParam = [Select Id From Link_Parameter__c Where Variable_Source__c = '123' AND Variable_Type__c = 'VisualForce Page' Limit 1];
            
            Map<String, String> mapJSON = new Map<String, String>{lnkParam.Id + '.123.' + lnk.id => ''};
            
            Link_parameter__c olnkParam = [Select Id,Link__c, Variable_Name__c ,Variable_Source__c ,Variable_Type__c  From Link_Parameter__c Where Variable_Source__c = 'sxs2222' AND Variable_Type__c = 'Cache' Limit 1];
            List<User> lstUser = new List<User>();
            lstUser.add(oUser);
            try
            {
                LinkParameterLogic_H_HUM.processCacheParameterType(olnkParam,lstUser); 
            }
            catch (HUMCustomException e) {
                System.assert(e.getMessage().contains('The aka name session has expired, please logout of salesforce and log back in again to continue'),e.getMessage());
            }
            mapLnkParams = LinkParameterLogic_H_HUM.includeLinkParameterFormVF(mapLnkParams, mapJSON, objPolMem.Id);
            System.assertNotEquals(mapLnkParams.size(), 6);
        }
    }
}