/*******************************************************************************************************************************
Apex Class Name : HUMContentDocumentLinkTrigger 
Version         : 1.0
Created On      : 08/07/2020
Function        : This trigger is used to give the visibility of the articles' related files  to Community Users/Customers
Test Class      : 

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                       08/07/2020                Created to provide files visibility to Community Users
 **************************************************************************************************************************************/

trigger HUMContentDocumentLinkTrigger on ContentDocumentLink (before insert) {
	
    if(Trigger.isBefore && Trigger.isInsert)
    {
        User loggedInUser= [select id,UserPermissionsKnowledgeUser from user where id=: userinfo.getUserId() limit 1];
        if(loggedInUser.UserPermissionsKnowledgeUser){
        ContentDocumentLinkTriggerHandler_H_HUM.ChangeFileVisibility(Trigger.new);
        }
        
    }
}