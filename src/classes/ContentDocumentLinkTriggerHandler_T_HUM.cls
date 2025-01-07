/*******************************************************************************************************************************
Apex Class Name : ContentDocumentLinkTriggerHandler_T_HUM 
Version         : 1.0
Created On      : 08/10/2020
Function        : This class is used for testing the class ContentDocumentLinkTriggerHandler_H_HUM and trigger HUMContentDocumentLinkTrigger

Modification Log: 
 * Developer Name           Code Review                 	Date                         	Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                               	   	08/10/2020                    	Original version
 ****************************************************************************************************************************/

@isTest
public class ContentDocumentLinkTriggerHandler_T_HUM {

    /*  
     * Method name : ChangeFileVisibility
     * Description : This method tests the update of the visibility of the files on attching to articles
     * Return Type : void
     * Parameter   : none
     */ 
        
    private static testmethod void testChangeFileVisibility()
    {
        test.startTest();
        //Create a Knowledge Article
        List<Knowledge__kav> allArticles = CommunityTestDataFactory_H_HUM.createTestKnowledgeArticlesUnpublished(1);
       
        //create article attachment
        List<ContentDocumentLink> allContDocLnks = CommunityTestDataFactory_H_HUM.createArticleAttachment(1,allArticles[0].id);   
    	Knowledge__kav testkv = [select id from Knowledge__kav limit 1];
        List<ContentDocumentLink> TestcontDocLnk =[select id,Visibility from ContentDocumentLink where LinkedEntityId=: testkv.Id limit 1];
        System.assertEquals('AllUsers', TestcontDocLnk[0].Visibility);
        test.stopTest();
    }
}