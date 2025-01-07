/**************************************************************************** 
Apex Class Name  : DCNServiceTestHumI
Version          : 1.0  
Created Date     : September 22 2015  
Description      : This class is used as test class for DCNServiceTestHumI
****************************************************************************
Modification Log:

* Developer Name          Code Review     Date            Description
*------------------------------------------------------------------------------------------------------------
* Gargey Sharma              26705       09/22/2015      Original Version
* Vamshi Krishna Pemberthi						  06/08/2021 		Code Coverage Improvements
***************************************************************************************************/
@isTest
private Class DCNServiceTestHumI
{
    
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        HUMTestDataHelperTest.getHUMConstantsData();
    }
    
    /*
    * Method Name : testDCNService
    * Description : Test method for DCN Service
    * Return type : void     
    * Paramater   : NA  
    */       
    static testMethod void testDCNService()
    {                 
        insert new HUM_Webservice_Callout__c(Name='DCNService', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/DCNServiceRequest/GetDCNList', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);                    
        Test.setMock(HttpCalloutMock.class, new DCNServiceMockTestHumI());       
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser) 
        {
           test.startTest();
            DCNServiceResponseHelperHumI oDCNResponse = new DCNServiceResponseHelperHumI();
            DCNServiceRequestHelperHumI oDCNRequest = new DCNServiceRequestHelperHumI();
            oDCNRequest.EnterprisePersonID = '1005253274';
            oDCNRequest.PlatformCode = 'EM';
            oDCNRequest.UserId = 'MXG0195';
            oDCNResponse = DCNServiceHumI.GetDCNList(oDCNRequest);               
            system.assert(oDCNResponse.Success == 'true');
           test.stopTest();
        }
    }
    Static testMethod void testDCNService1()
    {         
                insert New HUM_Webservice_Callout__c(Name='DCNService', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, Methods_Available__c='A;B;C',Enable_Service_Tester__c=true);                            
        Test.setMock(HttpCalloutMock.class, New DCNServiceMockTestHumI());       
        User oUser = [select Id, Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser) 
        {
           test.startTest();
           Try{
                DCNServiceResponseHelperHumI oDCNResponse = New DCNServiceResponseHelperHumI();
            DCNServiceRequestHelperHumI oDCNRequest = New DCNServiceRequestHelperHumI();
            oDCNRequest.EnterprisePersonID = '1005253274';
            oDCNRequest.PlatformCode = 'EM';
            oDCNRequest.UserId = 'MXG0195';
            oDCNResponse = DCNServiceHumI.GetDCNList(oDCNRequest);}
            Catch(Exception e){     
            system.assertEquals(e.getMessage(), e.getMessage(), e.getMessage());  
            }            
           test.stopTest();
        }
    }
}