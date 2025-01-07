/*****************************************************************************************
Apex class Name    : MemberIconsConfiguration_D_HUM 
Version            : 1.0
Function           : Data layer for MemberIconsConfiguration_C_HUM 
Created Date       : 11/01/2019
Test Class         : MemberIconsConfiguration_CT_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      11/01/2019             Original Version
 *   Y Sunil Kumar Reddy                                 02/12/2021             User Story 1874757: Checkmarx scan fix 
 ****************************************************************************************************************************/
public with sharing class MemberIconsConfiguration_D_HUM 
{
    /*
    * This method is used to get member icons document folder details
    * @param  N/A
    * @return String
    */
    public static String getDocumentFolderId()
    {
        String sId = '';
        List<Folder> lstFolders = [SELECT Id FROM Folder WHERE Name = 'MemberIcons'];
        If(lstFolders != Null && !lstFolders.IsEmpty()) sId = lstFolders[0].Id; 
        return sId;
    }
    
    /*
    * This method is used to get document details
    * @param  String - Folder id
    * @return List<Document> folder details
    */
    public static List<Document> getDocuments(String sFolderId)
    {
        return [SELECT Id, Name FROM Document WHERE FolderId =:sFolderId];    
    }

}