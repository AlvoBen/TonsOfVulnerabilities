/**************************************************************************** 
Apex Class Name  : CreateInteractionRequestWrapper_T_HUM
Version          : 1.0  
Created Date     : 04/24/2019
Function         : test class for CreateInteractionRequest_DTO_HUM
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar                         04/24/2019                Original Version
***************************************************************************************************/
@isTest
private Class CreateInteractionRequestWrapper_T_HUM
{
   /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
    }
    
    /*
     * Method Name : testInteractionRequestWrapper
     * Description : Test method for CreateInteractionRequest_DTO_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testInteractionRequestWrapper()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
          CreateInteractionRequest_DTO_HUM  owrap = new CreateInteractionRequest_DTO_HUM ();
          CreateInteractionRequest_DTO_HUM.CreateInteractionRequest intreq = new CreateInteractionRequest_DTO_HUM.CreateInteractionRequest ();
          CreateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintabout = new CreateInteractionRequest_DTO_HUM.CaseInteractingAbout();
          CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
          CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new CreateInteractionRequest_DTO_HUM.CreateCaseDetails();
          list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
          list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<CreateInteractionRequest_DTO_HUM.CaseInteractingAbout>();                       
          intabout.AddressLine1='street 1';
          intabout.City = 'Test City';
          intabout.DOB = '01/01/1990';
          intabout.EmailID='test2';
          intabout.FirstName = 'test2';
          intabout.HomePhone = '11111';
          intabout.ID = '360306641';
          intabout.IDType='Member-Id-Base';
          intabout.MemberIdDependentCode='00';
          intabout.LastName = 'method2';
          intabout.State = 'teststate';
          intabout.EntityType='Member';
          intabout.ZipCode='12345'; 
          caseintabout.CaseIdentifier = '123';           
          caseintabout.InteractingAbout = intabout; 
          intlist.add(caseintabout); 
          owrap.CreateInteractionRequest  = intreq; 
          system.assert(owrap!=null);
          
          Test.StopTest();
        }
    }  
}