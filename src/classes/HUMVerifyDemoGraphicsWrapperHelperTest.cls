/***********************************************************************************************************
Apex Class Name  : HUMVerifyDemoGraphicsWrapperHelperTest
Version          : 1.0
Created Date     : Sep 8 2014
Function         : Test class for HUMVerifyDemoGraphicsWrapperHelper
************************************************************************************************************

Modification Log:

* Developer Name                Code Review           Date                       Description
*-----------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana           17674                09/8/2014                   Original Version 
************************************************************************************************************/
@isTest
private class HUMVerifyDemoGraphicsWrapperHelperTest
{
    /*
    * Method Name   :    testVerifyDemographics
    * Description   :    This method is used to call HUMVerifyDemoGraphicsWrapperHelper class
    * Return Type   :    NA
    * Parameters    :    NA
    */
    static testMethod void testVerifyDemographics()
    {
        HUMVerifyDemoGraphicsWrapperHelper oVerifyDemo = new HUMVerifyDemoGraphicsWrapperHelper();
        System.assertNotEquals(oVerifyDemo , NULL, 'Object is not instantiated');
     }
 }