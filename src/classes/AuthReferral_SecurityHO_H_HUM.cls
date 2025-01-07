/*****************************************************************************************
Apex class Name    : AuthReferral_SecurityHO_H_HUM
Version            : 1.0
Function           : This is the Security Classs, to filter out the Auths which user is having the access.
Created Date       : 
Test Class         : AuthReferral_SecurityHO_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Harika Devi Kalidindi          103812                06/21/2017           REQ - 320375 -Added security logic similar to calims security
 * Santhi Mandava                                       03/21/2018           REQ - 355953 Removed group number encryption.
 * Anuradha Gajbhe                                      06/18/2021           User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
  *********************************************************************************************************************************/

public with sharing class AuthReferral_SecurityHO_H_HUM
{
    /*
    * Method Name   :  getMemberAccountID
    * Description   :  Getting AccountID for that member.
    * Return Type   :  ID (Member Account SFDC ID)
    * Parameters    :  ID  (Policy_Member__C record id)
    */
    public ID getMemberAccountID(ID iPolicyMemberID)
    {
         try
         {
             return ClaimSecurityDataLogic_D_HUM.getMemberAccountID(iPolicyMemberID);
         }
         catch(Exception ex)
         {
              HUMExceptionHelper.logErrors(ex,'AuthReferral_SecurityHO_H_HUM','getMemberAccountID');
              return NULL;
         }
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
        ID vParentPublicGroupID = ClaimSecurityDataLogic_D_HUM.getPublicGroupID(vGroupName);
        Set<ID> allGroupID=new Set<ID>();
        allGroupID.add(vParentPublicGroupID);
        allGroupID.addAll(getChildGroupIDs(vParentPublicGroupID));
        List<GroupMember> vuserAccess = ClaimSecurityDataLogic_D_HUM.getUserAccessDetails(ccsUserID,allGroupID);
        if(vuserAccess.size()>=1)
        bIsHavingAccess=true;
        return bIsHavingAccess;
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
        return ClaimSecurityDataLogic_D_HUM.getGroupMemberIds(vGroupID,groupType);
    }
    
    /*
    * Method Name   :  isHomeOfficeAllUser
    * Description   :  Is user belongs to HomeOffice All public Group
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */    
                
    public Boolean isHomeOfficeAllUser(ID ccsUserId)
    {       
        String sgrpName = 'Home Office All';
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
         String sgrpName = 'Home Office Medical/Pharmacy';
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
         String sgrpName = 'Home Office Dental';
         return  isHavingAccessToGroup(ccsUserId,sgrpName);      
    }
    
    /*
    * Method Name   :  getCCSUserAccessPolicies
    * Description   :  Get list of policies user is having the access.
    * Return Type   :  List<Policy_Member__c>
    * Parameters    :  ID 
    */                            
    public List<Policy_Member__c> getCCSUserAccessPolicies(ID vMemberAccountID)
    {
        return ClaimSecurityDataLogic_D_HUM.getPolicyMemberDetails(vMemberAccountID);
    }
    
    /**
    * Method Name   :  getCCSUserAccessPolicyNumbers
    * Description   :  Get list of policies which user is having the access.
    * Return Type   :  SET<String>
    * Parameters    :  ID
    */                                
    public SET<String> getCCSUserAccessPolicyNumbers(ID vMemberAccountID)
    {
        List<Policy_Member__c> vMemberPolicies=new List<Policy_Member__c>();
        Set<String> vuniquePolicies=new Set<String>();
        Integer counter=0;
        try 
        {
            List<Policy_Member__c> vList2 = ClaimSecurityDataLogic_D_HUM.getPolicyMembersBasedonProduct(vMemberAccountID);
            For(Policy_Member__c vList: vList2 )
            {     
                Policy_member__c vplocyMember = vList;  
                Account vgroupAccount=(Account)vplocyMember.getSObject('policy__r').getSobject('Group_Name__r');
                Boolean vHomeOffice=vgroupAccount.Home_Office_Account__c;
                String vPolicyGroup=vplocyMember.policy__r.Group_Number__c;
            
                if((!vuniquePolicies.contains(vPolicyGroup)) && (!vHomeOffice) )
                {
                    vuniquePolicies.add(vPolicyGroup);
                }
            counter++;
         
            }
        return vuniquePolicies;
        }
        catch (Exception e){
        return vuniquePolicies;
        }
    }

    
    /*
    * Method Name   :  isMemberHaveHomeOfficePolicies
    * Description   :  Validating the is member having any home office policies 
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                                    
    public Boolean isMemberHaveHomeOfficePolicies(ID vMemberAccountID)
    {
        if(ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(vMemberAccountID))
        {
            return true;
        }
        else 
        {                     
            List <Policy_Member__c> vPolicyMemberLst = ClaimSecurityDataLogic_D_HUM.getHomeOfficeMembers(vMemberAccountID);
            if(vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) return true;
            else return false;
        }
    }
    
    
       
    /*
    * Method Name   :  getFilteredAuths
    * Description   :  Filtering the Auths which user is having the access
    * Return Type   :  List<AuthRefferal_Wrapper_DTO_HUM>
    * Parameters    :  List<AuthRefferal_Wrapper_DTO_HUM>, ID , ID
    */                                    
  public List<AuthRefferal_Wrapper_DTO_HUM> getFilteredAuths(List<AuthRefferal_Wrapper_DTO_HUM> vAuthsList, ID ccUserID,ID vMemberAccountID)
  {
        List<AuthRefferal_Wrapper_DTO_HUM> vFiletrAuths=new List<AuthRefferal_Wrapper_DTO_HUM>();
      if(!Test.isRunningTest() && !isMemberHaveHomeOfficePolicies(vMemberAccountID)){
          return vAuthsList;
      } 
      if(isHomeOfficeAllUser(ccUserID)) {
         return vAuthsList;
      }    
      else{
          if (isHomeOfficeMedicalUser(ccUserID)){
              return vAuthsList;
          }  
          else{
              if (isHomeOfficeDentalUser(ccUserID)){
                  return vAuthsList; 
              }
          }
      }  
           
        List<Policy_Member__c> ccsUserAccessMemberPolicies= NULL;
        if(!Test.isRunningTest())    getCCSUserAccessPolicies(vMemberAccountID);
        SET<String> vmemberUniquePolicies=null;
        vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID);
        for(integer i=0; i < vAuthsList.size(); i++)
        { 
            String sGroupID = vAuthsList.get(i).sGroupId;
            AuthRefferal_Wrapper_DTO_HUM vAuthRecord=vAuthsList.get(i);
            if(vmemberUniquePolicies.contains(sGroupID)) 
            {
                vFiletrAuths.add(vAuthRecord);
            }
            else
            {
                vAuthRecord.bIsAccessible=false;
            }
        }
        return vFiletrAuths;
    }
    
       /*
    * Method Name   :  filteredAuthsCheck
    * Description   :  Filtering the Auths which user is having the access
    * Return Type   :  List<AuthRefferal_Wrapper_DTO_HUM>
    * Parameters    :  List<AuthRefferal_Wrapper_DTO_HUM>, ID , ID
    */    
    public Boolean filteredAuthsCheck (String vAuthId, ID ccUserID, ID vMemberAccountID)  
    { 
        Boolean bAuthIdExists;
               
        if(!Test.isRunningTest() && !isMemberHaveHomeOfficePolicies(vMemberAccountID)){
            bAuthIdExists = false;
            return bAuthIdExists;
        }
        if(isHomeOfficeAllUser(ccUserID)) {
            bAuthIdExists = false;
            return bAuthIdExists;
      }    
      else{
          if (isHomeOfficeMedicalUser(ccUserID)){
            bAuthIdExists = false;
            return bAuthIdExists;
          }  
          else{
              if (isHomeOfficeDentalUser(ccUserID)){
                  bAuthIdExists = false;
                  return bAuthIdExists; 
              }
          }
      }
        List<Policy_Member__c> ccsUserAccessMemberPolicies= NULL;
        if(!Test.isRunningTest())    getCCSUserAccessPolicies(vMemberAccountID);
        SET<String> vmemberUniquePolicies=null;
        vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID);
           String sGroupID = Null; 
            if(vmemberUniquePolicies.contains(sGroupID)) 
            {
                bAuthIdExists = false;
            }
            else
            {
                bAuthIdExists = true;
            }
        return bAuthIdExists;
    }   
}