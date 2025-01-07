/**************************************************************************** 
Apex Class Name  : RTISummaryWrapper_T_HUM
Version          : 1.0  
Created Date     : 18th January 2017
Function         : Test class for RTISummary_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi ravishankar                         01/18/2017                  Original Version
* Prasanthi Kandula                           04/17/2020                  Increased code coverage    
***************************************************************************************************/
@isTest
private Class RTISummaryWrapper_T_HUM
{
  /*
   * Method Name : prepareTestData
   * Description : Used to create wrapper class data
   * Return type : void
   * Paramater   : NA
   */
   @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
  /*
   * Method Name : runPositiveData
   * Description : Used to run scenerios of test data
   * Return type : void
   * Paramater   : NA
   */ 
   static testMethod void runPositiveData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            RTISummary_DTO_HUM oRTISummary = new RTISummary_DTO_HUM();
           
            oRTISummary.iconPath = '/img/icon/questions32.png';
            System.assertEquals(oRTISummary.iconPath, '/img/icon/questions32.png');
            
            oRTISummary.sInteractionkey = 'e0dbcdea-edd9-4681-9ce4-42792a9aaa8d';
            System.assertEquals(oRTISummary.sInteractionkey, 'e0dbcdea-edd9-4681-9ce4-42792a9aaa8d');
            
            oRTISummary.sInteractionsrckey = '7294440484825385219';
            System.assertEquals(oRTISummary.sInteractionsrckey, '7294440484825385219');
               
            oRTISummary.sSourceCode = 'IVR';
            System.assertEquals(oRTISummary.sSourceCode, 'IVR');
            
            oRTISummary.sChannelCode = 'IV';
            System.assertEquals(oRTISummary.sChannelCode, 'IV');
            
            oRTISummary.sChannelDescription = 'IVR';
            System.assertEquals(oRTISummary.sChannelDescription, 'IVR');
            
            oRTISummary.sSourceDescription = 'Interactive Voice Response';
            System.assertEquals(oRTISummary.sSourceDescription, 'Interactive Voice Response');
            
            oRTISummary.dDateAndTime = datetime.now();
            System.assertEquals(oRTISummary.dDateAndTime, datetime.now());
            
            oRTISummary.title = 'Member';
            System.assertEquals(oRTISummary.title, 'Member');
            
            oRTISummary.subtitle = 'Unknown';
            System.assertEquals(oRTISummary.subtitle, 'Unknown');
            
            oRTISummary.buttonTarget = 'xyz';
            System.assertEquals(oRTISummary.buttonTarget, 'xyz');
              
            oRTISummary.viewLabel = 'View'; 
            System.assertEquals( oRTISummary.viewLabel, 'View'); 
            oRTISummary.sMemberCardId = '123456789'; 
            oRTISummary.sDependentCode = '00'; 
            oRTISummary.sPlatformCode = 'LV'; 
            oRTISummary.sSDRPersonId = '123456'; 
            oRTISummary.sPersonGenKey = '123456'; 
            oRTISummary.sMessageFulfillmentId = '123456'; 
            oRTISummary.bPrintable = true; 
            oRTISummary.campaignName = 'Test'; 
            oRTISummary.statusDescription = 'statusDescription '; 
            oRTISummary.statusCode = 'statusCode'; 
            oRTISummary.PhoneNumber = '1254789632'; 
            oRTISummary.subStatusDesc= 'subStatusDesc'; 
            oRTISummary.trackingID = '123'; 
            Test.StopTest();
        }     
    }
}