/*******************************************************************************************************************************
Apex Class Name : CaseTransferAutoUser_LT_HUM
Version         : 1.0
Created On      : MAY 22 2014
Function        : It is a test class for CaseTransferAutoUser_LC_HUM.
Modification Log: 
* Developer Name            Code Review                  Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
*******************************************************************************************************************************/
@isTest
private class CaseTransferAutoUser_LT_HUM
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
           /* Group oGrp = new Group(Type='Queue',Name = 'Cincinati CRU1', DeveloperName='Cincinati_CRU1');
            insert oGrp ;*/
            Group oGrp = HUMTestDataHelperTest.insertGroup();
            
            System.assertNotEquals(oGrp , NULL);
            QueueSObject oQueue = HUMTestDataHelperTest.insertQueue(oGrp.id);
            /*QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGrp.Id);
            insert oQueue ;*/
            
            System.assertNotEquals(oQueue, NULL);
            GroupMember oGrpMem =HUMTestDataHelperTest.insertGroupMember(oGrp.id,UserInfo.getUserId());
            /*GroupMember oGrpMem = new GroupMember(GroupId=oGrp.Id, UserOrGroupId=UserInfo.getUserId());
            insert oGrpMem;*/
            
            System.assertNotEquals(oGrpMem, NULL);
            
            CaseTransferAutoUser_LC_HUM.searchObject('User', 'Cincinati_CRU1', 'Manish');
            
            System.assertNotEquals(CaseTransferAutoUser_LC_HUM.setUserId.size(), 0);
            
            CaseTransferAutoUser_LC_HUM.setUserId = new Set<Id>();
            
            CaseTransferAutoUser_LC_HUM.searchObject('User', '', 'Manish');
            
            System.assertEquals(CaseTransferAutoUser_LC_HUM.setUserId.size(), 0);
        }
    }
    static testMethod void searchObjectElse()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            Group oGrp = HUMTestDataHelperTest.insertGroup();
            System.assertNotEquals(oGrp , NULL);
           
            QueueSObject oQueue = HUMTestDataHelperTest.insertQueue(oGrp.id);
            System.assertNotEquals(oQueue, NULL);
           
            GroupMember oGrpMem =HUMTestDataHelperTest.insertGroupMember(oGrp.id,UserInfo.getUserId()); 
            System.assertNotEquals(oGrpMem, NULL);
            
            CaseTransferAutoUser_LC_HUM.searchObject('Case', 'Cincinati_CRU1', '');
            
            System.assertNotEquals(CaseTransferAutoUser_LC_HUM.setUserId.size(), 0);
            
            CaseTransferAutoUser_LC_HUM.setUserId = new Set<Id>();
            
            CaseTransferAutoUser_LC_HUM.searchObject('User', '', 'Manish');
            
            System.assertEquals(CaseTransferAutoUser_LC_HUM.setUserId.size(), 0);
        }
    }
}