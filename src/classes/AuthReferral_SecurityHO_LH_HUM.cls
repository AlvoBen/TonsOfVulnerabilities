/*****************************************************************************************
Apex class Name    : AuthReferral_SecurityHO_LH_HUM
Version            : 1.0
Function           : Security class to check the accesibility.
Created Date       : 14/07/2022
Test Class         : AuthReferral_SecurityHO_LT_HUM 
*******************************************************************************************
Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
* Rajesh Narode                                         14/07/2022         User story 3362694 Authorization Summary table
* Anuradha Gajbhe                                       05/12/2022         User story 3778026 - Auth/Referral: Lightning Build: Home Office Auths/referrals.
* Raj Paliwal						03/03/2023	   User story 4003693 - Auth/Referral: Lightning Verification: Toast/Error/Info Messages.
* Anuradha Gajbhe                                       06/30/2023         User story 4759901 - TECH - Surge- Blocker - AuthReferral_SecurityHO_LH_HUM : Avoid Soql queries inside loops.
  *********************************************************************************************************************************/
public with sharing class AuthReferral_SecurityHO_LH_HUM {
  /*
    * Method Name   :  getMemberAccountID
    * Description   :  Getting AccountID for that member.
    * Return Type   :  ID (Member Account SFDC ID)
    * Parameters    :  ID  (MemberPlan record id)
    */
    public ID getMemberAccountID(ID iPolicyMemberID)
    {
         try
         {
             return ClaimSecurityDataLogic_LD_HUM.getMemberAccountID(iPolicyMemberID);
         }
         catch(Exception ex)
         {
              HUMExceptionHelper.logErrors(ex,'AuthReferral_SecurityHO_LH_HUM','getMemberAccountID');
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
        ID vParentPublicGroupID = ClaimSecurityDataLogic_LD_HUM.getPublicGroupID(vGroupName);
        Set<ID> allGroupID=new Set<ID>();
        allGroupID.add(vParentPublicGroupID);
        allGroupID.addAll(getChildGroupIDs(vParentPublicGroupID));
        List<GroupMember> vuserAccess = ClaimSecurityDataLogic_LD_HUM.getUserAccessDetails(ccsUserID,allGroupID);
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
        return ClaimSecurityDataLogic_LD_HUM.getGroupMemberIds(vGroupID,groupType);
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
    * Return Type   :  List<MemberPlan>
    * Parameters    :  ID 
    */                            
    public List<MemberPlan> getCCSUserAccessPolicies(ID vMemberAccountID)
    {
        return ClaimSecurityDataLogic_LD_HUM.getPolicyMemberDetails(vMemberAccountID);
    }
    
    /**
    * Method Name   :  getCCSUserAccessPolicyNumbers
    * Description   :  Get list of policies which user is having the access.
    * Return Type   :  SET<String>
   * Parameters    :  ID
    */                                
    public SET<String> getCCSUserAccessPolicyNumbers(ID vMemberAccountID)
    {
        List<MemberPlan> vMemberPolicies=new List<MemberPlan>();
        Set<String> vuniquePolicies=new Set<String>();
		Set<String> vuniquePlanid=new Set<String>();
        map<String, boolean> planAccessMap = new map<String, boolean>();
		map<String, boolean> planHOMap = new map<String, boolean>();
		Boolean vHomeOffice;
        boolean userhasplanaccess;
        boolean bHOplan;
        String vPolicyGroup;
        Integer counter=0;
        try 
        {
            List<MemberPlan> vList2 = ClaimSecurityDataLogic_LD_HUM.getPolicyMembersBasedonProduct(vMemberAccountID);
			if(vList2 != null && (vList2.size() > 0)){
                for(MemberPlan vList: vList2)
				{
                    vuniquePlanid.add(vList.planId);
                    planHOMap.put(vList.planId, vList.Plan.Home_Office_Account__c);			
                }
                for (UserRecordAccess indUserPlanAccess : [SELECT RecordId, HasReadAccess FROM UserRecordAccess WHERE UserId=:UserInfo.getUserId() AND RecordId IN:vuniquePlanid LIMIT 200])
				{
                    planAccessMap.put(indUserPlanAccess.RecordId , indUserPlanAccess.HasReadAccess);
                }
				for(MemberPlan vList: vList2 )
				{     
					if(vList.planId != Null){
                        userhasplanaccess = planAccessMap.get(vList.planid);
                        bHOplan = planHOMap.get(vList.planid);
                        if(userhasplanaccess == true){
                            vHomeOffice = bHOplan;
						}else{
							vHomeOffice= true;
						}
					}else{
						vHomeOffice= true;
					}
					vPolicyGroup=vList.Plan.Policy_Group_Number__c;
            
					if((!vuniquePolicies.contains(vPolicyGroup)) && (!vHomeOffice))
					{
						vuniquePolicies.add(vPolicyGroup);
					}
					counter++;
				}
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
        if(ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(vMemberAccountID))
        {
            return true;
        }
        else 
        {                     
            List <MemberPlan> vPolicyMemberLst = ClaimSecurityDataLogic_LD_HUM.getHomeOfficeMembers(vMemberAccountID);
            if(vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) return true;
            else return false;
        }
    }
    
    
       
    /*
    * Method Name   :  getFilteredAuths
    * Description   :  Filtering the Auths which user is having the access
    * Return Type   :  List<AuthRefferal_Wrapper_LDTO_HUM>
    * Parameters    :  List<AuthRefferal_Wrapper_LDTO_HUM>, ID , ID
    */                                    
  public List<AuthRefferal_Wrapper_LDTO_HUM> getFilteredAuths(List<AuthRefferal_Wrapper_LDTO_HUM> vAuthsList, ID ccUserID,ID vMemberAccountID)
  {
        List<AuthRefferal_Wrapper_LDTO_HUM> vFiletrAuths=new List<AuthRefferal_Wrapper_LDTO_HUM>();
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
           
        List<MemberPlan> ccsUserAccessMemberPolicies= NULL;
        if(!Test.isRunningTest())    getCCSUserAccessPolicies(vMemberAccountID);
        SET<String> vmemberUniquePolicies=null;
        vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID);
        for(integer i=0; i < vAuthsList.size(); i++)
        { 
            String sGroupID = vAuthsList.get(i).sGroupId;
            AuthRefferal_Wrapper_LDTO_HUM vAuthRecord=vAuthsList.get(i);
            if(vmemberUniquePolicies.contains(sGroupID)) 
            {
                vFiletrAuths.add(vAuthRecord);
            }
            else
            {
                vAuthRecord.bIsAccessible=false;
		vFiletrAuths.add(vAuthRecord);
            }
        }
        return vFiletrAuths;
    }
    
       /*
    * Method Name   :  filteredAuthsCheck
    * Description   :  Filtering the Auths which user is having the access
    * Return Type   :  Boolean
    * Parameters    :  String , ID , ID
    */    
    public Boolean filteredAuthsCheck (String GroupId, ID ccUserID, ID vMemberAccountID)  
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
        List<MemberPlan> ccsUserAccessMemberPolicies= NULL;
        if(!Test.isRunningTest())    getCCSUserAccessPolicies(vMemberAccountID);
        SET<String> vmemberUniquePolicies=null;
        vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID);
           String sGroupID = GroupId; 
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