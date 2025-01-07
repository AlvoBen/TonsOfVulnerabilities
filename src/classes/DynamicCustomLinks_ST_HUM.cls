/*******************************************************************************************************************************
Apex Class Name : DynamicCustomLinks_ST_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for DynamicCustomLinks_S_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava             27372                      10/23/2015                 Original version
* Santhi Mandava             27518                      11/03/2015                 Fixed test class failure issue related to single sign-on custom settings.
* Manish Kumar                                          01/27/2016                 Modified test class to accomodate changes related to REQ - 237417, 237421
* Anupama Tavva                                         01/24/2017                 Modified test class to achieve code coverage
* Santhi Mandava                                        03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Lakshmi Madduri                                       03/19/2021                 US-1916924 SonarQube Fixes
*******************************************************************************************************************************/

@isTest
private class DynamicCustomLinks_ST_HUM
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
        insert oUser;
        System.assert(oUser.id != null);
        
        system.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
            
            Link__c lnk = [Select Id,Link_Action__c From Link__c Where Link_Action__c = 'Query String' Limit 1];
            System.assertEquals(lnk.Link_Action__c,'Query String');
            
            List<Link_Parameter__c> lstparam = new List<Link_Parameter__c>();
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Variable_Name__c = 'Id', Variable_Source__c = '123', Variable_Type__c = 'Static'));
            lstparam.add(new Link_Parameter__c(Link__c = lnk.Id, Variable_Name__c = 'AKA_Name', Variable_Source__c = 'AKAName', Variable_Type__c = 'Cache'));
            
            insert lstparam;
            system.assertEquals(2,lstparam.size());
            
            Endpoint_Url_HUM__c ObjEndpoint = new Endpoint_Url_HUM__c();
            ObjEndpoint.name ='SSO_LOGINURL';
            ObjEndpoint.Remote_Url__c='https://wwww.example.com';
            insert ObjEndpoint;
            System.assertEquals(ObjEndpoint.name, 'SSO_LOGINURL');
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
        User oUser = [Select Id,LastName from User where LastName = 'test User1'];
        System.assertEquals(oUser.LastName,'test User1');
        system.runAs(oUser)
        {
            Policy_Member__c objPolMem = [Select Id from Policy_Member__c Limit 1];
            Account acc = [Select Id from Account where name ='test'];
            DynamicCustomLinks_S_HUM objDCS = new DynamicCustomLinks_S_HUM(); 
            String sObjName = objDCS.getSObjName(objPolMem.Id);
            System.assertEquals('Policy_Member__c',sObjName );
            
            objDCS.getLinkDetails();
            System.assertEquals(7,objDCS.maplnktorf.size());
            
            List<DynamicCustomLinks_H_HUM> lstFinalCustomLinks = objDCS.prepareLinksToDisplay();
            System.assertEquals(2,lstFinalCustomLinks.size());
            
            ApexPages.currentPage().getParameters().put('sRecId',objPolMem.Id);
            
            Link__c objLink = NULL;
            
            for(integer i = 0 ; i < objDCS.maplnktorf.size() ; i++)
            {
                ApexPages.currentPage().getParameters().put('slinkId',objDCS.mapLinks.values()[i].Id);
                objLink = objDCS.prepareLinkUrl(String.valueOf(objDCS.mapLinks.values()[i].Id), String.valueOf(objPolMem.Id),objDCS.sObjName,JSON.serialize(objDCS.mapLinks), false);
                if(NULL != objLink)    System.assertNotEquals(objDCS.mapLinks.values()[i], null);
            }
            
            sObjName = objDCS.getSObjName(acc.Id);
            System.assertEquals('Account',sObjName );
            
            objDCS.mapLinks = null;
            objLink = objDCS.prepareLinkUrl(String.valueOf(objLink.Id), String.valueOf(objPolMem.Id),objDCS.sObjName,JSON.serialize(objDCS.mapLinks), false);
            System.assertEquals(Null, objLink );
            
            objDCS.setPageName('DisplayCustomLinks_VF_HUM');
            System.assertEquals(objDCS.sPageName, '');
            
            String sJSON = objDCS.checkVFLink();
            System.assertNotEquals(sJSON, '');
            
            Link__c lnk = [Select Id From Link__c Limit 1];
            
            Map<String, String> mapJSON = new Map<String, String>{lnk.id + '.' + 'Platform_Code' => ''};
            
            list<DynamicCustomLinks_H_HUM> lstLinks = objDCS.includeVFCriteriaLinks(mapJSON);
            System.assertNotEquals(lstLinks ,null);
            
            String sJSONLink = objDCS.checkVFParameter();
            System.assertNotEquals(sJSONLink , '');
                
            Map<String, String> mapJSONVFParameterValues = new Map<String, String>();
            System.assertNotEquals(mapJSONVFParameterValues , null);
               
            Link__c sJSONVFParameterValues = objDCS.includeVFParameterValues(mapJSONVFParameterValues);
            System.assertEquals(sJSONVFParameterValues ,null);
        }
    }
    /*
    * Method Name : testlinkstodisplay
    * Description : This method is used to test the main class functionality
    * Return type : void
    * Parameter   : NA
    */
    @isTest static void testlinkstodisplay()
    {
      User oUser = [Select Id, LastName from User where LastName = 'test User1'];
      System.assertEquals(oUser.LastName,'test User1');
      system.runAs(oUser)
      {
           Link__c lnk = [Select Id From Link__c Limit 1];
           DynamicCustomLinks_S_HUM objDCS = new DynamicCustomLinks_S_HUM(); 
           Map<String, String> mapJSONLink  = new Map<String, String>();
           mapJSONLink.put(lnk.id,'xyz');
           objDCS.includeVFCriteriaLinks(mapJSONLink);
           System.assertNotEquals(mapJSONLink, null);
          
           String sJSONcheckVFParameter = objDCS.checkVFParameter();
           System.assertNotEquals(sJSONcheckVFParameter, '');
               
           String sJSONcheckVFLink = objDCS.checkVFLink();
           System.assertEquals(sJSONcheckVFLink, '');
            
           List<DynamicCustomLinks_H_HUM> sJSONprepareLinks=objDCS.prepareLinksToDisplay();
           System.assertNotEquals(sJSONprepareLinks,null);
       }
   }
}