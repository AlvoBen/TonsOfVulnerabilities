/*******************************************************************************************************************************
Apex Class Name : CaseProcessEvaluation_LC_Hum
Version         : 1.0
Created On      : 2/27/2023
Function        : This class is used for displaying OI Process Template
Test Class      : CaseProcessEvaluation_LT_Hum

Modification Log:
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Nirmal Garg                                          02/27/2023                 Original Version
***********************************************************************************************************************/

public with sharing class CheckUserHOAccess_LC_Hum {
    

    @AuraEnabled(cacheable=true)
    public static Boolean isHomeOfficeUser(string groupname){
        try {
            if(string.isNotBlank(groupname)){
                string userId = UserInfo.getUserId();
                return  isHavingAccessToGroup(userId,groupname);
            }
            else{
                throw new HUMCustomException('Group name missing');
            }
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex,'CheckUserHOAccess_LC_Hum','isHomeOfficeUser');
            throw new AuraHandledException(ex.getMessage());
        }
    }

    /*
    * Method Name   :  isHavingAccessToGroup
    * Description   :  Verifying the User Access to Public Group which is the input parameter
    * Return Type   :  Boolean
    * Parameters    :  ID, String  
    */    
    @TestVisible
    private static Boolean isHavingAccessToGroup(ID ccsUserID, String vGroupName)
    {
       Boolean bIsHavingAccess = false;
       ID vParentPublicGroupID = getPublicGroupID(vGroupName);
       Set<ID> allGroupID=new Set<ID>();
       allGroupID.add(vParentPublicGroupID);    
       allGroupID.addAll(getChildGroupIDs(vParentPublicGroupID));
       List<GroupMember> vuserAccess = getUserAccessDetails(ccsUserID,allGroupID);
         if(vuserAccess.size()>=1)
         bIsHavingAccess=true;
        return bIsHavingAccess;
     }
    
     /*
    * Method Name   :  getPublicGroupID
    * Description   :  Getting the ID for the Public Group
    * Return Type   :  ID
    * Parameters    :  String 
    */     
           
    public static Id getPublicGroupID(String sGroupName)
    {
        try 
        {
            return([SELECT ID FROM Group WHERE name=:sGroupName].ID);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CheckUserHOAccess_LC_Hum','getPublicGroupID');
            return NULL;
        }
    }
    
     /*
    * Method Name   :  getChildGroupIDs
    * Description   :  Getting the Child Group Ids for the Group
    * Return Type   :  Set<ID>
    * Parameters    :  ID  
    */        
    private static Set<ID> getChildGroupIDs(ID vGroupID)
    {
        Set<Id> childGroupIDs = new Set<Id>();
        String groupType = Schema.SObjectType.Group.getKeyPrefix();
        return getGroupMemberIds(vGroupID,groupType);
    }
    
     /*
    * Method Name   :  getGroupMemberIds
    * Description   :  Getting set of GroupMember ids.
    * Return Type   :  Set<Id>
    * Parameters    :  Id, String
    */ 
    public static Set<Id> getGroupMemberIds(Id vGroupID ,String sgroupType )
    {
        Set<Id> childGroupIDs = new Set<Id>();
        
        For (GroupMember obj : [Select Id, UserOrGroupId From GroupMember Where GroupId = :vgroupId])
        {
            if(((String)obj .UserOrGroupId).startsWith(sgroupType ))   
            {
                childGroupIDs.add(obj .UserOrGroupId);         
            }
        }
        return childGroupIDs ;
    }
    
    
     /*
    * Method Name   :  getUserAccessDetails
    * Description   :  Getting user access details from GroupMember.
    * Return Type   :  ID (Member Account SFDC ID)
    * Parameters    :  ID  (Policy_Member__C record id)
    */
    public static List<GroupMember > getUserAccessDetails(ID ccsUserID , Set<ID> allGroupID)
    {
        return [Select Id, UserOrGroupId From GroupMember Where UserOrGroupId=:ccsUserID and GroupId  IN :allGroupId ];
    }
}