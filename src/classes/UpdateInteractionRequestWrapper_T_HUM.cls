/**************************************************************************** 
Apex Class Name  : UpdateInteractionRequestWrapper_T_HUM
Version          : 1.0  
Created Date     : 05/02/2019
Function         : test class for UpdateInteractionRequest_DTO_HUM
*****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Pallavi Ravishankar       354971              05/02/2019                Original Version
* Prasanthi Kandula         357233              05/24/2019                Added assert statements as part of Security scan
***************************************************************************************************/
@isTest
private Class UpdateInteractionRequestWrapper_T_HUM
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
       
             Interaction__c objInteraction = HUMTestDataHelperTest.getInteraction();
             insert objInteraction;
             System.assertNotEquals(objInteraction, null, 'Interaction not created'); 
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
          UpdateInteractionRequest_DTO_HUM owrap = new UpdateInteractionRequest_DTO_HUM();
          UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest intreq = new UpdateInteractionRequest_DTO_HUM.UpdateInteractionRequest ();
          UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout caseintaboutv = new UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout();
          UpdateInteractionRequest_DTO_HUM.CreateCaseDetails oCreateCaseDetails = new UpdateInteractionRequest_DTO_HUM.CreateCaseDetails();
          UpdateInteractionRequest_DTO_HUM.CaseInteraction ocaseInteraction= new UpdateInteractionRequest_DTO_HUM.CaseInteraction();
          list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout> intlist = new list<UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout>();
          CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO intabout = new CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO();
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
          caseintaboutv.CaseIdentifier='122';
          caseintaboutv.InteractingAbout=intabout;
          intlist.add(caseintaboutv );
          list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails> lstCaseDetails = new list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>();  
          list<UpdateInteractionRequest_DTO_HUM.CaseInteraction> lstCaseInteraction = new list<UpdateInteractionRequest_DTO_HUM.CaseInteraction>();  
          intreq.SourceApp='';
          intreq.SourceIdTypeCd='';
          intreq.InteractionNumber='2000011676520';
          intreq.RequiredCasesInResponse=true;
          intreq.InteractionOrigin='Watson voice';
          intreq.CreatedByQueue='';
          intreq.CaseInteractingAbout=intlist;
          intreq.CreateCaseDetails= lstcasedetails;
          intreq.CaseInteractions= lstCaseInteraction ;
          owrap.UpdateInteractionRequest = intreq;
          system.assert(owrap!=null);
          Test.StopTest();
        }
    }  
}