/****************************************************************************
Apex Class Name  : HUMInteractionWrapperHelperTest  
Version          : 1.0 
Created Date     : September 29 2014
Function         : Test class to HUMInteractionWrapperHelper Apex class
Modification Log :
 * Modification ID         Developer Name          Code Review                     Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0                     Ninad Patil                                         09/29/2014                Original Version
 * 1.1                     Vishal Verma              19758                     12/08/2014                Added caller Name for Interaction
 * 1.2                     Shreya Choodamani                                   01/12/2015                Added code to fix test class failure.          
  **********************************************************************************************************************************/
@isTest
private class HUMInteractionWrapperHelperTest 
{
    /*
     * Method Name : testInteractionSearchController
     * Description : This method covers Interaction search page controller.
     * Return type : Void
     * Paramater   : None 
     */
    static testMethod void testInteractionSearchController() 
    {
        Interaction__C oInt = new Interaction__C(Caller_Name__c = 'Test', Interaction_Origin__c = 'Inbound Call');
        insert oInt;
        String sIntName = [Select Name from Interaction__C WHere ID = : oInt.ID].name;
        //Putting Interaction Name in the search URL.
        ApexPages.currentPage().getParameters().put('intname',sIntName);  

        Test.startTest();  
        HUMInteractionSearchController hCustIntLogCont = new HUMInteractionSearchController ();

        //Simulating Search, When user gives interaction Name and hit on search button. 
        hCustIntLogCont.sIntName = sIntName;
        hCustIntLogCont.onSearchIntName();
        
        //Asserting whether standard set controller is populated with INteraction id being searched.
        system.assert(((List<Interaction__C >)hCustIntLogCont.con.getRecords())[0].id == oInt.ID);
            
        hCustIntLogCont.getlstInteractionWrapp();
        
        //Asserting whether list to be populated on screen has interaction id being searched.
        system.assert(hCustIntLogCont.lstInteractionWrapp[0].oInt.Id == oInt.ID);
        
        //Simulation of record selection by user.
        hCustIntLogCont.lstInteractionWrapp[0].isSelected = true;
        hCustIntLogCont.saveResponse();
        
        //Asserting whether sSelectedIntIds variable is set with selected Interaction record ID value.
        system.assert(hCustIntLogCont.sSelectedIntIds == oInt.id);
        Test.stopTest();
    }
    
    /*
     * Method Name : testInteractionSearchNoRecs
     * Description : This method covers Interaction Search Eror condition when no records are resulted by search.
     * Return type : Void
     * Paramater   : None 
     */
    static testMethod void testInteractionSearchNoRecs() 
    {
        String sIntName = '12345';
        //Putting any random  value in the String for search in Interaction Search page.
        ApexPages.currentPage().getParameters().put('intname',sIntName);  

        Test.startTest();  
        HUMInteractionSearchController hCustIntLogCont = new HUMInteractionSearchController ();

        //Simulating Search, When user gives interaction Name and hit on search button. 
        try
        {
            hCustIntLogCont.sIntName = sIntName;
            hCustIntLogCont.onSearchIntName();
            hCustIntLogCont.getlstInteractionWrapp();
        }
        catch(Exception e)
        {
            //Asserting whether standard set controller is populated with INteraction id being searched.
            system.assert(e.getMessage().contains(system.Label.HUMNoInteractionRecFound));
        } 
        Test.stopTest();
    }

    /*
     * Method Name : testInteractionSearchInNonDeveloperMode
     * Description : This method runs in Non Developer profile user mode.
     * Return type : Void
     * Paramater   : None 
     */
    static testMethod void testInteractionSearchInNonDeveloperMode() 
    {
        User testUser = HUMTestDataHelperTest.createTestUser(
                'Customer_Case_specialist_User', 
                'Customer Care Specialist');
        insert testUser;
        system.runAs(testUser)
        {
            testInteractionSearchController();
        }
    }
}