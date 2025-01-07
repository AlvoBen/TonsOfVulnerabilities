/*****************************************************************************************
Apex class Name    : GroupDetailSection_ST_HUM_V2
Version            : 1.0
Function           : Test class for GroupDetailSection_S_HUM_V2
Created Date       : 12 March 2016

Modification Log:
*   Developer                    Code Review         Date                   Description
* -------------------------------------------------------------------------------------------------------------------------               
*  Praveen Kumar Parimi           23955             03/12/2016              Original Version
*************************************************************************************************************************/
@isTest
private  class GroupDetailSection_ST_HUM_V2 
{
    /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : None
    */    
     @testSetup static void setup() 
      {               
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser.id,null,'​Value not matched');
        
        System.runAS(oUser)
         {
            HUM_Webservice_Callout__c oSvcHeaderGBEService = new HUM_Webservice_Callout__c();
            oSvcHeaderGBEService.name='GroupBusinessEntityService';
            oSvcHeaderGBEService.Active__c=true;
            oSvcHeaderGBEService.Request_Type__c = 'POST';
            oSvcHeaderGBEService.Content_Type__c = 'JSON';
            oSvcHeaderGBEService.Certificate_Name__c='Certificate';
            oSvcHeaderGBEService.End_Point_URL__c='https://int-crmservicews.humana.com/GBEServiceREST/GetGroupInfo';
            oSvcHeaderGBEService.Timeout__c='12000';
            insert oSvcHeaderGBEService ;
            system.assert(oSvcHeaderGBEService.id!=null);
            
            RecordType PersonAccountRecordType = [Select Id,Name from RecordType where Name = 'Group' and sObjectType='Account'];
            system.assert(PersonAccountRecordType.Name == 'Group');
            
         }
      }
    
    /*
    * Method Name : testsetuprequestToGBE
    * Description : This method tests the return of the request
    * Return type : Void
    * Paramater   : None
    */
    static testMethod void testsetuprequestToGBE()
    {
        User oUser = [Select id from User WHERE Lastname='test User1' LIMIT 1];
        HUM_Webservice_Callout__c oservicemapping = [select id,name,Active__c,Content_Type__c,Certificate_Name__c,End_Point_URL__c,Timeout__c,Request_Type__c from HUM_Webservice_Callout__c
        where name = 'GroupBusinessEntityService'];
        String SRequestBody = 'testrequestbody';
        HttpRequest request = new HttpRequest();        
        
        System.runAS(oUser)
        {
           Test.setMock(WebServiceMock.class, new HUMGBEServiceMockTest());           
           GroupDetailSection_S_HUM_V2 oservice = new GroupDetailSection_S_HUM_V2();
           request = oservice.setuprequestToGBE(oservicemapping,SRequestBody);           
           system.assertNotEquals(request,null,'​Value not matched');
        }
    }

}