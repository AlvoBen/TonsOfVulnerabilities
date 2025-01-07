/*******************************************************************************************************************************
Apex Class Name : METMilestoneTAS_DTO_T_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Test Class contains method for covering METMilestoneTAS_DTO_HUM class

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                       06/02/2018                 Original Version.
********************************************************************************************************************************/
@isTest
private class METMilestoneTAS_DTO_T_HUM {

    static testMethod void wrapperTest() {
        METMilestoneTAS_DTO_HUM m1 = new METMilestoneTAS_DTO_HUM('Customer Inquiry','Cancellation of Enrollment','Closing Case','All Updates Completed','Humana Representative','9999',FALSE,FALSE);
        System.assertNotEquals(null, m1);
        System.assertEquals(m1.sCaseType, 'Customer Inquiry');
        System.assertEquals(m1.sCaseSubtype, 'Cancellation of Enrollment');
        System.assertEquals(m1.sTaskName, 'Closing Case');
        System.assertEquals(m1.sActionName, 'All Updates Completed');
        System.assertEquals(m1.sSourceName, 'Humana Representative');
        System.assertEquals(m1.sMilestone, '9999');
        System.assertEquals(m1.bLaunchEmme, false);
        System.assertEquals(m1.bIsMilestone, false);
    }
}