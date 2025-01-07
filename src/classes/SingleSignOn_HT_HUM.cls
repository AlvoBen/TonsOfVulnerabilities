/*******************************************************************************************************************************
Apex Class Name : SingleSignOn_HT_HUM
Version         : 1.0
Created On      : 10/16/2015
Function        : Test class for SingleSignOn_H_HUM.
Modification Log: 
* Developer Name           Code Review              Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar               27372                  10/16/2015                  Original version
* Ranjeeth N                 27480                  11/2/2015                   Test class updated of failure accessing custom 
                                                                                setting  defect:205159.0001
* Santhi Mandava                                    03/12/2019                  Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*******************************************************************************************************************************/

@isTest
private class SingleSignOn_HT_HUM
{
    /*
    * Method Name   :    setupTestData
    * Description   :    This method is used to create test data which can be used later in test class
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testSetup
    static void setupTestData()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        System.assertEquals(oCCSUser.LastName, 'test User1', 'Value matched');
        Endpoint_Url_HUM__c ObjEndpoint = new Endpoint_Url_HUM__c();
        ObjEndpoint.name ='SSO_LOGINURL';
        ObjEndpoint.Remote_Url__c='https://wwww.example.com';
        insert ObjEndpoint;
        System.assertEquals(ObjEndpoint.name, 'SSO_LOGINURL');
        
    }
    
    /*
    * Method Name : prepareTestURLWithoutParam
    * Description : Test method create test URL
    * Return Type : void
    * Parameters  : NA
    */
    static testMethod void prepareTestURLWithoutParam()
    {
        User oCCSUser = [Select Id, Name From User Where LastName = 'test User1' Limit 1];
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            String finalURL = SingleSignOn_H_HUM.GetSingleSignOnUrl('https://wwww.example.com');
            System.assert(finalURL.contains(SingleSignOn_H_HUM.HSS_ONECLICK_URL + '?' + SingleSignOn_H_HUM.HSS_ONECLICK_TARGET + '='));
            
            Test.stopTest();
        }
    }
    
    
    /*
    * Method Name : prepareTestURLWithOneParam
    * Description : Test method create test URL
    * Return Type : void
    * Parameters  : NA
    */
    static testMethod void prepareTestURLWithOneParam()
    {
        User oCCSUser = [Select Id, Name From User Where LastName = 'test User1' Limit 1];
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            
            Map<String, String> mapParameters = new Map<String, String>{'hiddenTarget' => 'hiddenTarget'};
            
            String finalURL = SingleSignOn_H_HUM.GetSingleSignOnUrl(SingleSignOn_H_HUM.HSS_ONECLICK_URL, mapParameters);
            
            System.assert(finalURL.contains(SingleSignOn_H_HUM.HSS_ONECLICK_URL + '?' + SingleSignOn_H_HUM.HSS_ONECLICK_TARGET + '='));
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : prepareTestURLWithMultipleParam
    * Description : Test method create test URL
    * Return Type : void
    * Parameters  : NA
    */
    static testMethod void prepareTestURLWithMultipleParam()
    {
        User oCCSUser = [Select Id, Name From User Where LastName = 'test User1' Limit 1];
        
        System.runAs(oCCSUser)
        {
            Test.startTest();
            
            Map<String, String> mapParameters = new Map<String, String>
            {
                'hiddenTarget' => 'hiddenTarget',
                'target' => 'www.test.com'
            };
            
            
            String finalURL = SingleSignOn_H_HUM.GetSingleSignOnUrl('https://wwww.example.com', mapParameters);
           
            System.assert(finalURL.contains(SingleSignOn_H_HUM.HSS_ONECLICK_URL + '?' + SingleSignOn_H_HUM.HSS_ONECLICK_HIDDENTARGET + '='), 'Value matched');
            
            Test.stopTest();
        }
    }
}