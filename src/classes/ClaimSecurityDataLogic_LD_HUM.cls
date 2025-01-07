/*****************************************************************************************
Apex class Name    : ClaimSecurityDataLogic_LD_HUM
Version            : 1.0
Function           : This is test class for the claim security helper class ClaimSecurityDataLogic_LD_HUM.
Created Date       : 06/10/2022
Test Class         :  

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Suraj Patil                                      	 06/10/2022         Original Version
 *   Anuradha Gajbhe                                     06/30/2023         User story 4759901 - TECH - Surge- Blocker - AuthReferral_SecurityHO_LH_HUM : Avoid Soql queries inside loops.
 ******************************************************************************************************************************/

public with sharing class ClaimSecurityDataLogic_LD_HUM 
{	
    Public Static final String HOME_OFFICE_HUM = 'Home Office';

    /*
    * Method Name   :  getMemberAccountID
    * Description   :  Getting AccountID for that member.
    * Return Type   :  ID (Member Account SFDC ID)
    * Parameters    :  ID  (Policy_Member__C record id)
    */
    public static ID getMemberAccountID(ID iPolicyMemberID)
    {
        return([SELECT memberId FROM MemberPlan WHERE ID=:iPolicyMemberID].memberId);
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
    * Method Name   :  getPublicGroupID
    * Description   :  Getting the ID for the Public Group
    * Return Type   :  ID
    * Parameters    :  String 
    */     
    @TestVisible       
    public static Id getPublicGroupID(String sGroupName)
    {
        try 
        {
            return([SELECT ID FROM Group WHERE name=:sGroupName].ID);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'ClaimSecurityDataLogic_LD_HUM','getPublicGroupID');
            return NULL;
        }
    }
    
    /*
    * Method Name   :  getPolicyMemberDetails
    * Description   :  Getting policy member details
    * Return Type   :  List<MemberPlan>
    * Parameters    :  Id
    */ 
    public static List<MemberPlan> getPolicyMemberDetails(Id vMemberAccountID )
    {
        List<MemberPlan> vMemberPolicies=new List<MemberPlan>();
        Set<String> vuniquePolicies=new Set<String>();
        Integer counter=0;
        
        For(List<MemberPlan> vList: [SELECT NAME,Product__c, Plan.Home_Office_Account__c FROM MemberPlan WHERE Memberid=:vMemberAccountID and PRODUCT__C IN ('MED','DEN')]       )
        {            
            if(!vuniquePolicies.contains(vList.get(counter).name)) 
            {
            vuniquePolicies.add(vList.get(counter).name);
            vMemberPolicies.add(vList.get(counter));
            }
            counter++;
        }
        return vMemberPolicies;
    }
    
    /*
    * Method Name   :  getMemberIdName
    * Description   :  Getting Member name.
    * Return Type   :  ID
    * Parameters    :  String 
    */ 
    public static String getMemberIdName(Id policyRecordID )
    {
            return [SELECT Name FROM MemberPlan WHERE Id=:policyRecordID].Name;
    }
    
    /*
    * Method Name   :  getHomeOfficeMembers
    * Description   :  Getting Home Office Members
    * Return Type   :  ID
    * Parameters    :  List<MemberPlan>
    */ 
    public static List<MemberPlan> getHomeOfficeMembers(Id vMemberAccountID )
    {
        return [SELECT plan.Policy_Group_Number__c FROM MemberPlan WHERE plan.Home_Office_Account__c=true and Memberid=:vMemberAccountID LIMIT 1];
    }
    
    /*
    * Method Name   :  getPolicyMembersBasedonProduct
    * Description   :  Getting policy members based on product
    * Return Type   :  ID
    * Parameters    :  List<MemberPlan>
    */ 
    public static List<MemberPlan> getPolicyMembersBasedonProduct(ID vMemberAccountID)
    {
        return [SELECT Name,Product__c,PlanId, Plan.Home_Office_Account__c, plan.Policy_Group_Number__c FROM MemberPlan WHERE Member.id=:vMemberAccountID and PRODUCT__C IN ('MED','DEN')] ;
    }
    
    /*
    * Method Name   :  isHomeOfficeMember
    * Description   :  Validating the User is HomeOffice or not
    * Return Type   :  Boolean
    * Parameters    :  ID
    */ 
    public static Boolean isHomeOfficeMember(Id vMemberAccountID )
    {
        List<Account> oAccount=NULL;
        try 
        {       
           oAccount=[SELECT ID,Home_Office_Account__c,Security_Groups__c FROM Account where id=:vMemberAccountID];
           Boolean check = ((oAccount).size()>0) ? true : false;       
           if(check){  
                if(oAccount[0].Home_Office_Account__c)
                    return true;
                else if(oAccount[0].Security_Groups__c!=null && oAccount[0].Security_Groups__c.contains(HOME_OFFICE_HUM))
                     return true;
                else
                    return false;
            }
            else {
                return false;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'ClaimSecurityDataLogic_LD_HUM','isHomeOfficeMember');
            return NULL;
        }
    }
}