/**
   @Author Santhi Mandava
   @name QAAProviderSearch_H_HUM
   @CreateDate 02/05/2017
   @Description Contains methods to check logged-in user's enhanced QAA template permission
   @Version 1.0
   @reference REQ - 299435,REQ - 170202,REQ - 299436
   @Test Class CaseQAATemplate_T_HUM
 */
 /********************************************************************************************************************** 
* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            71723                       02/28/2017                 Original version
**************************************************************************************************************************/

public with sharing class CaseQAATemplate_C_HUM
{
    /**
    *  Description This method is used to check logged in users enhanced QAA template permission
    *  @name CaseQAATemplate_C_HUM
    *  @param NA
    *  @return NA
    *  @throws NA
    */
    public CaseQAATemplate_C_HUM()
    {
        bIsHavingAdavancedQAAPermission = checkForEnhancedQAAPermission();
    }
    
    public Boolean bIsHavingAdavancedQAAPermission{get;set;}
    
    /**
    *  Description This method is used to determine if the logged in user is having enhanced QAA template permission or not
    *  @name checkForEnhancedQAAPermission
    *  @param NA
    *  @return Boolean - Return true if user is having permission elase returns false.
    *  @throws NA
    */
    Public Boolean checkForEnhancedQAAPermission()
    {
        Boolean bIsUserHavingEnhancedQAAPermission = false;
        List<CustomPermission> lstSetups = [Select Id FROM CustomPermission where DeveloperName =: GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_ENHANCEDQAAACCESS ];
        
        List<PermissionSetAssignment> lstPermissions = [SELECT Assignee.Id, PermissionSet.Id FROM PermissionSetAssignment WHERE PermissionSetId IN (SELECT ParentId FROM SetupEntityAccess WHERE SetupEntityId =: lstSetups[0].Id) And Assignee.Id =: UserInfo.getUserId()];
        If(!lstPermissions.isEmpty())
        {
            bIsUserHavingEnhancedQAAPermission = true; 
        }                                                    
        return bIsUserHavingEnhancedQAAPermission ;
    }
}