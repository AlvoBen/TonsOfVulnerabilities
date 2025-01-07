/*****************************************************************************************
Apex class Name    : ClaimSecurityDataLogic_D_HUM
Version            : 1.0
Function           : This is test class for the claim security helper class ClaimSecurityDataLogic_D_HUM.
Created Date       : 15 Apr 2016
Test Class         : ClaimSecurityDataLogic_T_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Rajaram Korivi                                      04/22/2016         Original Version
 *   Harika Devi Kalidindi         30521                 06/17/2016         Defect# 230083.0001 Fix
 *   Harika Devi Kalidindi         103812                06/21/2017         Modified  getPolicyMembersBasedonProduct method to retrieve Policy__r.Group_Number__c
 *   Suresh Sunkari                                      02/06/2019         Handled Null Check
 *   Sunil Bukya                                         02/11/2019         Platform code changes 
 *   Santhi Mandava                                      04/15/2019         Replaced "Member_Id_Value__c" with Name
 *	 Samruddhi Rathi	                                 05/07/2021         REQ - 2230063 Humana Constant CleanUp
 *   Anuradha Gajbhe                                     06/18/2021         User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
 ******************************************************************************************************************************/

public with sharing class ClaimSecurityDataLogic_D_HUM 
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
        return([SELECT member__c FROM POLICY_Member__C WHERE ID=:iPolicyMemberID].member__C);
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
            HUMExceptionHelper.logErrors(ex,'ClaimSecurityDataLogic_D_HUM','getPublicGroupID');
            return NULL;
        }
    }
    
    /*
    * Method Name   :  getPolicyMemberDetails
    * Description   :  Getting policy member details
    * Return Type   :  List<POLICY_MEMBER__C>
    * Parameters    :  Id
    */ 
    public static List<POLICY_MEMBER__C> getPolicyMemberDetails(Id vMemberAccountID )
    {
        List<Policy_Member__c> vMemberPolicies=new List<Policy_Member__c>();
        Set<String> vuniquePolicies=new Set<String>();
        Integer counter=0;
        
        For(List<Policy_Member__c> vList: [SELECT NAME,Product__c, policy__r.Group_Name__r.Home_Office_Account__c FROM POLICY_MEMBER__C WHERE Member__r.id=:vMemberAccountID and PRODUCT__C IN ('MED','DEN')]       )
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
            return [SELECT Name FROM MEMBER_ID__C WHERE POLICY_MEMBER__C=:policyRecordID and TYPE__C='Member-Id-Base'].Name;
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
    * Method Name   :  getPolicyMembersBasedonProduct
    * Description   :  Getting policy members based on product
    * Return Type   :  ID
    * Parameters    :  List<Policy_Member__c>
    */ 
    public static List<Policy_Member__c> getPolicyMembersBasedonProduct(ID vMemberAccountID)
    {
        return [SELECT NAME,Product__c, policy__r.Group_Name__r.Home_Office_Account__c,policy__r.Group_Number__c FROM POLICY_MEMBER__C WHERE Member__r.id=:vMemberAccountID and PRODUCT__C IN ('MED','DEN')] ;
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
                else if(oAccount[0].Security_Groups__c.contains(HOME_OFFICE_HUM))
                     return true;
                else
                    return false;
            }
            else {
                return false;}
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'ClaimSecurityDataLogic_D_HUM','isHomeOfficeMember');
            return NULL;
        }
        return false;
    }
   
}