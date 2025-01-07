/*****************************************************************************************
Apex class Name    : ClaimsSummaryHOCheck_C_HUM_V2
Version            : 1.0
Function           : This is the Security Classs, to Check For Home Office Policies for the Member and checking the current user access level to display Home Office Error Message.
Created Date       : 24 Jan 2019
Test Class         : ClaimsSummary_T_HUM_V2

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Suresh Sunkari                331209                  24/01/2019           Original Version
 *   Abhishek Mangutkar								 	   05/05/2021			User Story 2230063 : Humana constants Cleanup Claims and MTV Claim Remarks
 *   Prasanthi Kandula                                     07/02/2021           US - 2356070 Claims Summary performance improvement 
 *********************************************************************************************************************************/


public without sharing class ClaimsSummaryHOCheck_C_HUM_V2
{
    Public Static final String HOME_OFFICE_HUM = 'Home Office'; 
     /*
    * Method Name   :  isNonHoUser
    * Description   :  Checking the current user access level.
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */
     public boolean isNonHoUser(ID curentUserID){
        if(isHomeOfficeAllUser(curentUserID)) return false;
        else if(isHomeOfficeMedicalUser(curentUserID)) return false;
        else if(isHomeOfficeDentalUser(curentUserID)) return false;       
        else return true;
        
    }
    
    
    /*
    * Method Name   :  isMemberHaveHomeOfficePolicies
    * Description   :  Validating the is member having any home office policies 
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                                    
    public Boolean isMemberHaveHomeOfficePolicies(ID vMemberAccountID)
    {
        if(isHomeOfficeMember(vMemberAccountID)){
            return true;
        }
        else {                     
        List <Policy_Member__c> vPolicyMemberLst = getHomeOfficeMembers(vMemberAccountID);
        if(vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) return true;
        else return false;
        }
    }
    
    
    /*
    * Method Name   :  isHomeOfficeMember
    * Description   :  Validating the User is HomeOffice or not
    * Return Type   :  Boolean
    * Parameters    :  ID
    */ 
    public static Boolean isHomeOfficeMember(Id vMemberAccountID )
    {
        Account oAccount=NULL;
        oAccount=[SELECT ID,Home_Office_Account__c,Security_Groups__c FROM Account where id=:vMemberAccountID];
        if(oAccount.Home_Office_Account__c)
                 return true;
        else if(oAccount.Security_Groups__c!=null && oAccount.Security_Groups__c.contains(HOME_OFFICE_HUM))
                 return true;
        else
                return false;
        
    }
    
     /*
    * Method Name   :  getHomeOfficeMembers
    * Description   :  Getting Home Office Members
    * Return Type   :  ID
    * Parameters    :  List<POLICY_Member__C>
    */ 
    public static List<POLICY_Member__C> getHomeOfficeMembers(Id vMemberAccountID )
    {
        return [SELECT policy__r.Group_Name__r.name FROM POLICY_Member__C WHERE policy__r.Group_Name__r.Home_Office_Account__c=true and Member__r.id=:vMemberAccountID LIMIT 1];
    }
    
    /*
    * Method Name   :  isHomeOfficeAllUser
    * Description   :  Is user belongs to HomeOffice All public Group
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */   
    public Boolean isHomeOfficeAllUser(ID ccsUserId)
    {       
        String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_ALL').StringValue__c);
        return  isHavingAccessToGroup(ccsUserId,sgrpName);       
    }
    
    /*
    * Method Name   :  isHomeOfficeMedicalUser
    * Description   :  Is user belongs to isHomeOfficeMedicalUser public Group
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                    
    public Boolean isHomeOfficeMedicalUser(ID ccsUserId)
    {        
        String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_MEDICAL').StringValue__c);
        return  isHavingAccessToGroup(ccsUserId,sgrpName);      
    }
    
    /*
    * Method Name   :  isHomeOfficeDentalUser
    * Description   :  Is user belongs to isHomeOfficeDentalUser public Group
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                        
    public Boolean isHomeOfficeDentalUser(ID ccsUserId)
    {
        String sgrpName = String.ValueOf(HUMConstants__c.getInstance('HOME_OFFICE_DENTAL').StringValue__c);
        return  isHavingAccessToGroup(ccsUserId,sgrpName);      
    }
    
    /*
    * Method Name   :  isHavingAccessToGroup
    * Description   :  Verifying the User Access to Public Group which is the input parameter
    * Return Type   :  Boolean
    * Parameters    :  ID, String  
    */    
    @TestVisible
    private Boolean isHavingAccessToGroup(ID ccsUserID, String vGroupName)
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
            HUMExceptionHelper.logErrors(ex,'ClaimsSummaryHOCheck_C_HUM_V2','getPublicGroupID');
            return NULL;
        }
    }
    
     /*
    * Method Name   :  getChildGroupIDs
    * Description   :  Getting the Child Group Ids for the Group
    * Return Type   :  Set<ID>
    * Parameters    :  ID  
    */        
    private Set<ID> getChildGroupIDs(ID vGroupID)
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