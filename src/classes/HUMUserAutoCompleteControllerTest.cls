/*******************************************************************************************************************************
Apex Class Name : HUMUserAutoCompleteControllerTest
Version         : 1.0
Created On      : MAY 22 2014
Function        : It is a test class for HUMMemberSearchController.
Modification Log: 
* Developer Name            Code Review                  Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              17122                        22/05/2014                  Original version
* Manish Kumar              19728                        12/05/2014                  Fixed production error             
* SuryaKumari Medicherla    21223                        27/02/2015                  Code modified for Production issue 146
*******************************************************************************************************************************/
@isTest
private class HUMUserAutoCompleteControllerTest
{
    /*
     * Method Name   :    overAllTerstMethod
     * Description   :    This method is for overall test coverage.
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void searchObject()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            Group oGrp = new Group(Type='Queue',Name = 'Cincinati CRU1', DeveloperName='Cincinati_CRU1');
            insert oGrp ;
            
            System.assertNotEquals(oGrp , NULL);
            
            QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGrp.Id);
            insert oQueue ;
            
            System.assertNotEquals(oQueue, NULL);
            
            GroupMember oGrpMem = new GroupMember(GroupId=oGrp.Id, UserOrGroupId=UserInfo.getUserId());
            insert oGrpMem;
            
            System.assertNotEquals(oGrpMem, NULL);
            
            HUMUserAutoCompleteController.searchObject('User', 'Cincinati_CRU1', 'Manish', '1', 'true');
            
            System.assertNotEquals(HUMUserAutoCompleteController.setUserId.size(), 0);
            
            HUMUserAutoCompleteController.setUserId = new Set<Id>();
            
            HUMUserAutoCompleteController.searchObject('User', '', 'Manish', '1', 'true');
            
            System.assertEquals(HUMUserAutoCompleteController.setUserId.size(), 0);
        }
    }
}