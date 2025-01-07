/*******************************************************************************************************************************
Apex Class Name : ContentDocumentLinkTriggerHandler_H_HUM 
Version         : 1.0
Created On      : 08/07/2020
Function        : This class is a Handler class for HUMContentDocumentLinkTrigger
Test Class      : ContentDocumentLinkTriggerHandler_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                      08/07/2020                    Original version
**************************************************************************************************************************************/

public with sharing class ContentDocumentLinkTriggerHandler_H_HUM {

    /**
     * ChangeFileVisibility
     * Change the visibility of the record 
     * @param List<ContentDocumentLink> contDocLinkLst
     * @return Void 
     **/
     
    public static void ChangeFileVisibility(List<ContentDocumentLink> contDocLinkLst){
        
        String Kavprefix = Schema.getGlobalDescribe().get('Knowledge__kav').getDescribe().getKeyPrefix();
        
        for(ContentDocumentLink contDocLnk:contDocLinkLst ){
            if(String.valueOf(contDocLnk.LinkedEntityId).startsWith(Kavprefix))
            {
                contDocLnk.Visibility='AllUsers';
            }
        }
    }
}