/*******************************************************************************************************************************
Apex Class Name : ExpressionEvaluate_HT_HUM
Version         : 1.0
Created On      : 10/23/2015
Function        : Test class for ExpressionEvaluate_H_HUM

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty       27372                       10/26/2015                 Original version
* Santhi Mandava            27518                       11/03/2015                 Corrected spelling mistake
*******************************************************************************************************************************/

@isTest
private class ExpressionEvaluate_HT_HUM
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
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        System.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
        }
    }
    
   /*
    * Method Name : testExpression
    * Description : This method is used to test the main class functionality
    * Return type : void
    * Parameter   : NA
    */ 
    static testMethod void testExpression()
    {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        
        System.runAs(oUser)
        {
            Map<String, Boolean> mapResult;
            Link__c objLink = [Select Id,Expression__c,Link_Action__c  from Link__c where Link_Label__c ='SPIDAR'];
            System.AssertEquals('Query String',objLink.Link_Action__c );
            objLink.Expression__c = '1 OR 2 OR 3';
            
            Map<String, Boolean> mapLnkCrt = new Map<String, Boolean>();
            mapLnkCrt.put('1',true);
            mapLnkCrt.put('2',true);
            mapLnkCrt.put('3',true);
            
            ExpressionEvaluate_H_HUM oExpression = new ExpressionEvaluate_H_HUM();
            mapResult = oExpression.evaluateExpression(objLink ,mapLnkCrt);
            System.AssertEquals(true,mapResult.get(objLink.Id));
            
            objLink.Expression__c = '1 OR (2 AND 3)';
            mapResult = oExpression.evaluateExpression(objLink ,mapLnkCrt);
            System.AssertEquals(mapResult.get(objLink.Id), true);
            
            mapLnkCrt.put('4',false);
            objLink.Expression__c = '1 OR 2 OR (3 AND 4)';
            mapResult = oExpression.evaluateExpression(objLink ,mapLnkCrt);
            System.AssertEquals(mapResult.get(objLink.Id),true);
            
            mapLnkCrt.put('4',false);
            mapLnkCrt.put('5',true);
            objLink.Expression__c = '1 OR 2 OR (3 AND 4 AND 5)';
            mapResult = oExpression.evaluateExpression(objLink ,mapLnkCrt);
            System.AssertEquals(mapResult.get(objLink.Id),true);
            
            objLink.Expression__c = '(1 OR 2 OR 3) AND 4 AND 5';
            mapResult = oExpression.evaluateExpression(objLink ,mapLnkCrt);
            System.AssertEquals(false,mapResult.get(objLink.Id));
        }
     }
 }