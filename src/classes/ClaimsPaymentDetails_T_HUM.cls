/**************************************************************************** 
Apex Class Name  : ClaimsPaymentDetails_T_HUM  
Version          : 1.0  
Created Date     : May 3, 2016
Function         : Test class for ClaimsPaymentDetails_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat          26844               05/4/2016                  Original Version
* Pallavi Ravishankar                        06/02/2016                 Added Logic forsPaymentUrl
***************************************************************************************************/
@isTest
private Class ClaimsPaymentDetails_T_HUM  
{
    /*
     * Method Name : prepareTestData
     * Description : Create common test Data
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
            
            ClaimsPaymentDetails_DTO_HUM oClaimPay = new ClaimsPaymentDetails_DTO_HUM();
            
            oClaimPay.sPayeeName = 'DB123';
            System.assertEquals(oClaimPay.sPayeeName, 'DB123');
            oClaimPay.sLastProcessDate = '09/01/2015';
            System.assertEquals(oClaimPay.sLastProcessDate, '09/01/2015');
            oClaimPay.sCheckNumber = 'DB123123';
            System.assertEquals(oClaimPay.sCheckNumber, 'DB123123');
            oClaimPay.sCheckDate = '09/01/2015';
            System.assertEquals(oClaimPay.sCheckDate, '09/01/2015');
            oClaimPay.sPaymentType = 'DB123';
            System.assertEquals(oClaimPay.sPaymentType, 'DB123');
            oClaimPay.sPayeeAddress = 'DB123';
            System.assertEquals(oClaimPay.sPayeeAddress, 'DB123');
            oClaimPay.sPaymentAmount = '123';
            System.assertEquals(oClaimPay.sPaymentAmount, '123');
            oClaimPay.sPaymentTypeID = 'DB123';
            System.assertEquals(oClaimPay.sPaymentTypeID, 'DB123');
            oClaimPay.sPayeeID = 'DB123';
            System.assertEquals(oClaimPay.sPayeeID, 'DB123');
            oClaimPay.sPaymentIssueDate = '09/01/2015';
            System.assertEquals(oClaimPay.sPaymentIssueDate, '09/01/2015');
            oClaimPay.sPaymentStatusDate = '09/01/2015';
            System.assertEquals(oClaimPay.sPaymentStatusDate, '09/01/2015');
            oClaimPay.sRemit = 'DB123';
            System.assertEquals(oClaimPay.sRemit, 'DB123');
            oClaimPay.sPaymentStatus = 'Pended';
            System.assertEquals(oClaimPay.sPaymentStatus, 'Pended');
            oClaimPay.sPaymentUrl = 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=http://test-associates.humana.com/RequiredTraining/default.asp?app_id=13700';
            System.assertEquals(oClaimPay.sPaymentUrl, 'http://test-hss.humana.com/ITSecurity/HSSOneClick/LaunchHSS.aspx?TARGET=http://test-associates.humana.com/RequiredTraining/default.asp?app_id=13700');
            
            ClaimsPaymentDetails_DTO_HUM.ClaimDetail oClaimDetail = new ClaimsPaymentDetails_DTO_HUM.ClaimDetail();
            oClaimDetail.ClaimNumber = '123456';
            System.assertEquals(oClaimDetail.ClaimNumber, '123456');
            
      
            
            Test.StopTest();
        }
        
    }    
}