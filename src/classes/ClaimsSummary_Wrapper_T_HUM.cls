/**************************************************************************** 
Apex Class Name  : ClaimsSummary_Wrapper_T_HUM
Version          : 1.0  
Created Date     : March 03, 2016
Function         : Test class for ClaimsSummary_Wrapper_DTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                               03/03/2016                  Original Version
***************************************************************************************************/
@isTest
private Class ClaimsSummary_Wrapper_T_HUM
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
            
            ClaimsSummary_Wrapper_DTO_HUM oClaimsSummary = new ClaimsSummary_Wrapper_DTO_HUM();
            
            oClaimsSummary.sAdjustInd = 'N';
            System.assertEquals(oClaimsSummary.sAdjustInd, 'N');
            
            oClaimsSummary.sClaimNbr='233542754';
            System.assertEquals(oClaimsSummary.sClaimNbr, '233542754');
            
            oClaimsSummary.sClaimTypeCd ='Ambulatory'; 
            System.assertEquals(oClaimsSummary.sClaimTypeCd, 'Ambulatory'); 
                      
            oClaimsSummary.sClaimType = 'Dental';
            System.assertEquals(oClaimsSummary.sClaimType, 'Dental');
            
            oClaimsSummary.sClmGenKey = '389672000459218880';
            System.assertEquals(oClaimsSummary.sClmGenKey, '389672000459218880');
            
            oClaimsSummary.sClmReceiptDate = '01/03/2016';
            System.assertEquals(oClaimsSummary.sClmReceiptDate, '01/03/2016');
                
            oClaimsSummary.sChargeAmt ='0.01';
            System.assertEquals(oClaimsSummary.sChargeAmt, '0.01');
            
            oClaimsSummary.sDispGrpID = '22875';
            System.assertEquals(oClaimsSummary.sDispGrpID, '22875');
            
            oClaimsSummary.sEncounterIndicator ='N';
            System.assertEquals(oClaimsSummary.sEncounterIndicator, 'N');
            
            oClaimsSummary.sLastProcessDate ='03/03/2016';
            System.assertEquals(oClaimsSummary.sLastProcessDate, '03/03/2016');
            
            oClaimsSummary.sMbrRespAmt ='0.00';
            System.assertEquals(oClaimsSummary.sMbrRespAmt, '0.00');
            
            oClaimsSummary.sPaidAmt ='0.00';
            System.assertEquals(oClaimsSummary.sPaidAmt, '0.00');
            
            oClaimsSummary.sPlatformCd = 'LV';
            System.assertEquals(oClaimsSummary.sPlatformCd, 'LV');
            
            oClaimsSummary.sPreDeterminationIndicator ='N';
            System.assertEquals(oClaimsSummary.sPreDeterminationIndicator, 'N');
            
            oClaimsSummary.sPreDeterminationNumber = '233542754';
            System.assertEquals(oClaimsSummary.sPreDeterminationNumber, '233542754');
                
            oClaimsSummary.sProviderID ='382589966';
            System.assertEquals(oClaimsSummary.sProviderID, '382589966');
            
            oClaimsSummary.sProviderName ='TREE LEVASSEUR';
            System.assertEquals(oClaimsSummary.sProviderName, 'TREE LEVASSEUR');
            
            oClaimsSummary.sSRCNPIID ='1194716878';
            System.assertEquals(oClaimsSummary.sSRCNPIID, '1194716878');
            
            oClaimsSummary.sServiceEndDate ='20160303';
            System.assertEquals(oClaimsSummary.sServiceEndDate, '20160303');
            
            oClaimsSummary.sServiceStartDate ='20160303';
            System.assertEquals(oClaimsSummary.sServiceStartDate, '20160303');

            oClaimsSummary.sStatusDesc ='COMPLETED';
            System.assertEquals(oClaimsSummary.sStatusDesc, 'COMPLETED');
            
            oClaimsSummary.sDeductAmt ='0.00';
            System.assertEquals(oClaimsSummary.sDeductAmt, '0.00');
            
            oClaimsSummary.sCopayAmt ='0.00';
            System.assertEquals(oClaimsSummary.sCopayAmt, '0.00');
            
            oClaimsSummary.sCoInsAmt ='0.00';
            System.assertEquals(oClaimsSummary.sCoInsAmt, '0.00');

            Test.StopTest();
        }
        
    }    
}