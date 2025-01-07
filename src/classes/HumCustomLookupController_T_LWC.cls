/************************************************************************************************************************
Apex class Name    : HumCustomLookupController_T_LWC
Version            : 1.0
Function           : Test Class for HumCustomLookupController_LWC.
Created On         : Mar 16 2022

Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Gowthami Thota               						3/16/2022          Original Version
*	M K Manoj											3/30/2022 		   Modified version	
***************************************************************************************************************************/

@istest
public class HumCustomLookupController_T_LWC {
     
    /*
     * Method Name   :    testSetup
     * Description   :    This method is for setting up data.
     * Return Type   :    void
     * Parameters    :    NA
     */
    @testSetup
    static void testSetup()
    {
        
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User','Customer Care Specialist');
        oCCSUser.IsActive = true;
        insert oCCSUser;
        System.assertEquals(oCCSUser.LastName,'test User');
        
        User oAdminUser = HUMTestDataHelperTest.createTestUser('Admin','system Administrator');
        oAdminUser.IsActive = true;
        insert oAdminUser;
        System.assertEquals(oAdminUser.LastName,'test Admin');
        
        System.runAs(oAdminUser)
        {
            
            Group oGrp = new Group(Type='Queue',Name = 'Cincinati CRU', DeveloperName='Cincinati_CRU');
            insert oGrp ;
             Group oGrp1 = new Group(Type='Regular',Name = 'Cincinati CRUTT1');
            insert oGrp1 ;
            
            System.assertNotEquals(oGrp , NULL);
            
            QueueSObject oQueue = new QueueSObject(SobjectType='Case', QueueId=oGrp.Id);
            insert oQueue ;
             
            System.assertNotEquals(oQueue, NULL);
            
            GroupMember oGrpMem = new GroupMember(GroupId=oGrp.Id, UserOrGroupId=oCCSUser.id);
            insert oGrpMem;
            GroupMember oGrpMem2 = new GroupMember(GroupId=oGrp.Id, UserOrGroupId=oGrp1.id);
            insert oGrpMem2;
             
            System.assertNotEquals(oGrpMem, NULL);
    }
    }
    
    /*
     * Method Name   :    testNonUserObject
     * Description   :    This method is for Non user object.
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testNonUserObject()
    {
     	User oCCSUser = [select id, name,lastname, isActive from user where lastname='test User' and isActive = true limit 1];
        
        test.startTest();
        system.runAs(oCCSUser)
        {
           
            try{
                HumCustomLookupController_LWC.fetchRecords('Account', 'Email','Cincinati_CRU', 'c','10010');
               HumCustomLookupController_LWC.fetchRecords('Account', 'Email','Cincinati_CRU', 'c','');
           
            }
            catch(exception e)
               {
                   HUMExceptionHelper.logErrors(e,'HumCustomLookupController_LWC','fetchRecords');
              }  
               
        }
        test.stopTest();
	}
    
    /*
     * Method Name   :    testUserObject
     * Description   :    This method is for User object.
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testMethod void testUserObject()
    {
     	User oCCSUser = [select id, name,lastname, isActive from user where lastname='test User' and isActive = true limit 1];
        system.runAs(oCCSUser)
        {
           test.startTest();
            try{
                HumCustomLookupController_LWC.fetchRecords('User', 'Name','Cincinati_CRU','chad',oCCSUser.id);
                HumCustomLookupController_LWC.fetchRecords('User', 'Name','Cincinati_CRU','chad', '');
             
            }
            catch(exception e)
               {
                   HUMExceptionHelper.logErrors(e,'HumCustomLookupController_LWC','fetchRecords');
              } 
            test.stopTest();
        }
	}	
    /*
     * Method Name   :    testNonUserObject1
     * Description   :    This method is for Non User object.
     * Return Type   :    void
     * Parameters    :    NA
     */
     static testMethod void testNonUserObject1()
    {
     	User oCCSUser = [select id, name,lastname, isActive from user where lastname='test User' and isActive = true limit 1];
        system.runAs(oCCSUser)
        {
          test.startTest();
          
            try{
                HumCustomLookupController_LWC.RecordsData rc = new HumCustomLookupController_LWC.RecordsData('test','test');
                HumCustomLookupController_LWC.fetchRecords('User11', 'Name','Cincinati_CRU','chad','');
              
            }
            catch(exception e)
               {
                   HUMExceptionHelper.logErrors(e,'HumCustomLookupController_LWC','fetchRecords');
              }  
            test.stopTest();
        }
	}	
}