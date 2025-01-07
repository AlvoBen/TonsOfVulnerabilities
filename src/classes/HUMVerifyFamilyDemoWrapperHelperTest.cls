/***********************************************************************************************************
Apex Class Name  : HUMVerifyFamilyDemoWrapperHelperTest
Version          : 1.0
Created Date     : Sep 8 2014
Function         : Test class for HUMVerifyFamilyDemoWrapperHelper 
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana           17674                09/8/2014                   Original Version 
************************************************************************************************************/
@isTest
private class HUMVerifyFamilyDemoWrapperHelperTest 
{
   /*
    * Method Name   :    testVerifyDemographics
    * Description   :    This method is used to call HUMVerifyFamilyDemoWrapperHelper class
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void testVerifyDemographics()
    {
          Account oAccount = new Account();
          oAccount.Name ='Test Account';
          oAccount.Enterprise_ID__c = '5328004527022';
          insert oAccount;
          System.assertNotEquals(oAccount, NULL, 'Account is not inserted');
          
          String sRelation = 'Spouse';
            
          HUMVerifyFamilyDemoWrapperHelper oVerifyDemo = new HUMVerifyFamilyDemoWrapperHelper(oAccount ,sRelation );
          System.assertNotEquals(oVerifyDemo , NULL, 'Object is not instantiated');
          System.assertEquals(oVerifyDemo.sRelation, 'Spouse',true);
    }
 }