/**************************************************************************************************************************
Apex class Name    : HUMAVFTemplateService_PluginTest 
Version            : 1.0
Function           : Test class for HUMAVFTemplateService_Plugin
Created On         : May 25 2015
Test Class         : HUMAVFTemplateService_PluginTest 

Modification Log:
*   Developer                   Code Review         Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Asha Deepika Koyyana            23343            5/25/2015             Original Version
*  Asha Deepika Koyyana            25637            08/19/2015            Modified the test class for field length validation
*  Santhi Mandava                                   03/12/2019            Removed "DpaaSWebServiceMockImpl" usage.
*********************************************************************************************************************************/

@isTest
private Class HUMAVFTemplateService_PluginTest {

    /*
    * Method Name   :    testHUMAVFService
    * Description   :    This method is used to setup test data 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    @testSetup static void testHUMAVFService()
    {  
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assert(oUser.id != null);
        
        System.runAs (oUser) 
        {
            HUM_Webservice_Callout__c oVfs = new HUM_Webservice_Callout__c(Name='AVFTemplate', Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/TemplateService_V1.0/SaveAVFTemplate', Timeout__c='12000', Request_Type__c='GET', Content_Type__c='application/xml'); 
            insert oVfs;
            System.assertEquals(oVfs.Name,'AVFTemplate','Object inserted succesfully');
            
            HUMAVFTemplateService oAvfTemp = new HUMAVFTemplateService();
            HUMAVFTemplateService_Plugin oPlugin= new HUMAVFTemplateService_Plugin  ();
             
            Map<String,Object> InputParameter = new Map<String,Object>();
            InputParameter.put('CaseNumber', '10000000262');
            InputParameter.put('EditSequenceNumber','0');
            InputParameter.put('HumanaAssociateName','Jeff');
            InputParameter.put('PersonFirstName','ABC');
            InputParameter.put('PersonLastName', 'XZY' );
            InputParameter.put('EnterprisePersonID','0');
            InputParameter.put('PersonSpeakingWith','Scott');
            InputParameter.put('RelationToMember','Spouse');
            InputParameter.put('PermanentResidentialAddress','110 LA Fon');
            InputParameter.put('PermanentResidentialCityName', 'USA');
            InputParameter.put('PermanentResidentialCountyName', 'Louisville');
            InputParameter.put('PermanentResidentialStateCode', 'KY');
            InputParameter.put('PermanentResidentialZipCode', '12345');
            InputParameter.put('PermanentResidentialPhoneNumber', '(000) 134-5678');
            InputParameter.put('PermanentResidentialStartDate', System.now());
            InputParameter.put('MailingAddress','112 JK');
            InputParameter.put('MailingCityName','Columbus');
            InputParameter.put('MailingStateCode', 'JK');
            InputParameter.put('MailingZipCode', '4567');
            InputParameter.put('TemporaryAddress','Down Street');
            InputParameter.put('TemporaryCityName', 'cincinnati');
            InputParameter.put('TemporaryCountyName', 'US');
            InputParameter.put('TemporaryStateCode', 'CI');
            InputParameter.put('TemporaryZipCode', '1223');
            InputParameter.put('TemporaryOSAStartDate', System.now());
            InputParameter.put('TemporaryOSAEndDate', System.now());                          
            InputParameter.put('MemberIdName', 'H44519392');
            InputParameter.put('MemberIdType',  'MedicaidID');
            InputParameter.put('PolicyContractNumber', 'H0002');
            System.assert(InputParameter.size() > 0 );
            
            Test.startTest();
            
                 Test.setMock(HttpCalloutMock.class, new HUMAVFTemplateMockHttpResponseTest());
                 Process.PluginRequest request = new Process.PluginRequest(InputParameter); 
                 Process.PluginResult result;
                 result = oPlugin.invoke(request);
                 
                 string sErrorcode=  String.ValueOf(result.outputParameters.get('Error'));
                 System.assertequals(sErrorcode, '00000', 'Value Matched'); 
                 
                 string sErrorDescription =  String.ValueOf(result.outputParameters.get('ErrorDescription'));                
                 System.assertequals(sErrorDescription , 'SUCCESSFUL UPDATE TO TMRAVF1', 'Value Matched');                  
                                         
            Test.stopTest(); 
        }
    }

    /*
    * Method Name   :    describeTest
    * Description   :    This method is used to call describemethod in HUMAVFTemplateService_Plugin  
    * Return Type   :    NA
    * Parameters    :    NA
    */
    static testMethod void describeTest()
    {
       User oUser = HUMTestDataHelperTest.createTestUser('User2','Customer Care Specialist');
       insert oUser;
       System.assert(oUser.id != null);
        
        System.runAs (oUser) 
        {
            Test.startTest();
                
                HUMAVFTemplateService_Plugin oAVFPlugin = new HUMAVFTemplateService_Plugin ();
                Process.PluginDescribeResult result =  oAVFPlugin.describe();
                String str = string.valueof(result.Name);
                System.assertEquals(str ,'AVFTemplateplugin','Value matched'); 
                
             Test.stopTest(); 
        }
   }
}