/*******************************************************************************************************************************
Class Name    : NBARecommendation_T_HUM 
Version         : 1.0
Created On      : 11/03/2020
Function        : Test Class for NBARecommendation_G_HUM Trigger
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                 11/03/2020                original version - 100% Coverage
********************************************************************************************************************************/

@isTest(seeAllData= false)
private class NBARecommendation_T_HUM {
/*
* Method Name : setUpTestData
* Description : This method creates test data for test class
* Return type : None
* Parameter   : None
*/

    @testSetup static void setUpTestData() {
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        List<Recommendation> listOfRecommendation = new List<Recommendation>();
        listOfRecommendation.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =1));
        listOfRecommendation.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', priority__c =2));
        listOfRecommendation.add(new Recommendation(Name = 'Recom3', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =3));
        listOfRecommendation.add(new Recommendation(Name = 'Recom4', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2',  Message__c = 'dynamic1',
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept',  RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member',priority__c =4));
                                                    
        insert listOfRecommendation;
        //Switch Data
        CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =true;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = 'REQ - 2164068 REQ - 2164068 NBA Checkmarx fix';
            crm3.Release_Name__c = '05/28/2021';
            insert crm3;
    }
    
    /*
    * Method Name : testCheckForDuplicateRecommendation
    * Description : This method is used to test the checkForDuplicateRecommendation method from NBARecommendation_H_HUM class.
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testCheckForDuplicateRecommendation() {
        
        List<Recommendation> listOfRecommendationExisting = [Select Id, Name, priority__c From Recommendation];
        Boolean result = false;     
        List<Recommendation> listOfRecommendationNew = new List<Recommendation>();
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom1', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    priority__c =1));
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom2', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    priority__c =2));
        Test.startTest();
        try{
            insert listOfRecommendationNew;   
        }
        catch(DmlException ex){ 
            System.assert(ex.getMessage().contains('same name already Exist'));
            result=  true;
        }
        System.assert(result);
        Test.stopTest();
    }
    
     /*
    * Method Name : testPositiveCheckForDuplicateRecommendation
    * Description : This method is used to test the checkForDuplicateRecommendation method from NBARecommendation_H_HUM class.
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testPositiveCheckForDuplicateRecommendation() {
        
        List<Recommendation> listOfRecommendationExisting = [Select Id, Name, priority__c From Recommendation];
        Boolean result = false;     
        List<Recommendation> listOfRecommendationNew = new List<Recommendation>();
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom7', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', priority__c =1));
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom8', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', priority__c =2));
        Test.startTest();
        try{
            insert listOfRecommendationNew;   
        }
        catch(DmlException ex){ 
            result=  true;
        }
        System.assertEquals(false, result);
        List<Recommendation> listOfRecommendationAll = [Select Id, Name From Recommendation order by Name];
        System.assertEquals(6, listOfRecommendationAll.size());
        listOfRecommendationAll[0].Name = 'Recom9';
        update listOfRecommendationAll;
        List<Recommendation> listOfRecommendations = [Select Id, Name From Recommendation order by Name];
        System.assertEquals('Recom9', listOfRecommendations[5].Name);
        Test.stopTest();
    }
    
       /*
    * Method Name : testPositiveCheckForDuplicateRecommendation
    * Description : This method is used to test the checkForDuplicateRecommendation method from NBARecommendation_H_HUM class.
    * Return type : None
    * Parameter   : None
    */
    static testMethod void testPositiveCheckForDuplicateRecommendationNeg() {
         //Switch Data
            CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =false;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '    REQ - 2164068 NBA Checkmarx fix';
            crm3.Release_Name__c = '05/28/2021';
        
        List<Recommendation> listOfRecommendationExisting = [Select Id, Name, priority__c From Recommendation];
        Boolean result = false;     
        List<Recommendation> listOfRecommendationNew = new List<Recommendation>();
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom7', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 1', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', priority__c =1));
        listOfRecommendationNew.add(new Recommendation(Name = 'Recom8', Alert_Type__c = 'Guidance', Show_To__c = 'Member', Description = 'context 2', Message__c = 'dynamic1',  
                                                    Location__c = 'All Pages', AcceptanceLabel = 'Accept', RejectionLabel = 'Reject', ActionReference = 'Save_Encrypted_Flow',
                                                    Interacting_With_Type__c = 'Member', priority__c =2));
        Test.startTest();
        try{
            insert listOfRecommendationNew;   
        }
        catch(DmlException ex){ 
            result=  true;
        }
        System.assertEquals(false, result);
        List<Recommendation> listOfRecommendationAll = [Select Id, Name From Recommendation order by Name];
        System.assertEquals(6, listOfRecommendationAll.size());
        listOfRecommendationAll[0].Name = 'Recom9';
        update listOfRecommendationAll;
        List<Recommendation> listOfRecommendations = [Select Id, Name From Recommendation order by Name];
        System.assertEquals('Recom9', listOfRecommendations[5].Name);
        Test.stopTest();
    }
    
  /*
    * Method Name : PriorityUpdateTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void PriorityUpdateTest() {
        Test.startTest();
          List<Recommendation> lstRecommendationsToUpdate = new List<Recommendation>();
            for(Recommendation objRecommendation : [SELECT Id,Name,priority__c from Recommendation])
            {
                if(objRecommendation.Name =='Recom1')
                {
                    objRecommendation.Name='Recom45';
                    objRecommendation.priority__c = 45;
                }
                lstRecommendationsToUpdate.add(objRecommendation);
            }
            if(lstRecommendationsToUpdate != null && !lstRecommendationsToUpdate.isEmpty())
            {
                update lstRecommendationsToUpdate;
            }
        Test.stopTest();
    }
    
    /*
    * Method Name : PriorityUpdateTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void PriorityUpdateTestNeg() {
        Test.startTest();
        //Switch Data
            CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =false;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = '    REQ - 2164068 NBA Checkmarx fix';
            crm3.Release_Name__c = '05/28/2021';
          List<Recommendation> lstRecommendationsToUpdate = new List<Recommendation>();
            for(Recommendation objRecommendation : [SELECT Id,Name,priority__c from Recommendation])
            {
                if(objRecommendation.Name =='Recom1')
                {
                    objRecommendation.Name='Recom45';
                    objRecommendation.priority__c = 45;
                }
                lstRecommendationsToUpdate.add(objRecommendation);
            }
            if(lstRecommendationsToUpdate != null && !lstRecommendationsToUpdate.isEmpty())
            {
                update lstRecommendationsToUpdate;
            }
        Test.stopTest();
    }
    
    
  /*
    * Method Name : PriorityDeleteTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void PriorityDeleteTest() {
        Test.startTest();
          List<Recommendation> lstRecommendationsToDelete = new List<Recommendation>();
            for(Recommendation objRecommendation : [SELECT Id,Name,priority__c from Recommendation])
            {
                if(objRecommendation.Name =='Recom1')
                lstRecommendationsToDelete.add(objRecommendation);
            }
            if(lstRecommendationsToDelete != null && !lstRecommendationsToDelete.isEmpty())
            {
                delete lstRecommendationsToDelete;
            }
        Test.stopTest();
    }
    
    /*
    * Method Name : PriorityDeleteTest
    * Return type : None
    * Parameter   : None
    */
    static testMethod void PriorityDeleteTestNeg() {
        Test.startTest();
            //Switch Data
            CRMFunctionality_ONOFF_Switch__c crm3 = new CRMFunctionality_ONOFF_Switch__c();
            crm3.IsON__c =false;
            crm3.Name = '2164068';
            crm3.Scrum_Team__c = 'Lego';
            crm3.Story_Title__c = ' REQ - 2164068 NBA Checkmarx fix';
            crm3.Release_Name__c = '05/28/2021';
            insert crm3;
          List<Recommendation> lstRecommendationsToDelete = new List<Recommendation>();
            for(Recommendation objRecommendation : [SELECT Id,Name,priority__c from Recommendation])
            {
                if(objRecommendation.Name =='Recom1')
                lstRecommendationsToDelete.add(objRecommendation);
            }
            if(lstRecommendationsToDelete != null && !lstRecommendationsToDelete.isEmpty())
            {
                delete lstRecommendationsToDelete;
            }
        Test.stopTest();
    }
}