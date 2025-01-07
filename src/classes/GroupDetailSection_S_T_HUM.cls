/*****************************************************************************************
Apex class Name    : GroupDetailSection_S_T_HUM
Version            : 1.0
Function           : Test class for GroupDetailSection_S_HUM
Created Date       : 22 Oct 2015

Modification Log:
*   Developer                    Code Review         Date                   Description
* -------------------------------------------------------------------------------------------------------------------------               
*  SuryaKumari Medicherla        27320              10/22/2015          Original Version
*  SuryaKumari Medicherla                           11/02/2015          Modified HUM_Webservice_Callout__c custom settings value
*************************************************************************************************************************/
@isTest
private  class GroupDetailSection_S_T_HUM
{
 /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : None
    */
    
     @testSetup static void setup() 
     {           
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser.id,null,'​Value not matched');
        
        System.runAS(oUser)
        {
            HUM_Webservice_Callout__c oHUMGBESservice = new HUM_Webservice_Callout__c();
            oHUMGBESservice.name='GBEService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/EnrollmentServices/Group_V1.0';
            oHUMGBESservice.Timeout__c='12000';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            
            RecordType PersonAccountRecordType = [Select Id,Name from RecordType where Name = 'Group' and sObjectType='Account'];
            system.assert(PersonAccountRecordType.Name == 'Group');
            
            Account oacc =  HUMTestDataHelperTest.getAccount();
            oacc.Group_Number__c = '987654321';
            oacc.recordtypeid = PersonAccountRecordType.id;
            oacc.name= 'duck';
            oacc.NPI_ID__c='0123456789';
            oacc.Individual_First_Name__c='testname';
            oacc.Individual_Last_Name__c='firstname';
            oacc.Source_Customer_Key__c ='608356';
            oacc.Source_Platform_Code__c='EM';
            insert oacc;
            system.assert(oacc.id!=null);
            system.assert(oacc.Source_Customer_Key__c  == '608356');
            system.assert(oacc.Source_Platform_Code__c== 'EM');
        }
    }
    
     /*
    * Method Name : testForGBESRequest
    * Description : This method tests search and creation of account and custom settings
    * Return type : Void
    * Paramater   : None
    */
    static testMethod void testForGBESRequest()
    {
        User oUser = [Select id from User WHERE Lastname='test User1' LIMIT 1];
        System.runAS(oUser)
        {
            Account oAccount = [Select id,Source_Platform_Code__c,Source_Customer_Key__c from Account limit 1];
            Test.setMock(WebServiceMock.class, new HUMGBEServiceMockTest());
            GroupDetailSection_S_HUM objService = new GroupDetailSection_S_HUM();
            Continuation con = new Continuation(10);
            objService.requestToServer(oAccount,con,True,'1','25');
            system.assertEquals(objService.asynchGBE.timeout_x ,12000);
        }
    }
}