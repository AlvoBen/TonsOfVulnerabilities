/*****************************************************************************************
Apex class Name    : MemIdCard_HOSecurity_LH_HUM
Version            : 1.0
Function           : This is the Security Classs, to filter out the policies which user is having the access.
Created Date       : 11/04/2022
Test Class         : MemIdCard_HOSecurity_LT_HUM

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*   Anuradha Gajbhe                                     11/14/2022         Original Version
*********************************************************************************************************************************/
public without sharing class MemIdCard_HOSecurity_LH_HUM {    
    
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
        List <MemberPlan> vPolicyMemberLst = getHomeOfficeMembers(vMemberAccountID);
        if(vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) return true;
        else return false;
        }
    }
    
    /*
    * Method Name   :  getHomeOfficeMembers
    * Description   :  Getting Home Office Members
    * Return Type   :  ID
    * Parameters    :  List<MemberPlan>
    */ 
    public static List<MemberPlan> getHomeOfficeMembers(Id vMemberAccountID )
    {
        return [SELECT Display_Group_Name__c FROM MemberPlan WHERE plan.Home_Office_Account__c=true and Memberid=:vMemberAccountID LIMIT 1];
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
            HUMExceptionHelper.logErrors(ex,'ClaimsSummaryHOCheck_LC_HUM','getPublicGroupID');
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
    
    /*
    * Method Name   :  getFilteredPolicies
    * Description   :  Filtering the Claims which user is having the access
    * Return Type   :  List<ClaimsSummary_Wrapper_LDTO_HUM>
    * Parameters    :  List<ClaimsSummary_Wrapper_LDTO_HUM>, ID , ID
    */                                    
    public List<MemberPlan> getFilteredPolicies(List<MemberPlan> vPolList, ID vMemberAccountID, Boolean bIsMemberAHomeOfficeAcct, Boolean bisHomeOfficeAllUser, Boolean bisHomeOfficeMedicalUser, Boolean bisHomeOfficeDentalUser)
    {
        List<MemberPlan> vFilterPolicies=new List<MemberPlan>();
        if(!Test.isRunningTest() && !bIsMemberAHomeOfficeAcct) {
            for (MemberPlan Pol : vPolList){
                vFilterPolicies.add(Pol);
            }
            return vFilterPolicies;   
        }
        if(bisHomeOfficeAllUser){ 
            for (MemberPlan Pol : vPolList){
                vFilterPolicies.add(Pol);
            }
            return vFilterPolicies; 
        }
        List<MemberPlan> ccsUserAccessMemberPolicies= NULL;
        SET<String> vmemberUniquePolicies=null;
        if(bisHomeOfficeMedicalUser)
        {
            //Show only Medical Policies for Home Office and General
            vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'MED'); 
        }
        else if(bisHomeOfficeDentalUser)
        {
            //Show only Dental Policies for Home Office and General
            vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'DEN'); 
        }
        else
        {
           //Show Only Genearl Policies
           vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'None');

        }
        for(integer i=0;i<vPolList.size();i++)
        {
            String sGroupID = (vPolList.get(i).Plan.Policy_Group_Number__c != null) ? vPolList.get(i).Plan.Policy_Group_Number__c: '';
            String sMemberID = (vPolList.get(i).Name != null) ? vPolList.get(i).Name: '';
            
            //if(vmemberUniquePolicies!= null && vPolList.get(i).Plan.Policy_Group_Number__c != null && vmemberUniquePolicies.contains(sGroupID)) {
            if(vmemberUniquePolicies!= null && vPolList.get(i).Name != null && vmemberUniquePolicies.contains(sMemberID)) {
                vFilterPolicies.add(vPolList.get(i));
            }
        }
        return vFilterPolicies;

        }

    /**
    * Method Name   :  getCCSUserAccessPolicyNumbers
    * Description   :  Get list of policies which user is having the access.
    * Return Type   :  SET<String>
    * Parameters    :  ID
    */                                
    public SET<String> getCCSUserAccessPolicyNumbers(ID vMemberAccountID, String userType)
    {
        List<MemberPlan> vMemberPolicies=new List<MemberPlan>();
        Set<String> vuniquePolicies=new Set<String>();
        try 
        {
            List<MemberPlan> vList2 = ClaimSecurityDataLogic_LD_HUM.getPolicyMembersBasedonProduct(vMemberAccountID);
            For(MemberPlan vList: vList2 )
            {     
                MemberPlan vmemberPlan = vList;  
                PurchaserPlan vgroupPlan=(PurchaserPlan)vmemberPlan.getSObject('plan');
                Boolean vHomeOffice=vgroupPlan.Home_Office_Account__c;
                String vPlanGroup=(vgroupPlan.Policy_Group_Number__c != null) ? vgroupPlan.Policy_Group_Number__c: '';
                String smemmberID=getPolicyMemberID(vList.id);
                //if((!vuniquePolicies.contains(vPlanGroup)) && ((!vHomeOffice) || (vList.Product__c.equalsIgnoreCase(userType))))
                if((!vuniquePolicies.contains(smemmberID)) &&( (!vHomeOffice) || (vList.Product__c.equalsIgnoreCase(userType))))
                {
                    //vuniquePolicies.add(vPlanGroup);
                    vuniquePolicies.add(smemmberID);
                }
            }
            return vuniquePolicies;
        }
        catch (Exception e){
            return vuniquePolicies;
        }
    }        
    /*
    * Method Name   :  getPolicyMemberID
    * Description   :  Get the Member ID from Policy
    * Return Type   :  String
    * Parameters    :  ID 
    */ 
    @TestVisible
    private String getPolicyMemberID(ID policyRecordID)
    {
        String cmemberID='';
        try 
        {
            cmemberID= ClaimSecurityDataLogic_LD_HUM.getMemberIdName(policyRecordID);
        }
        catch(Exception e)
        {
             cmemberID='';
        }                
        return cmemberID;
    }
}